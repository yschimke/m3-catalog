@file:OptIn(
  androidx.compose.foundation.layout.ExperimentalLayoutApi::class,
  androidx.compose.material3.ExperimentalMaterial3Api::class,
)

package ee.schimke.m3catalog.uibuilder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.carousel.HorizontalUncontainedCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.uibuilder.export.UiBuilderNode
import ee.schimke.composeai.uibuilder.renderer.sdk.CanvasMode
import ee.schimke.composeai.uibuilder.renderer.sdk.CanvasNodeScope
import ee.schimke.composeai.uibuilder.renderer.sdk.UiBuilderModifierPlan
import ee.schimke.composeai.uibuilder.renderer.sdk.alignmentFor
import ee.schimke.composeai.uibuilder.renderer.sdk.canvasAdapterRegistry
import ee.schimke.composeai.uibuilder.renderer.sdk.uiBuilderModifier
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.floatOrNull

/** Foundation donor adapters compiled into this catalog runtime, never interpreted by the host. */
val foundationCanvasAdapters = canvasAdapterRegistry {
  register("layout/box") {
    val canvas = this
    Box(modifier = modifier, contentAlignment = alignmentFor(string("contentAlignment"))) {
      canvas.Items("children") { child -> Content(boxChildModifier(child)) }
    }
  }
  register("layout/column") {
    val canvas = this
    Column(
      modifier = modifier,
      verticalArrangement = verticalArrangement(),
      horizontalAlignment = horizontalAlignment(),
    ) {
      canvas.Items("children") { child -> Content(columnChildModifier(child)) }
    }
  }
  register("layout/row") {
    val canvas = this
    Row(
      modifier = modifier,
      horizontalArrangement = horizontalArrangement(),
      verticalAlignment = verticalAlignment(),
    ) {
      canvas.Items("children") { child -> Content(rowChildModifier(child)) }
    }
  }
  register("layout/lazy-row") { LazyRowAdapter() }
  register("layout/lazy-column") { LazyColumnAdapter() }
  register("layout/lazy-grid") { LazyGridAdapter() }
  register("layout/horizontal-carousel") { HorizontalCarouselAdapter() }
  register("layout/spacer") { Spacer(modifier) }
}

@Composable
private fun CanvasNodeScope.LazyRowAdapter() {
  val canvas = this
  val count = itemCount("items")
  val state = rememberLazyListState()
  registerScrolling(state::dispatchRawDelta) { state.requestScrollToItem(it) }
  if (unrolledHorizontally) {
    Row(
      modifier =
        modifier
          .wrapContentWidth(unbounded = true)
          .padding(objectValue("contentPadding").paddingValues()),
      horizontalArrangement = Arrangement.spacedBy(float("horizontalSpacingDp").dp),
    ) {
      repeat(count) { index -> canvas.Item("items", index) }
    }
    return
  }
  LazyRow(
    modifier = modifier,
    state = state,
    contentPadding = objectValue("contentPadding").paddingValues(),
    horizontalArrangement = Arrangement.spacedBy(float("horizontalSpacingDp").dp),
  ) {
    items(count = count) { index -> canvas.Item("items", index) }
  }
}

@Composable
private fun CanvasNodeScope.LazyColumnAdapter() {
  val canvas = this
  val count = itemCount("items")
  val state = rememberLazyListState()
  registerScrolling(state::dispatchRawDelta) { state.requestScrollToItem(it) }
  if (mode == CanvasMode.AuthoringUnrolled) {
    Column(
      modifier = modifier.padding(objectValue("contentPadding").paddingValues()),
      verticalArrangement = Arrangement.spacedBy(float("verticalSpacingDp").dp),
    ) {
      repeat(count) { index -> canvas.Item("items", index) }
    }
    return
  }
  LazyColumn(
    modifier = modifier,
    state = state,
    contentPadding = objectValue("contentPadding").paddingValues(),
    verticalArrangement = Arrangement.spacedBy(float("verticalSpacingDp").dp),
    reverseLayout = boolean("reverseLayout"),
  ) {
    items(count = count) { index -> canvas.Item("items", index) }
  }
}

@Composable
private fun CanvasNodeScope.LazyGridAdapter() {
  val canvas = this
  val count = itemCount("items")
  val state = rememberLazyGridState()
  registerScrolling(state::dispatchRawDelta) { state.requestScrollToItem(it) }
  val minimum = objectValue("columns").number("minimumCellWidthDp", 362f).coerceAtLeast(1f)
  if (mode == CanvasMode.AuthoringUnrolled) {
    FlowRow(
      modifier = modifier.padding(objectValue("contentPadding").paddingValues()),
      horizontalArrangement = Arrangement.spacedBy(float("horizontalSpacingDp").dp),
      verticalArrangement = Arrangement.spacedBy(float("verticalSpacingDp").dp),
    ) {
      repeat(count) { index -> canvas.Item("items", index, Modifier.width(minimum.dp)) }
    }
    return
  }
  val ids = node.slots["items"].orEmpty()
  val spans = objectValue("itemSpans")
  LazyVerticalGrid(
    columns = GridCells.Adaptive(minimum.dp),
    modifier = modifier,
    state = state,
    contentPadding = objectValue("contentPadding").paddingValues(),
    horizontalArrangement = Arrangement.spacedBy(float("horizontalSpacingDp").dp),
    verticalArrangement = Arrangement.spacedBy(float("verticalSpacingDp").dp),
  ) {
    items(
      count = count,
      span = { index ->
        val span = ids.getOrNull(index)?.let(spans::get)
        if ((span as? JsonPrimitive)?.contentOrNull == "full") GridItemSpan(maxLineSpan)
        else GridItemSpan(1)
      },
    ) { index ->
      canvas.Item("items", index)
    }
  }
}

@Composable
private fun CanvasNodeScope.HorizontalCarouselAdapter() {
  val canvas = this
  val count = itemCount("items")
  val state = rememberCarouselState { count }
  val scope = rememberCoroutineScope()
  registerScrolling(state::dispatchRawDelta) { scope.launch { state.scrollToItem(it) } }
  val itemWidth = float("itemWidthDp", 128f).dp
  if (unrolledHorizontally) {
    Row(
      modifier =
        modifier
          .wrapContentWidth(unbounded = true)
          .padding(start = float("contentPaddingStartDp").dp),
      horizontalArrangement = Arrangement.spacedBy(float("itemSpacingDp").dp),
    ) {
      repeat(count) { index -> canvas.Item("items", index, Modifier.width(itemWidth)) }
    }
    return
  }
  HorizontalUncontainedCarousel(
    state = state,
    itemWidth = itemWidth,
    modifier = modifier,
    itemSpacing = float("itemSpacingDp").dp,
    contentPadding = PaddingValues(start = float("contentPaddingStartDp").dp),
  ) { index ->
    canvas.Item("items", index)
  }
}

private fun CanvasNodeScope.verticalArrangement(): Arrangement.Vertical {
  val spacing = float("verticalSpacingDp").dp
  return when (string("verticalArrangement")) {
    "center" -> Arrangement.spacedBy(spacing, Alignment.CenterVertically)
    "bottom" -> Arrangement.spacedBy(spacing, Alignment.Bottom)
    "spaceBetween" -> Arrangement.SpaceBetween
    "spaceAround" -> Arrangement.SpaceAround
    "spaceEvenly" -> Arrangement.SpaceEvenly
    else -> Arrangement.spacedBy(spacing, Alignment.Top)
  }
}

private fun CanvasNodeScope.horizontalArrangement(): Arrangement.Horizontal {
  val spacing = float("horizontalSpacingDp").dp
  return when (string("horizontalArrangement")) {
    "center" -> Arrangement.spacedBy(spacing, Alignment.CenterHorizontally)
    "end" -> Arrangement.spacedBy(spacing, Alignment.End)
    "spaceBetween" -> Arrangement.SpaceBetween
    "spaceAround" -> Arrangement.SpaceAround
    "spaceEvenly" -> Arrangement.SpaceEvenly
    else -> Arrangement.spacedBy(spacing, Alignment.Start)
  }
}

private fun CanvasNodeScope.horizontalAlignment(): Alignment.Horizontal =
  when (string("horizontalAlignment")) {
    "center" -> Alignment.CenterHorizontally
    "end" -> Alignment.End
    else -> Alignment.Start
  }

private fun CanvasNodeScope.verticalAlignment(): Alignment.Vertical =
  when (string("verticalAlignment")) {
    "top" -> Alignment.Top
    "bottom" -> Alignment.Bottom
    else -> Alignment.CenterVertically
  }

private fun BoxScope.boxChildModifier(node: UiBuilderNode): Modifier {
  var result: Modifier = Modifier
  node.modifierPlans().forEach { plan ->
    when (plan) {
      UiBuilderModifierPlan.MatchParentSize -> result = result.matchParentSize()
      is UiBuilderModifierPlan.Align -> result = result.align(alignmentFor(plan.alignment))
      else -> Unit
    }
  }
  return result
}

private fun ColumnScope.columnChildModifier(node: UiBuilderNode): Modifier {
  var result: Modifier = Modifier
  node.modifierPlans().forEach { plan ->
    when (plan) {
      is UiBuilderModifierPlan.AlignHorizontal ->
        result =
          result.align(
            when (plan.alignment) {
              "centerHorizontally" -> Alignment.CenterHorizontally
              "end" -> Alignment.End
              else -> Alignment.Start
            }
          )
      is UiBuilderModifierPlan.Weight -> result = result.weight(plan.weight, plan.fill ?: true)
      else -> Unit
    }
  }
  return result
}

private fun RowScope.rowChildModifier(node: UiBuilderNode): Modifier {
  var result: Modifier = Modifier
  node.modifierPlans().forEach { plan ->
    when (plan) {
      is UiBuilderModifierPlan.AlignVertical ->
        result =
          result.align(
            when (plan.alignment) {
              "top" -> Alignment.Top
              "bottom" -> Alignment.Bottom
              else -> Alignment.CenterVertically
            }
          )
      is UiBuilderModifierPlan.Weight -> result = result.weight(plan.weight, plan.fill ?: true)
      else -> Unit
    }
  }
  return result
}

private fun UiBuilderNode.modifierPlans(): List<UiBuilderModifierPlan> = modifiers.mapNotNull {
  (it as? JsonObject)?.let(::uiBuilderModifier)
}

private fun CanvasNodeScope.objectValue(name: String): JsonObject =
  ((node.properties[name] as? JsonObject)?.get("value") as? JsonObject) ?: JsonObject(emptyMap())

private fun JsonObject.number(name: String, fallback: Float = 0f): Float =
  (this[name] as? JsonPrimitive)?.floatOrNull ?: fallback

private fun JsonObject.paddingValues(): PaddingValues =
  PaddingValues(
    start = number("startDp").dp,
    top = number("topDp").dp,
    end = number("endDp").dp,
    bottom = number("bottomDp").dp,
  )
