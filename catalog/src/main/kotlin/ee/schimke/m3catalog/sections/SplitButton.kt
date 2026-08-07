@file:CatalogGroup(name = "Split button", section = "Actions")
@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
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
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogButtonSize
import ee.schimke.m3catalog.counted
import ee.schimke.m3catalog.splitLeadingContentPadding
import ee.schimke.m3catalog.splitTrailingContentPadding
import ee.schimke.m3catalog.splitTrailingIconSize
import ee.schimke.m3catalog.toggleable

// A split button pairs a primary action with a menu affordance: the leading half takes the click
// tally, the trailing half owns the expanded state.
//
// Five sizes and no shape axis — the kit models a split button's corners as inner/outer corner
// sizes that the component derives, not as a round/square variant property the way a plain button
// does. Adding a shape knob here would invent an axis the kit does not document.

@CatalogComponent(
  id = "SplitButton/Filled",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/57994:16184",
  caption = "Primary action plus a related-choices affordance. Five sizes fold in as variants.",
)
@CatalogModes
@OverrideVariant(name = "xs", strings = ["size=xs"])
@OverrideVariant(name = "m", strings = ["size=m"])
@OverrideVariant(name = "l", strings = ["size=l"])
@OverrideVariant(name = "xl", strings = ["size=xl"])
@Composable
fun SplitButton() = Sticker {
  val c = counted("Edit")
  val (expanded, setExpanded) = toggleable(false)
  val size = catalogButtonSize()
  SplitButtonLayout(
    leadingButton = {
      SplitButtonDefaults.LeadingButton(
        onClick = c.onClick,
        contentPadding = size.splitLeadingContentPadding,
      ) {
        Icon(Icons.Filled.Edit, contentDescription = null)
        ProvideTextStyle(size.labelStyle) { Text(c.label) }
      }
    },
    trailingButton = {
      SplitButtonDefaults.TrailingButton(
        checked = expanded,
        onCheckedChange = { setExpanded(it) },
        contentPadding = size.splitTrailingContentPadding,
      ) {
        Icon(
          Icons.Filled.KeyboardArrowDown,
          contentDescription = "More options",
          modifier = Modifier.size(size.splitTrailingIconSize),
        )
      }
    },
  )
}
