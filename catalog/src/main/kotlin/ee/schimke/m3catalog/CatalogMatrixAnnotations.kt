package ee.schimke.m3catalog

import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.composeai.preview.VariantInteraction

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

//
// The **interaction states**, which are not seeds at all.
//
// Every other cell in this file seeds a knob and re-renders the same composable with different
// data. These three seed nothing: `interaction` tells the renderer to drive real hover, focus or
// press against the composed node before it captures, so what moves is the harness rather than the
// sticker. That distinction is the whole point — a sticker that forged the state by emitting into a
// `MutableInteractionSource` would draw a state layer whether or not the component can actually
// receive the state, which is the failure compose-ai-tools#3672 removed from its own samples.
//
// They are the last third of the kit's `State` axis. `CatalogState` declares only Enabled and
// Disabled and says why: a static `@Preview` could not reach the other three. That was true until
// compose-ai-tools 1.6.0 (#3877) made an interaction variant addressable — it lands as a
// `_VARIANT_` preview id with its own render, which is what a design-map entry needs to join it to
// a kit node. So the comment in `CatalogAxes.kt` is now history rather than a live constraint.
//
// Which of the three a component carries is decided by its kit set, never by what Compose could
// draw: the search bar and the sliders publish no `Focused` variant, and the input chip and text
// fields publish no `Pressed` one. Authoring a state the kit does not draw would render a sticker
// with nothing to compare it against, which is the same waste as a combination variant.
//

//
// Each of the two annotations below carries the same interaction **twice**: once as the kit's
// `State=Focused` variant (`focused`) and once as the keyboard focus indicator (`focus-ring`).
//
// That is not a duplicate cell, because the two are different pictures of different things. The
// kit's focused VARIANT is the resting container under a 10% state layer and nothing else — go and
// read `58651:12409` in `design/pages/buttons.svg`, it is a `#6750A4` rect with a `white` rect at
// `fill-opacity="0.1"` over it. The keyboard focus indicator is a separate `Show focus indicator`
// BOOLEAN **property** on the same set, defaulting to false, and it is the ring a Tab draws. Issue
// #148 is the second of those two missing from the sheet while the first was on it, which read as
// the catalog claiming Compose draws no focus indicator at all.
//
// Only `focused` resolves to a kit node, and it is the one parity diffs. `focus-ring` seeds a kit
// property rather than a variant, so `scripts/design-map.sh` files it under "a component PROPERTY
// in the kit, not a variant beside it" and it adds no reference to `design-map.json` — the same
// place `Show FAB` and `Show icon` already sit. See [CatalogFocus] for why the honest place to
// record the ring's remaining divergence from the kit's utility node (3dp `secondary`, against
// Material's 2dp `secondary` over 3dp `onSecondary`) is a KDoc rather than a hand-tuned
// `InsetRing(...)`.
//

/** Hover, focus and press — the full interaction triple, for sets that publish all three. */
@OverrideVariant(name = "hovered", interaction = VariantInteraction.Hovered)
@OverrideVariant(name = "focused", interaction = VariantInteraction.Focused)
@OverrideVariant(
  name = "focus-ring",
  interaction = VariantInteraction.Focused,
  strings = ["focus=ring"],
)
@OverrideVariant(name = "pressed", interaction = VariantInteraction.Pressed)
annotation class InteractionStates

/** For sets that publish no `Pressed` variant: the input chip and the two text fields. */
@OverrideVariant(name = "hovered", interaction = VariantInteraction.Hovered)
@OverrideVariant(name = "focused", interaction = VariantInteraction.Focused)
@OverrideVariant(
  name = "focus-ring",
  interaction = VariantInteraction.Focused,
  strings = ["focus=ring"],
)
annotation class HoverFocusStates

// There is deliberately no hover-plus-press annotation, though the search bar and the sliders are
// the sets that would want one. Compose draws no hover state for either — see issue #91 — so those
// components carry a single `pressed` cell inline instead, and the search bar carries none at all.
// An annotation whose cells render nothing is worse than no annotation: it reads as coverage.

/**
 * [InteractionStates] plus the one square cell a state layer makes visible — for the two
 * **containerless** styles, `IconButton/Standard` and `Button/Text`.
 *
 * Those two draw no container in their resting state, so `catalogIconShape` / `catalogButtonShape`
 * resolve a different `Shape`, the component honours it, and nothing is painted with it. Every one
 * of the forty `*-square` cells their matrices carry is therefore byte-identical to its round
 * counterpart — measured across all 1340 baked renders in issue #175, thirty on the icon button
 * (five sizes x three widths, both schemes) and ten on the text button.
 *
 * Those cells stay, because the redundancy is the **kit's** as much as ours: `Icon button -
 * standard` and `Button - text` publish a separate `Type=Square` node for every one of them, and
 * each draws exactly what its `Type=Round` sibling draws. Read `57994:2254` and `57994:2264` in
 * `design/pages/buttons.svg` — same glyph path, same label path, no container between them, and the
 * only difference is the `rx=12` / `rx=20` clip around a frame with nothing painted inside it. So
 * the duplicates are a correct comparison of two identical pictures, and dropping them would strand
 * forty kit variants with no candidate render to claim a gap that is not there.
 *
 * What was missing is a cell where the axis shows. Under a state layer that clip stops being empty
 * — the kit's own `State=Hovered` square nodes (`58650:8161`, `58663:30916`) are the same 8%
 * overlay as their round siblings clipped to `rx=12` rather than `rx=20` — and Compose clips its
 * state layer the same way. `square-hovered` is that cell: one per component, at the default size,
 * against a kit node that genuinely differs from the round one beside it.
 *
 * Hover rather than focus or press, and one cell rather than three: all three states clip the same
 * layer to the same shape, so the second and third would re-publish the first's finding at three
 * times the render cost. This is the only place the catalog crosses `shape` with `state` — see
 * [CatalogVariantMatrix.alongside] for why the matrices themselves never do.
 */
@OverrideVariant(name = "hovered", interaction = VariantInteraction.Hovered)
@OverrideVariant(
  name = "square-hovered",
  interaction = VariantInteraction.Hovered,
  strings = ["shape=square"],
)
@OverrideVariant(name = "focused", interaction = VariantInteraction.Focused)
@OverrideVariant(
  name = "focus-ring",
  interaction = VariantInteraction.Focused,
  strings = ["focus=ring"],
)
@OverrideVariant(name = "pressed", interaction = VariantInteraction.Pressed)
annotation class ContainerlessInteractionStates
