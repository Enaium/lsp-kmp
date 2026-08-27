package cn.enaium.lsp.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/** The progress notification payload to start work done progress. */
@Serializable
data class WorkDoneProgressBegin(
    val title: String,
    val cancellable: Boolean? = null,
    val message: String? = null,
    val percentage: Int? = null,
)

/** The notification payload about progress reporting. */
@Serializable
data class WorkDoneProgressReport(
    val cancellable: Boolean? = null,
    val message: String? = null,
    val percentage: Int? = null,
)

/** The notification payload signaling the end of a progress reporting. */
@Serializable
data class WorkDoneProgressEnd(
    val message: String? = null,
)

/** A progress notification payload. */
@Serializable
data class ProgressParams(
    val token: JsonElement? = null,
    val value: JsonElement? = null,
)

/** An error response for an `initialize` request. */
@Serializable
data class InitializeError(
    val retry: Boolean = false,
)

/** The params sent in an `initialize` request. */
@Serializable
data class InitializeParams(
    val workDoneToken: JsonElement? = null,
    val processId: Int? = null,
    val rootPath: String? = null,
    val rootUri: String? = null,
    val initializationOptions: JsonElement? = null,
    val capabilities: ClientCapabilities,
    val clientInfo: ClientInfo? = null,
    val locale: String? = null,
    val trace: String? = null,
    val workspaceFolders: List<WorkspaceFolder>? = null,
)

/** The result of an `initialize` request. */
@Serializable
data class InitializeResult(
    val capabilities: ServerCapabilities,
    val serverInfo: ServerInfo? = null,
)

/** The params sent in an `initialized` notification. */
@Serializable
class InitializedParams

/** Information about the client. */
@Serializable
data class ClientInfo(
    val name: String,
    val version: String? = null,
)

/** Information about the server. */
@Serializable
data class ServerInfo(
    val name: String,
    val version: String? = null,
)

/** Represents a location inside a resource, such as a line inside a text file. */
@Serializable
data class Location(
    val uri: String,
    val range: Range,
)

/** Represents a link between a source and a target location. */
@Serializable
data class LocationLink(
    val originSelectionRange: Range? = null,
    val targetUri: String,
    val targetRange: Range,
    val targetSelectionRange: Range,
)

/** An item to choose between actions for a message. */
@Serializable
data class MessageActionItem(
    val title: String,
)

/** The show message / log message notification params. */
@Serializable
data class MessageParams(
    val type: Int? = null,
    val message: String,
)

/** A notification to log the trace of the server's execution. */
@Serializable
data class LogTraceParams(
    val message: String,
    val verbose: String? = null,
)

/** A notification used by the client to modify the trace setting of the server. */
@Serializable
data class SetTraceParams(
    val value: String,
)

/** Represents a parameter of a callable-signature. */
@Serializable
data class ParameterInformation(
    val label: JsonElement? = null,
    val documentation: JsonElement? = null,
)

/** Position in a text document expressed as zero-based line and character offset. */
@Serializable
data class Position(
    val line: Int = 0,
    val character: Int = 0,
)

/** The params of a `textDocument/publishDiagnostics` notification. */
@Serializable
data class PublishDiagnosticsParams(
    val uri: String,
    val diagnostics: List<Diagnostic>,
    val version: Int? = null,
)

/** A range in a text document expressed as a start and end position. */
@Serializable
data class Range(
    val start: Position,
    val end: Position,
)

/** The context of a `textDocument/references` request. */
@Serializable
data class ReferenceContext(
    val includeDeclaration: Boolean = false,
)

/** The options of a `textDocument/references` request. */
@Serializable
data class ReferenceOptions(
    val workDoneProgress: Boolean? = null,
)

/** The registration options of a `textDocument/references` request. */
@Serializable
data class ReferenceRegistrationOptions(
    val documentSelector: List<DocumentFilter>? = null,
    val workDoneProgress: Boolean? = null,
)

/** The params of a `textDocument/references` request. */
@Serializable
data class ReferenceParams(
    val textDocument: TextDocumentIdentifier,
    val position: Position,
    val workDoneToken: JsonElement? = null,
    val partialResultToken: JsonElement? = null,
    val context: ReferenceContext,
)

/** The params of a `textDocument/prepareRename` request. */
@Serializable
data class PrepareRenameParams(
    val textDocument: TextDocumentIdentifier,
    val position: Position,
    val workDoneToken: JsonElement? = null,
)

/** One of the result types of the `textDocument/prepareRename` request. */
@Serializable
data class PrepareRenameResult(
    val range: Range,
    val placeholder: String,
)

/** One of the result types of the `textDocument/prepareRename` request. */
@Serializable
data class PrepareRenameDefaultBehavior(
    val defaultBehavior: Boolean = false,
)

/** The params of a `textDocument/rename` request. */
@Serializable
data class RenameParams(
    val textDocument: TextDocumentIdentifier,
    val position: Position,
    val workDoneToken: JsonElement? = null,
    val newName: String,
)

/** The params of a `textDocument/linkedEditingRange` request. */
@Serializable
data class LinkedEditingRangeParams(
    val textDocument: TextDocumentIdentifier,
    val position: Position,
    val workDoneToken: JsonElement? = null,
)

/** Linked editing range options. */
@Serializable
data class LinkedEditingRangeOptions(
    val workDoneProgress: Boolean? = null,
)

/** Linked editing range registration options. */
@Serializable
data class LinkedEditingRangeRegistrationOptions(
    val documentSelector: List<DocumentFilter>? = null,
    val workDoneProgress: Boolean? = null,
    val id: String? = null,
)

/** The response of a `textDocument/linkedEditingRange` request. */
@Serializable
data class LinkedEditingRanges(
    val ranges: List<Range>,
    val wordPattern: String? = null,
)