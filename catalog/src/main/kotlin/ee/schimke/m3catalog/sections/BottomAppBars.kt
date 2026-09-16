@file:CatalogGroup(name = "Bottom app bar", section = "Navigation")
@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModesCompact
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.counted
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.action_add
import ee.schimke.m3catalog.generated.resources.action_check
import ee.schimke.m3catalog.generated.resources.action_edit
import ee.schimke.m3catalog.generated.resources.action_more
import org.jetbrains.compose.resources.stringResource

// The kit's axes: the FAB slot, and how many actions sit beside it (two to four).
//
// NO content padding is passed, which is the fix rather than an omission. `Icons=3` (51159:5115)
// puts its `leading-icon` frame at x=4 and its FAB at 340..396 x 12..68, and that is exactly what
// `BottomAppBarDefaults.ContentPadding` produces — measured. The sticker used to pass
// `PaddingValues(start = 4.dp, end = 16.dp)`, which looks like the kit's insets and is not: it
// displaced the FAB 12dp inwards and, by replacing the whole `PaddingValues`, dropped the
// component's own `BottomAppBarVerticalPadding`, lifting the FAB 4dp off the node's top edge.
//
// What the component does NOT give is the kit's 8dp between action buttons: its `leading-icon`
// frame is a `gap: 8` row, so the kit centres three icons on 28 / 84 / 140 where `BottomAppBar`
// packs them on 28 / 76 / 124. That gap is left OPEN rather than padded around — AndroidX's own
// `BottomAppBarWithFAB` and `ExitAlwaysBottomAppBar` samples pack their `IconButton`s the same
// way, so spacers here would publish a bar idiomatic Compose does not produce and hide the
// difference from the parity diff. Recorded in #455, the way Toolbars.kt records the floating
// toolbar's unapplied `ContainerBetweenSpace`.

@CatalogComponent(
  id = "BottomAppBar/Standard",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/51159:5115",
  caption = "Screen-level actions along the bottom. FAB and action count fold in.",
)
@CatalogModesCompact
@OverrideVariant(name = "no-fab", booleans = ["fab=false"])
@OverrideVariant(name = "two-actions", ints = ["actions=2"])
@OverrideVariant(name = "four-actions", ints = ["actions=4"])
@OverrideVariant(name = "four-actions-no-fab", ints = ["actions=4"], booleans = ["fab=false"])
@Composable
fun BottomAppBarSticker(actions: Int = 3, fab: Boolean = true) = Sticker {
  val check = counted(stringResource(Res.string.action_check))
  val edit = counted(stringResource(Res.string.action_edit))
  val more = counted(stringResource(Res.string.action_more))
  val add = counted(stringResource(Res.string.action_add))
  val count = actions
  BottomAppBar(
    modifier = Modifier.width(412.dp),
    actions = {
      // Outlined rather than filled for the bin and the download tray: the kit's symbols are
      // unfilled at `on-surface-variant`, and the filled bin in particular read as a much heavier
      // mark than the node it is compared against. `Search` and `Add` are strokes in both sets.
      IconButton(onClick = check.onClick) {
        Icon(Icons.Filled.Search, contentDescription = check.label)
      }
      if (count >= 2) {
        IconButton(onClick = edit.onClick) {
          Icon(Icons.Outlined.Delete, contentDescription = edit.label)
        }
      }
      if (count >= 3) {
        IconButton(onClick = more.onClick) {
          Icon(Icons.Outlined.Download, contentDescription = more.label)
        }
      }
      if (count >= 4) {
        IconButton(onClick = add.onClick) { Icon(Icons.Filled.Add, contentDescription = add.label) }
      }
    },
    floatingActionButton =
      if (fab) {
        {
          FloatingActionButton(onClick = add.onClick) {
            Icon(Icons.Filled.Add, contentDescription = add.label)
          }
        }
      } else null,
  )
}
