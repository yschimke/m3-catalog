@file:CatalogGroup(name = "Chips", section = "Selection")

package ee.schimke.m3catalog.uid

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.HoverFocusStates
import ee.schimke.m3catalog.InteractionStates
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogChoice
import ee.schimke.m3catalog.catalogText
import ee.schimke.m3catalog.counted
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.chip_add_to_calendar
import ee.schimke.m3catalog.generated.resources.chip_unread
import ee.schimke.m3catalog.toggleable
import generated.uibuilder.KitAssistChip
import generated.uibuilder.KitAssistChipAvatar
import generated.uibuilder.KitAssistChipBrand
import generated.uibuilder.KitAssistChipElevated
import generated.uibuilder.KitAssistChipElevatedAvatar
import generated.uibuilder.KitAssistChipElevatedBrand
import generated.uibuilder.KitAssistChipElevatedFavicon
import generated.uibuilder.KitAssistChipElevatedIcon
import generated.uibuilder.KitAssistChipFavicon
import generated.uibuilder.KitAssistChipIcon
import generated.uibuilder.KitFilterChip
import generated.uibuilder.KitFilterChipCaret
import generated.uibuilder.KitFilterChipCheck
import generated.uibuilder.KitFilterChipCheckCaret
import generated.uibuilder.KitFilterChipElevated
import generated.uibuilder.KitFilterChipElevatedCaret
import generated.uibuilder.KitFilterChipElevatedCheck
import generated.uibuilder.KitFilterChipElevatedCheckCaret
import generated.uibuilder.KitInputChip
import generated.uibuilder.KitInputChipAvatar
import generated.uibuilder.KitInputChipAvatarClosing
import generated.uibuilder.KitInputChipBrand
import generated.uibuilder.KitInputChipBrandClosing
import generated.uibuilder.KitInputChipClosing
import generated.uibuilder.KitInputChipFavicon
import generated.uibuilder.KitInputChipFaviconClosing
import generated.uibuilder.KitInputChipIcon
import generated.uibuilder.KitInputChipIconClosing
import org.jetbrains.compose.resources.stringResource

// The assist, filter and input chips, each one document (`stickers/assist-chip.uid`,
// `filter-chip.uid`, `input-chip.uid`) whose label, `enabled` and (for the filter and input chips)
// `selected` are bindings and whose click is an action-free event. The kit's structural axes are
// cells of each document: `Elevated` swaps the node to its elevated sibling function, a leading
// glyph (event icon, brand stars, person favicon, avatar) is an `addNode` into the chip's leading
// slot, the filter chip's check and caret and the input chip's close icon likewise. The shells read
// the same knobs, in the same order, as the hand-written stickers and pick the cell; selection is
// the shell's own state, toggled by the click event.

@Composable
private fun chipElevated(): Boolean =
  catalogChoice("style", "outlined", "outlined", "elevated") == "elevated"

@Composable
private fun chipLeading(): String =
  catalogChoice("leading", "none", "none", "icon", "avatar", "brand", "favicon")

@Composable
private fun chipEnabled(): Boolean =
  catalogChoice("status", "enabled", "enabled", "disabled") != "disabled"

@CatalogComponent(
  id = "Chip/Assist",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/53923:28267",
  caption = "A smart action related to the content. Elevated, leading icon and disabled fold in.",
)
@CatalogModes
@OverrideVariant(
  name = "icon",
  strings = ["leading=icon"],
  kitAxis = "Configuration",
  kitValue = "Label & icon",
)
@OverrideVariant(
  name = "brand-icon",
  strings = ["leading=brand"],
  kitAxis = "Configuration",
  kitValue = "Label & brand icon",
)
@OverrideVariant(
  name = "favicon",
  strings = ["leading=favicon"],
  kitAxis = "Configuration",
  kitValue = "Label & favicon",
)
@OverrideVariant(name = "elevated", strings = ["style=elevated"])
@OverrideVariant(name = "elevated-icon", strings = ["style=elevated", "leading=icon"])
@OverrideVariant(name = "disabled", strings = ["status=disabled"])
@OverrideVariant(name = "disabled-icon", strings = ["leading=icon", "status=disabled"])
@InteractionStates
@ee.schimke.m3catalog.AssistChipStickerExhaustiveKitCells
@Composable
fun AssistChipSticker() = Sticker {
  val c = counted(catalogText("label", stringResource(Res.string.chip_add_to_calendar)))
  val enabled = chipEnabled()
  val elevated = chipElevated()
  val m = Modifier
  when (chipLeading()) {
    "icon" ->
      if (elevated)
        KitAssistChipElevatedIcon(m, enabled = enabled, label = c.label, onChipClick = c.onClick)
      else KitAssistChipIcon(m, enabled = enabled, label = c.label, onChipClick = c.onClick)
    "brand" ->
      if (elevated)
        KitAssistChipElevatedBrand(m, enabled = enabled, label = c.label, onChipClick = c.onClick)
      else KitAssistChipBrand(m, enabled = enabled, label = c.label, onChipClick = c.onClick)
    "favicon" ->
      if (elevated)
        KitAssistChipElevatedFavicon(m, enabled = enabled, label = c.label, onChipClick = c.onClick)
      else KitAssistChipFavicon(m, enabled = enabled, label = c.label, onChipClick = c.onClick)
    "avatar" ->
      if (elevated)
        KitAssistChipElevatedAvatar(m, enabled = enabled, label = c.label, onChipClick = c.onClick)
      else KitAssistChipAvatar(m, enabled = enabled, label = c.label, onChipClick = c.onClick)
    else ->
      if (elevated)
        KitAssistChipElevated(m, enabled = enabled, label = c.label, onChipClick = c.onClick)
      else KitAssistChip(m, enabled = enabled, label = c.label, onChipClick = c.onClick)
  }
}

@CatalogComponent(
  id = "Chip/Filter",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/53923:28465",
  caption =
    "Filters content; carries a selected state. Unselected, elevated and disabled fold in. The " +
      "check mark is the selected state's leading icon, so it follows selection rather than a knob.",
)
@CatalogModes
@OverrideVariant(name = "unselected", strings = ["state=unselected"])
@OverrideVariant(
  name = "label-only",
  strings = ["configuration=label"],
  kitAxis = "Configuration",
  kitValue = "Label only",
)
@OverrideVariant(name = "elevated", strings = ["style=elevated"])
@OverrideVariant(name = "elevated-unselected", strings = ["style=elevated", "state=unselected"])
@OverrideVariant(name = "disabled", strings = ["status=disabled"])
@OverrideVariant(name = "disabled-unselected", strings = ["state=unselected", "status=disabled"])
@OverrideVariant(name = "trailing-icon", booleans = ["trailing=true"])
@InteractionStates
@ee.schimke.m3catalog.FilterChipStickerExhaustiveKitCells
@Composable
fun FilterChipSticker(trailing: Boolean = false) = Sticker {
  var selected by
    toggleable(catalogChoice("state", "selected", "selected", "unselected") == "selected")
  val enabled = chipEnabled()
  val labelText = catalogText("label", stringResource(Res.string.chip_unread))
  val showLeading = catalogChoice("configuration", "icon", "icon", "label") == "icon"
  // The check mark is the selected state's leading icon: a cell, chosen by the live selection.
  val check = selected && showLeading
  val elevated = chipElevated()
  val toggle = { selected = !selected }
  val m = Modifier
  when {
    elevated && check && trailing ->
      KitFilterChipElevatedCheckCaret(
        m,
        selected = selected,
        enabled = enabled,
        label = labelText,
        onChipClick = toggle,
      )
    elevated && check ->
      KitFilterChipElevatedCheck(
        m,
        selected = selected,
        enabled = enabled,
        label = labelText,
        onChipClick = toggle,
      )
    elevated && trailing ->
      KitFilterChipElevatedCaret(
        m,
        selected = selected,
        enabled = enabled,
        label = labelText,
        onChipClick = toggle,
      )
    elevated ->
      KitFilterChipElevated(
        m,
        selected = selected,
        enabled = enabled,
        label = labelText,
        onChipClick = toggle,
      )
    check && trailing ->
      KitFilterChipCheckCaret(
        m,
        selected = selected,
        enabled = enabled,
        label = labelText,
        onChipClick = toggle,
      )
    check ->
      KitFilterChipCheck(
        m,
        selected = selected,
        enabled = enabled,
        label = labelText,
        onChipClick = toggle,
      )
    trailing ->
      KitFilterChipCaret(
        m,
        selected = selected,
        enabled = enabled,
        label = labelText,
        onChipClick = toggle,
      )
    else ->
      KitFilterChip(
        m,
        selected = selected,
        enabled = enabled,
        label = labelText,
        onChipClick = toggle,
      )
  }
}

@CatalogComponent(
  id = "Chip/Input",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/53923:28082",
  caption =
    "A discrete piece of user input, removable. Leading icon, avatar, selected and disabled fold in.",
)
@CatalogModes
@OverrideVariant(
  name = "icon",
  strings = ["leading=icon"],
  kitAxis = "Configuration",
  kitValue = "Label & leading icon",
)
@OverrideVariant(name = "avatar", strings = ["leading=avatar"])
@OverrideVariant(name = "selected", strings = ["state=selected"])
@OverrideVariant(name = "selected-avatar", strings = ["state=selected", "leading=avatar"])
@OverrideVariant(name = "disabled", strings = ["status=disabled"])
@OverrideVariant(name = "disabled-avatar", strings = ["leading=avatar", "status=disabled"])
@OverrideVariant(
  name = "no-closing-icon",
  booleans = ["closing=false"],
  kitAxis = "Show closing icon",
  kitValue = "false",
)
@HoverFocusStates
@ee.schimke.m3catalog.InputChipStickerExhaustiveKitCells
@Composable
fun InputChipSticker(closing: Boolean = true) = Sticker {
  var selected by
    toggleable(catalogChoice("state", "unselected", "selected", "unselected") == "selected")
  val label = catalogText("label", stringResource(Res.string.chip_unread))
  val enabled = chipEnabled()
  val toggle = { selected = !selected }
  val m = Modifier
  when (chipLeading()) {
    "icon" ->
      if (closing)
        KitInputChipIconClosing(
          m,
          selected = selected,
          enabled = enabled,
          label = label,
          onChipClick = toggle,
        )
      else
        KitInputChipIcon(
          m,
          selected = selected,
          enabled = enabled,
          label = label,
          onChipClick = toggle,
        )
    "brand" ->
      if (closing)
        KitInputChipBrandClosing(
          m,
          selected = selected,
          enabled = enabled,
          label = label,
          onChipClick = toggle,
        )
      else
        KitInputChipBrand(
          m,
          selected = selected,
          enabled = enabled,
          label = label,
          onChipClick = toggle,
        )
    "favicon" ->
      if (closing)
        KitInputChipFaviconClosing(
          m,
          selected = selected,
          enabled = enabled,
          label = label,
          onChipClick = toggle,
        )
      else
        KitInputChipFavicon(
          m,
          selected = selected,
          enabled = enabled,
          label = label,
          onChipClick = toggle,
        )
    "avatar" ->
      if (closing)
        KitInputChipAvatarClosing(
          m,
          selected = selected,
          enabled = enabled,
          label = label,
          onChipClick = toggle,
        )
      else
        KitInputChipAvatar(
          m,
          selected = selected,
          enabled = enabled,
          label = label,
          onChipClick = toggle,
        )
    else ->
      if (closing)
        KitInputChipClosing(
          m,
          selected = selected,
          enabled = enabled,
          label = label,
          onChipClick = toggle,
        )
      else
        KitInputChip(m, selected = selected, enabled = enabled, label = label, onChipClick = toggle)
  }
}
