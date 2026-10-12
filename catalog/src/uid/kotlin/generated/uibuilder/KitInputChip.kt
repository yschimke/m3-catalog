package generated.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.InputChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
internal fun KitInputChip(modifier: androidx.compose.ui.Modifier, selected: kotlin.Boolean, enabled: kotlin.Boolean, label: kotlin.String, onChipClick: () -> kotlin.Unit) {
    Box(modifier = modifier) {
        InputChip(selected = selected, onClick = onChipClick, label = {
            Text(text = label)
        }, enabled = enabled)
    }
}
