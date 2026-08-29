package cn.enaium.lsp

import cn.enaium.lsp.model.*
import kotlinx.serialization.json.JsonElement

/**
 * The text document service handles notifications and requests for a specific
 * text document, as described by the LSP specification.
 *
 * Mirrors lsp4j's `TextDocumentService`. All methods have default
 * implementations so a server only overrides what it supports.
 */
interface TextDocumentService {
    // --- notifications ---
    fun didOpen(params: DidOpenTextDocumentParams) {}
    fun didChange(params: DidChangeTextDocumentParams) {}
    fun didClose(params: DidCloseTextDocumentParams) {}
    fun didSave(params: DidSaveTextDocumentParams) {}
    fun willSave(params: WillSaveTextDocumentParams) {}
    fun willSaveWaitUntil(params: WillSaveTextDocumentParams): List<TextEdit>? = null

    // --- basic requests ---
    fun completion(params: CompletionParams): CompletionResult? = null
    fun resolveCompletionItem(unresolved: CompletionItem): CompletionItem? = null
    fun hover(params: HoverParams): Hover? = null
    fun signatureHelp(params: SignatureHelpParams): SignatureHelp? = null
    fun declaration(params: DeclarationParams): LocationResult? = null
    fun definition(params: DefinitionParams): LocationResult? = null
    fun typeDefinition(params: TypeDefinitionParams): LocationResult? = null
    fun implementation(params: ImplementationParams): LocationResult? = null
    fun references(params: ReferenceParams): List<Location>? = null
    fun documentHighlight(params: DocumentHighlightParams): List<DocumentHighlight>? = null
    fun documentSymbol(params: DocumentSymbolParams): List<DocumentSymbolResult>? = null
    fun codeAction(params: CodeActionParams): List<CodeActionResult>? = null
    fun resolveCodeAction(unresolved: CodeAction): CodeAction? = null
    fun codeLens(params: CodeLensParams): List<CodeLens>? = null
    fun resolveCodeLens(unresolved: CodeLens): CodeLens? = null

    // --- formatting ---
    fun formatting(params: DocumentFormattingParams): List<TextEdit>? = null
    fun rangeFormatting(params: DocumentRangeFormattingParams): List<TextEdit>? = null
    fun rangesFormatting(params: DocumentRangesFormattingParams): List<TextEdit>? = null
    fun onTypeFormatting(params: DocumentOnTypeFormattingParams): List<TextEdit>? = null
    fun rename(params: RenameParams): WorkspaceEdit? = null
    fun prepareRename(params: PrepareRenameParams): PrepareRenameResultEither? = null
    fun linkedEditingRange(params: LinkedEditingRangeParams): LinkedEditingRanges? = null

    // --- links & colors ---
    fun documentLink(params: DocumentLinkParams): List<DocumentLink>? = null
    fun documentLinkResolve(params: DocumentLink): DocumentLink? = null
    fun documentColor(params: DocumentColorParams): List<ColorInformation>? = null
    fun colorPresentation(params: ColorPresentationParams): List<ColorPresentation>? = null
    fun foldingRange(params: FoldingRangeRequestParams): List<FoldingRange>? = null

    // --- hierarchy ---
    fun prepareTypeHierarchy(params: TypeHierarchyPrepareParams): List<TypeHierarchyItem>? = null
    fun typeHierarchySupertypes(params: TypeHierarchySupertypesParams): List<TypeHierarchyItem>? = null
    fun typeHierarchySubtypes(params: TypeHierarchySubtypesParams): List<TypeHierarchyItem>? = null
    fun prepareCallHierarchy(params: CallHierarchyPrepareParams): List<CallHierarchyItem>? = null
    fun callHierarchyIncomingCalls(params: CallHierarchyIncomingCallsParams): List<CallHierarchyIncomingCall>? = null
    fun callHierarchyOutgoingCalls(params: CallHierarchyOutgoingCallsParams): List<CallHierarchyOutgoingCall>? = null

    // --- ranges & tokens ---
    fun selectionRange(params: SelectionRangeParams): List<SelectionRange>? = null
    fun semanticTokensFull(params: SemanticTokensParams): SemanticTokens? = null
    fun semanticTokensFullDelta(params: SemanticTokensDeltaParams): SemanticTokensResult? = null
    fun semanticTokensRange(params: SemanticTokensRangeParams): SemanticTokens? = null
    fun moniker(params: MonikerParams): List<Moniker>? = null

    // --- hints & diagnostics ---
    fun inlayHint(params: InlayHintParams): List<InlayHint>? = null
    fun resolveInlayHint(unresolved: InlayHint): InlayHint? = null
    fun inlineValue(params: InlineValueParams): List<InlineValue>? = null
    fun diagnostic(params: DocumentDiagnosticParams): DocumentDiagnosticReport? = null
    fun inlineCompletion(params: InlineCompletionParams): InlineCompletionResult? = null
}

/** The workspace service handles workspace-wide requests and notifications. */
interface WorkspaceService {
    fun executeCommand(params: ExecuteCommandParams): JsonElement? = null
    fun symbol(params: WorkspaceSymbolParams): WorkspaceSymbolResult? = null
    fun resolveWorkspaceSymbol(workspaceSymbol: WorkspaceSymbol): WorkspaceSymbol? = null

    fun didChangeConfiguration(params: DidChangeConfigurationParams) {}
    fun didChangeWatchedFiles(params: DidChangeWatchedFilesParams) {}
    fun didChangeWorkspaceFolders(params: DidChangeWorkspaceFoldersParams) {}

    fun willCreateFiles(params: CreateFilesParams): WorkspaceEdit? = null
    fun didCreateFiles(params: CreateFilesParams) {}
    fun willRenameFiles(params: RenameFilesParams): WorkspaceEdit? = null
    fun didRenameFiles(params: RenameFilesParams) {}
    fun willDeleteFiles(params: DeleteFilesParams): WorkspaceEdit? = null
    fun didDeleteFiles(params: DeleteFilesParams) {}

    fun diagnostic(params: WorkspaceDiagnosticParams): WorkspaceDocumentDiagnosticReport? = null
    fun textDocumentContent(params: TextDocumentContentParams): TextDocumentContentResult? = null
}

/** The window service handles notifications sent to the client window. */
interface WindowService {
    fun showMessage(params: MessageParams) {}
    fun logMessage(params: MessageParams) {}
    fun publishDiagnostics(params: PublishDiagnosticsParams) {}
}

/** The client-facing interface the server uses to talk back to the client. */
interface LanguageClient {
    fun applyEdit(params: ApplyWorkspaceEditParams): ApplyWorkspaceEditResponse? = null
    fun registerCapability(params: RegistrationParams) {}
    fun unregisterCapability(params: UnregistrationParams) {}
    fun telemetryEvent(`object`: JsonElement?) {}
    fun publishDiagnostics(params: PublishDiagnosticsParams) {}
    fun showMessage(params: MessageParams) {}
    fun showMessageRequest(params: ShowMessageRequestParams): MessageActionItem? = null
    fun showDocument(params: ShowDocumentParams): ShowDocumentResult? = null
    fun logMessage(params: MessageParams) {}
    fun workspaceFolders(): List<WorkspaceFolder>? = null
    fun configuration(params: ConfigurationParams): List<JsonElement>? = null
    fun createProgress(params: WorkDoneProgressCreateParams) {}
    fun notifyProgress(params: ProgressParams) {}
    fun logTrace(params: LogTraceParams) {}
    fun refreshSemanticTokens() {}
    fun refreshCodeLenses() {}
    fun refreshInlayHints() {}
    fun refreshInlineValues() {}
    fun refreshDiagnostics() {}
    fun refreshFoldingRanges() {}
    fun refreshTextDocumentContent(params: TextDocumentContentRefreshParams) {}
}