@file:CatalogGroup(name = "Checkbox", section = "Selection")
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.state.ToggleableState
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogChoice
import ee.schimke.m3catalog.toggleable

// The kit's checkbox matrix is selection × status: three selection values (checked, unchecked,
// indeterminate) against three statuses (enabled, disabled, error). Both are states of one kit
// component, so both fold onto one sticker as knobs — even though indeterminate reaches for
// `TriStateCheckbox` internally, which is a Compose implementation detail rather than a second
// component.
//
// Compose has no `error` flag on `Checkbox`; the kit's error checkbox is expressed by driving the
// container and outline from the error role, which is what `errorCheckboxColors` does.

@Composable
private fun checkboxSelection(): String =
  catalogChoice("state", "checked", "checked", "unchecked", "indeterminate")

@Composable
private fun checkboxStatus(): String =
  catalogChoice("status", "enabled", "enabled", "disabled", "error")

@Composable
private fun checkboxColors(): CheckboxColors =
  if (checkboxStatus() == "error") {
    val scheme = MaterialTheme.colorScheme
    CheckboxDefaults.colors(
      checkedColor = scheme.error,
      checkmarkColor = scheme.onError,
      uncheckedColor = scheme.error,
    )
  } else {
    CheckboxDefaults.colors()
  }

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
@Composable
fun CheckboxChecked() = Sticker {
  val enabled = checkboxStatus() != "disabled"
  val colors = checkboxColors()
  when (checkboxSelection()) {
    "indeterminate" ->
      TriStateCheckbox(
        state = ToggleableState.Indeterminate,
        // Cycling the tri-state on the live lane would leave the sticker on whatever the last
        // visitor clicked; the indeterminate frame is the one the kit specifies, so it stays put.
        onClick = null,
        enabled = enabled,
        colors = colors,
      )
    else -> {
      var checked by toggleable(checkboxSelection() == "checked")
      Checkbox(
        checked = checked,
        onCheckedChange = if (enabled) ({ checked = it }) else null,
        enabled = enabled,
        colors = colors,
      )
    }
  }
}
