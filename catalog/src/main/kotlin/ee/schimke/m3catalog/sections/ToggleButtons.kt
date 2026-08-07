@file:CatalogGroup(name = "Toggle buttons", section = "Actions")
@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material3.ElevatedToggleButton
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.OutlinedToggleButton
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.TonalToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogButtonSize
import ee.schimke.m3catalog.catalogToggleButtonShapes
import ee.schimke.m3catalog.toggleable

// A toggle button is a button whose selection persists, so unlike the plain buttons it owns state
// and the interactive lane really flips it. It also shape-shifts when checked — M3 gives the
// checked state its own corner — which `catalogToggleButtonShape` resolves from BOTH the size and
// the checked flag, where `catalogButtonShape` needs neither.

@CatalogComponent(
  id = "ToggleButton/Filled",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/57994:2328",
  caption = "A button whose selection persists. Five sizes x two shapes fold in as variants.",
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
fun ToggleButtonSticker() = Sticker {
  val (on, set) = toggleable(true)
  val size = catalogButtonSize()
  ToggleButton(
    checked = on,
    onCheckedChange = set,
    shapes = catalogToggleButtonShapes(size),
    contentPadding = size.contentPadding,
    modifier = Modifier.defaultMinSize(minHeight = size.containerHeight),
  ) {
    ProvideTextStyle(size.labelStyle) { Text(if (on) "On" else "Filled") }
  }
}

@CatalogComponent(
  id = "ToggleButton/Tonal",
  caption = "Secondary emphasis toggle. Five sizes x two shapes fold in as variants.",
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
fun TonalToggleButtonSticker() = Sticker {
  val (on, set) = toggleable(true)
  val size = catalogButtonSize()
  TonalToggleButton(
    checked = on,
    onCheckedChange = set,
    shapes = catalogToggleButtonShapes(size),
    contentPadding = size.contentPadding,
    modifier = Modifier.defaultMinSize(minHeight = size.containerHeight),
  ) {
    ProvideTextStyle(size.labelStyle) { Text(if (on) "On" else "Tonal") }
  }
}

@CatalogComponent(
  id = "ToggleButton/Outlined",
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
fun OutlinedToggleButtonSticker() = Sticker {
  val (on, set) = toggleable(false)
  val size = catalogButtonSize()
  OutlinedToggleButton(
    checked = on,
    onCheckedChange = set,
    shapes = catalogToggleButtonShapes(size),
    contentPadding = size.contentPadding,
    modifier = Modifier.defaultMinSize(minHeight = size.containerHeight),
  ) {
    ProvideTextStyle(size.labelStyle) { Text(if (on) "On" else "Outlined") }
  }
}

@CatalogComponent(
  id = "ToggleButton/Elevated",
  caption = "Separated by shadow. Five sizes x two shapes fold in as variants.",
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
fun ElevatedToggleButtonSticker() = Sticker {
  val (on, set) = toggleable(false)
  val size = catalogButtonSize()
  ElevatedToggleButton(
    checked = on,
    onCheckedChange = set,
    shapes = catalogToggleButtonShapes(size),
    contentPadding = size.contentPadding,
    modifier = Modifier.defaultMinSize(minHeight = size.containerHeight),
  ) {
    ProvideTextStyle(size.labelStyle) { Text(if (on) "On" else "Elevated") }
  }
}
