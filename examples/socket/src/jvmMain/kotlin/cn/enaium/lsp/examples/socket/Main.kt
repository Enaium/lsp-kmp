package cn.enaium.lsp.examples.socket

import cn.enaium.lsp.LanguageServerLauncher
import cn.enaium.lsp.jsonrpc.StreamMessageTransport
import java.net.ServerSocket

fun main(args: Array<String>) {
    val port = args.firstOrNull()?.toIntOrNull() ?: 8080
    println("Listening on port $port")
    ServerSocket(port).use { serverSocket ->
        // Accept a single client and serve it. Restart per connection.
        while (true) {
            val socket = serverSocket.accept()
            println("Accepted connection from ${socket.inetAddress.hostAddress}")
            val transport = StreamMessageTransport(socket.getInputStream(), socket.getOutputStream())
            val launcher = LanguageServerLauncher(transport, ExampleServerLanguage())
            launcher.listen()
            socket.close()
        }
    }
}
