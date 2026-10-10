package ee.schimke.m3catalog.typed

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.discovery.ComponentRecordFile
import ee.schimke.composeai.uibuilder.protocol.UiBuilderRendererSurfaceModeV2
import ee.schimke.composeai.uibuilder.renderer.sdk.CanvasDocumentHost
import ee.schimke.composeai.uibuilder.renderer.sdk.CanvasMode
import ee.schimke.composeai.uibuilder.renderer.sdk.RenderCanvasNode
import ee.schimke.composeai.uibuilder.renderer.sdk.UiBuilderSemanticActionController
import ee.schimke.composeai.uibuilder.renderer.sdk.applyCanvasModifier
import ee.schimke.composeai.uibuilder.renderer.sdk.startCatalogRenderer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

fun main() {
  val typed =
    TypedM3Catalog(Json.decodeFromString(ComponentRecordFile.serializer(), typedM3RecordsJson))
  val registry = typedM3CanvasAdapters(typed)
  val ids = typed.adapters.associate { it.id to it.id }
  val actions = UiBuilderSemanticActionController()
  startCatalogRenderer(actions) { document, surface, session, inspection ->
    val density = Density(surface.density, LocalDensity.current.fontScale)
    val mode =
      if (surface.mode == UiBuilderRendererSurfaceModeV2.AUTHORING_UNROLLED)
        CanvasMode.AuthoringUnrolled
      else CanvasMode.Device
    CompositionLocalProvider(LocalDensity provides density) {
      MaterialTheme(
        colorScheme =
          if (document.environment["theme"]?.jsonPrimitive?.contentOrNull == "dark")
            darkColorScheme()
          else lightColorScheme()
      ) {
        Box(Modifier.requiredSize(surface.widthDp.dp, surface.heightDp.dp)) {
          CanvasDocumentHost(
            document = document,
            adapterIds = ids,
            adapterMappings = emptyMap(),
            mode = mode,
            density = density,
            modifier = Modifier.fillMaxSize(),
            renderSessionId = session,
            runtimeActionController = actions,
            onInspectionSnapshot = inspection,
          ) { entry, rootModifier ->
            RenderCanvasNode(
              entry = entry,
              registry = registry,
              modifier = rootModifier,
              applyModifier = { current, value ->
                current.applyCanvasModifier(
                  value = value,
                  mode = mode,
                  unrolledHorizontally = unrolledHorizontally,
                  resolveColor = { androidx.compose.ui.graphics.Color.Unspecified },
                  resolveShape = { androidx.compose.ui.graphics.RectangleShape },
                )
              },
              missingComponent = { label, next -> Text("Typed pilot: $label", modifier = next) },
            ) {
              Text("Typed pilot: ${node.componentId}", modifier = prepared.modifier)
            }
          }
        }
      }
    }
  }
}
