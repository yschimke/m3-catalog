package generated.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun KitSmallTopAppBarNoActions(modifier: androidx.compose.ui.Modifier, title: kotlin.String, back: kotlin.String, onBackClick: () -> kotlin.Unit) {
    Box(modifier = modifier) {
        TopAppBar(title = {
            Text(text = title)
        }, modifier = Modifier.width(412.dp).height(64.dp), navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = back)
            }
        }, actions = {

        }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0x00000000)))
    }
}
