package generated.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable

@Composable
internal fun KitSwitch(modifier: androidx.compose.ui.Modifier, checked: kotlin.Boolean, enabled: kotlin.Boolean, onSwitchCheckedChange: () -> kotlin.Unit) {
    Box(modifier = modifier) {
        Box {
            Switch(checked = checked, onCheckedChange = { _ -> onSwitchCheckedChange() }, enabled = enabled)
        }
    }
}
