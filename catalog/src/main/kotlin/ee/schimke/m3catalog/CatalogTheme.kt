@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalRippleThemeConfiguration
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.tooling.preview.Preview

/**
 * The sticker frame every component preview is wrapped in.
 *
 * Stock [MaterialTheme] or [MaterialExpressiveTheme] over a declared catalog scheme, so the
 * `compose/theme` token set the renderer extracts is the **real** Material 3 system rather than a
 * bespoke palette.
 *
 * There is deliberately no surface behind a sticker: it reads as a silhouette on the viewer's
 * backing, with `onSurface` provided as the content colour so text and icons stay themed. The
 * render itself carries no decorative padding: its bounds must be the component's bounds so a
 * resolution-free Figma reference is rasterised at the same density as the Compose candidate.
 * Catalog presentation spacing belongs to the viewer, outside the parity image.
 */
@Composable
fun Sticker(content: @Composable () -> Unit) {
  StickerFrame(
    colorScheme = catalogColorScheme(),
    themeStyle = LocalCatalogThemeStyle.current,
    content = content,
  )
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

/** Whether a named catalog theme uses standard or expressive Material component behavior. */
enum class CatalogThemeStyle {
  Standard,
  Expressive,
}

/** Set beside [LocalCatalogScheme] by a named theme provider. */
val LocalCatalogThemeStyle =
  androidx.compose.runtime.staticCompositionLocalOf { CatalogThemeStyle.Standard }

/** True while a sticker is being rendered under a Material 3 Expressive named theme. */
@Composable
fun catalogExpressive(): Boolean = LocalCatalogThemeStyle.current == CatalogThemeStyle.Expressive

/**
 * The sticker frame with its [colorScheme] / [themeStyle] / [typography] / [shapes] supplied by the
 * caller — the shared body of [Sticker] and every `@ThemeCatalog` provider in `CatalogThemes.kt`,
 * so a named theme re-skins every sticker from this one place without touching a single preview.
 */
@Composable
fun StickerFrame(
  colorScheme: ColorScheme,
  themeStyle: CatalogThemeStyle = CatalogThemeStyle.Standard,
  typography: Typography = CatalogTypography,
  shapes: Shapes = CatalogShapes,
  content: @Composable () -> Unit,
) {
  CompositionLocalProvider(
    LocalGenericFonts provides CatalogGenericFonts,
    // The focus axis, provided once for the whole catalog: `LocalRippleThemeConfiguration` is what
    // every Material component consults to decide whether focus is a state layer or the keyboard
    // focus indicator's inset ring, so one line here is what "throughout" means. Its default value
    // is `RippleDefaults.ThemeConfiguration`, which is the opacity branch — so providing it does
    // not move a render until a cell seeds `focus=ring`. See [CatalogFocus].
    LocalRippleThemeConfiguration provides catalogFocusIndication(),
  ) {
    val themedContent: @Composable () -> Unit = {
      // Deliberately NOT a `Surface`: `Surface` clips its content to its shape, and the pixels a
      // sticker draws OUTSIDE the component's bounds — an elevation shadow, a focus ring — are
      // exactly the ones a `@CaptureGutter` reserves canvas for. Clipped at the component's edge,
      // the gutter came out empty and the shadow vanished (#179). A transparent `Surface`
      // contributes nothing else a sticker needs: no tint at `Color.Transparent`, no border, and
      // the content colour is one composition local. `propagateMinConstraints` keeps the frame's
      // minimum reaching the component, which is what `Surface`'s own box did.
      Box(propagateMinConstraints = true) {
        CompositionLocalProvider(
          LocalContentColor provides MaterialTheme.colorScheme.onSurface,
          content = content,
        )
      }
    }
    when (themeStyle) {
      CatalogThemeStyle.Standard ->
        MaterialTheme(
          colorScheme = colorScheme,
          motionScheme = MotionScheme.standard(),
          typography = typography,
          shapes = shapes,
          content = themedContent,
        )
      CatalogThemeStyle.Expressive ->
        MaterialExpressiveTheme(
          colorScheme = colorScheme,
          motionScheme = MotionScheme.expressive(),
          typography = typography,
          shapes = shapes,
          content = themedContent,
        )
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

//
// THE WIDTH-CARRYING MODE FRAMES, NAMED FOR WHAT THEY FRAME
//
// Each of these is `@CatalogModes` at one non-default viewport width, because the kit draws that
// surface at a measured width the 400dp wrap-content harness does not reproduce. They are named
// for the **surface**, never for the number: the number is a measurement of the kit, and this repo
// moves when the kit is re-measured, so a re-measure has to be a one-line edit here rather than a
// rename at every call site (#106). Two of them differing by a single pixel — the standard and
// expressive linear progress frames — is why the numbers make poor names.
//
// Each KDoc keeps the record of where its width came from; that provenance is the part worth
// having, and it belongs next to the value rather than in the identifier.
//

/** Light/dark modes at the kit's 360dp container width, for components wider than the harness. */
@Preview(name = "Light", group = "modes", widthDp = 360)
@Preview(name = "Dark", uiMode = 32, group = "modes", widthDp = 360)
annotation class CatalogModesKitContainer

/** US-calendar light/dark modes for the Material kit's Sunday-first date-picker examples. */
@Preview(name = "Light", group = "modes", widthDp = 360, locale = "en-US")
@Preview(name = "Dark", uiMode = 32, group = "modes", widthDp = 360, locale = "en-US")
annotation class CatalogModesDatePickerUs

/**
 * Light/dark modes at Material's 560dp `DialogMaxWidth`, for the landscape time-picker dialog.
 *
 * The default harness caps a wrap-content sticker at 400dp, which is narrower than the landscape
 * `TimePicker` can lay out in — the clock face overflowed the clock display and clipped at the
 * dialog edge (#146). `widthDp` is per-`@Preview`, so it belongs to the whole component rather than
 * to the one `@OverrideVariant` that needs it; the portrait cells simply keep their own footprint
 * inside the wider viewport.
 */
@Preview(name = "Light", group = "modes", widthDp = 560)
@Preview(name = "Dark", uiMode = 32, group = "modes", widthDp = 560)
annotation class CatalogModesDialog

/** Light/dark modes for the kit's slider track frame, which it draws 354dp wide. */
@Preview(name = "Light", group = "modes", widthDp = 354)
@Preview(name = "Dark", uiMode = 32, group = "modes", widthDp = 354)
annotation class CatalogModesSliderTrack

/**
 * Light/dark modes for the kit's snackbar bar, which it draws 344dp wide.
 *
 * The bar and nothing else: a sticker that needs room for its shadow declares a `@CaptureGutter`,
 * which grows the canvas without moving what the component measures. This frame used to be 366dp —
 * the bar plus its 11dp gutter, arithmetic baked into a frame name — and the sticker paid for the
 * gutter twice, once in the frame and once in a padded `Box` (#179).
 */
@Preview(name = "Light", group = "modes", widthDp = 344)
@Preview(name = "Dark", uiMode = 32, group = "modes", widthDp = 344)
annotation class CatalogModesSnackbar

/** Light/dark modes for the expressive linear-progress frame, which the kit draws 404dp wide. */
@Preview(name = "Light", group = "modes", widthDp = 404)
@Preview(name = "Dark", uiMode = 32, group = "modes", widthDp = 404)
annotation class CatalogModesLinearProgressExpressive

/** Light/dark modes for the standard linear-progress frame, which the kit draws 405dp wide. */
@Preview(name = "Light", group = "modes", widthDp = 405)
@Preview(name = "Dark", uiMode = 32, group = "modes", widthDp = 405)
annotation class CatalogModesLinearProgress

/** Light/dark modes at the kit's standard 412dp compact-screen width. */
@Preview(name = "Light", group = "modes", widthDp = 412)
@Preview(name = "Dark", uiMode = 32, group = "modes", widthDp = 412)
annotation class CatalogModesCompact

/** Light/dark modes at the 905dp tablet width used by the kit's carousel examples. */
@Preview(name = "Light", group = "modes", widthDp = 905)
@Preview(name = "Dark", uiMode = 32, group = "modes", widthDp = 905)
annotation class CatalogModesCarouselTablet

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
