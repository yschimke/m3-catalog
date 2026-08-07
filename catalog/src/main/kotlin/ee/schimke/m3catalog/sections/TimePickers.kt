@file:CatalogGroup(name = "Time pickers", section = "Selection")
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerLayoutType
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import ee.schimke.composeai.overrides.previewOverrideString
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker

// Pinned to 10:10 — the traditional watch-face time, and deterministic across renders.
//
// The clock face and the keyboard entry form are two composables, so two components. Inside each,
// the 12-hour / 24-hour axis is a state parameter; the dial additionally has the vertical and
// horizontal layouts the kit shows for portrait and landscape.

@Composable private fun timeIs24Hour(): Boolean = previewOverrideString("hours", "24") == "24"

@CatalogComponent(
  id = "TimePicker/Dial",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/52949:27946",
  caption = "Clock dial for picking an hour and minute. 12-hour and the landscape layout fold in.",
)
@CatalogModes
@OverrideVariant(name = "12-hour", strings = ["hours=12"])
@OverrideVariant(name = "horizontal", strings = ["layout=horizontal"])
@OverrideVariant(name = "12-hour-horizontal", strings = ["hours=12", "layout=horizontal"])
@Composable
fun TimePickerSticker() = Sticker {
  val is24Hour = timeIs24Hour()
  TimePicker(
    state = rememberTimePickerState(initialHour = 10, initialMinute = 10, is24Hour = is24Hour),
    layoutType =
      if (previewOverrideString("layout", "vertical") == "horizontal")
        TimePickerLayoutType.Horizontal
      else TimePickerLayoutType.Vertical,
  )
}

@CatalogComponent(
  id = "TimePicker/Input",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/52949:28069",
  caption = "Keyboard entry instead of the dial. 12-hour folds in.",
)
@CatalogModes
@OverrideVariant(name = "12-hour", strings = ["hours=12"])
@Composable
fun TimeInputSticker() = Sticker {
  TimeInput(
    state = rememberTimePickerState(initialHour = 10, initialMinute = 10, is24Hour = timeIs24Hour())
  )
}
