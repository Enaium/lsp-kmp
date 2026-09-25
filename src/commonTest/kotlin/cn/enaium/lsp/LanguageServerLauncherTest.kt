package cn.enaium.lsp

import cn.enaium.lsp.jsonrpc.JsonRpcId
import cn.enaium.lsp.jsonrpc.JsonRpcJson
import cn.enaium.lsp.jsonrpc.JsonRpcMessage
import cn.enaium.lsp.jsonrpc.TestTransport
import cn.enaium.lsp.model.*
import kotlinx.serialization.json.JsonElement
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

private class FakeTextDocumentService : TextDocumentService {
    val notebookEvents = mutableListOf<String>()

    override fun didOpenNotebookDocument(params: DidOpenNotebookDocumentParams) {
        notebookEvents += "open:${params.notebookDocument.uri}"
    }

    override fun didChangeNotebookDocument(params: DidChangeNotebookDocumentParams) {
        notebookEvents += "change:${params.notebookDocument.uri}"
    }

    override fun didSaveNotebookDocument(params: DidSaveNotebookDocumentParams) {
        notebookEvents += "save:${params.notebookDocument.uri}"
    }

    override fun didCloseNotebookDocument(params: DidCloseNotebookDocumentParams) {
        notebookEvents += "close:${params.notebookDocument.uri}"
    }
}

private class FakeLanguageServer(
    private val textDocument: TextDocumentService? = null,
) : LanguageServer {
    var initialized = false
    var exited = false

    override fun initialize(params: InitializeParams): InitializeResult {
        initialized = true
        return InitializeResult(
            capabilities = ServerCapabilities(
                textDocumentSync = TextDocumentSync.Kind(TextDocumentSyncKind.Full),
                hoverProvider = HoverProvider.Enabled(true),
            ),
            serverInfo = ServerInfo(name = "fake", version = "1.0"),
        )
    }

    override fun shutdown(): Any? = null

    override fun exit() {
        exited = true
    }

    override fun textDocumentService(): TextDocumentService? = textDocument
    override fun workspaceService(): WorkspaceService? = null
    override fun windowService(): WindowService? = null
}

class LanguageServerLauncherTest {

    @Test
    fun initializeRequestIsWired() {
        val transport = TestTransport()
        val server = FakeLanguageServer()
        val launcher = LanguageServerLauncher(transport, server)

        transport.enqueue(
            JsonRpcJson.json.encodeToString(
                JsonRpcMessage.serializer(),
                JsonRpcMessage(
                    id = JsonRpcId(value = 1),
                    method = "initialize",
                    params = JsonRpcJson.json.encodeToJsonElement(
                        InitializeParams.serializer(),
                        InitializeParams(rootUri = "file:///tmp", capabilities = ClientCapabilities()),
                    ),
                ),
            )
        )
        launcher.listen()

        val reply = JsonRpcJson.json.decodeFromString(JsonRpcMessage.serializer(), transport.outbound.first())
        assertEquals(1L, reply.id?.value)
        val result = JsonRpcJson.json.decodeFromJsonElement(InitializeResult.serializer(), reply.result!!)
        assertEquals("fake", result.serverInfo?.name)
        assertEquals(HoverProvider.Enabled(true), result.capabilities.hoverProvider)
        assertNotNull(result.capabilities.textDocumentSync)
        assertEquals(true, server.initialized)
    }

    @Test
    fun notebookNotificationsAreWired() {
        val transport = TestTransport()
        val textDocument = FakeTextDocumentService()
        val launcher = LanguageServerLauncher(transport, FakeLanguageServer(textDocument))

        fun notification(method: String, params: JsonElement) {
            transport.enqueue(
                JsonRpcJson.json.encodeToString(
                    JsonRpcMessage.serializer(),
                    JsonRpcMessage(method = method, params = params),
                )
            )
        }

        val notebook = NotebookDocument(
            uri = "file:///nb.ipynb",
            notebookType = "jupyter-notebook",
            version = 1,
            cells = emptyList(),
        )
        notification(
            "notebookDocument/didOpen",
            JsonRpcJson.json.encodeToJsonElement(
                DidOpenNotebookDocumentParams.serializer(),
                DidOpenNotebookDocumentParams(notebookDocument = notebook, cellTextDocuments = emptyList()),
            ),
        )
        notification(
            "notebookDocument/didChange",
            JsonRpcJson.json.encodeToJsonElement(
                DidChangeNotebookDocumentParams.serializer(),
                DidChangeNotebookDocumentParams(
                    notebookDocument = VersionedNotebookDocumentIdentifier(uri = notebook.uri, version = 2),
                    change = NotebookDocumentChangeEvent(),
                ),
            ),
        )
        notification(
            "notebookDocument/didSave",
            JsonRpcJson.json.encodeToJsonElement(
                DidSaveNotebookDocumentParams.serializer(),
                DidSaveNotebookDocumentParams(notebookDocument = NotebookDocumentIdentifier(notebook.uri)),
            ),
        )
        notification(
            "notebookDocument/didClose",
            JsonRpcJson.json.encodeToJsonElement(
                DidCloseNotebookDocumentParams.serializer(),
                DidCloseNotebookDocumentParams(
                    notebookDocument = NotebookDocumentIdentifier(notebook.uri),
                    cellTextDocuments = emptyList(),
                ),
            ),
        )

        launcher.listen()

        assertEquals(
            listOf("open:file:///nb.ipynb", "change:file:///nb.ipynb", "save:file:///nb.ipynb", "close:file:///nb.ipynb"),
            textDocument.notebookEvents,
        )
    }

    @Test
    fun exitNotificationIsWired() {
        val transport = TestTransport()
        val server = FakeLanguageServer()
        val launcher = LanguageServerLauncher(transport, server)

        transport.enqueue(
            JsonRpcJson.json.encodeToString(
                JsonRpcMessage.serializer(),
                JsonRpcMessage(method = "exit"),
            )
        )
        launcher.listen()
        assertEquals(true, server.exited)
    }
}