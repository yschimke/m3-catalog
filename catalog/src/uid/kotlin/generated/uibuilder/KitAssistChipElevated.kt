package generated.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
internal fun KitAssistChipElevated(modifier: androidx.compose.ui.Modifier, enabled: kotlin.Boolean, label: kotlin.String, onChipClick: () -> kotlin.Unit) {
    Box(modifier = modifier) {
        ElevatedAssistChip(onClick = onChipClick, label = {
            Text(text = label)
        }, enabled = enabled)
    }
}
