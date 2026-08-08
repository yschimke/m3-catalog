@file:CatalogGroup(name = "Icon buttons", section = "Actions")
@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
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
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogButtonSize
import ee.schimke.m3catalog.catalogIconContainerSize
import ee.schimke.m3catalog.catalogIconShape
import ee.schimke.m3catalog.iconButtonIconSize
import ee.schimke.m3catalog.toggleable

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
@OverrideVariant(name = "xs-narrow", strings = ["size=xs", "width=narrow"])
@OverrideVariant(name = "xs-narrow-square", strings = ["size=xs", "width=narrow", "shape=square"])
@OverrideVariant(name = "xs", strings = ["size=xs"])
@OverrideVariant(name = "xs-square", strings = ["size=xs", "shape=square"])
@OverrideVariant(name = "xs-wide", strings = ["size=xs", "width=wide"])
@OverrideVariant(name = "xs-wide-square", strings = ["size=xs", "width=wide", "shape=square"])
@OverrideVariant(name = "s-narrow", strings = ["size=s", "width=narrow"])
@OverrideVariant(name = "s-narrow-square", strings = ["size=s", "width=narrow", "shape=square"])
@OverrideVariant(name = "s-square", strings = ["shape=square"])
@OverrideVariant(name = "s-wide", strings = ["size=s", "width=wide"])
@OverrideVariant(name = "s-wide-square", strings = ["size=s", "width=wide", "shape=square"])
@OverrideVariant(name = "m-narrow", strings = ["size=m", "width=narrow"])
@OverrideVariant(name = "m-narrow-square", strings = ["size=m", "width=narrow", "shape=square"])
@OverrideVariant(name = "m", strings = ["size=m"])
@OverrideVariant(name = "m-square", strings = ["size=m", "shape=square"])
@OverrideVariant(name = "m-wide", strings = ["size=m", "width=wide"])
@OverrideVariant(name = "m-wide-square", strings = ["size=m", "width=wide", "shape=square"])
@OverrideVariant(name = "l-narrow", strings = ["size=l", "width=narrow"])
@OverrideVariant(name = "l-narrow-square", strings = ["size=l", "width=narrow", "shape=square"])
@OverrideVariant(name = "l", strings = ["size=l"])
@OverrideVariant(name = "l-square", strings = ["size=l", "shape=square"])
@OverrideVariant(name = "l-wide", strings = ["size=l", "width=wide"])
@OverrideVariant(name = "l-wide-square", strings = ["size=l", "width=wide", "shape=square"])
@OverrideVariant(name = "xl-narrow", strings = ["size=xl", "width=narrow"])
@OverrideVariant(name = "xl-narrow-square", strings = ["size=xl", "width=narrow", "shape=square"])
@OverrideVariant(name = "xl", strings = ["size=xl"])
@OverrideVariant(name = "xl-square", strings = ["size=xl", "shape=square"])
@OverrideVariant(name = "xl-wide", strings = ["size=xl", "width=wide"])
@OverrideVariant(name = "xl-wide-square", strings = ["size=xl", "width=wide", "shape=square"])
@Composable
fun StandardIconButton() = Sticker {
  val (on, set) = toggleable(false)
  val size = catalogButtonSize()
  IconButton(
    onClick = { set(!on) },
    shape = catalogIconShape(size),
    modifier = Modifier.size(catalogIconContainerSize(size)),
  ) {
    Icon(
      if (on) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
      contentDescription = "Favourite",
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
@OverrideVariant(name = "xs-narrow", strings = ["size=xs", "width=narrow"])
@OverrideVariant(name = "xs-narrow-square", strings = ["size=xs", "width=narrow", "shape=square"])
@OverrideVariant(name = "xs", strings = ["size=xs"])
@OverrideVariant(name = "xs-square", strings = ["size=xs", "shape=square"])
@OverrideVariant(name = "xs-wide", strings = ["size=xs", "width=wide"])
@OverrideVariant(name = "xs-wide-square", strings = ["size=xs", "width=wide", "shape=square"])
@OverrideVariant(name = "s-narrow", strings = ["size=s", "width=narrow"])
@OverrideVariant(name = "s-narrow-square", strings = ["size=s", "width=narrow", "shape=square"])
@OverrideVariant(name = "s-square", strings = ["shape=square"])
@OverrideVariant(name = "s-wide", strings = ["size=s", "width=wide"])
@OverrideVariant(name = "s-wide-square", strings = ["size=s", "width=wide", "shape=square"])
@OverrideVariant(name = "m-narrow", strings = ["size=m", "width=narrow"])
@OverrideVariant(name = "m-narrow-square", strings = ["size=m", "width=narrow", "shape=square"])
@OverrideVariant(name = "m", strings = ["size=m"])
@OverrideVariant(name = "m-square", strings = ["size=m", "shape=square"])
@OverrideVariant(name = "m-wide", strings = ["size=m", "width=wide"])
@OverrideVariant(name = "m-wide-square", strings = ["size=m", "width=wide", "shape=square"])
@OverrideVariant(name = "l-narrow", strings = ["size=l", "width=narrow"])
@OverrideVariant(name = "l-narrow-square", strings = ["size=l", "width=narrow", "shape=square"])
@OverrideVariant(name = "l", strings = ["size=l"])
@OverrideVariant(name = "l-square", strings = ["size=l", "shape=square"])
@OverrideVariant(name = "l-wide", strings = ["size=l", "width=wide"])
@OverrideVariant(name = "l-wide-square", strings = ["size=l", "width=wide", "shape=square"])
@OverrideVariant(name = "xl-narrow", strings = ["size=xl", "width=narrow"])
@OverrideVariant(name = "xl-narrow-square", strings = ["size=xl", "width=narrow", "shape=square"])
@OverrideVariant(name = "xl", strings = ["size=xl"])
@OverrideVariant(name = "xl-square", strings = ["size=xl", "shape=square"])
@OverrideVariant(name = "xl-wide", strings = ["size=xl", "width=wide"])
@OverrideVariant(name = "xl-wide-square", strings = ["size=xl", "width=wide", "shape=square"])
@Composable
fun FilledIconButtonSticker() = Sticker {
  val (on, set) = toggleable(true)
  val size = catalogButtonSize()
  FilledIconButton(
    onClick = { set(!on) },
    shape = catalogIconShape(size),
    modifier = Modifier.size(catalogIconContainerSize(size)),
  ) {
    Icon(
      if (on) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
      contentDescription = "Favourite",
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
@OverrideVariant(name = "xs-narrow", strings = ["size=xs", "width=narrow"])
@OverrideVariant(name = "xs-narrow-square", strings = ["size=xs", "width=narrow", "shape=square"])
@OverrideVariant(name = "xs", strings = ["size=xs"])
@OverrideVariant(name = "xs-square", strings = ["size=xs", "shape=square"])
@OverrideVariant(name = "xs-wide", strings = ["size=xs", "width=wide"])
@OverrideVariant(name = "xs-wide-square", strings = ["size=xs", "width=wide", "shape=square"])
@OverrideVariant(name = "s-narrow", strings = ["size=s", "width=narrow"])
@OverrideVariant(name = "s-narrow-square", strings = ["size=s", "width=narrow", "shape=square"])
@OverrideVariant(name = "s-square", strings = ["shape=square"])
@OverrideVariant(name = "s-wide", strings = ["size=s", "width=wide"])
@OverrideVariant(name = "s-wide-square", strings = ["size=s", "width=wide", "shape=square"])
@OverrideVariant(name = "m-narrow", strings = ["size=m", "width=narrow"])
@OverrideVariant(name = "m-narrow-square", strings = ["size=m", "width=narrow", "shape=square"])
@OverrideVariant(name = "m", strings = ["size=m"])
@OverrideVariant(name = "m-square", strings = ["size=m", "shape=square"])
@OverrideVariant(name = "m-wide", strings = ["size=m", "width=wide"])
@OverrideVariant(name = "m-wide-square", strings = ["size=m", "width=wide", "shape=square"])
@OverrideVariant(name = "l-narrow", strings = ["size=l", "width=narrow"])
@OverrideVariant(name = "l-narrow-square", strings = ["size=l", "width=narrow", "shape=square"])
@OverrideVariant(name = "l", strings = ["size=l"])
@OverrideVariant(name = "l-square", strings = ["size=l", "shape=square"])
@OverrideVariant(name = "l-wide", strings = ["size=l", "width=wide"])
@OverrideVariant(name = "l-wide-square", strings = ["size=l", "width=wide", "shape=square"])
@OverrideVariant(name = "xl-narrow", strings = ["size=xl", "width=narrow"])
@OverrideVariant(name = "xl-narrow-square", strings = ["size=xl", "width=narrow", "shape=square"])
@OverrideVariant(name = "xl", strings = ["size=xl"])
@OverrideVariant(name = "xl-square", strings = ["size=xl", "shape=square"])
@OverrideVariant(name = "xl-wide", strings = ["size=xl", "width=wide"])
@OverrideVariant(name = "xl-wide-square", strings = ["size=xl", "width=wide", "shape=square"])
@Composable
fun TonalIconButton() = Sticker {
  val (on, set) = toggleable(true)
  val size = catalogButtonSize()
  FilledTonalIconButton(
    onClick = { set(!on) },
    shape = catalogIconShape(size),
    modifier = Modifier.size(catalogIconContainerSize(size)),
  ) {
    Icon(
      if (on) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
      contentDescription = "Favourite",
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
@OverrideVariant(name = "xs-narrow", strings = ["size=xs", "width=narrow"])
@OverrideVariant(name = "xs-narrow-square", strings = ["size=xs", "width=narrow", "shape=square"])
@OverrideVariant(name = "xs", strings = ["size=xs"])
@OverrideVariant(name = "xs-square", strings = ["size=xs", "shape=square"])
@OverrideVariant(name = "xs-wide", strings = ["size=xs", "width=wide"])
@OverrideVariant(name = "xs-wide-square", strings = ["size=xs", "width=wide", "shape=square"])
@OverrideVariant(name = "s-narrow", strings = ["size=s", "width=narrow"])
@OverrideVariant(name = "s-narrow-square", strings = ["size=s", "width=narrow", "shape=square"])
@OverrideVariant(name = "s-square", strings = ["shape=square"])
@OverrideVariant(name = "s-wide", strings = ["size=s", "width=wide"])
@OverrideVariant(name = "s-wide-square", strings = ["size=s", "width=wide", "shape=square"])
@OverrideVariant(name = "m-narrow", strings = ["size=m", "width=narrow"])
@OverrideVariant(name = "m-narrow-square", strings = ["size=m", "width=narrow", "shape=square"])
@OverrideVariant(name = "m", strings = ["size=m"])
@OverrideVariant(name = "m-square", strings = ["size=m", "shape=square"])
@OverrideVariant(name = "m-wide", strings = ["size=m", "width=wide"])
@OverrideVariant(name = "m-wide-square", strings = ["size=m", "width=wide", "shape=square"])
@OverrideVariant(name = "l-narrow", strings = ["size=l", "width=narrow"])
@OverrideVariant(name = "l-narrow-square", strings = ["size=l", "width=narrow", "shape=square"])
@OverrideVariant(name = "l", strings = ["size=l"])
@OverrideVariant(name = "l-square", strings = ["size=l", "shape=square"])
@OverrideVariant(name = "l-wide", strings = ["size=l", "width=wide"])
@OverrideVariant(name = "l-wide-square", strings = ["size=l", "width=wide", "shape=square"])
@OverrideVariant(name = "xl-narrow", strings = ["size=xl", "width=narrow"])
@OverrideVariant(name = "xl-narrow-square", strings = ["size=xl", "width=narrow", "shape=square"])
@OverrideVariant(name = "xl", strings = ["size=xl"])
@OverrideVariant(name = "xl-square", strings = ["size=xl", "shape=square"])
@OverrideVariant(name = "xl-wide", strings = ["size=xl", "width=wide"])
@OverrideVariant(name = "xl-wide-square", strings = ["size=xl", "width=wide", "shape=square"])
@Composable
fun OutlinedIconButtonSticker() = Sticker {
  val (on, set) = toggleable(false)
  val size = catalogButtonSize()
  OutlinedIconButton(
    onClick = { set(!on) },
    shape = catalogIconShape(size),
    modifier = Modifier.size(catalogIconContainerSize(size)),
  ) {
    Icon(
      if (on) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
      contentDescription = "Favourite",
      modifier = Modifier.size(size.iconButtonIconSize),
    )
  }
}
