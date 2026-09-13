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

// Two things the kit decides for these stickers, both of them #381:
//
//  * The `1-line` cell draws its ICON slot filled and labels it "Title", so the base sticker does
//    too. A bare `ListItem { Text("Primary label") }` matched the cell's 80dp height and nothing
//    else about it.
//  * `ListItem` fills the width it is given, and since #376 that width is the 960dp glasses display
//    used as a wrap sandbox — 2.3x the 420dp column the kit draws the component in. `ContentFrame`
//    is that bound, on a frame rather than on the component for the reason `AGENTS.md` gives.

@CatalogComponent(
  id = "ListItem",
  // `Type=1-line, State=Enabled` in the kit's `List Item` set (`384:4197`), whose axes are
  // `Type=1-line | 2-line | Card` x `State=`. One line is the base because this sticker
  // draws one line; the supporting-label variant below is a rendition of the kit's `2-line`
  // (`384:4191`).
  reference = "figma:HKfLClZDLRyMhf4IQQLna8/384:4195",
  caption = "One row of a list. The supporting label and icon slots fold in.",
)
@Preview(showBackground = true, backgroundColor = ADDITIVE_ZERO_BACKGROUND)
@Composable
fun ListItemSticker() = Sticker {
  ContentFrame { ListItem(leadingIcon = { Icon(StarIcon, "Favourite") }) { Text("Title") } }
}

@CatalogVariant(
  of = "ListItem",
  props = ["content=supporting-label"],
  caption = "A second line under the primary label.",
)
@Preview(showBackground = true, backgroundColor = ADDITIVE_ZERO_BACKGROUND)
@Composable
fun ListItemSupportingSticker() = Sticker {
  ContentFrame { ListItem(supportingLabel = { Text("Subtitle") }) { Text("Title") } }
}

@CatalogVariant(
  of = "ListItem",
  props = ["content=supporting-label-leading-icon"],
  caption = "Supporting label and a leading icon.",
)
@Preview(showBackground = true, backgroundColor = ADDITIVE_ZERO_BACKGROUND)
@Composable
fun ListItemSupportingIconSticker() = Sticker {
  ContentFrame {
    ListItem(
      supportingLabel = { Text("Subtitle") },
      leadingIcon = { Icon(StarIcon, "Favourite") },
    ) {
      Text("Title")
    }
  }
}
