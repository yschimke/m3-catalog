@file:CatalogGroup(name = "Divider", section = "Containment")

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.overrides.previewOverrideBoolean
import ee.schimke.composeai.overrides.previewOverrideString
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.divider_above
import ee.schimke.m3catalog.generated.resources.divider_below
import ee.schimke.m3catalog.generated.resources.divider_left
import ee.schimke.m3catalog.generated.resources.divider_right
import ee.schimke.m3catalog.generated.resources.divider_section
import org.jetbrains.compose.resources.stringResource

// A divider is a static separator: no state, nothing to click, so it ships no handler.
//
// The kit's default instances are the divider itself with no surrounding sample copy. Inset folds
// onto the horizontal form.

@CatalogComponent(
  id = "Divider/Horizontal",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/51816:5860",
  caption = "Separates content in a vertical list. Inset and subhead fold in.",
)
@CatalogModes
@OverrideVariant(name = "inset", strings = ["inset=16"])
@OverrideVariant(name = "context", booleans = ["context=true"])
@OverrideVariant(name = "subhead", booleans = ["subhead=true"])
@Composable
fun HorizontalDividerSticker() = Sticker {
  val inset = previewOverrideString("inset", "0").toIntOrNull() ?: 0
  Column(Modifier.width(280.dp)) {
    if (previewOverrideBoolean("context", false)) {
      Text(stringResource(Res.string.divider_above), Modifier.padding(vertical = 8.dp))
    }
    if (previewOverrideBoolean("subhead", false)) {
      Text(
        stringResource(Res.string.divider_section),
        Modifier.padding(start = inset.dp, top = 8.dp, bottom = 4.dp),
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
      )
    }
    HorizontalDivider(Modifier.padding(start = inset.dp))
    if (previewOverrideBoolean("context", false)) {
      Text(stringResource(Res.string.divider_below), Modifier.padding(vertical = 8.dp))
    }
  }
}

@CatalogComponent(
  id = "Divider/Vertical",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/51816:5862",
  caption = "Separates content in a horizontal row.",
)
@CatalogModes
@OverrideVariant(name = "context", booleans = ["context=true"])
@Composable
fun VerticalDividerSticker() = Sticker {
  Row(Modifier.height(120.dp)) {
    if (previewOverrideBoolean("context", false)) {
      Text(stringResource(Res.string.divider_left), Modifier.padding(horizontal = 8.dp))
    }
    VerticalDivider()
    if (previewOverrideBoolean("context", false)) {
      Text(stringResource(Res.string.divider_right), Modifier.padding(horizontal = 8.dp))
    }
  }
}
