package cn.enaium.lsp.jsonrpc

import java.io.EOFException
import java.io.InputStream
import java.io.OutputStream

/**
 * A [MessageTransport] over byte streams using the LSP/DAP framing:
 * `Content-Length: <N>` header followed by a blank line and N bytes of JSON.
 */
class StreamMessageTransport(
    private val input: InputStream,
    private val output: OutputStream,
) : MessageTransport {

    override fun send(message: String) {
        val body = message.toByteArray(Charsets.UTF_8)
        val header = "Content-Length: ${body.size}\r\n\r\n"
        output.write(header.toByteArray(Charsets.US_ASCII))
        output.write(body)
        output.flush()
    }

    override fun receive(): String? {
        val headers = readHeaders() ?: return null
        val length = headers
            .firstOrNull { it.startsWith("Content-Length:", ignoreCase = true) }
            ?.substringAfter(':')
            ?.trim()
            ?.toIntOrNull()
            ?: throw IllegalStateException("Missing or invalid Content-Length header")
        val body = ByteArray(length)
        var read = 0
        while (read < length) {
            val n = input.read(body, read, length - read)
            if (n < 0) throw EOFException("Stream closed mid-message")
            read += n
        }
        return body.toString(Charsets.UTF_8)
    }

    private fun readHeaders(): List<String>? {
        val headers = mutableListOf<String>()
        while (true) {
            val line = readLine() ?: return if (headers.isEmpty()) null else headers.toList()
            if (line.isEmpty()) return headers.toList()
            headers.add(line)
        }
    }

    private fun readLine(): String? {
        val sb = StringBuilder()
        while (true) {
            val b = input.read()
            if (b < 0) return if (sb.isEmpty()) null else sb.toString()
            if (b == '\n'.code) {
                if (sb.isNotEmpty() && sb.last() == '\r') sb.setLength(sb.length - 1)
                return sb.toString()
            }
            sb.append(b.toChar())
        }
    }
}