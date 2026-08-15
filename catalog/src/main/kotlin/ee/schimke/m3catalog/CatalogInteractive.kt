package ee.schimke.m3catalog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * Every catalog sticker is rendered on two surfaces that want opposite things from a pointer:
 *
 * |Lane                                           |Signal                       |A click must                                                                |
 * |-----------------------------------------------|-----------------------------|----------------------------------------------------------------------------|
 * |Baked snapshot / the published sticker sheet   |`LocalInspectionMode = true` |do **nothing** — a published PNG can't depend on whether something tapped it|
 * |Held Live Compose session on the preview server|`LocalInspectionMode = false`|visibly change the component                                                |
 *
 * **No sticker may ship a dead handler.** Components that carry state (switch, checkbox, radio,
 * chip, slider, segmented button, text fields) own it and mutate it via [toggleable] / [selectable]
 * / [multiSelectable]. Everything else takes the **click tally** [counted] as its default: it
 * appends `(n)` to a label, so `Filled` → `Filled (1)`. At `n == 0` the tally returns the bare
 * label and a no-op handler, so the baked capture is byte-identical either way.
 */
class Counted internal constructor(val label: String, val onClick: () -> Unit)

/**
 * A click tally over [label]. On the baked lane the label is unchanged and [Counted.onClick] is a
 * no-op; on the live lane each click appends `(n)`.
 */
@Composable
fun counted(label: String): Counted {
  if (!catalogInteractive()) return Counted(label) {}
  var n by remember { mutableIntStateOf(0) }
  return Counted(if (n == 0) label else "$label (${localizedDigits(n.toString())})") { n++ }
}

/**
 * Owned boolean state for a stateful component (checked / selected / expanded). Frozen at [initial]
 * on the baked lane — the deterministic frame the published catalog shows — and live on the
 * interactive lane.
 */
@Composable
fun toggleable(initial: Boolean): Pair<Boolean, (Boolean) -> Unit> {
  if (!catalogInteractive()) return initial to {}
  var checked by remember { mutableStateOf(initial) }
  return checked to { v: Boolean -> checked = v }
}

/** Owned index state, for single-select families (segmented buttons, tabs, navigation bars). */
@Composable
fun selectable(initial: Int): Pair<Int, (Int) -> Unit> {
  if (!catalogInteractive()) return initial to {}
  var index by remember { mutableIntStateOf(initial) }
  return index to { v: Int -> index = v }
}

/**
 * Owned multi-select state, for families where any number of the cells may be on at once
 * (multi-choice segmented buttons). The setter takes the cell's index and its new state, so a row
 * of any length wires up from one call — a fixed pair of booleans leaves every cell past the second
 * one inert.
 */
@Composable
fun multiSelectable(initial: Set<Int>): Pair<Set<Int>, (Int, Boolean) -> Unit> {
  if (!catalogInteractive()) return initial to { _, _ -> }
  var selected by remember { mutableStateOf(initial) }
  return selected to
    { index: Int, on: Boolean ->
      selected = if (on) selected + index else selected - index
    }
}

/** Owned float state, for continuous controls (sliders). */
@Composable
fun draggable(initial: Float): Pair<Float, (Float) -> Unit> {
  if (!catalogInteractive()) return initial to {}
  var value by remember { mutableStateOf(initial) }
  return value to { v: Float -> value = v }
}

/** Owned text state, for text fields. */
@Composable
fun editable(initial: String): Pair<String, (String) -> Unit> {
  if (!catalogInteractive()) return initial to {}
  var text by remember { mutableStateOf(initial) }
  return text to { v: String -> text = v }
}
