package generated.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun KitListItemThreeNoLeading(modifier: androidx.compose.ui.Modifier, headline: kotlin.String, supportingText: kotlin.String, supportingText2: kotlin.String, overline: kotlin.String) {
    Box(modifier = modifier) {
        Column(modifier = Modifier.width(340.dp)) {
            ListItem(headlineContent = {
                Text(text = headline)
            }, overlineContent = {
                Text(text = overline)
            }, supportingContent = {
                Text(text = supportingText)
                Text(text = supportingText2)
            }, leadingContent = null)
        }
    }
}
