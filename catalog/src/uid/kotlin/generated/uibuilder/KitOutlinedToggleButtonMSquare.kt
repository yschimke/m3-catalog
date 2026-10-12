package generated.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedToggleButton
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.m3catalog.uid.SelectionStarsIcon

@kotlin.OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun KitOutlinedToggleButtonMSquare(modifier: androidx.compose.ui.Modifier, checked: kotlin.Boolean, enabled: kotlin.Boolean, label: kotlin.String, onToggleCheckedChange: () -> kotlin.Unit) {
    Box(modifier = modifier) {
        Box(modifier = Modifier.height(56.dp), contentAlignment = Alignment.Center) {
            OutlinedToggleButton(checked = checked, onCheckedChange = { _ -> onToggleCheckedChange() }, modifier = Modifier.height(56.dp), enabled = enabled, shapes = ToggleButtonDefaults.shapes(shape = ToggleButtonDefaults.mediumSquareShape, pressedShape = ToggleButtonDefaults.mediumPressedShape, checkedShape = ToggleButtonDefaults.roundShape), contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)) {
                SelectionStarsIcon(selected = checked, modifier = Modifier.padding(end = 8.dp).size(24.dp))
                Text(text = label, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
