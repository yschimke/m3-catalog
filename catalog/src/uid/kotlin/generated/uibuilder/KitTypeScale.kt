package generated.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun KitTypeScale(modifier: androidx.compose.ui.Modifier, label: kotlin.String, style: androidx.compose.ui.text.TextStyle) {
    Box(modifier = modifier) {
        Box(modifier = Modifier.width(400.dp).padding(vertical = 8.dp)) {
            Text(text = label, style = style)
        }
    }
}
