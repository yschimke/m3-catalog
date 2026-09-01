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
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.overrides.previewOverrideString
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModesDatePickerUs
import ee.schimke.m3catalog.InlineDialogHost
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogChoice
import ee.schimke.m3catalog.catalogText
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
// says `range`, the Compose word, and `kitAxis`/`kitValue` carry the kit's own spelling beside it.
// The prop used to have to BE the kit's spelling — `type=full-screen (range)`, parentheses and all
// — because that is what the resolver matched against, which put a Figma string in Kotlin source
// that would rot the next time the kit renamed a value (compose-ai-tools#4086). The modal single
// picker uses the real `DatePickerDialog`; InlineDialogHost replaces only its platform window.

@Composable
private fun dateDisplayMode(): DisplayMode =
  if (catalogChoice("mode", "calendar", "calendar", "input") == "input") DisplayMode.Input
  else DisplayMode.Picker

@CatalogVariant(
  of = "DatePicker/Modal",
  props = ["type=range"],
  kitAxis = "Type",
  kitValue = "Full-screen (range)",
  caption = "Start and end dates in one grid.",
)
@CatalogModesDatePickerUs
@OverrideVariant(name = "input", strings = ["mode=input"])
@Composable
fun DateRangePickerSticker() = Sticker {
  val initialStartDateMillis = dateMillisOverride("dateMillis", PINNED_DATE_MILLIS)
  val initialEndDateMillis = dateMillisOverride("endDateMillis", PINNED_DATE_END_MILLIS)
  val displayMode = dateDisplayMode()
  val state =
    key(initialStartDateMillis, initialEndDateMillis, displayMode) {
      rememberDateRangePickerState(
        initialSelectedStartDateMillis = initialStartDateMillis,
        initialSelectedEndDateMillis = initialEndDateMillis,
        initialDisplayedMonthMillis = initialStartDateMillis,
        initialDisplayMode = displayMode,
      )
    }
  DateRangePicker(
    // The calendar grid is a lazy list, so it needs a bounded height to have a full-screen one to
    // fill; the two text fields the input mode puts in its place do not, and the 696dp published
    // that mode as a pair of fields above 500dp of empty surface (#142).
    modifier =
      if (displayMode == DisplayMode.Input) Modifier.fillMaxWidth()
      else Modifier.fillMaxWidth().height(696.dp),
    state = state,
    showModeToggle = true,
  )
}

@CatalogComponent(
  id = "DatePicker/Modal",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/51954:18137",
  caption = "The picker on its own dialog surface, with confirm and dismiss actions.",
)
@CatalogModesDatePickerUs
@OverrideVariant(name = "input", strings = ["mode=input"])
@Composable
fun DatePickerModalSticker() = Sticker {
  val initialDateMillis = dateMillisOverride("dateMillis", PINNED_DATE_MILLIS)
  val displayMode = dateDisplayMode()
  // Keyed for the same reason the time picker is: `rememberDatePickerState` saves through a keyless
  // `rememberSaveable`, so a knob that moves under a live re-render never reaches the state.
  val state =
    key(initialDateMillis, displayMode) {
      rememberDatePickerState(
        initialSelectedDateMillis = initialDateMillis,
        initialDisplayedMonthMillis = initialDateMillis,
        initialDisplayMode = displayMode,
      )
    }
  val confirm = counted(catalogText("confirmButton", stringResource(Res.string.action_ok)))
  val dismiss = counted(catalogText("dismissButton", stringResource(Res.string.action_cancel)))
  // Clear owns the picker's selection rather than taking the `counted` tally: the state is right
  // here, so the live lane empties the grid and the headline for real.
  val clear = catalogText("clearButton", stringResource(Res.string.action_clear))
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
