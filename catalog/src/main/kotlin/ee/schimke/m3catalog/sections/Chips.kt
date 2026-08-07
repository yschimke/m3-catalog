@file:CatalogGroup(name = "Chips", section = "Selection")
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.counted
import ee.schimke.m3catalog.toggleable

// A filter chip carries selection state and owns it; the other three are actions and take the
// click tally instead.

@CatalogComponent(
  id = "Chip/Assist",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/53923:28089",
  caption = "A smart action related to the content.",
)
@CatalogModes
@Composable
fun AssistChipSticker() = Sticker {
  val c = counted("Add to calendar")
  AssistChip(onClick = c.onClick, label = { Text(c.label) })
}

@CatalogComponent(
  id = "Chip/Filter",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/53923:28270",
  caption = "Filters content; carries a selected state.",
)
@CatalogModes
@Composable
fun FilterChipSticker() = Sticker {
  val (selected, set) = toggleable(true)
  FilterChip(
    selected = selected,
    onClick = { set(!selected) },
    label = { Text("Unread") },
    leadingIcon =
      if (selected) {
        {
          Icon(
            Icons.Filled.Check,
            contentDescription = null,
            modifier = Modifier.size(FilterChipDefaults.IconSize),
          )
        }
      } else null,
  )
}

@CatalogComponent(
  id = "Chip/Input",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/53923:27888",
  caption = "A discrete piece of user input, removable.",
)
@CatalogModes
@Composable
fun InputChipSticker() = Sticker {
  val c = counted("Alice")
  InputChip(
    selected = false,
    onClick = c.onClick,
    label = { Text(c.label) },
    trailingIcon = { Icon(Icons.Filled.Close, contentDescription = "Remove") },
  )
}

@CatalogComponent(
  id = "Chip/Suggestion",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/53923:28679",
  caption = "A dynamically generated suggestion.",
)
@CatalogModes
@Composable
fun SuggestionChipSticker() = Sticker {
  val c = counted("Sounds good")
  SuggestionChip(onClick = c.onClick, label = { Text(c.label) })
}

@CatalogVariant(of = "Chip/Assist", state = "disabled")
@CatalogModes
@Composable
fun AssistChipDisabled() = Sticker {
  AssistChip(onClick = {}, label = { Text("Add to calendar") }, enabled = false)
}
