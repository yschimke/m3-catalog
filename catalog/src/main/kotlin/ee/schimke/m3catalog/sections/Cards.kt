@file:CatalogGroup(name = "Cards", section = "Containment")
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogInteractive
import ee.schimke.m3catalog.counted

// M3's cards are the one family that ships **both** a plain and a clickable overload. The
// interactive lane picks the clickable one; the baked lane composes the plain one it always did,
// so the published capture keeps its exact node tree — otherwise the a11y touch-target greenlines
// and the layout wireframe would gain a clickable node that no longer describes the sticker.

@Composable
private fun CardBody(title: String, body: String) {
  Column(Modifier.padding(16.dp)) {
    Text(title, style = MaterialTheme.typography.titleMedium)
    Text(body, style = MaterialTheme.typography.bodyMedium)
  }
}

@CatalogComponent(id = "Card/Filled", caption = "Default container for related content.")
@CatalogModes
@Composable
fun FilledCard() = Sticker {
  val c = counted("Filled card")
  if (catalogInteractive()) {
    Card(onClick = c.onClick, modifier = Modifier.width(280.dp)) {
      CardBody(c.label, "Supporting text for the card.")
    }
  } else {
    Card(Modifier.width(280.dp)) { CardBody(c.label, "Supporting text for the card.") }
  }
}

@CatalogVariant(of = "Card/Filled", props = ["emphasis=elevated"], caption = "Separated by shadow.")
@CatalogModes
@Composable
fun ElevatedCardSticker() = Sticker {
  ElevatedCard(Modifier.width(280.dp)) { CardBody("Elevated card", "Supporting text.") }
}

@CatalogVariant(
  of = "Card/Filled",
  props = ["emphasis=outlined"],
  caption = "Separated by outline.",
)
@CatalogModes
@Composable
fun OutlinedCardSticker() = Sticker {
  OutlinedCard(Modifier.width(280.dp)) { CardBody("Outlined card", "Supporting text.") }
}
