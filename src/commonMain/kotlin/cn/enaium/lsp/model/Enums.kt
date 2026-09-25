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

/** JSON-RPC and LSP error codes (`ErrorCodes` in the specification). */
object ErrorCodes {
    const val ParseError = -32700
    const val InvalidRequest = -32600
    const val MethodNotFound = -32601
    const val InvalidParams = -32602
    const val InternalError = -32603
    const val ServerNotInitialized = -32002
    const val UnknownErrorCode = -32001
}

/** LSP-specific error codes. */
object LSPErrorCodes {
    const val RequestFailed = -32803
    const val ServerCancelled = -32802
    const val ContentModified = -32801
    const val RequestCancelled = -32800
}

/** The values of the `trace` setting (`initialize`, `$/setTrace`). */
object TraceValue {
    const val Off = "off"
    const val Messages = "messages"
    const val Verbose = "verbose"
}

/** Position encoding kinds a client and server negotiate in `initialize`. */
object PositionEncodingKind {
    const val Utf8 = "utf-8"
    const val Utf16 = "utf-16"
    const val Utf32 = "utf-32"
}

/** The kind of a `textDocument/diagnostic` report. */
object DocumentDiagnosticReportKind {
    const val Full = "full"
    const val Unchanged = "unchanged"
}

/** The format of tokens a client asks for in `textDocument/inlineCompletion`. */
object TokenFormat {
    const val Relative = "relative"
}

/** The semantic token types every client must understand. */
object SemanticTokenTypes {
    const val Namespace = "namespace"
    const val Type = "type"
    const val Class = "class"
    const val Enum = "enum"
    const val Interface = "interface"
    const val Struct = "struct"
    const val TypeParameter = "typeParameter"
    const val Parameter = "parameter"
    const val Variable = "variable"
    const val Property = "property"
    const val EnumMember = "enumMember"
    const val Event = "event"
    const val Function = "function"
    const val Method = "method"
    const val Macro = "macro"
    const val Keyword = "keyword"
    const val Modifier = "modifier"
    const val Comment = "comment"
    const val String = "string"
    const val Number = "number"
    const val Regexp = "regexp"
    const val Operator = "operator"
    const val Decorator = "decorator"
    const val Label = "label"
}

/** The semantic token modifiers every client must understand. */
object SemanticTokenModifiers {
    const val Declaration = "declaration"
    const val Definition = "definition"
    const val Readonly = "readonly"
    const val Static = "static"
    const val Deprecated = "deprecated"
    const val Abstract = "abstract"
    const val Async = "async"
    const val Modification = "modification"
    const val Documentation = "documentation"
    const val DefaultLibrary = "defaultLibrary"
}

/** The language ids of the `LanguageKind` enumeration. */
object LanguageKind {
    const val Abap = "abap"
    const val Bat = "bat"
    const val Bibtex = "bibtex"
    const val Clojure = "clojure"
    const val Coffeescript = "coffeescript"
    const val C = "c"
    const val Cpp = "cpp"
    const val Csharp = "csharp"
    const val Css = "css"
    const val D = "d"
    const val Pascal = "pascal"
    const val Diff = "diff"
    const val Dart = "dart"
    const val Dockerfile = "dockerfile"
    const val Elixir = "elixir"
    const val Erlang = "erlang"
    const val Fsharp = "fsharp"
    const val GitCommit = "git-commit"
    const val GitRebase = "git-rebase"
    const val Go = "go"
    const val Groovy = "groovy"
    const val Handlebars = "handlebars"
    const val Haskell = "haskell"
    const val Html = "html"
    const val Ini = "ini"
    const val Java = "java"
    const val Javascript = "javascript"
    const val JavascriptReact = "javascriptreact"
    const val Json = "json"
    const val Latex = "latex"
    const val Less = "less"
    const val Lua = "lua"
    const val Makefile = "makefile"
    const val Markdown = "markdown"
    const val ObjectiveC = "objective-c"
    const val ObjectiveCpp = "objective-cpp"
    const val Perl = "perl"
    const val Perl6 = "perl6"
    const val Php = "php"
    const val Plaintext = "plaintext"
    const val Powershell = "powershell"
    const val Jade = "jade"
    const val Python = "python"
    const val R = "r"
    const val Razor = "razor"
    const val Ruby = "ruby"
    const val Rust = "rust"
    const val Scss = "scss"
    const val Sass = "sass"
    const val Scala = "scala"
    const val Shaderlab = "shaderlab"
    const val Shellscript = "shellscript"
    const val Sql = "sql"
    const val Swift = "swift"
    const val Typescript = "typescript"
    const val TypescriptReact = "typescriptreact"
    const val Tex = "tex"
    const val Vb = "vb"
    const val Xml = "xml"
    const val Xsl = "xsl"
    const val Yaml = "yaml"
}
