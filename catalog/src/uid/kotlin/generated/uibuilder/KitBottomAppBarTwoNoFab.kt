package generated.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun KitBottomAppBarTwoNoFab(modifier: androidx.compose.ui.Modifier, check: kotlin.String, edit: kotlin.String, onCheckClick: () -> kotlin.Unit, onEditClick: () -> kotlin.Unit) {
    Box(modifier = modifier) {
        BottomAppBar(actions = {
            IconButton(onClick = onCheckClick) {
                Icon(imageVector = Icons.Filled.Search, contentDescription = check)
            }
            IconButton(onClick = onEditClick) {
                Icon(imageVector = Icons.Outlined.Delete, contentDescription = edit)
            }
        }, modifier = Modifier.width(412.dp), floatingActionButton = null)
    }
}
