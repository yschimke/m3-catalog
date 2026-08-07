@file:CatalogGroup(name = "Snackbar", section = "Communication")
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.counted

// Snackbars are composed directly rather than through a `SnackbarHost`: the host is a dispatcher
// with nothing of its own to draw, and a sticker must show the surface itself.

@CatalogComponent(id = "Snackbar/Message", caption = "Brief message about a process.")
@CatalogModes
@Composable
fun SnackbarMessage() = Sticker { Snackbar { Text("Message sent") } }

@CatalogVariant(
  of = "Snackbar/Message",
  props = ["content=action"],
  caption = "With a single, non-dismiss action.",
)
@CatalogModes
@Composable
fun SnackbarWithAction() = Sticker {
  val c = counted("Undo")
  Snackbar(action = { TextButton(onClick = c.onClick) { Text(c.label) } }) { Text("Message sent") }
}
