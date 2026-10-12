package generated.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable

@Composable
internal fun KitRadioButtonDisabled(modifier: androidx.compose.ui.Modifier, selected: kotlin.Boolean, enabled: kotlin.Boolean) {
    Box(modifier = modifier) {
        Box {
            RadioButton(selected = selected, onClick = null, enabled = enabled)
        }
    }
}
