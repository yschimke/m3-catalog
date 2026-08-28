@file:CatalogGroup(name = "Color", section = "Styles")

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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

/** One accent family: the 51dp role over its 40dp on-role, twice, split by the kit's 4dp gutter. */
@Composable
private fun AccentColumn(
  role: CatalogSwatch,
  onRole: CatalogSwatch,
  container: CatalogSwatch,
  onContainer: CatalogSwatch,
  modifier: Modifier = Modifier,
) {
  Column(modifier) {
    Swatch(role, Modifier.fillMaxWidth().height(51.dp))
    Swatch(onRole, Modifier.fillMaxWidth().height(40.dp))
    Spacer(Modifier.height(4.dp))
    Swatch(container, Modifier.fillMaxWidth().height(51.dp))
    Swatch(onContainer, Modifier.fillMaxWidth().height(40.dp))
  }
}

/** One fixed family: the fixed/fixed-dim pair side by side, then the two on-fixed roles. */
@Composable
private fun FixedColumn(
  fixed: CatalogSwatch,
  fixedDim: CatalogSwatch,
  onFixed: CatalogSwatch,
  onFixedVariant: CatalogSwatch,
  modifier: Modifier = Modifier,
) {
  Column(modifier) {
    Row(Modifier.fillMaxWidth().height(56.dp)) {
      Swatch(fixed, Modifier.weight(1f).fillMaxHeight())
      Swatch(fixedDim, Modifier.weight(1f).fillMaxHeight())
    }
    Swatch(onFixed, Modifier.fillMaxWidth().height(36.dp))
    Swatch(onFixedVariant, Modifier.fillMaxWidth().height(36.dp))
  }
}

@Composable
private fun SurfacesBand(scheme: ColorScheme, modifier: Modifier = Modifier) {
  Column(modifier) {
    Row(Modifier.fillMaxWidth().height(69.dp)) {
      Swatch(
        CatalogSwatch("surfaceDim", scheme.surfaceDim, scheme.onSurface),
        Modifier.weight(1f).fillMaxHeight(),
      )
      Swatch(
        CatalogSwatch("surface", scheme.surface, scheme.onSurface),
        Modifier.weight(1f).fillMaxHeight(),
      )
      Swatch(
        CatalogSwatch("surfaceBright", scheme.surfaceBright, scheme.onSurface),
        Modifier.weight(1f).fillMaxHeight(),
      )
    }
    Spacer(Modifier.height(4.dp))
    Row(Modifier.fillMaxWidth().height(69.dp)) {
      Swatch(
        CatalogSwatch("surfaceContainerLowest", scheme.surfaceContainerLowest, scheme.onSurface),
        Modifier.weight(1f).fillMaxHeight(),
      )
      Swatch(
        CatalogSwatch("surfaceContainerLow", scheme.surfaceContainerLow, scheme.onSurface),
        Modifier.weight(1f).fillMaxHeight(),
      )
      Swatch(
        CatalogSwatch("surfaceContainer", scheme.surfaceContainer, scheme.onSurface),
        Modifier.weight(1f).fillMaxHeight(),
      )
      Swatch(
        CatalogSwatch("surfaceContainerHigh", scheme.surfaceContainerHigh, scheme.onSurface),
        Modifier.weight(1f).fillMaxHeight(),
      )
      Swatch(
        CatalogSwatch("surfaceContainerHighest", scheme.surfaceContainerHighest, scheme.onSurface),
        Modifier.weight(1f).fillMaxHeight(),
      )
    }
    Spacer(Modifier.height(4.dp))
    Row(Modifier.fillMaxWidth().height(40.dp)) {
      Swatch(
        CatalogSwatch("onSurface", scheme.onSurface, scheme.surface),
        Modifier.weight(1f).fillMaxHeight(),
      )
      Swatch(
        CatalogSwatch("onSurfaceVariant", scheme.onSurfaceVariant, scheme.surface),
        Modifier.weight(1f).fillMaxHeight(),
      )
      Swatch(
        CatalogSwatch("outline", scheme.outline, scheme.surface),
        Modifier.weight(1f).fillMaxHeight(),
      )
      Swatch(
        CatalogSwatch("outlineVariant", scheme.outlineVariant, scheme.onSurface),
        Modifier.weight(1f).fillMaxHeight(),
      )
    }
  }
}

@Composable
private fun InversesColumn(scheme: ColorScheme, modifier: Modifier = Modifier) {
  Column(modifier) {
    Swatch(
      CatalogSwatch("inverseSurface", scheme.inverseSurface, scheme.inverseOnSurface),
      Modifier.fillMaxWidth().height(48.dp),
    )
    Swatch(
      CatalogSwatch("inverseOnSurface", scheme.inverseOnSurface, scheme.inverseSurface),
      Modifier.fillMaxWidth().height(38.dp),
    )
    Spacer(Modifier.height(4.dp))
    Swatch(
      CatalogSwatch("inversePrimary", scheme.inversePrimary, scheme.inverseSurface),
      Modifier.fillMaxWidth().height(40.dp),
    )
    Spacer(Modifier.height(16.dp))
    // The kit pairs Scrim with a Shadow swatch of the same 90dp width. `ColorScheme` has no shadow
    // role — Compose draws elevation shadows from the platform, not from a colour role — so the
    // second slot in this row stays empty rather than publishing a colour the library never
    // exposes.
    Row(Modifier.fillMaxWidth().height(40.dp)) {
      Swatch(
        CatalogSwatch("scrim", scheme.scrim, Color.White),
        Modifier.width(90.dp).fillMaxHeight(),
      )
    }
  }
}

@Composable
private fun ColorGrid(scheme: ColorScheme) {
  Column(Modifier.width(SCHEMATIC_WIDTH_DP.dp).background(scheme.surface)) {
    Row(Modifier.fillMaxWidth().height(186.dp)) {
      Row(Modifier.width(COLUMN_GROUP_WIDTH_DP.dp).fillMaxHeight()) {
        AccentColumn(
          CatalogSwatch("primary", scheme.primary, scheme.onPrimary),
          CatalogSwatch("onPrimary", scheme.onPrimary, scheme.primary),
          CatalogSwatch("primaryContainer", scheme.primaryContainer, scheme.onPrimaryContainer),
          CatalogSwatch("onPrimaryContainer", scheme.onPrimaryContainer, scheme.primaryContainer),
          Modifier.width(COLUMN_WIDTH_DP.dp).fillMaxHeight(),
        )
        Spacer(Modifier.width(4.dp))
        AccentColumn(
          CatalogSwatch("secondary", scheme.secondary, scheme.onSecondary),
          CatalogSwatch("onSecondary", scheme.onSecondary, scheme.secondary),
          CatalogSwatch(
            "secondaryContainer",
            scheme.secondaryContainer,
            scheme.onSecondaryContainer,
          ),
          CatalogSwatch(
            "onSecondaryContainer",
            scheme.onSecondaryContainer,
            scheme.secondaryContainer,
          ),
          Modifier.width(COLUMN_WIDTH_DP.dp).fillMaxHeight(),
        )
        Spacer(Modifier.width(4.dp))
        AccentColumn(
          CatalogSwatch("tertiary", scheme.tertiary, scheme.onTertiary),
          CatalogSwatch("onTertiary", scheme.onTertiary, scheme.tertiary),
          CatalogSwatch("tertiaryContainer", scheme.tertiaryContainer, scheme.onTertiaryContainer),
          CatalogSwatch(
            "onTertiaryContainer",
            scheme.onTertiaryContainer,
            scheme.tertiaryContainer,
          ),
          Modifier.width(COLUMN_WIDTH_DP.dp).fillMaxHeight(),
        )
      }
      Spacer(Modifier.width(16.dp))
      AccentColumn(
        CatalogSwatch("error", scheme.error, scheme.onError),
        CatalogSwatch("onError", scheme.onError, scheme.error),
        CatalogSwatch("errorContainer", scheme.errorContainer, scheme.onErrorContainer),
        CatalogSwatch("onErrorContainer", scheme.onErrorContainer, scheme.errorContainer),
        Modifier.width(COLUMN_WIDTH_DP.dp).fillMaxHeight(),
      )
    }
    Spacer(Modifier.height(16.dp))
    // The fixed band is 596dp wide, not 808: the kit leaves the error column's width empty here.
    Row(Modifier.width(COLUMN_GROUP_WIDTH_DP.dp).height(128.dp)) {
      FixedColumn(
        CatalogSwatch("primaryFixed", scheme.primaryFixed, scheme.onPrimaryFixed),
        CatalogSwatch("primaryFixedDim", scheme.primaryFixedDim, scheme.onPrimaryFixedVariant),
        CatalogSwatch("onPrimaryFixed", scheme.onPrimaryFixed, scheme.primaryFixed),
        CatalogSwatch("onPrimaryFixedVariant", scheme.onPrimaryFixedVariant, scheme.primaryFixed),
        Modifier.width(COLUMN_WIDTH_DP.dp).fillMaxHeight(),
      )
      Spacer(Modifier.width(4.dp))
      FixedColumn(
        CatalogSwatch("secondaryFixed", scheme.secondaryFixed, scheme.onSecondaryFixed),
        CatalogSwatch(
          "secondaryFixedDim",
          scheme.secondaryFixedDim,
          scheme.onSecondaryFixedVariant,
        ),
        CatalogSwatch("onSecondaryFixed", scheme.onSecondaryFixed, scheme.secondaryFixed),
        CatalogSwatch(
          "onSecondaryFixedVariant",
          scheme.onSecondaryFixedVariant,
          scheme.secondaryFixed,
        ),
        Modifier.width(COLUMN_WIDTH_DP.dp).fillMaxHeight(),
      )
      Spacer(Modifier.width(4.dp))
      FixedColumn(
        CatalogSwatch("tertiaryFixed", scheme.tertiaryFixed, scheme.onTertiaryFixed),
        CatalogSwatch("tertiaryFixedDim", scheme.tertiaryFixedDim, scheme.onTertiaryFixedVariant),
        CatalogSwatch("onTertiaryFixed", scheme.onTertiaryFixed, scheme.tertiaryFixed),
        CatalogSwatch(
          "onTertiaryFixedVariant",
          scheme.onTertiaryFixedVariant,
          scheme.tertiaryFixed,
        ),
        Modifier.width(COLUMN_WIDTH_DP.dp).fillMaxHeight(),
      )
    }
    Spacer(Modifier.height(16.dp))
    Row(Modifier.fillMaxWidth().height(186.dp)) {
      SurfacesBand(scheme, Modifier.width(COLUMN_GROUP_WIDTH_DP.dp).fillMaxHeight())
      Spacer(Modifier.width(16.dp))
      InversesColumn(scheme, Modifier.width(COLUMN_WIDTH_DP.dp).fillMaxHeight())
    }
  }
}

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
    ColorGrid(scheme)
  }
}
