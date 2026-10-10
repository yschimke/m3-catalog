package ee.schimke.m3catalog.typed

import ee.schimke.composeai.discovery.ComponentRecordFile
import ee.schimke.composeai.discovery.UiBuilderCatalogFile
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.serialization.json.Json

class GeneratedCatalogTest {
  private val json = Json { ignoreUnknownKeys = true }

  @Test
  fun `published pair preserves every discovered signature and template`() {
    val root = Path.of(System.getProperty("catalogRoot"))
    val output = Path.of(System.getProperty("generatedCatalogRoot"))
    catalogInputs().forEach { input ->
      val original = root.resolve(input.module).resolve("build/compose-previews/components.json")
      val record =
        json.decodeFromString(ComponentRecordFile.serializer(), Files.readString(original))
      val catalog =
        json.decodeFromString(
          UiBuilderCatalogFile.serializer(),
          Files.readString(output.resolve(input.policy.catalogId!!).resolve("ui-builder.json")),
        )
      val pair = output.resolve(catalog.catalog.id)
      assertTrue(record.components.isNotEmpty())
      assertEquals(Files.readString(original), Files.readString(pair.resolve("components.json")))
      assertEquals(
        record,
        json.decodeFromString(
          ComponentRecordFile.serializer(),
          Files.readString(pair.resolve("components.json")),
        ),
      )
      input.policy.templates.forEach {
        assertTrue(Files.isRegularFile(pair.resolve(it.path)), it.path)
      }
    }
  }

  @Test
  fun `missing discovery refuses generation before creating output`() {
    val root = Files.createTempDirectory("missing-discovery")
    val output = root.resolve("output")
    try {
      kotlin.test.assertFailsWith<IllegalStateException> { generateCatalogs(root, output) }
      kotlin.test.assertFalse(Files.exists(output))
    } finally {
      root.toFile().deleteRecursively()
    }
  }
}
