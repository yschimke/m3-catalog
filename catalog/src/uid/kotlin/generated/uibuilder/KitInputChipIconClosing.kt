package generated.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.action_remove
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun KitInputChipIconClosing(modifier: androidx.compose.ui.Modifier, selected: kotlin.Boolean, enabled: kotlin.Boolean, label: kotlin.String, onChipClick: () -> kotlin.Unit) {
    Box(modifier = modifier) {
        InputChip(selected = selected, onClick = onChipClick, label = {
            Text(text = label)
        }, enabled = enabled, avatar = {
            Icon(imageVector = Icons.Filled.Event, contentDescription = null, modifier = Modifier.size(18.dp))
        }, trailingIcon = {
            Icon(imageVector = Icons.Filled.Close, contentDescription = stringResource(Res.string.action_remove), modifier = Modifier.size(18.dp))
        })
    }
}
