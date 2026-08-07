@file:CatalogGroup(name = "Side sheets", section = "Containment")
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import ee.schimke.m3catalog.counted

// Compose Material 3 has no side-sheet component of its own — the kit's side sheet is a themed
// container, so the sticker builds it from `Surface` + the shape/colour roles the spec names. It is
// catalogued because the Figma kit ships it, and captioned so nobody mistakes it for an API.

@CatalogComponent(
  id = "SideSheet/Standard",
  caption = "Supporting pane anchored to the side (composed from Surface; no M3 Compose API).",
)
@CatalogModes
@Composable
fun StandardSideSheet() = Sticker {
  val close = counted("Close")
  Surface(
    modifier = Modifier.width(256.dp).height(280.dp),
    color = MaterialTheme.colorScheme.surfaceContainerLow,
    shape = MaterialTheme.shapes.large,
  ) {
    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text("Details", style = MaterialTheme.typography.titleMedium)
        Column(Modifier.weight(1f)) {}
        IconButton(onClick = close.onClick) {
          Icon(Icons.Filled.Close, contentDescription = close.label)
        }
      }
      Text(
        "Supporting content that sits beside the main pane.",
        style = MaterialTheme.typography.bodyMedium,
      )
    }
  }
}
