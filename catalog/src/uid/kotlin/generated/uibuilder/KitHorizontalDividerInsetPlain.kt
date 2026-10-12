package generated.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun KitHorizontalDividerInsetPlain(modifier: androidx.compose.ui.Modifier) {
    Box(modifier = modifier) {
        Column(modifier = Modifier.width(280.dp)) {
            HorizontalDivider(modifier = Modifier.padding(start = 16.dp))
        }
    }
}
