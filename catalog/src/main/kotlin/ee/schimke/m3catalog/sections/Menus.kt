@file:CatalogGroup(name = "Menus", section = "Selection")

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes
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
// the real `DropdownMenuItem` rows, in the real surface colour and the kit's corner.
//
// The kit varies three things inside that container, and all three are item parameters rather than
// different components: the leading icon, a trailing shortcut label, and dividers grouping the
// items.

/**
 * The menu container's corner, at the kit's 16dp rather than `MenuDefaults.shape`'s 4dp.
 *
 * This is the one corner in issue #1's triage where the code was genuinely wrong and moved:
 * `direction: "design-led"` makes the kit authoritative, and the kit's Menu container is 16.
 * Compose still resolves `MenuDefaults.shape` to `shapes.extraSmall` (4dp), so this is a deliberate
 * divergence from the library rather than an oversight — `KitCornerRadiusTest` pins both halves so
 * a Material release that moves the library onto 16 shows up as a failure to delete this constant.
 */
internal val MenuContainerShape = RoundedCornerShape(16.dp)

private data class MenuRow(val label: StringResource, val icon: ImageVector)

private val MENU_ROWS = List(6) { MenuRow(Res.string.label_text, CatalogOutlinedStars) }

@CatalogComponent(
  id = "Menu/Dropdown",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58966:4078",
  caption =
    "A list of choices on a temporary surface. Leading icons, trailing shortcuts, dividers and a " +
      "disabled item fold in.",
)
@CatalogModes
@OverrideVariant(name = "no-icons", strings = ["leading=none"])
@OverrideVariant(name = "shortcuts", strings = ["trailing=shortcut"])
@OverrideVariant(name = "icons-shortcuts", strings = ["leading=icon", "trailing=shortcut"])
@OverrideVariant(name = "dividers", strings = ["dividers=on"])
@OverrideVariant(name = "text-only", strings = ["leading=none", "dividers=on"])
@OverrideVariant(name = "disabled-item", strings = ["status=disabled"])
@Composable
fun DropdownMenuSticker() = Sticker {
  val icons = catalogChoice("leading", "icon", "icon", "none") == "icon"
  val shortcuts = catalogChoice("trailing", "chevron", "chevron", "shortcut") == "shortcut"
  val dividers = catalogChoice("dividers", "off", "off", "on") == "on"
  val disabledLast = catalogChoice("status", "enabled", "enabled", "disabled") == "disabled"
  Box(Modifier.padding(start = 11.dp, top = 7.dp, end = 11.dp, bottom = 15.dp)) {
    Surface(
      modifier = Modifier.width(208.dp).height(292.dp),
      shape = MenuContainerShape,
      color = MaterialTheme.colorScheme.surfaceContainer,
      tonalElevation = 3.dp,
      shadowElevation = 3.dp,
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
              else ({ Icon(row.icon, contentDescription = null, modifier = Modifier.size(20.dp)) }),
            trailingIcon =
              if (shortcuts) ({ Text("⌘C") })
              else
                ({
                  Icon(
                    Icons.Filled.ArrowRight,
                    contentDescription = null,
                    modifier = Modifier.size(10.dp),
                  )
                }),
          )
        }
      }
    }
  }
}
