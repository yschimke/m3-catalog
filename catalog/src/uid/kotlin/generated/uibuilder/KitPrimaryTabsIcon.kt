package generated.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun KitPrimaryTabsIcon(modifier: androidx.compose.ui.Modifier, onTab0Click: () -> kotlin.Unit, onTab1Click: () -> kotlin.Unit, onTab2Click: () -> kotlin.Unit) {
    Box(modifier = modifier) {
        PrimaryTabRow(selectedTabIndex = 0, modifier = Modifier.width(360.dp), tabs = {
            Tab(selected = true, onClick = onTab0Click, icon = {
                Icon(imageVector = Icons.Filled.Home, contentDescription = null)
            })
            Tab(selected = false, onClick = onTab1Click, icon = {
                Icon(imageVector = Icons.Filled.Search, contentDescription = null)
            })
            Tab(selected = false, onClick = onTab2Click, icon = {
                Icon(imageVector = Icons.Filled.Person, contentDescription = null)
            })
        })
    }
}
