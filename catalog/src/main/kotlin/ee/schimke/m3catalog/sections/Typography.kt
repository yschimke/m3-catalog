@file:CatalogGroup(name = "Typography", section = "Styles")

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.overrides.previewOverrideString
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker

// Typography role names are design-system token names, so they deliberately remain literals.

@Composable
private fun typeScaleRole(): Pair<String, TextStyle> =
  when (previewOverrideString("style", "display-large")) {
    "display-medium" -> "Display Medium" to MaterialTheme.typography.displayMedium
    "display-small" -> "Display Small" to MaterialTheme.typography.displaySmall
    "headline-large" -> "Headline Large" to MaterialTheme.typography.headlineLarge
    "headline-medium" -> "Headline Medium" to MaterialTheme.typography.headlineMedium
    "headline-small" -> "Headline Small" to MaterialTheme.typography.headlineSmall
    "title-large" -> "Title Large" to MaterialTheme.typography.titleLarge
    "title-medium" -> "Title Medium" to MaterialTheme.typography.titleMedium
    "title-small" -> "Title Small" to MaterialTheme.typography.titleSmall
    "body-large" -> "Body Large" to MaterialTheme.typography.bodyLarge
    "body-medium" -> "Body Medium" to MaterialTheme.typography.bodyMedium
    "body-small" -> "Body Small" to MaterialTheme.typography.bodySmall
    "label-large" -> "Label Large" to MaterialTheme.typography.labelLarge
    "label-medium" -> "Label Medium" to MaterialTheme.typography.labelMedium
    "label-small" -> "Label Small" to MaterialTheme.typography.labelSmall
    else -> "Display Large" to MaterialTheme.typography.displayLarge
  }

@CatalogComponent(
  id = "Typography/Type scale",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58186:19244",
  caption = "The Material 3 type scale, with each of its fifteen roles folded in as a variant.",
)
@CatalogModes
@OverrideVariant(name = "display-medium", strings = ["style=display-medium"])
@OverrideVariant(name = "display-small", strings = ["style=display-small"])
@OverrideVariant(name = "headline-large", strings = ["style=headline-large"])
@OverrideVariant(name = "headline-medium", strings = ["style=headline-medium"])
@OverrideVariant(name = "headline-small", strings = ["style=headline-small"])
@OverrideVariant(name = "title-large", strings = ["style=title-large"])
@OverrideVariant(name = "title-medium", strings = ["style=title-medium"])
@OverrideVariant(name = "title-small", strings = ["style=title-small"])
@OverrideVariant(name = "body-large", strings = ["style=body-large"])
@OverrideVariant(name = "body-medium", strings = ["style=body-medium"])
@OverrideVariant(name = "body-small", strings = ["style=body-small"])
@OverrideVariant(name = "label-large", strings = ["style=label-large"])
@OverrideVariant(name = "label-medium", strings = ["style=label-medium"])
@OverrideVariant(name = "label-small", strings = ["style=label-small"])
@Composable
fun TypeScaleRoleSticker() = Sticker {
  val (name, style) = typeScaleRole()
  Box(Modifier.width(400.dp).padding(vertical = 8.dp)) { Text(name, style = style) }
}
