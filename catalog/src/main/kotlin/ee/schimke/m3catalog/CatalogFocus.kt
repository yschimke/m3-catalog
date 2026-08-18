package ee.schimke.m3catalog

import androidx.compose.material3.RippleDefaults
import androidx.compose.material3.RippleThemeConfiguration
import androidx.compose.runtime.Composable

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
 * The ripple theming this sticker should draw focus with, read once by `StickerFrame` so the axis
 * reaches every component in the catalog from one place rather than a knob per section.
 *
 * Returns Material's own defaults on both branches. Re-tuning the ring's insets and widths to chase
 * the kit's utility node would publish a theme nobody ships; where the two disagree the honest
 * record is this KDoc, not a bespoke `InsetRing(...)`.
 */
@Composable
fun catalogFocusIndication(): RippleThemeConfiguration =
  when (CatalogFocus.Axis.current()) {
    CatalogFocus.Opacity -> RippleDefaults.OpacityFocusRippleThemeConfiguration
    CatalogFocus.Ring -> RippleDefaults.InsetFocusRingRippleThemeConfiguration
  }
