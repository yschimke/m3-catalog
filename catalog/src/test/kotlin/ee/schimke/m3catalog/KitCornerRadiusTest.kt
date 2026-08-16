@file:OptIn(
  ExperimentalTestApi::class,
  ExperimentalMaterial3Api::class,
  ExperimentalMaterial3ExpressiveApi::class,
)

package ee.schimke.m3catalog

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.Density
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * The corner radii design parity argued about, pinned as assertions.
 *
 * Parity reported a `radius.corner` divergence on most of the mapped components, and triage
 * ([issue #1](https://github.com/yschimke/m3-catalog/issues/1)) kept saying the same thing about
 * them: *the number attributed to the candidate is not the number the sticker's container draws*. A
 * report can only compare one value per component, and on a sticker that renders a whole composed
 * surface — a dialog holding a dial, a toolbar holding icon buttons — the value it picks need not
 * be the container's. That made the table impossible to act on component-by-component, which is why
 * the issue says **not** to bulk-change corner radii from it.
 *
 * This test settles it from the other side. It resolves the shape each sticker actually composes
 * its container from, in a real composition at the sticker's own footprint (a corner can be a
 * percentage, so the size matters), and compares that with the kit's value for the same node. No
 * render, no image diff, no Figma call: the question "does this component draw the kit's corner?"
 * is answered in milliseconds by the build, rather than waiting for the nightly parity run.
 *
 * The kit column comes from the reports on the `design-parity/main` branch — run
 * [31249027692](https://github.com/yschimke/m3-catalog/actions/runs/31249027692), the first with
 * the references pointing at single variant nodes rather than the variant grids that produced the
 * original numbers (issue #2).
 *
 * `direction: "design-led"` is what makes an *unrecorded* mismatch a failure: the kit is
 * authoritative here, so a corner that silently stops matching is this catalog's bug.
 */
class KitCornerRadiusTest {

  /**
   * One mapped component's container corner: what the kit specs, and what the sticker composes.
   *
   * [size] is the sticker's own footprint, because [CornerBasedShape] can express a percentage —
   * resolving one against an arbitrary box would answer a question nobody asked.
   */
  private data class KitCorner(
    val component: String,
    val kitDp: Float,
    val size: Size,
    val shape: Shape,
    /** Non-null when the composed corner is knowingly not the kit's; the value says why. */
    val divergence: String? = null,
  )

  @Test
  fun `every triaged component composes its container from the kit's corner`() = runComposeUiTest {
    val failures = mutableListOf<String>()
    setContent {
      for (corner in kitCorners()) {
        val actual = corner.shape.cornerDp(corner.size, LocalDensity.current)
        val matches = actual == corner.kitDp
        if (corner.divergence == null && !matches) {
          failures +=
            "${corner.component}: kit specs ${corner.kitDp}dp, the sticker composes ${actual}dp"
        }
        if (corner.divergence != null && matches) {
          failures +=
            "${corner.component}: recorded as diverging (${corner.divergence}) but it now " +
              "composes the kit's ${corner.kitDp}dp — drop the record"
        }
      }
    }
    assertTrue(failures.isEmpty(), failures.joinToString(prefix = "\n", separator = "\n"))
  }

  /**
   * The table, built inside a composition because most of these are `@Composable` getters that read
   * the theme.
   */
  @Composable
  private fun kitCorners(): List<KitCorner> = buildList {
    // Both time pickers reported 8 — the time-selector fields, from a sticker that drew the picker
    // without the dialog around it. The dialog the kit measures is `AlertDialogDefaults.shape`.
    add(
      KitCorner(
        component = "TimePicker/Dial",
        kitDp = 28f,
        size = Size(328f, 520f),
        shape = AlertDialogDefaults.shape,
      )
    )
    add(
      KitCorner(
        component = "TimePicker/Input",
        kitDp = 28f,
        size = Size(264f, 243f),
        shape = AlertDialogDefaults.shape,
      )
    )
    // Reported 24 against the kit's 32; the floating toolbar's own container shape is 32.
    add(
      KitCorner(
        component = "Toolbar/HorizontalFloating",
        kitDp = 32f,
        size = Size(168f, 64f),
        shape = FloatingToolbarDefaults.ContainerShape,
      )
    )
    add(
      KitCorner(
        component = "Toolbar/VerticalFloating",
        kitDp = 32f,
        size = Size(64f, 168f),
        shape = FloatingToolbarDefaults.ContainerShape,
      )
    )
  }

  /**
   * The shape's top-start corner in dp at [size], or `0` for a shape with no corners at all —
   * `RectangleShape` is not a [CornerBasedShape].
   */
  private fun Shape.cornerDp(size: Size, density: Density): Float =
    when (this) {
      is CornerBasedShape -> topStart.toPx(size, density) / density.density
      else -> 0f
    }
}
