package generated.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.runtime.Composable

@Composable
internal fun KitRadioButtonError(modifier: androidx.compose.ui.Modifier, selected: kotlin.Boolean, enabled: kotlin.Boolean, onRadioClick: () -> kotlin.Unit) {
    Box(modifier = modifier) {
        Box {
            RadioButton(selected = selected, onClick = onRadioClick, enabled = enabled, colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.error, unselectedColor = MaterialTheme.colorScheme.error))
        }
    }
}
