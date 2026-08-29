package cn.enaium.lsp.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement

/**
 * `Either<TextEdit, SnippetTextEdit>` — an entry of `TextDocumentEdit.edits`.
 * A snippet edit is distinguished by the presence of a `snippet` property.
 */
@Serializable(with = TextEditOrSnippetSerializer::class)
sealed interface TextEditOrSnippet {
    data class Edit(val value: TextEdit) : TextEditOrSnippet

    data class Snippet(val value: SnippetTextEdit) : TextEditOrSnippet
}

object TextEditOrSnippetSerializer : KSerializer<TextEditOrSnippet> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("TextEditOrSnippet")

    override fun serialize(encoder: Encoder, value: TextEditOrSnippet) {
        val element = when (value) {
            is TextEditOrSnippet.Edit ->
                LspJson.json.encodeToJsonElement(TextEdit.serializer(), value.value)
            is TextEditOrSnippet.Snippet ->
                LspJson.json.encodeToJsonElement(SnippetTextEdit.serializer(), value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): TextEditOrSnippet {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        val obj = element as? kotlinx.serialization.json.JsonObject
        return if (obj != null && obj.containsKey("snippet")) {
            TextEditOrSnippet.Snippet(LspJson.json.decodeFromJsonElement(SnippetTextEdit.serializer(), element))
        } else {
            TextEditOrSnippet.Edit(LspJson.json.decodeFromJsonElement(TextEdit.serializer(), element))
        }
    }
}

/**
 * `Either<TextDocumentEdit, ResourceOperation>` — an entry of
 * `WorkspaceEdit.documentChanges`. Resource operations (create/rename/delete
 * file) are distinguished by their `kind` property.
 */
@Serializable(with = DocumentChangeSerializer::class)
sealed interface DocumentChange {
    data class Edit(val value: TextDocumentEdit) : DocumentChange

    data class Operation(val value: ResourceOperation) : DocumentChange
}

object DocumentChangeSerializer : KSerializer<DocumentChange> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("DocumentChange")

    override fun serialize(encoder: Encoder, value: DocumentChange) {
        val element = when (value) {
            is DocumentChange.Edit ->
                LspJson.json.encodeToJsonElement(TextDocumentEdit.serializer(), value.value)
            is DocumentChange.Operation ->
                LspJson.json.encodeToJsonElement(ResourceOperation.serializer(), value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): DocumentChange {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        val obj = element as? kotlinx.serialization.json.JsonObject
        return if (obj != null && obj.containsKey("kind")) {
            DocumentChange.Operation(LspJson.json.decodeFromJsonElement(ResourceOperation.serializer(), element))
        } else {
            DocumentChange.Edit(LspJson.json.decodeFromJsonElement(TextDocumentEdit.serializer(), element))
        }
    }
}

/**
 * A resource operation — `create`/`rename`/`delete` file operations.
 * The concrete class is selected by the `kind` property on the wire.
 */
@Serializable(with = ResourceOperationSerializer::class)
sealed interface ResourceOperation {
    data class Create(val value: CreateFile) : ResourceOperation

    data class Rename(val value: RenameFile) : ResourceOperation

    data class Delete(val value: DeleteFile) : ResourceOperation
}

object ResourceOperationSerializer : KSerializer<ResourceOperation> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ResourceOperation")

    override fun serialize(encoder: Encoder, value: ResourceOperation) {
        val element = when (value) {
            is ResourceOperation.Create ->
                LspJson.json.encodeToJsonElement(CreateFile.serializer(), value.value)
            is ResourceOperation.Rename ->
                LspJson.json.encodeToJsonElement(RenameFile.serializer(), value.value)
            is ResourceOperation.Delete ->
                LspJson.json.encodeToJsonElement(DeleteFile.serializer(), value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): ResourceOperation {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        val obj = element as? kotlinx.serialization.json.JsonObject
        val kind = obj?.get("kind")?.toString() ?: ""
        return when (kind) {
            "\"create\"" -> ResourceOperation.Create(
                LspJson.json.decodeFromJsonElement(CreateFile.serializer(), element)
            )
            "\"rename\"" -> ResourceOperation.Rename(
                LspJson.json.decodeFromJsonElement(RenameFile.serializer(), element)
            )
            else -> ResourceOperation.Delete(
                LspJson.json.decodeFromJsonElement(DeleteFile.serializer(), element)
            )
        }
    }
}