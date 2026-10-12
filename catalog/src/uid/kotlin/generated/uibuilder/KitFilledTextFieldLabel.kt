package generated.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun KitFilledTextFieldLabel(modifier: androidx.compose.ui.Modifier, value: kotlin.String, enabled: kotlin.Boolean, labelText: kotlin.String) {
    Box(modifier = modifier) {
        TextField(value = value, onValueChange = {}, modifier = Modifier.width(210.dp), enabled = enabled, label = {
            Text(text = labelText)
        })
    }
}
