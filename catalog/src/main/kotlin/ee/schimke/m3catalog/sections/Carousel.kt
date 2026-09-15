@file:CatalogGroup(name = "Carousel", section = "Containment")
@file:OptIn(ExperimentalMaterial3Api::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.carousel.CarouselState
import androidx.compose.material3.carousel.HorizontalCenteredHeroCarousel
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.HorizontalUncontainedCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.overrides.previewOverrideDp
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogImagePlaceholder
import ee.schimke.m3catalog.CatalogModesCarouselTablet
import ee.schimke.m3catalog.CatalogModesCompact
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogInteractive
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.action_focus_item
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

// The kit documents four layouts. Compose ships the multi-browse and uncontained ones as distinct
// composables; hero and full-screen are the multi-browse with a large preferred item width, which
// is what the `width` knob expresses.
//
// Each carousel is baked at REST on the item the kit's own cell rests on — item 0 for multi-browse
// and uncontained, item 1 for the centred hero, whose cell draws a small item on each side of the
// hero rather than the hero flush against the leading edge. The live lane scrolls: a drag moves it,
// and a tap on an item animates it into focus, which is what [AndroidX's own carousel samples]
// (https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:compose/material3/material3/samples/src/main/java/androidx/compose/material3/samples/CarouselSamples.kt)
// hang off each item.

/**
 * One carousel item: the kit's placeholder inside the carousel's own mask.
 *
 * [modifier] carries `Modifier.maskClip(…)` from the carousel's item scope, and that is the whole
 * point of this indirection. A carousel does not RESIZE its items — it lays every one of them out
 * at the same size and then masks each down to the width its position in the strategy allows, so a
 * shape the item clips ITSELF to is the shape of the unmasked rect and gets cut straight through by
 * the mask. That is what published the small trailing items with square corners where the kit draws
 * the same 28dp `corner/extra-large` on every item, however narrow (#440). `maskClip` moves the
 * shape onto the mask, where it tracks the visible rect as the item grows and shrinks; the
 * placeholder's own shape is therefore [RectangleShape], not a second rounded clip fighting it.
 */
@Composable
private fun CarouselItem(modifier: Modifier = Modifier) {
  // The motif itself lives beside the other catalog helpers: the kit draws the same placeholder
  // inside an app bar's image cell, and one copy is what keeps the two identical.
  CatalogImagePlaceholder(modifier.fillMaxSize(), shape = RectangleShape)
}

/**
 * The sample's tap-to-focus, live-lane only: a click animates [index] into the focused position.
 *
 * A carousel is scrollable by drag on the live lane already, but a drag is invisible in a still and
 * hard to discover in a held session; the tap is the affordance AndroidX's own samples give each
 * item. Inert on the baked lane, like every other handler in this catalog — a published PNG cannot
 * depend on whether something tapped it.
 */
@Composable
private fun Modifier.tapToFocus(state: CarouselState, index: Int): Modifier {
  if (!catalogInteractive()) return this
  val scope = rememberCoroutineScope()
  val label = stringResource(Res.string.action_focus_item)
  return clickable(onClickLabel = label, role = Role.Image) {
    scope.launch { state.animateScrollToItem(index) }
  }
}

@CatalogComponent(
  id = "Carousel/MultiBrowse",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/53912:27490",
  referenceSet = "figma:ocdacdEsnHipMJD3egzxKb/53912:27480",
  caption = "Items that resize as they enter and leave the viewport. Item widths fold in.",
)
@CatalogModesCompact
@OverrideVariant(name = "hero", floats = ["preferredItemWidth=240"])
@OverrideVariant(name = "full-screen", floats = ["preferredItemWidth=320"])
@ee.schimke.m3catalog.MultiBrowseCarouselExhaustiveKitCells
@Composable
fun MultiBrowseCarousel() = Sticker {
  // The kit's own large item: its mobile cell measures 188 / 120 / 56 across 412dp, and this width
  // reproduces it to within 2dp. It is also the width at which the strategy stops fitting TWO large
  // items and starts drawing the large / medium / small run the kit shows — at the 160dp this used
  // to pass a 412dp carousel drew two equal items and a cut third, so the resizing the component
  // exists to demonstrate was invisible (#440).
  val width = previewOverrideDp("preferredItemWidth", 188.dp)
  MultiBrowseCarouselBody(width = width, frameWidth = 412)
}

/**
 * The 16dp gutter is on the FRAME, not in `contentPadding`. A carousel strategy sizes its
 * arrangement from the whole viewport and does not subtract the content padding first, so a 412dp
 * carousel asked for `contentPadding = 16` laid out an arrangement 397dp wide, offset it by the
 * leading 16, and ran the trailing item off the edge of the frame. Padding the frame gives the
 * carousel a 380dp viewport instead, and the arrangement lands on the kit's own bounds: 188 / 122 /
 * 54 at 16 / 212 / 342 against the kit's 188 / 120 / 56 at 16 / 212 / 340.
 */
@Composable
private fun MultiBrowseCarouselBody(width: Dp, frameWidth: Int) {
  val state = rememberCarouselState { 5 }
  Box(Modifier.width(frameWidth.dp).height(221.dp).padding(vertical = 8.dp, horizontal = 16.dp)) {
    HorizontalMultiBrowseCarousel(
      state = state,
      preferredItemWidth = width,
      modifier = Modifier.fillMaxSize(),
      itemSpacing = 8.dp,
    ) { index ->
      CarouselItem(Modifier.maskClip(MaterialTheme.shapes.extraLarge).tapToFocus(state, index))
    }
  }
}

/**
 * The same component at the kit's tablet context width, which a 412dp preview cannot express.
 *
 * The kit's tablet cell is a 600dp frame — four items at 184 / 184 / 120 / 56 — not the 905dp
 * breakpoint that width is named for, and rendering at 905 compared a sticker against a node half
 * again narrower than itself. At 600dp, with the kit's own 184dp item, Compose lands on 184 / 184 /
 * 120 / 56 at 16 / 208 / 400 / 528: the kit's cell, exactly.
 */
@CatalogVariant(of = "Carousel/MultiBrowse", props = ["context=tablet"])
@CatalogModesCarouselTablet
@Composable
fun MultiBrowseCarouselTablet() = Sticker {
  MultiBrowseCarouselBody(width = 184.dp, frameWidth = 600)
}

/**
 * The kit's `Layout=Center-aligned hero`. Its own composable rather than a knob, because Compose
 * publishes it as one — `HorizontalCenteredHeroCarousel` is a different component, not a parameter
 * of the multi-browse one, and `@CatalogVariant` is how this catalog folds that shape under a
 * parent id.
 *
 * The `Layout` axis is why `Carousel/Uncontained` folds the same way below. It was a top-level
 * component while this one was a variant, which made the file answer one axis two ways.
 *
 * Rests on item 1, not item 0: the layout is CENTRE-aligned, so the kit's cell — 56 / 252 / 56
 * across 412dp — shows the hero between two small items, and only an item with neighbours on both
 * sides can be drawn that way. At item 0 the hero sits against the leading edge with nothing to its
 * left, which is a real state of the component but not the one the kit publishes.
 *
 * The gutter is on the frame for the same reason it is on [MultiBrowseCarouselBody]: with
 * `contentPadding = 16` this carousel drew its leading item at x=0 and ran the trailing one off the
 * frame, where the kit keeps both side items inside a 16dp gutter.
 *
 * What stays different, and cannot be authored away: Compose pins the hero's side items to the 40dp
 * floor of its small-item range where the kit draws them at 56, so the hero measures 284 against
 * the kit's 252. `HorizontalCenteredHeroCarousel` has no size parameter to say otherwise.
 */
@CatalogVariant(of = "Carousel/MultiBrowse", props = ["layout=center-aligned-hero"])
@CatalogModesCompact
@Composable
fun CenteredHeroCarousel() = Sticker {
  val state = rememberCarouselState(initialItem = 1) { 5 }
  Box(Modifier.width(412.dp).height(221.dp).padding(vertical = 8.dp, horizontal = 16.dp)) {
    HorizontalCenteredHeroCarousel(
      state = state,
      modifier = Modifier.fillMaxSize(),
      itemSpacing = 8.dp,
    ) { index ->
      CarouselItem(Modifier.maskClip(MaterialTheme.shapes.extraLarge).tapToFocus(state, index))
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
  val state = rememberCarouselState { 5 }
  // The one carousel here that keeps its gutter in `contentPadding`: items that do NOT resize are
  // the case the kit draws running off the trailing edge — 154 / 154 / cut — rather than tucked
  // inside a 16dp gutter, and that is what this arrangement lands on.
  Box(Modifier.width(412.dp).height(221.dp).padding(vertical = 8.dp)) {
    HorizontalUncontainedCarousel(
      state = state,
      itemWidth = 153.dp,
      modifier = Modifier.fillMaxSize(),
      itemSpacing = 8.dp,
      contentPadding = PaddingValues(horizontal = 16.dp),
    ) { index ->
      CarouselItem(Modifier.maskClip(MaterialTheme.shapes.extraLarge).tapToFocus(state, index))
    }
  }
}
