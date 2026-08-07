@file:CatalogGroup(name = "Dialogs", section = "Containment")
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.counted

// A dialog is hosted in its own platform window, which a single-surface capture cannot reach — so
// the sticker composes the dialog's **container** (`AlertDialogDefaults`' own shape, tonal
// elevation and colours) rather than an `AlertDialog` whose window would render off-frame. The
// pixels are the component; only the scrim and the window are absent.

@CatalogComponent(id = "Dialog/Basic", caption = "Icon, headline, supporting text and two actions.")
@CatalogModes
@Composable
fun BasicDialog() = Sticker {
  val confirm = counted("Delete")
  val dismiss = counted("Cancel")
  Surface(
    modifier = Modifier.width(312.dp),
    shape = AlertDialogDefaults.shape,
    color = AlertDialogDefaults.containerColor,
    tonalElevation = AlertDialogDefaults.TonalElevation,
  ) {
    Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
      Icon(
        Icons.Filled.Delete,
        contentDescription = null,
        tint = AlertDialogDefaults.iconContentColor,
        modifier = Modifier.align(Alignment.CenterHorizontally),
      )
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
      Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        TextButton(onClick = dismiss.onClick) { Text(dismiss.label) }
        TextButton(onClick = confirm.onClick) { Text(confirm.label) }
      }
    }
  }
}
