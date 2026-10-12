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
import ee.schimke.m3catalog.catalogText
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.action_more
import ee.schimke.m3catalog.generated.resources.list_item
import ee.schimke.m3catalog.generated.resources.list_last_seen
import ee.schimke.m3catalog.generated.resources.list_overline
import ee.schimke.m3catalog.generated.resources.list_supporting
import ee.schimke.m3catalog.generated.resources.list_supporting_long
import ee.schimke.m3catalog.localizedDigits
import ee.schimke.m3catalog.toggleable
import generated.uibuilder.KitListFrame
import generated.uibuilder.KitListItem
import generated.uibuilder.KitListItemCheckbox
import generated.uibuilder.KitListItemIcon
import generated.uibuilder.KitListItemNoLeading
import generated.uibuilder.KitListItemNoLeadingCheckbox
import generated.uibuilder.KitListItemNoLeadingIcon
import generated.uibuilder.KitListItemNoLeadingSwitch
import generated.uibuilder.KitListItemNoLeadingText
import generated.uibuilder.KitListItemOne
import generated.uibuilder.KitListItemOneCheckbox
import generated.uibuilder.KitListItemOneIcon
import generated.uibuilder.KitListItemOneNoLeading
import generated.uibuilder.KitListItemOneNoLeadingCheckbox
import generated.uibuilder.KitListItemOneNoLeadingIcon
import generated.uibuilder.KitListItemOneNoLeadingSwitch
import generated.uibuilder.KitListItemOneNoLeadingText
import generated.uibuilder.KitListItemOneSwitch
import generated.uibuilder.KitListItemOneText
import generated.uibuilder.KitListItemSwitch
import generated.uibuilder.KitListItemText
import generated.uibuilder.KitListItemThree
import generated.uibuilder.KitListItemThreeCheckbox
import generated.uibuilder.KitListItemThreeIcon
import generated.uibuilder.KitListItemThreeNoLeading
import generated.uibuilder.KitListItemThreeNoLeadingCheckbox
import generated.uibuilder.KitListItemThreeNoLeadingIcon
import generated.uibuilder.KitListItemThreeNoLeadingSwitch
import generated.uibuilder.KitListItemThreeNoLeadingText
import generated.uibuilder.KitListItemThreeSwitch
import generated.uibuilder.KitListItemThreeText
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

@Composable
private fun FigmaList() {
  Column(Modifier.width(280.dp)) {
    repeat(6) { index ->
      Box(Modifier.fillMaxWidth().height(64.dp)) {
        ListItem(
          headlineContent = {
            Text(
              catalogText("headline", stringResource(Res.string.list_item), index),
              modifier = Modifier.offset(x = (-6).dp),
            )
          },
          supportingContent = {
            Text(
              catalogText(
                "supportingText",
                stringResource(
                  if (index == 3) Res.string.list_supporting_long else Res.string.list_supporting
                ),
                index,
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

// --- Generated sticker bodies, from uid-stickers/Lists.kt ---

@CatalogComponent(
  id = "List/Item",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/59106:13049",
  referenceSet = "figma:ocdacdEsnHipMJD3egzxKb/51964:63037",
  caption =
    "The kit's list frame. Line count, leading and trailing slots all fold in as single rows.",
)
@CatalogModes
@OverrideVariant(
  name = "one-line",
  strings = ["lines=1", "leading=none"],
  kitProps = ["Type=Standard", "Multi-line=False"],
)
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
  val leadingSetting = catalogChoice("leading", LEADING_DEFAULT, "icon", "none")
  val trailingSetting =
    catalogChoice("trailing", TRAILING_DEFAULT, "none", "text", "icon", "checkbox", "switch")
  if (
    lineSetting == LINES_DEFAULT &&
      leadingSetting == LEADING_DEFAULT &&
      trailingSetting == TRAILING_DEFAULT
  ) {
    val headline = stringResource(Res.string.list_item)
    val supporting = stringResource(Res.string.list_supporting)
    KitListFrame(
      Modifier,
      headline0 = catalogText("headline", headline, 0),
      supportingText0 = catalogText("supportingText", supporting, 0),
      headline1 = catalogText("headline", headline, 1),
      supportingText1 = catalogText("supportingText", supporting, 1),
      headline2 = catalogText("headline", headline, 2),
      supportingText2 = catalogText("supportingText", supporting, 2),
      headline3 = catalogText("headline", headline, 3),
      supportingText3 =
        catalogText("supportingText", stringResource(Res.string.list_supporting_long), 3),
      headline4 = catalogText("headline", headline, 4),
      supportingText4 = catalogText("supportingText", supporting, 4),
      headline5 = catalogText("headline", headline, 5),
      supportingText5 = catalogText("supportingText", supporting, 5),
    )
    return@Sticker
  }
  val lines = lineSetting.toIntOrNull() ?: 2
  var checked by toggleable(true)
  val trailingText = localizedDigits("10:30")
  val headline = catalogText("headline", stringResource(Res.string.list_item))
  val supportingText =
    if (lines >= 2)
      catalogText(
        "supportingText",
        stringResource(
          if (lines >= 3) Res.string.list_supporting_long else Res.string.list_supporting
        ),
      )
    else ""
  val supportingText2 =
    if (lines >= 3) catalogText("supportingText2", stringResource(Res.string.list_last_seen))
    else ""
  val overline =
    if (lines >= 3) catalogText("overline", stringResource(Res.string.list_overline)) else ""
  when {
    lines == 1 && leadingSetting == "icon" && trailingSetting == "none" ->
      KitListItemOne(
        Modifier,
        headline = headline,
      )
    lines == 1 && leadingSetting == "icon" && trailingSetting == "text" ->
      KitListItemOneText(
        Modifier,
        headline = headline,
        trailingText = trailingText,
      )
    lines == 1 && leadingSetting == "icon" && trailingSetting == "icon" ->
      KitListItemOneIcon(
        Modifier,
        headline = headline,
      )
    lines == 1 && leadingSetting == "icon" && trailingSetting == "checkbox" ->
      KitListItemOneCheckbox(
        Modifier,
        headline = headline,
        toggleChecked = checked,
        onToggleCheckedChange = { checked = !checked },
      )
    lines == 1 && leadingSetting == "icon" && trailingSetting == "switch" ->
      KitListItemOneSwitch(
        Modifier,
        headline = headline,
        toggleChecked = checked,
        onToggleCheckedChange = { checked = !checked },
      )
    lines == 1 && leadingSetting == "none" && trailingSetting == "none" ->
      KitListItemOneNoLeading(
        Modifier,
        headline = headline,
      )
    lines == 1 && leadingSetting == "none" && trailingSetting == "text" ->
      KitListItemOneNoLeadingText(
        Modifier,
        headline = headline,
        trailingText = trailingText,
      )
    lines == 1 && leadingSetting == "none" && trailingSetting == "icon" ->
      KitListItemOneNoLeadingIcon(
        Modifier,
        headline = headline,
      )
    lines == 1 && leadingSetting == "none" && trailingSetting == "checkbox" ->
      KitListItemOneNoLeadingCheckbox(
        Modifier,
        headline = headline,
        toggleChecked = checked,
        onToggleCheckedChange = { checked = !checked },
      )
    lines == 1 && leadingSetting == "none" && trailingSetting == "switch" ->
      KitListItemOneNoLeadingSwitch(
        Modifier,
        headline = headline,
        toggleChecked = checked,
        onToggleCheckedChange = { checked = !checked },
      )
    lines == 2 && leadingSetting == "icon" && trailingSetting == "none" ->
      KitListItem(
        Modifier,
        headline = headline,
        supportingText = supportingText,
      )
    lines == 2 && leadingSetting == "icon" && trailingSetting == "text" ->
      KitListItemText(
        Modifier,
        headline = headline,
        supportingText = supportingText,
        trailingText = trailingText,
      )
    lines == 2 && leadingSetting == "icon" && trailingSetting == "icon" ->
      KitListItemIcon(
        Modifier,
        headline = headline,
        supportingText = supportingText,
      )
    lines == 2 && leadingSetting == "icon" && trailingSetting == "checkbox" ->
      KitListItemCheckbox(
        Modifier,
        headline = headline,
        supportingText = supportingText,
        toggleChecked = checked,
        onToggleCheckedChange = { checked = !checked },
      )
    lines == 2 && leadingSetting == "icon" && trailingSetting == "switch" ->
      KitListItemSwitch(
        Modifier,
        headline = headline,
        supportingText = supportingText,
        toggleChecked = checked,
        onToggleCheckedChange = { checked = !checked },
      )
    lines == 2 && leadingSetting == "none" && trailingSetting == "none" ->
      KitListItemNoLeading(
        Modifier,
        headline = headline,
        supportingText = supportingText,
      )
    lines == 2 && leadingSetting == "none" && trailingSetting == "text" ->
      KitListItemNoLeadingText(
        Modifier,
        headline = headline,
        supportingText = supportingText,
        trailingText = trailingText,
      )
    lines == 2 && leadingSetting == "none" && trailingSetting == "icon" ->
      KitListItemNoLeadingIcon(
        Modifier,
        headline = headline,
        supportingText = supportingText,
      )
    lines == 2 && leadingSetting == "none" && trailingSetting == "checkbox" ->
      KitListItemNoLeadingCheckbox(
        Modifier,
        headline = headline,
        supportingText = supportingText,
        toggleChecked = checked,
        onToggleCheckedChange = { checked = !checked },
      )
    lines == 2 && leadingSetting == "none" && trailingSetting == "switch" ->
      KitListItemNoLeadingSwitch(
        Modifier,
        headline = headline,
        supportingText = supportingText,
        toggleChecked = checked,
        onToggleCheckedChange = { checked = !checked },
      )
    lines == 3 && leadingSetting == "icon" && trailingSetting == "none" ->
      KitListItemThree(
        Modifier,
        headline = headline,
        supportingText = supportingText,
        supportingText2 = supportingText2,
        overline = overline,
      )
    lines == 3 && leadingSetting == "icon" && trailingSetting == "text" ->
      KitListItemThreeText(
        Modifier,
        headline = headline,
        supportingText = supportingText,
        supportingText2 = supportingText2,
        overline = overline,
        trailingText = trailingText,
      )
    lines == 3 && leadingSetting == "icon" && trailingSetting == "icon" ->
      KitListItemThreeIcon(
        Modifier,
        headline = headline,
        supportingText = supportingText,
        supportingText2 = supportingText2,
        overline = overline,
      )
    lines == 3 && leadingSetting == "icon" && trailingSetting == "checkbox" ->
      KitListItemThreeCheckbox(
        Modifier,
        headline = headline,
        supportingText = supportingText,
        supportingText2 = supportingText2,
        overline = overline,
        toggleChecked = checked,
        onToggleCheckedChange = { checked = !checked },
      )
    lines == 3 && leadingSetting == "icon" && trailingSetting == "switch" ->
      KitListItemThreeSwitch(
        Modifier,
        headline = headline,
        supportingText = supportingText,
        supportingText2 = supportingText2,
        overline = overline,
        toggleChecked = checked,
        onToggleCheckedChange = { checked = !checked },
      )
    lines == 3 && leadingSetting == "none" && trailingSetting == "none" ->
      KitListItemThreeNoLeading(
        Modifier,
        headline = headline,
        supportingText = supportingText,
        supportingText2 = supportingText2,
        overline = overline,
      )
    lines == 3 && leadingSetting == "none" && trailingSetting == "text" ->
      KitListItemThreeNoLeadingText(
        Modifier,
        headline = headline,
        supportingText = supportingText,
        supportingText2 = supportingText2,
        overline = overline,
        trailingText = trailingText,
      )
    lines == 3 && leadingSetting == "none" && trailingSetting == "icon" ->
      KitListItemThreeNoLeadingIcon(
        Modifier,
        headline = headline,
        supportingText = supportingText,
        supportingText2 = supportingText2,
        overline = overline,
      )
    lines == 3 && leadingSetting == "none" && trailingSetting == "checkbox" ->
      KitListItemThreeNoLeadingCheckbox(
        Modifier,
        headline = headline,
        supportingText = supportingText,
        supportingText2 = supportingText2,
        overline = overline,
        toggleChecked = checked,
        onToggleCheckedChange = { checked = !checked },
      )
    lines == 3 && leadingSetting == "none" && trailingSetting == "switch" ->
      KitListItemThreeNoLeadingSwitch(
        Modifier,
        headline = headline,
        supportingText = supportingText,
        supportingText2 = supportingText2,
        overline = overline,
        toggleChecked = checked,
        onToggleCheckedChange = { checked = !checked },
      )
    else -> error("unreachable: lines=$lines leading=$leadingSetting trailing=$trailingSetting")
  }
}
