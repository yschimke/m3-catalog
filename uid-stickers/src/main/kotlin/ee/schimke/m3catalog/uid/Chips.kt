@file:CatalogGroup(name = "Chips", section = "Selection")

package ee.schimke.m3catalog.uid

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.composeai.preview.VariantInteraction
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogChoice
import ee.schimke.m3catalog.catalogText
import ee.schimke.m3catalog.counted
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.chip_sounds_good
import generated.uibuilder.KitSuggestionChip
import org.jetbrains.compose.resources.stringResource

// The body is `stickers/suggestion-chip.uid`, a `KitSuggestionChip` component whose `label` and
// `enabled` are bindings and whose click is an action-free event: the shell supplies the live
// `label` text, the `status` axis and a click counter, as the hand-written sticker does, so
// `disabled` is the same published override cell rather than a second document.
// The interaction cells are not in the document at all:
// `@OverrideVariant(interaction = …)` is applied by the render harness to whatever the preview
// draws, so a generated body gets hovered / focused / pressed renders from the shell alone. The
// `focus-ring` cell's `focus=ring` knob is read by `Sticker`'s frame, not by the body.
//
// `:catalog`'s `@CatalogModes` and `@InteractionStates` are spelled out because discovery does not
// follow those meta-annotations across the module boundary.

@CatalogComponent(
  id = "Chip/Suggestion",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/53923:28845",
  caption = "A dynamically generated action.",
)
@Preview(name = "Light", group = "modes")
@Preview(name = "Dark", uiMode = 32, group = "modes")
@OverrideVariant(name = "hovered", interaction = VariantInteraction.Hovered)
@OverrideVariant(name = "focused", interaction = VariantInteraction.Focused)
@OverrideVariant(
  name = "focus-ring",
  interaction = VariantInteraction.Focused,
  strings = ["focus=ring"],
)
@OverrideVariant(name = "pressed", interaction = VariantInteraction.Pressed)
@OverrideVariant(name = "disabled", strings = ["status=disabled"])
@Composable
fun SuggestionChipUid() = Sticker {
  val c = counted(catalogText("label", stringResource(Res.string.chip_sounds_good)))
  KitSuggestionChip(
    Modifier,
    label = c.label,
    enabled = catalogChoice("status", "enabled", "enabled", "disabled") != "disabled",
    onChipClick = c.onClick,
  )
}
