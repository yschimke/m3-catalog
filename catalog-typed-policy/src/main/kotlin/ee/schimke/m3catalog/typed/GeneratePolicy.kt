package ee.schimke.m3catalog.typed

import ee.schimke.composeai.discovery.UiBuilderPolicyFile
import java.nio.file.Files
import java.nio.file.Path
import kotlinx.serialization.json.Json

fun main(args: Array<String>) {
  require(args.size == 1) { "Expected a task-owned policy output path" }
  val output = Path.of(args.single())
  Files.createDirectories(output.toAbsolutePath().parent)
  val json = Json { prettyPrint = true }
  Files.writeString(
    output,
    json.encodeToString(UiBuilderPolicyFile.serializer(), m3BuilderPolicy()) + "\n",
  )
}
