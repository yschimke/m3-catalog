package generated.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ElevatedSuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
internal fun KitSuggestionChipElevated(modifier: androidx.compose.ui.Modifier, enabled: kotlin.Boolean, label: kotlin.String, onChipClick: () -> kotlin.Unit) {
    Box(modifier = modifier) {
        ElevatedSuggestionChip(onClick = onChipClick, label = {
            Text(text = label)
        }, enabled = enabled)
    }
}
