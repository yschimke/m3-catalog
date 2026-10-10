plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.compose.multiplatform)
  alias(libs.plugins.compose.compiler)
}

kotlin {
  jvmToolchain(21)
  jvm()
  @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
  wasmJs {
    browser()
    outputModuleName.set("m3TypedCatalogRenderer")
    binaries.executable()
  }
  sourceSets {
    commonMain.dependencies {
      implementation("ee.schimke.composeai:screen-model-source:0")
      implementation(libs.composeai.ui.builder.renderer.sdk.source)
      implementation(libs.compose.material3)
      @Suppress("DEPRECATION") implementation(compose.runtime)
      @Suppress("DEPRECATION") implementation(compose.foundation)
      @Suppress("DEPRECATION") implementation(compose.ui)
    }
    // This project belongs to the tools build's nested gradle-plugin build. Gradle automatically
    // substitutes its real coordinate; the synthetic version has no published fallback.
    jvmMain.dependencies { implementation("ee.schimke.composeai:preview-discovery:0") }
    jvmTest.dependencies {
      implementation(kotlin("test"))
      implementation(libs.compose.ui.test.junit4)
      @Suppress("DEPRECATION") implementation(compose.desktop.currentOs)
    }
  }
}

val generationDirectory = layout.buildDirectory.dir("catalog")
val generatedRecords = layout.buildDirectory.dir("generated/typedRecords")
val jvmMain = kotlin.jvm().compilations.getByName("main")

val generateTypedCatalog by
  tasks.registering(JavaExec::class) {
    group = "code generation"
    description = "Discover compiled M3 wrappers and generate a separate typed pilot catalog pair."
    classpath(jvmMain.output.allOutputs, jvmMain.runtimeDependencyFiles)
    mainClass.set("ee.schimke.m3catalog.typed.GenerateTypedCatalogKt")
    val legacy = rootProject.layout.projectDirectory.file("ui-builder.policy.json")
    inputs.file(legacy)
    inputs.dir(rootProject.layout.projectDirectory.dir("ui-builder/designs"))
    outputs.dir(generationDirectory)
    outputs.dir(generatedRecords)
    val catalogOutput = generationDirectory
    val recordsOutput = generatedRecords
    argumentProviders.add(
      CommandLineArgumentProvider {
        listOf(
          catalogOutput.get().asFile.absolutePath,
          recordsOutput.get().asFile.absolutePath,
          legacy.asFile.absolutePath,
        )
      }
    )
  }

kotlin.sourceSets.named("wasmJsMain") {
  kotlin.srcDir(files(generatedRecords).builtBy(generateTypedCatalog))
}

val verifyTypedCatalog by tasks.registering {
  group = "verification"
  description = "Validate the parallel policy, discovered adapters and their JVM/Wasm renderers."
  dependsOn(":catalog-typed-policy:test", ":catalog-typed-policy:generatePolicy")
  dependsOn("jvmTest", generateTypedCatalog, "compileKotlinWasmJs")
  dependsOn(":catalog-typed-consumer:compileKotlin")
}

val builderDirectory = providers.gradleProperty("composeUiBuilderDir").map { rootProject.file(it) }
val runtimeAssets by
  tasks.registering(Sync::class) {
    dependsOn(
      "wasmJsDevelopmentExecutableCompileSync",
      "wasmJsProcessResources",
      "processSkikoRuntimeForKWasm",
    )
    from(layout.buildDirectory.dir("compileSync/wasmJs/main/developmentExecutable/kotlin"))
    from(layout.buildDirectory.dir("compose/skiko-runtime-processed-wasmjs")) {
      include("skiko.mjs", "skiko.wasm")
    }
    from(layout.projectDirectory.dir("src/wasmJsMain/resources"))
    from(builderDirectory.map { it.resolve("assets/js-joda") }) { include("js-joda.esm.js") }
    from(builderDirectory.map { it.resolve("assets/rc-fonts") }) {
      include("*.ttf", "fonts.json", "*OFL.txt", "LICENSE.txt")
      into("fonts")
    }
    into(layout.buildDirectory.dir("runtimeAssets"))
  }

val runtimeDirectory = layout.buildDirectory.dir("wasmRendererDist")
val runtimeIdentity =
  providers
    .environmentVariable("GITHUB_SHA")
    .map { "m3-typed-pilot-${it.take(12)}" }
    .orElse("m3-typed-pilot-development")
val wasmRendererDist by
  tasks.registering(JavaExec::class) {
    dependsOn(runtimeAssets)
    classpath(jvmMain.output.allOutputs, jvmMain.runtimeDependencyFiles)
    mainClass.set("ee.schimke.m3catalog.typed.AssembleTypedRuntimeKt")
    inputs.dir(runtimeAssets.map { it.destinationDir })
    inputs.property("runtimeId", runtimeIdentity)
    outputs.dir(runtimeDirectory)
    val assets = runtimeAssets.map { it.destinationDir }
    val output = runtimeDirectory
    val identity = runtimeIdentity
    argumentProviders.add(
      CommandLineArgumentProvider {
        listOf(
          assets.get().absolutePath,
          output.get().asFile.absolutePath,
          identity.get(),
        )
      }
    )
  }

val rendererArchive by
  tasks.registering(Zip::class) {
    group = "distribution"
    description =
      "Package the parallel typed M3 runtime, without attaching it to production delivery."
    dependsOn(wasmRendererDist)
    from(runtimeDirectory)
    archiveFileName.set("m3-catalog-typed-pilot-renderer.zip")
    destinationDirectory.set(layout.buildDirectory.dir("distributions"))
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
  }

tasks.named<Test>("jvmTest") {
  // Both coordinates contain the same shared generator. Execute the JVM discovery variant only.
  classpath = classpath.filter { !it.name.startsWith("screen-model") }
  val evidence = layout.buildDirectory.dir("render-evidence")
  systemProperty("typedRenderEvidence", evidence.get().asFile.absolutePath)
  outputs.dir(evidence)
}

generateTypedCatalog { classpath = classpath.filter { !it.name.startsWith("screen-model") } }
