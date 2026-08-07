@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.ToggleButtonShapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
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

// --- Icon buttons ------------------------------------------------------------------------------
//
// Icon buttons carry a THIRD axis the plain buttons don't: width (narrow / uniform / wide), which
// the kit lists alongside colour, size and shape. Thirty cells per emphasis rather than ten.
//
// Their shapes are per-size constants (`IconButtonDefaults.smallRoundShape`, …) rather than one
// shape for the family, because an icon button's corner radius tracks its container — so unlike
// `catalogButtonShape`, the resolver needs the size to answer.

/** Container size for [size] at the resolved width option. */
@Composable
fun catalogIconContainerSize(size: CatalogSize): DpSize {
  val width = catalogIconWidthOption()
  return when (size) {
    CatalogSize.ExtraSmall -> IconButtonDefaults.extraSmallContainerSize(width)
    CatalogSize.Small -> IconButtonDefaults.smallContainerSize(width)
    CatalogSize.Medium -> IconButtonDefaults.mediumContainerSize(width)
    CatalogSize.Large -> IconButtonDefaults.largeContainerSize(width)
    CatalogSize.ExtraLarge -> IconButtonDefaults.extraLargeContainerSize(width)
  }
}

/** The `width` knob: `narrow`, `uniform` (the default) or `wide`. */
@Composable
fun catalogIconWidthOption(): IconButtonDefaults.IconButtonWidthOption =
  when (previewOverrideString("width", "uniform")) {
    "narrow" -> IconButtonDefaults.IconButtonWidthOption.Narrow
    "wide" -> IconButtonDefaults.IconButtonWidthOption.Wide
    else -> IconButtonDefaults.IconButtonWidthOption.Uniform
  }

/** Per-size round or square shape, from the `shape` knob. */
@Composable
fun catalogIconShape(size: CatalogSize): Shape {
  val square = previewOverrideString("shape", "round") == "square"
  return when (size) {
    CatalogSize.ExtraSmall ->
      if (square) IconButtonDefaults.extraSmallSquareShape
      else IconButtonDefaults.extraSmallRoundShape
    CatalogSize.Small ->
      if (square) IconButtonDefaults.smallSquareShape else IconButtonDefaults.smallRoundShape
    CatalogSize.Medium ->
      if (square) IconButtonDefaults.mediumSquareShape else IconButtonDefaults.mediumRoundShape
    CatalogSize.Large ->
      if (square) IconButtonDefaults.largeSquareShape else IconButtonDefaults.largeRoundShape
    CatalogSize.ExtraLarge ->
      if (square) IconButtonDefaults.extraLargeSquareShape
      else IconButtonDefaults.extraLargeRoundShape
  }
}

/** The glyph size [size] carries — an icon button scales its glyph with its container. */
val CatalogSize.iconButtonIconSize: Dp
  @Composable
  get() =
    when (this) {
      CatalogSize.ExtraSmall -> IconButtonDefaults.extraSmallIconSize
      CatalogSize.Small -> IconButtonDefaults.smallIconSize
      CatalogSize.Medium -> IconButtonDefaults.mediumIconSize
      CatalogSize.Large -> IconButtonDefaults.largeIconSize
      CatalogSize.ExtraLarge -> IconButtonDefaults.extraLargeIconSize
    }

// --- Toggle buttons ----------------------------------------------------------------------------

/**
 * A toggle button's **shape set** — resting, pressed and checked — for [size] and the `shape` knob.
 *
 * `ToggleButton` takes a whole `ToggleButtonShapes` rather than one shape, because M3 gives the
 * checked and pressed states their own corners: a toggle button shape-shifts as it turns on and as
 * it is held. Handing it a single shape is not possible, and picking only the resting one would
 * lose the state morph the component exists to show.
 *
 * The round side defers to `shapesFor(containerHeight)`, the library's own size-to-shapes mapping,
 * rather than a `when` over the five sizes here — that keeps one source of truth for which corner
 * belongs to which height. The square side is assembled by hand because there is no
 * `squareShapesFor`, pairing the per-size square and checked-square constants with the shared
 * pressed shape.
 */
@Composable
fun catalogToggleButtonShapes(size: CatalogSize): ToggleButtonShapes {
  if (previewOverrideString("shape", "round") != "square") {
    return ToggleButtonDefaults.shapesFor(size.containerHeight)
  }
  val (square, checkedSquare) =
    when (size) {
      CatalogSize.ExtraSmall ->
        ToggleButtonDefaults.extraSmallSquareShape to
          ToggleButtonDefaults.extraSmallCheckedSquareShape
      CatalogSize.Small -> ToggleButtonDefaults.squareShape to ToggleButtonDefaults.checkedShape
      CatalogSize.Medium ->
        ToggleButtonDefaults.mediumSquareShape to ToggleButtonDefaults.mediumCheckedSquareShape
      CatalogSize.Large ->
        ToggleButtonDefaults.largeSquareShape to ToggleButtonDefaults.largeCheckedSquareShape
      CatalogSize.ExtraLarge ->
        ToggleButtonDefaults.extraLargeSquareShape to
          ToggleButtonDefaults.extraLargeCheckedSquareShape
    }
  return ToggleButtonDefaults.shapes(
    shape = square,
    pressedShape = ToggleButtonDefaults.pressedShape,
    checkedShape = checkedSquare,
  )
}

// --- Split button ------------------------------------------------------------------------------
//
// No shape axis: the kit models a split button's corners as inner/outer corner sizes the component
// derives, not as a round/square variant property. A `shape` knob here would invent an axis the kit
// does not document.

/** Leading-half content padding for [CatalogSize]. */
val CatalogSize.splitLeadingContentPadding: PaddingValues
  get() =
    when (this) {
      CatalogSize.ExtraSmall -> SplitButtonDefaults.ExtraSmallLeadingButtonContentPadding
      CatalogSize.Small -> SplitButtonDefaults.SmallLeadingButtonContentPadding
      CatalogSize.Medium -> SplitButtonDefaults.MediumLeadingButtonContentPadding
      CatalogSize.Large -> SplitButtonDefaults.LargeLeadingButtonContentPadding
      CatalogSize.ExtraLarge -> SplitButtonDefaults.ExtraLargeLeadingButtonContentPadding
    }

/** Trailing-half content padding for [CatalogSize]. */
val CatalogSize.splitTrailingContentPadding: PaddingValues
  get() =
    when (this) {
      CatalogSize.ExtraSmall -> SplitButtonDefaults.ExtraSmallTrailingButtonContentPadding
      CatalogSize.Small -> SplitButtonDefaults.SmallTrailingButtonContentPadding
      CatalogSize.Medium -> SplitButtonDefaults.MediumTrailingButtonContentPadding
      CatalogSize.Large -> SplitButtonDefaults.LargeTrailingButtonContentPadding
      CatalogSize.ExtraLarge -> SplitButtonDefaults.ExtraLargeTrailingButtonContentPadding
    }

/** Trailing-half glyph size for [CatalogSize]. */
val CatalogSize.splitTrailingIconSize: Dp
  get() =
    when (this) {
      CatalogSize.ExtraSmall -> SplitButtonDefaults.ExtraSmallTrailingButtonIconSize
      CatalogSize.Small -> SplitButtonDefaults.SmallTrailingButtonIconSize
      CatalogSize.Medium -> SplitButtonDefaults.MediumTrailingButtonIconSize
      CatalogSize.Large -> SplitButtonDefaults.LargeTrailingButtonIconSize
      CatalogSize.ExtraLarge -> SplitButtonDefaults.ExtraLargeTrailingButtonIconSize
    }
