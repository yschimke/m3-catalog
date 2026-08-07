@file:CatalogGroup(name = "Menus", section = "Selection")
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.counted

// `DropdownMenu` renders into a popup window a single-surface capture cannot reach. Its **items**
// are plain composables, so the sticker composes them in the menu container the component uses —
// the real `DropdownMenuItem` rows, in the real surface shape and colour.

@CatalogComponent(id = "Menu/Dropdown", caption = "A list of choices on a temporary surface.")
@CatalogModes
@Composable
fun DropdownMenuSticker() = Sticker {
  val edit = counted("Edit")
  val share = counted("Share")
  val delete = counted("Delete")
  Surface(
    modifier = Modifier.width(200.dp),
    shape = MaterialTheme.shapes.extraSmall,
    color = MaterialTheme.colorScheme.surfaceContainer,
    tonalElevation = 3.dp,
    shadowElevation = 3.dp,
  ) {
    Column {
      DropdownMenuItem(
        text = { Text(edit.label) },
        onClick = edit.onClick,
        leadingIcon = { Icon(Icons.Filled.Edit, contentDescription = null) },
      )
      DropdownMenuItem(
        text = { Text(share.label) },
        onClick = share.onClick,
        leadingIcon = { Icon(Icons.Filled.Share, contentDescription = null) },
      )
      DropdownMenuItem(
        text = { Text(delete.label) },
        onClick = delete.onClick,
        leadingIcon = { Icon(Icons.Filled.Delete, contentDescription = null) },
      )
    }
  }
}
