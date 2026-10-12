package generated.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun KitBottomAppBarOne(modifier: androidx.compose.ui.Modifier, check: kotlin.String, add: kotlin.String, onCheckClick: () -> kotlin.Unit, onFabClick: () -> kotlin.Unit) {
    Box(modifier = modifier) {
        BottomAppBar(actions = {
            IconButton(onClick = onCheckClick) {
                Icon(imageVector = Icons.Filled.Search, contentDescription = check)
            }
        }, modifier = Modifier.width(412.dp), floatingActionButton = {
            FloatingActionButton(onClick = onFabClick) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = add)
            }
        })
    }
}
