@file:CatalogGroup(name = "Checkbox", section = "Selection")
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.ui.state.ToggleableState
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.toggleable

// A checkbox carries state, so it owns it: the primary sticker is checked, and the unchecked and
// disabled states fold under it as variants.

@CatalogComponent(id = "Checkbox/Checked", caption = "Select one or more items from a set.")
@CatalogModes
@Composable
fun CheckboxChecked() = Sticker {
  val (checked, set) = toggleable(true)
  Checkbox(checked = checked, onCheckedChange = set)
}

@CatalogVariant(of = "Checkbox/Checked", state = "unchecked")
@CatalogModes
@Composable
fun CheckboxUnchecked() = Sticker {
  val (checked, set) = toggleable(false)
  Checkbox(checked = checked, onCheckedChange = set)
}

@CatalogVariant(of = "Checkbox/Checked", state = "indeterminate", caption = "A partial selection.")
@CatalogModes
@Composable
fun CheckboxIndeterminate() = Sticker {
  TriStateCheckbox(state = ToggleableState.Indeterminate, onClick = {})
}

@CatalogVariant(of = "Checkbox/Checked", state = "disabled")
@CatalogModes
@Composable
fun CheckboxDisabled() = Sticker {
  Checkbox(checked = true, onCheckedChange = null, enabled = false)
}
