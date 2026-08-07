@file:CatalogGroup(name = "Badges", section = "Communication")
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker

// A badge is a status carrier, not a control: it has nothing to respond to, so it ships no handler.

@CatalogComponent(id = "Badge/Number", caption = "Unread count over its anchor icon.")
@CatalogModes
@Composable
fun NumberBadge() = Sticker {
  BadgedBox(badge = { Badge { Text("8") } }) {
    Icon(Icons.Filled.MailOutline, contentDescription = "Mail")
  }
}

@CatalogVariant(
  of = "Badge/Number",
  props = ["content=none"],
  caption = "The small dot: something changed, without a count.",
)
@CatalogModes
@Composable
fun DotBadge() = Sticker {
  BadgedBox(badge = { Badge() }) { Icon(Icons.Filled.MailOutline, contentDescription = "Mail") }
}
