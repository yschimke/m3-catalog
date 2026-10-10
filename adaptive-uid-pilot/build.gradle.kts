plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.compose.multiplatform)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.composePreview)
}

kotlin { jvmToolchain(21) }

dependencies {
  implementation(compose.desktop.currentOs)
  implementation(libs.compose.material3)
  implementation(libs.compose.adaptive)
  implementation(libs.compose.adaptive.layout)
  implementation(libs.compose.adaptive.navigation)
  implementation(libs.compose.ui.tooling.preview)
  testImplementation(kotlin("test"))
  testImplementation(libs.compose.ui.test)
  testImplementation(libs.compose.ui.test.junit4)
}

tasks.register<JavaExec>("renderPilot") {
  group = "verification"
  description =
    "Render the adaptive inbox's independent implementation at every reference size/state."
  classpath = sourceSets["main"].runtimeClasspath
  mainClass = "ee.schimke.adaptivepilot.RenderPilotKt"
  args(layout.buildDirectory.dir("pilot/previews").get().asFile.absolutePath)
  systemProperty("java.awt.headless", "true")
}
