package cn.enaium.lsp.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/** The parameters of a `textDocument/documentColor` request. */
@Serializable
data class DocumentColorParams(
    val textDocument: TextDocumentIdentifier,
    val workDoneToken: Token? = null,
    val partialResultToken: Token? = null,
)

/** Represents a color range in a document. */
@Serializable
data class ColorInformation(
    val range: Range,
    val color: Color,
)

/** Represents a color in RGBA space. */
@Serializable
data class Color(
    val red: Double,
    val green: Double,
    val blue: Double,
    val alpha: Double,
)

/** The parameters of a `textDocument/colorPresentation` request. */
@Serializable
data class ColorPresentationParams(
    val textDocument: TextDocumentIdentifier,
    val color: Color,
    val range: Range,
    val workDoneToken: Token? = null,
    val partialResultToken: Token? = null,
)

/** The result of a `textDocument/colorPresentation` request. */
@Serializable
data class ColorPresentation(
    val label: String,
    val textEdit: TextEdit? = null,
    val additionalTextEdits: List<TextEdit>? = null,
)

/** The parameters of a `textDocument/foldingRange` request. */
@Serializable
data class FoldingRangeRequestParams(
    val textDocument: TextDocumentIdentifier,
    val workDoneToken: Token? = null,
    val partialResultToken: Token? = null,
)

/** A set of predefined range kinds. */
object FoldingRangeKind {
    const val Comment = "comment"
    const val Imports = "imports"
    const val Region = "region"
}

/** Represents a folding range. */
@Serializable
data class FoldingRange(
    val startLine: Int,
    val endLine: Int,
    val startCharacter: Int? = null,
    val endCharacter: Int? = null,
    val kind: String? = null,
    val collapsedText: String? = null,
)

/** The parameters of a `textDocument/prepareCallHierarchy` request. */
@Serializable
data class CallHierarchyPrepareParams(
    val textDocument: TextDocumentIdentifier,
    val position: Position,
    val workDoneToken: Token? = null,
)

/** The parameters of a `callHierarchy/incomingCalls` request. */
@Serializable
data class CallHierarchyIncomingCallsParams(
    val item: CallHierarchyItem,
    val workDoneToken: Token? = null,
    val partialResultToken: Token? = null,
)

/** The parameters of a `callHierarchy/outgoingCalls` request. */
@Serializable
data class CallHierarchyOutgoingCallsParams(
    val item: CallHierarchyItem,
    val workDoneToken: Token? = null,
    val partialResultToken: Token? = null,
)

/** Represents an incoming call, e.g. a caller of a method or constructor. */
@Serializable
data class CallHierarchyIncomingCall(
    val from: CallHierarchyItem,
    val fromRanges: List<Range>,
)

/** Represents an outgoing call, e.g. calling a getter from a method or a method from a constructor. */
@Serializable
data class CallHierarchyOutgoingCall(
    val to: CallHierarchyItem,
    val fromRanges: List<Range>,
)

/** The result of a `textDocument/prepareCallHierarchy` request. */
@Serializable
data class CallHierarchyItem(
    val name: String,
    val detail: String? = null,
    val kind: Int,
    val tags: List<Int>? = null,
    val uri: String,
    val range: Range,
    val selectionRange: Range,
    val data: JsonElement? = null,
)

/** A parameter literal used in selection range requests. */
@Serializable
data class SelectionRangeParams(
    val textDocument: TextDocumentIdentifier,
    val positions: List<Position>,
    val workDoneToken: Token? = null,
    val partialResultToken: Token? = null,
)

/** Selection range options. */
@Serializable
data class SelectionRangeOptions(
    val workDoneProgress: Boolean? = null,
)

/** Selection range registration options. */
@Serializable
data class SelectionRangeRegistrationOptions(
    val documentSelector: List<DocumentFilter>? = null,
    val workDoneProgress: Boolean? = null,
    val id: String? = null,
)

/** A selection range represents a part of a selection hierarchy. */
@Serializable
data class SelectionRange(
    val range: Range,
    val parent: SelectionRange? = null,
)

/** Hover options. */
@Serializable
data class HoverOptions(
    val workDoneProgress: Boolean? = null,
)

/** Hover registration options. */
@Serializable
data class HoverRegistrationOptions(
    val documentSelector: List<DocumentFilter>? = null,
    val workDoneProgress: Boolean? = null,
)

/** The parameters of a `textDocument/hover` request. */
@Serializable
data class HoverParams(
    val textDocument: TextDocumentIdentifier,
    val position: Position,
    val workDoneToken: Token? = null,
)

/** Declaration options. */
@Serializable
data class DeclarationOptions(
    val workDoneProgress: Boolean? = null,
)

/** Declaration registration options. */
@Serializable
data class DeclarationRegistrationOptions(
    val documentSelector: List<DocumentFilter>? = null,
    val workDoneProgress: Boolean? = null,
    val id: String? = null,
)

/** The parameters of a `textDocument/declaration` request. */
@Serializable
data class DeclarationParams(
    val textDocument: TextDocumentIdentifier,
    val position: Position,
    val workDoneToken: Token? = null,
    val partialResultToken: Token? = null,
)

/** Definition options. */
@Serializable
data class DefinitionOptions(
    val workDoneProgress: Boolean? = null,
)

/** Definition registration options. */
@Serializable
data class DefinitionRegistrationOptions(
    val documentSelector: List<DocumentFilter>? = null,
    val workDoneProgress: Boolean? = null,
)

/** The parameters of a `textDocument/definition` request. */
@Serializable
data class DefinitionParams(
    val textDocument: TextDocumentIdentifier,
    val position: Position,
    val workDoneToken: Token? = null,
    val partialResultToken: Token? = null,
)

/** Type definition options. */
@Serializable
data class TypeDefinitionOptions(
    val workDoneProgress: Boolean? = null,
)

/** Type definition registration options. */
@Serializable
data class TypeDefinitionRegistrationOptions(
    val documentSelector: List<DocumentFilter>? = null,
    val workDoneProgress: Boolean? = null,
    val id: String? = null,
)

/** The parameters of a `textDocument/typeDefinition` request. */
@Serializable
data class TypeDefinitionParams(
    val textDocument: TextDocumentIdentifier,
    val position: Position,
    val workDoneToken: Token? = null,
    val partialResultToken: Token? = null,
)

/** Implementation options. */
@Serializable
data class ImplementationOptions(
    val workDoneProgress: Boolean? = null,
)

/** Implementation registration options. */
@Serializable
data class ImplementationRegistrationOptions(
    val documentSelector: List<DocumentFilter>? = null,
    val workDoneProgress: Boolean? = null,
    val id: String? = null,
)

/** The parameters of a `textDocument/implementation` request. */
@Serializable
data class ImplementationParams(
    val textDocument: TextDocumentIdentifier,
    val position: Position,
    val workDoneToken: Token? = null,
    val partialResultToken: Token? = null,
)

/** Document highlight options. */
@Serializable
data class DocumentHighlightOptions(
    val workDoneProgress: Boolean? = null,
)

/** Document highlight registration options. */
@Serializable
data class DocumentHighlightRegistrationOptions(
    val documentSelector: List<DocumentFilter>? = null,
    val workDoneProgress: Boolean? = null,
)

/** The parameters of a `textDocument/documentHighlight` request. */
@Serializable
data class DocumentHighlightParams(
    val textDocument: TextDocumentIdentifier,
    val position: Position,
    val workDoneToken: Token? = null,
    val partialResultToken: Token? = null,
)

/** Moniker options. */
@Serializable
data class MonikerOptions(
    val workDoneProgress: Boolean? = null,
)

/** Moniker registration options. */
@Serializable
data class MonikerRegistrationOptions(
    val documentSelector: List<DocumentFilter>? = null,
    val workDoneProgress: Boolean? = null,
)

/** The parameters of a `textDocument/moniker` request. */
@Serializable
data class MonikerParams(
    val textDocument: TextDocumentIdentifier,
    val position: Position,
    val workDoneToken: Token? = null,
    val partialResultToken: Token? = null,
)

/** Moniker uniqueness level to define scope of the moniker. */
object UniquenessLevel {
    const val Document = "document"
    const val Project = "project"
    const val Group = "group"
    const val Scheme = "scheme"
    const val Global = "global"
}

/** The moniker kind. */
object MonikerKind {
    const val Import = "import"
    const val Export = "export"
    const val Local = "local"
}

/** Moniker definition to match LSIF 0.5 moniker definition. */
@Serializable
data class Moniker(
    val scheme: String,
    val identifier: String,
    val unique: String,
    val kind: String? = null,
)

/** The parameters of a `window/workDoneProgress/create` request. */
@Serializable
data class WorkDoneProgressCreateParams(
    val token: Token? = null,
)

/** The parameters of a `window/workDoneProgress/cancel` notification. */
@Serializable
data class WorkDoneProgressCancelParams(
    val token: Token? = null,
)

/** Params to show a document. */
@Serializable
data class ShowDocumentParams(
    val uri: String,
    val external: Boolean? = null,
    val takeFocus: Boolean? = null,
    val selection: Range? = null,
)

/** The result of a show document request. */
@Serializable
data class ShowDocumentResult(
    val success: Boolean,
)