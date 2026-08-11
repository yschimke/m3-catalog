@file:CatalogGroup(name = "Toggle buttons", section = "Actions")
@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant
import ee.schimke.m3catalog.CatalogFilledStars
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.CatalogOutlinedStars
import ee.schimke.m3catalog.CatalogSize
import ee.schimke.m3catalog.SelectedToggleButtonMatrix
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.UnselectedToggleButtonMatrix
import ee.schimke.m3catalog.catalogButtonSize
import ee.schimke.m3catalog.catalogEnabled
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

@Composable
private fun FigmaToggleButtonContent(selected: Boolean, label: String) {
  val size = catalogButtonSize()
  Icon(
    if (selected) CatalogFilledStars else CatalogOutlinedStars,
    contentDescription = null,
    modifier = Modifier.size(size.iconSize),
  )
  Spacer(Modifier.width(size.iconSpacing))
  ProvideTextStyle(size.labelStyle) { Text(label) }
}

@Composable
private fun ToggleButtonFrame(size: CatalogSize, content: @Composable () -> Unit) {
  Box(
    modifier = Modifier.height(if (size == CatalogSize.Small) 48.dp else size.containerHeight),
    contentAlignment = Alignment.Center,
  ) {
    content()
  }
}

@CatalogComponent(
  id = "ToggleButton/Filled",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/57994:2475",
  caption = "A button whose selection persists. Five sizes x two shapes fold in as variants.",
)
@CatalogModes
@SelectedToggleButtonMatrix
@Composable
fun ToggleButtonSticker() = Sticker {
  val (on, set) = toggleable(catalogToggleSelected(default = true))
  val size = catalogButtonSize()
  ToggleButtonFrame(size) {
    ToggleButton(
      checked = on,
      onCheckedChange = set,
      enabled = catalogEnabled(),
      shapes = catalogToggleButtonShapes(size),
      contentPadding = size.contentPadding,
      modifier = Modifier.height(size.containerHeight),
    ) {
      FigmaToggleButtonContent(
        selected = on,
        label = stringResource(if (on) Res.string.label_on else Res.string.label_filled),
      )
    }
  }
}

@CatalogComponent(
  id = "ToggleButton/Tonal",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/57994:2435",
  caption = "Secondary emphasis toggle. Five sizes x two shapes fold in as variants.",
)
@CatalogModes
@SelectedToggleButtonMatrix
@Composable
fun TonalToggleButtonSticker() = Sticker {
  val (on, set) = toggleable(catalogToggleSelected(default = true))
  val size = catalogButtonSize()
  ToggleButtonFrame(size) {
    TonalToggleButton(
      checked = on,
      onCheckedChange = set,
      enabled = catalogEnabled(),
      shapes = catalogToggleButtonShapes(size),
      contentPadding = size.contentPadding,
      modifier = Modifier.height(size.containerHeight),
    ) {
      FigmaToggleButtonContent(
        selected = on,
        label = stringResource(if (on) Res.string.label_on else Res.string.label_tonal),
      )
    }
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
@Composable
fun OutlinedToggleButtonSticker() = Sticker {
  val (on, set) = toggleable(catalogToggleSelected(default = false))
  val size = catalogButtonSize()
  ToggleButtonFrame(size) {
    OutlinedToggleButton(
      checked = on,
      onCheckedChange = set,
      enabled = catalogEnabled(),
      shapes = catalogToggleButtonShapes(size),
      contentPadding = size.contentPadding,
      modifier = Modifier.height(size.containerHeight),
    ) {
      FigmaToggleButtonContent(
        selected = on,
        label = stringResource(if (on) Res.string.label_on else Res.string.label_outlined),
      )
    }
  }
}

@CatalogComponent(
  id = "ToggleButton/Elevated",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/57994:2365",
  caption = "Separated by shadow. Five sizes x two shapes fold in as variants.",
)
@CatalogModes
@UnselectedToggleButtonMatrix
@Composable
fun ElevatedToggleButtonSticker() = Sticker {
  val (on, set) = toggleable(catalogToggleSelected(default = false))
  val size = catalogButtonSize()
  Box(Modifier.padding(start = 4.dp, top = 4.dp, end = 4.dp, bottom = 5.dp)) {
    ElevatedToggleButton(
      checked = on,
      onCheckedChange = set,
      enabled = catalogEnabled(),
      shapes = catalogToggleButtonShapes(size),
      contentPadding = size.contentPadding,
      modifier = Modifier.height(size.containerHeight),
    ) {
      FigmaToggleButtonContent(
        selected = on,
        label = stringResource(if (on) Res.string.label_on else Res.string.label_elevated),
      )
    }
  }
}

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
  val (on, set) = toggleable(true)
  ToggleButton(checked = on, onCheckedChange = set) {
    Text(stringResource(Res.string.label_favourite))
  }
}
