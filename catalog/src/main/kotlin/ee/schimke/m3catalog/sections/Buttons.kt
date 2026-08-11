@file:CatalogGroup(name = "Buttons", section = "Actions")
@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant
import ee.schimke.m3catalog.CatalogFilledStars
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.CatalogSize
import ee.schimke.m3catalog.SizeShapeMatrix
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogButtonShape
import ee.schimke.m3catalog.catalogButtonSize
import ee.schimke.m3catalog.catalogEnabled
import ee.schimke.m3catalog.counted
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.label_elevated
import ee.schimke.m3catalog.generated.resources.label_filled
import ee.schimke.m3catalog.generated.resources.label_outlined
import ee.schimke.m3catalog.generated.resources.label_text
import ee.schimke.m3catalog.generated.resources.label_tonal
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

/**
 * The button's label at the type scale its size carries.
 *
 * M3 scales type with the container rather than holding one label size across a 32dp-to-136dp
 * range, so a size variant that changed only the box would render a correct height around visibly
 * wrong text.
 */
@Composable
private fun SizedLabel(label: String) {
  ProvideTextStyle(catalogButtonSize().labelStyle) { Text(label) }
}

@Composable
private fun FigmaButtonContent(label: String) {
  val size = catalogButtonSize()
  Icon(CatalogFilledStars, contentDescription = null, modifier = Modifier.size(size.iconSize))
  Spacer(Modifier.width(size.iconSpacing))
  SizedLabel(label)
}

@Composable
private fun ButtonFrame(size: CatalogSize, content: @Composable () -> Unit) {
  Box(
    modifier = Modifier.height(if (size == CatalogSize.Small) 48.dp else size.containerHeight),
    contentAlignment = Alignment.Center,
  ) {
    content()
  }
}

@CatalogComponent(
  id = "Button/Filled",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/57994:2324",
  caption = "Highest emphasis; the primary action. Five sizes x two shapes fold in as variants.",
)
@CatalogModes
@SizeShapeMatrix
@Composable
fun FilledButton() = Sticker {
  val c = counted(stringResource(Res.string.label_filled))
  val size = catalogButtonSize()
  ButtonFrame(size) {
    Button(
      onClick = c.onClick,
      enabled = catalogEnabled(),
      shape = catalogButtonShape(),
      contentPadding = size.contentPadding,
      modifier = Modifier.height(size.containerHeight),
    ) {
      FigmaButtonContent(c.label)
    }
  }
}

@CatalogComponent(
  id = "Button/Tonal",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/57994:2304",
  caption = "Secondary, still prominent. Five sizes x two shapes fold in as variants.",
)
@CatalogModes
@SizeShapeMatrix
@Composable
fun TonalButton() = Sticker {
  val c = counted(stringResource(Res.string.label_tonal))
  val size = catalogButtonSize()
  ButtonFrame(size) {
    FilledTonalButton(
      onClick = c.onClick,
      enabled = catalogEnabled(),
      shape = catalogButtonShape(),
      contentPadding = size.contentPadding,
      modifier = Modifier.height(size.containerHeight),
    ) {
      FigmaButtonContent(c.label)
    }
  }
}

@CatalogComponent(
  id = "Button/Outlined",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/57994:2284",
  caption = "Medium emphasis on a busy surface. Five sizes x two shapes fold in as variants.",
)
@CatalogModes
@SizeShapeMatrix
@Composable
fun OutlinedButtonSticker() = Sticker {
  val c = counted(stringResource(Res.string.label_outlined))
  val size = catalogButtonSize()
  ButtonFrame(size) {
    OutlinedButton(
      onClick = c.onClick,
      enabled = catalogEnabled(),
      shape = catalogButtonShape(),
      contentPadding = size.contentPadding,
      modifier = Modifier.height(size.containerHeight),
    ) {
      FigmaButtonContent(c.label)
    }
  }
}

@CatalogComponent(
  id = "Button/Elevated",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/57994:2244",
  caption = "Outlined alternative needing separation. Five sizes x two shapes fold in as variants.",
)
@CatalogModes
@SizeShapeMatrix
@Composable
fun ElevatedButtonSticker() = Sticker {
  val c = counted(stringResource(Res.string.label_elevated))
  val size = catalogButtonSize()
  Box(Modifier.padding(start = 4.dp, top = 4.dp, end = 4.dp, bottom = 5.dp)) {
    ElevatedButton(
      onClick = c.onClick,
      enabled = catalogEnabled(),
      shape = catalogButtonShape(),
      contentPadding = size.contentPadding,
      modifier = Modifier.height(size.containerHeight),
    ) {
      FigmaButtonContent(c.label)
    }
  }
}

@CatalogComponent(
  id = "Button/Text",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/57994:2264",
  caption = "Lowest emphasis; inline actions. Five sizes x two shapes fold in as variants.",
)
@CatalogModes
@SizeShapeMatrix
@Composable
fun TextButtonSticker() = Sticker {
  val c = counted(stringResource(Res.string.label_text))
  val size = catalogButtonSize()
  ButtonFrame(size) {
    TextButton(
      onClick = c.onClick,
      enabled = catalogEnabled(),
      shape = catalogButtonShape(),
      contentPadding = size.contentPadding,
      modifier = Modifier.height(size.containerHeight),
    ) {
      FigmaButtonContent(c.label)
    }
  }
}

// --- Content axes, folded under Button/Filled ---------------------------------------------------

@CatalogVariant(
  of = "Button/Filled",
  props = ["content=label"],
  caption = "Label only, vs the kit's icon + label default.",
)
@CatalogModes
@Composable
fun FilledButtonLabelOnly() = Sticker {
  val c = counted(stringResource(Res.string.label_filled))
  Button(onClick = c.onClick) { Text(c.label) }
}
