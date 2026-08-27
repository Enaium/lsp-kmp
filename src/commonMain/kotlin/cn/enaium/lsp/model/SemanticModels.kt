package cn.enaium.lsp.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/** The token types and modifiers that the client supports for semantic tokens. */
@Serializable
data class SemanticTokensLegend(
    val tokenTypes: List<String>,
    val tokenModifiers: List<String>,
)

/** Server supports providing semantic tokens for a full document. */
@Serializable
data class SemanticTokensServerFull(
    val delta: Boolean? = null,
)

/** Semantic tokens registration options including the legend and full/range support. */
@Serializable
data class SemanticTokensWithRegistrationOptions(
    val documentSelector: List<DocumentFilter>? = null,
    val workDoneProgress: Boolean? = null,
    val legend: SemanticTokensLegend,
    val range: JsonElement? = null,
    val full: JsonElement? = null,
    val id: String? = null,
)

/** The server can signal these capabilities. */
@Serializable
data class ServerCapabilities(
    val positionEncoding: String? = null,
    val textDocumentSync: JsonElement? = null,
    val notebookDocumentSync: NotebookDocumentSyncRegistrationOptions? = null,
    val hoverProvider: JsonElement? = null,
    val completionProvider: CompletionOptions? = null,
    val signatureHelpProvider: SignatureHelpOptions? = null,
    val definitionProvider: JsonElement? = null,
    val typeDefinitionProvider: JsonElement? = null,
    val implementationProvider: JsonElement? = null,
    val referencesProvider: JsonElement? = null,
    val documentHighlightProvider: JsonElement? = null,
    val documentSymbolProvider: JsonElement? = null,
    val workspaceSymbolProvider: JsonElement? = null,
    val codeActionProvider: JsonElement? = null,
    val codeLensProvider: CodeLensOptions? = null,
    val documentFormattingProvider: JsonElement? = null,
    val documentRangeFormattingProvider: JsonElement? = null,
    val documentOnTypeFormattingProvider: DocumentOnTypeFormattingOptions? = null,
    val renameProvider: JsonElement? = null,
    val documentLinkProvider: DocumentLinkOptions? = null,
    val colorProvider: JsonElement? = null,
    val foldingRangeProvider: JsonElement? = null,
    val declarationProvider: JsonElement? = null,
    val executeCommandProvider: ExecuteCommandOptions? = null,
    val workspace: WorkspaceServerCapabilities? = null,
    val typeHierarchyProvider: JsonElement? = null,
    val callHierarchyProvider: JsonElement? = null,
    val selectionRangeProvider: JsonElement? = null,
    val linkedEditingRangeProvider: JsonElement? = null,
    val semanticTokensProvider: SemanticTokensWithRegistrationOptions? = null,
    val monikerProvider: JsonElement? = null,
    val inlayHintProvider: JsonElement? = null,
    val inlineValueProvider: JsonElement? = null,
    val diagnosticProvider: DiagnosticRegistrationOptions? = null,
    val inlineCompletionProvider: JsonElement? = null,
    val textDocument: TextDocumentServerCapabilities? = null,
    val experimental: JsonElement? = null,
)

/** Workspace specific server capabilities. */
@Serializable
data class WorkspaceServerCapabilities(
    val workspaceFolders: WorkspaceFoldersOptions? = null,
    val fileOperations: FileOperationsServerCapabilities? = null,
    val textDocumentContent: TextDocumentContentRegistrationOptions? = null,
)

/** Text document specific server capabilities. */
@Serializable
data class TextDocumentServerCapabilities(
    val diagnostic: DiagnosticServerCapabilities? = null,
)

/** The show message request is sent from a server to a client to ask the client to display a message and wait for an answer. */
@Serializable
data class ShowMessageRequestParams(
    val type: Int,
    val message: String,
    val actions: List<MessageActionItem>? = null,
)

/** The signature help request is sent from the client to the server to request signature information at a given cursor position. */
@Serializable
data class SignatureHelpParams(
    val textDocument: TextDocumentIdentifier,
    val position: Position,
    val workDoneToken: JsonElement? = null,
    val context: SignatureHelpContext? = null,
)

/** The request is sent from the client to the server to resolve semantic tokens for a given whole file. */
@Serializable
data class SemanticTokensParams(
    val workDoneToken: JsonElement? = null,
    val partialResultToken: JsonElement? = null,
    val textDocument: TextDocumentIdentifier,
)

/** The result of a `textDocument/semanticTokens` request. */
@Serializable
data class SemanticTokens(
    val resultId: String? = null,
    val data: List<Int>,
)

/** A partial result of a `textDocument/semanticTokens` request. */
@Serializable
data class SemanticTokensPartialResult(
    val data: List<Int>,
)

/** The request is sent from the client to the server to resolve semantic token deltas for a given whole file. */
@Serializable
data class SemanticTokensDeltaParams(
    val workDoneToken: JsonElement? = null,
    val partialResultToken: JsonElement? = null,
    val textDocument: TextDocumentIdentifier,
    val previousResultId: String,
)

/** An edit to transform a previous semantic tokens result into a new result. */
@Serializable
data class SemanticTokensEdit(
    val start: Int,
    val deleteCount: Int,
    val data: List<Int>? = null,
)

/** The result of a `textDocument/semanticTokens/full/delta` request. */
@Serializable
data class SemanticTokensDelta(
    val resultId: String? = null,
    val edits: List<SemanticTokensEdit>,
)

/** A partial result of a `textDocument/semanticTokens/full/delta` request. */
@Serializable
data class SemanticTokensDeltaPartialResult(
    val edits: List<SemanticTokensEdit>,
)

/** The request is sent from the client to the server to resolve semantic tokens for a range in a given file. */
@Serializable
data class SemanticTokensRangeParams(
    val workDoneToken: JsonElement? = null,
    val partialResultToken: JsonElement? = null,
    val textDocument: TextDocumentIdentifier,
    val range: Range,
)

/** Additional information about the context in which a signature help request was triggered. */
@Serializable
data class SignatureHelpContext(
    val triggerKind: Int,
    val triggerCharacter: String? = null,
    val isRetrigger: Boolean = false,
    val activeSignatureHelp: SignatureHelp? = null,
)

/** Signature help represents the signature of something callable. */
@Serializable
data class SignatureHelp(
    val signatures: List<SignatureInformation>,
    val activeSignature: Int? = null,
    val activeParameter: Int? = null,
)

/** Signature help options. */
@Serializable
data class SignatureHelpOptions(
    val workDoneProgress: Boolean? = null,
    val triggerCharacters: List<String>? = null,
    val retriggerCharacters: List<String>? = null,
)

/** Represents the signature of something callable. */
@Serializable
data class SignatureInformation(
    val label: String,
    val documentation: JsonElement? = null,
    val parameters: List<ParameterInformation>? = null,
    val activeParameter: Int? = null,
)

/** Representation of an item that carries type information. */
@Serializable
data class TypeHierarchyItem(
    val name: String,
    val detail: String? = null,
    val kind: Int,
    val tags: List<Int>? = null,
    val uri: String,
    val range: Range,
    val selectionRange: Range,
    val data: JsonElement? = null,
)

/** Represents programming constructs like variables, classes, interfaces etc. that appear in a document. */
@Serializable
data class DocumentSymbol(
    val name: String,
    val kind: Int,
    val range: Range,
    val selectionRange: Range,
    val detail: String? = null,
    val tags: List<Int>? = null,
    val deprecated: Boolean? = null,
    val children: List<DocumentSymbol>? = null,
)

/** Represents information about programming constructs like variables, classes, interfaces etc. */
@Serializable
data class SymbolInformation(
    val name: String,
    val kind: Int,
    val tags: List<Int>? = null,
    val deprecated: Boolean? = null,
    val location: Location,
    val containerName: String? = null,
)

/** A special workspace symbol location that supports locations without a range. */
@Serializable
data class WorkspaceSymbolLocation(
    val uri: String,
)

/** A special workspace symbol that supports locations without a range. */
@Serializable
data class WorkspaceSymbol(
    val name: String,
    val kind: Int,
    val tags: List<Int>? = null,
    val location: JsonElement? = null,
    val containerName: String? = null,
    val data: JsonElement? = null,
)