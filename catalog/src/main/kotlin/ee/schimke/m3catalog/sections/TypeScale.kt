@file:CatalogGroup(name = "Typography", section = "Styles")

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker

// The M3 type scale, drawn in the scale itself: each specimen is set in the style it names, so the
// sheet can't claim a size it doesn't render.

@Composable
private fun Scale(rows: List<Pair<String, TextStyle>>) {
  Column(Modifier.width(420.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
    rows.forEach { (name, style) -> Text(name, style = style) }
  }
}

@CatalogComponent(
  id = "Typography/Display",
  caption = "Display large, medium and small.",
  noReference =
    "no component or component set in the kit publishes this; Material ships its type and elevation scales as Figma styles, which a node reference cannot address",
)
@CatalogModes
@Composable
fun DisplayScale() = Sticker {
  val t = MaterialTheme.typography
  Scale(
    listOf(
      "Display Large" to t.displayLarge,
      "Display Medium" to t.displayMedium,
      "Display Small" to t.displaySmall,
    )
  )
}

@CatalogVariant(of = "Typography/Display", props = ["scale=headline"])
@CatalogModes
@Composable
fun HeadlineScale() = Sticker {
  val t = MaterialTheme.typography
  Scale(
    listOf(
      "Headline Large" to t.headlineLarge,
      "Headline Medium" to t.headlineMedium,
      "Headline Small" to t.headlineSmall,
    )
  )
}

@CatalogVariant(of = "Typography/Display", props = ["scale=title"])
@CatalogModes
@Composable
fun TitleScale() = Sticker {
  val t = MaterialTheme.typography
  Scale(
    listOf(
      "Title Large" to t.titleLarge,
      "Title Medium" to t.titleMedium,
      "Title Small" to t.titleSmall,
    )
  )
}

@CatalogVariant(of = "Typography/Display", props = ["scale=body+label"])
@CatalogModes
@Composable
fun BodyAndLabelScale() = Sticker {
  val t = MaterialTheme.typography
  Scale(
    listOf(
      "Body Large" to t.bodyLarge,
      "Body Medium" to t.bodyMedium,
      "Body Small" to t.bodySmall,
      "Label Large" to t.labelLarge,
      "Label Medium" to t.labelMedium,
      "Label Small" to t.labelSmall,
    )
  )
}
