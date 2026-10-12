@file:CatalogGroup(name = "Color", section = "Styles")

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.m3catalog.CatalogSchemeChoice
import ee.schimke.m3catalog.CatalogShapes
import ee.schimke.m3catalog.CatalogTypography
import ee.schimke.m3catalog.ColorSchemeMatrix
import ee.schimke.m3catalog.LocalCatalogThemeStyle
import ee.schimke.m3catalog.StickerFrame
import generated.uibuilder.KitColorRoleGrid

/**
 * The kit's "Schematic group" is one 808x532 block per scheme, and every dimension below is read
 * off that node (`53699:35493`, the light half). It is not a uniform grid: the accent families are
 * 196dp columns of a 51/40 pair over a 51/40 pair, the fixed colors are a separate 128dp band with
 * no error column, and the surfaces band splits into rows of three, five and four. Chunking the
 * roles four to a row instead published a picture the kit never draws (#203).
 */
private const val SCHEMATIC_WIDTH_DP = 808
private const val COLUMN_WIDTH_DP = 196
private const val COLUMN_GROUP_WIDTH_DP = 596

private data class CatalogSwatch(val name: String, val color: Color, val content: Color)

@Composable
private fun Swatch(swatch: CatalogSwatch, modifier: Modifier = Modifier) {
  Text(
    text = swatch.name,
    // The kit insets every role label 12dp from the leading edge and 10dp from the top; the
    // shortest swatch is 36dp, so there is no bottom padding to give.
    modifier = modifier.background(swatch.color).padding(start = 12.dp, top = 10.dp, end = 12.dp),
    color = swatch.content,
    style = MaterialTheme.typography.labelSmall,
    maxLines = 2,
    overflow = TextOverflow.Ellipsis,
  )
}

// --- Generated sticker bodies, from uid-stickers/Colors.kt ---

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
