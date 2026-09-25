package cn.enaium.lsp.examples.stdio

import cn.enaium.lsp.LanguageServerLauncher
import cn.enaium.lsp.jsonrpc.StreamMessageTransport

fun main() {
    val transport = StreamMessageTransport(System.`in`, System.out)
    val launcher = LanguageServerLauncher(transport, ExampleServerLanguage())
    launcher.listen()
}
