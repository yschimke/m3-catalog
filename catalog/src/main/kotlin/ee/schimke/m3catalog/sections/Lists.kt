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
//
// The DEFAULT render is the kit's own frame — six rows with dividers, the shape a real list takes —
// because that is what `Type=Standard, Multi-line=True` draws, and the single-row axes above are
// what fold onto it. There used to be a second `List/Group` component for that group frame naming
// the same node and calling the same `FigmaList()`; it was a byte-identical duplicate of this
// component's default capture, not a variant of it, so it is gone rather than folded.

// The three axes read as knobs, and their defaults. The sticker reads all three BEFORE it decides
// which body to draw, because the Figma frame below is only the right answer while every one of
// them is still at its default — see `ListItemSticker`.

private const val LEADING_DEFAULT = "icon"
private const val TRAILING_DEFAULT = "none"
private const val LINES_DEFAULT = "figma"

@Composable
private fun leadingSetting(): String = catalogChoice("leading", LEADING_DEFAULT, "icon", "none")

@Composable
private fun trailingSetting(): String =
  catalogChoice("trailing", TRAILING_DEFAULT, "none", "text", "icon", "checkbox", "switch")

@Composable
private fun leading(setting: String): (@Composable () -> Unit)? =
  when (setting) {
    "none" -> null
    else -> {
      { Icon(Icons.Filled.Person, contentDescription = null) }
    }
  }

@Composable
private fun trailing(setting: String): (@Composable () -> Unit)? {
  var checked by toggleable(true)
  return when (setting) {
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
  caption =
    "The kit's list frame. Line count, leading and trailing slots all fold in as single rows.",
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
      LINES_DEFAULT,
      "figma" to "Figma default",
      "1" to "One line",
      "3" to "Three lines",
    )
  val leadingSetting = leadingSetting()
  val trailingSetting = trailingSetting()
  // The kit's default IS the six-row frame, so an unseeded render draws it. It is only the right
  // answer while every axis is at its default, though: the early return used to be taken on the
  // `lines` knob alone, so the five cells that seed only `leading=` / `trailing=` never reached
  // the single row that shows them and published the default frame under their own names
  // (issue #176). A seeded row axis falls through to the two-line row, which is what those cells
  // are variants of.
  val everyAxisAtItsDefault =
    lineSetting == LINES_DEFAULT &&
      leadingSetting == LEADING_DEFAULT &&
      trailingSetting == TRAILING_DEFAULT
  if (everyAxisAtItsDefault) {
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
      leadingContent = leading(leadingSetting),
      trailingContent = trailing(trailingSetting),
    )
  }
}

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
