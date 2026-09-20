package ee.schimke.m3catalog.uibuilder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.uibuilder.CanvasDocumentHost
import ee.schimke.composeai.uibuilder.CanvasMode
import ee.schimke.composeai.uibuilder.RenderCanvasNode
import ee.schimke.composeai.uibuilder.UiBuilderSemanticActionController
import ee.schimke.composeai.uibuilder.applyCanvasModifier
import ee.schimke.composeai.uibuilder.protocol.CanvasAdapterMappingV1
import ee.schimke.composeai.uibuilder.protocol.UiBuilderRendererSurfaceModeV2
import ee.schimke.composeai.uibuilder.startCatalogRenderer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

private val runtimeAdapters = foundationCanvasAdapters + materialCanvasAdapters

private val runtimePolicy by lazy {
  Json { ignoreUnknownKeys = true }.parseToJsonElement(catalogUiBuilderPolicyJson).jsonObject
}

private val adapterIds: Map<String, String> by lazy {
  buildMap {
    listOf("builtins", "components").forEach { section ->
      runtimePolicy[section]?.jsonObject?.forEach { (componentId, element) ->
        element.jsonObject["canvas"]?.jsonPrimitive?.contentOrNull?.let { put(componentId, it) }
      }
    }
  }
}

private val adapterMappings: Map<String, CanvasAdapterMappingV1> by lazy {
  val json = Json { ignoreUnknownKeys = true }
  buildMap {
    runtimePolicy["components"]?.jsonObject?.forEach { (componentId, element) ->
      element.jsonObject["canvasMapping"]?.let { mapping ->
        put(componentId, json.decodeFromJsonElement(CanvasAdapterMappingV1.serializer(), mapping))
      }
    }
  }
}

fun main() {
  val actions = UiBuilderSemanticActionController()
  startCatalogRenderer(actions) { document, surface, renderSessionId, onInspectionSnapshot ->
    val hostDensity = LocalDensity.current
    val density =
      Density(
        density = surface.density,
        fontScale =
          document.environment["fontScale"]
            ?.let { it as? JsonPrimitive }
            ?.contentOrNull
            ?.toFloatOrNull()
            ?.takeIf { it.isFinite() && it > 0f } ?: hostDensity.fontScale,
      )
    val mode =
      if (surface.mode == UiBuilderRendererSurfaceModeV2.AUTHORING_UNROLLED)
        CanvasMode.AuthoringUnrolled
      else CanvasMode.Device
    val dark = document.environment["theme"]?.jsonPrimitive?.contentOrNull == "dark"
    CompositionLocalProvider(LocalDensity provides density) {
      MaterialTheme(colorScheme = if (dark) darkColorScheme() else lightColorScheme()) {
        Box(Modifier.requiredSize(surface.widthDp.dp, surface.heightDp.dp)) {
          CanvasDocumentHost(
            document = document,
            adapterIds = adapterIds,
            adapterMappings = adapterMappings,
            mode = mode,
            density = density,
            modifier = Modifier.fillMaxSize(),
            renderSessionId = renderSessionId,
            runtimeActionController = actions,
            onInspectionSnapshot = onInspectionSnapshot,
          ) { entry, rootModifier ->
            RenderCanvasNode(
              entry = entry,
              registry = runtimeAdapters,
              modifier = rootModifier,
              applyModifier = { current, value ->
                current.applyCanvasModifier(
                  value = value,
                  mode = mode,
                  resolveColor = { materialColor(it) },
                  resolveShape = ::materialShape,
                )
              },
              missingComponent = { label, next -> UnsupportedComponent(label, next) },
            ) {
              UnsupportedComponent(node.componentId, prepared.modifier)
            }
          }
        }
      }
    }
  }
}

@Composable
internal fun materialColor(value: String, fallback: Color = Color.Unspecified): Color =
  when {
    value.startsWith("#") -> Color(parseArgb(value))
    value == "background" -> MaterialTheme.colorScheme.background
    value == "surface" -> MaterialTheme.colorScheme.surface
    value == "surfaceContainer" -> MaterialTheme.colorScheme.surfaceContainer
    value == "surfaceContainerLow" -> MaterialTheme.colorScheme.surfaceContainerLow
    value == "surfaceContainerHigh" -> MaterialTheme.colorScheme.surfaceContainerHigh
    value == "surfaceContainerHighest" -> MaterialTheme.colorScheme.surfaceContainerHighest
    value == "primary" -> MaterialTheme.colorScheme.primary
    value == "onPrimary" -> MaterialTheme.colorScheme.onPrimary
    value == "tertiary" -> MaterialTheme.colorScheme.tertiary
    value == "onTertiary" -> MaterialTheme.colorScheme.onTertiary
    value == "onSurface" -> MaterialTheme.colorScheme.onSurface
    value == "onSurfaceVariant" -> MaterialTheme.colorScheme.onSurfaceVariant
    value == "outlineVariant" -> MaterialTheme.colorScheme.outlineVariant
    value == "transparent" -> Color.Transparent
    else -> fallback
  }

@Composable
private fun materialShape(value: String?): Shape =
  when (value) {
    "large" -> MaterialTheme.shapes.large
    "medium" -> MaterialTheme.shapes.medium
    "small" -> MaterialTheme.shapes.small
    else -> RoundedCornerShape(value?.toFloatOrNull()?.dp ?: 0.dp)
  }

@Composable
private fun UnsupportedComponent(label: String, modifier: Modifier) {
  Box(modifier.background(MaterialTheme.colorScheme.errorContainer).padding(8.dp)) {
    Text(label, color = MaterialTheme.colorScheme.onErrorContainer)
  }
}

private fun parseArgb(value: String): ULong {
  val hex = value.removePrefix("#")
  return when (hex.length) {
    6 -> ("FF$hex").toULong(16)
    8 -> hex.toULong(16)
    else -> 0u
  }
}
