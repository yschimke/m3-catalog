@file:CatalogGroup(name = "Dialogs", section = "Containment")

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import ee.schimke.composeai.overrides.previewOverrideBoolean
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
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

// A dialog is hosted in its own platform window, which a single-surface capture cannot reach — so
// these compose the dialog's CONTAINER (AlertDialogDefaults' own shape, tonal elevation and
// colours) rather than an `AlertDialog` whose window would render off-frame. The pixels are the
// component; only the scrim and the window are absent.
//
// The kit's axes: the optional ICON, and the body layout (supporting text vs a choice list).

@Composable
private fun DialogSurface(content: @Composable () -> Unit) {
  Surface(
    modifier = Modifier.width(312.dp),
    shape = AlertDialogDefaults.shape,
    color = AlertDialogDefaults.containerColor,
    tonalElevation = AlertDialogDefaults.TonalElevation,
  ) {
    content()
  }
}

@Composable
private fun DialogActions(confirm: String, dismiss: String) {
  val c = counted(confirm)
  val d = counted(dismiss)
  Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
    TextButton(onClick = d.onClick) { Text(d.label) }
    TextButton(onClick = c.onClick) { Text(c.label) }
  }
}

@CatalogComponent(
  id = "Dialog/Basic",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/50723:10948",
  caption = "Headline, supporting text and two actions. The hero icon folds in.",
)
@CatalogModes
@OverrideVariant(name = "icon", booleans = ["icon=true"])
@Composable
fun BasicDialog() = Sticker {
  DialogSurface {
    Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
      if (previewOverrideBoolean("icon", false)) {
        Icon(
          Icons.Filled.Delete,
          contentDescription = null,
          tint = AlertDialogDefaults.iconContentColor,
          modifier = Modifier.align(Alignment.CenterHorizontally),
        )
      }
      Text(
        stringResource(Res.string.dialog_delete_title),
        style = MaterialTheme.typography.headlineSmall,
        color = AlertDialogDefaults.titleContentColor,
      )
      Text(
        stringResource(Res.string.dialog_delete_body),
        style = MaterialTheme.typography.bodyMedium,
        color = AlertDialogDefaults.textContentColor,
      )
      DialogActions(
        stringResource(Res.string.dialog_action_1),
        stringResource(Res.string.dialog_action_2),
      )
    }
  }
}

@CatalogComponent(
  id = "Dialog/List",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/50723:10964",
  caption = "A choice list in place of supporting text.",
)
@CatalogModes
@Composable
fun ListDialog() = Sticker {
  Surface(
    modifier = Modifier.width(312.dp).height(434.dp),
    shape = AlertDialogDefaults.shape,
    color = AlertDialogDefaults.containerColor,
    tonalElevation = AlertDialogDefaults.TonalElevation,
  ) {
    Column {
      Text(
        stringResource(Res.string.dialog_backup_title),
        Modifier.padding(start = 24.dp, top = 24.dp, end = 24.dp),
        style = MaterialTheme.typography.headlineSmall,
        color = AlertDialogDefaults.titleContentColor,
      )
      Spacer(Modifier.height(16.dp))
      Text(
        stringResource(Res.string.dialog_delete_body),
        Modifier.padding(horizontal = 24.dp),
        style = MaterialTheme.typography.bodyMedium,
        color = AlertDialogDefaults.textContentColor,
      )
      Spacer(Modifier.height(27.dp))
      repeat(3) { i ->
        var checked by toggleable(true)
        Row(
          modifier = Modifier.fillMaxWidth().height(57.dp).padding(horizontal = 16.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
          ) {
            Box(contentAlignment = Alignment.Center) { Text("A") }
          }
          Spacer(Modifier.width(16.dp))
          Text(stringResource(Res.string.dialog_add_account))
          Spacer(Modifier.weight(1f))
          Text(localizedDigits("100+"), style = MaterialTheme.typography.labelSmall)
          Checkbox(checked = checked, onCheckedChange = { checked = it })
        }
        if (i < 2) {
          HorizontalDivider(Modifier.padding(horizontal = 16.dp))
        }
      }
      Spacer(Modifier.height(15.dp))
      Row(Modifier.padding(horizontal = 24.dp).height(48.dp)) {
        DialogActions(
          stringResource(Res.string.dialog_action_1),
          stringResource(Res.string.dialog_action_2),
        )
      }
    }
  }
}
