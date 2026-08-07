@file:CatalogGroup(name = "Navigation rail", section = "Navigation")
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
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

private val railDestinations: List<Pair<String, ImageVector>> =
  listOf("Home" to Icons.Filled.Home, "Search" to Icons.Filled.Search, "You" to Icons.Filled.Person)

@CatalogComponent(
  id = "NavigationRail/Standard",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58016:36879",
  caption = "Top-level destinations along the side, medium and expanded widths.",
)
@CatalogModes
@Composable
fun NavigationRailSticker() = Sticker {
  val (selected, select) = selectable(0)
  NavigationRail(Modifier.height(280.dp)) {
    railDestinations.forEachIndexed { index, (label, icon) ->
      NavigationRailItem(
        selected = index == selected,
        onClick = { select(index) },
        icon = { Icon(icon, contentDescription = null) },
        label = { Text(label) },
      )
    }
  }
}
