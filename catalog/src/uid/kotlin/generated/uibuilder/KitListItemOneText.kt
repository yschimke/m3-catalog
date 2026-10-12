package generated.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun KitListItemOneText(modifier: androidx.compose.ui.Modifier, headline: kotlin.String, trailingText: kotlin.String) {
    Box(modifier = modifier) {
        Column(modifier = Modifier.width(340.dp)) {
            ListItem(headlineContent = {
                Text(text = headline)
            }, supportingContent = null, leadingContent = {
                Icon(imageVector = Icons.Filled.Person, contentDescription = null)
            }, trailingContent = {
                Text(text = trailingText)
            })
        }
    }
}
