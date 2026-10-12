@file:CatalogGroup(name = "Toolbars", section = "Actions")

package ee.schimke.m3catalog.uid

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogChoice
import ee.schimke.m3catalog.counted
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.action_add
import ee.schimke.m3catalog.generated.resources.action_edit
import ee.schimke.m3catalog.generated.resources.action_more
import generated.uibuilder.KitHorizontalFloatingToolbar
import generated.uibuilder.KitHorizontalFloatingToolbarVibrant
import org.jetbrains.compose.resources.stringResource

// `stickers/horizontal-floating-toolbar.uid`: the sticker's 168dp frame box (11dp inset) around
// `m3/horizontal-floating-toolbar`, holding three icon buttons whose glyphs are tinted
// `onSurfaceVariant` and named by the catalog's string resources; each click is an action-free
// event. `Vibrant` is the toolbar's container colour role, `primaryContainer`, which is the
// `toolbarContainerColor` `FloatingToolbarDefaults.vibrantFloatingToolbarColors()` resolves to;
// the glyphs carry their own tint, so the bundle's content colour never reaches a pixel.

@CatalogComponent(
  id = "Toolbar/HorizontalFloating",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58467:8210",
  caption = "A floating cluster of related actions over the content. Vibrant folds in.",
)
@CatalogModes
@OverrideVariant(name = "vibrant", strings = ["color=vibrant"])
@Composable
fun HorizontalFloatingToolbarSticker() = Sticker {
  val vibrant = catalogChoice("color", "standard", "standard", "vibrant") == "vibrant"
  val add = counted(stringResource(Res.string.action_add))
  val edit = counted(stringResource(Res.string.action_edit))
  val more = counted(stringResource(Res.string.action_more))
  if (vibrant)
    KitHorizontalFloatingToolbarVibrant(
      Modifier,
      onAddClick = add.onClick,
      onEditClick = edit.onClick,
      onMoreClick = more.onClick,
    )
  else
    KitHorizontalFloatingToolbar(
      Modifier,
      onAddClick = add.onClick,
      onEditClick = edit.onClick,
      onMoreClick = more.onClick,
    )
}
