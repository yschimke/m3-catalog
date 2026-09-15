/*
 * The kit's `State` axis, drawn.
 *
 * Every Glimmer component set in the kit varies on `State`, and until now this catalog drew only
 * `Enabled` — which is why #374 measured 34 of the kit's 50 cells as unreachable: a generated kit
 * cell is emitted only when every axis it changes is backed by an authored variant that resolves,
 * and the `State` axis had nothing behind it.
 *
 * | kit set | State values it publishes |
 * | --- | --- |
 * | `Button` (`40:655`) | Enabled, Focused, Pressed, Disabled, Disabled+Focused |
 * | `Toggle Button` (`40000113:3966`) | the same, x `Toggle=False \| True` |
 * | `Icon button` (`5315:4650`) | the same |
 * | `Toggle` (`40000113:4149`) | the same, x `Toggle=` |
 * | `List Item` (`384:4197`) | Enabled, **Hovered**, Pressed, Disabled |
 * | `Mic Indicators` (`40000116:9337`) | none — `Volume=` x `Contained=` only |
 *
 * ## Driven by the harness, not forged by the sticker
 *
 * `interaction = VariantInteraction.Focused` tells the RENDERER to drive real focus against the
 * composed node before it captures. That is the distinction `:catalog`'s
 * `CatalogInteractionAnnotations.kt` makes at length and it holds here: a sticker that emitted into
 * a `MutableInteractionSource` itself would draw a state layer whether or not the component can
 * actually take the state, which is precisely the thing a parity comparison must not fake.
 *
 * It works on this module's Robolectric lane, which was the open question — `@OverrideVariant`
 * lands as a `_VARIANT_` preview id with a render of its own, and focused and pressed each come out
 * pixel-distinct from the resting capture.
 *
 * ## Three of the kit's five states, and why not the other two
 *
 * - **`Hovered` is not authored anywhere.** Measured rather than assumed: a `hovered` cell on a
 *   clickable `ListItem` renders BYTE-IDENTICAL to its resting capture, because Glimmer draws no
 *   hover treatment — a glasses surface has no pointer. Authoring it would publish one picture
 *   under two names, which `AGENTS.md` fails the build for. The kit's `List Item` set names
 *   `Hovered` where its other sets name `Focused`; Glimmer's own answer is focus, so that is what
 *   this catalog draws, and the `Hovered` cells stay unreachable with a reason.
 * - **`Disabled+Focused` is not reachable.** A disabled Glimmer component does not take focus, so
 *   the render is identical to plain `Disabled` — the same duplicate, from the other direction.
 *
 * Those two are the kit's nine impossible cells, and they have an issue of their own:
 * [#392](https://github.com/yschimke/m3-catalog/issues/392). They will not close by drawing more —
 * either the kit moves, or `kit-unauthorable.json` grows to cover this kit and makes the exclusion
 * a checked declaration rather than a paragraph.
 */
package ee.schimke.m3catalog.glimmer

import androidx.compose.runtime.Composable
import androidx.xr.glimmer.ButtonSize
import ee.schimke.composeai.data.overrides.PreviewOverrideOption
import ee.schimke.composeai.overrides.previewOverrideChoice
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.composeai.preview.VariantInteraction

/**
 * Focused, pressed and disabled — the three of the kit's `State` values this catalog can draw.
 *
 * `focused` and `pressed` are harness-driven; `disabled` seeds the [glimmerEnabled] knob, because
 * it is a parameter rather than an interaction.
 */
@OverrideVariant(name = "focused", interaction = VariantInteraction.Focused)
@OverrideVariant(name = "pressed", interaction = VariantInteraction.Pressed)
@OverrideVariant(name = "disabled", strings = ["status=disabled"])
annotation class GlimmerStates

/**
 * [GlimmerStates] without the disabled cell, for `ListItem`.
 *
 * Not a judgement about whether a disabled row is worth drawing — alpha19's `ListItem` has **no
 * `enabled` parameter**, so there is no way to call it disabled. The kit publishes `Type=1-line,
 * State=Disabled` (`384:4192`) and this catalog cannot be a picture of it; see the note in
 * [Lists.kt]'s `ListItemSticker`.
 */
@OverrideVariant(name = "focused", interaction = VariantInteraction.Focused)
@OverrideVariant(name = "pressed", interaction = VariantInteraction.Pressed)
annotation class GlimmerInteractionStates

/**
 * The `size` knob: `Default` by default, and the kit's own two values.
 *
 * `ButtonSize` is a Compose parameter a caller really passes, so it is a knob rather than a second
 * composable — and the knob is what makes the kit's `Size=Large` crossings addressable. A generated
 * exact-cell annotation seeds a knob on the sticker it is attached to; a seed naming an axis the
 * sticker does not READ would publish the default pixels at the kit's Large address, which is worse
 * than leaving the cell uncovered. #374.
 *
 * The values are Compose's (`Medium` / `Large`); the kit calls the same two cells `Size=Default`
 * and `Size=Large`. Only the large one has to meet the kit, and `size=Large` — the prop the
 * existing `@CatalogVariant` already declares — is spelled the same on both sides.
 */
@Composable
fun glimmerButtonSize(): ButtonSize =
  if (
    previewOverrideChoice(
      "size",
      "Medium",
      listOf(PreviewOverrideOption("Medium", "Medium"), PreviewOverrideOption("Large", "Large")),
    ) == "Large"
  )
    ButtonSize.Large
  else ButtonSize.Medium

/**
 * The `state` knob behind the checked cells: unchecked by default.
 *
 * Seeds the INITIAL value of the sticker's own `checked` state rather than replacing it. A toggle
 * that read this on every recomposition would stop answering a live click, which is the one thing
 * `AGENTS.md` says a toggle's click has to do; keying the `remember` on the override-derived value
 * keeps the panel and the component in step.
 */
@Composable
fun glimmerChecked(): Boolean =
  previewOverrideChoice(
    "state",
    "unchecked",
    listOf(
      PreviewOverrideOption("unchecked", "Unchecked"),
      PreviewOverrideOption("checked", "Checked"),
    ),
  ) == "checked"

/**
 * The `status` knob behind the `disabled` cell: `enabled` by default, and a closed two-value set so
 * the live lane's Overrides panel offers the alternative rather than a text field.
 */
@Composable
fun glimmerEnabled(): Boolean =
  previewOverrideChoice(
    "status",
    "enabled",
    listOf(
      PreviewOverrideOption("enabled", "Enabled"),
      PreviewOverrideOption("disabled", "Disabled"),
    ),
  ) != "disabled"
