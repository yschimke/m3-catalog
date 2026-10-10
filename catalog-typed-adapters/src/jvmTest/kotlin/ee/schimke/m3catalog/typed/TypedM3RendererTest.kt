package ee.schimke.m3catalog.typed

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import ee.schimke.composeai.uibuilder.export.UiBuilderNode
import ee.schimke.composeai.uibuilder.renderer.sdk.CanvasMode
import ee.schimke.composeai.uibuilder.renderer.sdk.CanvasNodeScope
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.jetbrains.skia.Image

@OptIn(ExperimentalTestApi::class)
class TypedM3RendererTest {
  @Test
  fun `text controls write bound state and preserve their callback events`() {
    for ((id, event) in
      listOf(
        "m3/text-field" to "valueChange",
        "m3/outlined-text-field" to "valueChange",
        "m3/search-input-field" to "queryChange",
      )) {
      runComposeUiTest {
        val typed = TypedM3Catalog(discoverTypedM3Controls())
        val registry = typedM3CanvasAdapters(typed)
        val events = mutableListOf<String>()
        val updates = mutableListOf<Pair<String, String?>>()
        val properties = buildJsonObject {
          put(
            "value",
            buildJsonObject {
              put("type", "state")
              put("variable", "query")
              put("value", "Initial")
            },
          )
        }
        setContent {
          MaterialTheme { registry[id]!!.invoke(scope(id, properties, "field", events, updates)) }
        }
        onNodeWithTag("field").performTextInput(" edited")
        runOnIdle {
          assertEquals("query", updates.last().first)
          assertTrue(updates.last().second!!.contains("edited"))
          assertEquals(event, events.last())
        }
      }
    }
  }

  @Test
  fun `typed adapters match real Material controls across selection and enabled states`() {
    for (id in listOf("m3/checkbox", "m3/switch", "m3/radio-button")) {
      for (checked in listOf(false, true)) for (enabled in listOf(false, true)) {
        runComposeUiTest {
          val typed = TypedM3Catalog(discoverTypedM3Controls())
          val registry = typedM3CanvasAdapters(typed)
          val key = if (id == "m3/radio-button") "selected" else "checked"
          val properties = buildJsonObject {
            put(
              key,
              buildJsonObject {
                put("type", "boolean")
                put("value", checked)
              },
            )
            put(
              "enabled",
              buildJsonObject {
                put("type", "boolean")
                put("value", enabled)
              },
            )
          }
          setContent {
            MaterialTheme {
              Column {
                when (id) {
                  "m3/checkbox" -> Checkbox(checked, {}, Modifier.testTag("direct"), enabled)
                  "m3/switch" -> Switch(checked, {}, Modifier.testTag("direct"), enabled = enabled)
                  else -> RadioButton(checked, {}, Modifier.testTag("direct"), enabled)
                }
                registry[id]!!.invoke(scope(id, properties, "typed"))
              }
            }
          }
          val direct = onNodeWithTag("direct").captureToImage()
          val adapted = onNodeWithTag("typed").captureToImage()
          val evidence = Path.of(System.getProperty("typedRenderEvidence"))
          Files.createDirectories(evidence)
          val case = "${id.substringAfter('/')}-$checked-$enabled"
          Files.write(
            evidence.resolve("$case-material.png"),
            Image.makeFromBitmap(direct.asSkiaBitmap()).use {
              it.encodeToData()!!.use { data -> data.bytes }
            },
          )
          Files.write(
            evidence.resolve("$case-typed.png"),
            Image.makeFromBitmap(adapted.asSkiaBitmap()).use {
              it.encodeToData()!!.use { data -> data.bytes }
            },
          )
          assertEquals(direct.width, adapted.width, "$id width")
          assertEquals(direct.height, adapted.height, "$id height")
          val left = direct.toPixelMap()
          val right = adapted.toPixelMap()
          for (y in 0 until left.height) for (x in 0 until left.width) {
            assertEquals(
              left[x, y],
              right[x, y],
              "$id checked=$checked enabled=$enabled pixel($x,$y)",
            )
          }
        }
      }
    }
  }

  @Test
  fun `typed callbacks dispatch a click and update bound Boolean state`() = runComposeUiTest {
    val typed = TypedM3Catalog(discoverTypedM3Controls())
    val registry = typedM3CanvasAdapters(typed)
    val events = mutableListOf<String>()
    val updates = mutableListOf<Pair<String, String?>>()
    val bound = buildJsonObject {
      put(
        "checked",
        buildJsonObject {
          put("type", "state")
          put("variable", "selection")
          put("value", "false")
        },
      )
    }
    setContent {
      MaterialTheme {
        Column {
          registry["m3/checkbox"]!!.invoke(scope("m3/checkbox", bound, "checkbox", events, updates))
          registry["m3/switch"]!!.invoke(scope("m3/switch", bound, "switch", events, updates))
          registry["m3/radio-button"]!!.invoke(
            scope("m3/radio-button", JsonObject(emptyMap()), "radio", events, updates)
          )
        }
      }
    }
    onNodeWithTag("checkbox").performClick()
    onNodeWithTag("switch").performClick()
    onNodeWithTag("radio").performClick()
    assertEquals(
      listOf<Pair<String, String?>>("selection" to "true", "selection" to "true"),
      updates,
    )
    assertEquals(listOf("checkedChange", "checkedChange", "click"), events)
  }

  @Test
  fun `a malformed property diagnoses one node while its sibling still renders`() =
    runComposeUiTest {
      val typed = TypedM3Catalog(discoverTypedM3Controls())
      val registry = typedM3CanvasAdapters(typed)
      val bad = buildJsonObject {
        put(
          "checked",
          buildJsonObject {
            put("type", "boolean")
            put("value", "true")
          },
        )
      }
      setContent {
        MaterialTheme {
          Column {
            registry["m3/checkbox"]!!.invoke(scope("m3/checkbox", bad, "broken"))
            registry["m3/switch"]!!.invoke(scope("m3/switch", JsonObject(emptyMap()), "healthy"))
          }
        }
      }
      onNodeWithText("m3/checkbox: invalid properties", substring = true).assertExists()
      onNodeWithTag("healthy").assertExists()
    }

  private fun scope(
    id: String,
    properties: JsonObject,
    tag: String,
    events: MutableList<String> = mutableListOf(),
    updates: MutableList<Pair<String, String?>> = mutableListOf(),
  ) =
    CanvasNodeScope(
      node = UiBuilderNode(id = tag, componentId = id, properties = properties),
      modifier = Modifier.testTag(tag),
      mode = CanvasMode.Device,
      renderSlot = { _, _ -> },
      renderItems = { _, _ -> },
      countItems = { 0 },
      renderItem = { _, _, _ -> },
      dispatchEvent = events::add,
      updateState = { key, value -> updates += key to value },
      recordText = {},
    )
}
