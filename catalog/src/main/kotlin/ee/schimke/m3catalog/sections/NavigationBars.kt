@file:CatalogGroup(name = "Navigation bar", section = "Navigation")
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
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

// The bar owns its selected destination, so a click on the live lane really moves the indicator.

private val destinations: List<Pair<String, ImageVector>> =
  listOf("Home" to Icons.Filled.Home, "Search" to Icons.Filled.Search, "You" to Icons.Filled.Person)

@CatalogComponent(
  id = "NavigationBar/Standard",
  caption = "Three to five top-level destinations, compact widths.",
)
@CatalogModes
@Composable
fun NavigationBarSticker() = Sticker {
  val (selected, select) = selectable(0)
  NavigationBar(Modifier.width(360.dp)) {
    destinations.forEachIndexed { index, (label, icon) ->
      NavigationBarItem(
        selected = index == selected,
        onClick = { select(index) },
        icon = { Icon(icon, contentDescription = null) },
        label = { Text(label) },
      )
    }
  }
}
