@file:CatalogGroup(name = "Date pickers", section = "Selection")
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker

// The date is pinned to a fixed instant so the baked capture is deterministic: an unpinned picker
// would open on "today" and every nightly render would differ from the last.
//
// 2024-01-15T00:00:00Z, chosen because it sits mid-month and mid-week, so the sticker shows a full
// grid rather than a nearly empty first or last row.
private const val PINNED_DATE_MILLIS = 1705276800000L
private const val PINNED_DATE_END_MILLIS = 1705968000000L

@CatalogComponent(id = "DatePicker/Docked", caption = "Calendar grid for picking a single date.")
@CatalogModes
@Composable
fun DatePickerSticker() = Sticker {
  DatePicker(
    state =
      rememberDatePickerState(
        initialSelectedDateMillis = PINNED_DATE_MILLIS,
        initialDisplayedMonthMillis = PINNED_DATE_MILLIS,
      )
  )
}

@CatalogVariant(
  of = "DatePicker/Docked",
  props = ["selection=range"],
  caption = "Start and end dates in one grid.",
)
@CatalogModes
@Composable
fun DateRangePickerSticker() = Sticker {
  DateRangePicker(
    state =
      rememberDateRangePickerState(
        initialSelectedStartDateMillis = PINNED_DATE_MILLIS,
        initialSelectedEndDateMillis = PINNED_DATE_END_MILLIS,
        initialDisplayedMonthMillis = PINNED_DATE_MILLIS,
      )
  )
}
