@file:CatalogGroup(name = "Icon buttons", section = "Actions")
@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
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
import ee.schimke.m3catalog.ContainerlessInteractionStates
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
import generated.uibuilder.KitOutlinedIconButton
import generated.uibuilder.KitOutlinedIconButtonLNarrow
import generated.uibuilder.KitOutlinedIconButtonLNarrowSquare
import generated.uibuilder.KitOutlinedIconButtonLUniform
import generated.uibuilder.KitOutlinedIconButtonLUniformSquare
import generated.uibuilder.KitOutlinedIconButtonLWide
import generated.uibuilder.KitOutlinedIconButtonLWideSquare
import generated.uibuilder.KitOutlinedIconButtonMNarrow
import generated.uibuilder.KitOutlinedIconButtonMNarrowSquare
import generated.uibuilder.KitOutlinedIconButtonMUniform
import generated.uibuilder.KitOutlinedIconButtonMUniformSquare
import generated.uibuilder.KitOutlinedIconButtonMWide
import generated.uibuilder.KitOutlinedIconButtonMWideSquare
import generated.uibuilder.KitOutlinedIconButtonSNarrow
import generated.uibuilder.KitOutlinedIconButtonSNarrowSquare
import generated.uibuilder.KitOutlinedIconButtonSUniformSquare
import generated.uibuilder.KitOutlinedIconButtonSWide
import generated.uibuilder.KitOutlinedIconButtonSWideSquare
import generated.uibuilder.KitOutlinedIconButtonXlNarrow
import generated.uibuilder.KitOutlinedIconButtonXlNarrowSquare
import generated.uibuilder.KitOutlinedIconButtonXlUniform
import generated.uibuilder.KitOutlinedIconButtonXlUniformSquare
import generated.uibuilder.KitOutlinedIconButtonXlWide
import generated.uibuilder.KitOutlinedIconButtonXlWideSquare
import generated.uibuilder.KitOutlinedIconButtonXsNarrow
import generated.uibuilder.KitOutlinedIconButtonXsNarrowSquare
import generated.uibuilder.KitOutlinedIconButtonXsUniform
import generated.uibuilder.KitOutlinedIconButtonXsUniformSquare
import generated.uibuilder.KitOutlinedIconButtonXsWide
import generated.uibuilder.KitOutlinedIconButtonXsWideSquare
import generated.uibuilder.KitStandardIconButton
import generated.uibuilder.KitStandardIconButtonLNarrow
import generated.uibuilder.KitStandardIconButtonLNarrowSquare
import generated.uibuilder.KitStandardIconButtonLUniform
import generated.uibuilder.KitStandardIconButtonLUniformSquare
import generated.uibuilder.KitStandardIconButtonLWide
import generated.uibuilder.KitStandardIconButtonLWideSquare
import generated.uibuilder.KitStandardIconButtonMNarrow
import generated.uibuilder.KitStandardIconButtonMNarrowSquare
import generated.uibuilder.KitStandardIconButtonMUniform
import generated.uibuilder.KitStandardIconButtonMUniformSquare
import generated.uibuilder.KitStandardIconButtonMWide
import generated.uibuilder.KitStandardIconButtonMWideSquare
import generated.uibuilder.KitStandardIconButtonSNarrow
import generated.uibuilder.KitStandardIconButtonSNarrowSquare
import generated.uibuilder.KitStandardIconButtonSUniformSquare
import generated.uibuilder.KitStandardIconButtonSWide
import generated.uibuilder.KitStandardIconButtonSWideSquare
import generated.uibuilder.KitStandardIconButtonXlNarrow
import generated.uibuilder.KitStandardIconButtonXlNarrowSquare
import generated.uibuilder.KitStandardIconButtonXlUniform
import generated.uibuilder.KitStandardIconButtonXlUniformSquare
import generated.uibuilder.KitStandardIconButtonXlWide
import generated.uibuilder.KitStandardIconButtonXlWideSquare
import generated.uibuilder.KitStandardIconButtonXsNarrow
import generated.uibuilder.KitStandardIconButtonXsNarrowSquare
import generated.uibuilder.KitStandardIconButtonXsUniform
import generated.uibuilder.KitStandardIconButtonXsUniformSquare
import generated.uibuilder.KitStandardIconButtonXsWide
import generated.uibuilder.KitStandardIconButtonXsWideSquare
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

// Icon buttons carry no label, so they read as a favourite TOGGLE on the interactive lane rather
// than taking the click tally: the glyph swaps between outlined and filled.
//
// Each sticker resolves the arguments both theme styles pass — onClick, enabled and the container
// modifier — above the branch, so the standard/expressive split costs one call per arm rather than
// two full argument lists: `shape: Shape` and `shapes: IconButtonShapes` are separate overloads
// with no parameter in common, and that is the whole of what has to be written twice (#104).
//
// Three axes here, not two. The kit lists colour, size, WIDTH and shape, so each emphasis is five
// sizes x three widths x two shapes — thirty cells, carried by one `@Preview` through knobs and a
// stacked `@OverrideVariant` per cell. Shapes are per-size constants because an icon button's
// corner radius tracks its container, which is why `catalogIconShape` takes the size.

// --- Generated sticker bodies, from uid-stickers/IconButtons.kt ---

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

@CatalogComponent(
  id = "IconButton/Standard",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/57994:10132",
  caption =
    "Lowest emphasis; inline on a surface. Five sizes x three widths x two shapes fold in as " +
      "variants. The container is transparent, so the shape axis is invisible at rest — the kit " +
      "draws its square nodes the same way, and `square-hovered` is the cell where it shows.",
)
@CatalogModes
@IconButtonMatrix
@ContainerlessInteractionStates
@ee.schimke.m3catalog.StandardIconButtonExhaustiveKitCells
@Composable
fun StandardIconButton() = Sticker {
  var on by toggleable(true)
  val k = iconButtonKnobs()
  val onClick = { on = !on }
  when (k.size) {
    CatalogSize.ExtraSmall ->
      when (k.width) {
        CatalogIconWidth.Narrow ->
          if (k.square)
            KitStandardIconButtonXsNarrowSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitStandardIconButtonXsNarrow(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
        CatalogIconWidth.Uniform ->
          if (k.square)
            KitStandardIconButtonXsUniformSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitStandardIconButtonXsUniform(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
        CatalogIconWidth.Wide ->
          if (k.square)
            KitStandardIconButtonXsWideSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitStandardIconButtonXsWide(
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
            KitStandardIconButtonSNarrowSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitStandardIconButtonSNarrow(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
        CatalogIconWidth.Uniform ->
          if (k.square)
            KitStandardIconButtonSUniformSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitStandardIconButton(Modifier, enabled = k.enabled, on = on, onButtonClick = onClick)
        CatalogIconWidth.Wide ->
          if (k.square)
            KitStandardIconButtonSWideSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitStandardIconButtonSWide(
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
            KitStandardIconButtonMNarrowSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitStandardIconButtonMNarrow(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
        CatalogIconWidth.Uniform ->
          if (k.square)
            KitStandardIconButtonMUniformSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitStandardIconButtonMUniform(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
        CatalogIconWidth.Wide ->
          if (k.square)
            KitStandardIconButtonMWideSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitStandardIconButtonMWide(
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
            KitStandardIconButtonLNarrowSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitStandardIconButtonLNarrow(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
        CatalogIconWidth.Uniform ->
          if (k.square)
            KitStandardIconButtonLUniformSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitStandardIconButtonLUniform(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
        CatalogIconWidth.Wide ->
          if (k.square)
            KitStandardIconButtonLWideSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitStandardIconButtonLWide(
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
            KitStandardIconButtonXlNarrowSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitStandardIconButtonXlNarrow(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
        CatalogIconWidth.Uniform ->
          if (k.square)
            KitStandardIconButtonXlUniformSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitStandardIconButtonXlUniform(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
        CatalogIconWidth.Wide ->
          if (k.square)
            KitStandardIconButtonXlWideSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitStandardIconButtonXlWide(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
      }
  }
}

@CatalogComponent(
  id = "IconButton/Outlined",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/57994:10192",
  caption =
    "Medium emphasis on a busy surface. Five sizes x three widths x two shapes fold in as variants.",
)
@CatalogModes
@IconButtonMatrix
@InteractionStates
@ee.schimke.m3catalog.OutlinedIconButtonStickerExhaustiveKitCells
@Composable
fun OutlinedIconButtonSticker() = Sticker {
  var on by toggleable(true)
  val k = iconButtonKnobs()
  val onClick = { on = !on }
  when (k.size) {
    CatalogSize.ExtraSmall ->
      when (k.width) {
        CatalogIconWidth.Narrow ->
          if (k.square)
            KitOutlinedIconButtonXsNarrowSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitOutlinedIconButtonXsNarrow(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
        CatalogIconWidth.Uniform ->
          if (k.square)
            KitOutlinedIconButtonXsUniformSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitOutlinedIconButtonXsUniform(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
        CatalogIconWidth.Wide ->
          if (k.square)
            KitOutlinedIconButtonXsWideSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitOutlinedIconButtonXsWide(
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
            KitOutlinedIconButtonSNarrowSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitOutlinedIconButtonSNarrow(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
        CatalogIconWidth.Uniform ->
          if (k.square)
            KitOutlinedIconButtonSUniformSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitOutlinedIconButton(Modifier, enabled = k.enabled, on = on, onButtonClick = onClick)
        CatalogIconWidth.Wide ->
          if (k.square)
            KitOutlinedIconButtonSWideSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitOutlinedIconButtonSWide(
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
            KitOutlinedIconButtonMNarrowSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitOutlinedIconButtonMNarrow(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
        CatalogIconWidth.Uniform ->
          if (k.square)
            KitOutlinedIconButtonMUniformSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitOutlinedIconButtonMUniform(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
        CatalogIconWidth.Wide ->
          if (k.square)
            KitOutlinedIconButtonMWideSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitOutlinedIconButtonMWide(
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
            KitOutlinedIconButtonLNarrowSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitOutlinedIconButtonLNarrow(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
        CatalogIconWidth.Uniform ->
          if (k.square)
            KitOutlinedIconButtonLUniformSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitOutlinedIconButtonLUniform(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
        CatalogIconWidth.Wide ->
          if (k.square)
            KitOutlinedIconButtonLWideSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitOutlinedIconButtonLWide(
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
            KitOutlinedIconButtonXlNarrowSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitOutlinedIconButtonXlNarrow(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
        CatalogIconWidth.Uniform ->
          if (k.square)
            KitOutlinedIconButtonXlUniformSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitOutlinedIconButtonXlUniform(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
        CatalogIconWidth.Wide ->
          if (k.square)
            KitOutlinedIconButtonXlWideSquare(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
          else
            KitOutlinedIconButtonXlWide(
              Modifier,
              enabled = k.enabled,
              on = on,
              onButtonClick = onClick,
            )
      }
  }
}
