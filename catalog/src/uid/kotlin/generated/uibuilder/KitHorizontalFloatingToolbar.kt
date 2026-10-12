package generated.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.action_add
import ee.schimke.m3catalog.generated.resources.action_edit
import ee.schimke.m3catalog.generated.resources.action_more
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun KitHorizontalFloatingToolbar(modifier: androidx.compose.ui.Modifier, onAddClick: () -> kotlin.Unit, onEditClick: () -> kotlin.Unit, onMoreClick: () -> kotlin.Unit) {
    Box(modifier = modifier) {
        Box(modifier = Modifier.padding(11.dp).width(168.dp), contentAlignment = Alignment.Center) {
            HorizontalFloatingToolbar(expanded = true) {
                IconButton(onClick = onAddClick) {
                    Icon(imageVector = Icons.Filled.Share, contentDescription = stringResource(Res.string.action_add), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(onClick = onEditClick) {
                    Icon(imageVector = Icons.Filled.Comment, contentDescription = stringResource(Res.string.action_edit), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(onClick = onMoreClick) {
                    Icon(imageVector = Icons.Filled.Download, contentDescription = stringResource(Res.string.action_more), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
