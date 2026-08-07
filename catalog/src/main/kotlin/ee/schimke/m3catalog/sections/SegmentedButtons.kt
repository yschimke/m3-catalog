@file:CatalogGroup(name = "Segmented buttons", section = "Actions")
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.selectable

// A segmented button owns its selection, so the interactive lane really switches segments.

@CatalogComponent(
  id = "SegmentedButton/SingleChoice",
  caption = "Two to five related options; exactly one selected.",
)
@CatalogModes
@Composable
fun SegmentedButtons() = Sticker {
  val options = listOf("Day", "Week", "Month")
  val (selected, select) = selectable(0)
  SingleChoiceSegmentedButtonRow {
    options.forEachIndexed { index, label ->
      SegmentedButton(
        selected = index == selected,
        onClick = { select(index) },
        shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
      ) {
        Text(label)
      }
    }
  }
}
