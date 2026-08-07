@file:CatalogGroup(name = "Elevation", section = "Styles")

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker

// The six M3 elevation levels. Two distinct mechanisms share the scale and the sheet shows both:
// **tonal** elevation tints the surface (visible in light and dark), **shadow** elevation casts one
// (visible against the background).

@Composable
private fun Level(label: String, tonal: Dp = 0.dp, shadow: Dp = 0.dp) {
  Surface(
    modifier = Modifier.size(72.dp),
    shape = MaterialTheme.shapes.medium,
    tonalElevation = tonal,
    shadowElevation = shadow,
  ) {
    Box(Modifier, contentAlignment = Alignment.Center) {
      Text(label, style = MaterialTheme.typography.labelMedium)
    }
  }
}

private val LEVELS =
  listOf("0" to 0.dp, "1" to 1.dp, "2" to 3.dp, "3" to 6.dp, "4" to 8.dp, "5" to 12.dp)

@CatalogComponent(id = "Elevation/Tonal", caption = "Levels 0–5 as a surface tint.")
@CatalogModes
@Composable
fun TonalElevation() = Sticker {
  Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
    LEVELS.forEach { (label, dp) -> Level(label, tonal = dp) }
  }
}

@CatalogVariant(
  of = "Elevation/Tonal",
  props = ["kind=shadow"],
  caption = "Levels 0–5 as a shadow.",
)
@CatalogModes
@Composable
fun ShadowElevation() = Sticker {
  Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
    LEVELS.forEach { (label, dp) -> Level(label, shadow = dp) }
  }
}
