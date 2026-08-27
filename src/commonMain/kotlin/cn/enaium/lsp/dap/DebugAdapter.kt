package cn.enaium.lsp.dap

import cn.enaium.lsp.dap.model.*
import kotlinx.serialization.json.JsonElement

/**
 * A debug adapter as described by the Debug Adapter Protocol.
 *
 * Mirrors the role of the `DebugAdapter` interface in the DAP reference
 * implementation. Each method corresponds to a request command; the adapter
 * returns the response body for that command.
 */
interface DebugAdapter {
    /** The `initialize` request. */
    fun initialize(request: InitializeRequestArguments): Capabilities

    /** The `configurationDone` request. */
    fun configurationDone(): JsonElement? = null

    /** The `launch` request. */
    fun launch(args: JsonElement?): JsonElement? = null

    /** The `attach` request. */
    fun attach(args: JsonElement?): JsonElement? = null

    /** The `disconnect` request. */
    fun disconnect(args: JsonElement?): JsonElement? = null

    /** The `continue` request. */
    fun continue_(args: ContinueArguments): ContinueResponseBody

    /** The `next` request. */
    fun next(args: JsonElement?): JsonElement? = null

    /** The `stepIn` request. */
    fun stepIn(args: JsonElement?): JsonElement? = null

    /** The `stepOut` request. */
    fun stepOut(args: JsonElement?): JsonElement? = null

    /** The `pause` request. */
    fun pause(args: JsonElement?): JsonElement? = null

    /** The `setBreakpoints` request. */
    fun setBreakpoints(args: SetBreakpointsArguments): SetBreakpointsResponseBody

    /** The `setFunctionBreakpoints` request. */
    fun setFunctionBreakpoints(args: JsonElement?): JsonElement? = null

    /** The `setExceptionBreakpoints` request. */
    fun setExceptionBreakpoints(args: JsonElement?): JsonElement? = null

    /** The `threads` request. */
    fun threads(): ThreadsResponseBody

    /** The `stackTrace` request. */
    fun stackTrace(args: StackTraceArguments): StackTraceResponseBody

    /** The `scopes` request. */
    fun scopes(args: ScopesArguments): ScopesResponseBody

    /** The `variables` request. */
    fun variables(args: VariablesArguments): VariablesResponseBody

    /** The `evaluate` request. */
    fun evaluate(args: EvaluateArguments): EvaluateResponseBody

    /** The `terminate` request. */
    fun terminate(args: JsonElement?): JsonElement? = null
}

/**
 * The client-facing interface the debug adapter uses to send events to the
 * debug client.
 */
interface DebugClient {
    fun sendEvent(event: String, body: JsonElement? = null)
}