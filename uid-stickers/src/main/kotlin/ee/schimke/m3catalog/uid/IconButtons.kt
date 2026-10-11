@file:CatalogGroup(name = "Icon buttons", section = "Actions")

package ee.schimke.m3catalog.uid

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.InteractionGesture
import ee.schimke.composeai.preview.InteractionPreview
import ee.schimke.m3catalog.CatalogIconWidth
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.CatalogShape
import ee.schimke.m3catalog.CatalogSize
import ee.schimke.m3catalog.IconButtonMatrix
import ee.schimke.m3catalog.InteractionStates
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogButtonSize
import ee.schimke.m3catalog.catalogEnabled
import ee.schimke.m3catalog.catalogIconWidth
import ee.schimke.m3catalog.toggleable
import generated.uibuilder.KitFilledIconButton
import generated.uibuilder.KitFilledIconButtonLNarrow
import generated.uibuilder.KitFilledIconButtonLNarrowSquare
import generated.uibuilder.KitFilledIconButtonLUniform
import generated.uibuilder.KitFilledIconButtonLUniformSquare
import generated.uibuilder.KitFilledIconButtonLWide
import generated.uibuilder.KitFilledIconButtonLWideSquare
import generated.uibuilder.KitFilledIconButtonMNarrow
import generated.uibuilder.KitFilledIconButtonMNarrowSquare
import generated.uibuilder.KitFilledIconButtonMUniform
import generated.uibuilder.KitFilledIconButtonMUniformSquare
import generated.uibuilder.KitFilledIconButtonMWide
import generated.uibuilder.KitFilledIconButtonMWideSquare
import generated.uibuilder.KitFilledIconButtonSNarrow
import generated.uibuilder.KitFilledIconButtonSNarrowSquare
import generated.uibuilder.KitFilledIconButtonSUniformSquare
import generated.uibuilder.KitFilledIconButtonSWide
import generated.uibuilder.KitFilledIconButtonSWideSquare
import generated.uibuilder.KitFilledIconButtonXlNarrow
import generated.uibuilder.KitFilledIconButtonXlNarrowSquare
import generated.uibuilder.KitFilledIconButtonXlUniform
import generated.uibuilder.KitFilledIconButtonXlUniformSquare
import generated.uibuilder.KitFilledIconButtonXlWide
import generated.uibuilder.KitFilledIconButtonXlWideSquare
import generated.uibuilder.KitFilledIconButtonXsNarrow
import generated.uibuilder.KitFilledIconButtonXsNarrowSquare
import generated.uibuilder.KitFilledIconButtonXsUniform
import generated.uibuilder.KitFilledIconButtonXsUniformSquare
import generated.uibuilder.KitFilledIconButtonXsWide
import generated.uibuilder.KitFilledIconButtonXsWideSquare
import generated.uibuilder.KitTonalIconButton
import generated.uibuilder.KitTonalIconButtonLNarrow
import generated.uibuilder.KitTonalIconButtonLNarrowSquare
import generated.uibuilder.KitTonalIconButtonLUniform
import generated.uibuilder.KitTonalIconButtonLUniformSquare
import generated.uibuilder.KitTonalIconButtonLWide
import generated.uibuilder.KitTonalIconButtonLWideSquare
import generated.uibuilder.KitTonalIconButtonMNarrow
import generated.uibuilder.KitTonalIconButtonMNarrowSquare
import generated.uibuilder.KitTonalIconButtonMUniform
import generated.uibuilder.KitTonalIconButtonMUniformSquare
import generated.uibuilder.KitTonalIconButtonMWide
import generated.uibuilder.KitTonalIconButtonMWideSquare
import generated.uibuilder.KitTonalIconButtonSNarrow
import generated.uibuilder.KitTonalIconButtonSNarrowSquare
import generated.uibuilder.KitTonalIconButtonSUniformSquare
import generated.uibuilder.KitTonalIconButtonSWide
import generated.uibuilder.KitTonalIconButtonSWideSquare
import generated.uibuilder.KitTonalIconButtonXlNarrow
import generated.uibuilder.KitTonalIconButtonXlNarrowSquare
import generated.uibuilder.KitTonalIconButtonXlUniform
import generated.uibuilder.KitTonalIconButtonXlUniformSquare
import generated.uibuilder.KitTonalIconButtonXlWide
import generated.uibuilder.KitTonalIconButtonXlWideSquare
import generated.uibuilder.KitTonalIconButtonXsNarrow
import generated.uibuilder.KitTonalIconButtonXsNarrowSquare
import generated.uibuilder.KitTonalIconButtonXsUniform
import generated.uibuilder.KitTonalIconButtonXsUniformSquare
import generated.uibuilder.KitTonalIconButtonXsWide
import generated.uibuilder.KitTonalIconButtonXsWideSquare

// `stickers/filled-icon-button.uid` and `stickers/tonal-icon-button.uid`: a frame box 8dp larger
// than the container, holding the Material icon button with the favourite glyph
// (`m3catalog/favourite-stars-icon`, filled or outlined by the bound `on`) in its content slot. The
// kit's five sizes x three widths x two shapes are thirty cells per document: a size and width set
// the frame, the container and the glyph sizes, and `square` swaps the button's `shape` from
// `circle` to the theme role `IconButtonDefaults.<size>SquareShape` resolves to (`medium` for XS
// and S, `large` for M, `extraLarge` for L and XL). The click toggles `on`, as the hand-written
// sticker's does.
//
// `IconButton/Standard` and `IconButton/Outlined` are not here: their hand-written stickers pass
// `IconButtonColors` (and the outlined one a `BorderStroke`), which the exporter cannot spell
// (`IconButton has no parameter contentColor`). As with the buttons, the Material 3 Expressive
// overload (`shapes: IconButtonShapes`) has no cell: no capture renders it, and the record is the
// standard overload.

/** The knobs, read in the hand-written sticker's order: size, width, enabled, then shape. */
private class IconButtonKnobs(
  val size: CatalogSize,
  val width: CatalogIconWidth,
  val enabled: Boolean,
  val square: Boolean,
)

@Composable
private fun iconButtonKnobs(): IconButtonKnobs {
  val size = catalogButtonSize()
  val width = catalogIconWidth()
  val enabled = catalogEnabled()
  val square = CatalogShape.Axis.current() == CatalogShape.Square
  return IconButtonKnobs(size, width, enabled, square)
}

@CatalogComponent(
  id = "IconButton/Filled",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/57994:10312",
  caption =
    "Highest emphasis icon-only action. Five sizes x three widths x two shapes fold in as variants.",
)
// PressAndHold rather than Tap: the pressed shape is held for as long as the finger is down, and
// a momentary tap passes through it too fast to read. This is the one capture here whose subject
// is a state rather than a transition.
@InteractionPreview(
  gesture = InteractionGesture.PressAndHold,
  targets = [0],
  caption =
    "Press and hold. Expressive animates the container into its pressed shape and holds it there " +
      "for the duration of the press; Baseline leaves the container static.",
)
@CatalogModes
@IconButtonMatrix
@InteractionStates
@ee.schimke.m3catalog.FilledIconButtonStickerExhaustiveKitCells
@Composable
fun FilledIconButtonSticker() = Sticker {
  var on by toggleable(true)
  val k = iconButtonKnobs()
  val onClick = { on = !on }
  when (k.size) {
    CatalogSize.ExtraSmall ->
      when (k.width) {
        CatalogIconWidth.Narrow ->
          if (k.square)
            KitFilledIconButtonXsNarrowSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitFilledIconButtonXsNarrow(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
        CatalogIconWidth.Uniform ->
          if (k.square)
            KitFilledIconButtonXsUniformSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitFilledIconButtonXsUniform(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
        CatalogIconWidth.Wide ->
          if (k.square)
            KitFilledIconButtonXsWideSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitFilledIconButtonXsWide(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
      }
    CatalogSize.Small ->
      when (k.width) {
        CatalogIconWidth.Narrow ->
          if (k.square)
            KitFilledIconButtonSNarrowSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitFilledIconButtonSNarrow(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
        CatalogIconWidth.Uniform ->
          if (k.square)
            KitFilledIconButtonSUniformSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else KitFilledIconButton(Modifier, enabled = k.enabled, on = on, onButtonClick = onClick)
        CatalogIconWidth.Wide ->
          if (k.square)
            KitFilledIconButtonSWideSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitFilledIconButtonSWide(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
      }
    CatalogSize.Medium ->
      when (k.width) {
        CatalogIconWidth.Narrow ->
          if (k.square)
            KitFilledIconButtonMNarrowSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitFilledIconButtonMNarrow(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
        CatalogIconWidth.Uniform ->
          if (k.square)
            KitFilledIconButtonMUniformSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitFilledIconButtonMUniform(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
        CatalogIconWidth.Wide ->
          if (k.square)
            KitFilledIconButtonMWideSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitFilledIconButtonMWide(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
      }
    CatalogSize.Large ->
      when (k.width) {
        CatalogIconWidth.Narrow ->
          if (k.square)
            KitFilledIconButtonLNarrowSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitFilledIconButtonLNarrow(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
        CatalogIconWidth.Uniform ->
          if (k.square)
            KitFilledIconButtonLUniformSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitFilledIconButtonLUniform(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
        CatalogIconWidth.Wide ->
          if (k.square)
            KitFilledIconButtonLWideSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitFilledIconButtonLWide(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
      }
    CatalogSize.ExtraLarge ->
      when (k.width) {
        CatalogIconWidth.Narrow ->
          if (k.square)
            KitFilledIconButtonXlNarrowSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitFilledIconButtonXlNarrow(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
        CatalogIconWidth.Uniform ->
          if (k.square)
            KitFilledIconButtonXlUniformSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitFilledIconButtonXlUniform(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
        CatalogIconWidth.Wide ->
          if (k.square)
            KitFilledIconButtonXlWideSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitFilledIconButtonXlWide(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
      }
  }
}

@CatalogComponent(
  id = "IconButton/Tonal",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/57994:10252",
  caption = "Secondary emphasis. Five sizes x three widths x two shapes fold in as variants.",
)
@CatalogModes
@IconButtonMatrix
@InteractionStates
@ee.schimke.m3catalog.TonalIconButtonExhaustiveKitCells
@Composable
fun TonalIconButton() = Sticker {
  var on by toggleable(true)
  val k = iconButtonKnobs()
  val onClick = { on = !on }
  when (k.size) {
    CatalogSize.ExtraSmall ->
      when (k.width) {
        CatalogIconWidth.Narrow ->
          if (k.square)
            KitTonalIconButtonXsNarrowSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitTonalIconButtonXsNarrow(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
        CatalogIconWidth.Uniform ->
          if (k.square)
            KitTonalIconButtonXsUniformSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitTonalIconButtonXsUniform(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
        CatalogIconWidth.Wide ->
          if (k.square)
            KitTonalIconButtonXsWideSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitTonalIconButtonXsWide(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
      }
    CatalogSize.Small ->
      when (k.width) {
        CatalogIconWidth.Narrow ->
          if (k.square)
            KitTonalIconButtonSNarrowSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitTonalIconButtonSNarrow(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
        CatalogIconWidth.Uniform ->
          if (k.square)
            KitTonalIconButtonSUniformSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else KitTonalIconButton(Modifier, enabled = k.enabled, on = on, onButtonClick = onClick)
        CatalogIconWidth.Wide ->
          if (k.square)
            KitTonalIconButtonSWideSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitTonalIconButtonSWide(Modifier, enabled = k.enabled, on = on, onButtonClick = onClick)
      }
    CatalogSize.Medium ->
      when (k.width) {
        CatalogIconWidth.Narrow ->
          if (k.square)
            KitTonalIconButtonMNarrowSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitTonalIconButtonMNarrow(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
        CatalogIconWidth.Uniform ->
          if (k.square)
            KitTonalIconButtonMUniformSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitTonalIconButtonMUniform(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
        CatalogIconWidth.Wide ->
          if (k.square)
            KitTonalIconButtonMWideSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitTonalIconButtonMWide(Modifier, enabled = k.enabled, on = on, onButtonClick = onClick)
      }
    CatalogSize.Large ->
      when (k.width) {
        CatalogIconWidth.Narrow ->
          if (k.square)
            KitTonalIconButtonLNarrowSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitTonalIconButtonLNarrow(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
        CatalogIconWidth.Uniform ->
          if (k.square)
            KitTonalIconButtonLUniformSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitTonalIconButtonLUniform(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
        CatalogIconWidth.Wide ->
          if (k.square)
            KitTonalIconButtonLWideSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitTonalIconButtonLWide(Modifier, enabled = k.enabled, on = on, onButtonClick = onClick)
      }
    CatalogSize.ExtraLarge ->
      when (k.width) {
        CatalogIconWidth.Narrow ->
          if (k.square)
            KitTonalIconButtonXlNarrowSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitTonalIconButtonXlNarrow(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
        CatalogIconWidth.Uniform ->
          if (k.square)
            KitTonalIconButtonXlUniformSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitTonalIconButtonXlUniform(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
        CatalogIconWidth.Wide ->
          if (k.square)
            KitTonalIconButtonXlWideSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitTonalIconButtonXlWide(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
      }
  }
}
