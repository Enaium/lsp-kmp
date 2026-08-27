# lsp-kmp

A Kotlin Multiplatform implementation of the [Language Server Protocol](https://microsoft.github.io/language-server-protocol/) (LSP) and the [Debug Adapter Protocol](https://microsoft.github.io/debug-adapter-protocol/) (DAP), modeled after [eclipse-lsp4j/lsp4j](https://github.com/eclipse-lsp4j/lsp4j).

## Features

- **JSON-RPC 2.0 core** — message models, request/response correlation, `Content-Length` framed transport (shared by LSP and DAP)
- **LSP model parity with lsp4j** — all 365 model classes from lsp4j's `Protocol.xtend`, including client/server capabilities, completion, hover, diagnostics, semantic tokens, inlay hints, inline values, notebook documents, and pull diagnostics (LSP 3.18)
- **LSP services** — `LanguageServer`, `TextDocumentService`, `WorkspaceService`, `LanguageClient` interfaces mirroring lsp4j, wired to the wire by `LanguageServerLauncher`
- **DAP** — debug adapter models (`initialize`, `setBreakpoints`, `threads`, `stackTrace`, `scopes`, `variables`, `evaluate`, …), `DebugAdapter` interface, and `DebugAdapterLauncher`
- **Multiplatform** — `jvm`, `macosArm64`, `iosArm64`, `iosSimulatorArm64`
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
│   ├── model        # DAP data models
│   ├── DebugAdapter
│   └── DebugAdapterLauncher
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

### Custom transports

`MessageTransport` is a two-method interface (`send` / `receive`); `StreamMessageTransport` implements the standard `Content-Length` framing over any `InputStream`/`OutputStream` — including sockets, pipes, or WebSocket byte streams. Use the socket example as a reference for a TCP server.

## Examples

Build and run:

```bash
# stdio language server (LSP client integration, e.g. VS Code language client)
./gradlew :examples:stdio:run

# socket language server on port 8080 (pass a port as the first argument)
./gradlew :examples:socket:run
```

Both examples accept the standard LSP handshake (`initialize` → requests → `shutdown` → `exit`).

## Testing

```bash
./gradlew :jvmTest        # JVM tests
./gradlew :macosArm64Test # native (macOS arm64) tests
```

The test suite covers JSON-RPC dispatch/correlation, typed launcher round-trips, stream framing, end-to-end pipe communication, and LSP lifecycle wiring.

## License

[MIT](LICENSE)
