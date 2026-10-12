package generated.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import ee.schimke.m3catalog.uid.DescribedStarsIcon

@Composable
internal fun KitFabSecondary(modifier: androidx.compose.ui.Modifier, label: kotlin.String, onFabClick: () -> kotlin.Unit) {
    Box(modifier = modifier) {
        FloatingActionButton(onClick = onFabClick, containerColor = MaterialTheme.colorScheme.secondary) {
            DescribedStarsIcon(contentDescription = label)
        }
    }
}
