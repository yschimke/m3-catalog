package ee.schimke.m3catalog

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.Dp
import ee.schimke.composeai.overrides.LocalPreviewOverrideHost
import ee.schimke.composeai.overrides.PreviewOverrideHost
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * The colour-role grid answers to the **selected theme**, like every other sticker.
 *
 * The grid is the one sticker whose own knob also names a scheme, and reading that knob directly
 * made it the one sticker a `@ThemeCatalog` provider could not re-skin: picking `Dark High
 * Contrast` in the viewer's Theme select left the swatches painting baseline light, on the sheet
 * whose entire subject is colour ([issue #202]). The resolver has to satisfy both selectors at
 * once, which is what these three cases pin — and the third is why this is not simply "prefer the
 * theme": the five published matrix cells name their mode on purpose and must keep it.
 *
 * [issue #202]: https://github.com/yschimke/m3-catalog/issues/202
 */
@OptIn(ExperimentalTestApi::class)
class CatalogSchemeSelectionTest {

  /** A [PreviewOverrideHost] answering the string knobs it was seeded with, defaulting the rest. */
  private class SeededHost(private val seeds: Map<String, String>) : PreviewOverrideHost {
    @Composable
    override fun string(key: String, default: String, index: Int?): String = seeds[key] ?: default

    @Composable override fun int(key: String, default: Int, index: Int?): Int = default

    @Composable override fun float(key: String, default: Float, index: Int?): Float = default

    @Composable override fun boolean(key: String, default: Boolean, index: Int?): Boolean = default

    @Composable override fun color(key: String, default: Color, index: Int?): Color = default

    @Composable override fun dp(key: String, default: Dp, index: Int?): Dp = default
  }

  /** [CatalogSchemeChoice.currentScheme] under an optional theme provider and an optional seed. */
  private fun resolve(theme: ColorScheme?, seed: String? = null): ColorScheme {
    var resolved: ColorScheme? = null
    runComposeUiTest {
      setContent {
        CompositionLocalProvider(
          LocalPreviewOverrideHost provides
            SeededHost(seed?.let { mapOf("theme" to it) }.orEmpty()),
          LocalCatalogScheme provides theme,
        ) {
          resolved = CatalogSchemeChoice.currentScheme()
        }
      }
    }
    return resolved!!
  }

  @Test
  fun `an unseeded grid paints the theme the viewer selected`() {
    assertEquals(BaselineDarkHighContrast, resolve(theme = BaselineDarkHighContrast))
  }

  @Test
  fun `with no theme selected the grid keeps the axis default`() {
    assertEquals(BaselineLight, resolve(theme = null))
  }

  @Test
  fun `a cell that names its mode keeps it under any theme`() {
    assertEquals(
      BaselineLightHighContrast,
      resolve(theme = BaselineDark, seed = "light-high-contrast"),
      "the published `light-high-contrast` cell renders its own mode, not the selected theme",
    )
  }
}
