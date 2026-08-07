@file:CatalogGroup(name = "Text fields", section = "Text inputs")
@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.editable

// Text fields own their text, so typing works on the live lane and the baked capture is frozen at
// the seeded value.

@CatalogComponent(
  id = "TextField/Filled",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/52798:24373",
  caption = "The default; a filled container.",
)
@CatalogModes
@Composable
fun FilledTextField() = Sticker {
  val (text, set) = editable("Alice")
  TextField(
    value = text,
    onValueChange = set,
    label = { Text("Name") },
    modifier = Modifier.width(280.dp),
  )
}

@CatalogComponent(id = "TextField/Outlined", caption = "Outlined container; less visual weight.")
@CatalogModes
@Composable
fun OutlinedTextFieldSticker() = Sticker {
  val (text, set) = editable("Alice")
  OutlinedTextField(
    value = text,
    onValueChange = set,
    label = { Text("Name") },
    modifier = Modifier.width(280.dp),
  )
}

@CatalogVariant(of = "TextField/Filled", state = "empty", caption = "Placeholder, no value yet.")
@CatalogModes
@Composable
fun FilledTextFieldEmpty() = Sticker {
  val (text, set) = editable("")
  TextField(
    value = text,
    onValueChange = set,
    label = { Text("Name") },
    placeholder = { Text("Your name") },
    modifier = Modifier.width(280.dp),
  )
}

@CatalogVariant(
  of = "TextField/Filled",
  state = "error",
  caption = "Invalid input, with support text.",
)
@CatalogModes
@Composable
fun FilledTextFieldError() = Sticker {
  TextField(
    value = "not-an-email",
    onValueChange = {},
    label = { Text("Email") },
    isError = true,
    supportingText = { Text("Enter a valid email address") },
    modifier = Modifier.width(280.dp),
  )
}

@CatalogVariant(of = "TextField/Filled", props = ["content=icon"], caption = "With a leading icon.")
@CatalogModes
@Composable
fun FilledTextFieldWithIcon() = Sticker {
  val (text, set) = editable("Material")
  TextField(
    value = text,
    onValueChange = set,
    label = { Text("Search") },
    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
    modifier = Modifier.width(280.dp),
  )
}

@CatalogVariant(of = "TextField/Filled", state = "disabled")
@CatalogModes
@Composable
fun FilledTextFieldDisabled() = Sticker {
  TextField(
    value = "Alice",
    onValueChange = {},
    label = { Text("Name") },
    enabled = false,
    modifier = Modifier.width(280.dp),
  )
}
