package generated.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.label_favourite
import ee.schimke.m3catalog.uid.FavouriteStarsIcon
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun KitTonalIconButton(modifier: androidx.compose.ui.Modifier, enabled: kotlin.Boolean, on: kotlin.Boolean, onButtonClick: () -> kotlin.Unit) {
    Box(modifier = modifier) {
        Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
            FilledTonalIconButton(onClick = onButtonClick, modifier = Modifier.size(40.dp), enabled = enabled, shape = CircleShape) {
                FavouriteStarsIcon(modifier = Modifier.size(24.dp), selected = on, contentDescription = stringResource(Res.string.label_favourite))
            }
        }
    }
}
