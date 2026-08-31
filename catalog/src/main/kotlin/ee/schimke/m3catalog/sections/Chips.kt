@file:CatalogGroup(name = "Chips", section = "Selection")
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
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
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.overrides.previewOverrideBoolean
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogFilledStars
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.HoverFocusStates
import ee.schimke.m3catalog.InteractionStates
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogChoice
import ee.schimke.m3catalog.counted
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.action_remove
import ee.schimke.m3catalog.generated.resources.chip_add_to_calendar
import ee.schimke.m3catalog.generated.resources.chip_sounds_good
import ee.schimke.m3catalog.generated.resources.chip_unread
import ee.schimke.m3catalog.toggleable
import org.jetbrains.compose.resources.stringResource

// The kit's four chip kinds are four components. Within each, the axes the kit varies are
// parameters or one-line composable swaps, so they fold in as knobs: elevation (outlined default /
// elevated), leading content (none / icon, plus an avatar on the input chip), and status
// (enabled / disabled). Selection is the input and filter chips' own state.
//
// `ElevatedAssistChip` and friends are separate Compose functions, but they are the *same* kit
// component with its elevation property flipped — so the branch lives inside one sticker rather
// than doubling the card count.

@Composable
private fun chipElevated(): Boolean =
  catalogChoice("style", "outlined", "outlined", "elevated") == "elevated"

@Composable
private fun chipLeading(): String =
  catalogChoice("leading", "none", "none", "icon", "avatar", "brand", "favicon")

@Composable
private fun chipEnabled(): Boolean =
  catalogChoice("status", "enabled", "enabled", "disabled") != "disabled"

@Composable
private fun chipIcon(): (@Composable () -> Unit)? =
  when (chipLeading()) {
    "icon",
    "brand",
    "favicon" -> ({
        Icon(
          if (chipLeading() == "favicon") Icons.Filled.Person else Icons.Filled.Event,
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
  reference = "figma:ocdacdEsnHipMJD3egzxKb/53923:28267",
  caption = "A smart action related to the content. Elevated, leading icon and disabled fold in.",
)
@CatalogModes
@OverrideVariant(
  name = "icon",
  strings = ["leading=icon"],
  kitAxis = "Configuration",
  kitValue = "Label & icon",
)
@OverrideVariant(
  name = "brand-icon",
  strings = ["leading=brand"],
  kitAxis = "Configuration",
  kitValue = "Label & brand icon",
)
@OverrideVariant(
  name = "favicon",
  strings = ["leading=favicon"],
  kitAxis = "Configuration",
  kitValue = "Label & favicon",
)
@OverrideVariant(name = "elevated", strings = ["style=elevated"])
@OverrideVariant(name = "elevated-icon", strings = ["style=elevated", "leading=icon"])
@OverrideVariant(name = "disabled", strings = ["status=disabled"])
@OverrideVariant(name = "disabled-icon", strings = ["leading=icon", "status=disabled"])
@InteractionStates
@ee.schimke.m3catalog.AssistChipStickerExhaustiveKitCells
@Composable
fun AssistChipSticker() = Sticker {
  val c = counted(stringResource(Res.string.chip_add_to_calendar))
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
  reference = "figma:ocdacdEsnHipMJD3egzxKb/53923:28465",
  caption =
    "Filters content; carries a selected state. Unselected, elevated and disabled fold in. The " +
      "check mark is the selected state's leading icon, so it follows selection rather than a knob.",
)
@CatalogModes
@OverrideVariant(name = "unselected", strings = ["state=unselected"])
@OverrideVariant(
  name = "label-only",
  strings = ["configuration=label"],
  kitAxis = "Configuration",
  kitValue = "Label only",
)
@OverrideVariant(name = "elevated", strings = ["style=elevated"])
@OverrideVariant(name = "elevated-unselected", strings = ["style=elevated", "state=unselected"])
@OverrideVariant(name = "disabled", strings = ["status=disabled"])
@OverrideVariant(name = "disabled-unselected", strings = ["state=unselected", "status=disabled"])
@OverrideVariant(name = "trailing-icon", booleans = ["trailing=true"])
@InteractionStates
@ee.schimke.m3catalog.FilterChipStickerExhaustiveKitCells
@Composable
fun FilterChipSticker() = Sticker {
  var selected by
    toggleable(catalogChoice("state", "selected", "selected", "unselected") == "selected")
  val enabled = chipEnabled()
  val label: @Composable () -> Unit = { Text(stringResource(Res.string.chip_unread)) }
  val showLeading = catalogChoice("configuration", "icon", "icon", "label") == "icon"
  val check: (@Composable () -> Unit)? =
    if (!selected || !showLeading) null
    else
      ({
        Icon(
          Icons.Filled.Check,
          contentDescription = null,
          modifier = Modifier.size(FilterChipDefaults.IconSize),
        )
      })
  // The kit's `Show trailing icon` axis — the caret that says this chip opens a menu. Decorative
  // beside its own label, so no contentDescription.
  val caret: (@Composable () -> Unit)? =
    if (!previewOverrideBoolean("trailing", false)) null
    else
      ({
        Icon(
          Icons.Filled.ArrowDropDown,
          contentDescription = null,
          modifier = Modifier.size(FilterChipDefaults.IconSize),
        )
      })
  if (chipElevated()) {
    ElevatedFilterChip(
      selected = selected,
      onClick = { selected = !selected },
      label = label,
      enabled = enabled,
      leadingIcon = check,
      trailingIcon = caret,
    )
  } else {
    FilterChip(
      selected = selected,
      onClick = { selected = !selected },
      label = label,
      enabled = enabled,
      leadingIcon = check,
      trailingIcon = caret,
    )
  }
}

@CatalogComponent(
  id = "Chip/Input",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/53923:28082",
  caption =
    "A discrete piece of user input, removable. Leading icon, avatar, selected and disabled fold in.",
)
@CatalogModes
@OverrideVariant(
  name = "icon",
  strings = ["leading=icon"],
  kitAxis = "Configuration",
  kitValue = "Label & leading icon",
)
@OverrideVariant(name = "avatar", strings = ["leading=avatar"])
@OverrideVariant(name = "selected", strings = ["state=selected"])
@OverrideVariant(name = "selected-avatar", strings = ["state=selected", "leading=avatar"])
@OverrideVariant(name = "disabled", strings = ["status=disabled"])
@OverrideVariant(name = "disabled-avatar", strings = ["leading=avatar", "status=disabled"])
@OverrideVariant(
  name = "no-closing-icon",
  booleans = ["closing=false"],
  kitAxis = "Show closing icon",
  kitValue = "false",
)
@HoverFocusStates
@ee.schimke.m3catalog.InputChipStickerExhaustiveKitCells
@Composable
fun InputChipSticker() = Sticker {
  var selected by
    toggleable(catalogChoice("state", "unselected", "selected", "unselected") == "selected")
  InputChip(
    selected = selected,
    onClick = { selected = !selected },
    label = { Text(stringResource(Res.string.chip_unread)) },
    enabled = chipEnabled(),
    avatar = chipIcon(),
    trailingIcon =
      if (!previewOverrideBoolean("closing", true)) null
      else
        ({
          Icon(
            Icons.Filled.Close,
            contentDescription = stringResource(Res.string.action_remove),
            modifier = Modifier.size(InputChipDefaults.IconSize),
          )
        }),
  )
}

@CatalogComponent(
  id = "Chip/Suggestion",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/53923:28845",
  caption = "A dynamically generated suggestion. Elevated and disabled fold in.",
)
@CatalogModes
@OverrideVariant(name = "elevated", strings = ["style=elevated"])
@OverrideVariant(name = "disabled", strings = ["status=disabled"])
@OverrideVariant(name = "icon", booleans = ["icon=true"])
@InteractionStates
@ee.schimke.m3catalog.SuggestionChipStickerExhaustiveKitCells
@Composable
fun SuggestionChipSticker() = Sticker {
  val c = counted(stringResource(Res.string.chip_sounds_good))
  val enabled = chipEnabled()
  // The kit's `Show icon` axis. Decorative beside its own label, so no contentDescription.
  val icon: (@Composable () -> Unit)? =
    if (!previewOverrideBoolean("icon", false)) null
    else
      ({
        Icon(
          CatalogFilledStars,
          contentDescription = null,
          modifier = Modifier.size(SuggestionChipDefaults.IconSize),
        )
      })
  if (chipElevated()) {
    ElevatedSuggestionChip(
      onClick = c.onClick,
      label = { Text(c.label) },
      enabled = enabled,
      icon = icon,
    )
  } else {
    SuggestionChip(onClick = c.onClick, label = { Text(c.label) }, enabled = enabled, icon = icon)
  }
}
