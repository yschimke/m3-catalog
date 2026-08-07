@file:CatalogGroup(name = "Dialogs", section = "Containment")

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import ee.schimke.m3catalog.selectable

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
        "Delete this item?",
        style = MaterialTheme.typography.headlineSmall,
        color = AlertDialogDefaults.titleContentColor,
      )
      Text(
        "This can't be undone. The item will be removed from every device.",
        style = MaterialTheme.typography.bodyMedium,
        color = AlertDialogDefaults.textContentColor,
      )
      DialogActions("Delete", "Cancel")
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
  val (selected, select) = selectable(0)
  DialogSurface {
    Column(Modifier.padding(vertical = 24.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Text(
        "Choose a backup account",
        Modifier.padding(horizontal = 24.dp),
        style = MaterialTheme.typography.headlineSmall,
        color = AlertDialogDefaults.titleContentColor,
      )
      listOf("alice@example.com", "bala@example.com", "Add account").forEachIndexed { i, label ->
        ListItem(
          headlineContent = { Text(label) },
          leadingContent = { RadioButton(selected = i == selected, onClick = { select(i) }) },
        )
      }
      Row(Modifier.padding(horizontal = 24.dp)) { DialogActions("OK", "Cancel") }
    }
  }
}
