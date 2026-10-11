@file:CatalogGroup(name = "Dialogs", section = "Containment")
@file:OptIn(ExperimentalMaterial3ComponentOverrideApi::class)

package ee.schimke.m3catalog.uid

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.BasicAlertDialogOverride
import androidx.compose.material3.BasicAlertDialogOverrideScope
import androidx.compose.material3.ExperimentalMaterial3ComponentOverrideApi
import androidx.compose.material3.LocalBasicAlertDialogOverride
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import ee.schimke.composeai.preview.BuilderComponent
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogText
import ee.schimke.m3catalog.counted
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.dialog_action_1
import ee.schimke.m3catalog.generated.resources.dialog_action_2
import ee.schimke.m3catalog.generated.resources.dialog_delete_body
import ee.schimke.m3catalog.generated.resources.dialog_delete_title
import generated.uibuilder.KitBasicDialog
import generated.uibuilder.KitBasicDialogIcon
import org.jetbrains.compose.resources.stringResource

// The body is `stickers/basic-dialog.uid`: a `KitBasicDialog` whose `AlertDialog` holds the title,
// the supporting text and two text buttons, their copy bindings and their clicks action-free
// events.
// The hero icon is the document's `Icon` cell (`stickers/basic-dialog.json`), which adds the delete
// glyph to the dialog's `icon` slot.
//
// What stays here is the host, not the dialog: `AlertDialog` opens a platform window that a
// single-surface capture cannot reach, so [InlineDialogHost] replaces only that window, as the
// hand-written sticker's `:catalog` host does (which is `internal` there, so it is restated).

/** Renders Material dialog content in the sticker surface instead of a platform window. */
@Composable
private fun InlineDialogHost(content: @Composable () -> Unit) {
  CompositionLocalProvider(LocalBasicAlertDialogOverride provides InlineBasicAlertDialogOverride) {
    content()
  }
}

private object InlineBasicAlertDialogOverride : BasicAlertDialogOverride {
  @Composable
  override fun BasicAlertDialogOverrideScope.BasicAlertDialog() {
    Box(modifier = modifier, propagateMinConstraints = true) { content() }
  }
}

@CatalogComponent(
  id = "Dialog/Basic",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/50723:10948",
  caption = "Headline, supporting text and two actions. The hero icon folds in.",
)
@CatalogModes
// The builder has always shelved this as `m3/dialog`, and a saved design stores that string. The
// derived id would be `m3/alert-dialog` after the component's own symbol, so the rename is stated
// here rather than letting the derivation quietly re-key every design that references it.
@BuilderComponent(component = "AlertDialog", id = "m3/dialog")
@OverrideVariant(name = "icon", booleans = ["icon=true"])
@Composable
fun BasicDialog(icon: Boolean = false) = Sticker {
  val confirm = counted(catalogText("confirmButton", stringResource(Res.string.dialog_action_1)))
  val dismiss = counted(catalogText("dismissButton", stringResource(Res.string.dialog_action_2)))
  val title = catalogText("title", stringResource(Res.string.dialog_delete_title))
  val text = catalogText("text", stringResource(Res.string.dialog_delete_body))
  InlineDialogHost {
    if (icon)
      KitBasicDialogIcon(
        Modifier,
        title = title,
        text = text,
        confirmButton = confirm.label,
        dismissButton = dismiss.label,
        onConfirmClick = confirm.onClick,
        onDismissClick = dismiss.onClick,
      )
    else
      KitBasicDialog(
        Modifier,
        title = title,
        text = text,
        confirmButton = confirm.label,
        dismissButton = dismiss.label,
        onConfirmClick = confirm.onClick,
        onDismissClick = dismiss.onClick,
      )
  }
}
