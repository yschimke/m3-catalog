@file:CatalogGroup(name = "Content", section = "Content")

package ee.schimke.foundationcatalog.sections

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BrushPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.preview.BuilderComponent
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.foundationcatalog.FoundationModes
import ee.schimke.foundationcatalog.Sticker

// The one leaf on this shelf. Everything else `compose-foundation` owns is something you put things
// INSIDE; an image is a thing you put in.
//
// The picture is a gradient rather than a bundled photograph, for the reason `:glimmer-catalog`'s
// own header artwork gives: a painter computed from constants cannot decode differently between
// runs, so the sticker is reproducible without shipping bytes. What a design actually draws is the
// asset its `assetKey` names — one the catalog ships, or one pinned through the builder's asset
// lane — and the placeholder here is a picture of the SLOT rather than of any particular asset.

private const val ARTWORK_INTRINSIC = 1000f

private val PlaceholderArtwork =
  BrushPainter(
    Brush.linearGradient(
      0.0f to Color(0xFF5B6BF5),
      0.55f to Color(0xFF8E7BEF),
      1.0f to Color(0xFFE0669B),
      start = Offset.Zero,
      end = Offset(ARTWORK_INTRINSIC, ARTWORK_INTRINSIC),
    )
  )

// The one `@BuilderComponent` in this module, and it declares nothing: it keeps `Sticker` — this
// module's own theme wrapper — OFF the published shelf. `Sticker` is project code, so unlike the
// foundation symbols the stickers draw it does enter the component record, and every record
// component a catalog does not exclude is offered as a component of it. It sits on THIS preview
// because the policy attaches to a component the annotated preview actually renders, and the
// primitives' own stickers render nothing the record can see: `Box`, `Column` and `Row` are inline,
// so there is no call left in the bytecode to attribute.
@BuilderComponent(
  component = "Sticker",
  exclude =
    "The theme wrapper every sticker in this module is drawn in, not a component anyone " +
      "puts in a design.",
)
@CatalogComponent(
  id = "Content/Image",
  noReference = "Not a kit component — `Image` is Compose's own leaf for drawing a painter.",
  caption = "Draws the asset a design pins, scaled and aligned within its bounds.",
)
@FoundationModes
@Composable
fun ImageSticker() = Sticker {
  Image(
    painter = PlaceholderArtwork,
    contentDescription = null,
    modifier = Modifier.size(width = 160.dp, height = 96.dp),
    contentScale = ContentScale.Crop,
  )
}
