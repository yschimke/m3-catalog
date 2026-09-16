# `:glimmer-samples` — the kit's typeface

Evidence that the vendored AndroidX Glimmer samples published in the **platform default**, not in
Google Sans Flex, and that a preview wrapper fixes it without touching a sample body.

## What was wrong

Every `@Preview` in `:glimmer-samples` is upstream's own, and each body opens with a bare
`GlimmerTheme { }` as the **outermost** composable. A stock `GlimmerTheme()` types in the platform
default — Roboto on this Robolectric lane — so all 50 previews drew in the wrong face, while
`:glimmer-catalog` beside it applied `createGoogleSansFlexTypography()` in its own `Sticker` wrapper
and drew correctly.

It was invisible from inside the module. The samples are faithful to upstream, nothing fails, and
`composeai.fonts.failOnFallback` has nothing to say: no font *fell back*, because none was ever
requested. The sharpest evidence is what the renderer fix did — compose-preview-daemon#114 moved
**52 image files** in `:glimmer-catalog` on 2026-09-16 and **not one byte** here. There was no
Google Sans Flex in this module to fix.

## Why wrapping from outside works

`GlimmerTheme`'s `typography` parameter defaults to the ambient `GlimmerTheme.typography` rather
than resetting to a fresh default. Verified against `glimmer-1.0.0-alpha19.aar`: the default branch
of `GlimmerTheme` calls `GlimmerTheme$Companion.getTypography`, which reads
`_localGlimmerTheme.current.typography`.

So a `PreviewWrapperProvider` supplying the typography from outside is **inherited** by the sample's
own inner `GlimmerTheme { }` instead of being overridden by it — which is what lets the sample body
stay byte-identical to upstream while the face changes.

## Before / after

`TitleChipPreview`. Before is the published byte from `design-artifacts/glimmer-samples`
(`images/titlechip-titlechipsample/ideal__default.png`, md5 `988fac14`, unchanged since 2026-09-12);
after is `:glimmer-samples:composePreviewRender` with the wrapper (md5 `7181433b`).

| Before (platform default) | After (Google Sans Flex) |
| --- | --- |
| ![Before](titlechip-before.png) | ![After](titlechip-after.png) |

The label is a Glimmer `title` role, which the kit sets on the weight axis rather than on a named
style — so the difference is the weight the axis carries, the same property
`docs/evidence/glimmer-google-sans-flex/` records for the catalog.
