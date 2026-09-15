@file:CatalogGroup(name = "Stack", section = "Containment")

package ee.schimke.m3catalog.glimmer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.xr.glimmer.Card
import androidx.xr.glimmer.CardDefaults
import androidx.xr.glimmer.Text
import androidx.xr.glimmer.stack.VerticalStack
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup

@CatalogComponent(
  id = "VerticalStack",
  // `Type=Cards` in the kit's `Stack` set (`40000042:4077`). The other published cell,
  // `Type=Home`, is a system-UI composition rather than a second Stack API.
  reference = "figma:HKfLClZDLRyMhf4IQQLna8/40000042:4078",
  caption =
    "Layers focusable content in depth, scaling and revealing the items behind the front one.",
)
@Preview
@Composable
fun VerticalStackSticker() = Sticker {
  VerticalStack(modifier = Modifier.width(KitContentWidth).height(178.dp)) {
    item(key = "front") {
      Card(
        modifier = Modifier.fillMaxSize().itemDecoration(CardDefaults.shape),
        title = { Text(stringResource(R.string.label_title)) },
        subtitle = { Text(stringResource(R.string.label_subtitle)) },
      ) {
        Text(stringResource(R.string.stack_card_body))
      }
    }
    item(key = "back") {
      Card(modifier = Modifier.fillMaxSize().itemDecoration(CardDefaults.shape)) {
        Text(stringResource(R.string.label_body))
      }
    }
  }
}
