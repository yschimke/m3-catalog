@file:CatalogGroup(name = "Cards", section = "Containment")

package ee.schimke.m3catalog.uid

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ee.schimke.composeai.preview.CaptureGutter
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModesKitContainer
import ee.schimke.m3catalog.Counted
import ee.schimke.m3catalog.KitShadowGutter
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogChoice
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

// All the bodies below are ONE document, `stickers/card.uid`: a `KitCard` component placed once.
// Emphasis is its card node's `style` property and the kit's empty `Slot` layout is one
// `deleteNode`; `stickers.json` spells each cell as the builder operations that make it, and
// `generateUidStickers` exports each cell's component with the UI builder's own exporter. What a
// reader sees as a sticker's code is therefore the builder's export, with nothing to strip.
//
// The component's parameters are the document's bindings, named for the published knob keys, and
// its buttons' action-free click events. This file supplies them live: `catalogText` for the copy,
// `counted` for the clicks and `catalogChoice` for the `layout` axis, read in the same order as the
// hand-written sticker so the published override keys are unchanged.
//
// Each shell carries its hand-written sticker's annotations verbatim (`annotations-of.py`), so it
// renders the same captures, and `compare-local.py` holds every one to the pixel.

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

// A locale check rather than a sticker: the document's copy is the catalog's string resources, so
// the generated card and the hand-written one must also agree in German. Plain previews, outside
// the inventory; `compare-published.py` diffs the pair.

@Preview(name = "German", locale = "de", widthDp = 360)
@Composable
fun FilledCardUidGerman() = FilledCard()

@Preview(name = "German", locale = "de", widthDp = 360)
@Composable
fun FilledCardHandWrittenGerman() = ee.schimke.m3catalog.sections.FilledCard()
