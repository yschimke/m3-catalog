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
import ee.schimke.composeai.overrides.previewOverrideString
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.counted

// A tooltip is normally keyed to hover / long-press, neither of which a baked capture can express.
// `PlainTooltip` / `RichTooltip` are `TooltipScope` extensions, so they can only be composed inside
// a `TooltipBox` — the sticker therefore holds a real `TooltipBox` open by seeding its state
// `initialIsVisible = true` and `isPersistent = true`, which is what the component looks like in
// use rather than a hand-drawn lookalike of its surface.
//
// Plain and rich are two composables, so two components. Inside each the kit's axes are parameters:
// the plain tooltip's caret, and the rich tooltip's title and action.

@CatalogComponent(
  id = "Tooltip/Plain",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/54061:33881",
  caption = "Short label for an unlabelled element. The caret pointing at the anchor folds in.",
)
@CatalogModes
@OverrideVariant(name = "caret", strings = ["caret=on"])
@Composable
fun PlainTooltipSticker() = Sticker {
  val anchor = counted("Favourite")
  TooltipBox(
    positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
    tooltip = {
      PlainTooltip(
        caretShape =
          if (previewOverrideString("caret", "off") == "on") TooltipDefaults.caretShape() else null
      ) {
        Text("Add to favourites")
      }
    },
    state = rememberTooltipState(initialIsVisible = true, isPersistent = true),
  ) {
    IconButton(onClick = anchor.onClick) {
      Icon(Icons.Filled.Favorite, contentDescription = anchor.label)
    }
  }
}

@CatalogComponent(
  id = "Tooltip/Rich",
  caption =
    "Longer guidance on its own surface. The title and the single action each fold in as knobs.",
)
@CatalogModes
@OverrideVariant(name = "no-title", strings = ["title=off"])
@OverrideVariant(name = "no-action", strings = ["action=off"])
@OverrideVariant(name = "body-only", strings = ["title=off", "action=off"])
@Composable
fun RichTooltipSticker() = Sticker {
  val anchor = counted("Favourite")
  val learn = counted("Learn more")
  TooltipBox(
    positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
    tooltip = {
      RichTooltip(
        title =
          if (previewOverrideString("title", "on") == "off") null else ({ Text("Favourites") }),
        action =
          if (previewOverrideString("action", "on") == "off") null
          else ({ TextButton(onClick = learn.onClick) { Text(learn.label) } }),
      ) {
        Text("Items you mark are kept here and sync across your devices.")
      }
    },
    state = rememberTooltipState(initialIsVisible = true, isPersistent = true),
  ) {
    IconButton(onClick = anchor.onClick) {
      Icon(Icons.Filled.Favorite, contentDescription = anchor.label)
    }
  }
}
