package cn.enaium.lsp.jsonrpc

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.longOrNull

/**
 * A JSON-RPC 2.0 message as defined by https://www.jsonrpc.org/specification.
 *
 * One of [request], [response] or [notification] is set; the others are null.
 */
@Serializable
data class JsonRpcMessage(
    val jsonrpc: String = "2.0",
    val id: JsonRpcId? = null,
    val method: String? = null,
    val params: JsonElement? = null,
    val result: JsonElement? = null,
    val error: JsonRpcError? = null,
)

/**
 * Request id. The spec allows either a number or a string, so this serializes
 * as a bare JSON number or string (never an object).
 */
@Serializable(with = JsonRpcIdSerializer::class)
data class JsonRpcId(val value: Long? = null, val string: String? = null) {
    fun isNull(): Boolean = value == null && string == null
}

/** Serializes a [JsonRpcId] as a bare JSON number or string per the spec. */
object JsonRpcIdSerializer : KSerializer<JsonRpcId> {
    override val descriptor: SerialDescriptor = kotlinx.serialization.descriptors.PrimitiveSerialDescriptor("JsonRpcId", kotlinx.serialization.descriptors.PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: JsonRpcId) {
        when {
            value.value != null -> encoder.encodeLong(value.value)
            value.string != null -> encoder.encodeString(value.string)
            else -> encoder.encodeNull()
        }
    }

    override fun deserialize(decoder: Decoder): JsonRpcId {
        val element = decoder.decodeSerializableValue(kotlinx.serialization.json.JsonElement.serializer())
        val primitive = element as? kotlinx.serialization.json.JsonPrimitive ?: return JsonRpcId()
        if (primitive.isString) return JsonRpcId(string = primitive.content)
        return primitive.longOrNull?.let { JsonRpcId(value = it) } ?: JsonRpcId(string = primitive.content)
    }
}

@Serializable
data class JsonRpcError(
    val code: Int,
    val message: String,
    val data: JsonElement? = null,
)
/** JSON-RPC error codes mandated by the specification. */
object JsonRpcErrorCodes {
    const val PARSE_ERROR = -32700
    const val INVALID_REQUEST = -32600
    const val METHOD_NOT_FOUND = -32601
    const val INVALID_PARAMS = -32602
    const val INTERNAL_ERROR = -32603
}

/** JSON-RPC request/response kinds. */
enum class MessageType {
    REQUEST,
    RESPONSE,
    NOTIFICATION,
}
