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

  // The catalog annotations: `@CatalogGroup`, `@CatalogComponent`,
  // `@CatalogVariant`, `@OverrideVariant`, `@ThemeCatalog`, `@ColorCatalog`,
  // `@TypographyCatalog`, `@ShapeCatalog`, `@FixedTheme`.
  implementation(libs.composeai.preview.annotations)

  testImplementation(kotlin("test"))
  testImplementation(libs.compose.ui.test)
  testImplementation(libs.compose.ui.test.junit4)
}
