@file:CatalogGroup(name = "Sliders", section = "Selection")
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

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
import ee.schimke.composeai.overrides.previewOverrideString
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker

// `SliderState` / `RangeSliderState` are the state the component owns, so these stickers need no
// `draggable` gate: nothing drags them on the baked lane, and dragging works for real on the live
// one. (The earlier `enabled = catalogInteractive()` trick published the range slider looking
// *disabled* in every baked render, which is a defect, not a policy.)
//
// The stops axis (continuous / discrete) and the track axis (standard / centered) are parameters,
// so
// they fold onto one sticker. The orientation and the two-thumb form are separate composables and
// stay separate components.

@Composable
private fun sliderSteps(): Int = if (previewOverrideString("steps", "") == "discrete") 4 else 0

@Composable
private fun sliderEnabled(): Boolean = previewOverrideString("status", "enabled") != "disabled"

@CatalogComponent(
  id = "Slider/Continuous",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58008:10356",
  caption = "Select a value from a range. Discrete stops, a centered track and disabled fold in.",
)
@CatalogModes
@OverrideVariant(name = "discrete", strings = ["steps=discrete"])
@OverrideVariant(name = "centered", strings = ["track=centered"])
@OverrideVariant(name = "centered-discrete", strings = ["track=centered", "steps=discrete"])
@OverrideVariant(name = "disabled", strings = ["status=disabled"])
@OverrideVariant(name = "disabled-discrete", strings = ["steps=discrete", "status=disabled"])
@Composable
fun ContinuousSlider() = Sticker {
  val steps = sliderSteps()
  val enabled = sliderEnabled()
  val centered = previewOverrideString("track", "standard") == "centered"
  val state =
    remember(steps, centered) { SliderState(value = if (centered) 0.7f else 0.5f, steps = steps) }
  Slider(
    state = state,
    enabled = enabled,
    modifier = Modifier.width(280.dp),
    track = { s ->
      if (centered) SliderDefaults.CenteredTrack(sliderState = s, enabled = enabled)
      else SliderDefaults.Track(sliderState = s, enabled = enabled)
    },
  )
}

@CatalogComponent(
  id = "Slider/Range",
  caption = "Two thumbs bounding a sub-range. Discrete stops and disabled fold in.",
)
@CatalogModes
@OverrideVariant(name = "discrete", strings = ["steps=discrete"])
@OverrideVariant(name = "disabled", strings = ["status=disabled"])
@Composable
fun RangeSliderSticker() = Sticker {
  val steps = sliderSteps()
  val state =
    remember(steps) {
      RangeSliderState(activeRangeStart = 0.2f, activeRangeEnd = 0.8f, steps = steps)
    }
  RangeSlider(state = state, enabled = sliderEnabled(), modifier = Modifier.width(280.dp))
}

@CatalogComponent(
  id = "Slider/Vertical",
  caption = "The same control rotated for vertical controls such as volume and brightness.",
)
@CatalogModes
@OverrideVariant(name = "discrete", strings = ["steps=discrete"])
@OverrideVariant(name = "disabled", strings = ["status=disabled"])
@Composable
fun VerticalSliderSticker() = Sticker {
  val steps = sliderSteps()
  val state = remember(steps) { SliderState(value = 0.5f, steps = steps) }
  VerticalSlider(state = state, enabled = sliderEnabled(), modifier = Modifier.height(220.dp))
}
