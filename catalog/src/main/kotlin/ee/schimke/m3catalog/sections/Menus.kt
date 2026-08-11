@file:CatalogGroup(name = "Menus", section = "Selection")

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
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
import ee.schimke.composeai.overrides.previewOverrideString
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.counted
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.action_delete
import ee.schimke.m3catalog.generated.resources.action_duplicate
import ee.schimke.m3catalog.generated.resources.action_edit
import ee.schimke.m3catalog.generated.resources.action_share
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

// `DropdownMenu` renders into a popup window a single-surface capture cannot reach. Its **items**
// are plain composables, so the sticker composes them in the menu container the component uses —
// the real `DropdownMenuItem` rows, in the real surface shape and colour.
//
// The kit varies three things inside that container, and all three are item parameters rather than
// different components: the leading icon, a trailing shortcut label, and dividers grouping the
// items.

private data class MenuRow(val label: StringResource, val icon: ImageVector, val shortcut: String)

private val MENU_ROWS =
  listOf(
    MenuRow(Res.string.action_edit, Icons.Filled.Edit, "⌘E"),
    MenuRow(Res.string.action_duplicate, Icons.Filled.ContentCopy, "⌘D"),
    MenuRow(Res.string.action_share, Icons.Filled.Share, "⌘S"),
    MenuRow(Res.string.action_delete, Icons.Filled.Delete, "⌫"),
  )

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
  val icons = previewOverrideString("leading", "icon") == "icon"
  val shortcuts = previewOverrideString("trailing", "none") == "shortcut"
  val dividers = previewOverrideString("dividers", "off") == "on"
  val disabledLast = previewOverrideString("status", "enabled") == "disabled"
  Surface(
    modifier = Modifier.width(220.dp),
    shape = MaterialTheme.shapes.extraSmall,
    color = MaterialTheme.colorScheme.surfaceContainer,
    tonalElevation = 3.dp,
    shadowElevation = 3.dp,
  ) {
    Column {
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
          leadingIcon = if (!icons) null else ({ Icon(row.icon, contentDescription = null) }),
          trailingIcon = if (!shortcuts) null else ({ Text(row.shortcut) }),
        )
      }
    }
  }
}
