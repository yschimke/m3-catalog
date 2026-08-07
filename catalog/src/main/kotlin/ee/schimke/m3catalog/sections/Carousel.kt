@file:CatalogGroup(name = "Carousel", section = "Containment")
@file:OptIn(ExperimentalMaterial3Api::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.HorizontalUncontainedCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.overrides.previewOverrideString
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker

// Baked at rest on the first item, which is deterministic; the live lane scrolls it.
//
// The kit documents four layouts. Compose ships the multi-browse and uncontained ones as distinct
// composables; hero and full-screen are the multi-browse with a large preferred item width, which
// is what the `width` knob expresses.

@Composable
private fun swatches(): List<Color> =
  listOf(
    MaterialTheme.colorScheme.primaryContainer,
    MaterialTheme.colorScheme.secondaryContainer,
    MaterialTheme.colorScheme.tertiaryContainer,
    MaterialTheme.colorScheme.surfaceVariant,
    MaterialTheme.colorScheme.errorContainer,
  )

@CatalogComponent(
  id = "Carousel/MultiBrowse",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/53912:27480",
  caption = "Items that resize as they enter and leave the viewport. Item widths fold in.",
)
@CatalogModes
@OverrideVariant(name = "hero", strings = ["width=240"])
@OverrideVariant(name = "full-screen", strings = ["width=320"])
@Composable
fun MultiBrowseCarousel() = Sticker {
  val colors = swatches()
  val width = previewOverrideString("width", "160").toIntOrNull() ?: 160
  HorizontalMultiBrowseCarousel(
    state = rememberCarouselState { colors.size },
    preferredItemWidth = width.dp,
    modifier = Modifier.width(320.dp).height(180.dp),
    itemSpacing = 8.dp,
  ) { index ->
    Box(Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)).background(colors[index]))
  }
}

@CatalogComponent(
  id = "Carousel/Uncontained",
  caption = "Items keep their size and scroll past the edge.",
)
@CatalogModes
@Composable
fun UncontainedCarousel() = Sticker {
  val colors = swatches()
  HorizontalUncontainedCarousel(
    state = rememberCarouselState { colors.size },
    itemWidth = 140.dp,
    modifier = Modifier.width(320.dp).height(180.dp),
    itemSpacing = 8.dp,
  ) { index ->
    Box(Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)).background(colors[index]))
  }
}
