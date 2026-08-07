// `:catalog` — the Material 3 Design Kit rebuilt as code-led `@Preview`s.
//
// A Compose **Multiplatform (desktop)** module, deliberately not Android: it
// applies `org.jetbrains.compose` with no AGP plugin, so the compose-preview
// plugin routes it to the Desktop renderer (`ImageComposeScene`, no Robolectric
// and no Android SDK). That is what lets the public preview server
// (preview.coo.ee) build and **live re-render** the published bundle through the
// daemon (`serve --allow-render-trusted`) rather than only replaying baked PNGs.
//
// The catalog's inventory lives in **annotations next to the previews**
// (`@CatalogGroup` / `@CatalogComponent` / `@CatalogVariant` / `@OverrideVariant`,
// plus `@ThemeCatalog` / `@ColorCatalog` / `@TypographyCatalog` / `@ShapeCatalog`
// for the theme sheets). `catalog.spec.json` carries only the cover-sheet fields.
plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.compose.multiplatform)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.composePreview)
}

kotlin { jvmToolchain(21) }

dependencies {
  implementation(compose.desktop.currentOs)
  implementation(libs.compose.material3)
  implementation(libs.compose.material.icons.extended)
  implementation(libs.compose.foundation)
  implementation(libs.compose.runtime)
  implementation(libs.compose.ui)
  implementation(libs.compose.ui.tooling)
  // Republishes `androidx.compose.ui.tooling.preview.Preview` (the FQN preview
  // discovery scans for) and `PreviewWrapperProvider` (what `@ThemeCatalog`
  // providers implement) on the desktop JVM target.
  implementation(libs.compose.ui.tooling.preview)

  // The kit's six theme modes are Light/Dark x standard/medium/high contrast. Compose ships no
  // baseline contrast schemes, and the contrast role-to-tone mapping is a continuous function of
  // the contrast level rather than a table anyone can re-type correctly. MaterialKolor is a
  // multiplatform port of Google's material-color-utilities — the same generator the Material Theme
  // Builder plugin that produced the kit's variables runs — so the six schemes are COMPUTED from
  // the baseline seed instead of transcribed. `CatalogThemesTest` pins that by asserting the
  // zero-contrast pair reproduces Compose's own `lightColorScheme()` / `darkColorScheme()`: if the
  // generator ever disagrees with the baseline, the build says so rather than publishing a sheet
  // that quietly isn't Material 3.
  implementation(libs.materialkolor)

  // The catalog annotations: `@CatalogGroup`, `@CatalogComponent`,
  // `@CatalogVariant`, `@OverrideVariant`, `@ThemeCatalog`, `@ColorCatalog`,
  // `@TypographyCatalog`, `@ShapeCatalog`, `@FixedTheme`.
  implementation(libs.composeai.preview.annotations)

  // `previewOverrideString` — the knob surface `@OverrideVariant` seeds. This is what lets ONE
  // `@Preview` carry a whole variant matrix: the sticker reads its size and shape from named knobs,
  // and each stacked `@OverrideVariant` bakes an extra capture with those knobs seeded, instead of
  // the catalog growing one near-identical `@Composable` per cell.
  //
  // A released Maven coordinate rather than a project dependency, so `bundle pack` records a small
  // re-resolvable reference in the published bundle instead of inlining the runtime jar into every
  // per-preview bundle.
  implementation(libs.composeai.preview.overrides)

  testImplementation(kotlin("test"))
  testImplementation(libs.compose.ui.test)
  testImplementation(libs.compose.ui.test.junit4)
}
