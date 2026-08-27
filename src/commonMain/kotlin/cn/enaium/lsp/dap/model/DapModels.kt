package cn.enaium.lsp.dap.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement


/** A client or debug-adapter initiated request. */
@Serializable
data class Request(
    val seq: Int = 0,
    val type: String = "request",
    val command: String,
    val arguments: JsonElement? = null,
)

/** A debug-adapter initiated response to a request. */
@Serializable
data class Response(
    val seq: Int = 0,
    val type: String = "response",
    val request_seq: Int = 0,
    val success: Boolean = true,
    val command: String = "",
    val message: String? = null,
    val body: JsonElement? = null,
)

/** A debug-adapter or client initiated event. */
@Serializable
data class Event(
    val seq: Int = 0,
    val type: String = "event",
    val event: String,
    val body: JsonElement? = null,
)

/** Event names defined by the DAP spec. */
object DapEvent {
    const val Initialized = "initialized"
    const val Stopped = "stopped"
    const val Continued = "continued"
    const val Exited = "exited"
    const val Terminated = "terminated"
    const val Thread = "thread"
    const val Output = "output"
    const val Breakpoint = "breakpoint"
    const val Module = "module"
    const val LoadedSource = "loadedSource"
    const val Process = "process"
    const val Capabilities = "capabilities"
}

/** Request command names defined by the DAP spec. */
object DapCommands {
    const val Initialize = "initialize"
    const val Launch = "launch"
    const val Attach = "attach"
    const val Disconnect = "disconnect"
    const val Terminate = "terminate"
    const val Continue = "continue"
    const val Next = "next"
    const val StepIn = "stepIn"
    const val StepOut = "stepOut"
    const val Pause = "pause"
    const val SetBreakpoints = "setBreakpoints"
    const val SetFunctionBreakpoints = "setFunctionBreakpoints"
    const val SetExceptionBreakpoints = "setExceptionBreakpoints"
    const val Threads = "threads"
    const val StackTrace = "stackTrace"
    const val Scopes = "scopes"
    const val Variables = "variables"
    const val Evaluate = "evaluate"
    const val ConfigurationDone = "configurationDone"
    const val Source = "source"
    const val Restart = "restart"
    const val StepBack = "stepBack"
    const val ReverseContinue = "reverseContinue"
}

/** The `initialize` request arguments. */
@Serializable
data class InitializeRequestArguments(
    val clientID: String? = null,
    val clientName: String? = null,
    val adapterID: String,
    val locale: String? = null,
    val linesStartAt1: Boolean = true,
    val columnsStartAt1: Boolean = true,
    val pathFormat: String = "path",
    val supportsVariableType: Boolean = false,
    val supportsVariablePaging: Boolean = false,
    val supportsRunInTerminalRequest: Boolean = false,
    val supportsMemoryReferences: Boolean = false,
    val supportsProgressReporting: Boolean = false,
    val supportsInvalidatedEvent: Boolean = false,
    val supportsMemoryEvent: Boolean = false,
)

/** The response to an `initialize` request. */
@Serializable
data class Capabilities(
    val supportsConfigurationDoneRequest: Boolean = false,
    val supportsFunctionBreakpoints: Boolean = false,
    val supportsConditionalBreakpoints: Boolean = false,
    val supportsHitConditionalBreakpoints: Boolean = false,
    val supportsEvaluateForHovers: Boolean = false,
    val supportsSetVariable: Boolean = false,
    val supportsRestartRequest: Boolean = false,
    val supportsTerminateRequest: Boolean = false,
    val supportsExceptionInfoRequest: Boolean = false,
    val supportsDelayedStackTraceLoading: Boolean = false,
    val supportsLoadedSourcesRequest: Boolean = false,
    val supportsLogPoints: Boolean = false,
    val supportsTerminateThreadsRequest: Boolean = false,
    val supportsSetExpression: Boolean = false,
    val supportsConditionalRequests: Boolean = false,
    val supportsCompletionsRequest: Boolean = false,
    val supportsRestartFrame: Boolean = false,
    val supportsExceptionOptions: Boolean = false,
    val supportsValueFormattingOptions: Boolean = false,
    val exceptionBreakpointFilters: List<ExceptionBreakpointsFilter>? = null,
)

@Serializable
data class ExceptionBreakpointsFilter(
    val filter: String,
    val label: String,
    val description: String? = null,
    val default: Boolean = false,
)

/** The `setBreakpoints` request arguments. */
@Serializable
data class SetBreakpointsArguments(
    val source: Source,
    val breakpoints: List<SourceBreakpoint>? = null,
    val lines: List<Int>? = null,
    val sourceModified: Boolean? = null,
)

@Serializable
data class Source(
    val name: String? = null,
    val path: String? = null,
    val sourceReference: Int? = null,
)

@Serializable
data class SourceBreakpoint(
    val line: Int,
    val column: Int? = null,
    val condition: String? = null,
    val hitCondition: String? = null,
    val logMessage: String? = null,
)

@Serializable
data class Breakpoint(
    val id: Int? = null,
    val verified: Boolean = false,
    val message: String? = null,
    val source: Source? = null,
    val line: Int? = null,
    val column: Int? = null,
    val endLine: Int? = null,
    val endColumn: Int? = null,
)

@Serializable
data class SetBreakpointsResponseBody(
    val breakpoints: List<Breakpoint>,
)

/** The `threads` request result. */
@Serializable
data class ThreadsResponseBody(val threads: List<Thread>)

@Serializable
data class Thread(val id: Int, val name: String)

/** The `stackTrace` request arguments/result. */
@Serializable
data class StackTraceArguments(
    val threadId: Int,
    val startFrame: Int? = null,
    val levels: Int? = null,
    val format: JsonElement? = null,
)

@Serializable
data class StackTraceResponseBody(
    val stackFrames: List<StackFrame>,
    val totalFrames: Int? = null,
)

@Serializable
data class StackFrame(
    val id: Int,
    val name: String,
    val source: Source? = null,
    val line: Int,
    val column: Int,
    val endLine: Int? = null,
    val endColumn: Int? = null,
    val canRestart: Boolean? = null,
)

/** The `scopes` request arguments/result. */
@Serializable
data class ScopesArguments(val frameId: Int)

@Serializable
data class ScopesResponseBody(val scopes: List<Scope>)

@Serializable
data class Scope(
    val name: String,
    val variablesReference: Int,
    val expensive: Boolean = false,
    val source: Source? = null,
    val line: Int? = null,
    val column: Int? = null,
    val endLine: Int? = null,
    val endColumn: Int? = null,
)

/** The `variables` request arguments/result. */
@Serializable
data class VariablesArguments(
    val variablesReference: Int,
    val filter: String? = null,
    val start: Int? = null,
    val count: Int? = null,
)

@Serializable
data class VariablesResponseBody(val variables: List<Variable>)

@Serializable
data class Variable(
    val name: String,
    val value: String,
    val type: String? = null,
    val variablesReference: Int = 0,
    val namedVariables: Int? = null,
    val indexedVariables: Int? = null,
    val memoryReference: String? = null,
)

/** The `continue` request arguments/result. */
@Serializable
data class ContinueArguments(val threadId: Int)

@Serializable
data class ContinueResponseBody(val allThreadsContinued: Boolean = false)

/** The `evaluate` request arguments/result. */
@Serializable
data class EvaluateArguments(
    val expression: String,
    val frameId: Int? = null,
    val context: String? = null,
    val format: JsonElement? = null,
)

@Serializable
data class EvaluateResponseBody(
    val result: String,
    val type: String? = null,
    val variablesReference: Int = 0,
    val namedVariables: Int? = null,
    val indexedVariables: Int? = null,
    val memoryReference: String? = null,
)

/** The `launch` request arguments. */
@Serializable
data class LaunchRequestArguments(
    val noDebug: Boolean? = null,
    val args: List<String>? = null,
    val cwd: String? = null,
    val env: JsonElement? = null,
    val program: String? = null,
    val stopOnEntry: Boolean? = null,
)
