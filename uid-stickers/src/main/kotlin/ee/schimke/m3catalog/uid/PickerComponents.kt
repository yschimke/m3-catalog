@file:OptIn(
  ExperimentalMaterial3Api::class,
  ExperimentalMaterial3ComponentOverrideApi::class,
)

package ee.schimke.m3catalog.uid

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.BasicAlertDialogOverride
import androidx.compose.material3.BasicAlertDialogOverrideScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ComponentOverrideApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalBasicAlertDialogOverride
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TimePickerDialogDefaults
import androidx.compose.material3.TimePickerDisplayMode
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.m3catalog.CatalogFilledStars
import ee.schimke.m3catalog.CatalogOutlinedStars

// Components the picker and rail documents place (`project-components-pickers.json`), each a value
// the builder cannot spell yet beside the Material component under comparison, never a wrapper of
// it. `PickerDialogHost` is shell scaffolding, not a document component.

/**
 * `TimePickerDialogDefaults.Title`: the builder refuses `m3/title` because a member of a `Defaults`
 * object has no call site it can write, so the dialog's headline is placed through this.
 */
@Composable
fun TimePickerDialogTitle(input: Boolean, modifier: Modifier = Modifier) {
  TimePickerDialogDefaults.Title(displayMode = displayMode(input), modifier = modifier)
}

/**
 * `TimePickerDialogDefaults.DisplayModeToggle`, refused as `m3/display-mode-toggle` for the same
 * reason as the title. `AlertDialog` tints its button slots `primary`; the kit and material3's own
 * `TimePickerDialog` draw this toggle `on-surface-variant`, which is restored here as the
 * hand-written frame restores it.
 */
@Composable
fun TimePickerModeToggle(onClick: () -> Unit, input: Boolean, modifier: Modifier = Modifier) {
  CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
    TimePickerDialogDefaults.DisplayModeToggle(
      onDisplayModeChange = onClick,
      displayMode = displayMode(input),
      modifier = modifier,
    )
  }
}

private fun displayMode(input: Boolean) =
  if (input) TimePickerDisplayMode.Input else TimePickerDisplayMode.Picker

/** The rail destination glyph: the kit's filled stars when selected, outlined otherwise. */
@Composable
fun RailStarsIcon(selected: Boolean, modifier: Modifier = Modifier) {
  Icon(
    if (selected) CatalogFilledStars else CatalogOutlinedStars,
    contentDescription = null,
    modifier = modifier,
  )
}

/** The rail header's flat FAB: the builder cannot spell a `FloatingActionButtonElevation`. */
@Composable
fun FlatFloatingActionButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit,
) {
  FloatingActionButton(onClick = onClick, modifier = modifier, elevation = flat(), content = content)
}

/** The expanded rail header's flat extended FAB, for the same reason as [FlatFloatingActionButton]. */
@Composable
fun FlatExtendedFloatingActionButton(
  onClick: () -> Unit,
  icon: @Composable () -> Unit,
  text: @Composable () -> Unit,
  modifier: Modifier = Modifier,
) {
  ExtendedFloatingActionButton(
    onClick = onClick,
    modifier = modifier,
    elevation = flat(),
    icon = icon,
    text = text,
  )
}

@Composable
private fun flat() =
  FloatingActionButtonDefaults.elevation(
    defaultElevation = 0.dp,
    pressedElevation = 0.dp,
    focusedElevation = 0.dp,
    hoveredElevation = 0.dp,
  )

/**
 * Shell scaffolding: draws a Material dialog in the sticker surface instead of a platform window,
 * as `:catalog`'s internal `InlineDialogHost` does for the hand-written stickers.
 */
@Composable
internal fun PickerDialogHost(content: @Composable () -> Unit) {
  CompositionLocalProvider(LocalBasicAlertDialogOverride provides InlineDialog) { content() }
}

private object InlineDialog : BasicAlertDialogOverride {
  @Composable
  override fun BasicAlertDialogOverrideScope.BasicAlertDialog() {
    Box(modifier = modifier, propagateMinConstraints = true) { content() }
  }
}
