package generated.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.m3catalog.uid.TimePickerDialogTitle
import ee.schimke.m3catalog.uid.TimePickerModeToggle

@Composable
internal fun KitTimePickerInput(modifier: androidx.compose.ui.Modifier, dismissButton: kotlin.String, confirmButton: kotlin.String, onToggleClick: () -> kotlin.Unit, onCancelClick: () -> kotlin.Unit, onOkClick: () -> kotlin.Unit) {
    Box(modifier = modifier) {
        AlertDialog(onDismissRequest = {}, confirmButton = {
            TextButton(onClick = onOkClick) {
                Text(text = confirmButton)
            }
        }, modifier = Modifier.width(264.dp), dismissButton = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TimePickerModeToggle(onClick = onToggleClick, input = true)
                TextButton(onClick = onCancelClick) {
                    Text(text = dismissButton)
                }
            }
        }, title = {
            TimePickerDialogTitle(input = true)
        }, text = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                TimeInput(state = rememberTimePickerState(initialHour = 20, initialMinute = 0, is24Hour = true))
            }
        })
    }
}
