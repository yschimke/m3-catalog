package generated.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.action_more
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun KitListItemNoLeadingIcon(modifier: androidx.compose.ui.Modifier, headline: kotlin.String, supportingText: kotlin.String) {
    Box(modifier = modifier) {
        Column(modifier = Modifier.width(340.dp)) {
            ListItem(headlineContent = {
                Text(text = headline)
            }, supportingContent = {
                Text(text = supportingText)
            }, leadingContent = null, trailingContent = {
                Icon(imageVector = Icons.Filled.MoreVert, contentDescription = stringResource(Res.string.action_more))
            })
        }
    }
}
