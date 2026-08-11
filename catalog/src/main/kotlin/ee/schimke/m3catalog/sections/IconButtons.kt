@file:CatalogGroup(name = "Icon buttons", section = "Actions")
@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.outlined.Stars
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.IconButtonMatrix
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogButtonSize
import ee.schimke.m3catalog.catalogEnabled
import ee.schimke.m3catalog.catalogIconContainerSize
import ee.schimke.m3catalog.catalogIconShape
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.label_favourite
import ee.schimke.m3catalog.iconButtonIconSize
import ee.schimke.m3catalog.toggleable
import org.jetbrains.compose.resources.stringResource

// Icon buttons carry no label, so they read as a favourite TOGGLE on the interactive lane rather
// than taking the click tally: the glyph swaps between outlined and filled.
//
// Three axes here, not two. The kit lists colour, size, WIDTH and shape, so each emphasis is five
// sizes x three widths x two shapes — thirty cells, carried by one `@Preview` through knobs and a
// stacked `@OverrideVariant` per cell. Shapes are per-size constants because an icon button's
// corner radius tracks its container, which is why `catalogIconShape` takes the size.

@CatalogComponent(
  id = "IconButton/Standard",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/57994:10132",
  caption =
    "Lowest emphasis; inline on a surface. Five sizes x three widths x two shapes fold in as variants.",
)
@CatalogModes
@IconButtonMatrix
@Composable
fun StandardIconButton() = Sticker {
  val (on, set) = toggleable(true)
  val size = catalogButtonSize()
  IconButton(
    onClick = { set(!on) },
    enabled = catalogEnabled(),
    shape = catalogIconShape(size),
    modifier = Modifier.size(catalogIconContainerSize(size)),
  ) {
    Icon(
      if (on) Icons.Filled.Stars else Icons.Outlined.Stars,
      contentDescription = stringResource(Res.string.label_favourite),
      modifier = Modifier.size(size.iconButtonIconSize),
    )
  }
}

@CatalogComponent(
  id = "IconButton/Filled",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/57994:10312",
  caption =
    "Highest emphasis icon-only action. Five sizes x three widths x two shapes fold in as variants.",
)
@CatalogModes
@IconButtonMatrix
@Composable
fun FilledIconButtonSticker() = Sticker {
  val (on, set) = toggleable(true)
  val size = catalogButtonSize()
  FilledIconButton(
    onClick = { set(!on) },
    enabled = catalogEnabled(),
    shape = catalogIconShape(size),
    modifier = Modifier.size(catalogIconContainerSize(size)),
  ) {
    Icon(
      if (on) Icons.Filled.Stars else Icons.Outlined.Stars,
      contentDescription = stringResource(Res.string.label_favourite),
      modifier = Modifier.size(size.iconButtonIconSize),
    )
  }
}

@CatalogComponent(
  id = "IconButton/Tonal",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/57994:10252",
  caption = "Secondary emphasis. Five sizes x three widths x two shapes fold in as variants.",
)
@CatalogModes
@IconButtonMatrix
@Composable
fun TonalIconButton() = Sticker {
  val (on, set) = toggleable(true)
  val size = catalogButtonSize()
  FilledTonalIconButton(
    onClick = { set(!on) },
    enabled = catalogEnabled(),
    shape = catalogIconShape(size),
    modifier = Modifier.size(catalogIconContainerSize(size)),
  ) {
    Icon(
      if (on) Icons.Filled.Stars else Icons.Outlined.Stars,
      contentDescription = stringResource(Res.string.label_favourite),
      modifier = Modifier.size(size.iconButtonIconSize),
    )
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
@Composable
fun OutlinedIconButtonSticker() = Sticker {
  val (on, set) = toggleable(true)
  val size = catalogButtonSize()
  OutlinedIconButton(
    onClick = { set(!on) },
    enabled = catalogEnabled(),
    shape = catalogIconShape(size),
    modifier = Modifier.size(catalogIconContainerSize(size)),
  ) {
    Icon(
      if (on) Icons.Filled.Stars else Icons.Outlined.Stars,
      contentDescription = stringResource(Res.string.label_favourite),
      modifier = Modifier.size(size.iconButtonIconSize),
    )
  }
}
