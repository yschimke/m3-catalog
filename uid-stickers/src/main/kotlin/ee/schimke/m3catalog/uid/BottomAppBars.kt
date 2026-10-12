@file:CatalogGroup(name = "Bottom app bar", section = "Navigation")

package ee.schimke.m3catalog.uid

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

// The body is `stickers/bottom-app-bar.uid`: a `BottomAppBar` with all four actions and the FAB,
// each icon's content description a binding (the click counter's label) and each click an
// action-free event. The action count and the FAB are structural, so they are cells of that
// document (`stickers/bottom-app-bar.json`), each deleting the actions or the FAB it lacks. The
// fourth action and the FAB are the same `add` intent, as in the hand-written sticker.

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
