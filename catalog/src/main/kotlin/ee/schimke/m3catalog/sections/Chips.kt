@file:CatalogGroup(name = "Chips", section = "Selection")
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ElevatedSuggestionChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.overrides.previewOverrideString
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.counted
import ee.schimke.m3catalog.toggleable

// The kit's four chip kinds are four components. Within each, the axes the kit varies are
// parameters or one-line composable swaps, so they fold in as knobs: elevation (outlined default /
// elevated), leading content (none / icon, plus an avatar on the input chip), and status
// (enabled / disabled). Selection is the input and filter chips' own state.
//
// `ElevatedAssistChip` and friends are separate Compose functions, but they are the *same* kit
// component with its elevation property flipped — so the branch lives inside one sticker rather
// than doubling the card count.

@Composable
private fun chipElevated(): Boolean = previewOverrideString("style", "outlined") == "elevated"

@Composable private fun chipLeading(): String = previewOverrideString("leading", "none")

@Composable
private fun chipEnabled(): Boolean = previewOverrideString("status", "enabled") != "disabled"

@Composable
private fun chipIcon(): (@Composable () -> Unit)? =
  when (chipLeading()) {
    "icon" -> ({
        Icon(
          Icons.Filled.Event,
          contentDescription = null,
          modifier = Modifier.size(AssistChipDefaults.IconSize),
        )
      })
    "avatar" -> ({
        Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = CircleShape) {
          Icon(
            Icons.Filled.Person,
            contentDescription = null,
            modifier = Modifier.size(InputChipDefaults.AvatarSize).padding(2.dp),
          )
        }
      })
    else -> null
  }

@CatalogComponent(
  id = "Chip/Assist",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/53923:28089",
  caption = "A smart action related to the content. Elevated, leading icon and disabled fold in.",
)
@CatalogModes
@OverrideVariant(name = "icon", strings = ["leading=icon"])
@OverrideVariant(name = "elevated", strings = ["style=elevated"])
@OverrideVariant(name = "elevated-icon", strings = ["style=elevated", "leading=icon"])
@OverrideVariant(name = "disabled", strings = ["status=disabled"])
@OverrideVariant(name = "disabled-icon", strings = ["leading=icon", "status=disabled"])
@Composable
fun AssistChipSticker() = Sticker {
  val c = counted("Add to calendar")
  val enabled = chipEnabled()
  if (chipElevated()) {
    ElevatedAssistChip(
      onClick = c.onClick,
      label = { Text(c.label) },
      enabled = enabled,
      leadingIcon = chipIcon(),
    )
  } else {
    AssistChip(
      onClick = c.onClick,
      label = { Text(c.label) },
      enabled = enabled,
      leadingIcon = chipIcon(),
    )
  }
}

@CatalogComponent(
  id = "Chip/Filter",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/53923:28270",
  caption =
    "Filters content; carries a selected state. Unselected, elevated and disabled fold in. The " +
      "check mark is the selected state's leading icon, so it follows selection rather than a knob.",
)
@CatalogModes
@OverrideVariant(name = "unselected", strings = ["state=unselected"])
@OverrideVariant(name = "elevated", strings = ["style=elevated"])
@OverrideVariant(name = "elevated-unselected", strings = ["style=elevated", "state=unselected"])
@OverrideVariant(name = "disabled", strings = ["status=disabled"])
@OverrideVariant(name = "disabled-unselected", strings = ["state=unselected", "status=disabled"])
@Composable
fun FilterChipSticker() = Sticker {
  val (selected, set) = toggleable(previewOverrideString("state", "selected") == "selected")
  val enabled = chipEnabled()
  val label: @Composable () -> Unit = { Text("Unread") }
  val check: (@Composable () -> Unit)? =
    if (!selected) null
    else
      ({
        Icon(
          Icons.Filled.Check,
          contentDescription = null,
          modifier = Modifier.size(FilterChipDefaults.IconSize),
        )
      })
  if (chipElevated()) {
    ElevatedFilterChip(
      selected = selected,
      onClick = { set(!selected) },
      label = label,
      enabled = enabled,
      leadingIcon = check,
    )
  } else {
    FilterChip(
      selected = selected,
      onClick = { set(!selected) },
      label = label,
      enabled = enabled,
      leadingIcon = check,
    )
  }
}

@CatalogComponent(
  id = "Chip/Input",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/53923:27888",
  caption =
    "A discrete piece of user input, removable. Leading icon, avatar, selected and disabled fold in.",
)
@CatalogModes
@OverrideVariant(name = "icon", strings = ["leading=icon"])
@OverrideVariant(name = "avatar", strings = ["leading=avatar"])
@OverrideVariant(name = "selected", strings = ["state=selected"])
@OverrideVariant(name = "selected-avatar", strings = ["state=selected", "leading=avatar"])
@OverrideVariant(name = "disabled", strings = ["status=disabled"])
@OverrideVariant(name = "disabled-avatar", strings = ["leading=avatar", "status=disabled"])
@Composable
fun InputChipSticker() = Sticker {
  val (selected, set) = toggleable(previewOverrideString("state", "unselected") == "selected")
  InputChip(
    selected = selected,
    onClick = { set(!selected) },
    label = { Text("Alice") },
    enabled = chipEnabled(),
    avatar = chipIcon(),
    trailingIcon = {
      Icon(
        Icons.Filled.Close,
        contentDescription = "Remove",
        modifier = Modifier.size(InputChipDefaults.IconSize),
      )
    },
  )
}

@CatalogComponent(
  id = "Chip/Suggestion",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/53923:28679",
  caption = "A dynamically generated suggestion. Elevated and disabled fold in.",
)
@CatalogModes
@OverrideVariant(name = "elevated", strings = ["style=elevated"])
@OverrideVariant(name = "disabled", strings = ["status=disabled"])
@Composable
fun SuggestionChipSticker() = Sticker {
  val c = counted("Sounds good")
  val enabled = chipEnabled()
  if (chipElevated()) {
    ElevatedSuggestionChip(onClick = c.onClick, label = { Text(c.label) }, enabled = enabled)
  } else {
    SuggestionChip(onClick = c.onClick, label = { Text(c.label) }, enabled = enabled)
  }
}
