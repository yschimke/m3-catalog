@file:CatalogGroup(name = "Navigation drawer", section = "Navigation")
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.selectable

// The drawer *sheet* is the component; `ModalNavigationDrawer` is the host that slides it in, and
// a sticker of the host at rest would be an empty screen. The sheet is composed directly.

private val drawerDestinations: List<Pair<String, ImageVector>> =
  listOf("Home" to Icons.Filled.Home, "Search" to Icons.Filled.Search, "You" to Icons.Filled.Person)

@CatalogComponent(
  id = "NavigationDrawer/Modal",
  caption = "Top-level destinations in a sheet that slides over the content.",
)
@CatalogModes
@Composable
fun ModalDrawerSheetSticker() = Sticker {
  val (selected, select) = selectable(0)
  ModalDrawerSheet {
    Text("Mail", Modifier.padding(16.dp))
    drawerDestinations.forEachIndexed { index, (label, icon) ->
      NavigationDrawerItem(
        label = { Text(label) },
        icon = { Icon(icon, contentDescription = null) },
        selected = index == selected,
        onClick = { select(index) },
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
      )
    }
  }
}
