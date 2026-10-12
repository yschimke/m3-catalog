@file:CatalogGroup(name = "Checkbox", section = "Selection")

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
import generated.uibuilder.KitCheckbox
import generated.uibuilder.KitCheckboxDisabled
import generated.uibuilder.KitCheckboxError
import generated.uibuilder.KitCheckboxIndeterminate
import generated.uibuilder.KitCheckboxIndeterminateError

// `stickers/checkbox.uid`: `m3/checkbox` with `checked` and `enabled` bound and an action-free
// `checkedChange` the shell flips `checked` on. Error is the `CheckboxDefaults.colors` bundle
// (error
// container and outline, `onError` check mark); `Disabled` drops the event, so it exports
// `onCheckedChange = null` as the hand-written sticker passes. Indeterminate is
// `m3/tri-state-checkbox` at `state = indeterminate` with no click, as the hand-written sticker
// keeps it put.

@CatalogComponent(
  id = "Checkbox/Checked",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/51859:5629",
  caption =
    "Select one or more items from a set. Unchecked, indeterminate, disabled and error fold in.",
)
@CatalogModes
@OverrideVariant(name = "unchecked", strings = ["state=unchecked"])
@OverrideVariant(name = "indeterminate", strings = ["state=indeterminate"])
@OverrideVariant(name = "disabled", strings = ["status=disabled"])
@OverrideVariant(name = "disabled-unchecked", strings = ["state=unchecked", "status=disabled"])
@OverrideVariant(
  name = "disabled-indeterminate",
  strings = ["state=indeterminate", "status=disabled"],
)
@OverrideVariant(name = "error", strings = ["status=error"])
@OverrideVariant(name = "error-unchecked", strings = ["state=unchecked", "status=error"])
@OverrideVariant(name = "error-indeterminate", strings = ["state=indeterminate", "status=error"])
@InteractionStates
@ee.schimke.m3catalog.CheckboxCheckedExhaustiveKitCells
@Composable
fun CheckboxChecked() = Sticker {
  // The knobs, read in the hand-written sticker's order: status (twice), then selection.
  val enabled = checkboxStatus() != "disabled"
  val error = checkboxStatus() == "error"
  val m = Modifier
  when (checkboxSelection()) {
    "indeterminate" ->
      if (error) KitCheckboxIndeterminateError(m, enabled = enabled)
      else KitCheckboxIndeterminate(m, enabled = enabled)
    else -> {
      var checked by toggleable(checkboxSelection() == "checked")
      val toggle = { checked = !checked }
      when {
        !enabled -> KitCheckboxDisabled(m, checked = checked, enabled = false)
        error ->
          KitCheckboxError(m, checked = checked, enabled = true, onCheckboxCheckedChange = toggle)
        else -> KitCheckbox(m, checked = checked, enabled = true, onCheckboxCheckedChange = toggle)
      }
    }
  }
}

@Composable
private fun checkboxSelection(): String =
  catalogChoice("state", "checked", "checked", "unchecked", "indeterminate")

@Composable
private fun checkboxStatus(): String =
  catalogChoice("status", "enabled", "enabled", "disabled", "error")
