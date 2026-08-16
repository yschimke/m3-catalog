@file:CatalogGroup(name = "Bottom sheets", section = "Containment")
@file:OptIn(ExperimentalMaterial3Api::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.overrides.previewOverrideBoolean
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.action_compose
import ee.schimke.m3catalog.generated.resources.action_delete
import ee.schimke.m3catalog.generated.resources.action_duplicate
import ee.schimke.m3catalog.generated.resources.action_share
import ee.schimke.m3catalog.generated.resources.label_add_to_favourites
import ee.schimke.m3catalog.generated.resources.label_report
import ee.schimke.m3catalog.generated.resources.sheet_share_to
import org.jetbrains.compose.resources.stringResource

// Like a dialog, a modal bottom sheet is hosted in its own window, so the sticker composes the
// complete modal scene from BottomSheetDefaults: scrim, expanded container, elevation and drag
// handle. The fixed scene matches the kit component's own published frame.
//
// The kit's axes: the drag handle, and an optional header line above the content.

@Composable
private fun SheetSurface(content: @Composable () -> Unit) {
  Box(Modifier.size(width = 492.dp, height = 794.dp).background(BottomSheetDefaults.ScrimColor)) {
    Surface(
      modifier = Modifier.align(Alignment.BottomCenter).width(412.dp).height(480.dp),
      shape = BottomSheetDefaults.ExpandedShape,
      color = BottomSheetDefaults.ContainerColor,
      tonalElevation = BottomSheetDefaults.Elevation,
    ) {
      content()
    }
  }
}

@Composable
private fun SheetBody() {
  Column(Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
    if (previewOverrideBoolean("handle", true)) {
      Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        BottomSheetDefaults.DragHandle()
      }
    }
    if (previewOverrideBoolean("header", false)) {
      Text(
        stringResource(Res.string.sheet_share_to),
        Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp),
        style = MaterialTheme.typography.titleLarge,
      )
    }
    if (previewOverrideBoolean("content", false)) {
      listOf(
          Res.string.action_share,
          Res.string.action_compose,
          Res.string.action_duplicate,
          Res.string.action_delete,
          Res.string.label_add_to_favourites,
          Res.string.label_report,
        )
        .forEach { label ->
          ListItem(
            headlineContent = { Text(stringResource(label)) },
            leadingContent = { Icon(Icons.Filled.Share, contentDescription = null) },
          )
        }
    }
  }
}

// Not a catalog comparison until popup surfaces can be captured (compose-ai-tools#3916).
@Composable fun ModalBottomSheetSticker() = Sticker { SheetSurface { SheetBody() } }
