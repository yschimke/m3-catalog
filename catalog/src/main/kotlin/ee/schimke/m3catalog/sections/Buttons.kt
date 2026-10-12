@file:CatalogGroup(name = "Buttons", section = "Actions")
@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ee.schimke.composeai.preview.CaptureGutter
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.CatalogShape
import ee.schimke.m3catalog.CatalogSize
import ee.schimke.m3catalog.ContainerlessInteractionStates
import ee.schimke.m3catalog.Counted
import ee.schimke.m3catalog.InteractionStates
import ee.schimke.m3catalog.KitShadowGutter
import ee.schimke.m3catalog.SizeShapeMatrix
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogButtonSize
import ee.schimke.m3catalog.catalogEnabled
import ee.schimke.m3catalog.catalogText
import ee.schimke.m3catalog.counted
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.label_elevated
import ee.schimke.m3catalog.generated.resources.label_filled
import ee.schimke.m3catalog.generated.resources.label_outlined
import ee.schimke.m3catalog.generated.resources.label_text
import ee.schimke.m3catalog.generated.resources.label_tonal
import generated.uibuilder.KitElevatedButton
import generated.uibuilder.KitElevatedButtonL
import generated.uibuilder.KitElevatedButtonLSquare
import generated.uibuilder.KitElevatedButtonM
import generated.uibuilder.KitElevatedButtonMSquare
import generated.uibuilder.KitElevatedButtonSquare
import generated.uibuilder.KitElevatedButtonXl
import generated.uibuilder.KitElevatedButtonXlSquare
import generated.uibuilder.KitElevatedButtonXs
import generated.uibuilder.KitElevatedButtonXsSquare
import generated.uibuilder.KitFilledButton
import generated.uibuilder.KitFilledButtonL
import generated.uibuilder.KitFilledButtonLSquare
import generated.uibuilder.KitFilledButtonM
import generated.uibuilder.KitFilledButtonMSquare
import generated.uibuilder.KitFilledButtonSquare
import generated.uibuilder.KitFilledButtonXl
import generated.uibuilder.KitFilledButtonXlSquare
import generated.uibuilder.KitFilledButtonXs
import generated.uibuilder.KitFilledButtonXsSquare
import generated.uibuilder.KitOutlinedButton
import generated.uibuilder.KitOutlinedButtonL
import generated.uibuilder.KitOutlinedButtonLSquare
import generated.uibuilder.KitOutlinedButtonM
import generated.uibuilder.KitOutlinedButtonMSquare
import generated.uibuilder.KitOutlinedButtonSquare
import generated.uibuilder.KitOutlinedButtonXl
import generated.uibuilder.KitOutlinedButtonXlSquare
import generated.uibuilder.KitOutlinedButtonXs
import generated.uibuilder.KitOutlinedButtonXsSquare
import generated.uibuilder.KitTextButton
import generated.uibuilder.KitTextButtonL
import generated.uibuilder.KitTextButtonLSquare
import generated.uibuilder.KitTextButtonM
import generated.uibuilder.KitTextButtonMSquare
import generated.uibuilder.KitTextButtonSquare
import generated.uibuilder.KitTextButtonXl
import generated.uibuilder.KitTextButtonXlSquare
import generated.uibuilder.KitTextButtonXs
import generated.uibuilder.KitTextButtonXsSquare
import generated.uibuilder.KitTonalButton
import generated.uibuilder.KitTonalButtonL
import generated.uibuilder.KitTonalButtonLSquare
import generated.uibuilder.KitTonalButtonM
import generated.uibuilder.KitTonalButtonMSquare
import generated.uibuilder.KitTonalButtonSquare
import generated.uibuilder.KitTonalButtonXl
import generated.uibuilder.KitTonalButtonXlSquare
import generated.uibuilder.KitTonalButtonXs
import generated.uibuilder.KitTonalButtonXsSquare
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

// The five common M3 buttons, highest to lowest emphasis.
//
// Each is ONE `@Preview` carrying the whole expressive matrix — five sizes x two shapes — through
// the `size` / `shape` knobs and a stacked `@OverrideVariant` per cell. The alternative, fifty
// near-identical `@Composable`s, would say the same thing in fifty places and drift in forty-nine
// of them. The unseeded render (small, round) is byte-identical to what a bare `Button(...)`
// produces, so the default sticker did not move when the matrix arrived.
//
// The kit's own Button component set carries exactly these variant properties, and design-parity
// walks them looking for a candidate render of each — so an un-fanned catalog reports "reference
// variant has no candidate render" for every cell it does not cover.
//
// Every sticker renders under BOTH theme styles, and Compose splits that across two overloads:
// `shape: Shape` for the standard one, `shapes: ButtonShapes` for the expressive one, with no
// parameter in common. So the branch cannot go away — but the four arguments both arms pass can be
// resolved once, above it, instead of being written out per arm (#104).

// --- Content axes, folded under Button/Filled ---------------------------------------------------

@CatalogVariant(
  of = "Button/Filled",
  props = ["content=label"],
  caption = "Label only, vs the kit's icon + label default.",
)
@CatalogModes
@Composable
fun FilledButtonLabelOnly() = Sticker {
  val c = counted(catalogText("label", stringResource(Res.string.label_filled)))
  Button(onClick = c.onClick) { Text(c.label) }
}

// --- Generated sticker bodies, from uid-stickers/Buttons.kt ---

/** The button's live knobs, read in the hand-written sticker's order. */
private class ButtonKnobs(
  val click: Counted,
  val size: CatalogSize,
  val enabled: Boolean,
  val square: Boolean,
)

@Composable
private fun buttonKnobs(label: StringResource): ButtonKnobs {
  val click = counted(catalogText("label", stringResource(label)))
  val size = catalogButtonSize()
  val enabled = catalogEnabled()
  val square = CatalogShape.Axis.current() == CatalogShape.Square
  return ButtonKnobs(click, size, enabled, square)
}

@CatalogComponent(
  id = "Button/Filled",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/57994:2324",
  caption = "Highest emphasis; the primary action. Five sizes x two shapes fold in as variants.",
)
@CatalogModes
@SizeShapeMatrix
@InteractionStates
@ee.schimke.m3catalog.FilledButtonExhaustiveKitCells
@Composable
fun FilledButton() = Sticker {
  val k = buttonKnobs(Res.string.label_filled)
  val label = k.click.label
  val onClick = k.click.onClick
  when (k.size) {
    CatalogSize.ExtraSmall ->
      if (k.square)
        KitFilledButtonXsSquare(
          Modifier,
          enabled = k.enabled,
          label = label,
          onButtonClick = onClick,
        )
      else KitFilledButtonXs(Modifier, enabled = k.enabled, label = label, onButtonClick = onClick)
    CatalogSize.Small ->
      if (k.square)
        KitFilledButtonSquare(Modifier, enabled = k.enabled, label = label, onButtonClick = onClick)
      else KitFilledButton(Modifier, enabled = k.enabled, label = label, onButtonClick = onClick)
    CatalogSize.Medium ->
      if (k.square)
        KitFilledButtonMSquare(
          Modifier,
          enabled = k.enabled,
          label = label,
          onButtonClick = onClick,
        )
      else KitFilledButtonM(Modifier, enabled = k.enabled, label = label, onButtonClick = onClick)
    CatalogSize.Large ->
      if (k.square)
        KitFilledButtonLSquare(
          Modifier,
          enabled = k.enabled,
          label = label,
          onButtonClick = onClick,
        )
      else KitFilledButtonL(Modifier, enabled = k.enabled, label = label, onButtonClick = onClick)
    CatalogSize.ExtraLarge ->
      if (k.square)
        KitFilledButtonXlSquare(
          Modifier,
          enabled = k.enabled,
          label = label,
          onButtonClick = onClick,
        )
      else KitFilledButtonXl(Modifier, enabled = k.enabled, label = label, onButtonClick = onClick)
  }
}

@CatalogComponent(
  id = "Button/Tonal",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/57994:2304",
  caption = "Secondary, still prominent. Five sizes x two shapes fold in as variants.",
)
@CatalogModes
@SizeShapeMatrix
@InteractionStates
@ee.schimke.m3catalog.TonalButtonExhaustiveKitCells
@Composable
fun TonalButton() = Sticker {
  val k = buttonKnobs(Res.string.label_tonal)
  val label = k.click.label
  val onClick = k.click.onClick
  when (k.size) {
    CatalogSize.ExtraSmall ->
      if (k.square)
        KitTonalButtonXsSquare(
          Modifier,
          enabled = k.enabled,
          label = label,
          onButtonClick = onClick,
        )
      else KitTonalButtonXs(Modifier, enabled = k.enabled, label = label, onButtonClick = onClick)
    CatalogSize.Small ->
      if (k.square)
        KitTonalButtonSquare(Modifier, enabled = k.enabled, label = label, onButtonClick = onClick)
      else KitTonalButton(Modifier, enabled = k.enabled, label = label, onButtonClick = onClick)
    CatalogSize.Medium ->
      if (k.square)
        KitTonalButtonMSquare(Modifier, enabled = k.enabled, label = label, onButtonClick = onClick)
      else KitTonalButtonM(Modifier, enabled = k.enabled, label = label, onButtonClick = onClick)
    CatalogSize.Large ->
      if (k.square)
        KitTonalButtonLSquare(Modifier, enabled = k.enabled, label = label, onButtonClick = onClick)
      else KitTonalButtonL(Modifier, enabled = k.enabled, label = label, onButtonClick = onClick)
    CatalogSize.ExtraLarge ->
      if (k.square)
        KitTonalButtonXlSquare(
          Modifier,
          enabled = k.enabled,
          label = label,
          onButtonClick = onClick,
        )
      else KitTonalButtonXl(Modifier, enabled = k.enabled, label = label, onButtonClick = onClick)
  }
}

@CatalogComponent(
  id = "Button/Outlined",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/57994:2284",
  caption = "Medium emphasis on a busy surface. Five sizes x two shapes fold in as variants.",
)
@CatalogModes
@SizeShapeMatrix
@InteractionStates
@ee.schimke.m3catalog.OutlinedButtonStickerExhaustiveKitCells
@Composable
fun OutlinedButtonSticker() = Sticker {
  val k = buttonKnobs(Res.string.label_outlined)
  val label = k.click.label
  val onClick = k.click.onClick
  when (k.size) {
    CatalogSize.ExtraSmall ->
      if (k.square)
        KitOutlinedButtonXsSquare(
          Modifier,
          enabled = k.enabled,
          label = label,
          onButtonClick = onClick,
        )
      else
        KitOutlinedButtonXs(Modifier, enabled = k.enabled, label = label, onButtonClick = onClick)
    CatalogSize.Small ->
      if (k.square)
        KitOutlinedButtonSquare(
          Modifier,
          enabled = k.enabled,
          label = label,
          onButtonClick = onClick,
        )
      else KitOutlinedButton(Modifier, enabled = k.enabled, label = label, onButtonClick = onClick)
    CatalogSize.Medium ->
      if (k.square)
        KitOutlinedButtonMSquare(
          Modifier,
          enabled = k.enabled,
          label = label,
          onButtonClick = onClick,
        )
      else KitOutlinedButtonM(Modifier, enabled = k.enabled, label = label, onButtonClick = onClick)
    CatalogSize.Large ->
      if (k.square)
        KitOutlinedButtonLSquare(
          Modifier,
          enabled = k.enabled,
          label = label,
          onButtonClick = onClick,
        )
      else KitOutlinedButtonL(Modifier, enabled = k.enabled, label = label, onButtonClick = onClick)
    CatalogSize.ExtraLarge ->
      if (k.square)
        KitOutlinedButtonXlSquare(
          Modifier,
          enabled = k.enabled,
          label = label,
          onButtonClick = onClick,
        )
      else
        KitOutlinedButtonXl(Modifier, enabled = k.enabled, label = label, onButtonClick = onClick)
  }
}

@CatalogComponent(
  id = "Button/Elevated",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/57994:2244",
  caption = "Outlined alternative needing separation. Five sizes x two shapes fold in as variants.",
)
@CatalogModes
@SizeShapeMatrix
@InteractionStates
@CaptureGutter(all = KitShadowGutter.Level1All, bottom = KitShadowGutter.Level1Bottom)
@ee.schimke.m3catalog.ElevatedButtonStickerExhaustiveKitCells
@Composable
fun ElevatedButtonSticker() = Sticker {
  val k = buttonKnobs(Res.string.label_elevated)
  val label = k.click.label
  val onClick = k.click.onClick
  when (k.size) {
    CatalogSize.ExtraSmall ->
      if (k.square)
        KitElevatedButtonXsSquare(
          Modifier,
          enabled = k.enabled,
          label = label,
          onButtonClick = onClick,
        )
      else
        KitElevatedButtonXs(Modifier, enabled = k.enabled, label = label, onButtonClick = onClick)
    CatalogSize.Small ->
      if (k.square)
        KitElevatedButtonSquare(
          Modifier,
          enabled = k.enabled,
          label = label,
          onButtonClick = onClick,
        )
      else KitElevatedButton(Modifier, enabled = k.enabled, label = label, onButtonClick = onClick)
    CatalogSize.Medium ->
      if (k.square)
        KitElevatedButtonMSquare(
          Modifier,
          enabled = k.enabled,
          label = label,
          onButtonClick = onClick,
        )
      else KitElevatedButtonM(Modifier, enabled = k.enabled, label = label, onButtonClick = onClick)
    CatalogSize.Large ->
      if (k.square)
        KitElevatedButtonLSquare(
          Modifier,
          enabled = k.enabled,
          label = label,
          onButtonClick = onClick,
        )
      else KitElevatedButtonL(Modifier, enabled = k.enabled, label = label, onButtonClick = onClick)
    CatalogSize.ExtraLarge ->
      if (k.square)
        KitElevatedButtonXlSquare(
          Modifier,
          enabled = k.enabled,
          label = label,
          onButtonClick = onClick,
        )
      else
        KitElevatedButtonXl(Modifier, enabled = k.enabled, label = label, onButtonClick = onClick)
  }
}

@CatalogComponent(
  id = "Button/Text",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/57994:2264",
  caption =
    "Lowest emphasis; inline actions. Five sizes x two shapes fold in as variants. The container " +
      "is transparent, so the shape axis is invisible at rest — the kit draws its square nodes " +
      "the same way, and `square-hovered` is the cell where it shows.",
)
@CatalogModes
@SizeShapeMatrix
@ContainerlessInteractionStates
@ee.schimke.m3catalog.TextButtonStickerExhaustiveKitCells
@Composable
fun TextButtonSticker() = Sticker {
  val k = buttonKnobs(Res.string.label_text)
  val label = k.click.label
  val onClick = k.click.onClick
  when (k.size) {
    CatalogSize.ExtraSmall ->
      if (k.square)
        KitTextButtonXsSquare(Modifier, enabled = k.enabled, label = label, onButtonClick = onClick)
      else KitTextButtonXs(Modifier, enabled = k.enabled, label = label, onButtonClick = onClick)
    CatalogSize.Small ->
      if (k.square)
        KitTextButtonSquare(Modifier, enabled = k.enabled, label = label, onButtonClick = onClick)
      else KitTextButton(Modifier, enabled = k.enabled, label = label, onButtonClick = onClick)
    CatalogSize.Medium ->
      if (k.square)
        KitTextButtonMSquare(Modifier, enabled = k.enabled, label = label, onButtonClick = onClick)
      else KitTextButtonM(Modifier, enabled = k.enabled, label = label, onButtonClick = onClick)
    CatalogSize.Large ->
      if (k.square)
        KitTextButtonLSquare(Modifier, enabled = k.enabled, label = label, onButtonClick = onClick)
      else KitTextButtonL(Modifier, enabled = k.enabled, label = label, onButtonClick = onClick)
    CatalogSize.ExtraLarge ->
      if (k.square)
        KitTextButtonXlSquare(Modifier, enabled = k.enabled, label = label, onButtonClick = onClick)
      else KitTextButtonXl(Modifier, enabled = k.enabled, label = label, onButtonClick = onClick)
  }
}
