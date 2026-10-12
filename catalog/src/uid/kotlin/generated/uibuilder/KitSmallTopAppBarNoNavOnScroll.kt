package generated.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun KitSmallTopAppBarNoNavOnScroll(modifier: androidx.compose.ui.Modifier, title: kotlin.String, search: kotlin.String, onSearchClick: () -> kotlin.Unit) {
    Box(modifier = modifier) {
        TopAppBar(title = {
            Text(text = title)
        }, modifier = Modifier.width(412.dp).height(64.dp), navigationIcon = {

        }, actions = {
            IconButton(onClick = onSearchClick) {
                Icon(imageVector = Icons.Filled.Search, contentDescription = search)
            }
        }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceContainer))
    }
}
