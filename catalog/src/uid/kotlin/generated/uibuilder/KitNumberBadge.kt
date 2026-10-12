package generated.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Badge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
internal fun KitNumberBadge(modifier: androidx.compose.ui.Modifier, label: kotlin.String) {
    Box(modifier = modifier) {
        Badge {
            Text(text = label)
        }
    }
}
