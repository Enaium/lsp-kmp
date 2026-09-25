package cn.enaium.lsp.model

import kotlinx.serialization.Serializable

/** A code action represents a change that can be performed in code, e.g. to fix a problem or to refactor code. */
@Serializable
data class CodeAction(
    val title: String,
    val kind: String? = null,
    val diagnostics: List<Diagnostic>? = null,
    val isPreferred: Boolean? = null,
    val disabled: CodeActionDisabled? = null,
    val edit: WorkspaceEdit? = null,
    val command: Command? = null,
    val data: LSPAny? = null,
    val tags: List<Int>? = null,
)

/** Marks that the code action cannot currently be applied. */
@Serializable
data class CodeActionDisabled(
    val reason: String,
)

/** Contains additional diagnostic information about the context in which a code action is run. */
@Serializable
data class CodeActionContext(
    val diagnostics: List<Diagnostic>,
    val only: List<String>? = null,
    val triggerKind: Int? = null,
)

/** The code action request is sent from the client to the server to compute commands for a given text document and range. */
@Serializable
data class CodeActionParams(
    val textDocument: TextDocumentIdentifier,
    val range: Range,
    val context: CodeActionContext,
    val workDoneToken: Token? = null,
    val partialResultToken: Token? = null,
)

/** Documentation for a class of code actions. */
@Serializable
data class CodeActionKindDocumentation(
    val kind: String,
    val command: Command,
)

/** Code Action options. */
@Serializable
data class CodeActionOptions(
    val codeActionKinds: List<String>? = null,
    val documentation: List<CodeActionKindDocumentation>? = null,
    val resolveProvider: Boolean? = null,
    val workDoneProgress: Boolean? = null,
)

/** Code Action registration options. */
@Serializable
data class CodeActionRegistrationOptions(
    val codeActionKinds: List<String>? = null,
    val documentation: List<CodeActionKindDocumentation>? = null,
    val resolveProvider: Boolean? = null,
    val documentSelector: List<DocumentFilter>? = null,
    val workDoneProgress: Boolean? = null,
)

/** A code lens represents a command that should be shown along with source text, like the number of references, a way to run tests, etc. */
@Serializable
data class CodeLens(
    val range: Range,
    val command: Command? = null,
    val data: LSPAny? = null,
)

/** Code Lens options. */
@Serializable
data class CodeLensOptions(
    val resolveProvider: Boolean? = null,
    val workDoneProgress: Boolean? = null,
)

/** The code lens request is sent from the client to the server to compute code lenses for a given text document. */
@Serializable
data class CodeLensParams(
    val textDocument: TextDocumentIdentifier,
    val workDoneToken: Token? = null,
    val partialResultToken: Token? = null,
)

/** Represents a reference to a command. */
@Serializable
data class Command(
    val title: String,
    val tooltip: String? = null,
    val command: String,
    val arguments: List<LSPAny>? = null,
)

/** Additional details for a completion item label. */
@Serializable
data class CompletionItemLabelDetails(
    val detail: String? = null,
    val description: String? = null,
)

/** A completion item represents a text snippet in a completion list. */
@Serializable
data class CompletionItem(
    val label: String,
    val labelDetails: CompletionItemLabelDetails? = null,
    val kind: Int? = null,
    val tags: List<Int>? = null,
    val detail: String? = null,
    val documentation: Documentation? = null,
    val deprecated: Boolean? = null,
    val preselect: Boolean? = null,
    val sortText: String? = null,
    val filterText: String? = null,
    val insertText: String? = null,
    val insertTextFormat: Int? = null,
    val insertTextMode: Int? = null,
    val textEdit: TextEditOrInsert? = null,
    val textEditText: String? = null,
    val additionalTextEdits: List<TextEdit>? = null,
    val commitCharacters: List<String>? = null,
    val command: Command? = null,
    val data: LSPAny? = null,
)

/** The range if the insert is requested. */
@Serializable
data class InsertReplaceRange(
    val insert: Range,
    val replace: Range,
)

/** In many cases, the items of an actual completion result share the same value for properties. */
@Serializable
data class CompletionItemDefaults(
    val commitCharacters: List<String>? = null,
    val editRange: EditRange? = null,
    val insertTextFormat: Int? = null,
    val insertTextMode: Int? = null,
    val data: LSPAny? = null,
)

/** Specifies how fields from a completion item should be combined with those from CompletionList.itemDefaults. */
@Serializable
data class CompletionApplyKind(
    val commitCharacters: Int? = null,
    val data: Int? = null,
)

/** Represents a collection of completion items to be presented in the editor. */
@Serializable
data class CompletionList(
    val isIncomplete: Boolean = false,
    val items: List<CompletionItem>,
    val itemDefaults: CompletionItemDefaults? = null,
    val applyKind: CompletionApplyKind? = null,
)

/** Completion options. */
@Serializable
data class CompletionOptions(
    val resolveProvider: Boolean? = null,
    val triggerCharacters: List<String>? = null,
    val allCommitCharacters: List<String>? = null,
    val completionItem: CompletionItemOptions? = null,
    val workDoneProgress: Boolean? = null,
)

/** The server supports the following CompletionItem specific capabilities. */
@Serializable
data class CompletionItemOptions(
    val labelDetailsSupport: Boolean? = null,
)