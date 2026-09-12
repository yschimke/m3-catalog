@file:CatalogGroup(name = "List item", section = "Containment")

package ee.schimke.m3catalog.glimmer

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.xr.glimmer.Icon
import androidx.xr.glimmer.ListItem
import androidx.xr.glimmer.Text
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant

@CatalogComponent(
  id = "ListItem",
  noReference = "Glimmer publishes no Figma kit; see Button.",
  caption = "One row of a list. The supporting label and icon slots fold in.",
)
@Preview(
  device = AI_GLASSES_DEVICE_SPEC,
  showBackground = true,
  backgroundColor = ADDITIVE_ZERO_BACKGROUND,
)
@Composable
fun ListItemSticker() = Sticker { ListItem { Text("Primary label") } }

@CatalogVariant(
  of = "ListItem",
  props = ["content=supporting-label"],
  caption = "A second line under the primary label.",
)
@Preview(
  device = AI_GLASSES_DEVICE_SPEC,
  showBackground = true,
  backgroundColor = ADDITIVE_ZERO_BACKGROUND,
)
@Composable
fun ListItemSupportingSticker() = Sticker {
  ListItem(supportingLabel = { Text("Supporting label") }) { Text("Primary label") }
}

@CatalogVariant(
  of = "ListItem",
  props = ["content=supporting-label-leading-icon"],
  caption = "Supporting label and a leading icon.",
)
@Preview(
  device = AI_GLASSES_DEVICE_SPEC,
  showBackground = true,
  backgroundColor = ADDITIVE_ZERO_BACKGROUND,
)
@Composable
fun ListItemSupportingIconSticker() = Sticker {
  ListItem(
    supportingLabel = { Text("Supporting label") },
    leadingIcon = { Icon(StarIcon, "Favourite") },
  ) {
    Text("Primary label")
  }
}
