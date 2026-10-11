// `:uid-stickers` — a PROTOTYPE: catalog stickers whose body is a UI builder `.uid` document.
//
// Opt-in (`-PuidStickers=true`), outside the published inventory, like `:adaptive-uid-pilot`.
// `stickers/*.uid` is the sticker body; its sibling `stickers/<name>.json` lists its variant cells, each a
// list of builder operations applied to the document. `generateUidStickers` exports every document
// and variant through the UI builder's own exporter (`ScreenExportGate`, the same code behind the
// builder's code pane) into `build/generated/uid`, and `src/main` wraps each generated composable
// in the catalog's `Sticker` frame with the usual annotations. Nothing in the generated Kotlin is
// catalog scaffolding, so the published source needs no cleaning: it IS what the builder exports.
plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.compose.multiplatform)
  alias(libs.plugins.compose.compiler)
  id("ee.schimke.composeai.preview")
}

kotlin { jvmToolchain(21) }

// `-PuidExportVersion=<x>-SNAPSHOT` tries an unreleased exporter published to Maven local.
// `exportComponents` is newer than 3.107.0, so the prototype currently needs both:
// `-PuidExportVersion=3.107.1-SNAPSHOT -PscreenModelVersion=2.50.1-SNAPSHOT`.
if (providers.gradleProperty("uidExportVersion").isPresent) {
  repositories {
    mavenLocal()
    mavenCentral()
    google()
  }
}

val generator by sourceSets.creating

dependencies {
  implementation(platform(libs.composeai.contracts.bom))
  implementation(platform(libs.composeai.daemon.bom))
  implementation(project(":catalog"))
  implementation(compose.desktop.currentOs)
  implementation(libs.compose.material3)
  implementation(libs.compose.material.icons.extended)
  implementation(libs.compose.ui.tooling.preview)
  implementation(libs.composeai.preview.annotations)
  // `previewOverrideInt` & co.: the shells read the hand-written stickers' numeric knobs themselves.
  implementation(libs.composeai.preview.overrides)
  // `stringResource(Res.string.…)`, which the generated bodies call for the catalog's copy.
  @Suppress("DEPRECATION") implementation(compose.components.resources)

  // The exporter the builder's code pane and the server's export.compose route run.
  "generatorImplementation"(
    "ee.schimke.composeai:compose-preview-ui-builder-export-jvm:" +
      providers.gradleProperty("uidExportVersion").getOrElse("3.107.0")
  )
  // `-PscreenModelVersion=<x>-SNAPSHOT` pairs an unreleased exporter with the unreleased
  // generator it was built against.
  providers.gradleProperty("screenModelVersion").orNull?.let { pinned ->
    "generatorImplementation"("ee.schimke.composeai:screen-model-jvm") {
      version { strictly(pinned) }
    }
    "generatorImplementation"("ee.schimke.composeai:screen-model") {
      version { strictly(pinned) }
    }
  }
  // The Compose compiler plugin applies to every source set; it only needs the runtime present.
  "generatorImplementation"(libs.compose.runtime)
}

val generatedDir = layout.buildDirectory.dir("generated/uid/kotlin")

val generateUidStickers by
  tasks.registering(JavaExec::class) {
    group = "build"
    description = "Exports every sticker document and variant cell to Kotlin with the UI builder."
    classpath = generator.runtimeClasspath
    mainClass = "ee.schimke.m3catalog.uid.GenerateUidStickersKt"
    // The vocabulary is `:catalog`'s own published pair (its discovered record and builder policy),
    // so a document can name only what the builder serves for `m3-catalog`.
    val catalogDiscovery = project(":catalog").layout.buildDirectory.dir("compose-previews")
    dependsOn(":catalog:composePreviewDiscover")
    inputs.dir("stickers")
    inputs.file("stickers.json")
    inputs.file("builder-foundation-record.json")
    inputs.files(fileTree(".") { include("project-components*.json") })
    inputs.file(catalogDiscovery.map { it.file("components.json") })
    inputs.file(catalogDiscovery.map { it.file("ui-builder.json") })
    outputs.dir(generatedDir)
    args(
      layout.projectDirectory.asFile.absolutePath,
      generatedDir.get().asFile.absolutePath,
      catalogDiscovery.get().asFile.absolutePath,
    )
  }

sourceSets.main { kotlin.srcDir(generateUidStickers) }

// `:catalog`'s jar holds the sticker meta-annotations (`@CatalogModesKitContainer`,
// `@InteractionStates`, the generated `…ExhaustiveKitCells`). Discovery scans a dependency jar only
// when its coordinate looks preview-related or it holds a configured component library, so name
// the catalog's package: the shells then carry exactly the hand-written stickers' annotations.
composePreview { componentLibraryPrefixes.add("ee.schimke.m3catalog.") }
