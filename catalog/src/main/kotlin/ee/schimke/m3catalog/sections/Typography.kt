@file:CatalogGroup(name = "Typography", section = "Styles")

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.TypeScaleMatrix
import ee.schimke.m3catalog.TypeScaleRole

// The fifteen roles are one closed axis, declared in `TypeScaleRole` (root package, beside the
// other catalog axes) rather than as string literals here: the `when`, the drawn label and the
// variant cells all come off the enum, and the cells arrive as `@TypeScaleMatrix` (#103).

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
  Box(Modifier.width(400.dp).padding(vertical = 8.dp)) { Text(role.label, style = role.style) }
}
