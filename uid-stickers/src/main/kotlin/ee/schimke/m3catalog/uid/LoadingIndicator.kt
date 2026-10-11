@file:CatalogGroup(name = "Loading indicator", section = "Communication")

package ee.schimke.m3catalog.uid

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import generated.uibuilder.KitLoadingIndicator

// The body is `stickers/loading-indicator.uid`: the expressive `LoadingIndicator`, alone.

@CatalogComponent(
  id = "LoadingIndicator/Uncontained",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58005:8556",
  caption = "The shape-morph indicator on its own.",
)
@CatalogModes
@Composable
fun LoadingIndicatorSticker() = Sticker { KitLoadingIndicator(Modifier) }
