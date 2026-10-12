package generated.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable

@Composable
internal fun KitCircularProgress(modifier: androidx.compose.ui.Modifier, progress: kotlin.Float) {
    Box(modifier = modifier) {
        CircularProgressIndicator(progress = { progress })
    }
}
