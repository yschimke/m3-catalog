@file:CatalogGroup(name = "Buttons", section = "Actions")

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.counted

// The five common M3 buttons, highest to lowest emphasis. Each is a thin `Sticker { … }` wrapper so
// the catalog identity (`@CatalogComponent`) sits next to the composable rather than being restated
// in `catalog.spec.json`, and each carries the click tally so a live session responds to a pointer.

@CatalogComponent(id = "Button/Filled", caption = "Highest emphasis; the primary action.")
@CatalogModes
@Composable
fun FilledButton() = Sticker {
  val c = counted("Filled")
  Button(onClick = c.onClick) { Text(c.label) }
}

@CatalogComponent(id = "Button/Tonal", caption = "Secondary, still prominent.")
@CatalogModes
@Composable
fun TonalButton() = Sticker {
  val c = counted("Tonal")
  FilledTonalButton(onClick = c.onClick) { Text(c.label) }
}

@CatalogComponent(id = "Button/Outlined", caption = "Medium emphasis on a busy surface.")
@CatalogModes
@Composable
fun OutlinedButtonSticker() = Sticker {
  val c = counted("Outlined")
  OutlinedButton(onClick = c.onClick) { Text(c.label) }
}

@CatalogComponent(id = "Button/Elevated", caption = "Outlined alternative needing separation.")
@CatalogModes
@Composable
fun ElevatedButtonSticker() = Sticker {
  val c = counted("Elevated")
  ElevatedButton(onClick = c.onClick) { Text(c.label) }
}

@CatalogComponent(id = "Button/Text", caption = "Lowest emphasis; inline actions.")
@CatalogModes
@Composable
fun TextButtonSticker() = Sticker {
  val c = counted("Text")
  TextButton(onClick = c.onClick) { Text(c.label) }
}

// --- Variants folded under Button/Filled ------------------------------------------------------
// A variant is a distinct render surfaced *under* its parent sticker rather than as its own
// component. Disabled stays inert by design — unresponsiveness is the state it documents.

@CatalogVariant(
  of = "Button/Filled",
  state = "disabled",
  caption = "enabled = false; the disabled container / content roles.",
)
@CatalogModes
@Composable
fun FilledButtonDisabled() = Sticker { Button(onClick = {}, enabled = false) { Text("Filled") } }

@CatalogVariant(
  of = "Button/Filled",
  props = ["content=icon+label"],
  caption = "Leading icon + label, vs the label-only default.",
)
@CatalogModes
@Composable
fun FilledButtonIconLabel() = Sticker {
  val c = counted("Filled")
  Button(onClick = c.onClick, contentPadding = ButtonDefaults.ButtonWithIconContentPadding) {
    Icon(
      Icons.Filled.Add,
      contentDescription = null,
      modifier = Modifier.size(ButtonDefaults.IconSize),
    )
    Spacer(Modifier.width(ButtonDefaults.IconSpacing))
    Text(c.label)
  }
}
