@file:CatalogGroup(name = "Toolbars", section = "Actions")
@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.VerticalFloatingToolbar
import androidx.compose.runtime.Composable
import ee.schimke.composeai.overrides.previewOverrideBoolean
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
import ee.schimke.m3catalog.generated.resources.action_new
import org.jetbrains.compose.resources.stringResource

// The expressive floating toolbar: a small cluster of related actions floating over content.
//
// Orientation is two composables rather than a parameter, so horizontal and vertical are separate
// components. Within each, the kit documents expanded/collapsed and standard/vibrant colours, and
// the horizontal form additionally takes a FAB.

@Composable
private fun ToolbarActions() {
  val add = counted(stringResource(Res.string.action_add))
  val edit = counted(stringResource(Res.string.action_edit))
  val more = counted(stringResource(Res.string.action_more))
  IconButton(onClick = add.onClick) {
    Icon(Icons.Filled.Add, contentDescription = stringResource(Res.string.action_add))
  }
  IconButton(onClick = edit.onClick) {
    Icon(Icons.Filled.Edit, contentDescription = stringResource(Res.string.action_edit))
  }
  IconButton(onClick = more.onClick) {
    Icon(Icons.Filled.MoreVert, contentDescription = stringResource(Res.string.action_more))
  }
}

@CatalogComponent(
  id = "Toolbar/HorizontalFloating",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58467:8210",
  caption = "A floating cluster of related actions over the content. Expanded and vibrant fold in.",
)
@CatalogModes
@OverrideVariant(name = "collapsed", booleans = ["expanded=false"])
@OverrideVariant(name = "vibrant", booleans = ["vibrant=true"])
@OverrideVariant(name = "collapsed-vibrant", booleans = ["expanded=false", "vibrant=true"])
@Composable
fun HorizontalFloatingToolbarSticker() = Sticker {
  val vibrant = previewOverrideBoolean("vibrant", false)
  HorizontalFloatingToolbar(
    expanded = previewOverrideBoolean("expanded", true),
    colors =
      if (vibrant) FloatingToolbarDefaults.vibrantFloatingToolbarColors()
      else FloatingToolbarDefaults.standardFloatingToolbarColors(),
  ) {
    ToolbarActions()
  }
}

@CatalogComponent(
  id = "Toolbar/VerticalFloating",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58467:8325",
  caption = "The side-anchored form. Expanded and vibrant fold in.",
)
@CatalogModes
@OverrideVariant(name = "collapsed", booleans = ["expanded=false"])
@OverrideVariant(name = "vibrant", booleans = ["vibrant=true"])
@Composable
fun VerticalFloatingToolbarSticker() = Sticker {
  val vibrant = previewOverrideBoolean("vibrant", false)
  VerticalFloatingToolbar(
    expanded = previewOverrideBoolean("expanded", true),
    colors =
      if (vibrant) FloatingToolbarDefaults.vibrantFloatingToolbarColors()
      else FloatingToolbarDefaults.standardFloatingToolbarColors(),
  ) {
    ToolbarActions()
  }
}

@CatalogComponent(
  id = "Toolbar/WithFab",
  caption = "Horizontal toolbar paired with the screen's primary action.",
)
@CatalogModes
@OverrideVariant(name = "collapsed", booleans = ["expanded=false"])
@Composable
fun FloatingToolbarWithFab() = Sticker {
  val fab = counted(stringResource(Res.string.action_new))
  HorizontalFloatingToolbar(
    expanded = previewOverrideBoolean("expanded", true),
    floatingActionButton = {
      FloatingActionButton(onClick = fab.onClick) {
        Icon(Icons.Filled.Add, contentDescription = fab.label)
      }
    },
  ) {
    ToolbarActions()
  }
}
