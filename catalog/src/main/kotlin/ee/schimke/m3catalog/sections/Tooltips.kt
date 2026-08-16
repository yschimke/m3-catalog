@file:CatalogGroup(name = "Tooltips", section = "Communication")
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogChoice
import ee.schimke.m3catalog.counted
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.action_action
import ee.schimke.m3catalog.generated.resources.action_learn_more
import ee.schimke.m3catalog.generated.resources.label_add_to_favourites
import ee.schimke.m3catalog.generated.resources.tooltip_body
import ee.schimke.m3catalog.generated.resources.tooltip_title
import org.jetbrains.compose.resources.stringResource

// A tooltip is normally keyed to hover / long-press, neither of which a baked capture can express.
// `PlainTooltip` / `RichTooltip` are popup-hosted `TooltipScope` extensions and cannot be captured
// by the renderer's single surface. These stickers therefore compose their containers from
// TooltipDefaults' real shapes, colours and elevation, following the components' own layout.
//
// Plain and rich are two composables, so two components. Inside each the kit's axes are parameters:
// the plain tooltip's caret, and the rich tooltip's title and action.

// Not catalog comparisons until popup surfaces can be captured (compose-ai-tools#3916).
@Composable
fun PlainTooltipSticker() = Sticker {
  Surface(
    shape = TooltipDefaults.plainTooltipContainerShape,
    color = TooltipDefaults.plainTooltipContainerColor,
    contentColor = TooltipDefaults.plainTooltipContentColor,
  ) {
    Box(Modifier.width(104.dp).height(24.dp).padding(horizontal = 8.dp, vertical = 4.dp)) {
      Text(
        stringResource(Res.string.label_add_to_favourites),
        style = MaterialTheme.typography.bodySmall,
      )
    }
  }
}

@Composable
fun RichTooltipSticker() = Sticker {
  val primary = counted(stringResource(Res.string.action_action))
  val secondary = counted(stringResource(Res.string.action_action))
  val colors = TooltipDefaults.richTooltipColors()
  Box(Modifier.padding(start = 8.dp, top = 6.dp, end = 8.dp, bottom = 10.dp)) {
    Surface(
      modifier = Modifier.width(312.dp).height(136.dp),
      shape = TooltipDefaults.richTooltipContainerShape,
      color = colors.containerColor,
      contentColor = colors.contentColor,
      shadowElevation = 6.dp,
    ) {
      Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
        if (catalogChoice("title", "on", "on", "off") != "off") {
          Text(
            stringResource(Res.string.tooltip_title),
            color = colors.titleContentColor,
            style = MaterialTheme.typography.titleSmall,
          )
        }
        Text(
          stringResource(Res.string.tooltip_body),
          modifier = Modifier.padding(top = 4.dp),
          style = MaterialTheme.typography.bodyMedium,
        )
        if (catalogChoice("action", "on", "on", "off", "legacy") != "off") {
          Row(Modifier.padding(top = 4.dp)) {
            if (catalogChoice("action", "on", "on", "off", "legacy") == "legacy") {
              val learn = counted(stringResource(Res.string.action_learn_more))
              TextButton(onClick = learn.onClick) { Text(learn.label) }
            } else {
              TextButton(onClick = primary.onClick) { Text(primary.label) }
              TextButton(onClick = secondary.onClick) { Text(secondary.label) }
            }
          }
        }
      }
    }
  }
}
