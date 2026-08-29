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

/**
 * `Either<Boolean, Object>` — a semantic tokens `range` capability that is
 * either `true`/`false` or a free-form options object.
 */
@Serializable(with = BooleanOrRawSerializer::class)
sealed interface BooleanOrRaw {
    @JvmInline
    value class Enabled(val value: Boolean) : BooleanOrRaw

    data class Value(val value: JsonElement) : BooleanOrRaw
}

object BooleanOrRawSerializer : KSerializer<BooleanOrRaw> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("BooleanOrRaw")

    override fun serialize(encoder: Encoder, value: BooleanOrRaw) {
        val element = when (value) {
            is BooleanOrRaw.Enabled -> JsonPrimitive(value.value)
            is BooleanOrRaw.Value -> value.value
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): BooleanOrRaw {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        val bool = (element as? JsonPrimitive)?.booleanOrNull
        return if (bool != null) {
            BooleanOrRaw.Enabled(bool)
        } else {
            BooleanOrRaw.Value(element)
        }
    }
}

/**
 * `Either<Boolean, X>` — a semantic tokens `full` capability that is either
 * `true`/`false` or an options object with a `delta` flag.
 */
@Serializable(with = BooleanOrDeltaSerializer::class)
sealed interface BooleanOrDelta {
    @JvmInline
    value class Enabled(val value: Boolean) : BooleanOrDelta

    data class Delta(val value: Boolean) : BooleanOrDelta
}

object BooleanOrDeltaSerializer : KSerializer<BooleanOrDelta> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("BooleanOrDelta")

    override fun serialize(encoder: Encoder, value: BooleanOrDelta) {
        val element = when (value) {
            is BooleanOrDelta.Enabled -> JsonPrimitive(value.value)
            is BooleanOrDelta.Delta -> JsonPrimitive(value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): BooleanOrDelta {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        return if (element is JsonPrimitive) {
            BooleanOrDelta.Enabled(element.booleanOrNull ?: false)
        } else {
            BooleanOrDelta.Delta(
                (element as? kotlinx.serialization.json.JsonObject)
                    ?.get("delta")?.toString()?.toBooleanStrictOrNull() ?: false
            )
        }
    }
}

/**
 * `Either<Boolean, DiagnosticsTagSupport>` — a `publishDiagnostics` capability
 * that is either `true`/`false` or a tag support object.
 */
@Serializable(with = BooleanOrTagSupportSerializer::class)
sealed interface BooleanOrTagSupport {
    @JvmInline
    value class Enabled(val value: Boolean) : BooleanOrTagSupport

    data class Support(val value: DiagnosticsTagSupport) : BooleanOrTagSupport
}

object BooleanOrTagSupportSerializer : KSerializer<BooleanOrTagSupport> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("BooleanOrTagSupport")

    override fun serialize(encoder: Encoder, value: BooleanOrTagSupport) {
        val element = when (value) {
            is BooleanOrTagSupport.Enabled -> JsonPrimitive(value.value)
            is BooleanOrTagSupport.Support ->
                LspJson.json.encodeToJsonElement(DiagnosticsTagSupport.serializer(), value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): BooleanOrTagSupport {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        return if (element is JsonPrimitive) {
            BooleanOrTagSupport.Enabled(element.booleanOrNull ?: false)
        } else {
            BooleanOrTagSupport.Support(
                LspJson.json.decodeFromJsonElement(DiagnosticsTagSupport.serializer(), element)
            )
        }
    }
}
/**
 * `Either<Location, WorkspaceSymbolLocation>` — a workspace symbol `location`
 * that is either a plain location or a workspace-symbol-specific location.
 * A workspace symbol location has `uri` but no `range`.
 */
@Serializable(with = SymbolLocationSerializer::class)
sealed interface SymbolLocation {
    data class LocationValue(val value: Location) : SymbolLocation

    data class Workspace(val value: WorkspaceSymbolLocation) : SymbolLocation
}

object SymbolLocationSerializer : KSerializer<SymbolLocation> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("SymbolLocation")

    override fun serialize(encoder: Encoder, value: SymbolLocation) {
        val element = when (value) {
            is SymbolLocation.LocationValue ->
                LspJson.json.encodeToJsonElement(Location.serializer(), value.value)
            is SymbolLocation.Workspace ->
                LspJson.json.encodeToJsonElement(WorkspaceSymbolLocation.serializer(), value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): SymbolLocation {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        val obj = element as? kotlinx.serialization.json.JsonObject
        return if (obj != null && obj.containsKey("range")) {
            SymbolLocation.LocationValue(LspJson.json.decodeFromJsonElement(Location.serializer(), element))
        } else {
            SymbolLocation.Workspace(
                LspJson.json.decodeFromJsonElement(WorkspaceSymbolLocation.serializer(), element)
            )
        }
    }
}
