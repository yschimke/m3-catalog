@file:CatalogGroup(name = "Lists", section = "Containment")
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker

@CatalogComponent(
  id = "List/OneLine",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/59106:13028",
  caption = "A single line of primary text.",
)
@CatalogModes
@Composable
fun OneLineListItem() = Sticker {
  Column(Modifier.width(320.dp)) { ListItem(headlineContent = { Text("List item") }) }
}

@CatalogVariant(
  of = "List/OneLine",
  props = ["lines=2"],
  caption = "Headline plus supporting text.",
)
@CatalogModes
@Composable
fun TwoLineListItem() = Sticker {
  Column(Modifier.width(320.dp)) {
    ListItem(
      headlineContent = { Text("List item") },
      supportingContent = { Text("Supporting text") },
      leadingContent = { Icon(Icons.Filled.Person, contentDescription = null) },
    )
  }
}

@CatalogVariant(
  of = "List/OneLine",
  props = ["content=group"],
  caption = "Several items with dividers — the shape a real list takes.",
)
@CatalogModes
@Composable
fun ListItemGroup() = Sticker {
  Column(Modifier.width(320.dp)) {
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
