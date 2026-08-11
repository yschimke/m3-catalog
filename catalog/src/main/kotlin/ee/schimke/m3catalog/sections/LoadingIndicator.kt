@file:CatalogGroup(name = "Loading indicator", section = "Communication")
@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker

// The expressive loading indicator: a morphing shape sequence, baked at its first frame (which is
// deterministic) and animated on the live lane. The kit's axis is contained vs uncontained, which
// Compose models as two composables rather than a parameter.

@CatalogComponent(
  id = "LoadingIndicator/Uncontained",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58005:8556",
  caption = "The shape-morph indicator on its own.",
)
@CatalogModes
@Composable
fun LoadingIndicatorSticker() = Sticker {
  val kitShape = MaterialShapes.Burst
  LoadingIndicator(
    progress = { 1f },
    color = MaterialTheme.colorScheme.primary,
    polygons = listOf(kitShape, kitShape),
  )
}

@CatalogComponent(
  id = "LoadingIndicator/Contained",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58005:8559",
  caption = "The same indicator inside its container surface.",
)
@CatalogModes
@Composable
fun ContainedLoadingIndicatorSticker() = Sticker {
  val kitShape = MaterialShapes.Burst
  ContainedLoadingIndicator(
    progress = { 1f },
    indicatorColor = MaterialTheme.colorScheme.onPrimaryFixedVariant,
    polygons = listOf(kitShape, kitShape),
  )
}
