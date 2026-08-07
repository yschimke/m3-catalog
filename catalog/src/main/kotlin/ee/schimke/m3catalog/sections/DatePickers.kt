@file:CatalogGroup(name = "Date pickers", section = "Selection")
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.overrides.previewOverrideString
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.counted

// The date is pinned to a fixed instant so the baked capture is deterministic: an unpinned picker
// would open on "today" and every nightly render would differ from the last.
//
// 2024-01-15T00:00:00Z, chosen because it sits mid-month and mid-week, so the sticker shows a full
// grid rather than a nearly empty first or last row.
private const val PINNED_DATE_MILLIS = 1705276800000L
private const val PINNED_DATE_END_MILLIS = 1705968000000L

// Calendar vs keyboard entry is `DisplayMode`, a parameter, so it folds in. Single vs range is
// `DatePicker` vs `DateRangePicker`, two composables, so it stays two components. The modal form is
// the same picker inside `DatePickerDialog`, which owns a platform window — its container is
// composed here from `DatePickerDefaults` instead.

@Composable
private fun dateDisplayMode(): DisplayMode =
  if (previewOverrideString("mode", "calendar") == "input") DisplayMode.Input
  else DisplayMode.Picker

@CatalogComponent(
  id = "DatePicker/Docked",
  caption = "Calendar grid for picking a single date. Keyboard entry and the mode toggle fold in.",
)
@CatalogModes
@OverrideVariant(name = "input", strings = ["mode=input"])
@OverrideVariant(name = "toggle", strings = ["toggle=on"])
@OverrideVariant(name = "input-toggle", strings = ["mode=input", "toggle=on"])
@Composable
fun DatePickerSticker() = Sticker {
  val mode = dateDisplayMode()
  DatePicker(
    state =
      rememberDatePickerState(
        initialSelectedDateMillis = PINNED_DATE_MILLIS,
        initialDisplayedMonthMillis = PINNED_DATE_MILLIS,
        initialDisplayMode = mode,
      ),
    showModeToggle = previewOverrideString("toggle", "off") == "on",
  )
}

@CatalogComponent(
  id = "DatePicker/Range",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/51954:18254",
  caption = "Start and end dates in one grid. Keyboard entry folds in.",
)
@CatalogModes
@OverrideVariant(name = "input", strings = ["mode=input"])
@Composable
fun DateRangePickerSticker() = Sticker {
  DateRangePicker(
    state =
      rememberDateRangePickerState(
        initialSelectedStartDateMillis = PINNED_DATE_MILLIS,
        initialSelectedEndDateMillis = PINNED_DATE_END_MILLIS,
        initialDisplayedMonthMillis = PINNED_DATE_MILLIS,
        initialDisplayMode = dateDisplayMode(),
      ),
    showModeToggle = false,
  )
}

@CatalogComponent(
  id = "DatePicker/Modal",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/51954:18137",
  caption = "The picker on its own dialog surface, with confirm and dismiss actions.",
)
@CatalogModes
@OverrideVariant(name = "input", strings = ["mode=input"])
@Composable
fun DatePickerModalSticker() = Sticker {
  // `DatePickerDialog` hosts itself in a platform window; the container is composed here from
  // `DatePickerDefaults` so the sticker carries the real shape, colour and elevation.
  val confirm = counted("OK")
  val dismiss = counted("Cancel")
  Surface(
    shape = DatePickerDefaults.shape,
    color = MaterialTheme.colorScheme.surfaceContainerHigh,
    tonalElevation = 6.dp,
  ) {
    Column {
      DatePicker(
        state =
          rememberDatePickerState(
            initialSelectedDateMillis = PINNED_DATE_MILLIS,
            initialDisplayedMonthMillis = PINNED_DATE_MILLIS,
            initialDisplayMode = dateDisplayMode(),
          ),
        showModeToggle = true,
      )
      Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
      ) {
        TextButton(onClick = dismiss.onClick) { Text(dismiss.label) }
        TextButton(onClick = confirm.onClick) { Text(confirm.label) }
      }
    }
  }
}
