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
import ee.schimke.composeai.preview.CaptureGutter
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.m3catalog.CatalogModesKitContainer
import ee.schimke.m3catalog.KitShadowGutter
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogChoice
import ee.schimke.m3catalog.catalogInteractive
import ee.schimke.m3catalog.counted
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.action_action
import ee.schimke.m3catalog.generated.resources.action_cancel
import ee.schimke.m3catalog.generated.resources.card_supporting
import ee.schimke.m3catalog.generated.resources.card_title
import org.jetbrains.compose.resources.stringResource

// M3's cards are the one family shipping BOTH a plain and a clickable overload. The interactive
// lane picks the clickable one; the baked lane composes the plain one, so the published capture
// keeps its exact node tree — otherwise the a11y touch-target greenlines and the layout wireframe
// would gain a clickable node that no longer describes the sticker.
//
// Three emphases (filled / elevated / outlined) and the CONTENT layout: the kit's `Layout` axis
// (`Media & text` and `Slot`), plus the action layouts the guidelines document.

/**
 * The card's content lane. `media` is the default because that is what the kit's `Layout = Media &
 * text` node draws, and all three emphases map to it.
 *
 * `slot` publishes an **empty container** on purpose. The kit's `Layout = Slot` node fills the card
 * with Figma's slot placeholder — a dashed boundary captioned "Replace this subcomponent in the
 * variant properties with one you've built locally" — which is authoring chrome addressed to
 * someone editing the kit, not content the kit specifies for a card. Compose has no equivalent to
 * draw and no reason to invent one: `Card { }` with an empty content lambda IS the Compose reading
 * of that node, so the render is a bare container and the divergence is this comment plus the
 * captions below.
 */
@Composable
private fun CardBody(title: String) {
  val layout = catalogChoice("layout", "media", "slot", "media", "actions", "media+actions")
  if (layout == "slot") return
  val action = counted(stringResource(Res.string.action_action))
  val cancel = counted(stringResource(Res.string.action_cancel))
  Column {
    if (layout == "media" || layout == "media+actions") {
      // surfaceContainerHigh, not surfaceVariant: the kit's media placeholder binds
      // `Schemes/Surface Container High` (#ece6f0). The two are a shade apart against most
      // containers, but against the FILLED card (surfaceContainerHighest) surfaceVariant is
      // near-invisible, so the kit's own token is also the one that reads.
      Box(
        Modifier.fillMaxWidth()
          .height(110.dp)
          .background(MaterialTheme.colorScheme.surfaceContainerHigh)
      )
    }
    Column(Modifier.padding(16.dp)) {
      Text(title, style = MaterialTheme.typography.titleMedium)
      Spacer(Modifier.height(4.dp))
      Text(stringResource(Res.string.card_supporting), style = MaterialTheme.typography.bodyMedium)
    }
    if (layout == "actions" || layout == "media+actions") {
      Row(Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp)) {
        TextButton(onClick = action.onClick) { Text(action.label) }
        TextButton(onClick = cancel.onClick) { Text(cancel.label) }
      }
    }
  }
}

@CatalogComponent(
  id = "Card/Filled",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/52350:27738",
  caption =
    "Default container for related content, with media and text by default. The kit's empty " +
      "slot layout and the action layouts fold in.",
)
@CatalogModesKitContainer
@ee.schimke.composeai.preview.OverrideVariant(name = "slot", strings = ["layout=slot"])
@ee.schimke.composeai.preview.OverrideVariant(name = "actions", strings = ["layout=actions"])
@ee.schimke.composeai.preview.OverrideVariant(
  name = "media-actions",
  strings = ["layout=media+actions"],
)
@Composable
fun FilledCard() = Sticker {
  val c = counted(stringResource(Res.string.card_title))
  if (catalogInteractive()) {
    Card(onClick = c.onClick, modifier = Modifier.width(360.dp).height(480.dp)) {
      CardBody(c.label)
    }
  } else {
    Card(Modifier.width(360.dp).height(480.dp)) { CardBody(c.label) }
  }
}

@CatalogComponent(
  id = "Card/Elevated",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/52350:27693",
  caption =
    "Separated by shadow, with media and text by default. The kit's empty slot layout and the " +
      "action layouts fold in.",
)
@CatalogModesKitContainer
@ee.schimke.composeai.preview.OverrideVariant(name = "slot", strings = ["layout=slot"])
@ee.schimke.composeai.preview.OverrideVariant(name = "actions", strings = ["layout=actions"])
@ee.schimke.composeai.preview.OverrideVariant(
  name = "media-actions",
  strings = ["layout=media+actions"],
)
// The Level 1 shadow falls outside the card's bounds, so it needs room in the CAPTURE — not a
// padded `Box`, which would measure the card in a smaller frame and publish a canvas 8dp wider
// than every other card on the sheet (#179).
@CaptureGutter(all = KitShadowGutter.Level1All)
@Composable
fun ElevatedCardSticker() = Sticker {
  val c = counted(stringResource(Res.string.card_title))
  if (catalogInteractive()) {
    ElevatedCard(onClick = c.onClick, modifier = Modifier.width(360.dp).height(480.dp)) {
      CardBody(c.label)
    }
  } else {
    ElevatedCard(Modifier.width(360.dp).height(480.dp)) { CardBody(c.label) }
  }
}

@CatalogComponent(
  id = "Card/Outlined",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/52346:27574",
  caption =
    "Separated by outline, with media and text by default. The kit's empty slot layout and the " +
      "action layouts fold in.",
)
@CatalogModesKitContainer
@ee.schimke.composeai.preview.OverrideVariant(name = "slot", strings = ["layout=slot"])
@ee.schimke.composeai.preview.OverrideVariant(name = "actions", strings = ["layout=actions"])
@ee.schimke.composeai.preview.OverrideVariant(
  name = "media-actions",
  strings = ["layout=media+actions"],
)
@Composable
fun OutlinedCardSticker() = Sticker {
  val c = counted(stringResource(Res.string.card_title))
  if (catalogInteractive()) {
    OutlinedCard(onClick = c.onClick, modifier = Modifier.width(360.dp).height(480.dp)) {
      CardBody(c.label)
    }
  } else {
    OutlinedCard(Modifier.width(360.dp).height(480.dp)) { CardBody(c.label) }
  }
}
