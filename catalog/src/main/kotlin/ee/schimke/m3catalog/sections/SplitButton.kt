@file:CatalogGroup(name = "Split button", section = "Actions")
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.material3.SplitButtonLayout
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.counted
import ee.schimke.m3catalog.toggleable

// A split button pairs a primary action with a menu affordance: the leading half takes the click
// tally, the trailing half owns the expanded state.

@CatalogComponent(
  id = "SplitButton/Filled",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/57994:15751",
  caption = "Primary action plus a related-choices affordance.",
)
@CatalogModes
@Composable
fun SplitButton() = Sticker {
  val c = counted("Edit")
  val (expanded, setExpanded) = toggleable(false)
  SplitButtonLayout(
    leadingButton = {
      SplitButtonDefaults.LeadingButton(onClick = c.onClick) {
        Icon(Icons.Filled.Edit, contentDescription = null)
        Text(c.label)
      }
    },
    trailingButton = {
      SplitButtonDefaults.TrailingButton(
        checked = expanded,
        onCheckedChange = { setExpanded(it) },
      ) {
        Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "More options")
      }
    },
  )
}
