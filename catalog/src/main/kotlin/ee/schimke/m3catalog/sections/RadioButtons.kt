@file:CatalogGroup(name = "Radio button", section = "Selection")
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogChoice
import ee.schimke.m3catalog.toggleable

// Selection × status, the same two axes the kit gives the checkbox. A lone radio button cannot be
// deselected by clicking it, so the live lane toggles it rather than pretending a single-button
// group behaves like a real one — the sticker is the control, not the group.
//
// As with the checkbox, Compose has no `error` flag; the kit's error radio is the error role
// driving
// both the selected and unselected outline.

@Composable
private fun radioSelection(): String = catalogChoice("state", "selected", "selected", "unselected")

@Composable
private fun radioStatus(): String =
  catalogChoice("status", "enabled", "enabled", "disabled", "error")

@Composable
private fun radioColors(): RadioButtonColors =
  if (radioStatus() == "error") {
    val scheme = MaterialTheme.colorScheme
    RadioButtonDefaults.colors(selectedColor = scheme.error, unselectedColor = scheme.error)
  } else {
    RadioButtonDefaults.colors()
  }

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
@Composable
fun RadioSelected() = Sticker {
  val enabled = radioStatus() != "disabled"
  var selected by toggleable(radioSelection() == "selected")
  RadioButton(
    selected = selected,
    onClick = if (enabled) ({ selected = !selected }) else null,
    enabled = enabled,
    colors = radioColors(),
  )
}
