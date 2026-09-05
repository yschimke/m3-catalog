@file:CatalogGroup(name = "Dialogs", section = "Containment")

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogFilledStars
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.InlineDialogHost
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogText
import ee.schimke.m3catalog.counted
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.dialog_action_1
import ee.schimke.m3catalog.generated.resources.dialog_action_2
import ee.schimke.m3catalog.generated.resources.dialog_add_account
import ee.schimke.m3catalog.generated.resources.dialog_backup_title
import ee.schimke.m3catalog.generated.resources.dialog_delete_body
import ee.schimke.m3catalog.generated.resources.dialog_delete_title
import ee.schimke.m3catalog.localizedDigits
import ee.schimke.m3catalog.toggleable
import org.jetbrains.compose.resources.stringResource

// A dialog is hosted in its own platform window, which a single-surface capture cannot reach.
// InlineDialogHost replaces only that window; AlertDialog still owns the complete Material layout.
//
// The kit's axes: the optional ICON, and the body layout (supporting text vs a choice list).

@CatalogComponent(
  id = "Dialog/Basic",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/50723:10948",
  caption = "Headline, supporting text and two actions. The hero icon folds in.",
)
@CatalogModes
@OverrideVariant(name = "icon", booleans = ["icon=true"])
@Composable
fun BasicDialog(icon: Boolean = false) = Sticker {
  val confirm = counted(catalogText("confirmButton", stringResource(Res.string.dialog_action_1)))
  val dismiss = counted(catalogText("dismissButton", stringResource(Res.string.dialog_action_2)))
  val title = catalogText("title", stringResource(Res.string.dialog_delete_title))
  val text = catalogText("text", stringResource(Res.string.dialog_delete_body))
  val showIcon = icon
  InlineDialogHost {
    AlertDialog(
      onDismissRequest = {},
      modifier = Modifier.width(312.dp),
      icon =
        if (showIcon) {
          {
            Icon(
              Icons.Filled.Delete,
              contentDescription = null,
              tint = AlertDialogDefaults.iconContentColor,
            )
          }
        } else null,
      title = { Text(title) },
      text = { Text(text) },
      confirmButton = { TextButton(onClick = confirm.onClick) { Text(confirm.label) } },
      dismissButton = { TextButton(onClick = dismiss.onClick) { Text(dismiss.label) } },
    )
  }
}

@CatalogComponent(
  id = "Dialog/List",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/50723:10964",
  caption = "A choice list in place of supporting text.",
)
@CatalogModes
@OverrideVariant(name = "icon", booleans = ["icon=true"])
@Composable
fun ListDialog(icon: Boolean = false) = Sticker {
  val showIcon = icon
  val confirm = counted(catalogText("confirmButton", stringResource(Res.string.dialog_action_1)))
  val dismiss = counted(catalogText("dismissButton", stringResource(Res.string.dialog_action_2)))
  val title = catalogText("title", stringResource(Res.string.dialog_backup_title))
  val text = catalogText("text", stringResource(Res.string.dialog_delete_body))
  val item = catalogText("item", stringResource(Res.string.dialog_add_account))
  InlineDialogHost {
    AlertDialog(
      onDismissRequest = {},
      modifier = Modifier.width(312.dp),
      icon =
        if (showIcon) {
          {
            Icon(
              CatalogFilledStars,
              contentDescription = null,
              tint = AlertDialogDefaults.iconContentColor,
            )
          }
        } else null,
      title = { Text(title) },
      text = {
        Column {
          Text(text)
          repeat(3) { index ->
            var checked by toggleable(true)
            ListItem(
              headlineContent = { Text(item) },
              leadingContent = {
                Surface(
                  modifier = Modifier.size(40.dp),
                  shape = CircleShape,
                  color = MaterialTheme.colorScheme.primaryContainer,
                  contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ) {
                  Box(contentAlignment = Alignment.Center) { Text("A") }
                }
              },
              trailingContent = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(localizedDigits("100+"), style = MaterialTheme.typography.labelSmall)
                  Checkbox(checked = checked, onCheckedChange = { checked = it })
                }
              },
            )
            if (index < 2) HorizontalDivider()
          }
        }
      },
      confirmButton = { TextButton(onClick = confirm.onClick) { Text(confirm.label) } },
      dismissButton = { TextButton(onClick = dismiss.onClick) { Text(dismiss.label) } },
    )
  }
}
