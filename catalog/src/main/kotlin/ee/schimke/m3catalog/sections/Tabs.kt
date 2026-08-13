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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
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

// Emphasis (primary / secondary) and scroll behaviour (fixed / scrollable) are four separate row
// composables, so they are four components. The foldable axis within each is the tab CONTENT:
// label, icon, or both.

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
  val (selected, select) = selectable(0)
  PrimaryTabRow(selectedTabIndex = selected, modifier = Modifier.width(360.dp)) {
    TABS.take(3).forEachIndexed { index, (title, icon) ->
      Tab(
        selected = index == selected,
        onClick = { select(index) },
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
  val (selected, select) = selectable(1)
  SecondaryTabRow(selectedTabIndex = selected, modifier = Modifier.width(360.dp)) {
    TABS.take(3).forEachIndexed { index, (title, icon) ->
      Tab(
        selected = index == selected,
        onClick = { select(index) },
        text = tabText(stringResource(title)),
        icon = tabIcon(icon),
      )
    }
  }
}

@CatalogComponent(
  id = "Tabs/PrimaryScrollable",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/54563:40124",
  caption = "More categories than fit; the row scrolls.",
)
@CatalogModes
@OverrideVariant(name = "icon-label", strings = ["content=icon+label"])
@Composable
fun PrimaryScrollableTabs() = Sticker {
  val (selected, select) = selectable(0)
  PrimaryScrollableTabRow(selectedTabIndex = selected, modifier = Modifier.width(360.dp)) {
    TABS.forEachIndexed { index, (title, icon) ->
      Tab(
        selected = index == selected,
        onClick = { select(index) },
        text = tabText(stringResource(title)),
        icon = tabIcon(icon),
      )
    }
  }
}

@CatalogComponent(
  id = "Tabs/SecondaryScrollable",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/54563:40055",
  caption = "The scrolling form of the nested row.",
)
@CatalogModes
@OverrideVariant(name = "icon-label", strings = ["content=icon+label"])
@Composable
fun SecondaryScrollableTabs() = Sticker {
  val (selected, select) = selectable(0)
  SecondaryScrollableTabRow(selectedTabIndex = selected, modifier = Modifier.width(360.dp)) {
    TABS.forEachIndexed { index, (title, icon) ->
      Tab(
        selected = index == selected,
        onClick = { select(index) },
        text = tabText(stringResource(title)),
        icon = tabIcon(icon),
      )
    }
  }
}
