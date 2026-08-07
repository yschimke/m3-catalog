@file:CatalogGroup(name = "Search", section = "Navigation")
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.editable

// Collapsed is the sticker: an expanded search bar fills the screen and is a *screen*, not a
// component. The input field owns its query, so typing works on the live lane.

@CatalogComponent(id = "Search/Bar", caption = "Collapsed search entry point.")
@CatalogModes
@Composable
fun SearchBarSticker() = Sticker {
  val (query, setQuery) = editable("")
  SearchBar(
    modifier = Modifier.width(360.dp),
    inputField = {
      SearchBarDefaults.InputField(
        query = query,
        onQueryChange = setQuery,
        onSearch = {},
        expanded = false,
        onExpandedChange = {},
        placeholder = { Text("Search") },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
      )
    },
    expanded = false,
    onExpandedChange = {},
    content = {},
  )
}
