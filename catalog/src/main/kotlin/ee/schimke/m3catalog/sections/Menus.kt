@file:CatalogGroup(name = "Menus", section = "Selection")
@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material3.DropdownMenuGroup
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.preview.CaptureGutter
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.CatalogOutlinedStars
import ee.schimke.m3catalog.KitShadowGutter
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogChoice
import ee.schimke.m3catalog.catalogText
import ee.schimke.m3catalog.counted
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.label_text
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

// Two kit menus, two Compose APIs, and they are not the same component:
//
//   The kit's expressive `Menu` set (`58966:3975`, `Theme=Standard, Groups=1` is `58966:4078`) is
//   Compose's grouped menu — `DropdownMenuGroup`, which the app hosts in a `DropdownMenuPopup`.
//   `MenuDefaults.groupShape(index, count)` / `standaloneGroupShape` resolve to CornerLarge (16dp)
//   and `MenuDefaults.groupStandardContainerColor` to surface-container-low: the kit's 16dp and
//   `#F7F2FA` exactly (#85, #95). [DropdownMenuGroupSticker] renders it.
//
//   The kit's `Menu (baseline)` set (`54061:36963`, `Density=0` is `54061:36964`) is the baseline
//   `DropdownMenu`, whose 4dp `MenuDefaults.shape` and `MenuTokens.ContainerColor`
//   (surface-container) are correct for that node. [DropdownMenuSticker] draws it, and stays out of
//   the inventory: `DropdownMenu` composes its own popup window, which a single-surface capture
//   cannot reach (compose-ai-tools#3916).
//
// The 4dp-vs-16dp and surface-container-vs-low "divergences" were the grouped node compared against
// the baseline component. `MenuDefaults.LeadingIconSize` / `TrailingIconSize` are 20dp, which is
// the
// kit's 20x20 icon slot on both nodes.

private data class MenuRow(val label: StringResource, val icon: ImageVector)

private val MENU_ROWS = List(6) { MenuRow(Res.string.label_text, CatalogOutlinedStars) }

// The baseline menu (`54061:36964`). Not a catalog comparison until popup surfaces can be captured
// (compose-ai-tools#3916): `DropdownMenu` composes its container inside its own popup, so this
// composes the items in the container `MenuDefaults` describes rather than invoking `DropdownMenu`.
@Composable
fun DropdownMenuSticker() = Sticker {
  val icons = catalogChoice("leading", "icon", "icon", "none") == "icon"
  val shortcuts = catalogChoice("trailing", "chevron", "chevron", "shortcut") == "shortcut"
  val dividers = catalogChoice("dividers", "off", "off", "on") == "on"
  val disabledLast = catalogChoice("status", "enabled", "enabled", "disabled") == "disabled"
  // The container's Level 3 shadow needs room, and this composable has no `@Preview` to hang a
  // `@CaptureGutter` on yet — a dropdown lives in a popup window a capture cannot reach
  // (compose-ai-tools#3916). So it pads, with the same measurements the captured stickers reserve;
  // when the popup becomes capturable this becomes a `@CaptureGutter` like the others (#105).
  Box(
    Modifier.padding(
      start = KitShadowGutter.Level3Side.dp,
      top = KitShadowGutter.Level3Top.dp,
      end = KitShadowGutter.Level3Side.dp,
      bottom = KitShadowGutter.Level3Bottom.dp,
    )
  ) {
    // A dropdown menu lives in its own platform window, so this composes the container rather
    // than capturing one, and every part of it comes from `MenuDefaults`: the baseline menu's 4dp
    // shape and surface-container colour, both correct for the baseline node.
    Surface(
      modifier = Modifier.width(208.dp).height(292.dp),
      shape = MenuDefaults.shape,
      // The kit's `Theme` axis. `MenuDefaults` carries the vibrant container itself, so this is a
      // knob over two published colours rather than a hand-mixed one.
      color =
        if (catalogChoice("theme", "standard", "standard", "vibrant") == "vibrant")
          MenuDefaults.groupVibrantContainerColor
        else MenuDefaults.containerColor,
      tonalElevation = MenuDefaults.TonalElevation,
      shadowElevation = MenuDefaults.ShadowElevation,
    ) {
      Column(Modifier.padding(vertical = 2.dp)) {
        MENU_ROWS.forEachIndexed { index, row ->
          // The kit groups destructive actions behind a divider, so the divider lands before the
          // last row rather than between every pair.
          if (dividers && index == MENU_ROWS.lastIndex) HorizontalDivider()
          val c = counted(catalogText("label", stringResource(row.label), index))
          val enabled = !(disabledLast && index == MENU_ROWS.lastIndex)
          DropdownMenuItem(
            text = { Text(c.label) },
            onClick = c.onClick,
            enabled = enabled,
            leadingIcon =
              if (!icons) null
              // `MenuDefaults.LeadingIconSize` is 20dp, the kit's 20x20 leading slot.
              else
                ({
                  Icon(
                    row.icon,
                    contentDescription = null,
                    modifier = Modifier.size(MenuDefaults.LeadingIconSize),
                  )
                }),
            trailingIcon =
              if (shortcuts) ({ Text("⌘C") })
              else
                ({
                  // `MenuDefaults.TrailingIconSize` is 20dp, the kit's 20x20 trailing slot.
                  Icon(
                    Icons.Filled.ArrowRight,
                    contentDescription = null,
                    modifier = Modifier.size(MenuDefaults.TrailingIconSize),
                  )
                }),
          )
        }
      }
    }
  }
}

/**
 * The kit's expressive menu, `Theme=Standard, Groups=1`: one [DropdownMenuGroup].
 *
 * `DropdownMenuGroup` is the container itself — a `Surface` in the group's shape and colour — and
 * an app hosts it in a `DropdownMenuPopup`. The popup only positions and animates its content, so
 * the sticker renders the group directly: the pixels are the real composable's, not a replica. The
 * shape is `MenuDefaults.standaloneGroupShape` (CornerLarge, 16dp) and the colour
 * `MenuDefaults.groupStandardContainerColor` (surface-container-low), both the kit's (#85, #95).
 *
 * The kit's other `Menu` cells are not authored here: the vibrant theme needs vibrant item colours
 * that only Compose's selectable items carry, and the multi-group cells' per-group content is not
 * yet measured.
 */
@CatalogComponent(
  id = "Menu/DropdownGroup",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58966:4078",
  caption =
    "The expressive menu: items in a DropdownMenuGroup, in the group's 16dp corner and " +
      "surface-container-low.",
)
@CatalogModes
// The leading / trailing / status knobs stay live-panel overrides rather than baked variants: they
// are slot contents the kit's `Menu` set does not split into nodes, so a baked cell would have no
// kit node to be compared against.
@CaptureGutter(
  start = KitShadowGutter.Level3Side,
  top = KitShadowGutter.Level3Top,
  end = KitShadowGutter.Level3Side,
  bottom = KitShadowGutter.Level3Bottom,
)
@Composable
fun DropdownMenuGroupSticker() = Sticker {
  val icons = catalogChoice("leading", "icon", "icon", "none") == "icon"
  val shortcuts = catalogChoice("trailing", "chevron", "chevron", "shortcut") == "shortcut"
  val disabledLast = catalogChoice("status", "enabled", "enabled", "disabled") == "disabled"
  // 208dp is the kit node's width — a size the caller passes, so it goes on the component.
  DropdownMenuGroup(
    shapes = MenuDefaults.groupShape(index = 0, count = 1),
    modifier = Modifier.width(208.dp),
    containerColor = MenuDefaults.groupStandardContainerColor,
  ) {
    MENU_ROWS.forEachIndexed { index, row ->
      val c = counted(catalogText("label", stringResource(row.label), index))
      // The expressive item overload: a grouped item takes its position's shape (leading, middle,
      // trailing), which is the shape its state layers are drawn in, and the selectable-item
      // content padding — the baseline overload has neither.
      DropdownMenuItem(
        text = { Text(c.label) },
        onClick = c.onClick,
        shape =
          when (index) {
            0 -> MenuDefaults.leadingItemShape
            MENU_ROWS.lastIndex -> MenuDefaults.trailingItemShape
            else -> MenuDefaults.middleItemShape
          },
        enabled = !(disabledLast && index == MENU_ROWS.lastIndex),
        leadingIcon =
          if (!icons) null
          else
            ({
              Icon(
                row.icon,
                contentDescription = null,
                modifier = Modifier.size(MenuDefaults.LeadingIconSize),
              )
            }),
        trailingIcon =
          if (shortcuts) ({ Text("⌘C") })
          else
            ({
              Icon(
                Icons.Filled.ArrowRight,
                contentDescription = null,
                modifier = Modifier.size(MenuDefaults.TrailingIconSize),
              )
            }),
      )
    }
  }
}
