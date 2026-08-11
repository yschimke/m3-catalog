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
import ee.schimke.composeai.overrides.previewOverrideString
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.counted
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.action_add
import ee.schimke.m3catalog.generated.resources.action_edit
import ee.schimke.m3catalog.generated.resources.action_more
import org.jetbrains.compose.resources.stringResource

// The expressive floating toolbar: a small cluster of related actions floating over content.
//
// Orientation is two composables rather than a parameter, so horizontal and vertical are separate
// components. Within each, the kit documents expanded/collapsed and standard/vibrant colours, and
// the horizontal and vertical forms are separate components.

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
  val vibrant = previewOverrideString("color", "standard") == "vibrant"
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

@CatalogComponent(
  id = "Toolbar/VerticalFloating",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58467:8325",
  caption = "The side-anchored form. Expanded and vibrant fold in.",
)
@CatalogModes
@OverrideVariant(name = "collapsed", booleans = ["expanded=false"])
@OverrideVariant(name = "vibrant", strings = ["color=vibrant"])
@Composable
fun VerticalFloatingToolbarSticker() = Sticker {
  val vibrant = previewOverrideString("color", "standard") == "vibrant"
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
