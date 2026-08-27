package cn.enaium.lsp.examples.stdio

import cn.enaium.lsp.LanguageServer
import cn.enaium.lsp.LanguageServerLauncher
import cn.enaium.lsp.TextDocumentService
import cn.enaium.lsp.WindowService
import cn.enaium.lsp.WorkspaceService
import cn.enaium.lsp.jsonrpc.JsonRpcJson
import cn.enaium.lsp.jsonrpc.StreamMessageTransport
import cn.enaium.lsp.model.*
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

/**
 * A minimal language server that runs over stdin/stdout.
 *
 * It responds to `initialize`/`shutdown`/`exit` and provides a trivial
 * completion and hover handler so the protocol can be exercised end-to-end.
 */
class ExampleServerLanguage : LanguageServer {
    private var shutdown = false

    override fun initialize(params: InitializeParams): InitializeResult {
        return InitializeResult(
            capabilities = ServerCapabilities(
                textDocumentSync = JsonPrimitive(TextDocumentSyncKind.Full),
                completionProvider = CompletionOptions(triggerCharacters = listOf(".")),
                hoverProvider = JsonPrimitive(true),
            ),
            serverInfo = ServerInfo(name = "lsp-kmp-example", version = "1.0.0"),
        )
    }

    override fun shutdown(): Any? {
        shutdown = true
        return null
    }

    override fun exit() {
        Runtime.getRuntime().halt(if (shutdown) 0 else 1)
    }

    override fun textDocumentService(): TextDocumentService? = ExampleTextDocumentService()

    override fun workspaceService(): WorkspaceService? = null
    override fun windowService(): WindowService? = null
}

/** A text document service that exposes completion and hover requests. */
class ExampleTextDocumentService : TextDocumentService {
    override fun completion(params: CompletionParams): JsonElement? {
        val list = CompletionList(
            isIncomplete = false,
            items = listOf(
                CompletionItem(label = "hello", kind = CompletionItemKind.Text, detail = "Example item"),
                CompletionItem(label = "world", kind = CompletionItemKind.Text, detail = "Another item"),
            ),
        )
        return JsonRpcJson.json.encodeToJsonElement(CompletionList.serializer(), list)
    }

    override fun hover(params: HoverParams): Hover? {
        return Hover(contents = JsonPrimitive("Example hover for ${params.textDocument.uri}"))
    }
}

fun main() {
    val transport = StreamMessageTransport(System.`in`, System.out)
    val launcher = LanguageServerLauncher(transport, ExampleServerLanguage())
    launcher.listen()
}