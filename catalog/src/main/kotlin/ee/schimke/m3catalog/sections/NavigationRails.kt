@file:CatalogGroup(name = "Navigation rail", section = "Navigation")
@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.outlined.Stars
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
import ee.schimke.m3catalog.counted
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.action_menu
import ee.schimke.m3catalog.generated.resources.action_new
import ee.schimke.m3catalog.generated.resources.nav_home
import ee.schimke.m3catalog.generated.resources.nav_saved
import ee.schimke.m3catalog.generated.resources.nav_search
import ee.schimke.m3catalog.generated.resources.nav_you
import ee.schimke.m3catalog.selectable
import org.jetbrains.compose.resources.stringResource

// The kit's axes: destination count, the optional menu and FAB header slots, and label visibility.
// The expanded form is a separate composable (WideNavigationRail), so it is its own component.

private val RAIL =
  listOf(Res.string.nav_home, Res.string.nav_search, Res.string.nav_you, Res.string.nav_saved)

@Composable
private fun RailHeaderContent() {
  val menu = previewOverrideBoolean("menu", true)
  val fab = previewOverrideBoolean("fab", true)
  // Both tallies are read before the early return so the call sequence does not depend on the
  // knobs — the same reason `TopAppBars.NavIcon` resolves its tally ahead of the `nav` check.
  val menuClick = counted("menu")
  val fabClick = counted("new")
  if (menu) {
    IconButton(onClick = menuClick.onClick) {
      Icon(Icons.Filled.Menu, contentDescription = stringResource(Res.string.action_menu))
    }
  }
  if (fab) {
    FloatingActionButton(onClick = fabClick.onClick) {
      Icon(Icons.Filled.Edit, contentDescription = stringResource(Res.string.action_new))
    }
  }
}

@CatalogComponent(
  id = "NavigationRail/Standard",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58016:36880",
  caption = "Destinations along the side. Count, menu, FAB and labels fold in.",
)
@CatalogModes
@OverrideVariant(name = "four", strings = ["count=4"])
@OverrideVariant(name = "no-menu", booleans = ["menu=false"])
@OverrideVariant(name = "no-fab", booleans = ["fab=false"])
@OverrideVariant(name = "no-menu-fab", booleans = ["menu=false", "fab=false"])
@OverrideVariant(name = "labels-none", strings = ["labels=none"])
@Composable
fun NavigationRailSticker() = Sticker {
  val count = previewOverrideString("count", "3").toIntOrNull() ?: 3
  val labels = previewOverrideString("labels", "always")
  val (selected, select) = selectable(0)
  NavigationRail(modifier = Modifier.height(320.dp), header = { RailHeaderContent() }) {
    RAIL.take(count).forEachIndexed { index, label ->
      NavigationRailItem(
        selected = index == selected,
        onClick = { select(index) },
        icon = {
          Icon(
            if (index == selected) Icons.Filled.Stars else Icons.Outlined.Stars,
            contentDescription = null,
          )
        },
        label = if (labels == "none") null else ({ Text(stringResource(label)) }),
      )
    }
  }
}

@CatalogComponent(
  id = "NavigationRail/Wide",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58016:36671",
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
    header = { RailHeaderContent() },
  ) {
    RAIL.take(count).forEachIndexed { index, label ->
      WideNavigationRailItem(
        railExpanded = true,
        selected = index == selected,
        onClick = { select(index) },
        icon = {
          Icon(
            if (index == selected) Icons.Filled.Stars else Icons.Outlined.Stars,
            contentDescription = null,
          )
        },
        label = { Text(stringResource(label)) },
      )
    }
  }
}
