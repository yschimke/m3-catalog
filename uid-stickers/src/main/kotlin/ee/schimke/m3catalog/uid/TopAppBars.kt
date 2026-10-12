@file:CatalogGroup(name = "Top app bar", section = "Navigation")

package ee.schimke.m3catalog.uid

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModesCompact
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogChoice
import ee.schimke.m3catalog.catalogText
import ee.schimke.m3catalog.counted
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.action_back
import ee.schimke.m3catalog.generated.resources.action_more
import ee.schimke.m3catalog.generated.resources.action_search
import ee.schimke.m3catalog.generated.resources.appbar_title
import generated.uibuilder.KitSmallTopAppBar
import generated.uibuilder.KitSmallTopAppBarNoActions
import generated.uibuilder.KitSmallTopAppBarNoActionsOnScroll
import generated.uibuilder.KitSmallTopAppBarNoActionsSmallImage
import generated.uibuilder.KitSmallTopAppBarNoActionsSmallImageOnScroll
import generated.uibuilder.KitSmallTopAppBarNoNav
import generated.uibuilder.KitSmallTopAppBarNoNavNoActions
import generated.uibuilder.KitSmallTopAppBarNoNavNoActionsOnScroll
import generated.uibuilder.KitSmallTopAppBarNoNavNoActionsSmallImage
import generated.uibuilder.KitSmallTopAppBarNoNavNoActionsSmallImageOnScroll
import generated.uibuilder.KitSmallTopAppBarNoNavOnScroll
import generated.uibuilder.KitSmallTopAppBarNoNavSmallImage
import generated.uibuilder.KitSmallTopAppBarNoNavSmallImageOnScroll
import generated.uibuilder.KitSmallTopAppBarNoNavTwoActions
import generated.uibuilder.KitSmallTopAppBarNoNavTwoActionsOnScroll
import generated.uibuilder.KitSmallTopAppBarNoNavTwoActionsSmallImage
import generated.uibuilder.KitSmallTopAppBarNoNavTwoActionsSmallImageOnScroll
import generated.uibuilder.KitSmallTopAppBarOnScroll
import generated.uibuilder.KitSmallTopAppBarSmallImage
import generated.uibuilder.KitSmallTopAppBarSmallImageOnScroll
import generated.uibuilder.KitSmallTopAppBarTwoActions
import generated.uibuilder.KitSmallTopAppBarTwoActionsOnScroll
import generated.uibuilder.KitSmallTopAppBarTwoActionsSmallImage
import generated.uibuilder.KitSmallTopAppBarTwoActionsSmallImageOnScroll
import org.jetbrains.compose.resources.stringResource

// `stickers/small-top-app-bar.uid`: `m3/top-app-bar` at the sticker's 412 x 64dp, the title a
// bound `m3/text`, the back arrow and the search action icon buttons named by bindings with
// action-free clicks. The resting container is transparent (`containerColor = #00000000`, the
// hand-written `Color.Transparent`); `on scroll` cells paint `surfaceContainer`, the
// `scrolledContainerColor` `TopAppBarDefaults.topAppBarColors()` resolves to, as its container
// colour. The nav icon, the action count (0, 1, 2), the small-image title (the image placeholder at
// 40dp, square corners, scale basis 122) and the scrolled colour are cells crossed in full; the
// shell reads the knobs in the hand-written sticker's order and picks the cell.

@CatalogComponent(
  id = "TopAppBar/Small",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58114:20585",
  caption =
    "The app bar. Its four sizes, centred, image and search layouts, nav icon and actions fold in.",
)
@CatalogModesCompact
@OverrideVariant(name = "no-nav", booleans = ["nav=false"])
@OverrideVariant(name = "no-actions", ints = ["actions=0"])
@OverrideVariant(name = "two-actions", ints = ["actions=2"])
@OverrideVariant(name = "on-scroll", strings = ["elevation=on-scroll"])
@OverrideVariant(name = "small-image", strings = ["content=small-image"])
@Composable
fun SmallTopAppBar(nav: Boolean = true, actions: Int = 1) = Sticker {
  val back = counted(stringResource(Res.string.action_back))
  val more = counted(stringResource(Res.string.action_more))
  val search = counted(stringResource(Res.string.action_search))
  val onScroll = catalogChoice("elevation", "flat", "flat", "on-scroll") == "on-scroll"
  val image = catalogChoice("content", "title", "title", "small-image") == "small-image"
  // The hand-written title slot reads the copy only when it draws the text.
  val title = if (image) "" else catalogText("title", stringResource(Res.string.appbar_title))
  val m = Modifier
  if (nav) {
    if (actions <= 0) {
      if (!image) {
        if (onScroll)
          KitSmallTopAppBarNoActionsOnScroll(
            m,
            title = title,
            back = back.label,
            onBackClick = back.onClick,
          )
        else
          KitSmallTopAppBarNoActions(
            m,
            title = title,
            back = back.label,
            onBackClick = back.onClick,
          )
      } else {
        if (onScroll)
          KitSmallTopAppBarNoActionsSmallImageOnScroll(
            m,
            back = back.label,
            onBackClick = back.onClick,
          )
        else KitSmallTopAppBarNoActionsSmallImage(m, back = back.label, onBackClick = back.onClick)
      }
    } else if (actions == 1) {
      if (!image) {
        if (onScroll)
          KitSmallTopAppBarOnScroll(
            m,
            title = title,
            back = back.label,
            search = search.label,
            onBackClick = back.onClick,
            onSearchClick = search.onClick,
          )
        else
          KitSmallTopAppBar(
            m,
            title = title,
            back = back.label,
            search = search.label,
            onBackClick = back.onClick,
            onSearchClick = search.onClick,
          )
      } else {
        if (onScroll)
          KitSmallTopAppBarSmallImageOnScroll(
            m,
            back = back.label,
            search = search.label,
            onBackClick = back.onClick,
            onSearchClick = search.onClick,
          )
        else
          KitSmallTopAppBarSmallImage(
            m,
            back = back.label,
            search = search.label,
            onBackClick = back.onClick,
            onSearchClick = search.onClick,
          )
      }
    } else {
      if (!image) {
        if (onScroll)
          KitSmallTopAppBarTwoActionsOnScroll(
            m,
            title = title,
            back = back.label,
            search = search.label,
            more = more.label,
            onBackClick = back.onClick,
            onSearchClick = search.onClick,
            onMoreClick = more.onClick,
          )
        else
          KitSmallTopAppBarTwoActions(
            m,
            title = title,
            back = back.label,
            search = search.label,
            more = more.label,
            onBackClick = back.onClick,
            onSearchClick = search.onClick,
            onMoreClick = more.onClick,
          )
      } else {
        if (onScroll)
          KitSmallTopAppBarTwoActionsSmallImageOnScroll(
            m,
            back = back.label,
            search = search.label,
            more = more.label,
            onBackClick = back.onClick,
            onSearchClick = search.onClick,
            onMoreClick = more.onClick,
          )
        else
          KitSmallTopAppBarTwoActionsSmallImage(
            m,
            back = back.label,
            search = search.label,
            more = more.label,
            onBackClick = back.onClick,
            onSearchClick = search.onClick,
            onMoreClick = more.onClick,
          )
      }
    }
  } else {
    if (actions <= 0) {
      if (!image) {
        if (onScroll) KitSmallTopAppBarNoNavNoActionsOnScroll(m, title = title)
        else KitSmallTopAppBarNoNavNoActions(m, title = title)
      } else {
        if (onScroll) KitSmallTopAppBarNoNavNoActionsSmallImageOnScroll(m)
        else KitSmallTopAppBarNoNavNoActionsSmallImage(m)
      }
    } else if (actions == 1) {
      if (!image) {
        if (onScroll)
          KitSmallTopAppBarNoNavOnScroll(
            m,
            title = title,
            search = search.label,
            onSearchClick = search.onClick,
          )
        else
          KitSmallTopAppBarNoNav(
            m,
            title = title,
            search = search.label,
            onSearchClick = search.onClick,
          )
      } else {
        if (onScroll)
          KitSmallTopAppBarNoNavSmallImageOnScroll(
            m,
            search = search.label,
            onSearchClick = search.onClick,
          )
        else
          KitSmallTopAppBarNoNavSmallImage(m, search = search.label, onSearchClick = search.onClick)
      }
    } else {
      if (!image) {
        if (onScroll)
          KitSmallTopAppBarNoNavTwoActionsOnScroll(
            m,
            title = title,
            search = search.label,
            more = more.label,
            onSearchClick = search.onClick,
            onMoreClick = more.onClick,
          )
        else
          KitSmallTopAppBarNoNavTwoActions(
            m,
            title = title,
            search = search.label,
            more = more.label,
            onSearchClick = search.onClick,
            onMoreClick = more.onClick,
          )
      } else {
        if (onScroll)
          KitSmallTopAppBarNoNavTwoActionsSmallImageOnScroll(
            m,
            search = search.label,
            more = more.label,
            onSearchClick = search.onClick,
            onMoreClick = more.onClick,
          )
        else
          KitSmallTopAppBarNoNavTwoActionsSmallImage(
            m,
            search = search.label,
            more = more.label,
            onSearchClick = search.onClick,
            onMoreClick = more.onClick,
          )
      }
    }
  }
}
