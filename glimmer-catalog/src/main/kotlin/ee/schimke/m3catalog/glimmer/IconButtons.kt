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
  noReference = "Glimmer publishes no Figma kit; see Button.",
  caption = "An action carried by its icon alone.",
)
@Preview(
  device = AI_GLASSES_DEVICE_SPEC,
  showBackground = true,
  backgroundColor = ADDITIVE_ZERO_BACKGROUND,
)
@Composable
fun IconButtonSticker() = Sticker {
  // `counted`'s label is unused here — an icon button has none — but its handler is the point: a
  // live click has to reach the component rather than a dead lambda.
  val c = counted("")
  IconButton(onClick = c.onClick) { Icon(StarIcon, "Favourite") }
}

@CatalogComponent(
  id = "IconToggleButton",
  noReference = "Glimmer publishes no Figma kit; see Button.",
  caption = "An icon button that holds its checked state.",
)
@Preview(
  device = AI_GLASSES_DEVICE_SPEC,
  showBackground = true,
  backgroundColor = ADDITIVE_ZERO_BACKGROUND,
)
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
@Preview(
  device = AI_GLASSES_DEVICE_SPEC,
  showBackground = true,
  backgroundColor = ADDITIVE_ZERO_BACKGROUND,
)
@Composable
fun IconToggleButtonCheckedSticker() = Sticker {
  var checked by remember { mutableStateOf(true) }
  IconToggleButton(checked = checked, onCheckedChange = { checked = it }) {
    Icon(StarIcon, "Favourite")
  }
}
