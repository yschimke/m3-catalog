@file:CatalogGroup(name = "Toolbars", section = "Actions")
@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalFloatingToolbar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogChoice
import ee.schimke.m3catalog.counted
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.action_add
import ee.schimke.m3catalog.generated.resources.action_edit
import ee.schimke.m3catalog.generated.resources.action_more
import org.jetbrains.compose.resources.stringResource

// The expressive floating toolbar: a small cluster of related actions floating over content.
//
// The kit models one `Toolbar` set varying `Configuration` x `Orientation` x `Color`, so this is
// one component. Colour was already folded as cells; orientation folds the same way, as a
// `@CatalogVariant` — two composables, one component.
//
// There is no expanded/collapsed axis here, and there was never a picture behind the one that used
// to be (#177). The kit's `Toolbar` set publishes exactly `Configuration` x `Orientation` x `Color`
// — no collapsed node — and Compose's `expanded` flag gates nothing but the `leadingContent` /
// `trailingContent` slots, which this kit node does not have: `ContainerExpandedElevation` and
// `ContainerCollapsedElevation` are both Level 0 on the FAB-less toolbar, so a toolbar whose
// actions all sit in `content` measures and paints identically either way. Six `collapsed` renders
// were byte-identical to their expanded sibling for that reason. Same resolution as #175: an axis
// the kit does not publish and the component cannot draw is dropped rather than captioned.

@Composable
private fun ToolbarActions() {
  val add = counted(stringResource(Res.string.action_add))
  val edit = counted(stringResource(Res.string.action_edit))
  val more = counted(stringResource(Res.string.action_more))
  IconButton(onClick = add.onClick) {
    Icon(
      Icons.Filled.Share,
      contentDescription = stringResource(Res.string.action_add),
      tint = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
  IconButton(onClick = edit.onClick) {
    Icon(
      Icons.Filled.Comment,
      contentDescription = stringResource(Res.string.action_edit),
      tint = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
  IconButton(onClick = more.onClick) {
    Icon(
      Icons.Filled.Download,
      contentDescription = stringResource(Res.string.action_more),
      tint = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}

/**
 * The cell a floating-toolbar sticker renders in: the kit node's 168dp main-axis extent, with the
 * toolbar centred inside it.
 *
 * The kit's floating toolbar is 168dp along its main axis for three icon buttons.
 * `HorizontalFloatingToolbar` measures itself at 160dp for the same three, because
 * `FloatingToolbarTokens.ContainerBetweenSpace` (4dp) is declared in the token set and never
 * applied — the toolbar's `Row` arranges its content with `Arrangement.Center` and no spacing, so
 * the two gaps the kit draws between the icons are missing, and 2 x 4dp is the whole difference.
 *
 * Pinning `Modifier.width(168.dp)` ON the toolbar closed that gap by stretching the container while
 * leaving the icons packed in the middle, which publishes a width Compose does not produce. The
 * frame carries the kit's bounds instead, so the sticker still renders at the kit node's size while
 * the toolbar measures itself — the same split `ButtonFrame` / `ToggleButtonFrame` already make for
 * the button families, and the divergence stays where the parity diff can see it (#177).
 */
@Composable
private fun FloatingToolbarFrame(vertical: Boolean, content: @Composable () -> Unit) {
  val extent = if (vertical) Modifier.height(168.dp) else Modifier.width(168.dp)
  Box(Modifier.padding(11.dp).then(extent), contentAlignment = Alignment.Center) { content() }
}

@CatalogComponent(
  id = "Toolbar/HorizontalFloating",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58467:8210",
  caption = "A floating cluster of related actions over the content. Vibrant folds in.",
)
@CatalogModes
@OverrideVariant(name = "vibrant", strings = ["color=vibrant"])
@Composable
fun HorizontalFloatingToolbarSticker() = Sticker {
  val vibrant = catalogChoice("color", "standard", "standard", "vibrant") == "vibrant"
  FloatingToolbarFrame(vertical = false) {
    HorizontalFloatingToolbar(
      expanded = true,
      colors =
        if (vibrant) FloatingToolbarDefaults.vibrantFloatingToolbarColors()
        else FloatingToolbarDefaults.standardFloatingToolbarColors(),
    ) {
      ToolbarActions()
    }
  }
}

@CatalogVariant(
  of = "Toolbar/HorizontalFloating",
  props = ["orientation=vertical"],
  caption = "The side-anchored form. Vibrant folds in.",
)
@CatalogModes
@OverrideVariant(name = "vibrant", strings = ["color=vibrant"])
@Composable
fun VerticalFloatingToolbarSticker() = Sticker {
  val vibrant = catalogChoice("color", "standard", "standard", "vibrant") == "vibrant"
  FloatingToolbarFrame(vertical = true) {
    VerticalFloatingToolbar(
      expanded = true,
      colors =
        if (vibrant) FloatingToolbarDefaults.vibrantFloatingToolbarColors()
        else FloatingToolbarDefaults.standardFloatingToolbarColors(),
    ) {
      ToolbarActions()
    }
  }
}
