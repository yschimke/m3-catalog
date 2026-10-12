package ee.schimke.m3catalog.typed

import ee.schimke.composeai.discovery.ComponentRecordFile
import ee.schimke.composeai.discovery.UiBuilderCatalogFile
import ee.schimke.composeai.discovery.writeTo
import java.nio.file.Files
import java.nio.file.Path
import kotlinx.serialization.json.Json

fun M3CatalogOutput.writeTo(directory: Path) {
  val destination = directory.toAbsolutePath().normalize()
  require(!Files.exists(destination)) { "catalog output already exists: $destination" }
  Files.createDirectories(destination.parent)
  val staging = Files.createTempDirectory(destination.parent, ".m3-catalog-")
  try {
    val pair = staging.resolve("pair")
    portable.writeTo(pair)
    val json = Json {
      prettyPrint = true
      encodeDefaults = true
    }
    // The published record also carries the real records the projections export to.
    Files.writeString(
      pair.resolve("components.json"),
      json.encodeToString(ComponentRecordFile.serializer(), record),
    )
    Files.writeString(
      pair.resolve("ui-builder.json"),
      json.encodeToString(UiBuilderCatalogFile.serializer(), catalog),
    )
    Files.move(pair, destination)
  } finally {
    staging.toFile().deleteRecursively()
  }
}
