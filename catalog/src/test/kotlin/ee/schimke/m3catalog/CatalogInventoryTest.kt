package ee.schimke.m3catalog

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.expressiveLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.PreviewWrapperProvider
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant
import ee.schimke.composeai.preview.ThemeCatalog
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Guards the catalog's **inventory invariants** — the ones a compile can't catch and that would
 * otherwise only surface as a wrong or missing sticker at the end of a long CI render.
 *
 * The annotations are the source of truth for the published sheet (`catalog.spec.json` carries only
 * cover-sheet fields), so a duplicated `id`, a `@CatalogVariant` pointing at a parent that no
 * longer exists, or a component with no caption is a real defect in the deliverable — not a style
 * nit.
 *
 * The source tree is read directly rather than through reflection: the annotations are `BINARY`
 * retention and discovery reads them with ClassGraph, so a source scan is both sufficient here and
 * independent of the render pipeline being available.
 */
class CatalogInventoryTest {

  private val sections: List<File> =
    File("src/main/kotlin/ee/schimke/m3catalog/sections")
      .listFiles { f: File -> f.name.endsWith(".kt") }!!
      .sortedBy { it.name }

  private val sources: List<Pair<File, String>> = sections.map { it to it.readText() }

  private fun idsOf(pattern: Regex): List<Pair<String, File>> = sources.flatMap { (file, text) ->
    pattern.findAll(text).map { it.groupValues[1] to file }
  }

  private val componentIds =
    idsOf(Regex("""@CatalogComponent\(\s*id = "([^"]+)"""", RegexOption.DOT_MATCHES_ALL))
  private val variantParents =
    idsOf(Regex("""@CatalogVariant\(\s*of = "([^"]+)"""", RegexOption.DOT_MATCHES_ALL))

  @Test
  fun `every section file declares its group and section`() {
    for ((file, text) in sources) {
      assertTrue(
        text.startsWith("@file:CatalogGroup(name = ") && text.contains(", section = \""),
        "${file.name} must open with @file:CatalogGroup(name = …, section = …) so its stickers " +
          "land in a named group under a named tab",
      )
    }
  }

  @Test
  fun `component ids are unique`() {
    val byId = componentIds.groupBy({ it.first }, { it.second.name })
    val duplicated = byId.filterValues { it.size > 1 }
    assertEquals(
      emptyMap(),
      duplicated,
      "a componentId is the join key between the annotation, the published sticker URL and any " +
        "@CatalogVariant(of = …) — duplicates collapse two components onto one card",
    )
  }

  @Test
  fun `every variant names a component that exists`() {
    val known = componentIds.map { it.first }.toSet()
    val orphans =
      variantParents.filter { it.first !in known }.map { "${it.first} (${it.second.name})" }
    assertEquals(
      emptyList(),
      orphans,
      "a @CatalogVariant whose `of` matches no @CatalogComponent id is dropped from the sheet " +
        "silently — the render succeeds and the sticker simply never appears",
    )
  }

  @Test
  fun `every component carries a caption`() {
    val uncaptioned = sources.flatMap { (file, text) ->
      Regex("""@CatalogComponent\((.*?)\)\s*\n""", RegexOption.DOT_MATCHES_ALL)
        .findAll(text)
        // `caption\s*=`, not `"caption = "`: ktfmt wraps a long caption onto a continuation line
        // (`caption =\n    "…"`), and matching the literal with its trailing space failed every
        // component whose caption grew past the column limit — a green-to-red flip caused by
        // formatting, with nothing wrong in the catalog.
        .filterNot { Regex("""caption\s*=""").containsMatchIn(it.groupValues[1]) }
        .map { "${file.name}: ${it.groupValues[1].trim()}" }
    }
    assertEquals(
      emptyList(),
      uncaptioned,
      "a component with no caption publishes as a bare picture — the caption is the one line of " +
        "guidance the sticker sheet carries",
    )
  }

  @Test
  fun `every component maps to Figma`() {
    val silent = sources.flatMap { (file, text) ->
      Regex("""@CatalogComponent\((.*?)\)\s*\n""", RegexOption.DOT_MATCHES_ALL)
        .findAll(text)
        .filterNot { annotation ->
          val args = annotation.groupValues[1]
          Regex("""reference\s*=""").containsMatchIn(args)
        }
        .map { "${file.name}: ${it.groupValues[1].trim()}" }
    }
    assertEquals(
      emptyList(),
      silent,
      "this catalog reproduces the Figma kit: a component without an exact Figma reference does " +
        "not belong in its published component inventory",
    )
  }

  @Test
  fun `the declared hero resolves to a component`() {
    val spec = File("../catalog.spec.json").readText()
    val hero = Regex(""""hero"\s*:\s*"([^"]+)"""").find(spec)!!.groupValues[1]
    assertTrue(
      hero in componentIds.map { it.first }.toSet(),
      "display.hero ($hero) must resolve, else the preview server silently falls back to its own " +
        "heuristic — which picks a lone filled button for a component-library catalog",
    )
  }

  @Test
  fun `the baseline scheme matches the kit's published Figma variables`() {
    // Spot-check against the `M3.sys.light.*` variables read from the Figma kit itself. Compose's
    // `lightColorScheme()` defaults ARE the Material 3 baseline, which is why the catalog expresses
    // the baseline as the stock objects instead of re-typing hex — and this is the test that stops
    // someone "helpfully" re-typing them anyway, or swapping the default for a seeded scheme.
    fun hex(color: Color) = "#%06X".format(color.toArgb() and 0xFFFFFF)
    assertEquals("#6750A4", hex(BaselineLight.primary), "M3.sys.light.primary")
    assertEquals("#1D1B20", hex(BaselineLight.onSurface), "M3.sys.light.on-surface")
    assertEquals("#49454F", hex(BaselineLight.onSurfaceVariant), "M3.sys.light.on-surface-variant")
    assertEquals("#CAC4D0", hex(BaselineLight.outlineVariant), "M3.sys.light.outline-variant")
    assertEquals("#DED8E1", hex(BaselineLight.surfaceDim), "M3.sys.light.surface-dim")
    assertEquals(
      "#E6E0E9",
      hex(BaselineLight.surfaceContainerHighest),
      "M3.sys.light.surface-container-highest",
    )
    assertEquals(lightColorScheme().toString(), BaselineLight.toString())
    assertEquals(darkColorScheme().toString(), BaselineDark.toString())
  }

  @OptIn(androidx.compose.material3.ExperimentalMaterial3ExpressiveApi::class)
  @Test
  fun `expressive schemes remain the stock Compose schemes`() {
    assertEquals(expressiveLightColorScheme().toString(), ExpressiveLight.toString())
    assertEquals(darkColorScheme().toString(), ExpressiveDark.toString())
    assertTrue(
      ExpressiveLight.onPrimaryContainer != BaselineLight.onPrimaryContainer,
      "the expressive light provider must not silently collapse to the baseline scheme",
    )
  }

  @Test
  fun `declared themes are wrapper providers`() {
    // `@ThemeCatalog` is BINARY retention — discovery reads it off the class file with ClassGraph,
    // so it is deliberately NOT reflectable at runtime. The annotation's presence is checked in the
    // source, and what reflection CAN prove is the half that would break the render: the provider
    // must implement `PreviewWrapperProvider`, because that is how the renderer invokes it.
    val providers =
      listOf(
        BaselineLightTheme(),
        BaselineDarkTheme(),
        ExpressiveLightTheme(),
        ExpressiveDarkTheme(),
        LightMediumContrastTheme(),
        LightHighContrastTheme(),
        DarkMediumContrastTheme(),
        DarkHighContrastTheme(),
      )
    for (provider in providers) {
      assertTrue(
        provider is PreviewWrapperProvider,
        "${provider::class.simpleName} must implement PreviewWrapperProvider",
      )
    }
    val themes = File("src/main/kotlin/ee/schimke/m3catalog/CatalogThemes.kt").readText()
    assertEquals(
      providers.size,
      Regex("""@ThemeCatalog\(""").findAll(themes).count(),
      "every declared theme provider must keep its @ThemeCatalog annotation, else it stops being " +
        "offered in the preview server's theme select and its specimen sheet stops being generated",
    )
  }

  @Suppress("unused")
  private fun annotationsAreOnTheClasspath(): List<Any> =
    listOf(CatalogComponent::class, CatalogVariant::class, CatalogGroup::class, ThemeCatalog::class)
}
