@file:CatalogGroup(name = "Navigation rail", section = "Navigation")

package ee.schimke.m3catalog.uid

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogChoice
import ee.schimke.m3catalog.catalogText
import ee.schimke.m3catalog.counted
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.nav_home
import ee.schimke.m3catalog.generated.resources.nav_saved
import ee.schimke.m3catalog.generated.resources.nav_search
import ee.schimke.m3catalog.generated.resources.nav_you
import ee.schimke.m3catalog.selectable
import generated.uibuilder.KitNavigationRail
import generated.uibuilder.KitNavigationRailFour
import generated.uibuilder.KitNavigationRailFourNoFab
import generated.uibuilder.KitNavigationRailFourNoFabNoLabels
import generated.uibuilder.KitNavigationRailFourNoLabels
import generated.uibuilder.KitNavigationRailFourNoMenu
import generated.uibuilder.KitNavigationRailFourNoMenuNoFab
import generated.uibuilder.KitNavigationRailFourNoMenuNoFabNoLabels
import generated.uibuilder.KitNavigationRailFourNoMenuNoLabels
import generated.uibuilder.KitNavigationRailNoFab
import generated.uibuilder.KitNavigationRailNoFabNoLabels
import generated.uibuilder.KitNavigationRailNoLabels
import generated.uibuilder.KitNavigationRailNoMenu
import generated.uibuilder.KitNavigationRailNoMenuNoFab
import generated.uibuilder.KitNavigationRailNoMenuNoFabNoLabels
import generated.uibuilder.KitNavigationRailNoMenuNoLabels
import org.jetbrains.compose.resources.stringResource

// The body is `stickers/navigation-rail.uid`: a collapsed `WideNavigationRail` (`m3/wide-navigation-
// rail`, its default state) whose header is the menu button over the kit's flat FAB, and whose
// three destinations are `m3/wide-navigation-rail-item`s with the stars glyph. Each item's
// `selected` is a binding and its click an action-free event, so the selection lives here in the
// shell (`selectable`), as the hand-written sticker keeps it. The kit's count, menu, FAB and label
// axes change the tree, so they cross as sixteen cells (`stickers/navigation-rail.json`).
//
// Not yet expressible, and the reason this shell has no `pairs` entry: the `alignment=middle` axis
// (`arrangement = Arrangement.Center`; the exporter maps no values of `arrangement`), the
// transparent container (`WideNavigationRail` takes `colors`, which no colour bundle builds), and a
// label-less item, which the exporter writes as `label = {}` where the hand-written rail passes
// `null`. A `count` below three draws three destinations.

private val RAIL =
  listOf(Res.string.nav_home, Res.string.nav_search, Res.string.nav_you, Res.string.nav_saved)

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
  // Read for its knob key: the document cannot arrange the rail yet (see above).
  catalogChoice("alignment", "top", "top", "middle")
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
    !four && menu && fab && labelled -> KitNavigationRail(Modifier, homeSelected = selected == 0, homeLabel = labels[0], searchSelected = selected == 1, searchLabel = labels[1], youSelected = selected == 2, youLabel = labels[2], onMenuClick = menuClick.onClick, onFabClick = fabClick.onClick, onHomeClick = { selected = 0 }, onSearchClick = { selected = 1 }, onYouClick = { selected = 2 })
    !four && menu && fab && !labelled -> KitNavigationRailNoLabels(Modifier, homeSelected = selected == 0, searchSelected = selected == 1, youSelected = selected == 2, onMenuClick = menuClick.onClick, onFabClick = fabClick.onClick, onHomeClick = { selected = 0 }, onSearchClick = { selected = 1 }, onYouClick = { selected = 2 })
    !four && menu && !fab && labelled -> KitNavigationRailNoFab(Modifier, homeSelected = selected == 0, homeLabel = labels[0], searchSelected = selected == 1, searchLabel = labels[1], youSelected = selected == 2, youLabel = labels[2], onMenuClick = menuClick.onClick, onHomeClick = { selected = 0 }, onSearchClick = { selected = 1 }, onYouClick = { selected = 2 })
    !four && menu && !fab && !labelled -> KitNavigationRailNoFabNoLabels(Modifier, homeSelected = selected == 0, searchSelected = selected == 1, youSelected = selected == 2, onMenuClick = menuClick.onClick, onHomeClick = { selected = 0 }, onSearchClick = { selected = 1 }, onYouClick = { selected = 2 })
    !four && !menu && fab && labelled -> KitNavigationRailNoMenu(Modifier, homeSelected = selected == 0, homeLabel = labels[0], searchSelected = selected == 1, searchLabel = labels[1], youSelected = selected == 2, youLabel = labels[2], onFabClick = fabClick.onClick, onHomeClick = { selected = 0 }, onSearchClick = { selected = 1 }, onYouClick = { selected = 2 })
    !four && !menu && fab && !labelled -> KitNavigationRailNoMenuNoLabels(Modifier, homeSelected = selected == 0, searchSelected = selected == 1, youSelected = selected == 2, onFabClick = fabClick.onClick, onHomeClick = { selected = 0 }, onSearchClick = { selected = 1 }, onYouClick = { selected = 2 })
    !four && !menu && !fab && labelled -> KitNavigationRailNoMenuNoFab(Modifier, homeSelected = selected == 0, homeLabel = labels[0], searchSelected = selected == 1, searchLabel = labels[1], youSelected = selected == 2, youLabel = labels[2], onHomeClick = { selected = 0 }, onSearchClick = { selected = 1 }, onYouClick = { selected = 2 })
    !four && !menu && !fab && !labelled -> KitNavigationRailNoMenuNoFabNoLabels(Modifier, homeSelected = selected == 0, searchSelected = selected == 1, youSelected = selected == 2, onHomeClick = { selected = 0 }, onSearchClick = { selected = 1 }, onYouClick = { selected = 2 })
    four && menu && fab && labelled -> KitNavigationRailFour(Modifier, homeSelected = selected == 0, homeLabel = labels[0], searchSelected = selected == 1, searchLabel = labels[1], youSelected = selected == 2, youLabel = labels[2], savedSelected = selected == 3, savedLabel = labels[3], onMenuClick = menuClick.onClick, onFabClick = fabClick.onClick, onHomeClick = { selected = 0 }, onSearchClick = { selected = 1 }, onYouClick = { selected = 2 }, onSavedClick = { selected = 3 })
    four && menu && fab && !labelled -> KitNavigationRailFourNoLabels(Modifier, homeSelected = selected == 0, searchSelected = selected == 1, youSelected = selected == 2, savedSelected = selected == 3, onMenuClick = menuClick.onClick, onFabClick = fabClick.onClick, onHomeClick = { selected = 0 }, onSearchClick = { selected = 1 }, onYouClick = { selected = 2 }, onSavedClick = { selected = 3 })
    four && menu && !fab && labelled -> KitNavigationRailFourNoFab(Modifier, homeSelected = selected == 0, homeLabel = labels[0], searchSelected = selected == 1, searchLabel = labels[1], youSelected = selected == 2, youLabel = labels[2], savedSelected = selected == 3, savedLabel = labels[3], onMenuClick = menuClick.onClick, onHomeClick = { selected = 0 }, onSearchClick = { selected = 1 }, onYouClick = { selected = 2 }, onSavedClick = { selected = 3 })
    four && menu && !fab && !labelled -> KitNavigationRailFourNoFabNoLabels(Modifier, homeSelected = selected == 0, searchSelected = selected == 1, youSelected = selected == 2, savedSelected = selected == 3, onMenuClick = menuClick.onClick, onHomeClick = { selected = 0 }, onSearchClick = { selected = 1 }, onYouClick = { selected = 2 }, onSavedClick = { selected = 3 })
    four && !menu && fab && labelled -> KitNavigationRailFourNoMenu(Modifier, homeSelected = selected == 0, homeLabel = labels[0], searchSelected = selected == 1, searchLabel = labels[1], youSelected = selected == 2, youLabel = labels[2], savedSelected = selected == 3, savedLabel = labels[3], onFabClick = fabClick.onClick, onHomeClick = { selected = 0 }, onSearchClick = { selected = 1 }, onYouClick = { selected = 2 }, onSavedClick = { selected = 3 })
    four && !menu && fab && !labelled -> KitNavigationRailFourNoMenuNoLabels(Modifier, homeSelected = selected == 0, searchSelected = selected == 1, youSelected = selected == 2, savedSelected = selected == 3, onFabClick = fabClick.onClick, onHomeClick = { selected = 0 }, onSearchClick = { selected = 1 }, onYouClick = { selected = 2 }, onSavedClick = { selected = 3 })
    four && !menu && !fab && labelled -> KitNavigationRailFourNoMenuNoFab(Modifier, homeSelected = selected == 0, homeLabel = labels[0], searchSelected = selected == 1, searchLabel = labels[1], youSelected = selected == 2, youLabel = labels[2], savedSelected = selected == 3, savedLabel = labels[3], onHomeClick = { selected = 0 }, onSearchClick = { selected = 1 }, onYouClick = { selected = 2 }, onSavedClick = { selected = 3 })
    four && !menu && !fab && !labelled -> KitNavigationRailFourNoMenuNoFabNoLabels(Modifier, homeSelected = selected == 0, searchSelected = selected == 1, youSelected = selected == 2, savedSelected = selected == 3, onHomeClick = { selected = 0 }, onSearchClick = { selected = 1 }, onYouClick = { selected = 2 }, onSavedClick = { selected = 3 })
    else -> error("unreachable")
  }
}
