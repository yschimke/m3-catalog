@file:CatalogGroup(name = "Radio button", section = "Selection")
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.selectable

@CatalogComponent(
  id = "RadioButton/Selected",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/51739:4608",
  caption = "Select exactly one option from a set.",
)
@CatalogModes
@Composable
fun RadioSelected() = Sticker {
  val (selected, select) = selectable(0)
  Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
    repeat(2) { index -> RadioButton(selected = index == selected, onClick = { select(index) }) }
  }
}

@CatalogVariant(of = "RadioButton/Selected", state = "disabled")
@CatalogModes
@Composable
fun RadioDisabled() = Sticker { RadioButton(selected = true, onClick = null, enabled = false) }
