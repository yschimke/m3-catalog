@file:CatalogGroup(name = "Carousel", section = "Containment")
@file:OptIn(ExperimentalMaterial3Api::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.HorizontalUncontainedCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.overrides.previewOverrideString
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes412
import ee.schimke.m3catalog.Sticker

// Baked at rest on the first item, which is deterministic; the live lane scrolls it.
//
// The kit documents four layouts. Compose ships the multi-browse and uncontained ones as distinct
// composables; hero and full-screen are the multi-browse with a large preferred item width, which
// is what the `width` knob expresses.

@Composable
private fun CarouselItem() {
  BoxWithConstraints(
    Modifier.fillMaxSize()
      .clip(RoundedCornerShape(24.dp))
      .background(MaterialTheme.colorScheme.surfaceContainer)
  ) {
    val scale = minOf(1f, maxWidth.value / 188f)
    val tint = MaterialTheme.colorScheme.outlineVariant
    Box(
      Modifier.align(Alignment.Center)
        .offset(y = (-36).dp * scale)
        .size(56.dp * scale)
        .clip(MaterialShapes.Triangle.toShape())
        .background(tint)
    )
    Box(
      Modifier.align(Alignment.Center)
        .offset(x = (-36).dp * scale, y = 28.dp * scale)
        .size(52.dp * scale)
        .clip(MaterialShapes.SoftBurst.toShape())
        .background(tint)
    )
    Box(
      Modifier.align(Alignment.Center)
        .offset(x = 34.dp * scale, y = 28.dp * scale)
        .size(52.dp * scale)
        .clip(MaterialShapes.Square.toShape())
        .background(tint)
    )
  }
}

@CatalogComponent(
  id = "Carousel/MultiBrowse",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/53912:27490",
  referenceSet = "figma:ocdacdEsnHipMJD3egzxKb/53912:27480",
  caption = "Items that resize as they enter and leave the viewport. Item widths fold in.",
)
@CatalogModes412
@OverrideVariant(name = "hero", strings = ["width=240"])
@OverrideVariant(name = "full-screen", strings = ["width=320"])
@Composable
fun MultiBrowseCarousel() = Sticker {
  val width = previewOverrideString("width", "160").toIntOrNull() ?: 160
  Box(Modifier.width(412.dp).height(221.dp).padding(vertical = 8.dp)) {
    HorizontalMultiBrowseCarousel(
      state = rememberCarouselState { 5 },
      preferredItemWidth = width.dp,
      modifier = Modifier.fillMaxSize(),
      itemSpacing = 8.dp,
      contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
      CarouselItem()
    }
  }
}

@CatalogComponent(
  id = "Carousel/Uncontained",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/54577:26035",
  caption = "Items keep their size and scroll past the edge.",
)
@CatalogModes412
@Composable
fun UncontainedCarousel() = Sticker {
  Box(Modifier.width(412.dp).height(221.dp).padding(vertical = 8.dp)) {
    HorizontalUncontainedCarousel(
      state = rememberCarouselState { 5 },
      itemWidth = 153.dp,
      modifier = Modifier.fillMaxSize(),
      itemSpacing = 8.dp,
      contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
      CarouselItem()
    }
  }
}
