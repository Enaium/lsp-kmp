package cn.enaium.lsp.dap.model

import kotlin.jvm.JvmInline
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive


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
    const val Invalidated = "invalidated"
    const val ProgressStart = "progressStart"
    const val ProgressUpdate = "progressUpdate"
    const val ProgressEnd = "progressEnd"
    const val Memory = "memory"
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
    const val StepBack = "stepBack"
    const val ReverseContinue = "reverseContinue"
    const val RestartFrame = "restartFrame"
    const val StepInTargets = "stepInTargets"
    const val Pause = "pause"
    const val SetBreakpoints = "setBreakpoints"
    const val SetFunctionBreakpoints = "setFunctionBreakpoints"
    const val SetInstructionBreakpoints = "setInstructionBreakpoints"
    const val SetExceptionBreakpoints = "setExceptionBreakpoints"
    const val BreakpointLocations = "breakpointLocations"
    const val Threads = "threads"
    const val StackTrace = "stackTrace"
    const val Scopes = "scopes"
    const val Variables = "variables"
    const val Evaluate = "evaluate"
    const val ConfigurationDone = "configurationDone"
    const val Source = "source"
    const val Locations = "locations"
    const val Disassemble = "disassemble"
    const val ReadMemory = "readMemory"
    const val WriteMemory = "writeMemory"
    const val Restart = "restart"
    const val SetVariable = "setVariable"
    const val SetExpression = "setExpression"
    const val ExceptionInfo = "exceptionInfo"
    const val LoadedSources = "loadedSources"
    const val Modules = "modules"
    const val Goto = "goto"
    const val GotoTargets = "gotoTargets"
    const val DataBreakpointInfo = "dataBreakpointInfo"
    const val SetDataBreakpoints = "setDataBreakpoints"
    const val Completions = "completions"
    const val TerminateThreads = "terminateThreads"
    const val Cancel = "cancel"

    /** Adapter -> client requests (the adapter asks the client to do something). */
    const val RunInTerminal = "runInTerminal"
    const val StartDebugging = "startDebugging"
}

/** Step granularity values for the stepping requests. */
object SteppingGranularity {
    const val Statement = "statement"
    const val Line = "line"
    const val Instruction = "instruction"
}

/** How a client asks an adapter to format a value (e.g. `variables`). */
@Serializable
data class ValueFormat(
    /** Display the value in hexadecimal. */
    val hex: Boolean? = null,
)

/** How a client asks an adapter to format a stack frame (`stackTrace`). */
@Serializable
data class StackFrameFormat(
    val hex: Boolean? = null,
    val parameters: Boolean? = null,
    val parameterTypes: Boolean? = null,
    val parameterNames: Boolean? = null,
    val parameterValues: Boolean? = null,
    val line: Boolean? = null,
    val module: Boolean? = null,
    val includeAll: Boolean? = null,
)

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
    val supportsArgsCanBeInterpretedByShell: Boolean = false,
    val supportsStartDebuggingRequest: Boolean = false,
    val supportsANSIStyling: Boolean = false,
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
    val supportsCompletionsRequest: Boolean = false,
    val supportsRestartFrame: Boolean = false,
    val supportsExceptionOptions: Boolean = false,
    val supportsValueFormattingOptions: Boolean = false,
    val supportsDataBreakpoints: Boolean = false,
    val supportsGotoTargetsRequest: Boolean = false,
    val supportsStepBack: Boolean = false,
    val supportsSteppingGranularity: Boolean = false,
    val supportsCancelRequest: Boolean = false,
    val supportsClipboardContext: Boolean = false,
    val supportsExceptionFilterOptions: Boolean = false,
    val supportsSingleThreadExecutionRequests: Boolean = false,
    val supportsInstructionBreakpoints: Boolean = false,
    val supportsReadMemoryRequest: Boolean = false,
    val supportsWriteMemoryRequest: Boolean = false,
    val supportsDisassembleRequest: Boolean = false,
    val supportsStepInTargetsRequest: Boolean = false,
    val supportsModulesRequest: Boolean = false,
    val supportsBreakpointLocationsRequest: Boolean = false,
    val supportsDataBreakpointBytes: Boolean = false,
    val supportsANSIStyling: Boolean = false,
    val supportTerminateDebuggee: Boolean = false,
    val supportSuspendDebuggee: Boolean = false,
    val completionTriggerCharacters: List<String>? = null,
    val additionalModuleColumns: List<ColumnDescriptor>? = null,
    val supportedChecksumAlgorithms: List<String>? = null,
    val breakpointModes: List<BreakpointMode>? = null,
    val exceptionBreakpointFilters: List<ExceptionBreakpointsFilter>? = null,
)

@Serializable
data class ExceptionBreakpointsFilter(
    val filter: String,
    val label: String,
    val description: String? = null,
    val default: Boolean = false,
    val supportsCondition: Boolean = false,
    val conditionDescription: String? = null,
)

/** A module attribute column a client is able to show (`Capabilities.additionalModuleColumns`). */
@Serializable
data class ColumnDescriptor(
    val attributeName: String,
    val label: String,
    val format: String? = null,
    /** One of `string`, `number`, `boolean`, `unixTimestampUTC`. */
    val type: String? = null,
    val width: Int? = null,
)

/** A breakpoint mode a client offers when setting breakpoints (`Capabilities.breakpointModes`). */
@Serializable
data class BreakpointMode(
    /** The internal id passed back in the `mode` of `setBreakpoints` / `setDataBreakpoints`. */
    val mode: String,
    val label: String,
    /** Where this mode applies: `source`, `exception`, `data`, or `instruction`. */
    val appliesTo: List<String>,
    val description: String? = null,
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
    /** One of `normal`, `emphasize`, `deemphasize`. */
    val presentationHint: String? = null,
    val origin: String? = null,
    val sources: List<Source>? = null,
    val adapterData: JsonElement? = null,
    val checksums: List<Checksum>? = null,
)

/** A checksum of a source, as carried by [Source.checksums]. */
@Serializable
data class Checksum(
    /** One of the constants in [ChecksumAlgorithm]. */
    val algorithm: String,
    val checksum: String,
)

/** Checksum algorithms a source checksum may be calculated with. */
object ChecksumAlgorithm {
    const val MD5 = "MD5"
    const val SHA1 = "SHA1"
    const val SHA256 = "SHA256"
    const val Timestamp = "timestamp"
}

@Serializable
data class SourceBreakpoint(
    val line: Int,
    val column: Int? = null,
    val condition: String? = null,
    val hitCondition: String? = null,
    val logMessage: String? = null,
    /** A mode id from [Capabilities.breakpointModes]. */
    val mode: String? = null,
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
    val instructionReference: String? = null,
    /** Offset in bytes from [instructionReference]. */
    val offset: Int? = null,
    /** One of `pending`, `failed`. */
    val reason: String? = null,
)

@Serializable
data class SetBreakpointsResponseBody(
    val breakpoints: List<Breakpoint>,
)

/** The `breakpointLocations` request arguments. */
@Serializable
data class BreakpointLocationsArguments(
    val source: Source,
    val line: Int,
    val column: Int? = null,
    val endLine: Int? = null,
    val endColumn: Int? = null,
)

/** A location in a source an adapter is able to set a breakpoint at. */
@Serializable
data class BreakpointLocation(
    val line: Int,
    val column: Int? = null,
    val endLine: Int? = null,
    val endColumn: Int? = null,
)

/** The `breakpointLocations` request result. */
@Serializable
data class BreakpointLocationsResponseBody(val breakpoints: List<BreakpointLocation>)

/** An instruction breakpoint, as used by `setInstructionBreakpoints`. */
@Serializable
data class InstructionBreakpoint(
    val instructionReference: String,
    /** Offset in bytes from [instructionReference]. */
    val offset: Int? = null,
    val condition: String? = null,
    val hitCondition: String? = null,
    /** A mode id from [Capabilities.breakpointModes]. */
    val mode: String? = null,
)

/** The `setInstructionBreakpoints` request arguments. */
@Serializable
data class SetInstructionBreakpointsArguments(val breakpoints: List<InstructionBreakpoint>)

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
    val format: StackFrameFormat? = null,
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
    val instructionPointerReference: String? = null,
    val moduleId: ModuleId? = null,
    /** One of `normal`, `label`, `subtle`. */
    val presentationHint: String? = null,
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
    /** One of `arguments`, `locals`, `registers`, `returnValue`. */
    val presentationHint: String? = null,
    val namedVariables: Int? = null,
    val indexedVariables: Int? = null,
)

/** The `variables` request arguments/result. */
@Serializable
data class VariablesArguments(
    val variablesReference: Int,
    val filter: String? = null,
    val start: Int? = null,
    val count: Int? = null,
    val format: ValueFormat? = null,
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
    val presentationHint: VariablePresentationHint? = null,
    val evaluateName: String? = null,
    /** A reference resolved with the `locations` request. */
    val declarationLocationReference: Int? = null,
    /** A reference resolved with the `locations` request. */
    val valueLocationReference: Int? = null,
)

/** How a client should present a [Variable] (or an evaluated expression). */
@Serializable
data class VariablePresentationHint(
    /** One of `property`, `method`, `class`, `data`, `event`, `baseClass`, `innerClass`, `interface`, `mostDerivedClass`, `virtual`, `dataBreakpoint`. */
    val kind: String? = null,
    /** e.g. `static`, `constant`, `readOnly`, `rawString`, `hasObjectId`, `canHaveObjectId`, `hasSideEffects`, `hasDataBreakpoint`. */
    val attributes: List<String>? = null,
    /** One of `public`, `private`, `protected`, `internal`, `final`. */
    val visibility: String? = null,
    val lazy: Boolean? = null,
)

/** The `continue` request arguments/result. */
@Serializable
data class ContinueArguments(
    val threadId: Int,
    val singleThread: Boolean? = null,
)

@Serializable
data class ContinueResponseBody(
    /** Absent means all threads were resumed. */
    val allThreadsContinued: Boolean? = null,
)

/** The `evaluate` request arguments/result. */
@Serializable
data class EvaluateArguments(
    val expression: String,
    val frameId: Int? = null,
    val context: String? = null,
    val format: ValueFormat? = null,
    val line: Int? = null,
    val column: Int? = null,
    val source: Source? = null,
)

@Serializable
data class EvaluateResponseBody(
    val result: String,
    val type: String? = null,
    val variablesReference: Int = 0,
    val namedVariables: Int? = null,
    val indexedVariables: Int? = null,
    val memoryReference: String? = null,
    val presentationHint: VariablePresentationHint? = null,
    /** A reference resolved with the `locations` request. */
    val valueLocationReference: Int? = null,
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
    /** The restart data an adapter passed in `terminated`, when the client restarts a session. */
    @kotlinx.serialization.SerialName("__restart")
    val restartData: JsonElement? = null,
)

/** The `attach` request arguments. */
@Serializable
data class AttachRequestArguments(
    /** The restart data an adapter passed in `terminated`, when the client restarts a session. */
    @kotlinx.serialization.SerialName("__restart")
    val restartData: JsonElement? = null,
)

// ==================== Event bodies ====================
//
// An adapter event carries one of these in its `body`. Decode them with the
// matching serializer when registering a handler on a DAP client, e.g.
// `onEvent(DapEvent.Stopped, StoppedEventBody.serializer()) { it?.threadId }`.

/** The `stopped` event body. */
@Serializable
data class StoppedEventBody(
    val reason: String,
    val description: String? = null,
    val threadId: Int? = null,
    val preserveFocusHint: Boolean? = null,
    val text: String? = null,
    val allThreadsStopped: Boolean? = null,
    val hitBreakpointIds: List<Int>? = null,
)

/** The `continued` event body. */
@Serializable
data class ContinuedEventBody(
    val threadId: Int,
    val allThreadsContinued: Boolean? = null,
)

/** The `exited` event body. */
@Serializable
data class ExitedEventBody(val exitCode: Int)

/** The `terminated` event body; `restart` asks the client to start a new session. */
@Serializable
data class TerminatedEventBody(
    val restart: JsonElement? = null,
)

/** The `thread` event body (`reason` is `started` or `exited`). */
@Serializable
data class ThreadEventBody(
    val reason: String,
    val threadId: Int,
)

/** The `output` event body. */
@Serializable
data class OutputEventBody(
    val category: String? = null,
    val output: String,
    val group: String? = null,
    val variablesReference: Int? = null,
    val source: Source? = null,
    val line: Int? = null,
    val column: Int? = null,
    val data: JsonElement? = null,
    /** A reference the client can resolve with a `locations` request. */
    val locationReference: Int? = null,
)

/** The `breakpoint` event body (a breakpoint changed, e.g. verification). */
@Serializable
data class BreakpointEventBody(
    val reason: String,
    val breakpoint: Breakpoint,
)

/** The `module` event body. */
@Serializable
data class ModuleEventBody(
    val reason: String,
    val module: Module,
)

/** The `loadedSource` event body. */
@Serializable
data class LoadedSourceEventBody(
    val reason: String,
    val source: Source,
)

/** The `process` event body. */
@Serializable
data class ProcessEventBody(
    val name: String,
    val systemProcessId: Int? = null,
    val isLocalProcess: Boolean? = null,
    val startMethod: String? = null,
    val pointerSize: Int? = null,
)

/** The `capabilities` event body: an adapter announcing updated capabilities. */
@Serializable
data class CapabilitiesEventBody(val capabilities: Capabilities)

/** The `invalidated` event body: parts of the UI a client caches are stale. */
@Serializable
data class InvalidatedEventBody(
    val areas: List<String>? = null,
    val threadId: Int? = null,
    val stackFrameId: Int? = null,
)

/** Area names for [InvalidatedEventBody]. */
object InvalidatedAreas {
    const val All = "all"
    const val Stacks = "stacks"
    const val Threads = "threads"
    const val Variables = "variables"
}

/** The `progressStart` event body. */
@Serializable
data class ProgressStartEventBody(
    val progressId: String,
    val title: String,
    val requestId: Int? = null,
    val cancellable: Boolean? = null,
    val message: String? = null,
    val percentage: Double? = null,
)

/** The `progressUpdate` event body. */
@Serializable
data class ProgressUpdateEventBody(
    val progressId: String,
    val message: String? = null,
    val percentage: Double? = null,
)

/** The `progressEnd` event body. */
@Serializable
data class ProgressEndEventBody(
    val progressId: String,
    val message: String? = null,
)

/** The `memory` event body: memory was invalidated or changed. */
@Serializable
data class MemoryEventBody(
    val memoryReference: String,
    /** Starting offset in bytes of the updated range; may be negative. */
    val offset: Int,
    /** Number of bytes updated. */
    val count: Int,
)

// ==================== Remaining requests ====================

/** The `disconnect` request arguments. */
@Serializable
data class DisconnectArguments(
    val restart: Boolean? = null,
    val terminateDebuggee: Boolean? = null,
    val suspendDebuggee: Boolean? = null,
)

/** The `terminate` request arguments. */
@Serializable
data class TerminateArguments(val restart: Boolean? = null)

/** The `restart` request arguments. */
@Serializable
data class RestartArguments(val arguments: JsonElement? = null)

/** The `setFunctionBreakpoints` request arguments. */
@Serializable
data class SetFunctionBreakpointsArguments(val breakpoints: List<FunctionBreakpoint>)

@Serializable
data class FunctionBreakpoint(
    val name: String,
    val condition: String? = null,
    val hitCondition: String? = null,
)

/** The `setExceptionBreakpoints` request arguments. */
@Serializable
data class SetExceptionBreakpointsArguments(
    val filters: List<String>,
    val filterOptions: List<ExceptionFilterOptions>? = null,
    val exceptionOptions: List<ExceptionOptions>? = null,
)

@Serializable
data class ExceptionFilterOptions(
    val filterId: String,
    val condition: String? = null,
    /** A mode id from [Capabilities.breakpointModes]. */
    val mode: String? = null,
)

/** What to do when an exception matched by [path] is thrown. */
@Serializable
data class ExceptionOptions(
    val path: List<ExceptionPathSegment>? = null,
    /** One of `never`, `always`, `unhandled`, `userUnhandled`. */
    val breakMode: String,
)

/** One segment of an [ExceptionOptions.path]: a type name, optionally negated. */
@Serializable
data class ExceptionPathSegment(
    val names: List<String>,
    val negate: Boolean? = null,
)

/** The `setVariable` request arguments/result. */
@Serializable
data class SetVariableArguments(
    val variablesReference: Int,
    val name: String,
    val value: String,
    val format: ValueFormat? = null,
)

@Serializable
data class SetVariableResponseBody(
    val value: String,
    val type: String? = null,
    val variablesReference: Int = 0,
    val namedVariables: Int? = null,
    val indexedVariables: Int? = null,
    val memoryReference: String? = null,
    /** A reference resolved with the `locations` request. */
    val valueLocationReference: Int? = null,
)

/** The `setExpression` request arguments/result (assign to an expression). */
@Serializable
data class SetExpressionArguments(
    val expression: String,
    val value: String,
    val frameId: Int? = null,
    val format: ValueFormat? = null,
)

@Serializable
data class SetExpressionResponseBody(
    val value: String,
    val type: String? = null,
    val presentationHint: VariablePresentationHint? = null,
    val variablesReference: Int = 0,
    val namedVariables: Int? = null,
    val indexedVariables: Int? = null,
    val memoryReference: String? = null,
    /** A reference resolved with the `locations` request. */
    val valueLocationReference: Int? = null,
)

/** The `completions` request arguments/result (REPL / debug console). */
@Serializable
data class CompletionsArguments(
    val text: String,
    val column: Int,
    val frameId: Int? = null,
    val line: Int? = null,
)

@Serializable
data class CompletionsResponseBody(val targets: List<CompletionItem>)

@Serializable
data class CompletionItem(
    val label: String,
    val text: String? = null,
    val sortText: String? = null,
    val detail: String? = null,
    val type: String? = null,
    val start: Int? = null,
    val length: Int? = null,
    val selectionStart: Int? = null,
    val selectionLength: Int? = null,
)

/** The `exceptionInfo` request arguments/result. */
@Serializable
data class ExceptionInfoArguments(val threadId: Int)

@Serializable
data class ExceptionInfoResponseBody(
    val exceptionId: String,
    val description: String? = null,
    val breakMode: String,
    val details: ExceptionDetails? = null,
)

@Serializable
data class ExceptionDetails(
    val message: String? = null,
    val typeName: String? = null,
    val fullTypeName: String? = null,
    val evaluateName: String? = null,
    val stackTrace: String? = null,
    val innerException: List<ExceptionDetails>? = null,
)

/** The `loadedSources` request result. */
@Serializable
data class LoadedSourcesResponseBody(val sources: List<Source>)

/** The `modules` request arguments/result. */
@Serializable
data class ModulesArguments(
    val startModule: Int? = null,
    val moduleCount: Int? = null,
)

@Serializable
data class ModulesResponseBody(
    val modules: List<Module>,
    val totalModules: Int? = null,
)

/** A module id: the spec's `integer | string`. */
@Serializable(with = ModuleIdSerializer::class)
sealed interface ModuleId {
    @JvmInline
    value class NumberValue(val value: Long) : ModuleId

    @JvmInline
    value class StringValue(val value: String) : ModuleId
}

object ModuleIdSerializer : KSerializer<ModuleId> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ModuleId")

    override fun serialize(encoder: Encoder, value: ModuleId) {
        val element = when (value) {
            is ModuleId.NumberValue -> JsonPrimitive(value.value)
            is ModuleId.StringValue -> JsonPrimitive(value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): ModuleId {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        val primitive = element as? JsonPrimitive
            ?: throw SerializationException("A module id is an integer or a string")
        if (primitive.isString) return ModuleId.StringValue(primitive.content)
        return primitive.content.toLongOrNull()?.let { ModuleId.NumberValue(it) }
            ?: throw SerializationException("A module id is an integer or a string")
    }
}

@Serializable
data class Module(
    val id: ModuleId,
    val name: String,
    val path: String? = null,
    val isOptimized: Boolean? = null,
    val isUserCode: Boolean? = null,
    val version: String? = null,
    val symbolStatus: String? = null,
    val symbolFilePath: String? = null,
    val dateTimeStamp: String? = null,
    val addressRange: String? = null,
)

/** The `goto` request arguments. */
@Serializable
data class GotoArguments(
    val threadId: Int,
    val targetId: Int,
)

/** The `gotoTargets` request arguments/result. */
@Serializable
data class GotoTargetsArguments(
    val source: Source,
    val line: Int,
    val column: Int? = null,
)

@Serializable
data class GotoTargetsResponseBody(val targets: List<GotoTarget>)

@Serializable
data class GotoTarget(
    val id: Int,
    val label: String,
    val line: Int,
    val column: Int? = null,
    val endLine: Int? = null,
    val endColumn: Int? = null,
    val instructionPointerReference: String? = null,
)

/** The `dataBreakpointInfo` request arguments/result. */
@Serializable
data class DataBreakpointInfoArguments(
    val name: String,
    val variablesReference: Int? = null,
    val frameId: Int? = null,
    /** Size of the data to watch, for byte-granular breakpoints. */
    val bytes: Int? = null,
    /** Whether to watch an address rather than the object behind the name. */
    val asAddress: Boolean? = null,
    /** A mode id from [Capabilities.breakpointModes]. */
    val mode: String? = null,
)

@Serializable
data class DataBreakpointInfoResponseBody(
    val dataId: String? = null,
    val description: String,
    val accessTypes: List<String>? = null,
    val canPersist: Boolean? = null,
)

/** The `setDataBreakpoints` request arguments/result. */
@Serializable
data class SetDataBreakpointsArguments(val breakpoints: List<DataBreakpoint>)

@Serializable
data class SetDataBreakpointsResponseBody(val breakpoints: List<Breakpoint>)

@Serializable
data class DataBreakpoint(
    val dataId: String,
    val accessType: String? = null,
    val condition: String? = null,
    val hitCondition: String? = null,
)

/** The `source` request arguments/result. */
@Serializable
data class SourceArguments(
    val sourceReference: Int,
    /** The source whose content is requested, when the adapter wants it echoed back. */
    val source: Source? = null,
)

@Serializable
data class SourceResponseBody(
    val content: String,
    val mimeType: String? = null,
)

/** The `locations` request arguments: resolve a reference from an event or response. */
@Serializable
data class LocationsArguments(val locationReference: Int)

/** The `locations` request result. */
@Serializable
data class LocationsResponseBody(
    val source: Source,
    val line: Int,
    val column: Int? = null,
    val endLine: Int? = null,
    val endColumn: Int? = null,
)

/** The `disassemble` request arguments. */
@Serializable
data class DisassembleArguments(
    val memoryReference: String,
    val instructionCount: Int,
    /** Offset in bytes from the reference location. */
    val offset: Int? = null,
    /** Offset in instructions from the reference location. */
    val instructionOffset: Int? = null,
    val resolveSymbols: Boolean? = null,
)

/** One disassembled instruction. */
@Serializable
data class DisassembledInstruction(
    /** The address of the instruction, in the adapter's own format. */
    val address: String,
    val instruction: String,
    val instructionBytes: String? = null,
    val symbol: String? = null,
    val location: Source? = null,
    val line: Int? = null,
    val column: Int? = null,
    val endLine: Int? = null,
    val endColumn: Int? = null,
    /** One of `normal`, `invalid`. */
    val presentationHint: String? = null,
)

/** The `disassemble` request result. */
@Serializable
data class DisassembleResponseBody(val instructions: List<DisassembledInstruction>)

/** The `readMemory` request arguments. */
@Serializable
data class ReadMemoryArguments(
    val memoryReference: String,
    val count: Int,
    /** Offset in bytes from the reference location. */
    val offset: Int? = null,
)

/** The `readMemory` request result; `data` is base64-encoded. */
@Serializable
data class ReadMemoryResponseBody(
    /** The address of the first byte of data, in the adapter's own format. */
    val address: String,
    val unreadableBytes: Int? = null,
    val data: String? = null,
)

/** The `writeMemory` request arguments; `data` is base64-encoded. */
@Serializable
data class WriteMemoryArguments(
    val memoryReference: String,
    val data: String,
    /** Offset in bytes from the reference location. */
    val offset: Int? = null,
    val allowPartial: Boolean? = null,
)

/** The `writeMemory` request result. */
@Serializable
data class WriteMemoryResponseBody(
    /** Offset of the first byte that was not written. */
    val offset: Int? = null,
    val bytesWritten: Int? = null,
)

/** The `next` request arguments. */
@Serializable
data class NextArguments(
    val threadId: Int,
    val singleThread: Boolean? = null,
    val granularity: String? = null,
)

/** The `stepIn` request arguments. */
@Serializable
data class StepInArguments(
    val threadId: Int,
    val targetId: Int? = null,
    val singleThread: Boolean? = null,
    val granularity: String? = null,
)

/** The `stepOut` request arguments. */
@Serializable
data class StepOutArguments(
    val threadId: Int,
    val singleThread: Boolean? = null,
    val granularity: String? = null,
)

/** The `stepBack` request arguments (reverse debugging). */
@Serializable
data class StepBackArguments(
    val threadId: Int,
    val singleThread: Boolean? = null,
    val granularity: String? = null,
)

/** The `reverseContinue` request arguments (reverse debugging). */
@Serializable
data class ReverseContinueArguments(
    val threadId: Int,
    val singleThread: Boolean? = null,
)

@Serializable
data class PauseArguments(val threadId: Int)

/** The `restartFrame` request arguments. */
@Serializable
data class RestartFrameArguments(val frameId: Int)

/** The `stepInTargets` request arguments/result: where `stepIn` can go from a frame. */
@Serializable
data class StepInTargetsArguments(val frameId: Int)

@Serializable
data class StepInTargetsResponseBody(val targets: List<StepInTarget>)

@Serializable
data class StepInTarget(
    val id: Int,
    val label: String,
    val line: Int? = null,
    val column: Int? = null,
    val endLine: Int? = null,
    val endColumn: Int? = null,
)

/** The `terminateThreads` request arguments. */
@Serializable
data class TerminateThreadsArguments(
    /** The threads to terminate; absent means all threads. */
    val threadIds: List<Int>? = null,
)

/** The `cancel` request arguments. */
@Serializable
data class CancelArguments(
    val requestId: Int? = null,
    val progressId: String? = null,
)

// ==================== Adapter -> client requests ====================
//
// The reverse direction: a debug adapter asks the client to do something the
// adapter cannot do itself. A client that never answers leaves the adapter
// waiting, so a session must respond to these (even if only to decline).

/** The `runInTerminal` request arguments. */
@Serializable
data class RunInTerminalArguments(
    val kind: String? = null,
    val title: String? = null,
    val cwd: String? = null,
    val args: List<String> = emptyList(),
    val env: Map<String, String>? = null,
    val argsCanBeInterpretedByShell: Boolean? = null,
)

/** The `runInTerminal` response body: the ids of what was started. */
@Serializable
data class RunInTerminalResponseBody(
    val processId: Int? = null,
    val shellProcessId: Int? = null,
)

/** The `startDebugging` request arguments: launch another debug session. */
@Serializable
data class StartDebuggingArguments(
    /** `launch` or `attach`. */
    val request: String,
    /** The configuration of the session to start; adapter-defined. */
    val configuration: JsonElement,
)
