package generated.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun KitSecondaryTabsSelected0(modifier: androidx.compose.ui.Modifier, text0: kotlin.String, text1: kotlin.String, text2: kotlin.String, onTab0Click: () -> kotlin.Unit, onTab1Click: () -> kotlin.Unit, onTab2Click: () -> kotlin.Unit) {
    Box(modifier = modifier) {
        SecondaryTabRow(selectedTabIndex = 0, modifier = Modifier.width(360.dp), tabs = {
            Tab(selected = true, onClick = onTab0Click, text = {
                Text(text = text0)
            })
            Tab(selected = false, onClick = onTab1Click, text = {
                Text(text = text1)
            })
            Tab(selected = false, onClick = onTab2Click, text = {
                Text(text = text2)
            })
        })
    }
}
