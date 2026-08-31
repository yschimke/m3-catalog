@file:CatalogGroup(name = "Carousel", section = "Containment")
@file:OptIn(ExperimentalMaterial3Api::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.carousel.HorizontalCenteredHeroCarousel
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.HorizontalUncontainedCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.overrides.previewOverrideString
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogImagePlaceholder
import ee.schimke.m3catalog.CatalogModesCarouselTablet
import ee.schimke.m3catalog.CatalogModesCompact
import ee.schimke.m3catalog.Sticker

// Baked at rest on the first item, which is deterministic; the live lane scrolls it.
//
// The kit documents four layouts. Compose ships the multi-browse and uncontained ones as distinct
// composables; hero and full-screen are the multi-browse with a large preferred item width, which
// is what the `width` knob expresses.

@Composable
private fun CarouselItem() {
  // The motif itself now lives beside the other catalog helpers: the kit draws the same
  // placeholder inside an app bar's image cell, and one copy is what keeps the two identical.
  CatalogImagePlaceholder(Modifier.fillMaxSize())
}

@CatalogComponent(
  id = "Carousel/MultiBrowse",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/53912:27490",
  referenceSet = "figma:ocdacdEsnHipMJD3egzxKb/53912:27480",
  caption = "Items that resize as they enter and leave the viewport. Item widths fold in.",
)
@CatalogModesCompact
@OverrideVariant(name = "hero", strings = ["width=240"])
@OverrideVariant(name = "full-screen", strings = ["width=320"])
@ee.schimke.m3catalog.MultiBrowseCarouselExhaustiveKitCells
@Composable
fun MultiBrowseCarousel() = Sticker {
  val width = previewOverrideString("width", "160").toIntOrNull() ?: 160
  MultiBrowseCarouselBody(width = width, frameWidth = 412)
}

@Composable
private fun MultiBrowseCarouselBody(width: Int, frameWidth: Int) {
  Box(Modifier.width(frameWidth.dp).height(221.dp).padding(vertical = 8.dp)) {
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

/** The same component at the kit's tablet context width, which a 412dp preview cannot express. */
@CatalogVariant(of = "Carousel/MultiBrowse", props = ["context=tablet"])
@CatalogModesCarouselTablet
@Composable
fun MultiBrowseCarouselTablet() = Sticker { MultiBrowseCarouselBody(width = 160, frameWidth = 905) }

/**
 * The kit's `Layout=Center-aligned hero`. Its own composable rather than a knob, because Compose
 * publishes it as one — `HorizontalCenteredHeroCarousel` is a different component, not a parameter
 * of the multi-browse one, and `@CatalogVariant` is how this catalog folds that shape under a
 * parent id.
 *
 * The `Layout` axis is why `Carousel/Uncontained` folds the same way below. It was a top-level
 * component while this one was a variant, which made the file answer one axis two ways.
 */
@CatalogVariant(of = "Carousel/MultiBrowse", props = ["layout=center-aligned-hero"])
@CatalogModesCompact
@Composable
fun CenteredHeroCarousel() = Sticker {
  Box(Modifier.width(412.dp).height(221.dp).padding(vertical = 8.dp)) {
    HorizontalCenteredHeroCarousel(
      state = rememberCarouselState { 5 },
      modifier = Modifier.fillMaxSize(),
      itemSpacing = 8.dp,
      contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
      CarouselItem()
    }
  }
}

@CatalogVariant(
  of = "Carousel/MultiBrowse",
  props = ["layout=uncontained"],
  caption = "Items keep their size and scroll past the edge.",
)
@CatalogModesCompact
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
