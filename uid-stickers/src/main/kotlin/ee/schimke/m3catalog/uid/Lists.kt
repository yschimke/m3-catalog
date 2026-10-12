@file:CatalogGroup(name = "Lists", section = "Containment")

package ee.schimke.m3catalog.uid

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogChoice
import ee.schimke.m3catalog.catalogText
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.list_item
import ee.schimke.m3catalog.generated.resources.list_last_seen
import ee.schimke.m3catalog.generated.resources.list_overline
import ee.schimke.m3catalog.generated.resources.list_supporting
import ee.schimke.m3catalog.generated.resources.list_supporting_long
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

// Two documents. The default render is the kit's six-row frame, `stickers/list-frame.uid`: a
// `KitListFrame` column of transparent 64dp `ListItem`s, each with the outlined stars glyph, the
// nudged headline and one-line supporting text, and the `⌘C ▸` shortcut; its copy is one binding
// per row. Any seeded axis draws the single row instead, `stickers/list-item.uid`: a `KitListItem`
// (two lines, the person icon, no trailing content) whose LINE COUNT, LEADING and TRAILING axes
// change the tree, so they cross as thirty cells (`stickers/list-item.json`). A checkbox or switch
// trails with `checked` bound and its change an action-free toggle intent, so the tick lives here
// (`toggleable`), as the hand-written sticker keeps it.

private const val LEADING_DEFAULT = "icon"
private const val TRAILING_DEFAULT = "none"
private const val LINES_DEFAULT = "figma"

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
