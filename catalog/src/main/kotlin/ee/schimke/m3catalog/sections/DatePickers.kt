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
import ee.schimke.composeai.preview.SettledPreview
import ee.schimke.m3catalog.CatalogModesDatePickerUs
import ee.schimke.m3catalog.InlineDialogHost
import ee.schimke.m3catalog.KeyboardNavigable
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

// Both stickers below PIN their capture to 600ms of virtual time rather than to the first frame.
// It is the input mode that needs it; the calendar cells settle before the first frame and come
// back byte-identical either way.
//
// `DateInputTextField` starts its `OutlinedTextField` empty and writes the seeded date in a
// `LaunchedEffect(initialDateMillis)`, and `DatePickerDialog` focuses that field after
// `DurationMedium2`. Both land after the first frame, so the label's `updateTransition` is still
// at its unfocused-empty coordinate when a first-frame capture is taken: the resting "Date" label
// published on top of the "08/17/2025" it should have floated above (#269). Nothing the sticker
// passes can move that — the tween is internal to the Material composable.
//
// `afterMs` rather than the auto walk because the focused field carries a BLINKING CURSOR, which
// never quiesces: auto would spend the whole budget and record the capture as unsettled, which is
// the diagnostic a genuinely broken reveal needs to keep for itself. 600ms is past the text, the
// focus request and both tweens, and inside the cursor's first on-phase, so the still is
// deterministic — the same requirement the pinned instant above exists for.
@CatalogVariant(
  of = "DatePicker/Modal",
  props = ["type=range"],
  kitAxis = "Type",
  kitValue = "Full-screen (range)",
  caption = "Start and end dates in one grid.",
)
@CatalogModesDatePickerUs
@SettledPreview(afterMs = 600)
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
// Pinned for the reason recorded above the range sticker.
@SettledPreview(afterMs = 600)
@OverrideVariant(name = "input", strings = ["mode=input"])
@Composable
fun DatePickerModalSticker() = Sticker {
  // Its input mode is a form — the entry field, the mode toggle, dismiss and confirm — so it takes
  // the keyboard-navigation knob. See [KeyboardNavigable]; off by default.
  //
  // It carries no BAKED walk, though, and neither does the range picker beside it: under
  // `@FocusedPreview` both compose a second root — measured, "Expected exactly '1' node but found
  // '2' nodes that satisfy: (isRoot)", the second one always the full 945x2100 canvas — and the
  // capture resolves the root to exactly one node, so every capture of the preview fails, the
  // undriven ones included. `TimeInputSticker` is the same form and does not do it, so the baked
  // walk lives there and this sticker keeps the live knob only.
  KeyboardNavigable {
    val initialDateMillis = dateMillisOverride("dateMillis", PINNED_DATE_MILLIS)
    val displayMode = dateDisplayMode()
    // Keyed for the same reason the time picker is: `rememberDatePickerState` saves through a
    // keyless
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
}
