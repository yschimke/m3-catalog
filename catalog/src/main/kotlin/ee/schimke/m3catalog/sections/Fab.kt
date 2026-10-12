@file:CatalogGroup(name = "FAB", section = "Actions")
@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)
package ee.schimke.m3catalog.sections

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import ee.schimke.composeai.preview.CaptureGutter
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogFilledStars
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Counted
import ee.schimke.m3catalog.InteractionStates
import ee.schimke.m3catalog.KitShadowGutter
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogChoice
import ee.schimke.m3catalog.catalogText
import ee.schimke.m3catalog.counted
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.label_text
import generated.uibuilder.KitExtendedFab
import generated.uibuilder.KitExtendedFabPrimary
import generated.uibuilder.KitExtendedFabSecondary
import generated.uibuilder.KitExtendedFabSecondaryContainer
import generated.uibuilder.KitExtendedFabTertiary
import generated.uibuilder.KitExtendedFabTertiaryContainer
import generated.uibuilder.KitFab
import generated.uibuilder.KitFabPrimary
import generated.uibuilder.KitFabSecondary
import generated.uibuilder.KitFabSecondaryContainer
import generated.uibuilder.KitFabTertiary
import generated.uibuilder.KitFabTertiaryContainer
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

@CatalogVariant(of = "Fab/Standard", props = ["size=small"], caption = "Compact surfaces.")
@CatalogModes
@Composable
fun FabSmall() = Sticker {
  val c = counted(stringResource(Res.string.label_text))
  SmallFloatingActionButton(onClick = c.onClick) {
    Icon(CatalogFilledStars, contentDescription = c.label)
  }
}

@CatalogVariant(of = "Fab/Standard", props = ["size=medium"], caption = "The expressive mid size.")
@CatalogModes
@Composable
fun FabMedium() = Sticker {
  val c = counted(stringResource(Res.string.label_text))
  MediumFloatingActionButton(onClick = c.onClick) {
    Icon(CatalogFilledStars, contentDescription = c.label)
  }
}

@CatalogVariant(of = "Fab/Standard", props = ["size=large"], caption = "Expansive surfaces.")
@CatalogModes
@Composable
fun FabLarge() = Sticker {
  val c = counted(stringResource(Res.string.label_text))
  LargeFloatingActionButton(onClick = c.onClick) {
    Icon(CatalogFilledStars, contentDescription = c.label)
  }
}

@CatalogVariant(of = "Fab/Extended", props = ["size=small"])
@CatalogModes
@Composable
fun ExtendedFabSmall() = Sticker {
  val c = counted(catalogText("label", stringResource(Res.string.label_text)))
  SmallExtendedFloatingActionButton(
    onClick = c.onClick,
    icon = { Icon(CatalogFilledStars, contentDescription = null) },
    text = { Text(c.label) },
  )
}

@CatalogVariant(of = "Fab/Extended", props = ["size=medium"])
@CatalogModes
@Composable
fun ExtendedFabMedium() = Sticker {
  val c = counted(catalogText("label", stringResource(Res.string.label_text)))
  MediumExtendedFloatingActionButton(
    onClick = c.onClick,
    icon = { Icon(CatalogFilledStars, contentDescription = null) },
    text = { Text(c.label) },
  )
}

@CatalogVariant(of = "Fab/Extended", props = ["size=large"])
@CatalogModes
@Composable
fun ExtendedFabLarge() = Sticker {
  val c = counted(catalogText("label", stringResource(Res.string.label_text)))
  LargeExtendedFloatingActionButton(
    onClick = c.onClick,
    icon = { Icon(CatalogFilledStars, contentDescription = null) },
    text = { Text(c.label) },
  )
}

// --- Generated sticker bodies, from uid-stickers/Fabs.kt ---

/** The `color` knob, read exactly as the hand-written sticker reads it. */
@Composable
private fun fabColour(): String =
  catalogChoice(
    "color",
    "primary-container",
    "primary-container",
    "secondary-container",
    "tertiary-container",
    "primary",
    "secondary",
    "tertiary",
  )

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
@InteractionStates
// A FAB's Level 3 shadow is deep and offset downward, and it falls outside the container's bounds.
// Room for it belongs to the capture: as padding inside the tree it measured the FAB in a smaller
// box and grew the canvas, which is what drew a guttered sticker smaller than its siblings (#179).
@CaptureGutter(
  start = KitShadowGutter.Level3Side,
  top = KitShadowGutter.Level3Top,
  end = KitShadowGutter.Level3Side,
  bottom = KitShadowGutter.Level3Bottom,
)
@ee.schimke.m3catalog.FabExhaustiveKitCells
@Composable
fun Fab() = Sticker {
  val c: Counted = counted(stringResource(Res.string.label_text))
  when (fabColour()) {
    "primary" -> KitFabPrimary(Modifier, label = c.label, onFabClick = c.onClick)
    "secondary" -> KitFabSecondary(Modifier, label = c.label, onFabClick = c.onClick)
    "tertiary" -> KitFabTertiary(Modifier, label = c.label, onFabClick = c.onClick)
    "secondary-container" ->
      KitFabSecondaryContainer(Modifier, label = c.label, onFabClick = c.onClick)
    "tertiary-container" ->
      KitFabTertiaryContainer(Modifier, label = c.label, onFabClick = c.onClick)
    else -> KitFab(Modifier, label = c.label, onFabClick = c.onClick)
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
@InteractionStates
// As `Fab`: the shadow gets its room from the capture, not from padding inside the bounds (#179).
@CaptureGutter(
  start = KitShadowGutter.Level3Side,
  top = KitShadowGutter.Level3Top,
  end = KitShadowGutter.Level3Side,
  bottom = KitShadowGutter.Level3Bottom,
)
@ee.schimke.m3catalog.ExtendedFabExhaustiveKitCells
@Composable
fun ExtendedFab() = Sticker {
  val c: Counted = counted(catalogText("label", stringResource(Res.string.label_text)))
  when (fabColour()) {
    "primary" -> KitExtendedFabPrimary(Modifier, label = c.label, onFabClick = c.onClick)
    "secondary" -> KitExtendedFabSecondary(Modifier, label = c.label, onFabClick = c.onClick)
    "tertiary" -> KitExtendedFabTertiary(Modifier, label = c.label, onFabClick = c.onClick)
    "secondary-container" ->
      KitExtendedFabSecondaryContainer(Modifier, label = c.label, onFabClick = c.onClick)
    "tertiary-container" ->
      KitExtendedFabTertiaryContainer(Modifier, label = c.label, onFabClick = c.onClick)
    else -> KitExtendedFab(Modifier, label = c.label, onFabClick = c.onClick)
  }
}
