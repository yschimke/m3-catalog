package generated.uibuilder

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.label_favourite
import ee.schimke.m3catalog.uid.FavouriteStarsIcon
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun KitOutlinedIconButtonXsUniformSquare(modifier: androidx.compose.ui.Modifier, enabled: kotlin.Boolean, on: kotlin.Boolean, onButtonClick: () -> kotlin.Unit) {
    Box(modifier = modifier) {
        Box(modifier = Modifier.size(40.dp), contentAlignment = Alignment.Center) {
            OutlinedIconButton(onClick = onButtonClick, modifier = Modifier.size(32.dp), enabled = enabled, shape = MaterialTheme.shapes.medium, colors = IconButtonDefaults.outlinedIconButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant), border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
                FavouriteStarsIcon(modifier = Modifier.size(20.dp), selected = on, contentDescription = stringResource(Res.string.label_favourite))
            }
        }
    }
}
