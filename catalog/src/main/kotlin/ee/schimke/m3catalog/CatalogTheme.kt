package ee.schimke.m3catalog

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * The sticker frame every component preview is wrapped in.
 *
 * Stock [MaterialTheme] over the kit's baseline scheme, so the `compose/theme` token set the
 * renderer extracts is the **real** Material 3 system rather than a bespoke palette. A uniform 16dp
 * [padding] frames each sticker so the sheet reads cleanly and the `compose/semantics-wireframe`
 * layout variant has breathing room around the component.
 *
 * The surface is deliberately [Color.Transparent]: a component sticker reads as a silhouette on the
 * viewer's backing, and `contentColor = onSurface` keeps text and icons themed against it. The
 * full-screen [FullScreenM3] frame keeps its opaque device background instead.
 */
@Composable
fun Sticker(content: @Composable () -> Unit) {
  StickerFrame(colorScheme = catalogColorScheme(), content = content)
}

/**
 * The scheme a sticker should paint itself in: the one an enclosing `@ThemeCatalog` provider
 * selected, else the light/dark baseline picked from `isSystemInDarkTheme()` (which the
 * `@CatalogModes` `uiMode` bit flips).
 *
 * Without this indirection a sticker's own [MaterialTheme] would shadow the theme the provider just
 * wrapped it in, and every declared theme's specimen sheet would render identically — which is the
 * bug the two-way handshake exists to avoid.
 */
@Composable
fun catalogColorScheme(): ColorScheme =
  LocalCatalogScheme.current ?: if (isSystemInDarkTheme()) BaselineDark else BaselineLight

/**
 * Set by a `@ThemeCatalog` provider (see `CatalogThemes.kt`); null means "use the light/dark axis".
 */
val LocalCatalogScheme = androidx.compose.runtime.staticCompositionLocalOf<ColorScheme?> { null }

/**
 * The sticker frame with its [colorScheme] / [typography] / [shapes] supplied by the caller — the
 * shared body of [Sticker] and of every `@ThemeCatalog` provider in `CatalogThemes.kt`, so a named
 * theme re-skins every sticker from this one place without touching a single preview.
 */
@Composable
fun StickerFrame(
  colorScheme: ColorScheme,
  typography: Typography = CatalogTypography,
  shapes: Shapes = CatalogShapes,
  content: @Composable () -> Unit,
) {
  CompositionLocalProvider(LocalGenericFonts provides CatalogGenericFonts) {
    MaterialTheme(colorScheme = colorScheme, typography = typography, shapes = shapes) {
      Surface(color = Color.Transparent, contentColor = MaterialTheme.colorScheme.onSurface) {
        Box(Modifier.padding(16.dp)) { content() }
      }
    }
  }
}

/**
 * Frame for **full-screen scaffold templates** — as opposed to the centred component [Sticker].
 * Just the theme filling the device with the opaque `background` surface; the template supplies its
 * own `Scaffold` and drives system-bar spacing through window insets (see [SYSTEM_BAR_INSET]).
 */
@Composable
fun FullScreenM3(content: @Composable () -> Unit) {
  MaterialTheme(
    colorScheme = catalogColorScheme(),
    typography = CatalogTypography,
    shapes = CatalogShapes,
  ) {
    Surface(Modifier.fillMaxSize()) { content() }
  }
}

/**
 * Height of the renderer's synthetic status / navigation bars (`SystemBarsFrame` draws both at
 * 24dp). The render environment has no real window insets behind that overlay, so a template feeds
 * this height to its `Scaffold` / `TopAppBar` `windowInsets` — reproducing a real edge-to-edge M3
 * scaffold rather than an outer padding that pushes the scaffold into a band.
 */
val SYSTEM_BAR_INSET = 24.dp

/**
 * The catalog's primary-mode multipreview: every component renders in both light and dark, the two
 * modes M3 ships. Stacking it on a composable yields the `· Light` / `· Dark` captures the sticker
 * sheet pairs. Further axes (states, content, breakpoints) are folded on per component with
 * `@OverrideVariant` / `@CatalogVariant` rather than by splitting this annotation.
 *
 * `uiMode = 32` is the raw value of Android's `Configuration.UI_MODE_NIGHT_YES` — the CMP desktop
 * source set has no `android.content.res.Configuration`, so the bit is written directly; the
 * renderer treats `uiMode` as an int and flips `isSystemInDarkTheme()`.
 *
 * No `showBackground`: the harness background stays transparent so a component sticker is a
 * silhouette on the viewer's checkerboard. [CatalogTemplate] keeps its device background.
 */
@Preview(name = "Light", group = "modes")
@Preview(name = "Dark", uiMode = 32, group = "modes")
annotation class CatalogModes

/**
 * Full-screen template multipreview: a phone (`id:pixel_8`) with `showSystemUi = true` so the
 * capture carries the synthetic OS status + nav chrome, in both light and dark.
 */
@Preview(name = "Light", device = "id:pixel_8", showSystemUi = true, group = "template")
@Preview(name = "Dark", device = "id:pixel_8", showSystemUi = true, uiMode = 32, group = "template")
annotation class CatalogTemplate

/**
 * Window-size-class multipreview for the adaptive layouts the kit documents at three widths. The
 * catalog's `breakpoints` table in `catalog.spec.json` names the same three, so a component
 * annotated `@CatalogComponent(perBreakpoint = true)` fans out to one card per size.
 */
@Preview(name = "Compact", widthDp = 412, heightDp = 892, group = "breakpoints")
@Preview(name = "Medium", widthDp = 700, heightDp = 892, group = "breakpoints")
@Preview(name = "Expanded", widthDp = 960, heightDp = 892, group = "breakpoints")
annotation class CatalogBreakpoints

/**
 * True when the sticker is being composed on an **interactive** lane — a held Live Compose daemon
 * session on the preview server — and false when it is being baked into a published PNG.
 *
 * Derived from `LocalInspectionMode` rather than hard-coded, so one sticker body serves both lanes:
 * a baked capture must not depend on whether something tapped it, while a live session must visibly
 * change when clicked. **No sticker may ship a dead handler** — a component that carries state owns
 * it and mutates it here; everything else takes the [counted] click tally.
 */
@Composable fun catalogInteractive(): Boolean = !LocalInspectionMode.current

// --- Fonts, loaded once from the faces vendored under src/main/resources/fonts/. ---------------

private fun fontBytes(name: String): ByteArray =
  object {}.javaClass.getResourceAsStream("/fonts/$name")?.readBytes()
    ?: error("catalog font resource missing: fonts/$name")

/**
 * Roboto Flex — the catalog's default typeface and the face Material 3 ships as its default sans.
 * One variable TTF carries the whole weight axis, so a single face backs the entire type scale;
 * Skiko rasterises the default (400) instance and synthesises the heavier scale steps.
 */
val RobotoFlex: FontFamily =
  FontFamily(Font("RobotoFlex", fontBytes("RobotoFlex.ttf"), FontWeight.Normal, FontStyle.Normal))

/**
 * Roboto — the static M3 sans, offered as a selectable typeface beside the variable [RobotoFlex].
 */
val Roboto: FontFamily =
  FontFamily(
    Font("Roboto-Regular", fontBytes("Roboto-Regular.ttf"), FontWeight.Normal, FontStyle.Normal),
    Font("Roboto-Medium", fontBytes("Roboto-Medium.ttf"), FontWeight.Medium, FontStyle.Normal),
  )

/**
 * Generic-family substitutes keyed by the name a component looks up, so a sticker that asks for
 * `serif` / `monospace` rasterises the vendored face instead of whatever Skiko's system font table
 * happens to resolve on the render host — which is what keeps the baked stickers stable.
 */
val CatalogGenericFonts: Map<String, FontFamily> =
  mapOf(
    "serif" to FontFamily(Font("NotoSerif-Regular", fontBytes("NotoSerif-Regular.ttf"))),
    "monospace" to FontFamily(Font("DroidSansMono", fontBytes("DroidSansMono.ttf"))),
  )

/** Composition-local carrying [CatalogGenericFonts] to any sticker that needs a generic family. */
val LocalGenericFonts = androidx.compose.runtime.staticCompositionLocalOf { CatalogGenericFonts }
