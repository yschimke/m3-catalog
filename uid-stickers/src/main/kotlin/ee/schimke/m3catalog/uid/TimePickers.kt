@file:CatalogGroup(name = "Time pickers", section = "Selection")

package ee.schimke.m3catalog.uid

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import ee.schimke.composeai.overrides.previewOverrideInt
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.FocusDirection
import ee.schimke.composeai.preview.FocusedPreview
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.KeyboardNavigable
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogChoice
import ee.schimke.m3catalog.catalogText
import ee.schimke.m3catalog.counted
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.action_cancel
import ee.schimke.m3catalog.generated.resources.action_ok
import ee.schimke.m3catalog.toggleable
import generated.uibuilder.KitTimePicker
import generated.uibuilder.KitTimePicker12Hour
import generated.uibuilder.KitTimePickerInput
import generated.uibuilder.KitTimePickerInput12Hour
import org.jetbrains.compose.resources.stringResource

// The body is `stickers/time-picker.uid`: the hand-written frame's `AlertDialog` (`m3/dialog`) with
// the dial (`m3/time-picker`, `mode=dial`) centred in its text slot, the mode toggle and Cancel in
// its dismiss row and OK as its confirm button. The picker's state is the document's own: `hour`,
// `minute` and `is24Hour` are state properties the exporter writes as
// `rememberTimePickerState(initialHour = 20, initialMinute = 0, is24Hour = …)`. Keyboard entry
// (`mode=input`, which exports `TimeInput`) and the 12-hour clock are cells, because a variant
// selector and a state property are literals; so is the dialog's width, a modifier.
//
// The shell owns what the document cannot: the dial/keyboard switch (`toggleable`, as the
// hand-written frame owns it), the `key` that rebuilds the state when a seed moves, and the
// inline dialog host. Gaps, read here for their published knob keys but not yet reaching the
// document: `hour`/`minute` (the exporter has no binding type for a state property, so the
// document pins the kit's 20:00) and `layout=horizontal` (no enum mapping for `layoutType`).
// Neither moves in any capture of these two stickers.

@Composable
private fun TimePickerDialogFrame(seedInput: Boolean) {
  val is24Hour = catalogChoice("hours", "24", "24" to "24 hour", "12" to "12 hour") == "24"
  // Read for its knob key only: the landscape layout is `TimePickerHorizontalSticker`'s, and the
  // builder cannot yet spell `TimePickerLayoutType.Horizontal`.
  catalogChoice("layout", "vertical", "vertical", "horizontal")
  val hour = previewOverrideInt("hour", 20).takeIf { it in 0..23 } ?: 20
  val minute = previewOverrideInt("minute", 0).takeIf { it in 0..59 } ?: 0
  var input by toggleable(seedInput)
  val cancel = counted(catalogText("dismissButton", stringResource(Res.string.action_cancel)))
  val ok = counted(catalogText("confirmButton", stringResource(Res.string.action_ok)))
  val toggle = { input = !input }
  key(is24Hour, hour, minute) {
    PickerDialogHost {
      when {
        input && is24Hour ->
          KitTimePickerInput(
            Modifier,
            dismissButton = cancel.label,
            confirmButton = ok.label,
            onToggleClick = toggle,
            onCancelClick = cancel.onClick,
            onOkClick = ok.onClick,
          )
        input ->
          KitTimePickerInput12Hour(
            Modifier,
            dismissButton = cancel.label,
            confirmButton = ok.label,
            onToggleClick = toggle,
            onCancelClick = cancel.onClick,
            onOkClick = ok.onClick,
          )
        is24Hour ->
          KitTimePicker(
            Modifier,
            dismissButton = cancel.label,
            confirmButton = ok.label,
            onToggleClick = toggle,
            onCancelClick = cancel.onClick,
            onOkClick = ok.onClick,
          )
        else ->
          KitTimePicker12Hour(
            Modifier,
            dismissButton = cancel.label,
            confirmButton = ok.label,
            onToggleClick = toggle,
            onCancelClick = cancel.onClick,
            onOkClick = ok.onClick,
          )
      }
    }
  }
}

@CatalogComponent(
  id = "TimePicker/Dial",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/52949:27946",
  caption = "Clock dial for picking an hour and minute. 12-hour and the landscape layout fold in.",
)
@CatalogModes
@OverrideVariant(name = "12-hour", strings = ["hours=12"])
@Composable
fun TimePickerSticker() = Sticker { TimePickerDialogFrame(seedInput = false) }

@CatalogComponent(
  id = "TimePicker/Input",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/52949:28069",
  caption = "Keyboard entry instead of the dial. 12-hour folds in.",
)
@CatalogModes
@OverrideVariant(name = "12-hour", strings = ["hours=12"])
// The baked half of the question `KeyboardNavigable` answers live: four `moveFocus(Next)` steps,
// one PNG per stop, so a reviewer can see which control each Tab lands on and what the focus
// treatment draws when it gets there. The annotation flips the input mode for its own captures, so
// the knob is not what drives these — and because a fan-out of more than one step now keeps the
// undriven capture too (compose-ai-tools#5088), the resting sticker is still published beside them.
@FocusedPreview(
  traverse =
    [
      FocusDirection.Next,
      FocusDirection.Next,
      FocusDirection.Next,
      FocusDirection.Next,
    ]
)
@Composable
fun TimeInputSticker() = Sticker { KeyboardNavigable { TimePickerDialogFrame(seedInput = true) } }
