@file:CatalogGroup(name = "Switch", section = "Selection")
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.toggleable

@CatalogComponent(
  id = "Switch/On",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/54446:25289",
  caption = "Toggle a single setting on or off.",
)
@CatalogModes
@Composable
fun SwitchOn() = Sticker {
  val (checked, set) = toggleable(true)
  Switch(checked = checked, onCheckedChange = set)
}

@CatalogVariant(of = "Switch/On", state = "off")
@CatalogModes
@Composable
fun SwitchOff() = Sticker {
  val (checked, set) = toggleable(false)
  Switch(checked = checked, onCheckedChange = set)
}

@CatalogVariant(of = "Switch/On", props = ["content=icon"], caption = "With an icon in the thumb.")
@CatalogModes
@Composable
fun SwitchWithIcon() = Sticker {
  val (checked, set) = toggleable(true)
  Switch(
    checked = checked,
    onCheckedChange = set,
    thumbContent = {
      Icon(
        Icons.Filled.Check,
        contentDescription = null,
        modifier = Modifier.size(SwitchDefaults.IconSize),
      )
    },
  )
}

@CatalogVariant(of = "Switch/On", state = "disabled")
@CatalogModes
@Composable
fun SwitchDisabled() = Sticker { Switch(checked = true, onCheckedChange = null, enabled = false) }
