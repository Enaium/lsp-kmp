package cn.enaium.lsp.jsonrpc

import kotlinx.serialization.json.Json

/** Shared JSON configuration for LSP/DAP messages. */
object JsonRpcJson {
    val json: Json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        encodeDefaults = true
    }
}