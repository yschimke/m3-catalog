@file:CatalogGroup(name = "Navigation drawer", section = "Navigation")

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.DismissibleDrawerSheet
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.overrides.previewOverrideBoolean
import ee.schimke.composeai.overrides.previewOverrideString
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.label_mail
import ee.schimke.m3catalog.generated.resources.nav_home
import ee.schimke.m3catalog.generated.resources.nav_saved
import ee.schimke.m3catalog.generated.resources.nav_search
import ee.schimke.m3catalog.generated.resources.nav_you
import ee.schimke.m3catalog.selectable
import org.jetbrains.compose.resources.stringResource

// The drawer SHEET is the component; `ModalNavigationDrawer` is the host that slides it in, and a
// sticker of the host at rest would be an empty screen. So the sheets are composed directly — and
// the kit's three kinds (modal / dismissible / permanent) are three sheet composables, hence three
// components rather than knob cells.
//
// Foldable within each: the headline, section dividers, and per-item badges.

private val DRAWER =
  listOf(
    Res.string.nav_home to Icons.Filled.Home,
    Res.string.nav_search to Icons.Filled.Search,
    Res.string.nav_you to Icons.Filled.Person,
    Res.string.nav_saved to Icons.Filled.Favorite,
  )

@Composable
private fun DrawerItems() {
  val headline = previewOverrideBoolean("headline", true)
  val dividers = previewOverrideBoolean("dividers", false)
  val badge = previewOverrideBoolean("badge", false)
  val count = previewOverrideString("count", "3").toIntOrNull() ?: 3
  val (selected, select) = selectable(0)
  if (headline) {
    Text(
      stringResource(Res.string.label_mail),
      Modifier.padding(16.dp),
      style = MaterialTheme.typography.titleSmall,
    )
  }
  DRAWER.take(count).forEachIndexed { index, (label, icon) ->
    if (dividers && index == 2) HorizontalDivider(Modifier.padding(vertical = 8.dp))
    NavigationDrawerItem(
      label = { Text(stringResource(label)) },
      icon = { Icon(icon, contentDescription = null) },
      badge = if (badge && index == 1) ({ Badge { Text("3") } }) else null,
      selected = index == selected,
      onClick = { select(index) },
      modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
    )
  }
}

@CatalogComponent(
  id = "NavigationDrawer/Modal",
  caption = "Slides over the content. Headline, dividers, badges and count fold in.",
  noReference =
    "the kit files its only drawer under Navigation / Deprecated and publishes no replacement; nothing else in the file matches",
)
@CatalogModes
@OverrideVariant(name = "no-headline", booleans = ["headline=false"])
@OverrideVariant(name = "dividers", booleans = ["dividers=true"])
@OverrideVariant(name = "badge", booleans = ["badge=true"])
@OverrideVariant(name = "four", strings = ["count=4"])
@OverrideVariant(
  name = "four-dividers-badge",
  strings = ["count=4"],
  booleans = ["dividers=true", "badge=true"],
)
@Composable
fun ModalDrawerSheetSticker() = Sticker { ModalDrawerSheet { DrawerItems() } }

@CatalogComponent(
  id = "NavigationDrawer/Dismissible",
  caption = "Sits beside the content and can be pushed away.",
  noReference =
    "the kit files its only drawer under Navigation / Deprecated and publishes no replacement; nothing else in the file matches",
)
@CatalogModes
@OverrideVariant(name = "dividers", booleans = ["dividers=true"])
@Composable
fun DismissibleDrawerSheetSticker() = Sticker { DismissibleDrawerSheet { DrawerItems() } }

@CatalogComponent(
  id = "NavigationDrawer/Permanent",
  caption = "Always visible on expanded widths.",
  noReference =
    "the kit files its only drawer under Navigation / Deprecated and publishes no replacement; nothing else in the file matches",
)
@CatalogModes
@OverrideVariant(name = "dividers", booleans = ["dividers=true"])
@Composable
fun PermanentDrawerSheetSticker() = Sticker { PermanentDrawerSheet { DrawerItems() } }
