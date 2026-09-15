@file:CatalogGroup(name = "Lazy list", section = "Containment")

package ee.schimke.m3catalog.glimmer

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.xr.glimmer.Icon
import androidx.xr.glimmer.ListItem
import androidx.xr.glimmer.Text
import androidx.xr.glimmer.TitleChip
import androidx.xr.glimmer.list.GlimmerLazyColumn
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant

// The rows are copy rather than sample data — they are words a reader reads — so they resolve from
// resources like everything else this module draws. Held as id lists so the row count stays one
// number rather than three.
private val GroceryLabels =
  listOf(R.string.item_milk, R.string.item_bread, R.string.item_spinach, R.string.item_cereal)
private val MeetingLabels =
  listOf(
    R.string.meeting_design_check_in,
    R.string.meeting_one_to_one,
    R.string.meeting_lunch,
    R.string.meeting_review,
  )
private val MeetingTimes =
  listOf(
    R.string.time_early_morning,
    R.string.time_late_morning,
    R.string.time_midday,
    R.string.time_afternoon,
  )

@CatalogComponent(
  id = "GlimmerLazyColumn",
  // `List index=Top, Title=False` in the kit's `1-line list` set (`4116:5211`).
  reference = "figma:HKfLClZDLRyMhf4IQQLna8/4116:5212",
  caption = "A vertically scrolling Glimmer list with edge scrims and focus-aware snapping.",
)
@Preview
@Composable
fun GlimmerLazyColumnSticker() = Sticker {
  GlimmerLazyColumn(modifier = Modifier.width(KitContentWidth).height(364.dp)) {
    items(count = GroceryLabels.size) { index -> GroceryListItem(index) }
  }
}

@CatalogVariant(
  of = "GlimmerLazyColumn",
  props = ["Title=True"],
  caption = "Keeps a title chip above the scrolling items.",
)
@Preview
@Composable
fun GlimmerLazyColumnWithTitleSticker() = Sticker {
  GlimmerLazyColumn(
    title = { TitleChip { Text(stringResource(R.string.list_title_ingredients)) } },
    modifier = Modifier.width(KitContentWidth).height(364.dp),
  ) {
    items(count = GroceryLabels.size) { index -> GroceryListItem(index) }
  }
}

@CatalogComponent(
  id = "GlimmerLazyColumn/TwoLine",
  // The kit makes its item-content choice a separate set. This remains the same named Compose API,
  // with the supporting-label slot on each real ListItem supplying the second line.
  reference = "figma:HKfLClZDLRyMhf4IQQLna8/4116:5251",
  caption = "The lazy column populated with two-line list items.",
)
@Preview
@Composable
fun GlimmerLazyColumnTwoLineSticker() = Sticker {
  GlimmerLazyColumn(modifier = Modifier.width(KitContentWidth).height(364.dp)) {
    items(count = MeetingLabels.size) { index ->
      val c = counted(stringResource(MeetingLabels[index]))
      ListItem(
        onClick = c.onClick,
        supportingLabel = { Text(stringResource(MeetingTimes[index])) },
        leadingIcon = { Icon(Icons.AutoMirrored.Rounded.Send, stringResource(R.string.cd_send)) },
      ) {
        Text(c.label)
      }
    }
  }
}

@Composable
private fun GroceryListItem(index: Int) {
  val c = counted(stringResource(GroceryLabels[index]))
  ListItem(
    onClick = c.onClick,
    leadingIcon = { Icon(Icons.AutoMirrored.Rounded.Send, stringResource(R.string.cd_send)) },
  ) {
    Text(c.label)
  }
}
