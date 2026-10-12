package generated.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun KitSwitchIconOff(modifier: androidx.compose.ui.Modifier, checked: kotlin.Boolean, enabled: kotlin.Boolean, onSwitchCheckedChange: () -> kotlin.Unit) {
    Box(modifier = modifier) {
        Box {
            Switch(checked = checked, onCheckedChange = { _ -> onSwitchCheckedChange() }, thumbContent = {
                Icon(imageVector = Icons.Filled.Close, contentDescription = null, modifier = Modifier.size(16.dp))
            }, enabled = enabled)
        }
    }
}
