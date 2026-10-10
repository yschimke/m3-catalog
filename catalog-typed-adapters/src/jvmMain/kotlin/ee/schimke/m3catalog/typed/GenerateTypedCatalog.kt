package ee.schimke.m3catalog.typed

import ee.schimke.composeai.discovery.BuilderPolicy
import ee.schimke.composeai.discovery.ComponentRecordFile
import ee.schimke.composeai.discovery.ScreenDocument
import ee.schimke.composeai.discovery.ScreenGenerator
import ee.schimke.composeai.discovery.ScreenNode
import ee.schimke.composeai.discovery.ScreenValue
import ee.schimke.composeai.discovery.TypedAdapterDiscovery
import ee.schimke.composeai.discovery.UiBuilderPolicyFile
import ee.schimke.composeai.discovery.writeTo
import java.nio.file.Files
import java.nio.file.Path
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

fun discoverTypedM3Controls(): ComponentRecordFile {
  val names = listOf("M3Checkbox", "M3Switch", "M3RadioButton")
  val record =
    TypedAdapterDiscovery.discover(
      module = "catalog-typed-adapters",
      variant = "jvm",
      callables =
        names.map { TypedAdapterDiscovery.Callable("ee.schimke.m3catalog.typed.M3ControlsKt", it) },
    )
  return record
    .newBuilder()
    .apply {
      components = components.map { component ->
        component
          .newBuilder()
          .apply {
            builder = BuilderPolicy.Builder().apply { group = "Selection controls" }.build()
          }
          .build()
      }
    }
    .build()
}

fun main(args: Array<String>) {
  require(args.size == 3) {
    "Expected task-owned catalog output, generated sources and legacy policy"
  }
  val output = Path.of(args[0])
  val sources = Path.of(args[1])
  val typed = TypedM3Catalog(discoverTypedM3Controls())
  val pair = typed.generate()
  // Both directories belong exclusively to this task. The writer publishes the pair atomically.
  output.toFile().deleteRecursively()
  pair.writeTo(output)
  val exportSources = output.resolve("generated-source")
  Files.createDirectories(exportSources)
  for (component in pair.record.components) {
    for (explicit in listOf(false, true)) {
      val name = component.symbol.name + if (explicit) "ExplicitExport" else "DefaultExport"
      val arguments =
        if (explicit)
          mapOf(
            (if (component.symbol.name == "M3RadioButton") "selected" else "checked") to
              ScreenValue.Bool(true),
            "enabled" to ScreenValue.Bool(false),
          )
        else emptyMap()
      val result =
        ScreenGenerator.generate(
          ScreenDocument(name, ScreenNode(component.canonicalId, arguments = arguments)),
          pair.record,
          packageName = "ee.schimke.m3catalog.typed.consumer",
        )
      check(result is ScreenGenerator.Result.Emitted) { "Source fixture refused: $result" }
      Files.writeString(exportSources.resolve("$name.kt"), result.source)
    }
  }
  val json = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
  }
  val legacy =
    json.decodeFromString(UiBuilderPolicyFile.serializer(), Files.readString(Path.of(args[2])))
  val covered = typed.adapters.map { it.id }.toSet()
  val remaining = legacy.components.keys - covered
  val compatibilityChanges =
    covered
      .sorted()
      .associateWith { id ->
        val old = requireNotNull(legacy.components[id])
        val new = pair.catalog.statusSemantics.components.getValue(id)
        buildList {
          if (old.record != new.record) add("record")
          if (old.canvas != new.canvas) add("canvas")
          if (old.modifierCapabilities != new.modifierCapabilities) add("modifierCapabilities")
          if (old.propertyCapabilities != new.propertyCapabilities) add("propertyCapabilities")
          if (old.slotCapabilities != new.slotCapabilities) add("slotCapabilities")
        }
      }
      .filterValues { it.isNotEmpty() }
  val unverified =
    listOf(
      "Existing saved designs through the standard host, including stateEquals",
      "Production source export with complete state/event projection",
      "Packaged browser runtime exercised through the sandbox host",
    )
  val templateCoverage =
    legacy.templates.associate { template ->
      val document =
        json
          .parseToJsonElement(Files.readString(Path.of(args[2]).parent.resolve(template.path)))
          .jsonObject
      val referenced =
        document
          .getValue("nodes")
          .jsonObject
          .values
          .map { it.jsonObject.getValue("componentId").jsonPrimitive.content }
          .toSet()
      template.path to
        buildJsonObject {
          put(
            "uncoveredComponents",
            JsonArray((referenced - covered).sorted().map(::JsonPrimitive)),
          )
        }
    }
  val report = buildJsonObject {
    put(
      "readyForCutover",
      remaining.isEmpty() &&
        legacy.builtins.isEmpty() &&
        compatibilityChanges.isEmpty() &&
        unverified.isEmpty(),
    )
    put("policyEquivalenceTask", ":catalog-typed-policy:test")
    put("typedComponents", JsonArray(covered.sorted().map(::JsonPrimitive)))
    put("remainingComponents", JsonArray(remaining.sorted().map(::JsonPrimitive)))
    put("remainingBuiltins", JsonArray(legacy.builtins.keys.sorted().map(::JsonPrimitive)))
    put(
      "compatibilityChanges",
      JsonObject(
        compatibilityChanges.mapValues { (_, fields) -> JsonArray(fields.map(::JsonPrimitive)) }
      ),
    )
    put("unverifiedGates", JsonArray(unverified.map(::JsonPrimitive)))
    put("templateCoverage", JsonObject(templateCoverage))
  }
  Files.writeString(
    output.resolve("readiness.json"),
    json.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), report) + "\n",
  )
  sources.toFile().deleteRecursively()
  val source = sources.resolve("ee/schimke/m3catalog/typed/GeneratedTypedRecords.kt")
  Files.createDirectories(source.parent)
  // JVM discovery is the only writer of the browser's records; signatures are never transcribed.
  val encoded =
    Json.encodeToString(ComponentRecordFile.serializer(), typed.discovered)
      .replace("\\", "\\\\")
      .replace("\"", "\\\"")
      .replace("$", "\\$")
  Files.writeString(
    source,
    "package ee.schimke.m3catalog.typed\n\ninternal const val typedM3RecordsJson = \"$encoded\"\n",
  )
  println(
    "Typed catalog pair: $output; cutover blocked by ${remaining.size} remaining declarations and compatibility gates."
  )
}
