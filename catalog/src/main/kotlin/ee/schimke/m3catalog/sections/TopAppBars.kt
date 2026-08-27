@file:CatalogGroup(name = "Top app bar", section = "Navigation")
@file:OptIn(ExperimentalMaterial3Api::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Interests
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.overrides.previewOverrideBoolean
import ee.schimke.composeai.overrides.previewOverrideString
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogImagePlaceholder
import ee.schimke.m3catalog.CatalogModesCompact
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogChoice
import ee.schimke.m3catalog.counted
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.action_back
import ee.schimke.m3catalog.generated.resources.action_menu
import ee.schimke.m3catalog.generated.resources.action_more
import ee.schimke.m3catalog.generated.resources.action_search
import ee.schimke.m3catalog.generated.resources.appbar_title
import ee.schimke.m3catalog.generated.resources.label_account
import org.jetbrains.compose.resources.stringResource

// One component. The kit's `App bar` set varies `Configuration` x `Elevation`, and `Configuration`
// carries the four sizes alongside the centred, image and search layouts — a size/layout axis, not
// an emphasis one, so it folds rather than standing as four cards (`AGENTS.md`; emphasis is the
// only carve-out, and these bars have none). Each size is its own composable, which is a fact about
// the Compose API rather than about the taxonomy.
//
// `Small` is the parent because it is the kit's own base `Configuration`. The nav icon and action
// count fold in as knob cells on top, and they cross with the size: an `@OverrideVariant` on a
// `@CatalogVariant` projects since compose-ai-tools#4081, so a folded bar keeps its own matrix.
//
// `Search/AppBar` is the `Configuration=Search` cell of this same set but lives in the Search group
// with the other search entry points; folding it would move it across groups, which is a separate
// decision and not made here. See issue #128.

/**
 * Whether the bar draws a navigation icon — the `nav` knob the `no-nav` cells seed.
 *
 * Read through this rather than inline, because every bar in the file has to answer the seed the
 * same way. `CenterTopAppBar` used to hardcode both slots while still declaring `no-nav` and
 * `no-actions`, so those two cells published the default render under their own names (issue #176).
 */
@Composable private fun navEnabled(): Boolean = previewOverrideBoolean("nav", true)

/** How many trailing actions the bar draws — the `actions` knob the action cells seed. */
@Composable
private fun actionCount(): Int = previewOverrideString("actions", "1").toIntOrNull() ?: 1

@Composable
private fun NavIcon(): (@Composable () -> Unit)? {
  val c = counted(stringResource(Res.string.action_back))
  if (!navEnabled()) return null
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
  val count = actionCount()
  return {
    if (count >= 1) {
      IconButton(onClick = search.onClick) {
        Icon(Icons.Filled.Search, contentDescription = search.label)
      }
    }
    if (count >= 2) {
      IconButton(onClick = more.onClick) {
        Icon(Icons.Filled.MoreVert, contentDescription = more.label)
      }
    }
  }
}

private const val W = 412

@CatalogComponent(
  id = "TopAppBar/Small",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58114:20585",
  caption =
    "The app bar. Its four sizes, the centred and image layouts, nav icon and action count fold in.",
)
@CatalogModesCompact
@OverrideVariant(name = "no-nav", booleans = ["nav=false"])
@OverrideVariant(name = "no-actions", strings = ["actions=0"])
@OverrideVariant(name = "two-actions", strings = ["actions=2"])
@OverrideVariant(name = "on-scroll", strings = ["elevation=on-scroll"])
@OverrideVariant(name = "small-image", strings = ["content=small-image"])
@Composable
fun SmallTopAppBar() = Sticker {
  val nav = NavIcon()
  TopAppBar(
    // The kit's `Configuration=Small-image` puts an image where the title goes. What that node
    // actually shows is Figma's placeholder graphic, which this catalog already draws from
    // `MaterialShapes` for its carousel items — so the cell reuses that rather than carrying the
    // kit's raster into the repo, and it themes and scales as a consequence.
    title = {
      if (catalogChoice("content", "title", "title", "small-image") == "small-image") {
        CatalogImagePlaceholder(
          Modifier.fillMaxWidth().height(44.dp),
          shape = RoundedCornerShape(8.dp),
          scaleBasis = 122f,
        )
      } else {
        Text(stringResource(Res.string.appbar_title))
      }
    },
    navigationIcon = nav ?: {},
    actions = Actions(),
    // The kit's `Elevation` axis is not a shadow on this component — it is the container colour a
    // bar takes once content has scrolled under it. A `TopAppBarScrollBehavior` seeded with an
    // offset was the first attempt and rendered nothing: the colour transition is driven by a
    // scroll this lane cannot make. So the cell paints `TopAppBarDefaults`' OWN scrolled colour,
    // which is the value that behaviour would have reached rather than one picked here.
    colors = catalogAppBarColors(),
    modifier = Modifier.width(W.dp).height(64.dp),
  )
}

/**
 * A pinned scroll behaviour reporting content already scrolled under the bar, or null for the
 * resting bar. Deterministic: the offset is seeded rather than produced by a gesture the baked lane
 * cannot make.
 */
@Composable
private fun catalogAppBarColors() =
  TopAppBarDefaults.topAppBarColors(
    containerColor =
      if (catalogChoice("elevation", "flat", "flat", "on-scroll") == "on-scroll")
        TopAppBarDefaults.topAppBarColors().scrolledContainerColor
      else androidx.compose.ui.graphics.Color.Transparent
  )

@CatalogVariant(
  of = "TopAppBar/Small",
  props = ["configuration=small-centered"],
  caption = "Title centred; one action at most.",
)
@CatalogModesCompact
@OverrideVariant(name = "no-nav", booleans = ["nav=false"])
@OverrideVariant(name = "no-actions", strings = ["actions=0"])
@Composable
fun CenterTopAppBar() = Sticker {
  val menu = counted(stringResource(Res.string.action_menu))
  val account = counted(stringResource(Res.string.label_account))
  // The centred bar keeps its own slot CONTENT — the kit draws it with a menu icon and an avatar
  // rather than the parent's back arrow and search — but reads the same two knobs, so `no-nav` and
  // `no-actions` mean here what they mean on every other bar in the file.
  val nav = navEnabled()
  val actions = actionCount()
  CenterAlignedTopAppBar(
    title = { Text(stringResource(Res.string.appbar_title)) },
    navigationIcon = {
      if (nav) {
        IconButton(onClick = menu.onClick) {
          Icon(Icons.Filled.Menu, contentDescription = menu.label)
        }
      }
    },
    actions = {
      if (actions >= 1) {
        IconButton(onClick = account.onClick) {
          Surface(
            modifier = Modifier.size(32.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
          ) {
            Icon(
              Icons.Filled.Interests,
              contentDescription = account.label,
              tint = MaterialTheme.colorScheme.outlineVariant,
              modifier = Modifier.padding(4.dp),
            )
          }
        }
      }
    },
    colors =
      TopAppBarDefaults.topAppBarColors(
        containerColor = androidx.compose.ui.graphics.Color.Transparent
      ),
    modifier = Modifier.width(W.dp).height(64.dp),
  )
}

@CatalogVariant(
  of = "TopAppBar/Small",
  props = ["configuration=medium"],
  caption = "Two rows; title below the actions.",
)
@CatalogModesCompact
@OverrideVariant(name = "two-actions", strings = ["actions=2"])
@Composable
fun MediumTopAppBarSticker() = Sticker {
  val nav = NavIcon()
  MediumTopAppBar(
    title = { Text(stringResource(Res.string.appbar_title)) },
    navigationIcon = nav ?: {},
    actions = Actions(),
    colors =
      TopAppBarDefaults.topAppBarColors(
        containerColor = androidx.compose.ui.graphics.Color.Transparent
      ),
    modifier = Modifier.width(W.dp).height(112.dp),
  )
}

@CatalogVariant(
  of = "TopAppBar/Small",
  props = ["configuration=large"],
  caption = "The tallest form, for a prominent headline.",
)
@CatalogModesCompact
@OverrideVariant(name = "two-actions", strings = ["actions=2"])
@Composable
fun LargeTopAppBarSticker() = Sticker {
  val nav = NavIcon()
  LargeTopAppBar(
    title = { Text(stringResource(Res.string.appbar_title)) },
    navigationIcon = nav ?: {},
    actions = Actions(),
    colors =
      TopAppBarDefaults.topAppBarColors(
        containerColor = androidx.compose.ui.graphics.Color.Transparent
      ),
    modifier = Modifier.width(W.dp).height(120.dp),
  )
}
