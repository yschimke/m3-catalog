// `:uid-stickers` — a PROTOTYPE: catalog stickers whose body is a UI builder `.uid` document.
//
// Opt-in (`-PuidStickers=true`), outside the published inventory, like `:adaptive-uid-pilot`.
// `stickers/*.uid` is the sticker body; `stickers.json` lists them and their variant cells, each a
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

  // The exporter the builder's code pane and the server's export.compose route run.
  "generatorImplementation"("ee.schimke.composeai:compose-preview-ui-builder-export-jvm:3.106.0")
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
    inputs.dir("stickers")
    inputs.file("stickers.json")
    inputs.file("component-record.json")
    outputs.dir(generatedDir)
    args(
      layout.projectDirectory.asFile.absolutePath,
      generatedDir.get().asFile.absolutePath,
    )
  }

sourceSets.main { kotlin.srcDir(generateUidStickers) }
