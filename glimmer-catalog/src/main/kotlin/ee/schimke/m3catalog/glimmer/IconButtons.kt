@file:CatalogGroup(name = "Icon button", section = "Actions")

package ee.schimke.m3catalog.glimmer

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.MicOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.xr.glimmer.Icon
import androidx.xr.glimmer.IconButton
import androidx.xr.glimmer.IconToggleButton
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant

@CatalogComponent(
  id = "IconButton",
  // `State=Enabled` in the `Icon button` set (`5315:4650`) under the kit's `icon buttons`
  // frame. `State=` is its only axis, matching a component with no size or checked axis.
  reference = "figma:HKfLClZDLRyMhf4IQQLna8/5315:4651",
  caption = "An action carried by its icon alone.",
)
@GlimmerStates
@Preview
@Composable
fun IconButtonSticker() = Sticker {
  // `counted`'s label is unused here — an icon button has none — but its handler is the point: a
  // live click has to reach the component rather than a dead lambda.
  val c = counted("")
  IconButton(onClick = c.onClick, enabled = glimmerEnabled()) {
    Icon(Icons.Rounded.MicOff, stringResource(R.string.cd_unmute_microphone))
  }
}

@CatalogComponent(
  id = "IconToggleButton",
  // `Toggle=False, State=Enabled` in the `Toggle` set (`40000113:4149`), also under
  // `icon buttons`. That the set carries `Toggle=` at all is what makes it the icon-toggle
  // rather than a second plain icon button.
  reference = "figma:HKfLClZDLRyMhf4IQQLna8/40000113:4150",
  caption = "An icon button that holds its checked state.",
)
@GlimmerStates
@ee.schimke.m3catalog.glimmer.IconToggleButtonStickerExhaustiveKitCells
@Preview
@Composable
fun IconToggleButtonSticker() = Sticker {
  val initiallyChecked = glimmerChecked()
  var checked by remember(initiallyChecked) { mutableStateOf(initiallyChecked) }
  IconToggleButton(
    checked = checked,
    onCheckedChange = { checked = it },
    enabled = glimmerEnabled(),
  ) {
    Icon(
      if (checked) Icons.Rounded.Mic else Icons.Rounded.MicOff,
      stringResource(R.string.cd_microphone),
    )
  }
}

// Same as `ToggleButton` above: the checked variant is the kit's `Toggle=True, State=Enabled` cell,
// and its `Focused` / `Pressed` crossings are generated exact cells on the base sticker rather than
// variants here. See #374.
@CatalogVariant(
  of = "IconToggleButton",
  state = "checked",
  // `Toggle=True` in the kit's vocabulary. See the note on `ToggleButtonCheckedSticker`.
  kitAxis = "Toggle",
  kitValue = "True",
  caption = "Checked, where the container reads as lit rather than outlined.",
)
@Preview
@Composable
fun IconToggleButtonCheckedSticker() = Sticker {
  var checked by remember { mutableStateOf(true) }
  IconToggleButton(
    checked = checked,
    onCheckedChange = { checked = it },
    enabled = glimmerEnabled(),
  ) {
    Icon(
      if (checked) Icons.Rounded.Mic else Icons.Rounded.MicOff,
      stringResource(R.string.cd_microphone),
    )
  }
}
