package ee.schimke.m3catalog.glimmersamples

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapperProvider
import androidx.xr.glimmer.GlimmerTheme
import androidx.xr.glimmer.googlefonts.createGoogleSansFlexTypography
import ee.schimke.composeai.preview.PreviewWrapperClass

/**
 * The kit's typeface, for the vendored samples.
 *
 * `:glimmer-catalog` applies this in its own `Sticker` wrapper; these samples cannot, because every
 * `@Preview` here is upstream's own and its body opens with a bare `GlimmerTheme { }` as the
 * OUTERMOST composable. A stock `GlimmerTheme()` types in the platform default — Roboto on this
 * Robolectric lane — so the whole sheet was published in the wrong face while the catalog beside it
 * was correct. `titlechip-titlechipsample__ideal__default.png` was byte-identical across the
 * 2026-09-16 Google Sans Flex republish for exactly that reason: there was no Google Sans Flex in
 * this module to fix.
 *
 * Hoisted to a `val` for the reason `:glimmer-catalog` hoists its own: it allocates seven
 * `FontFamily`s, the function is not `@Composable` and its result does not vary, so calling it per
 * preview would build the same object fifty times per render.
 */
private val GoogleSansFlexTypography = createGoogleSansFlexTypography()

/**
 * Installs the kit's typography around a vendored sample.
 *
 * Wrapping from OUTSIDE works — and is the only thing that can — because `GlimmerTheme`'s
 * `typography` parameter defaults to `GlimmerTheme.typography`, which reads the ambient value off
 * the theme's `CompositionLocal` rather than resetting to a fresh default. Verified against
 * `glimmer-1.0.0-alpha19.aar`: the default branch of `GlimmerTheme` calls
 * `GlimmerTheme$Companion.getTypography`, and that reads `_localGlimmerTheme.current.typography`.
 * So the sample's own inner `GlimmerTheme { }` inherits what this provides instead of overriding
 * it, and the sample body stays byte-identical to upstream.
 */
class GoogleSansFlexPreviewWrapper : PreviewWrapperProvider {
  @Composable
  override fun Wrap(content: @Composable () -> Unit) {
    GlimmerTheme(typography = GoogleSansFlexTypography) { content() }
  }
}

/**
 * What the vendored samples' `@Preview` becomes: the same preview, in the kit's typeface.
 *
 * A multi-preview annotation rather than a per-file edit, so the patch that applies it is one
 * mechanical substitution and the sample bodies are untouched. `@PreviewWrapperClass` is this
 * project's companion to androidx's `@PreviewWrapper`, which is `@Target(FUNCTION)`-only and so
 * cannot ride on an annotation class.
 *
 * Preview ids are derived from the function, not the annotation, so nothing downstream moves:
 * `glimmer-design-map.json`, the parity references and the published slugs all stay as they are.
 */
@Preview
@PreviewWrapperClass("ee.schimke.m3catalog.glimmersamples.GoogleSansFlexPreviewWrapper")
annotation class GlimmerSamplePreview
