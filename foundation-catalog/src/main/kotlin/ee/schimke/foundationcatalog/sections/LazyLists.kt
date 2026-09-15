@file:CatalogGroup(name = "Lazy layout", section = "Layout")

package ee.schimke.foundationcatalog.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.foundationcatalog.Child
import ee.schimke.foundationcatalog.FoundationModes
import ee.schimke.foundationcatalog.Sticker

// The scrolling containers. Each sticker is deliberately SMALLER than its content so the picture
// shows a list that scrolls rather than a column that happens to be lazy — the one thing that
// distinguishes these from the primitives beside them.
//
// Like the primitives, they carry no `@BuilderComponent`: their builder policy is a builtin in
// `ui-builder.policy.json`, because discovery's record does not reach foundation symbols. See
// Layout.kt.

@CatalogComponent(
  id = "Layout/LazyColumn",
  noReference = "Not a kit component — Compose's own vertically scrolling list.",
  caption = "Composes only the items in view; scrolls vertically.",
)
@FoundationModes
@Composable
fun LazyColumnSticker() = Sticker {
  LazyColumn(
    modifier = Modifier.size(width = 120.dp, height = 132.dp),
    contentPadding = PaddingValues(8.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    items(5) { Child(width = 104.dp, height = 28.dp) }
  }
}

@CatalogComponent(
  id = "Layout/LazyRow",
  noReference = "Not a kit component — Compose's own horizontally scrolling list.",
  caption = "Composes only the items in view; scrolls horizontally.",
)
@FoundationModes
@Composable
fun LazyRowSticker() = Sticker {
  LazyRow(
    modifier = Modifier.width(140.dp).height(64.dp),
    contentPadding = PaddingValues(8.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    items(5) { Child(width = 48.dp, height = 48.dp) }
  }
}

@CatalogComponent(
  id = "Layout/LazyVerticalGrid",
  noReference = "Not a kit component — Compose's own adaptive grid.",
  caption = "Wraps items into as many columns as the width allows.",
)
@FoundationModes
@Composable
fun LazyVerticalGridSticker() = Sticker {
  LazyVerticalGrid(
    columns = GridCells.Adaptive(minSize = 56.dp),
    modifier = Modifier.size(width = 200.dp, height = 132.dp),
    contentPadding = PaddingValues(8.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    items(9) { Child(width = 56.dp, height = 36.dp) }
  }
}
