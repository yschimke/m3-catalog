@file:CatalogGroup(name = "Loading indicator", section = "Communication")
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.runtime.Composable
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker

// The expressive loading indicator: a morphing shape sequence. Baked at its first frame, which is
// deterministic; the live lane animates it.

@CatalogComponent(
  id = "LoadingIndicator/Standard",
  caption = "Expressive shape-morph loading indicator.",
)
@CatalogModes
@Composable
fun LoadingIndicatorSticker() = Sticker { LoadingIndicator() }
