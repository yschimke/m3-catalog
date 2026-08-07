@file:CatalogGroup(name = "Badges", section = "Communication")

package ee.schimke.m3catalog.sections

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import ee.schimke.composeai.overrides.previewOverrideString
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker

// A badge is a status carrier, not a control: nothing to respond to, so it ships no handler.
//
// The kit's axis is the LABEL, and digit count is not cosmetic — the container grows from a circle
// at one digit to a pill at three, and overflows to "999+" past its cap. Each width is its own
// cell.

@CatalogComponent(
  id = "Badge/Number",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/51592:4768",
  caption = "Unread count over its anchor icon. One to three digits and overflow fold in.",
)
@CatalogModes
@OverrideVariant(name = "digits-2", strings = ["label=42"])
@OverrideVariant(name = "digits-3", strings = ["label=147"])
@OverrideVariant(name = "overflow", strings = ["label=999+"])
@Composable
fun NumberBadge() = Sticker {
  BadgedBox(badge = { Badge { Text(previewOverrideString("label", "8")) } }) {
    Icon(Icons.Filled.MailOutline, contentDescription = "Mail")
  }
}

@CatalogComponent(
  id = "Badge/Dot",
  caption = "Something changed, without a count — the smallest form.",
)
@CatalogModes
@Composable
fun DotBadge() = Sticker {
  BadgedBox(badge = { Badge() }) { Icon(Icons.Filled.MailOutline, contentDescription = "Mail") }
}
