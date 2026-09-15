@file:CatalogGroup(name = "Cards", section = "Containment")

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.preview.CaptureGutter
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.m3catalog.CatalogImagePlaceholder
import ee.schimke.m3catalog.CatalogModesKitContainer
import ee.schimke.m3catalog.KitShadowGutter
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogChoice
import ee.schimke.m3catalog.catalogInteractive
import ee.schimke.m3catalog.catalogText
import ee.schimke.m3catalog.counted
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.action_action
import ee.schimke.m3catalog.generated.resources.action_cancel
import ee.schimke.m3catalog.generated.resources.action_more_options
import ee.schimke.m3catalog.generated.resources.card_header
import ee.schimke.m3catalog.generated.resources.card_subhead
import ee.schimke.m3catalog.generated.resources.card_subtitle
import ee.schimke.m3catalog.generated.resources.card_supporting
import ee.schimke.m3catalog.generated.resources.card_title
import org.jetbrains.compose.resources.stringResource

// M3's cards are the one family shipping BOTH a plain and a clickable overload. The interactive
// lane picks the clickable one; the baked lane composes the plain one, so the published capture
// keeps its exact node tree — otherwise the a11y touch-target greenlines and the layout wireframe
// would gain a clickable node that no longer describes the sticker.
//
// Three emphases (filled / elevated / outlined) and the CONTENT layout, which is the kit's `Layout`
// axis and only that: `Media & text` and `Slot`, its two published values. The `actions` and
// `media+actions` cells that used to sit beside them were the catalog's own invention — no kit node
// answered either, so both resolved to nothing and were compared against nothing, and once the
// default draws the actions the kit's node draws, `media+actions` was the default under a second
// name.

/**
 * The card's content lane, which is the kit's `Layout = Media & text` node read back into Compose:
 * a 72dp header (monogram avatar, header and subhead, an overflow icon button), 188dp of media, and
 * a text block of headline, supporting text and two actions, 32dp apart inside 16dp of padding.
 *
 * It used to draw a 110dp band, a title and a paragraph, and stop — a 360x480 card with its bottom
 * half empty, against a kit node that fills all of it. Everything below is the node's own
 * measurement or its own token, and the numbers are in the code rather than here.
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
  val layout = catalogChoice("layout", "media", "slot", "media")
  if (layout == "slot") return
  val action = counted(catalogText("action", stringResource(Res.string.action_action)))
  val cancel = counted(catalogText("dismissAction", stringResource(Res.string.action_cancel)))
  Column {
    CardHeader()
    // The kit's media cell is the shared `M3/.add-on/placeholder image` graphic, the same one the
    // carousel items and the app bar's image cell draw, at the node's own 188dp. Square, because
    // the card clips it: the corner belongs to the card, not to the media.
    CatalogImagePlaceholder(
      Modifier.fillMaxWidth().height(188.dp),
      shape = RectangleShape,
      scaleBasis = 360f,
    )
    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(32.dp)) {
      Column {
        // Body-large, not title-medium: the kit's headline binds `M3/body/large` for the title and
        // `M3/body/medium` on `on-surface-variant` for the subtitle under it.
        Text(title, style = MaterialTheme.typography.bodyLarge)
        Text(
          catalogText("subtitle", stringResource(Res.string.card_subtitle)),
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
      Text(
        catalogText("supportingText", stringResource(Res.string.card_supporting)),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      // The kit's actions are an OUTLINED secondary and a FILLED primary, trailing-aligned 8dp
      // apart — not the two text buttons this used to draw.
      Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        OutlinedButton(onClick = cancel.onClick) { Text(cancel.label) }
        Button(onClick = action.onClick) { Text(action.label) }
      }
    }
  }
}

/**
 * The kit's card header: a 40dp monogram avatar, the header and subhead text, and a standard icon
 * button, in a 72dp row inset 16dp at the start and 4dp at the end.
 *
 * The avatar is drawn here rather than composed from a Material API because there is none —
 * `Generic avatar / Style=Monogram` is a kit building block, and Material 3 publishes no avatar
 * composable to invoke. It is slot CONTENT, like [CatalogImagePlaceholder], not the component under
 * comparison: the card is, and the card is still a real `Card`.
 */
@Composable
private fun CardHeader() {
  val more = counted(stringResource(Res.string.action_more_options))
  Row(
    Modifier.fillMaxWidth().height(72.dp).padding(start = 16.dp, end = 4.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Surface(
      modifier = Modifier.size(40.dp),
      shape = CircleShape,
      color = MaterialTheme.colorScheme.primaryContainer,
      contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    ) {
      Box(contentAlignment = Alignment.Center) {
        Text("A", style = MaterialTheme.typography.titleMedium)
      }
    }
    Spacer(Modifier.width(16.dp))
    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
      Text(
        catalogText("header", stringResource(Res.string.card_header)),
        style = MaterialTheme.typography.titleMedium,
      )
      Text(
        catalogText("subhead", stringResource(Res.string.card_subhead)),
        style = MaterialTheme.typography.bodyMedium,
      )
    }
    IconButton(onClick = more.onClick) {
      Icon(Icons.Filled.MoreVert, contentDescription = more.label)
    }
  }
}

@CatalogComponent(
  id = "Card/Filled",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/52350:27738",
  caption =
    "Default container for related content: header, media, text and actions. The kit's empty " +
      "slot layout folds in.",
)
@CatalogModesKitContainer
@ee.schimke.composeai.preview.OverrideVariant(name = "slot", strings = ["layout=slot"])
@Composable
fun FilledCard() = Sticker {
  val c = counted(catalogText("title", stringResource(Res.string.card_title)))
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
    "Separated by shadow; header, media, text and actions. The kit's empty slot layout folds " +
      "in.",
)
@CatalogModesKitContainer
@ee.schimke.composeai.preview.OverrideVariant(name = "slot", strings = ["layout=slot"])
// The Level 1 shadow falls outside the card's bounds, so it needs room in the CAPTURE — not a
// padded `Box`, which would measure the card in a smaller frame and publish a canvas 8dp wider
// than every other card on the sheet (#179).
@CaptureGutter(all = KitShadowGutter.Level1All)
@Composable
fun ElevatedCardSticker() = Sticker {
  val c = counted(catalogText("title", stringResource(Res.string.card_title)))
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
    "Separated by outline; header, media, text and actions. The kit's empty slot layout folds " +
      "in.",
)
@CatalogModesKitContainer
@ee.schimke.composeai.preview.OverrideVariant(name = "slot", strings = ["layout=slot"])
@Composable
fun OutlinedCardSticker() = Sticker {
  val c = counted(catalogText("title", stringResource(Res.string.card_title)))
  if (catalogInteractive()) {
    OutlinedCard(onClick = c.onClick, modifier = Modifier.width(360.dp).height(480.dp)) {
      CardBody(c.label)
    }
  } else {
    OutlinedCard(Modifier.width(360.dp).height(480.dp)) { CardBody(c.label) }
  }
}
