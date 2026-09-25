package cn.enaium.lsp.dap

import cn.enaium.lsp.dap.model.*
import kotlinx.serialization.json.JsonElement

/**
 * A debug adapter as described by the Debug Adapter Protocol.
 *
 * Mirrors the role of the `DebugAdapter` interface in the DAP reference
 * implementation. Each method corresponds to a request command; the adapter
 * returns the response body for that command.
 *
 * Methods without a body default to `null`, which the launcher reports as a
 * body-less success — override the ones the adapter actually supports. The
 * commands whose response the spec requires (`initialize`, `setBreakpoints`,
 * `threads`, `stackTrace`, `scopes`, `variables`, `evaluate`, `continue`) are
 * abstract, because a session cannot work without them.
 */
interface DebugAdapter {
    // ==================== Lifecycle ====================

    /** The `initialize` request. */
    fun initialize(request: InitializeRequestArguments): Capabilities

    /** The `configurationDone` request. */
    fun configurationDone(): JsonElement? = null

    /** The `launch` request; the arguments are adapter-defined. */
    fun launch(args: JsonElement?): JsonElement? = null

    /** The `attach` request; the arguments are adapter-defined. */
    fun attach(args: JsonElement?): JsonElement? = null

    /** The `disconnect` request. */
    fun disconnect(args: DisconnectArguments?): JsonElement? = null

    /** The `terminate` request. */
    fun terminate(args: TerminateArguments?): JsonElement? = null

    /** The `restart` request. */
    fun restart(args: RestartArguments?): JsonElement? = null

    // ==================== Execution control ====================

    /** The `continue` request. */
    fun continue_(args: ContinueArguments): ContinueResponseBody

    /** The `next` request (step over). */
    fun next(args: NextArguments): JsonElement? = null

    /** The `stepIn` request. */
    fun stepIn(args: StepInArguments): JsonElement? = null

    /** The `stepInTargets` request: where `stepIn` can go from a stack frame. */
    fun stepInTargets(args: StepInTargetsArguments): StepInTargetsResponseBody? = null

    /** The `stepOut` request. */
    fun stepOut(args: StepOutArguments): JsonElement? = null

    /** The `stepBack` request (reverse debugging). */
    fun stepBack(args: StepBackArguments): JsonElement? = null

    /** The `reverseContinue` request (reverse debugging). */
    fun reverseContinue(args: ReverseContinueArguments): JsonElement? = null

    /** The `restartFrame` request: re-enter a stack frame. */
    fun restartFrame(args: RestartFrameArguments): JsonElement? = null

    /** The `pause` request. */
    fun pause(args: PauseArguments): JsonElement? = null

    /** The `goto` request. */
    fun goto_(args: GotoArguments): JsonElement? = null

    /** The `gotoTargets` request. */
    fun gotoTargets(args: GotoTargetsArguments): GotoTargetsResponseBody? = null

    /** The `terminateThreads` request; absent arguments mean all threads. */
    fun terminateThreads(args: TerminateThreadsArguments?): JsonElement? = null

    // ==================== Breakpoints ====================

    /** The `setBreakpoints` request. */
    fun setBreakpoints(args: SetBreakpointsArguments): SetBreakpointsResponseBody

    /** The `setFunctionBreakpoints` request. */
    fun setFunctionBreakpoints(args: SetFunctionBreakpointsArguments): SetBreakpointsResponseBody? = null

    /** The `setInstructionBreakpoints` request. */
    fun setInstructionBreakpoints(args: SetInstructionBreakpointsArguments): SetBreakpointsResponseBody? = null

    /** The `setExceptionBreakpoints` request. */
    fun setExceptionBreakpoints(args: SetExceptionBreakpointsArguments): SetBreakpointsResponseBody? = null

    /** The `dataBreakpointInfo` request. */
    fun dataBreakpointInfo(args: DataBreakpointInfoArguments): DataBreakpointInfoResponseBody? = null

    /** The `setDataBreakpoints` request. */
    fun setDataBreakpoints(args: SetDataBreakpointsArguments): SetDataBreakpointsResponseBody? = null

    /** The `breakpointLocations` request: legal breakpoint positions in a source range. */
    fun breakpointLocations(args: BreakpointLocationsArguments): BreakpointLocationsResponseBody? = null

    // ==================== Inspection ====================

    /** The `threads` request. */
    fun threads(): ThreadsResponseBody

    /** The `stackTrace` request. */
    fun stackTrace(args: StackTraceArguments): StackTraceResponseBody

    /** The `scopes` request. */
    fun scopes(args: ScopesArguments): ScopesResponseBody

    /** The `variables` request. */
    fun variables(args: VariablesArguments): VariablesResponseBody

    /** The `setVariable` request. */
    fun setVariable(args: SetVariableArguments): SetVariableResponseBody? = null

    /** The `setExpression` request. */
    fun setExpression(args: SetExpressionArguments): SetExpressionResponseBody? = null

    /** The `evaluate` request. */
    fun evaluate(args: EvaluateArguments): EvaluateResponseBody

    /** The `exceptionInfo` request. */
    fun exceptionInfo(args: ExceptionInfoArguments): ExceptionInfoResponseBody? = null

    /** The `completions` request (debug console). */
    fun completions(args: CompletionsArguments): CompletionsResponseBody? = null

    /** The `loadedSources` request. */
    fun loadedSources(): LoadedSourcesResponseBody? = null

    /** The `modules` request. */
    fun modules(args: ModulesArguments?): ModulesResponseBody? = null

    /** The `source` request (source of a `sourceReference`). */
    fun source(args: SourceArguments): SourceResponseBody? = null

    /** The `locations` request: resolve a reference from an event or a response. */
    fun locations(args: LocationsArguments): LocationsResponseBody? = null

    /** The `disassemble` request. */
    fun disassemble(args: DisassembleArguments): DisassembleResponseBody? = null

    /** The `readMemory` request; the body's `data` is base64-encoded. */
    fun readMemory(args: ReadMemoryArguments): ReadMemoryResponseBody? = null

    /** The `writeMemory` request; the arguments' `data` is base64-encoded. */
    fun writeMemory(args: WriteMemoryArguments): WriteMemoryResponseBody? = null

    /** The `cancel` request. */
    fun cancel(args: CancelArguments?): JsonElement? = null
}

/**
 * The client-facing interface the debug adapter uses to send events to the
 * debug client.
 */
interface DebugClient {
    fun sendEvent(event: String, body: JsonElement? = null)
}
