package generated.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.m3catalog.uid.StarsIcon

@Composable
internal fun KitTonalButtonLSquare(modifier: androidx.compose.ui.Modifier, enabled: kotlin.Boolean, label: kotlin.String, onButtonClick: () -> kotlin.Unit) {
    Box(modifier = modifier) {
        Box(modifier = Modifier.height(96.dp), contentAlignment = Alignment.Center) {
            FilledTonalButton(onClick = onButtonClick, modifier = Modifier.height(96.dp), enabled = enabled, shape = MaterialTheme.shapes.medium, contentPadding = PaddingValues(horizontal = 48.dp, vertical = 32.dp)) {
                StarsIcon(modifier = Modifier.padding(end = 12.dp).size(32.dp))
                Text(text = label, style = MaterialTheme.typography.headlineSmall)
            }
        }
    }
}
