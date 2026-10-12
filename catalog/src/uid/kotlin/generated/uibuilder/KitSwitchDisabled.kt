package generated.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable

@Composable
internal fun KitSwitchDisabled(modifier: androidx.compose.ui.Modifier, checked: kotlin.Boolean, enabled: kotlin.Boolean) {
    Box(modifier = modifier) {
        Box {
            Switch(checked = checked, onCheckedChange = null, enabled = enabled)
        }
    }
}
