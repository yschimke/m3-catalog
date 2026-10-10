package ee.schimke.m3catalog.typed

import ee.schimke.composeai.discovery.UiBuilderPolicyFile
import ee.schimke.composeai.uibuilder.protocol.PropertyCapabilityV1
import ee.schimke.composeai.uibuilder.protocol.SlotCapabilityV1
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

class M3BuilderPolicyTest {
  private val json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
  }

  @Test
  fun `Kotlin policy preserves the complete production vocabulary`() {
    val production =
      json.decodeFromString(
        UiBuilderPolicyFile.serializer(),
        File(System.getProperty("m3LegacyPolicy")).readText(),
      )
    fun canonical(policy: UiBuilderPolicyFile): JsonElement {
      val normalized =
        policy
          .newBuilder()
          .apply {
            comment = null
            components = components.mapValues { (_, component) ->
              component
                .newBuilder()
                .apply {
                  propertyCapabilities = propertyCapabilities?.map(::property)
                  slotCapabilities = slotCapabilities?.map(::slot)
                }
                .build()
            }
            builtins = builtins.mapValues { (_, builtin) ->
              builtin.newBuilder().apply { properties = properties.map(::property) }.build()
            }
          }
          .build()
      return withoutComments(json.encodeToJsonElement(UiBuilderPolicyFile.serializer(), normalized))
    }
    assertEquals(canonical(production), canonical(m3BuilderPolicy()))
    assertEquals(production.components.keys.toList(), m3BuilderPolicy().components.keys.toList())
    assertEquals(production.builtins.keys.toList(), m3BuilderPolicy().builtins.keys.toList())
  }

  private fun property(value: JsonElement) =
    json.encodeToJsonElement(
      PropertyCapabilityV1.serializer(),
      json.decodeFromJsonElement(PropertyCapabilityV1.serializer(), value),
    )

  private fun slot(value: JsonElement) =
    json.encodeToJsonElement(
      SlotCapabilityV1.serializer(),
      json.decodeFromJsonElement(SlotCapabilityV1.serializer(), value),
    )

  private fun withoutComments(value: JsonElement): JsonElement =
    when (value) {
      is JsonObject ->
        JsonObject(
          value.filterKeys { !it.startsWith("\$comment") }.mapValues { withoutComments(it.value) }
        )
      is JsonArray -> JsonArray(value.map(::withoutComments))
      else -> value
    }
}
