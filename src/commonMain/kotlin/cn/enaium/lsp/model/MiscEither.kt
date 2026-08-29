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
 * `Either<String, List<InlayHintLabelPart>>` — an inlay hint `label` that is
 * either a plain string or a structured list of label parts.
 */
@Serializable(with = InlayHintLabelSerializer::class)
sealed interface InlayHintLabel {
    @JvmInline
    value class StringValue(val value: String) : InlayHintLabel

    data class Parts(val value: List<InlayHintLabelPart>) : InlayHintLabel
}

object InlayHintLabelSerializer : KSerializer<InlayHintLabel> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("InlayHintLabel")

    override fun serialize(encoder: Encoder, value: InlayHintLabel) {
        val element = when (value) {
            is InlayHintLabel.StringValue -> JsonPrimitive(value.value)
            is InlayHintLabel.Parts ->
                LspJson.json.encodeToJsonElement(
                    kotlinx.serialization.builtins.ListSerializer(InlayHintLabelPart.serializer()),
                    value.value,
                )
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): InlayHintLabel {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        return if (element is JsonPrimitive) {
            InlayHintLabel.StringValue(element.content)
        } else {
            InlayHintLabel.Parts(
                LspJson.json.decodeFromJsonElement(
                    kotlinx.serialization.builtins.ListSerializer(InlayHintLabelPart.serializer()),
                    element,
                )
            )
        }
    }
}

/**
 * `Either<String, NotebookDocumentFilter>` — a filter that matches either a
 * notebook type string or a structured filter.
 */
@Serializable(with = NotebookFilterEitherSerializer::class)
sealed interface NotebookFilterEither {
    @JvmInline
    value class Type(val value: String) : NotebookFilterEither

    data class Filter(val value: NotebookDocumentFilter) : NotebookFilterEither
}

object NotebookFilterEitherSerializer : KSerializer<NotebookFilterEither> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("NotebookFilterEither")

    override fun serialize(encoder: Encoder, value: NotebookFilterEither) {
        val element = when (value) {
            is NotebookFilterEither.Type -> JsonPrimitive(value.value)
            is NotebookFilterEither.Filter ->
                LspJson.json.encodeToJsonElement(NotebookDocumentFilter.serializer(), value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): NotebookFilterEither {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        return if (element is JsonPrimitive) {
            NotebookFilterEither.Type(element.content)
        } else {
            NotebookFilterEither.Filter(
                LspJson.json.decodeFromJsonElement(NotebookDocumentFilter.serializer(), element)
            )
        }
    }
}

/**
 * `Either<String, StringValue>` — an inline completion item `insertText` that
 * is either a plain string or a `{ value: string }` object.
 */
@Serializable(with = InlineCompletionInsertTextSerializer::class)
sealed interface InlineCompletionInsertText {
    @JvmInline
    value class Plain(val value: String) : InlineCompletionInsertText

    data class Structured(val value: StringValue) : InlineCompletionInsertText
}

object InlineCompletionInsertTextSerializer : KSerializer<InlineCompletionInsertText> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("InlineCompletionInsertText")

    override fun serialize(encoder: Encoder, value: InlineCompletionInsertText) {
        val element = when (value) {
            is InlineCompletionInsertText.Plain -> JsonPrimitive(value.value)
            is InlineCompletionInsertText.Structured ->
                LspJson.json.encodeToJsonElement(StringValue.serializer(), value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): InlineCompletionInsertText {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        return if (element is JsonPrimitive) {
            InlineCompletionInsertText.Plain(element.content)
        } else {
            InlineCompletionInsertText.Structured(
                LspJson.json.decodeFromJsonElement(StringValue.serializer(), element)
            )
        }
    }
}

/**
 * `Either<FullDocumentDiagnosticReport, UnchangedDocumentDiagnosticReport>` —
 * a document diagnostic report. The unchanged variant has an `unchanged`
 * boolean or `resultId` without `items`/`kind` marking.
 */
@Serializable(with = DocumentDiagnosticEitherSerializer::class)
sealed interface DocumentDiagnosticEither {
    data class Full(val value: FullDocumentDiagnosticReport) : DocumentDiagnosticEither

    data class Unchanged(val value: UnchangedDocumentDiagnosticReport) : DocumentDiagnosticEither
}

object DocumentDiagnosticEitherSerializer : KSerializer<DocumentDiagnosticEither> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("DocumentDiagnosticEither")

    override fun serialize(encoder: Encoder, value: DocumentDiagnosticEither) {
        val element = when (value) {
            is DocumentDiagnosticEither.Full ->
                LspJson.json.encodeToJsonElement(FullDocumentDiagnosticReport.serializer(), value.value)
            is DocumentDiagnosticEither.Unchanged ->
                LspJson.json.encodeToJsonElement(UnchangedDocumentDiagnosticReport.serializer(), value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): DocumentDiagnosticEither {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        val obj = element as? kotlinx.serialization.json.JsonObject
        return if (obj != null && obj.containsKey("items")) {
            DocumentDiagnosticEither.Full(
                LspJson.json.decodeFromJsonElement(FullDocumentDiagnosticReport.serializer(), element)
            )
        } else {
            DocumentDiagnosticEither.Unchanged(
                LspJson.json.decodeFromJsonElement(UnchangedDocumentDiagnosticReport.serializer(), element)
            )
        }
    }
}

/**
 * `Either<WorkspaceFullDocumentDiagnosticReport, WorkspaceUnchangedDocumentDiagnosticReport>` —
 * an entry of a workspace diagnostic report.
 */
@Serializable(with = WorkspaceDocumentDiagnosticEitherSerializer::class)
sealed interface WorkspaceDocumentDiagnosticEither {
    data class Full(val value: WorkspaceFullDocumentDiagnosticReport) : WorkspaceDocumentDiagnosticEither

    data class Unchanged(val value: WorkspaceUnchangedDocumentDiagnosticReport) : WorkspaceDocumentDiagnosticEither
}

object WorkspaceDocumentDiagnosticEitherSerializer : KSerializer<WorkspaceDocumentDiagnosticEither> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("WorkspaceDocumentDiagnosticEither")

    override fun serialize(encoder: Encoder, value: WorkspaceDocumentDiagnosticEither) {
        val element = when (value) {
            is WorkspaceDocumentDiagnosticEither.Full ->
                LspJson.json.encodeToJsonElement(WorkspaceFullDocumentDiagnosticReport.serializer(), value.value)
            is WorkspaceDocumentDiagnosticEither.Unchanged ->
                LspJson.json.encodeToJsonElement(WorkspaceUnchangedDocumentDiagnosticReport.serializer(), value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): WorkspaceDocumentDiagnosticEither {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        val obj = element as? kotlinx.serialization.json.JsonObject
        return if (obj != null && obj.containsKey("items")) {
            WorkspaceDocumentDiagnosticEither.Full(
                LspJson.json.decodeFromJsonElement(WorkspaceFullDocumentDiagnosticReport.serializer(), element)
            )
        } else {
            WorkspaceDocumentDiagnosticEither.Unchanged(
                LspJson.json.decodeFromJsonElement(WorkspaceUnchangedDocumentDiagnosticReport.serializer(), element)
            )
        }
    }
}
/**
 * `Either3<InlineValueText, InlineValueVariableLookup, InlineValueEvaluatableExpression>` —
 * an inline value. Distinguished by which fields are present.
 */
@Serializable(with = InlineValueSerializerKt::class)
sealed interface InlineValue {
    data class Text(val value: InlineValueText) : InlineValue

    data class VariableLookup(val value: InlineValueVariableLookup) : InlineValue

    data class EvaluatableExpression(val value: InlineValueEvaluatableExpression) : InlineValue
}

object InlineValueSerializerKt : KSerializer<InlineValue> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("InlineValue")

    override fun serialize(encoder: Encoder, value: InlineValue) {
        val element = when (value) {
            is InlineValue.Text ->
                LspJson.json.encodeToJsonElement(InlineValueText.serializer(), value.value)
            is InlineValue.VariableLookup ->
                LspJson.json.encodeToJsonElement(InlineValueVariableLookup.serializer(), value.value)
            is InlineValue.EvaluatableExpression ->
                LspJson.json.encodeToJsonElement(InlineValueEvaluatableExpression.serializer(), value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): InlineValue {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        val obj = element as? kotlinx.serialization.json.JsonObject
        return when {
            obj != null && obj.containsKey("text") -> InlineValue.Text(
                LspJson.json.decodeFromJsonElement(InlineValueText.serializer(), element)
            )
            obj != null && obj.containsKey("caseSensitiveLookup") -> InlineValue.VariableLookup(
                LspJson.json.decodeFromJsonElement(InlineValueVariableLookup.serializer(), element)
            )
            else -> InlineValue.EvaluatableExpression(
                LspJson.json.decodeFromJsonElement(InlineValueEvaluatableExpression.serializer(), element)
            )
        }
    }
}
