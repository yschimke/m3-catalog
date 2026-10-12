@file:CatalogGroup(name = "Toggle buttons", section = "Actions")
@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)
package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ElevatedToggleButton
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedToggleButton
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.TonalToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.preview.CaptureGutter
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant
import ee.schimke.composeai.preview.InteractionPreview
import ee.schimke.m3catalog.CatalogFilledStars
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.CatalogOutlinedStars
import ee.schimke.m3catalog.CatalogShape
import ee.schimke.m3catalog.CatalogSize
import ee.schimke.m3catalog.InteractionStates
import ee.schimke.m3catalog.KitShadowGutter
import ee.schimke.m3catalog.SelectedToggleButtonMatrix
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.UnselectedToggleButtonMatrix
import ee.schimke.m3catalog.catalogButtonSize
import ee.schimke.m3catalog.catalogEnabled
import ee.schimke.m3catalog.catalogText
import ee.schimke.m3catalog.catalogToggleButtonShapes
import ee.schimke.m3catalog.catalogToggleSelected
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.label_elevated
import ee.schimke.m3catalog.generated.resources.label_favourite
import ee.schimke.m3catalog.generated.resources.label_filled
import ee.schimke.m3catalog.generated.resources.label_on
import ee.schimke.m3catalog.generated.resources.label_outlined
import ee.schimke.m3catalog.generated.resources.label_tonal
import ee.schimke.m3catalog.toggleable
import generated.uibuilder.KitElevatedToggleButton
import generated.uibuilder.KitElevatedToggleButtonL
import generated.uibuilder.KitElevatedToggleButtonLSquare
import generated.uibuilder.KitElevatedToggleButtonM
import generated.uibuilder.KitElevatedToggleButtonMSquare
import generated.uibuilder.KitElevatedToggleButtonSquare
import generated.uibuilder.KitElevatedToggleButtonXl
import generated.uibuilder.KitElevatedToggleButtonXlSquare
import generated.uibuilder.KitElevatedToggleButtonXs
import generated.uibuilder.KitElevatedToggleButtonXsSquare
import generated.uibuilder.KitOutlinedToggleButton
import generated.uibuilder.KitOutlinedToggleButtonL
import generated.uibuilder.KitOutlinedToggleButtonLSquare
import generated.uibuilder.KitOutlinedToggleButtonM
import generated.uibuilder.KitOutlinedToggleButtonMSquare
import generated.uibuilder.KitOutlinedToggleButtonSquare
import generated.uibuilder.KitOutlinedToggleButtonXl
import generated.uibuilder.KitOutlinedToggleButtonXlSquare
import generated.uibuilder.KitOutlinedToggleButtonXs
import generated.uibuilder.KitOutlinedToggleButtonXsSquare
import generated.uibuilder.KitToggleButton
import generated.uibuilder.KitToggleButtonL
import generated.uibuilder.KitToggleButtonLSquare
import generated.uibuilder.KitToggleButtonM
import generated.uibuilder.KitToggleButtonMSquare
import generated.uibuilder.KitToggleButtonSquare
import generated.uibuilder.KitToggleButtonXl
import generated.uibuilder.KitToggleButtonXlSquare
import generated.uibuilder.KitToggleButtonXs
import generated.uibuilder.KitToggleButtonXsSquare
import generated.uibuilder.KitTonalToggleButton
import generated.uibuilder.KitTonalToggleButtonL
import generated.uibuilder.KitTonalToggleButtonLSquare
import generated.uibuilder.KitTonalToggleButtonM
import generated.uibuilder.KitTonalToggleButtonMSquare
import generated.uibuilder.KitTonalToggleButtonSquare
import generated.uibuilder.KitTonalToggleButtonXl
import generated.uibuilder.KitTonalToggleButtonXlSquare
import generated.uibuilder.KitTonalToggleButtonXs
import generated.uibuilder.KitTonalToggleButtonXsSquare
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

// A toggle button is a button whose selection persists, so unlike the plain buttons it owns state
// and the interactive lane really flips it. It also shape-shifts when checked — M3 gives the
// checked state its own corner — which `catalogToggleButtonShape` resolves from BOTH the size and
// the checked flag, where `catalogButtonShape` needs neither.
//
// Selected/unselected is a full axis, not a footnote: the kit's four sets each carry it as a
// component property, and the container SHAPE morphs across it, so a selected and an unselected
// cell of the same size differ by design. The `selected` knob defaults per component to the state
// its sticker was authored in, so no unseeded render moves; the unsuffixed variant names are that
// authored state, and the `-on` / `-off` ones name the state absolutely.

// --- Content axis, folded under the filled toggle ------------------------------------------------
// The kit's remaining documented axis: "Can contain an optional leading icon". It also specifies
// the glyph swap — outlined circle when unselected, filled circle when selected — so this variant
// reads its own state rather than pinning one icon.

@CatalogVariant(
  of = "ToggleButton/Filled",
  props = ["content=label"],
  caption = "Label only, vs the kit's icon + label default.",
)
@CatalogModes
@Composable
fun ToggleButtonLabelOnly() = Sticker {
  var on by toggleable(true)
  ToggleButton(checked = on, onCheckedChange = { on = it }) {
    Text(catalogText("label", stringResource(Res.string.label_favourite)))
  }
}

// --- Generated sticker bodies, from uid-stickers/ToggleButtons.kt ---

/** The toggle button's live knobs, read in the hand-written sticker's order. */
private class ToggleButtonKnobs(
  val on: MutableState<Boolean>,
  val size: CatalogSize,
  val enabled: Boolean,
  val square: Boolean,
  val label: String,
) {
  val toggle: () -> Unit = { on.value = !on.value }
}

@Composable
private fun toggleButtonKnobs(default: Boolean, off: StringResource): ToggleButtonKnobs {
  val on = toggleable(catalogToggleSelected(default = default))
  val size = catalogButtonSize()
  val enabled = catalogEnabled()
  val square = CatalogShape.Axis.current() == CatalogShape.Square
  // The hand-written content reads the size a second time before the label.
  catalogButtonSize()
  val label = catalogText("label", stringResource(if (on.value) Res.string.label_on else off))
  return ToggleButtonKnobs(on, size, enabled, square, label)
}

@CatalogComponent(
  id = "ToggleButton/Filled",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/57994:2475",
  caption = "A button whose selection persists. Five sizes x two shapes fold in as variants.",
)
// `[0, 0, 0]` is the toggle idiom: one control tapped three times, so both directions of the
// shape transition are shown and the second cycle confirms the first was not a one-off.
@InteractionPreview(
  targets = [0, 0, 0],
  caption =
    "Toggle repeatedly. The container morphs between its unchecked and checked shapes through " +
      "the theme's spatial animation — Baseline swaps the shape, Expressive travels between them.",
)
@CatalogModes
@SelectedToggleButtonMatrix
@InteractionStates
@ee.schimke.m3catalog.ToggleButtonStickerExhaustiveKitCells
@Composable
fun ToggleButtonSticker() = Sticker {
  val k = toggleButtonKnobs(default = true, Res.string.label_filled)
  val m = Modifier
  when (k.size) {
    CatalogSize.ExtraSmall ->
      if (k.square)
        KitToggleButtonXsSquare(
          m,
          checked = k.on.value,
          enabled = k.enabled,
          label = k.label,
          onToggleCheckedChange = k.toggle,
        )
      else
        KitToggleButtonXs(
          m,
          checked = k.on.value,
          enabled = k.enabled,
          label = k.label,
          onToggleCheckedChange = k.toggle,
        )
    CatalogSize.Small ->
      if (k.square)
        KitToggleButtonSquare(
          m,
          checked = k.on.value,
          enabled = k.enabled,
          label = k.label,
          onToggleCheckedChange = k.toggle,
        )
      else
        KitToggleButton(
          m,
          checked = k.on.value,
          enabled = k.enabled,
          label = k.label,
          onToggleCheckedChange = k.toggle,
        )
    CatalogSize.Medium ->
      if (k.square)
        KitToggleButtonMSquare(
          m,
          checked = k.on.value,
          enabled = k.enabled,
          label = k.label,
          onToggleCheckedChange = k.toggle,
        )
      else
        KitToggleButtonM(
          m,
          checked = k.on.value,
          enabled = k.enabled,
          label = k.label,
          onToggleCheckedChange = k.toggle,
        )
    CatalogSize.Large ->
      if (k.square)
        KitToggleButtonLSquare(
          m,
          checked = k.on.value,
          enabled = k.enabled,
          label = k.label,
          onToggleCheckedChange = k.toggle,
        )
      else
        KitToggleButtonL(
          m,
          checked = k.on.value,
          enabled = k.enabled,
          label = k.label,
          onToggleCheckedChange = k.toggle,
        )
    CatalogSize.ExtraLarge ->
      if (k.square)
        KitToggleButtonXlSquare(
          m,
          checked = k.on.value,
          enabled = k.enabled,
          label = k.label,
          onToggleCheckedChange = k.toggle,
        )
      else
        KitToggleButtonXl(
          m,
          checked = k.on.value,
          enabled = k.enabled,
          label = k.label,
          onToggleCheckedChange = k.toggle,
        )
  }
}

@CatalogComponent(
  id = "ToggleButton/Tonal",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/57994:2435",
  caption = "Secondary emphasis toggle. Five sizes x two shapes fold in as variants.",
)
@CatalogModes
@SelectedToggleButtonMatrix
@InteractionStates
@ee.schimke.m3catalog.TonalToggleButtonStickerExhaustiveKitCells
@Composable
fun TonalToggleButtonSticker() = Sticker {
  val k = toggleButtonKnobs(default = true, Res.string.label_tonal)
  val m = Modifier
  when (k.size) {
    CatalogSize.ExtraSmall ->
      if (k.square)
        KitTonalToggleButtonXsSquare(
          m,
          checked = k.on.value,
          enabled = k.enabled,
          label = k.label,
          onToggleCheckedChange = k.toggle,
        )
      else
        KitTonalToggleButtonXs(
          m,
          checked = k.on.value,
          enabled = k.enabled,
          label = k.label,
          onToggleCheckedChange = k.toggle,
        )
    CatalogSize.Small ->
      if (k.square)
        KitTonalToggleButtonSquare(
          m,
          checked = k.on.value,
          enabled = k.enabled,
          label = k.label,
          onToggleCheckedChange = k.toggle,
        )
      else
        KitTonalToggleButton(
          m,
          checked = k.on.value,
          enabled = k.enabled,
          label = k.label,
          onToggleCheckedChange = k.toggle,
        )
    CatalogSize.Medium ->
      if (k.square)
        KitTonalToggleButtonMSquare(
          m,
          checked = k.on.value,
          enabled = k.enabled,
          label = k.label,
          onToggleCheckedChange = k.toggle,
        )
      else
        KitTonalToggleButtonM(
          m,
          checked = k.on.value,
          enabled = k.enabled,
          label = k.label,
          onToggleCheckedChange = k.toggle,
        )
    CatalogSize.Large ->
      if (k.square)
        KitTonalToggleButtonLSquare(
          m,
          checked = k.on.value,
          enabled = k.enabled,
          label = k.label,
          onToggleCheckedChange = k.toggle,
        )
      else
        KitTonalToggleButtonL(
          m,
          checked = k.on.value,
          enabled = k.enabled,
          label = k.label,
          onToggleCheckedChange = k.toggle,
        )
    CatalogSize.ExtraLarge ->
      if (k.square)
        KitTonalToggleButtonXlSquare(
          m,
          checked = k.on.value,
          enabled = k.enabled,
          label = k.label,
          onToggleCheckedChange = k.toggle,
        )
      else
        KitTonalToggleButtonXl(
          m,
          checked = k.on.value,
          enabled = k.enabled,
          label = k.label,
          onToggleCheckedChange = k.toggle,
        )
  }
}

// Deliberately unreferenced. The kit ships "Toggle button - outline" as its own set, but that node
// is not reachable in this file: the resolver's best offer is "Toggle button" — the FILLED set's
// node, which ToggleButton/Filled already points at. Aiming both at it would report every finding
// against the wrong component, which is worse than a visible gap the generator names on every run.
// Button/Outlined has the same gap for the same reason.
@CatalogComponent(
  id = "ToggleButton/Outlined",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/57994:2405",
  caption = "Medium emphasis on a busy surface. Five sizes x two shapes fold in as variants.",
)
@CatalogModes
@UnselectedToggleButtonMatrix
@InteractionStates
@ee.schimke.m3catalog.OutlinedToggleButtonStickerExhaustiveKitCells
@Composable
fun OutlinedToggleButtonSticker() = Sticker {
  val k = toggleButtonKnobs(default = false, Res.string.label_outlined)
  val m = Modifier
  when (k.size) {
    CatalogSize.ExtraSmall ->
      if (k.square)
        KitOutlinedToggleButtonXsSquare(
          m,
          checked = k.on.value,
          enabled = k.enabled,
          label = k.label,
          onToggleCheckedChange = k.toggle,
        )
      else
        KitOutlinedToggleButtonXs(
          m,
          checked = k.on.value,
          enabled = k.enabled,
          label = k.label,
          onToggleCheckedChange = k.toggle,
        )
    CatalogSize.Small ->
      if (k.square)
        KitOutlinedToggleButtonSquare(
          m,
          checked = k.on.value,
          enabled = k.enabled,
          label = k.label,
          onToggleCheckedChange = k.toggle,
        )
      else
        KitOutlinedToggleButton(
          m,
          checked = k.on.value,
          enabled = k.enabled,
          label = k.label,
          onToggleCheckedChange = k.toggle,
        )
    CatalogSize.Medium ->
      if (k.square)
        KitOutlinedToggleButtonMSquare(
          m,
          checked = k.on.value,
          enabled = k.enabled,
          label = k.label,
          onToggleCheckedChange = k.toggle,
        )
      else
        KitOutlinedToggleButtonM(
          m,
          checked = k.on.value,
          enabled = k.enabled,
          label = k.label,
          onToggleCheckedChange = k.toggle,
        )
    CatalogSize.Large ->
      if (k.square)
        KitOutlinedToggleButtonLSquare(
          m,
          checked = k.on.value,
          enabled = k.enabled,
          label = k.label,
          onToggleCheckedChange = k.toggle,
        )
      else
        KitOutlinedToggleButtonL(
          m,
          checked = k.on.value,
          enabled = k.enabled,
          label = k.label,
          onToggleCheckedChange = k.toggle,
        )
    CatalogSize.ExtraLarge ->
      if (k.square)
        KitOutlinedToggleButtonXlSquare(
          m,
          checked = k.on.value,
          enabled = k.enabled,
          label = k.label,
          onToggleCheckedChange = k.toggle,
        )
      else
        KitOutlinedToggleButtonXl(
          m,
          checked = k.on.value,
          enabled = k.enabled,
          label = k.label,
          onToggleCheckedChange = k.toggle,
        )
  }
}

@CatalogComponent(
  id = "ToggleButton/Elevated",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/57994:2365",
  caption = "Separated by shadow. Five sizes x two shapes fold in as variants.",
)
@CatalogModes
@UnselectedToggleButtonMatrix
@InteractionStates
@CaptureGutter(all = KitShadowGutter.Level1All, bottom = KitShadowGutter.Level1Bottom)
@ee.schimke.m3catalog.ElevatedToggleButtonStickerExhaustiveKitCells
@Composable
fun ElevatedToggleButtonSticker() = Sticker {
  val k = toggleButtonKnobs(default = false, Res.string.label_elevated)
  val m = Modifier
  when (k.size) {
    CatalogSize.ExtraSmall ->
      if (k.square)
        KitElevatedToggleButtonXsSquare(
          m,
          checked = k.on.value,
          enabled = k.enabled,
          label = k.label,
          onToggleCheckedChange = k.toggle,
        )
      else
        KitElevatedToggleButtonXs(
          m,
          checked = k.on.value,
          enabled = k.enabled,
          label = k.label,
          onToggleCheckedChange = k.toggle,
        )
    CatalogSize.Small ->
      if (k.square)
        KitElevatedToggleButtonSquare(
          m,
          checked = k.on.value,
          enabled = k.enabled,
          label = k.label,
          onToggleCheckedChange = k.toggle,
        )
      else
        KitElevatedToggleButton(
          m,
          checked = k.on.value,
          enabled = k.enabled,
          label = k.label,
          onToggleCheckedChange = k.toggle,
        )
    CatalogSize.Medium ->
      if (k.square)
        KitElevatedToggleButtonMSquare(
          m,
          checked = k.on.value,
          enabled = k.enabled,
          label = k.label,
          onToggleCheckedChange = k.toggle,
        )
      else
        KitElevatedToggleButtonM(
          m,
          checked = k.on.value,
          enabled = k.enabled,
          label = k.label,
          onToggleCheckedChange = k.toggle,
        )
    CatalogSize.Large ->
      if (k.square)
        KitElevatedToggleButtonLSquare(
          m,
          checked = k.on.value,
          enabled = k.enabled,
          label = k.label,
          onToggleCheckedChange = k.toggle,
        )
      else
        KitElevatedToggleButtonL(
          m,
          checked = k.on.value,
          enabled = k.enabled,
          label = k.label,
          onToggleCheckedChange = k.toggle,
        )
    CatalogSize.ExtraLarge ->
      if (k.square)
        KitElevatedToggleButtonXlSquare(
          m,
          checked = k.on.value,
          enabled = k.enabled,
          label = k.label,
          onToggleCheckedChange = k.toggle,
        )
      else
        KitElevatedToggleButtonXl(
          m,
          checked = k.on.value,
          enabled = k.enabled,
          label = k.label,
          onToggleCheckedChange = k.toggle,
        )
  }
}
