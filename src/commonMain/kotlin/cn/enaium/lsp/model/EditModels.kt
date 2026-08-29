package cn.enaium.lsp.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/** A content change event describing a single change to a text document. */
@Serializable
data class TextDocumentContentChangeEvent(
    val range: Range? = null,
    val rangeLength: Int? = null,
    val text: String,
)

/** Text documents are identified using a URI. On the protocol level URIs are passed as strings. */
@Serializable
data class TextDocumentIdentifier(
    val uri: String,
)

/** An item to transfer a text document from the client to the server. */
@Serializable
data class TextDocumentItem(
    val uri: String,
    val languageId: String,
    val version: Int,
    val text: String,
)

/** A parameter literal used in requests to pass a text document and a position inside that document. */
@Serializable
data class TextDocumentPositionParams(
    val textDocument: TextDocumentIdentifier,
    val position: Position,
)

/** The Completion request is sent from the client to the server to compute completion items at a given cursor position. */
@Serializable
data class CompletionParams(
    val textDocument: TextDocumentIdentifier,
    val position: Position,
    val workDoneToken: Token? = null,
    val partialResultToken: Token? = null,
    val context: CompletionContext? = null,
)

/** How the completion was triggered. */
@Serializable
data class CompletionContext(
    val triggerKind: Int,
    val triggerCharacter: String? = null,
)

/** A textual edit applicable to a text document. */
@Serializable
data class TextEdit(
    val range: Range,
    val newText: String,
)

/** Additional information that describes document changes. */
@Serializable
data class ChangeAnnotation(
    val label: String,
    val needsConfirmation: Boolean? = null,
    val description: String? = null,
)

/** A special text edit with an additional change annotation. */
@Serializable
data class AnnotatedTextEdit(
    val range: Range,
    val newText: String,
    val annotationId: String,
)

/** A special text edit to provide an insert and a replace operation. */
@Serializable
data class InsertReplaceEdit(
    val newText: String,
    val insert: Range,
    val replace: Range,
)

/** An identifier to denote a specific version of a text document. */
@Serializable
data class VersionedTextDocumentIdentifier(
    val uri: String,
    val version: Int? = null,
)

/** Describes textual changes on a single text document. */
@Serializable
data class TextDocumentEdit(
    val textDocument: VersionedTextDocumentIdentifier,
    val edits: List<TextEditOrSnippet>,
)

/** Options to create a file. */
@Serializable
data class CreateFileOptions(
    val overwrite: Boolean? = null,
    val ignoreIfExists: Boolean? = null,
)

/** Create file operation. */
@Serializable
data class CreateFile(
    val kind: String,
    val annotationId: String? = null,
    val uri: String,
    val options: CreateFileOptions? = null,
)

/** Rename file options. */
@Serializable
data class RenameFileOptions(
    val overwrite: Boolean? = null,
    val ignoreIfExists: Boolean? = null,
)

/** Rename file operation. */
@Serializable
data class RenameFile(
    val kind: String,
    val annotationId: String? = null,
    val oldUri: String,
    val newUri: String,
    val options: RenameFileOptions? = null,
)

/** Delete file options. */
@Serializable
data class DeleteFileOptions(
    val recursive: Boolean? = null,
    val ignoreIfNotExists: Boolean? = null,
)

/** Delete file operation. */
@Serializable
data class DeleteFile(
    val kind: String,
    val annotationId: String? = null,
    val uri: String,
    val options: DeleteFileOptions? = null,
)

/** A workspace edit represents changes to many resources managed in the workspace. */
@Serializable
data class WorkspaceEdit(
    val changes: Map<String, List<TextEdit>>? = null,
    val documentChanges: List<DocumentChange>? = null,
    val changeAnnotations: Map<String, ChangeAnnotation>? = null,
)

/** The options of a Workspace Edit. */
@Serializable
data class WorkspaceEditMetadata(
    val isRefactoring: Boolean? = null,
)