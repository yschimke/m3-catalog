@file:CatalogGroup(name = "Date pickers", section = "Selection")
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
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
import ee.schimke.composeai.preview.CatalogVariant
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes360Us
import ee.schimke.m3catalog.InlineDialogHost
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogChoice
import ee.schimke.m3catalog.counted
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.action_cancel
import ee.schimke.m3catalog.generated.resources.action_clear
import ee.schimke.m3catalog.generated.resources.action_ok
import org.jetbrains.compose.resources.stringResource

// The date is pinned to a fixed instant so the baked capture is deterministic: an unpinned picker
// would open on "today" and every nightly render would differ from the last.
//
// Seed the kit's stated August 17–23, 2025 dates as real instants. Parity must expose a weekday or
// grid disagreement instead of relabelling a different calendar year.
private const val PINNED_DATE_MILLIS = 1755388800000L
private const val PINNED_DATE_END_MILLIS = 1755907200000L

@Composable
private fun dateMillisOverride(key: String, default: Long): Long =
  previewOverrideString(key, default.toString()).toLongOrNull() ?: default

// Calendar vs keyboard entry is `DisplayMode`, a parameter, so it folds in. Single vs range is
// `DatePicker` vs `DateRangePicker`, two composables — but the kit carries both as `Type` values of
// the one `Modal date picker` set, so the range form folds in as a `@CatalogVariant` too. Its prop
// spells the kit's own value, `full-screen (range)`, because that is what the resolver matches
// against; `type=range` resolves to nothing and silently drops the node. The modal single picker
// uses the real `DatePickerDialog`; InlineDialogHost replaces only its platform window.

@Composable
private fun dateDisplayMode(): DisplayMode =
  if (catalogChoice("mode", "calendar", "calendar", "input") == "input") DisplayMode.Input
  else DisplayMode.Picker

@CatalogVariant(
  of = "DatePicker/Modal",
  props = ["type=full-screen (range)"],
  caption = "Start and end dates in one grid.",
)
@CatalogModes360Us
@OverrideVariant(name = "input", strings = ["mode=input"])
@Composable
fun DateRangePickerSticker() = Sticker {
  val initialStartDateMillis = dateMillisOverride("dateMillis", PINNED_DATE_MILLIS)
  val initialEndDateMillis = dateMillisOverride("endDateMillis", PINNED_DATE_END_MILLIS)
  val state =
    rememberDateRangePickerState(
      initialSelectedStartDateMillis = initialStartDateMillis,
      initialSelectedEndDateMillis = initialEndDateMillis,
      initialDisplayedMonthMillis = initialStartDateMillis,
      initialDisplayMode = dateDisplayMode(),
    )
  DateRangePicker(
    modifier = Modifier.fillMaxWidth().height(696.dp),
    state = state,
    showModeToggle = true,
  )
}

@CatalogComponent(
  id = "DatePicker/Modal",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/51954:18137",
  caption = "The picker on its own dialog surface, with confirm and dismiss actions.",
)
@CatalogModes360Us
@OverrideVariant(name = "input", strings = ["mode=input"])
@Composable
fun DatePickerModalSticker() = Sticker {
  val initialDateMillis = dateMillisOverride("dateMillis", PINNED_DATE_MILLIS)
  val state =
    rememberDatePickerState(
      initialSelectedDateMillis = initialDateMillis,
      initialDisplayedMonthMillis = initialDateMillis,
      initialDisplayMode = dateDisplayMode(),
    )
  val confirm = counted(stringResource(Res.string.action_ok))
  val dismiss = counted(stringResource(Res.string.action_cancel))
  // Clear owns the picker's selection rather than taking the `counted` tally: the state is right
  // here, so the live lane empties the grid and the headline for real.
  val clear = stringResource(Res.string.action_clear)
  InlineDialogHost {
    DatePickerDialog(
      onDismissRequest = {},
      confirmButton = { TextButton(onClick = confirm.onClick) { Text(confirm.label) } },
      dismissButton = {
        Row {
          TextButton(onClick = { state.selectedDateMillis = null }) { Text(clear) }
          TextButton(onClick = dismiss.onClick) { Text(dismiss.label) }
        }
      },
    ) {
      DatePicker(
        state = state,
        showModeToggle = true,
      )
    }
  }
}
