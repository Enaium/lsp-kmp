package cn.enaium.lsp.jsonrpc

import cn.enaium.lsp.jsonrpc.JsonRpcErrorCodes
import cn.enaium.lsp.jsonrpc.JsonRpcJson
import cn.enaium.lsp.jsonrpc.JsonRpcMessage
import cn.enaium.lsp.jsonrpc.JsonRpcServer
import cn.enaium.lsp.jsonrpc.MessageTransport
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull

/**
 * Binds a [JsonRpcServer] to a [MessageTransport] and drives the receive loop.
 *
 * This is the entry point for a language server or debug adapter process: it
 * decodes inbound framed messages, dispatches them to registered handlers, and
 * encodes outbound messages back onto the transport.
 */
class JsonRpcLauncher(
    private val transport: MessageTransport,
) {
    private val server = JsonRpcServer { message ->
        val json = JsonRpcJson.json.encodeToString(JsonRpcMessage.serializer(), message)
        transport.send(json)
    }

    fun <T, R> onRequest(method: String, paramsSerializer: KSerializer<T>, resultSerializer: KSerializer<R>, handler: (T) -> R?) {
        server.onRequest(method) { params ->
            val typed = params?.let { JsonRpcJson.json.decodeFromJsonElement(paramsSerializer, it) }
                ?: throw IllegalArgumentException("Missing params for request '$method'")
            val result = handler(typed)
            result?.let { JsonRpcJson.json.encodeToJsonElement(resultSerializer, it) } ?: JsonNull
        }
    }

    /** Register a request handler with a typed result and no params. */
    fun <R> onRequest(method: String, resultSerializer: KSerializer<R>, handler: () -> R) {
        server.onRequest(method) {
            JsonRpcJson.json.encodeToJsonElement(resultSerializer, handler())
        }
    }

    /** Register a request handler that decodes params but returns a raw JSON result. */
    fun <T> onRequestJson(method: String, paramsSerializer: KSerializer<T>, handler: (T) -> JsonElement?) {
        server.onRequest(method) { params ->
            val typed = params?.let { JsonRpcJson.json.decodeFromJsonElement(paramsSerializer, it) }
                ?: throw IllegalArgumentException("Missing params for request '$method'")
            handler(typed) ?: JsonNull
        }
    }

    /** Register a request handler with an untyped payload. */
    fun onRequest(method: String, handler: (JsonElement?) -> JsonElement?) {
        server.onRequest(method, handler)
    }

    /** Register a typed notification handler. */
    fun <T> onNotification(method: String, paramsSerializer: KSerializer<T>, handler: (T) -> Unit) {
        server.onNotification(method) { params ->
            val typed = params?.let { JsonRpcJson.json.decodeFromJsonElement(paramsSerializer, it) } ?: return@onNotification
            handler(typed)
        }
    }

    /** Register a notification handler with an untyped payload. */
    fun onNotification(method: String, handler: (JsonElement?) -> Unit) {
        server.onNotification(method, handler)
    }

    /** Send a request and await its result, decoded as [R]. */
    suspend fun <R> request(method: String, resultSerializer: KSerializer<R>): R {
        val deferred = server.request(method, null)
        return JsonRpcJson.json.decodeFromJsonElement(resultSerializer, deferred.await())
    }

    /** Send a request with a typed payload and await its typed result. */
    suspend fun <T, R> request(method: String, params: T?, paramsSerializer: KSerializer<T>, resultSerializer: KSerializer<R>): R {
        val element = params?.let { JsonRpcJson.json.encodeToJsonElement(paramsSerializer, it) }
        val deferred = server.request(method, element)
        return JsonRpcJson.json.decodeFromJsonElement(resultSerializer, deferred.await())
    }

    /** Send a typed notification. */
    fun <T> notify(method: String, params: T?, paramsSerializer: KSerializer<T>) {
        val element = params?.let { JsonRpcJson.json.encodeToJsonElement(paramsSerializer, it) }
        server.notify(method, element)
    }

    /** Send a raw notification. */
    fun notify(method: String, params: JsonElement?) {
        server.notify(method, params)
    }

    /**
     * Run the receive loop until the transport is closed. Blocks the calling
     * thread. Returns when the underlying stream reaches EOF.
     */
    fun listen() {
        while (true) {
            val line = transport.receive() ?: break
            val message = try {
                JsonRpcJson.json.decodeFromString(JsonRpcMessage.serializer(), line)
            } catch (e: Exception) {
                server.sendError(null, JsonRpcErrorCodes.PARSE_ERROR, "Parse error: ${e.message}")
                continue
            }
            server.handle(message)
        }
    }
}