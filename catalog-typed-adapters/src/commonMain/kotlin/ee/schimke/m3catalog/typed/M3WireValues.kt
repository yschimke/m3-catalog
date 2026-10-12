package ee.schimke.m3catalog.typed

import ee.schimke.composeai.discovery.AdapterProperty
import ee.schimke.composeai.discovery.TypedComponentAdapter
import ee.schimke.composeai.uibuilder.renderer.sdk.CanvasNodeScope
import ee.schimke.composeai.uibuilder.renderer.sdk.canvasAdapterRegistry
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull

/** Adapt the established document spellings after the SDK has resolved bindings. */
fun typedM3CanvasAdapters(catalog: TypedM3Catalog) = canvasAdapterRegistry {
  val typed = rawTypedM3CanvasAdapters(catalog)
  catalog.runtimeAdapters.forEach { definition ->
    register(definition.id) { typed[definition.id]!!.invoke(normalizedScope(this, definition)) }
  }
}

private fun normalizedScope(
  scope: CanvasNodeScope,
  definition: TypedComponentAdapter<*>,
): CanvasNodeScope {
  val properties = scope.node.properties.toMutableMap()
  definition.properties.forEach { property ->
    val wrapper = properties[property.name] as? JsonObject ?: return@forEach
    val type = (wrapper["type"] as? JsonPrimitive)?.contentOrNull
    val raw = wrapper["value"]
    val expected = property.codec.jsonType
    val canonical =
      when {
        type == "bool" && expected == "boolean" -> "boolean"
        type in setOf("enum", "color", "colorToken", "text") && expected == "string" -> "string"
        type in setOf("float", "dp", "sp", "int") && expected in setOf("number", "integer") ->
          expected
        type == "string" &&
          expected == "boolean" &&
          (raw as? JsonPrimitive)?.booleanOrNull != null -> "boolean"
        else -> type
      }
    val value =
      when {
        type == "state" && raw == JsonNull -> defaultValue(property)
        canonical == "boolean" && type == "string" ->
          JsonPrimitive((raw as JsonPrimitive).booleanOrNull!!)
        expected == "string" &&
          raw == JsonNull &&
          property.name in setOf("contentDescription", "actionLabel") -> JsonPrimitive("")
        else -> raw
      }
    // The codec unwraps its own literal spelling (`bool`, `int`, `number`, `string`), which
    // differs from the JSON Schema type for booleans and integers; a state wrapper stays as it is.
    val written = if (canonical == expected) property.codec.literalType else canonical
    properties[property.name] =
      JsonObject(wrapper + mapOf("type" to JsonPrimitive(written), "value" to (value ?: JsonNull)))
  }
  return CanvasNodeScope(
    node = scope.node.copy(properties = JsonObject(properties)),
    modifier = scope.modifier,
    mode = scope.mode,
    renderSlot = { name, modifier -> scope.Slot(name, modifier) },
    renderItems = { name, render -> scope.Items(name, render) },
    countItems = scope::itemCount,
    renderItem = { name, index, modifier -> scope.Item(name, index, modifier) },
    dispatchEvent = scope::dispatch,
    updateState = { variable, value ->
      scope.node.properties.entries
        .firstOrNull { (_, value) ->
          ((value as? JsonObject)?.get("variable") as? JsonPrimitive)?.contentOrNull == variable
        }
        ?.let { scope.updateBoundState(it.key, value) }
    },
    recordText = scope::recordTextLayout,
    registerScrolling = scope::registerScrolling,
    unrolledHorizontally = scope.unrolledHorizontally,
  )
}

private fun <P, T> defaultValue(property: AdapterProperty<P, T>) =
  property.codec.encode(property.default)
