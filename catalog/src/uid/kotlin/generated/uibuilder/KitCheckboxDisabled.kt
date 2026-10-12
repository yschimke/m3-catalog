package generated.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable

@Composable
internal fun KitCheckboxDisabled(modifier: androidx.compose.ui.Modifier, checked: kotlin.Boolean, enabled: kotlin.Boolean) {
    Box(modifier = modifier) {
        Box {
            Checkbox(checked = checked, onCheckedChange = null, enabled = enabled)
        }
    }
}
