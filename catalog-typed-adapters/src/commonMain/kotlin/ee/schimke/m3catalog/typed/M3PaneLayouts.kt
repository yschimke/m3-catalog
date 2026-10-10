@file:OptIn(
  androidx.compose.material3.ExperimentalMaterial3Api::class,
  androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi::class,
)

package ee.schimke.m3catalog.typed

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldDefaults
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.PaneExpansionAnchor
import androidx.compose.material3.adaptive.layout.PaneScaffoldScope
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffold
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldDefaults
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldDestinationItem
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldValue
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.calculateThreePaneScaffoldValue
import androidx.compose.material3.adaptive.layout.rememberPaneExpansionState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import ee.schimke.composeai.uibuilder.export.UiBuilderNode
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.floatOrNull

@Composable
internal fun AdaptiveSupportingPaneScaffold(
  unrolled: Boolean,
  node: UiBuilderNode,
  modifier: Modifier,
  mainPane: @Composable (Modifier) -> Unit,
  supportingPane: @Composable (Modifier) -> Unit,
  extraPane: (@Composable (Modifier) -> Unit)? = null,
) {
  val listDetail = node.componentId == "layout/list-detail-pane-scaffold"
  if (unrolled) {
    UnfoldedSupportingPaneScaffold(node, modifier, mainPane, supportingPane, extraPane)
    return
  }
  val mainVisible = node.bool(if (listDetail) "listPaneVisible" else "mainPaneVisible", true)
  val supportingVisible =
    node.bool(if (listDetail) "detailPaneVisible" else "supportingPaneVisible", true)
  // The three widths the catalog declares. Absent is not zero: an unstated width means "whatever
  // the library would have chosen", which is what every design authored before these were read
  // has been getting.
  val mainWidth =
    node.dimension(if (listDetail) "listPanePreferredWidthDp" else "mainPanePreferredWidthDp")
  val supportingWidth =
    node.dimension(
      if (listDetail) "detailPanePreferredWidthDp" else "supportingPanePreferredWidthDp"
    )
  val paneSpacing =
    node.dimension("paneSpacingDp")
      ?: if (node.string("paneSizing") in setOf("fixedStart", "fixedEnd")) 24.dp else null
  val posture = currentWindowAdaptiveInfo().windowPosture
  BoxWithConstraints(modifier) {
    val frameInfo =
      WindowAdaptiveInfo(
        WindowSizeClass.compute(maxWidth.value, maxHeight.value),
        posture,
      )
    val frameDirective = calculatePaneScaffoldDirective(frameInfo)
    val directive =
      (if (node.string("layoutMode") == "singlePane")
          frameDirective.copy(maxHorizontalPartitions = 1)
        else frameDirective)
        // The gap BETWEEN partitions is the directive's, not a pane's, which is why it is the one
        // of the three that is set here rather than on a pane modifier.
        .let {
          if (paneSpacing != null) it.copy(horizontalPartitionSpacerSize = paneSpacing) else it
        }
    val anchor =
      when (node.string("paneSizing")) {
        "fixedStart" ->
          PaneExpansionAnchor.Offset.fromStart(
            node.float("fixedPaneWidthDp", 360f).coerceAtLeast(0f).dp +
              directive.horizontalPartitionSpacerSize / 2
          )
        "fixedEnd" ->
          PaneExpansionAnchor.Offset.fromEnd(
            node.float("fixedPaneWidthDp", 360f).coerceAtLeast(0f).dp +
              directive.horizontalPartitionSpacerSize / 2
          )
        "split" ->
          PaneExpansionAnchor.Proportion(node.float("splitFraction", .5f).coerceIn(.1f, .9f))
        else -> null
      }
    // Key the policy so editing it replaces the remembered initial expansion anchor.
    key(anchor) {
      val expansion =
        rememberPaneExpansionState(
          anchors = listOfNotNull(anchor),
          initialAnchoredIndex = if (anchor == null) -1 else 0,
        )
      if (listDetail) {
        val requestedRole =
          if ("activePaneIndex" in node.properties)
            when (node.float("activePaneIndex").toInt().coerceIn(0, 2)) {
              1 -> ListDetailPaneScaffoldRole.Detail
              2 -> ListDetailPaneScaffoldRole.Extra
              else -> ListDetailPaneScaffoldRole.List
            }
          else
            when (node.string("activePane")) {
              "detail" -> ListDetailPaneScaffoldRole.Detail
              "extra" -> ListDetailPaneScaffoldRole.Extra
              else -> ListDetailPaneScaffoldRole.List
            }
        val available = buildList {
          if (mainVisible) add(ListDetailPaneScaffoldRole.List)
          if (supportingVisible) add(ListDetailPaneScaffoldRole.Detail)
          if (extraPane != null) add(ListDetailPaneScaffoldRole.Extra)
        }
        val role = requestedRole.takeIf { it in available } ?: available.firstOrNull()
        val computed =
          calculateThreePaneScaffoldValue(
            maxHorizontalPartitions = directive.maxHorizontalPartitions,
            adaptStrategies = ListDetailPaneScaffoldDefaults.adaptStrategies(),
            currentDestination = role?.let { ThreePaneScaffoldDestinationItem<Nothing>(it) },
          )
        ListDetailPaneScaffold(
          directive = directive,
          value =
            ThreePaneScaffoldValue(
              primary = if (supportingVisible) computed.primary else PaneAdaptedValue.Hidden,
              secondary = if (mainVisible) computed.secondary else PaneAdaptedValue.Hidden,
              tertiary = if (extraPane != null) computed.tertiary else PaneAdaptedValue.Hidden,
            ),
          listPane = { mainPane(preferredPaneWidth(Modifier, mainWidth).fillMaxSize()) },
          detailPane = {
            supportingPane(preferredPaneWidth(Modifier, supportingWidth).fillMaxSize())
          },
          extraPane =
            extraPane?.let { content ->
              {
                content(
                  preferredPaneWidth(Modifier, node.dimension("extraPanePreferredWidthDp"))
                    .fillMaxSize()
                )
              }
            },
          paneExpansionState = expansion,
          modifier = Modifier.fillMaxSize(),
        )
      } else {
        val computed =
          calculateThreePaneScaffoldValue(
            maxHorizontalPartitions = directive.maxHorizontalPartitions,
            adaptStrategies = SupportingPaneScaffoldDefaults.adaptStrategies(),
            currentDestination =
              if (!mainVisible && supportingVisible)
                ThreePaneScaffoldDestinationItem<Nothing>(SupportingPaneScaffoldRole.Supporting)
              else null,
          )
        SupportingPaneScaffold(
          directive = directive,
          value =
            ThreePaneScaffoldValue(
              primary = if (mainVisible) computed.primary else PaneAdaptedValue.Hidden,
              secondary = if (supportingVisible) computed.secondary else PaneAdaptedValue.Hidden,
              tertiary = PaneAdaptedValue.Hidden,
            ),
          mainPane = { mainPane(preferredPaneWidth(Modifier, mainWidth).fillMaxSize()) },
          supportingPane = {
            supportingPane(preferredPaneWidth(Modifier, supportingWidth).fillMaxSize())
          },
          paneExpansionState = expansion,
          modifier = Modifier.fillMaxSize(),
        )
      }
    }
  }
}

/** [PaneScaffoldScope.preferredWidth] where a width was authored, and nothing where none was. */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
private fun PaneScaffoldScope.preferredPaneWidth(modifier: Modifier, width: Dp?): Modifier =
  if (width == null) modifier else modifier.preferredWidth(width)

/**
 * The unrolled editor's stand-in for [AdaptiveSupportingPaneScaffold]: every declared pane at every
 * width, with `layoutMode` ignored, so no pane becomes unselectable. Widths become weights so the
 * pair fits any canvas.
 */
@Composable
private fun UnfoldedSupportingPaneScaffold(
  node: UiBuilderNode,
  modifier: Modifier,
  mainPane: @Composable (Modifier) -> Unit,
  supportingPane: @Composable (Modifier) -> Unit,
  extraPane: (@Composable (Modifier) -> Unit)?,
) {
  val listDetail = node.componentId == "layout/list-detail-pane-scaffold"
  val mainVisible = node.bool(if (listDetail) "listPaneVisible" else "mainPaneVisible", true)
  val supportingVisible =
    node.bool(if (listDetail) "detailPaneVisible" else "supportingPaneVisible", true)
  val mainWidth =
    node
      .float(if (listDetail) "listPanePreferredWidthDp" else "mainPanePreferredWidthDp", 744f)
      .coerceAtLeast(1f)
  val supportWidth =
    node
      .float(
        if (listDetail) "detailPanePreferredWidthDp" else "supportingPanePreferredWidthDp",
        512f,
      )
      .coerceAtLeast(1f)
  val spacing = node.float("paneSpacingDp").coerceAtLeast(0f)
  Row(modifier) {
    if (mainVisible) mainPane(Modifier.weight(mainWidth).fillMaxSize())
    if (mainVisible && supportingVisible) Spacer(Modifier.width(spacing.dp))
    if (supportingVisible) supportingPane(Modifier.weight(supportWidth).fillMaxSize())
    if (extraPane != null && (mainVisible || supportingVisible)) Spacer(Modifier.width(spacing.dp))
    extraPane?.invoke(
      Modifier.weight(node.float("extraPanePreferredWidthDp", 360f).coerceAtLeast(1f)).fillMaxSize()
    )
    if (!mainVisible && !supportingVisible) Box(Modifier.fillMaxSize())
  }
}

private fun UiBuilderNode.scalar(name: String) =
  ((properties[name] as? JsonObject)?.get("value") as? JsonPrimitive)

private fun UiBuilderNode.bool(name: String, fallback: Boolean = false) =
  scalar(name)?.booleanOrNull ?: fallback

private fun UiBuilderNode.string(name: String) = scalar(name)?.contentOrNull ?: ""

private fun UiBuilderNode.float(name: String, fallback: Float = 0f) =
  scalar(name)?.floatOrNull ?: fallback

private fun UiBuilderNode.dimension(name: String): Dp? = scalar(name)?.floatOrNull?.dp
