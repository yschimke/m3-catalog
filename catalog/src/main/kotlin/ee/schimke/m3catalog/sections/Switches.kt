@file:CatalogGroup(name = "Switch", section = "Selection")
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ee.schimke.composeai.overrides.previewOverrideString
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.toggleable

// Three axes, all parameters of one composable: selection (on / off), thumb icon (none / icon), and
// status (enabled / disabled). The kit's icon switch carries a check when on and a cross when off,
// so the thumb content follows the selection rather than being pinned to one glyph.

@Composable private fun switchSelection(): String = previewOverrideString("state", "on")

@Composable private fun switchStatus(): String = previewOverrideString("status", "enabled")

@Composable private fun switchIcon(): Boolean = previewOverrideString("content", "none") == "icon"

@CatalogComponent(
  id = "Switch/On",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/54446:25289",
  caption = "Toggle a single setting on or off. Off, thumb icon and disabled fold in.",
)
@CatalogModes
@OverrideVariant(name = "off", strings = ["state=off"])
@OverrideVariant(name = "icon", strings = ["content=icon"])
@OverrideVariant(name = "icon-off", strings = ["state=off", "content=icon"])
@OverrideVariant(name = "disabled", strings = ["status=disabled"])
@OverrideVariant(name = "disabled-off", strings = ["state=off", "status=disabled"])
@OverrideVariant(name = "disabled-icon", strings = ["content=icon", "status=disabled"])
@OverrideVariant(
  name = "disabled-icon-off",
  strings = ["state=off", "content=icon", "status=disabled"],
)
@Composable
fun SwitchOn() = Sticker {
  val enabled = switchStatus() != "disabled"
  val (checked, set) = toggleable(switchSelection() == "on")
  Switch(
    checked = checked,
    onCheckedChange = if (enabled) set else null,
    enabled = enabled,
    thumbContent =
      if (!switchIcon()) null
      else
        ({
          Icon(
            if (checked) Icons.Filled.Check else Icons.Filled.Close,
            contentDescription = null,
            modifier = Modifier.size(SwitchDefaults.IconSize),
          )
        }),
  )
}
