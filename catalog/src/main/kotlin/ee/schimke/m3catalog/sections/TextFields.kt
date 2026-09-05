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
  val spec = fieldSpec(leading, trailing)
  var text by editable(spec.value)
  TextField(
    value = text,
    onValueChange = { text = it },
    enabled = spec.enabled,
    isError = spec.isError,
    label = spec.label,
    placeholder = spec.placeholder,
    leadingIcon = spec.leading,
    trailingIcon = spec.trailing,
    supportingText = spec.supporting,
    modifier = Modifier.width(210.dp),
  )
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
  val spec = fieldSpec(leading, trailing)
  var text by editable(spec.value)
  Box(Modifier.padding(top = 8.dp)) {
    OutlinedTextField(
      value = text,
      onValueChange = { text = it },
      enabled = spec.enabled,
      isError = spec.isError,
      label = spec.label,
      placeholder = spec.placeholder,
      leadingIcon = spec.leading,
      trailingIcon = spec.trailing,
      supportingText = spec.supporting,
      modifier = Modifier.width(210.dp),
    )
  }
}
