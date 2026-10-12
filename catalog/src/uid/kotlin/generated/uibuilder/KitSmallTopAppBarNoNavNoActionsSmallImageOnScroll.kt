package generated.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.m3catalog.uid.ImagePlaceholder

@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun KitSmallTopAppBarNoNavNoActionsSmallImageOnScroll(modifier: androidx.compose.ui.Modifier) {
    Box(modifier = modifier) {
        TopAppBar(title = {
            ImagePlaceholder(modifier = Modifier.fillMaxWidth().height(40.dp), cornerRadiusDp = 0.0f, scaleBasis = 122.0f)
        }, modifier = Modifier.width(412.dp).height(64.dp), navigationIcon = {

        }, actions = {

        }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceContainer))
    }
}
