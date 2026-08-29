package cn.enaium.lsp.jsonrpc

import cn.enaium.lsp.jsonrpc.JsonRpcLauncher
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.JsonPrimitive
import java.util.concurrent.LinkedBlockingQueue
import kotlin.test.Test
import kotlin.test.assertEquals

/** A transport that answers every request with a canned response. */
class RespondingTransport : MessageTransport {
    val outbound = LinkedBlockingQueue<String>()

    override fun send(message: String) {
        outbound.add(message)
    }

    override fun receive(): String? {
        val message = outbound.take()
        val request = JsonRpcJson.json.decodeFromString(JsonRpcMessage.serializer(), message)
        if (request.id == null) return null
        return JsonRpcJson.json.encodeToString(
            JsonRpcMessage.serializer(),
            JsonRpcMessage(id = request.id, result = JsonPrimitive("hello world")),
        )
    }
}

class JsonRpcLauncherJvmTest {

    @Test
    fun launcherSendsTypedRequest() = runBlocking {
        val transport = RespondingTransport()
        val launcher = JsonRpcLauncher(transport)
        val t = kotlin.concurrent.thread(isDaemon = true) { launcher.listen() }
        val result = launcher.request("greet", "world", String.serializer(), String.serializer())
        assertEquals("hello world", result)
        t.interrupt()
    }
}