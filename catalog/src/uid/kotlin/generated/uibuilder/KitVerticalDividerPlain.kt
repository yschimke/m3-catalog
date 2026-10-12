package generated.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun KitVerticalDividerPlain(modifier: androidx.compose.ui.Modifier) {
    Box(modifier = modifier) {
        Row(modifier = Modifier.height(120.dp)) {
            VerticalDivider()
        }
    }
}
