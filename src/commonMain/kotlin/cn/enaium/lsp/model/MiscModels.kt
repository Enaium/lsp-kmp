package cn.enaium.lsp.model

import kotlinx.serialization.Serializable

@Serializable
data class InlayHintCapabilities(
    val dynamicRegistration: Boolean? = null,
    val resolveSupport: InlayHintResolveSupportCapabilities? = null,
)

@Serializable
data class InlayHintResolveSupportCapabilities(
    val properties: List<String>,
)

@Serializable
data class InlayHintRegistrationOptions(
    val documentSelector: List<DocumentFilter>? = null,
    val workDoneProgress: Boolean? = null,
    val id: String? = null,
    val resolveProvider: Boolean? = null,
)

@Serializable
data class InlayHintParams(
    val workDoneToken: Token? = null,
    val textDocument: TextDocumentIdentifier,
    val range: Range,
)

@Serializable
data class InlayHint(
    val position: Position,
    val label: InlayHintLabel,
    val kind: Int? = null,
    val textEdits: List<TextEdit>? = null,
    val tooltip: Documentation? = null,
    val paddingLeft: Boolean? = null,
    val paddingRight: Boolean? = null,
    val data: LSPAny? = null,
)

@Serializable
data class InlayHintLabelPart(
    val value: String,
    val tooltip: Documentation? = null,
    val location: Location? = null,
    val command: Command? = null,
)

@Serializable
data class InlayHintWorkspaceCapabilities(
    val refreshSupport: Boolean? = null,
)

@Serializable
data class InlineValueCapabilities(
    val dynamicRegistration: Boolean? = null,
)

@Serializable
data class InlineValueRegistrationOptions(
    val documentSelector: List<DocumentFilter>? = null,
    val workDoneProgress: Boolean? = null,
    val id: String? = null,
)

@Serializable
data class InlineValueParams(
    val workDoneToken: Token? = null,
    val textDocument: TextDocumentIdentifier,
    val range: Range,
    val context: InlineValueContext,
)

@Serializable
data class InlineValueContext(
    val frameId: Int = 0,
    val stoppedLocation: Range,
)

@Serializable
data class InlineValueText(
    val range: Range,
    val text: String,
)

@Serializable
data class InlineValueVariableLookup(
    val range: Range,
    val variableName: String? = null,
    val caseSensitiveLookup: Boolean = false,
)

@Serializable
data class InlineValueEvaluatableExpression(
    val range: Range,
    val expression: String? = null,
)

@Serializable
data class InlineValueWorkspaceCapabilities(
    val refreshSupport: Boolean? = null,
)

@Serializable
data class DiagnosticCapabilities(
    val dynamicRegistration: Boolean? = null,
    val relatedDocumentSupport: Boolean? = null,
    val relatedInformation: Boolean? = null,
    val tagSupport: DiagnosticsTagSupport? = null,
    val codeDescriptionSupport: Boolean? = null,
    val dataSupport: Boolean? = null,
    val markupMessageSupport: Boolean? = null,
)

@Serializable
data class DiagnosticServerCapabilities(
    val markupMessageSupport: Boolean? = null,
)

@Serializable
data class DiagnosticRegistrationOptions(
    val documentSelector: List<DocumentFilter>? = null,
    val workDoneProgress: Boolean? = null,
    val id: String? = null,
    val identifier: String? = null,
    val interFileDependencies: Boolean = false,
    val workspaceDiagnostics: Boolean = false,
)

@Serializable
data class DocumentDiagnosticParams(
    val workDoneToken: Token? = null,
    val partialResultToken: Token? = null,
    val textDocument: TextDocumentIdentifier,
    val identifier: String? = null,
    val previousResultId: String? = null,
)

@Serializable
data class FullDocumentDiagnosticReport(
    val kind: String = "full",
    val resultId: String? = null,
    val items: List<Diagnostic>,
)

@Serializable
data class UnchangedDocumentDiagnosticReport(
    val kind: String = "unchanged",
    val resultId: String,
)

@Serializable
data class RelatedFullDocumentDiagnosticReport(
    val kind: String = "full",
    val resultId: String? = null,
    val items: List<Diagnostic>,
    val relatedDocuments: Map<String, DocumentDiagnosticEither>? = null,
)

@Serializable
data class RelatedUnchangedDocumentDiagnosticReport(
    val kind: String = "unchanged",
    val resultId: String,
    val relatedDocuments: Map<String, DocumentDiagnosticEither>? = null,
)

@Serializable
data class DocumentDiagnosticReportPartialResult(
    val relatedDocuments: Map<String, DocumentDiagnosticEither>,
)

@Serializable
data class DiagnosticServerCancellationData(
    val retriggerRequest: Boolean = false,
)

@Serializable
data class WorkspaceDiagnosticParams(
    val workDoneToken: Token? = null,
    val partialResultToken: Token? = null,
    val identifier: String? = null,
    val previousResultIds: List<PreviousResultId>,
)

@Serializable
data class PreviousResultId(
    val uri: String,
    val value: String,
)

@Serializable
data class WorkspaceDiagnosticReport(
    val items: List<WorkspaceDocumentDiagnosticEither>,
)

@Serializable
data class WorkspaceFullDocumentDiagnosticReport(
    val kind: String = "full",
    val resultId: String? = null,
    val items: List<Diagnostic>,
    val uri: String,
    val version: Int? = null,
)

@Serializable
data class WorkspaceUnchangedDocumentDiagnosticReport(
    val kind: String = "unchanged",
    val resultId: String,
    val uri: String,
    val version: Int? = null,
)

@Serializable
data class WorkspaceDiagnosticReportPartialResult(
    val items: List<WorkspaceDocumentDiagnosticEither>,
)

@Serializable
data class DiagnosticWorkspaceCapabilities(
    val refreshSupport: Boolean? = null,
)

@Serializable
data class NotebookDocument(
    val uri: String,
    val notebookType: String,
    val version: Int = 0,
    val metadata: LSPObject? = null,
    val cells: List<NotebookCell>,
)

@Serializable
data class NotebookCell(
    val kind: Int,
    val document: String,
    val metadata: LSPObject? = null,
    val executionSummary: ExecutionSummary? = null,
)

@Serializable
data class ExecutionSummary(
    val executionOrder: Int = 0,
    val success: Boolean? = null,
)

@Serializable
data class NotebookCellTextDocumentFilter(
    val notebook: NotebookFilterEither,
    val language: String? = null,
)

@Serializable
data class NotebookDocumentFilter(
    val notebookType: String? = null,
    val scheme: String? = null,
    val pattern: GlobPattern? = null,
)

@Serializable
data class NotebookDocumentSyncClientCapabilities(
    val dynamicRegistration: Boolean? = null,
    val executionSummarySupport: Boolean? = null,
)

@Serializable
data class NotebookSelector(
    val notebook: NotebookFilterEither? = null,
    val cells: List<NotebookSelectorCell>? = null,
)

@Serializable
data class NotebookSelectorCell(
    val language: String,
)

@Serializable
data class NotebookDocumentSyncOptions(
    val notebookSelector: List<NotebookSelector>,
    val save: Boolean? = null,
)

@Serializable
data class NotebookDocumentSyncRegistrationOptions(
    val id: String? = null,
    val notebookSelector: List<NotebookSelector>,
    val save: Boolean? = null,
)

@Serializable
data class DidOpenNotebookDocumentParams(
    val notebookDocument: NotebookDocument,
    val cellTextDocuments: List<TextDocumentItem>,
)

@Serializable
data class DidChangeNotebookDocumentParams(
    val notebookDocument: VersionedNotebookDocumentIdentifier,
    val change: NotebookDocumentChangeEvent,
)

@Serializable
data class VersionedNotebookDocumentIdentifier(
    val version: Int = 0,
    val uri: String,
)

@Serializable
data class NotebookDocumentChangeEvent(
    val metadata: LSPObject? = null,
    val cells: NotebookDocumentChangeEventCells? = null,
)

@Serializable
data class NotebookDocumentChangeEventCells(
    val structure: NotebookDocumentChangeEventCellStructure? = null,
    val data: List<NotebookCell>? = null,
    val textContent: List<NotebookDocumentChangeEventCellTextContent>? = null,
)

@Serializable
data class NotebookDocumentChangeEventCellStructure(
    val array: NotebookCellArrayChange,
    val didOpen: List<TextDocumentItem>? = null,
    val didClose: List<TextDocumentIdentifier>? = null,
)

@Serializable
data class NotebookDocumentChangeEventCellTextContent(
    val document: VersionedTextDocumentIdentifier,
    val changes: List<TextDocumentContentChangeEvent>,
)

@Serializable
data class NotebookCellArrayChange(
    val start: Int = 0,
    val deleteCount: Int = 0,
    val cells: List<NotebookCell>? = null,
)

@Serializable
data class DidSaveNotebookDocumentParams(
    val notebookDocument: NotebookDocumentIdentifier,
)

@Serializable
data class DidCloseNotebookDocumentParams(
    val notebookDocument: NotebookDocumentIdentifier,
    val cellTextDocuments: List<TextDocumentIdentifier>,
)

@Serializable
data class NotebookDocumentIdentifier(
    val uri: String,
)

@Serializable
data class StringValue(
    val kind: String,
    val value: String,
)

@Serializable
data class InlineCompletionCapabilities(
    val dynamicRegistration: Boolean? = null,
)

@Serializable
data class InlineCompletionRegistrationOptions(
    val documentSelector: List<DocumentFilter>? = null,
    val workDoneProgress: Boolean? = null,
    val id: String? = null,
)

@Serializable
data class InlineCompletionParams(
    val textDocument: TextDocumentIdentifier,
    val position: Position,
    val workDoneToken: Token? = null,
    val context: InlineCompletionContext,
)

@Serializable
data class InlineCompletionContext(
    val triggerKind: Int,
    val selectedCompletionInfo: SelectedCompletionInfo? = null,
)

@Serializable
data class SelectedCompletionInfo(
    val range: Range,
    val text: String,
)

@Serializable
data class InlineCompletionList(
    val items: List<InlineCompletionItem>,
)

@Serializable
data class InlineCompletionItem(
    val insertText: InlineCompletionInsertText,
    val filterText: String? = null,
    val range: Range? = null,
    val command: Command? = null,
)

@Serializable
data class SnippetTextEdit(
    val range: Range,
    val snippet: StringValue,
    val annotationId: String? = null,
)

@Serializable
data class TextDocumentContentCapabilities(
    val dynamicRegistration: Boolean? = null,
)

@Serializable
data class TextDocumentContentRegistrationOptions(
    val id: String? = null,
    val schemes: List<String>,
)

@Serializable
data class TextDocumentContentParams(
    val uri: String,
)

@Serializable
data class TextDocumentContentResult(
    val text: String,
)

@Serializable
data class TextDocumentContentRefreshParams(
    val uri: String,
)