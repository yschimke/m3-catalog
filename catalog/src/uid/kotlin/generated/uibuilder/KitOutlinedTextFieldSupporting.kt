package generated.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun KitOutlinedTextFieldSupporting(modifier: androidx.compose.ui.Modifier, value: kotlin.String, enabled: kotlin.Boolean, supportingText: kotlin.String) {
    Box(modifier = modifier) {
        Box(modifier = Modifier.padding(top = 8.dp)) {
            OutlinedTextField(value = value, onValueChange = {}, modifier = Modifier.width(210.dp), enabled = enabled, supportingText = {
                Text(text = supportingText)
            })
        }
    }
}
