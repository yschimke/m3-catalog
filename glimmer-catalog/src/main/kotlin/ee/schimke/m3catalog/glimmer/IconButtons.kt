@file:CatalogGroup(name = "Icon button", section = "Actions")

package ee.schimke.m3catalog.glimmer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
@Preview(showBackground = true, backgroundColor = ADDITIVE_ZERO_BACKGROUND)
@Composable
fun IconButtonSticker() = Sticker {
  // `counted`'s label is unused here — an icon button has none — but its handler is the point: a
  // live click has to reach the component rather than a dead lambda.
  val c = counted("")
  IconButton(onClick = c.onClick) { Icon(StarIcon, "Favourite") }
}

@CatalogComponent(
  id = "IconToggleButton",
  // `Toggle=False, State=Enabled` in the `Toggle` set (`40000113:4149`), also under
  // `icon buttons`. That the set carries `Toggle=` at all is what makes it the icon-toggle
  // rather than a second plain icon button.
  reference = "figma:HKfLClZDLRyMhf4IQQLna8/40000113:4150",
  caption = "An icon button that holds its checked state.",
)
@Preview(showBackground = true, backgroundColor = ADDITIVE_ZERO_BACKGROUND)
@Composable
fun IconToggleButtonSticker() = Sticker {
  var checked by remember { mutableStateOf(false) }
  IconToggleButton(checked = checked, onCheckedChange = { checked = it }) {
    Icon(StarIcon, "Favourite")
  }
}

@CatalogVariant(
  of = "IconToggleButton",
  state = "checked",
  caption = "Checked, where the container reads as lit rather than outlined.",
)
@Preview(showBackground = true, backgroundColor = ADDITIVE_ZERO_BACKGROUND)
@Composable
fun IconToggleButtonCheckedSticker() = Sticker {
  var checked by remember { mutableStateOf(true) }
  IconToggleButton(checked = checked, onCheckedChange = { checked = it }) {
    Icon(StarIcon, "Favourite")
  }
}
