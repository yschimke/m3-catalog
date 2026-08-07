@file:CatalogGroup(name = "Tooltips", section = "Communication")
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker

// A tooltip is normally keyed to hover / long-press, neither of which a baked capture can express.
// `PlainTooltip` / `RichTooltip` are `TooltipScope` extensions, so they can only be composed inside
// a `TooltipBox` — the sticker therefore holds a real `TooltipBox` open by seeding its state
// `initialIsVisible = true` and `isPersistent = true`, which is what the component looks like in
// use rather than a hand-drawn lookalike of its surface.

@CatalogComponent(id = "Tooltip/Plain", caption = "Short label for an unlabelled element.")
@CatalogModes
@Composable
fun PlainTooltipSticker() = Sticker {
  TooltipBox(
    positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
    tooltip = { PlainTooltip { Text("Add to favourites") } },
    state = rememberTooltipState(initialIsVisible = true, isPersistent = true),
  ) {
    IconButton(onClick = {}) { Icon(Icons.Filled.Favorite, contentDescription = "Favourite") }
  }
}

@CatalogVariant(
  of = "Tooltip/Plain",
  props = ["kind=rich"],
  caption = "Title, supporting text and a single action.",
)
@CatalogModes
@Composable
fun RichTooltipSticker() = Sticker {
  TooltipBox(
    positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
    tooltip = {
      RichTooltip(
        title = { Text("Favourites") },
        action = { TextButton(onClick = {}) { Text("Learn more") } },
      ) {
        Text("Items you mark are kept here and sync across your devices.")
      }
    },
    state = rememberTooltipState(initialIsVisible = true, isPersistent = true),
  ) {
    IconButton(onClick = {}) { Icon(Icons.Filled.Favorite, contentDescription = "Favourite") }
  }
}
