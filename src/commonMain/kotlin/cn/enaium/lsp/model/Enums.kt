package cn.enaium.lsp.model

object ApplyKind {
    const val Replace = 1
    const val Merge = 2
}

object CodeActionTag {
    const val LLMGenerated = 1
}

object CodeActionTriggerKind {
    const val Invoked = 1
    const val Automatic = 2
}

object CompletionItemKind {
    const val Text = 1
    const val Method = 2
    const val Function = 3
    const val Constructor = 4
    const val Field = 5
    const val Variable = 6
    const val Class = 7
    const val Interface = 8
    const val Module = 9
    const val Property = 10
    const val Unit = 11
    const val Value = 12
    const val Enum = 13
    const val Keyword = 14
    const val Snippet = 15
    const val Color = 16
    const val File = 17
    const val Reference = 18
    const val Folder = 19
    const val EnumMember = 20
    const val Constant = 21
    const val Struct = 22
    const val Event = 23
    const val Operator = 24
    const val TypeParameter = 25
}

object CompletionItemTag {
    const val Deprecated = 1
}

object CompletionTriggerKind {
    const val Invoked = 1
    const val TriggerCharacter = 2
    const val TriggerForIncompleteCompletions = 3
}

object DiagnosticSeverity {
    const val Error = 1
    const val Warning = 2
    const val Information = 3
    const val Hint = 4
}

object DiagnosticTag {
    const val Unnecessary = 1
    const val Deprecated = 2
}

object DocumentHighlightKind {
    const val Text = 1
    const val Read = 2
    const val Write = 3
}

object FileChangeType {
    const val Created = 1
    const val Changed = 2
    const val Deleted = 3
}

object InlayHintKind {
    const val Type = 1
    const val Parameter = 2
}

object InlineCompletionTriggerKind {
    const val Invoked = 1
    const val Automatic = 2
}

object InsertTextFormat {
    const val PlainText = 1
    const val Snippet = 2
}

object InsertTextMode {
    const val AsIs = 1
    const val AdjustIndentation = 2
}

object MessageType {
    const val Error = 1
    const val Warning = 2
    const val Info = 3
    const val Log = 4
    const val Debug = 5
}

object NotebookCellKind {
    const val Markup = 1
    const val Code = 2
}

object PrepareSupportDefaultBehavior {
    const val Identifier = 1
}

object SignatureHelpTriggerKind {
    const val Invoked = 1
    const val TriggerCharacter = 2
    const val ContentChange = 3
}

object SymbolKind {
    const val File = 1
    const val Module = 2
    const val Namespace = 3
    const val Package = 4
    const val Class = 5
    const val Method = 6
    const val Property = 7
    const val Field = 8
    const val Constructor = 9
    const val Enum = 10
    const val Interface = 11
    const val Function = 12
    const val Variable = 13
    const val Constant = 14
    const val String = 15
    const val Number = 16
    const val Boolean = 17
    const val Array = 18
    const val Object = 19
    const val Key = 20
    const val Null = 21
    const val EnumMember = 22
    const val Struct = 23
    const val Event = 24
    const val Operator = 25
    const val TypeParameter = 26
}

object SymbolTag {
    const val Deprecated = 1
    const val Private = 2
    const val Package = 3
    const val Protected = 4
    const val Public = 5
    const val Internal = 6
    const val File = 7
    const val Static = 8
    const val Abstract = 9
    const val Final = 10
    const val Sealed = 11
    const val Transient = 12
    const val Volatile = 13
    const val Synchronized = 14
    const val Virtual = 15
    const val Nullable = 16
    const val NonNull = 17
    const val Declaration = 18
    const val Definition = 19
    const val ReadOnly = 20
    const val Overrides = 21
    const val Implements = 22
}

object TextDocumentSaveReason {
    const val Manual = 1
    const val AfterDelay = 2
    const val FocusOut = 3
}

object TextDocumentSyncKind {
    const val None = 0
    const val Full = 1
    const val Incremental = 2
}

/** Work done progress kinds; values are the literal strings "begin", "report", "end". */
object WorkDoneProgressKind {
    const val Begin = "begin"
    const val Report = "report"
    const val End = "end"
}

/** Markup content kinds; values are literal strings. */
object MarkupKind {
    const val PlainText = "plaintext"
    const val Markdown = "markdown"
}
