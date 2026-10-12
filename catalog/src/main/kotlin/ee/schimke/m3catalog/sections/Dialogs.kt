@file:CatalogGroup(name = "Dialogs", section = "Containment")
@file:OptIn(ExperimentalMaterial3ComponentOverrideApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialogOverride
import androidx.compose.material3.BasicAlertDialogOverrideScope
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3ComponentOverrideApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalBasicAlertDialogOverride
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.preview.BuilderComponent
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogFilledStars
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.InlineDialogHost
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogText
import ee.schimke.m3catalog.counted
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.dialog_action_1
import ee.schimke.m3catalog.generated.resources.dialog_action_2
import ee.schimke.m3catalog.generated.resources.dialog_add_account
import ee.schimke.m3catalog.generated.resources.dialog_backup_title
import ee.schimke.m3catalog.generated.resources.dialog_delete_body
import ee.schimke.m3catalog.generated.resources.dialog_delete_title
import ee.schimke.m3catalog.localizedDigits
import ee.schimke.m3catalog.toggleable
import generated.uibuilder.KitBasicDialog
import generated.uibuilder.KitBasicDialogIcon
import generated.uibuilder.KitListDialog
import generated.uibuilder.KitListDialogIcon
import org.jetbrains.compose.resources.stringResource

// A dialog is hosted in its own platform window, which a single-surface capture cannot reach.
// InlineDialogHost replaces only that window; AlertDialog still owns the complete Material layout.
//
// The kit's axes: the optional ICON, and the body layout (supporting text vs a choice list).

// --- Generated sticker bodies, from uid-stickers/Dialogs.kt ---

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

@CatalogComponent(
  id = "Dialog/List",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/50723:10964",
  caption = "A choice list in place of supporting text.",
)
@CatalogModes
@OverrideVariant(name = "icon", booleans = ["icon=true"])
@Composable
fun ListDialog(icon: Boolean = false) = Sticker {
  val confirm = counted(catalogText("confirmButton", stringResource(Res.string.dialog_action_1)))
  val dismiss = counted(catalogText("dismissButton", stringResource(Res.string.dialog_action_2)))
  val title = catalogText("title", stringResource(Res.string.dialog_backup_title))
  val text = catalogText("text", stringResource(Res.string.dialog_delete_body))
  val item = catalogText("item", stringResource(Res.string.dialog_add_account))
  var check1 by toggleable(true)
  var check2 by toggleable(true)
  var check3 by toggleable(true)
  val itemCount = localizedDigits("100+")
  InlineDialogHost {
    if (icon)
      KitListDialogIcon(
        Modifier,
        title = title,
        text = text,
        item = item,
        itemCount = itemCount,
        check1Checked = check1,
        check2Checked = check2,
        check3Checked = check3,
        confirmButton = confirm.label,
        dismissButton = dismiss.label,
        onCheck1CheckedChange = { check1 = !check1 },
        onCheck2CheckedChange = { check2 = !check2 },
        onCheck3CheckedChange = { check3 = !check3 },
        onConfirmClick = confirm.onClick,
        onDismissClick = dismiss.onClick,
      )
    else
      KitListDialog(
        Modifier,
        title = title,
        text = text,
        item = item,
        itemCount = itemCount,
        check1Checked = check1,
        check2Checked = check2,
        check3Checked = check3,
        confirmButton = confirm.label,
        dismissButton = dismiss.label,
        onCheck1CheckedChange = { check1 = !check1 },
        onCheck2CheckedChange = { check2 = !check2 },
        onCheck3CheckedChange = { check3 = !check3 },
        onConfirmClick = confirm.onClick,
        onDismissClick = dismiss.onClick,
      )
  }
}
