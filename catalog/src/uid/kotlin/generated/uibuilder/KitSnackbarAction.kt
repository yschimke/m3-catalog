package generated.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun KitSnackbarAction(modifier: androidx.compose.ui.Modifier, text: kotlin.String, action: kotlin.String, onUndoClick: () -> kotlin.Unit) {
    Box(modifier = modifier) {
        Box(modifier = Modifier.width(344.dp)) {
            Snackbar(action = {
                TextButton(onClick = onUndoClick) {
                    Text(text = action)
                }
            }) {
                Text(text = text)
            }
        }
    }
}
