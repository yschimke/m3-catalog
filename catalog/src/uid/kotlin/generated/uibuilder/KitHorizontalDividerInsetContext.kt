package generated.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun KitHorizontalDividerInsetContext(modifier: androidx.compose.ui.Modifier, beforeText: kotlin.String, afterText: kotlin.String) {
    Box(modifier = modifier) {
        Column(modifier = Modifier.width(280.dp)) {
            Text(text = beforeText, modifier = Modifier.padding(vertical = 8.dp))
            HorizontalDivider(modifier = Modifier.padding(start = 16.dp))
            Text(text = afterText, modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}
