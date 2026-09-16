// `:foundation-catalog` — the UI builder's own vocabulary, rendered and published as
// `compose-foundation`.
//
// ── What this catalog is for ───────────────────────────────────────────────────────────────────
//
// A design has to be put INSIDE something. The containers, the shapes and the image asset a drawing
// tool offers — `layout/*`, `shape/*`, `asset/image` — are not Material 3's and not Wear Material
// 3's: `androidx.compose.foundation` and `androidx.compose.ui` publish one of each, not one per
// design system. This repository's own `ui-builder.policy.json` says so from the other side, where
// it declares ZERO builtins on the grounds that claiming them "would be this catalog claiming to
// own
// the builder's own vocabulary".
//
// Somebody still has to own them, and today that somebody is a `mapOf` synthesised at startup
// inside
// the preview server — which means a catalog repository cannot change its own palette without a
// server release. This module is the replacement: the vocabulary as a catalog, published like every
// other, so `m3-catalog` and `wear-m3-catalog` borrow their containers from a delivery branch
// instead of from Kotlin nobody outside that binary can see. See
// docs/design/FOUNDATION_CATALOG.md and yschimke/compose-preview-server#819.
//
// ── A Compose Multiplatform (desktop) module ──────────────────────────────────────────────────
//
// No AGP, for the reason `catalog/build.gradle.kts` opens with: the compose-preview plugin routes
// it
// to the Desktop renderer, and preview.coo.ee can live re-render the published bundle rather than
// only replaying baked PNGs.
//
// ── Nothing but foundation on the classpath, deliberately ─────────────────────────────────────
//
// Every library callable a sticker renders enters the component record and is published on this
// catalog's shelf. One `Text` inside one sticker would put `compose-foundation/text` in a palette
// whose whole claim is that it owns containers and NOT components — so this module depends on
// `foundation` and `ui` and nothing else, and draws its children as plain coloured blocks.
//
// That is also why the two scaffolds and the carousel in this vocabulary carry no picture: they are
// Material 3 composables, and rendering them here would drag `Surface`, `Text` and `TopAppBar` onto
// the shelf with them. They are declared in `ui-builder.policy.json` instead, and `:catalog` is
// where a Material scaffold is pictured. See docs/design/FOUNDATION_CATALOG.md.
plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.compose.multiplatform)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.composePreview)
}

kotlin { jvmToolchain(21) }

dependencies {
  implementation(compose.desktop.currentOs)
  implementation(libs.compose.foundation)
  implementation(libs.compose.runtime)
  implementation(libs.compose.ui)
  implementation(libs.compose.ui.tooling)
  implementation(libs.compose.ui.tooling.preview)

  // `@CatalogGroup` / `@CatalogComponent`, and `@BuilderComponent` — the per-component builder
  // policy that makes this catalog a builder catalog rather than a sticker sheet.
  implementation(libs.composeai.preview.annotations)
}
