package ee.schimke.m3catalog

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import ee.schimke.composeai.preview.ColorCatalog
import ee.schimke.composeai.preview.ShapeCatalog
import ee.schimke.composeai.preview.TypographyCatalog

/**
 * The catalog's **declared theme objects**, surfaced through the whole-object catalog annotations
 * (`@ColorCatalog` / `@TypographyCatalog` / `@ShapeCatalog` on a whole [ColorScheme] / [Typography]
 * / [Shapes]). Discovery auto-detects each and synthesises a specimen sheet with no `@Preview`
 * needed, and the theme-override surface offers them as the selectable palette / type scale / shape
 * scale.
 *
 * Everything here is the **Material 3 baseline** — the same tokens the Figma kit carries in its
 * `M3.sys` variable collection. Spot-checked against the file's published variables:
 * `M3.sys.light.primary` = `#6750A4`, `on-surface` = `#1D1B20`, `on-surface-variant` = `#49454F`,
 * `outline-variant` = `#CAC4D0`, `surface-dim` = `#DED8E1`, `surface-container-highest` =
 * `#E6E0E9`, `Corner.Extra-large` = `28`. Those are exactly what Compose's [lightColorScheme] and
 * [Shapes] defaults resolve to, so the baseline is expressed as the stock objects rather than as
 * re-typed hex — there is nothing to drift.
 */

/** The Material 3 **baseline light** scheme — the kit's default mode. */
@ColorCatalog(name = "Baseline Light", group = "Scheme")
val BaselineLight: ColorScheme = lightColorScheme()

/** The Material 3 **baseline dark** scheme. */
@ColorCatalog(name = "Baseline Dark", group = "Scheme")
val BaselineDark: ColorScheme = darkColorScheme()

/**
 * The M3 type scale on **static Roboto** — the face the Figma kit's text styles are authored in.
 */
@TypographyCatalog(name = "Roboto", group = "Typeface")
val CatalogTypography: Typography = typographyOn(Roboto)

/** The same scale on **Roboto Flex**, offered as an alternate variable typeface. */
@TypographyCatalog(name = "Roboto Flex", group = "Typeface")
val RobotoFlexTypography: Typography = typographyOn(RobotoFlex)

/**
 * The stock Material 3 shape scale (`extraSmall` 4dp … `extraLarge` 28dp) — matching the kit's
 * `Corner.*` variables.
 */
@ShapeCatalog(name = "M3 shapes", group = "Shape") val CatalogShapes: Shapes = Shapes()

/**
 * Re-points the whole default M3 [Typography] at [family], keeping every metric (size, line height,
 * tracking, weight) exactly as the scale defines it — so only the face changes.
 */
fun typographyOn(family: FontFamily): Typography =
  Typography().let { t ->
    fun TextStyle.on() = copy(fontFamily = family)
    Typography(
      displayLarge = t.displayLarge.on(),
      displayMedium = t.displayMedium.on(),
      displaySmall = t.displaySmall.on(),
      headlineLarge = t.headlineLarge.on(),
      headlineMedium = t.headlineMedium.on(),
      headlineSmall = t.headlineSmall.on(),
      titleLarge = t.titleLarge.on(),
      titleMedium = t.titleMedium.on(),
      titleSmall = t.titleSmall.on(),
      bodyLarge = t.bodyLarge.on(),
      bodyMedium = t.bodyMedium.on(),
      bodySmall = t.bodySmall.on(),
      labelLarge = t.labelLarge.on(),
      labelMedium = t.labelMedium.on(),
      labelSmall = t.labelSmall.on(),
    )
  }
