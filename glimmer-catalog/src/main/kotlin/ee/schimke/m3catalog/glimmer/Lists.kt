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
  // `Type=1-line, State=Enabled` in the kit's `List Item` set (`384:4197`), whose axes are
  // `Type=1-line | 2-line | Card` x `State=`. One line is the base because this sticker
  // draws `ListItem { Text(…) }` and nothing else; the supporting-label variant below is a
  // rendition of the kit's `2-line` (`384:4191`).
  reference = "figma:HKfLClZDLRyMhf4IQQLna8/384:4195",
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
