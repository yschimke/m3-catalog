@file:CatalogGroup(name = "Shape", section = "Styles")

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker

// The corner scale, drawn as the shapes themselves — matching the kit's `Corner` variables
// (4 / 8 / 12 / 16 / 28 dp).

@Composable
private fun ShapeSpecimen(name: String, shape: Shape) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Surface(
      modifier = Modifier.size(72.dp),
      shape = shape,
      color = MaterialTheme.colorScheme.primaryContainer,
    ) {
      Box(Modifier)
    }
    Text(name, style = MaterialTheme.typography.labelMedium)
  }
}

@CatalogComponent(
  id = "Shape/Scale",
  noReference =
    "the kit publishes Corner variables and a Shape Set specimen sheet, not a shape-scale " +
      "component; a node reference would compare two different specimen layouts",
  caption = "extraSmall 4dp through extraLarge 28dp.",
)
@CatalogModes
@Composable
fun ShapeScale() = Sticker {
  val s = MaterialTheme.shapes
  Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
    ShapeSpecimen("XS", s.extraSmall)
    ShapeSpecimen("S", s.small)
    ShapeSpecimen("M", s.medium)
    ShapeSpecimen("L", s.large)
    ShapeSpecimen("XL", s.extraLarge)
  }
}
