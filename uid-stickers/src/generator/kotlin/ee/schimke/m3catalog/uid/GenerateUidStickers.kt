package ee.schimke.m3catalog.uid

import ee.schimke.composeai.discovery.ComponentRecordFile
import ee.schimke.composeai.uibuilder.export.CatalogScreenStructure
import ee.schimke.composeai.uibuilder.export.ScreenExportGate
import ee.schimke.composeai.uibuilder.protocol.DesignDocumentV1
import java.io.File
import kotlin.system.exitProcess
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

private val json = Json {
  ignoreUnknownKeys = true
  explicitNulls = false
}

/**
 * `<module dir> <output dir>`: exports every sticker in `stickers.json`, and every variant cell of
 * it, to one Kotlin file per composable.
 *
 * A variant cell is a list of builder operations, spelled the way `ui_builder_apply` spells them
 * (`setProperty`, `removeNodeProperty`, `deleteNode`, `setModifiers`). They are applied to the
 * document before export, so a cell is the same kind of edit a designer makes in the builder and
 * the cell's Kotlin is exactly what the builder would export for it.
 */
fun main(args: Array<String>) {
  val module = File(args[0])
  val out = File(args[1])
  val record =
    json.decodeFromString(
      ComponentRecordFile.serializer(),
      module.resolve("component-record.json").readText(),
    )
  val manifest = json.parseToJsonElement(module.resolve("stickers.json").readText()).jsonObject
  // The catalog's string resource accessor, so `resource` values export as its translated strings.
  val structure =
    CatalogScreenStructure(stringResources = manifest["stringResources"]?.jsonPrimitive?.content)
  out.deleteRecursively()
  val failures = mutableListOf<String>()
  for (sticker in manifest.getValue("stickers").jsonArray.map { it.jsonObject }) {
    val file = sticker.string("file")
    val base = json.parseToJsonElement(module.resolve(file).readText()).jsonObject
    val cells = buildMap {
      put(null, emptyList<JsonObject>())
      sticker["variants"]?.jsonObject?.forEach { (name, ops) ->
        put(name, ops.jsonArray.map { it.jsonObject })
      }
    }
    for ((cell, operations) in cells) {
      val title = listOfNotNull(base.string("title"), cell).joinToString(" ")
      val patched = operations.fold(base) { doc, op -> apply(doc, op) }.with("title", title)
      val document = json.decodeFromJsonElement(DesignDocumentV1.serializer(), patched)
      when (val outcome = ScreenExportGate.export(document, record, emptyMap(), structure)) {
        is ScreenExportGate.Outcome.Emitted -> {
          val name = functionName(outcome.source) ?: error("$file: no composable in the export")
          out.resolve("generated/uibuilder/$name.kt").apply {
            parentFile.mkdirs()
            writeText(outcome.source)
          }
          println("$file${cell?.let { " [$it]" }.orEmpty()} -> $name")
        }
        is ScreenExportGate.Outcome.Refused ->
          failures +=
            "$file${cell?.let { " [$it]" }.orEmpty()}: ${outcome.reasons.joinToString("; ")}"
      }
    }
  }
  if (failures.isNotEmpty()) {
    System.err.println("The builder refuses to export:\n" + failures.joinToString("\n"))
    exitProcess(1)
  }
}

/** The one top-level composable the exporter writes; its name is the design title's. */
private fun functionName(source: String): String? =
  Regex("""^fun (\w+)\(""", RegexOption.MULTILINE).find(source)?.groupValues?.get(1)

private fun apply(doc: JsonObject, op: JsonObject): JsonObject {
  val nodes = doc.getValue("nodes").jsonObject
  fun node(id: String) = nodes[id]?.jsonObject ?: error("operation names unknown node $id")
  fun withNode(id: String, node: JsonObject) = doc.with("nodes", JsonObject(nodes + (id to node)))
  return when (op.string("type")) {
    "setProperty" -> {
      val id = op.string("nodeId")
      val n = node(id)
      val properties = n["properties"]?.jsonObject.orEmpty()
      withNode(
        id,
        n.with(
          "properties",
          JsonObject(properties + (op.string("property") to op.getValue("value"))),
        ),
      )
    }
    "removeNodeProperty" -> {
      val id = op.string("nodeId")
      val n = node(id)
      withNode(
        id,
        n.with(
          "properties",
          JsonObject(n["properties"]?.jsonObject.orEmpty() - op.string("property")),
        ),
      )
    }
    "setModifiers" -> {
      val id = op.string("nodeId")
      withNode(id, node(id).with("modifiers", op.getValue("modifiers")))
    }
    "deleteNode" -> {
      val gone = descendants(nodes, op.string("nodeId"))
      val kept =
        (nodes - gone).mapValues { (_, n) ->
          val slots = n.jsonObject["slots"]?.jsonObject.orEmpty()
          n.jsonObject.with(
            "slots",
            JsonObject(
              slots.mapValues { (_, kids) ->
                JsonArray(kids.jsonArray.filter { it.jsonPrimitive.content !in gone })
              }
            ),
          )
        }
      doc.with("nodes", JsonObject(kept))
    }
    else -> error("unsupported variant operation ${op.string("type")}")
  }
}

private fun descendants(nodes: Map<String, JsonElement>, id: String): Set<String> =
  setOf(id) +
    nodes[id]
      ?.jsonObject
      ?.get("slots")
      ?.jsonObject
      .orEmpty()
      .values
      .flatMap { it.jsonArray }
      .flatMap { descendants(nodes, it.jsonPrimitive.content) }

private fun JsonObject.string(key: String): String = getValue(key).jsonPrimitive.content

private fun JsonObject.with(key: String, value: JsonElement): JsonObject =
  JsonObject(this + (key to value))

private fun JsonObject.with(key: String, value: String): JsonObject =
  with(key, JsonPrimitive(value))
