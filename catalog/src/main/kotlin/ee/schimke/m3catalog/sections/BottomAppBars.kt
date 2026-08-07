@file:CatalogGroup(name = "Bottom app bar", section = "Navigation")
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.counted

@CatalogComponent(
  id = "BottomAppBar/Standard",
  caption = "Screen-level actions along the bottom, with the primary action as a FAB.",
)
@CatalogModes
@Composable
fun BottomAppBarSticker() = Sticker {
  val check = counted("Check")
  val edit = counted("Edit")
  val more = counted("More")
  val add = counted("Add")
  BottomAppBar(
    modifier = Modifier.width(360.dp),
    actions = {
      IconButton(onClick = check.onClick) {
        Icon(Icons.Filled.Check, contentDescription = check.label)
      }
      IconButton(onClick = edit.onClick) {
        Icon(Icons.Filled.Edit, contentDescription = edit.label)
      }
      IconButton(onClick = more.onClick) {
        Icon(Icons.Filled.MoreVert, contentDescription = more.label)
      }
    },
    floatingActionButton = {
      FloatingActionButton(onClick = add.onClick) {
        Icon(Icons.Filled.Add, contentDescription = add.label)
      }
    },
  )
}
