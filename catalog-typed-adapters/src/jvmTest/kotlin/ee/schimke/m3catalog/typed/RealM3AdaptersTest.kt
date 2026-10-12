package ee.schimke.m3catalog.typed

import ee.schimke.composeai.discovery.ExportProjection
import ee.schimke.composeai.discovery.TypedComponentAdapter
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * The real-record definitions against the library they export to: discovery reads the composables
 * and factories they name, each definition validates against its record, and the properties the
 * committed designs set lower onto the real call.
 */
class RealM3AdaptersTest {
  private val records = RealM3Records.discover().components.associateBy { it.symbol.name }

  private val adapters: Map<String, TypedComponentAdapter<*>> =
    RealM3Records.adapters().associateBy { it.id }

  private fun projection(id: String) = adapters.getValue(id).projection!!

  private fun wrapped(type: String, value: Any): JsonElement = buildJsonObject {
    put("type", type)
    when (value) {
      is String -> put("value", value)
      is Number -> put("value", value)
      is Boolean -> put("value", value)
    }
  }

  private fun node(id: String, vararg properties: Pair<String, JsonElement>) =
    assertIs<ExportProjection.Lowered.Node>(projection(id).lower(properties.toMap()))

  @Test
  fun `every real composable is discovered as a library record`() {
    assertEquals(RealM3Records.callables.map { it.name }.toSet(), records.keys)
    assertTrue(records.values.all { it.signatureKnown })
    assertEquals(
      "kotlin.String",
      records.getValue("Text").parameters.single { it.name == "text" }.typeFqn,
    )
    val scaffold = records.getValue("NavigationSuiteScaffold").parameters.map { it.name }
    assertTrue("navigationItems" in scaffold && "primaryActionContent" in scaffold, "$scaffold")
    // The card record is the plain overload; the clickable one is published beside it.
    assertTrue(records.getValue("Card").parameters.none { it.name == "onClick" })
    assertTrue(
      records.getValue("Card").overloads.any { o -> o.parameters.any { it.name == "onClick" } }
    )
  }

  @Test
  fun `every definition validates against the record discovery reads`() {
    assertEquals(
      setOf(
        "m3/text",
        "m3/icon",
        "m3/card",
        "m3/navigation-suite-item",
        "m3/navigation-suite-scaffold",
      ),
      adapters.keys,
    )
    for (adapter in adapters.values) {
      assertEquals(emptyList(), adapter.validateAgainst(adapter.component), adapter.id)
    }
  }

  @Test
  fun `a design's text writes its typography role`() {
    val text =
      node(
        "m3/text",
        "text" to wrapped("string", "Your app"),
        "style" to wrapped("enum", "headlineMedium"),
      )
    assertEquals(setOf("text", "style"), text.arguments.keys)
    assertContains(text.arguments.getValue("style").toString(), "headlineMedium")
  }

  @Test
  fun `a card's style picks the real card`() {
    assertTrue(node("m3/card").record.endsWith("CardKt.Card"))
    val elevated = node("m3/card", "style" to wrapped("enum", "elevated"))
    assertTrue(elevated.record.endsWith("CardKt.ElevatedCard"), elevated.record)
    val flat = node("m3/card", "elevationDp" to wrapped("float", 0.0))
    assertEquals(setOf("elevation"), flat.arguments.keys)
  }

  @Test
  fun `what no parameter carries is left out of the projection, so the builder refuses it`() {
    // A host refuses a node that sets a property outside `ExportProjection.properties`, by name.
    for ((id, property) in
      listOf(
        "m3/card" to "shape",
        "m3/card" to "stableKey",
        "m3/text" to "alignment",
        "m3/icon" to "iconName",
      )) {
      assertTrue(property !in projection(id).properties, "$id.$property")
      assertTrue(adapters.getValue(id).properties.any { it.name == property }, "$id.$property")
    }
  }
}
