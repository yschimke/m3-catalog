plugins {
  // Declared here, `apply false`, so AGP and the Kotlin plugins land on ONE coordinated buildscript
  // classpath. Without it, AGP 9's application plugin meets a Kotlin Gradle Plugin it did not
  // negotiate with and dies creating a `KotlinAndroidTarget` against `BaseVariant`, the old variant
  // API AGP 9 removed. Applies to the two Glimmer modules only; every other module here is
  // deliberately AGP-free.
  alias(libs.plugins.android.library) apply false
  alias(libs.plugins.kotlin.jvm) apply false
  alias(libs.plugins.compose.compiler) apply false
  alias(libs.plugins.compose.multiplatform) apply false
  alias(libs.plugins.composePreview) apply false
  alias(libs.plugins.ktfmt)
}

allprojects {
  apply(plugin = "com.ncorti.ktfmt.gradle")
  ktfmt { googleStyle() }
}
