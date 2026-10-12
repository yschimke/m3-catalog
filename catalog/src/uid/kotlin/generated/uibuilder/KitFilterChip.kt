package generated.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
internal fun KitFilterChip(modifier: androidx.compose.ui.Modifier, selected: kotlin.Boolean, enabled: kotlin.Boolean, label: kotlin.String, onChipClick: () -> kotlin.Unit) {
    Box(modifier = modifier) {
        FilterChip(selected = selected, onClick = onChipClick, label = {
            Text(text = label)
        }, enabled = enabled)
    }
}
