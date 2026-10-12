package generated.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun KitAssistChipElevatedFavicon(modifier: androidx.compose.ui.Modifier, enabled: kotlin.Boolean, label: kotlin.String, onChipClick: () -> kotlin.Unit) {
    Box(modifier = modifier) {
        ElevatedAssistChip(onClick = onChipClick, label = {
            Text(text = label)
        }, enabled = enabled, leadingIcon = {
            Icon(imageVector = Icons.Filled.Person, contentDescription = null, modifier = Modifier.size(18.dp))
        })
    }
}
