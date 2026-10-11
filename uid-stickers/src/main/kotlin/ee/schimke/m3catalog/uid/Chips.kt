@file:CatalogGroup(name = "Chips", section = "Selection")

package ee.schimke.m3catalog.uid

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.composeai.preview.VariantInteraction
import ee.schimke.m3catalog.Sticker
import generated.uibuilder.KitSuggestionChip
import generated.uibuilder.KitSuggestionChipDisabled

// The body is `stickers/suggestion-chip.uid`. The interaction cells are not in the document at all:
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
@Composable
fun SuggestionChipUid() = Sticker { KitSuggestionChip() }

@Preview(name = "Light", group = "modes")
@Preview(name = "Dark", uiMode = 32, group = "modes")
@Composable
fun SuggestionChipUidDisabled() = Sticker { KitSuggestionChipDisabled() }
