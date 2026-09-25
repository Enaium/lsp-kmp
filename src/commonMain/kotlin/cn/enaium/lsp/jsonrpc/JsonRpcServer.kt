package cn.enaium.lsp.jsonrpc

import kotlinx.coroutines.CompletableDeferred
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive

/** Receives outbound JSON-RPC messages. */
fun interface MessageConsumer {
    fun consume(message: JsonRpcMessage)
}

/**
 * A JSON-RPC 2.0 endpoint that both dispatches inbound messages to registered
 * handlers and sends outbound requests/notifications, tracking pending request
 * ids so responses can be correlated.
 *
 * Mirrors the role of lsp4j's `LocalEndpoint` + `RemoteEndpoint` combined.
 */
class JsonRpcServer(private val consumer: MessageConsumer) {
    private val requestHandlers = mutableMapOf<String, (JsonElement?) -> JsonElement?>()
    private val notificationHandlers = mutableMapOf<String, (JsonElement?) -> Unit>()
    private val pending = mutableMapOf<JsonRpcId, CompletableDeferred<JsonElement>>()
    private var nextId = 1L

    /** Register a handler for a request method. The handler returns the result. */
    fun onRequest(method: String, handler: (JsonElement?) -> JsonElement?) {
        requestHandlers[method] = handler
    }

    /** Register a handler for a notification method. */
    fun onNotification(method: String, handler: (JsonElement?) -> Unit) {
        notificationHandlers[method] = handler
    }

    /** Dispatch an inbound message. */
    fun handle(message: JsonRpcMessage) {
        when {
            message.method != null && message.id != null && !message.id.isNull() -> handleRequest(message)
            message.method != null -> handleNotification(message)
            message.id != null -> handleResponse(message)
            else -> sendError(null, JsonRpcErrorCodes.INVALID_REQUEST, "Invalid request")
        }
    }

    /** Send a request and return a deferred that resolves when the response arrives. */
    fun request(method: String, params: JsonElement?): CompletableDeferred<JsonElement> {
        val id = JsonRpcId(value = nextId++)
        val deferred = CompletableDeferred<JsonElement>()
        pending[id] = deferred
        consumer.consume(JsonRpcMessage(id = id, method = method, params = params))
        return deferred
    }

    /** Send a notification. */
    fun notify(method: String, params: JsonElement?) {
        consumer.consume(JsonRpcMessage(method = method, params = params))
    }

    /** Send a response to a request. */
    fun respond(id: JsonRpcId, result: JsonElement?) {
        consumer.consume(JsonRpcMessage(id = id, result = result ?: JsonNull))
    }

    /** Send an error response to a request. */
    fun sendError(id: JsonRpcId?, code: Int, message: String) {
        consumer.consume(
            JsonRpcMessage(id = id, error = JsonRpcError(code = code, message = message))
        )
    }

    private fun handleRequest(message: JsonRpcMessage) {
        val method = message.method ?: return
        val handler = requestHandlers[method]
        if (handler == null) {
            sendError(message.id, JsonRpcErrorCodes.METHOD_NOT_FOUND, "Method not found: $method")
            return
        }
        try {
            val result = handler(message.params)
            respond(message.id!!, result ?: JsonNull)
        } catch (e: Throwable) {
            sendError(message.id, JsonRpcErrorCodes.INTERNAL_ERROR, e.message ?: "Internal error")
        }
    }

    private fun handleNotification(message: JsonRpcMessage) {
        val method = message.method ?: return
        val handler = notificationHandlers[method] ?: return
        // A notification has no response to carry a failure, and the spec makes
        // notifications non-confirmable: a malformed one is dropped rather than
        // allowed to tear down the receive loop.
        try {
            handler(message.params)
        } catch (_: Throwable) {
        }
    }

    private fun handleResponse(message: JsonRpcMessage) {
        val id = message.id ?: return
        val deferred = pending.remove(id) ?: return
        if (message.error != null) {
            deferred.completeExceptionally(JsonRpcException(message.error))
        } else {
            deferred.complete(message.result ?: JsonNull)
        }
    }
}

/** Thrown when a JSON-RPC response carries an error. */
class JsonRpcException(val error: JsonRpcError) : RuntimeException(error.message) {
    val code: Int get() = error.code
}