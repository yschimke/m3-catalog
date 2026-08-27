@file:CatalogGroup(name = "Buttons", section = "Actions")
@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import ee.schimke.composeai.preview.CaptureGutter
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant
import ee.schimke.m3catalog.CatalogFilledStars
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.CatalogSize
import ee.schimke.m3catalog.ContainerlessInteractionStates
import ee.schimke.m3catalog.InteractionStates
import ee.schimke.m3catalog.KitShadowGutter
import ee.schimke.m3catalog.SizeShapeMatrix
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogButtonShape
import ee.schimke.m3catalog.catalogButtonShapes
import ee.schimke.m3catalog.catalogButtonSize
import ee.schimke.m3catalog.catalogEnabled
import ee.schimke.m3catalog.catalogExpressive
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
//
// Every sticker renders under BOTH theme styles, and Compose splits that across two overloads:
// `shape: Shape` for the standard one, `shapes: ButtonShapes` for the expressive one, with no
// parameter in common. So the branch cannot go away — but the four arguments both arms pass can be
// resolved once, above it, instead of being written out per arm (#104).

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

/**
 * The cell a button sticker renders in: the size's container height, content centred.
 *
 * Identical for all five emphases, and that is the point: they publish one basis for their render
 * bounds at every size, so the sheet can lay them side by side and have the comparison be about the
 * design. Room for `Button/Elevated`'s Level 1 shadow is
 * [ee.schimke.composeai.preview.CaptureGutter] on that sticker's preview, not padding in here — a
 * gutter inside the frame would measure the button in a smaller box and grow its canvas, which is
 * what drew the elevated arm 7% smaller than its siblings (#179, and #102 for the crop the gutter
 * fixes).
 */
@Composable
private fun ButtonFrame(size: CatalogSize, content: @Composable () -> Unit) {
  val height = if (size == CatalogSize.Small) 48.dp else size.containerHeight
  Box(modifier = Modifier.height(height), contentAlignment = Alignment.Center) { content() }
}

@CatalogComponent(
  id = "Button/Filled",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/57994:2324",
  caption = "Highest emphasis; the primary action. Five sizes x two shapes fold in as variants.",
)
@CatalogModes
@SizeShapeMatrix
@InteractionStates
@Composable
fun FilledButton() = Sticker {
  val c = counted(stringResource(Res.string.label_filled))
  val size = catalogButtonSize()
  ButtonFrame(size) {
    val enabled = catalogEnabled()
    val padding = size.contentPadding
    val modifier = Modifier.height(size.containerHeight)
    val content: @Composable RowScope.() -> Unit = { FigmaButtonContent(c.label) }
    if (catalogExpressive()) {
      val shapes = catalogButtonShapes(size)
      Button(c.onClick, shapes, modifier, enabled, contentPadding = padding, content = content)
    } else {
      val shape = catalogButtonShape()
      Button(c.onClick, modifier, enabled, shape, contentPadding = padding, content = content)
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
@InteractionStates
@Composable
fun TonalButton() = Sticker {
  val c = counted(stringResource(Res.string.label_tonal))
  val size = catalogButtonSize()
  ButtonFrame(size) {
    val enabled = catalogEnabled()
    val pad = size.contentPadding
    val modifier = Modifier.height(size.containerHeight)
    val content: @Composable RowScope.() -> Unit = { FigmaButtonContent(c.label) }
    if (catalogExpressive()) {
      val shapes = catalogButtonShapes(size)
      FilledTonalButton(
        c.onClick,
        shapes,
        modifier,
        enabled,
        contentPadding = pad,
        content = content,
      )
    } else {
      val shape = catalogButtonShape()
      FilledTonalButton(
        c.onClick,
        modifier,
        enabled,
        shape,
        contentPadding = pad,
        content = content,
      )
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
@InteractionStates
@Composable
fun OutlinedButtonSticker() = Sticker {
  val c = counted(stringResource(Res.string.label_outlined))
  val size = catalogButtonSize()
  ButtonFrame(size) {
    val enabled = catalogEnabled()
    val pad = size.contentPadding
    val modifier = Modifier.height(size.containerHeight)
    val content: @Composable RowScope.() -> Unit = { FigmaButtonContent(c.label) }
    if (catalogExpressive()) {
      val shapes = catalogButtonShapes(size)
      OutlinedButton(c.onClick, shapes, modifier, enabled, contentPadding = pad, content = content)
    } else {
      val shape = catalogButtonShape()
      OutlinedButton(c.onClick, modifier, enabled, shape, contentPadding = pad, content = content)
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
@InteractionStates
@CaptureGutter(all = KitShadowGutter.Level1All, bottom = KitShadowGutter.Level1Bottom)
@Composable
fun ElevatedButtonSticker() = Sticker {
  val c = counted(stringResource(Res.string.label_elevated))
  val size = catalogButtonSize()
  ButtonFrame(size) {
    val enabled = catalogEnabled()
    val pad = size.contentPadding
    val modifier = Modifier.height(size.containerHeight)
    val content: @Composable RowScope.() -> Unit = { FigmaButtonContent(c.label) }
    if (catalogExpressive()) {
      val shapes = catalogButtonShapes(size)
      ElevatedButton(c.onClick, shapes, modifier, enabled, contentPadding = pad, content = content)
    } else {
      val shape = catalogButtonShape()
      ElevatedButton(c.onClick, modifier, enabled, shape, contentPadding = pad, content = content)
    }
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
@Composable
fun TextButtonSticker() = Sticker {
  val c = counted(stringResource(Res.string.label_text))
  val size = catalogButtonSize()
  ButtonFrame(size) {
    val enabled = catalogEnabled()
    val pad = size.contentPadding
    val modifier = Modifier.height(size.containerHeight)
    val content: @Composable RowScope.() -> Unit = { FigmaButtonContent(c.label) }
    if (catalogExpressive()) {
      val shapes = catalogButtonShapes(size)
      TextButton(c.onClick, shapes, modifier, enabled, contentPadding = pad, content = content)
    } else {
      val shape = catalogButtonShape()
      TextButton(c.onClick, modifier, enabled, shape, contentPadding = pad, content = content)
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
