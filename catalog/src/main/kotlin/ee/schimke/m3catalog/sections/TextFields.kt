@file:CatalogGroup(name = "Text fields", section = "Text inputs")
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.composeai.preview.VariantInteraction
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogChoice
import ee.schimke.m3catalog.catalogText
import ee.schimke.m3catalog.editable
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.action_clear
import ee.schimke.m3catalog.generated.resources.field_email
import ee.schimke.m3catalog.generated.resources.field_error
import ee.schimke.m3catalog.generated.resources.field_name
import ee.schimke.m3catalog.generated.resources.field_placeholder
import ee.schimke.m3catalog.generated.resources.field_supporting
import generated.uibuilder.KitFilledTextField
import generated.uibuilder.KitFilledTextFieldError
import generated.uibuilder.KitFilledTextFieldErrorLeading
import generated.uibuilder.KitFilledTextFieldErrorLeadingTrailing
import generated.uibuilder.KitFilledTextFieldErrorTrailing
import generated.uibuilder.KitFilledTextFieldLabel
import generated.uibuilder.KitFilledTextFieldLabelError
import generated.uibuilder.KitFilledTextFieldLabelErrorLeading
import generated.uibuilder.KitFilledTextFieldLabelErrorLeadingTrailing
import generated.uibuilder.KitFilledTextFieldLabelErrorTrailing
import generated.uibuilder.KitFilledTextFieldLabelLeading
import generated.uibuilder.KitFilledTextFieldLabelLeadingTrailing
import generated.uibuilder.KitFilledTextFieldLabelSupporting
import generated.uibuilder.KitFilledTextFieldLabelSupportingLeading
import generated.uibuilder.KitFilledTextFieldLabelSupportingLeadingTrailing
import generated.uibuilder.KitFilledTextFieldLabelSupportingTrailing
import generated.uibuilder.KitFilledTextFieldLabelTrailing
import generated.uibuilder.KitFilledTextFieldLeading
import generated.uibuilder.KitFilledTextFieldLeadingTrailing
import generated.uibuilder.KitFilledTextFieldPlaceholder
import generated.uibuilder.KitFilledTextFieldPlaceholderError
import generated.uibuilder.KitFilledTextFieldPlaceholderErrorLeading
import generated.uibuilder.KitFilledTextFieldPlaceholderErrorLeadingTrailing
import generated.uibuilder.KitFilledTextFieldPlaceholderErrorTrailing
import generated.uibuilder.KitFilledTextFieldPlaceholderLeading
import generated.uibuilder.KitFilledTextFieldPlaceholderLeadingTrailing
import generated.uibuilder.KitFilledTextFieldPlaceholderSupporting
import generated.uibuilder.KitFilledTextFieldPlaceholderSupportingLeading
import generated.uibuilder.KitFilledTextFieldPlaceholderSupportingLeadingTrailing
import generated.uibuilder.KitFilledTextFieldPlaceholderSupportingTrailing
import generated.uibuilder.KitFilledTextFieldPlaceholderTrailing
import generated.uibuilder.KitFilledTextFieldSupporting
import generated.uibuilder.KitFilledTextFieldSupportingLeading
import generated.uibuilder.KitFilledTextFieldSupportingLeadingTrailing
import generated.uibuilder.KitFilledTextFieldSupportingTrailing
import generated.uibuilder.KitFilledTextFieldTrailing
import generated.uibuilder.KitOutlinedTextField
import generated.uibuilder.KitOutlinedTextFieldError
import generated.uibuilder.KitOutlinedTextFieldErrorLeading
import generated.uibuilder.KitOutlinedTextFieldErrorLeadingTrailing
import generated.uibuilder.KitOutlinedTextFieldErrorTrailing
import generated.uibuilder.KitOutlinedTextFieldLabel
import generated.uibuilder.KitOutlinedTextFieldLabelError
import generated.uibuilder.KitOutlinedTextFieldLabelErrorLeading
import generated.uibuilder.KitOutlinedTextFieldLabelErrorLeadingTrailing
import generated.uibuilder.KitOutlinedTextFieldLabelErrorTrailing
import generated.uibuilder.KitOutlinedTextFieldLabelLeading
import generated.uibuilder.KitOutlinedTextFieldLabelLeadingTrailing
import generated.uibuilder.KitOutlinedTextFieldLabelSupporting
import generated.uibuilder.KitOutlinedTextFieldLabelSupportingLeading
import generated.uibuilder.KitOutlinedTextFieldLabelSupportingLeadingTrailing
import generated.uibuilder.KitOutlinedTextFieldLabelSupportingTrailing
import generated.uibuilder.KitOutlinedTextFieldLabelTrailing
import generated.uibuilder.KitOutlinedTextFieldLeading
import generated.uibuilder.KitOutlinedTextFieldLeadingTrailing
import generated.uibuilder.KitOutlinedTextFieldPlaceholder
import generated.uibuilder.KitOutlinedTextFieldPlaceholderError
import generated.uibuilder.KitOutlinedTextFieldPlaceholderErrorLeading
import generated.uibuilder.KitOutlinedTextFieldPlaceholderErrorLeadingTrailing
import generated.uibuilder.KitOutlinedTextFieldPlaceholderErrorTrailing
import generated.uibuilder.KitOutlinedTextFieldPlaceholderLeading
import generated.uibuilder.KitOutlinedTextFieldPlaceholderLeadingTrailing
import generated.uibuilder.KitOutlinedTextFieldPlaceholderSupporting
import generated.uibuilder.KitOutlinedTextFieldPlaceholderSupportingLeading
import generated.uibuilder.KitOutlinedTextFieldPlaceholderSupportingLeadingTrailing
import generated.uibuilder.KitOutlinedTextFieldPlaceholderSupportingTrailing
import generated.uibuilder.KitOutlinedTextFieldPlaceholderTrailing
import generated.uibuilder.KitOutlinedTextFieldSupporting
import generated.uibuilder.KitOutlinedTextFieldSupportingLeading
import generated.uibuilder.KitOutlinedTextFieldSupportingLeadingTrailing
import generated.uibuilder.KitOutlinedTextFieldSupportingTrailing
import generated.uibuilder.KitOutlinedTextFieldTrailing
import org.jetbrains.compose.resources.stringResource

// Filled and outlined are two composables, so two components. Everything the kit varies inside each
// — value / empty / error / disabled, leading and trailing icons, supporting text, and whether the
// label is present at all — is a parameter, so it folds in as a knob and the two cards carry the
// whole matrix between them.
//
// Text fields own their text, so typing works on the live lane and the baked capture is frozen at
// the seeded value.

private class FieldSpec(
  val value: String,
  val label: (@Composable () -> Unit)?,
  val placeholder: (@Composable () -> Unit)?,
  val leading: (@Composable () -> Unit)?,
  val trailing: (@Composable () -> Unit)?,
  val supporting: (@Composable () -> Unit)?,
  val isError: Boolean,
  val enabled: Boolean,
)

@Composable
private fun fieldSpec(leading: Boolean, trailing: Boolean): FieldSpec {
  val state = catalogChoice("state", "value", "value", "empty", "error", "disabled")
  val textConfiguration = catalogChoice("text", "input", "input", "label", "placeholder")
  // Two knobs rather than one four-valued `content`, because the kit models them as two
  // independent axes — `Leading icon` and `Trailing icon`, each True/False. Seeding one of them
  // now lands on a real kit variant; `content=leading` named an axis the kit does not have and
  // resolved to nothing. The variant NAMES are unchanged, so no published URL moves.
  val leadingIcon = leading
  val trailingIcon = trailing
  val error = state == "error"
  val defaultValue =
    if (state == "empty" || textConfiguration != "input") ""
    else if (error) "not-an-email" else stringResource(Res.string.field_placeholder)
  val labelText =
    catalogText(
      "labelText",
      stringResource(if (error) Res.string.field_email else Res.string.field_name),
    )
  val placeholderText = catalogText("placeholder", stringResource(Res.string.field_placeholder))
  val supportingText =
    catalogText(
      "supportingText",
      stringResource(if (error) Res.string.field_error else Res.string.field_supporting),
    )
  return FieldSpec(
    value = catalogText("value", defaultValue),
    label =
      if (
        catalogChoice("label", "on", "on", "off") == "off" ||
          state == "empty" ||
          textConfiguration == "placeholder"
      )
        null
      else ({ Text(labelText) }),
    placeholder =
      if (state == "empty" || textConfiguration == "placeholder") ({ Text(placeholderText) })
      else null,
    leading =
      if (!leadingIcon) null else ({ Icon(Icons.Filled.Search, contentDescription = null) }),
    trailing =
      if (!trailingIcon) null
      else
        ({
          Icon(Icons.Filled.Cancel, contentDescription = stringResource(Res.string.action_clear))
        }),
    supporting =
      if (error) ({ Text(supportingText) })
      else if (catalogChoice("supporting", "on", "on", "off") == "on") ({ Text(supportingText) })
      else null,
    isError = error,
    enabled = state != "disabled",
  )
}

// `State=Hovered` is absent for the same reason it is absent on the sliders: `TextFieldColors`
// carries focused, unfocused, disabled and error containers and no hovered one, so a real hover
// over a text field is byte-identical to its resting render. Issue #91.

// --- Generated sticker bodies, from uid-stickers/TextFields.kt ---

/** The field's live knobs, read as the hand-written `fieldSpec` reads them. */
private class FieldKnobs(
  val value: String,
  val labelText: String,
  val placeholderText: String,
  val supportingText: String,
  val cell: Cell,
  val enabled: Boolean,
)

/** Which cell of the document draws the field. */
private data class Cell(
  val label: String,
  val supporting: String,
  val leading: Boolean,
  val trailing: Boolean,
)

@Composable
private fun fieldKnobs(leading: Boolean, trailing: Boolean): FieldKnobs {
  val state = catalogChoice("state", "value", "value", "empty", "error", "disabled")
  val textConfiguration = catalogChoice("text", "input", "input", "label", "placeholder")
  val error = state == "error"
  val defaultValue =
    if (state == "empty" || textConfiguration != "input") ""
    else if (error) "not-an-email" else stringResource(Res.string.field_placeholder)
  val labelText =
    catalogText(
      "labelText",
      stringResource(if (error) Res.string.field_email else Res.string.field_name),
    )
  val placeholderText = catalogText("placeholder", stringResource(Res.string.field_placeholder))
  val supportingText =
    catalogText(
      "supportingText",
      stringResource(if (error) Res.string.field_error else Res.string.field_supporting),
    )
  val value = catalogText("value", defaultValue)
  val labelOff = catalogChoice("label", "on", "on", "off") == "off"
  val placeholder = state == "empty" || textConfiguration == "placeholder"
  val label =
    when {
      placeholder -> "Placeholder"
      labelOff -> ""
      else -> "Label"
    }
  val supporting =
    when {
      error -> "Error"
      catalogChoice("supporting", "on", "on", "off") == "on" -> "Supporting"
      else -> ""
    }
  return FieldKnobs(
    value = value,
    labelText = labelText,
    placeholderText = placeholderText,
    supportingText = supportingText,
    cell = Cell(label, supporting, leading, trailing),
    enabled = state != "disabled",
  )
}

@CatalogComponent(
  id = "TextField/Filled",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/52798:24430",
  caption =
    "The default; a filled container. Empty, error, disabled, icons, supporting text and the " +
      "label-less form fold in.",
)
@CatalogModes
@OverrideVariant(
  name = "empty",
  strings = ["state=empty"],
  kitAxis = "Text configurations",
  kitValue = "Placeholder text",
)
@OverrideVariant(
  name = "label-text",
  strings = ["text=label"],
  kitAxis = "Text configurations",
  kitValue = "Label text",
)
@OverrideVariant(name = "error", strings = ["state=error"])
@OverrideVariant(name = "disabled", strings = ["state=disabled"])
@OverrideVariant(name = "leading-icon", booleans = ["leading=true"])
@OverrideVariant(name = "trailing-icon", booleans = ["trailing=true"])
@OverrideVariant(name = "both-icons", booleans = ["leading=true", "trailing=true"])
@OverrideVariant(name = "no-supporting", strings = ["supporting=off"])
@OverrideVariant(name = "no-label", strings = ["label=off"])
@OverrideVariant(name = "focused", interaction = VariantInteraction.Focused)
@ee.schimke.m3catalog.FilledTextFieldExhaustiveKitCells
@Composable
fun FilledTextField(leading: Boolean = false, trailing: Boolean = false) = Sticker {
  val f = fieldKnobs(leading, trailing)
  val text by editable(f.value)
  KitFilledTextFieldCell(f, text)
}

@CatalogComponent(
  id = "TextField/Outlined",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/52798:24397",
  caption = "Outlined container; less visual weight. Carries the same matrix as the filled field.",
)
@CatalogModes
@OverrideVariant(
  name = "empty",
  strings = ["state=empty"],
  kitAxis = "Text configurations",
  kitValue = "Placeholder text",
)
@OverrideVariant(
  name = "label-text",
  strings = ["text=label"],
  kitAxis = "Text configurations",
  kitValue = "Label text",
)
@OverrideVariant(name = "error", strings = ["state=error"])
@OverrideVariant(name = "disabled", strings = ["state=disabled"])
@OverrideVariant(name = "leading-icon", booleans = ["leading=true"])
@OverrideVariant(name = "trailing-icon", booleans = ["trailing=true"])
@OverrideVariant(name = "both-icons", booleans = ["leading=true", "trailing=true"])
@OverrideVariant(name = "no-supporting", strings = ["supporting=off"])
@OverrideVariant(name = "no-label", strings = ["label=off"])
@OverrideVariant(name = "focused", interaction = VariantInteraction.Focused)
@ee.schimke.m3catalog.OutlinedTextFieldStickerExhaustiveKitCells
@Composable
fun OutlinedTextFieldSticker(leading: Boolean = false, trailing: Boolean = false) = Sticker {
  val f = fieldKnobs(leading, trailing)
  val text by editable(f.value)
  KitOutlinedTextFieldCell(f, text)
}

@Composable
private fun KitFilledTextFieldCell(f: FieldKnobs, value: String) {
  val enabled = f.enabled
  when (f.cell) {
    Cell("", "", false, false) -> KitFilledTextField(Modifier, value = value, enabled = enabled)
    Cell("", "", false, true) ->
      KitFilledTextFieldTrailing(Modifier, value = value, enabled = enabled)
    Cell("", "", true, false) ->
      KitFilledTextFieldLeading(Modifier, value = value, enabled = enabled)
    Cell("", "", true, true) ->
      KitFilledTextFieldLeadingTrailing(Modifier, value = value, enabled = enabled)
    Cell("", "Supporting", false, false) ->
      KitFilledTextFieldSupporting(
        Modifier,
        value = value,
        enabled = enabled,
        supportingText = f.supportingText,
      )
    Cell("", "Supporting", false, true) ->
      KitFilledTextFieldSupportingTrailing(
        Modifier,
        value = value,
        enabled = enabled,
        supportingText = f.supportingText,
      )
    Cell("", "Supporting", true, false) ->
      KitFilledTextFieldSupportingLeading(
        Modifier,
        value = value,
        enabled = enabled,
        supportingText = f.supportingText,
      )
    Cell("", "Supporting", true, true) ->
      KitFilledTextFieldSupportingLeadingTrailing(
        Modifier,
        value = value,
        enabled = enabled,
        supportingText = f.supportingText,
      )
    Cell("", "Error", false, false) ->
      KitFilledTextFieldError(
        Modifier,
        value = value,
        enabled = enabled,
        supportingText = f.supportingText,
      )
    Cell("", "Error", false, true) ->
      KitFilledTextFieldErrorTrailing(
        Modifier,
        value = value,
        enabled = enabled,
        supportingText = f.supportingText,
      )
    Cell("", "Error", true, false) ->
      KitFilledTextFieldErrorLeading(
        Modifier,
        value = value,
        enabled = enabled,
        supportingText = f.supportingText,
      )
    Cell("", "Error", true, true) ->
      KitFilledTextFieldErrorLeadingTrailing(
        Modifier,
        value = value,
        enabled = enabled,
        supportingText = f.supportingText,
      )
    Cell("Label", "", false, false) ->
      KitFilledTextFieldLabel(Modifier, value = value, enabled = enabled, labelText = f.labelText)
    Cell("Label", "", false, true) ->
      KitFilledTextFieldLabelTrailing(
        Modifier,
        value = value,
        enabled = enabled,
        labelText = f.labelText,
      )
    Cell("Label", "", true, false) ->
      KitFilledTextFieldLabelLeading(
        Modifier,
        value = value,
        enabled = enabled,
        labelText = f.labelText,
      )
    Cell("Label", "", true, true) ->
      KitFilledTextFieldLabelLeadingTrailing(
        Modifier,
        value = value,
        enabled = enabled,
        labelText = f.labelText,
      )
    Cell("Label", "Supporting", false, false) ->
      KitFilledTextFieldLabelSupporting(
        Modifier,
        value = value,
        enabled = enabled,
        labelText = f.labelText,
        supportingText = f.supportingText,
      )
    Cell("Label", "Supporting", false, true) ->
      KitFilledTextFieldLabelSupportingTrailing(
        Modifier,
        value = value,
        enabled = enabled,
        labelText = f.labelText,
        supportingText = f.supportingText,
      )
    Cell("Label", "Supporting", true, false) ->
      KitFilledTextFieldLabelSupportingLeading(
        Modifier,
        value = value,
        enabled = enabled,
        labelText = f.labelText,
        supportingText = f.supportingText,
      )
    Cell("Label", "Supporting", true, true) ->
      KitFilledTextFieldLabelSupportingLeadingTrailing(
        Modifier,
        value = value,
        enabled = enabled,
        labelText = f.labelText,
        supportingText = f.supportingText,
      )
    Cell("Label", "Error", false, false) ->
      KitFilledTextFieldLabelError(
        Modifier,
        value = value,
        enabled = enabled,
        labelText = f.labelText,
        supportingText = f.supportingText,
      )
    Cell("Label", "Error", false, true) ->
      KitFilledTextFieldLabelErrorTrailing(
        Modifier,
        value = value,
        enabled = enabled,
        labelText = f.labelText,
        supportingText = f.supportingText,
      )
    Cell("Label", "Error", true, false) ->
      KitFilledTextFieldLabelErrorLeading(
        Modifier,
        value = value,
        enabled = enabled,
        labelText = f.labelText,
        supportingText = f.supportingText,
      )
    Cell("Label", "Error", true, true) ->
      KitFilledTextFieldLabelErrorLeadingTrailing(
        Modifier,
        value = value,
        enabled = enabled,
        labelText = f.labelText,
        supportingText = f.supportingText,
      )
    Cell("Placeholder", "", false, false) ->
      KitFilledTextFieldPlaceholder(
        Modifier,
        value = value,
        enabled = enabled,
        placeholder = f.placeholderText,
      )
    Cell("Placeholder", "", false, true) ->
      KitFilledTextFieldPlaceholderTrailing(
        Modifier,
        value = value,
        enabled = enabled,
        placeholder = f.placeholderText,
      )
    Cell("Placeholder", "", true, false) ->
      KitFilledTextFieldPlaceholderLeading(
        Modifier,
        value = value,
        enabled = enabled,
        placeholder = f.placeholderText,
      )
    Cell("Placeholder", "", true, true) ->
      KitFilledTextFieldPlaceholderLeadingTrailing(
        Modifier,
        value = value,
        enabled = enabled,
        placeholder = f.placeholderText,
      )
    Cell("Placeholder", "Supporting", false, false) ->
      KitFilledTextFieldPlaceholderSupporting(
        Modifier,
        value = value,
        enabled = enabled,
        placeholder = f.placeholderText,
        supportingText = f.supportingText,
      )
    Cell("Placeholder", "Supporting", false, true) ->
      KitFilledTextFieldPlaceholderSupportingTrailing(
        Modifier,
        value = value,
        enabled = enabled,
        placeholder = f.placeholderText,
        supportingText = f.supportingText,
      )
    Cell("Placeholder", "Supporting", true, false) ->
      KitFilledTextFieldPlaceholderSupportingLeading(
        Modifier,
        value = value,
        enabled = enabled,
        placeholder = f.placeholderText,
        supportingText = f.supportingText,
      )
    Cell("Placeholder", "Supporting", true, true) ->
      KitFilledTextFieldPlaceholderSupportingLeadingTrailing(
        Modifier,
        value = value,
        enabled = enabled,
        placeholder = f.placeholderText,
        supportingText = f.supportingText,
      )
    Cell("Placeholder", "Error", false, false) ->
      KitFilledTextFieldPlaceholderError(
        Modifier,
        value = value,
        enabled = enabled,
        placeholder = f.placeholderText,
        supportingText = f.supportingText,
      )
    Cell("Placeholder", "Error", false, true) ->
      KitFilledTextFieldPlaceholderErrorTrailing(
        Modifier,
        value = value,
        enabled = enabled,
        placeholder = f.placeholderText,
        supportingText = f.supportingText,
      )
    Cell("Placeholder", "Error", true, false) ->
      KitFilledTextFieldPlaceholderErrorLeading(
        Modifier,
        value = value,
        enabled = enabled,
        placeholder = f.placeholderText,
        supportingText = f.supportingText,
      )
    Cell("Placeholder", "Error", true, true) ->
      KitFilledTextFieldPlaceholderErrorLeadingTrailing(
        Modifier,
        value = value,
        enabled = enabled,
        placeholder = f.placeholderText,
        supportingText = f.supportingText,
      )
    else -> error("no cell for ${f.cell}")
  }
}

@Composable
private fun KitOutlinedTextFieldCell(f: FieldKnobs, value: String) {
  val enabled = f.enabled
  when (f.cell) {
    Cell("", "", false, false) -> KitOutlinedTextField(Modifier, value = value, enabled = enabled)
    Cell("", "", false, true) ->
      KitOutlinedTextFieldTrailing(Modifier, value = value, enabled = enabled)
    Cell("", "", true, false) ->
      KitOutlinedTextFieldLeading(Modifier, value = value, enabled = enabled)
    Cell("", "", true, true) ->
      KitOutlinedTextFieldLeadingTrailing(Modifier, value = value, enabled = enabled)
    Cell("", "Supporting", false, false) ->
      KitOutlinedTextFieldSupporting(
        Modifier,
        value = value,
        enabled = enabled,
        supportingText = f.supportingText,
      )
    Cell("", "Supporting", false, true) ->
      KitOutlinedTextFieldSupportingTrailing(
        Modifier,
        value = value,
        enabled = enabled,
        supportingText = f.supportingText,
      )
    Cell("", "Supporting", true, false) ->
      KitOutlinedTextFieldSupportingLeading(
        Modifier,
        value = value,
        enabled = enabled,
        supportingText = f.supportingText,
      )
    Cell("", "Supporting", true, true) ->
      KitOutlinedTextFieldSupportingLeadingTrailing(
        Modifier,
        value = value,
        enabled = enabled,
        supportingText = f.supportingText,
      )
    Cell("", "Error", false, false) ->
      KitOutlinedTextFieldError(
        Modifier,
        value = value,
        enabled = enabled,
        supportingText = f.supportingText,
      )
    Cell("", "Error", false, true) ->
      KitOutlinedTextFieldErrorTrailing(
        Modifier,
        value = value,
        enabled = enabled,
        supportingText = f.supportingText,
      )
    Cell("", "Error", true, false) ->
      KitOutlinedTextFieldErrorLeading(
        Modifier,
        value = value,
        enabled = enabled,
        supportingText = f.supportingText,
      )
    Cell("", "Error", true, true) ->
      KitOutlinedTextFieldErrorLeadingTrailing(
        Modifier,
        value = value,
        enabled = enabled,
        supportingText = f.supportingText,
      )
    Cell("Label", "", false, false) ->
      KitOutlinedTextFieldLabel(Modifier, value = value, enabled = enabled, labelText = f.labelText)
    Cell("Label", "", false, true) ->
      KitOutlinedTextFieldLabelTrailing(
        Modifier,
        value = value,
        enabled = enabled,
        labelText = f.labelText,
      )
    Cell("Label", "", true, false) ->
      KitOutlinedTextFieldLabelLeading(
        Modifier,
        value = value,
        enabled = enabled,
        labelText = f.labelText,
      )
    Cell("Label", "", true, true) ->
      KitOutlinedTextFieldLabelLeadingTrailing(
        Modifier,
        value = value,
        enabled = enabled,
        labelText = f.labelText,
      )
    Cell("Label", "Supporting", false, false) ->
      KitOutlinedTextFieldLabelSupporting(
        Modifier,
        value = value,
        enabled = enabled,
        labelText = f.labelText,
        supportingText = f.supportingText,
      )
    Cell("Label", "Supporting", false, true) ->
      KitOutlinedTextFieldLabelSupportingTrailing(
        Modifier,
        value = value,
        enabled = enabled,
        labelText = f.labelText,
        supportingText = f.supportingText,
      )
    Cell("Label", "Supporting", true, false) ->
      KitOutlinedTextFieldLabelSupportingLeading(
        Modifier,
        value = value,
        enabled = enabled,
        labelText = f.labelText,
        supportingText = f.supportingText,
      )
    Cell("Label", "Supporting", true, true) ->
      KitOutlinedTextFieldLabelSupportingLeadingTrailing(
        Modifier,
        value = value,
        enabled = enabled,
        labelText = f.labelText,
        supportingText = f.supportingText,
      )
    Cell("Label", "Error", false, false) ->
      KitOutlinedTextFieldLabelError(
        Modifier,
        value = value,
        enabled = enabled,
        labelText = f.labelText,
        supportingText = f.supportingText,
      )
    Cell("Label", "Error", false, true) ->
      KitOutlinedTextFieldLabelErrorTrailing(
        Modifier,
        value = value,
        enabled = enabled,
        labelText = f.labelText,
        supportingText = f.supportingText,
      )
    Cell("Label", "Error", true, false) ->
      KitOutlinedTextFieldLabelErrorLeading(
        Modifier,
        value = value,
        enabled = enabled,
        labelText = f.labelText,
        supportingText = f.supportingText,
      )
    Cell("Label", "Error", true, true) ->
      KitOutlinedTextFieldLabelErrorLeadingTrailing(
        Modifier,
        value = value,
        enabled = enabled,
        labelText = f.labelText,
        supportingText = f.supportingText,
      )
    Cell("Placeholder", "", false, false) ->
      KitOutlinedTextFieldPlaceholder(
        Modifier,
        value = value,
        enabled = enabled,
        placeholder = f.placeholderText,
      )
    Cell("Placeholder", "", false, true) ->
      KitOutlinedTextFieldPlaceholderTrailing(
        Modifier,
        value = value,
        enabled = enabled,
        placeholder = f.placeholderText,
      )
    Cell("Placeholder", "", true, false) ->
      KitOutlinedTextFieldPlaceholderLeading(
        Modifier,
        value = value,
        enabled = enabled,
        placeholder = f.placeholderText,
      )
    Cell("Placeholder", "", true, true) ->
      KitOutlinedTextFieldPlaceholderLeadingTrailing(
        Modifier,
        value = value,
        enabled = enabled,
        placeholder = f.placeholderText,
      )
    Cell("Placeholder", "Supporting", false, false) ->
      KitOutlinedTextFieldPlaceholderSupporting(
        Modifier,
        value = value,
        enabled = enabled,
        placeholder = f.placeholderText,
        supportingText = f.supportingText,
      )
    Cell("Placeholder", "Supporting", false, true) ->
      KitOutlinedTextFieldPlaceholderSupportingTrailing(
        Modifier,
        value = value,
        enabled = enabled,
        placeholder = f.placeholderText,
        supportingText = f.supportingText,
      )
    Cell("Placeholder", "Supporting", true, false) ->
      KitOutlinedTextFieldPlaceholderSupportingLeading(
        Modifier,
        value = value,
        enabled = enabled,
        placeholder = f.placeholderText,
        supportingText = f.supportingText,
      )
    Cell("Placeholder", "Supporting", true, true) ->
      KitOutlinedTextFieldPlaceholderSupportingLeadingTrailing(
        Modifier,
        value = value,
        enabled = enabled,
        placeholder = f.placeholderText,
        supportingText = f.supportingText,
      )
    Cell("Placeholder", "Error", false, false) ->
      KitOutlinedTextFieldPlaceholderError(
        Modifier,
        value = value,
        enabled = enabled,
        placeholder = f.placeholderText,
        supportingText = f.supportingText,
      )
    Cell("Placeholder", "Error", false, true) ->
      KitOutlinedTextFieldPlaceholderErrorTrailing(
        Modifier,
        value = value,
        enabled = enabled,
        placeholder = f.placeholderText,
        supportingText = f.supportingText,
      )
    Cell("Placeholder", "Error", true, false) ->
      KitOutlinedTextFieldPlaceholderErrorLeading(
        Modifier,
        value = value,
        enabled = enabled,
        placeholder = f.placeholderText,
        supportingText = f.supportingText,
      )
    Cell("Placeholder", "Error", true, true) ->
      KitOutlinedTextFieldPlaceholderErrorLeadingTrailing(
        Modifier,
        value = value,
        enabled = enabled,
        placeholder = f.placeholderText,
        supportingText = f.supportingText,
      )
    else -> error("no cell for ${f.cell}")
  }
}
