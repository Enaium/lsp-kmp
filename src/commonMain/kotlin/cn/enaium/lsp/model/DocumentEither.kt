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
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement

/**
 * `Either<String, Integer>` — a diagnostic `code` that is either a string
 * or a numeric code.
 */
@Serializable(with = DiagnosticCodeSerializer::class)
sealed interface DiagnosticCode {
    @JvmInline
    value class StringValue(val value: String) : DiagnosticCode

    @JvmInline
    value class NumberValue(val value: Long) : DiagnosticCode
}

object DiagnosticCodeSerializer : KSerializer<DiagnosticCode> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("DiagnosticCode")

    override fun serialize(encoder: Encoder, value: DiagnosticCode) {
        val element = when (value) {
            is DiagnosticCode.StringValue -> JsonPrimitive(value.value)
            is DiagnosticCode.NumberValue -> JsonPrimitive(value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): DiagnosticCode {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        val primitive = element as JsonPrimitive
        return if (primitive.isString) DiagnosticCode.StringValue(primitive.content)
        else DiagnosticCode.NumberValue(primitive.content.toLongOrNull() ?: 0L)
    }
}

/**
 * `Either<String, RelativePattern>` — a `globPattern` that is either a plain
 * glob string or a structured relative pattern.
 */
@Serializable(with = GlobPatternSerializer::class)
sealed interface GlobPattern {
    @JvmInline
    value class StringValue(val value: String) : GlobPattern

    data class Relative(val value: RelativePattern) : GlobPattern
}

object GlobPatternSerializer : KSerializer<GlobPattern> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("GlobPattern")

    override fun serialize(encoder: Encoder, value: GlobPattern) {
        val element = when (value) {
            is GlobPattern.StringValue -> JsonPrimitive(value.value)
            is GlobPattern.Relative ->
                LspJson.json.encodeToJsonElement(RelativePattern.serializer(), value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): GlobPattern {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        return if (element is JsonPrimitive) {
            GlobPattern.StringValue(element.content)
        } else {
            GlobPattern.Relative(LspJson.json.decodeFromJsonElement(RelativePattern.serializer(), element))
        }
    }
}

/**
 * `Either<WorkspaceFolder, String>` — a `baseUri` that is either a workspace
 * folder object or a URI string.
 */
@Serializable(with = BaseUriSerializer::class)
sealed interface BaseUri {
    data class Folder(val value: WorkspaceFolder) : BaseUri

    @JvmInline
    value class Uri(val value: String) : BaseUri
}

object BaseUriSerializer : KSerializer<BaseUri> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("BaseUri")

    override fun serialize(encoder: Encoder, value: BaseUri) {
        val element = when (value) {
            is BaseUri.Folder ->
                LspJson.json.encodeToJsonElement(WorkspaceFolder.serializer(), value.value)
            is BaseUri.Uri -> JsonPrimitive(value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): BaseUri {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        return if (element is JsonPrimitive) {
            BaseUri.Uri(element.content)
        } else {
            BaseUri.Folder(LspJson.json.decodeFromJsonElement(WorkspaceFolder.serializer(), element))
        }
    }
}

/**
 * `Either<Boolean, SaveOptions>` — a `textDocumentSync.save` capability that
 * is either `true`/`false` or an options object.
 */
@Serializable(with = SaveSerializer::class)
sealed interface Save {
    @JvmInline
    value class Enabled(val value: Boolean) : Save

    data class Options(val value: SaveOptions) : Save
}

object SaveSerializer : KSerializer<Save> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Save")

    override fun serialize(encoder: Encoder, value: Save) {
        val element = when (value) {
            is Save.Enabled -> JsonPrimitive(value.value)
            is Save.Options -> LspJson.json.encodeToJsonElement(SaveOptions.serializer(), value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): Save {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        return if (element is JsonPrimitive) {
            Save.Enabled(element.content.toBooleanStrictOrNull() ?: false)
        } else {
            Save.Options(LspJson.json.decodeFromJsonElement(SaveOptions.serializer(), element))
        }
    }
}

/**
 * `Either<List<Either<String, MarkedString>>, MarkupContent>` — hover
 * contents that are either a list of marked strings or a markup content.
 */
@Serializable(with = HoverContentsSerializer::class)
sealed interface HoverContents {
    data class MarkedStrings(val value: List<MarkedStringOrString>) : HoverContents

    data class Markup(val value: MarkupContent) : HoverContents
}

object HoverContentsSerializer : KSerializer<HoverContents> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("HoverContents")

    override fun serialize(encoder: Encoder, value: HoverContents) {
        val element = when (value) {
            is HoverContents.MarkedStrings ->
                LspJson.json.encodeToJsonElement(
                    kotlinx.serialization.builtins.ListSerializer(MarkedStringOrString.serializer()),
                    value.value,
                )
            is HoverContents.Markup ->
                LspJson.json.encodeToJsonElement(MarkupContent.serializer(), value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): HoverContents {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        return if (element is kotlinx.serialization.json.JsonArray) {
            HoverContents.MarkedStrings(
                LspJson.json.decodeFromJsonElement(
                    kotlinx.serialization.builtins.ListSerializer(MarkedStringOrString.serializer()),
                    element,
                )
            )
        } else {
            HoverContents.Markup(LspJson.json.decodeFromJsonElement(MarkupContent.serializer(), element))
        }
    }
}

/**
 * `Either<String, MarkedString>` — an element of a hover contents list.
 */
@Serializable(with = MarkedStringOrStringSerializer::class)
sealed interface MarkedStringOrString {
    @JvmInline
    value class StringValue(val value: String) : MarkedStringOrString

    data class Marked(val value: MarkedString) : MarkedStringOrString
}

object MarkedStringOrStringSerializer : KSerializer<MarkedStringOrString> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("MarkedStringOrString")

    override fun serialize(encoder: Encoder, value: MarkedStringOrString) {
        val element = when (value) {
            is MarkedStringOrString.StringValue -> JsonPrimitive(value.value)
            is MarkedStringOrString.Marked ->
                LspJson.json.encodeToJsonElement(MarkedString.serializer(), value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): MarkedStringOrString {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        return if (element is JsonPrimitive) {
            MarkedStringOrString.StringValue(element.content)
        } else {
            MarkedStringOrString.Marked(LspJson.json.decodeFromJsonElement(MarkedString.serializer(), element))
        }
    }
}
/**
 * `Either<String, Boolean>` — a `changeNotifications` workspace capability.
 */
@Serializable(with = StringOrBooleanSerializer::class)
sealed interface StringOrBoolean {
    @JvmInline
    value class StringValue(val value: String) : StringOrBoolean

    @JvmInline
    value class BooleanValue(val value: Boolean) : StringOrBoolean
}

object StringOrBooleanSerializer : KSerializer<StringOrBoolean> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("StringOrBoolean")

    override fun serialize(encoder: Encoder, value: StringOrBoolean) {
        val element = when (value) {
            is StringOrBoolean.StringValue -> JsonPrimitive(value.value)
            is StringOrBoolean.BooleanValue -> JsonPrimitive(value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): StringOrBoolean {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        val primitive = element as JsonPrimitive
        return if (primitive.isString) StringOrBoolean.StringValue(primitive.content)
        else StringOrBoolean.BooleanValue(primitive.content.toBooleanStrictOrNull() ?: false)
    }
}
