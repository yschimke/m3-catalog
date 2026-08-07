@file:CatalogGroup(name = "Tabs", section = "Navigation")
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.selectable

private val tabTitles = listOf("Overview", "Specs", "Reviews")

@CatalogComponent(id = "Tabs/Primary", caption = "Top-level content categories under an app bar.")
@CatalogModes
@Composable
fun PrimaryTabs() = Sticker {
  val (selected, select) = selectable(0)
  PrimaryTabRow(selectedTabIndex = selected, modifier = Modifier.width(360.dp)) {
    tabTitles.forEachIndexed { index, title ->
      Tab(selected = index == selected, onClick = { select(index) }, text = { Text(title) })
    }
  }
}

@CatalogVariant(
  of = "Tabs/Primary",
  props = ["emphasis=secondary"],
  caption = "Nested categories within a primary tab.",
)
@CatalogModes
@Composable
fun SecondaryTabs() = Sticker {
  val (selected, select) = selectable(1)
  SecondaryTabRow(selectedTabIndex = selected, modifier = Modifier.width(360.dp)) {
    tabTitles.forEachIndexed { index, title ->
      Tab(selected = index == selected, onClick = { select(index) }, text = { Text(title) })
    }
  }
}
