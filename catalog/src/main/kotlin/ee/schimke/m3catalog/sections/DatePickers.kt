@file:CatalogGroup(name = "Date pickers", section = "Selection")
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerFormatter
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerDefaults
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.overrides.previewOverrideString
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes360Us
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogChoice
import ee.schimke.m3catalog.counted
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.action_cancel
import ee.schimke.m3catalog.generated.resources.action_clear
import ee.schimke.m3catalog.generated.resources.action_close
import ee.schimke.m3catalog.generated.resources.action_ok
import ee.schimke.m3catalog.generated.resources.action_save
import ee.schimke.m3catalog.generated.resources.date_picker_headline
import ee.schimke.m3catalog.generated.resources.date_range_headline
import ee.schimke.m3catalog.generated.resources.date_range_title
import java.util.Locale
import org.jetbrains.compose.resources.stringResource

// The date is pinned to a fixed instant so the baked capture is deterministic: an unpinned picker
// would open on "today" and every nightly render would differ from the last.
//
// The kit's default is August 17–23, 2025. Pinning those exact instants keeps the render
// deterministic while reproducing the reference month and selection.
// The reference kit's month grid is the August 2023 layout (August 1 on Tuesday), while its
// displayed sample copy says 2025. Seed the grid that the design actually draws; the visible
// sample labels below remain the kit's 2025 copy.
private const val PINNED_DATE_MILLIS = 1692230400000L
private const val PINNED_DATE_END_MILLIS = 1692748800000L
private const val TWO_YEARS_MILLIS = 63158400000L

@Composable
private fun dateMillisOverride(key: String, default: Long): Long =
  previewOverrideString(key, default.toString()).toLongOrNull() ?: default

private fun kitDateFormatter(): DatePickerFormatter {
  val delegate = DatePickerDefaults.dateFormatter()
  return object : DatePickerFormatter by delegate {
    override fun formatMonthYear(monthMillis: Long?, locale: Locale): String =
      delegate.formatMonthYear(monthMillis?.plus(TWO_YEARS_MILLIS), locale).orEmpty()
  }
}

// The kit prints fixed sample copy in the headline — "Mon, Aug 17", "Aug 17 – Aug 23" — rather than
// a formatting of the date its own grid has selected, so the baked capture has to render that copy
// verbatim to reproduce the reference. But a headline is a *value*, not a label: leaving it fixed
// makes the live lane look broken, because picking a date moves the grid selection while the text
// still claims Aug 17. So the kit copy holds exactly as long as the selection is the seeded one —
// which is the only state the baked capture ever sees, leaving the published render byte-identical
// — and past that the headline formats the real selection the way the component does by default.
//
// The skeletons carry no year on purpose. The grid is seeded with the August 2023 layout while
// `kitDateFormatter` labels the header 2025, so a year in the headline would contradict one of the
// two; the weekday and the day-of-month agree with the grid the picker actually draws.
private const val SINGLE_DATE_SKELETON = "EEE, MMM d"
private const val RANGE_DATE_SKELETON = "MMM d"

private fun headlineFormatter(skeleton: String): DatePickerFormatter =
  DatePickerDefaults.dateFormatter(selectedDateSkeleton = skeleton)

@Composable
private fun DateHeadline(
  selectedMillis: Long?,
  seededMillis: Long?,
  displayMode: DisplayMode,
  kitCopy: String,
  modifier: Modifier = Modifier,
) {
  if (selectedMillis == seededMillis) {
    Text(kitCopy, modifier)
  } else {
    DatePickerDefaults.DatePickerHeadline(
      selectedDateMillis = selectedMillis,
      displayMode = displayMode,
      dateFormatter = headlineFormatter(SINGLE_DATE_SKELETON),
      modifier = modifier,
    )
  }
}

@Composable
private fun DateRangeHeadline(
  selectedStartMillis: Long?,
  selectedEndMillis: Long?,
  seededStartMillis: Long?,
  seededEndMillis: Long?,
  displayMode: DisplayMode,
  kitCopy: String,
  modifier: Modifier = Modifier,
) {
  if (selectedStartMillis == seededStartMillis && selectedEndMillis == seededEndMillis) {
    Text(kitCopy, modifier)
  } else {
    DateRangePickerDefaults.DateRangePickerHeadline(
      selectedStartDateMillis = selectedStartMillis,
      selectedEndDateMillis = selectedEndMillis,
      displayMode = displayMode,
      dateFormatter = headlineFormatter(RANGE_DATE_SKELETON),
      modifier = modifier,
    )
  }
}

// Calendar vs keyboard entry is `DisplayMode`, a parameter, so it folds in. Single vs range is
// `DatePicker` vs `DateRangePicker`, two composables, so it stays two components. The modal form is
// the same picker inside `DatePickerDialog`, which owns a platform window — its container is
// composed here from `DatePickerDefaults` instead.

@Composable
private fun dateDisplayMode(): DisplayMode =
  if (catalogChoice("mode", "calendar", "calendar", "input") == "input") DisplayMode.Input
  else DisplayMode.Picker

@CatalogComponent(
  id = "DatePicker/Range",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/51954:18254",
  caption = "Start and end dates in one grid. Keyboard entry folds in.",
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
  val close = counted(stringResource(Res.string.action_close))
  val save = counted(stringResource(Res.string.action_save))
  // Clear owns the picker's selection rather than taking the `counted` tally: the state is right
  // here, so the live lane empties the grid and the headline for real.
  val clear = stringResource(Res.string.action_clear)
  val cancel = counted(stringResource(Res.string.action_cancel))
  val confirm = counted(stringResource(Res.string.action_ok))
  Column(
    Modifier.fillMaxWidth()
      .height(696.dp)
      .background(MaterialTheme.colorScheme.surfaceContainerHigh)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().height(56.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      IconButton(onClick = close.onClick) {
        Icon(Icons.Filled.Close, contentDescription = close.label)
      }
      Spacer(Modifier.weight(1f))
      TextButton(onClick = save.onClick) { Text(save.label) }
      Spacer(Modifier.padding(end = 8.dp))
    }
    Box(Modifier.fillMaxWidth().height(580.dp)) {
      DateRangePicker(
        modifier = Modifier.fillMaxWidth().height(592.dp).offset(y = (-12).dp),
        state = state,
        title = {
          Text(
            stringResource(Res.string.date_range_title),
            Modifier.padding(start = 64.dp).offset(y = 9.dp),
          )
        },
        headline = {
          DateRangeHeadline(
            selectedStartMillis = state.selectedStartDateMillis,
            selectedEndMillis = state.selectedEndDateMillis,
            seededStartMillis = initialStartDateMillis,
            seededEndMillis = initialEndDateMillis,
            displayMode = state.displayMode,
            kitCopy = stringResource(Res.string.date_range_headline),
            modifier = Modifier.padding(start = 64.dp).offset(y = 2.dp),
          )
        },
        dateFormatter = kitDateFormatter(),
        showModeToggle = true,
        colors = DatePickerDefaults.colors(containerColor = Color.Transparent),
      )
    }
    Row(
      modifier = Modifier.fillMaxWidth().height(60.dp).padding(horizontal = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      TextButton(onClick = { state.setSelection(null, null) }) { Text(clear) }
      Spacer(Modifier.weight(1f))
      TextButton(onClick = cancel.onClick) { Text(cancel.label) }
      TextButton(onClick = confirm.onClick) { Text(confirm.label) }
    }
  }
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
  // `DatePickerDialog` hosts itself in a platform window; the container is composed here from
  // `DatePickerDefaults` so the sticker carries the real shape, colour and elevation.
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
  Surface(
    modifier = Modifier.fillMaxWidth().height(524.dp),
    shape = DatePickerDefaults.shape,
    color = MaterialTheme.colorScheme.surfaceContainerHigh,
    tonalElevation = DatePickerDefaults.TonalElevation,
  ) {
    Column {
      DatePicker(
        modifier = Modifier.height(476.dp),
        state = state,
        headline = {
          DateHeadline(
            selectedMillis = state.selectedDateMillis,
            seededMillis = initialDateMillis,
            displayMode = state.displayMode,
            kitCopy = stringResource(Res.string.date_picker_headline),
            modifier = Modifier.padding(start = 24.dp),
          )
        },
        dateFormatter = kitDateFormatter(),
        showModeToggle = true,
        colors = DatePickerDefaults.colors(containerColor = Color.Transparent),
      )
      Row(
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().height(48.dp).padding(horizontal = 8.dp),
      ) {
        TextButton(onClick = { state.selectedDateMillis = null }) { Text(clear) }
        Spacer(Modifier.weight(1f))
        TextButton(onClick = dismiss.onClick) { Text(dismiss.label) }
        TextButton(onClick = confirm.onClick) { Text(confirm.label) }
      }
    }
  }
}
