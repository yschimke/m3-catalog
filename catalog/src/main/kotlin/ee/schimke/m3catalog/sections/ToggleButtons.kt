@file:CatalogGroup(name = "Toggle buttons", section = "Actions")
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.material3.ElevatedToggleButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.OutlinedToggleButton
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.TonalToggleButton
import androidx.compose.runtime.Composable
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.toggleable

// The expressive toggle button: a button that stays on. It owns its checked state.

@CatalogComponent(
  id = "ToggleButton/Filled",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/57994:2328",
  caption = "A button whose selection persists.",
)
@CatalogModes
@Composable
fun ToggleButtonSticker() = Sticker {
  val (on, set) = toggleable(true)
  ToggleButton(checked = on, onCheckedChange = set) { Text("On") }
}

@CatalogVariant(of = "ToggleButton/Filled", props = ["emphasis=tonal"])
@CatalogModes
@Composable
fun TonalToggleButtonSticker() = Sticker {
  val (on, set) = toggleable(true)
  TonalToggleButton(checked = on, onCheckedChange = set) { Text("Tonal") }
}

@CatalogVariant(of = "ToggleButton/Filled", props = ["emphasis=outlined"])
@CatalogModes
@Composable
fun OutlinedToggleButtonSticker() = Sticker {
  val (on, set) = toggleable(false)
  OutlinedToggleButton(checked = on, onCheckedChange = set) { Text("Outlined") }
}

@CatalogVariant(of = "ToggleButton/Filled", props = ["emphasis=elevated"])
@CatalogModes
@Composable
fun ElevatedToggleButtonSticker() = Sticker {
  val (on, set) = toggleable(false)
  ElevatedToggleButton(checked = on, onCheckedChange = set) { Text("Elevated") }
}
