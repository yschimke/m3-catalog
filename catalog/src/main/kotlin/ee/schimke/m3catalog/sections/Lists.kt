@file:CatalogGroup(name = "Lists", section = "Containment")

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.CatalogOutlinedStars
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogChoice
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.action_more
import ee.schimke.m3catalog.generated.resources.list_item
import ee.schimke.m3catalog.generated.resources.list_last_seen
import ee.schimke.m3catalog.generated.resources.list_overline
import ee.schimke.m3catalog.generated.resources.list_supporting
import ee.schimke.m3catalog.generated.resources.list_supporting_long
import ee.schimke.m3catalog.localizedDigits
import ee.schimke.m3catalog.toggleable
import org.jetbrains.compose.resources.stringResource

// Three axes the kit documents: LINE COUNT (one, two, three), the LEADING slot (none, icon,
// avatar), and the TRAILING slot (none, text, icon, checkbox, switch). They compose, so one
// preview carries the grid.

@Composable
private fun leading(): (@Composable () -> Unit)? =
  when (catalogChoice("leading", "icon", "icon", "none")) {
    "none" -> null
    else -> {
      { Icon(Icons.Filled.Person, contentDescription = null) }
    }
  }

@Composable
private fun trailing(): (@Composable () -> Unit)? {
  var checked by toggleable(true)
  return when (catalogChoice("trailing", "none", "none", "text", "icon", "checkbox", "switch")) {
    "text" -> {
      { Text(localizedDigits("10:30")) }
    }
    "icon" -> {
      { Icon(Icons.Filled.MoreVert, contentDescription = stringResource(Res.string.action_more)) }
    }
    "checkbox" -> {
      { Checkbox(checked = checked, onCheckedChange = { checked = it }) }
    }
    "switch" -> {
      { Switch(checked = checked, onCheckedChange = { checked = it }) }
    }
    else -> null
  }
}

@CatalogComponent(
  id = "List/Item",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/59106:13049",
  referenceSet = "figma:ocdacdEsnHipMJD3egzxKb/51964:63037",
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
  val lineSetting =
    catalogChoice(
      "lines",
      "figma",
      "figma" to "Figma default",
      "1" to "One line",
      "3" to "Three lines",
    )
  if (lineSetting == "figma") {
    FigmaList()
    return@Sticker
  }
  val lines = lineSetting.toIntOrNull() ?: 2
  Column(Modifier.width(340.dp)) {
    ListItem(
      headlineContent = { Text(stringResource(Res.string.list_item)) },
      supportingContent =
        if (lines >= 2) {
          {
            Text(
              stringResource(
                if (lines >= 3) Res.string.list_supporting_long else Res.string.list_supporting
              )
            )
            if (lines >= 3) Text(stringResource(Res.string.list_last_seen))
          }
        } else null,
      overlineContent =
        if (lines >= 3) ({ Text(stringResource(Res.string.list_overline)) }) else null,
      leadingContent = leading(),
      trailingContent = trailing(),
    )
  }
}

@CatalogComponent(
  id = "List/Group",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/59106:13049",
  caption = "Several rows with dividers — the shape a real list takes.",
)
@CatalogModes
@Composable
fun ListItemGroup() = Sticker { FigmaList() }

@Composable
private fun FigmaList() {
  Column(Modifier.width(280.dp)) {
    repeat(6) { index ->
      Box(Modifier.fillMaxWidth().height(64.dp)) {
        ListItem(
          headlineContent = {
            Text(stringResource(Res.string.list_item), modifier = Modifier.offset(x = (-6).dp))
          },
          supportingContent = {
            Text(
              stringResource(
                if (index == 3) Res.string.list_supporting_long else Res.string.list_supporting
              ),
              modifier = Modifier.offset(x = (-6).dp),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
            )
          },
          leadingContent = {
            Icon(CatalogOutlinedStars, contentDescription = null, modifier = Modifier.size(20.dp))
          },
          trailingContent = {
            Row(Modifier.offset(x = (-4).dp)) {
              Text("⌘C")
              Spacer(Modifier.width(12.dp))
              Icon(
                Icons.Filled.ArrowRight,
                contentDescription = null,
                modifier = Modifier.size(10.dp),
              )
            }
          },
          modifier = Modifier.height(64.dp),
          colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        )
      }
    }
  }
}
