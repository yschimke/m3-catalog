@file:CatalogGroup(name = "Snackbar", section = "Communication")

package ee.schimke.m3catalog.uid

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ee.schimke.composeai.preview.CaptureGutter
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModesSnackbar
import ee.schimke.m3catalog.KitShadowGutter
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogChoice
import ee.schimke.m3catalog.catalogText
import ee.schimke.m3catalog.counted
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.action_dismiss
import ee.schimke.m3catalog.generated.resources.action_undo
import ee.schimke.m3catalog.generated.resources.snackbar_long
import ee.schimke.m3catalog.generated.resources.snackbar_short
import generated.uibuilder.KitSnackbar
import generated.uibuilder.KitSnackbarAction
import generated.uibuilder.KitSnackbarActionClose
import generated.uibuilder.KitSnackbarClose
import org.jetbrains.compose.resources.stringResource

// The body is `stickers/snackbar-message.uid`: a `KitSnackbar` component, the bar in a 344dp `Box`
// (the frame's width, loose, as the hand-written sticker explains) around a bound message. The
// kit's action and close affordance are cells that add a `TextButton` and an `IconButton` to the
// bar's slots, each click an action-free event. Line count is the message's doing, so the shell
// only picks the copy.

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
fun SnackbarMessage(close: Boolean = false) = Sticker {
  val undo = counted(catalogText("action", stringResource(Res.string.action_undo)))
  val dismiss = counted(stringResource(Res.string.action_dismiss))
  val long = catalogChoice("lines", "one", "one" to "One line", "two" to "Two lines") == "two"
  val action =
    catalogChoice("configuration", "text", "text", "text+action").startsWith("text+action")
  val text =
    catalogText(
      "text",
      stringResource(if (long) Res.string.snackbar_long else Res.string.snackbar_short),
    )
  val m = Modifier
  when {
    action && close ->
      KitSnackbarActionClose(
        m,
        text = text,
        action = undo.label,
        dismissAction = dismiss.label,
        onUndoClick = undo.onClick,
        onCloseClick = dismiss.onClick,
      )
    action -> KitSnackbarAction(m, text = text, action = undo.label, onUndoClick = undo.onClick)
    close ->
      KitSnackbarClose(
        m,
        text = text,
        dismissAction = dismiss.label,
        onCloseClick = dismiss.onClick,
      )
    else -> KitSnackbar(m, text = text)
  }
}
