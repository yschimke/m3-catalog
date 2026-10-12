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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.preview.CaptureGutter
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogImagePlaceholder
import ee.schimke.m3catalog.CatalogModesKitContainer
import ee.schimke.m3catalog.Counted
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
import generated.uibuilder.KitCard
import generated.uibuilder.KitCardElevated
import generated.uibuilder.KitCardElevatedSlot
import generated.uibuilder.KitCardOutlined
import generated.uibuilder.KitCardOutlinedSlot
import generated.uibuilder.KitCardSlot
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

// --- Generated sticker bodies, from uid-stickers/Cards.kt ---

/** The card's live knobs, read whatever the layout, as the hand-written sticker reads them. */
private class CardKnobs(
  val title: String,
  val slot: Boolean,
  val action: Counted,
  val dismiss: Counted,
  val header: String,
  val subhead: String,
  val more: Counted,
  val subtitle: String,
  val supportingText: String,
)

@Composable
private fun cardKnobs(): CardKnobs =
  CardKnobs(
    title = catalogText("title", stringResource(Res.string.card_title)),
    slot = catalogChoice("layout", "media", "slot", "media") == "slot",
    action = counted(catalogText("action", stringResource(Res.string.action_action))),
    dismiss = counted(catalogText("dismissAction", stringResource(Res.string.action_cancel))),
    header = catalogText("header", stringResource(Res.string.card_header)),
    subhead = catalogText("subhead", stringResource(Res.string.card_subhead)),
    more = counted(stringResource(Res.string.action_more_options)),
    subtitle = catalogText("subtitle", stringResource(Res.string.card_subtitle)),
    supportingText = catalogText("supportingText", stringResource(Res.string.card_supporting)),
  )

/** One emphasis: its generated media card and slot card, chosen by the live `layout` knob. */
@Composable
private fun KitCardSticker(slot: @Composable () -> Unit, media: @Composable (CardKnobs) -> Unit) =
  Sticker {
    val knobs = cardKnobs()
    if (knobs.slot) slot() else media(knobs)
  }

@CatalogComponent(
  id = "Card/Filled",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/52350:27738",
  caption =
    "Default container for related content: header, media, text and actions. The kit's empty " +
      "slot layout folds in.",
)
@CatalogModesKitContainer
@OverrideVariant(name = "slot", strings = ["layout=slot"])
@Composable
fun FilledCard() =
  KitCardSticker(slot = { KitCardSlot(Modifier) }) { k ->
    KitCard(
      Modifier,
      header = k.header,
      subhead = k.subhead,
      title = k.title,
      subtitle = k.subtitle,
      supportingText = k.supportingText,
      dismissAction = k.dismiss.label,
      action = k.action.label,
      onMoreClick = k.more.onClick,
      onCancelClick = k.dismiss.onClick,
      onActionClick = k.action.onClick,
    )
  }

@CatalogComponent(
  id = "Card/Elevated",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/52350:27693",
  caption =
    "Separated by shadow; header, media, text and actions. The kit's empty slot layout folds " +
      "in.",
)
@CatalogModesKitContainer
@OverrideVariant(name = "slot", strings = ["layout=slot"])
// The Level 1 shadow falls outside the card's bounds, so it needs room in the CAPTURE — not a
// padded `Box`, which would measure the card in a smaller frame and publish a canvas 8dp wider
// than every other card on the sheet (#179).
@CaptureGutter(all = KitShadowGutter.Level1All)
@Composable
fun ElevatedCardSticker() =
  KitCardSticker(slot = { KitCardElevatedSlot(Modifier) }) { k ->
    KitCardElevated(
      Modifier,
      header = k.header,
      subhead = k.subhead,
      title = k.title,
      subtitle = k.subtitle,
      supportingText = k.supportingText,
      dismissAction = k.dismiss.label,
      action = k.action.label,
      onMoreClick = k.more.onClick,
      onCancelClick = k.dismiss.onClick,
      onActionClick = k.action.onClick,
    )
  }

@CatalogComponent(
  id = "Card/Outlined",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/52346:27574",
  caption =
    "Separated by outline; header, media, text and actions. The kit's empty slot layout folds " +
      "in.",
)
@CatalogModesKitContainer
@OverrideVariant(name = "slot", strings = ["layout=slot"])
@Composable
fun OutlinedCardSticker() =
  KitCardSticker(slot = { KitCardOutlinedSlot(Modifier) }) { k ->
    KitCardOutlined(
      Modifier,
      header = k.header,
      subhead = k.subhead,
      title = k.title,
      subtitle = k.subtitle,
      supportingText = k.supportingText,
      dismissAction = k.dismiss.label,
      action = k.action.label,
      onMoreClick = k.more.onClick,
      onCancelClick = k.dismiss.onClick,
      onActionClick = k.action.onClick,
    )
  }
