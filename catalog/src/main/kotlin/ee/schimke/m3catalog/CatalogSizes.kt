@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconButtonShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.ToggleButtonShapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.overrides.previewOverrideBoolean

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
  override val knob: String,
  val containerHeight: Dp,
  val contentPadding: PaddingValues,
  val iconSize: Dp,
  val iconSpacing: Dp,
) : CatalogKnob {
  ExtraSmall(
    "xs",
    ButtonDefaults.ExtraSmallContainerHeight,
    ButtonDefaults.ExtraSmallContentPadding,
    ButtonDefaults.ExtraSmallIconSize,
    ButtonDefaults.ExtraSmallIconSpacing,
  ),
  Small(
    "s",
    40.dp,
    ButtonDefaults.SmallContentPadding,
    ButtonDefaults.SmallIconSize,
    ButtonDefaults.IconSpacing,
  ),
  Medium(
    "m",
    ButtonDefaults.MediumContainerHeight,
    ButtonDefaults.MediumContentPadding,
    ButtonDefaults.MediumIconSize,
    ButtonDefaults.MediumIconSpacing,
  ),
  Large(
    "l",
    ButtonDefaults.LargeContainerHeight,
    ButtonDefaults.LargeContentPadding,
    ButtonDefaults.LargeIconSize,
    ButtonDefaults.LargeIconSpacing,
  ),
  ExtraLarge(
    "xl",
    ButtonDefaults.ExtraLargeContainerHeight,
    ButtonDefaults.ExtraLargeContentPadding,
    ButtonDefaults.ExtraLargeIconSize,
    ButtonDefaults.ExtraLargeIconSpacing,
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
    /**
     * The `size` axis, defaulting to [Small] — the size a bare `Button(...)` is, so an unseeded
     * render is pixel-identical to the pre-matrix sticker.
     */
    val Axis = CatalogKnobAxis("size", entries, Small)

    /**
     * The same axis as the **slider** family carries it, defaulting to [ExtraSmall].
     *
     * One axis, two defaults, for the same reason [catalogToggleSelected] takes one: the default is
     * a property of the component, not of the axis. The kit's `Standard slider` and `Range slider`
     * sets both draw `Size=XSmall` as their first variant, and a bare `Slider(...)` is that size —
     * a 16dp track under a 44dp handle — so an unseeded slider render stays exactly what it
     * published before this axis arrived.
     */
    val SliderAxis = CatalogKnobAxis("size", entries, ExtraSmall)
  }
}

/** The size this sticker should render at. See [CatalogSize.Axis]. */
@Composable fun catalogButtonSize(): CatalogSize = CatalogSize.Axis.current()

/**
 * The shape this sticker should render in: the `shape` knob, `round` (the default) or `square`.
 *
 * The round default is a full pill at every size, as the kit's measured button frames specify.
 * Square variants continue to use the library's expressive shape token.
 */
@Composable
fun catalogButtonShape(): Shape =
  when (CatalogShape.Axis.current()) {
    CatalogShape.Round -> RoundedCornerShape(percent = 50)
    CatalogShape.Square -> ButtonDefaults.squareShape
  }

/** Resting and pressed shapes for an expressive button at the selected size and shape. */
@Composable
fun catalogButtonShapes(size: CatalogSize): ButtonShapes =
  ButtonDefaults.shapesFor(size.containerHeight).copy(shape = catalogButtonShape())

/**
 * Whether this sticker renders enabled: the [CatalogState] axis.
 *
 * A knob rather than a composable per style: `enabled` is a parameter on every button-family
 * component, so the matrix already in use for size and shape extends to it without a twelvefold
 * copy of the body.
 */
@Composable fun catalogEnabled(): Boolean = CatalogState.Axis.current() == CatalogState.Enabled

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
  val width = catalogIconWidth().option
  return when (size) {
    CatalogSize.ExtraSmall -> IconButtonDefaults.extraSmallContainerSize(width)
    CatalogSize.Small -> IconButtonDefaults.smallContainerSize(width)
    CatalogSize.Medium -> IconButtonDefaults.mediumContainerSize(width)
    CatalogSize.Large -> IconButtonDefaults.largeContainerSize(width)
    CatalogSize.ExtraLarge -> IconButtonDefaults.extraLargeContainerSize(width)
  }
}

/** The width this sticker should render at. See [CatalogIconWidth.Axis]. */
@Composable fun catalogIconWidth(): CatalogIconWidth = CatalogIconWidth.Axis.current()

/**
 * The library's width option for this axis value.
 *
 * A property on the enum rather than a `when` at the call site, so the axis and the option it maps
 * to cannot fall out of step — adding a width to [CatalogIconWidth] fails to compile here until it
 * names the option it means.
 */
val CatalogIconWidth.option: IconButtonDefaults.IconButtonWidthOption
  get() =
    when (this) {
      CatalogIconWidth.Narrow -> IconButtonDefaults.IconButtonWidthOption.Narrow
      CatalogIconWidth.Uniform -> IconButtonDefaults.IconButtonWidthOption.Uniform
      CatalogIconWidth.Wide -> IconButtonDefaults.IconButtonWidthOption.Wide
    }

/** Per-size round or square shape, from the `shape` knob. */
@Composable
fun catalogIconShape(size: CatalogSize): Shape {
  val square = CatalogShape.Axis.current() == CatalogShape.Square
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

/** Resting and pressed shapes for an expressive icon button at [size]. */
@Composable
fun catalogIconShapes(size: CatalogSize): IconButtonShapes =
  IconButtonDefaults.shapes(
    shape = catalogIconShape(size),
    pressedShape =
      when (size) {
        CatalogSize.ExtraSmall -> IconButtonDefaults.extraSmallPressedShape
        CatalogSize.Small -> IconButtonDefaults.smallPressedShape
        CatalogSize.Medium -> IconButtonDefaults.mediumPressedShape
        CatalogSize.Large -> IconButtonDefaults.largePressedShape
        CatalogSize.ExtraLarge -> IconButtonDefaults.extraLargePressedShape
      },
  )

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
  if (CatalogShape.Axis.current() != CatalogShape.Square) {
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
    // Per-size, from the same `shapesFor` the round branch defers to. `ToggleButtonDefaults`
    // exposes `extraSmallPressedShape` … `extraLargePressedShape`, and the bare `pressedShape` is
    // the SMALL one — using it here gave an extra-large square toggle a small button's pressed
    // corner, so the two branches disagreed about a shape that is per-size on both.
    pressedShape = ToggleButtonDefaults.shapesFor(size.containerHeight).pressedShape,
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

/**
 * The selected state, from the `selected` knob.
 *
 * [default] is per component rather than one constant for the family, because an unseeded knob has
 * to return the value its sticker already published — the filled and tonal toggles were authored
 * selected, the outlined and elevated ones unselected, and a shared default would silently move two
 * of the four published renders.
 *
 * This is a real axis of the kit's toggle-button sets rather than a state footnote: the container
 * shape morphs across it, so the selected and unselected cells of one size are different renders.
 */
@Composable
fun catalogToggleSelected(default: Boolean): Boolean = previewOverrideBoolean("selected", default)

// --- Sliders -----------------------------------------------------------------------------------
//
// The slider size axis is authored from the kit rather than from the library, because
// `SliderTokens` carries ONE set of handle and track dimensions — the extra-small ones — and no
// per-size scale to read the other four off. So unlike the button family, where a size resolves to
// `ButtonDefaults.MediumContainerHeight` and the library owns the number, these are measured from
// the `Standard slider` set's own variant frames:
//
//   Size     node          frame     track height   handle    track corner
//   XSmall   58008:10357   354x44    16             4x44      Corner/Large (16)
//   Small    58008:10412   354x44    24             4x44      Corner/Small (8)
//   Medium   58008:10467   354x52    40             4x52      Corner/Medium (12)
//   Large    58008:10534   354x68    56             4x68      Corner/Large (16)
//   XLarge   58008:10601   354x108   96             4x108     Corner/Extra-large (28)
//
// The handle stays 4dp wide at every size and as tall as the frame, and the 6dp gap either side of
// it is `SliderDefaults`' own `thumbTrackGapSize`, so neither is restated here.
//
// This table is a stand-in for a library gap, not a catalog constant, and it is reported as one in
// issue #89 — every other expressive family resolves a size to `ButtonDefaults.…` /
// `IconButtonDefaults.…` and owns none of the numbers. **When Compose Material 3 exposes a slider
// size scale, delete this and resolve against it**; leaving both would put the spec in two places,
// which is the failure the rest of this file is written to avoid.

/**
 * Track height for [CatalogSize], from the kit's slider frames.
 *
 * `SliderDefaults.Track` sizes itself from `SliderTokens.ActiveTrackHeight` — 16dp, the extra-small
 * value — so the height travels as a modifier on the track rather than as a parameter of it.
 */
val CatalogSize.sliderTrackHeight: Dp
  get() =
    when (this) {
      CatalogSize.ExtraSmall -> 16.dp
      CatalogSize.Small -> 24.dp
      CatalogSize.Medium -> 40.dp
      CatalogSize.Large -> 56.dp
      CatalogSize.ExtraLarge -> 96.dp
    }

/**
 * Handle size for [CatalogSize]: 4dp wide, as tall as the frame.
 *
 * Also the sticker's own height, since the handle is the tallest thing in a slider frame at every
 * size — see [sliderFrameHeight].
 */
val CatalogSize.sliderThumbSize: DpSize
  get() = DpSize(4.dp, sliderFrameHeight)

/** Frame height for [CatalogSize] — the handle's height, which sets the sticker's own. */
val CatalogSize.sliderFrameHeight: Dp
  get() =
    when (this) {
      CatalogSize.ExtraSmall -> 44.dp
      CatalogSize.Small -> 44.dp
      CatalogSize.Medium -> 52.dp
      CatalogSize.Large -> 68.dp
      CatalogSize.ExtraLarge -> 108.dp
    }

/**
 * Outer track corner for [CatalogSize], from the corner variable the kit binds on each frame.
 *
 * Extra small is the one that does not read across literally: the kit binds `Corner/Large` (16dp)
 * to a 16dp-tall track, which Figma clamps to a full pill at 8dp. Compose does not clamp, so
 * passing 16 there would draw a corner half again as tall as the track it rounds. 8dp is the value
 * the kit actually renders, and it is also what `SliderTokens` already resolves `CornerFull` to at
 * that height — so the base render does not move.
 *
 * The **inside** corners, the ones facing the handle, stay `SliderDefaults`' 2dp at every size; the
 * kit draws them the same way.
 */
val CatalogSize.sliderTrackCorner: Dp
  get() =
    when (this) {
      CatalogSize.ExtraSmall -> 8.dp
      CatalogSize.Small -> 8.dp
      CatalogSize.Medium -> 12.dp
      CatalogSize.Large -> 16.dp
      CatalogSize.ExtraLarge -> 28.dp
    }

/** The size this slider sticker should render at. See [CatalogSize.SliderAxis]. */
@Composable fun catalogSliderSize(): CatalogSize = CatalogSize.SliderAxis.current()
