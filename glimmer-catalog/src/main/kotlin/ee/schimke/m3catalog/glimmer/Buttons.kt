@file:CatalogGroup(name = "Button", section = "Actions")

package ee.schimke.m3catalog.glimmer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.xr.glimmer.Button
import androidx.xr.glimmer.ButtonSize
import androidx.xr.glimmer.Icon
import androidx.xr.glimmer.Text
import androidx.xr.glimmer.ToggleButton
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant

// Glimmer publishes ONE button, sized rather than styled: there is no filled / outlined / tonal
// axis to split on, which is the axis `m3-catalog` keeps a component per. `ButtonSize.Large` is a
// variant of the same component, and the icon slots are content rather than kind.

@CatalogComponent(
  id = "Button",
  noReference =
    "Glimmer publishes no Figma kit. There is nothing to compare against, and no kit node exists " +
      "to name — this is an audited absence, not a mapping nobody has done yet.",
  caption = "The primary action. Two sizes and the leading / trailing icon slots fold in.",
)
@Preview(
  device = AI_GLASSES_DEVICE_SPEC,
  showBackground = true,
  backgroundColor = ADDITIVE_ZERO_BACKGROUND,
)
@Composable
fun ButtonSticker() = Sticker { Button(onClick = {}) { Text("Send") } }

@CatalogVariant(of = "Button", props = ["size=Large"], caption = "The larger of the two sizes.")
@Preview(
  device = AI_GLASSES_DEVICE_SPEC,
  showBackground = true,
  backgroundColor = ADDITIVE_ZERO_BACKGROUND,
)
@Composable
fun ButtonLargeSticker() = Sticker {
  Button(onClick = {}, buttonSize = ButtonSize.Large) { Text("Send") }
}

@CatalogVariant(
  of = "Button",
  props = ["content=leading-icon"],
  caption = "Icon before the label.",
)
@Preview(
  device = AI_GLASSES_DEVICE_SPEC,
  showBackground = true,
  backgroundColor = ADDITIVE_ZERO_BACKGROUND,
)
@Composable
fun ButtonLeadingIconSticker() = Sticker {
  Button(onClick = {}, leadingIcon = { Icon(StarIcon, "Favourite") }) { Text("Send") }
}

@CatalogVariant(
  of = "Button",
  props = ["content=trailing-icon"],
  caption = "Icon after the label.",
)
@Preview(
  device = AI_GLASSES_DEVICE_SPEC,
  showBackground = true,
  backgroundColor = ADDITIVE_ZERO_BACKGROUND,
)
@Composable
fun ButtonTrailingIconSticker() = Sticker {
  Button(onClick = {}, trailingIcon = { Icon(StarIcon, "Favourite") }) { Text("Send") }
}

// The toggle is its own component rather than a Button variant: it carries checked state, its own
// colours, and a corner size that MORPHS between checked and unchecked. That is a different
// control, not a state of this one.

@CatalogComponent(
  id = "ToggleButton",
  noReference = "Glimmer publishes no Figma kit; see Button.",
  caption = "A button that holds its state. The corner morphs between checked and unchecked.",
)
@Preview(
  device = AI_GLASSES_DEVICE_SPEC,
  showBackground = true,
  backgroundColor = ADDITIVE_ZERO_BACKGROUND,
)
@Composable
fun ToggleButtonSticker() = Sticker {
  // Stateful on purpose: a live click on the preview server has to answer with the component's
  // own checked state, not with a label the sticker swapped underneath it.
  var checked by remember { mutableStateOf(false) }
  ToggleButton(checked = checked, onCheckedChange = { checked = it }) {
    Text(if (checked) "Toggle on" else "Toggle off")
  }
}

@CatalogVariant(
  of = "ToggleButton",
  state = "checked",
  caption = "Checked, where the corner size differs from the unchecked default.",
)
@Preview(
  device = AI_GLASSES_DEVICE_SPEC,
  showBackground = true,
  backgroundColor = ADDITIVE_ZERO_BACKGROUND,
)
@Composable
fun ToggleButtonCheckedSticker() = Sticker {
  var checked by remember { mutableStateOf(true) }
  ToggleButton(checked = checked, onCheckedChange = { checked = it }) {
    Text(if (checked) "Toggle on" else "Toggle off")
  }
}
