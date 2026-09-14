@file:CatalogGroup(name = "List item", section = "Containment")

package ee.schimke.m3catalog.glimmer

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
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
//
// The rows are CLICKABLE, and that follows from the kit too: the `List Item` set publishes a
// `State` axis with Pressed on it, which a row that takes no click cannot be a picture of — and a
// Glimmer component only takes focus when something can act on it. `counted` is the handler
// contract the rest of this module already uses. It also gives the row an accessibility ROLE,
// which is half of what #382 reports; the other half is `TitleChip` and `VoiceInputIndicator`,
// where nothing at the call site can supply one.
//
// What is missing and cannot be authored: the kit's `State=Disabled` cell (`384:4192`). alpha19's
// `ListItem` has no `enabled` parameter at all, so there is no call that draws a disabled row —
// which is why these carry `@GlimmerInteractionStates` rather than `@GlimmerStates`.

@CatalogComponent(
  id = "ListItem",
  // `Type=1-line, State=Enabled` in the kit's `List Item` set (`384:4197`), whose axes are
  // `Type=1-line | 2-line | Card` x `State=`. One line is the base because this sticker
  // draws one line; the supporting-label variant below is a rendition of the kit's `2-line`
  // (`384:4191`).
  reference = "figma:HKfLClZDLRyMhf4IQQLna8/384:4195",
  caption = "One row of a list. The supporting label, icon slots and the kit's states fold in.",
)
@GlimmerInteractionStates
@Preview
@Composable
fun ListItemSticker() = Sticker {
  val c = counted("Title")
  ContentFrame {
    ListItem(onClick = c.onClick, leadingIcon = { Icon(Icons.AutoMirrored.Rounded.Send, "Send") }) {
      Text(c.label)
    }
  }
}

@CatalogVariant(
  of = "ListItem",
  props = ["content=supporting-label"],
  caption = "A second line under the primary label.",
)
@GlimmerInteractionStates
@Preview
@Composable
fun ListItemSupportingSticker() = Sticker {
  val c = counted("Title")
  ContentFrame {
    ListItem(onClick = c.onClick, supportingLabel = { Text("Subtitle") }) { Text(c.label) }
  }
}

@CatalogVariant(
  of = "ListItem",
  props = ["content=supporting-label-leading-icon"],
  caption = "Supporting label and a leading icon.",
)
@Preview
@Composable
fun ListItemSupportingIconSticker() = Sticker {
  val c = counted("Title")
  ContentFrame {
    ListItem(
      onClick = c.onClick,
      supportingLabel = { Text("Subtitle") },
      leadingIcon = { Icon(Icons.AutoMirrored.Rounded.Send, "Send") },
    ) {
      Text(c.label)
    }
  }
}
