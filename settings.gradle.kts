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

// The Kotlin-authored catalog runs beside the committed JSON policy until its compatibility
// gates pass. No ordinary discovery, renderer or delivery task consumes its outputs.
if (providers.gradleProperty("typedM3Catalog").orNull == "true") {
  val tools =
    file(providers.gradleProperty("composeAiToolsDir").orElse("../compose-ai-tools").get())
  require(tools.resolve("settings.gradle.kts").isFile) {
    "typedM3Catalog requires a compose-ai-tools checkout (-PcomposeAiToolsDir)."
  }
  require(providers.gradleProperty("composeUiBuilderDir").isPresent) {
    "typedM3Catalog requires -PcomposeUiBuilderDir and the typed renderer bridge."
  }
  require(providers.gradleProperty("typedAdapterCatalog").orNull == "true") {
    "typedM3Catalog requires -PtypedAdapterCatalog=true."
  }
  includeBuild(tools) {
    dependencySubstitution {
      substitute(module("ee.schimke.composeai:screen-model-source")).using(project(":screen-model"))
      substitute(module("ee.schimke.composeai:screen-model")).using(project(":screen-model"))
    }
  }
  include(":catalog-typed-adapters", ":catalog-typed-consumer", ":catalog-typed-host")
}

if (
  providers.gradleProperty("typedM3Catalog").orNull == "true" ||
    providers.gradleProperty("typedM3Policy").orNull == "true"
) {
  include(":catalog-typed-policy")
}

providers.gradleProperty("composeUiBuilderDir").orNull?.let { path ->
  val directory = file(path).canonicalFile
  require(directory.resolve("settings.gradle.kts").isFile) {
    "-PcomposeUiBuilderDir names $directory, which is not a compose-ui-builder Gradle checkout."
  }
  logger.lifecycle("Composite build: uiBuilder -> $directory")
  includeBuild(directory) {
    dependencySubstitution {
      substitute(module("ee.schimke.composeai:ui-builder-renderer-sdk-source"))
        .using(project(":ui-builder-renderer-sdk"))
      substitute(module("ee.schimke.composeai:ui-builder-runtime-source"))
        .using(project(":ui-builder-runtime"))
    }
  }
}

include(":catalog")

include(":catalog-ui-builder-renderer")

// The AndroidX samples rendition — `androidx.compose.material3`'s own `@Sampled` composables,
// vendored from a pinned upstream commit and rendered beside the kit catalog. Separate module, not
// a source set: the vendored sources are upstream's bytes under upstream's package, and must not be
// formatted, linted or refactored with this repo's own code. See docs/design/ANDROIDX_SAMPLES.md.
include(":samples-catalog")

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

// An app-style adaptive screen with repository-owned UID references, outside the Figma inventory.
if (providers.gradleProperty("adaptiveUidPilot").orNull == "true") {
  include(":adaptive-uid-pilot")
}
