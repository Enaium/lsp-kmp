package cn.enaium.lsp.examples.socket

import cn.enaium.lsp.LanguageServer
import cn.enaium.lsp.LanguageServerLauncher
import cn.enaium.lsp.TextDocumentService
import cn.enaium.lsp.WindowService
import cn.enaium.lsp.WorkspaceService
import cn.enaium.lsp.jsonrpc.JsonRpcJson
import cn.enaium.lsp.jsonrpc.StreamMessageTransport
import cn.enaium.lsp.model.*
import kotlinx.serialization.json.JsonPrimitive
import java.net.ServerSocket

/**
 * A minimal language server that accepts a single client over a TCP socket.
 *
 * The LSP messages are framed with `Content-Length` headers exactly as over
 * stdio, so a client can connect via `nc localhost 8080` or any socket client.
 */
class ExampleServerLanguage : LanguageServer {
    private var shutdown = false

    override fun initialize(params: InitializeParams): InitializeResult {
        return InitializeResult(
            capabilities = ServerCapabilities(
                textDocumentSync = TextDocumentSync.Kind(TextDocumentSyncKind.Full),
                hoverProvider = HoverProvider.Enabled(true),
            ),
            serverInfo = ServerInfo(name = "lsp-kmp-socket-example", version = "1.0.0"),
        )
    }

    override fun shutdown(): Any? {
        shutdown = true
        return null
    }

    override fun exit() {
        Runtime.getRuntime().halt(if (shutdown) 0 else 1)
    }

    override fun textDocumentService(): TextDocumentService? = SocketTextDocumentService()

    override fun workspaceService(): WorkspaceService? = null
    override fun windowService(): WindowService? = null
}

/** A text document service that answers hover requests over the socket. */
class SocketTextDocumentService : TextDocumentService {
    override fun hover(params: HoverParams): Hover? {
        return Hover(
            contents = HoverContents.Markup(MarkupContent(MarkupKind.PlainText, "Socket hover for ${params.textDocument.uri}"))
        )
    }
}

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