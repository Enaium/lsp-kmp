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
 * `Either<Boolean, HoverOptions>` — the `hoverProvider` server capability.
 * Either `true`/`false` or an options object.
 */
@Serializable(with = HoverProviderSerializer::class)
sealed interface HoverProvider {
    @JvmInline
    value class Enabled(val value: Boolean) : HoverProvider

    data class Options(val value: HoverOptions) : HoverProvider
}

object HoverProviderSerializer : KSerializer<HoverProvider> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("HoverProvider")

    override fun serialize(encoder: Encoder, value: HoverProvider) {
        val element = when (value) {
            is HoverProvider.Enabled -> JsonPrimitive(value.value)
            is HoverProvider.Options -> LspJson.json.encodeToJsonElement(HoverOptions.serializer(), value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): HoverProvider {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        return if (element is JsonPrimitive) {
            HoverProvider.Enabled(element.booleanOrNull ?: false)
        } else {
            HoverProvider.Options(LspJson.json.decodeFromJsonElement(HoverOptions.serializer(), element))
        }
    }
}

/**
 * `Either<Boolean, DefinitionOptions>` — the `definitionProvider` server capability.
 * Either `true`/`false` or an options object.
 */
@Serializable(with = DefinitionProviderSerializer::class)
sealed interface DefinitionProvider {
    @JvmInline
    value class Enabled(val value: Boolean) : DefinitionProvider

    data class Options(val value: DefinitionOptions) : DefinitionProvider
}

object DefinitionProviderSerializer : KSerializer<DefinitionProvider> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("DefinitionProvider")

    override fun serialize(encoder: Encoder, value: DefinitionProvider) {
        val element = when (value) {
            is DefinitionProvider.Enabled -> JsonPrimitive(value.value)
            is DefinitionProvider.Options -> LspJson.json.encodeToJsonElement(DefinitionOptions.serializer(), value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): DefinitionProvider {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        return if (element is JsonPrimitive) {
            DefinitionProvider.Enabled(element.booleanOrNull ?: false)
        } else {
            DefinitionProvider.Options(LspJson.json.decodeFromJsonElement(DefinitionOptions.serializer(), element))
        }
    }
}

/**
 * `Either<Boolean, TypeDefinitionRegistrationOptions>` — the `typeDefinitionProvider` server capability.
 * Either `true`/`false` or an options object.
 */
@Serializable(with = TypeDefinitionProviderSerializer::class)
sealed interface TypeDefinitionProvider {
    @JvmInline
    value class Enabled(val value: Boolean) : TypeDefinitionProvider

    data class Options(val value: TypeDefinitionRegistrationOptions) : TypeDefinitionProvider
}

object TypeDefinitionProviderSerializer : KSerializer<TypeDefinitionProvider> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("TypeDefinitionProvider")

    override fun serialize(encoder: Encoder, value: TypeDefinitionProvider) {
        val element = when (value) {
            is TypeDefinitionProvider.Enabled -> JsonPrimitive(value.value)
            is TypeDefinitionProvider.Options -> LspJson.json.encodeToJsonElement(TypeDefinitionRegistrationOptions.serializer(), value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): TypeDefinitionProvider {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        return if (element is JsonPrimitive) {
            TypeDefinitionProvider.Enabled(element.booleanOrNull ?: false)
        } else {
            TypeDefinitionProvider.Options(LspJson.json.decodeFromJsonElement(TypeDefinitionRegistrationOptions.serializer(), element))
        }
    }
}

/**
 * `Either<Boolean, ImplementationRegistrationOptions>` — the `implementationProvider` server capability.
 * Either `true`/`false` or an options object.
 */
@Serializable(with = ImplementationProviderSerializer::class)
sealed interface ImplementationProvider {
    @JvmInline
    value class Enabled(val value: Boolean) : ImplementationProvider

    data class Options(val value: ImplementationRegistrationOptions) : ImplementationProvider
}

object ImplementationProviderSerializer : KSerializer<ImplementationProvider> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ImplementationProvider")

    override fun serialize(encoder: Encoder, value: ImplementationProvider) {
        val element = when (value) {
            is ImplementationProvider.Enabled -> JsonPrimitive(value.value)
            is ImplementationProvider.Options -> LspJson.json.encodeToJsonElement(ImplementationRegistrationOptions.serializer(), value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): ImplementationProvider {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        return if (element is JsonPrimitive) {
            ImplementationProvider.Enabled(element.booleanOrNull ?: false)
        } else {
            ImplementationProvider.Options(LspJson.json.decodeFromJsonElement(ImplementationRegistrationOptions.serializer(), element))
        }
    }
}

/**
 * `Either<Boolean, ReferenceOptions>` — the `referencesProvider` server capability.
 * Either `true`/`false` or an options object.
 */
@Serializable(with = ReferencesProviderSerializer::class)
sealed interface ReferencesProvider {
    @JvmInline
    value class Enabled(val value: Boolean) : ReferencesProvider

    data class Options(val value: ReferenceOptions) : ReferencesProvider
}

object ReferencesProviderSerializer : KSerializer<ReferencesProvider> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ReferencesProvider")

    override fun serialize(encoder: Encoder, value: ReferencesProvider) {
        val element = when (value) {
            is ReferencesProvider.Enabled -> JsonPrimitive(value.value)
            is ReferencesProvider.Options -> LspJson.json.encodeToJsonElement(ReferenceOptions.serializer(), value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): ReferencesProvider {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        return if (element is JsonPrimitive) {
            ReferencesProvider.Enabled(element.booleanOrNull ?: false)
        } else {
            ReferencesProvider.Options(LspJson.json.decodeFromJsonElement(ReferenceOptions.serializer(), element))
        }
    }
}

/**
 * `Either<Boolean, DocumentHighlightOptions>` — the `documentHighlightProvider` server capability.
 * Either `true`/`false` or an options object.
 */
@Serializable(with = DocumentHighlightProviderSerializer::class)
sealed interface DocumentHighlightProvider {
    @JvmInline
    value class Enabled(val value: Boolean) : DocumentHighlightProvider

    data class Options(val value: DocumentHighlightOptions) : DocumentHighlightProvider
}

object DocumentHighlightProviderSerializer : KSerializer<DocumentHighlightProvider> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("DocumentHighlightProvider")

    override fun serialize(encoder: Encoder, value: DocumentHighlightProvider) {
        val element = when (value) {
            is DocumentHighlightProvider.Enabled -> JsonPrimitive(value.value)
            is DocumentHighlightProvider.Options -> LspJson.json.encodeToJsonElement(DocumentHighlightOptions.serializer(), value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): DocumentHighlightProvider {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        return if (element is JsonPrimitive) {
            DocumentHighlightProvider.Enabled(element.booleanOrNull ?: false)
        } else {
            DocumentHighlightProvider.Options(LspJson.json.decodeFromJsonElement(DocumentHighlightOptions.serializer(), element))
        }
    }
}

/**
 * `Either<Boolean, DocumentSymbolOptions>` — the `documentSymbolProvider` server capability.
 * Either `true`/`false` or an options object.
 */
@Serializable(with = DocumentSymbolProviderSerializer::class)
sealed interface DocumentSymbolProvider {
    @JvmInline
    value class Enabled(val value: Boolean) : DocumentSymbolProvider

    data class Options(val value: DocumentSymbolOptions) : DocumentSymbolProvider
}

object DocumentSymbolProviderSerializer : KSerializer<DocumentSymbolProvider> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("DocumentSymbolProvider")

    override fun serialize(encoder: Encoder, value: DocumentSymbolProvider) {
        val element = when (value) {
            is DocumentSymbolProvider.Enabled -> JsonPrimitive(value.value)
            is DocumentSymbolProvider.Options -> LspJson.json.encodeToJsonElement(DocumentSymbolOptions.serializer(), value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): DocumentSymbolProvider {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        return if (element is JsonPrimitive) {
            DocumentSymbolProvider.Enabled(element.booleanOrNull ?: false)
        } else {
            DocumentSymbolProvider.Options(LspJson.json.decodeFromJsonElement(DocumentSymbolOptions.serializer(), element))
        }
    }
}

/**
 * `Either<Boolean, WorkspaceSymbolOptions>` — the `workspaceSymbolProvider` server capability.
 * Either `true`/`false` or an options object.
 */
@Serializable(with = WorkspaceSymbolProviderSerializer::class)
sealed interface WorkspaceSymbolProvider {
    @JvmInline
    value class Enabled(val value: Boolean) : WorkspaceSymbolProvider

    data class Options(val value: WorkspaceSymbolOptions) : WorkspaceSymbolProvider
}

object WorkspaceSymbolProviderSerializer : KSerializer<WorkspaceSymbolProvider> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("WorkspaceSymbolProvider")

    override fun serialize(encoder: Encoder, value: WorkspaceSymbolProvider) {
        val element = when (value) {
            is WorkspaceSymbolProvider.Enabled -> JsonPrimitive(value.value)
            is WorkspaceSymbolProvider.Options -> LspJson.json.encodeToJsonElement(WorkspaceSymbolOptions.serializer(), value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): WorkspaceSymbolProvider {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        return if (element is JsonPrimitive) {
            WorkspaceSymbolProvider.Enabled(element.booleanOrNull ?: false)
        } else {
            WorkspaceSymbolProvider.Options(LspJson.json.decodeFromJsonElement(WorkspaceSymbolOptions.serializer(), element))
        }
    }
}

/**
 * `Either<Boolean, CodeActionOptions>` — the `codeActionProvider` server capability.
 * Either `true`/`false` or an options object.
 */
@Serializable(with = CodeActionProviderSerializer::class)
sealed interface CodeActionProvider {
    @JvmInline
    value class Enabled(val value: Boolean) : CodeActionProvider

    data class Options(val value: CodeActionOptions) : CodeActionProvider
}

object CodeActionProviderSerializer : KSerializer<CodeActionProvider> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("CodeActionProvider")

    override fun serialize(encoder: Encoder, value: CodeActionProvider) {
        val element = when (value) {
            is CodeActionProvider.Enabled -> JsonPrimitive(value.value)
            is CodeActionProvider.Options -> LspJson.json.encodeToJsonElement(CodeActionOptions.serializer(), value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): CodeActionProvider {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        return if (element is JsonPrimitive) {
            CodeActionProvider.Enabled(element.booleanOrNull ?: false)
        } else {
            CodeActionProvider.Options(LspJson.json.decodeFromJsonElement(CodeActionOptions.serializer(), element))
        }
    }
}

/**
 * `Either<Boolean, DocumentFormattingOptions>` — the `documentFormattingProvider` server capability.
 * Either `true`/`false` or an options object.
 */
@Serializable(with = DocumentFormattingProviderSerializer::class)
sealed interface DocumentFormattingProvider {
    @JvmInline
    value class Enabled(val value: Boolean) : DocumentFormattingProvider

    data class Options(val value: DocumentFormattingOptions) : DocumentFormattingProvider
}

object DocumentFormattingProviderSerializer : KSerializer<DocumentFormattingProvider> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("DocumentFormattingProvider")

    override fun serialize(encoder: Encoder, value: DocumentFormattingProvider) {
        val element = when (value) {
            is DocumentFormattingProvider.Enabled -> JsonPrimitive(value.value)
            is DocumentFormattingProvider.Options -> LspJson.json.encodeToJsonElement(DocumentFormattingOptions.serializer(), value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): DocumentFormattingProvider {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        return if (element is JsonPrimitive) {
            DocumentFormattingProvider.Enabled(element.booleanOrNull ?: false)
        } else {
            DocumentFormattingProvider.Options(LspJson.json.decodeFromJsonElement(DocumentFormattingOptions.serializer(), element))
        }
    }
}

/**
 * `Either<Boolean, DocumentRangeFormattingOptions>` — the `documentRangeFormattingProvider` server capability.
 * Either `true`/`false` or an options object.
 */
@Serializable(with = DocumentRangeFormattingProviderSerializer::class)
sealed interface DocumentRangeFormattingProvider {
    @JvmInline
    value class Enabled(val value: Boolean) : DocumentRangeFormattingProvider

    data class Options(val value: DocumentRangeFormattingOptions) : DocumentRangeFormattingProvider
}

object DocumentRangeFormattingProviderSerializer : KSerializer<DocumentRangeFormattingProvider> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("DocumentRangeFormattingProvider")

    override fun serialize(encoder: Encoder, value: DocumentRangeFormattingProvider) {
        val element = when (value) {
            is DocumentRangeFormattingProvider.Enabled -> JsonPrimitive(value.value)
            is DocumentRangeFormattingProvider.Options -> LspJson.json.encodeToJsonElement(DocumentRangeFormattingOptions.serializer(), value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): DocumentRangeFormattingProvider {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        return if (element is JsonPrimitive) {
            DocumentRangeFormattingProvider.Enabled(element.booleanOrNull ?: false)
        } else {
            DocumentRangeFormattingProvider.Options(LspJson.json.decodeFromJsonElement(DocumentRangeFormattingOptions.serializer(), element))
        }
    }
}

/**
 * `Either<Boolean, RenameOptions>` — the `renameProvider` server capability.
 * Either `true`/`false` or an options object.
 */
@Serializable(with = RenameProviderSerializer::class)
sealed interface RenameProvider {
    @JvmInline
    value class Enabled(val value: Boolean) : RenameProvider

    data class Options(val value: RenameOptions) : RenameProvider
}

object RenameProviderSerializer : KSerializer<RenameProvider> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("RenameProvider")

    override fun serialize(encoder: Encoder, value: RenameProvider) {
        val element = when (value) {
            is RenameProvider.Enabled -> JsonPrimitive(value.value)
            is RenameProvider.Options -> LspJson.json.encodeToJsonElement(RenameOptions.serializer(), value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): RenameProvider {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        return if (element is JsonPrimitive) {
            RenameProvider.Enabled(element.booleanOrNull ?: false)
        } else {
            RenameProvider.Options(LspJson.json.decodeFromJsonElement(RenameOptions.serializer(), element))
        }
    }
}

/**
 * `Either<Boolean, ColorProviderOptions>` — the `colorProvider` server capability.
 * Either `true`/`false` or an options object.
 */
@Serializable(with = ColorProviderSerializer::class)
sealed interface ColorProvider {
    @JvmInline
    value class Enabled(val value: Boolean) : ColorProvider

    data class Options(val value: ColorProviderOptions) : ColorProvider
}

object ColorProviderSerializer : KSerializer<ColorProvider> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ColorProvider")

    override fun serialize(encoder: Encoder, value: ColorProvider) {
        val element = when (value) {
            is ColorProvider.Enabled -> JsonPrimitive(value.value)
            is ColorProvider.Options -> LspJson.json.encodeToJsonElement(ColorProviderOptions.serializer(), value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): ColorProvider {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        return if (element is JsonPrimitive) {
            ColorProvider.Enabled(element.booleanOrNull ?: false)
        } else {
            ColorProvider.Options(LspJson.json.decodeFromJsonElement(ColorProviderOptions.serializer(), element))
        }
    }
}

/**
 * `Either<Boolean, FoldingRangeProviderOptions>` — the `foldingRangeProvider` server capability.
 * Either `true`/`false` or an options object.
 */
@Serializable(with = FoldingRangeProviderSerializer::class)
sealed interface FoldingRangeProvider {
    @JvmInline
    value class Enabled(val value: Boolean) : FoldingRangeProvider

    data class Options(val value: FoldingRangeProviderOptions) : FoldingRangeProvider
}

object FoldingRangeProviderSerializer : KSerializer<FoldingRangeProvider> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("FoldingRangeProvider")

    override fun serialize(encoder: Encoder, value: FoldingRangeProvider) {
        val element = when (value) {
            is FoldingRangeProvider.Enabled -> JsonPrimitive(value.value)
            is FoldingRangeProvider.Options -> LspJson.json.encodeToJsonElement(FoldingRangeProviderOptions.serializer(), value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): FoldingRangeProvider {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        return if (element is JsonPrimitive) {
            FoldingRangeProvider.Enabled(element.booleanOrNull ?: false)
        } else {
            FoldingRangeProvider.Options(LspJson.json.decodeFromJsonElement(FoldingRangeProviderOptions.serializer(), element))
        }
    }
}

/**
 * `Either<Boolean, DeclarationRegistrationOptions>` — the `declarationProvider` server capability.
 * Either `true`/`false` or an options object.
 */
@Serializable(with = DeclarationProviderSerializer::class)
sealed interface DeclarationProvider {
    @JvmInline
    value class Enabled(val value: Boolean) : DeclarationProvider

    data class Options(val value: DeclarationRegistrationOptions) : DeclarationProvider
}

object DeclarationProviderSerializer : KSerializer<DeclarationProvider> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("DeclarationProvider")

    override fun serialize(encoder: Encoder, value: DeclarationProvider) {
        val element = when (value) {
            is DeclarationProvider.Enabled -> JsonPrimitive(value.value)
            is DeclarationProvider.Options -> LspJson.json.encodeToJsonElement(DeclarationRegistrationOptions.serializer(), value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): DeclarationProvider {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        return if (element is JsonPrimitive) {
            DeclarationProvider.Enabled(element.booleanOrNull ?: false)
        } else {
            DeclarationProvider.Options(LspJson.json.decodeFromJsonElement(DeclarationRegistrationOptions.serializer(), element))
        }
    }
}

/**
 * `Either<Boolean, TypeHierarchyRegistrationOptions>` — the `typeHierarchyProvider` server capability.
 * Either `true`/`false` or an options object.
 */
@Serializable(with = TypeHierarchyProviderSerializer::class)
sealed interface TypeHierarchyProvider {
    @JvmInline
    value class Enabled(val value: Boolean) : TypeHierarchyProvider

    data class Options(val value: TypeHierarchyRegistrationOptions) : TypeHierarchyProvider
}

object TypeHierarchyProviderSerializer : KSerializer<TypeHierarchyProvider> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("TypeHierarchyProvider")

    override fun serialize(encoder: Encoder, value: TypeHierarchyProvider) {
        val element = when (value) {
            is TypeHierarchyProvider.Enabled -> JsonPrimitive(value.value)
            is TypeHierarchyProvider.Options -> LspJson.json.encodeToJsonElement(TypeHierarchyRegistrationOptions.serializer(), value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): TypeHierarchyProvider {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        return if (element is JsonPrimitive) {
            TypeHierarchyProvider.Enabled(element.booleanOrNull ?: false)
        } else {
            TypeHierarchyProvider.Options(LspJson.json.decodeFromJsonElement(TypeHierarchyRegistrationOptions.serializer(), element))
        }
    }
}

/**
 * `Either<Boolean, CallHierarchyRegistrationOptions>` — the `callHierarchyProvider` server capability.
 * Either `true`/`false` or an options object.
 */
@Serializable(with = CallHierarchyProviderSerializer::class)
sealed interface CallHierarchyProvider {
    @JvmInline
    value class Enabled(val value: Boolean) : CallHierarchyProvider

    data class Options(val value: CallHierarchyRegistrationOptions) : CallHierarchyProvider
}

object CallHierarchyProviderSerializer : KSerializer<CallHierarchyProvider> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("CallHierarchyProvider")

    override fun serialize(encoder: Encoder, value: CallHierarchyProvider) {
        val element = when (value) {
            is CallHierarchyProvider.Enabled -> JsonPrimitive(value.value)
            is CallHierarchyProvider.Options -> LspJson.json.encodeToJsonElement(CallHierarchyRegistrationOptions.serializer(), value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): CallHierarchyProvider {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        return if (element is JsonPrimitive) {
            CallHierarchyProvider.Enabled(element.booleanOrNull ?: false)
        } else {
            CallHierarchyProvider.Options(LspJson.json.decodeFromJsonElement(CallHierarchyRegistrationOptions.serializer(), element))
        }
    }
}

/**
 * `Either<Boolean, SelectionRangeRegistrationOptions>` — the `selectionRangeProvider` server capability.
 * Either `true`/`false` or an options object.
 */
@Serializable(with = SelectionRangeProviderSerializer::class)
sealed interface SelectionRangeProvider {
    @JvmInline
    value class Enabled(val value: Boolean) : SelectionRangeProvider

    data class Options(val value: SelectionRangeRegistrationOptions) : SelectionRangeProvider
}

object SelectionRangeProviderSerializer : KSerializer<SelectionRangeProvider> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("SelectionRangeProvider")

    override fun serialize(encoder: Encoder, value: SelectionRangeProvider) {
        val element = when (value) {
            is SelectionRangeProvider.Enabled -> JsonPrimitive(value.value)
            is SelectionRangeProvider.Options -> LspJson.json.encodeToJsonElement(SelectionRangeRegistrationOptions.serializer(), value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): SelectionRangeProvider {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        return if (element is JsonPrimitive) {
            SelectionRangeProvider.Enabled(element.booleanOrNull ?: false)
        } else {
            SelectionRangeProvider.Options(LspJson.json.decodeFromJsonElement(SelectionRangeRegistrationOptions.serializer(), element))
        }
    }
}

/**
 * `Either<Boolean, LinkedEditingRangeRegistrationOptions>` — the `linkedEditingRangeProvider` server capability.
 * Either `true`/`false` or an options object.
 */
@Serializable(with = LinkedEditingRangeProviderSerializer::class)
sealed interface LinkedEditingRangeProvider {
    @JvmInline
    value class Enabled(val value: Boolean) : LinkedEditingRangeProvider

    data class Options(val value: LinkedEditingRangeRegistrationOptions) : LinkedEditingRangeProvider
}

object LinkedEditingRangeProviderSerializer : KSerializer<LinkedEditingRangeProvider> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("LinkedEditingRangeProvider")

    override fun serialize(encoder: Encoder, value: LinkedEditingRangeProvider) {
        val element = when (value) {
            is LinkedEditingRangeProvider.Enabled -> JsonPrimitive(value.value)
            is LinkedEditingRangeProvider.Options -> LspJson.json.encodeToJsonElement(LinkedEditingRangeRegistrationOptions.serializer(), value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): LinkedEditingRangeProvider {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        return if (element is JsonPrimitive) {
            LinkedEditingRangeProvider.Enabled(element.booleanOrNull ?: false)
        } else {
            LinkedEditingRangeProvider.Options(LspJson.json.decodeFromJsonElement(LinkedEditingRangeRegistrationOptions.serializer(), element))
        }
    }
}

/**
 * `Either<Boolean, MonikerRegistrationOptions>` — the `monikerProvider` server capability.
 * Either `true`/`false` or an options object.
 */
@Serializable(with = MonikerProviderSerializer::class)
sealed interface MonikerProvider {
    @JvmInline
    value class Enabled(val value: Boolean) : MonikerProvider

    data class Options(val value: MonikerRegistrationOptions) : MonikerProvider
}

object MonikerProviderSerializer : KSerializer<MonikerProvider> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("MonikerProvider")

    override fun serialize(encoder: Encoder, value: MonikerProvider) {
        val element = when (value) {
            is MonikerProvider.Enabled -> JsonPrimitive(value.value)
            is MonikerProvider.Options -> LspJson.json.encodeToJsonElement(MonikerRegistrationOptions.serializer(), value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): MonikerProvider {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        return if (element is JsonPrimitive) {
            MonikerProvider.Enabled(element.booleanOrNull ?: false)
        } else {
            MonikerProvider.Options(LspJson.json.decodeFromJsonElement(MonikerRegistrationOptions.serializer(), element))
        }
    }
}

/**
 * `Either<Boolean, InlayHintRegistrationOptions>` — the `inlayHintProvider` server capability.
 * Either `true`/`false` or an options object.
 */
@Serializable(with = InlayHintProviderSerializer::class)
sealed interface InlayHintProvider {
    @JvmInline
    value class Enabled(val value: Boolean) : InlayHintProvider

    data class Options(val value: InlayHintRegistrationOptions) : InlayHintProvider
}

object InlayHintProviderSerializer : KSerializer<InlayHintProvider> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("InlayHintProvider")

    override fun serialize(encoder: Encoder, value: InlayHintProvider) {
        val element = when (value) {
            is InlayHintProvider.Enabled -> JsonPrimitive(value.value)
            is InlayHintProvider.Options -> LspJson.json.encodeToJsonElement(InlayHintRegistrationOptions.serializer(), value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): InlayHintProvider {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        return if (element is JsonPrimitive) {
            InlayHintProvider.Enabled(element.booleanOrNull ?: false)
        } else {
            InlayHintProvider.Options(LspJson.json.decodeFromJsonElement(InlayHintRegistrationOptions.serializer(), element))
        }
    }
}

/**
 * `Either<Boolean, InlineValueRegistrationOptions>` — the `inlineValueProvider` server capability.
 * Either `true`/`false` or an options object.
 */
@Serializable(with = InlineValueProviderSerializer::class)
sealed interface InlineValueProvider {
    @JvmInline
    value class Enabled(val value: Boolean) : InlineValueProvider

    data class Options(val value: InlineValueRegistrationOptions) : InlineValueProvider
}

object InlineValueProviderSerializer : KSerializer<InlineValueProvider> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("InlineValueProvider")

    override fun serialize(encoder: Encoder, value: InlineValueProvider) {
        val element = when (value) {
            is InlineValueProvider.Enabled -> JsonPrimitive(value.value)
            is InlineValueProvider.Options -> LspJson.json.encodeToJsonElement(InlineValueRegistrationOptions.serializer(), value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): InlineValueProvider {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        return if (element is JsonPrimitive) {
            InlineValueProvider.Enabled(element.booleanOrNull ?: false)
        } else {
            InlineValueProvider.Options(LspJson.json.decodeFromJsonElement(InlineValueRegistrationOptions.serializer(), element))
        }
    }
}

/**
 * `Either<Boolean, InlineCompletionRegistrationOptions>` — the `inlineCompletionProvider` server capability.
 * Either `true`/`false` or an options object.
 */
@Serializable(with = InlineCompletionProviderSerializer::class)
sealed interface InlineCompletionProvider {
    @JvmInline
    value class Enabled(val value: Boolean) : InlineCompletionProvider

    data class Options(val value: InlineCompletionRegistrationOptions) : InlineCompletionProvider
}

object InlineCompletionProviderSerializer : KSerializer<InlineCompletionProvider> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("InlineCompletionProvider")

    override fun serialize(encoder: Encoder, value: InlineCompletionProvider) {
        val element = when (value) {
            is InlineCompletionProvider.Enabled -> JsonPrimitive(value.value)
            is InlineCompletionProvider.Options -> LspJson.json.encodeToJsonElement(InlineCompletionRegistrationOptions.serializer(), value.value)
        }
        encoder.encodeSerializableValue(JsonElement.serializer(), element)
    }

    override fun deserialize(decoder: Decoder): InlineCompletionProvider {
        val element = decoder.decodeSerializableValue(JsonElement.serializer())
        return if (element is JsonPrimitive) {
            InlineCompletionProvider.Enabled(element.booleanOrNull ?: false)
        } else {
            InlineCompletionProvider.Options(LspJson.json.decodeFromJsonElement(InlineCompletionRegistrationOptions.serializer(), element))
        }
    }
}