package cn.enaium.lsp.examples.socket

import cn.enaium.lsp.LanguageServer
import cn.enaium.lsp.TextDocumentService
import cn.enaium.lsp.WindowService
import cn.enaium.lsp.WorkspaceService
import cn.enaium.lsp.model.*
import kotlin.system.exitProcess

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
        exitProcess(if (shutdown) 0 else 1)
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
