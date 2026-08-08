@file:CatalogGroup(name = "Navigation bar", section = "Navigation")
@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.ShortNavigationBar
import androidx.compose.material3.ShortNavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.overrides.previewOverrideBoolean
import ee.schimke.composeai.overrides.previewOverrideString
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.nav_home
import ee.schimke.m3catalog.generated.resources.nav_saved
import ee.schimke.m3catalog.generated.resources.nav_search
import ee.schimke.m3catalog.generated.resources.nav_settings
import ee.schimke.m3catalog.generated.resources.nav_you
import ee.schimke.m3catalog.selectable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

// The bar owns its selected destination, so a click on the live lane really moves the indicator.
//
// The kit's axes: DESTINATION COUNT (three to five), LABEL VISIBILITY (always / selected only /
// never), and an optional badge.

private val DESTINATIONS: List<Pair<StringResource, ImageVector>> =
  listOf(
    Res.string.nav_home to Icons.Filled.Home,
    Res.string.nav_search to Icons.Filled.Search,
    Res.string.nav_you to Icons.Filled.Person,
    Res.string.nav_saved to Icons.Filled.Favorite,
    Res.string.nav_settings to Icons.Filled.Settings,
  )

@CatalogComponent(
  id = "NavigationBar/Standard",
  caption = "Three to five top-level destinations. Count, labels and badges fold in.",
)
@CatalogModes
@OverrideVariant(name = "four", strings = ["count=4"])
@OverrideVariant(name = "five", strings = ["count=5"])
@OverrideVariant(name = "labels-selected", strings = ["labels=selected"])
@OverrideVariant(name = "labels-none", strings = ["labels=none"])
@OverrideVariant(name = "badge", booleans = ["badge=true"])
@OverrideVariant(name = "five-labels-none", strings = ["count=5", "labels=none"])
@Composable
fun NavigationBarSticker() = Sticker {
  val count = previewOverrideString("count", "3").toIntOrNull() ?: 3
  val labels = previewOverrideString("labels", "always")
  val badge = previewOverrideBoolean("badge", false)
  val (selected, select) = selectable(0)
  NavigationBar(Modifier.width(360.dp)) {
    DESTINATIONS.take(count).forEachIndexed { index, (label, icon) ->
      NavigationBarItem(
        selected = index == selected,
        onClick = { select(index) },
        icon = {
          if (badge && index == 1) {
            BadgedBox(badge = { Badge { Text("3") } }) { Icon(icon, contentDescription = null) }
          } else {
            Icon(icon, contentDescription = null)
          }
        },
        label = if (labels == "none") null else ({ Text(stringResource(label)) }),
        alwaysShowLabel = labels == "always",
      )
    }
  }
}

@CatalogComponent(
  id = "NavigationBar/Short",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58016:37260",
  caption = "The expressive compact bar. Count and labels fold in.",
)
@CatalogModes
@OverrideVariant(name = "four", strings = ["count=4"])
@OverrideVariant(name = "five", strings = ["count=5"])
@OverrideVariant(name = "labels-none", strings = ["labels=none"])
@Composable
fun ShortNavigationBarSticker() = Sticker {
  val count = previewOverrideString("count", "3").toIntOrNull() ?: 3
  val labels = previewOverrideString("labels", "always")
  val (selected, select) = selectable(0)
  ShortNavigationBar(Modifier.width(360.dp)) {
    DESTINATIONS.take(count).forEachIndexed { index, (label, icon) ->
      ShortNavigationBarItem(
        selected = index == selected,
        onClick = { select(index) },
        icon = { Icon(icon, contentDescription = null) },
        label = if (labels == "none") null else ({ Text(stringResource(label)) }),
      )
    }
  }
}
