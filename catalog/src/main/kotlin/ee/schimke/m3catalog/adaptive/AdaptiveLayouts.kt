@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package ee.schimke.m3catalog.adaptive

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.Posture
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffold
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirectiveWithTwoPanesOnMediumWidth
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItem
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import ee.schimke.m3catalog.CatalogFilledStars
import ee.schimke.m3catalog.CatalogOutlinedStars
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogText
import ee.schimke.m3catalog.counted
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.action_more
import ee.schimke.m3catalog.generated.resources.card_supporting
import ee.schimke.m3catalog.generated.resources.card_title
import ee.schimke.m3catalog.generated.resources.list_item
import ee.schimke.m3catalog.generated.resources.list_supporting
import ee.schimke.m3catalog.generated.resources.nav_home
import ee.schimke.m3catalog.generated.resources.nav_saved
import ee.schimke.m3catalog.generated.resources.nav_search
import ee.schimke.m3catalog.generated.resources.nav_you
import ee.schimke.m3catalog.generated.resources.sheet_details
import ee.schimke.m3catalog.generated.resources.sheet_supporting
import ee.schimke.m3catalog.selectable
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

// The Compose Multiplatform **adaptive layout** surfaces — `NavigationSuiteScaffold`,
// `ListDetailPaneScaffold` and `SupportingPaneScaffold`.
//
// These are deliberately NOT `@CatalogComponent`s, and this file deliberately does not live under
// `sections/`. The Material 3 Design Kit publishes no node for any of them — no adaptive layout, no
// pane scaffold, no navigation suite — and `AGENTS.md` makes an exact, renderable kit reference a
// condition of entering the component inventory, with no `noReference` escape hatch. A sticker with
// nothing to compare against is a picture this catalog cannot defend, so these publish as plain
// `@Preview`s instead: rendered, browsable and live on the preview server, absent from the design
// map and from every parity comparison.
//
// What they are for is the axis the sticker sheet has nowhere else to show. Every component in the
// inventory is a picture of one thing at one size; an adaptive layout IS the relationship between
// the window and the layout, so each of these renders at all three widths `catalog.spec.json`
// documents (`@CatalogBreakpoints`) and the three renders together are the component.

private val DESTINATIONS =
  listOf(Res.string.nav_home, Res.string.nav_search, Res.string.nav_you, Res.string.nav_saved)

private const val LIST_ITEMS = 5

/**
 * The sticker frame for an adaptive layout, with the [WindowAdaptiveInfo] these layouts adapt to
 * **measured from the preview's own frame**.
 *
 * The library's own default is `currentWindowAdaptiveInfo()`, which reads the host window. That is
 * right in an app and wrong here twice over: the renderer draws into an offscreen scene rather than
 * a window, so there is no size to read that has anything to do with the `widthDp` the preview
 * asked for — and whatever it did read would be a property of the machine doing the rendering,
 * which is the definition of a nondeterministic capture. Measuring the frame instead makes
 * `widthDp` the one input, so a compact render is compact on any host and the three breakpoint
 * captures differ because the frame differs and for no other reason.
 *
 * The composables underneath are the real ones: the size class is Material's own `WindowSizeClass`,
 * and the layout decision is still `calculatePaneScaffoldDirective` /
 * `NavigationSuiteScaffoldDefaults.navigationSuiteType` making it.
 */
@Composable
private fun AdaptiveSticker(content: @Composable (WindowAdaptiveInfo) -> Unit) = Sticker {
  BoxWithConstraints(Modifier.fillMaxSize()) {
    val width = maxWidth
    val height = maxHeight
    val info =
      remember(width, height) {
        WindowAdaptiveInfo(WindowSizeClass.compute(width.value, height.value), Posture())
      }
    content(info)
  }
}

/** A pane's body: a real `Card`, so a pane reads as a surface rather than as bare text. */
@Composable
private fun Pane(content: @Composable ColumnScope.() -> Unit) {
  Card(Modifier.fillMaxSize().padding(8.dp)) { Column(Modifier.padding(16.dp), content = content) }
}

/**
 * The pane scaffolds' layout policy, as a parameter knob.
 *
 * Material's default splits into two panes at the **expanded** width and keeps one pane at medium,
 * which is why the medium render below shows a single pane and is not a bug. The library publishes
 * the other policy as a function of its own, and it is the interesting half of this component: a
 * layout that goes two-up at medium is a different product decision, not a different value in a
 * config.
 *
 * It stays a live-lane knob rather than an `@OverrideVariant` cell, and that is a deliberate cost.
 * A variant cell fans out across the whole `@CatalogBreakpoints` multipreview, and the policy only
 * changes a pixel at ONE of the three widths — compact is single-pane under either and expanded is
 * two-pane under either — so baking it would publish eight renders byte-identical to their defaults
 * to gain four that differ. `AGENTS.md` calls that out as the thing a variant cell must not be.
 *
 * Each scaffold declares it on its own signature — the parameter format — so the value arrives by
 * ordinary argument passing and this helper reads no override harness at all. Which knobs use which
 * format, and why the rest are waiting on a compose-ai-tools release, is in
 * `docs/PARAMETER_KNOBS.md`.
 */
@Composable
private fun paneDirective(info: WindowAdaptiveInfo, twoPanesOnMedium: Boolean) =
  if (twoPanesOnMedium) calculatePaneScaffoldDirectiveWithTwoPanesOnMediumWidth(info)
  else calculatePaneScaffoldDirective(info)

/**
 * `NavigationSuiteScaffold` — one declaration of a set of destinations, drawn as the navigation
 * component the window has room for: a short navigation bar when it is compact, a collapsed wide
 * rail when it is not. The pane names the type the scaffold resolved, which is an API identifier
 * rather than copy and so is deliberately not a string resource.
 *
 * Medium and expanded both resolve to a collapsed wide rail: Material's recommendation is
 * two-valued above compact, so the two renders differ in how much room the content pane gets and
 * not in which navigation component is drawn. That is the API's answer rather than a gap in the
 * capture, and it is worth being able to see.
 *
 * `navigationSuiteType` is passed to the items as well as to the scaffold: each item styles itself
 * for the type it is told about, and left to its own default it would style for the *host* window
 * while sitting inside a rail the scaffold chose from the frame.
 */
@CatalogBreakpoints
@Composable
fun NavigationSuiteScaffoldSticker() = AdaptiveSticker { info ->
  val type = NavigationSuiteScaffoldDefaults.navigationSuiteType(info)
  var selected by selectable(0)
  NavigationSuiteScaffold(
    navigationItems = {
      DESTINATIONS.forEachIndexed { index, label ->
        NavigationSuiteItem(
          navigationSuiteType = type,
          selected = index == selected,
          onClick = { selected = index },
          icon = {
            Icon(
              if (index == selected) CatalogFilledStars else CatalogOutlinedStars,
              contentDescription = null,
            )
          },
          label = { Text(catalogText("label", stringResource(label), index)) },
        )
      }
    },
    navigationSuiteType = type,
    containerColor = Color.Transparent,
  ) {
    Pane { Text(type.toString(), style = MaterialTheme.typography.titleMedium) }
  }
}

/**
 * `ListDetailPaneScaffold` — a list beside its detail where there is room, and one pane at a time
 * where there is not. Compact shows the list alone and the detail is a destination the navigator
 * moves to; medium and expanded show both.
 *
 * The scaffold is driven by `rememberListDetailPaneScaffoldNavigator` rather than by a hand-built
 * `ThreePaneScaffoldValue`, so the render is what the real navigation state produces — including on
 * the live lane, where selecting a row navigates and the compact render swaps the list for the
 * detail.
 */
@CatalogBreakpoints
@Composable
fun ListDetailPaneScaffoldSticker(twoPanesOnMedium: Boolean = false) = AdaptiveSticker { info ->
  val navigator =
    rememberListDetailPaneScaffoldNavigator<Int>(
      scaffoldDirective = paneDirective(info, twoPanesOnMedium)
    )
  val scope = rememberCoroutineScope()
  var selected by selectable(0)
  ListDetailPaneScaffold(
    directive = navigator.scaffoldDirective,
    value = navigator.scaffoldValue,
    listPane = {
      AnimatedPane {
        Pane {
          repeat(LIST_ITEMS) { index ->
            ListItem(
              colors = ListItemDefaults.colors(containerColor = Color.Transparent),
              headlineContent = {
                Text(catalogText("item", stringResource(Res.string.list_item), index))
              },
              supportingContent = { Text(stringResource(Res.string.list_supporting)) },
              modifier =
                Modifier.clickable {
                  selected = index
                  scope.launch {
                    navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, contentKey = index)
                  }
                },
            )
          }
        }
      }
    },
    detailPane = {
      AnimatedPane {
        Pane {
          Text(
            catalogText("item", stringResource(Res.string.list_item), selected),
            style = MaterialTheme.typography.titleMedium,
          )
          Text(stringResource(Res.string.card_supporting))
        }
      }
    },
  )
}

/**
 * `SupportingPaneScaffold` — a main pane with secondary content beside it when the window is wide
 * enough, and behind a navigation step when it is not. The compact render is the main pane alone,
 * which is the state the button in it exists to leave.
 */
@CatalogBreakpoints
@Composable
fun SupportingPaneScaffoldSticker(twoPanesOnMedium: Boolean = false) = AdaptiveSticker { info ->
  val navigator =
    rememberSupportingPaneScaffoldNavigator<Unit>(
      scaffoldDirective = paneDirective(info, twoPanesOnMedium)
    )
  val scope = rememberCoroutineScope()
  val more = counted(stringResource(Res.string.action_more))
  SupportingPaneScaffold(
    directive = navigator.scaffoldDirective,
    value = navigator.scaffoldValue,
    mainPane = {
      AnimatedPane {
        Pane {
          Text(
            stringResource(Res.string.card_title),
            style = MaterialTheme.typography.titleMedium,
          )
          Text(stringResource(Res.string.card_supporting))
          Button(
            onClick = {
              more.onClick()
              scope.launch { navigator.navigateTo(SupportingPaneScaffoldRole.Supporting) }
            },
            modifier = Modifier.padding(top = 16.dp),
          ) {
            Text(more.label)
          }
        }
      }
    },
    supportingPane = {
      AnimatedPane {
        Pane {
          Text(
            stringResource(Res.string.sheet_details),
            style = MaterialTheme.typography.titleMedium,
          )
          Text(stringResource(Res.string.sheet_supporting))
        }
      }
    },
  )
}
