package cn.enaium.lsp.dap

import cn.enaium.lsp.dap.model.*
import cn.enaium.lsp.jsonrpc.JsonRpcJson
import cn.enaium.lsp.jsonrpc.StreamMessageTransport
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.JsonElement
import java.io.PipedInputStream
import java.io.PipedOutputStream
import kotlin.concurrent.thread
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * A debug adapter and a debug client talking over a real transport: the
 * requests a debugging UI makes, the response bodies the spec requires, the
 * ones an adapter is allowed to omit, and the events the client receives.
 */
class DapClientServerTest {

    private class TestAdapter : DebugAdapter {
        var launchedWith: JsonElement? = null
        var configurationDoneCalled = false
        var disconnected: DisconnectArguments? = null
        var continuedThread: Int? = null
        var breakpointLines: List<Int?> = emptyList()
        var restartedFrame: Int? = null
        var lastVariablesArgs: VariablesArguments? = null
        var lastEvaluateArgs: EvaluateArguments? = null
        var lastDataBreakpointArgs: DataBreakpointInfoArguments? = null
        var lastInstructionBreakpoints: SetInstructionBreakpointsArguments? = null
        var terminateThreadsCalled = false
        var terminateThreadsArgs: TerminateThreadsArguments? = null

        override fun initialize(request: InitializeRequestArguments): Capabilities =
            Capabilities(
                supportsConfigurationDoneRequest = true,
                supportsSetVariable = true,
                supportsExceptionInfoRequest = true,
            )

        override fun launch(args: JsonElement?): JsonElement? {
            launchedWith = args
            return null
        }

        override fun configurationDone(): JsonElement? {
            configurationDoneCalled = true
            return null
        }

        override fun disconnect(args: DisconnectArguments?): JsonElement? {
            disconnected = args
            return null
        }

        override fun setBreakpoints(args: SetBreakpointsArguments): SetBreakpointsResponseBody {
            breakpointLines = args.breakpoints.orEmpty().map { it.line }
            return SetBreakpointsResponseBody(
                args.breakpoints.orEmpty().mapIndexed { i, bp ->
                    Breakpoint(id = i + 1, verified = true, line = bp.line)
                },
            )
        }

        override fun threads(): ThreadsResponseBody = ThreadsResponseBody(listOf(Thread(1, "main")))

        override fun stackTrace(args: StackTraceArguments): StackTraceResponseBody =
            StackTraceResponseBody(listOf(StackFrame(id = 7, name = "main", line = 3, column = 1)))

        override fun scopes(args: ScopesArguments): ScopesResponseBody =
            ScopesResponseBody(listOf(Scope(name = "Locals", variablesReference = 100)))

        override fun variables(args: VariablesArguments): VariablesResponseBody {
            lastVariablesArgs = args
            return VariablesResponseBody(listOf(Variable(name = "x", value = "42", type = "Int")))
        }

        override fun setVariable(args: SetVariableArguments): SetVariableResponseBody =
            SetVariableResponseBody(value = args.value, type = "Int")

        override fun evaluate(args: EvaluateArguments): EvaluateResponseBody {
            lastEvaluateArgs = args
            return EvaluateResponseBody(result = "42", type = "Int")
        }

        override fun setExceptionBreakpoints(args: SetExceptionBreakpointsArguments): SetBreakpointsResponseBody =
            SetBreakpointsResponseBody(listOf(Breakpoint(id = 1, verified = true, reason = "pending")))

        override fun breakpointLocations(args: BreakpointLocationsArguments): BreakpointLocationsResponseBody =
            BreakpointLocationsResponseBody(listOf(BreakpointLocation(line = args.line, column = 1)))

        override fun setInstructionBreakpoints(args: SetInstructionBreakpointsArguments): SetBreakpointsResponseBody {
            lastInstructionBreakpoints = args
            return SetBreakpointsResponseBody(
                args.breakpoints.map { Breakpoint(id = 1, verified = true, instructionReference = it.instructionReference) },
            )
        }

        override fun dataBreakpointInfo(args: DataBreakpointInfoArguments): DataBreakpointInfoResponseBody {
            lastDataBreakpointArgs = args
            return DataBreakpointInfoResponseBody(dataId = "data:${args.name}", description = args.name, canPersist = true)
        }

        override fun stepInTargets(args: StepInTargetsArguments): StepInTargetsResponseBody =
            StepInTargetsResponseBody(listOf(StepInTarget(id = 1, label = "into()")))

        override fun terminateThreads(args: TerminateThreadsArguments?): JsonElement? {
            terminateThreadsCalled = true
            terminateThreadsArgs = args
            return null
        }

        override fun restartFrame(args: RestartFrameArguments): JsonElement? {
            restartedFrame = args.frameId
            return null
        }

        override fun locations(args: LocationsArguments): LocationsResponseBody =
            LocationsResponseBody(source = Source(path = "/tmp/demo.kt"), line = args.locationReference, column = 1)

        override fun disassemble(args: DisassembleArguments): DisassembleResponseBody =
            DisassembleResponseBody(
                listOf(DisassembledInstruction(address = "0x1", instruction = "nop", line = args.offset, location = Source(path = "/tmp/demo.kt"))),
            )

        override fun readMemory(args: ReadMemoryArguments): ReadMemoryResponseBody =
            ReadMemoryResponseBody(address = "0x0", data = "AA==", unreadableBytes = args.count - 1)

        override fun writeMemory(args: WriteMemoryArguments): WriteMemoryResponseBody =
            WriteMemoryResponseBody(offset = args.offset, bytesWritten = args.data.length)

        override fun continue_(args: ContinueArguments): ContinueResponseBody {
            continuedThread = args.threadId
            return ContinueResponseBody(allThreadsContinued = true)
        }

        // exceptionInfo and setFunctionBreakpoints stay unimplemented on
        // purpose: the client must see "no body", not an error.
    }

    @Test
    fun clientDrivesAnAdapterAndReceivesEvents() {
        val adapterIn = PipedInputStream()
        val adapterOut = PipedOutputStream()
        val clientOut = PipedOutputStream(adapterIn)
        val clientIn = PipedInputStream(adapterOut)

        val adapter = TestAdapter()
        val adapterLauncher = DebugAdapterLauncher(StreamMessageTransport(adapterIn, adapterOut), adapter)
        val adapterThread = thread(isDaemon = true) { adapterLauncher.listen() }

        val client = DebugClientLauncher(StreamMessageTransport(clientIn, clientOut))
        val clientThread = thread(isDaemon = true) { client.listen() }

        val stopped = CompletableDeferred<StoppedEventBody?>()
        client.onEvent(DapEvent.Stopped, StoppedEventBody.serializer()) { stopped.complete(it) }
        val terminated = CompletableDeferred<Unit>()
        client.onEvent(DapEvent.Terminated) { terminated.complete(Unit) }

        try {
            runBlocking {
                val capabilities = client.initialize(InitializeRequestArguments(adapterID = "lsp-kmp-test"))
                assertTrue(capabilities.supportsConfigurationDoneRequest)
                assertTrue(capabilities.supportsSetVariable)
                assertEquals(capabilities, client.capabilities, "capabilities are kept on the client")

                client.launch(JsonRpcJson.json.parseToJsonElement("""{"program":"demo"}"""))
                client.configurationDone()
                assertTrue(adapter.configurationDoneCalled)

                val breakpoints = client.setBreakpoints(
                    source = Source(path = "/tmp/demo.kt"),
                    breakpoints = listOf(SourceBreakpoint(line = 4), SourceBreakpoint(line = 9)),
                )
                assertEquals(listOf(4, 9), adapter.breakpointLines)
                assertEquals(listOf(4, 9), breakpoints.breakpoints.map { it.line })
                assertTrue(breakpoints.breakpoints.all { it.verified })

                assertEquals(listOf("main"), client.threads().threads.map { it.name })

                val frames = client.stackTrace(1).stackFrames
                assertEquals(1, frames.size)
                assertEquals(7, frames[0].id)

                val scopes = client.scopes(frames[0].id).scopes
                assertEquals(100, scopes[0].variablesReference)

                val variables = client.variables(scopes[0].variablesReference, format = ValueFormat(hex = true)).variables
                assertEquals("x", variables[0].name)
                assertEquals("42", variables[0].value)
                assertEquals(true, adapter.lastVariablesArgs?.format?.hex)

                assertEquals("7", client.setVariable(100, "x", "7")?.value)
                assertEquals("42", client.evaluate("x", frameId = 7, line = 2, column = 3, source = Source(path = "/tmp/demo.kt")).result)
                assertEquals(2, adapter.lastEvaluateArgs?.line)
                assertEquals("/tmp/demo.kt", adapter.lastEvaluateArgs?.source?.path)

                assertEquals(true, client.continue_(1).allThreadsContinued)
                assertEquals(1, adapter.continuedThread)

                // Breakpoints: source, instruction, exception, and the
                // locations an adapter is able to set them at.
                assertEquals("pending", client.setExceptionBreakpoints(listOf("uncaught"))?.breakpoints?.single()?.reason)
                assertEquals(1, client.breakpointLocations(Source(path = "/tmp/demo.kt"), line = 4)?.breakpoints?.single()?.column)
                val instructionBreakpoints = client.setInstructionBreakpoints(
                    listOf(InstructionBreakpoint(instructionReference = "0x10", mode = "hw")),
                )
                assertEquals("0x10", instructionBreakpoints?.breakpoints?.single()?.instructionReference)
                assertEquals("hw", adapter.lastInstructionBreakpoints?.breakpoints?.single()?.mode)
                assertEquals(true, client.dataBreakpointInfo("x", variablesReference = 100, bytes = 4, asAddress = true)?.canPersist)
                assertEquals(4, adapter.lastDataBreakpointArgs?.bytes)
                assertEquals(true, adapter.lastDataBreakpointArgs?.asAddress)

                // Stepping, frame restart, and address resolution.
                assertEquals("into()", client.stepInTargets(7)?.targets?.single()?.label)
                client.restartFrame(7)
                assertEquals(7, adapter.restartedFrame)
                assertEquals(99, client.locations(99)?.line)

                // Disassembly and memory access.
                assertEquals("nop", client.disassemble("mem:1", instructionCount = 2, offset = 0)?.instructions?.single()?.instruction)
                assertEquals("AA==", client.readMemory("mem:1", count = 1)?.data)
                assertEquals(4, client.writeMemory("mem:1", data = "AAAA", offset = 8)?.bytesWritten)

                // Supported by the spec but not implemented by this adapter:
                // a body-less response, not an error.
                assertNull(client.exceptionInfo(1))
                assertNull(client.setFunctionBreakpoints(listOf(FunctionBreakpoint(name = "main"))))

                client.disconnect(DisconnectArguments(terminateDebuggee = true))
                assertEquals(true, adapter.disconnected?.terminateDebuggee)

                // Requests with optional arguments may be sent without a
                // `params` member at all (other DAP clients do). Regression:
                // the typed registration used to reject them as "missing
                // params" even though the serializer accepts null.
                client.terminate()
                client.restart()
                client.terminateThreads()
                assertTrue(adapter.terminateThreadsCalled)
                assertNull(adapter.terminateThreadsArgs, "omitted arguments mean all threads")
                client.terminateThreads(listOf(1, 2))
                assertEquals(listOf(1, 2), adapter.terminateThreadsArgs?.threadIds)
            }

            // Adapter -> client events reach the registered handlers.
            adapterLauncher.client.sendEvent(
                DapEvent.Stopped,
                JsonRpcJson.json.encodeToJsonElement(
                    StoppedEventBody.serializer(),
                    StoppedEventBody(reason = "breakpoint", threadId = 1, allThreadsStopped = true),
                ),
            )
            adapterLauncher.client.sendEvent(DapEvent.Terminated)

            runBlocking {
                val body = withTimeout(5_000) { stopped.await() }
                assertNotNull(body)
                assertEquals("breakpoint", body.reason)
                assertEquals(1, body.threadId)
                withTimeout(5_000) { terminated.await() }
            }
        } finally {
            clientOut.close()
            adapterOut.close()
            adapterThread.join(3_000)
            clientThread.join(3_000)
        }
    }
}
