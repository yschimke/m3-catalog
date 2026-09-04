package ee.schimke.m3catalog

import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.RippleDefaults
import androidx.compose.material3.RippleThemeConfiguration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.platform.LocalInputModeManager
import ee.schimke.composeai.overrides.previewOverrideBoolean
import ee.schimke.composeai.overrides.previewOverrideColor

/**
 * The **focus indication** axis: how a component draws the fact that it holds focus.
 *
 * M3 draws focus two different ways, and the kit publishes both — which is why this is an axis and
 * not a constant:
 * * **Opacity.** The `State=Focused` variant of every component set is the resting container under
 *   a 10% state layer, and nothing else. That is what `Button` / `Chip` / `Switch` / … draw today,
 *   and what an unseeded focused render has always published, so [Opacity] is the default and no
 *   committed render moves because this axis arrived.
 * * **The keyboard focus indicator.** The ring a **Tab** draws around whatever now holds focus. The
 *   kit publishes it twice over: as a standalone utility (`Focus indicator`, `58683:23015` on the
 *   Utilities page — a 3dp `secondary` stroke) and as a `Show focus indicator` BOOLEAN **component
 *   property**, defaulting to `false`, on all 23 of the sets that can take focus. A property, not a
 *   variant — which is why it is absent from every `State=Focused` node and why grepping the
 *   variant index for it finds nothing. It is also the subject of
 *   [issue #148](https://github.com/yschimke/m3-catalog/issues/148): the focused stickers showed
 *   the state layer and no ring, because before the material3 1.12 line Compose had no ring to
 *   draw.
 *
 * [Ring] is that indicator, and it is **Material's own**:
 * `RippleThemeConfiguration.Focus.InsetRing` is a published theming surface, resolved by the
 * library into `secondary` / `onSecondary` strokes and applied by `Surface`, `IconButton`,
 * `Checkbox`, `RadioButton`, `Switch`, `Slider`, `ListItem` and the navigation items alike. So a
 * component's focus-ring render is still the real composable drawing its real focus indication —
 * the catalog only selects which of the two styles the theme asks for, exactly as
 * [catalogButtonShape] selects the kit's corner over `ButtonDefaults.shape`.
 *
 * ### Why the two land differently in the design map
 *
 * `state=focused` names a kit **variant**, so it pairs with a node and parity diffs it.
 * `focus=ring` names a kit **property**, and `scripts/design-map.sh` reports those as *"a component
 * PROPERTY in the kit, not a variant beside it"* — unpaired, because the kit indexes a definition
 * node at its defaults and no configured instance with the property flipped. That is the same
 * bucket `Show FAB`, `Show icon` and `Badge label` have always sat in, and it is why this cell adds
 * no kit reference to `design-map.json`: the reference count is unchanged at 543 across 59
 * components. See `InteractionStates` in `CatalogMatrixAnnotations.kt` for the cells themselves.
 *
 * Text fields are the one focus-bearing family this axis does not reach: their focused appearance
 * is the indicator line (`IndicatorLineNode`), which is drawn by the text field itself rather than
 * by a ripple, so there is no ring for the theme to switch on. Their `focused` cells stay the lone
 * inline `@OverrideVariant` they already were.
 */
enum class CatalogFocus(override val knob: String) : CatalogKnob {
  /** The 10% state layer — Compose's default, and what every kit `State=Focused` variant draws. */
  Opacity("opacity"),
  /** The keyboard focus indicator, as Material's inset focus ring. */
  Ring("ring");

  companion object {
    val Axis = CatalogKnobAxis("focus", entries, Opacity)
  }
}

/**
 * What `StickerFrame` provides for this sticker's focus indication: which of the two treatments the
 * theme draws, and — only where a reader asked for it — what colour to draw it in.
 *
 * Read once, from one knob, so the axis reaches every component in the catalog from one place
 * rather than a knob per section.
 */
class CatalogFocusChoice(
  /** Which of the two treatments this render draws, resolved once. */
  val treatment: CatalogFocus,
  /** `LocalRippleThemeConfiguration`: Material's own defaults on both branches. */
  val indication: RippleThemeConfiguration,
  /** `LocalRippleConfiguration`, or null to leave the library's own colours alone. */
  val ripple: RippleConfiguration?,
)

/**
 * The focus treatment for this render, and the colour override if this cell offers one.
 *
 * Returns Material's own defaults for [CatalogFocusChoice.indication] on both branches. Re-tuning
 * the ring's insets and widths to chase the kit's utility node would publish a theme nobody ships;
 * where the two disagree the honest record is this KDoc, not a bespoke `InsetRing(...)`.
 *
 * ### The ring's colours, and where the state layer's live instead
 *
 * Both focus treatments are hard to *see*. The opacity treatment is a 10% white overlay — `#6750A4`
 * becomes `#7661AD`, 17/255 on one channel — and only 4/255 away from the hover state layer beside
 * it, so the sheet shows two cells a reader cannot tell apart without an eyedropper. The ring is
 * bolder but sits at `secondary` over `onSecondary`, which on several schemes is close to the
 * container it rings.
 *
 * So the ring publishes colours a reader can turn up, on the `focus-ring` cells and nowhere else:
 * those are the cells that seed `focus=ring`, so gating the reads on the Ring branch scopes them by
 * construction. The knobs default to `Color.Unspecified`, which returns null below, which provides
 * nothing, which leaves the library resolving its own colours exactly as before — so every baked
 * PNG is byte-identical and the tint exists only where someone reaches for it.
 *
 * **The state layer's colour is deliberately not offered here.** `StickerFrame` composes for every
 * sticker, so a knob read on this branch would appear on all of them, and the only way to scope it
 * to the `focused` cells is to have them seed the axis explicitly — which costs exactly what those
 * cells are for. Measured: seeding `focus=opacity` moved all 22 of them out of the resolver's
 * variant bucket and into its property bucket, taking design-map coverage from 120 unresolved
 * variants to 142. `focused` is the one cell that parity-diffs the kit's `State=Focused` node, so
 * paying its kit reference for a colour knob is the wrong trade. The state layer's colour is
 * offered by [KeyboardNavigable] instead, where a sticker opts in and the scoping is free.
 */
@Composable
fun catalogFocusChoice(): CatalogFocusChoice =
  when (CatalogFocus.Axis.current()) {
    CatalogFocus.Opacity ->
      CatalogFocusChoice(
        treatment = CatalogFocus.Opacity,
        indication = RippleDefaults.OpacityFocusRippleThemeConfiguration,
        ripple = null,
      )
    CatalogFocus.Ring ->
      CatalogFocusChoice(
        treatment = CatalogFocus.Ring,
        indication = RippleDefaults.InsetFocusRingRippleThemeConfiguration,
        ripple = insetRingRipple(),
      )
  }

/**
 * The state layer's colour, when a reader has set one.
 *
 * `RippleConfiguration.color` is the state layer *and* the ripple, which is the right granularity
 * here: on a `focused` cell the only interaction the harness drives is focus, so the colour a
 * reader sets is the one thing they see.
 */
@Composable
private fun overlayRipple(): RippleConfiguration? {
  val overlay = previewOverrideColor("focusOverlayColour", Color.Unspecified)
  return if (overlay.isSpecified) RippleConfiguration(color = overlay) else null
}

/**
 * The ring's two strokes, when a reader has set either.
 *
 * Setting one fills the other from the scheme role the library resolves it from — `secondary`
 * outside, `onSecondary` inside. That fallback is a *reading* of the library rather than a value it
 * hands over, so it is deliberately confined to the overridden case: with both knobs unset this
 * returns null, nothing is provided, and the ring is drawn by the library's own resolution rather
 * than by this guess at it.
 */
@Composable
private fun insetRingRipple(): RippleConfiguration? {
  val outer = previewOverrideColor("focusRingOuterColour", Color.Unspecified)
  val inner = previewOverrideColor("focusRingInnerColour", Color.Unspecified)
  if (!outer.isSpecified && !inner.isSpecified) return null
  val scheme = MaterialTheme.colorScheme
  return RippleConfiguration(
    focus =
      RippleConfiguration.Focus.InsetRing(
        outerStrokeColor = if (outer.isSpecified) outer else scheme.secondary,
        innerStrokeColor = if (inner.isSpecified) inner else scheme.onSecondary,
      )
  )
}

/**
 * The treatment `StickerFrame` resolved for this render, so a sticker that wants to act on it does
 * not read the axis a second time.
 */
internal val LocalCatalogFocus = staticCompositionLocalOf { CatalogFocus.Opacity }

/**
 * **Keyboard navigation**, for the stickers a reader can actually navigate: focus becomes takeable,
 * and with it the focus colours become adjustable.
 *
 * ### Why a sticker has to ask for this
 *
 * Compose's `Modifier.clickable` registers its focusable as `Focusability.SystemDefined`, which
 * refuses focus while `LocalInputModeManager.inputMode` is `Touch` — and a preview host is in touch
 * mode, because no real key event ever arrives in one. So Tab in a held live session moves nothing:
 * not a bug in the component, an input mode that says nobody is using a keyboard. Turning this knob
 * on requests `InputMode.Keyboard`, which is the state a real device is in the moment its user
 * reaches for Tab, and is the same flip `@FocusedPreview` performs for its baked captures.
 *
 * ### What it implies, and why the colour knobs ride with it
 *
 * A picker in input mode is a *form* — two text fields, a mode toggle, a confirm and a dismiss —
 * and it is the one place in this catalog where focus is a **path** rather than a state: the reader
 * wants to watch it move. That is also where the two focus treatments are hardest to read, because
 * the eye is tracking a moving target rather than comparing two stills. So this knob carries the
 * colours with it: switch keyboard navigation on and the ring's two strokes (or the state layer's
 * colour, depending on the `focus` axis) become knobs, defaulting to Material's own so nothing
 * moves until someone reaches for them. Off — which is every baked render — nothing is read, so
 * these previews publish exactly the bytes they published before.
 *
 * This is deliberately *not* in `StickerFrame`. Everything read there is read by every sticker in
 * the catalog, and a keyboard-navigation knob on a `Badge` would offer a reader a walk with one
 * stop on it.
 */
@Composable
fun KeyboardNavigable(content: @Composable () -> Unit) {
  if (!previewOverrideBoolean("keyboardNav", false)) {
    content()
    return
  }
  val inputMode = LocalInputModeManager.current
  // Requested rather than provided: `InputModeManager` is owned by the host, and asking is the
  // documented way in. A host that refuses (the request returns false) leaves the sticker exactly
  // as it was, which is the honest outcome — better than a composition local that claims a keyboard
  // the platform will not deliver events from.
  LaunchedEffect(inputMode) { inputMode.requestInputMode(InputMode.Keyboard) }
  val ripple = focusRipple(LocalCatalogFocus.current)
  if (ripple == null) content()
  else CompositionLocalProvider(LocalRippleConfiguration provides ripple, content = content)
}

/** The colour override for [focus], or null when the reader has set no colour. */
@Composable
private fun focusRipple(focus: CatalogFocus): RippleConfiguration? =
  when (focus) {
    CatalogFocus.Opacity -> overlayRipple()
    CatalogFocus.Ring -> insetRingRipple()
  }
