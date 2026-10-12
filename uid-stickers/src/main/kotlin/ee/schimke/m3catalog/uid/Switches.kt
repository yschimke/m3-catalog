@file:CatalogGroup(name = "Switch", section = "Selection")

package ee.schimke.m3catalog.uid

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.InteractionPreview
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.InteractionStates
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogChoice
import ee.schimke.m3catalog.toggleable
import generated.uibuilder.KitSwitch
import generated.uibuilder.KitSwitchDisabled
import generated.uibuilder.KitSwitchDisabledIconOff
import generated.uibuilder.KitSwitchDisabledIconOn
import generated.uibuilder.KitSwitchIconOff
import generated.uibuilder.KitSwitchIconOn

// `stickers/switch.uid`: `m3/switch` with `checked` and `enabled` bound and an action-free
// `checkedChange` the shell flips `checked` on. The thumb icon follows the selection (a check when
// on, a cross when off), so it is a cell per glyph, `Icon On` and `Icon Off`, chosen by the live
// `checked`; the `Disabled` cells drop the event, so they export `onCheckedChange = null` as the
// hand-written sticker passes.

@CatalogComponent(
  id = "Switch/On",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/54446:25290",
  caption = "Toggle a single setting on or off. Off, thumb icon and disabled fold in.",
)
// A shorter gap than the default: the point is the thumb resolving under quick, successive
// toggles, which a long settle between taps would turn back into two separate slow animations.
@InteractionPreview(
  targets = [0, 0, 0],
  gapMs = 420,
  caption =
    "Toggle quickly. The thumb's travel resolves through Expressive's FastSpatial motion spec — " +
      "fast enough to feel immediate, still springy enough to overshoot slightly on arrival.",
)
@CatalogModes
@OverrideVariant(name = "off", strings = ["state=off"])
@OverrideVariant(name = "icon", strings = ["content=icon"])
@OverrideVariant(name = "icon-off", strings = ["state=off", "content=icon"])
@OverrideVariant(name = "disabled", strings = ["status=disabled"])
@OverrideVariant(name = "disabled-off", strings = ["state=off", "status=disabled"])
@OverrideVariant(name = "disabled-icon", strings = ["content=icon", "status=disabled"])
@OverrideVariant(
  name = "disabled-icon-off",
  strings = ["state=off", "content=icon", "status=disabled"],
)
@InteractionStates
@ee.schimke.m3catalog.SwitchOnExhaustiveKitCells
@Composable
fun SwitchOn() = Sticker {
  // The knobs, read in the hand-written sticker's order: status, selection, then the thumb icon.
  val enabled = switchStatus() != "disabled"
  var checked by toggleable(switchSelection() == "on")
  val icon = switchIcon()
  val toggle = { checked = !checked }
  val m = Modifier
  when {
    !enabled && !icon -> KitSwitchDisabled(m, checked = checked, enabled = false)
    !enabled && checked -> KitSwitchDisabledIconOn(m, checked = checked, enabled = false)
    !enabled -> KitSwitchDisabledIconOff(m, checked = checked, enabled = false)
    !icon -> KitSwitch(m, checked = checked, enabled = true, onSwitchCheckedChange = toggle)
    checked -> KitSwitchIconOn(m, checked = checked, enabled = true, onSwitchCheckedChange = toggle)
    else -> KitSwitchIconOff(m, checked = checked, enabled = true, onSwitchCheckedChange = toggle)
  }
}

@Composable private fun switchSelection(): String = catalogChoice("state", "on", "on", "off")

@Composable
private fun switchStatus(): String = catalogChoice("status", "enabled", "enabled", "disabled")

@Composable
private fun switchIcon(): Boolean = catalogChoice("content", "none", "none", "icon") == "icon"
