package ee.schimke.m3catalog.typed

import ee.schimke.composeai.discovery.AdapterStateChange
import ee.schimke.composeai.uibuilder.renderer.sdk.TypedCanvasNodeScope
import kotlinx.serialization.json.JsonPrimitive

/** Search retains its established event name on the same checked state/property handle. */
internal fun <P, T> TypedCanvasNodeScope<P>.m3StateCallback(
  change: AdapterStateChange<P, T>,
  event: String,
): (T) -> Unit {
  require(definition.owns(change)) { "state callback belongs to another adapter" }
  return { value ->
    val encoded = change.property.codec.encode(value)
    require(encoded is JsonPrimitive) { "state callback requires a scalar" }
    canvas.changeBoundState(change.property.name, encoded.content, event)
  }
}
