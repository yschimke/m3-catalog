@file:CatalogGroup(name = "Tabs", section = "Navigation")

package ee.schimke.m3catalog.uid

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogChoice
import ee.schimke.m3catalog.catalogText
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.tab_overview
import ee.schimke.m3catalog.generated.resources.tab_reviews
import ee.schimke.m3catalog.generated.resources.tab_specs
import ee.schimke.m3catalog.selectable
import generated.uibuilder.KitPrimaryTabs
import generated.uibuilder.KitPrimaryTabsIcon
import generated.uibuilder.KitPrimaryTabsIconLabel
import generated.uibuilder.KitPrimaryTabsIconLabelSelected1
import generated.uibuilder.KitPrimaryTabsIconLabelSelected2
import generated.uibuilder.KitPrimaryTabsIconSelected1
import generated.uibuilder.KitPrimaryTabsIconSelected2
import generated.uibuilder.KitPrimaryTabsSelected1
import generated.uibuilder.KitPrimaryTabsSelected2
import generated.uibuilder.KitSecondaryTabs
import generated.uibuilder.KitSecondaryTabsIcon
import generated.uibuilder.KitSecondaryTabsIconLabel
import generated.uibuilder.KitSecondaryTabsIconLabelSelected0
import generated.uibuilder.KitSecondaryTabsIconLabelSelected2
import generated.uibuilder.KitSecondaryTabsIconSelected0
import generated.uibuilder.KitSecondaryTabsIconSelected2
import generated.uibuilder.KitSecondaryTabsSelected0
import generated.uibuilder.KitSecondaryTabsSelected2
import org.jetbrains.compose.resources.stringResource

// The bodies are `stickers/primary-tabs.uid` and `stickers/secondary-tabs.uid`: a tab row of three
// `Tab`s, each label a binding and each click an action-free event. The kit's `Layout` axis is a
// cell (`Icon label` adds each tab's icon, `Icon` also deletes its text).
//
// The selected tab is the shell's state, as in the hand-written sticker, but the tab row's
// `selectedTabIndex` cannot be a binding: the exporter refuses it ("node `row`.selectedIndex: no
// binding type mapping"), since it knows no binding type for an index. So the selection is a cell
// too — `Selected N` sets the row's index and each tab's `selected` — and the shell picks the cell
// from its state. The baked captures never move the selection, so they agree to the pixel; on the
// live lane a click swaps to another generated function, which restarts the indicator rather than
// animating it. An `Int` binding type for `selectedIndex` / `selectedTabIndex` would let one cell
// per layout serve every selection.

@Composable
private fun tabTexts(): List<String> =
  listOf(
    catalogText("text", stringResource(Res.string.tab_overview), 0),
    catalogText("text", stringResource(Res.string.tab_specs), 1),
    catalogText("text", stringResource(Res.string.tab_reviews), 2),
  )

@CatalogComponent(
  id = "Tabs/Primary",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/54563:40116",
  caption = "Top-level content categories under an app bar. Icon and icon+label fold in.",
)
@CatalogModes
@OverrideVariant(name = "icon", strings = ["content=icon"])
@OverrideVariant(name = "icon-label", strings = ["content=icon+label"])
@ee.schimke.m3catalog.PrimaryTabsExhaustiveKitCells
@Composable
fun PrimaryTabs() = Sticker {
  var selected by selectable(0)
  val content = catalogChoice("content", "label", "label", "icon", "icon+label")
  val text = if (content == "icon") emptyList() else tabTexts()
  val m = Modifier
  when {
    content == "label" && selected == 0 ->
      KitPrimaryTabs(
        modifier = m,
        text0 = text[0],
        text1 = text[1],
        text2 = text[2],
        onTab0Click = { selected = 0 },
        onTab1Click = { selected = 1 },
        onTab2Click = { selected = 2 },
      )
    content == "label" && selected == 1 ->
      KitPrimaryTabsSelected1(
        modifier = m,
        text0 = text[0],
        text1 = text[1],
        text2 = text[2],
        onTab0Click = { selected = 0 },
        onTab1Click = { selected = 1 },
        onTab2Click = { selected = 2 },
      )
    content == "label" && selected == 2 ->
      KitPrimaryTabsSelected2(
        modifier = m,
        text0 = text[0],
        text1 = text[1],
        text2 = text[2],
        onTab0Click = { selected = 0 },
        onTab1Click = { selected = 1 },
        onTab2Click = { selected = 2 },
      )
    content == "icon+label" && selected == 0 ->
      KitPrimaryTabsIconLabel(
        modifier = m,
        text0 = text[0],
        text1 = text[1],
        text2 = text[2],
        onTab0Click = { selected = 0 },
        onTab1Click = { selected = 1 },
        onTab2Click = { selected = 2 },
      )
    content == "icon+label" && selected == 1 ->
      KitPrimaryTabsIconLabelSelected1(
        modifier = m,
        text0 = text[0],
        text1 = text[1],
        text2 = text[2],
        onTab0Click = { selected = 0 },
        onTab1Click = { selected = 1 },
        onTab2Click = { selected = 2 },
      )
    content == "icon+label" && selected == 2 ->
      KitPrimaryTabsIconLabelSelected2(
        modifier = m,
        text0 = text[0],
        text1 = text[1],
        text2 = text[2],
        onTab0Click = { selected = 0 },
        onTab1Click = { selected = 1 },
        onTab2Click = { selected = 2 },
      )
    content == "icon" && selected == 0 ->
      KitPrimaryTabsIcon(
        modifier = m,
        onTab0Click = { selected = 0 },
        onTab1Click = { selected = 1 },
        onTab2Click = { selected = 2 },
      )
    content == "icon" && selected == 1 ->
      KitPrimaryTabsIconSelected1(
        modifier = m,
        onTab0Click = { selected = 0 },
        onTab1Click = { selected = 1 },
        onTab2Click = { selected = 2 },
      )
    content == "icon" && selected == 2 ->
      KitPrimaryTabsIconSelected2(
        modifier = m,
        onTab0Click = { selected = 0 },
        onTab1Click = { selected = 1 },
        onTab2Click = { selected = 2 },
      )
    else -> {}
  }
}

@CatalogComponent(
  id = "Tabs/Secondary",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/54563:40047",
  caption = "Nested categories within a primary tab. Icon and icon+label fold in.",
)
@CatalogModes
@OverrideVariant(name = "icon", strings = ["content=icon"])
@OverrideVariant(name = "icon-label", strings = ["content=icon+label"])
@Composable
fun SecondaryTabs() = Sticker {
  var selected by selectable(1)
  val content = catalogChoice("content", "label", "label", "icon", "icon+label")
  val text = if (content == "icon") emptyList() else tabTexts()
  val m = Modifier
  when {
    content == "label" && selected == 0 ->
      KitSecondaryTabsSelected0(
        modifier = m,
        text0 = text[0],
        text1 = text[1],
        text2 = text[2],
        onTab0Click = { selected = 0 },
        onTab1Click = { selected = 1 },
        onTab2Click = { selected = 2 },
      )
    content == "label" && selected == 1 ->
      KitSecondaryTabs(
        modifier = m,
        text0 = text[0],
        text1 = text[1],
        text2 = text[2],
        onTab0Click = { selected = 0 },
        onTab1Click = { selected = 1 },
        onTab2Click = { selected = 2 },
      )
    content == "label" && selected == 2 ->
      KitSecondaryTabsSelected2(
        modifier = m,
        text0 = text[0],
        text1 = text[1],
        text2 = text[2],
        onTab0Click = { selected = 0 },
        onTab1Click = { selected = 1 },
        onTab2Click = { selected = 2 },
      )
    content == "icon+label" && selected == 0 ->
      KitSecondaryTabsIconLabelSelected0(
        modifier = m,
        text0 = text[0],
        text1 = text[1],
        text2 = text[2],
        onTab0Click = { selected = 0 },
        onTab1Click = { selected = 1 },
        onTab2Click = { selected = 2 },
      )
    content == "icon+label" && selected == 1 ->
      KitSecondaryTabsIconLabel(
        modifier = m,
        text0 = text[0],
        text1 = text[1],
        text2 = text[2],
        onTab0Click = { selected = 0 },
        onTab1Click = { selected = 1 },
        onTab2Click = { selected = 2 },
      )
    content == "icon+label" && selected == 2 ->
      KitSecondaryTabsIconLabelSelected2(
        modifier = m,
        text0 = text[0],
        text1 = text[1],
        text2 = text[2],
        onTab0Click = { selected = 0 },
        onTab1Click = { selected = 1 },
        onTab2Click = { selected = 2 },
      )
    content == "icon" && selected == 0 ->
      KitSecondaryTabsIconSelected0(
        modifier = m,
        onTab0Click = { selected = 0 },
        onTab1Click = { selected = 1 },
        onTab2Click = { selected = 2 },
      )
    content == "icon" && selected == 1 ->
      KitSecondaryTabsIcon(
        modifier = m,
        onTab0Click = { selected = 0 },
        onTab1Click = { selected = 1 },
        onTab2Click = { selected = 2 },
      )
    content == "icon" && selected == 2 ->
      KitSecondaryTabsIconSelected2(
        modifier = m,
        onTab0Click = { selected = 0 },
        onTab1Click = { selected = 1 },
        onTab2Click = { selected = 2 },
      )
    else -> {}
  }
}
