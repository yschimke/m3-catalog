package generated.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun KitVerticalDivider(modifier: androidx.compose.ui.Modifier, beforeText: kotlin.String, afterText: kotlin.String) {
    Box(modifier = modifier) {
        Row(modifier = Modifier.height(120.dp)) {
            Text(text = beforeText, modifier = Modifier.padding(horizontal = 8.dp))
            VerticalDivider()
            Text(text = afterText, modifier = Modifier.padding(horizontal = 8.dp))
        }
    }
}
