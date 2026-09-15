pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenCentral()
    google()
  }
}

dependencyResolutionManagement {
  repositories {
    mavenCentral()
    google()
  }
}

rootProject.name = "m3-catalog"

include(":catalog")

// The AndroidX samples rendition — `androidx.compose.material3`'s own `@Sampled` composables,
// vendored from a pinned upstream commit and rendered beside the kit catalog. Separate module, not
// a source set: the vendored sources are upstream's bytes under upstream's package, and must not be
// formatted, linted or refactored with this repo's own code. See docs/design/ANDROIDX_SAMPLES.md.
include(":samples-catalog")

// `androidx.xr.glimmer` as a sticker sheet. The FIRST Android modules in this build, and the
// exception that proves the no-AGP rule rather than relaxing it: glimmer ships only as an AAR, so a
// Compose Multiplatform desktop module cannot resolve it and the previews can only be rendered by
// Robolectric. See glimmer-catalog/build.gradle.kts and docs/design/GLIMMER.md.
include(":glimmer-catalog")

// The AndroidX Glimmer samples, vendored and rendered beside the catalog above — the Android-side
// counterpart of `:samples-catalog`. See glimmer-samples/build.gradle.kts.
include(":glimmer-samples")

// The AndroidX **foundation** samples — `androidx.compose.foundation` and `-layout`, vendored from
// a pinned upstream commit and published as the `compose-ui-samples` system. A second samples
// module rather than two more `paths` in `:samples-catalog`, for the same "separate module, not a
// source set" reason the include above carries, plus one this tier adds: `m3-samples` declares
// `compareWith: { system: "m3-catalog" }`, and a `LazyColumn` sample has no kit cell to be paired
// with. See ui-samples-catalog/build.gradle.kts and issue #346.
include(":ui-samples-catalog")

// The UI builder's own vocabulary — the containers, screen frames and image asset a design is
// assembled out of — rendered and published as the `compose-foundation` system, so `m3-catalog` and
// `wear-m3-catalog` borrow them from a delivery branch instead of from Kotlin synthesised inside
// the preview server. See foundation-catalog/build.gradle.kts and
// docs/design/FOUNDATION_CATALOG.md.
include(":foundation-catalog")
