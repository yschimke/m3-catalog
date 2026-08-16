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
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.m3catalog.CatalogOutlinedStars
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogChoice
import ee.schimke.m3catalog.counted
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.label_text
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

// `DropdownMenu` renders into a popup window a single-surface capture cannot reach. Its **items**
// are plain composables, so the sticker composes them in the menu container the component uses —
// the real `DropdownMenuItem` rows, in the real surface shape and colour.
//
// `MenuDefaults.shape` is 4dp where the kit specs 16 (#85). It stays the library's value on
// purpose: hard-coding 16 would draw a container `DropdownMenu` never draws, and would hide the
// divergence from every future parity run. The break is left visible and filed upstream instead.
//
// The kit varies three things inside that container, and all three are item parameters rather than
// different components: the leading icon, a trailing shortcut label, and dividers grouping the
// items.

private data class MenuRow(val label: StringResource, val icon: ImageVector)

private val MENU_ROWS = List(6) { MenuRow(Res.string.label_text, CatalogOutlinedStars) }

// Not a catalog comparison until popup surfaces can be captured (compose-ai-tools#3916).
@Composable
fun DropdownMenuSticker() = Sticker {
  val icons = catalogChoice("leading", "icon", "icon", "none") == "icon"
  val shortcuts = catalogChoice("trailing", "chevron", "chevron", "shortcut") == "shortcut"
  val dividers = catalogChoice("dividers", "off", "off", "on") == "on"
  val disabledLast = catalogChoice("status", "enabled", "enabled", "disabled") == "disabled"
  Box(Modifier.padding(start = 11.dp, top = 7.dp, end = 11.dp, bottom = 15.dp)) {
    // A dropdown menu lives in its own platform window, so this composes the container rather
    // than capturing one — and the rule for that is to take every part of it from `MenuDefaults`,
    // never to pick numbers that look right. The three that were literals now do:
    //
    //   `tonalElevation` was 3.dp and `MenuDefaults.TonalElevation` is Level0 — the tint was
    //   drawn by nothing but the literal, and neither Compose nor the kit asks for it.
    //   `shadowElevation` was also 3.dp, which happens to be `MenuTokens.ContainerElevation`
    //   (Level2) — right number, no name.
    //   `color` was `surfaceContainer` spelled by hand, which is what `MenuDefaults.containerColor`
    //   resolves to (`MenuTokens.ContainerColor`) — so same pixels, and now it tracks the token.
    //
    // The kit fills this node with `surface-container-LOW` instead (see issue #95), which is also
    // what `StandardMenuTokens.ContainerColor` says. Following the component's own default here
    // leaves that disagreement where parity can see it.
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
          val c = counted(stringResource(row.label))
          val enabled = !(disabledLast && index == MENU_ROWS.lastIndex)
          DropdownMenuItem(
            text = { Text(c.label) },
            onClick = c.onClick,
            enabled = enabled,
            leadingIcon =
              if (!icons) null
              // 20dp is the kit's leading element (`I58966:4081;58966:4103`, 20x20), not a guess.
              // `MenuDefaults.LeadingIconSize` says 24; the kit wins on drawn content, and the
              // difference is recorded in issue #95 rather than split between the two.
              else ({ Icon(row.icon, contentDescription = null, modifier = Modifier.size(20.dp)) }),
            trailingIcon =
              if (shortcuts) ({ Text("⌘C") })
              else
                ({
                  // The kit's trailing element is 20x20, the same box as the leading one; 10dp
                  // was half of it and matched neither the kit nor `MenuDefaults`.
                  Icon(
                    Icons.Filled.ArrowRight,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                  )
                }),
          )
        }
      }
    }
  }
}
