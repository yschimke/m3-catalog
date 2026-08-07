@file:CatalogGroup(name = "Bottom sheets", section = "Containment")
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker

// Like a dialog, a modal bottom sheet is hosted in its own window. The sticker composes the sheet
// container itself — `BottomSheetDefaults`' expanded shape, container colour and real drag handle —
// so the published pixels are the component, minus the scrim and the window.

@CatalogComponent(
  id = "BottomSheet/Modal",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/51827:5859",
  caption = "Secondary content anchored to the bottom.",
)
@CatalogModes
@Composable
fun ModalBottomSheetSticker() = Sticker {
  Surface(
    modifier = Modifier.width(360.dp),
    shape = BottomSheetDefaults.ExpandedShape,
    color = BottomSheetDefaults.ContainerColor,
    tonalElevation = BottomSheetDefaults.Elevation,
  ) {
    Column(Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
      Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        BottomSheetDefaults.DragHandle()
      }
      listOf("Share", "Add to favourites", "Report").forEach { label ->
        ListItem(
          headlineContent = { Text(label) },
          leadingContent = { Icon(Icons.Filled.Share, contentDescription = null) },
        )
      }
    }
  }
}
