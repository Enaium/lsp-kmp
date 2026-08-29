package cn.enaium.lsp.model
import kotlin.jvm.JvmInline

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement

private val locationListSerializer =
    kotlinx.serialization.builtins.ListSerializer(Location.serializer())
private val locationLinkListSerializer =
    kotlinx.serialization.builtins.ListSerializer(LocationLink.serializer())

/**
 * `Either<List<Location>, List<LocationLink>>` — the result of a
 * go-to-declaration/definition/type-definition/implementation request.
 * A location has a `range`; a location link has `targetUri`.
 */
@Serializable(with = LocationResultSerializer::class)
sealed interface LocationResult {
    data class Locations(val value: List<Location>) : LocationResult

    data class Links(val value: List<LocationLink>) : LocationResult
}

object LocationResultSerializer : KSerializer<LocationResult> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("LocationResult")

    override fun serialize(encoder: Encoder, value: LocationResult) {
        val element = when (value) {
            is LocationResult.Locations ->
                LspJson.json.encodeToJsonElement(locationListSerializer, value.value)
            is LocationResult.Links ->
                LspJson.json.encodeToJsonElement(locationLinkListSerializer, value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): LocationResult {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        val items = element as? kotlinx.serialization.json.JsonArray ?: return LocationResult.Locations(emptyList())
        val anyHasTargetUri = items.any {
            (it as? kotlinx.serialization.json.JsonObject)?.containsKey("targetUri") == true
        }
        return if (anyHasTargetUri) {
            LocationResult.Links(LspJson.json.decodeFromJsonElement(locationLinkListSerializer, element))
        } else {
            LocationResult.Locations(LspJson.json.decodeFromJsonElement(locationListSerializer, element))
        }
    }
}

/**
 * `Either<List<CompletionItem>, CompletionList>` — the result of a completion
 * request. A bare array is a list of items; an object is a completion list.
 */
@Serializable(with = CompletionResultSerializer::class)
sealed interface CompletionResult {
    data class Items(val value: List<CompletionItem>) : CompletionResult

    data class ListValue(val value: CompletionList) : CompletionResult
}

object CompletionResultSerializer : KSerializer<CompletionResult> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("CompletionResult")

    override fun serialize(encoder: Encoder, value: CompletionResult) {
        val element = when (value) {
            is CompletionResult.Items ->
                LspJson.json.encodeToJsonElement(
                    kotlinx.serialization.builtins.ListSerializer(CompletionItem.serializer()),
                    value.value,
                )
            is CompletionResult.ListValue ->
                LspJson.json.encodeToJsonElement(CompletionList.serializer(), value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): CompletionResult {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        return if (element is kotlinx.serialization.json.JsonArray) {
            CompletionResult.Items(
                LspJson.json.decodeFromJsonElement(
                    kotlinx.serialization.builtins.ListSerializer(CompletionItem.serializer()),
                    element,
                )
            )
        } else {
            CompletionResult.ListValue(
                LspJson.json.decodeFromJsonElement(CompletionList.serializer(), element)
            )
        }
    }
}

/**
 * `Either3<Range, PrepareRenameResult, PrepareRenameDefaultBehavior>` — the
 * result of a prepare-rename request. A `Range` has no `placeholder`; behavior
 * is a boolean; otherwise it is a full [PrepareRenameResult].
 */
@Serializable(with = PrepareRenameResultEitherSerializer::class)
sealed interface PrepareRenameResultEither {
    data class RangeValue(val value: Range) : PrepareRenameResultEither

    data class Result(val value: PrepareRenameResult) : PrepareRenameResultEither

    @JvmInline
    value class DefaultBehavior(val value: Boolean) : PrepareRenameResultEither
}

object PrepareRenameResultEitherSerializer : KSerializer<PrepareRenameResultEither> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("PrepareRenameResultEither")

    override fun serialize(encoder: Encoder, value: PrepareRenameResultEither) {
        val element = when (value) {
            is PrepareRenameResultEither.RangeValue ->
                LspJson.json.encodeToJsonElement(Range.serializer(), value.value)
            is PrepareRenameResultEither.Result ->
                LspJson.json.encodeToJsonElement(PrepareRenameResult.serializer(), value.value)
            is PrepareRenameResultEither.DefaultBehavior -> kotlinx.serialization.json.JsonPrimitive(value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): PrepareRenameResultEither {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        if (element is kotlinx.serialization.json.JsonPrimitive) {
            return PrepareRenameResultEither.DefaultBehavior(
                element.content.toBooleanStrictOrNull() ?: false
            )
        }
        val obj = element as? kotlinx.serialization.json.JsonObject ?: return PrepareRenameResultEither.Result(
            LspJson.json.decodeFromJsonElement(PrepareRenameResult.serializer(), element)
        )
        return if (obj.containsKey("placeholder")) {
            PrepareRenameResultEither.Result(
                LspJson.json.decodeFromJsonElement(PrepareRenameResult.serializer(), element)
            )
        } else {
            PrepareRenameResultEither.RangeValue(
                LspJson.json.decodeFromJsonElement(Range.serializer(), element)
            )
        }
    }
}

/**
 * `Either<SemanticTokens, SemanticTokensDelta>` — the result of a
 * `semanticTokens/full/delta` request. A delta has `edits`; full tokens have
 * `data` without `edits`.
 */
@Serializable(with = SemanticTokensResultSerializer::class)
sealed interface SemanticTokensResult {
    data class Tokens(val value: SemanticTokens) : SemanticTokensResult

    data class Delta(val value: SemanticTokensDelta) : SemanticTokensResult
}

object SemanticTokensResultSerializer : KSerializer<SemanticTokensResult> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("SemanticTokensResult")

    override fun serialize(encoder: Encoder, value: SemanticTokensResult) {
        val element = when (value) {
            is SemanticTokensResult.Tokens ->
                LspJson.json.encodeToJsonElement(SemanticTokens.serializer(), value.value)
            is SemanticTokensResult.Delta ->
                LspJson.json.encodeToJsonElement(SemanticTokensDelta.serializer(), value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): SemanticTokensResult {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        val obj = element as? kotlinx.serialization.json.JsonObject
        return if (obj != null && obj.containsKey("edits")) {
            SemanticTokensResult.Delta(
                LspJson.json.decodeFromJsonElement(SemanticTokensDelta.serializer(), element)
            )
        } else {
            SemanticTokensResult.Tokens(
                LspJson.json.decodeFromJsonElement(SemanticTokens.serializer(), element)
            )
        }
    }
}

/**
 * `Either<List<InlineCompletionItem>, InlineCompletionList>` — the result of
 * an inline-completion request.
 */
@Serializable(with = InlineCompletionResultSerializer::class)
sealed interface InlineCompletionResult {
    data class Items(val value: List<InlineCompletionItem>) : InlineCompletionResult

    data class ListValue(val value: InlineCompletionList) : InlineCompletionResult
}

object InlineCompletionResultSerializer : KSerializer<InlineCompletionResult> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("InlineCompletionResult")

    override fun serialize(encoder: Encoder, value: InlineCompletionResult) {
        val element = when (value) {
            is InlineCompletionResult.Items ->
                LspJson.json.encodeToJsonElement(
                    kotlinx.serialization.builtins.ListSerializer(InlineCompletionItem.serializer()),
                    value.value,
                )
            is InlineCompletionResult.ListValue ->
                LspJson.json.encodeToJsonElement(InlineCompletionList.serializer(), value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): InlineCompletionResult {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        return if (element is kotlinx.serialization.json.JsonArray) {
            InlineCompletionResult.Items(
                LspJson.json.decodeFromJsonElement(
                    kotlinx.serialization.builtins.ListSerializer(InlineCompletionItem.serializer()),
                    element,
                )
            )
        } else {
            InlineCompletionResult.ListValue(
                LspJson.json.decodeFromJsonElement(InlineCompletionList.serializer(), element)
            )
        }
    }
}

/**
 * `Either<SymbolInformation, DocumentSymbol>` — an element of a
 * document-symbol response.
 */
@Serializable(with = DocumentSymbolResultSerializer::class)
sealed interface DocumentSymbolResult {
    data class SymbolInfo(val value: SymbolInformation) : DocumentSymbolResult

    data class Symbol(val value: DocumentSymbol) : DocumentSymbolResult
}

object DocumentSymbolResultSerializer : KSerializer<DocumentSymbolResult> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("DocumentSymbolResult")

    override fun serialize(encoder: Encoder, value: DocumentSymbolResult) {
        val element = when (value) {
            is DocumentSymbolResult.SymbolInfo ->
                LspJson.json.encodeToJsonElement(SymbolInformation.serializer(), value.value)
            is DocumentSymbolResult.Symbol ->
                LspJson.json.encodeToJsonElement(DocumentSymbol.serializer(), value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): DocumentSymbolResult {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        val obj = element as? kotlinx.serialization.json.JsonObject
        return if (obj != null && obj.containsKey("selectionRange")) {
            DocumentSymbolResult.Symbol(
                LspJson.json.decodeFromJsonElement(DocumentSymbol.serializer(), element)
            )
        } else {
            DocumentSymbolResult.SymbolInfo(
                LspJson.json.decodeFromJsonElement(SymbolInformation.serializer(), element)
            )
        }
    }
}

/**
 * `Either<Command, CodeAction>` — an element of a code-action response.
 */
@Serializable(with = CodeActionResultSerializer::class)
sealed interface CodeActionResult {
    data class CommandValue(val value: Command) : CodeActionResult

    data class Action(val value: CodeAction) : CodeActionResult
}

object CodeActionResultSerializer : KSerializer<CodeActionResult> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("CodeActionResult")

    override fun serialize(encoder: Encoder, value: CodeActionResult) {
        val element = when (value) {
            is CodeActionResult.CommandValue ->
                LspJson.json.encodeToJsonElement(Command.serializer(), value.value)
            is CodeActionResult.Action ->
                LspJson.json.encodeToJsonElement(CodeAction.serializer(), value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): CodeActionResult {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        val obj = element as? kotlinx.serialization.json.JsonObject
        return if (obj != null && obj.containsKey("title")) {
            CodeActionResult.Action(
                LspJson.json.decodeFromJsonElement(CodeAction.serializer(), element)
            )
        } else {
            CodeActionResult.CommandValue(
                LspJson.json.decodeFromJsonElement(Command.serializer(), element)
            )
        }
    }
}

/**
 * `Either<List<SymbolInformation>, List<WorkspaceSymbol>>` — the result of a
 * workspace-symbol request. Workspace symbols have no `range`/`location.range`.
 */
@Serializable(with = WorkspaceSymbolResultSerializer::class)
sealed interface WorkspaceSymbolResult {
    data class SymbolInfos(val value: List<SymbolInformation>) : WorkspaceSymbolResult

    data class Symbols(val value: List<WorkspaceSymbol>) : WorkspaceSymbolResult
}

object WorkspaceSymbolResultSerializer : KSerializer<WorkspaceSymbolResult> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("WorkspaceSymbolResult")

    override fun serialize(encoder: Encoder, value: WorkspaceSymbolResult) {
        val element = when (value) {
            is WorkspaceSymbolResult.SymbolInfos ->
                LspJson.json.encodeToJsonElement(
                    kotlinx.serialization.builtins.ListSerializer(SymbolInformation.serializer()),
                    value.value,
                )
            is WorkspaceSymbolResult.Symbols ->
                LspJson.json.encodeToJsonElement(
                    kotlinx.serialization.builtins.ListSerializer(WorkspaceSymbol.serializer()),
                    value.value,
                )
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): WorkspaceSymbolResult {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        val items = element as? kotlinx.serialization.json.JsonArray ?: return WorkspaceSymbolResult.SymbolInfos(emptyList())
        val anyWorkspaceSymbol = items.any {
            (it as? kotlinx.serialization.json.JsonObject)?.containsKey("location") == true
        }
        return if (anyWorkspaceSymbol) {
            WorkspaceSymbolResult.Symbols(
                LspJson.json.decodeFromJsonElement(
                    kotlinx.serialization.builtins.ListSerializer(WorkspaceSymbol.serializer()),
                    element,
                )
            )
        } else {
            WorkspaceSymbolResult.SymbolInfos(
                LspJson.json.decodeFromJsonElement(
                    kotlinx.serialization.builtins.ListSerializer(SymbolInformation.serializer()),
                    element,
                )
            )
        }
    }
}