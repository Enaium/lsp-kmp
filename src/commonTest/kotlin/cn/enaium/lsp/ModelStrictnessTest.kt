package cn.enaium.lsp

import cn.enaium.lsp.dap.model.Module
import cn.enaium.lsp.dap.model.ModuleId
import cn.enaium.lsp.jsonrpc.JsonRpcJson
import cn.enaium.lsp.model.*
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.nullable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * The models mirror the specification strictly: a member the spec makes
 * required has no default, so a message that omits it fails to decode instead
 * of silently arriving with a null, and the members the spec types as a union
 * are sealed types rather than free-form JSON.
 */
class ModelStrictnessTest {
    private val json = JsonRpcJson.json

    @Test
    fun requiredMembersAreNotOptional() {
        // `Hover.contents: MarkedString | MarkedString[] | MarkupContent` is required.
        assertFailsWith<SerializationException> {
            json.decodeFromString(Hover.serializer(), """{"range":{"start":{"line":0,"character":0},"end":{"line":0,"character":1}}}""")
        }
        assertEquals(
            HoverContents.Markup(MarkupContent(kind = MarkupKind.PlainText, value = "hi")),
            json.decodeFromString(Hover.serializer(), """{"contents":{"kind":"plaintext","value":"hi"}}""").contents,
        )

        // `Diagnostic.message: string | MarkupContent` is required.
        assertFailsWith<SerializationException> {
            json.decodeFromString(
                Diagnostic.serializer(),
                """{"range":{"start":{"line":0,"character":0},"end":{"line":0,"character":1}}}""",
            )
        }

        // `WorkspaceSymbol.location: Location | {uri}` is required.
        assertFailsWith<SerializationException> {
            json.decodeFromString(WorkspaceSymbol.serializer(), """{"name":"n","kind":5}""")
        }

        // `VersionedTextDocumentIdentifier.version: integer` is required.
        assertFailsWith<SerializationException> {
            json.decodeFromString(VersionedTextDocumentIdentifier.serializer(), """{"uri":"file:///a.kt"}""")
        }

        // `FileSystemWatcher.globPattern: string | RelativePattern` is required.
        assertFailsWith<SerializationException> {
            json.decodeFromString(FileSystemWatcher.serializer(), """{"kind":7}""")
        }

        // `ParameterInformation.label: string | [uinteger, uinteger]` is required.
        assertFailsWith<SerializationException> {
            json.decodeFromString(ParameterInformation.serializer(), """{}""")
        }

        // `RelativePattern.baseUri: DocumentUri | WorkspaceFolder` is required.
        assertFailsWith<SerializationException> {
            json.decodeFromString(RelativePattern.serializer(), """{"pattern":"**/*.kt"}""")
        }
    }

    @Test
    fun unionMembersCarryTheirAlternative() {
        assertEquals(
            HoverContents.MarkedStrings(listOf(MarkedStringOrString.Marked(MarkedString("kotlin", "val x = 1")))),
            json.decodeFromString(
                Hover.serializer(),
                """{"contents":[{"language":"kotlin","value":"val x = 1"}]}""",
            ).contents,
        )

        assertEquals(
            WorkspaceSymbol(name = "n", kind = SymbolKind.Class, location = SymbolLocation.Workspace(WorkspaceSymbolLocation(uri = "file:///a.kt"))),
            json.decodeFromString(WorkspaceSymbol.serializer(), """{"name":"n","kind":5,"location":{"uri":"file:///a.kt"}}"""),
        )

        assertEquals(
            Diagnostic(
                range = Range(start = Position(0, 0), end = Position(0, 1)),
                message = Documentation.StringValue("boom"),
            ),
            json.decodeFromString(
                Diagnostic.serializer(),
                """{"range":{"start":{"line":0,"character":0},"end":{"line":0,"character":1}},"message":"boom"}""",
            ),
        )

        // `NotebookDocument.metadata` is an object, not any JSON value.
        assertFailsWith<SerializationException> {
            json.decodeFromString(
                NotebookDocument.serializer(),
                """{"uri":"file:///nb.ipynb","notebookType":"jupyter","metadata":[1],"cells":[]}""",
            )
        }
        assertEquals(
            mapOf<String, kotlinx.serialization.json.JsonElement>("kernelspec" to JsonRpcJson.json.parseToJsonElement("""{"name":"kotlin"}""")),
            json.decodeFromString(
                NotebookDocument.serializer(),
                """{"uri":"file:///nb.ipynb","notebookType":"jupyter","metadata":{"kernelspec":{"name":"kotlin"}},"cells":[]}""",
            ).metadata,
        )
    }

    @Test
    fun moduleIdIsAnIntegerOrAString() {
        assertEquals("""{"id":1,"name":"main"}""", json.encodeToString(Module.serializer(), Module(id = ModuleId.NumberValue(1), name = "main")))
        assertEquals("""{"id":"main","name":"main"}""", json.encodeToString(Module.serializer(), Module(id = ModuleId.StringValue("main"), name = "main")))
        assertEquals(ModuleId.NumberValue(7), json.decodeFromString(Module.serializer(), """{"id":7,"name":"m"}""").id)
        assertEquals(ModuleId.StringValue("m"), json.decodeFromString(Module.serializer(), """{"id":"m","name":"m"}""").id)

        assertFailsWith<SerializationException> {
            json.decodeFromString(Module.serializer(), """{"id":{"name":"m"},"name":"m"}""")
        }
    }

    @Test
    fun optionalMembersStayOptional() {
        // The counterpart of the strictness above: a member the spec marks
        // optional is still absent-able.
        assertEquals(
            ParameterInformation(label = ParameterLabel.StringValue("x")),
            json.decodeFromString(ParameterInformation.serializer(), """{"label":"x"}"""),
        )
        assertEquals(
            Hover(contents = HoverContents.Markup(MarkupContent(kind = MarkupKind.Markdown, value = "hi"))),
            json.decodeFromString(Hover.serializer(), """{"contents":{"kind":"markdown","value":"hi"}}"""),
        )
    }
}
