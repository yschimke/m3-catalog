package generated.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
internal fun KitCheckboxError(modifier: androidx.compose.ui.Modifier, checked: kotlin.Boolean, enabled: kotlin.Boolean, onCheckboxCheckedChange: () -> kotlin.Unit) {
    Box(modifier = modifier) {
        Box {
            Checkbox(checked = checked, onCheckedChange = { _ -> onCheckboxCheckedChange() }, enabled = enabled, colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.error, uncheckedColor = MaterialTheme.colorScheme.error, checkmarkColor = MaterialTheme.colorScheme.onError))
        }
    }
}
