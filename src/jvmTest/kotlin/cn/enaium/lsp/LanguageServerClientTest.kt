package cn.enaium.lsp

import cn.enaium.lsp.jsonrpc.JsonRpcJson
import cn.enaium.lsp.jsonrpc.JsonRpcMessage
import cn.enaium.lsp.jsonrpc.StreamMessageTransport
import cn.enaium.lsp.model.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import java.io.PipedInputStream
import java.io.PipedOutputStream
import kotlin.concurrent.thread
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * A language server talking back to its client over a real transport: the
 * server-to-client requests are `suspend` functions that resume with the
 * editor's answer (or fail when it errors), and the notifications it fires
 * reach the wire unchanged.
 */
class LanguageServerClientTest {

    private class QuietServer : LanguageServer {
        override fun initialize(params: InitializeParams): InitializeResult =
            InitializeResult(capabilities = ServerCapabilities(), serverInfo = null)

        override fun shutdown(): Any? = null
        override fun exit() {}
        override fun textDocumentService(): TextDocumentService? = null
        override fun workspaceService(): WorkspaceService? = null
        override fun windowService(): WindowService? = null
    }

    /** Reads what the server sends and answers it the way an editor would. */
    private class Editor(
        private val transport: StreamMessageTransport,
        private val trace: CompletableDeferred<LogTraceParams>,
        private val progress: CompletableDeferred<String>,
    ) {
        fun serve() {
            while (true) {
                val text = transport.receive() ?: return
                val message = JsonRpcJson.json.decodeFromString(JsonRpcMessage.serializer(), text)
                val id = message.id ?: run {
                    when (message.method) {
                        "\$/logTrace" ->
                            trace.complete(JsonRpcJson.json.decodeFromJsonElement(LogTraceParams.serializer(), message.params!!))
                        "\$/progress" -> progress.complete(text)
                    }
                    continue
                }
                when (message.method) {
                    "window/showMessageRequest" -> reply(
                        id,
                        MessageActionItem.serializer(),
                        MessageActionItem(title = "Continue"),
                    )
                    "workspace/applyEdit" -> reply(id, ApplyWorkspaceEditResponse.serializer(), ApplyWorkspaceEditResponse(applied = true))
                    "workspace/configuration" -> reply(
                        id,
                        JsonElement.serializer(),
                        JsonRpcJson.json.encodeToJsonElement(
                            kotlinx.serialization.builtins.ListSerializer(JsonElement.serializer()),
                            listOf(JsonRpcJson.json.parseToJsonElement("""{"indent":4}""")),
                        ),
                    )
                    else -> replyRaw(id, JsonNull)
                }
            }
        }

        private fun <T> reply(id: cn.enaium.lsp.jsonrpc.JsonRpcId, serializer: KSerializer<T>, value: T) =
            replyRaw(id, JsonRpcJson.json.encodeToJsonElement(serializer, value))

        private fun replyRaw(id: cn.enaium.lsp.jsonrpc.JsonRpcId, result: JsonElement) {
            transport.send(
                JsonRpcJson.json.encodeToString(JsonRpcMessage.serializer(), JsonRpcMessage(id = id, result = result)),
            )
        }
    }

    @Test
    fun suspendClientRequestsRoundTripAndNotificationsReachTheWire() {
        val serverIn = PipedInputStream()
        val serverOut = PipedOutputStream()
        val clientOut = PipedOutputStream(serverIn)
        val clientIn = PipedInputStream(serverOut)

        val launcher = LanguageServerLauncher(StreamMessageTransport(serverIn, serverOut), QuietServer())
        val serverThread = thread(isDaemon = true) { launcher.listen() }

        val trace = CompletableDeferred<LogTraceParams>()
        val progress = CompletableDeferred<String>()
        val editorTransport = StreamMessageTransport(clientIn, clientOut)
        val editorThread = thread(isDaemon = true) { Editor(editorTransport, trace, progress).serve() }

        try {
            runBlocking {
                // A request with a result: resumes with what the editor answered.
                val item = launcher.client.showMessageRequest(
                    ShowMessageRequestParams(type = MessageType.Info, message = "Continue?"),
                )
                assertEquals("Continue", item?.title)

                // A request whose result an editor may omit: null, not a failure.
                assertNull(launcher.client.workspaceFolders())

                // A request with a required result.
                assertEquals(true, launcher.client.applyEdit(ApplyWorkspaceEditParams(edit = WorkspaceEdit()))?.applied)

                // A required collection result.
                assertEquals(1, launcher.client.configuration(ConfigurationParams(listOf(ConfigurationItem(section = "editor"))))?.size)

                // A request answered without a body.
                launcher.client.refreshCodeLenses()
                launcher.client.createProgress(WorkDoneProgressCreateParams(token = Token.NumberValue(1)))

                // Notifications are fire-and-forget.
                launcher.client.logTrace(LogTraceParams(message = "trace"))
                launcher.client.notifyProgress(
                    ProgressParams(
                        token = Token.NumberValue(1),
                        value = ProgressValue.WorkDone(
                            WorkDoneProgressNotificationValue.Begin(WorkDoneProgressBegin(title = "indexing")),
                        ),
                    ),
                )

                val traceParams = withTimeout(5_000) { trace.await() }
                assertEquals("trace", traceParams.message)

                val progressFrame = withTimeout(5_000) { progress.await() }
                // Regression: the work-done-progress payload must carry the `kind`
                // discriminator a client (and our own decoder) tells begin/report/end
                // apart with.
                assertTrue(progressFrame.contains("\"kind\":\"begin\""), progressFrame)
                val params = JsonRpcJson.json.decodeFromJsonElement(
                    ProgressParams.serializer(),
                    (JsonRpcJson.json.parseToJsonElement(progressFrame) as JsonObject)["params"]!!,
                )
                assertEquals(Token.NumberValue(1), params.token)
                val begin = (params.value as ProgressValue.WorkDone).value as WorkDoneProgressNotificationValue.Begin
                assertEquals("indexing", begin.value.title)
            }
        } finally {
            clientOut.close()
            serverOut.close()
            serverThread.join(3_000)
            editorThread.join(3_000)
        }
    }
}
