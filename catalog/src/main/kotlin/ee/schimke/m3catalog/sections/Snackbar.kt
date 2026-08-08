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
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.action_dismiss
import ee.schimke.m3catalog.generated.resources.action_undo
import ee.schimke.m3catalog.generated.resources.snackbar_long
import ee.schimke.m3catalog.generated.resources.snackbar_short
import org.jetbrains.compose.resources.stringResource

// Snackbars are composed directly rather than through a `SnackbarHost`: the host is a dispatcher
// with nothing of its own to draw, and a sticker must show the surface itself.
//
// Three axes the kit documents, all foldable onto one preview: line count (the message length
// decides it), the action, and the close affordance.

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
  val undo = counted(stringResource(Res.string.action_undo))
  val dismiss = counted(stringResource(Res.string.action_dismiss))
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
    Text(stringResource(if (long) Res.string.snackbar_long else Res.string.snackbar_short))
  }
}
