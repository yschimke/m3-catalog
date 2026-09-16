package ee.schimke.m3catalog

import androidx.compose.runtime.Composable
import ee.schimke.composeai.overrides.previewOverrideBoolean

/**
 * A difference between the kit and the component that this catalog draws over, and its tracking
 * bug.
 *
 * ### What belongs here, and what does not
 *
 * Most parity findings are the sticker's own to fix: a `contentPadding`, an item width, a colour, a
 * slot's content — anything the app *passes* is authoring, and getting it wrong is this repo's bug.
 * Those are not workarounds; they are just corrections, and they leave nothing behind.
 *
 * A second kind is upstream's: the component declares a gap it never applies
 * ([#455](https://github.com/yschimke/m3-catalog/issues/455),
 * [FloatingToolbarTokens.ContainerBetweenSpace][ee.schimke.m3catalog.sections.HorizontalFloatingToolbarSticker]),
 * or sizes something the kit sizes differently with no parameter to say otherwise. The rule there
 * is to publish what the component produces and record the difference as an issue, so the parity
 * diff keeps showing it. Padding around such a component publishes a picture the library does not
 * draw and hides the break from the one thing that was watching for it.
 *
 * This enum is the third kind, and it is meant to stay small: a difference of the second kind where
 * publishing the component's own output is *also* wrong, because the sticker is then not showing
 * the component in the arrangement the kit documents at all. Each entry names the tracking bug for
 * the upstream fix, so the cover-up is greppable, dated, and removable the moment the issue closes.
 *
 * @property issue the tracking bug in this repository.
 * @property summary what is covered up, in one line.
 */
enum class FigmaWorkaround(val issue: Int, val summary: String) {
  /**
   * `NavigationRail` supplies neither of the header insets its wide sibling does, so the standard
   * rail's destinations start where the header ends. See
   * [#458](https://github.com/yschimke/m3-catalog/issues/458).
   */
  NavigationRailHeaderInsets(
    issue = 458,
    summary =
      "NavigationRail leaves the kit's 44dp header inset and 40dp header-to-destination " +
        "gap to the caller; WideNavigationRail applies both itself",
  )
}

/**
 * Whether the stickers draw over the differences registered in [FigmaWorkaround].
 *
 * On by default, because the published sheet is read as "what this component looks like", and a
 * sticker missing the arrangement the kit documents answers that badly. Off is the honest render:
 * every entry above disappears and the sticker shows exactly what the component produces unaided.
 *
 * It is a preview override rather than a constant so that either answer is one render away — pass
 * `figmaWorkarounds=false` to see the raw component, with no edit and no rebuild. That is how the
 * measurements in each entry's issue were taken.
 */
@Composable fun figmaWorkarounds(): Boolean = previewOverrideBoolean("figmaWorkarounds", true)

/**
 * [workaround] while [figmaWorkarounds] is on, [upstream] when it is off.
 *
 * Both values are spelled out at the call site on purpose: what the component does without help is
 * as much a part of the record as the value that hides it, and a reader comparing the two can see
 * the size of the difference without rendering anything.
 */
@Composable
fun <T> figmaWorkaround(
  @Suppress("UNUSED_PARAMETER") workaroundFor: FigmaWorkaround,
  upstream: T,
  workaround: T,
): T = if (figmaWorkarounds()) workaround else upstream
