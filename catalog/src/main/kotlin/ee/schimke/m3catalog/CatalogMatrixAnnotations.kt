package ee.schimke.m3catalog

import ee.schimke.composeai.preview.OverrideVariant

//
// The catalog's variant matrices, declared **once each** as annotations rather than retyped per
// component.
//
// A matrix is a cross product — five sizes by two shapes, or by three widths as well — and every
// component of a family carries the identical set of cells. Written out per component that was 250
// `@OverrideVariant` annotations across thirteen blocks: the same cell spelled out five times for
// the buttons, four for the icon buttons, twice for each toggle default. Nothing kept the copies in
// step, and they had already drifted (`IconButtons.kt` spelled the default size explicitly in
// `s-narrow` and implicitly in `s-square`).
//
// `@OverrideVariant` targets `ANNOTATION_CLASS` as of compose-ai-tools 0.19.50, so a matrix can be
// declared here and applied with one line. The cells are **exactly** what the component blocks
// carried — this was a lift, not a rewrite, so no render moved — and `CatalogVariantMatrixTest`
// holds them to the [CatalogVariantMatrices] declarations in `CatalogAxes.kt`, so the axes stay the
// source of truth for what a matrix should contain.
//
// Note that stacking two of these would **union** their cells, not multiply them: each annotation
// is
// a finished matrix, not an axis. A component needing a genuine extra dimension wants a different
// matrix declared here, not two of these together.
//

/**
 * The **button family matrix**: five sizes x two shapes, plus the disabled state beside them.
 *
 * Ten cells, and every one of the five common buttons carries exactly these — which is why this
 * annotation exists. Written out per component they were fifty near-identical lines maintained in
 * five places.
 */
@OverrideVariant(name = "xs", strings = ["size=xs"])
@OverrideVariant(name = "xs-square", strings = ["size=xs", "shape=square"])
@OverrideVariant(name = "s-square", strings = ["shape=square"])
@OverrideVariant(name = "m", strings = ["size=m"])
@OverrideVariant(name = "m-square", strings = ["size=m", "shape=square"])
@OverrideVariant(name = "l", strings = ["size=l"])
@OverrideVariant(name = "l-square", strings = ["size=l", "shape=square"])
@OverrideVariant(name = "xl", strings = ["size=xl"])
@OverrideVariant(name = "xl-square", strings = ["size=xl", "shape=square"])
@OverrideVariant(name = "disabled", strings = ["state=disabled"])
annotation class SizeShapeMatrix

/**
 * The **icon button matrix**: five sizes x three widths x two shapes, plus disabled.
 *
 * Icon buttons carry a width axis the plain buttons do not, so this is thirty cells rather than ten
 * — a hundred and twenty lines across the four emphases before it was declared once.
 */
@OverrideVariant(name = "xs-narrow", strings = ["size=xs", "width=narrow"])
@OverrideVariant(name = "xs-narrow-square", strings = ["size=xs", "width=narrow", "shape=square"])
@OverrideVariant(name = "xs", strings = ["size=xs"])
@OverrideVariant(name = "xs-square", strings = ["size=xs", "shape=square"])
@OverrideVariant(name = "xs-wide", strings = ["size=xs", "width=wide"])
@OverrideVariant(name = "xs-wide-square", strings = ["size=xs", "width=wide", "shape=square"])
@OverrideVariant(name = "s-narrow", strings = ["width=narrow"])
@OverrideVariant(name = "s-narrow-square", strings = ["size=s", "width=narrow", "shape=square"])
@OverrideVariant(name = "s-square", strings = ["shape=square"])
@OverrideVariant(name = "s-wide", strings = ["width=wide"])
@OverrideVariant(name = "s-wide-square", strings = ["size=s", "width=wide", "shape=square"])
@OverrideVariant(name = "m-narrow", strings = ["size=m", "width=narrow"])
@OverrideVariant(name = "m-narrow-square", strings = ["size=m", "width=narrow", "shape=square"])
@OverrideVariant(name = "m", strings = ["size=m"])
@OverrideVariant(name = "m-square", strings = ["size=m", "shape=square"])
@OverrideVariant(name = "m-wide", strings = ["size=m", "width=wide"])
@OverrideVariant(name = "m-wide-square", strings = ["size=m", "width=wide", "shape=square"])
@OverrideVariant(name = "l-narrow", strings = ["size=l", "width=narrow"])
@OverrideVariant(name = "l-narrow-square", strings = ["size=l", "width=narrow", "shape=square"])
@OverrideVariant(name = "l", strings = ["size=l"])
@OverrideVariant(name = "l-square", strings = ["size=l", "shape=square"])
@OverrideVariant(name = "l-wide", strings = ["size=l", "width=wide"])
@OverrideVariant(name = "l-wide-square", strings = ["size=l", "width=wide", "shape=square"])
@OverrideVariant(name = "xl-narrow", strings = ["size=xl", "width=narrow"])
@OverrideVariant(name = "xl-narrow-square", strings = ["size=xl", "width=narrow", "shape=square"])
@OverrideVariant(name = "xl", strings = ["size=xl"])
@OverrideVariant(name = "xl-square", strings = ["size=xl", "shape=square"])
@OverrideVariant(name = "xl-wide", strings = ["size=xl", "width=wide"])
@OverrideVariant(name = "xl-wide-square", strings = ["size=xl", "width=wide", "shape=square"])
@OverrideVariant(name = "disabled", strings = ["state=disabled"])
annotation class IconButtonMatrix

/**
 * The toggle-button matrix for a component **authored selected** (filled, tonal).
 *
 * Twenty cells: five sizes x two shapes x two selected states, plus disabled. The unsuffixed cells
 * are the selected ones, so the cells that turn it off are named `-off` — which is why there are
 * two of these annotations rather than one. See [UnselectedToggleButtonMatrix].
 */
@OverrideVariant(name = "xs", strings = ["size=xs"])
@OverrideVariant(name = "xs-square", strings = ["size=xs", "shape=square"])
@OverrideVariant(name = "s-square", strings = ["shape=square"])
@OverrideVariant(name = "m", strings = ["size=m"])
@OverrideVariant(name = "m-square", strings = ["size=m", "shape=square"])
@OverrideVariant(name = "l", strings = ["size=l"])
@OverrideVariant(name = "l-square", strings = ["size=l", "shape=square"])
@OverrideVariant(name = "xl", strings = ["size=xl"])
@OverrideVariant(name = "xl-square", strings = ["size=xl", "shape=square"])
@OverrideVariant(name = "xs-off", booleans = ["selected=false"], strings = ["size=xs"])
@OverrideVariant(
  name = "xs-square-off",
  booleans = ["selected=false"],
  strings = ["size=xs", "shape=square"],
)
@OverrideVariant(name = "s-off", booleans = ["selected=false"])
@OverrideVariant(name = "s-square-off", booleans = ["selected=false"], strings = ["shape=square"])
@OverrideVariant(name = "m-off", booleans = ["selected=false"], strings = ["size=m"])
@OverrideVariant(
  name = "m-square-off",
  booleans = ["selected=false"],
  strings = ["size=m", "shape=square"],
)
@OverrideVariant(name = "l-off", booleans = ["selected=false"], strings = ["size=l"])
@OverrideVariant(
  name = "l-square-off",
  booleans = ["selected=false"],
  strings = ["size=l", "shape=square"],
)
@OverrideVariant(name = "xl-off", booleans = ["selected=false"], strings = ["size=xl"])
@OverrideVariant(
  name = "xl-square-off",
  booleans = ["selected=false"],
  strings = ["size=xl", "shape=square"],
)
@OverrideVariant(name = "disabled", strings = ["state=disabled"])
annotation class SelectedToggleButtonMatrix

/**
 * The toggle-button matrix for a component **authored unselected** (outlined, elevated).
 *
 * The mirror of [SelectedToggleButtonMatrix]: same twenty cells, but the unsuffixed ones are the
 * unselected renders and the cells that turn it on are named `-on`. The default a component was
 * authored in decides which of the two it carries, because that default is what the sticker's
 * `catalogToggleSelected(default = …)` passes and what the naming is relative to.
 */
@OverrideVariant(name = "xs", strings = ["size=xs"])
@OverrideVariant(name = "xs-square", strings = ["size=xs", "shape=square"])
@OverrideVariant(name = "s-square", strings = ["shape=square"])
@OverrideVariant(name = "m", strings = ["size=m"])
@OverrideVariant(name = "m-square", strings = ["size=m", "shape=square"])
@OverrideVariant(name = "l", strings = ["size=l"])
@OverrideVariant(name = "l-square", strings = ["size=l", "shape=square"])
@OverrideVariant(name = "xl", strings = ["size=xl"])
@OverrideVariant(name = "xl-square", strings = ["size=xl", "shape=square"])
@OverrideVariant(name = "xs-on", booleans = ["selected=true"], strings = ["size=xs"])
@OverrideVariant(
  name = "xs-square-on",
  booleans = ["selected=true"],
  strings = ["size=xs", "shape=square"],
)
@OverrideVariant(name = "s-on", booleans = ["selected=true"])
@OverrideVariant(name = "s-square-on", booleans = ["selected=true"], strings = ["shape=square"])
@OverrideVariant(name = "m-on", booleans = ["selected=true"], strings = ["size=m"])
@OverrideVariant(
  name = "m-square-on",
  booleans = ["selected=true"],
  strings = ["size=m", "shape=square"],
)
@OverrideVariant(name = "l-on", booleans = ["selected=true"], strings = ["size=l"])
@OverrideVariant(
  name = "l-square-on",
  booleans = ["selected=true"],
  strings = ["size=l", "shape=square"],
)
@OverrideVariant(name = "xl-on", booleans = ["selected=true"], strings = ["size=xl"])
@OverrideVariant(
  name = "xl-square-on",
  booleans = ["selected=true"],
  strings = ["size=xl", "shape=square"],
)
@OverrideVariant(name = "disabled", strings = ["state=disabled"])
annotation class UnselectedToggleButtonMatrix

/**
 * The **slider matrix**: the size axis alone, four cells.
 *
 * Its base is extra small rather than small — the size a bare `Slider(...)` is, and the first
 * variant of the kit's slider sets — so `s` is a cell here where it is the unnamed base everywhere
 * else. Nothing is crossed into it: the size carries the track's corner, and the `steps`, `track`
 * and `status` knobs the slider stickers already had stay beside it as single-axis variants.
 */
@OverrideVariant(name = "s", strings = ["size=s"])
@OverrideVariant(name = "m", strings = ["size=m"])
@OverrideVariant(name = "l", strings = ["size=l"])
@OverrideVariant(name = "xl", strings = ["size=xl"])
annotation class SliderSizeMatrix
