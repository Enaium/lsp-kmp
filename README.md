# lsp-kmp

A Kotlin Multiplatform implementation of the [Language Server Protocol](https://microsoft.github.io/language-server-protocol/) (LSP) and the [Debug Adapter Protocol](https://microsoft.github.io/debug-adapter-protocol/) (DAP), modeled after [eclipse-lsp4j/lsp4j](https://github.com/eclipse-lsp4j/lsp4j).

## Features

- **JSON-RPC 2.0 core** — message models, request/response correlation, `Content-Length` framed transport (shared by LSP and DAP)
- **LSP model parity with lsp4j** — all 365 model classes from lsp4j's `Protocol.xtend`, including client/server capabilities, completion, hover, diagnostics, semantic tokens, inlay hints, inline values, notebook documents, and pull diagnostics (LSP 3.18)
- **LSP services** — `LanguageServer`, `TextDocumentService` (text *and* notebook documents), `WorkspaceService`, `LanguageClient` interfaces mirroring lsp4j, wired to the wire by `LanguageServerLauncher`
- **DAP** — both ends of a debug session, speaking DAP's own envelope (`seq`/`type`/`command`, not JSON-RPC 2.0, which is what real adapters such as debugpy validate): the full request/response model (lifecycle, execution control, all four breakpoint kinds, inspection, reverse debugging) with typed event bodies, `DebugAdapter` + `DebugAdapterLauncher` for implementing an adapter, and `DebugClientLauncher` for driving one (`awaitInitialized`, and a non-blocking `send` for `launch`/`attach`, whose responses only arrive once the debuggee runs)
- **Multiplatform** — JVM, Android, and every Kotlin/Native tier-1 target (macOS, iOS, tvOS, watchOS, Linux, Windows); all except JS/Wasm
- **Examples** — a stdio-based language server and a TCP socket-based language server

## Modules

| Path | Description |
| --- | --- |
| `:` (root) | Library: `cn.enaium.lsp` (LSP + JSON-RPC) and `cn.enaium.lsp.dap` (DAP) |
| `:examples:stdio` | Language server over stdin/stdout |
| `:examples:socket` | Language server over a TCP socket |

## Package layout

```
cn.enaium.lsp
├── jsonrpc          # JSON-RPC 2.0 core (messages, server, transport, launcher)
├── model            # LSP data models (lsp4j parity)
├── dap              # Debug Adapter Protocol
│   ├── model        # DAP data models (requests, responses, event bodies)
│   ├── DapLauncher            # the DAP envelope over a transport
│   ├── DebugAdapter           # implement an adapter
│   ├── DebugAdapterLauncher   # serve it over a transport
│   ├── DebugClient            # what an adapter sends events to
│   └── DebugClientLauncher    # drive an adapter (the client end)
├── LanguageServer
├── LanguageServerLauncher
└── LanguageServerServices
```

## Requirements

- JDK 25 (jvmToolchain)
- Gradle 9.7+ (wrapper included)

## Usage

The library is not yet published to a Maven repository. Consume it as a local project dependency or wait for publication.

### Writing a language server

Implement `LanguageServer` (only the methods you support need real bodies; every service method has a default), then run it over a `MessageTransport`:

```kotlin
class MyServer : LanguageServer {
    override fun initialize(params: InitializeParams): InitializeResult =
        InitializeResult(
            capabilities = ServerCapabilities(
                textDocumentSync = JsonPrimitive(TextDocumentSyncKind.Full),
                hoverProvider = JsonPrimitive(true),
            ),
            serverInfo = ServerInfo(name = "my-server", version = "1.0.0"),
        )

    override fun shutdown(): Any? = null
    override fun exit() { Runtime.getRuntime().halt(0) }

    override fun textDocumentService(): TextDocumentService? = MyTextDocumentService()
    override fun workspaceService(): WorkspaceService? = null
    override fun windowService(): WindowService? = null
}

class MyTextDocumentService : TextDocumentService {
    override fun hover(params: HoverParams): Hover? =
        Hover(contents = JsonPrimitive("Hello from ${params.textDocument.uri}"))
}

fun main() {
    // stdio transport: content-length framed messages on stdin/stdout
    val transport = StreamMessageTransport(System.`in`, System.out)
    val launcher = LanguageServerLauncher(transport, MyServer())

    // The launcher reaches back to the editor through `client`: notifications
    // are fire-and-forget, requests suspend until the editor answers.
    //
    //   val answer = launcher.client.showMessageRequest(
    //       ShowMessageRequestParams(type = MessageType.Info, message = "Deploy?")
    //   )
    //   launcher.client.publishDiagnostics(PublishDiagnosticsParams(uri, diagnostics))
    //
    // Suspend requests complete while the transport is being read, so run
    // `listen` concurrently when the server awaits the client.
    launcher.listen()
}
```

### Writing a debug adapter

```kotlin
class MyAdapter : DebugAdapter {
    override fun initialize(request: InitializeRequestArguments): Capabilities =
        Capabilities(supportsConfigurationDoneRequest = true)

    override fun launch(args: JsonElement?): JsonElement? = null
    override fun threads(): ThreadsResponseBody = ThreadsResponseBody(listOf(Thread(1, "main")))
    // ... implement the remaining commands your adapter supports
}

fun main() {
    val transport = StreamMessageTransport(System.`in`, System.out)
    val launcher = DebugAdapterLauncher(transport, MyAdapter())
    launcher.listen()
}
```

### Driving a debug adapter

`DebugClientLauncher` is the client end: it sends the standard requests and
delivers adapter events to handlers you register. Requests suspend until the
adapter answers, so run `listen()` concurrently.

```kotlin
val client = DebugClientLauncher(StreamMessageTransport(process.inputStream, process.outputStream))
scope.launch { client.listen() }

val capabilities = client.initialize(InitializeRequestArguments(adapterID = "kotlin"))
client.onEvent(DapEvent.Stopped, StoppedEventBody.serializer()) { body ->
    body?.threadId?.let { threadId ->
        val frames = client.stackTrace(threadId).stackFrames
        val scopes = client.scopes(frames.first().id).scopes
        val locals = client.variables(scopes.first().variablesReference).variables
        println("stopped at ${frames.first().name}: $locals")
    }
}
client.onEvent(DapEvent.Output, OutputEventBody.serializer()) { print(it?.output) }

client.setBreakpoints(Source(path = "/tmp/demo.kt"), listOf(SourceBreakpoint(line = 12)))
client.launch(JsonElement) // adapter-defined arguments
client.configurationDone()
```

Bodies the spec makes optional come back as `null` when the adapter omits them
(a body-less response to a request the adapter supports is not an error), and
`onRequest` registers handlers for the reverse direction (`runInTerminal`,
`startDebugging`).

### Custom transports

`MessageTransport` is a two-method interface (`send` / `receive`); `StreamMessageTransport` implements the standard `Content-Length` framing over any `InputStream`/`OutputStream` — including sockets, pipes, or WebSocket byte streams. Use the socket example as a reference for a TCP server.

## Examples

Build and run:

```bash
# stdio language server (LSP client integration, e.g. VS Code language client)
./gradlew :examples:stdio:runJvm

# socket language server on port 8080 (pass a port as the first argument)
./gradlew :examples:socket:runJvm
```

Both examples accept the standard LSP handshake (`initialize` → requests → `shutdown` → `exit`).

## Testing

```bash
./gradlew :jvmTest        # JVM tests
./gradlew :macosArm64Test # native (macOS arm64) tests
./gradlew :linuxX64Test   # native (Linux x64) tests
```

Simulator-based tests (iOS/tvOS/watchOS simulators) require the corresponding
simulator runtime installed via Xcode; they are skipped when no runtime is
available. Apple x64 test tasks are disabled (Rosetta not required for
compilation, only for running tests).

Dependencies are declared through a Gradle version catalog at
`gradle/libs.versions.toml`. Android builds need an SDK location; provide it
via `ANDROID_HOME` or a local `local.properties` (`sdk.dir=...`).

The test suite covers JSON-RPC dispatch/correlation, typed launcher round-trips, stream framing, end-to-end pipe communication, LSP lifecycle wiring, and a full DAP session between a debug adapter and a debug client.

## License

[MIT](LICENSE)
