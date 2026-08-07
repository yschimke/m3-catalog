@file:CatalogGroup(name = "Bottom sheets", section = "Containment")
@file:OptIn(ExperimentalMaterial3Api::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.overrides.previewOverrideBoolean
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker

// Like a dialog, a modal bottom sheet is hosted in its own window, so the sticker composes the
// sheet CONTAINER itself — BottomSheetDefaults' expanded shape, container colour and real drag
// handle. The published pixels are the component, minus the scrim and the window.
//
// The kit's axes: the drag handle, and an optional header line above the content.

@Composable
private fun SheetSurface(content: @Composable () -> Unit) {
  Surface(
    modifier = Modifier.width(360.dp),
    shape = BottomSheetDefaults.ExpandedShape,
    color = BottomSheetDefaults.ContainerColor,
    tonalElevation = BottomSheetDefaults.Elevation,
  ) {
    content()
  }
}

@Composable
private fun SheetBody() {
  Column(Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
    if (previewOverrideBoolean("handle", true)) {
      Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        BottomSheetDefaults.DragHandle()
      }
    }
    if (previewOverrideBoolean("header", false)) {
      Text(
        "Share to",
        Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp),
        style = MaterialTheme.typography.titleLarge,
      )
    }
    listOf("Share", "Add to favourites", "Report").forEach { label ->
      ListItem(
        headlineContent = { Text(label) },
        leadingContent = { Icon(Icons.Filled.Share, contentDescription = null) },
      )
    }
  }
}

@CatalogComponent(
  id = "BottomSheet/Modal",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/51827:5859",
  caption = "Secondary content anchored to the bottom. Handle and header fold in.",
)
@CatalogModes
@OverrideVariant(name = "no-handle", booleans = ["handle=false"])
@OverrideVariant(name = "header", booleans = ["header=true"])
@OverrideVariant(name = "header-no-handle", booleans = ["header=true", "handle=false"])
@Composable
fun ModalBottomSheetSticker() = Sticker { SheetSurface { SheetBody() } }
