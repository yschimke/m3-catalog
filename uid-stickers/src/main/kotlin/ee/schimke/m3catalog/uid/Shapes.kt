@file:CatalogGroup(name = "Shapes", section = "Styles")

package ee.schimke.m3catalog.uid

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.CornerScaleMatrix
import ee.schimke.m3catalog.CornerScaleToken
import ee.schimke.m3catalog.Sticker
import generated.uibuilder.KitCornerScale
import generated.uibuilder.KitCornerScaleExtraExtraLarge
import generated.uibuilder.KitCornerScaleExtraLarge
import generated.uibuilder.KitCornerScaleExtraLargeIncreased
import generated.uibuilder.KitCornerScaleExtraSmall
import generated.uibuilder.KitCornerScaleFull
import generated.uibuilder.KitCornerScaleLarge
import generated.uibuilder.KitCornerScaleLargeIncreased
import generated.uibuilder.KitCornerScaleMedium
import generated.uibuilder.KitCornerScaleSmall

// The body is `stickers/corner-scale.uid`: a `KitCornerScale` specimen, a primary silhouette over
// its caption. The caption is a binding; the radius is the silhouette's `clip` modifier, whose
// shape is a literal name (a dp radius, or `circle` for the 50% `Full`), so each of the ten tokens
// is a cell of the document (`stickers/corner-scale.json`) and the shell picks one off the same
// `corner` axis the hand-written sticker reads.

@CatalogComponent(
  id = "Shape/Corner scale",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58548:7181",
  caption = "The Material corner scale, with each of its ten radius tokens folded in as a variant.",
)
@CatalogModes
@CornerScaleMatrix
@Composable
fun CornerScaleSticker() = Sticker {
  val token = CornerScaleToken.Axis.current()
  val label = token.label
  when (token) {
    CornerScaleToken.None -> KitCornerScale(Modifier, label = label)
    CornerScaleToken.ExtraSmall -> KitCornerScaleExtraSmall(Modifier, label = label)
    CornerScaleToken.Small -> KitCornerScaleSmall(Modifier, label = label)
    CornerScaleToken.Medium -> KitCornerScaleMedium(Modifier, label = label)
    CornerScaleToken.Large -> KitCornerScaleLarge(Modifier, label = label)
    CornerScaleToken.LargeIncreased -> KitCornerScaleLargeIncreased(Modifier, label = label)
    CornerScaleToken.ExtraLarge -> KitCornerScaleExtraLarge(Modifier, label = label)
    CornerScaleToken.ExtraLargeIncreased ->
      KitCornerScaleExtraLargeIncreased(Modifier, label = label)
    CornerScaleToken.ExtraExtraLarge -> KitCornerScaleExtraExtraLarge(Modifier, label = label)
    CornerScaleToken.Full -> KitCornerScaleFull(Modifier, label = label)
  }
}
