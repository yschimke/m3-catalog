@file:CatalogGroup(name = "Time pickers", section = "Selection")
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerLayoutType
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.overrides.previewOverrideString
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.CatalogModes560
import ee.schimke.m3catalog.InlineDialogHost
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogChoice
import ee.schimke.m3catalog.counted
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.action_cancel
import ee.schimke.m3catalog.generated.resources.action_ok
import ee.schimke.m3catalog.generated.resources.time_enter
import ee.schimke.m3catalog.generated.resources.time_select
import ee.schimke.m3catalog.toggleable
import org.jetbrains.compose.resources.stringResource

// Pinned to the kit's 20:00 example so the baked capture is deterministic across renders.
//
// The clock face and the keyboard entry form are two composables, so two components. Inside each,
// the 12-hour / 24-hour axis is a state parameter; the dial additionally has the vertical and
// horizontal layouts the kit shows for portrait and landscape.

/**
 * The landscape dialog's width.
 *
 * `AlertDialog` clamps itself to Material's 560dp `DialogMaxWidth`, so the 572dp this used to ask
 * for was never granted — and 400dp was all the default harness offered anyway. The landscape
 * `TimePicker` lays out 216dp of clock display, a 36dp gutter and the 256dp dial: 508dp, which is
 * what the 560dp dialog leaves once its own padding is taken off.
 */
private val HorizontalDialogWidth = 560.dp

@Composable
private fun timeIs24Hour(): Boolean =
  catalogChoice("hours", "24", "24" to "24 hour", "12" to "12 hour") == "24"

@Composable
private fun initialTime(): Pair<Int, Int> {
  val hour = previewOverrideString("hour", "20").toIntOrNull()?.takeIf { it in 0..23 } ?: 20
  val minute = previewOverrideString("minute", "0").toIntOrNull()?.takeIf { it in 0..59 } ?: 0
  return hour to minute
}

@Composable
private fun TimePickerFrame(
  headline: String,
  switchIcon: ImageVector,
  switchDescription: String,
  onSwitch: () -> Unit,
  modifier: Modifier,
  content: @Composable () -> Unit,
) {
  val cancel = counted(stringResource(Res.string.action_cancel))
  val ok = counted(stringResource(Res.string.action_ok))
  InlineDialogHost {
    AlertDialog(
      onDismissRequest = {},
      modifier = modifier,
      title = { Text(headline) },
      text = { Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { content() } },
      dismissButton = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(onClick = onSwitch) {
            Icon(switchIcon, contentDescription = switchDescription)
          }
          TextButton(onClick = cancel.onClick) { Text(cancel.label) }
        }
      },
      confirmButton = { TextButton(onClick = ok.onClick) { Text(ok.label) } },
    )
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

/**
 * The landscape dial, which is a `@CatalogVariant` rather than another `@OverrideVariant` for one
 * reason: **`widthDp` belongs to the `@Preview`, not to the seed.**
 *
 * The landscape layout needs 508dp of content — 216dp of clock display, a 36dp gutter and the 256dp
 * dial — where the portrait one needs 328dp, and the default harness caps a wrap-content sticker at
 * 400dp. Seeded onto the portrait sticker the layout had no way to ask for more room, so the dial
 * overflowed the display and clipped at the dialog edge (#146). A variant is a composable of its
 * own, so it carries [CatalogModes560] and leaves the portrait cells on their own footprint. The
 * `layout` knob still exists and still flips on the live lane; this sticker only starts it on the
 * other value.
 */
@CatalogVariant(
  of = "TimePicker/Dial",
  props = ["layout=horizontal"],
  kitAxis = "Orientation",
  kitValue = "Horizontal",
  caption = "The landscape dial the kit pairs with the portrait one. 12-hour folds in.",
)
@CatalogModes560
@OverrideVariant(name = "12-hour", strings = ["hours=12"])
@Composable
fun TimePickerHorizontalSticker() = Sticker {
  TimePickerDialogFrame(seedInput = false, seedHorizontal = true)
}

/**
 * The dial and the keyboard form publish as two components — two composables, two kit nodes — but
 * inside the dialog the icon button between them is the *mode switch*, not decoration. So the frame
 * owns the mode rather than handing that button the [counted] tally, whose only effect there was on
 * a `contentDescription` nothing paints: the baked capture stays frozen on the form its component
 * publishes, and on the live lane the switch really swaps dial for keyboard, carrying the title,
 * the icon and the dialog's own footprint with it. The entered time is hoisted alongside it, so
 * switching modes keeps the time the way the real dialog does instead of resetting to the seed.
 */
@Composable
private fun TimePickerDialogFrame(seedInput: Boolean, seedHorizontal: Boolean = false) {
  val is24Hour = timeIs24Hour()
  val horizontal =
    catalogChoice(
      "layout",
      if (seedHorizontal) "horizontal" else "vertical",
      "vertical",
      "horizontal",
    ) == "horizontal"
  val (initialHour, initialMinute) = initialTime()
  var input by toggleable(seedInput)
  // `rememberTimePickerState` saves through a keyless `rememberSaveable`, so a knob that moves
  // under a live re-render — the preview server's `?themeProvider=` lane recomposes a held session
  // rather than building a fresh one — never reaches the state, and the 12-hour cells came back
  // rendering the 24-hour dial (#146, #147). `key` rebuilds the state when the seed does.
  val state =
    key(is24Hour, initialHour, initialMinute) {
      rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = is24Hour,
      )
    }
  TimePickerFrame(
    headline = stringResource(if (input) Res.string.time_enter else Res.string.time_select),
    switchIcon = if (input) Icons.Filled.AccessTime else Icons.Filled.Keyboard,
    switchDescription =
      stringResource(if (input) Res.string.time_select else Res.string.time_enter),
    onSwitch = { input = !input },
    modifier =
      if (input) Modifier.width(if (is24Hour) 264.dp else 328.dp)
      else if (horizontal) Modifier.width(HorizontalDialogWidth) else Modifier.width(328.dp),
  ) {
    if (input) {
      TimeInput(state = state)
    } else {
      TimePicker(
        state = state,
        layoutType =
          if (horizontal) TimePickerLayoutType.Horizontal else TimePickerLayoutType.Vertical,
      )
    }
  }
}

@CatalogComponent(
  id = "TimePicker/Input",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/52949:28069",
  caption = "Keyboard entry instead of the dial. 12-hour folds in.",
)
@CatalogModes
@OverrideVariant(name = "12-hour", strings = ["hours=12"])
@Composable
fun TimeInputSticker() = Sticker { TimePickerDialogFrame(seedInput = true) }
