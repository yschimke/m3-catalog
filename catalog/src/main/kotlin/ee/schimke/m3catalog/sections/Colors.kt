@file:CatalogGroup(name = "Color", section = "Styles")

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ColorScheme
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

private data class CatalogSwatch(val name: String, val color: Color, val content: Color)

private fun ColorScheme.catalogSwatches(): List<CatalogSwatch> =
  listOf(
    CatalogSwatch("primary", primary, onPrimary),
    CatalogSwatch("onPrimary", onPrimary, primary),
    CatalogSwatch("primaryContainer", primaryContainer, onPrimaryContainer),
    CatalogSwatch("onPrimaryContainer", onPrimaryContainer, primaryContainer),
    CatalogSwatch("secondary", secondary, onSecondary),
    CatalogSwatch("onSecondary", onSecondary, secondary),
    CatalogSwatch("secondaryContainer", secondaryContainer, onSecondaryContainer),
    CatalogSwatch("onSecondaryContainer", onSecondaryContainer, secondaryContainer),
    CatalogSwatch("tertiary", tertiary, onTertiary),
    CatalogSwatch("onTertiary", onTertiary, tertiary),
    CatalogSwatch("tertiaryContainer", tertiaryContainer, onTertiaryContainer),
    CatalogSwatch("onTertiaryContainer", onTertiaryContainer, tertiaryContainer),
    CatalogSwatch("error", error, onError),
    CatalogSwatch("onError", onError, error),
    CatalogSwatch("errorContainer", errorContainer, onErrorContainer),
    CatalogSwatch("onErrorContainer", onErrorContainer, errorContainer),
    CatalogSwatch("primaryFixed", primaryFixed, onPrimaryFixed),
    CatalogSwatch("primaryFixedDim", primaryFixedDim, onPrimaryFixedVariant),
    CatalogSwatch("onPrimaryFixed", onPrimaryFixed, primaryFixed),
    CatalogSwatch("onPrimaryFixedVariant", onPrimaryFixedVariant, primaryFixed),
    CatalogSwatch("secondaryFixed", secondaryFixed, onSecondaryFixed),
    CatalogSwatch("secondaryFixedDim", secondaryFixedDim, onSecondaryFixedVariant),
    CatalogSwatch("onSecondaryFixed", onSecondaryFixed, secondaryFixed),
    CatalogSwatch("onSecondaryFixedVariant", onSecondaryFixedVariant, secondaryFixed),
    CatalogSwatch("tertiaryFixed", tertiaryFixed, onTertiaryFixed),
    CatalogSwatch("tertiaryFixedDim", tertiaryFixedDim, onTertiaryFixedVariant),
    CatalogSwatch("onTertiaryFixed", onTertiaryFixed, tertiaryFixed),
    CatalogSwatch("onTertiaryFixedVariant", onTertiaryFixedVariant, tertiaryFixed),
    CatalogSwatch("surfaceDim", surfaceDim, onSurface),
    CatalogSwatch("surface", surface, onSurface),
    CatalogSwatch("surfaceBright", surfaceBright, onSurface),
    CatalogSwatch("inverseSurface", inverseSurface, inverseOnSurface),
    CatalogSwatch("surfaceContainerLowest", surfaceContainerLowest, onSurface),
    CatalogSwatch("surfaceContainerLow", surfaceContainerLow, onSurface),
    CatalogSwatch("surfaceContainer", surfaceContainer, onSurface),
    CatalogSwatch("surfaceContainerHigh", surfaceContainerHigh, onSurface),
    CatalogSwatch("surfaceContainerHighest", surfaceContainerHighest, onSurface),
    CatalogSwatch("onSurface", onSurface, surface),
    CatalogSwatch("onSurfaceVariant", onSurfaceVariant, surface),
    CatalogSwatch("outline", outline, surface),
    CatalogSwatch("outlineVariant", outlineVariant, onSurface),
    CatalogSwatch("inverseOnSurface", inverseOnSurface, inverseSurface),
    CatalogSwatch("inversePrimary", inversePrimary, inverseSurface),
    CatalogSwatch("scrim", scrim, Color.White),
  )

@Composable
private fun Swatch(swatch: CatalogSwatch, modifier: Modifier = Modifier) {
  Text(
    text = swatch.name,
    modifier = modifier.height(48.dp).background(swatch.color).padding(8.dp),
    color = swatch.content,
    style = MaterialTheme.typography.labelSmall,
    maxLines = 2,
    overflow = TextOverflow.Ellipsis,
  )
}

@Composable
private fun ColorGrid(scheme: ColorScheme) {
  val swatches = scheme.catalogSwatches()
  Column(Modifier.fillMaxWidth().background(scheme.surface)) {
    swatches.chunked(4).forEach { row ->
      Row(Modifier.fillMaxWidth()) {
        row.forEach { swatch -> Swatch(swatch, Modifier.weight(1f)) }
        repeat(4 - row.size) {
          Swatch(CatalogSwatch("", scheme.surface, scheme.onSurface), Modifier.weight(1f))
        }
      }
    }
  }
}

@CatalogComponent(
  id = "Color/Role grid",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/53699:35493",
  caption = "Material color roles as a swatch grid, with all six catalog themes as variants.",
)
@Preview(name = "Light", group = "modes", widthDp = 720)
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
    ColorGrid(scheme)
  }
}
