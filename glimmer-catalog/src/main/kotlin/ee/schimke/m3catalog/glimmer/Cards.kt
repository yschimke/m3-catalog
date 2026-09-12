@file:CatalogGroup(name = "Card", section = "Containment")

package ee.schimke.m3catalog.glimmer

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.xr.glimmer.Card
import androidx.xr.glimmer.Icon
import androidx.xr.glimmer.Text
import androidx.xr.glimmer.TitleChip
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant

// The card's slots — title, subtitle, leading and trailing icon — are content axes of ONE
// component, so they fold in as variants rather than splitting the card four ways.

@CatalogComponent(
  id = "Card",
  // The kit's `Card` component — title chip, image slot, title, subtitle and content
  // area, every slot this component exposes.
  reference = "figma:HKfLClZDLRyMhf4IQQLna8/416:2700",
  caption = "A surface for a unit of content. Title, subtitle and the icon slots fold in.",
)
@Preview(showBackground = true, backgroundColor = ADDITIVE_ZERO_BACKGROUND)
@Composable
fun CardSticker() = Sticker { Card { Text("This is a card") } }

@CatalogVariant(
  of = "Card",
  props = ["content=trailing-icon"],
  caption = "Icon after the content.",
)
@Preview(showBackground = true, backgroundColor = ADDITIVE_ZERO_BACKGROUND)
@Composable
fun CardTrailingIconSticker() = Sticker {
  Card(trailingIcon = { Icon(StarIcon, "Favourite") }) { Text("Card with an icon") }
}

@CatalogVariant(
  of = "Card",
  props = ["content=title-subtitle-icon"],
  caption = "The fullest form: title, subtitle and a leading icon above the content.",
)
@Preview(showBackground = true, backgroundColor = ADDITIVE_ZERO_BACKGROUND)
@Composable
fun CardTitleSubtitleSticker() = Sticker {
  Card(
    title = { Text("Title") },
    subtitle = { Text("Subtitle") },
    leadingIcon = { Icon(StarIcon, "Favourite") },
  ) {
    Text("Card content")
  }
}

// The title chip is a separate component, not a card slot: it labels the content BESIDE it, and
// Glimmer publishes a spacing token (`TitleChipDefaults.associatedContentSpacing`) for the gap
// between the two. A chip drawn inside a card would misstate that relationship.

@CatalogComponent(
  id = "TitleChip",
  // The kit's `Title chip` component, leading-icon slot included.
  reference = "figma:HKfLClZDLRyMhf4IQQLna8/5315:4722",
  caption = "Labels the content it sits above. Carries its own leading-icon slot.",
)
@Preview(showBackground = true, backgroundColor = ADDITIVE_ZERO_BACKGROUND)
@Composable
fun TitleChipSticker() = Sticker { TitleChip { Text("Messages") } }

@CatalogVariant(
  of = "TitleChip",
  props = ["content=leading-icon"],
  caption = "Icon before the label.",
)
@Preview(showBackground = true, backgroundColor = ADDITIVE_ZERO_BACKGROUND)
@Composable
fun TitleChipLeadingIconSticker() = Sticker {
  TitleChip(leadingIcon = { Icon(StarIcon, "Favourite") }) { Text("Messages") }
}
