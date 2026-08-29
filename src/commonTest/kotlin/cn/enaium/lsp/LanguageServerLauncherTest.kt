package cn.enaium.lsp

import cn.enaium.lsp.jsonrpc.JsonRpcId
import cn.enaium.lsp.jsonrpc.JsonRpcJson
import cn.enaium.lsp.jsonrpc.JsonRpcMessage
import cn.enaium.lsp.jsonrpc.TestTransport
import cn.enaium.lsp.model.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

private class FakeLanguageServer : LanguageServer {
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

    override fun textDocumentService(): TextDocumentService? = null
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