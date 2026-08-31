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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.InteractionPreview
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.InteractionStates
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogChoice
import ee.schimke.m3catalog.toggleable

// Three axes, all parameters of one composable: selection (on / off), thumb icon (none / icon), and
// status (enabled / disabled). The kit's icon switch carries a check when on and a cross when off,
// so the thumb content follows the selection rather than being pinned to one glyph.

@Composable private fun switchSelection(): String = catalogChoice("state", "on", "on", "off")

@Composable
private fun switchStatus(): String = catalogChoice("status", "enabled", "enabled", "disabled")

@Composable
private fun switchIcon(): Boolean = catalogChoice("content", "none", "none", "icon") == "icon"

@CatalogComponent(
  id = "Switch/On",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/54446:25290",
  caption = "Toggle a single setting on or off. Off, thumb icon and disabled fold in.",
)
// A shorter gap than the default: the point is the thumb resolving under quick, successive
// toggles, which a long settle between taps would turn back into two separate slow animations.
@InteractionPreview(
  targets = [0, 0, 0],
  gapMs = 420,
  caption =
    "Toggle quickly. The thumb's travel resolves through Expressive's FastSpatial motion spec — " +
      "fast enough to feel immediate, still springy enough to overshoot slightly on arrival.",
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
@InteractionStates
@ee.schimke.m3catalog.SwitchOnExhaustiveKitCells
@Composable
fun SwitchOn() = Sticker {
  val enabled = switchStatus() != "disabled"
  var checked by toggleable(switchSelection() == "on")
  Switch(
    checked = checked,
    onCheckedChange = if (enabled) ({ checked = it }) else null,
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
