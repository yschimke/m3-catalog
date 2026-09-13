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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.BrushPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import androidx.xr.glimmer.GlimmerTheme

/**
 * Opaque black: the additive-zero baseline described above, and the ONLY thing this module states
 * about the capture.
 *
 * There used to be an `AI_GLASSES_DEVICE_SPEC` beside it — `spec:width=960,height=720,dpi=160`,
 * borrowed from compose-ai-tools' `:samples:xr-glimmer` on the reasoning that a Glimmer sticker
 * should be captured on a Glimmer-shaped screen. It was wrong twice over, and #367 is what it looks
 * like from the outside:
 *
 * * **It framed every sticker in a screen it does not fill.** A sticker is a picture of a
 *   COMPONENT, not a screenshot of a device, and the harness sizes the canvas to the content unless
 *   told otherwise. Forcing 960x720 put a 118x48 toggle button in a 691,200-pixel frame: 0.8%
 *   content, and a mean of 4.1% across all 19 renders. `:catalog` never did this — its
 *   `@CatalogModes` carries no device, and the only widths it names are ones the KIT measures.
 * * **It broke the pairing it was supposed to serve.** dpi 160 is density 1.0, while
 *   `glimmer-samples` renders at the default 2.625, so `compareWith` stood the same Glimmer
 *   `Button` beside itself at two different pixel scales — a renderer difference inside a
 *   comparison meant to be about design, which is the exact confound `docs/design/GLIMMER.md`
 *   refuses elsewhere.
 *
 * What the spec was really buying was 1dp = 1px for reading a token's size off the PNG. That is a
 * measuring convenience, and it is not worth either cost: `catalog.json` records every image's
 * `density` beside its `width`, so the dp size is still exactly recoverable.
 */
const val ADDITIVE_ZERO_BACKGROUND: Long = 0xFF000000L

/**
 * Wraps a sticker in `GlimmerTheme`. That is all it does, and the subtraction is the point.
 *
 * It used to be a `fillMaxSize()` Box painting `Color.Black` with `padding(24.dp)` inside it, and
 * every part of that was the capture's job rather than the tree's:
 *
 * * `fillMaxSize()` is what stretched the sticker to the device spec above — and, on `Card` and
 *   `ListItem`, what drew them 912dp wide because they fill whatever they are given. A component
 *   that measures itself is the honest picture of it.
 * * The black ground is `showBackground` / `backgroundColor` on the `@Preview`, where it belongs:
 *   the additive-zero ground is a property of the CAPTURE, and a `background()` node in the tree
 *   only re-states it somewhere it can drift.
 * * `padding(24.dp)` is the mistake `CatalogCaptureGutters.kt` already has a name and an issue
 *   number for (#179): "padding inside the sticker measures the component in a smaller box and
 *   grows the canvas". Room for a glow belongs on a `@CaptureGutter`, which reserves canvas without
 *   shrinking the measurement.
 *
 * It stays a function rather than being inlined at 19 call sites: it is still the seam that swaps
 * to compose-ai-tools' `GlimmerSurface` when that helper is published from a runtime artifact.
 */
@Composable
fun Sticker(content: @Composable () -> Unit) {
  GlimmerTheme(content = content)
}

/**
 * The width the Glimmer kit draws its fill-width components at: 420dp, read from the `Card`
 * (`416:2700`, 420x412) and `List Item` (`384:4195`, 420x80) nodes.
 *
 * It is a kit BOUND rather than a kit size, in the sense `AGENTS.md` draws the distinction — it is
 * the component's own measured extent in the kit, not a number a caller passes — so it goes on a
 * frame around the component ([ContentFrame]) rather than on the component itself. `Modifier.width`
 * on a `Card` would hand it a tight minimum; a `Box` loosens the minimum for its child, so the card
 * still measures itself and simply stops filling more than the kit gives it.
 *
 * Without it these two fill the 960dp glasses display that #376 made the wrap sandbox, which is
 * 2.3x the kit's column and the second half of #381.
 */
val KitContentWidth = 420.dp

/**
 * A [KitContentWidth]-wide bound for the two components that fill whatever width they are given.
 */
@Composable
fun ContentFrame(content: @Composable () -> Unit) {
  Box(modifier = Modifier.width(KitContentWidth)) { content() }
}

private const val HEADER_INTRINSIC = 1000f

/**
 * The card header's placeholder artwork: a gradient standing in for the photograph the kit's `Card`
 * node draws in its image slot.
 *
 * A gradient rather than a bundled photo for the reason every sticker here is deterministic — a
 * painter computed from constants cannot decode differently between runs — and it is the same
 * device upstream's own `CardSamples.kt` uses for its header samples, with a large intrinsic size
 * so the slot scales it the way a loaded bitmap would be scaled.
 */
val HeaderImage: Painter =
  BrushPainter(
    Brush.linearGradient(
      0.0f to Color(0xFF3C8CDE),
      0.4f to Color(0xFFED73A8),
      0.6f to Color(0xFFED73A8),
      1.0f to Color(0xFFE763F9),
      start = Offset.Zero,
      end = Offset(HEADER_INTRINSIC, HEADER_INTRINSIC),
    )
  )

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
