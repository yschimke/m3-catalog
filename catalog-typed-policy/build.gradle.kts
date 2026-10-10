plugins { alias(libs.plugins.kotlin.jvm) }

kotlin { jvmToolchain(21) }

kotlin.sourceSets.named("main") { kotlin.srcDir("src/commonMain/kotlin") }

dependencies {
  implementation(platform(libs.composeai.contracts.bom))
  implementation("ee.schimke.composeai:component-catalog-protocol")
  implementation("ee.schimke.composeai:ui-builder-protocol")
  testImplementation(kotlin("test"))
}

val generatePolicy by
  tasks.registering(JavaExec::class) {
    group = "code generation"
    description = "Generate the parallel M3 builder policy from Kotlin declarations."
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("ee.schimke.m3catalog.typed.GeneratePolicyKt")
    val output = layout.buildDirectory.file("catalog/ui-builder.policy.json")
    outputs.file(output)
    argumentProviders.add(CommandLineArgumentProvider { listOf(output.get().asFile.absolutePath) })
  }

tasks.test {
  inputs.file(rootProject.file("ui-builder.policy.json"))
  systemProperty("m3LegacyPolicy", rootProject.file("ui-builder.policy.json").absolutePath)
}
