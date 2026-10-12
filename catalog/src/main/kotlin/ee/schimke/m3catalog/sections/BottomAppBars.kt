@file:CatalogGroup(name = "Bottom app bar", section = "Navigation")
@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import generated.uibuilder.KitBottomAppBar
import generated.uibuilder.KitBottomAppBarNoFab
import generated.uibuilder.KitBottomAppBarOne
import generated.uibuilder.KitBottomAppBarOneNoFab
import generated.uibuilder.KitBottomAppBarThree
import generated.uibuilder.KitBottomAppBarThreeNoFab
import generated.uibuilder.KitBottomAppBarTwo
import generated.uibuilder.KitBottomAppBarTwoNoFab
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

// --- Generated sticker bodies, from uid-stickers/BottomAppBars.kt ---

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
  val m = Modifier
  when {
    actions <= 1 && fab ->
      KitBottomAppBarOne(
        modifier = m,
        check = check.label,
        add = add.label,
        onCheckClick = check.onClick,
        onFabClick = add.onClick,
      )
    actions <= 1 ->
      KitBottomAppBarOneNoFab(modifier = m, check = check.label, onCheckClick = check.onClick)
    actions == 2 && fab ->
      KitBottomAppBarTwo(
        modifier = m,
        check = check.label,
        edit = edit.label,
        add = add.label,
        onCheckClick = check.onClick,
        onEditClick = edit.onClick,
        onFabClick = add.onClick,
      )
    actions == 2 ->
      KitBottomAppBarTwoNoFab(
        modifier = m,
        check = check.label,
        edit = edit.label,
        onCheckClick = check.onClick,
        onEditClick = edit.onClick,
      )
    actions == 3 && fab ->
      KitBottomAppBarThree(
        modifier = m,
        check = check.label,
        edit = edit.label,
        more = more.label,
        add = add.label,
        onCheckClick = check.onClick,
        onEditClick = edit.onClick,
        onMoreClick = more.onClick,
        onFabClick = add.onClick,
      )
    actions == 3 ->
      KitBottomAppBarThreeNoFab(
        modifier = m,
        check = check.label,
        edit = edit.label,
        more = more.label,
        onCheckClick = check.onClick,
        onEditClick = edit.onClick,
        onMoreClick = more.onClick,
      )
    fab ->
      KitBottomAppBar(
        modifier = m,
        check = check.label,
        edit = edit.label,
        more = more.label,
        add = add.label,
        onCheckClick = check.onClick,
        onEditClick = edit.onClick,
        onMoreClick = more.onClick,
        onAddClick = add.onClick,
        onFabClick = add.onClick,
      )
    else ->
      KitBottomAppBarNoFab(
        modifier = m,
        check = check.label,
        edit = edit.label,
        more = more.label,
        add = add.label,
        onCheckClick = check.onClick,
        onEditClick = edit.onClick,
        onMoreClick = more.onClick,
        onAddClick = add.onClick,
      )
  }
}
