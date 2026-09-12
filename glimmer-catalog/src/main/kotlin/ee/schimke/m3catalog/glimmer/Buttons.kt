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
  // `State=Enabled, Size=Default` in the kit's `Button` set (`40:655`), whose axes are
  // `State=` x `Size=Default | Large`. The CELL rather than the set: a reference is the
  // node this sticker is a rendition OF, and the set is a family, not a drawing.
  reference = "figma:HKfLClZDLRyMhf4IQQLna8/40:660",
  caption = "The primary action. Two sizes and the leading / trailing icon slots fold in.",
)
@Preview(showBackground = true, backgroundColor = ADDITIVE_ZERO_BACKGROUND)
@Composable
fun ButtonSticker() = Sticker {
  val c = counted("Send")
  Button(onClick = c.onClick) { Text(c.label) }
}

@CatalogVariant(of = "Button", props = ["size=Large"], caption = "The larger of the two sizes.")
@Preview(showBackground = true, backgroundColor = ADDITIVE_ZERO_BACKGROUND)
@Composable
fun ButtonLargeSticker() = Sticker {
  val c = counted("Send")
  Button(onClick = c.onClick, buttonSize = ButtonSize.Large) { Text(c.label) }
}

@CatalogVariant(
  of = "Button",
  props = ["content=leading-icon"],
  caption = "Icon before the label.",
)
@Preview(showBackground = true, backgroundColor = ADDITIVE_ZERO_BACKGROUND)
@Composable
fun ButtonLeadingIconSticker() = Sticker {
  val c = counted("Send")
  Button(onClick = c.onClick, leadingIcon = { Icon(StarIcon, "Favourite") }) { Text(c.label) }
}

@CatalogVariant(
  of = "Button",
  props = ["content=trailing-icon"],
  caption = "Icon after the label.",
)
@Preview(showBackground = true, backgroundColor = ADDITIVE_ZERO_BACKGROUND)
@Composable
fun ButtonTrailingIconSticker() = Sticker {
  val c = counted("Send")
  Button(onClick = c.onClick, trailingIcon = { Icon(StarIcon, "Favourite") }) { Text(c.label) }
}

// The toggle is its own component rather than a Button variant: it carries checked state, its own
// colours, and a corner size that MORPHS between checked and unchecked. That is a different
// control, not a state of this one.

@CatalogComponent(
  id = "ToggleButton",
  // `State=Enabled, Size=Default, Toggle=False` in the `Toggle Button` set
  // (`40000113:3966`). Unchecked is the base because this sticker draws unchecked; the
  // `Toggle=True` cell is what `state = "checked"` below is a rendition of.
  reference = "figma:HKfLClZDLRyMhf4IQQLna8/40000113:3991",
  caption = "A button that holds its state. The corner morphs between checked and unchecked.",
)
@Preview(showBackground = true, backgroundColor = ADDITIVE_ZERO_BACKGROUND)
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
@Preview(showBackground = true, backgroundColor = ADDITIVE_ZERO_BACKGROUND)
@Composable
fun ToggleButtonCheckedSticker() = Sticker {
  var checked by remember { mutableStateOf(true) }
  ToggleButton(checked = checked, onCheckedChange = { checked = it }) {
    Text(if (checked) "Toggle on" else "Toggle off")
  }
}
