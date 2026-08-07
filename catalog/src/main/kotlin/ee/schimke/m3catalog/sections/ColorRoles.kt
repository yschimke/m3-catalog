@file:CatalogGroup(name = "Color", section = "Styles")

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker

// The colour-role sheet: each role drawn as its container with its `on` colour written on top, so
// the swatch proves its own contrast pair rather than asserting it.
//
// Deliberately NOT `@FixedTheme`: a role sheet *should* redraw when a visitor picks another theme —
// showing the selected scheme's roles is the whole point. It is the per-theme specimen sheets that
// discovery synthesises from `@ThemeCatalog` (see `CatalogThemes.kt`) that stay pinned to the one
// theme they name.

@Composable
private fun Swatch(name: String, container: Color, onContainer: Color) {
  Box(
    Modifier.fillMaxWidth().height(44.dp).background(container).padding(horizontal = 12.dp),
    contentAlignment = Alignment.CenterStart,
  ) {
    Text(name, color = onContainer, style = MaterialTheme.typography.labelLarge)
  }
}

@Composable
private fun RoleColumn(title: String, rows: List<Triple<String, Color, Color>>) {
  Column(Modifier.width(200.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
    Text(
      title,
      style = MaterialTheme.typography.titleSmall,
      modifier = Modifier.padding(bottom = 4.dp),
    )
    rows.forEach { (name, container, onContainer) -> Swatch(name, container, onContainer) }
  }
}

@CatalogComponent(
  id = "Color/AccentRoles",
  caption = "Primary, secondary and tertiary — each accent role with its `on` pair.",
)
@CatalogModes
@Composable
fun AccentColorRoles() = Sticker {
  val s: ColorScheme = MaterialTheme.colorScheme
  Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
    RoleColumn(
      "Primary",
      listOf(
        Triple("primary", s.primary, s.onPrimary),
        Triple("onPrimary", s.onPrimary, s.primary),
        Triple("primaryContainer", s.primaryContainer, s.onPrimaryContainer),
        Triple("onPrimaryContainer", s.onPrimaryContainer, s.primaryContainer),
      ),
    )
    RoleColumn(
      "Secondary",
      listOf(
        Triple("secondary", s.secondary, s.onSecondary),
        Triple("onSecondary", s.onSecondary, s.secondary),
        Triple("secondaryContainer", s.secondaryContainer, s.onSecondaryContainer),
        Triple("onSecondaryContainer", s.onSecondaryContainer, s.secondaryContainer),
      ),
    )
    RoleColumn(
      "Tertiary",
      listOf(
        Triple("tertiary", s.tertiary, s.onTertiary),
        Triple("onTertiary", s.onTertiary, s.tertiary),
        Triple("tertiaryContainer", s.tertiaryContainer, s.onTertiaryContainer),
        Triple("onTertiaryContainer", s.onTertiaryContainer, s.tertiaryContainer),
      ),
    )
  }
}

@CatalogVariant(
  of = "Color/AccentRoles",
  props = ["roles=surface"],
  caption = "The surface container ramp and its content roles.",
)
@CatalogModes
@Composable
fun SurfaceColorRoles() = Sticker {
  val s: ColorScheme = MaterialTheme.colorScheme
  Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
    RoleColumn(
      "Surfaces",
      listOf(
        Triple("surface", s.surface, s.onSurface),
        Triple("surfaceDim", s.surfaceDim, s.onSurface),
        Triple("surfaceBright", s.surfaceBright, s.onSurface),
        Triple("surfaceVariant", s.surfaceVariant, s.onSurfaceVariant),
      ),
    )
    RoleColumn(
      "Containers",
      listOf(
        Triple("containerLowest", s.surfaceContainerLowest, s.onSurface),
        Triple("containerLow", s.surfaceContainerLow, s.onSurface),
        Triple("container", s.surfaceContainer, s.onSurface),
        Triple("containerHigh", s.surfaceContainerHigh, s.onSurface),
        Triple("containerHighest", s.surfaceContainerHighest, s.onSurface),
      ),
    )
    RoleColumn(
      "Utility",
      listOf(
        Triple("error", s.error, s.onError),
        Triple("errorContainer", s.errorContainer, s.onErrorContainer),
        Triple("outline", s.outline, s.surface),
        Triple("outlineVariant", s.outlineVariant, s.onSurface),
        Triple("inverseSurface", s.inverseSurface, s.inverseOnSurface),
      ),
    )
  }
}
