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

// --- Generated sticker bodies, from uid-stickers/RadioButtons.kt ---

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
