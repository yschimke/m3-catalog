@file:CatalogGroup(name = "Checkbox", section = "Selection")
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
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

// The kit's checkbox matrix is selection × status: three selection values (checked, unchecked,
// indeterminate) against three statuses (enabled, disabled, error). Both are states of one kit
// component, so both fold onto one sticker as knobs — even though indeterminate reaches for
// `TriStateCheckbox` internally, which is a Compose implementation detail rather than a second
// component.
//
// Compose has no `error` flag on `Checkbox`; the kit's error checkbox is expressed by driving the
// container and outline from the error role, which is what `errorCheckboxColors` does.
//
// The kit specifies 4dp around its checkbox drawing; Compose's actual Checkbox node reports and
// renders 2dp (`CheckboxDefaultPadding`). That constant is private inside material3 and reachable
// from no parameter, so matching 4dp here would mean hand-drawing a checkbox rather than rendering
// the component. Issue #87 tracks the upstream gap, and `.design-parity.json` accepts only this
// exact component/token/4-vs-2 tuple: any changed value becomes blocking again.

@Composable
private fun checkboxSelection(): String =
  catalogChoice("state", "checked", "checked", "unchecked", "indeterminate")

@Composable
private fun checkboxStatus(): String =
  catalogChoice("status", "enabled", "enabled", "disabled", "error")

// --- Generated sticker bodies, from uid-stickers/Checkboxes.kt ---

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
