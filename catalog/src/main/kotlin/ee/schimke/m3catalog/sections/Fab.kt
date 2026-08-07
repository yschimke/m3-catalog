@file:CatalogGroup(name = "FAB", section = "Actions")
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.counted

// The floating action button family: one primary action, raised above the content.

@CatalogComponent(id = "Fab/Standard", caption = "The screen's single primary action.")
@CatalogModes
@Composable
fun Fab() = Sticker {
  val c = counted("Edit")
  FloatingActionButton(onClick = c.onClick) {
    Icon(Icons.Filled.Edit, contentDescription = c.label)
  }
}

@CatalogVariant(of = "Fab/Standard", props = ["size=small"], caption = "Compact surfaces.")
@CatalogModes
@Composable
fun FabSmall() = Sticker {
  val c = counted("Edit")
  SmallFloatingActionButton(onClick = c.onClick) {
    Icon(Icons.Filled.Edit, contentDescription = c.label)
  }
}

@CatalogVariant(of = "Fab/Standard", props = ["size=large"], caption = "Expansive surfaces.")
@CatalogModes
@Composable
fun FabLarge() = Sticker {
  val c = counted("Edit")
  LargeFloatingActionButton(onClick = c.onClick) {
    Icon(Icons.Filled.Edit, contentDescription = c.label)
  }
}

@CatalogComponent(id = "Fab/Extended", caption = "FAB with a label; the action is named.")
@CatalogModes
@Composable
fun ExtendedFab() = Sticker {
  val c = counted("Compose")
  ExtendedFloatingActionButton(
    onClick = c.onClick,
    icon = { Icon(Icons.Filled.Edit, contentDescription = null) },
    text = { Text(c.label) },
  )
}
