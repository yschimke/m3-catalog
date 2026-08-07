package ee.schimke.m3catalog

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import com.materialkolor.Contrast
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Guards the four generated contrast themes.
 *
 * These are the only schemes in the catalog that are *computed* rather than taken from Compose, so
 * they are the only ones that can silently change under a dependency bump. Two properties matter,
 * and they are asserted separately because they fail for different reasons:
 *
 * 1. **The generator hasn't moved.** A MaterialKolor upgrade that retunes
 *    `material-color-utilities` would re-tint four published themes with no source change — the
 *    delivery branch would show a diff nobody asked for. Pinning the zero-contrast output turns
 *    that into a build failure with a diff you can read.
 * 2. **The tiers actually escalate.** A contrast tier's whole promise is the *relationship* between
 *    a surface and its content, not any particular hex. If medium and high ever stopped separating
 *    `onSurface` from `surface` more than standard does, the sheet would still render — it would
 *    just be documenting accessibility guidance it doesn't follow.
 */
class CatalogSchemesTest {

  private fun hex(color: Color) = "#%06X".format(color.toArgb() and 0xFFFFFF)

  /**
   * How hard a scheme works to hold its content off its surface, aggregated over the three content
   * roles a contrast tier moves.
   *
   * Deliberately not `onSurface` alone: in **dark** mode the medium tier already drives `onSurface`
   * to pure white, so that one role saturates and medium and high measure identical on it. A tier
   * that can't be distinguished on a saturated role is still doing more work on the roles that have
   * headroom, and the aggregate is what sees it.
   */
  private fun contentSeparation(scheme: ColorScheme) =
    abs(scheme.onSurface.luminance() - scheme.surface.luminance()) +
      abs(scheme.onSurfaceVariant.luminance() - scheme.surface.luminance()) +
      abs(scheme.outline.luminance() - scheme.surface.luminance())

  @Test
  fun `the generator's zero-contrast output is pinned`() {
    // Pinned to MaterialKolor 5.0.0. These are NOT the published M3 baseline — see the caveat table
    // in CatalogSchemes.kt. The point is that they don't move without someone noticing.
    val light = baselineScheme(dark = false, Contrast.Default)
    assertEquals("#65558F", hex(light.primary))
    assertEquals("#49454E", hex(light.onSurfaceVariant))
    assertEquals("#CAC4CF", hex(light.outlineVariant))
    assertEquals("#BA1A1A", hex(light.error))

    val dark = baselineScheme(dark = true, Contrast.Default)
    assertEquals("#CFBDFE", hex(dark.primary))
    assertEquals("#141218", hex(dark.surface))
  }

  @Test
  fun `the published standard modes stay Compose's stock schemes, not the generated ones`() {
    // The catalog deliberately does NOT publish the generated zero-contrast pair as its standard
    // modes: those would drift from the kit's own variables (primary #65558F vs #6750A4). This test
    // is what stops a well-meaning "make all six consistent" change from replacing an exact match
    // with an approximation.
    assertEquals("#6750A4", hex(BaselineLight.primary))
    assertTrue(
      hex(BaselineLight.primary) != hex(baselineScheme(dark = false, Contrast.Default).primary),
      "the two are known to differ; if they ever converge, the caveat table in CatalogSchemes.kt " +
        "is stale and should be simplified away",
    )
  }

  @Test
  fun `each light contrast tier separates content from surface more than the one below`() {
    val standard = contentSeparation(BaselineLight)
    val medium = contentSeparation(BaselineLightMediumContrast)
    val high = contentSeparation(BaselineLightHighContrast)
    assertTrue(medium > standard, "medium ($medium) must exceed standard ($standard)")
    assertTrue(high > medium, "high ($high) must exceed medium ($medium)")
  }

  @Test
  fun `each dark contrast tier separates content from surface more than the one below`() {
    val standard = contentSeparation(BaselineDark)
    val medium = contentSeparation(BaselineDarkMediumContrast)
    val high = contentSeparation(BaselineDarkHighContrast)
    assertTrue(medium > standard, "medium ($medium) must exceed standard ($standard)")
    assertTrue(high > medium, "high ($high) must exceed medium ($medium)")
  }

  @Test
  fun `high contrast reaches full separation`() {
    // The high tier is what a user with low vision actually selects; M3 drives its content roles to
    // the palette extremes. Anything short of near-total separation means the tier isn't doing the
    // job its name claims.
    assertEquals("#000000", hex(BaselineLightHighContrast.onSurface))
    assertEquals("#FFFFFF", hex(BaselineDarkHighContrast.onSurface))
  }
}
