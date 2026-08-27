@file:OptIn(ExperimentalTestApi::class, ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ToggleButtonShapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import ee.schimke.composeai.overrides.LocalPreviewOverrideHost
import ee.schimke.composeai.overrides.PreviewOverrideHost
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * The toggle button's shape morph is a **swap**, pinned per size.
 *
 * The kit draws a round toggle button as a pill that turns squarer when checked, and a square one
 * as a squarer container that turns into a pill: `xl-on` is `CornerExtraLarge` where `xl-square-on`
 * is `CornerFull`. The catalog once read only half of that — the square branch took
 * `<size>CheckedSquareShape` for its checked corner, which is the shape `shapesFor` already hands
 * the ROUND branch — so all four checked cells rendered byte-identically to their round siblings
 * while `design-map.json` compared each against its own kit node ([issue #157]).
 *
 * A render catches that only as a hash collision, one full nightly render later. This asks the
 * question directly, in a composition, from the resolver the stickers actually call: at every size
 * the two branches must swap, not converge.
 *
 * [issue #157]: https://github.com/yschimke/m3-catalog/issues/157
 */
class ToggleButtonShapeAxisTest {

  /** Enough to catch a wrong constant, loose enough for the px round trip. */
  private val tolerance = 0.01f

  /** A [PreviewOverrideHost] that answers one string knob and defaults everything else. */
  private class SeededHost(private val seeds: Map<String, String>) : PreviewOverrideHost {
    @Composable
    override fun string(key: String, default: String, index: Int?): String = seeds[key] ?: default

    @Composable override fun int(key: String, default: Int, index: Int?): Int = default

    @Composable override fun float(key: String, default: Float, index: Int?): Float = default

    @Composable override fun boolean(key: String, default: Boolean, index: Int?): Boolean = default

    @Composable override fun color(key: String, default: Color, index: Int?): Color = default

    @Composable override fun dp(key: String, default: Dp, index: Int?): Dp = default
  }

  @Composable
  private fun shapesWith(shape: CatalogShape, size: CatalogSize): ToggleButtonShapes {
    var resolved: ToggleButtonShapes? = null
    CompositionLocalProvider(
      LocalPreviewOverrideHost provides SeededHost(mapOf("shape" to shape.knob))
    ) {
      resolved = catalogToggleButtonShapes(size)
    }
    return resolved!!
  }

  @Test
  fun `the checked corner swaps with the shape knob at every size`() = runComposeUiTest {
    val failures = mutableListOf<String>()
    setContent {
      val density = LocalDensity.current
      for (size in CatalogSize.entries) {
        // The sticker's own footprint: a corner can be a percentage, so the box matters.
        val box = Size(200f * density.density, size.containerHeight.value * density.density)
        val round = shapesWith(CatalogShape.Round, size)
        val square = shapesWith(CatalogShape.Square, size)

        val roundChecked = round.checkedShape.cornerDp(box, density)
        val squareChecked = square.checkedShape.cornerDp(box, density)
        val squareResting = square.shape.cornerDp(box, density)
        val pill = size.containerHeight.value / 2f

        // The bug: both branches landing on the same checked picture.
        if (abs(roundChecked - squareChecked) <= tolerance) {
          failures +=
            "${size.knob}: round and square both compose a ${roundChecked}dp checked corner — " +
              "the shape knob does not reach the checked state"
        }
        // A round toggle button turns squarer when checked: it lands on the square resting corner.
        if (abs(roundChecked - squareResting) > tolerance) {
          failures +=
            "${size.knob}: round checks into ${roundChecked}dp, but the square branch rests at " +
              "${squareResting}dp — the round morph should land on the square shape"
        }
        // A square one turns into a pill.
        if (abs(squareChecked - pill) > tolerance) {
          failures +=
            "${size.knob}: square checks into ${squareChecked}dp, expected the ${pill}dp pill"
        }
      }
    }
    assertTrue(failures.isEmpty(), failures.joinToString(prefix = "\n", separator = "\n"))
  }

  /** The shape's top-start corner in dp at [size]; `0` for a shape with no corners at all. */
  private fun Shape.cornerDp(size: Size, density: Density): Float =
    when (this) {
      is CornerBasedShape -> topStart.toPx(size, density) / density.density
      else -> 0f
    }
}
