package cn.enaium.lsp

import cn.enaium.lsp.model.*
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EitherTest {

    @Test
    fun providerEncodesAsBoolean() {
        val element = LspJson.json.encodeToJsonElement(
            HoverProvider.serializer(),
            HoverProvider.Enabled(true),
        )
        assertEquals(JsonPrimitive(true), element)
    }

    @Test
    fun providerDecodesBoolean() {
        val provider = LspJson.json.decodeFromJsonElement(
            HoverProvider.serializer(),
            JsonPrimitive(true),
        )
        assertTrue(provider is HoverProvider.Enabled)
        assertEquals(true, (provider as HoverProvider.Enabled).value)
    }

    @Test
    fun providerEncodesAsOptions() {
        val options = HoverProvider.Options(HoverOptions(workDoneProgress = true))
        val element = LspJson.json.encodeToJsonElement(HoverProvider.serializer(), options)
        val obj = element as kotlinx.serialization.json.JsonObject
        assertEquals(JsonPrimitive(true), obj["workDoneProgress"])
    }

    @Test
    fun providerDecodesOptions() {
        val provider = LspJson.json.decodeFromJsonElement(
            HoverProvider.serializer(),
            kotlinx.serialization.json.buildJsonObject { put("workDoneProgress", JsonPrimitive(true)) },
        )
        assertTrue(provider is HoverProvider.Options)
        assertEquals(true, (provider as HoverProvider.Options).value.workDoneProgress)
    }

    @Test
    fun tokenRoundTrip() {
        val t = Token.NumberValue(7)
        val el = LspJson.json.encodeToJsonElement(Token.serializer(), t)
        assertEquals(JsonPrimitive(7), el)
        val back = LspJson.json.decodeFromJsonElement(Token.serializer(), el)
        assertEquals(Token.NumberValue(7), back)
    }
}

class SealedShapeTest {

    @Test
    fun textDocumentSyncKinds() {
        // numeric kind
        val el = LspJson.json.encodeToJsonElement(
            TextDocumentSync.serializer(),
            TextDocumentSync.Kind(2),
        )
        assertEquals(JsonPrimitive(2), el)
        // options object
        val opts = TextDocumentSync.Options(TextDocumentSyncOptions(openClose = true, change = 1))
        val el2 = LspJson.json.encodeToJsonElement(TextDocumentSync.serializer(), opts)
        val obj = el2 as kotlinx.serialization.json.JsonObject
        assertEquals(JsonPrimitive(1), obj["change"])
    }

    @Test
    fun completionResultShape() {
        val items = CompletionResult.Items(listOf(CompletionItem(label = "x")))
        val el = LspJson.json.encodeToJsonElement(CompletionResult.serializer(), items)
        assertTrue(el is kotlinx.serialization.json.JsonArray)
        val back = LspJson.json.decodeFromJsonElement(CompletionResult.serializer(), el)
        assertTrue(back is CompletionResult.Items)
        assertEquals("x", (back as CompletionResult.Items).value[0].label)
    }

    @Test
    fun documentationShape() {
        val doc = Documentation.StringValue("plain doc")
        val el = LspJson.json.encodeToJsonElement(Documentation.serializer(), doc)
        assertEquals(JsonPrimitive("plain doc"), el)
        val markup = Documentation.Markup(MarkupContent(MarkupKind.Markdown, "# h"))
        val el2 = LspJson.json.encodeToJsonElement(Documentation.serializer(), markup)
        val obj = el2 as kotlinx.serialization.json.JsonObject
        assertEquals(JsonPrimitive("markdown"), obj["kind"])
    }

    @Test
    fun hoverContentsShape() {
        val markup = HoverContents.Markup(MarkupContent(MarkupKind.PlainText, "hi"))
        val el = LspJson.json.encodeToJsonElement(HoverContents.serializer(), markup)
        val obj = el as kotlinx.serialization.json.JsonObject
        assertEquals(JsonPrimitive("plaintext"), obj["kind"])
        val back = LspJson.json.decodeFromJsonElement(HoverContents.serializer(), el)
        assertTrue(back is HoverContents.Markup)
    }

    @Test
    fun workspaceEditShapes() {
        val change = DocumentChange.Operation(
            ResourceOperation.Create(CreateFile(kind = "create", uri = "file:///a"))
        )
        val el = LspJson.json.encodeToJsonElement(DocumentChange.serializer(), change)
        val obj = el as kotlinx.serialization.json.JsonObject
        assertEquals(JsonPrimitive("create"), obj["kind"])
        val back = LspJson.json.decodeFromJsonElement(DocumentChange.serializer(), el)
        assertTrue(back is DocumentChange.Operation)
        assertTrue((back as DocumentChange.Operation).value is ResourceOperation.Create)
        assertEquals("file:///a", ((back.value as ResourceOperation.Create).value).uri)
    }
}
