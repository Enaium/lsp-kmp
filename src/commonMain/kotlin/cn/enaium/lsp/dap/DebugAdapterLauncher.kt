package cn.enaium.lsp.dap

import cn.enaium.lsp.jsonrpc.JsonRpcLauncher
import cn.enaium.lsp.dap.model.*
import cn.enaium.lsp.jsonrpc.MessageTransport
import kotlinx.serialization.json.JsonElement

/**
 * Wires a [DebugAdapter] to a [MessageTransport], registering the standard DAP
 * request commands and providing a [DebugClient] that sends events to the
 * debug client.
 */
class DebugAdapterLauncher(
    transport: MessageTransport,
    private val adapter: DebugAdapter,
) {
    private val launcher = JsonRpcLauncher(transport)

    /** Client-facing facade that sends events to the connected debug client. */
    val client: DebugClient = object : DebugClient {
        override fun sendEvent(event: String, body: JsonElement?) {
            launcher.notify(event, body)
        }
    }

    init {
        launcher.onRequest(DapCommands.Initialize, InitializeRequestArguments.serializer(), Capabilities.serializer()) {
            adapter.initialize(it)
        }
        launcher.onRequest(DapCommands.ConfigurationDone) { adapter.configurationDone() }
        launcher.onRequest(DapCommands.Launch) { adapter.launch(it) }
        launcher.onRequest(DapCommands.Attach) { adapter.attach(it) }
        launcher.onRequest(DapCommands.Disconnect) { adapter.disconnect(it) }
        launcher.onRequest(DapCommands.Continue, ContinueArguments.serializer(), ContinueResponseBody.serializer()) {
            adapter.continue_(it)
        }
        launcher.onRequest(DapCommands.Next) { adapter.next(it) }
        launcher.onRequest(DapCommands.StepIn) { adapter.stepIn(it) }
        launcher.onRequest(DapCommands.StepOut) { adapter.stepOut(it) }
        launcher.onRequest(DapCommands.Pause) { adapter.pause(it) }
        launcher.onRequest(DapCommands.SetBreakpoints, SetBreakpointsArguments.serializer(), SetBreakpointsResponseBody.serializer()) {
            adapter.setBreakpoints(it)
        }
        launcher.onRequest(DapCommands.SetFunctionBreakpoints) { adapter.setFunctionBreakpoints(it) }
        launcher.onRequest(DapCommands.SetExceptionBreakpoints) { adapter.setExceptionBreakpoints(it) }
        launcher.onRequest(DapCommands.Threads, ThreadsResponseBody.serializer()) { adapter.threads() }
        launcher.onRequest(DapCommands.StackTrace, StackTraceArguments.serializer(), StackTraceResponseBody.serializer()) {
            adapter.stackTrace(it)
        }
        launcher.onRequest(DapCommands.Scopes, ScopesArguments.serializer(), ScopesResponseBody.serializer()) {
            adapter.scopes(it)
        }
        launcher.onRequest(DapCommands.Variables, VariablesArguments.serializer(), VariablesResponseBody.serializer()) {
            adapter.variables(it)
        }
        launcher.onRequest(DapCommands.Evaluate, EvaluateArguments.serializer(), EvaluateResponseBody.serializer()) {
            adapter.evaluate(it)
        }
        launcher.onRequest(DapCommands.Terminate) { adapter.terminate(it) }
    }

    /** Run the receive loop until the transport closes. */
    fun listen() = launcher.listen()
}