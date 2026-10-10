package ee.schimke.m3catalog.typed

import ee.schimke.composeai.discovery.AdapterValueCodec
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.intOrNull

object M3ValueCodecs {
  val Float: AdapterValueCodec<Float> =
    object : AdapterValueCodec<Float> {
      override val jsonType = "number"
      override val kotlinType = "kotlin.Float"
      override val stateType = "number"

      override fun encode(value: Float): JsonElement {
        require(value.isFinite()) { "expected a finite number" }
        return JsonPrimitive(value)
      }

      override fun decode(value: JsonElement): Float {
        require(value is JsonPrimitive && !value.isString) { "expected a JSON number" }
        return requireNotNull(value.floatOrNull?.takeIf { it.isFinite() }) {
          "expected a finite number"
        }
      }
    }
  val Int: AdapterValueCodec<Int> =
    object : AdapterValueCodec<Int> {
      override val jsonType = "integer"
      override val kotlinType = "kotlin.Int"
      override val stateType = "number"

      override fun encode(value: Int): JsonElement = JsonPrimitive(value)

      override fun decode(value: JsonElement): Int {
        require(value is JsonPrimitive && !value.isString) { "expected a JSON integer" }
        return requireNotNull(value.intOrNull) { "expected a JSON integer" }
      }
    }
}
