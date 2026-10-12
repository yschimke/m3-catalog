@file:CatalogGroup(name = "Radio button", section = "Selection")

package ee.schimke.m3catalog.uid

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.InteractionStates
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogChoice
import ee.schimke.m3catalog.toggleable
import generated.uibuilder.KitRadioButton
import generated.uibuilder.KitRadioButtonDisabled
import generated.uibuilder.KitRadioButtonError

// `stickers/radio-button.uid`: `m3/radio-button` with `selected` and `enabled` bound and an
// action-free click the shell toggles `selected` on. The kit's error radio is the `Error` cell,
// the error role on `selectedColor` and `unselectedColor`; the `Disabled` cell drops the click, so
// it exports `onClick = null` as the hand-written sticker passes, and with it the 48dp touch
// target a disabled radio does not reserve.

@CatalogComponent(
  id = "RadioButton/Selected",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/51739:4609",
  caption = "Select exactly one option from a set. Unselected, disabled and error fold in.",
)
@CatalogModes
@OverrideVariant(name = "unselected", strings = ["state=unselected"])
@OverrideVariant(name = "disabled", strings = ["status=disabled"])
@OverrideVariant(name = "disabled-unselected", strings = ["state=unselected", "status=disabled"])
@OverrideVariant(name = "error", strings = ["status=error"])
@OverrideVariant(name = "error-unselected", strings = ["state=unselected", "status=error"])
@InteractionStates
@ee.schimke.m3catalog.RadioSelectedExhaustiveKitCells
@Composable
fun RadioSelected() = Sticker {
  // The knobs, read in the hand-written sticker's order: status, selection, then status again.
  val enabled = radioStatus() != "disabled"
  var selected by toggleable(radioSelection() == "selected")
  val error = radioStatus() == "error"
  val m = Modifier
  when {
    !enabled -> KitRadioButtonDisabled(m, selected = selected, enabled = false)
    error ->
      KitRadioButtonError(
        m,
        selected = selected,
        enabled = true,
        onRadioClick = { selected = !selected },
      )
    else ->
      KitRadioButton(
        m,
        selected = selected,
        enabled = true,
        onRadioClick = { selected = !selected },
      )
  }
}

@Composable
private fun radioSelection(): String = catalogChoice("state", "selected", "selected", "unselected")

@Composable
private fun radioStatus(): String =
  catalogChoice("status", "enabled", "enabled", "disabled", "error")
