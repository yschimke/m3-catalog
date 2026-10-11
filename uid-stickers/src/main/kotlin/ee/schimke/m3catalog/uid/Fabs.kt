@file:CatalogGroup(name = "FAB", section = "Actions")

package ee.schimke.m3catalog.uid

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ee.schimke.composeai.preview.CaptureGutter
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
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

// `stickers/fab.uid` and `stickers/extended-fab.uid`: a `KitFab` / `KitExtendedFab` component
// whose root is the Material FAB, with its `containerColor` a theme colour role and its content
// colour left to `contentColorFor`, as the hand-written sticker leaves it. The kit's six container
// roles are cells (`primaryContainer` is the document itself; the other five each set the role),
// so the colour stays a theme role in the document rather than a `Color` the shell computes. The
// FAB's glyph names the action (`m3catalog/described-stars-icon`), and the extended FAB's glyph is
// decorative beside its label. The shell reads the same knobs in the same order and picks the cell.

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
