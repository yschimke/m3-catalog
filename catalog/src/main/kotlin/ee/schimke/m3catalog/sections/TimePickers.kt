@file:CatalogGroup(name = "Time pickers", section = "Selection")
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker

// Pinned to 10:10 — the traditional watch-face time, and deterministic across renders.

@CatalogComponent(id = "TimePicker/Dial", caption = "Clock dial for picking an hour and minute.")
@CatalogModes
@Composable
fun TimePickerSticker() = Sticker {
  TimePicker(state = rememberTimePickerState(initialHour = 10, initialMinute = 10, is24Hour = true))
}

@CatalogVariant(
  of = "TimePicker/Dial",
  props = ["entry=input"],
  caption = "Keyboard entry instead of the dial.",
)
@CatalogModes
@Composable
fun TimeInputSticker() = Sticker {
  TimeInput(state = rememberTimePickerState(initialHour = 10, initialMinute = 10, is24Hour = true))
}
