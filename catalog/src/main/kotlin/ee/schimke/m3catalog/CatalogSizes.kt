@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import ee.schimke.composeai.overrides.previewOverrideString

/**
 * The expressive **size** and **shape** axes, resolved from preview knobs.
 *
 * M3 Expressive gives every button-family component five sizes (extra small through extra large)
 * and two shapes (round and square). The Figma kit ships that as one component set with variant
 * properties, and design-parity reads the reference the same way: it walks the set's variants and
 * looks for a candidate render of each. A catalog that renders one cell reports "reference variant
 * has no candidate render" for every other — which is what the first parity run said, 37 times.
 *
 * The matrix is expressed as **knobs plus `@OverrideVariant`**, not as one `@Composable` per cell.
 * A sticker reads [catalogButtonSize] and [catalogButtonShape] once; each stacked annotation bakes
 * an extra capture with those knobs seeded. Fifty cells cost fifty annotation lines instead of
 * fifty near-identical functions, the default render stays byte-identical (an unseeded knob returns
 * its author default), and adding a size later is one line in one place rather than a new function
 * per emphasis.
 */
enum class CatalogSize(
  val knob: String,
  val containerHeight: Dp,
  val contentPadding: PaddingValues,
  val iconSize: Dp,
) {
  ExtraSmall(
    "xs",
    ButtonDefaults.ExtraSmallContainerHeight,
    ButtonDefaults.ExtraSmallContentPadding,
    ButtonDefaults.ExtraSmallIconSize,
  ),
  Small(
    "s",
    ButtonDefaults.MinHeight,
    ButtonDefaults.SmallContentPadding,
    ButtonDefaults.SmallIconSize,
  ),
  Medium(
    "m",
    ButtonDefaults.MediumContainerHeight,
    ButtonDefaults.MediumContentPadding,
    ButtonDefaults.MediumIconSize,
  ),
  Large(
    "l",
    ButtonDefaults.LargeContainerHeight,
    ButtonDefaults.LargeContentPadding,
    ButtonDefaults.LargeIconSize,
  ),
  ExtraLarge(
    "xl",
    ButtonDefaults.ExtraLargeContainerHeight,
    ButtonDefaults.ExtraLargeContentPadding,
    ButtonDefaults.ExtraLargeIconSize,
  );

  /**
   * The label style the size carries. M3 scales type with the container rather than holding one
   * label size across a 32dp-to-136dp range, so a size variant that changed only the box would
   * render a correct height around visibly wrong text.
   */
  val labelStyle: TextStyle
    @Composable
    get() =
      when (this) {
        ExtraSmall,
        Small -> MaterialTheme.typography.labelLarge
        Medium -> MaterialTheme.typography.titleMedium
        Large -> MaterialTheme.typography.headlineSmall
        ExtraLarge -> MaterialTheme.typography.headlineLarge
      }

  companion object {
    /** Resolves a knob value to its size, falling back to the M3 default rather than throwing. */
    fun of(knob: String): CatalogSize = entries.firstOrNull { it.knob == knob } ?: Small
  }
}

/**
 * The size this sticker should render at: the `size` knob, defaulting to [CatalogSize.Small] — the
 * size a bare `Button(...)` is, so an unseeded render is pixel-identical to the pre-matrix sticker.
 */
@Composable
fun catalogButtonSize(): CatalogSize = CatalogSize.of(previewOverrideString("size", "s"))

/**
 * The shape this sticker should render in: the `shape` knob, `round` (the default) or `square`.
 *
 * Both come from `ButtonDefaults` rather than from hand-written corner radii. The library is the
 * authority for the code side of a code-led catalog, and design-parity is what reports a divergence
 * from the kit — inventing radii here would launder a guess into something that looks measured.
 */
@Composable
fun catalogButtonShape(): Shape =
  if (previewOverrideString("shape", "round") == "square") ButtonDefaults.squareShape
  else ButtonDefaults.shape
