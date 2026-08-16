@file:CatalogGroup(name = "Toolbars", section = "Actions")
@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalFloatingToolbar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.overrides.previewOverrideBoolean
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogChoice
import ee.schimke.m3catalog.counted
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.action_add
import ee.schimke.m3catalog.generated.resources.action_edit
import ee.schimke.m3catalog.generated.resources.action_more
import org.jetbrains.compose.resources.stringResource

// The expressive floating toolbar: a small cluster of related actions floating over content.
//
// The kit models one `Toolbar` set varying `Configuration` x `Orientation` x `Color`, so this is
// one component. Colour and the expanded/collapsed axis were already folded as cells; orientation
// now folds the same way, as a `@CatalogVariant` — two composables, one component.

@Composable
private fun ToolbarActions() {
  val add = counted(stringResource(Res.string.action_add))
  val edit = counted(stringResource(Res.string.action_edit))
  val more = counted(stringResource(Res.string.action_more))
  IconButton(onClick = add.onClick) {
    Icon(
      Icons.Filled.Share,
      contentDescription = stringResource(Res.string.action_add),
      tint = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
  IconButton(onClick = edit.onClick) {
    Icon(
      Icons.Filled.Comment,
      contentDescription = stringResource(Res.string.action_edit),
      tint = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
  IconButton(onClick = more.onClick) {
    Icon(
      Icons.Filled.Download,
      contentDescription = stringResource(Res.string.action_more),
      tint = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}

@CatalogComponent(
  id = "Toolbar/HorizontalFloating",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58467:8210",
  caption = "A floating cluster of related actions over the content. Expanded and vibrant fold in.",
)
@CatalogModes
@OverrideVariant(name = "collapsed", booleans = ["expanded=false"])
@OverrideVariant(name = "vibrant", strings = ["color=vibrant"])
@OverrideVariant(
  name = "collapsed-vibrant",
  booleans = ["expanded=false"],
  strings = ["color=vibrant"],
)
@Composable
fun HorizontalFloatingToolbarSticker() = Sticker {
  val vibrant = catalogChoice("color", "standard", "standard", "vibrant") == "vibrant"
  Box(Modifier.padding(11.dp)) {
    HorizontalFloatingToolbar(
      modifier = Modifier.width(168.dp),
      expanded = previewOverrideBoolean("expanded", true),
      colors =
        if (vibrant) FloatingToolbarDefaults.vibrantFloatingToolbarColors()
        else FloatingToolbarDefaults.standardFloatingToolbarColors(),
    ) {
      ToolbarActions()
    }
  }
}

@CatalogVariant(
  of = "Toolbar/HorizontalFloating",
  props = ["orientation=vertical"],
  caption = "The side-anchored form. Vibrant folds in.",
)
@CatalogModes
@OverrideVariant(name = "vibrant", strings = ["color=vibrant"])
@OverrideVariant(name = "collapsed", booleans = ["expanded=false"])
@OverrideVariant(name = "vibrant", strings = ["color=vibrant"])
@Composable
fun VerticalFloatingToolbarSticker() = Sticker {
  val vibrant = catalogChoice("color", "standard", "standard", "vibrant") == "vibrant"
  Box(Modifier.padding(11.dp)) {
    VerticalFloatingToolbar(
      modifier = Modifier.height(168.dp),
      expanded = previewOverrideBoolean("expanded", true),
      colors =
        if (vibrant) FloatingToolbarDefaults.vibrantFloatingToolbarColors()
        else FloatingToolbarDefaults.standardFloatingToolbarColors(),
    ) {
      ToolbarActions()
    }
  }
}
