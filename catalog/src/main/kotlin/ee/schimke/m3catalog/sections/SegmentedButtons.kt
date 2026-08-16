@file:CatalogGroup(name = "Segmented buttons", section = "Actions")
@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.overrides.previewOverrideString
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogChoice
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.segment_all
import ee.schimke.m3catalog.generated.resources.segment_day
import ee.schimke.m3catalog.generated.resources.segment_month
import ee.schimke.m3catalog.generated.resources.segment_week
import ee.schimke.m3catalog.generated.resources.segment_year
import ee.schimke.m3catalog.multiSelectable
import ee.schimke.m3catalog.selectable
import org.jetbrains.compose.resources.stringResource

// Two axes the kit documents: segment COUNT (2-5) and segment CONTENT (label, icon, or both). Both
// fold onto the one component below.
//
// Selection mode does NOT appear in the kit at all: the `Segmented button` set varies `Segments` x
// `Density` and nothing else, so single- and multi-choice both resolve to the same node — they were
// two components naming one reference, which is one component with a variant. Multi-choice is a
// separate row composable rather than a parameter, and `@CatalogVariant` is how this catalog folds
// that shape under a parent id (the move `Carousel/MultiBrowse` already makes for
// `HorizontalCenteredHeroCarousel`).
//
// The folded render carries no count/content cells of its own. They would resolve to the same
// `Segments=N` values the single-choice cells already compare, and a second set of renders against
// one reference variant adds no comparison — the same reasoning that keeps the vertical slider at
// one size in `Sliders.kt`.

private val LABELS =
  listOf(
    Res.string.segment_day,
    Res.string.segment_week,
    Res.string.segment_month,
    Res.string.segment_year,
    Res.string.segment_all,
  )

@Composable private fun segmentCount(): Int = previewOverrideString("count", "3").toIntOrNull() ?: 3

@Composable
private fun segmentContent(): String =
  catalogChoice("content", "label", "label", "icon", "icon+label")

@Composable
private fun SegmentContent(label: String, checked: Boolean) {
  val content = segmentContent()
  if (content != "label" && checked) {
    Icon(
      Icons.Filled.Check,
      contentDescription = null,
      modifier = Modifier.size(SegmentedButtonDefaults.IconSize),
    )
    if (content == "icon+label") Spacer(Modifier.width(6.dp))
  }
  if (content != "icon") Text(label)
}

@CatalogComponent(
  id = "SegmentedButton/SingleChoice",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/53923:36653",
  caption = "Two to five related options. Count, content and multi-select fold in.",
)
@CatalogModes
@OverrideVariant(name = "count-2", strings = ["count=2"])
@OverrideVariant(name = "count-4", strings = ["count=4"])
@OverrideVariant(name = "count-5", strings = ["count=5"])
@OverrideVariant(name = "icon", strings = ["content=icon"])
@OverrideVariant(name = "icon-label", strings = ["content=icon+label"])
@OverrideVariant(name = "count-5-icon-label", strings = ["count=5", "content=icon+label"])
@Composable
fun SegmentedButtons() = Sticker {
  val count = segmentCount()
  var selected by selectable(0)
  SingleChoiceSegmentedButtonRow(Modifier.width(310.dp).height(40.dp)) {
    LABELS.take(count).forEachIndexed { index, label ->
      SegmentedButton(
        selected = index == selected,
        onClick = { selected = index },
        shape = SegmentedButtonDefaults.itemShape(index = index, count = count),
        modifier = Modifier.weight(1f),
      ) {
        SegmentContent(stringResource(label), index == selected)
      }
    }
  }
}

@CatalogVariant(
  of = "SegmentedButton/SingleChoice",
  props = ["selection=multi"],
  caption = "Any number of the options selected at once.",
)
@CatalogModes
@Composable
fun MultiChoiceSegmentedButtons() = Sticker {
  val count = segmentCount()
  // Every segment carries its own checked state — a fixed pair of booleans pinned the third and
  // later segments to `false` behind a handler that dropped the click, so the `count-4` / `count-5`
  // variants published segments the live lane could not move.
  var checkedSegments by multiSelectable(setOf(0))
  MultiChoiceSegmentedButtonRow(Modifier.width(310.dp).height(40.dp)) {
    LABELS.take(count).forEachIndexed { index, label ->
      val checked = index in checkedSegments
      SegmentedButton(
        checked = checked,
        onCheckedChange = {
          checkedSegments = if (it) checkedSegments + index else checkedSegments - index
        },
        shape = SegmentedButtonDefaults.itemShape(index = index, count = count),
        modifier = Modifier.weight(1f),
      ) {
        SegmentContent(stringResource(label), checked)
      }
    }
  }
}
