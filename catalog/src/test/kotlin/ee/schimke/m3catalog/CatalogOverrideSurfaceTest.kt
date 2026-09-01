package ee.schimke.m3catalog

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Keeps the live catalog's override surface aligned with the Wear catalog's control contract. */
class CatalogOverrideSurfaceTest {

  private val sourceRoot = File("src/main/kotlin/ee/schimke/m3catalog")
  private val sources =
    sourceRoot.walkTopDown().filter { it.extension == "kt" }.joinToString("\n") { it.readText() }

  @Test
  fun publishesEverySupportedScalarControlKind() {
    val requiredCalls =
      setOf(
        "previewOverrideBoolean(",
        "previewOverrideChoice(",
        "previewOverrideDp(",
        "previewOverrideFloat(",
        "previewOverrideInt(",
        "previewOverrideString(",
      )
    val missing = requiredCalls.filterNot(sources::contains)
    assertEquals(emptyList(), missing.sorted(), "The catalog lost a supported override control")
  }

  @Test
  fun numericControlsAreNotHiddenInTextFields() {
    val parsedStringKnobs =
      Regex("""previewOverrideString\([^\n]+\)\.(?:toIntOrNull|toFloatOrNull)\(""")
        .findAll(sources)
        .map { it.value }
        .toList()
    assertEquals(
      emptyList(),
      parsedStringKnobs,
      "Use the typed int, float, or dp override so the preview server renders a numeric control",
    )
  }

  @Test
  fun primaryTextSurfacesPublishEditableCopy() {
    val expected =
      mapOf(
        "sections/Buttons.kt" to "catalogText(\"label\"",
        "sections/Cards.kt" to "catalogText(\"title\"",
        "sections/Chips.kt" to "catalogText(\"label\"",
        "sections/Dialogs.kt" to "catalogText(\"title\"",
        "sections/TextFields.kt" to "catalogText(\"value\"",
      )
    for ((relativePath, call) in expected) {
      val source = File(sourceRoot, relativePath).readText()
      assertTrue(source.contains(call), "$relativePath does not publish $call")
    }
  }
}
