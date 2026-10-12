package generated.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ShortNavigationBar
import androidx.compose.material3.ShortNavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.m3catalog.uid.SelectionStarsIcon

@Composable
internal fun KitShortNavigationBarThree(modifier: androidx.compose.ui.Modifier, selected0: kotlin.Boolean, label0: kotlin.String, selected1: kotlin.Boolean, label1: kotlin.String, selected2: kotlin.Boolean, label2: kotlin.String, onItem0Click: () -> kotlin.Unit, onItem1Click: () -> kotlin.Unit, onItem2Click: () -> kotlin.Unit) {
    Box(modifier = modifier) {
        ShortNavigationBar(modifier = Modifier.width(412.dp).height(64.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                ShortNavigationBarItem(selected = selected0, onClick = onItem0Click, icon = {
                    SelectionStarsIcon(selected = selected0)
                }, label = {
                    Text(text = label0)
                }, modifier = Modifier.weight(1f))
                ShortNavigationBarItem(selected = selected1, onClick = onItem1Click, icon = {
                    SelectionStarsIcon(selected = selected1)
                }, label = {
                    Text(text = label1)
                }, modifier = Modifier.weight(1f))
                ShortNavigationBarItem(selected = selected2, onClick = onItem2Click, icon = {
                    SelectionStarsIcon(selected = selected2)
                }, label = {
                    Text(text = label2)
                }, modifier = Modifier.weight(1f))
            }
        }
    }
}
