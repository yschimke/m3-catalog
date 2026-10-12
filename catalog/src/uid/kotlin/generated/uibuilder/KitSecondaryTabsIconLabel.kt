package generated.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun KitSecondaryTabsIconLabel(modifier: androidx.compose.ui.Modifier, text0: kotlin.String, text1: kotlin.String, text2: kotlin.String, onTab0Click: () -> kotlin.Unit, onTab1Click: () -> kotlin.Unit, onTab2Click: () -> kotlin.Unit) {
    Box(modifier = modifier) {
        SecondaryTabRow(selectedTabIndex = 1, modifier = Modifier.width(360.dp), tabs = {
            Tab(selected = false, onClick = onTab0Click, text = {
                Text(text = text0)
            }, icon = {
                Icon(imageVector = Icons.Filled.Home, contentDescription = null)
            })
            Tab(selected = true, onClick = onTab1Click, text = {
                Text(text = text1)
            }, icon = {
                Icon(imageVector = Icons.Filled.Search, contentDescription = null)
            })
            Tab(selected = false, onClick = onTab2Click, text = {
                Text(text = text2)
            }, icon = {
                Icon(imageVector = Icons.Filled.Person, contentDescription = null)
            })
        })
    }
}
