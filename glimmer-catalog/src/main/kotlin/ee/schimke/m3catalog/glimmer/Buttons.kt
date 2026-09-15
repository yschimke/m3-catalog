@file:CatalogGroup(name = "Button", section = "Actions")

package ee.schimke.m3catalog.glimmer

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
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
//
// ## Why the base sticker draws a leading icon and the label "Button"
//
// Because the cell it names does. The kit's `State=Enabled, Size=Default` cell (`40:660`) carries
// component-property defaults the variant NAME does not state — `Label Text=Button,
// Show Leading icon=true, Show Trailing icon=false` — and design-parity reports them as a pairing
// finding rather than inventing them. A sticker that draws `Button { Text("Send") }` is a picture
// of a different thing: 77x48 against the kit's 146x48, which is the whole of #381's Button row.
// Design-led means the code moves, so the base is the populated form and the BARE form is the
// variant under it.

@CatalogComponent(
  id = "Button",
  // `State=Enabled, Size=Default` in the kit's `Button` set (`40:655`), whose axes are
  // `State=` x `Size=Default | Large`. The CELL rather than the set: a reference is the
  // node this sticker is a rendition OF, and the set is a family, not a drawing.
  reference = "figma:HKfLClZDLRyMhf4IQQLna8/40:660",
  caption = "The primary action. Two sizes, the icon slots and the kit's states fold in.",
)
@GlimmerStates
@Preview
@Composable
fun ButtonSticker() = Sticker {
  val c = counted(glimmerText("label", stringResource(R.string.label_button)))
  Button(
    onClick = c.onClick,
    enabled = glimmerEnabled(),
    buttonSize = glimmerButtonSize(),
    leadingIcon = { Icon(Icons.AutoMirrored.Rounded.Send, stringResource(R.string.cd_send)) },
  ) {
    Text(c.label)
  }
}

@CatalogVariant(of = "Button", props = ["size=Large"], caption = "The larger of the two sizes.")
@GlimmerStates
@Preview
@Composable
fun ButtonLargeSticker() = Sticker {
  val c = counted(glimmerText("label", stringResource(R.string.label_button)))
  Button(
    onClick = c.onClick,
    enabled = glimmerEnabled(),
    buttonSize = ButtonSize.Large,
    leadingIcon = { Icon(Icons.AutoMirrored.Rounded.Send, stringResource(R.string.cd_send)) },
  ) {
    Text(c.label)
  }
}

// The bare form is a variant rather than the default, which is the inversion #381 asks for: the
// kit's base cell fills the leading slot, so a label-only button is the thing that differs from it.
// It is also the form `Button(onClick) { Text(…) }` produces, which is what a reader writes first.
@CatalogVariant(of = "Button", props = ["content=label-only"], caption = "Label with no icon.")
@Preview
@Composable
fun ButtonLabelOnlySticker() = Sticker {
  val c = counted(glimmerText("label", stringResource(R.string.label_button)))
  Button(onClick = c.onClick) { Text(c.label) }
}

@CatalogVariant(
  of = "Button",
  props = ["content=trailing-icon"],
  caption = "Icon after the label as well as before it.",
)
@Preview
@Composable
fun ButtonTrailingIconSticker() = Sticker {
  val c = counted(glimmerText("label", stringResource(R.string.label_button)))
  Button(
    onClick = c.onClick,
    leadingIcon = { Icon(Icons.AutoMirrored.Rounded.Send, stringResource(R.string.cd_send)) },
    trailingIcon = { Icon(Icons.AutoMirrored.Rounded.Send, stringResource(R.string.cd_send)) },
  ) {
    Text(c.label)
  }
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
@GlimmerStates
@ee.schimke.m3catalog.glimmer.ToggleButtonStickerExhaustiveKitCells
@Preview
@Composable
fun ToggleButtonSticker() = Sticker {
  // Stateful on purpose: a live click on the preview server has to answer with the component's
  // own checked state, not with a label the sticker swapped underneath it.
  //
  // The label stays "Button" across both states, which is the kit's copy for this cell
  // (`40000113:3991`, 146x48, leading icon on). An earlier "Toggle off" / "Toggle on" pair said
  // the state in words, which is the one thing a toggle button is supposed to say in PIXELS — the
  // corner morph and the lit container — and it cost 25dp of width against the cell besides.
  val initiallyChecked = glimmerChecked()
  var checked by remember(initiallyChecked) { mutableStateOf(initiallyChecked) }
  ToggleButton(
    checked = checked,
    onCheckedChange = { checked = it },
    enabled = glimmerEnabled(),
    buttonSize = glimmerButtonSize(),
    leadingIcon = { Icon(Icons.AutoMirrored.Rounded.Send, stringResource(R.string.cd_send)) },
  ) {
    Text(stringResource(R.string.label_button))
  }
}

@CatalogVariant(
  of = "ToggleButton",
  props = ["size=Large"],
  caption = "The larger of the two sizes.",
)
@GlimmerStates
@Preview
@Composable
fun ToggleButtonLargeSticker() = Sticker {
  var checked by remember { mutableStateOf(false) }
  ToggleButton(
    checked = checked,
    onCheckedChange = { checked = it },
    enabled = glimmerEnabled(),
    buttonSize = ButtonSize.Large,
    leadingIcon = { Icon(Icons.AutoMirrored.Rounded.Send, stringResource(R.string.cd_send)) },
  ) {
    Text(stringResource(R.string.label_button))
  }
}

// The CHECKED sticker is the kit's `Toggle=True, State=Enabled, Size=Default` cell
// (`40000113:4138`)
// and nothing else. Its state crossings — `Toggle=True` x `Focused | Pressed` x `Default | Large` —
// are NOT authored here: they are generated into `ExhaustiveKitCellAnnotations.kt` and attached to
// the base sticker above, which is what #374's step 3 asks for. A secondary generated cell seeds
// the `size` and `state` knobs and drives the interaction, so it renders the exact vector it is
// addressed by; authoring them as ordinary variants would have published a second navigable card
// per cell for pictures the sheet already offers.
@CatalogVariant(
  of = "ToggleButton",
  state = "checked",
  // The kit spells this axis `Toggle=True`; Compose spells it `checked`, and a reader of a Compose
  // catalog greps for the second. `kitAxis`/`kitValue` carry the kit's own words beside the
  // Compose ones so the resolver can match the cell without the sheet having to say `True`.
  kitAxis = "Toggle",
  kitValue = "True",
  caption = "Checked, where the corner size differs from the unchecked default.",
)
@Preview
@Composable
fun ToggleButtonCheckedSticker() = Sticker {
  var checked by remember { mutableStateOf(true) }
  ToggleButton(
    checked = checked,
    onCheckedChange = { checked = it },
    enabled = glimmerEnabled(),
    leadingIcon = { Icon(Icons.AutoMirrored.Rounded.Send, stringResource(R.string.cd_send)) },
  ) {
    Text(stringResource(R.string.label_button))
  }
}
