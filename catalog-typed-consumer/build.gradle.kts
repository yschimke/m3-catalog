plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.compose.compiler)
}

kotlin { jvmToolchain(21) }

dependencies {
  implementation(project(":catalog-typed-adapters"))
  implementation(libs.compose.runtime)
  implementation(libs.compose.ui)
}

// This is a different compilation from discovery and renderer registration: generated call sites
// must resolve against the public wrapper API, including default and explicitly authored values.
val generation = project(":catalog-typed-adapters").tasks.named("generateTypedCatalog")
val generatedSources =
  project(":catalog-typed-adapters").layout.buildDirectory.dir("catalog/generated-source")

kotlin.sourceSets.named("main") { kotlin.srcDir(files(generatedSources).builtBy(generation)) }
