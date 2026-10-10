plugins { alias(libs.plugins.kotlin.jvm) }

kotlin { jvmToolchain(21) }

dependencies {
  testImplementation("ee.schimke.composeai:ui-builder-runtime-source:0")
  testImplementation(kotlin("test"))
}

tasks.test {
  val catalog = rootProject.layout.projectDirectory.dir("catalog-typed-adapters/build/catalog")
  inputs.files(catalog.file("components.json"), catalog.file("ui-builder.json"))
  systemProperty("typedCatalogSmokeDirectory", catalog.asFile.absolutePath)
}
