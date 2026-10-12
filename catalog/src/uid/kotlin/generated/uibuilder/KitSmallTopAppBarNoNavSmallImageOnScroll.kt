package generated.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.m3catalog.uid.ImagePlaceholder

@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun KitSmallTopAppBarNoNavSmallImageOnScroll(modifier: androidx.compose.ui.Modifier, search: kotlin.String, onSearchClick: () -> kotlin.Unit) {
    Box(modifier = modifier) {
        TopAppBar(title = {
            ImagePlaceholder(modifier = Modifier.fillMaxWidth().height(40.dp), cornerRadiusDp = 0.0f, scaleBasis = 122.0f)
        }, modifier = Modifier.width(412.dp).height(64.dp), navigationIcon = {

        }, actions = {
            IconButton(onClick = onSearchClick) {
                Icon(imageVector = Icons.Filled.Search, contentDescription = search)
            }
        }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceContainer))
    }
}
