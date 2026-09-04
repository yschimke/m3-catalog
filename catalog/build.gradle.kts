// `:catalog` — the Material 3 Design Kit rebuilt as design-led `@Preview`s.
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

  // The adaptive layout surfaces — `NavigationSuiteScaffold`, `ListDetailPaneScaffold` and
  // `SupportingPaneScaffold`. These are NOT part of the kit-mapped component inventory: the
  // Material 3 Design Kit publishes no node for any of them, and `AGENTS.md` makes a kit reference
  // a condition of membership. They are published as plain `@Preview`s under
  // `ee.schimke.m3catalog.adaptive` instead, so the catalog can show what Compose draws at each
  // documented breakpoint without claiming a comparison it has nothing to compare against.
  implementation(libs.compose.adaptive)
  implementation(libs.compose.adaptive.layout)
  implementation(libs.compose.adaptive.navigation)
  implementation(libs.compose.material3.adaptive.navigation.suite)
  implementation(libs.compose.foundation)
  implementation(libs.compose.runtime)
  implementation(libs.compose.ui)
  implementation(libs.compose.ui.tooling)
  // Republishes `androidx.compose.ui.tooling.preview.Preview` (the FQN preview
  // discovery scans for) and `PreviewWrapperProvider` (what `@ThemeCatalog`
  // providers implement) on the desktop JVM target.
  implementation(libs.compose.ui.tooling.preview)

  // Compose Multiplatform string resources. Every string a sticker renders resolves from
  // `src/main/composeResources/values*/strings.xml`, so a render carrying `localeTag` — or a
  // `@Preview(locale = …)` — comes back translated instead of English-with-a-different-layout.
  // The desktop renderer applies the tag twice on purpose: the composition's `LocaleList` (which
  // steers layout direction and locale-aware text) AND the JVM default `Locale`, which is what
  // `stringResource(...)` actually reads on Skiko. Both halves are already in the daemon, so this
  // module only has to declare its strings.
  @Suppress("DEPRECATION") implementation(compose.components.resources)

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

// The generated `Res` accessor's package. Without this it derives from the module's group/name,
// which would put `Res` somewhere no section file expects; pinning it keeps the imports in the
// stickers (`ee.schimke.m3catalog.generated.resources.*`) stable across a module rename.
compose.resources { packageOfResClass = "ee.schimke.m3catalog.generated.resources" }

// `CatalogMatrixAnnotations.kt` is GENERATED from the matrices declared in `CatalogAxes.kt` and
// paired with their annotation names in `CatalogMatrixDeclarations.kt` — see
// `MatrixAnnotationsGenerator.kt` for why the cells are expanded rather than hand-typed (#107).
//
// The output is committed like any other source (it is what the compiler reads, and a matrix cell
// has to be greppable), and `CatalogMatrixAnnotationsTest` fails if the committed copy is not what
// the declarations produce — the same regenerate-and-diff contract `design-map.json` has, run in
// the unit tests rather than as its own CI job.
//
// It reads the compiled main classes, which include the file it writes. That is not circular in
// practice: the generator depends only on the axis declarations, so a stale committed copy still
// compiles and still regenerates correctly.
val generateMatrixAnnotations by
  tasks.registering(JavaExec::class) {
    group = "build"
    description = "Regenerates CatalogMatrixAnnotations.kt from the declared variant matrices."
    classpath = sourceSets["main"].runtimeClasspath
    mainClass = "ee.schimke.m3catalog.MatrixAnnotationsGeneratorKt"
    args("src/main/kotlin/ee/schimke/m3catalog/CatalogMatrixAnnotations.kt")
    // The generated file is source, so it is written into the source tree rather than into build/.
    outputs.upToDateWhen { false }
  }
