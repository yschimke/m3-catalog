@file:CatalogGroup(name = "Cards", section = "Containment")

package ee.schimke.m3catalog.uid

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import ee.schimke.composeai.preview.CaptureGutter
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant
import ee.schimke.m3catalog.KitShadowGutter
import ee.schimke.m3catalog.Sticker
import generated.uibuilder.KitCard
import generated.uibuilder.KitCardElevated
import generated.uibuilder.KitCardElevatedSlot
import generated.uibuilder.KitCardOutlined
import generated.uibuilder.KitCardOutlinedSlot
import generated.uibuilder.KitCardSlot

// All six bodies below are ONE document, `stickers/card.uid`. Emphasis is its card node's `style`
// property and the kit's empty `Slot` layout is one `deleteNode`; `stickers.json` spells each cell
// as the builder operations that make it, and `generateUidStickers` exports each patched document
// with the UI builder's own exporter. What a reader sees as a sticker's code is therefore the
// builder's export, with nothing to strip. This file only frames it: annotations and `Sticker`.
//
// The modes are spelled out because discovery does not follow `:catalog`'s
// `@CatalogModesKitContainer` across the module boundary.

@CatalogComponent(
  id = "Card/Filled",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/52350:27738",
  caption = "Default container for related content: header, media, text and actions.",
)
@Preview(name = "Light", group = "modes", widthDp = 360)
@Preview(name = "Dark", uiMode = 32, group = "modes", widthDp = 360)
@Composable
fun FilledCardUid() = Sticker { KitCard() }

@CatalogVariant(of = "Card/Filled", props = ["layout=slot"], caption = "The kit's slot layout.")
@Preview(name = "Light", group = "modes", widthDp = 360)
@Preview(name = "Dark", uiMode = 32, group = "modes", widthDp = 360)
@Composable
fun FilledCardUidSlot() = Sticker { KitCardSlot() }

@CatalogComponent(
  id = "Card/Elevated",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/52350:27693",
  caption = "Separated by shadow; header, media, text and actions.",
)
@Preview(name = "Light", group = "modes", widthDp = 360)
@Preview(name = "Dark", uiMode = 32, group = "modes", widthDp = 360)
@CaptureGutter(all = KitShadowGutter.Level1All)
@Composable
fun ElevatedCardUid() = Sticker { KitCardElevated() }

@CatalogVariant(of = "Card/Elevated", props = ["layout=slot"], caption = "The kit's slot layout.")
@Preview(name = "Light", group = "modes", widthDp = 360)
@Preview(name = "Dark", uiMode = 32, group = "modes", widthDp = 360)
@CaptureGutter(all = KitShadowGutter.Level1All)
@Composable
fun ElevatedCardUidSlot() = Sticker { KitCardElevatedSlot() }

@CatalogComponent(
  id = "Card/Outlined",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/52346:27574",
  caption = "Separated by outline; header, media, text and actions.",
)
@Preview(name = "Light", group = "modes", widthDp = 360)
@Preview(name = "Dark", uiMode = 32, group = "modes", widthDp = 360)
@Composable
fun OutlinedCardUid() = Sticker { KitCardOutlined() }

@CatalogVariant(of = "Card/Outlined", props = ["layout=slot"], caption = "The kit's slot layout.")
@Preview(name = "Light", group = "modes", widthDp = 360)
@Preview(name = "Dark", uiMode = 32, group = "modes", widthDp = 360)
@Composable
fun OutlinedCardUidSlot() = Sticker { KitCardOutlinedSlot() }

// A locale check rather than a sticker: the document's copy is the catalog's string resources, so
// the generated card and the hand-written one must also agree in German. Plain previews, outside
// the inventory; `compare-published.py` diffs the pair.

@Preview(name = "German", locale = "de", widthDp = 360)
@Composable
fun FilledCardUidGerman() = Sticker { KitCard() }

@Preview(name = "German", locale = "de", widthDp = 360)
@Composable
fun FilledCardHandWrittenGerman() = ee.schimke.m3catalog.sections.FilledCard()
