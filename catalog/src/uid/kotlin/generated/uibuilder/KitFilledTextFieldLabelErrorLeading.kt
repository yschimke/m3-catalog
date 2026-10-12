package generated.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun KitFilledTextFieldLabelErrorLeading(modifier: androidx.compose.ui.Modifier, value: kotlin.String, enabled: kotlin.Boolean, labelText: kotlin.String, supportingText: kotlin.String) {
    Box(modifier = modifier) {
        TextField(value = value, onValueChange = {}, modifier = Modifier.width(210.dp), enabled = enabled, label = {
            Text(text = labelText)
        }, leadingIcon = {
            Icon(imageVector = Icons.Filled.Search, contentDescription = null)
        }, supportingText = {
            Text(text = supportingText)
        }, isError = true)
    }
}
