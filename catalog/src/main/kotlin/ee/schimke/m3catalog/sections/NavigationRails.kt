@file:CatalogGroup(name = "Navigation rail", section = "Navigation")
@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuOpen
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.material3.WideNavigationRail
import androidx.compose.material3.WideNavigationRailDefaults
import androidx.compose.material3.WideNavigationRailItem
import androidx.compose.material3.WideNavigationRailValue
import androidx.compose.material3.rememberWideNavigationRailState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.overrides.previewOverrideBoolean
import ee.schimke.composeai.overrides.previewOverrideString
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.InteractionPreview
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogFilledStars
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.CatalogOutlinedStars
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogChoice
import ee.schimke.m3catalog.counted
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.action_menu
import ee.schimke.m3catalog.generated.resources.action_new
import ee.schimke.m3catalog.generated.resources.label_text
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
private fun RailHeaderContent(wide: Boolean) {
  val menu = previewOverrideBoolean("menu", true)
  val fab = previewOverrideBoolean("fab", true)
  // Both tallies are read before the early return so the call sequence does not depend on the
  // knobs — the same reason `TopAppBars.NavIcon` resolves its tally ahead of the `nav` check.
  val menuClick = counted("menu")
  val fabClick = counted("new")
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    // WideNavigationRail reserves its own header inset; the standard rail does not.
    if (!wide) Spacer(Modifier.height(44.dp))
    if (menu) {
      IconButton(onClick = menuClick.onClick) {
        Icon(
          if (wide) Icons.AutoMirrored.Filled.MenuOpen else Icons.Filled.Menu,
          contentDescription = stringResource(Res.string.action_menu),
        )
      }
    }
    if (fab) {
      if (menu) Spacer(Modifier.height(8.dp))
      val elevation =
        FloatingActionButtonDefaults.elevation(
          defaultElevation = 0.dp,
          pressedElevation = 0.dp,
          focusedElevation = 0.dp,
          hoveredElevation = 0.dp,
        )
      if (wide) {
        ExtendedFloatingActionButton(
          onClick = fabClick.onClick,
          elevation = elevation,
          icon = {
            Icon(Icons.Filled.Edit, contentDescription = stringResource(Res.string.action_new))
          },
          text = { Text(stringResource(Res.string.label_text)) },
        )
      } else {
        FloatingActionButton(onClick = fabClick.onClick, elevation = elevation) {
          Icon(Icons.Filled.Edit, contentDescription = stringResource(Res.string.action_new))
        }
      }
      // NavigationRail starts destinations immediately after its header, while WideNavigationRail
      // supplies the 40 dp header-to-destination gap itself.
      if (!wide) Spacer(Modifier.height(92.dp))
    }
  }
}

@CatalogComponent(
  id = "NavigationRail/Standard",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58016:36948",
  caption = "Destinations along the side. Count, menu, FAB and labels fold in.",
)
@CatalogModes
@OverrideVariant(name = "four", strings = ["count=4"])
@OverrideVariant(name = "no-menu", booleans = ["menu=false"])
@OverrideVariant(name = "no-fab", booleans = ["fab=false"])
@OverrideVariant(name = "no-menu-fab", booleans = ["menu=false", "fab=false"])
@OverrideVariant(name = "labels-none", strings = ["labels=none"])
@OverrideVariant(name = "middle", strings = ["alignment=middle"])
@ee.schimke.m3catalog.NavigationRailStickerExhaustiveKitCells
@Composable
fun NavigationRailSticker() = Sticker {
  val count = previewOverrideString("count", "3").toIntOrNull() ?: 3
  val labels = catalogChoice("labels", "always", "always", "none")
  // The kit's `Alignment` axis. `NavigationRail` stacks its header and items from the top and
  // offers no arrangement parameter, so TOP is what an unseeded rail draws — the reference above
  // points at the kit's `Alignment=Top` node for that reason, having previously named `Middle`
  // while rendering neither. Middle is the variant, and a leading weighted spacer is what makes
  // it: there is nothing on `NavigationRail` to ask for it.
  val middleAligned = catalogChoice("alignment", "top", "top", "middle") == "middle"
  var selected by selectable(0)
  Box(Modifier.padding(horizontal = 8.dp)) {
    NavigationRail(
      modifier = Modifier.height(800.dp),
      containerColor = Color.Transparent,
      header = { RailHeaderContent(wide = false) },
    ) {
      if (middleAligned) Column(Modifier.weight(1f)) {}
      RAIL.take(count).forEachIndexed { index, label ->
        NavigationRailItem(
          selected = index == selected,
          onClick = { selected = index },
          icon = {
            Icon(
              if (index == selected) CatalogFilledStars else CatalogOutlinedStars,
              contentDescription = null,
            )
          },
          label = if (labels == "none") null else ({ Text(stringResource(label)) }),
        )
      }
      if (middleAligned) Column(Modifier.weight(1f)) {}
    }
  }
}

@CatalogComponent(
  id = "NavigationRail/Wide",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58016:36671",
  caption = "The expanded form, labels beside the icons. Count folds in.",
)
// Indices count EVERY clickable node in layout order, and this rail's header contributes two of
// its own before any destination: the menu button (0) and the extended FAB (1). So the three
// destinations are 2, 3 and 4 — targeting [0] here would repeatedly tap "menu" and record a rail
// whose selection never moved. Both header knobs default on; a variant that turned them off would
// shift these, which is one more reason the interaction rides the default render only.
@InteractionPreview(
  targets = [4, 2, 3],
  caption =
    "Change destinations. The indicator travels while the item's label and icon cross-fade — the " +
      "two run on different specs, which is only visible when they move together.",
)
@CatalogModes
@OverrideVariant(name = "four", strings = ["count=4"])
@OverrideVariant(name = "middle", strings = ["alignment=middle"])
@ee.schimke.m3catalog.WideNavigationRailStickerExhaustiveKitCells
@Composable
fun WideNavigationRailSticker() = Sticker {
  val count = previewOverrideString("count", "3").toIntOrNull() ?: 3
  var selected by selectable(0)
  // `WideNavigationRail` takes a state object, not an `expanded` flag — the expansion is animated
  // and the rail owns it. Seeded Expanded so the baked capture shows the form this component is
  // for; the collapsed rail is `NavigationRail/Standard` above.
  // The kit's `Alignment` axis. Unlike the standard rail, this one takes an `arrangement`, so
  // the middle cell is a parameter rather than a spacer.
  val middle = catalogChoice("alignment", "top", "top", "middle") == "middle"
  WideNavigationRail(
    modifier = Modifier.height(800.dp),
    arrangement = if (middle) Arrangement.Center else Arrangement.Top,
    state = rememberWideNavigationRailState(WideNavigationRailValue.Expanded),
    colors = WideNavigationRailDefaults.colors(containerColor = Color.Transparent),
    header = { RailHeaderContent(wide = true) },
  ) {
    RAIL.take(count).forEachIndexed { index, label ->
      WideNavigationRailItem(
        railExpanded = true,
        selected = index == selected,
        onClick = { selected = index },
        icon = {
          Icon(
            if (index == selected) CatalogFilledStars else CatalogOutlinedStars,
            contentDescription = null,
          )
        },
        label = { Text(stringResource(label)) },
      )
    }
  }
}
