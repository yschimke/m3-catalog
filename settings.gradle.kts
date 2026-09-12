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
