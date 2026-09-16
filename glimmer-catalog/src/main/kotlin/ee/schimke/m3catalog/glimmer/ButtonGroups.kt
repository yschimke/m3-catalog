@file:CatalogGroup(name = "Button group", section = "Actions")

package ee.schimke.m3catalog.glimmer

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.xr.glimmer.Button
import androidx.xr.glimmer.ButtonGroup
import androidx.xr.glimmer.Icon
import androidx.xr.glimmer.Text
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup

@CatalogComponent(
  id = "ButtonGroup",
  // `Type=Button set, Length=3+, Focus=Start` in the kit's `Button group` set
  // (`40000116:6496`). The group owns the initial focus and trailing scrim.
  reference = "figma:HKfLClZDLRyMhf4IQQLna8/40000116:6497",
  caption = "A horizontally scrolling set of buttons that enlarges and centers the focused item.",
)
@Preview
@Composable
fun ButtonGroupSticker() = Sticker {
  // The kit gives the strip 16dp horizontal insets. alpha19's temporary default is 44dp
  // (b/535205202), but contentPadding is a public caller parameter, so the design-led value belongs
  // here rather than being recorded as an unexplained divergence.
  ButtonGroup(
    modifier = Modifier.width(KitContentWidth),
    contentPadding = PaddingValues(horizontal = 16.dp),
  ) {
    repeat(3) {
      val c = counted(stringResource(R.string.label_button))
      Button(
        modifier = Modifier.width(146.dp),
        onClick = c.onClick,
        leadingIcon = { Icon(Icons.AutoMirrored.Rounded.Send, stringResource(R.string.cd_send)) },
      ) {
        Text(c.label)
      }
    }
  }
}
