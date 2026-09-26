// `:ui-samples-catalog` — the AndroidX **foundation** samples, rendered as the `compose-ui-samples`
// system.
//
// ── Why a second samples module rather than more `paths` in `:samples-catalog` ────────────────
//
// `:samples-catalog` is `androidx.compose.material3`, and it declares
// `compareWith: { system: "m3-catalog" }` so a sample lands beside the kit cell it is a call site
// for. A `LazyColumn` or `pointerInput` sample has no kit cell and never will, so folding these in
// would either drag them into a pairing that cannot score them or make the pairing conditional per
// component. Two further reasons, in order: one system is one taxonomy (`m3-samples` mirrors
// `m3-catalog`'s ids so every cross-link is an identity mapping; these group by source file), and
// each library carries its own pin and its own opt-in set. See issue #346.
//
// Everything else is shared: same renderer, same importer, same vendoring contract. This is a
// second module using `scripts/import-samples.mjs`, not a second pipeline.
//
// ── A Compose Multiplatform (desktop) module ──────────────────────────────────────────────────
//
// No AGP, for the reason `catalog/build.gradle.kts` opens with: the compose-preview plugin then
// routes it to the Desktop renderer and preview.coo.ee can live re-render the published bundle.
//
// Everything under `src/main/kotlin/upstream/` is VENDORED: upstream's bytes, in upstream's own
// packages (`androidx.compose.foundation.samples` and
// `androidx.compose.foundation.layout.samples`),
// fetched by `scripts/import-samples.mjs` from the commit pinned in
// `ui-samples-catalog/import.json`.
// It is never edited in place and never formatted — a fix is a patch in
// `ui-samples-catalog/patches/`
// with a stated reason.
//
// ── The wrappers are the point ────────────────────────────────────────────────────────────────
//
// `docs/design/ANDROIDX_SAMPLES.md` records that this repository generates no `@Preview` wrappers,
// because 298 of the 317 material3 samples carry one upstream. That does not hold here: foundation
// ships a preview annotation on **7 of its 73 sample files**, so without wrappers this module would
// publish almost nothing. `scripts/samples-previews.mjs` generates one per zero-argument
// `@Sampled` composable, into this module's own package and never into the vendored tree.
plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.compose.multiplatform)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.composePreview)
}

kotlin {
  jvmToolchain(21)

  // AndroidX compiles its own samples with these opt-ins: a sample demonstrating an experimental
  // API cannot avoid naming it. Opting in here rather than patching the files with `@file:OptIn`
  // keeps the vendored tree byte-identical, which is the whole bargain. The list is what the
  // compiler asked for against the pinned artifacts, not a precautionary sweep.
  compilerOptions {
    optIn.addAll(
      "androidx.compose.foundation.ExperimentalFoundationApi",
      "androidx.compose.foundation.layout.ExperimentalLayoutApi",
      "androidx.compose.foundation.layout.ExperimentalFlexBoxApi",
      "androidx.compose.foundation.layout.ExperimentalGridApi",
      "androidx.compose.foundation.text.InternalFoundationTextApi",
      "androidx.compose.ui.ExperimentalComposeUiApi",
      "androidx.compose.ui.text.ExperimentalTextApi",
      "androidx.compose.material3.ExperimentalMaterial3Api",
      "kotlin.ExperimentalStdlibApi",
    )
  }
}

dependencies {
  implementation(compose.desktop.currentOs)
  implementation(libs.compose.foundation)
  implementation(libs.compose.runtime)
  implementation(libs.compose.ui)
  implementation(libs.compose.ui.tooling)
  // `androidx.compose.ui.tooling.preview.Preview` — the FQN discovery scans for, and what the
  // generated wrappers carry.
  implementation(libs.compose.ui.tooling.preview)
  // A foundation sample is about foundation, and it still has to SHOW something: 74 imports across
  // the corpus reach for `Text`, `Button` and the rest of Material 3, which is upstream's own habit
  // rather than a choice made here.
  implementation(libs.compose.material3)
  // `Icons.Filled.*`, named by the flow-layout and text samples.
  implementation(libs.compose.material.icons.extended)
  // `androidx.annotation.Sampled`, which every vendored sample carries. Annotation-only.
  implementation(libs.androidx.annotation)
}

// Two exclusions, for two different reasons.
//
// `upstream/` is upstream's bytes: ktfmt would rewrite it into a permanent diff against every
// future
// import — the formatting counterpart of "a fix is a patch, never an edit". The same narrowing
// `:samples-catalog` applies.
//
// `SamplePreviews.kt` is `scripts/samples-previews.mjs`'s output, and its canonical form is
// whatever
// that script emits: `--check` regenerates and diffs it, exactly as `design-map.json` is checked.
// Letting a formatter rewrite it would put the two checks in direct conflict — ktfmt wraps the long
// fully-qualified calls at 100 columns and the generator does not, so whichever ran last would make
// the other fail. The generator is the single source of truth, so the formatter stays out. Named by
// FILE rather than by a `generated/` directory because it lives under the directories its own
// package names, beside hand-written sources it must not drag out of the formatter with it. The
// same resolution `yschimke/wear-m3-catalog` reached for the generator this one is ported from.
tasks.withType<com.ncorti.ktfmt.gradle.tasks.KtfmtBaseTask>().configureEach {
  exclude {
    val path = it.file.absolutePath.replace('\\', '/')
    path.contains("/src/main/kotlin/upstream/") ||
      path.endsWith("/src/main/kotlin/ee/schimke/uisamplescatalog/SamplePreviews.kt")
  }
}
