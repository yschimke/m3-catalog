package generated.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.m3catalog.uid.StarsIcon

@Composable
internal fun KitFilledButtonM(modifier: androidx.compose.ui.Modifier, enabled: kotlin.Boolean, label: kotlin.String, onButtonClick: () -> kotlin.Unit) {
    Box(modifier = modifier) {
        Box(modifier = Modifier.height(56.dp), contentAlignment = Alignment.Center) {
            Button(onClick = onButtonClick, modifier = Modifier.height(56.dp), enabled = enabled, shape = CircleShape, contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)) {
                StarsIcon(modifier = Modifier.padding(end = 8.dp).size(24.dp))
                Text(text = label, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
