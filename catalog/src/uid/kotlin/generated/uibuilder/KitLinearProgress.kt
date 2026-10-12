package generated.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun KitLinearProgress(modifier: androidx.compose.ui.Modifier, progress: kotlin.Float) {
    Box(modifier = modifier) {
        Box(modifier = Modifier.size(width = 405.dp, height = 12.dp), contentAlignment = Alignment.Center) {
            LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
        }
    }
}
