package generated.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun KitSnackbar(modifier: androidx.compose.ui.Modifier, text: kotlin.String) {
    Box(modifier = modifier) {
        Box(modifier = Modifier.width(344.dp)) {
            Snackbar {
                Text(text = text)
            }
        }
    }
}
