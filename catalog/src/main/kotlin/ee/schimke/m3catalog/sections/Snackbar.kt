@file:CatalogGroup(name = "Snackbar", section = "Communication")

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.overrides.previewOverrideBoolean
import ee.schimke.composeai.preview.CaptureGutter
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModesSnackbar
import ee.schimke.m3catalog.KitShadowGutter
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogChoice
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
//
// Because the line count is the message's doing, `snackbar_long` has to be sized for the NARROWEST
// of the cells it appears in — wide enough to wrap at the full 344dp bar, short enough to still fit
// two lines once the action and close slots have taken their share. It used to be longer than that,
// and got away with it only because the broken measurement below handed the message the whole bar
// (#177); with the slots measured correctly the same copy spilled to a third line and the cell grew
// past the kit's 68dp.

@CatalogComponent(
  id = "Snackbar/Message",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/53977:33611",
  caption = "Brief message about a process. Two-line, action and close affordance fold in.",
)
@CatalogModesSnackbar
// The bar's shadow falls outside its bounds; the room for it belongs to the capture, so the
// component still measures the kit's 344dp bar and the canvas carries the gutter (#179).
@CaptureGutter(
  start = KitShadowGutter.Level3Side,
  top = KitShadowGutter.Level3Top,
  end = KitShadowGutter.Level3Side,
  bottom = KitShadowGutter.Level3Bottom,
)
@OverrideVariant(name = "two-line", strings = ["lines=two"])
@OverrideVariant(name = "action", strings = ["configuration=text+action"])
@OverrideVariant(name = "close", booleans = ["close=true"])
@OverrideVariant(
  name = "action-close",
  booleans = ["close=true"],
  strings = ["configuration=text+action"],
)
@OverrideVariant(
  name = "two-line-action-close",
  strings = ["lines=two", "configuration=text+action"],
  booleans = ["close=true"],
)
@Composable
fun SnackbarMessage() = Sticker {
  val undo = counted(stringResource(Res.string.action_undo))
  val dismiss = counted(stringResource(Res.string.action_dismiss))
  val long = catalogChoice("lines", "one", "one" to "One line", "two" to "Two lines") == "two"
  // The kit's bar width belongs to the FRAME, not to the `Snackbar` (#177). `OneRowSnackbar`
  // measures the action and dismiss slots with the constraints it was handed and then coerces the
  // message's width up to `constraints.minWidth`, so a tight `Modifier.width(344.dp)` measured both
  // slots at the full bar width: the message was laid out across them, the ✕ landed at x = 0 on top
  // of the first word, and the action was placed at a negative x — off the bar entirely, which is
  // why `action-close` published the same bytes as `close`. A `Box` gives the same 344dp with a
  // loose minimum, which is what the component needs in order to divide it.
  //
  // The height is the component's own, for the same reason `ButtonFrame` keeps the frame off the
  // button: `SnackbarTokens.SingleLineContainerHeight` is already 48dp, and two lines of
  // body-medium plus `SnackbarVerticalPadding` either side come to exactly the kit's 68dp. Pinning
  // it only ever took away the layout choice the component makes when the slots do not fit on one
  // line.
  Box(Modifier.width(344.dp)) {
    Snackbar(
      action =
        if (
          catalogChoice("configuration", "text", "text", "text+action").startsWith("text+action")
        ) {
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
}
