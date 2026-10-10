package ee.schimke.m3catalog.typed

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.uibuilder.export.UiBuilderNode
import ee.schimke.composeai.uibuilder.renderer.sdk.CanvasMode
import ee.schimke.composeai.uibuilder.renderer.sdk.CanvasNodeScope
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.jetbrains.skia.Image

@OptIn(ExperimentalTestApi::class)
class FullM3RendererTest {
  @Test
  fun `every typed registration matches its compiled wrapper with populated slots`() {
    val typed = TypedM3Catalog(discoverTypedM3Controls())
    val registry = typedM3CanvasAdapters(typed)
    for (definition in typed.runtimeAdapters) {
      runComposeUiTest {
        var renderTyped by mutableStateOf(false)
        val properties = buildJsonObject {
          definition.properties.forEach { property ->
            if (property.name in setOf("text", "value") && property.codec.jsonType == "string")
              put(
                property.name,
                buildJsonObject {
                  put("type", "string")
                  put("value", "Typed catalog")
                },
              )
            if (property.name == "visible")
              put(
                property.name,
                buildJsonObject {
                  put("type", "boolean")
                  put("value", true)
                },
              )
          }
        }
        val slots = definition.slots.associate { it.name to listOf("child") }
        setContent {
          MaterialTheme {
            Box(Modifier.requiredSize(450.dp, 600.dp)) {
              if (!renderTyped) {
                DirectM3Fixture(definition.id, Modifier.testTag("control"))
              } else {
                registry[definition.id]!!.invoke(
                  CanvasNodeScope(
                    node = UiBuilderNode("adapted", definition.id, properties, slots = slots),
                    modifier = Modifier.testTag("control"),
                    mode = CanvasMode.Device,
                    renderSlot = { _, _ -> Text("Slot content") },
                    renderItems = { _, _ -> },
                    countItems = { 1 },
                    renderItem = { _, _, _ -> },
                    dispatchEvent = {},
                    updateState = { _, _ -> },
                    recordText = {},
                  )
                )
              }
            }
          }
        }
        onNodeWithText("invalid properties", substring = true).assertDoesNotExist()
        val direct = onNodeWithTag("control", useUnmergedTree = true).captureToImage()
        runOnIdle { renderTyped = true }
        onNodeWithText("invalid properties", substring = true).assertDoesNotExist()
        val adapted = onNodeWithTag("control", useUnmergedTree = true).captureToImage()
        assertEquals(direct.width, adapted.width, "${definition.id} width")
        assertEquals(direct.height, adapted.height, "${definition.id} height")
        val a = direct.toPixelMap()
        val b = adapted.toPixelMap()
        for (y in 0 until a.height) for (x in 0 until a.width) assertEquals(
          a[x, y],
          b[x, y],
          "${definition.id} pixel($x,$y)",
        )
        val output = Path.of(System.getProperty("typedRenderEvidence")).resolve("full-catalog")
        Files.createDirectories(output)
        listOf("direct" to direct, "typed" to adapted).forEach { (kind, bitmap) ->
          Files.write(
            output.resolve("${definition.id.substringAfter('/')}-$kind.png"),
            Image.makeFromBitmap(bitmap.asSkiaBitmap()).use {
              it.encodeToData()!!.use { data -> data.bytes }
            },
          )
        }
      }
    }
  }
}
