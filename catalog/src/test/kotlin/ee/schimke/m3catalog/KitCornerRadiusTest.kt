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
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.TooltipDefaults
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
 * Two rows carry a [KitCorner.divergence] — `Menu/Dropdown` (#85) and `Search/ExpandedFullScreen`
 * (#86). Those are the corners that really do differ, and the catalog leaves them differing: both
 * are Compose Material 3's own value, and hard-coding the kit's number would draw a container the
 * component never draws while hiding the divergence from every future parity run. The break stays
 * visible in the report and is filed upstream instead. Pinning them here is what keeps those
 * records honest — when a Material release closes a gap, this test fails asking for the record to
 * go, instead of the divergence quietly outliving its explanation.
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
    // Both tooltips report a candidate corner of 20 — the anchor button the sticker used to draw
    // around them, not the tooltip surface. The surfaces themselves are the kit's 4 and 12.
    add(
      KitCorner(
        component = "Tooltip/Plain",
        kitDp = 4f,
        size = Size(104f, 24f),
        shape = TooltipDefaults.plainTooltipContainerShape,
      )
    )
    add(
      KitCorner(
        component = "Tooltip/Rich",
        kitDp = 12f,
        size = Size(312f, 136f),
        shape = TooltipDefaults.richTooltipContainerShape,
      )
    )
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
    // A real divergence, and the only one in the table that is about a container the catalog could
    // trivially hard-code. It doesn't: `MenuDefaults.shape` stays, so parity keeps reporting it.
    add(
      KitCorner(
        component = "Menu/Dropdown",
        kitDp = 16f,
        size = Size(208f, 292f),
        shape = MenuDefaults.shape,
        divergence = "MenuDefaults.shape is 4dp — #85",
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
    // Also real. `SearchBarDefaults.fullScreenShape` is `RectangleShape`: a full-screen search view
    // has no corners in Compose, where the kit's node specs 16.
    add(
      KitCorner(
        component = "Search/ExpandedFullScreen",
        kitDp = 16f,
        size = Size(412f, 250f),
        shape = SearchBarDefaults.fullScreenShape,
        divergence = "SearchBarDefaults.fullScreenShape is RectangleShape — #86",
      )
    )
  }

  /**
   * The shape's top-start corner in dp at [size], or `0` for a shape with no corners at all —
   * `RectangleShape` is not a [CornerBasedShape], and reporting it as "no corner" is exactly what
   * the `Search/ExpandedFullScreen` row records.
   */
  private fun Shape.cornerDp(size: Size, density: Density): Float =
    when (this) {
      is CornerBasedShape -> topStart.toPx(size, density) / density.density
      else -> 0f
    }
}
