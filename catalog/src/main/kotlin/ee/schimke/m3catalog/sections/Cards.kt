@file:CatalogGroup(name = "Cards", section = "Containment")

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.overrides.previewOverrideString
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogInteractive
import ee.schimke.m3catalog.counted

// M3's cards are the one family shipping BOTH a plain and a clickable overload. The interactive
// lane picks the clickable one; the baked lane composes the plain one, so the published capture
// keeps its exact node tree — otherwise the a11y touch-target greenlines and the layout wireframe
// would gain a clickable node that no longer describes the sticker.
//
// Three emphases (filled / elevated / outlined) and the CONTENT layout the kit documents: text
// only, with media, and with actions.

@Composable
private fun CardBody(title: String) {
  val layout = previewOverrideString("layout", "text")
  Column {
    if (layout == "media" || layout == "media+actions") {
      Box(
        Modifier.fillMaxWidth().height(110.dp).background(MaterialTheme.colorScheme.surfaceVariant)
      )
    }
    Column(Modifier.padding(16.dp)) {
      Text(title, style = MaterialTheme.typography.titleMedium)
      Spacer(Modifier.height(4.dp))
      Text("Supporting text for the card.", style = MaterialTheme.typography.bodyMedium)
    }
    if (layout == "actions" || layout == "media+actions") {
      Row(Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp)) {
        TextButton(onClick = {}) { Text("Action") }
        TextButton(onClick = {}) { Text("Cancel") }
      }
    }
  }
}

@CatalogComponent(
  id = "Card/Filled",
  caption = "Default container for related content. Media and action layouts fold in.",
)
@CatalogModes
@ee.schimke.composeai.preview.OverrideVariant(name = "media", strings = ["layout=media"])
@ee.schimke.composeai.preview.OverrideVariant(name = "actions", strings = ["layout=actions"])
@ee.schimke.composeai.preview.OverrideVariant(
  name = "media-actions",
  strings = ["layout=media+actions"],
)
@Composable
fun FilledCard() = Sticker {
  val c = counted("Filled card")
  if (catalogInteractive()) {
    Card(onClick = c.onClick, modifier = Modifier.width(280.dp)) { CardBody(c.label) }
  } else {
    Card(Modifier.width(280.dp)) { CardBody(c.label) }
  }
}

@CatalogComponent(
  id = "Card/Elevated",
  caption = "Separated by shadow. Media and action layouts fold in.",
)
@CatalogModes
@ee.schimke.composeai.preview.OverrideVariant(name = "media", strings = ["layout=media"])
@ee.schimke.composeai.preview.OverrideVariant(name = "actions", strings = ["layout=actions"])
@ee.schimke.composeai.preview.OverrideVariant(
  name = "media-actions",
  strings = ["layout=media+actions"],
)
@Composable
fun ElevatedCardSticker() = Sticker {
  ElevatedCard(Modifier.width(280.dp)) { CardBody("Elevated card") }
}

@CatalogComponent(
  id = "Card/Outlined",
  caption = "Separated by outline. Media and action layouts fold in.",
)
@CatalogModes
@ee.schimke.composeai.preview.OverrideVariant(name = "media", strings = ["layout=media"])
@ee.schimke.composeai.preview.OverrideVariant(name = "actions", strings = ["layout=actions"])
@ee.schimke.composeai.preview.OverrideVariant(
  name = "media-actions",
  strings = ["layout=media+actions"],
)
@Composable
fun OutlinedCardSticker() = Sticker {
  OutlinedCard(Modifier.width(280.dp)) { CardBody("Outlined card") }
}
