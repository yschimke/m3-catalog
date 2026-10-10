package ee.schimke.m3catalog.typed

import ee.schimke.composeai.discovery.ComponentRecordFile
import ee.schimke.composeai.discovery.UiBuilderCatalogFile
import ee.schimke.composeai.discovery.writeTo
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlinx.serialization.json.Json

class TypedM3CatalogTest {
  @Test
  fun `compiled wrapper signatures generate a matching portable pair`() {
    val discovered = discoverTypedM3Controls()
    val typed = TypedM3Catalog(discovered)
    typed.adapters.forEach { assertEquals(emptyList(), it.validateAgainst(it.component)) }
    val pair = typed.generate()
    assertTrue(pair.catalog.diagnostics.isEmpty())
    assertEquals(
      setOf("m3/checkbox", "m3/switch", "m3/radio-button"),
      pair.catalog.statusSemantics.components.keys,
    )
    assertEquals(
      "checked:boolean",
      pair.record.components
        .single { it.symbol.name == "M3Checkbox" }
        .builder!!
        .stateCallbacks
        .single()
        .value,
    )
    assertEquals(
      "checked:boolean",
      pair.record.components
        .single { it.symbol.name == "M3Switch" }
        .builder!!
        .stateCallbacks
        .single()
        .value,
    )

    val directory = Files.createTempDirectory("typed-m3-catalog-test")
    try {
      val output = directory.resolve("pair")
      pair.writeTo(output)
      assertEquals(
        pair.record,
        Json.decodeFromString(
          ComponentRecordFile.serializer(),
          Files.readString(output.resolve("components.json")),
        ),
      )
      assertEquals(
        pair.catalog,
        Json.decodeFromString(
          UiBuilderCatalogFile.serializer(),
          Files.readString(output.resolve("ui-builder.json")),
        ),
      )
      assertFailsWith<IllegalArgumentException> { pair.writeTo(output) }
    } finally {
      directory.toFile().deleteRecursively()
    }
  }

  @Test
  fun `a stale or changed compiled property is refused before publication`() {
    val discovered = discoverTypedM3Controls()
    val typed = TypedM3Catalog(discovered)
    val changed =
      typed.checkbox.component
        .newBuilder()
        .apply {
          parameters = parameters.map { parameter ->
            if (parameter.name == "checked")
              parameter.newBuilder().apply { typeFqn = "kotlin.String" }.build()
            else parameter
          }
        }
        .build()
    assertTrue(typed.checkbox.validateAgainst(changed).isNotEmpty())
  }
}
