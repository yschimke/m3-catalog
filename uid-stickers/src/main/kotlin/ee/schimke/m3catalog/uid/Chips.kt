@file:CatalogGroup(name = "Chips", section = "Selection")

package ee.schimke.m3catalog.uid

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.InteractionStates
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogChoice
import ee.schimke.m3catalog.catalogText
import ee.schimke.m3catalog.counted
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.chip_sounds_good
import generated.uibuilder.KitSuggestionChip
import generated.uibuilder.KitSuggestionChipElevated
import generated.uibuilder.KitSuggestionChipElevatedIcon
import generated.uibuilder.KitSuggestionChipIcon
import org.jetbrains.compose.resources.stringResource

// The body is `stickers/suggestion-chip.uid`, a `KitSuggestionChip` component whose `label` and
// `enabled` are bindings and whose click is an action-free event. The kit's `Style` and `Show icon`
// axes are cells of that document (`stickers/suggestion-chip.json`): `ElevatedSuggestionChip` is a
// sibling function, so `Elevated` swaps the node's component, and `Icon` adds the stars glyph to its
// icon slot. The shell reads the same knobs as the hand-written sticker and picks the cell; the
// interaction and exhaustive kit cells come from its annotations, which are the hand-written
// sticker's verbatim.

@CatalogComponent(
  id = "Chip/Suggestion",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/53923:28845",
  caption =
    "A dynamically generated action. Elevated and disabled fold in; the kit's selected cells " +
      "stay excluded because SuggestionChip has no selection state.",
)
@CatalogModes
@OverrideVariant(name = "elevated", strings = ["style=elevated"])
@OverrideVariant(name = "disabled", strings = ["status=disabled"])
@OverrideVariant(name = "icon", booleans = ["icon=true"])
@InteractionStates
@ee.schimke.m3catalog.SuggestionChipStickerExhaustiveKitCells
@Composable
fun SuggestionChipSticker(icon: Boolean = false) = Sticker {
  val c = counted(catalogText("label", stringResource(Res.string.chip_sounds_good)))
  val enabled = catalogChoice("status", "enabled", "enabled", "disabled") != "disabled"
  val elevated = catalogChoice("style", "outlined", "outlined", "elevated") == "elevated"
  when {
    elevated && icon -> KitSuggestionChipElevatedIcon(Modifier, enabled = enabled, label = c.label, onChipClick = c.onClick)
    elevated -> KitSuggestionChipElevated(Modifier, enabled = enabled, label = c.label, onChipClick = c.onClick)
    icon -> KitSuggestionChipIcon(Modifier, enabled = enabled, label = c.label, onChipClick = c.onClick)
    else -> KitSuggestionChip(Modifier, enabled = enabled, label = c.label, onChipClick = c.onClick)
  }
}
