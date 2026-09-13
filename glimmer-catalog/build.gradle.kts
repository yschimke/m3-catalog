// `:glimmer-catalog` — `androidx.xr.glimmer` rebuilt as `@Preview` stickers.
//
// ── Why this one IS an Android module ─────────────────────────────────────────────────────────
//
// `catalog/build.gradle.kts` opens by explaining that `:catalog` is deliberately NOT an Android
// module: no AGP means the compose-preview plugin routes it to the Desktop renderer, which is what
// lets preview.coo.ee hold a live Compose session against the published bundle. That rule has not
// been relaxed — it simply cannot apply here. `androidx.xr.glimmer:glimmer` is published as an AAR
// (`minCompileSdk=37`) with no Compose Multiplatform port, so a desktop module cannot put it on the
// classpath at all. The choice is a Robolectric lane or no Glimmer sheet.
//
// The cost is stated rather than hidden: this sheet is rasterised by a different renderer from
// `m3-catalog`, so the two are never compared pixel-to-pixel. Nothing in this repository pairs
// them, and `catalog.spec.json` here declares no `compareWith`.
//
// ── A library module ──────────────────────────────────────────────────────────────────────────
//
// Nothing here is installed, so there is no application to declare: the Robolectric lane renders a
// library module directly, which is the shape compose-ai-tools' own `:samples:xr-glimmer` uses.
plugins {
  // No Kotlin plugin of its own: AGP 9 brings Kotlin support with it, and applying
  // `org.jetbrains.kotlin.android` on top fails outright — KGP's Android target reaches for
  // `com.android.build.gradle.api.BaseVariant`, the old variant API AGP 9 removed.
  alias(libs.plugins.android.library)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.composePreview)
}

composePreview {
  // Robolectric ships up to API 36, and 36 needs JDK 21+; 35 runs on the JDK 17 toolchain this
  // module compiles with. The same pin both Android modules in the sibling repository carry.
  sdkVersion.set(35)
}

android {
  namespace = "ee.schimke.m3catalog.glimmer"
  // glimmer 1.0.0-alpha19's AAR metadata says `minCompileSdk=37`; anything lower fails to resolve.
  compileSdk = 37

  // The AAR's own `uses-sdk` floor.
  defaultConfig { minSdk = 24 }

  buildFeatures { compose = true }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }

  testOptions { unitTests { isIncludeAndroidResources = true } }
}

dependencies {
  implementation(libs.glimmer)
  // `createGoogleSansFlexTypography()` — the kit's own typeface, resolved through Android's
  // downloadable-font provider and cached in `~/.cache/composeai/fonts`. See the note beside this
  // artifact in `gradle/libs.versions.toml` for why the render can depend on a downloaded face.
  implementation(libs.glimmer.google.fonts)
  // `androidx.compose.ui.tooling.preview.Preview` — the FQN discovery scans for. The ANDROIDX
  // artifact, not the Compose Multiplatform republication the desktop modules use.
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.compose.ui.tooling)
  implementation(libs.composeai.preview.annotations)
  // `previewOverrideBoolean`, which backs the `clickCount` knob `counted` exposes — the same
  // live-lane contract `:catalog` carries. See GlimmerInteractive.kt.
  implementation(libs.composeai.preview.overrides)
  // The passthrough backdrops behind @GlimmerEnvironmentPreview.
  implementation(libs.composeai.glimmer.environment)
  // The glyphs the kit draws. Every icon slot in the Glimmer kit is a Material Symbol named by
  // ligature in the node itself — `send` on the buttons and the list item, `mic_off` on the icon
  // buttons — so the icons are the kit's choice rather than this catalog's, and they come from the
  // published set rather than being redrawn here.
  implementation(libs.androidx.compose.material.icons.extended)
  testImplementation(libs.robolectric)
}
