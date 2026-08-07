@file:CatalogGroup(name = "Buttons", section = "Actions")
@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogButtonShape
import ee.schimke.m3catalog.catalogButtonSize
import ee.schimke.m3catalog.counted

// The five common M3 buttons, highest to lowest emphasis.
//
// Each is ONE `@Preview` carrying the whole expressive matrix — five sizes x two shapes — through
// the `size` / `shape` knobs and a stacked `@OverrideVariant` per cell. The alternative, fifty
// near-identical `@Composable`s, would say the same thing in fifty places and drift in forty-nine
// of them. The unseeded render (small, round) is byte-identical to what a bare `Button(...)`
// produces, so the default sticker did not move when the matrix arrived.
//
// The kit's own Button component set carries exactly these variant properties, and design-parity
// walks them looking for a candidate render of each — so an un-fanned catalog reports "reference
// variant has no candidate render" for every cell it does not cover.

/**
 * The button's label at the type scale its size carries.
 *
 * M3 scales type with the container rather than holding one label size across a 32dp-to-136dp
 * range, so a size variant that changed only the box would render a correct height around visibly
 * wrong text.
 */
@Composable
private fun SizedLabel(label: String) {
  ProvideTextStyle(catalogButtonSize().labelStyle) { Text(label) }
}

@CatalogComponent(
  id = "Button/Filled",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/57994:2227",
  caption = "Highest emphasis; the primary action. Five sizes x two shapes fold in as variants.",
)
@CatalogModes
@OverrideVariant(name = "xs", strings = ["size=xs"])
@OverrideVariant(name = "xs-square", strings = ["size=xs", "shape=square"])
@OverrideVariant(name = "s-square", strings = ["size=s", "shape=square"])
@OverrideVariant(name = "m", strings = ["size=m"])
@OverrideVariant(name = "m-square", strings = ["size=m", "shape=square"])
@OverrideVariant(name = "l", strings = ["size=l"])
@OverrideVariant(name = "l-square", strings = ["size=l", "shape=square"])
@OverrideVariant(name = "xl", strings = ["size=xl"])
@OverrideVariant(name = "xl-square", strings = ["size=xl", "shape=square"])
@Composable
fun FilledButton() = Sticker {
  val c = counted("Filled")
  val size = catalogButtonSize()
  Button(
    onClick = c.onClick,
    shape = catalogButtonShape(),
    contentPadding = size.contentPadding,
    modifier = Modifier.defaultMinSize(minHeight = size.containerHeight),
  ) {
    SizedLabel(c.label)
  }
}

@CatalogComponent(
  id = "Button/Tonal",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58651:11237",
  caption = "Secondary, still prominent. Five sizes x two shapes fold in as variants.",
)
@CatalogModes
@OverrideVariant(name = "xs", strings = ["size=xs"])
@OverrideVariant(name = "xs-square", strings = ["size=xs", "shape=square"])
@OverrideVariant(name = "s-square", strings = ["size=s", "shape=square"])
@OverrideVariant(name = "m", strings = ["size=m"])
@OverrideVariant(name = "m-square", strings = ["size=m", "shape=square"])
@OverrideVariant(name = "l", strings = ["size=l"])
@OverrideVariant(name = "l-square", strings = ["size=l", "shape=square"])
@OverrideVariant(name = "xl", strings = ["size=xl"])
@OverrideVariant(name = "xl-square", strings = ["size=xl", "shape=square"])
@Composable
fun TonalButton() = Sticker {
  val c = counted("Tonal")
  val size = catalogButtonSize()
  FilledTonalButton(
    onClick = c.onClick,
    shape = catalogButtonShape(),
    contentPadding = size.contentPadding,
    modifier = Modifier.defaultMinSize(minHeight = size.containerHeight),
  ) {
    SizedLabel(c.label)
  }
}

@CatalogComponent(
  id = "Button/Outlined",
  caption = "Medium emphasis on a busy surface. Five sizes x two shapes fold in as variants.",
)
@CatalogModes
@OverrideVariant(name = "xs", strings = ["size=xs"])
@OverrideVariant(name = "xs-square", strings = ["size=xs", "shape=square"])
@OverrideVariant(name = "s-square", strings = ["size=s", "shape=square"])
@OverrideVariant(name = "m", strings = ["size=m"])
@OverrideVariant(name = "m-square", strings = ["size=m", "shape=square"])
@OverrideVariant(name = "l", strings = ["size=l"])
@OverrideVariant(name = "l-square", strings = ["size=l", "shape=square"])
@OverrideVariant(name = "xl", strings = ["size=xl"])
@OverrideVariant(name = "xl-square", strings = ["size=xl", "shape=square"])
@Composable
fun OutlinedButtonSticker() = Sticker {
  val c = counted("Outlined")
  val size = catalogButtonSize()
  OutlinedButton(
    onClick = c.onClick,
    shape = catalogButtonShape(),
    contentPadding = size.contentPadding,
    modifier = Modifier.defaultMinSize(minHeight = size.containerHeight),
  ) {
    SizedLabel(c.label)
  }
}

@CatalogComponent(
  id = "Button/Elevated",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58650:9294",
  caption = "Outlined alternative needing separation. Five sizes x two shapes fold in as variants.",
)
@CatalogModes
@OverrideVariant(name = "xs", strings = ["size=xs"])
@OverrideVariant(name = "xs-square", strings = ["size=xs", "shape=square"])
@OverrideVariant(name = "s-square", strings = ["size=s", "shape=square"])
@OverrideVariant(name = "m", strings = ["size=m"])
@OverrideVariant(name = "m-square", strings = ["size=m", "shape=square"])
@OverrideVariant(name = "l", strings = ["size=l"])
@OverrideVariant(name = "l-square", strings = ["size=l", "shape=square"])
@OverrideVariant(name = "xl", strings = ["size=xl"])
@OverrideVariant(name = "xl-square", strings = ["size=xl", "shape=square"])
@Composable
fun ElevatedButtonSticker() = Sticker {
  val c = counted("Elevated")
  val size = catalogButtonSize()
  ElevatedButton(
    onClick = c.onClick,
    shape = catalogButtonShape(),
    contentPadding = size.contentPadding,
    modifier = Modifier.defaultMinSize(minHeight = size.containerHeight),
  ) {
    SizedLabel(c.label)
  }
}

@CatalogComponent(
  id = "Button/Text",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58650:8094",
  caption = "Lowest emphasis; inline actions. Five sizes x two shapes fold in as variants.",
)
@CatalogModes
@OverrideVariant(name = "xs", strings = ["size=xs"])
@OverrideVariant(name = "xs-square", strings = ["size=xs", "shape=square"])
@OverrideVariant(name = "s-square", strings = ["size=s", "shape=square"])
@OverrideVariant(name = "m", strings = ["size=m"])
@OverrideVariant(name = "m-square", strings = ["size=m", "shape=square"])
@OverrideVariant(name = "l", strings = ["size=l"])
@OverrideVariant(name = "l-square", strings = ["size=l", "shape=square"])
@OverrideVariant(name = "xl", strings = ["size=xl"])
@OverrideVariant(name = "xl-square", strings = ["size=xl", "shape=square"])
@Composable
fun TextButtonSticker() = Sticker {
  val c = counted("Text")
  val size = catalogButtonSize()
  TextButton(
    onClick = c.onClick,
    shape = catalogButtonShape(),
    contentPadding = size.contentPadding,
    modifier = Modifier.defaultMinSize(minHeight = size.containerHeight),
  ) {
    SizedLabel(c.label)
  }
}

// --- State and content axes, folded under Button/Filled ----------------------------------------
// Disabled stays inert by design: unresponsiveness is the state it documents.

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
