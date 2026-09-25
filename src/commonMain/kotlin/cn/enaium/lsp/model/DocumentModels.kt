package cn.enaium.lsp.model

import kotlinx.serialization.Serializable

/** The diagnostic's severity and related metadata. */
@Serializable
data class Diagnostic(
    val range: Range,
    val severity: Int? = null,
    val code: DiagnosticCode? = null,
    val codeDescription: DiagnosticCodeDescription? = null,
    val source: String? = null,
    val message: Documentation,
    val tags: List<Int>? = null,
    val relatedInformation: List<DiagnosticRelatedInformation>? = null,
    val data: LSPAny? = null,
)

/** Represents a related message and source code location for a diagnostic. */
@Serializable
data class DiagnosticRelatedInformation(
    val location: Location,
    val message: String,
)

/** Structure to capture a description for an error code. */
@Serializable
data class DiagnosticCodeDescription(
    val href: String,
)

/** A notification sent from the client to the server to signal the change of configuration settings. */
@Serializable
data class DidChangeConfigurationParams(
    val settings: LSPAny,
)

/** The document change notification is sent from the client to the server to signal changes to a text document. */
@Serializable
data class DidChangeTextDocumentParams(
    val textDocument: VersionedTextDocumentIdentifier,
    val contentChanges: List<TextDocumentContentChangeEvent>,
)

/** The watched files notification is sent from the client to the server when the client detects changes to file watched by the language client. */
@Serializable
data class DidChangeWatchedFilesParams(
    val changes: List<FileEvent>,
)

@Serializable
data class DidChangeWatchedFilesRegistrationOptions(
    val watchers: List<FileSystemWatcher>,
)

@Serializable
data class FileSystemWatcher(
    val globPattern: GlobPattern,
    val kind: Int? = null,
)

/** The kind of file system events to watch. */
object WatchKind {
    const val Create = 1
    const val Change = 2
    const val Delete = 4
}

/** A relative pattern is a helper to construct glob patterns that are matched relatively to a base URI. */
@Serializable
data class RelativePattern(
    val baseUri: BaseUri,
    val pattern: String,
)

/** The document close notification is sent from the client to the server when the document got closed in the client. */
@Serializable
data class DidCloseTextDocumentParams(
    val textDocument: TextDocumentIdentifier,
)

/** The document open notification is sent from the client to the server to signal newly opened text documents. */
@Serializable
data class DidOpenTextDocumentParams(
    val textDocument: TextDocumentItem,
)

/** The document save notification is sent from the client to the server when the document was saved in the client. */
@Serializable
data class DidSaveTextDocumentParams(
    val textDocument: TextDocumentIdentifier,
    val text: String? = null,
)

@Serializable
data class WillSaveTextDocumentParams(
    val textDocument: TextDocumentIdentifier,
    val reason: Int,
)

/** Value-object describing what options formatting should use. */
@Serializable
data class FormattingOptions(
    val tabSize: Int = 0,
    val insertSpaces: Boolean = false,
    val trimTrailingWhitespace: Boolean? = null,
    val insertFinalNewline: Boolean? = null,
    val trimFinalNewlines: Boolean? = null,
)

/** The document formatting request is sent from the server to the client to format a whole document. */
@Serializable
data class DocumentFormattingParams(
    val workDoneToken: Token? = null,
    val textDocument: TextDocumentIdentifier,
    val options: FormattingOptions,
)

/** Document formatting options. */
@Serializable
data class DocumentFormattingOptions(
    val workDoneProgress: Boolean? = null,
)

/** Document formatting registration options. */
@Serializable
data class DocumentFormattingRegistrationOptions(
    val documentSelector: List<DocumentFilter>? = null,
    val workDoneProgress: Boolean? = null,
)

/** A document highlight is a range inside a text document which deserves special attention. */
@Serializable
data class DocumentHighlight(
    val range: Range,
    val kind: Int? = null,
)

/** A document link is a range in a text document that links to an internal or external resource, like another text document or a web site. */
@Serializable
data class DocumentLink(
    val range: Range,
    val target: String? = null,
    val tooltip: String? = null,
    val data: LSPAny? = null,
)

/** The document links request is sent from the client to the server to request the location of links in a document. */
@Serializable
data class DocumentLinkParams(
    val workDoneToken: Token? = null,
    val partialResultToken: Token? = null,
    val textDocument: TextDocumentIdentifier,
)

/** Document link options. */
@Serializable
data class DocumentLinkOptions(
    val workDoneProgress: Boolean? = null,
    val resolveProvider: Boolean? = null,
)

/** Execute command options. */
@Serializable
data class ExecuteCommandOptions(
    val workDoneProgress: Boolean? = null,
    val commands: List<String>,
)

/** Save options. */
@Serializable
data class SaveOptions(
    val includeText: Boolean? = null,
)

/** Rename options. */
@Serializable
data class RenameOptions(
    val documentSelector: List<DocumentFilter>? = null,
    val workDoneProgress: Boolean? = null,
    val prepareProvider: Boolean? = null,
)

/** Document color options. */
@Serializable
data class ColorProviderOptions(
    val documentSelector: List<DocumentFilter>? = null,
    val workDoneProgress: Boolean? = null,
    val id: String? = null,
)

/** Folding range options. */
@Serializable
data class FoldingRangeProviderOptions(
    val documentSelector: List<DocumentFilter>? = null,
    val workDoneProgress: Boolean? = null,
    val id: String? = null,
)

@Serializable
data class TextDocumentSyncOptions(
    val openClose: Boolean? = null,
    val change: Int? = null,
    val willSave: Boolean? = null,
    val willSaveWaitUntil: Boolean? = null,
    val save: Save? = null,
)

/** Static registration options to be returned in the initialize request. */
@Serializable
data class StaticRegistrationOptions(
    val documentSelector: List<DocumentFilter>? = null,
    val id: String? = null,
)

/** Format document on type options. */
@Serializable
data class DocumentOnTypeFormattingOptions(
    val firstTriggerCharacter: String,
    val moreTriggerCharacter: List<String>? = null,
)

/** The document on type formatting request is sent from the client to the server to format parts of the document during typing. */
@Serializable
data class DocumentOnTypeFormattingParams(
    val textDocument: TextDocumentIdentifier,
    val position: Position,
    val options: FormattingOptions,
    val ch: String,
)

/** The document range formatting request is sent from the client to the server to format a given range in a document. */
@Serializable
data class DocumentRangeFormattingParams(
    val workDoneToken: Token? = null,
    val textDocument: TextDocumentIdentifier,
    val options: FormattingOptions,
    val range: Range,
)

/** The document ranges formatting request is sent from the client to the server to format multiple ranges at once in a document. */
@Serializable
data class DocumentRangesFormattingParams(
    val workDoneToken: Token? = null,
    val textDocument: TextDocumentIdentifier,
    val options: FormattingOptions,
    val ranges: List<Range>,
)

/** Document range formatting options. */
@Serializable
data class DocumentRangeFormattingOptions(
    val workDoneProgress: Boolean? = null,
    val rangesSupport: Boolean? = null,
)

/** Document range formatting registration options. */
@Serializable
data class DocumentRangeFormattingRegistrationOptions(
    val documentSelector: List<DocumentFilter>? = null,
    val workDoneProgress: Boolean? = null,
    val rangesSupport: Boolean? = null,
)

@Serializable
data class TypeHierarchyPrepareParams(
    val textDocument: TextDocumentIdentifier,
    val position: Position,
    val workDoneToken: Token? = null,
)

@Serializable
data class TypeHierarchySupertypesParams(
    val workDoneToken: Token? = null,
    val partialResultToken: Token? = null,
    val item: TypeHierarchyItem,
)

@Serializable
data class TypeHierarchySubtypesParams(
    val workDoneToken: Token? = null,
    val partialResultToken: Token? = null,
    val item: TypeHierarchyItem,
)

@Serializable
data class DocumentSymbolOptions(
    val workDoneProgress: Boolean? = null,
    val label: String? = null,
)

@Serializable
data class DocumentSymbolRegistrationOptions(
    val documentSelector: List<DocumentFilter>? = null,
    val workDoneProgress: Boolean? = null,
    val label: String? = null,
)

/** The document symbol request is sent from the client to the server to list all symbols found in a given text document. */
@Serializable
data class DocumentSymbolParams(
    val workDoneToken: Token? = null,
    val partialResultToken: Token? = null,
    val textDocument: TextDocumentIdentifier,
)

/** An event describing a file change. */
@Serializable
data class FileEvent(
    val uri: String,
    val type: Int,
)

/** A MarkupContent literal represents a string value which content is interpreted based on its kind flag. */
@Serializable
data class MarkupContent(
    val kind: String,
    val value: String,
)

/** The result of a `textDocument/hover` request. */
@Serializable
data class Hover(
    val contents: HoverContents,
    val range: Range? = null,
)

/** MarkedString can be used to render human readable text. */
@Serializable
data class MarkedString(
    val language: String,
    val value: String,
)