package cn.enaium.lsp.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/** Supports dynamic registration. */
@Serializable
data class DynamicRegistrationCapabilities(
    val dynamicRegistration: Boolean? = null,
)

/** Whether the client in general supports change annotations on text edits, create file, rename file and delete file changes. */
@Serializable
data class WorkspaceEditChangeAnnotationSupportCapabilities(
    val groupsOnLabel: Boolean? = null,
)

/** Capabilities specific to {@link WorkspaceEdit}s */
@Serializable
data class WorkspaceEditCapabilities(
    val documentChanges: Boolean? = null,
    val resourceOperations: List<String>? = null,
    val failureHandling: String? = null,
    val normalizesLineEndings: Boolean? = null,
    val changeAnnotationSupport: WorkspaceEditChangeAnnotationSupportCapabilities? = null,
    val snippetEditSupport: Boolean? = null,
    val metadataSupport: Boolean? = null,
)

/** The kind of resource operations supported by the client. */
object ResourceOperationKind {
    const val Create = "create"
    const val Rename = "rename"
    const val Delete = "delete"
}

/** The kind of failure handling supported by the client. */
object FailureHandlingKind {
    const val Abort = "abort"
    const val Transactional = "transactional"
    const val TextOnlyTransactional = "textOnlyTransactional"
    const val Undo = "undo"
}

/** Capabilities specific to the `workspace/didChangeConfiguration` notification. */
@Serializable
data class DidChangeConfigurationCapabilities(
    val dynamicRegistration: Boolean? = null,
)

/** Capabilities specific to the `workspace/didChangeWatchedFiles` notification. */
@Serializable
data class DidChangeWatchedFilesCapabilities(
    val dynamicRegistration: Boolean? = null,
    val relativePatternSupport: Boolean? = null,
)

/** The client support partial workspace symbols. */
@Serializable
data class WorkspaceSymbolResolveSupportCapabilities(
    val properties: List<String>,
)

/** Capabilities specific to the `workspace/symbol` request. */
@Serializable
data class SymbolCapabilities(
    val dynamicRegistration: Boolean? = null,
    val symbolKind: SymbolKindCapabilities? = null,
    val tagSupport: SymbolTagSupportCapabilities? = null,
    val resolveSupport: WorkspaceSymbolResolveSupportCapabilities? = null,
)

/** Capabilities specific to the `workspace/executeCommand` request. */
@Serializable
data class ExecuteCommandCapabilities(
    val dynamicRegistration: Boolean? = null,
)

/** Workspace specific client capabilities. */
@Serializable
data class WorkspaceClientCapabilities(
    val applyEdit: Boolean? = null,
    val workspaceEdit: WorkspaceEditCapabilities? = null,
    val didChangeConfiguration: DidChangeConfigurationCapabilities? = null,
    val didChangeWatchedFiles: DidChangeWatchedFilesCapabilities? = null,
    val symbol: SymbolCapabilities? = null,
    val executeCommand: ExecuteCommandCapabilities? = null,
    val workspaceFolders: Boolean? = null,
    val configuration: Boolean? = null,
    val semanticTokens: SemanticTokensWorkspaceCapabilities? = null,
    val codeLens: CodeLensWorkspaceCapabilities? = null,
    val fileOperations: FileOperationsWorkspaceCapabilities? = null,
    val inlayHint: InlayHintWorkspaceCapabilities? = null,
    val inlineValue: InlineValueWorkspaceCapabilities? = null,
    val diagnostics: DiagnosticWorkspaceCapabilities? = null,
    val foldingRange: FoldingRangeWorkspaceCapabilities? = null,
    val textDocumentContent: TextDocumentContentCapabilities? = null,
)

/** Defines which synchronization capabilities the client supports. */
@Serializable
data class SynchronizationCapabilities(
    val dynamicRegistration: Boolean? = null,
    val willSave: Boolean? = null,
    val willSaveWaitUntil: Boolean? = null,
    val didSave: Boolean? = null,
)

/** Defines which filters the client supports. */
@Serializable
data class FiltersCapabilities(
    val relativePatternSupport: Boolean? = null,
)

/** The client supports the following {@link CompletionItem} specific capabilities. */
@Serializable
data class CompletionItemCapabilities(
    val snippetSupport: Boolean? = null,
    val commitCharactersSupport: Boolean? = null,
    val documentationFormat: List<String>? = null,
    val deprecatedSupport: Boolean? = null,
    val preselectSupport: Boolean? = null,
    val tagSupport: CompletionItemTagSupportCapabilities? = null,
    val insertReplaceSupport: Boolean? = null,
    val resolveSupport: CompletionItemResolveSupportCapabilities? = null,
    val insertTextModeSupport: CompletionItemInsertTextModeSupportCapabilities? = null,
    val labelDetailsSupport: Boolean? = null,
)

/** Client supports the tag property on a completion item. */
@Serializable
data class CompletionItemTagSupportCapabilities(
    val valueSet: List<Int>,
)

/** Indicates which properties a client can resolve lazily on a completion item. */
@Serializable
data class CompletionItemResolveSupportCapabilities(
    val properties: List<String>,
)

/** The client supports the {@link CompletionItem#insertTextMode} property on a completion item. */
@Serializable
data class CompletionItemInsertTextModeSupportCapabilities(
    val valueSet: List<Int>,
)

/** The client supports the following {@link CompletionItemKind} specific capabilities. */
@Serializable
data class CompletionItemKindCapabilities(
    val valueSet: List<Int>? = null,
)

/** The client supports the following {@link CompletionList} specific capabilities. */
@Serializable
data class CompletionListCapabilities(
    val itemDefaults: List<String>? = null,
    val applyKindSupport: Boolean? = null,
)

/** Capabilities specific to the `textDocument/completion` */
@Serializable
data class CompletionCapabilities(
    val dynamicRegistration: Boolean? = null,
    val completionItem: CompletionItemCapabilities? = null,
    val completionItemKind: CompletionItemKindCapabilities? = null,
    val contextSupport: Boolean? = null,
    val insertTextMode: Int? = null,
    val completionList: CompletionListCapabilities? = null,
)

/** Capabilities specific to the `textDocument/hover` */
@Serializable
data class HoverCapabilities(
    val dynamicRegistration: Boolean? = null,
    val contentFormat: List<String>? = null,
)

/** The client supports the following {@link SignatureInformation} specific properties. */
@Serializable
data class SignatureInformationCapabilities(
    val documentationFormat: List<String>? = null,
    val parameterInformation: ParameterInformationCapabilities? = null,
    val activeParameterSupport: Boolean? = null,
    val noActiveParameterSupport: Boolean? = null,
)

/** Client capabilities specific to parameter information. */
@Serializable
data class ParameterInformationCapabilities(
    val labelOffsetSupport: Boolean? = null,
)

/** Capabilities specific to the `textDocument/signatureHelp` */
@Serializable
data class SignatureHelpCapabilities(
    val dynamicRegistration: Boolean? = null,
    val signatureInformation: SignatureInformationCapabilities? = null,
    val contextSupport: Boolean? = null,
)

/** Capabilities specific to the `textDocument/references` */
@Serializable
data class ReferencesCapabilities(
    val dynamicRegistration: Boolean? = null,
)

/** Capabilities specific to the `textDocument/documentHighlight` */
@Serializable
data class DocumentHighlightCapabilities(
    val dynamicRegistration: Boolean? = null,
)

/** Specific capabilities for the {@link SymbolKind}. */
@Serializable
data class SymbolKindCapabilities(
    val valueSet: List<Int>? = null,
)

/** Specific capabilities for the {@link SymbolTag}. */
@Serializable
data class SymbolTagSupportCapabilities(
    val valueSet: List<Int>,
)

/** Capabilities specific to the `textDocument/documentSymbol` */
@Serializable
data class DocumentSymbolCapabilities(
    val dynamicRegistration: Boolean? = null,
    val symbolKind: SymbolKindCapabilities? = null,
    val hierarchicalDocumentSymbolSupport: Boolean? = null,
    val tagSupport: SymbolTagSupportCapabilities? = null,
    val labelSupport: Boolean? = null,
)

/** Capabilities specific to the `textDocument/formatting` */
@Serializable
data class FormattingCapabilities(
    val dynamicRegistration: Boolean? = null,
)

/** Capabilities specific to the `textDocument/rangeFormatting` and `textDocument/rangesFormatting` */
@Serializable
data class RangeFormattingCapabilities(
    val dynamicRegistration: Boolean? = null,
    val rangesSupport: Boolean? = null,
)

/** Capabilities specific to the `textDocument/onTypeFormatting` */
@Serializable
data class OnTypeFormattingCapabilities(
    val dynamicRegistration: Boolean? = null,
)

/** Capabilities specific to the `textDocument/definition` */
@Serializable
data class DefinitionCapabilities(
    val dynamicRegistration: Boolean? = null,
    val linkSupport: Boolean? = null,
)

/** Capabilities specific to the `textDocument/declaration` */
@Serializable
data class DeclarationCapabilities(
    val dynamicRegistration: Boolean? = null,
    val linkSupport: Boolean? = null,
)

/** Capabilities specific to the `textDocument/typeDefinition` */
@Serializable
data class TypeDefinitionCapabilities(
    val dynamicRegistration: Boolean? = null,
    val linkSupport: Boolean? = null,
)

/** Capabilities specific to the `textDocument/implementation` */
@Serializable
data class ImplementationCapabilities(
    val dynamicRegistration: Boolean? = null,
    val linkSupport: Boolean? = null,
)

@Serializable
data class CodeActionKindCapabilities(
    val valueSet: List<String>,
)

@Serializable
data class CodeActionLiteralSupportCapabilities(
    val codeActionKind: CodeActionKindCapabilities? = null,
)

/** Whether the client supports resolving additional code action properties via a separate `codeAction/resolve` request. */
@Serializable
data class CodeActionResolveSupportCapabilities(
    val properties: List<String>,
)

/** Client supports the tag property on a code action. */
@Serializable
data class CodeActionTagSupportCapabilities(
    val valueSet: List<Int>,
)

/** Capabilities specific to the `textDocument/codeAction` */
@Serializable
data class CodeActionCapabilities(
    val dynamicRegistration: Boolean? = null,
    val codeActionLiteralSupport: CodeActionLiteralSupportCapabilities? = null,
    val isPreferredSupport: Boolean? = null,
    val disabledSupport: Boolean? = null,
    val dataSupport: Boolean? = null,
    val resolveSupport: CodeActionResolveSupportCapabilities? = null,
    val honorsChangeAnnotations: Boolean? = null,
    val documentationSupport: Boolean? = null,
    val tagSupport: CodeActionTagSupportCapabilities? = null,
)

/** Whether the client supports resolving additional code lens properties via a separate `codeLens/resolve` request. */
@Serializable
data class CodeLensResolveSupportCapabilities(
    val properties: List<String>,
)

/** Capabilities specific to the `textDocument/codeLens` */
@Serializable
data class CodeLensCapabilities(
    val dynamicRegistration: Boolean? = null,
    val resolveSupport: CodeLensResolveSupportCapabilities? = null,
)

/** Capabilities specific to the code lens requests scoped to the workspace. */
@Serializable
data class CodeLensWorkspaceCapabilities(
    val refreshSupport: Boolean? = null,
)

/** The client has support for file requests/notifications. */
@Serializable
data class FileOperationsWorkspaceCapabilities(
    val dynamicRegistration: Boolean? = null,
    val didCreate: Boolean? = null,
    val willCreate: Boolean? = null,
    val didRename: Boolean? = null,
    val willRename: Boolean? = null,
    val didDelete: Boolean? = null,
    val willDelete: Boolean? = null,
)

/** Capabilities specific to the `textDocument/documentLink` */
@Serializable
data class DocumentLinkCapabilities(
    val dynamicRegistration: Boolean? = null,
    val tooltipSupport: Boolean? = null,
)

/** Capabilities specific to the `textDocument/documentColor` and the `textDocument/colorPresentation` request. */
@Serializable
data class ColorProviderCapabilities(
    val dynamicRegistration: Boolean? = null,
)

/** Capabilities specific to the `textDocument/rename` */
@Serializable
data class RenameCapabilities(
    val dynamicRegistration: Boolean? = null,
    val prepareSupport: Boolean? = null,
    val prepareSupportDefaultBehavior: Int? = null,
    val honorsChangeAnnotations: Boolean? = null,
)

/** Capabilities specific to `textDocument/publishDiagnostics`. */
@Serializable
data class PublishDiagnosticsCapabilities(
    val relatedInformation: Boolean? = null,
    val tagSupport: BooleanOrTagSupport? = null,
    val versionSupport: Boolean? = null,
    val codeDescriptionSupport: Boolean? = null,
    val dataSupport: Boolean? = null,
)

@Serializable
data class DiagnosticsTagSupport(
    val valueSet: List<Int>,
)

/** Specific options for the folding range kind. */
@Serializable
data class FoldingRangeKindSupportCapabilities(
    val valueSet: List<String>? = null,
)

/** Specific options for the folding range. */
@Serializable
data class FoldingRangeSupportCapabilities(
    val collapsedText: Boolean? = null,
)

/** Capabilities specific to `textDocument/foldingRange` requests. */
@Serializable
data class FoldingRangeCapabilities(
    val dynamicRegistration: Boolean? = null,
    val rangeLimit: Int? = null,
    val lineFoldingOnly: Boolean? = null,
    val foldingRangeKind: FoldingRangeKindSupportCapabilities? = null,
    val foldingRange: FoldingRangeSupportCapabilities? = null,
)

/** Client workspace capabilities specific to folding ranges. */
@Serializable
data class FoldingRangeWorkspaceCapabilities(
    val refreshSupport: Boolean? = null,
)

/** Capabilities specific to the `textDocument/prepareTypeHierarchy`. */
@Serializable
data class TypeHierarchyCapabilities(
    val dynamicRegistration: Boolean? = null,
)

/** Capabilities specific to the `textDocument/prepareCallHierarchy`. */
@Serializable
data class CallHierarchyCapabilities(
    val dynamicRegistration: Boolean? = null,
)

/** Capabilities specific to `textDocument/selectionRange` requests */
@Serializable
data class SelectionRangeCapabilities(
    val dynamicRegistration: Boolean? = null,
)

@Serializable
data class SemanticTokensClientCapabilitiesRequestsFull(
    val delta: Boolean? = null,
)

@Serializable
data class SemanticTokensClientCapabilitiesRequests(
    val range: BooleanOrRaw? = null,
    val full: BooleanOrDelta? = null,
)

@Serializable
data class SemanticTokensCapabilities(
    val dynamicRegistration: Boolean? = null,
    val requests: SemanticTokensClientCapabilitiesRequests,
    val tokenTypes: List<String>,
    val tokenModifiers: List<String>,
    val formats: List<String>,
    val overlappingTokenSupport: Boolean? = null,
    val multilineTokenSupport: Boolean? = null,
    val serverCancelSupport: Boolean? = null,
    val augmentsSyntaxTokens: Boolean? = null,
)

/** Capabilities specific to the `textDocument/linkedEditingRange` request. */
@Serializable
data class LinkedEditingRangeCapabilities(
    val dynamicRegistration: Boolean? = null,
)

/** Capabilities specific to the semantic token requests scoped to the workspace. */
@Serializable
data class SemanticTokensWorkspaceCapabilities(
    val refreshSupport: Boolean? = null,
)

/** Capabilities specific to the `textDocument/moniker` request. */
@Serializable
data class MonikerCapabilities(
    val dynamicRegistration: Boolean? = null,
)

/** Show message request client capabilities */
@Serializable
data class WindowShowMessageRequestCapabilities(
    val messageActionItem: WindowShowMessageRequestActionItemCapabilities? = null,
)

/** Client capabilities for the show document request. */
@Serializable
data class ShowDocumentCapabilities(
    val support: Boolean = false,
)

/** Capabilities specific to the {@link MessageActionItem} type of show message request. */
@Serializable
data class WindowShowMessageRequestActionItemCapabilities(
    val additionalPropertiesSupport: Boolean? = null,
)

/** Client capabilities specific to regular expressions. */
@Serializable
data class RegularExpressionsCapabilities(
    val engine: String,
    val version: String? = null,
)

/** Regular Expression Engines. */
object RegularExpressionEngineKind {
    const val ES2020 = "ES2020"
}

/** Client capabilities specific to the used markdown parser. */
@Serializable
data class MarkdownCapabilities(
    val parser: String,
    val version: String? = null,
    val allowedTags: List<String>? = null,
)

/** Client capability that signals how the client handles stale requests. */
@Serializable
data class StaleRequestCapabilities(
    val cancel: Boolean = false,
    val retryOnContentModified: List<String>,
)

/** Text document specific client capabilities. */
@Serializable
data class TextDocumentClientCapabilities(
    val synchronization: SynchronizationCapabilities? = null,
    val filters: FiltersCapabilities? = null,
    val completion: CompletionCapabilities? = null,
    val hover: HoverCapabilities? = null,
    val signatureHelp: SignatureHelpCapabilities? = null,
    val references: ReferencesCapabilities? = null,
    val documentHighlight: DocumentHighlightCapabilities? = null,
    val documentSymbol: DocumentSymbolCapabilities? = null,
    val formatting: FormattingCapabilities? = null,
    val rangeFormatting: RangeFormattingCapabilities? = null,
    val onTypeFormatting: OnTypeFormattingCapabilities? = null,
    val declaration: DeclarationCapabilities? = null,
    val definition: DefinitionCapabilities? = null,
    val typeDefinition: TypeDefinitionCapabilities? = null,
    val implementation: ImplementationCapabilities? = null,
    val codeAction: CodeActionCapabilities? = null,
    val codeLens: CodeLensCapabilities? = null,
    val documentLink: DocumentLinkCapabilities? = null,
    val colorProvider: ColorProviderCapabilities? = null,
    val rename: RenameCapabilities? = null,
    val publishDiagnostics: PublishDiagnosticsCapabilities? = null,
    val foldingRange: FoldingRangeCapabilities? = null,
    val typeHierarchy: TypeHierarchyCapabilities? = null,
    val callHierarchy: CallHierarchyCapabilities? = null,
    val selectionRange: SelectionRangeCapabilities? = null,
    val semanticTokens: SemanticTokensCapabilities? = null,
    val moniker: MonikerCapabilities? = null,
    val linkedEditingRange: LinkedEditingRangeCapabilities? = null,
    val inlayHint: InlayHintCapabilities? = null,
    val inlineValue: InlineValueCapabilities? = null,
    val diagnostic: DiagnosticCapabilities? = null,
    val inlineCompletion: InlineCompletionCapabilities? = null,
)

/** Capabilities specific to the notebook document support. */
@Serializable
data class NotebookDocumentClientCapabilities(
    val synchronization: NotebookDocumentSyncClientCapabilities,
)

/** Window specific client capabilities. */
@Serializable
data class WindowClientCapabilities(
    val workDoneProgress: Boolean? = null,
    val showMessage: WindowShowMessageRequestCapabilities? = null,
    val showDocument: ShowDocumentCapabilities? = null,
)

/** General client capabilities. */
@Serializable
data class GeneralClientCapabilities(
    val regularExpressions: RegularExpressionsCapabilities? = null,
    val markdown: MarkdownCapabilities? = null,
    val staleRequestSupport: StaleRequestCapabilities? = null,
    val positionEncodings: List<String>? = null,
)

/** `ClientCapabilities` now define capabilities for dynamic registration, workspace and text document features the client supports. */
@Serializable
data class ClientCapabilities(
    val workspace: WorkspaceClientCapabilities? = null,
    val textDocument: TextDocumentClientCapabilities? = null,
    val notebookDocument: NotebookDocumentClientCapabilities? = null,
    val window: WindowClientCapabilities? = null,
    val general: GeneralClientCapabilities? = null,
    val experimental: JsonElement? = null,
)

/** The kind of a code action. */
object CodeActionKind {
    const val Empty = ""
    const val QuickFix = "quickfix"
    const val Refactor = "refactor"
    const val RefactorExtract = "refactor.extract"
    const val RefactorInline = "refactor.inline"
    const val RefactorMove = "refactor.move"
    const val RefactorRewrite = "refactor.rewrite"
    const val Source = "source"
    const val SourceOrganizeImports = "source.organizeImports"
    const val SourceFixAll = "source.fixAll"
    const val Notebook = "notebook"
}