@file:CatalogGroup(name = "Split button", section = "Actions")
@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.material3.SplitButtonLayout
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogFilledStars
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogButtonSize
import ee.schimke.m3catalog.catalogChoice
import ee.schimke.m3catalog.counted
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.action_more_options
import ee.schimke.m3catalog.generated.resources.label_text
import ee.schimke.m3catalog.splitLeadingContentPadding
import ee.schimke.m3catalog.splitTrailingContentPadding
import ee.schimke.m3catalog.splitTrailingIconSize
import ee.schimke.m3catalog.toggleable
import org.jetbrains.compose.resources.stringResource

// A split button pairs a primary action with a menu affordance: the leading half takes the click
// tally, the trailing half owns the expanded state.
//
// Five sizes and no shape axis — the kit models a split button's corners as inner/outer corner
// sizes that the component derives, not as a round/square variant property the way a plain button
// does. Adding a shape knob here would invent an axis the kit does not document.

@CatalogComponent(
  id = "SplitButton/Filled",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/57994:16184",
  caption =
    "Primary action plus a related-choices affordance. Five sizes and four colours fold in.",
)
@CatalogModes
@OverrideVariant(name = "xs", strings = ["size=xs"])
@OverrideVariant(name = "m", strings = ["size=m"])
@OverrideVariant(name = "l", strings = ["size=l"])
@OverrideVariant(name = "xl", strings = ["size=xl"])
@OverrideVariant(name = "tonal", strings = ["color=tonal"])
@OverrideVariant(name = "outlined", strings = ["color=outlined"])
@OverrideVariant(name = "elevated", strings = ["color=elevated"])
@Composable
fun SplitButton() = Sticker {
  val c = counted(stringResource(Res.string.label_text))
  val (expanded, setExpanded) = toggleable(false)
  val size = catalogButtonSize()
  val colour = catalogChoice("color", "filled", "filled", "tonal", "outlined", "elevated")
  val label: @Composable RowScope.() -> Unit = {
    Icon(CatalogFilledStars, contentDescription = null, modifier = Modifier.size(size.iconSize))
    // `LeadingButton` lays its content out as a bare centred `Row` and adds no spacing of its own,
    // so the icon-to-label gap is the caller's to supply — exactly as the upstream
    // `FilledSplitButtonSample` does. Without it the leading half renders 8dp narrow with the glyph
    // welded to the label, which is what the kit comparison was flagging.
    Spacer(Modifier.width(size.iconSpacing))
    ProvideTextStyle(size.labelStyle) { Text(c.label) }
  }
  val chevron: @Composable RowScope.() -> Unit = {
    Icon(
      Icons.Filled.KeyboardArrowDown,
      contentDescription = stringResource(Res.string.action_more_options),
      modifier = Modifier.size(size.splitTrailingIconSize),
    )
  }
  SplitButtonLayout(
    leadingButton = {
      val padding = size.splitLeadingContentPadding
      when (colour) {
        "tonal" ->
          SplitButtonDefaults.TonalLeadingButton(
            c.onClick,
            contentPadding = padding,
            content = label,
          )
        "outlined" ->
          SplitButtonDefaults.OutlinedLeadingButton(
            c.onClick,
            contentPadding = padding,
            content = label,
          )
        "elevated" ->
          SplitButtonDefaults.ElevatedLeadingButton(
            c.onClick,
            contentPadding = padding,
            content = label,
          )
        else ->
          SplitButtonDefaults.LeadingButton(c.onClick, contentPadding = padding, content = label)
      }
    },
    trailingButton = {
      val padding = size.splitTrailingContentPadding
      val onCheck: (Boolean) -> Unit = { setExpanded(it) }
      when (colour) {
        "tonal" ->
          SplitButtonDefaults.TonalTrailingButton(
            expanded,
            onCheck,
            contentPadding = padding,
            content = chevron,
          )
        "outlined" ->
          SplitButtonDefaults.OutlinedTrailingButton(
            expanded,
            onCheck,
            contentPadding = padding,
            content = chevron,
          )
        "elevated" ->
          SplitButtonDefaults.ElevatedTrailingButton(
            expanded,
            onCheck,
            contentPadding = padding,
            content = chevron,
          )
        else ->
          SplitButtonDefaults.TrailingButton(
            expanded,
            onCheck,
            contentPadding = padding,
            content = chevron,
          )
      }
    },
  )
}
