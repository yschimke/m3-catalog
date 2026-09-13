@file:CatalogGroup(name = "Card", section = "Containment")

package ee.schimke.m3catalog.glimmer

import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.xr.glimmer.ActionCard
import androidx.xr.glimmer.Button
import androidx.xr.glimmer.Card
import androidx.xr.glimmer.Icon
import androidx.xr.glimmer.Text
import androidx.xr.glimmer.TitleChip
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant

// The card's slots — header image, title, subtitle, leading and trailing icon — are content axes of
// ONE component, so they fold in as variants rather than splitting the card four ways.
//
// ## The base sticker draws the card the kit draws
//
// The kit's `Card` (`416:2700`) is a single symbol whose content differences are hidden LAYERS, and
// the layers it ships turned on are `Show Image`, `Show Entity`, `Show Title`, `Show Subtitle` and
// `Show Body` — a 420x412 content card. A sticker drawing `Card { Text("This is a card") }` was an
// 80dp row against it, which is why #381's board reported 98% of pixels differing: two different
// pictures, not a card drawn wrongly. Design-led, so the base is the populated form and the bare
// one is a variant under it.
//
// `Show Action` is turned on in that symbol too, and it is the one layer the base cannot carry:
// alpha19 puts the action slot on `ActionCard` rather than on `Card`, and a sticker published as
// `Card` has to invoke `Card`. It folds in as `content=action` below — the same treatment
// `AGENTS.md` gives every "it is a separate composable" axis.

@CatalogComponent(
  id = "Card",
  // The kit's `Card` component — title chip, image slot, title, subtitle and content
  // area, every slot this component exposes.
  reference = "figma:HKfLClZDLRyMhf4IQQLna8/416:2700",
  caption = "A surface for a unit of content. Header image, title, subtitle and the icon slots.",
)
@Preview(showBackground = true, backgroundColor = ADDITIVE_ZERO_BACKGROUND)
@Composable
fun CardSticker() = Sticker {
  ContentFrame {
    Card(
      header = { Image(HeaderImage, "Header artwork", contentScale = ContentScale.FillWidth) },
      title = { Text("Title") },
      subtitle = { Text("Subtitle") },
      leadingIcon = { Icon(Icons.Rounded.AccountCircle, "Sender") },
    ) {
      Text("Body")
    }
  }
}

@CatalogVariant(
  of = "Card",
  props = ["content=text-only"],
  caption = "The bare form: content and nothing else.",
)
@Preview(showBackground = true, backgroundColor = ADDITIVE_ZERO_BACKGROUND)
@Composable
fun CardTextOnlySticker() = Sticker { ContentFrame { Card { Text("Body") } } }

@CatalogVariant(
  of = "Card",
  props = ["content=trailing-icon"],
  caption = "Icon after the content.",
)
@Preview(showBackground = true, backgroundColor = ADDITIVE_ZERO_BACKGROUND)
@Composable
fun CardTrailingIconSticker() = Sticker {
  ContentFrame {
    Card(
      trailingIcon = { Icon(Icons.Rounded.AccountCircle, "Sender") },
      title = { Text("Title") },
    ) {
      Text("Body")
    }
  }
}

@CatalogVariant(
  of = "Card",
  props = ["content=action"],
  caption = "The kit's action layer, which alpha19 puts on `ActionCard` rather than on `Card`.",
)
@Preview(showBackground = true, backgroundColor = ADDITIVE_ZERO_BACKGROUND)
@Composable
fun CardActionSticker() = Sticker {
  ContentFrame {
    val c = counted("Button")
    ActionCard(
      action = { Button(onClick = c.onClick) { Text(c.label) } },
      title = { Text("Title") },
    ) {
      Text("Body")
    }
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
fun TitleChipSticker() = Sticker {
  // The kit's symbol draws its entity slot filled and labels it "Title Chip" (146x44); the
  // label-only chip is 107x44 and is the variant below.
  TitleChip(leadingIcon = { Icon(Icons.Rounded.AccountCircle, "Sender") }) { Text("Title Chip") }
}

@CatalogVariant(
  of = "TitleChip",
  props = ["content=label-only"],
  caption = "Label with no icon.",
)
@Preview(showBackground = true, backgroundColor = ADDITIVE_ZERO_BACKGROUND)
@Composable
fun TitleChipLabelOnlySticker() = Sticker { TitleChip { Text("Title Chip") } }
