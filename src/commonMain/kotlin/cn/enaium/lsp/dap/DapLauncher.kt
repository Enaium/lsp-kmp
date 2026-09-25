package cn.enaium.lsp.dap

import cn.enaium.lsp.dap.model.Event
import cn.enaium.lsp.dap.model.Request
import cn.enaium.lsp.dap.model.Response
import cn.enaium.lsp.jsonrpc.JsonRpcJson
import cn.enaium.lsp.jsonrpc.MessageTransport
import kotlinx.coroutines.CompletableDeferred
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Thrown when an adapter answers a request with `success: false`.
 */
class DapException(
    /** The `command` that failed. */
    val command: String,
    /** The adapter's own `message`, when it sent one. */
    message: String?,
) : RuntimeException(message ?: "DAP request '$command' failed")

/**
 * One end of a Debug Adapter Protocol conversation: dispatches inbound
 * requests and events to handlers and sends outbound requests, correlating
 * the responses by `request_seq`.
 *
 * DAP is **not** JSON-RPC 2.0. Every message carries `seq` and a `type` of
 * `request` / `response` / `event`; requests use `command` + `arguments` and
 * responses answer with `request_seq` + `success` + `body`. Real adapters
 * (debugpy, java-debug, …) validate that envelope, so it is what this speaks —
 * the LSP-shaped JSON-RPC launcher would only ever work against another
 * endpoint built on the same launcher.
 */
class DapServer(private val send: (JsonElement) -> Unit) {

    private val json = JsonRpcJson.json
    private val requestHandlers = mutableMapOf<String, (JsonElement?) -> JsonElement?>()
    private val eventHandlers = mutableMapOf<String, MutableList<(JsonElement?) -> Unit>>()
    private val pending = mutableMapOf<Int, CompletableDeferred<JsonElement?>>()
    private var nextSeq = 1

    /** Registers a handler for an inbound request `command`. */
    fun onRequest(command: String, handler: (JsonElement?) -> JsonElement?) {
        requestHandlers[command] = handler
    }

    /**
     * Registers a handler for an inbound `event`.
     *
     * Events are broadcasts, so several handlers may observe the same one —
     * the endpoint itself listens for some (`initialized`), and a host adding
     * its own must not silently replace that.
     */
    fun onEvent(event: String, handler: (JsonElement?) -> Unit) {
        eventHandlers.getOrPut(event) { mutableListOf() }.add(handler)
    }

    /**
     * Sends a request. The returned deferred resolves with its `body` (null
     * when the adapter sent none) or fails with a [DapException].
     */
    fun request(command: String, arguments: JsonElement?): CompletableDeferred<JsonElement?> {
        val seq = nextSeq++
        val deferred = CompletableDeferred<JsonElement?>()
        pending[seq] = deferred
        send(json.encodeToJsonElement(Request.serializer(), Request(seq = seq, command = command, arguments = arguments)))
        return deferred
    }

    /** Sends an event. */
    fun event(name: String, body: JsonElement?) {
        send(json.encodeToJsonElement(Event.serializer(), Event(seq = nextSeq++, event = name, body = body)))
    }

    /** Handles one inbound message. */
    fun handle(message: JsonElement) {
        val type = (message as? JsonObject)?.get("type")?.jsonPrimitive?.content ?: return
        when (type) {
            "request" -> handleRequest(json.decodeFromJsonElement(Request.serializer(), message))
            "response" -> handleResponse(json.decodeFromJsonElement(Response.serializer(), message))
            "event" -> {
                val event = json.decodeFromJsonElement(Event.serializer(), message)
                eventHandlers[event.event]?.forEach { it(event.body) }
            }
        }
    }

    private fun handleRequest(request: Request) {
        val handler = requestHandlers[request.command]
        val response = try {
            if (handler == null) {
                failed(request, "unsupported command: ${request.command}")
            } else {
                Response(
                    seq = nextSeq++,
                    request_seq = request.seq,
                    success = true,
                    command = request.command,
                    body = handler(request.arguments),
                )
            }
        } catch (e: Throwable) {
            failed(request, e.message ?: "request failed")
        }
        send(json.encodeToJsonElement(Response.serializer(), response))
    }

    private fun failed(request: Request, message: String) = Response(
        seq = nextSeq++,
        request_seq = request.seq,
        success = false,
        command = request.command,
        message = message,
    )

    private fun handleResponse(response: Response) {
        val deferred = pending.remove(response.request_seq) ?: return
        if (response.success) {
            deferred.complete(response.body)
        } else {
            deferred.completeExceptionally(DapException(response.command, response.message))
        }
    }
}

/**
 * Drives a [DapServer] over a [MessageTransport]: the DAP entry point for a
 * debug adapter process or a debug client, mirroring [cn.enaium.lsp.jsonrpc.JsonRpcLauncher]
 * on the LSP side.
 */
class DapLauncher(private val transport: MessageTransport) {

    private val json = JsonRpcJson.json
    private val server = DapServer { element ->
        transport.send(json.encodeToString(JsonElement.serializer(), element))
    }

    // ==================== Registration ====================

    fun onRequest(command: String, handler: (JsonElement?) -> JsonElement?) =
        server.onRequest(command, handler)

    fun onEvent(event: String, handler: (JsonElement?) -> Unit) = server.onEvent(event, handler)

    /** Registers a typed request handler; a null result means "no body". */
    fun <T, R> onRequest(
        command: String,
        paramsSerializer: KSerializer<T>,
        resultSerializer: KSerializer<R>,
        handler: (T) -> R?,
    ) {
        server.onRequest(command) { arguments ->
            handler(decodeParams(command, paramsSerializer, arguments))
                ?.let { json.encodeToJsonElement(resultSerializer, it) }
        }
    }

    /** Registers a handler for a request with no arguments and a typed result. */
    fun <R> onRequest(command: String, resultSerializer: KSerializer<R>, handler: () -> R) {
        server.onRequest(command) { json.encodeToJsonElement(resultSerializer, handler()) }
    }

    /** Registers a typed request handler that returns a raw body. */
    fun <T> onRequestJson(command: String, paramsSerializer: KSerializer<T>, handler: (T) -> JsonElement?) {
        server.onRequest(command) { arguments ->
            handler(decodeParams(command, paramsSerializer, arguments))
        }
    }

    // ==================== Sending ====================

    /**
     * Sends a request without waiting for its response; the deferred resolves
     * with the body, or fails with a [DapException].
     */
    fun send(command: String, arguments: JsonElement? = null): CompletableDeferred<JsonElement?> =
        server.request(command, arguments)

    /** Sends a request and awaits its body (null when the adapter sent none). */
    suspend fun request(command: String, arguments: JsonElement? = null): JsonElement? =
        server.request(command, arguments).await()

    /** Sends a request with typed arguments and awaits its raw body. */
    suspend fun <T> request(command: String, arguments: T?, argumentsSerializer: KSerializer<T>): JsonElement? =
        request(command, arguments?.let { json.encodeToJsonElement(argumentsSerializer, it) })

    /** Sends an event. */
    fun event(name: String, body: JsonElement? = null) = server.event(name, body)

    /**
     * Runs the receive loop until the transport closes. Blocks the caller.
     */
    fun listen() {
        while (true) {
            val text = transport.receive() ?: return
            server.handle(json.parseToJsonElement(text))
        }
    }

    private fun <T> decodeParams(command: String, serializer: KSerializer<T>, arguments: JsonElement?): T {
        if (arguments == null && !serializer.descriptor.isNullable) {
            throw IllegalArgumentException("Missing arguments for request '$command'")
        }
        return json.decodeFromJsonElement(serializer, arguments ?: JsonNull)
    }
}
