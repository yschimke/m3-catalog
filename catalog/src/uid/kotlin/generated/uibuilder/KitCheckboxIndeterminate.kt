package generated.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.ui.state.ToggleableState

@Composable
internal fun KitCheckboxIndeterminate(modifier: androidx.compose.ui.Modifier, enabled: kotlin.Boolean) {
    Box(modifier = modifier) {
        Box {
            TriStateCheckbox(state = ToggleableState.Indeterminate, onClick = null, enabled = enabled)
        }
    }
}
