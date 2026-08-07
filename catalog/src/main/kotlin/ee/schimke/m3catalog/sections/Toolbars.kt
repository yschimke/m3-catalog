@file:CatalogGroup(name = "Toolbars", section = "Actions")
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.counted

// The expressive floating toolbar: a small cluster of related actions that floats over content.

@CatalogComponent(
  id = "Toolbar/HorizontalFloating",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58467:8206",
  caption = "A floating cluster of related actions over the content.",
)
@CatalogModes
@Composable
fun HorizontalFloatingToolbarSticker() = Sticker {
  val add = counted("Add")
  val edit = counted("Edit")
  val more = counted("More")
  HorizontalFloatingToolbar(expanded = true) {
    IconButton(onClick = add.onClick) { Icon(Icons.Filled.Add, contentDescription = "Add") }
    IconButton(onClick = edit.onClick) { Icon(Icons.Filled.Edit, contentDescription = "Edit") }
    IconButton(onClick = more.onClick) { Icon(Icons.Filled.MoreVert, contentDescription = "More") }
  }
}
