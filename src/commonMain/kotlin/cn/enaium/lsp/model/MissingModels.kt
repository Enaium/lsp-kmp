package cn.enaium.lsp.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/** Registration options for the call hierarchy request. */
@Serializable
data class CallHierarchyOptions(
    val workDoneProgress: Boolean? = null,
)

/** Registration options for the call hierarchy request. */
@Serializable
data class CallHierarchyRegistrationOptions(
    val documentSelector: List<DocumentFilter>? = null,
    val workDoneProgress: Boolean? = null,
    val id: String? = null,
)

/** Registration options for the type hierarchy request. */
@Serializable
data class TypeHierarchyRegistrationOptions(
    val documentSelector: List<DocumentFilter>? = null,
    val workDoneProgress: Boolean? = null,
    val id: String? = null,
)

/** Options for the workspace symbol request. */
@Serializable
data class WorkspaceSymbolOptions(
    val workDoneProgress: Boolean? = null,
    val resolveProvider: Boolean? = null,
)

/** Registration options for the workspace symbol request. */
@Serializable
data class WorkspaceSymbolRegistrationOptions(
    val workDoneProgress: Boolean? = null,
    val resolveProvider: Boolean? = null,
)

/**
 * The result of a `textDocument/diagnostic` request (pull diagnostics).
 * Either a related full or related unchanged document diagnostic report.
 */
/**
 * Any JSON value — the spec's `LSPAny`. kotlinx's [JsonElement] is itself a
 * sealed union (`JsonObject` | `JsonArray` | `JsonPrimitive` | `JsonNull`), so
 * it is the union the spec asks for, named as the spec names it.
 */
typealias LSPAny = JsonElement

/** A JSON object — the spec's `LSPObject`: `{ [key: string]: LSPAny }`. */
typealias LSPObject = Map<String, LSPAny>

typealias DocumentDiagnosticReport = DocumentDiagnosticEither

/**
 * The result of a `workspace/diagnostic` request.
 * Either a workspace full or workspace unchanged document diagnostic report.
 */
typealias WorkspaceDocumentDiagnosticReport = WorkspaceDocumentDiagnosticEither
