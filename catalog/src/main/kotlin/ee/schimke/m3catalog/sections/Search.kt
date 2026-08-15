@file:CatalogGroup(name = "Search", section = "Navigation")
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Interests
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AppBarWithSearch
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes360
import ee.schimke.m3catalog.CatalogModes412
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogChoice
import ee.schimke.m3catalog.counted
import ee.schimke.m3catalog.editable
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.action_back
import ee.schimke.m3catalog.generated.resources.action_clear
import ee.schimke.m3catalog.generated.resources.action_menu
import ee.schimke.m3catalog.generated.resources.label_account
import ee.schimke.m3catalog.generated.resources.list_supporting
import ee.schimke.m3catalog.generated.resources.search_app_hint
import ee.schimke.m3catalog.generated.resources.search_hint
import ee.schimke.m3catalog.generated.resources.search_input_text
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

@Composable
private fun searchContent(): String =
  catalogChoice("content", "placeholder", "placeholder", "query", "avatar")

@Composable private fun searchQuery(): String = if (searchContent() == "query") "material" else ""

@Composable
private fun searchPlaceholder(): (@Composable () -> Unit)? =
  if (searchContent() == "query") null else ({ Text(stringResource(Res.string.search_hint)) })

@Composable
private fun searchLeading(): @Composable () -> Unit = {
  Icon(Icons.Filled.Menu, contentDescription = stringResource(Res.string.action_menu))
}

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
    else -> ({ Icon(Icons.Filled.Search, contentDescription = null) })
  }

@CatalogComponent(
  id = "Search/Bar",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/52977:33948",
  caption = "The collapsed entry point, floating over content. Query and avatar states fold in.",
)
@CatalogModes360
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
        leadingIcon = searchLeading(),
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
@CatalogModes360
@OverrideVariant(name = "query", strings = ["content=query"])
@OverrideVariant(name = "avatar", strings = ["content=avatar"])
@Composable
fun DockedSearchBarSticker() = Sticker {
  // The sibling bars own their query through `rememberTextFieldState`; this one takes the older
  // `query` / `onQueryChange` pair, so it owns the same state through `editable` rather than
  // dropping every keystroke. The dropdown is the bar's own expansion rather than a popup window,
  // so the live lane can open it — the baked capture stays collapsed on the seeded state.
  var query by editable(searchQuery())
  var expanded by toggleable(false)
  DockedSearchBar(
    inputField = {
      SearchBarDefaults.InputField(
        query = query,
        onQueryChange = { query = it },
        onSearch = {},
        expanded = expanded,
        onExpandedChange = { expanded = it },
        placeholder = searchPlaceholder(),
        leadingIcon = searchLeading(),
        trailingIcon = searchTrailing(),
      )
    },
    expanded = expanded,
    onExpandedChange = { expanded = it },
    modifier = Modifier.width(360.dp),
    content = { SuggestionRows() },
  )
}

@CatalogComponent(
  id = "Search/AppBar",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58114:20571",
  caption =
    "A top app bar that carries a search field between its navigation icon and its actions.",
)
@CatalogModes412
@OverrideVariant(name = "query", strings = ["content=query"])
@OverrideVariant(name = "avatar", strings = ["content=avatar"])
@Composable
fun AppBarWithSearchSticker() = Sticker {
  val state = rememberSearchBarState(SearchBarValue.Collapsed)
  val text = rememberTextFieldState(searchQuery())
  val menu = counted("menu")
  AppBarWithSearch(
    state = state,
    inputField = {
      SearchBarDefaults.InputField(
        textFieldState = text,
        searchBarState = state,
        onSearch = {},
        placeholder = { Text(stringResource(Res.string.search_app_hint)) },
        leadingIcon = null,
        trailingIcon = null,
      )
    },
    modifier = Modifier.width(412.dp).height(64.dp),
    navigationIcon = {
      IconButton(onClick = menu.onClick) {
        Icon(Icons.Filled.Menu, contentDescription = stringResource(Res.string.action_menu))
      }
    },
    actions = {
      Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = CircleShape) {
        Icon(
          Icons.Filled.Interests,
          contentDescription = stringResource(Res.string.label_account),
          modifier = Modifier.padding(4.dp).size(22.dp),
        )
      }
    },
  )
}

@CatalogComponent(
  id = "Search/ExpandedDocked",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/59178:4993",
  caption = "The docked search view: field plus suggestion list, in the dropdown's own shape.",
)
@CatalogModes360
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
    modifier = Modifier.width(360.dp).height(250.dp),
  ) {
    Column {
      ExpandedInputField(fullScreen = false)
      SuggestionRows(avatars = false)
    }
  }
}

@CatalogComponent(
  id = "Search/ExpandedFullScreen",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/59178:4964",
  referenceContentsOnly = false,
  caption = "The full-screen search view, where results take the whole surface.",
)
@CatalogModes412
@Composable
fun ExpandedFullScreenSearchBarSticker() = Sticker {
  // `ExpandedFullScreenSearchBar` renders into a Dialog; the container is composed from
  // `SearchBarDefaults.fullScreenShape` so the sticker shows the real surface, not a lookalike.
  //
  // That shape is `RectangleShape` — a full-screen search view has no corners in Compose — while
  // the kit specs a 16dp corner on this node (issue #1). It is the only corner divergence that
  // survived that triage, and it is the library's value, not a catalog choice: hard-coding 16 here
  // would draw something `ExpandedFullScreenSearchBar` never draws. Recorded rather than silently
  // rendered, per AGENTS.md; `KitCornerRadiusTest` pins it so a library change is not missed.
  Surface(
    shape = SearchBarDefaults.fullScreenShape,
    color = MaterialTheme.colorScheme.surface,
    modifier = Modifier.width(412.dp).height(250.dp),
  ) {
    Column {
      ExpandedInputField(fullScreen = true)
      Column(Modifier.offset(y = (-6).dp)) { SuggestionRows(avatars = true) }
    }
  }
}

/**
 * The field both expanded views carry, seeded with a typed query. The query is [editable] rather
 * than a constant with a dropped `onQueryChange`, so the live lane types into the view the way the
 * real expanded search bar does; the baked capture is frozen on the seed.
 */
@Composable
private fun ExpandedInputField(fullScreen: Boolean) {
  var query by editable(stringResource(Res.string.search_input_text))
  val back = counted("back")
  val clear = counted("clear")
  val microphone = counted("microphone")
  SearchBarDefaults.InputField(
    query = query,
    onQueryChange = { query = it },
    onSearch = {},
    expanded = true,
    onExpandedChange = {},
    modifier =
      if (fullScreen)
        Modifier.fillMaxWidth()
          .padding(horizontal = 12.dp, vertical = 4.dp)
          .height(56.dp)
          .background(MaterialTheme.colorScheme.surfaceContainerHigh, CircleShape)
      else Modifier.fillMaxWidth(),
    leadingIcon = {
      IconButton(onClick = back.onClick) {
        Icon(
          Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = stringResource(Res.string.action_back),
        )
      }
    },
    trailingIcon = {
      Row {
        IconButton(onClick = clear.onClick) {
          Icon(Icons.Filled.Close, contentDescription = stringResource(Res.string.action_clear))
        }
        if (!fullScreen) {
          IconButton(onClick = microphone.onClick) {
            Icon(Icons.Filled.Mic, contentDescription = null)
          }
        }
      }
    },
  )
}

@Composable
private fun SuggestionRows(avatars: Boolean = false) {
  for (suggestion in SUGGESTIONS) {
    val row = counted(stringResource(suggestion))
    Row(
      modifier =
        Modifier.fillMaxWidth()
          .height(64.dp)
          .clickable(onClick = row.onClick)
          .padding(horizontal = if (avatars) 16.dp else 24.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      if (avatars) {
        Surface(
          modifier = Modifier.size(40.dp),
          shape = CircleShape,
          color = MaterialTheme.colorScheme.primaryContainer,
        ) {
          Icon(
            Icons.Filled.Interests,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier.padding(8.dp),
          )
        }
        Spacer(Modifier.width(12.dp))
      } else {
        Icon(
          Icons.Filled.Interests,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.outlineVariant,
        )
        Spacer(Modifier.width(20.dp))
      }
      Column {
        Text(row.label, style = MaterialTheme.typography.bodyLarge)
        Text(
          stringResource(Res.string.list_supporting),
          style = MaterialTheme.typography.bodyMedium,
        )
      }
    }
  }
}
