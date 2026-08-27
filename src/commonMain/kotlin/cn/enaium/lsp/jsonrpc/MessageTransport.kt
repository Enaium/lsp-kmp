package cn.enaium.lsp.jsonrpc

/**
 * A byte-level transport for JSON-RPC messages.
 *
 * The LSP/DAP wire format frames each message as:
 * `Content-Length: <N>\r\n\r\n<json bytes>`
 *
 * A [MessageTransport] sends and receives serialized JSON strings.
 */
interface MessageTransport {
    /** Serialize and send a single message. */
    fun send(message: String)

    /**
     * Read the next message, or `null` when the stream is closed.
     * Blocks the calling thread while waiting for input.
     */
    fun receive(): String?
}