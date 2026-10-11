@file:CatalogGroup(name = "Progress indicators", section = "Communication")

package ee.schimke.m3catalog.uid

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ee.schimke.composeai.preview.BuilderComponent
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.CatalogModesLinearProgress
import ee.schimke.m3catalog.Sticker
import generated.uibuilder.KitCircularProgress
import generated.uibuilder.KitLinearProgress

// The bodies are `stickers/linear-progress.uid` and `stickers/circular-progress.uid`: each a
// determinate indicator whose `progress` is a binding, so the generated component takes the
// sticker's `progress` parameter knob and the indicator reads it as `progress = { progress }`.

@CatalogComponent(
  id = "Progress/Linear",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58005:8017",
  caption = "Determinate linear progress. Values across the range fold in.",
)
@CatalogModesLinearProgress
// `m3/progress-indicator`, not `m3/linear-progress-indicator`: the builder offers ONE progress
// component with the shape as a property, which is a coarser cut than this catalog's, and it is
// the id every saved design already stores.
@BuilderComponent(component = "LinearProgressIndicator", id = "m3/progress-indicator")
@OverrideVariant(name = "empty", floats = ["progress=0.0"])
@OverrideVariant(name = "quarter", floats = ["progress=0.25"])
@OverrideVariant(name = "full", floats = ["progress=1.0"])
@ee.schimke.m3catalog.LinearProgressExhaustiveKitCells
@Composable
fun LinearProgress(progress: Float = 0.5f) = Sticker {
  KitLinearProgress(Modifier, progress = progress)
}

@CatalogComponent(
  id = "Progress/Circular",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58005:8492",
  caption = "Determinate circular progress. Values across the range fold in.",
)
@CatalogModes
@OverrideVariant(name = "empty", floats = ["progress=0.0"])
@OverrideVariant(name = "quarter", floats = ["progress=0.25"])
@OverrideVariant(name = "full", floats = ["progress=1.0"])
@ee.schimke.m3catalog.CircularProgressExhaustiveKitCells
@Composable
fun CircularProgress(progress: Float = 0.5f) = Sticker {
  KitCircularProgress(Modifier, progress = progress)
}
