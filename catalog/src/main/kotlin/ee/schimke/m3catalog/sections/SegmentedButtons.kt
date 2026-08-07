@file:CatalogGroup(name = "Segmented buttons", section = "Actions")
@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.overrides.previewOverrideString
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.selectable
import ee.schimke.m3catalog.toggleable

// Two axes the kit documents: segment COUNT (2-5) and segment CONTENT (label, icon, or both). The
// selection mode is the third, but single- and multi-choice are separate row composables rather
// than a parameter, so they are separate components rather than knob cells.

private val LABELS = listOf("Day", "Week", "Month", "Year", "All")

@Composable private fun segmentCount(): Int = previewOverrideString("count", "3").toIntOrNull() ?: 3

@Composable private fun segmentContent(): String = previewOverrideString("content", "label")

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
  reference = "figma:ocdacdEsnHipMJD3egzxKb/53923:36615",
  caption = "Two to five related options; exactly one selected. Count and content fold in.",
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
  val (selected, select) = selectable(0)
  SingleChoiceSegmentedButtonRow {
    LABELS.take(count).forEachIndexed { index, label ->
      SegmentedButton(
        selected = index == selected,
        onClick = { select(index) },
        shape = SegmentedButtonDefaults.itemShape(index = index, count = count),
      ) {
        SegmentContent(label, index == selected)
      }
    }
  }
}

@CatalogComponent(
  id = "SegmentedButton/MultiChoice",
  caption = "Any number of the options selected at once. Count and content fold in.",
)
@CatalogModes
@OverrideVariant(name = "count-2", strings = ["count=2"])
@OverrideVariant(name = "count-4", strings = ["count=4"])
@OverrideVariant(name = "count-5", strings = ["count=5"])
@OverrideVariant(name = "icon", strings = ["content=icon"])
@OverrideVariant(name = "icon-label", strings = ["content=icon+label"])
@Composable
fun MultiChoiceSegmentedButtons() = Sticker {
  val count = segmentCount()
  val (first, setFirst) = toggleable(true)
  val (second, setSecond) = toggleable(false)
  MultiChoiceSegmentedButtonRow {
    LABELS.take(count).forEachIndexed { index, label ->
      val checked = if (index == 0) first else if (index == 1) second else false
      SegmentedButton(
        checked = checked,
        onCheckedChange = { if (index == 0) setFirst(it) else if (index == 1) setSecond(it) },
        shape = SegmentedButtonDefaults.itemShape(index = index, count = count),
      ) {
        SegmentContent(label, checked)
      }
    }
  }
}
