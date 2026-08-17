@file:CatalogGroup(name = "Tabs", section = "Navigation")

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogChoice
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.tab_overview
import ee.schimke.m3catalog.generated.resources.tab_related
import ee.schimke.m3catalog.generated.resources.tab_reviews
import ee.schimke.m3catalog.generated.resources.tab_specs
import ee.schimke.m3catalog.selectable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

// The kit's `Tabs` set varies three properties, and this file splits on exactly one of them.
//
// `Style = Primary | Secondary` is the EMPHASIS axis, and emphasis stays a component per style —
// the carve-out in `AGENTS.md`, and what the kit itself does everywhere else it carries this axis
// (five button sets, four icon-button sets, four toggle-button sets, four chip sets). `Stacked
// card` and `Text field` are the only sets the kit does not split that way, and they are its
// outliers rather than its rule.
//
// `Configuration = Fixed | Scrollable` is not emphasis, so it folds — a `@CatalogVariant` under its
// own style, rather than a second pair of top-level cards. Compose backs it with a separate
// composable, which is a fact about the API and not about the taxonomy.
//
// `Layout` is the tab CONTENT — label, icon, or both — and folds as knob cells on each.

private val TABS: List<Pair<StringResource, ImageVector>> =
  listOf(
    Res.string.tab_overview to Icons.Filled.Home,
    Res.string.tab_specs to Icons.Filled.Search,
    Res.string.tab_reviews to Icons.Filled.Person,
    Res.string.tab_related to Icons.Filled.Favorite,
  )

@Composable
private fun tabContent(): String = catalogChoice("content", "label", "label", "icon", "icon+label")

@Composable
private fun tabText(label: String): (@Composable () -> Unit)? =
  if (tabContent() == "icon") null else ({ Text(label) })

@Composable
private fun tabIcon(icon: ImageVector): (@Composable () -> Unit)? =
  if (tabContent() == "label") null else ({ Icon(icon, contentDescription = null) })

@CatalogComponent(
  id = "Tabs/Primary",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/54563:40116",
  caption = "Top-level content categories under an app bar. Icon and icon+label fold in.",
)
@CatalogModes
@OverrideVariant(name = "icon", strings = ["content=icon"])
@OverrideVariant(name = "icon-label", strings = ["content=icon+label"])
@Composable
fun PrimaryTabs() = Sticker {
  var selected by selectable(0)
  PrimaryTabRow(selectedTabIndex = selected, modifier = Modifier.width(360.dp)) {
    TABS.take(3).forEachIndexed { index, (title, icon) ->
      Tab(
        selected = index == selected,
        onClick = { selected = index },
        text = tabText(stringResource(title)),
        icon = tabIcon(icon),
      )
    }
  }
}

@CatalogComponent(
  id = "Tabs/Secondary",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/54563:40047",
  caption = "Nested categories within a primary tab. Icon and icon+label fold in.",
)
@CatalogModes
@OverrideVariant(name = "icon", strings = ["content=icon"])
@OverrideVariant(name = "icon-label", strings = ["content=icon+label"])
@Composable
fun SecondaryTabs() = Sticker {
  var selected by selectable(1)
  SecondaryTabRow(selectedTabIndex = selected, modifier = Modifier.width(360.dp)) {
    TABS.take(3).forEachIndexed { index, (title, icon) ->
      Tab(
        selected = index == selected,
        onClick = { selected = index },
        text = tabText(stringResource(title)),
        icon = tabIcon(icon),
      )
    }
  }
}

@CatalogVariant(
  of = "Tabs/Primary",
  props = ["configuration=scrollable"],
  caption = "More categories than fit; the row scrolls.",
)
@CatalogModes
@OverrideVariant(name = "icon-label", strings = ["content=icon+label"])
@Composable
fun PrimaryScrollableTabs() = Sticker {
  var selected by selectable(0)
  PrimaryScrollableTabRow(selectedTabIndex = selected, modifier = Modifier.width(360.dp)) {
    TABS.forEachIndexed { index, (title, icon) ->
      Tab(
        selected = index == selected,
        onClick = { selected = index },
        text = tabText(stringResource(title)),
        icon = tabIcon(icon),
      )
    }
  }
}

@CatalogVariant(
  of = "Tabs/Secondary",
  props = ["configuration=scrollable"],
  caption = "The scrolling form of the nested row.",
)
@CatalogModes
@OverrideVariant(name = "icon-label", strings = ["content=icon+label"])
@Composable
fun SecondaryScrollableTabs() = Sticker {
  var selected by selectable(0)
  SecondaryScrollableTabRow(selectedTabIndex = selected, modifier = Modifier.width(360.dp)) {
    TABS.forEachIndexed { index, (title, icon) ->
      Tab(
        selected = index == selected,
        onClick = { selected = index },
        text = tabText(stringResource(title)),
        icon = tabIcon(icon),
      )
    }
  }
}
