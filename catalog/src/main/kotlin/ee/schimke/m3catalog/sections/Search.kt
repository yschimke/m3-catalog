@file:CatalogGroup(name = "Search", section = "Navigation")
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AppBarWithSearch
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopSearchBar
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.overrides.previewOverrideString
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.counted
import ee.schimke.m3catalog.editable
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.action_back
import ee.schimke.m3catalog.generated.resources.action_clear
import ee.schimke.m3catalog.generated.resources.action_menu
import ee.schimke.m3catalog.generated.resources.action_more
import ee.schimke.m3catalog.generated.resources.label_account
import ee.schimke.m3catalog.generated.resources.search_hint
import ee.schimke.m3catalog.generated.resources.search_suggestion_motion
import ee.schimke.m3catalog.generated.resources.search_suggestion_sticker_sheet
import ee.schimke.m3catalog.generated.resources.search_suggestion_symbols
import ee.schimke.m3catalog.toggleable
import org.jetbrains.compose.resources.stringResource

// The kit's Search section is four collapsed entry points and two expanded search views, and
// Compose
// models each as its own composable — so each is a component. The foldable axis inside a collapsed
// bar is the input field's CONTENT: an empty placeholder, a typed query with a clear affordance, or
// an account avatar in the trailing slot.
//
// The two expanded views are hosted in their own platform window (`ExpandedDockedSearchBar` in a
// Popup, `ExpandedFullScreenSearchBar` in a Dialog), which a single-surface capture cannot reach.
// Per AGENTS.md their CONTAINERS are composed from `SearchBarDefaults` — `dockedShape` /
// `fullScreenShape` and `colors()` — so the sticker shows the real shape, colour and elevation
// rather than a hand-drawn lookalike.

private val SUGGESTIONS =
  listOf(
    Res.string.search_suggestion_sticker_sheet,
    Res.string.search_suggestion_symbols,
    Res.string.search_suggestion_motion,
  )

@Composable private fun searchContent(): String = previewOverrideString("content", "placeholder")

@Composable private fun searchQuery(): String = if (searchContent() == "query") "material" else ""

@Composable
private fun searchPlaceholder(): (@Composable () -> Unit)? =
  if (searchContent() == "query") null else ({ Text(stringResource(Res.string.search_hint)) })

@Composable
private fun searchTrailing(): (@Composable () -> Unit)? =
  when (searchContent()) {
    "query" -> ({
        val c = counted("clear")
        IconButton(onClick = c.onClick) {
          Icon(Icons.Filled.Close, contentDescription = stringResource(Res.string.action_clear))
        }
      })
    "avatar" -> ({
        Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = CircleShape) {
          Icon(
            Icons.Filled.Person,
            contentDescription = stringResource(Res.string.label_account),
            modifier = Modifier.padding(4.dp).size(22.dp),
          )
        }
      })
    else -> null
  }

@CatalogComponent(
  id = "Search/Bar",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/52977:33948",
  caption = "The collapsed entry point, floating over content. Query and avatar states fold in.",
)
@CatalogModes
@OverrideVariant(name = "query", strings = ["content=query"])
@OverrideVariant(name = "avatar", strings = ["content=avatar"])
@Composable
fun SearchBarSticker() = Sticker {
  val state = rememberSearchBarState(SearchBarValue.Collapsed)
  val text = rememberTextFieldState(searchQuery())
  SearchBar(
    state = state,
    inputField = {
      SearchBarDefaults.InputField(
        textFieldState = text,
        searchBarState = state,
        onSearch = {},
        placeholder = searchPlaceholder(),
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        trailingIcon = searchTrailing(),
      )
    },
    modifier = Modifier.width(360.dp),
  )
}

@CatalogComponent(
  id = "Search/Docked",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/52977:33948",
  caption =
    "The collapsed bar anchored in the layout rather than floating; expands into a dropdown.",
)
@CatalogModes
@OverrideVariant(name = "query", strings = ["content=query"])
@OverrideVariant(name = "avatar", strings = ["content=avatar"])
@Composable
fun DockedSearchBarSticker() = Sticker {
  // The sibling bars own their query through `rememberTextFieldState`; this one takes the older
  // `query` / `onQueryChange` pair, so it owns the same state through `editable` rather than
  // dropping every keystroke. The dropdown is the bar's own expansion rather than a popup window,
  // so the live lane can open it — the baked capture stays collapsed on the seeded state.
  val (query, setQuery) = editable(searchQuery())
  val (expanded, setExpanded) = toggleable(false)
  DockedSearchBar(
    inputField = {
      SearchBarDefaults.InputField(
        query = query,
        onQueryChange = setQuery,
        onSearch = {},
        expanded = expanded,
        onExpandedChange = setExpanded,
        placeholder = searchPlaceholder(),
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        trailingIcon = searchTrailing(),
      )
    },
    expanded = expanded,
    onExpandedChange = setExpanded,
    modifier = Modifier.width(360.dp),
    content = { SuggestionRows() },
  )
}

@CatalogComponent(
  id = "Search/TopBar",
  caption =
    "The search bar used as the top app bar itself, with the window insets a top bar takes.",
  noReference =
    "the kit's Search bar is this component without the window insets TopSearchBar carries, " +
      "so pointing at it would report the insets as a height defect",
)
@CatalogModes
@OverrideVariant(name = "query", strings = ["content=query"])
@OverrideVariant(name = "avatar", strings = ["content=avatar"])
@Composable
fun TopSearchBarSticker() = Sticker {
  val state = rememberSearchBarState(SearchBarValue.Collapsed)
  val text = rememberTextFieldState(searchQuery())
  TopSearchBar(
    state = state,
    inputField = {
      SearchBarDefaults.InputField(
        textFieldState = text,
        searchBarState = state,
        onSearch = {},
        placeholder = searchPlaceholder(),
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        trailingIcon = searchTrailing(),
      )
    },
    modifier = Modifier.width(400.dp),
  )
}

@CatalogComponent(
  id = "Search/AppBar",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58114:20571",
  caption = "A top app bar that carries a search field between its navigation icon and its actions.",
)
@CatalogModes
@OverrideVariant(name = "query", strings = ["content=query"])
@OverrideVariant(name = "avatar", strings = ["content=avatar"])
@Composable
fun AppBarWithSearchSticker() = Sticker {
  val state = rememberSearchBarState(SearchBarValue.Collapsed)
  val text = rememberTextFieldState(searchQuery())
  val menu = counted("menu")
  val overflow = counted("overflow")
  AppBarWithSearch(
    state = state,
    inputField = {
      SearchBarDefaults.InputField(
        textFieldState = text,
        searchBarState = state,
        onSearch = {},
        placeholder = searchPlaceholder(),
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        trailingIcon = searchTrailing(),
      )
    },
    modifier = Modifier.width(400.dp),
    navigationIcon = {
      IconButton(onClick = menu.onClick) {
        Icon(Icons.Filled.Menu, contentDescription = stringResource(Res.string.action_menu))
      }
    },
    actions = {
      IconButton(onClick = overflow.onClick) {
        Icon(Icons.Filled.MoreVert, contentDescription = stringResource(Res.string.action_more))
      }
    },
  )
}

@CatalogComponent(
  id = "Search/ExpandedDocked",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/59178:4993",
  caption = "The docked search view: field plus suggestion list, in the dropdown's own shape.",
)
@CatalogModes
@Composable
fun ExpandedDockedSearchBarSticker() = Sticker {
  // `ExpandedDockedSearchBar` renders into a Popup, so the container is composed here from the same
  // `SearchBarDefaults` the real component uses.
  val colors = SearchBarDefaults.colors()
  Surface(
    shape = SearchBarDefaults.dockedShape,
    color = colors.containerColor,
    tonalElevation = SearchBarDefaults.TonalElevation,
    shadowElevation = SearchBarDefaults.ShadowElevation,
    modifier = Modifier.width(360.dp),
  ) {
    Column {
      ExpandedInputField()
      SuggestionRows()
    }
  }
}

@CatalogComponent(
  id = "Search/ExpandedFullScreen",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/59178:4964",
  caption = "The full-screen search view, where results take the whole surface.",
)
@CatalogModes
@Composable
fun ExpandedFullScreenSearchBarSticker() = Sticker {
  // `ExpandedFullScreenSearchBar` renders into a Dialog; the container is composed from
  // `SearchBarDefaults.fullScreenShape` so the sticker shows the real surface, not a lookalike.
  val colors = SearchBarDefaults.colors()
  Surface(
    shape = SearchBarDefaults.fullScreenShape,
    color = colors.containerColor,
    modifier = Modifier.width(360.dp).height(320.dp),
  ) {
    Column {
      ExpandedInputField()
      SuggestionRows()
    }
  }
}

/**
 * The field both expanded views carry, seeded with a typed query. The query is [editable] rather
 * than a constant with a dropped `onQueryChange`, so the live lane types into the view the way the
 * real expanded search bar does; the baked capture is frozen on the seed.
 */
@Composable
private fun ExpandedInputField() {
  val (query, setQuery) = editable("material")
  val back = counted("back")
  SearchBarDefaults.InputField(
    query = query,
    onQueryChange = setQuery,
    onSearch = {},
    expanded = true,
    onExpandedChange = {},
    modifier = Modifier.fillMaxWidth(),
    leadingIcon = {
      IconButton(onClick = back.onClick) {
        Icon(
          Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = stringResource(Res.string.action_back),
        )
      }
    },
  )
}

@Composable
private fun SuggestionRows() {
  for (suggestion in SUGGESTIONS) {
    val row = counted(stringResource(suggestion))
    ListItem(
      headlineContent = { Text(row.label) },
      leadingContent = { Icon(Icons.Filled.Search, contentDescription = null) },
      modifier = Modifier.fillMaxWidth().clickable(onClick = row.onClick),
    )
  }
}
