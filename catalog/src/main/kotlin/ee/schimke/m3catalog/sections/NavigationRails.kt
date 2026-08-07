@file:CatalogGroup(name = "Navigation rail", section = "Navigation")
@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.material3.WideNavigationRail
import androidx.compose.material3.WideNavigationRailItem
import androidx.compose.material3.WideNavigationRailValue
import androidx.compose.material3.rememberWideNavigationRailState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.overrides.previewOverrideBoolean
import ee.schimke.composeai.overrides.previewOverrideString
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.selectable

// The kit's axes: destination count, the optional menu and FAB header slots, and label visibility.
// The expanded form is a separate composable (WideNavigationRail), so it is its own component.

private val RAIL =
  listOf(
    "Home" to Icons.Filled.Home,
    "Search" to Icons.Filled.Search,
    "You" to Icons.Filled.Person,
    "Saved" to Icons.Filled.Favorite,
  )

@Composable
private fun railHeader(): (@Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit)? {
  val menu = previewOverrideBoolean("menu", false)
  val fab = previewOverrideBoolean("fab", false)
  if (!menu && !fab) return null
  return {
    if (menu) {
      IconButton(onClick = {}) { Icon(Icons.Filled.Menu, contentDescription = "Menu") }
    }
    if (fab) {
      FloatingActionButton(onClick = {}) { Icon(Icons.Filled.Add, contentDescription = "New") }
    }
  }
}

@CatalogComponent(
  id = "NavigationRail/Standard",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58016:36879",
  caption = "Destinations along the side. Count, menu, FAB and labels fold in.",
)
@CatalogModes
@OverrideVariant(name = "four", strings = ["count=4"])
@OverrideVariant(name = "menu", booleans = ["menu=true"])
@OverrideVariant(name = "fab", booleans = ["fab=true"])
@OverrideVariant(name = "menu-fab", booleans = ["menu=true", "fab=true"])
@OverrideVariant(name = "labels-none", strings = ["labels=none"])
@Composable
fun NavigationRailSticker() = Sticker {
  val count = previewOverrideString("count", "3").toIntOrNull() ?: 3
  val labels = previewOverrideString("labels", "always")
  val (selected, select) = selectable(0)
  NavigationRail(modifier = Modifier.height(320.dp), header = railHeader()) {
    RAIL.take(count).forEachIndexed { index, (label, icon) ->
      NavigationRailItem(
        selected = index == selected,
        onClick = { select(index) },
        icon = { Icon(icon, contentDescription = null) },
        label = if (labels == "none") null else ({ Text(label) }),
      )
    }
  }
}

@CatalogComponent(
  id = "NavigationRail/Wide",
  caption = "The expanded form, labels beside the icons. Count folds in.",
)
@CatalogModes
@OverrideVariant(name = "four", strings = ["count=4"])
@Composable
fun WideNavigationRailSticker() = Sticker {
  val count = previewOverrideString("count", "3").toIntOrNull() ?: 3
  val (selected, select) = selectable(0)
  // `WideNavigationRail` takes a state object, not an `expanded` flag — the expansion is animated
  // and the rail owns it. Seeded Expanded so the baked capture shows the form this component is
  // for; the collapsed rail is `NavigationRail/Standard` above.
  WideNavigationRail(
    modifier = Modifier.height(320.dp),
    state = rememberWideNavigationRailState(WideNavigationRailValue.Expanded),
  ) {
    RAIL.take(count).forEachIndexed { index, (label, icon) ->
      WideNavigationRailItem(
        railExpanded = true,
        selected = index == selected,
        onClick = { select(index) },
        icon = { Icon(icon, contentDescription = null) },
        label = { Text(label) },
      )
    }
  }
}
