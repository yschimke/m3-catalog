@file:CatalogGroup(name = "Top app bar", section = "Navigation")
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import ee.schimke.m3catalog.counted
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.action_back
import ee.schimke.m3catalog.generated.resources.action_more
import ee.schimke.m3catalog.generated.resources.action_search
import ee.schimke.m3catalog.generated.resources.appbar_subtitle
import ee.schimke.m3catalog.generated.resources.appbar_title
import org.jetbrains.compose.resources.stringResource

// Four sizes, each its own composable, plus the expressive "flexible" pair. Within each, the kit's
// foldable axes are the navigation icon and the action count.

@Composable
private fun NavIcon(): (@Composable () -> Unit)? {
  val c = counted(stringResource(Res.string.action_back))
  if (!previewOverrideBoolean("nav", true)) return null
  return {
    IconButton(onClick = c.onClick) {
      Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = c.label)
    }
  }
}

@Composable
private fun Actions(): @Composable androidx.compose.foundation.layout.RowScope.() -> Unit {
  val more = counted(stringResource(Res.string.action_more))
  val search = counted(stringResource(Res.string.action_search))
  val count = previewOverrideString("actions", "1").toIntOrNull() ?: 1
  return {
    if (count >= 2) {
      IconButton(onClick = search.onClick) {
        Icon(Icons.Filled.Search, contentDescription = search.label)
      }
    }
    if (count >= 1) {
      IconButton(onClick = more.onClick) {
        Icon(Icons.Filled.MoreVert, contentDescription = more.label)
      }
    }
  }
}

private const val W = 360

@CatalogComponent(
  id = "TopAppBar/Small",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58114:20585",
  caption = "The default; title beside the actions. Nav icon and action count fold in.",
)
@CatalogModes
@OverrideVariant(name = "no-nav", booleans = ["nav=false"])
@OverrideVariant(name = "no-actions", strings = ["actions=0"])
@OverrideVariant(name = "two-actions", strings = ["actions=2"])
@Composable
fun SmallTopAppBar() = Sticker {
  val nav = NavIcon()
  TopAppBar(
    title = { Text(stringResource(Res.string.appbar_title)) },
    navigationIcon = nav ?: {},
    actions = Actions(),
    modifier = Modifier.width(W.dp),
  )
}

@CatalogComponent(
  id = "TopAppBar/CenterAligned",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58114:20566",
  caption = "Title centred; one action at most.",
)
@CatalogModes
@OverrideVariant(name = "no-nav", booleans = ["nav=false"])
@OverrideVariant(name = "no-actions", strings = ["actions=0"])
@Composable
fun CenterTopAppBar() = Sticker {
  val nav = NavIcon()
  CenterAlignedTopAppBar(
    title = { Text(stringResource(Res.string.appbar_title)) },
    navigationIcon = nav ?: {},
    actions = Actions(),
    modifier = Modifier.width(W.dp),
  )
}

@CatalogComponent(
  id = "TopAppBar/Medium",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58114:20592",
  caption = "Two rows; title below the actions.",
)
@CatalogModes
@OverrideVariant(name = "two-actions", strings = ["actions=2"])
@Composable
fun MediumTopAppBarSticker() = Sticker {
  val nav = NavIcon()
  MediumTopAppBar(
    title = { Text(stringResource(Res.string.appbar_title)) },
    navigationIcon = nav ?: {},
    actions = Actions(),
    modifier = Modifier.width(W.dp),
  )
}

@CatalogComponent(
  id = "TopAppBar/Large",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58114:20600",
  caption = "The tallest form, for a prominent headline.",
)
@CatalogModes
@OverrideVariant(name = "two-actions", strings = ["actions=2"])
@Composable
fun LargeTopAppBarSticker() = Sticker {
  val nav = NavIcon()
  LargeTopAppBar(
    title = { Text(stringResource(Res.string.appbar_title)) },
    navigationIcon = nav ?: {},
    actions = Actions(),
    modifier = Modifier.width(W.dp),
  )
}

@CatalogComponent(
  id = "TopAppBar/MediumFlexible",
  caption = "The expressive two-row bar, with a subtitle slot.",
)
@CatalogModes
@Composable
fun MediumFlexibleTopAppBarSticker() = Sticker {
  val nav = NavIcon()
  MediumFlexibleTopAppBar(
    title = { Text(stringResource(Res.string.appbar_title)) },
    subtitle = { Text(stringResource(Res.string.appbar_subtitle)) },
    navigationIcon = nav ?: {},
    actions = Actions(),
    modifier = Modifier.width(W.dp),
  )
}

@CatalogComponent(
  id = "TopAppBar/LargeFlexible",
  caption = "The expressive tall bar, with a subtitle slot.",
)
@CatalogModes
@Composable
fun LargeFlexibleTopAppBarSticker() = Sticker {
  val nav = NavIcon()
  LargeFlexibleTopAppBar(
    title = { Text(stringResource(Res.string.appbar_title)) },
    subtitle = { Text(stringResource(Res.string.appbar_subtitle)) },
    navigationIcon = nav ?: {},
    actions = Actions(),
    modifier = Modifier.width(W.dp),
  )
}
