package ee.schimke.m3catalog

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.tooling.preview.PreviewWrapperProvider
import ee.schimke.composeai.preview.ThemeCatalog

/**
 * The catalog's **named themes**, declared as `@ThemeCatalog` wrapper providers.
 *
 * `@ColorCatalog` (see `CatalogTokens.kt`) catalogues a static token object by reflection — it
 * never enters composition. `@ThemeCatalog` catalogues a whole **resolved theme**: the plugin
 * composes the provider's `Wrap(content)` around a canned Material 3 role + type-scale grid, so the
 * synthesised specimen sheet shows the live `MaterialTheme.colorScheme` / `typography` the theme
 * actually resolves to. Those synthesised sheets are theme-fixed by construction, so the browse
 * surface never re-renders a "Baseline Light" card in dark.
 *
 * It is also the N-ary generalisation of `@Preview(uiMode = …)`: the catalog's themes include light
 * and dark, contrast tiers, and the standard/expressive behavior axis rather than only a single
 * light/dark bit. Each declaration becomes an entry in the preview server's **Theme** select, so
 * any sticker can be re-rendered under any of them.
 *
 * A provider sets [LocalCatalogScheme] and [LocalCatalogThemeStyle] rather than composing a
 * Material theme directly, because each sticker composes its own theme inside. Without the
 * handshake the inner theme would shadow the outer one and every declared theme would render
 * identically.
 */
@Composable
private fun ThemeOverride(scheme: ColorScheme, content: @Composable () -> Unit) {
  ThemeOverride(scheme, CatalogThemeStyle.Standard, content)
}

@Composable
private fun ThemeOverride(
  scheme: ColorScheme,
  style: CatalogThemeStyle,
  content: @Composable () -> Unit,
) {
  CompositionLocalProvider(
    LocalCatalogScheme provides scheme,
    LocalCatalogThemeStyle provides style,
    content = content,
  )
}

/** The kit's default mode: Material 3 baseline light. */
@ThemeCatalog(name = "Baseline Light", group = "Material 3")
class BaselineLightTheme : PreviewWrapperProvider {
  @Composable
  override fun Wrap(content: @Composable () -> Unit) = ThemeOverride(BaselineLight, content)
}

/** The kit's dark mode: Material 3 baseline dark. */
@ThemeCatalog(name = "Baseline Dark", group = "Material 3")
class BaselineDarkTheme : PreviewWrapperProvider {
  @Composable
  override fun Wrap(content: @Composable () -> Unit) = ThemeOverride(BaselineDark, content)
}

/** Material 3 Expressive's stock light color and motion theme. */
@ThemeCatalog(name = "Expressive Light", group = "Material 3 Expressive")
class ExpressiveLightTheme : PreviewWrapperProvider {
  @Composable
  override fun Wrap(content: @Composable () -> Unit) =
    ThemeOverride(ExpressiveLight, CatalogThemeStyle.Expressive, content)
}

/** Material 3 Expressive in dark mode; Compose shares the baseline dark color scheme. */
@ThemeCatalog(name = "Expressive Dark", group = "Material 3 Expressive")
class ExpressiveDarkTheme : PreviewWrapperProvider {
  @Composable
  override fun Wrap(content: @Composable () -> Unit) =
    ThemeOverride(ExpressiveDark, CatalogThemeStyle.Expressive, content)
}

// The kit's four accessibility contrast tiers. Compose has no primitive for these, so they are
// generated from the baseline seed — see `CatalogSchemes.kt` for what that does and does not
// reproduce exactly.

@ThemeCatalog(name = "Light Medium Contrast", group = "Contrast")
class LightMediumContrastTheme : PreviewWrapperProvider {
  @Composable
  override fun Wrap(content: @Composable () -> Unit) =
    ThemeOverride(BaselineLightMediumContrast, content)
}

@ThemeCatalog(name = "Light High Contrast", group = "Contrast")
class LightHighContrastTheme : PreviewWrapperProvider {
  @Composable
  override fun Wrap(content: @Composable () -> Unit) =
    ThemeOverride(BaselineLightHighContrast, content)
}

@ThemeCatalog(name = "Dark Medium Contrast", group = "Contrast")
class DarkMediumContrastTheme : PreviewWrapperProvider {
  @Composable
  override fun Wrap(content: @Composable () -> Unit) =
    ThemeOverride(BaselineDarkMediumContrast, content)
}

@ThemeCatalog(name = "Dark High Contrast", group = "Contrast")
class DarkHighContrastTheme : PreviewWrapperProvider {
  @Composable
  override fun Wrap(content: @Composable () -> Unit) =
    ThemeOverride(BaselineDarkHighContrast, content)
}
