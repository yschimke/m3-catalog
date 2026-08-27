@file:CatalogGroup(name = "Bottom app bar", section = "Navigation")
@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.overrides.previewOverrideBoolean
import ee.schimke.composeai.overrides.previewOverrideString
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModesCompact
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.counted
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.action_add
import ee.schimke.m3catalog.generated.resources.action_check
import ee.schimke.m3catalog.generated.resources.action_edit
import ee.schimke.m3catalog.generated.resources.action_more
import org.jetbrains.compose.resources.stringResource

// The kit's axes: the FAB slot, and how many actions sit beside it (two to four).

@CatalogComponent(
  id = "BottomAppBar/Standard",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/51159:5115",
  caption = "Screen-level actions along the bottom. FAB and action count fold in.",
)
@CatalogModesCompact
@OverrideVariant(name = "no-fab", booleans = ["fab=false"])
@OverrideVariant(name = "two-actions", strings = ["actions=2"])
@OverrideVariant(name = "four-actions", strings = ["actions=4"])
@OverrideVariant(name = "four-actions-no-fab", strings = ["actions=4"], booleans = ["fab=false"])
@Composable
fun BottomAppBarSticker() = Sticker {
  val check = counted(stringResource(Res.string.action_check))
  val edit = counted(stringResource(Res.string.action_edit))
  val more = counted(stringResource(Res.string.action_more))
  val add = counted(stringResource(Res.string.action_add))
  val count = previewOverrideString("actions", "3").toIntOrNull() ?: 3
  BottomAppBar(
    modifier = Modifier.width(412.dp),
    actions = {
      IconButton(onClick = check.onClick) {
        Icon(Icons.Filled.Search, contentDescription = check.label)
      }
      if (count >= 2) {
        IconButton(onClick = edit.onClick) {
          Icon(Icons.Filled.Delete, contentDescription = edit.label)
        }
      }
      if (count >= 3) {
        IconButton(onClick = more.onClick) {
          Icon(Icons.Filled.Download, contentDescription = more.label)
        }
      }
      if (count >= 4) {
        IconButton(onClick = add.onClick) { Icon(Icons.Filled.Add, contentDescription = add.label) }
      }
    },
    floatingActionButton =
      if (previewOverrideBoolean("fab", true)) {
        {
          FloatingActionButton(onClick = add.onClick) {
            Icon(Icons.Filled.Add, contentDescription = add.label)
          }
        }
      } else null,
  )
}
