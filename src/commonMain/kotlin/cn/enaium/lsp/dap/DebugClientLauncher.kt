package cn.enaium.lsp.dap

import cn.enaium.lsp.dap.model.*
import cn.enaium.lsp.jsonrpc.JsonRpcJson
import cn.enaium.lsp.jsonrpc.MessageTransport
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull

/**
 * The client end of a DAP connection: sends the standard requests to a debug
 * adapter and delivers the adapter's events to handlers registered here.
 *
 * This is the counterpart of [DebugAdapterLauncher]: that one serves an adapter
 * you implement and hands it a [DebugClient] to send events with, this one *is*
 * the client — you call [initialize], set breakpoints and step, and receive
 * `stopped`/`output`/`terminated` through [onEvent].
 *
 * Requests suspend until the adapter answers, so [listen] must be running
 * concurrently for them to complete:
 *
 * ```kotlin
 * val client = DebugClientLauncher(transport)
 * scope.launch { client.listen() }
 * val capabilities = client.initialize(InitializeRequestArguments(adapterID = "my-debugger"))
 * client.onEvent(DapEvent.Stopped, StoppedEventBody.serializer()) { body ->
 *     body?.threadId?.let { showStack(client.stackTrace(it).stackFrames) }
 * }
 * ```
 *
 * Bodies the spec makes optional are nullable: a conforming adapter may answer
 * a request it supports with no body at all, which is not an error.
 */
class DebugClientLauncher(
    val transport: MessageTransport,
) {
    private val launcher = DapLauncher(transport)

    /** The adapter's capabilities, populated by [initialize]. */
    var capabilities: Capabilities? = null
        private set

    /** Completed when the adapter announces readiness with `initialized`. */
    private var initialized = CompletableDeferred<Unit>()

    init {
        // The adapter signals readiness with this event; breakpoints sent
        // before it arrive before the adapter can accept them (debugpy rejects
        // them outright with "Server is not available").
        onEventRaw(DapEvent.Initialized) { initialized.complete(Unit) }
    }

    /**
     * Suspends until the adapter has sent `initialized` — the point from which
     * breakpoint requests are accepted — or [timeoutMs] passes.
     *
     * Returns false when the adapter never announced readiness (a minimal
     * adapter may not), so a caller can decide to continue anyway.
     */
    suspend fun awaitInitialized(timeoutMs: Long = 10_000): Boolean {
        val pending = initialized
        return withTimeoutOrNull(timeoutMs) { pending.await() } != null
    }

    // ==================== Lifecycle ====================

    /** The `initialize` request; must be the first request of a session. */
    suspend fun initialize(args: InitializeRequestArguments): Capabilities {
        initialized = CompletableDeferred()
        return request(
            DapCommands.Initialize,
            args,
            InitializeRequestArguments.serializer(),
            Capabilities.serializer(),
        ).also { capabilities = it }
    }

    /** The `launch` request; the arguments are adapter-defined. */
    suspend fun launch(args: JsonElement? = null): JsonElement = raw(DapCommands.Launch, args)

    /** The `attach` request; the arguments are adapter-defined. */
    suspend fun attach(args: JsonElement? = null): JsonElement = raw(DapCommands.Attach, args)

    /**
     * Sends [command] without waiting for its response.
     *
     * `launch` and `attach` are answered only once the debuggee is running —
     * which takes `configurationDone` — so awaiting them deadlocks the
     * handshake. A client sends them and goes on with the breakpoint sequence
     * (what VS Code does), then inspects the returned deferred for failures.
     */
    fun send(command: String, arguments: JsonElement? = null): CompletableDeferred<JsonElement?> =
        launcher.send(command, arguments)

    /** The `configurationDone` request: start running once setup is complete. */
    suspend fun configurationDone(): JsonElement = raw(DapCommands.ConfigurationDone)

    /** The `disconnect` request. */
    suspend fun disconnect(args: DisconnectArguments? = null): JsonElement =
        optional(DapCommands.Disconnect, args, DisconnectArguments.serializer())

    /** The `terminate` request. */
    suspend fun terminate(args: TerminateArguments? = null): JsonElement =
        optional(DapCommands.Terminate, args, TerminateArguments.serializer())

    /** The `restart` request. */
    suspend fun restart(args: RestartArguments? = null): JsonElement =
        optional(DapCommands.Restart, args, RestartArguments.serializer())

    // ==================== Breakpoints ====================

    /** The `setBreakpoints` request; returns the adapter's breakpoints as verified. */
    suspend fun setBreakpoints(
        source: Source,
        breakpoints: List<SourceBreakpoint>,
        sourceModified: Boolean? = null,
    ): SetBreakpointsResponseBody = request(
        DapCommands.SetBreakpoints,
        SetBreakpointsArguments(source = source, breakpoints = breakpoints, sourceModified = sourceModified),
        SetBreakpointsArguments.serializer(),
        SetBreakpointsResponseBody.serializer(),
    )

    /** The `setFunctionBreakpoints` request. */
    suspend fun setFunctionBreakpoints(breakpoints: List<FunctionBreakpoint>): SetBreakpointsResponseBody? =
        optional(
            DapCommands.SetFunctionBreakpoints,
            SetFunctionBreakpointsArguments(breakpoints),
            SetFunctionBreakpointsArguments.serializer(),
            SetBreakpointsResponseBody.serializer(),
        )

    /** The `setExceptionBreakpoints` request. */
    suspend fun setExceptionBreakpoints(
        filters: List<String>,
        filterOptions: List<ExceptionFilterOptions>? = null,
        exceptionOptions: List<ExceptionOptions>? = null,
    ): SetBreakpointsResponseBody? = optional(
        DapCommands.SetExceptionBreakpoints,
        SetExceptionBreakpointsArguments(
            filters = filters,
            filterOptions = filterOptions,
            exceptionOptions = exceptionOptions,
        ),
        SetExceptionBreakpointsArguments.serializer(),
        SetBreakpointsResponseBody.serializer(),
    )

    /** The `setInstructionBreakpoints` request. */
    suspend fun setInstructionBreakpoints(breakpoints: List<InstructionBreakpoint>): SetBreakpointsResponseBody? =
        optional(
            DapCommands.SetInstructionBreakpoints,
            SetInstructionBreakpointsArguments(breakpoints),
            SetInstructionBreakpointsArguments.serializer(),
            SetBreakpointsResponseBody.serializer(),
        )

    /** The `breakpointLocations` request: legal breakpoint positions in a source range. */
    suspend fun breakpointLocations(
        source: Source,
        line: Int,
        column: Int? = null,
        endLine: Int? = null,
        endColumn: Int? = null,
    ): BreakpointLocationsResponseBody? = optional(
        DapCommands.BreakpointLocations,
        BreakpointLocationsArguments(source = source, line = line, column = column, endLine = endLine, endColumn = endColumn),
        BreakpointLocationsArguments.serializer(),
        BreakpointLocationsResponseBody.serializer(),
    )

    /** The `dataBreakpointInfo` request: can [name] be watched? */
    suspend fun dataBreakpointInfo(
        name: String,
        variablesReference: Int? = null,
        frameId: Int? = null,
        bytes: Int? = null,
        asAddress: Boolean? = null,
        mode: String? = null,
    ): DataBreakpointInfoResponseBody? = optional(
        DapCommands.DataBreakpointInfo,
        DataBreakpointInfoArguments(
            name = name,
            variablesReference = variablesReference,
            frameId = frameId,
            bytes = bytes,
            asAddress = asAddress,
            mode = mode,
        ),
        DataBreakpointInfoArguments.serializer(),
        DataBreakpointInfoResponseBody.serializer(),
    )

    /** The `setDataBreakpoints` request. */
    suspend fun setDataBreakpoints(breakpoints: List<DataBreakpoint>): SetDataBreakpointsResponseBody? = optional(
        DapCommands.SetDataBreakpoints,
        SetDataBreakpointsArguments(breakpoints),
        SetDataBreakpointsArguments.serializer(),
        SetDataBreakpointsResponseBody.serializer(),
    )

    // ==================== Execution control ====================

    /** The `continue` request. */
    suspend fun continue_(threadId: Int, singleThread: Boolean? = null): ContinueResponseBody = request(
        DapCommands.Continue,
        ContinueArguments(threadId = threadId, singleThread = singleThread),
        ContinueArguments.serializer(),
        ContinueResponseBody.serializer(),
    )

    /** The `next` (step over) request. */
    suspend fun next(
        threadId: Int,
        granularity: String? = null,
        singleThread: Boolean? = null,
    ): JsonElement = optional(
        DapCommands.Next,
        NextArguments(threadId = threadId, singleThread = singleThread, granularity = granularity),
        NextArguments.serializer(),
    )

    /** The `stepIn` request. */
    suspend fun stepIn(
        threadId: Int,
        targetId: Int? = null,
        granularity: String? = null,
        singleThread: Boolean? = null,
    ): JsonElement = optional(
        DapCommands.StepIn,
        StepInArguments(
            threadId = threadId,
            targetId = targetId,
            singleThread = singleThread,
            granularity = granularity,
        ),
        StepInArguments.serializer(),
    )

    /** The `stepInTargets` request: where `stepIn` can go from [frameId]. */
    suspend fun stepInTargets(frameId: Int): StepInTargetsResponseBody? = optional(
        DapCommands.StepInTargets,
        StepInTargetsArguments(frameId),
        StepInTargetsArguments.serializer(),
        StepInTargetsResponseBody.serializer(),
    )

    /** The `stepOut` request. */
    suspend fun stepOut(
        threadId: Int,
        granularity: String? = null,
        singleThread: Boolean? = null,
    ): JsonElement = optional(
        DapCommands.StepOut,
        StepOutArguments(threadId = threadId, singleThread = singleThread, granularity = granularity),
        StepOutArguments.serializer(),
    )

    /** The `stepBack` request (reverse debugging). */
    suspend fun stepBack(
        threadId: Int,
        granularity: String? = null,
        singleThread: Boolean? = null,
    ): JsonElement = optional(
        DapCommands.StepBack,
        StepBackArguments(threadId = threadId, singleThread = singleThread, granularity = granularity),
        StepBackArguments.serializer(),
    )

    /** The `reverseContinue` request (reverse debugging). */
    suspend fun reverseContinue(threadId: Int, singleThread: Boolean? = null): JsonElement = optional(
        DapCommands.ReverseContinue,
        ReverseContinueArguments(threadId = threadId, singleThread = singleThread),
        ReverseContinueArguments.serializer(),
    )

    /** The `restartFrame` request: re-enter a stack frame. */
    suspend fun restartFrame(frameId: Int): JsonElement = optional(
        DapCommands.RestartFrame,
        RestartFrameArguments(frameId),
        RestartFrameArguments.serializer(),
    )

    /** The `pause` request. */
    suspend fun pause(threadId: Int): JsonElement =
        optional(DapCommands.Pause, PauseArguments(threadId), PauseArguments.serializer())

    /** The `goto` request. */
    suspend fun goto_(threadId: Int, targetId: Int): JsonElement =
        optional(DapCommands.Goto, GotoArguments(threadId, targetId), GotoArguments.serializer())

    /** The `gotoTargets` request: where can [threadId] jump to in [source]? */
    suspend fun gotoTargets(source: Source, line: Int, column: Int? = null): GotoTargetsResponseBody? = optional(
        DapCommands.GotoTargets,
        GotoTargetsArguments(source = source, line = line, column = column),
        GotoTargetsArguments.serializer(),
        GotoTargetsResponseBody.serializer(),
    )

    /** The `terminateThreads` request; omitting [threadIds] terminates all threads. */
    suspend fun terminateThreads(threadIds: List<Int>? = null): JsonElement = optional(
        DapCommands.TerminateThreads,
        threadIds?.let { TerminateThreadsArguments(it) },
        TerminateThreadsArguments.serializer().nullable,
    )

    /** The `cancel` request: cancel a running request or progress. */
    suspend fun cancel(requestId: Int? = null, progressId: String? = null): JsonElement = optional(
        DapCommands.Cancel,
        CancelArguments(requestId = requestId, progressId = progressId),
        CancelArguments.serializer(),
    )

    // ==================== Inspection ====================

    /** The `threads` request. */
    suspend fun threads(): ThreadsResponseBody = requestNoArgs(DapCommands.Threads, ThreadsResponseBody.serializer())

    /** The `stackTrace` request for [threadId]. */
    suspend fun stackTrace(threadId: Int, startFrame: Int? = null, levels: Int? = null): StackTraceResponseBody =
        request(
            DapCommands.StackTrace,
            StackTraceArguments(threadId = threadId, startFrame = startFrame, levels = levels),
            StackTraceArguments.serializer(),
            StackTraceResponseBody.serializer(),
        )

    /** The `scopes` request for a stack frame. */
    suspend fun scopes(frameId: Int): ScopesResponseBody = request(
        DapCommands.Scopes,
        ScopesArguments(frameId),
        ScopesArguments.serializer(),
        ScopesResponseBody.serializer(),
    )

    /** The `variables` request for a scope or a structured variable. */
    suspend fun variables(
        variablesReference: Int,
        filter: String? = null,
        start: Int? = null,
        count: Int? = null,
        format: ValueFormat? = null,
    ): VariablesResponseBody = request(
        DapCommands.Variables,
        VariablesArguments(
            variablesReference = variablesReference,
            filter = filter,
            start = start,
            count = count,
            format = format,
        ),
        VariablesArguments.serializer(),
        VariablesResponseBody.serializer(),
    )

    /** The `setVariable` request: assign to a variable of a scope or struct. */
    suspend fun setVariable(
        variablesReference: Int,
        name: String,
        value: String,
        format: ValueFormat? = null,
    ): SetVariableResponseBody? = optional(
        DapCommands.SetVariable,
        SetVariableArguments(variablesReference = variablesReference, name = name, value = value, format = format),
        SetVariableArguments.serializer(),
        SetVariableResponseBody.serializer(),
    )

    /** The `setExpression` request: assign to an expression in [frameId]. */
    suspend fun setExpression(
        expression: String,
        value: String,
        frameId: Int? = null,
        format: ValueFormat? = null,
    ): SetExpressionResponseBody? = optional(
        DapCommands.SetExpression,
        SetExpressionArguments(expression = expression, value = value, frameId = frameId, format = format),
        SetExpressionArguments.serializer(),
        SetExpressionResponseBody.serializer(),
    )

    /** The `evaluate` request (hover, watch, REPL). */
    suspend fun evaluate(
        expression: String,
        frameId: Int? = null,
        context: String? = null,
        line: Int? = null,
        column: Int? = null,
        source: Source? = null,
    ): EvaluateResponseBody = request(
        DapCommands.Evaluate,
        EvaluateArguments(
            expression = expression,
            frameId = frameId,
            context = context,
            line = line,
            column = column,
            source = source,
        ),
        EvaluateArguments.serializer(),
        EvaluateResponseBody.serializer(),
    )

    /** The `exceptionInfo` request. */
    suspend fun exceptionInfo(threadId: Int): ExceptionInfoResponseBody? = optional(
        DapCommands.ExceptionInfo,
        ExceptionInfoArguments(threadId),
        ExceptionInfoArguments.serializer(),
        ExceptionInfoResponseBody.serializer(),
    )

    /** The `completions` request for the debug console. */
    suspend fun completions(
        text: String,
        column: Int,
        frameId: Int? = null,
        line: Int? = null,
    ): CompletionsResponseBody? = optional(
        DapCommands.Completions,
        CompletionsArguments(text = text, column = column, frameId = frameId, line = line),
        CompletionsArguments.serializer(),
        CompletionsResponseBody.serializer(),
    )

    /** The `loadedSources` request. */
    suspend fun loadedSources(): LoadedSourcesResponseBody? =
        optionalNoArgs(DapCommands.LoadedSources, LoadedSourcesResponseBody.serializer())

    /** The `modules` request. */
    suspend fun modules(startModule: Int? = null, moduleCount: Int? = null): ModulesResponseBody? = optional(
        DapCommands.Modules,
        ModulesArguments(startModule = startModule, moduleCount = moduleCount),
        ModulesArguments.serializer(),
        ModulesResponseBody.serializer(),
    )

    /** The `source` request: source text behind a `sourceReference`. */
    suspend fun source(sourceReference: Int, source: Source? = null): SourceResponseBody? = optional(
        DapCommands.Source,
        SourceArguments(sourceReference = sourceReference, source = source),
        SourceArguments.serializer(),
        SourceResponseBody.serializer(),
    )

    /** The `locations` request: resolve a reference from an event or a response. */
    suspend fun locations(locationReference: Int): LocationsResponseBody? = optional(
        DapCommands.Locations,
        LocationsArguments(locationReference),
        LocationsArguments.serializer(),
        LocationsResponseBody.serializer(),
    )

    /** The `disassemble` request. */
    suspend fun disassemble(
        memoryReference: String,
        instructionCount: Int,
        offset: Int? = null,
        instructionOffset: Int? = null,
        resolveSymbols: Boolean? = null,
    ): DisassembleResponseBody? = optional(
        DapCommands.Disassemble,
        DisassembleArguments(
            memoryReference = memoryReference,
            instructionCount = instructionCount,
            offset = offset,
            instructionOffset = instructionOffset,
            resolveSymbols = resolveSymbols,
        ),
        DisassembleArguments.serializer(),
        DisassembleResponseBody.serializer(),
    )

    /** The `readMemory` request; the result's `data` is base64-encoded. */
    suspend fun readMemory(memoryReference: String, count: Int, offset: Int? = null): ReadMemoryResponseBody? = optional(
        DapCommands.ReadMemory,
        ReadMemoryArguments(memoryReference = memoryReference, count = count, offset = offset),
        ReadMemoryArguments.serializer(),
        ReadMemoryResponseBody.serializer(),
    )

    /** The `writeMemory` request; [data] is base64-encoded. */
    suspend fun writeMemory(
        memoryReference: String,
        data: String,
        offset: Int? = null,
        allowPartial: Boolean? = null,
    ): WriteMemoryResponseBody? = optional(
        DapCommands.WriteMemory,
        WriteMemoryArguments(memoryReference = memoryReference, data = data, offset = offset, allowPartial = allowPartial),
        WriteMemoryArguments.serializer(),
        WriteMemoryResponseBody.serializer(),
    )

    // ==================== Events and adapter requests ====================

    /**
     * Registers a handler for an adapter event (e.g. `stopped`, `output`,
     * `terminated`), decoded as [T]. Pass a null [bodySerializer] for events
     * without a body.
     *
     * Handlers run on the thread calling [listen] — marshal to the UI thread
     * before touching UI state.
     */
    fun <T> onEvent(event: String, bodySerializer: KSerializer<T>?, handler: (T?) -> Unit) {
        if (bodySerializer == null) {
            launcher.onEvent(event) { handler(null) }
        } else {
            launcher.onEvent(event) { body ->
                handler(body?.let { JsonRpcJson.json.decodeFromJsonElement(bodySerializer, it) })
            }
        }
    }

    /** Registers a handler for an event with no body (e.g. `terminated`). */
    fun onEvent(event: String, handler: () -> Unit) {
        launcher.onEvent(event) { handler() }
    }

    /**
     * Registers a handler for [event] that always fires, receiving the raw body
     * (`null` when the adapter sent none).
     *
     * The typed overloads skip an event whose body is missing, which is right
     * when a body is expected but wrong for events like `terminated` that carry
     * one only sometimes — use this when one handler covers several events, or
     * when the body is optional.
     */
    fun onEventRaw(event: String, handler: (JsonElement?) -> Unit) {
        launcher.onEvent(event, handler)
    }

    /**
     * Registers a handler for a request the adapter sends to the client — the
     * reverse direction, e.g. `runInTerminal` or `startDebugging`. A null
     * return answers the request without a body.
     */
    fun onRequest(command: String, handler: (JsonElement?) -> JsonElement?) {
        launcher.onRequest(command, handler)
    }

    /** Run the receive loop until the transport closes. Blocks the caller. */
    fun listen() = launcher.listen()

    // ==================== Internal ====================

    /** Sends [command] and decodes a body the spec requires. */
    private suspend fun <T, R : Any> request(
        command: String,
        args: T?,
        paramsSerializer: KSerializer<T>,
        resultSerializer: KSerializer<R>,
    ): R {
        val body = launcher.request(command, args, paramsSerializer)
            ?: throw DapException(command, "the adapter sent no body")
        return JsonRpcJson.json.decodeFromJsonElement(resultSerializer, body)
    }

    /** Sends [command] with arguments and returns the raw body. */
    private suspend fun <T> optional(command: String, args: T?, paramsSerializer: KSerializer<T>): JsonElement =
        launcher.request(command, args, paramsSerializer) ?: JsonNull

    /** Sends [command] and decodes a body the adapter may omit. */
    private suspend fun <T, R : Any> optional(
        command: String,
        args: T?,
        paramsSerializer: KSerializer<T>,
        resultSerializer: KSerializer<R>,
    ): R? = launcher.request(command, args, paramsSerializer)
        ?.let { JsonRpcJson.json.decodeFromJsonElement(resultSerializer, it) }

    /** Sends [command] without arguments and decodes a body the spec requires. */
    private suspend fun <R : Any> requestNoArgs(command: String, resultSerializer: KSerializer<R>): R {
        val body = launcher.request(command) ?: throw DapException(command, "the adapter sent no body")
        return JsonRpcJson.json.decodeFromJsonElement(resultSerializer, body)
    }

    /** Sends [command] without arguments and decodes a body the adapter may omit. */
    private suspend fun <R : Any> optionalNoArgs(command: String, resultSerializer: KSerializer<R>): R? =
        launcher.request(command)?.let { JsonRpcJson.json.decodeFromJsonElement(resultSerializer, it) }

    /** Sends [command] with a raw payload and returns the raw body. */
    private suspend fun raw(command: String, args: JsonElement? = null): JsonElement =
        launcher.request(command, args) ?: JsonNull
}
