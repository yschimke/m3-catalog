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
