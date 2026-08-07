@file:CatalogGroup(name = "Carousel", section = "Containment")
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker

// The multi-browse carousel: items that grow and shrink as they scroll through the viewport. Baked
// at rest on the first item, which is deterministic; the live lane scrolls it.

@CatalogComponent(
  id = "Carousel/MultiBrowse",
  caption = "Scrollable items that resize as they enter and leave the viewport.",
)
@CatalogModes
@Composable
fun MultiBrowseCarousel() = Sticker {
  val colors =
    listOf(
      MaterialTheme.colorScheme.primaryContainer,
      MaterialTheme.colorScheme.secondaryContainer,
      MaterialTheme.colorScheme.tertiaryContainer,
      MaterialTheme.colorScheme.surfaceVariant,
    )
  HorizontalMultiBrowseCarousel(
    state = rememberCarouselState { colors.size },
    preferredItemWidth = 160.dp,
    modifier = Modifier.width(320.dp).height(180.dp),
    itemSpacing = 8.dp,
  ) { index ->
    Box(Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)).background(colors[index]))
  }
}
