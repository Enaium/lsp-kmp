package cn.enaium.lsp.model
import kotlin.jvm.JvmInline

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject

/**
 * JSON configuration shared by all LSP message (de)serialization.
 */
object LspJson {
    val json: kotlinx.serialization.json.Json = kotlinx.serialization.json.Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        encodeDefaults = true
    }
}

/**
 * A `workDoneToken` / `partialResultToken` in request params.
 * Either a string or an integer number on the wire.
 */
@Serializable(with = TokenSerializer::class)
sealed interface Token {
    @JvmInline
    value class StringValue(val value: String) : Token

    @JvmInline
    value class NumberValue(val value: Long) : Token
}

object TokenSerializer : KSerializer<Token> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Token")

    override fun serialize(encoder: Encoder, value: Token) {
        val element = when (value) {
            is Token.StringValue -> JsonPrimitive(value.value)
            is Token.NumberValue -> JsonPrimitive(value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): Token {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        return when (element) {
            is JsonPrimitive ->
                if (element.isString) Token.StringValue(element.content)
                else Token.NumberValue(element.content.toLongOrNull() ?: 0L)
            else -> Token.StringValue(element.toString())
        }
    }
}

/**
 * `textDocumentSync` server capability — either a sync kind number
 * (`TextDocumentSyncKind`) or a [TextDocumentSyncOptions] object.
 */
@Serializable(with = TextDocumentSyncSerializer::class)
sealed interface TextDocumentSync {
    @JvmInline
    value class Kind(val value: Int) : TextDocumentSync

    data class Options(val value: TextDocumentSyncOptions) : TextDocumentSync
}

object TextDocumentSyncSerializer : KSerializer<TextDocumentSync> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("TextDocumentSync")

    override fun serialize(encoder: Encoder, value: TextDocumentSync) {
        val element = when (value) {
            is TextDocumentSync.Kind -> JsonPrimitive(value.value)
            is TextDocumentSync.Options ->
                LspJson.json.encodeToJsonElement(TextDocumentSyncOptions.serializer(), value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): TextDocumentSync {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        return if (element is JsonPrimitive) {
            TextDocumentSync.Kind(element.content.toIntOrNull() ?: 0)
        } else {
            TextDocumentSync.Options(LspJson.json.decodeFromJsonElement(TextDocumentSyncOptions.serializer(), element))
        }
    }
}
