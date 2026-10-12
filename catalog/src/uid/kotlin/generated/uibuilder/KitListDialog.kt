package generated.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
internal fun KitListDialog(modifier: androidx.compose.ui.Modifier, title: kotlin.String, text: kotlin.String, item: kotlin.String, itemCount: kotlin.String, check1Checked: kotlin.Boolean, check2Checked: kotlin.Boolean, check3Checked: kotlin.Boolean, confirmButton: kotlin.String, dismissButton: kotlin.String, onCheck1CheckedChange: () -> kotlin.Unit, onCheck2CheckedChange: () -> kotlin.Unit, onCheck3CheckedChange: () -> kotlin.Unit, onConfirmClick: () -> kotlin.Unit, onDismissClick: () -> kotlin.Unit) {
    Box(modifier = modifier) {
        AlertDialog(onDismissRequest = {}, confirmButton = {
            TextButton(onClick = onConfirmClick) {
                Text(text = confirmButton)
            }
        }, modifier = Modifier.width(312.dp), dismissButton = {
            TextButton(onClick = onDismissClick) {
                Text(text = dismissButton)
            }
        }, title = {
            Text(text = title)
        }, text = {
            Column {
                Text(text = text)
                ListItem(headlineContent = {
                    Text(text = item)
                }, leadingContent = {
                    Surface(modifier = Modifier.size(40.dp), shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = "A")
                        }
                    }
                }, trailingContent = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = itemCount, style = MaterialTheme.typography.labelSmall)
                        Checkbox(checked = check1Checked, onCheckedChange = { _ -> onCheck1CheckedChange() })
                    }
                }, colors = ListItemDefaults.colors(containerColor = Color(0x00000000)))
                HorizontalDivider()
                ListItem(headlineContent = {
                    Text(text = item)
                }, leadingContent = {
                    Surface(modifier = Modifier.size(40.dp), shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = "A")
                        }
                    }
                }, trailingContent = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = itemCount, style = MaterialTheme.typography.labelSmall)
                        Checkbox(checked = check2Checked, onCheckedChange = { _ -> onCheck2CheckedChange() })
                    }
                }, colors = ListItemDefaults.colors(containerColor = Color(0x00000000)))
                HorizontalDivider()
                ListItem(headlineContent = {
                    Text(text = item)
                }, leadingContent = {
                    Surface(modifier = Modifier.size(40.dp), shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = "A")
                        }
                    }
                }, trailingContent = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = itemCount, style = MaterialTheme.typography.labelSmall)
                        Checkbox(checked = check3Checked, onCheckedChange = { _ -> onCheck3CheckedChange() })
                    }
                }, colors = ListItemDefaults.colors(containerColor = Color(0x00000000)))
            }
        })
    }
}
