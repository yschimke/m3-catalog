/*
 * The shared scaffolding every Glimmer sticker in this module is drawn with.
 *
 * ## Additive display, not a dark theme
 *
 * Glimmer draws for AI glasses with an ADDITIVE display: the panel emits light, so a pure black
 * pixel emits nothing and reads as fully transparent on the device. That is not a styling choice
 * this catalog makes — it is what the library's colour tokens are calibrated against, and it
 * decides how a sticker has to be captured.
 *
 * So every preview here captures on an opaque `Color.Black` ground: what the PNG shows as black is
 * exactly what the glasses would leave unlit, and everything else is the light the UI adds. A
 * sticker sheet that composited Glimmer onto white would be a picture of something the hardware
 * cannot produce.
 *
 * `@GlimmerEnvironmentPreview` is the second half of that story and is applied per component where
 * the backdrop is the point: it composites the same additive capture over a Light / Dark / Busy
 * passthrough scene, which is what a wearer actually sees through the lens.
 */
package ee.schimke.m3catalog.glimmer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import androidx.xr.glimmer.GlimmerTheme

/**
 * The AI-glasses capture surface: 960x720 at dpi 160, so 1dp is 1px and a token's declared size is
 * the size in the published PNG. Taken from compose-ai-tools' `:samples:xr-glimmer`, so the two
 * agree on what a Glimmer capture is.
 */
const val AI_GLASSES_DEVICE_SPEC: String = "spec:width=960,height=720,dpi=160"

/** Opaque black: the additive-zero baseline described above. */
const val ADDITIVE_ZERO_BACKGROUND: Long = 0xFF000000L

/**
 * Wraps a sticker in `GlimmerTheme` over the additive-zero ground, centred with room to breathe.
 *
 * Same shape as the `GlimmerSurface` helper in compose-ai-tools' Glimmer sample, deliberately: when
 * that module's helper is published from a runtime artifact, this becomes a one-line swap rather
 * than a re-authoring.
 */
@Composable
fun Sticker(content: @Composable () -> Unit) {
  Box(
    Modifier.fillMaxSize().background(Color.Black).padding(24.dp).wrapContentSize(Alignment.Center)
  ) {
    GlimmerTheme(content = content)
  }
}

/**
 * One icon, used wherever a sticker needs to show a leading or trailing slot filled.
 *
 * Declared here rather than pulled from `material-icons-extended`: Glimmer is not Material, and
 * putting a Material icon set on this module's classpath would invite exactly the mix-up this
 * catalog exists to avoid. The path is the standard filled-star outline.
 */
val StarIcon: ImageVector =
  ImageVector.Builder(
      name = "Star",
      defaultWidth = 24.dp,
      defaultHeight = 24.dp,
      viewportWidth = 24f,
      viewportHeight = 24f,
    )
    .apply {
      path(fill = SolidColor(Color.White)) {
        moveTo(12f, 17.27f)
        lineTo(18.18f, 21f)
        lineToRelative(-1.64f, -7.03f)
        lineTo(22f, 9.24f)
        lineToRelative(-7.19f, -0.61f)
        lineTo(12f, 2f)
        lineTo(9.19f, 8.63f)
        lineTo(2f, 9.24f)
        lineToRelative(5.46f, 4.73f)
        lineTo(5.82f, 21f)
        close()
      }
    }
    .build()
