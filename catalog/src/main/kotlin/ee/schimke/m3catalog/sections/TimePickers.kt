@file:CatalogGroup(name = "Time pickers", section = "Selection")
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerLayoutType
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.overrides.previewOverrideString
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.counted
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.action_cancel
import ee.schimke.m3catalog.generated.resources.action_ok
import ee.schimke.m3catalog.generated.resources.time_enter
import ee.schimke.m3catalog.generated.resources.time_select
import org.jetbrains.compose.resources.stringResource

// Pinned to 10:10 — the traditional watch-face time, and deterministic across renders.
//
// The clock face and the keyboard entry form are two composables, so two components. Inside each,
// the 12-hour / 24-hour axis is a state parameter; the dial additionally has the vertical and
// horizontal layouts the kit shows for portrait and landscape.

@Composable private fun timeIs24Hour(): Boolean = previewOverrideString("hours", "24") == "24"

@Composable
private fun TimePickerFrame(
  headline: String,
  switchIcon: ImageVector,
  switchDescription: String,
  modifier: Modifier,
  content: @Composable () -> Unit,
) {
  val switch = counted(switchDescription)
  val cancel = counted(stringResource(Res.string.action_cancel))
  val ok = counted(stringResource(Res.string.action_ok))
  Surface(
    modifier = modifier,
    shape = AlertDialogDefaults.shape,
    color = AlertDialogDefaults.containerColor,
    tonalElevation = AlertDialogDefaults.TonalElevation,
  ) {
    Column {
      Text(
        headline,
        modifier = Modifier.padding(start = 24.dp, top = 24.dp, end = 24.dp),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) { content() }
      Row(
        Modifier.fillMaxWidth().padding(start = 12.dp, end = 24.dp, bottom = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        IconButton(onClick = switch.onClick) { Icon(switchIcon, contentDescription = switch.label) }
        Spacer(Modifier.weight(1f))
        TextButton(onClick = cancel.onClick) { Text(cancel.label) }
        TextButton(onClick = ok.onClick) { Text(ok.label) }
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
@OverrideVariant(name = "horizontal", strings = ["layout=horizontal"])
@OverrideVariant(name = "12-hour-horizontal", strings = ["hours=12", "layout=horizontal"])
@Composable
fun TimePickerSticker() = Sticker {
  val is24Hour = timeIs24Hour()
  val horizontal = previewOverrideString("layout", "vertical") == "horizontal"
  TimePickerFrame(
    headline = stringResource(Res.string.time_select),
    switchIcon = Icons.Filled.Keyboard,
    switchDescription = stringResource(Res.string.time_enter),
    modifier =
      if (horizontal) Modifier.width(572.dp).height(384.dp)
      else Modifier.width(328.dp).height(520.dp),
  ) {
    TimePicker(
      state = rememberTimePickerState(initialHour = 10, initialMinute = 10, is24Hour = is24Hour),
      layoutType =
        if (horizontal) TimePickerLayoutType.Horizontal else TimePickerLayoutType.Vertical,
    )
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
fun TimeInputSticker() = Sticker {
  val is24Hour = timeIs24Hour()
  TimePickerFrame(
    headline = stringResource(Res.string.time_enter),
    switchIcon = Icons.Filled.AccessTime,
    switchDescription = stringResource(Res.string.time_select),
    modifier = Modifier.width(if (is24Hour) 264.dp else 328.dp).height(243.dp),
  ) {
    TimeInput(
      state = rememberTimePickerState(initialHour = 10, initialMinute = 10, is24Hour = is24Hour)
    )
  }
}
