package cn.enaium.lsp

import cn.enaium.lsp.jsonrpc.JsonRpcLauncher
import cn.enaium.lsp.jsonrpc.MessageTransport
import cn.enaium.lsp.model.*
import kotlinx.serialization.json.JsonElement

/**
 * Wires a [LanguageServer] to a [MessageTransport], registering the standard
 * LSP lifecycle and service methods, and provides a [LanguageClient] that
 * sends notifications/requests back to the client.
 */
class LanguageServerLauncher(
    transport: MessageTransport,
    private val languageServer: LanguageServer,
) {
    private val launcher = JsonRpcLauncher(transport)

    /** Client-facing facade that sends notifications/requests to the connected client. */
    val client: LanguageClient = object : LanguageClient {
        override fun showMessage(params: MessageParams) {
            launcher.notify("window/showMessage", params, MessageParams.serializer())
        }

        override fun logMessage(params: MessageParams) {
            launcher.notify("window/logMessage", params, MessageParams.serializer())
        }

        override fun publishDiagnostics(params: PublishDiagnosticsParams) {
            launcher.notify("textDocument/publishDiagnostics", params, PublishDiagnosticsParams.serializer())
        }

        override fun telemetryEvent(`object`: JsonElement?) {
            launcher.notify("telemetry/event", `object`)
        }
    }

    init {
        launcher.onRequest("initialize", InitializeParams.serializer(), InitializeResult.serializer()) {
            languageServer.initialize(it)
        }
        launcher.onRequest("shutdown") { languageServer.shutdown(); null }
        launcher.onNotification("exit") { languageServer.exit() }
        launcher.onNotification("initialized", InitializedParams.serializer()) { languageServer.initialized(it) }
        launcher.onNotification("window/workDoneProgress/cancel", WorkDoneProgressCancelParams.serializer()) {
            languageServer.cancelProgress(it)
        }
        launcher.onNotification("\$/setTrace", SetTraceParams.serializer()) { languageServer.setTrace(it) }

        val textDocument = languageServer.textDocumentService()
        if (textDocument != null) {
            launcher.onNotification("textDocument/didOpen", DidOpenTextDocumentParams.serializer()) { textDocument.didOpen(it) }
            launcher.onNotification("textDocument/didChange", DidChangeTextDocumentParams.serializer()) { textDocument.didChange(it) }
            launcher.onNotification("textDocument/didSave", DidSaveTextDocumentParams.serializer()) { textDocument.didSave(it) }
            launcher.onNotification("textDocument/didClose", DidCloseTextDocumentParams.serializer()) { textDocument.didClose(it) }
            launcher.onNotification("textDocument/willSave", WillSaveTextDocumentParams.serializer()) { textDocument.willSave(it) }
            launcher.onRequest("textDocument/willSaveWaitUntil", WillSaveTextDocumentParams.serializer(), list(TextEdit.serializer())) {
                textDocument.willSaveWaitUntil(it)
            }

            launcher.onRequest("textDocument/hover", HoverParams.serializer(), Hover.serializer()) { textDocument.hover(it) }
            launcher.onRequestJson("textDocument/completion", CompletionParams.serializer()) { textDocument.completion(it) }
            launcher.onRequest("completionItem/resolve", CompletionItem.serializer(), CompletionItem.serializer()) {
                textDocument.resolveCompletionItem(it)
            }
            launcher.onRequest("textDocument/signatureHelp", SignatureHelpParams.serializer(), SignatureHelp.serializer()) {
                textDocument.signatureHelp(it)
            }
            launcher.onRequestJson("textDocument/declaration", DeclarationParams.serializer()) { textDocument.declaration(it) }
            launcher.onRequestJson("textDocument/definition", DefinitionParams.serializer()) { textDocument.definition(it) }
            launcher.onRequestJson("textDocument/typeDefinition", TypeDefinitionParams.serializer()) { textDocument.typeDefinition(it) }
            launcher.onRequestJson("textDocument/implementation", ImplementationParams.serializer()) { textDocument.implementation(it) }
            launcher.onRequest("textDocument/references", ReferenceParams.serializer(), list(Location.serializer())) { textDocument.references(it) }
            launcher.onRequest("textDocument/documentHighlight", DocumentHighlightParams.serializer(), list(DocumentHighlight.serializer())) {
                textDocument.documentHighlight(it)
            }
            launcher.onRequestJson("textDocument/documentSymbol", DocumentSymbolParams.serializer()) { textDocument.documentSymbol(it) }
            launcher.onRequestJson("textDocument/codeAction", CodeActionParams.serializer()) { textDocument.codeAction(it) }
            launcher.onRequest("codeAction/resolve", CodeAction.serializer(), CodeAction.serializer()) {
                textDocument.resolveCodeAction(it)
            }
            launcher.onRequest("textDocument/codeLens", CodeLensParams.serializer(), list(CodeLens.serializer())) { textDocument.codeLens(it) }
            launcher.onRequest("codeLens/resolve", CodeLens.serializer(), CodeLens.serializer()) { textDocument.resolveCodeLens(it) }

            launcher.onRequest("textDocument/formatting", DocumentFormattingParams.serializer(), list(TextEdit.serializer())) {
                textDocument.formatting(it)
            }
            launcher.onRequest("textDocument/rangeFormatting", DocumentRangeFormattingParams.serializer(), list(TextEdit.serializer())) {
                textDocument.rangeFormatting(it)
            }
            launcher.onRequest("textDocument/rangesFormatting", DocumentRangesFormattingParams.serializer(), list(TextEdit.serializer())) {
                textDocument.rangesFormatting(it)
            }
            launcher.onRequest("textDocument/onTypeFormatting", DocumentOnTypeFormattingParams.serializer(), list(TextEdit.serializer())) {
                textDocument.onTypeFormatting(it)
            }
            launcher.onRequest("textDocument/rename", RenameParams.serializer(), WorkspaceEdit.serializer()) { textDocument.rename(it) }
            launcher.onRequestJson("textDocument/prepareRename", PrepareRenameParams.serializer()) { textDocument.prepareRename(it) }
            launcher.onRequest("textDocument/linkedEditingRange", LinkedEditingRangeParams.serializer(), LinkedEditingRanges.serializer()) {
                textDocument.linkedEditingRange(it)
            }

            launcher.onRequest("textDocument/documentLink", DocumentLinkParams.serializer(), list(DocumentLink.serializer())) {
                textDocument.documentLink(it)
            }
            launcher.onRequest("documentLink/resolve", DocumentLink.serializer(), DocumentLink.serializer()) {
                textDocument.documentLinkResolve(it)
            }
            launcher.onRequest("textDocument/documentColor", DocumentColorParams.serializer(), list(ColorInformation.serializer())) {
                textDocument.documentColor(it)
            }
            launcher.onRequest("textDocument/colorPresentation", ColorPresentationParams.serializer(), list(ColorPresentation.serializer())) {
                textDocument.colorPresentation(it)
            }
            launcher.onRequest("textDocument/foldingRange", FoldingRangeRequestParams.serializer(), list(FoldingRange.serializer())) {
                textDocument.foldingRange(it)
            }

            launcher.onRequest("textDocument/prepareTypeHierarchy", TypeHierarchyPrepareParams.serializer(), list(TypeHierarchyItem.serializer())) {
                textDocument.prepareTypeHierarchy(it)
            }
            launcher.onRequest("typeHierarchy/supertypes", TypeHierarchySupertypesParams.serializer(), list(TypeHierarchyItem.serializer())) {
                textDocument.typeHierarchySupertypes(it)
            }
            launcher.onRequest("typeHierarchy/subtypes", TypeHierarchySubtypesParams.serializer(), list(TypeHierarchyItem.serializer())) {
                textDocument.typeHierarchySubtypes(it)
            }
            launcher.onRequest("textDocument/prepareCallHierarchy", CallHierarchyPrepareParams.serializer(), list(CallHierarchyItem.serializer())) {
                textDocument.prepareCallHierarchy(it)
            }
            launcher.onRequest("callHierarchy/incomingCalls", CallHierarchyIncomingCallsParams.serializer(), list(CallHierarchyIncomingCall.serializer())) {
                textDocument.callHierarchyIncomingCalls(it)
            }
            launcher.onRequest("callHierarchy/outgoingCalls", CallHierarchyOutgoingCallsParams.serializer(), list(CallHierarchyOutgoingCall.serializer())) {
                textDocument.callHierarchyOutgoingCalls(it)
            }

            launcher.onRequest("textDocument/selectionRange", SelectionRangeParams.serializer(), list(SelectionRange.serializer())) {
                textDocument.selectionRange(it)
            }
            launcher.onRequest("textDocument/semanticTokens/full", SemanticTokensParams.serializer(), SemanticTokens.serializer()) {
                textDocument.semanticTokensFull(it)
            }
            launcher.onRequestJson("textDocument/semanticTokens/full/delta", SemanticTokensDeltaParams.serializer()) {
                textDocument.semanticTokensFullDelta(it)
            }
            launcher.onRequest("textDocument/semanticTokens/range", SemanticTokensRangeParams.serializer(), SemanticTokens.serializer()) {
                textDocument.semanticTokensRange(it)
            }
            launcher.onRequest("textDocument/moniker", MonikerParams.serializer(), list(Moniker.serializer())) { textDocument.moniker(it) }

            launcher.onRequest("textDocument/inlayHint", InlayHintParams.serializer(), list(InlayHint.serializer())) { textDocument.inlayHint(it) }
            launcher.onRequest("inlayHint/resolve", InlayHint.serializer(), InlayHint.serializer()) { textDocument.resolveInlayHint(it) }
            launcher.onRequest("textDocument/inlineValue", InlineValueParams.serializer(), list(JsonElement.serializer())) { textDocument.inlineValue(it) }
            launcher.onRequestJson("textDocument/diagnostic", DocumentDiagnosticParams.serializer()) { textDocument.diagnostic(it) }
            launcher.onRequestJson("textDocument/inlineCompletion", InlineCompletionParams.serializer()) {
                textDocument.inlineCompletion(it)
            }
        }

        val workspace = languageServer.workspaceService()
        if (workspace != null) {
            launcher.onRequestJson("workspace/executeCommand", ExecuteCommandParams.serializer()) { workspace.executeCommand(it) }
            launcher.onRequestJson("workspace/symbol", WorkspaceSymbolParams.serializer()) { workspace.symbol(it) }
            launcher.onRequest("workspaceSymbol/resolve", WorkspaceSymbol.serializer(), WorkspaceSymbol.serializer()) {
                workspace.resolveWorkspaceSymbol(it)
            }
            launcher.onNotification("workspace/didChangeConfiguration", DidChangeConfigurationParams.serializer()) {
                workspace.didChangeConfiguration(it)
            }
            launcher.onNotification("workspace/didChangeWatchedFiles", DidChangeWatchedFilesParams.serializer()) {
                workspace.didChangeWatchedFiles(it)
            }
            launcher.onNotification("workspace/didChangeWorkspaceFolders", DidChangeWorkspaceFoldersParams.serializer()) {
                workspace.didChangeWorkspaceFolders(it)
            }
            launcher.onRequest("workspace/willCreateFiles", CreateFilesParams.serializer(), WorkspaceEdit.serializer()) {
                workspace.willCreateFiles(it)
            }
            launcher.onNotification("workspace/didCreateFiles", CreateFilesParams.serializer()) { workspace.didCreateFiles(it) }
            launcher.onRequest("workspace/willRenameFiles", RenameFilesParams.serializer(), WorkspaceEdit.serializer()) {
                workspace.willRenameFiles(it)
            }
            launcher.onNotification("workspace/didRenameFiles", RenameFilesParams.serializer()) { workspace.didRenameFiles(it) }
            launcher.onRequest("workspace/willDeleteFiles", DeleteFilesParams.serializer(), WorkspaceEdit.serializer()) {
                workspace.willDeleteFiles(it)
            }
            launcher.onNotification("workspace/didDeleteFiles", DeleteFilesParams.serializer()) { workspace.didDeleteFiles(it) }
            launcher.onRequestJson("workspace/diagnostic", WorkspaceDiagnosticParams.serializer()) { workspace.diagnostic(it) }
            launcher.onRequest("workspace/textDocumentContent", TextDocumentContentParams.serializer(), TextDocumentContentResult.serializer()) {
                workspace.textDocumentContent(it)
            }
        }
    }

    /** Run the receive loop until the transport closes. */
    fun listen() = launcher.listen()
}

/** Helper to build a list serializer. */
private inline fun <reified T> list(serializer: kotlinx.serialization.KSerializer<T>): kotlinx.serialization.KSerializer<List<T>> =
    kotlinx.serialization.builtins.ListSerializer(serializer)