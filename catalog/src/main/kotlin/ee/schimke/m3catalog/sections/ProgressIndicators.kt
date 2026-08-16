@file:CatalogGroup(name = "Progress indicators", section = "Communication")
@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.overrides.previewOverrideString
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.CatalogModes404
import ee.schimke.m3catalog.CatalogModes405
import ee.schimke.m3catalog.Sticker

// Determinate is the primary sticker on every one of these. An indeterminate indicator is an
// animation, and a baked PNG freezes it at an arbitrary frame — so the published capture is the
// fixed fraction, and the progress value is the axis that folds in.
//
// The kit's other axis is the TRACK: `Type = Flat | Wave`, one property of one component set.
// Wavy is a separate composable rather than a parameter, but that is a fact about the Compose API,
// not about the taxonomy — so it folds in as a `@CatalogVariant` rather than standing as its own
// component. The kit-index resolver reads `type=wave` back to the set's `Type=Wave` variant, so
// the wavy node is still compared; it is now compared as a cell of the indicator it varies.

@Composable
private fun progress(): Float = previewOverrideString("progress", "0.5").toFloatOrNull() ?: 0.5f

@CatalogComponent(
  id = "Progress/Linear",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58005:8017",
  caption = "Determinate linear progress. Values across the range fold in.",
)
@CatalogModes405
@OverrideVariant(name = "empty", strings = ["progress=0.0"])
@OverrideVariant(name = "quarter", strings = ["progress=0.25"])
@OverrideVariant(name = "full", strings = ["progress=1.0"])
@Composable
fun LinearProgress() = Sticker {
  // Read the knob in composition, then close over the value: `progress = {}` is a plain lambda the
  // indicator samples on each draw, not a @Composable scope.
  val value = progress()
  Box(Modifier.width(405.dp).height(12.dp)) {
    LinearProgressIndicator(
      progress = { value },
      modifier = Modifier.align(Alignment.Center).fillMaxWidth(),
    )
  }
}

@CatalogComponent(
  id = "Progress/Circular",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58005:8492",
  caption = "Determinate circular progress. Values across the range fold in.",
)
@CatalogModes
@OverrideVariant(name = "empty", strings = ["progress=0.0"])
@OverrideVariant(name = "quarter", strings = ["progress=0.25"])
@OverrideVariant(name = "full", strings = ["progress=1.0"])
@Composable
fun CircularProgress() = Sticker {
  val value = progress()
  CircularProgressIndicator(progress = { value })
}

@CatalogVariant(
  of = "Progress/Linear",
  props = ["type=wave"],
  caption = "The expressive wavy track. Values across the range fold in.",
)
@CatalogModes404
@OverrideVariant(name = "quarter", strings = ["progress=0.25"])
@OverrideVariant(name = "full", strings = ["progress=1.0"])
@Composable
fun LinearWavyProgress() = Sticker {
  val value = progress()
  Box(Modifier.width(404.dp).height(12.dp)) {
    LinearWavyProgressIndicator(
      progress = { value },
      modifier = Modifier.align(Alignment.Center).fillMaxWidth(),
    )
  }
}

@CatalogVariant(
  of = "Progress/Circular",
  props = ["type=wave"],
  caption = "The expressive wavy circular track. Values across the range fold in.",
)
@CatalogModes
@OverrideVariant(name = "quarter", strings = ["progress=0.25"])
@OverrideVariant(name = "full", strings = ["progress=1.0"])
@Composable
fun CircularWavyProgress() = Sticker {
  val value = progress()
  CircularWavyProgressIndicator(progress = { value })
}
