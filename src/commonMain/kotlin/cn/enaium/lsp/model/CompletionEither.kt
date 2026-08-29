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
 * `Either<String, MarkupContent>` — a completion item / signature
 * documentation that is either plain text or a structured markup content.
 */
@Serializable(with = DocumentationSerializer::class)
sealed interface Documentation {
    @JvmInline
    value class StringValue(val value: String) : Documentation

    data class Markup(val value: MarkupContent) : Documentation
}

object DocumentationSerializer : KSerializer<Documentation> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Documentation")

    override fun serialize(encoder: Encoder, value: Documentation) {
        val element = when (value) {
            is Documentation.StringValue -> JsonPrimitive(value.value)
            is Documentation.Markup ->
                LspJson.json.encodeToJsonElement(MarkupContent.serializer(), value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): Documentation {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        return if (element is JsonPrimitive) {
            Documentation.StringValue(element.content)
        } else {
            Documentation.Markup(LspJson.json.decodeFromJsonElement(MarkupContent.serializer(), element))
        }
    }
}

/**
 * `Either<TextEdit, InsertReplaceEdit>` — a completion item `textEdit`.
 */
@Serializable(with = TextEditOrInsertSerializer::class)
sealed interface TextEditOrInsert {
    data class Edit(val value: TextEdit) : TextEditOrInsert

    data class InsertReplace(val value: InsertReplaceEdit) : TextEditOrInsert
}

object TextEditOrInsertSerializer : KSerializer<TextEditOrInsert> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("TextEditOrInsert")

    override fun serialize(encoder: Encoder, value: TextEditOrInsert) {
        val element = when (value) {
            is TextEditOrInsert.Edit ->
                LspJson.json.encodeToJsonElement(TextEdit.serializer(), value.value)
            is TextEditOrInsert.InsertReplace ->
                LspJson.json.encodeToJsonElement(InsertReplaceEdit.serializer(), value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): TextEditOrInsert {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        val obj = element as? kotlinx.serialization.json.JsonObject
        return if (obj != null && obj.containsKey("insert")) {
            TextEditOrInsert.InsertReplace(
                LspJson.json.decodeFromJsonElement(InsertReplaceEdit.serializer(), element)
            )
        } else {
            TextEditOrInsert.Edit(LspJson.json.decodeFromJsonElement(TextEdit.serializer(), element))
        }
    }
}

/**
 * `Either<Range, InsertReplaceRange>` — a completion list `itemDefaults.editRange`.
 */
@Serializable(with = EditRangeSerializer::class)
sealed interface EditRange {
    data class RangeValue(val value: Range) : EditRange

    data class InsertReplace(val value: InsertReplaceRange) : EditRange
}

object EditRangeSerializer : KSerializer<EditRange> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("EditRange")

    override fun serialize(encoder: Encoder, value: EditRange) {
        val element = when (value) {
            is EditRange.RangeValue ->
                LspJson.json.encodeToJsonElement(Range.serializer(), value.value)
            is EditRange.InsertReplace ->
                LspJson.json.encodeToJsonElement(InsertReplaceRange.serializer(), value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): EditRange {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        val obj = element as? kotlinx.serialization.json.JsonObject
        return if (obj != null && (obj.containsKey("insert") || obj.containsKey("replace"))) {
            EditRange.InsertReplace(
                LspJson.json.decodeFromJsonElement(InsertReplaceRange.serializer(), element)
            )
        } else {
            EditRange.RangeValue(LspJson.json.decodeFromJsonElement(Range.serializer(), element))
        }
    }
}