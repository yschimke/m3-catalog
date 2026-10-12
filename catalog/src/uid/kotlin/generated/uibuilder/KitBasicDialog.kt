package generated.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun KitBasicDialog(modifier: androidx.compose.ui.Modifier, title: kotlin.String, text: kotlin.String, confirmButton: kotlin.String, dismissButton: kotlin.String, onConfirmClick: () -> kotlin.Unit, onDismissClick: () -> kotlin.Unit) {
    Box(modifier = modifier) {
        AlertDialog(onDismissRequest = {}, confirmButton = {
            TextButton(onClick = onConfirmClick) {
                Text(text = confirmButton)
            }
        }, modifier = Modifier.width(312.dp), dismissButton = {
            TextButton(onClick = onDismissClick) {
                Text(text = dismissButton)
            }
        }, title = {
            Text(text = title)
        }, text = {
            Text(text = text)
        })
    }
}
