package ee.schimke.m3catalog

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.tooling.preview.Preview

/**
 * The sticker frame every component preview is wrapped in.
 *
 * Stock [MaterialTheme] over the kit's baseline scheme, so the `compose/theme` token set the
 * renderer extracts is the **real** Material 3 system rather than a bespoke palette.
 *
 * The surface is deliberately [Color.Transparent]: a component sticker reads as a silhouette on the
 * viewer's backing, and `contentColor = onSurface` keeps text and icons themed against it. The
 * render itself carries no decorative padding: its bounds must be the component's bounds so a
 * resolution-free Figma reference is rasterised at the same density as the Compose candidate.
 * Catalog presentation spacing belongs to the viewer, outside the parity image.
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
  typography: Typography = catalogTypographyFor(Locale.current),
  shapes: Shapes = CatalogShapes,
  content: @Composable () -> Unit,
) {
  CompositionLocalProvider(LocalGenericFonts provides CatalogGenericFonts) {
    MaterialTheme(colorScheme = colorScheme, typography = typography, shapes = shapes) {
      Surface(color = Color.Transparent, contentColor = MaterialTheme.colorScheme.onSurface) {
        content()
      }
    }
  }
}

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
 * silhouette on the viewer's checkerboard.
 */
@Preview(name = "Light", group = "modes")
@Preview(name = "Dark", uiMode = 32, group = "modes")
annotation class CatalogModes

/** Light/dark modes with a 360dp viewport for kit components wider than the default harness. */
@Preview(name = "Light", group = "modes", widthDp = 360)
@Preview(name = "Dark", uiMode = 32, group = "modes", widthDp = 360)
annotation class CatalogModes360

/** US-calendar light/dark modes for the Material kit's Sunday-first date-picker examples. */
@Preview(name = "Light", group = "modes", widthDp = 360, locale = "en-US")
@Preview(name = "Dark", uiMode = 32, group = "modes", widthDp = 360, locale = "en-US")
annotation class CatalogModes360Us

/** Light/dark modes for the kit's 354dp slider track frame. */
@Preview(name = "Light", group = "modes", widthDp = 354)
@Preview(name = "Dark", uiMode = 32, group = "modes", widthDp = 354)
annotation class CatalogModes354

/** Light/dark modes for a 344dp snackbar plus its 11dp shadow gutter. */
@Preview(name = "Light", group = "modes", widthDp = 366)
@Preview(name = "Dark", uiMode = 32, group = "modes", widthDp = 366)
annotation class CatalogModes366

/** Light/dark modes for a 360dp container with the kit's 4dp shadow gutter. */
@Preview(name = "Light", group = "modes", widthDp = 368)
@Preview(name = "Dark", uiMode = 32, group = "modes", widthDp = 368)
annotation class CatalogModes368

/** Light/dark modes for the 404dp expressive linear-progress frame. */
@Preview(name = "Light", group = "modes", widthDp = 404)
@Preview(name = "Dark", uiMode = 32, group = "modes", widthDp = 404)
annotation class CatalogModes404

/** Light/dark modes for the 405dp standard linear-progress frame. */
@Preview(name = "Light", group = "modes", widthDp = 405)
@Preview(name = "Dark", uiMode = 32, group = "modes", widthDp = 405)
annotation class CatalogModes405

/** Light/dark modes with the kit's standard 412dp compact-screen width. */
@Preview(name = "Light", group = "modes", widthDp = 412)
@Preview(name = "Dark", uiMode = 32, group = "modes", widthDp = 412)
annotation class CatalogModes412

/** Light/dark modes for the modal-sheet kit frame, including its 40dp side gutters. */
@Preview(name = "Light", group = "modes", widthDp = 492)
@Preview(name = "Dark", uiMode = 32, group = "modes", widthDp = 492)
annotation class CatalogModes492

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

// Roboto and Roboto Flex deliberately cover the Latin/Greek/Cyrillic scripts they were designed
// for; neither is a pan-Unicode font. These OFL-licensed Noto faces travel inside the executable
// bundle so translated live renders do not depend on the preview host's fontconfig installation.
// CJK uses region-specific regular OTFs: unified code points can have different preferred glyph
// forms in Japanese, Korean, Simplified Chinese and Traditional Chinese.
private val NotoSansArabic: FontFamily by lazy { bundledFamily("NotoSansArabic.ttf") }
private val NotoSansDevanagari: FontFamily by lazy { bundledFamily("NotoSansDevanagari.ttf") }
private val NotoSansThai: FontFamily by lazy { bundledFamily("NotoSansThai.ttf") }
private val NotoSansJapanese: FontFamily by lazy { bundledFamily("NotoSansJP-Regular.otf") }
private val NotoSansKorean: FontFamily by lazy { bundledFamily("NotoSansKR-Regular.otf") }
private val NotoSansSimplifiedChinese: FontFamily by lazy {
  bundledFamily("NotoSansSC-Regular.otf")
}
private val NotoSansTraditionalChinese: FontFamily by lazy {
  bundledFamily("NotoSansTC-Regular.otf")
}

private fun bundledFamily(name: String): FontFamily =
  FontFamily(
    Font(name.substringBeforeLast('.'), fontBytes(name), FontWeight.Normal, FontStyle.Normal)
  )

/**
 * The baseline M3 type scale with a deterministic Noto face for locales Roboto cannot render.
 *
 * `zh-Hant` chooses the Traditional Chinese face even without a region. Hong Kong and Macau also
 * use it as the nearest bundled traditional-glyph form; the catalog's published Chinese locales
 * remain the exact `zh-CN` and `zh-TW` pair.
 */
internal fun catalogTypographyFor(locale: Locale): Typography =
  when (catalogFontResourceFor(locale)) {
    "NotoSansArabic.ttf" -> typographyOn(NotoSansArabic)
    "NotoSansDevanagari.ttf" -> typographyOn(NotoSansDevanagari)
    "NotoSansThai.ttf" -> typographyOn(NotoSansThai)
    "NotoSansJP-Regular.otf" -> typographyOn(NotoSansJapanese)
    "NotoSansKR-Regular.otf" -> typographyOn(NotoSansKorean)
    "NotoSansSC-Regular.otf" -> typographyOn(NotoSansSimplifiedChinese)
    "NotoSansTC-Regular.otf" -> typographyOn(NotoSansTraditionalChinese)
    else -> CatalogTypography
  }

/** The bundled face selected for [locale], exposed separately so coverage tests need no render. */
internal fun catalogFontResourceFor(locale: Locale): String? =
  when (locale.language) {
    "ar" -> "NotoSansArabic.ttf"
    "hi" -> "NotoSansDevanagari.ttf"
    "th" -> "NotoSansThai.ttf"
    "ja" -> "NotoSansJP-Regular.otf"
    "ko" -> "NotoSansKR-Regular.otf"
    "zh" ->
      if (
        locale.script.equals("Hant", ignoreCase = true) || locale.region in setOf("TW", "HK", "MO")
      ) {
        "NotoSansTC-Regular.otf"
      } else {
        "NotoSansSC-Regular.otf"
      }
    else -> null
  }

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
