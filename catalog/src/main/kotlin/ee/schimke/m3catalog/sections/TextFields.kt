@file:CatalogGroup(name = "Text fields", section = "Text inputs")
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.overrides.previewOverrideString
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.editable

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
private fun fieldSpec(): FieldSpec {
  val state = previewOverrideString("state", "value")
  val content = previewOverrideString("content", "none")
  val error = state == "error"
  return FieldSpec(
    value = if (state == "empty") "" else if (error) "not-an-email" else "Alice",
    label =
      if (previewOverrideString("label", "on") == "off") null
      else ({ Text(if (error) "Email" else "Name") }),
    placeholder = if (state == "empty") ({ Text("Your name") }) else null,
    leading =
      if (content != "leading" && content != "both") null
      else ({ Icon(Icons.Filled.Search, contentDescription = null) }),
    trailing =
      if (content != "trailing" && content != "both") null
      else ({ Icon(Icons.Filled.Cancel, contentDescription = "Clear") }),
    supporting =
      if (error) ({ Text("Enter a valid email address") })
      else if (previewOverrideString("supporting", "off") == "on")
        ({ Text("Shown on your profile") })
      else null,
    isError = error,
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
@OverrideVariant(name = "empty", strings = ["state=empty"])
@OverrideVariant(name = "error", strings = ["state=error"])
@OverrideVariant(name = "disabled", strings = ["state=disabled"])
@OverrideVariant(name = "leading-icon", strings = ["content=leading"])
@OverrideVariant(name = "trailing-icon", strings = ["content=trailing"])
@OverrideVariant(name = "both-icons", strings = ["content=both"])
@OverrideVariant(name = "supporting", strings = ["supporting=on"])
@OverrideVariant(name = "no-label", strings = ["label=off"])
@OverrideVariant(name = "no-label-empty", strings = ["label=off", "state=empty"])
@Composable
fun FilledTextField() = Sticker {
  val spec = fieldSpec()
  val (text, set) = editable(spec.value)
  TextField(
    value = text,
    onValueChange = set,
    enabled = spec.enabled,
    isError = spec.isError,
    label = spec.label,
    placeholder = spec.placeholder,
    leadingIcon = spec.leading,
    trailingIcon = spec.trailing,
    supportingText = spec.supporting,
    modifier = Modifier.width(280.dp),
  )
}

@CatalogComponent(
  id = "TextField/Outlined",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/52798:24397",
  caption = "Outlined container; less visual weight. Carries the same matrix as the filled field.",
)
@CatalogModes
@OverrideVariant(name = "empty", strings = ["state=empty"])
@OverrideVariant(name = "error", strings = ["state=error"])
@OverrideVariant(name = "disabled", strings = ["state=disabled"])
@OverrideVariant(name = "leading-icon", strings = ["content=leading"])
@OverrideVariant(name = "trailing-icon", strings = ["content=trailing"])
@OverrideVariant(name = "both-icons", strings = ["content=both"])
@OverrideVariant(name = "supporting", strings = ["supporting=on"])
@OverrideVariant(name = "no-label", strings = ["label=off"])
@Composable
fun OutlinedTextFieldSticker() = Sticker {
  val spec = fieldSpec()
  val (text, set) = editable(spec.value)
  OutlinedTextField(
    value = text,
    onValueChange = set,
    enabled = spec.enabled,
    isError = spec.isError,
    label = spec.label,
    placeholder = spec.placeholder,
    leadingIcon = spec.leading,
    trailingIcon = spec.trailing,
    supportingText = spec.supporting,
    modifier = Modifier.width(280.dp),
  )
}
