@file:CatalogGroup(name = "Top app bar", section = "Navigation")
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.counted

@Composable
private fun NavIcon() {
  val c = counted("Back")
  IconButton(onClick = c.onClick) {
    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = c.label)
  }
}

@Composable
private fun OverflowIcon() {
  val c = counted("More")
  IconButton(onClick = c.onClick) { Icon(Icons.Filled.MoreVert, contentDescription = c.label) }
}

@CatalogComponent(
  id = "TopAppBar/Small",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58114:20565",
  caption = "The default; title beside the actions.",
)
@CatalogModes
@Composable
fun SmallTopAppBar() = Sticker {
  TopAppBar(
    title = { Text("Title") },
    navigationIcon = { NavIcon() },
    actions = { OverflowIcon() },
    modifier = Modifier.width(360.dp),
  )
}

@CatalogVariant(of = "TopAppBar/Small", props = ["size=centered"])
@CatalogModes
@Composable
fun CenterTopAppBar() = Sticker {
  CenterAlignedTopAppBar(
    title = { Text("Title") },
    navigationIcon = { NavIcon() },
    actions = { OverflowIcon() },
    modifier = Modifier.width(360.dp),
  )
}

@CatalogVariant(of = "TopAppBar/Small", props = ["size=medium"])
@CatalogModes
@Composable
fun MediumTopAppBarSticker() = Sticker {
  MediumTopAppBar(
    title = { Text("Title") },
    navigationIcon = { NavIcon() },
    actions = { OverflowIcon() },
    modifier = Modifier.width(360.dp),
  )
}

@CatalogVariant(of = "TopAppBar/Small", props = ["size=large"])
@CatalogModes
@Composable
fun LargeTopAppBarSticker() = Sticker {
  LargeTopAppBar(
    title = { Text("Title") },
    navigationIcon = { NavIcon() },
    actions = { OverflowIcon() },
    modifier = Modifier.width(360.dp),
  )
}
