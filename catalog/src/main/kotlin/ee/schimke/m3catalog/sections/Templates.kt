@file:CatalogGroup(name = "Scaffold templates", section = "Templates")
@file:OptIn(ExperimentalMaterial3Api::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.m3catalog.CatalogTemplate
import ee.schimke.m3catalog.FullScreenM3
import ee.schimke.m3catalog.SYSTEM_BAR_INSET
import ee.schimke.m3catalog.counted
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.action_menu
import ee.schimke.m3catalog.generated.resources.action_new
import ee.schimke.m3catalog.generated.resources.template_row_captions
import ee.schimke.m3catalog.generated.resources.template_row_parity
import ee.schimke.m3catalog.generated.resources.template_row_pipeline
import ee.schimke.m3catalog.generated.resources.template_row_spec
import ee.schimke.m3catalog.generated.resources.template_row_tokens
import ee.schimke.m3catalog.generated.resources.template_title
import org.jetbrains.compose.resources.stringResource

// Beyond the per-component stickers, the catalog ships full-screen **templates**: screen skeletons
// an app copies whole. They sit on a real device frame with `showSystemUi = true`, so the renderer
// paints the OS status bar and gesture pill around them, and the scaffold reserves
// `SYSTEM_BAR_INSET` top and bottom so its own chrome clears that overlay — a real edge-to-edge M3
// scaffold, not an inset band.
//
// `Template/AppScaffold` is the catalog's declared **hero** (`display.hero` in catalog.spec.json):
// the preview server features it on the system's card. A hero must be a screen — the fallback
// otherwise settles on a lone filled button, which is true to the inventory and useless as a shop
// window.

// The sender names stay literals — a person's name is not copy to translate; the message
// beside each one is.
private val ROWS =
  listOf(
    "Alice" to Res.string.template_row_tokens,
    "Bala" to Res.string.template_row_pipeline,
    "Chen" to Res.string.template_row_parity,
    "Dara" to Res.string.template_row_captions,
    "Emre" to Res.string.template_row_spec,
  )

@CatalogComponent(
  id = "Template/AppScaffold",
  caption = "Top app bar, list and FAB — the canonical full-screen M3 layout.",
  noReference =
    "the kit's Examples page holds finished screens (Home-Mobile, Library-Mobile), not a " +
      "scaffold component; pairing this with one of them would compare against that screen's " +
      "content rather than the layout",
)
@CatalogTemplate
@Composable
fun AppScaffoldTemplate() = FullScreenM3 {
  val menu = counted(stringResource(Res.string.action_menu))
  val add = counted(stringResource(Res.string.action_new))
  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(stringResource(Res.string.template_title)) },
        navigationIcon = {
          IconButton(onClick = menu.onClick) {
            Icon(Icons.Filled.Menu, contentDescription = menu.label)
          }
        },
        windowInsets = WindowInsets(top = SYSTEM_BAR_INSET),
      )
    },
    floatingActionButton = {
      FloatingActionButton(onClick = add.onClick) {
        Icon(Icons.Filled.Add, contentDescription = add.label)
      }
    },
    contentWindowInsets = WindowInsets(bottom = SYSTEM_BAR_INSET),
    modifier = Modifier.fillMaxSize(),
  ) { padding ->
    LazyColumn(Modifier.padding(padding)) {
      itemsIndexed(ROWS) { index, (name, message) ->
        if (index > 0) HorizontalDivider()
        ListItem(
          headlineContent = { Text(name) },
          supportingContent = { Text(stringResource(message)) },
          leadingContent = { Icon(Icons.Filled.Person, contentDescription = null) },
        )
      }
    }
  }
}
