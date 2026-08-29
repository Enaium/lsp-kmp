package cn.enaium.lsp.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/** The id used to register the request. The id can be used to deregister the request again. */
@Serializable
data class Registration(
    val id: String,
    val method: String,
    val registerOptions: JsonElement? = null,
)

/** The client/registerCapability request is sent from the server to the client to register for a new capability on the client side. */
@Serializable
data class RegistrationParams(
    val registrations: List<Registration>,
)

/** A document filter denotes a document through properties like language, schema or pattern. */
@Serializable
data class DocumentFilter(
    val language: String? = null,
    val scheme: String? = null,
    val pattern: GlobPattern? = null,
)

/** Since most of the registration options require to specify a document selector there is a base interface that can be used. */
@Serializable
data class TextDocumentRegistrationOptions(
    val documentSelector: List<DocumentFilter>? = null,
)

/** General parameters to unregister a capability. */
@Serializable
data class Unregistration(
    val id: String,
    val method: String,
)

/** The client/unregisterCapability request is sent from the server to the client to unregister a previously registered capability. */
@Serializable
data class UnregistrationParams(
    val unregisterations: List<Unregistration>,
)

/** Describe options to be used when registered for text document change events. */
@Serializable
data class TextDocumentChangeRegistrationOptions(
    val documentSelector: List<DocumentFilter>? = null,
    val syncKind: Int,
)

@Serializable
data class TextDocumentSaveRegistrationOptions(
    val documentSelector: List<DocumentFilter>? = null,
    val includeText: Boolean? = null,
)

@Serializable
data class CompletionRegistrationOptions(
    val documentSelector: List<DocumentFilter>? = null,
    val workDoneProgress: Boolean? = null,
    val triggerCharacters: List<String>? = null,
    val resolveProvider: Boolean? = null,
    val allCommitCharacters: List<String>? = null,
    val completionItem: CompletionItemOptions? = null,
)

@Serializable
data class SignatureHelpRegistrationOptions(
    val documentSelector: List<DocumentFilter>? = null,
    val workDoneProgress: Boolean? = null,
    val triggerCharacters: List<String>? = null,
    val retriggerCharacters: List<String>? = null,
)

@Serializable
data class CodeLensRegistrationOptions(
    val documentSelector: List<DocumentFilter>? = null,
    val workDoneProgress: Boolean? = null,
    val resolveProvider: Boolean? = null,
)

@Serializable
data class DocumentLinkRegistrationOptions(
    val documentSelector: List<DocumentFilter>? = null,
    val workDoneProgress: Boolean? = null,
    val resolveProvider: Boolean? = null,
)

@Serializable
data class DocumentOnTypeFormattingRegistrationOptions(
    val documentSelector: List<DocumentFilter>? = null,
    val firstTriggerCharacter: String,
    val moreTriggerCharacter: List<String>? = null,
)

/** The workspace/executeCommand request is sent from the client to the server to trigger command execution on the server. */
@Serializable
data class ExecuteCommandParams(
    val workDoneToken: Token? = null,
    val command: String,
    val arguments: List<JsonElement>? = null,
)

/** Execute command registration options. */
@Serializable
data class ExecuteCommandRegistrationOptions(
    val workDoneProgress: Boolean? = null,
    val commands: List<String>,
)

/** The workspace/applyEdit request is sent from the server to the client to modify resource on the client side. */
@Serializable
data class ApplyWorkspaceEditParams(
    val edit: WorkspaceEdit,
    val label: String? = null,
    val metadata: WorkspaceEditMetadata? = null,
)

/** The result of the `workspace/applyEdit` request. */
@Serializable
data class ApplyWorkspaceEditResponse(
    val applied: Boolean,
    val failureReason: String? = null,
    val failedChange: Int? = null,
)

/** The server supports workspace folder. */
@Serializable
data class WorkspaceFoldersOptions(
    val supported: Boolean? = null,
    val changeNotifications: StringOrBoolean? = null,
)

/** The workspace/workspaceFolders request is sent from the server to the client to fetch the current open list of workspace folders. */
@Serializable
data class WorkspaceFolder(
    val uri: String,
    val name: String,
)

/** The workspace folder change event. */
@Serializable
data class WorkspaceFoldersChangeEvent(
    val added: List<WorkspaceFolder>,
    val removed: List<WorkspaceFolder>,
)

/** The workspace/didChangeWorkspaceFolders notification is sent from the client to the server to inform the server about workspace folder configuration changes. */
@Serializable
data class DidChangeWorkspaceFoldersParams(
    val event: WorkspaceFoldersChangeEvent,
)

/** The server is interested in file notifications/requests. */
@Serializable
data class FileOperationsServerCapabilities(
    val didCreate: FileOperationOptions? = null,
    val willCreate: FileOperationOptions? = null,
    val didRename: FileOperationOptions? = null,
    val willRename: FileOperationOptions? = null,
    val didDelete: FileOperationOptions? = null,
    val willDelete: FileOperationOptions? = null,
)

/** The options for file operations. */
@Serializable
data class FileOperationOptions(
    val filters: List<FileOperationFilter>,
)

/** A filter to describe in which file operation requests or notifications the server is interested in. */
@Serializable
data class FileOperationFilter(
    val pattern: FileOperationPattern,
    val scheme: String? = null,
)

/** A pattern to describe in which file operation requests or notifications the server is interested in. */
@Serializable
data class FileOperationPattern(
    val glob: String,
    val matches: String? = null,
    val options: FileOperationPatternOptions? = null,
)

/** Matching options for the file operation pattern. */
@Serializable
data class FileOperationPatternOptions(
    val ignoreCase: Boolean? = null,
)

/** A pattern kind describing if a glob pattern matches a file a folder or both. */
object FileOperationPatternKind {
    const val File = "file"
    const val Folder = "folder"
}

/** The parameters sent in notifications/requests for user-initiated creation of files. */
@Serializable
data class CreateFilesParams(
    val files: List<FileCreate>,
)

/** Represents information on a file/folder create. */
@Serializable
data class FileCreate(
    val uri: String,
)

/** The parameters sent in notifications/requests for user-initiated renames of files. */
@Serializable
data class RenameFilesParams(
    val files: List<FileRename>,
)

/** Represents information on a file/folder rename. */
@Serializable
data class FileRename(
    val oldUri: String,
    val newUri: String,
)

/** The parameters sent in notifications/requests for user-initiated deletes of files. */
@Serializable
data class DeleteFilesParams(
    val files: List<FileDelete>,
)

/** Represents information on a file/folder delete. */
@Serializable
data class FileDelete(
    val uri: String,
)

/** The workspace/configuration request is sent from the server to the client to fetch configuration settings from the client. */
@Serializable
data class ConfigurationParams(
    val items: List<ConfigurationItem>,
)

/** A ConfigurationItem consists of the configuration section to ask for and an additional scope URI. */
@Serializable
data class ConfigurationItem(
    val scopeUri: String? = null,
    val section: String? = null,
)
/** The params of a `workspace/symbol` request. */
@Serializable
data class WorkspaceSymbolParams(
    val workDoneToken: Token? = null,
    val partialResultToken: Token? = null,
    val query: String,
)
