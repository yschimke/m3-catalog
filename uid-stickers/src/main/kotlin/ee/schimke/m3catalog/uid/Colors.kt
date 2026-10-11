@file:CatalogGroup(name = "Color", section = "Styles")

package ee.schimke.m3catalog.uid

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.m3catalog.CatalogSchemeChoice
import ee.schimke.m3catalog.CatalogShapes
import ee.schimke.m3catalog.CatalogTypography
import ee.schimke.m3catalog.ColorSchemeMatrix
import ee.schimke.m3catalog.LocalCatalogThemeStyle
import ee.schimke.m3catalog.StickerFrame
import generated.uibuilder.KitColorRoleGrid

// The body is `stickers/color-role-grid.uid`: the kit's 808dp schematic group as nested rows and
// columns of `m3/text` swatches, each filled by a colour-role `background` modifier and inked with
// its paired role (scrim's ink is the one literal, white). Every colour is a theme token, so the
// document has no knobs at all: the scheme is the frame's, which the shell sets from the same
// `CatalogSchemeChoice` the hand-written sticker reads, and the six theme cells come from its
// `@ColorSchemeMatrix`.

/** The schematic's width, which the hand-written sticker's `@Preview` names. */
private const val SCHEMATIC_WIDTH_DP = 808

@CatalogComponent(
  id = "Color/Role grid",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/53699:35493",
  caption =
    "Material color roles in the kit's schematic layout — accent families, fixed colors and " +
      "surfaces — with all six catalog themes as variants.",
)
@Preview(name = "Light", group = "modes", widthDp = SCHEMATIC_WIDTH_DP)
@ColorSchemeMatrix
@Composable
fun ColorRoleGridSticker() {
  val scheme = CatalogSchemeChoice.currentScheme()
  StickerFrame(
    colorScheme = scheme,
    themeStyle = LocalCatalogThemeStyle.current,
    typography = CatalogTypography,
    shapes = CatalogShapes,
  ) {
    KitColorRoleGrid(Modifier)
  }
}
