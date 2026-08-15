@file:CatalogGroup(name = "Sliders", section = "Selection")
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.RangeSliderState
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SliderState
import androidx.compose.material3.VerticalSlider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.composeai.preview.VariantInteraction
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.CatalogModes354
import ee.schimke.m3catalog.SliderSizeMatrix
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogChoice
import ee.schimke.m3catalog.catalogSliderSize
import ee.schimke.m3catalog.sliderFrameHeight
import ee.schimke.m3catalog.sliderThumbSize
import ee.schimke.m3catalog.sliderTrackCorner
import ee.schimke.m3catalog.sliderTrackHeight

// `SliderState` / `RangeSliderState` are the state the component owns, so these stickers need no
// `draggable` gate: nothing drags them on the baked lane, and dragging works for real on the live
// one. (The earlier `enabled = catalogInteractive()` trick published the range slider looking
// *disabled* in every baked render, which is a defect, not a policy.)
//
// The stops axis (continuous / discrete) and the track axis (standard / centered) are parameters,
// so
// they fold onto one sticker. The orientation and the two-thumb form are separate composables and
// stay separate components.
//
// The size axis is `@SliderSizeMatrix`, and it is the one axis a slider cannot get from the
// library: `SliderDefaults` sizes its track and handle from `SliderTokens`, which carries the
// extra-small values and no scale for the other four. So each sticker passes the track's height and
// corner and the handle's size explicitly, from the kit's own frames — see the table in
// `CatalogSizes.kt`, and issue #89 for the upstream gap it stands in for.
//
// The vertical slider is deliberately left at one size: it shares the `Standard slider` set with
// the horizontal one, whose size variants these already compare, so a second set of four would add
// renders that resolve to the same axis values.

@Composable
private fun sliderSteps(): Int =
  if (catalogChoice("steps", "", "" to "Continuous", "discrete" to "Discrete") == "discrete") 4
  else 0

@Composable
private fun sliderEnabled(): Boolean =
  catalogChoice("status", "enabled", "enabled", "disabled") != "disabled"

// The kit publishes `State=Hovered` for both slider sets and Compose draws nothing for it: the
// thumb collects Drag, Focus and Press interactions and not Hover, even though `SliderTokens`
// declares a `HoverHandleWidth`. So these carry `pressed` and no `hovered` — issue #91.

@CatalogComponent(
  id = "Slider/Continuous",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58008:10357",
  caption = "Select a value from a range. Discrete stops, a centered track and disabled fold in.",
)
@CatalogModes354
@SliderSizeMatrix
@OverrideVariant(name = "discrete", strings = ["steps=discrete"])
@OverrideVariant(name = "centered", strings = ["track=centered"])
@OverrideVariant(name = "centered-discrete", strings = ["track=centered", "steps=discrete"])
@OverrideVariant(name = "disabled", strings = ["status=disabled"])
@OverrideVariant(name = "disabled-discrete", strings = ["steps=discrete", "status=disabled"])
@OverrideVariant(name = "pressed", interaction = VariantInteraction.Pressed)
@Composable
fun ContinuousSlider() = Sticker {
  val steps = sliderSteps()
  val enabled = sliderEnabled()
  val size = catalogSliderSize()
  val centered = catalogChoice("track", "standard", "standard", "centered") == "centered"
  val interaction = remember { MutableInteractionSource() }
  val state =
    remember(steps, centered) { SliderState(value = if (centered) 0.7f else 0.5f, steps = steps) }
  Slider(
    state = state,
    enabled = enabled,
    interactionSource = interaction,
    modifier = Modifier.width(354.dp).height(size.sliderFrameHeight),
    thumb = {
      SliderDefaults.Thumb(
        interactionSource = interaction,
        enabled = enabled,
        thumbSize = size.sliderThumbSize,
      )
    },
    track = { s ->
      val trackModifier = Modifier.height(size.sliderTrackHeight)
      if (centered)
        SliderDefaults.CenteredTrack(
          sliderState = s,
          modifier = trackModifier,
          enabled = enabled,
          trackCornerSize = size.sliderTrackCorner,
        )
      else
        SliderDefaults.Track(
          sliderState = s,
          trackCornerSize = size.sliderTrackCorner,
          modifier = trackModifier,
          enabled = enabled,
        )
    },
  )
}

@CatalogComponent(
  id = "Slider/Range",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58008:11811",
  caption = "Two thumbs bounding a sub-range. Discrete stops and disabled fold in.",
)
@CatalogModes354
@SliderSizeMatrix
@OverrideVariant(name = "discrete", strings = ["steps=discrete"])
@OverrideVariant(name = "disabled", strings = ["status=disabled"])
@OverrideVariant(name = "pressed", interaction = VariantInteraction.Pressed)
@Composable
fun RangeSliderSticker() = Sticker {
  val steps = sliderSteps()
  val enabled = sliderEnabled()
  val size = catalogSliderSize()
  val startInteraction = remember { MutableInteractionSource() }
  val endInteraction = remember { MutableInteractionSource() }
  val state =
    remember(steps) {
      RangeSliderState(activeRangeStart = 0.49f, activeRangeEnd = 0.51f, steps = steps)
    }
  RangeSlider(
    state = state,
    enabled = enabled,
    startInteractionSource = startInteraction,
    endInteractionSource = endInteraction,
    modifier = Modifier.width(354.dp).height(size.sliderFrameHeight),
    startThumb = {
      SliderDefaults.Thumb(
        interactionSource = startInteraction,
        enabled = enabled,
        thumbSize = size.sliderThumbSize,
      )
    },
    endThumb = {
      SliderDefaults.Thumb(
        interactionSource = endInteraction,
        enabled = enabled,
        thumbSize = size.sliderThumbSize,
      )
    },
    track = { s ->
      SliderDefaults.Track(
        rangeSliderState = s,
        trackCornerSize = size.sliderTrackCorner,
        modifier = Modifier.height(size.sliderTrackHeight),
        enabled = enabled,
      )
    },
  )
}

@CatalogComponent(
  id = "Slider/Vertical",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58008:10668",
  caption = "The same control rotated for vertical controls such as volume and brightness.",
)
@CatalogModes
@OverrideVariant(name = "discrete", strings = ["steps=discrete"])
@OverrideVariant(name = "disabled", strings = ["status=disabled"])
@Composable
fun VerticalSliderSticker() = Sticker {
  val steps = sliderSteps()
  val state = remember(steps) { SliderState(value = 0.5f, steps = steps) }
  VerticalSlider(
    state = state,
    enabled = sliderEnabled(),
    reverseDirection = true,
    modifier = Modifier.width(44.dp).height(354.dp),
  )
}
