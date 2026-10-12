package generated.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable

@Composable
internal fun KitRadioButton(modifier: androidx.compose.ui.Modifier, selected: kotlin.Boolean, enabled: kotlin.Boolean, onRadioClick: () -> kotlin.Unit) {
    Box(modifier = modifier) {
        Box {
            RadioButton(selected = selected, onClick = onRadioClick, enabled = enabled)
        }
    }
}
