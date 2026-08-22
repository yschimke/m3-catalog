@file:CatalogGroup(name = "Icon buttons", section = "Actions")
@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.InteractionGesture
import ee.schimke.composeai.preview.InteractionPreview
import ee.schimke.m3catalog.CatalogFilledStars
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.CatalogOutlinedStars
import ee.schimke.m3catalog.ContainerlessInteractionStates
import ee.schimke.m3catalog.IconButtonMatrix
import ee.schimke.m3catalog.InteractionStates
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogButtonSize
import ee.schimke.m3catalog.catalogEnabled
import ee.schimke.m3catalog.catalogExpressive
import ee.schimke.m3catalog.catalogIconContainerSize
import ee.schimke.m3catalog.catalogIconShape
import ee.schimke.m3catalog.catalogIconShapes
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.label_favourite
import ee.schimke.m3catalog.iconButtonIconSize
import ee.schimke.m3catalog.toggleable
import org.jetbrains.compose.resources.stringResource

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

@Composable
private fun IconButtonFrame(content: @Composable () -> Unit) {
  val container = catalogIconContainerSize(catalogButtonSize())
  Box(
    modifier = Modifier.size(container.width + 8.dp, container.height + 8.dp),
    contentAlignment = Alignment.Center,
  ) {
    content()
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
@Composable
fun StandardIconButton() = Sticker {
  var on by toggleable(true)
  val size = catalogButtonSize()
  IconButtonFrame {
    val content: @Composable () -> Unit = {
      Icon(
        if (on) CatalogFilledStars else CatalogOutlinedStars,
        contentDescription = stringResource(Res.string.label_favourite),
        modifier = Modifier.size(size.iconButtonIconSize),
      )
    }
    val colors =
      IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
    val onClick = { on = !on }
    val enabled = catalogEnabled()
    val modifier = Modifier.size(catalogIconContainerSize(size))
    if (catalogExpressive()) {
      val shapes = catalogIconShapes(size)
      IconButton(onClick, shapes, modifier, enabled, colors = colors, content = content)
    } else {
      val shape = catalogIconShape(size)
      IconButton(onClick, modifier, enabled, colors, shape = shape, content = content)
    }
  }
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
@Composable
fun FilledIconButtonSticker() = Sticker {
  var on by toggleable(true)
  val size = catalogButtonSize()
  IconButtonFrame {
    val content: @Composable () -> Unit = {
      Icon(
        if (on) CatalogFilledStars else CatalogOutlinedStars,
        contentDescription = stringResource(Res.string.label_favourite),
        modifier = Modifier.size(size.iconButtonIconSize),
      )
    }
    val onClick = { on = !on }
    val enabled = catalogEnabled()
    val modifier = Modifier.size(catalogIconContainerSize(size))
    if (catalogExpressive()) {
      val shapes = catalogIconShapes(size)
      FilledIconButton(onClick, shapes, modifier, enabled, content = content)
    } else {
      val shape = catalogIconShape(size)
      FilledIconButton(onClick, modifier, enabled, shape, content = content)
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
@Composable
fun TonalIconButton() = Sticker {
  var on by toggleable(true)
  val size = catalogButtonSize()
  IconButtonFrame {
    val content: @Composable () -> Unit = {
      Icon(
        if (on) CatalogFilledStars else CatalogOutlinedStars,
        contentDescription = stringResource(Res.string.label_favourite),
        modifier = Modifier.size(size.iconButtonIconSize),
      )
    }
    val onClick = { on = !on }
    val enabled = catalogEnabled()
    val modifier = Modifier.size(catalogIconContainerSize(size))
    if (catalogExpressive()) {
      val shapes = catalogIconShapes(size)
      FilledTonalIconButton(onClick, shapes, modifier, enabled, content = content)
    } else {
      val shape = catalogIconShape(size)
      FilledTonalIconButton(onClick, modifier, enabled, shape, content = content)
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
@Composable
fun OutlinedIconButtonSticker() = Sticker {
  var on by toggleable(true)
  val size = catalogButtonSize()
  IconButtonFrame {
    val content: @Composable () -> Unit = {
      Icon(
        if (on) CatalogFilledStars else CatalogOutlinedStars,
        contentDescription = stringResource(Res.string.label_favourite),
        modifier = Modifier.size(size.iconButtonIconSize),
      )
    }
    val colors =
      IconButtonDefaults.outlinedIconButtonColors(
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
      )
    val border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    val onClick = { on = !on }
    val enabled = catalogEnabled()
    val modifier = Modifier.size(catalogIconContainerSize(size))
    if (catalogExpressive()) {
      val shapes = catalogIconShapes(size)
      OutlinedIconButton(onClick, shapes, modifier, enabled, colors, border, content = content)
    } else {
      val shape = catalogIconShape(size)
      OutlinedIconButton(onClick, modifier, enabled, shape, colors, border, content = content)
    }
  }
}
