@file:CatalogGroup(name = "Navigation bar", section = "Navigation")

package ee.schimke.m3catalog.uid

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.InteractionPreview
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModesCompact
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogChoice
import ee.schimke.m3catalog.catalogText
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.nav_home
import ee.schimke.m3catalog.generated.resources.nav_saved
import ee.schimke.m3catalog.generated.resources.nav_search
import ee.schimke.m3catalog.generated.resources.nav_settings
import ee.schimke.m3catalog.generated.resources.nav_you
import ee.schimke.m3catalog.selectable
import generated.uibuilder.KitShortNavigationBar
import generated.uibuilder.KitShortNavigationBarFour
import generated.uibuilder.KitShortNavigationBarFourNoLabels
import generated.uibuilder.KitShortNavigationBarNoLabels
import generated.uibuilder.KitShortNavigationBarThree
import generated.uibuilder.KitShortNavigationBarThreeNoLabels
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

// The body is `stickers/short-navigation-bar.uid`: a `ShortNavigationBar` over a full-width row of
// five `ShortNavigationBarItem`s, each weighted, its icon the kit's stars glyph
// (`m3catalog/selection-stars-icon`, filled when selected) and its label a binding. The selected
// destination is the shell's state, as in the hand-written sticker: each item's `selected` is a
// binding and its click an action-free event the shell answers by moving the selection, so the
// document stays stateless. Destination count and label visibility are cells
// (`stickers/short-navigation-bar.json`) that delete the items or labels a cell lacks.

private val DESTINATIONS: List<StringResource> =
  listOf(
    Res.string.nav_home,
    Res.string.nav_search,
    Res.string.nav_you,
    Res.string.nav_saved,
    Res.string.nav_settings,
  )

@CatalogComponent(
  id = "NavigationBar/Short",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58016:37260",
  caption = "The expressive compact bar. Count and labels fold in.",
)
// Three destinations by default, so 2 -> 0 -> 2 is the longest travel this bar offers. The
// distance is the point: the indicator's spring is a function of how far it has to go, so a
// neighbouring hop under-sells exactly what Expressive changed.
@InteractionPreview(
  targets = [2, 0, 2],
  caption =
    "Tap between distant destinations. The selection indicator moves on Expressive's spatial " +
      "spring — watch it overshoot and settle rather than ease linearly into place.",
)
@CatalogModesCompact
@OverrideVariant(name = "four", ints = ["count=4"])
@OverrideVariant(name = "five", ints = ["count=5"])
@OverrideVariant(name = "labels-none", strings = ["labels=none"])
@Composable
fun ShortNavigationBarSticker(count: Int = 3) = Sticker {
  val labels = catalogChoice("labels", "always", "always", "none")
  var selected by selectable(0)
  val n = count.coerceIn(3, 5)
  val label =
    if (labels == "none") emptyList()
    else
      DESTINATIONS.take(n).mapIndexed { index, res ->
        catalogText("label", stringResource(res), index)
      }
  val m = Modifier
  when {
    labels == "none" && n == 3 ->
      KitShortNavigationBarThreeNoLabels(
        modifier = m,
        selected0 = selected == 0,
        selected1 = selected == 1,
        selected2 = selected == 2,
        onItem0Click = { selected = 0 },
        onItem1Click = { selected = 1 },
        onItem2Click = { selected = 2 },
      )
    labels == "none" && n == 4 ->
      KitShortNavigationBarFourNoLabels(
        modifier = m,
        selected0 = selected == 0,
        selected1 = selected == 1,
        selected2 = selected == 2,
        selected3 = selected == 3,
        onItem0Click = { selected = 0 },
        onItem1Click = { selected = 1 },
        onItem2Click = { selected = 2 },
        onItem3Click = { selected = 3 },
      )
    labels == "none" ->
      KitShortNavigationBarNoLabels(
        modifier = m,
        selected0 = selected == 0,
        selected1 = selected == 1,
        selected2 = selected == 2,
        selected3 = selected == 3,
        selected4 = selected == 4,
        onItem0Click = { selected = 0 },
        onItem1Click = { selected = 1 },
        onItem2Click = { selected = 2 },
        onItem3Click = { selected = 3 },
        onItem4Click = { selected = 4 },
      )
    n == 3 ->
      KitShortNavigationBarThree(
        modifier = m,
        selected0 = selected == 0,
        label0 = label[0],
        selected1 = selected == 1,
        label1 = label[1],
        selected2 = selected == 2,
        label2 = label[2],
        onItem0Click = { selected = 0 },
        onItem1Click = { selected = 1 },
        onItem2Click = { selected = 2 },
      )
    n == 4 ->
      KitShortNavigationBarFour(
        modifier = m,
        selected0 = selected == 0,
        label0 = label[0],
        selected1 = selected == 1,
        label1 = label[1],
        selected2 = selected == 2,
        label2 = label[2],
        selected3 = selected == 3,
        label3 = label[3],
        onItem0Click = { selected = 0 },
        onItem1Click = { selected = 1 },
        onItem2Click = { selected = 2 },
        onItem3Click = { selected = 3 },
      )
    else ->
      KitShortNavigationBar(
        modifier = m,
        selected0 = selected == 0,
        label0 = label[0],
        selected1 = selected == 1,
        label1 = label[1],
        selected2 = selected == 2,
        label2 = label[2],
        selected3 = selected == 3,
        label3 = label[3],
        selected4 = selected == 4,
        label4 = label[4],
        onItem0Click = { selected = 0 },
        onItem1Click = { selected = 1 },
        onItem2Click = { selected = 2 },
        onItem3Click = { selected = 3 },
        onItem4Click = { selected = 4 },
      )
  }
}
