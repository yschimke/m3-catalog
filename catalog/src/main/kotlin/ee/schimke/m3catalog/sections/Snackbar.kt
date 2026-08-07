@file:CatalogGroup(name = "Snackbar", section = "Communication")

package ee.schimke.m3catalog.sections

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import ee.schimke.composeai.overrides.previewOverrideBoolean
import ee.schimke.composeai.overrides.previewOverrideString
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.counted

// Snackbars are composed directly rather than through a `SnackbarHost`: the host is a dispatcher
// with nothing of its own to draw, and a sticker must show the surface itself.
//
// Three axes the kit documents, all foldable onto one preview: line count (the message length
// decides it), the action, and the close affordance.

private const val LONG =
  "Your changes were saved and will sync to your other devices the next time they connect."

@CatalogComponent(
  id = "Snackbar/Message",
  caption = "Brief message about a process. Two-line, action and close affordance fold in.",
)
@CatalogModes
@OverrideVariant(name = "two-line", strings = ["message=long"])
@OverrideVariant(name = "action", booleans = ["action=true"])
@OverrideVariant(name = "close", booleans = ["close=true"])
@OverrideVariant(name = "action-close", booleans = ["action=true", "close=true"])
@OverrideVariant(
  name = "two-line-action-close",
  strings = ["message=long"],
  booleans = ["action=true", "close=true"],
)
@Composable
fun SnackbarMessage() = Sticker {
  val undo = counted("Undo")
  val dismiss = counted("Dismiss")
  val long = previewOverrideString("message", "short") == "long"
  Snackbar(
    action =
      if (previewOverrideBoolean("action", false)) {
        { TextButton(onClick = undo.onClick) { Text(undo.label) } }
      } else null,
    dismissAction =
      if (previewOverrideBoolean("close", false)) {
        {
          IconButton(onClick = dismiss.onClick) {
            Icon(Icons.Filled.Close, contentDescription = dismiss.label)
          }
        }
      } else null,
  ) {
    Text(if (long) LONG else "Message sent")
  }
}
