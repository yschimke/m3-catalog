@file:CatalogGroup(name = "Side sheets", section = "Containment")

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
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import ee.schimke.m3catalog.counted

// Compose Material 3 has NO side-sheet component: the kit ships one, the library does not. So this
// is built from `Surface` plus the shape and colour roles the spec names, and captioned to say so —
// under design-led that is the honest form of "the code cannot express this", rather than silently
// rendering a lookalike and letting parity call it a match.
//
// The kit's axes are the header and the footer action row.

@CatalogComponent(
  id = "SideSheet/Standard",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/53198:27873",
  caption = "Supporting pane anchored to the side. NO M3 Compose API — composed from Surface.",
)
@CatalogModes
@OverrideVariant(name = "no-header", booleans = ["header=false"])
@OverrideVariant(name = "footer", booleans = ["footer=true"])
@OverrideVariant(name = "header-footer", booleans = ["footer=true"])
@Composable
fun StandardSideSheet() = Sticker {
  val close = counted("Close")
  Surface(
    modifier = Modifier.width(256.dp).height(320.dp),
    color = MaterialTheme.colorScheme.surfaceContainerLow,
    shape = MaterialTheme.shapes.large,
  ) {
    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      if (previewOverrideBoolean("header", true)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
          Text("Details", style = MaterialTheme.typography.titleMedium)
          Column(Modifier.weight(1f)) {}
          IconButton(onClick = close.onClick) {
            Icon(Icons.Filled.Close, contentDescription = close.label)
          }
        }
        HorizontalDivider()
      }
      Text(
        "Supporting content that sits beside the main pane.",
        style = MaterialTheme.typography.bodyMedium,
      )
      if (previewOverrideBoolean("footer", false)) {
        Column(Modifier.weight(1f)) {}
        HorizontalDivider()
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
          TextButton(onClick = {}) { Text("Cancel") }
          Button(onClick = {}) { Text("Save") }
        }
      }
    }
  }
}
