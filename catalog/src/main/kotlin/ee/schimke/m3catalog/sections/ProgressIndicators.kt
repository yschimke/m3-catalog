@file:CatalogGroup(name = "Progress indicators", section = "Communication")
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker

// Determinate progress is the primary sticker: an indeterminate spinner is an animation, and a
// baked PNG would freeze it at an arbitrary frame — so the fixed fraction is what gets published.

@CatalogComponent(
  id = "Progress/Linear",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58005:7997",
  caption = "Determinate linear progress at 70%.",
)
@CatalogModes
@Composable
fun LinearProgress() = Sticker { LinearProgressIndicator(progress = { 0.7f }) }

@CatalogComponent(
  id = "Progress/Circular",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58005:8459",
  caption = "Determinate circular progress at 70%.",
)
@CatalogModes
@Composable
fun CircularProgress() = Sticker { CircularProgressIndicator(progress = { 0.7f }) }

@CatalogVariant(
  of = "Progress/Linear",
  props = ["shape=wavy"],
  caption = "The expressive wavy track.",
)
@CatalogModes
@Composable
fun LinearWavyProgress() = Sticker {
  Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
    LinearWavyProgressIndicator(progress = { 0.7f })
  }
}
