package cn.enaium.lsp.model
import kotlin.jvm.JvmInline

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
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
 * `Either<String, Tuple.Two<Integer, Integer>>` — a parameter information
 * `label` that is either a plain string or a `[start, end]` offset tuple.
 */
@Serializable(with = ParameterLabelSerializer::class)
sealed interface ParameterLabel {
    @JvmInline
    value class StringValue(val value: String) : ParameterLabel

    data class Offsets(val value: Pair<Int, Int>) : ParameterLabel
}

object ParameterLabelSerializer : KSerializer<ParameterLabel> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ParameterLabel")

    override fun serialize(encoder: Encoder, value: ParameterLabel) {
        val element = when (value) {
            is ParameterLabel.StringValue -> JsonPrimitive(value.value)
            is ParameterLabel.Offsets ->
                LspJson.json.encodeToJsonElement(
                    kotlinx.serialization.builtins.ListSerializer(Int.serializer()),
                    listOf(value.value.first, value.value.second),
                )
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): ParameterLabel {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        return if (element is JsonPrimitive) {
            ParameterLabel.StringValue(element.content)
        } else {
            val items = LspJson.json.decodeFromJsonElement(
                kotlinx.serialization.builtins.ListSerializer(Int.serializer()),
                element,
            )
            ParameterLabel.Offsets(items[0] to items[1])
        }
    }
}

/**
 * The value of a `$/progress` notification — either a structured
 * work-done-progress notification (`begin`/`report`/`end`) or an arbitrary
 * object payload.
 */
@Serializable(with = ProgressValueSerializer::class)
sealed interface ProgressValue {
    data class WorkDone(val value: WorkDoneProgressNotificationValue) : ProgressValue

    data class Raw(val value: LSPAny) : ProgressValue
}

/**
 * A work-done-progress notification, one of begin/report/end.
 */
@Serializable(with = WorkDoneProgressNotificationSerializer::class)
sealed interface WorkDoneProgressNotificationValue {
    data class Begin(val value: WorkDoneProgressBegin) : WorkDoneProgressNotificationValue

    data class Report(val value: WorkDoneProgressReport) : WorkDoneProgressNotificationValue

    data class End(val value: WorkDoneProgressEnd) : WorkDoneProgressNotificationValue
}

object WorkDoneProgressNotificationSerializer : KSerializer<WorkDoneProgressNotificationValue> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("WorkDoneProgressNotificationValue")

    override fun serialize(encoder: Encoder, value: WorkDoneProgressNotificationValue) {
        val element = when (value) {
            is WorkDoneProgressNotificationValue.Begin ->
                LspJson.json.encodeToJsonElement(WorkDoneProgressBegin.serializer(), value.value)
            is WorkDoneProgressNotificationValue.Report ->
                LspJson.json.encodeToJsonElement(WorkDoneProgressReport.serializer(), value.value)
            is WorkDoneProgressNotificationValue.End ->
                LspJson.json.encodeToJsonElement(WorkDoneProgressEnd.serializer(), value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): WorkDoneProgressNotificationValue {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        val obj = element as? kotlinx.serialization.json.JsonObject
        val kind = obj?.get("kind")?.toString()
        return when (kind) {
            "\"begin\"" -> WorkDoneProgressNotificationValue.Begin(
                LspJson.json.decodeFromJsonElement(WorkDoneProgressBegin.serializer(), element)
            )
            "\"report\"" -> WorkDoneProgressNotificationValue.Report(
                LspJson.json.decodeFromJsonElement(WorkDoneProgressReport.serializer(), element)
            )
            else -> WorkDoneProgressNotificationValue.End(
                LspJson.json.decodeFromJsonElement(WorkDoneProgressEnd.serializer(), element)
            )
        }
    }
}

object ProgressValueSerializer : KSerializer<ProgressValue> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ProgressValue")

    override fun serialize(encoder: Encoder, value: ProgressValue) {
        val element = when (value) {
            is ProgressValue.WorkDone ->
                LspJson.json.encodeToJsonElement(WorkDoneProgressNotificationValue.serializer(), value.value)
            is ProgressValue.Raw -> value.value
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): ProgressValue {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        val obj = element as? kotlinx.serialization.json.JsonObject
        return if (obj != null && obj.containsKey("kind")) {
            ProgressValue.WorkDone(
                LspJson.json.decodeFromJsonElement(WorkDoneProgressNotificationValue.serializer(), element)
            )
        } else {
            ProgressValue.Raw(element)
        }
    }
}