package ee.schimke.m3catalog.typed

import ee.schimke.composeai.discovery.ComponentRecordFile
import ee.schimke.composeai.uibuilder.protocol.ExportCapabilitiesV1
import ee.schimke.composeai.uibuilder.service.PublishedUiBuilderCatalog
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive

class PublishedM3CatalogTest {
  @Test
  fun `published reader loads the pilot without application classes`() {
    val directory = Path.of(System.getProperty("typedCatalogSmokeDirectory"))
    val records =
      Json.decodeFromString(
        ComponentRecordFile.serializer(),
        Files.readString(directory.resolve("components.json")),
      )
    assertEquals(3, records.components.size)
    for (record in records.components) {
      assertNull(
        javaClass.classLoader.getResource(record.symbol.jvmOwner.replace('.', '/') + ".class")
      )
    }
    val result =
      assertIs<PublishedUiBuilderCatalog.Result.Composed>(
        PublishedUiBuilderCatalog.compose(
          Files.readString(directory.resolve("ui-builder.json")),
          records,
          ExportCapabilitiesV1.Builder().build(),
        )
      )
    assertEquals(
      setOf("m3/checkbox", "m3/switch", "m3/radio-button"),
      result.catalog.components.map { it.componentId }.toSet(),
    )
    for (component in result.catalog.components) {
      assertEquals(component.componentId, component.wasm.canvas)
      // Standard M3 ids are recognized by the host's existing native implementation. That is
      // separate from the pilot's executable Wasm adapter and does not prove it is installed.
      assertEquals(JsonPrimitive(true), component.wasm.platformSupported)
      assertTrue(result.records.containsKey(component.componentId))
    }
  }
}
