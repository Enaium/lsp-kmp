package cn.enaium.lsp.jsonrpc

import cn.enaium.lsp.jsonrpc.JsonRpcLauncher
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** An in-memory [MessageTransport] for tests. */
class TestTransport : MessageTransport {
    val outbound = mutableListOf<String>()
    private val inbound = ArrayDeque<String>()

    fun enqueue(message: String) {
        inbound.addLast(message)
    }

    override fun send(message: String) {
        outbound.add(message)
    }

    override fun receive(): String? = inbound.removeFirstOrNull()
}

class JsonRpcServerTest {

    @Test
    fun requestIsAnswered() {
        val outbound = mutableListOf<JsonRpcMessage>()
        val server = JsonRpcServer { outbound.add(it) }
        server.onRequest("echo") { params -> params }
        server.handle(
            JsonRpcMessage(
                id = JsonRpcId(value = 1),
                method = "echo",
                params = JsonPrimitive("hi"),
            )
        )
        val response = outbound.first { it.id?.value == 1L }
        assertEquals(JsonPrimitive("hi"), response.result)
    }

    @Test
    fun unknownMethodReturnsMethodNotFound() {
        val outbound = mutableListOf<JsonRpcMessage>()
        val server = JsonRpcServer { outbound.add(it) }
        server.handle(JsonRpcMessage(id = JsonRpcId(value = 1), method = "nope"))
        val response = outbound.first()
        assertEquals(JsonRpcErrorCodes.METHOD_NOT_FOUND, response.error?.code)
    }

    @Test
    fun notificationIsDispatched() {
        val outbound = mutableListOf<JsonRpcMessage>()
        val server = JsonRpcServer { outbound.add(it) }
        var received: String? = null
        server.onNotification("test/notify") { received = (it as? JsonPrimitive)?.content }
        server.handle(JsonRpcMessage(method = "test/notify", params = JsonPrimitive("payload")))
        assertEquals("payload", received)
        assertTrue(outbound.isEmpty())
    }

    @Test
    fun responseCorrelatesPendingRequest() = runBlocking {
        val outbound = mutableListOf<JsonRpcMessage>()
        val server = JsonRpcServer { outbound.add(it) }
        val deferred = server.request("ping", null)
        val requestId = outbound.first().id!!
        server.handle(JsonRpcMessage(id = requestId, result = JsonPrimitive("pong")))
        assertEquals("pong", (deferred.await() as JsonPrimitive).content)
    }
}

class JsonRpcLauncherTest {

    @Test
    fun launcherDispatchesTypedRequest() {
        val transport = TestTransport()
        val launcher = JsonRpcLauncher(transport)
        launcher.onRequest("add", Int.serializer(), Int.serializer()) { a -> a + 1 }
        transport.enqueue(
            JsonRpcJson.json.encodeToString(
                JsonRpcMessage.serializer(),
                JsonRpcMessage(id = JsonRpcId(value = 1), method = "add", params = JsonPrimitive(41)),
            )
        )
        launcher.listen()
        val reply = JsonRpcJson.json.decodeFromString(JsonRpcMessage.serializer(), transport.outbound.first())
        assertEquals(JsonPrimitive(42), reply.result)
    }
}
