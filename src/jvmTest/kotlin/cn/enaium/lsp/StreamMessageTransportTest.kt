package cn.enaium.lsp.jsonrpc

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import kotlin.test.Test
import kotlin.test.assertEquals

class StreamMessageTransportTest {

    @Test
    fun framesMessageWithContentLength() {
        val out = ByteArrayOutputStream()
        val transport = StreamMessageTransport(ByteArrayInputStream(ByteArray(0)), out)
        transport.send("{\"jsonrpc\":\"2.0\",\"method\":\"ping\"}")

        val raw = out.toString(Charsets.US_ASCII)
        val header = raw.substringBefore("\r\n\r\n")
        assertEquals("Content-Length: 33", header)
    }

    @Test
    fun roundTripsMessageOverPipes() {
        val out = ByteArrayOutputStream()
        val sender = StreamMessageTransport(ByteArrayInputStream(ByteArray(0)), out)
        val body = """{"jsonrpc":"2.0","id":1,"method":"ping"}"""
        sender.send(body)

        val receiver = StreamMessageTransport(
            ByteArrayInputStream(out.toByteArray()),
            ByteArrayOutputStream(),
        )
        assertEquals(body, receiver.receive())
        assertEquals(null, receiver.receive())
    }
}