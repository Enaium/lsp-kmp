package cn.enaium.lsp.dap

import cn.enaium.lsp.dap.model.*
import cn.enaium.lsp.jsonrpc.JsonRpcJson
import cn.enaium.lsp.jsonrpc.MessageTransport
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.json.JsonElement

/**
 * Wires a [DebugAdapter] to a [MessageTransport], registering the standard DAP
 * request commands and providing a [DebugClient] that sends events to the
 * debug client.
 *
 * Commands whose response body is optional are registered with a raw result:
 * a body-less response is what an adapter sends when it has nothing to report,
 * and it is not an error.
 */
class DebugAdapterLauncher(
    transport: MessageTransport,
    private val adapter: DebugAdapter,
) {
    private val launcher = DapLauncher(transport)

    /** Client-facing facade that sends events to the connected debug client. */
    val client: DebugClient = object : DebugClient {
        override fun sendEvent(event: String, body: JsonElement?) {
            launcher.event(event, body)
        }
    }

    init {
        // ==================== Lifecycle ====================
        launcher.onRequest(DapCommands.Initialize, InitializeRequestArguments.serializer(), Capabilities.serializer()) {
            adapter.initialize(it)
        }
        launcher.onRequest(DapCommands.ConfigurationDone) { adapter.configurationDone() }
        // launch/attach arguments are adapter-defined, so they stay untyped.
        launcher.onRequest(DapCommands.Launch) { adapter.launch(it) }
        launcher.onRequest(DapCommands.Attach) { adapter.attach(it) }
        launcher.onRequestJson(DapCommands.Disconnect, DisconnectArguments.serializer().nullable) {
            adapter.disconnect(it)
        }
        launcher.onRequestJson(DapCommands.Terminate, TerminateArguments.serializer().nullable) {
            adapter.terminate(it)
        }
        launcher.onRequestJson(DapCommands.Restart, RestartArguments.serializer().nullable) {
            adapter.restart(it)
        }

        // ==================== Execution control ====================
        launcher.onRequest(DapCommands.Continue, ContinueArguments.serializer(), ContinueResponseBody.serializer()) {
            adapter.continue_(it)
        }
        launcher.onRequestJson(DapCommands.Next, NextArguments.serializer()) { adapter.next(it) }
        launcher.onRequestJson(DapCommands.StepIn, StepInArguments.serializer()) { adapter.stepIn(it) }
        launcher.onRequest(
            DapCommands.StepInTargets,
            StepInTargetsArguments.serializer(),
            StepInTargetsResponseBody.serializer(),
        ) { adapter.stepInTargets(it) }
        launcher.onRequestJson(DapCommands.StepOut, StepOutArguments.serializer()) { adapter.stepOut(it) }
        launcher.onRequestJson(DapCommands.StepBack, StepBackArguments.serializer()) { adapter.stepBack(it) }
        launcher.onRequestJson(DapCommands.ReverseContinue, ReverseContinueArguments.serializer()) {
            adapter.reverseContinue(it)
        }
        launcher.onRequestJson(DapCommands.RestartFrame, RestartFrameArguments.serializer()) {
            adapter.restartFrame(it)
        }
        launcher.onRequestJson(DapCommands.Pause, PauseArguments.serializer()) { adapter.pause(it) }
        launcher.onRequestJson(DapCommands.Goto, GotoArguments.serializer()) { adapter.goto_(it) }
        launcher.onRequest(
            DapCommands.GotoTargets,
            GotoTargetsArguments.serializer(),
            GotoTargetsResponseBody.serializer(),
        ) { adapter.gotoTargets(it) }
        launcher.onRequestJson(DapCommands.TerminateThreads, TerminateThreadsArguments.serializer().nullable) {
            adapter.terminateThreads(it)
        }

        // ==================== Breakpoints ====================
        launcher.onRequest(
            DapCommands.SetBreakpoints,
            SetBreakpointsArguments.serializer(),
            SetBreakpointsResponseBody.serializer(),
        ) { adapter.setBreakpoints(it) }
        launcher.onRequest(
            DapCommands.SetFunctionBreakpoints,
            SetFunctionBreakpointsArguments.serializer(),
            SetBreakpointsResponseBody.serializer(),
        ) { adapter.setFunctionBreakpoints(it) }
        launcher.onRequest(
            DapCommands.SetInstructionBreakpoints,
            SetInstructionBreakpointsArguments.serializer(),
            SetBreakpointsResponseBody.serializer(),
        ) { adapter.setInstructionBreakpoints(it) }
        launcher.onRequest(
            DapCommands.SetExceptionBreakpoints,
            SetExceptionBreakpointsArguments.serializer(),
            SetBreakpointsResponseBody.serializer(),
        ) { adapter.setExceptionBreakpoints(it) }
        launcher.onRequest(
            DapCommands.DataBreakpointInfo,
            DataBreakpointInfoArguments.serializer(),
            DataBreakpointInfoResponseBody.serializer(),
        ) { adapter.dataBreakpointInfo(it) }
        launcher.onRequest(
            DapCommands.SetDataBreakpoints,
            SetDataBreakpointsArguments.serializer(),
            SetDataBreakpointsResponseBody.serializer(),
        ) { adapter.setDataBreakpoints(it) }
        launcher.onRequest(
            DapCommands.BreakpointLocations,
            BreakpointLocationsArguments.serializer(),
            BreakpointLocationsResponseBody.serializer(),
        ) { adapter.breakpointLocations(it) }

        // ==================== Inspection ====================
        launcher.onRequest(DapCommands.Threads, ThreadsResponseBody.serializer()) { adapter.threads() }
        launcher.onRequest(
            DapCommands.StackTrace,
            StackTraceArguments.serializer(),
            StackTraceResponseBody.serializer(),
        ) { adapter.stackTrace(it) }
        launcher.onRequest(DapCommands.Scopes, ScopesArguments.serializer(), ScopesResponseBody.serializer()) {
            adapter.scopes(it)
        }
        launcher.onRequest(
            DapCommands.Variables,
            VariablesArguments.serializer(),
            VariablesResponseBody.serializer(),
        ) { adapter.variables(it) }
        launcher.onRequest(
            DapCommands.SetVariable,
            SetVariableArguments.serializer(),
            SetVariableResponseBody.serializer(),
        ) { adapter.setVariable(it) }
        launcher.onRequest(
            DapCommands.SetExpression,
            SetExpressionArguments.serializer(),
            SetExpressionResponseBody.serializer(),
        ) { adapter.setExpression(it) }
        launcher.onRequest(
            DapCommands.Evaluate,
            EvaluateArguments.serializer(),
            EvaluateResponseBody.serializer(),
        ) { adapter.evaluate(it) }
        launcher.onRequest(
            DapCommands.ExceptionInfo,
            ExceptionInfoArguments.serializer(),
            ExceptionInfoResponseBody.serializer(),
        ) { adapter.exceptionInfo(it) }
        launcher.onRequest(
            DapCommands.Completions,
            CompletionsArguments.serializer(),
            CompletionsResponseBody.serializer(),
        ) { adapter.completions(it) }
        // No arguments, but the body is optional: register a raw params
        // serializer so the body can be omitted (a typed no-argument
        // registration would demand a body).
        launcher.onRequest(
            DapCommands.LoadedSources,
            // Nullable params: the request takes no arguments, and the body is
            // optional, so neither may be demanded.
            JsonElement.serializer().nullable,
            LoadedSourcesResponseBody.serializer(),
        ) { adapter.loadedSources() }
        launcher.onRequest(
            DapCommands.Modules,
            ModulesArguments.serializer().nullable,
            ModulesResponseBody.serializer(),
        ) { adapter.modules(it) }
        launcher.onRequest(DapCommands.Source, SourceArguments.serializer(), SourceResponseBody.serializer()) {
            adapter.source(it)
        }
        launcher.onRequest(
            DapCommands.Locations,
            LocationsArguments.serializer(),
            LocationsResponseBody.serializer(),
        ) { adapter.locations(it) }
        launcher.onRequest(
            DapCommands.Disassemble,
            DisassembleArguments.serializer(),
            DisassembleResponseBody.serializer(),
        ) { adapter.disassemble(it) }
        launcher.onRequest(
            DapCommands.ReadMemory,
            ReadMemoryArguments.serializer(),
            ReadMemoryResponseBody.serializer(),
        ) { adapter.readMemory(it) }
        launcher.onRequest(
            DapCommands.WriteMemory,
            WriteMemoryArguments.serializer(),
            WriteMemoryResponseBody.serializer(),
        ) { adapter.writeMemory(it) }
        launcher.onRequestJson(DapCommands.Cancel, CancelArguments.serializer().nullable) { adapter.cancel(it) }
    }

    // ==================== Requests to the client ====================
    //
    // The reverse direction: an adapter asks the client for something only the
    // client can do (open a terminal, start another session). The client must
    // answer — a request that is ignored leaves the adapter waiting.

    /**
     * The `runInTerminal` request: asks the client to run [args] in a terminal
     * it owns, so the debuggee's input and output are visible to the user.
     */
    suspend fun runInTerminal(args: RunInTerminalArguments): RunInTerminalResponseBody {
        val body = launcher.request(DapCommands.RunInTerminal, args, RunInTerminalArguments.serializer())
            ?: throw DapException(DapCommands.RunInTerminal, "the client sent no body")
        return JsonRpcJson.json.decodeFromJsonElement(RunInTerminalResponseBody.serializer(), body)
    }

    /** The `startDebugging` request: asks the client to start another session. */
    suspend fun startDebugging(args: StartDebuggingArguments) {
        launcher.request(DapCommands.StartDebugging, args, StartDebuggingArguments.serializer())
    }

    /** Run the receive loop until the transport closes. */
    fun listen() = launcher.listen()
}
