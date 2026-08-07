@file:CatalogGroup(name = "Icon buttons", section = "Actions")
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.runtime.Composable
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.toggleable

// Icon buttons carry no label, so they read as a favourite **toggle** on the interactive lane
// rather than taking the click tally: the glyph swaps between outlined and filled.

@CatalogComponent(
  id = "IconButton/Standard",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58663:30358",
  caption = "Lowest emphasis; inline on a surface.",
)
@CatalogModes
@Composable
fun StandardIconButton() = Sticker {
  val (on, set) = toggleable(false)
  IconButton(onClick = { set(!on) }) {
    Icon(
      if (on) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
      contentDescription = "Favourite",
    )
  }
}

@CatalogVariant(of = "IconButton/Standard", props = ["emphasis=filled"])
@CatalogModes
@Composable
fun FilledIconButtonSticker() = Sticker {
  val (on, set) = toggleable(true)
  FilledIconButton(onClick = { set(!on) }) {
    Icon(
      if (on) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
      contentDescription = "Favourite",
    )
  }
}

@CatalogVariant(of = "IconButton/Standard", props = ["emphasis=tonal"])
@CatalogModes
@Composable
fun TonalIconButton() = Sticker {
  val (on, set) = toggleable(true)
  FilledTonalIconButton(onClick = { set(!on) }) {
    Icon(
      if (on) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
      contentDescription = "Favourite",
    )
  }
}

@CatalogVariant(of = "IconButton/Standard", props = ["emphasis=outlined"])
@CatalogModes
@Composable
fun OutlinedIconButtonSticker() = Sticker {
  val (on, set) = toggleable(false)
  OutlinedIconButton(onClick = { set(!on) }) {
    Icon(
      if (on) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
      contentDescription = "Favourite",
    )
  }
}
