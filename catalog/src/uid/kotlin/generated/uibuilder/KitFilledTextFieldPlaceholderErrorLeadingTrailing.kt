package generated.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.action_clear
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun KitFilledTextFieldPlaceholderErrorLeadingTrailing(modifier: androidx.compose.ui.Modifier, value: kotlin.String, enabled: kotlin.Boolean, placeholder: kotlin.String, supportingText: kotlin.String) {
    Box(modifier = modifier) {
        TextField(value = value, onValueChange = {}, modifier = Modifier.width(210.dp), enabled = enabled, placeholder = {
            Text(text = placeholder)
        }, leadingIcon = {
            Icon(imageVector = Icons.Filled.Search, contentDescription = null)
        }, trailingIcon = {
            Icon(imageVector = Icons.Filled.Cancel, contentDescription = stringResource(Res.string.action_clear))
        }, supportingText = {
            Text(text = supportingText)
        }, isError = true)
    }
}
