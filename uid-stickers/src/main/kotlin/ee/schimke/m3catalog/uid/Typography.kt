@file:CatalogGroup(name = "Typography", section = "Styles")

package ee.schimke.m3catalog.uid

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.TypeScaleMatrix
import ee.schimke.m3catalog.TypeScaleRole
import generated.uibuilder.KitTypeScale

// The body is `stickers/type-scale.uid`: a `KitTypeScale` specimen, one `m3/text` in a 400dp box,
// whose copy AND style are bindings. A bound `m3/text` style is a `TextStyle` parameter, so the
// fifteen roles are one generated component rather than fifteen cells; the shell reads the `style`
// axis exactly as the hand-written sticker does and passes the role's label and theme style.

@CatalogComponent(
  id = "Typography/Type scale",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58186:19244",
  caption = "The Material 3 type scale, with each of its fifteen roles folded in as a variant.",
)
@CatalogModes
@TypeScaleMatrix
@Composable
fun TypeScaleRoleSticker() = Sticker {
  val role = TypeScaleRole.Axis.current()
  KitTypeScale(Modifier, label = role.label, style = role.style)
}
