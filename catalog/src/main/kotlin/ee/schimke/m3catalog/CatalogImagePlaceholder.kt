package ee.schimke.m3catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * The kit's image placeholder — a triangle, a soft burst and a square in `outlineVariant` over a
 * `surfaceContainer` ground.
 *
 * Where a kit node shows a photograph, it is showing THIS: Figma's own placeholder graphic, not
 * artwork the component depends on. So the catalog draws it from [MaterialShapes] rather than
 * committing the kit's raster, which is why an app bar's image cell and a carousel item can be the
 * same fifteen lines. A drawn placeholder also themes — it takes the dark scheme's `outlineVariant`
 * without a second asset — and scales to whatever box it is handed, neither of which a PNG does.
 *
 * [scaleBasis] is the width the motif is drawn at full size for; narrower boxes scale it down, so
 * the same call works in a 188dp carousel item and in a shallow app bar strip.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CatalogImagePlaceholder(
  modifier: Modifier = Modifier,
  shape: Shape = RoundedCornerShape(24.dp),
  scaleBasis: Float = 188f,
) {
  BoxWithConstraints(modifier.clip(shape).background(MaterialTheme.colorScheme.surfaceContainer)) {
    val scale = minOf(1f, maxWidth.value / scaleBasis, maxHeight.value / MOTIF_HEIGHT)
    val tint = MaterialTheme.colorScheme.outlineVariant
    Box(
      Modifier.align(Alignment.Center)
        .offset(y = (-36).dp * scale)
        .size(56.dp * scale)
        .clip(MaterialShapes.Triangle.toShape())
        .background(tint)
    )
    Box(
      Modifier.align(Alignment.Center)
        .offset(x = (-36).dp * scale, y = 28.dp * scale)
        .size(52.dp * scale)
        .clip(MaterialShapes.SoftBurst.toShape())
        .background(tint)
    )
    Box(
      Modifier.align(Alignment.Center)
        .offset(x = 34.dp * scale, y = 28.dp * scale)
        .size(52.dp * scale)
        .clip(MaterialShapes.Square.toShape())
        .background(tint)
    )
  }
}

/**
 * How tall the motif is at scale 1, in dp: the triangle's top edge (-36 - 56/2) up to the burst and
 * square's bottom (28 + 52/2). Named so the fit above is arithmetic rather than a tuned constant —
 * a carousel item is taller than this and is unaffected, an app bar strip is not and scales down.
 */
private const val MOTIF_HEIGHT = 118f
