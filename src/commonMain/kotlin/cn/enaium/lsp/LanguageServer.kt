package cn.enaium.lsp

import cn.enaium.lsp.model.*

/**
 * A language server as described by the Language Server Protocol.
 *
 * Mirrors the role of lsp4j's `LanguageServer` interface. Implementations
 * provide the core lifecycle methods and expose per-area services.
 */
interface LanguageServer {
    /** The `initialize` request, called once before any other request. */
    fun initialize(params: InitializeParams): InitializeResult

    /** The `initialized` notification, sent after the client received the initialize result. */
    fun initialized(params: InitializedParams) {}

    /** The `shutdown` request. Returns `null` on successful shutdown. */
    fun shutdown(): Any?

    /** The `exit` notification. */
    fun exit()

    /** The `window/workDoneProgress/cancel` notification. */
    fun cancelProgress(params: WorkDoneProgressCancelParams) {}

    /** The `$/setTrace` notification. */
    fun setTrace(params: SetTraceParams) {}

    /** Provides access to the text document service, or null if not supported. */
    fun textDocumentService(): TextDocumentService?

    /** Provides access to the workspace service, or null if not supported. */
    fun workspaceService(): WorkspaceService?

    /** Provides access to the window service, or null if not supported. */
    fun windowService(): WindowService?
}