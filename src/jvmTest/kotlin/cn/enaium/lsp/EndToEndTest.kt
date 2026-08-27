package cn.enaium.lsp.jsonrpc

import cn.enaium.lsp.jsonrpc.JsonRpcLauncher
import cn.enaium.lsp.jsonrpc.StreamMessageTransport
import java.io.PipedInputStream
import java.io.PipedOutputStream
import kotlin.concurrent.thread
import kotlin.test.Test
import kotlin.test.assertTrue

class EndToEndTest {

    @Test
    fun fullRoundTripOverPipes() {
        val serverIn = PipedInputStream()
        val serverOut = PipedOutputStream()
        val clientOut = PipedOutputStream(serverIn)
        val clientIn = PipedInputStream(serverOut)

        val serverTransport = StreamMessageTransport(serverIn, serverOut)
        val launcher = JsonRpcLauncher(serverTransport)
        launcher.onRequest("echo") { params -> params }

        val t = thread(isDaemon = true) {
            launcher.listen()
        }

        val clientTransport = StreamMessageTransport(clientIn, clientOut)
        clientTransport.send("""{"jsonrpc":"2.0","id":1,"method":"echo","params":"hello"}""")
        val response = clientTransport.receive()
        assertTrue(response!!.contains("\"result\":\"hello\""), response)

        // close the client side to end the server loop
        clientOut.close()
        t.join(3000)
        assertTrue(!t.isAlive, "server listen loop should exit on EOF (state=${t.state})")
    }
}