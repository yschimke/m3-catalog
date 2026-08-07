@file:CatalogGroup(name = "Toggle buttons", section = "Actions")
@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ElevatedToggleButton
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedToggleButton
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TonalToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogButtonSize
import ee.schimke.m3catalog.catalogToggleSelected
import ee.schimke.m3catalog.catalogToggleShapes
import ee.schimke.m3catalog.toggleable

// The expressive toggle button: a button that stays on. It owns its checked state.
//
// The kit ships each emphasis as its OWN component set ("Toggle button", "Toggle button - tonal",
// "Toggle button - outline", "Toggle button - elevated"), and every one of them documents the same
// axes: "Four color options ... Five size recommendations ... Two shape options: round and square",
// over a selected/unselected component property. So each emphasis is a top-level component here
// rather than a variant of the filled one — `@CatalogVariant` carries no `reference`, so folding
// the other three under Filled would leave three of the kit's four sets with nothing to compare
// against and parity would report each as entirely uncovered.
//
// Twenty cells per emphasis (5 sizes x 2 shapes x 2 selected states), carried by one `@Preview`
// through knobs and a stacked `@OverrideVariant` per cell. The selected axis is not optional
// decoration: the container SHAPE morphs on selection, so an unselected cell and a selected cell of
// the same size render differently by design.
//
// Defaults differ across the four on purpose — filled and tonal were authored selected, outlined
// and elevated unselected — and `catalogToggleSelected` takes that per component so no unseeded
// render moves.

/**
 * The label at the type scale its size carries.
 *
 * M3 scales type with the container, so a size variant that changed only the box would render a
 * correct height around visibly wrong text. At the default (small) this resolves to `labelLarge` —
 * exactly what `ToggleButton` provides itself — so the unseeded cell is unchanged.
 */
@Composable
private fun SizedLabel(label: String) {
  ProvideTextStyle(catalogButtonSize().labelStyle) { Text(label) }
}

@CatalogComponent(
  id = "ToggleButton/Filled",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/57994:2328",
  caption =
    "A button whose selection persists; highest emphasis. Five sizes x two shapes x " +
      "selected/unselected fold in as variants.",
)
@CatalogModes
@OverrideVariant(name = "xs-on", booleans = ["selected=true"], strings = ["size=xs"])
@OverrideVariant(name = "xs-off", booleans = ["selected=false"], strings = ["size=xs"])
@OverrideVariant(
  name = "xs-square-on",
  booleans = ["selected=true"],
  strings = ["size=xs", "shape=square"],
)
@OverrideVariant(
  name = "xs-square-off",
  booleans = ["selected=false"],
  strings = ["size=xs", "shape=square"],
)
@OverrideVariant(name = "s-off", booleans = ["selected=false"])
@OverrideVariant(name = "s-square-on", booleans = ["selected=true"], strings = ["shape=square"])
@OverrideVariant(name = "s-square-off", booleans = ["selected=false"], strings = ["shape=square"])
@OverrideVariant(name = "m-on", booleans = ["selected=true"], strings = ["size=m"])
@OverrideVariant(name = "m-off", booleans = ["selected=false"], strings = ["size=m"])
@OverrideVariant(
  name = "m-square-on",
  booleans = ["selected=true"],
  strings = ["size=m", "shape=square"],
)
@OverrideVariant(
  name = "m-square-off",
  booleans = ["selected=false"],
  strings = ["size=m", "shape=square"],
)
@OverrideVariant(name = "l-on", booleans = ["selected=true"], strings = ["size=l"])
@OverrideVariant(name = "l-off", booleans = ["selected=false"], strings = ["size=l"])
@OverrideVariant(
  name = "l-square-on",
  booleans = ["selected=true"],
  strings = ["size=l", "shape=square"],
)
@OverrideVariant(
  name = "l-square-off",
  booleans = ["selected=false"],
  strings = ["size=l", "shape=square"],
)
@OverrideVariant(name = "xl-on", booleans = ["selected=true"], strings = ["size=xl"])
@OverrideVariant(name = "xl-off", booleans = ["selected=false"], strings = ["size=xl"])
@OverrideVariant(
  name = "xl-square-on",
  booleans = ["selected=true"],
  strings = ["size=xl", "shape=square"],
)
@OverrideVariant(
  name = "xl-square-off",
  booleans = ["selected=false"],
  strings = ["size=xl", "shape=square"],
)
@Composable
fun ToggleButtonSticker() = Sticker {
  val size = catalogButtonSize()
  val (on, set) = toggleable(catalogToggleSelected(default = true))
  ToggleButton(
    checked = on,
    onCheckedChange = set,
    shapes = catalogToggleShapes(size),
    contentPadding = size.contentPadding,
    modifier = Modifier.defaultMinSize(minHeight = size.containerHeight),
  ) {
    SizedLabel("On")
  }
}

@CatalogComponent(
  id = "ToggleButton/Tonal",
  caption =
    "Secondary emphasis, still filled. Five sizes x two shapes x selected/unselected fold in as " +
      "variants.",
)
@CatalogModes
@OverrideVariant(name = "xs-on", booleans = ["selected=true"], strings = ["size=xs"])
@OverrideVariant(name = "xs-off", booleans = ["selected=false"], strings = ["size=xs"])
@OverrideVariant(
  name = "xs-square-on",
  booleans = ["selected=true"],
  strings = ["size=xs", "shape=square"],
)
@OverrideVariant(
  name = "xs-square-off",
  booleans = ["selected=false"],
  strings = ["size=xs", "shape=square"],
)
@OverrideVariant(name = "s-off", booleans = ["selected=false"])
@OverrideVariant(name = "s-square-on", booleans = ["selected=true"], strings = ["shape=square"])
@OverrideVariant(name = "s-square-off", booleans = ["selected=false"], strings = ["shape=square"])
@OverrideVariant(name = "m-on", booleans = ["selected=true"], strings = ["size=m"])
@OverrideVariant(name = "m-off", booleans = ["selected=false"], strings = ["size=m"])
@OverrideVariant(
  name = "m-square-on",
  booleans = ["selected=true"],
  strings = ["size=m", "shape=square"],
)
@OverrideVariant(
  name = "m-square-off",
  booleans = ["selected=false"],
  strings = ["size=m", "shape=square"],
)
@OverrideVariant(name = "l-on", booleans = ["selected=true"], strings = ["size=l"])
@OverrideVariant(name = "l-off", booleans = ["selected=false"], strings = ["size=l"])
@OverrideVariant(
  name = "l-square-on",
  booleans = ["selected=true"],
  strings = ["size=l", "shape=square"],
)
@OverrideVariant(
  name = "l-square-off",
  booleans = ["selected=false"],
  strings = ["size=l", "shape=square"],
)
@OverrideVariant(name = "xl-on", booleans = ["selected=true"], strings = ["size=xl"])
@OverrideVariant(name = "xl-off", booleans = ["selected=false"], strings = ["size=xl"])
@OverrideVariant(
  name = "xl-square-on",
  booleans = ["selected=true"],
  strings = ["size=xl", "shape=square"],
)
@OverrideVariant(
  name = "xl-square-off",
  booleans = ["selected=false"],
  strings = ["size=xl", "shape=square"],
)
@Composable
fun TonalToggleButtonSticker() = Sticker {
  val size = catalogButtonSize()
  val (on, set) = toggleable(catalogToggleSelected(default = true))
  TonalToggleButton(
    checked = on,
    onCheckedChange = set,
    shapes = catalogToggleShapes(size),
    contentPadding = size.contentPadding,
    modifier = Modifier.defaultMinSize(minHeight = size.containerHeight),
  ) {
    SizedLabel("Tonal")
  }
}

@CatalogComponent(
  id = "ToggleButton/Outlined",
  caption =
    "Medium emphasis on a busy surface. Five sizes x two shapes x selected/unselected fold in " +
      "as variants.",
)
@CatalogModes
@OverrideVariant(name = "xs-on", booleans = ["selected=true"], strings = ["size=xs"])
@OverrideVariant(name = "xs-off", booleans = ["selected=false"], strings = ["size=xs"])
@OverrideVariant(
  name = "xs-square-on",
  booleans = ["selected=true"],
  strings = ["size=xs", "shape=square"],
)
@OverrideVariant(
  name = "xs-square-off",
  booleans = ["selected=false"],
  strings = ["size=xs", "shape=square"],
)
@OverrideVariant(name = "s-on", booleans = ["selected=true"])
@OverrideVariant(name = "s-square-on", booleans = ["selected=true"], strings = ["shape=square"])
@OverrideVariant(name = "s-square-off", booleans = ["selected=false"], strings = ["shape=square"])
@OverrideVariant(name = "m-on", booleans = ["selected=true"], strings = ["size=m"])
@OverrideVariant(name = "m-off", booleans = ["selected=false"], strings = ["size=m"])
@OverrideVariant(
  name = "m-square-on",
  booleans = ["selected=true"],
  strings = ["size=m", "shape=square"],
)
@OverrideVariant(
  name = "m-square-off",
  booleans = ["selected=false"],
  strings = ["size=m", "shape=square"],
)
@OverrideVariant(name = "l-on", booleans = ["selected=true"], strings = ["size=l"])
@OverrideVariant(name = "l-off", booleans = ["selected=false"], strings = ["size=l"])
@OverrideVariant(
  name = "l-square-on",
  booleans = ["selected=true"],
  strings = ["size=l", "shape=square"],
)
@OverrideVariant(
  name = "l-square-off",
  booleans = ["selected=false"],
  strings = ["size=l", "shape=square"],
)
@OverrideVariant(name = "xl-on", booleans = ["selected=true"], strings = ["size=xl"])
@OverrideVariant(name = "xl-off", booleans = ["selected=false"], strings = ["size=xl"])
@OverrideVariant(
  name = "xl-square-on",
  booleans = ["selected=true"],
  strings = ["size=xl", "shape=square"],
)
@OverrideVariant(
  name = "xl-square-off",
  booleans = ["selected=false"],
  strings = ["size=xl", "shape=square"],
)
@Composable
fun OutlinedToggleButtonSticker() = Sticker {
  val size = catalogButtonSize()
  val (on, set) = toggleable(catalogToggleSelected(default = false))
  OutlinedToggleButton(
    checked = on,
    onCheckedChange = set,
    shapes = catalogToggleShapes(size),
    contentPadding = size.contentPadding,
    modifier = Modifier.defaultMinSize(minHeight = size.containerHeight),
  ) {
    SizedLabel("Outlined")
  }
}

@CatalogComponent(
  id = "ToggleButton/Elevated",
  caption =
    "Outlined alternative needing separation. Five sizes x two shapes x selected/unselected " +
      "fold in as variants.",
)
@CatalogModes
@OverrideVariant(name = "xs-on", booleans = ["selected=true"], strings = ["size=xs"])
@OverrideVariant(name = "xs-off", booleans = ["selected=false"], strings = ["size=xs"])
@OverrideVariant(
  name = "xs-square-on",
  booleans = ["selected=true"],
  strings = ["size=xs", "shape=square"],
)
@OverrideVariant(
  name = "xs-square-off",
  booleans = ["selected=false"],
  strings = ["size=xs", "shape=square"],
)
@OverrideVariant(name = "s-on", booleans = ["selected=true"])
@OverrideVariant(name = "s-square-on", booleans = ["selected=true"], strings = ["shape=square"])
@OverrideVariant(name = "s-square-off", booleans = ["selected=false"], strings = ["shape=square"])
@OverrideVariant(name = "m-on", booleans = ["selected=true"], strings = ["size=m"])
@OverrideVariant(name = "m-off", booleans = ["selected=false"], strings = ["size=m"])
@OverrideVariant(
  name = "m-square-on",
  booleans = ["selected=true"],
  strings = ["size=m", "shape=square"],
)
@OverrideVariant(
  name = "m-square-off",
  booleans = ["selected=false"],
  strings = ["size=m", "shape=square"],
)
@OverrideVariant(name = "l-on", booleans = ["selected=true"], strings = ["size=l"])
@OverrideVariant(name = "l-off", booleans = ["selected=false"], strings = ["size=l"])
@OverrideVariant(
  name = "l-square-on",
  booleans = ["selected=true"],
  strings = ["size=l", "shape=square"],
)
@OverrideVariant(
  name = "l-square-off",
  booleans = ["selected=false"],
  strings = ["size=l", "shape=square"],
)
@OverrideVariant(name = "xl-on", booleans = ["selected=true"], strings = ["size=xl"])
@OverrideVariant(name = "xl-off", booleans = ["selected=false"], strings = ["size=xl"])
@OverrideVariant(
  name = "xl-square-on",
  booleans = ["selected=true"],
  strings = ["size=xl", "shape=square"],
)
@OverrideVariant(
  name = "xl-square-off",
  booleans = ["selected=false"],
  strings = ["size=xl", "shape=square"],
)
@Composable
fun ElevatedToggleButtonSticker() = Sticker {
  val size = catalogButtonSize()
  val (on, set) = toggleable(catalogToggleSelected(default = false))
  ElevatedToggleButton(
    checked = on,
    onCheckedChange = set,
    shapes = catalogToggleShapes(size),
    contentPadding = size.contentPadding,
    modifier = Modifier.defaultMinSize(minHeight = size.containerHeight),
  ) {
    SizedLabel("Elevated")
  }
}

// --- Content axis, folded under the filled toggle ------------------------------------------------
// The kit's last documented axis: "Can contain an optional leading icon". It also specifies the
// glyph swap — outlined when unselected, filled when selected — which is why this variant reads
// its own state rather than pinning one icon.

@CatalogVariant(
  of = "ToggleButton/Filled",
  props = ["content=icon+label"],
  caption = "Leading icon + label; the glyph fills as the button is selected.",
)
@CatalogModes
@Composable
fun ToggleButtonIconLabel() = Sticker {
  val (on, set) = toggleable(true)
  ToggleButton(checked = on, onCheckedChange = set) {
    Icon(
      if (on) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
      contentDescription = null,
      modifier = Modifier.size(ToggleButtonDefaults.IconSize),
    )
    Spacer(Modifier.width(ToggleButtonDefaults.IconSpacing))
    Text("Favourite")
  }
}
