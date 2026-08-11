@file:CatalogGroup(name = "FAB", section = "Actions")
@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeExtendedFloatingActionButton
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumExtendedFloatingActionButton
import androidx.compose.material3.MediumFloatingActionButton
import androidx.compose.material3.SmallExtendedFloatingActionButton
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import ee.schimke.composeai.overrides.previewOverrideString
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.counted
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.label_text
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
//
// Colour IS an axis, and a knob rather than more composables: the kit gives a FAB six container
// roles, and `containerColor` is one parameter on every FAB function. Only the standard and
// extended FABs carry the variants — the T-shape rule wants each axis covered once, not colour
// crossed with size.

/**
 * The container role from the `color` knob, defaulting to the one a bare FAB already uses.
 *
 * Values are spelled as the kit spells them (`secondary-container` for `Secondary container`) so
 * the design map resolves them without a per-value translation. `contentColor` is left to
 * `contentColorFor`, which is what picks the readable pairing — hard-coding it here would be
 * inventing a role the theme already knows.
 */
@Composable
private fun catalogFabColor(): Color =
  when (previewOverrideString("color", "primary-container")) {
    "primary" -> MaterialTheme.colorScheme.primary
    "secondary" -> MaterialTheme.colorScheme.secondary
    "tertiary" -> MaterialTheme.colorScheme.tertiary
    "secondary-container" -> MaterialTheme.colorScheme.secondaryContainer
    "tertiary-container" -> MaterialTheme.colorScheme.tertiaryContainer
    else -> MaterialTheme.colorScheme.primaryContainer
  }

@CatalogComponent(
  id = "Fab/Standard",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/57998:43658",
  caption = "The screen's single primary action. Six container roles fold in as variants.",
)
@CatalogModes
@OverrideVariant(name = "primary", strings = ["color=primary"])
@OverrideVariant(name = "secondary", strings = ["color=secondary"])
@OverrideVariant(name = "tertiary", strings = ["color=tertiary"])
@OverrideVariant(name = "secondary-container", strings = ["color=secondary-container"])
@OverrideVariant(name = "tertiary-container", strings = ["color=tertiary-container"])
@Composable
fun Fab() = Sticker {
  val c = counted(stringResource(Res.string.label_text))
  FloatingActionButton(onClick = c.onClick, containerColor = catalogFabColor()) {
    Icon(Icons.Filled.Stars, contentDescription = c.label)
  }
}

@CatalogVariant(of = "Fab/Standard", props = ["size=small"], caption = "Compact surfaces.")
@CatalogModes
@Composable
fun FabSmall() = Sticker {
  val c = counted(stringResource(Res.string.label_text))
  SmallFloatingActionButton(onClick = c.onClick) {
    Icon(Icons.Filled.Stars, contentDescription = c.label)
  }
}

@CatalogVariant(of = "Fab/Standard", props = ["size=medium"], caption = "The expressive mid size.")
@CatalogModes
@Composable
fun FabMedium() = Sticker {
  val c = counted(stringResource(Res.string.label_text))
  MediumFloatingActionButton(onClick = c.onClick) {
    Icon(Icons.Filled.Stars, contentDescription = c.label)
  }
}

@CatalogVariant(of = "Fab/Standard", props = ["size=large"], caption = "Expansive surfaces.")
@CatalogModes
@Composable
fun FabLarge() = Sticker {
  val c = counted(stringResource(Res.string.label_text))
  LargeFloatingActionButton(onClick = c.onClick) {
    Icon(Icons.Filled.Stars, contentDescription = c.label)
  }
}

@CatalogComponent(
  id = "Fab/Extended",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/57998:43398",
  caption = "FAB with a label; the action is named. Six container roles fold in as variants.",
)
@CatalogModes
@OverrideVariant(name = "primary", strings = ["color=primary"])
@OverrideVariant(name = "secondary", strings = ["color=secondary"])
@OverrideVariant(name = "tertiary", strings = ["color=tertiary"])
@OverrideVariant(name = "secondary-container", strings = ["color=secondary-container"])
@OverrideVariant(name = "tertiary-container", strings = ["color=tertiary-container"])
@Composable
fun ExtendedFab() = Sticker {
  val c = counted(stringResource(Res.string.label_text))
  ExtendedFloatingActionButton(
    onClick = c.onClick,
    containerColor = catalogFabColor(),
    icon = { Icon(Icons.Filled.Stars, contentDescription = null) },
    text = { Text(c.label) },
  )
}

@CatalogVariant(of = "Fab/Extended", props = ["size=small"])
@CatalogModes
@Composable
fun ExtendedFabSmall() = Sticker {
  val c = counted(stringResource(Res.string.label_text))
  SmallExtendedFloatingActionButton(
    onClick = c.onClick,
    icon = { Icon(Icons.Filled.Stars, contentDescription = null) },
    text = { Text(c.label) },
  )
}

@CatalogVariant(of = "Fab/Extended", props = ["size=medium"])
@CatalogModes
@Composable
fun ExtendedFabMedium() = Sticker {
  val c = counted(stringResource(Res.string.label_text))
  MediumExtendedFloatingActionButton(
    onClick = c.onClick,
    icon = { Icon(Icons.Filled.Stars, contentDescription = null) },
    text = { Text(c.label) },
  )
}

@CatalogVariant(of = "Fab/Extended", props = ["size=large"])
@CatalogModes
@Composable
fun ExtendedFabLarge() = Sticker {
  val c = counted(stringResource(Res.string.label_text))
  LargeExtendedFloatingActionButton(
    onClick = c.onClick,
    icon = { Icon(Icons.Filled.Stars, contentDescription = null) },
    text = { Text(c.label) },
  )
}
