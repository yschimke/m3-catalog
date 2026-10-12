package generated.uibuilder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
internal fun KitCornerScaleLargeIncreased(modifier: androidx.compose.ui.Modifier, label: kotlin.String) {
    Box(modifier = modifier) {
        Column(modifier = Modifier.size(width = 220.dp, height = 176.dp), verticalArrangement = Arrangement.spacedBy(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(136.dp).clip(RoundedCornerShape(20.dp)).background(color = MaterialTheme.colorScheme.primary), content = {})
            Text(text = label, style = MaterialTheme.typography.labelLarge)
        }
    }
}
