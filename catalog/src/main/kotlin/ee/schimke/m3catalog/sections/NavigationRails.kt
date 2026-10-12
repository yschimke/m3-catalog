@file:CatalogGroup(name = "Navigation rail", section = "Navigation")
@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)
package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
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
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.InteractionPreview
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogFilledStars
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.CatalogOutlinedStars
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogChoice
import ee.schimke.m3catalog.catalogText
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
import generated.uibuilder.KitNavigationRail
import generated.uibuilder.KitNavigationRailFour
import generated.uibuilder.KitNavigationRailFourMiddle
import generated.uibuilder.KitNavigationRailFourNoFab
import generated.uibuilder.KitNavigationRailFourNoFabMiddle
import generated.uibuilder.KitNavigationRailFourNoFabNoLabels
import generated.uibuilder.KitNavigationRailFourNoFabNoLabelsMiddle
import generated.uibuilder.KitNavigationRailFourNoLabels
import generated.uibuilder.KitNavigationRailFourNoLabelsMiddle
import generated.uibuilder.KitNavigationRailFourNoMenu
import generated.uibuilder.KitNavigationRailFourNoMenuMiddle
import generated.uibuilder.KitNavigationRailFourNoMenuNoFab
import generated.uibuilder.KitNavigationRailFourNoMenuNoFabMiddle
import generated.uibuilder.KitNavigationRailFourNoMenuNoFabNoLabels
import generated.uibuilder.KitNavigationRailFourNoMenuNoFabNoLabelsMiddle
import generated.uibuilder.KitNavigationRailFourNoMenuNoLabels
import generated.uibuilder.KitNavigationRailFourNoMenuNoLabelsMiddle
import generated.uibuilder.KitNavigationRailMiddle
import generated.uibuilder.KitNavigationRailNoFab
import generated.uibuilder.KitNavigationRailNoFabMiddle
import generated.uibuilder.KitNavigationRailNoFabNoLabels
import generated.uibuilder.KitNavigationRailNoFabNoLabelsMiddle
import generated.uibuilder.KitNavigationRailNoLabels
import generated.uibuilder.KitNavigationRailNoLabelsMiddle
import generated.uibuilder.KitNavigationRailNoMenu
import generated.uibuilder.KitNavigationRailNoMenuMiddle
import generated.uibuilder.KitNavigationRailNoMenuNoFab
import generated.uibuilder.KitNavigationRailNoMenuNoFabMiddle
import generated.uibuilder.KitNavigationRailNoMenuNoFabNoLabels
import generated.uibuilder.KitNavigationRailNoMenuNoFabNoLabelsMiddle
import generated.uibuilder.KitNavigationRailNoMenuNoLabels
import generated.uibuilder.KitNavigationRailNoMenuNoLabelsMiddle
import generated.uibuilder.KitWideNavigationRail
import generated.uibuilder.KitWideNavigationRailFour
import generated.uibuilder.KitWideNavigationRailFourMiddle
import generated.uibuilder.KitWideNavigationRailFourNoFab
import generated.uibuilder.KitWideNavigationRailFourNoFabMiddle
import generated.uibuilder.KitWideNavigationRailFourNoMenu
import generated.uibuilder.KitWideNavigationRailFourNoMenuMiddle
import generated.uibuilder.KitWideNavigationRailFourNoMenuNoFab
import generated.uibuilder.KitWideNavigationRailFourNoMenuNoFabMiddle
import generated.uibuilder.KitWideNavigationRailMiddle
import generated.uibuilder.KitWideNavigationRailNoFab
import generated.uibuilder.KitWideNavigationRailNoFabMiddle
import generated.uibuilder.KitWideNavigationRailNoMenu
import generated.uibuilder.KitWideNavigationRailNoMenuMiddle
import generated.uibuilder.KitWideNavigationRailNoMenuNoFab
import generated.uibuilder.KitWideNavigationRailNoMenuNoFabMiddle
import org.jetbrains.compose.resources.stringResource

// The kit's axes: destination count, the optional menu and FAB header slots, and label visibility.
// Both kit sets are `WideNavigationRail`: collapsed (`Navigation Rail`) and expanded
// (`Navigation Rail: Expanded`). The kit publishes them as two sets, so they are two components.

private val RAIL =
  listOf(Res.string.nav_home, Res.string.nav_search, Res.string.nav_you, Res.string.nav_saved)

// --- Generated sticker bodies, from uid-stickers/NavigationRails.kt ---

@CatalogComponent(
  id = "NavigationRail/Standard",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58016:36948",
  caption =
    "Destinations along the side: a collapsed WideNavigationRail. Count, menu, FAB and labels " +
      "fold in.",
)
@CatalogModes
@OverrideVariant(name = "four", ints = ["count=4"])
@OverrideVariant(name = "no-menu", booleans = ["menu=false"])
@OverrideVariant(name = "no-fab", booleans = ["fab=false"])
@OverrideVariant(name = "no-menu-fab", booleans = ["menu=false", "fab=false"])
@OverrideVariant(name = "labels-none", strings = ["labels=none"])
@OverrideVariant(name = "middle", strings = ["alignment=middle"])
@ee.schimke.m3catalog.NavigationRailStickerExhaustiveKitCells
@Composable
fun NavigationRailSticker(count: Int = 3, menu: Boolean = true, fab: Boolean = true) = Sticker {
  val labelled = catalogChoice("labels", "always", "always", "none") != "none"
  val middle = catalogChoice("alignment", "top", "top", "middle") == "middle"
  var selected by selectable(0)
  val menuClick = counted("menu")
  val fabClick = counted("new")
  val four = count >= 4
  val labels =
    if (labelled)
      RAIL.take(if (four) 4 else 3).mapIndexed { index, label ->
        catalogText("label", stringResource(label), index)
      }
    else emptyList()
  when {
    !four && menu && fab && labelled && !middle ->
      KitNavigationRail(
        Modifier,
        homeSelected = selected == 0,
        homeLabel = labels[0],
        searchSelected = selected == 1,
        searchLabel = labels[1],
        youSelected = selected == 2,
        youLabel = labels[2],
        onMenuClick = menuClick.onClick,
        onFabClick = fabClick.onClick,
        onHomeClick = { selected = 0 },
        onSearchClick = { selected = 1 },
        onYouClick = { selected = 2 },
      )
    !four && menu && fab && labelled && middle ->
      KitNavigationRailMiddle(
        Modifier,
        homeSelected = selected == 0,
        homeLabel = labels[0],
        searchSelected = selected == 1,
        searchLabel = labels[1],
        youSelected = selected == 2,
        youLabel = labels[2],
        onMenuClick = menuClick.onClick,
        onFabClick = fabClick.onClick,
        onHomeClick = { selected = 0 },
        onSearchClick = { selected = 1 },
        onYouClick = { selected = 2 },
      )
    !four && menu && fab && !labelled && !middle ->
      KitNavigationRailNoLabels(
        Modifier,
        homeSelected = selected == 0,
        searchSelected = selected == 1,
        youSelected = selected == 2,
        onMenuClick = menuClick.onClick,
        onFabClick = fabClick.onClick,
        onHomeClick = { selected = 0 },
        onSearchClick = { selected = 1 },
        onYouClick = { selected = 2 },
      )
    !four && menu && fab && !labelled && middle ->
      KitNavigationRailNoLabelsMiddle(
        Modifier,
        homeSelected = selected == 0,
        searchSelected = selected == 1,
        youSelected = selected == 2,
        onMenuClick = menuClick.onClick,
        onFabClick = fabClick.onClick,
        onHomeClick = { selected = 0 },
        onSearchClick = { selected = 1 },
        onYouClick = { selected = 2 },
      )
    !four && menu && !fab && labelled && !middle ->
      KitNavigationRailNoFab(
        Modifier,
        homeSelected = selected == 0,
        homeLabel = labels[0],
        searchSelected = selected == 1,
        searchLabel = labels[1],
        youSelected = selected == 2,
        youLabel = labels[2],
        onMenuClick = menuClick.onClick,
        onHomeClick = { selected = 0 },
        onSearchClick = { selected = 1 },
        onYouClick = { selected = 2 },
      )
    !four && menu && !fab && labelled && middle ->
      KitNavigationRailNoFabMiddle(
        Modifier,
        homeSelected = selected == 0,
        homeLabel = labels[0],
        searchSelected = selected == 1,
        searchLabel = labels[1],
        youSelected = selected == 2,
        youLabel = labels[2],
        onMenuClick = menuClick.onClick,
        onHomeClick = { selected = 0 },
        onSearchClick = { selected = 1 },
        onYouClick = { selected = 2 },
      )
    !four && menu && !fab && !labelled && !middle ->
      KitNavigationRailNoFabNoLabels(
        Modifier,
        homeSelected = selected == 0,
        searchSelected = selected == 1,
        youSelected = selected == 2,
        onMenuClick = menuClick.onClick,
        onHomeClick = { selected = 0 },
        onSearchClick = { selected = 1 },
        onYouClick = { selected = 2 },
      )
    !four && menu && !fab && !labelled && middle ->
      KitNavigationRailNoFabNoLabelsMiddle(
        Modifier,
        homeSelected = selected == 0,
        searchSelected = selected == 1,
        youSelected = selected == 2,
        onMenuClick = menuClick.onClick,
        onHomeClick = { selected = 0 },
        onSearchClick = { selected = 1 },
        onYouClick = { selected = 2 },
      )
    !four && !menu && fab && labelled && !middle ->
      KitNavigationRailNoMenu(
        Modifier,
        homeSelected = selected == 0,
        homeLabel = labels[0],
        searchSelected = selected == 1,
        searchLabel = labels[1],
        youSelected = selected == 2,
        youLabel = labels[2],
        onFabClick = fabClick.onClick,
        onHomeClick = { selected = 0 },
        onSearchClick = { selected = 1 },
        onYouClick = { selected = 2 },
      )
    !four && !menu && fab && labelled && middle ->
      KitNavigationRailNoMenuMiddle(
        Modifier,
        homeSelected = selected == 0,
        homeLabel = labels[0],
        searchSelected = selected == 1,
        searchLabel = labels[1],
        youSelected = selected == 2,
        youLabel = labels[2],
        onFabClick = fabClick.onClick,
        onHomeClick = { selected = 0 },
        onSearchClick = { selected = 1 },
        onYouClick = { selected = 2 },
      )
    !four && !menu && fab && !labelled && !middle ->
      KitNavigationRailNoMenuNoLabels(
        Modifier,
        homeSelected = selected == 0,
        searchSelected = selected == 1,
        youSelected = selected == 2,
        onFabClick = fabClick.onClick,
        onHomeClick = { selected = 0 },
        onSearchClick = { selected = 1 },
        onYouClick = { selected = 2 },
      )
    !four && !menu && fab && !labelled && middle ->
      KitNavigationRailNoMenuNoLabelsMiddle(
        Modifier,
        homeSelected = selected == 0,
        searchSelected = selected == 1,
        youSelected = selected == 2,
        onFabClick = fabClick.onClick,
        onHomeClick = { selected = 0 },
        onSearchClick = { selected = 1 },
        onYouClick = { selected = 2 },
      )
    !four && !menu && !fab && labelled && !middle ->
      KitNavigationRailNoMenuNoFab(
        Modifier,
        homeSelected = selected == 0,
        homeLabel = labels[0],
        searchSelected = selected == 1,
        searchLabel = labels[1],
        youSelected = selected == 2,
        youLabel = labels[2],
        onHomeClick = { selected = 0 },
        onSearchClick = { selected = 1 },
        onYouClick = { selected = 2 },
      )
    !four && !menu && !fab && labelled && middle ->
      KitNavigationRailNoMenuNoFabMiddle(
        Modifier,
        homeSelected = selected == 0,
        homeLabel = labels[0],
        searchSelected = selected == 1,
        searchLabel = labels[1],
        youSelected = selected == 2,
        youLabel = labels[2],
        onHomeClick = { selected = 0 },
        onSearchClick = { selected = 1 },
        onYouClick = { selected = 2 },
      )
    !four && !menu && !fab && !labelled && !middle ->
      KitNavigationRailNoMenuNoFabNoLabels(
        Modifier,
        homeSelected = selected == 0,
        searchSelected = selected == 1,
        youSelected = selected == 2,
        onHomeClick = { selected = 0 },
        onSearchClick = { selected = 1 },
        onYouClick = { selected = 2 },
      )
    !four && !menu && !fab && !labelled && middle ->
      KitNavigationRailNoMenuNoFabNoLabelsMiddle(
        Modifier,
        homeSelected = selected == 0,
        searchSelected = selected == 1,
        youSelected = selected == 2,
        onHomeClick = { selected = 0 },
        onSearchClick = { selected = 1 },
        onYouClick = { selected = 2 },
      )
    four && menu && fab && labelled && !middle ->
      KitNavigationRailFour(
        Modifier,
        homeSelected = selected == 0,
        homeLabel = labels[0],
        searchSelected = selected == 1,
        searchLabel = labels[1],
        youSelected = selected == 2,
        youLabel = labels[2],
        savedSelected = selected == 3,
        savedLabel = labels[3],
        onMenuClick = menuClick.onClick,
        onFabClick = fabClick.onClick,
        onHomeClick = { selected = 0 },
        onSearchClick = { selected = 1 },
        onYouClick = { selected = 2 },
        onSavedClick = { selected = 3 },
      )
    four && menu && fab && labelled && middle ->
      KitNavigationRailFourMiddle(
        Modifier,
        homeSelected = selected == 0,
        homeLabel = labels[0],
        searchSelected = selected == 1,
        searchLabel = labels[1],
        youSelected = selected == 2,
        youLabel = labels[2],
        savedSelected = selected == 3,
        savedLabel = labels[3],
        onMenuClick = menuClick.onClick,
        onFabClick = fabClick.onClick,
        onHomeClick = { selected = 0 },
        onSearchClick = { selected = 1 },
        onYouClick = { selected = 2 },
        onSavedClick = { selected = 3 },
      )
    four && menu && fab && !labelled && !middle ->
      KitNavigationRailFourNoLabels(
        Modifier,
        homeSelected = selected == 0,
        searchSelected = selected == 1,
        youSelected = selected == 2,
        savedSelected = selected == 3,
        onMenuClick = menuClick.onClick,
        onFabClick = fabClick.onClick,
        onHomeClick = { selected = 0 },
        onSearchClick = { selected = 1 },
        onYouClick = { selected = 2 },
        onSavedClick = { selected = 3 },
      )
    four && menu && fab && !labelled && middle ->
      KitNavigationRailFourNoLabelsMiddle(
        Modifier,
        homeSelected = selected == 0,
        searchSelected = selected == 1,
        youSelected = selected == 2,
        savedSelected = selected == 3,
        onMenuClick = menuClick.onClick,
        onFabClick = fabClick.onClick,
        onHomeClick = { selected = 0 },
        onSearchClick = { selected = 1 },
        onYouClick = { selected = 2 },
        onSavedClick = { selected = 3 },
      )
    four && menu && !fab && labelled && !middle ->
      KitNavigationRailFourNoFab(
        Modifier,
        homeSelected = selected == 0,
        homeLabel = labels[0],
        searchSelected = selected == 1,
        searchLabel = labels[1],
        youSelected = selected == 2,
        youLabel = labels[2],
        savedSelected = selected == 3,
        savedLabel = labels[3],
        onMenuClick = menuClick.onClick,
        onHomeClick = { selected = 0 },
        onSearchClick = { selected = 1 },
        onYouClick = { selected = 2 },
        onSavedClick = { selected = 3 },
      )
    four && menu && !fab && labelled && middle ->
      KitNavigationRailFourNoFabMiddle(
        Modifier,
        homeSelected = selected == 0,
        homeLabel = labels[0],
        searchSelected = selected == 1,
        searchLabel = labels[1],
        youSelected = selected == 2,
        youLabel = labels[2],
        savedSelected = selected == 3,
        savedLabel = labels[3],
        onMenuClick = menuClick.onClick,
        onHomeClick = { selected = 0 },
        onSearchClick = { selected = 1 },
        onYouClick = { selected = 2 },
        onSavedClick = { selected = 3 },
      )
    four && menu && !fab && !labelled && !middle ->
      KitNavigationRailFourNoFabNoLabels(
        Modifier,
        homeSelected = selected == 0,
        searchSelected = selected == 1,
        youSelected = selected == 2,
        savedSelected = selected == 3,
        onMenuClick = menuClick.onClick,
        onHomeClick = { selected = 0 },
        onSearchClick = { selected = 1 },
        onYouClick = { selected = 2 },
        onSavedClick = { selected = 3 },
      )
    four && menu && !fab && !labelled && middle ->
      KitNavigationRailFourNoFabNoLabelsMiddle(
        Modifier,
        homeSelected = selected == 0,
        searchSelected = selected == 1,
        youSelected = selected == 2,
        savedSelected = selected == 3,
        onMenuClick = menuClick.onClick,
        onHomeClick = { selected = 0 },
        onSearchClick = { selected = 1 },
        onYouClick = { selected = 2 },
        onSavedClick = { selected = 3 },
      )
    four && !menu && fab && labelled && !middle ->
      KitNavigationRailFourNoMenu(
        Modifier,
        homeSelected = selected == 0,
        homeLabel = labels[0],
        searchSelected = selected == 1,
        searchLabel = labels[1],
        youSelected = selected == 2,
        youLabel = labels[2],
        savedSelected = selected == 3,
        savedLabel = labels[3],
        onFabClick = fabClick.onClick,
        onHomeClick = { selected = 0 },
        onSearchClick = { selected = 1 },
        onYouClick = { selected = 2 },
        onSavedClick = { selected = 3 },
      )
    four && !menu && fab && labelled && middle ->
      KitNavigationRailFourNoMenuMiddle(
        Modifier,
        homeSelected = selected == 0,
        homeLabel = labels[0],
        searchSelected = selected == 1,
        searchLabel = labels[1],
        youSelected = selected == 2,
        youLabel = labels[2],
        savedSelected = selected == 3,
        savedLabel = labels[3],
        onFabClick = fabClick.onClick,
        onHomeClick = { selected = 0 },
        onSearchClick = { selected = 1 },
        onYouClick = { selected = 2 },
        onSavedClick = { selected = 3 },
      )
    four && !menu && fab && !labelled && !middle ->
      KitNavigationRailFourNoMenuNoLabels(
        Modifier,
        homeSelected = selected == 0,
        searchSelected = selected == 1,
        youSelected = selected == 2,
        savedSelected = selected == 3,
        onFabClick = fabClick.onClick,
        onHomeClick = { selected = 0 },
        onSearchClick = { selected = 1 },
        onYouClick = { selected = 2 },
        onSavedClick = { selected = 3 },
      )
    four && !menu && fab && !labelled && middle ->
      KitNavigationRailFourNoMenuNoLabelsMiddle(
        Modifier,
        homeSelected = selected == 0,
        searchSelected = selected == 1,
        youSelected = selected == 2,
        savedSelected = selected == 3,
        onFabClick = fabClick.onClick,
        onHomeClick = { selected = 0 },
        onSearchClick = { selected = 1 },
        onYouClick = { selected = 2 },
        onSavedClick = { selected = 3 },
      )
    four && !menu && !fab && labelled && !middle ->
      KitNavigationRailFourNoMenuNoFab(
        Modifier,
        homeSelected = selected == 0,
        homeLabel = labels[0],
        searchSelected = selected == 1,
        searchLabel = labels[1],
        youSelected = selected == 2,
        youLabel = labels[2],
        savedSelected = selected == 3,
        savedLabel = labels[3],
        onHomeClick = { selected = 0 },
        onSearchClick = { selected = 1 },
        onYouClick = { selected = 2 },
        onSavedClick = { selected = 3 },
      )
    four && !menu && !fab && labelled && middle ->
      KitNavigationRailFourNoMenuNoFabMiddle(
        Modifier,
        homeSelected = selected == 0,
        homeLabel = labels[0],
        searchSelected = selected == 1,
        searchLabel = labels[1],
        youSelected = selected == 2,
        youLabel = labels[2],
        savedSelected = selected == 3,
        savedLabel = labels[3],
        onHomeClick = { selected = 0 },
        onSearchClick = { selected = 1 },
        onYouClick = { selected = 2 },
        onSavedClick = { selected = 3 },
      )
    four && !menu && !fab && !labelled && !middle ->
      KitNavigationRailFourNoMenuNoFabNoLabels(
        Modifier,
        homeSelected = selected == 0,
        searchSelected = selected == 1,
        youSelected = selected == 2,
        savedSelected = selected == 3,
        onHomeClick = { selected = 0 },
        onSearchClick = { selected = 1 },
        onYouClick = { selected = 2 },
        onSavedClick = { selected = 3 },
      )
    four && !menu && !fab && !labelled && middle ->
      KitNavigationRailFourNoMenuNoFabNoLabelsMiddle(
        Modifier,
        homeSelected = selected == 0,
        searchSelected = selected == 1,
        youSelected = selected == 2,
        savedSelected = selected == 3,
        onHomeClick = { selected = 0 },
        onSearchClick = { selected = 1 },
        onYouClick = { selected = 2 },
        onSavedClick = { selected = 3 },
      )
    else -> error("unreachable")
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
@OverrideVariant(name = "four", ints = ["count=4"])
@OverrideVariant(name = "middle", strings = ["alignment=middle"])
@ee.schimke.m3catalog.WideNavigationRailStickerExhaustiveKitCells
@Composable
fun WideNavigationRailSticker(count: Int = 3, menu: Boolean = true, fab: Boolean = true) = Sticker {
  var selected by selectable(0)
  val middle = catalogChoice("alignment", "top", "top", "middle") == "middle"
  val menuClick = counted("menu")
  val fabClick = counted("new")
  val fabLabel = if (fab) catalogText("fabLabel", stringResource(Res.string.label_text)) else ""
  val four = count >= 4
  val labels =
    RAIL.take(if (four) 4 else 3).mapIndexed { index, label ->
      catalogText("label", stringResource(label), index)
    }
  when {
    !four && menu && fab && !middle ->
      KitWideNavigationRail(
        Modifier,
        homeSelected = selected == 0,
        homeLabel = labels[0],
        searchSelected = selected == 1,
        searchLabel = labels[1],
        youSelected = selected == 2,
        youLabel = labels[2],
        fabLabel = fabLabel,
        onMenuClick = menuClick.onClick,
        onFabClick = fabClick.onClick,
        onHomeClick = { selected = 0 },
        onSearchClick = { selected = 1 },
        onYouClick = { selected = 2 },
      )
    !four && menu && fab && middle ->
      KitWideNavigationRailMiddle(
        Modifier,
        homeSelected = selected == 0,
        homeLabel = labels[0],
        searchSelected = selected == 1,
        searchLabel = labels[1],
        youSelected = selected == 2,
        youLabel = labels[2],
        fabLabel = fabLabel,
        onMenuClick = menuClick.onClick,
        onFabClick = fabClick.onClick,
        onHomeClick = { selected = 0 },
        onSearchClick = { selected = 1 },
        onYouClick = { selected = 2 },
      )
    !four && menu && !fab && !middle ->
      KitWideNavigationRailNoFab(
        Modifier,
        homeSelected = selected == 0,
        homeLabel = labels[0],
        searchSelected = selected == 1,
        searchLabel = labels[1],
        youSelected = selected == 2,
        youLabel = labels[2],
        onMenuClick = menuClick.onClick,
        onHomeClick = { selected = 0 },
        onSearchClick = { selected = 1 },
        onYouClick = { selected = 2 },
      )
    !four && menu && !fab && middle ->
      KitWideNavigationRailNoFabMiddle(
        Modifier,
        homeSelected = selected == 0,
        homeLabel = labels[0],
        searchSelected = selected == 1,
        searchLabel = labels[1],
        youSelected = selected == 2,
        youLabel = labels[2],
        onMenuClick = menuClick.onClick,
        onHomeClick = { selected = 0 },
        onSearchClick = { selected = 1 },
        onYouClick = { selected = 2 },
      )
    !four && !menu && fab && !middle ->
      KitWideNavigationRailNoMenu(
        Modifier,
        homeSelected = selected == 0,
        homeLabel = labels[0],
        searchSelected = selected == 1,
        searchLabel = labels[1],
        youSelected = selected == 2,
        youLabel = labels[2],
        fabLabel = fabLabel,
        onFabClick = fabClick.onClick,
        onHomeClick = { selected = 0 },
        onSearchClick = { selected = 1 },
        onYouClick = { selected = 2 },
      )
    !four && !menu && fab && middle ->
      KitWideNavigationRailNoMenuMiddle(
        Modifier,
        homeSelected = selected == 0,
        homeLabel = labels[0],
        searchSelected = selected == 1,
        searchLabel = labels[1],
        youSelected = selected == 2,
        youLabel = labels[2],
        fabLabel = fabLabel,
        onFabClick = fabClick.onClick,
        onHomeClick = { selected = 0 },
        onSearchClick = { selected = 1 },
        onYouClick = { selected = 2 },
      )
    !four && !menu && !fab && !middle ->
      KitWideNavigationRailNoMenuNoFab(
        Modifier,
        homeSelected = selected == 0,
        homeLabel = labels[0],
        searchSelected = selected == 1,
        searchLabel = labels[1],
        youSelected = selected == 2,
        youLabel = labels[2],
        onHomeClick = { selected = 0 },
        onSearchClick = { selected = 1 },
        onYouClick = { selected = 2 },
      )
    !four && !menu && !fab && middle ->
      KitWideNavigationRailNoMenuNoFabMiddle(
        Modifier,
        homeSelected = selected == 0,
        homeLabel = labels[0],
        searchSelected = selected == 1,
        searchLabel = labels[1],
        youSelected = selected == 2,
        youLabel = labels[2],
        onHomeClick = { selected = 0 },
        onSearchClick = { selected = 1 },
        onYouClick = { selected = 2 },
      )
    four && menu && fab && !middle ->
      KitWideNavigationRailFour(
        Modifier,
        homeSelected = selected == 0,
        homeLabel = labels[0],
        searchSelected = selected == 1,
        searchLabel = labels[1],
        youSelected = selected == 2,
        youLabel = labels[2],
        savedSelected = selected == 3,
        savedLabel = labels[3],
        fabLabel = fabLabel,
        onMenuClick = menuClick.onClick,
        onFabClick = fabClick.onClick,
        onHomeClick = { selected = 0 },
        onSearchClick = { selected = 1 },
        onYouClick = { selected = 2 },
        onSavedClick = { selected = 3 },
      )
    four && menu && fab && middle ->
      KitWideNavigationRailFourMiddle(
        Modifier,
        homeSelected = selected == 0,
        homeLabel = labels[0],
        searchSelected = selected == 1,
        searchLabel = labels[1],
        youSelected = selected == 2,
        youLabel = labels[2],
        savedSelected = selected == 3,
        savedLabel = labels[3],
        fabLabel = fabLabel,
        onMenuClick = menuClick.onClick,
        onFabClick = fabClick.onClick,
        onHomeClick = { selected = 0 },
        onSearchClick = { selected = 1 },
        onYouClick = { selected = 2 },
        onSavedClick = { selected = 3 },
      )
    four && menu && !fab && !middle ->
      KitWideNavigationRailFourNoFab(
        Modifier,
        homeSelected = selected == 0,
        homeLabel = labels[0],
        searchSelected = selected == 1,
        searchLabel = labels[1],
        youSelected = selected == 2,
        youLabel = labels[2],
        savedSelected = selected == 3,
        savedLabel = labels[3],
        onMenuClick = menuClick.onClick,
        onHomeClick = { selected = 0 },
        onSearchClick = { selected = 1 },
        onYouClick = { selected = 2 },
        onSavedClick = { selected = 3 },
      )
    four && menu && !fab && middle ->
      KitWideNavigationRailFourNoFabMiddle(
        Modifier,
        homeSelected = selected == 0,
        homeLabel = labels[0],
        searchSelected = selected == 1,
        searchLabel = labels[1],
        youSelected = selected == 2,
        youLabel = labels[2],
        savedSelected = selected == 3,
        savedLabel = labels[3],
        onMenuClick = menuClick.onClick,
        onHomeClick = { selected = 0 },
        onSearchClick = { selected = 1 },
        onYouClick = { selected = 2 },
        onSavedClick = { selected = 3 },
      )
    four && !menu && fab && !middle ->
      KitWideNavigationRailFourNoMenu(
        Modifier,
        homeSelected = selected == 0,
        homeLabel = labels[0],
        searchSelected = selected == 1,
        searchLabel = labels[1],
        youSelected = selected == 2,
        youLabel = labels[2],
        savedSelected = selected == 3,
        savedLabel = labels[3],
        fabLabel = fabLabel,
        onFabClick = fabClick.onClick,
        onHomeClick = { selected = 0 },
        onSearchClick = { selected = 1 },
        onYouClick = { selected = 2 },
        onSavedClick = { selected = 3 },
      )
    four && !menu && fab && middle ->
      KitWideNavigationRailFourNoMenuMiddle(
        Modifier,
        homeSelected = selected == 0,
        homeLabel = labels[0],
        searchSelected = selected == 1,
        searchLabel = labels[1],
        youSelected = selected == 2,
        youLabel = labels[2],
        savedSelected = selected == 3,
        savedLabel = labels[3],
        fabLabel = fabLabel,
        onFabClick = fabClick.onClick,
        onHomeClick = { selected = 0 },
        onSearchClick = { selected = 1 },
        onYouClick = { selected = 2 },
        onSavedClick = { selected = 3 },
      )
    four && !menu && !fab && !middle ->
      KitWideNavigationRailFourNoMenuNoFab(
        Modifier,
        homeSelected = selected == 0,
        homeLabel = labels[0],
        searchSelected = selected == 1,
        searchLabel = labels[1],
        youSelected = selected == 2,
        youLabel = labels[2],
        savedSelected = selected == 3,
        savedLabel = labels[3],
        onHomeClick = { selected = 0 },
        onSearchClick = { selected = 1 },
        onYouClick = { selected = 2 },
        onSavedClick = { selected = 3 },
      )
    four && !menu && !fab && middle ->
      KitWideNavigationRailFourNoMenuNoFabMiddle(
        Modifier,
        homeSelected = selected == 0,
        homeLabel = labels[0],
        searchSelected = selected == 1,
        searchLabel = labels[1],
        youSelected = selected == 2,
        youLabel = labels[2],
        savedSelected = selected == 3,
        savedLabel = labels[3],
        onHomeClick = { selected = 0 },
        onSearchClick = { selected = 1 },
        onYouClick = { selected = 2 },
        onSavedClick = { selected = 3 },
      )
    else -> error("unreachable")
  }
}
