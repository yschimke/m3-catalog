@file:CatalogGroup(name = "Sliders", section = "Selection")
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogInteractive
import ee.schimke.m3catalog.draggable

@CatalogComponent(
  id = "Slider/Continuous",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58008:10356",
  caption = "Select a value from a continuous range.",
)
@CatalogModes
@Composable
fun ContinuousSlider() = Sticker {
  val (value, set) = draggable(0.5f)
  Slider(value = value, onValueChange = set, modifier = Modifier.width(280.dp))
}

@CatalogVariant(
  of = "Slider/Continuous",
  props = ["steps=discrete"],
  caption = "Discrete stops along the track.",
)
@CatalogModes
@Composable
fun DiscreteSlider() = Sticker {
  val (value, set) = draggable(0.6f)
  Slider(value = value, onValueChange = set, steps = 4, modifier = Modifier.width(280.dp))
}

@CatalogVariant(
  of = "Slider/Continuous",
  props = ["selection=range"],
  caption = "Two thumbs bounding a sub-range.",
)
@CatalogModes
@Composable
fun RangeSliderSticker() = Sticker {
  RangeSlider(
    value = 0.2f..0.8f,
    onValueChange = {},
    enabled = catalogInteractive(),
    modifier = Modifier.width(280.dp),
  )
}
