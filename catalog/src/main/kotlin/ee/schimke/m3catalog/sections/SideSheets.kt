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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.overrides.previewOverrideBoolean
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.counted
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.action_cancel
import ee.schimke.m3catalog.generated.resources.action_close
import ee.schimke.m3catalog.generated.resources.action_save
import ee.schimke.m3catalog.generated.resources.sheet_details
import ee.schimke.m3catalog.generated.resources.sheet_supporting
import org.jetbrains.compose.resources.stringResource

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
@OverrideVariant(name = "no-footer", booleans = ["footer=false"])
@OverrideVariant(name = "no-header-no-footer", booleans = ["header=false", "footer=false"])
@Composable
fun StandardSideSheet() = Sticker {
  val close = counted(stringResource(Res.string.action_close))
  val cancel = counted(stringResource(Res.string.action_cancel))
  val save = counted(stringResource(Res.string.action_save))
  Surface(
    modifier = Modifier.width(320.dp).height(700.dp),
    color = MaterialTheme.colorScheme.surface,
    shape = RectangleShape,
  ) {
    Column {
      if (previewOverrideBoolean("header", true)) {
        Row(
          Modifier.fillMaxWidth().padding(start = 24.dp, top = 12.dp, end = 12.dp, bottom = 16.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            stringResource(Res.string.sheet_details),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
          Column(Modifier.weight(1f)) {}
          IconButton(onClick = close.onClick) {
            Icon(Icons.Filled.Close, contentDescription = close.label)
          }
        }
      }
      Text(
        stringResource(Res.string.sheet_supporting),
        modifier = Modifier.weight(1f).padding(horizontal = 24.dp),
        style = MaterialTheme.typography.bodyMedium,
      )
      if (previewOverrideBoolean("footer", true)) {
        HorizontalDivider()
        Row(
          Modifier.fillMaxWidth().padding(start = 24.dp, top = 12.dp, end = 24.dp, bottom = 20.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Button(onClick = save.onClick) { Text(save.label) }
          OutlinedButton(onClick = cancel.onClick) { Text(cancel.label) }
        }
      }
    }
  }
}
