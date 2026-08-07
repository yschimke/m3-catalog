@file:CatalogGroup(name = "Divider", section = "Containment")
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker

// A divider is a static separator: no state, nothing to click, so it ships no handler.

@CatalogComponent(id = "Divider/Horizontal", caption = "Separates content in a vertical list.")
@CatalogModes
@Composable
fun HorizontalDividerSticker() = Sticker {
  Column(Modifier.width(280.dp)) {
    Text("Above", Modifier.padding(vertical = 8.dp))
    HorizontalDivider()
    Text("Below", Modifier.padding(vertical = 8.dp))
  }
}

@CatalogVariant(of = "Divider/Horizontal", props = ["orientation=vertical"])
@CatalogModes
@Composable
fun VerticalDividerSticker() = Sticker {
  Row(Modifier.height(48.dp)) {
    Text("Left", Modifier.padding(horizontal = 8.dp))
    VerticalDivider()
    Text("Right", Modifier.padding(horizontal = 8.dp))
  }
}
