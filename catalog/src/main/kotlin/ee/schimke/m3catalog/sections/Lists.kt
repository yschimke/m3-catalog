@file:CatalogGroup(name = "Lists", section = "Containment")

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Switch
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
import ee.schimke.m3catalog.toggleable

// Three axes the kit documents: LINE COUNT (one, two, three), the LEADING slot (none, icon,
// avatar), and the TRAILING slot (none, text, icon, checkbox, switch). They compose, so one
// preview carries the grid.

@Composable
private fun leading(): (@Composable () -> Unit)? =
  when (previewOverrideString("leading", "icon")) {
    "none" -> null
    else -> {
      { Icon(Icons.Filled.Person, contentDescription = null) }
    }
  }

@Composable
private fun trailing(): (@Composable () -> Unit)? {
  val (checked, set) = toggleable(true)
  return when (previewOverrideString("trailing", "none")) {
    "text" -> {
      { Text("10:30") }
    }
    "icon" -> {
      { Icon(Icons.Filled.MoreVert, contentDescription = "More") }
    }
    "checkbox" -> {
      { Checkbox(checked = checked, onCheckedChange = set) }
    }
    "switch" -> {
      { Switch(checked = checked, onCheckedChange = set) }
    }
    else -> null
  }
}

@CatalogComponent(
  id = "List/Item",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/59106:13028",
  caption = "A list row. Line count, leading and trailing slots all fold in.",
)
@CatalogModes
@OverrideVariant(name = "one-line", strings = ["lines=1", "leading=none"])
@OverrideVariant(name = "three-line", strings = ["lines=3"])
@OverrideVariant(name = "trailing-text", strings = ["trailing=text"])
@OverrideVariant(name = "trailing-icon", strings = ["trailing=icon"])
@OverrideVariant(name = "trailing-checkbox", strings = ["trailing=checkbox"])
@OverrideVariant(name = "trailing-switch", strings = ["trailing=switch"])
@OverrideVariant(name = "no-leading", strings = ["leading=none"])
@OverrideVariant(name = "three-line-switch", strings = ["lines=3", "trailing=switch"])
@Composable
fun ListItemSticker() = Sticker {
  val lines = previewOverrideString("lines", "2").toIntOrNull() ?: 2
  Column(Modifier.width(340.dp)) {
    ListItem(
      headlineContent = { Text("List item") },
      supportingContent =
        if (lines >= 2) {
          {
            Text(
              if (lines >= 3)
                "Supporting text that runs long enough to wrap onto a second line and make this a three-line row."
              else "Supporting text"
            )
          }
        } else null,
      overlineContent = if (lines >= 3) ({ Text("OVERLINE") }) else null,
      leadingContent = leading(),
      trailingContent = trailing(),
    )
  }
}

@CatalogComponent(
  id = "List/Group",
  caption = "Several rows with dividers — the shape a real list takes.",
)
@CatalogModes
@Composable
fun ListItemGroup() = Sticker {
  Column(Modifier.width(340.dp)) {
    listOf("Alice", "Bala", "Chen").forEachIndexed { index, name ->
      if (index > 0) HorizontalDivider()
      ListItem(
        headlineContent = { Text(name) },
        supportingContent = { Text("Last seen recently") },
        leadingContent = { Icon(Icons.Filled.Person, contentDescription = null) },
      )
    }
  }
}
