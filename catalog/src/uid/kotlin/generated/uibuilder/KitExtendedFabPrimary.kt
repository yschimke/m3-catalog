package generated.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import ee.schimke.m3catalog.uid.StarsIcon

@Composable
internal fun KitExtendedFabPrimary(modifier: androidx.compose.ui.Modifier, label: kotlin.String, onFabClick: () -> kotlin.Unit) {
    Box(modifier = modifier) {
        ExtendedFloatingActionButton(text = {
            Text(text = label)
        }, icon = {
            StarsIcon()
        }, onClick = onFabClick, containerColor = MaterialTheme.colorScheme.primary)
    }
}
