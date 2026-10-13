// `:uid-stickers` — the UI builder documents behind `:catalog`'s generated stickers.
//
// `stickers/<name>.uid` is a sticker body (one component definition placed once) and its sibling
// `stickers/<name>.json` lists its cells: builder operations applied before export.
// `generateUidStickers` exports every document and cell through the UI builder's own exporter
// (`ScreenExportGate.exportComponents`, the code behind the builder's code pane) into
// `catalog/src/uid/kotlin`, where the catalog's stickers call them; `verifyUidStickers` fails
// when that checked-in source is stale. Opt-in (`-PuidStickers=true`): the catalog itself builds
// from the checked-in source, with no exporter.
plugins { alias(libs.plugins.kotlin.jvm) }

kotlin { jvmToolchain(21) }

// The released exporter (`exportComponents` since compose-ui-builder 3.108, its generator's toggle
// intents since compose-ai-tools 2.53). `-PuidExportVersion=<x>-SNAPSHOT` tries an unreleased one
// published to Maven local, with `-PscreenModelVersion=` for the generator it was built against.
if (providers.gradleProperty("uidExportVersion").isPresent) {
  repositories {
    mavenLocal()
    mavenCentral()
    google()
  }
}

val generator by sourceSets.creating

dependencies {
  // The exporter the builder's code pane and the server's export.compose route run.
  // `uiBuilderExport` in gradle/libs.versions.toml, which Renovate owns.
  "generatorImplementation"(libs.composeai.ui.builder.export) {
    providers.gradleProperty("uidExportVersion").orNull?.let { version { strictly(it) } }
  }
  // `-PscreenModelVersion=<x>-SNAPSHOT` pairs an unreleased exporter with the unreleased
  // generator it was built against.
  providers.gradleProperty("screenModelVersion").orNull?.let { pinned ->
    "generatorImplementation"("ee.schimke.composeai:screen-model-jvm") {
      version { strictly(pinned) }
    }
    "generatorImplementation"("ee.schimke.composeai:screen-model") { version { strictly(pinned) } }
  }
}

// The generated components are `:catalog` source, checked in (`catalog/src/uid/kotlin`), so the
// catalog builds without this module or an exporter. Regenerate after editing a document, and
// `verifyUidStickers` (in `check`) fails when the checked-in source is stale.
val catalogGeneratedDir = rootProject.layout.projectDirectory.dir("catalog/src/uid/kotlin")
val verifyDir = layout.buildDirectory.dir("uid-verify/kotlin")

fun JavaExec.exportStickers(output: Any) {
  group = "build"
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
  outputs.dir(output)
  args(
    layout.projectDirectory.asFile.absolutePath,
    file(output).absolutePath,
    catalogDiscovery.get().asFile.absolutePath,
  )
}

val generateUidStickers by
  tasks.registering(JavaExec::class) {
    description = "Exports every sticker document and cell into :catalog's checked-in source."
    exportStickers(catalogGeneratedDir)
  }

val exportForVerify by
  tasks.registering(JavaExec::class) {
    description = "Exports every sticker document and cell to compare with the checked-in source."
    exportStickers(verifyDir)
  }

val verifyUidStickers by
  tasks.registering(Exec::class) {
    group = "verification"
    description = "Fails when catalog/src/uid/kotlin is not what the documents export."
    dependsOn(exportForVerify)
    commandLine(
      "diff",
      "-r",
      verifyDir.get().asFile.absolutePath,
      catalogGeneratedDir.asFile.absolutePath,
    )
  }

tasks.named("check") { dependsOn(verifyUidStickers) }
