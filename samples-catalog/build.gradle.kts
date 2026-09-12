// `:samples-catalog` — the AndroidX material3 and material3-adaptive samples, rendered.
//
// A Compose **Multiplatform (desktop)** module for the same reason `:catalog` is one: no AGP, so
// the compose-preview plugin routes it to the Desktop renderer and the published bundle can be
// live re-rendered by preview.coo.ee. It also keeps both sides of the compare page on ONE
// rasteriser, so a sample beside its kit sticker shows an API-usage difference and not a renderer
// difference.
//
// Everything under `src/main/kotlin/upstream/` is VENDORED: upstream's bytes, in upstream's own
// packages — `androidx.compose.material3.samples` and `androidx.compose.material3.adaptive.samples`
// — fetched by `scripts/import-samples.mjs` from the commits pinned in `samples/import.json`, one
// per library, because the two lines are published on independent cadences. It is never edited in
// place and never formatted — a fix is a patch in `samples/patches/` with a stated reason, so the
// next import re-applies it and a patch that stops applying fails the import instead of silently
// vanishing.
//
// The samples already carry `@Preview` upstream — 298 of the 317 `@Sampled` material3 functions and
// 11 of the 13 adaptive ones — so discovery finds them directly and no wrapper is generated for
// them. What they cannot declare for themselves is a theme, so `catalog.spec.json` supplies one
// through its `themes` block, which exists precisely for an imported project.
plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.compose.multiplatform)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.composePreview)
}

kotlin {
  jvmToolchain(21)

  // The samples are AndroidX's own, and AndroidX compiles them WITH these opt-ins: a sample
  // demonstrating an experimental API cannot avoid naming it. Opting in here rather than patching
  // 22 files with `@file:OptIn` keeps the vendored tree byte-identical, which is the whole bargain.
  compilerOptions {
    optIn.addAll(
      "androidx.compose.material3.ExperimentalMaterial3Api",
      "androidx.compose.material3.ExperimentalMaterial3ExpressiveApi",
      "androidx.compose.material3.ExperimentalMaterial3ComponentOverrideApi",
      "androidx.compose.material.ExperimentalMaterialApi",
      "androidx.compose.foundation.ExperimentalFoundationApi",
      "androidx.compose.ui.ExperimentalComposeUiApi",
      "androidx.compose.animation.ExperimentalAnimationApi",
      "androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi",
      "kotlin.ExperimentalStdlibApi",
    )
  }
}

dependencies {
  implementation(compose.desktop.currentOs)
  implementation(libs.compose.material3)
  implementation(libs.compose.material.icons.extended)
  implementation(libs.compose.foundation)
  implementation(libs.compose.runtime)
  implementation(libs.compose.ui)
  implementation(libs.compose.ui.tooling)
  // `androidx.compose.ui.tooling.preview.Preview` — the FQN discovery scans for, and the one the
  // vendored samples already import.
  implementation(libs.compose.ui.tooling.preview)
  // `androidx.annotation.Sampled`, which every vendored sample carries. Annotation-only.
  implementation(libs.androidx.annotation)

  // `currentWindowAdaptiveInfo` / `WindowSizeClass`, which the app-bar samples call, and the pane
  // scaffolds the adaptive samples are about. The same adaptive artifacts `:catalog` carries, plus
  // the one it does not: `adaptive-navigation`, for the `ThreePaneScaffoldNavigator` every adaptive
  // sample drives and its `navigateTo` / `navigateBack` / `BackNavigationBehavior` surface.
  implementation(libs.compose.adaptive)
  implementation(libs.compose.adaptive.layout)
  implementation(libs.compose.adaptive.navigation)
  // `ViewModel` / `viewModelScope`, which the pull-to-refresh samples build their state on.
  implementation(libs.jb.lifecycle.viewmodel.compose)
  // `viewModelScope` dispatches on `Dispatchers.Main`, which the desktop JVM only has when a
  // provider is on the classpath. Without it `PullToRefreshViewModelSample` renders as
  // "Module with the Main dispatcher is missing" -- and since a failed preview fails the render
  // job, that one sample took the whole sheet down with it.
  implementation(libs.kotlinx.coroutines.swing)

  implementation(libs.composeai.preview.annotations)
}

// The vendored tree is upstream's bytes. ktfmt would rewrite it into a permanent diff against every
// future import, so it is excluded — the formatting counterpart of "a fix is a patch, never an
// edit".
// The root build applies `com.ncorti.ktfmt.gradle` to every project, so this narrows its inputs
// here
// rather than re-declaring the plugin.
tasks.withType<com.ncorti.ktfmt.gradle.tasks.KtfmtBaseTask>().configureEach {
  exclude { it.file.absolutePath.replace('\\', '/').contains("/src/main/kotlin/upstream/") }
}
