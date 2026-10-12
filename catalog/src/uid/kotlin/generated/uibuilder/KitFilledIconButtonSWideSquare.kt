package generated.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.label_favourite
import ee.schimke.m3catalog.uid.FavouriteStarsIcon
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun KitFilledIconButtonSWideSquare(modifier: androidx.compose.ui.Modifier, enabled: kotlin.Boolean, on: kotlin.Boolean, onButtonClick: () -> kotlin.Unit) {
    Box(modifier = modifier) {
        Box(modifier = Modifier.size(width = 60.dp, height = 48.dp), contentAlignment = Alignment.Center) {
            FilledIconButton(onClick = onButtonClick, modifier = Modifier.size(width = 52.dp, height = 40.dp), enabled = enabled, shape = MaterialTheme.shapes.medium) {
                FavouriteStarsIcon(modifier = Modifier.size(24.dp), selected = on, contentDescription = stringResource(Res.string.label_favourite))
            }
        }
    }
}
