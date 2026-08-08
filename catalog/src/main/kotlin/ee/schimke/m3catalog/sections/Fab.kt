@file:CatalogGroup(name = "FAB", section = "Actions")
@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeExtendedFloatingActionButton
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MediumExtendedFloatingActionButton
import androidx.compose.material3.MediumFloatingActionButton
import androidx.compose.material3.SmallExtendedFloatingActionButton
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.counted
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.action_compose
import ee.schimke.m3catalog.generated.resources.action_edit
import org.jetbrains.compose.resources.stringResource

// The floating action button family: one primary action, raised above the content.
//
// Sizes are SEPARATE COMPOSABLES here (`SmallFloatingActionButton`, `MediumFloatingActionButton`,
// …), not a size parameter, so the knob-plus-`@OverrideVariant` matrix the buttons use does not
// apply — a knob cannot pick which function to call. Each size is its own `@CatalogVariant` folded
// under the standard FAB, which lands in the same place on the sticker sheet by a different route.
//
// No shape axis: `FloatingActionButtonDefaults` exposes a shape per size but no square counterpart,
// so unlike a plain button there is no round/square variant property to document.

@CatalogComponent(
  id = "Fab/Standard",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/57998:43658",
  caption = "The screen's single primary action.",
)
@CatalogModes
@Composable
fun Fab() = Sticker {
  val c = counted(stringResource(Res.string.action_edit))
  FloatingActionButton(onClick = c.onClick) {
    Icon(Icons.Filled.Edit, contentDescription = c.label)
  }
}

@CatalogVariant(of = "Fab/Standard", props = ["size=small"], caption = "Compact surfaces.")
@CatalogModes
@Composable
fun FabSmall() = Sticker {
  val c = counted(stringResource(Res.string.action_edit))
  SmallFloatingActionButton(onClick = c.onClick) {
    Icon(Icons.Filled.Edit, contentDescription = c.label)
  }
}

@CatalogVariant(of = "Fab/Standard", props = ["size=medium"], caption = "The expressive mid size.")
@CatalogModes
@Composable
fun FabMedium() = Sticker {
  val c = counted(stringResource(Res.string.action_edit))
  MediumFloatingActionButton(onClick = c.onClick) {
    Icon(Icons.Filled.Edit, contentDescription = c.label)
  }
}

@CatalogVariant(of = "Fab/Standard", props = ["size=large"], caption = "Expansive surfaces.")
@CatalogModes
@Composable
fun FabLarge() = Sticker {
  val c = counted(stringResource(Res.string.action_edit))
  LargeFloatingActionButton(onClick = c.onClick) {
    Icon(Icons.Filled.Edit, contentDescription = c.label)
  }
}

@CatalogComponent(
  id = "Fab/Extended",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/57998:43398",
  caption = "FAB with a label; the action is named.",
)
@CatalogModes
@Composable
fun ExtendedFab() = Sticker {
  val c = counted(stringResource(Res.string.action_compose))
  ExtendedFloatingActionButton(
    onClick = c.onClick,
    icon = { Icon(Icons.Filled.Edit, contentDescription = null) },
    text = { Text(c.label) },
  )
}

@CatalogVariant(of = "Fab/Extended", props = ["size=small"])
@CatalogModes
@Composable
fun ExtendedFabSmall() = Sticker {
  val c = counted(stringResource(Res.string.action_compose))
  SmallExtendedFloatingActionButton(
    onClick = c.onClick,
    icon = { Icon(Icons.Filled.Edit, contentDescription = null) },
    text = { Text(c.label) },
  )
}

@CatalogVariant(of = "Fab/Extended", props = ["size=medium"])
@CatalogModes
@Composable
fun ExtendedFabMedium() = Sticker {
  val c = counted(stringResource(Res.string.action_compose))
  MediumExtendedFloatingActionButton(
    onClick = c.onClick,
    icon = { Icon(Icons.Filled.Edit, contentDescription = null) },
    text = { Text(c.label) },
  )
}

@CatalogVariant(of = "Fab/Extended", props = ["size=large"])
@CatalogModes
@Composable
fun ExtendedFabLarge() = Sticker {
  val c = counted(stringResource(Res.string.action_compose))
  LargeExtendedFloatingActionButton(
    onClick = c.onClick,
    icon = { Icon(Icons.Filled.Edit, contentDescription = null) },
    text = { Text(c.label) },
  )
}
