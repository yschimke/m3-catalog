package ee.schimke.m3catalog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
 * Owned boolean state (checked / selected / expanded), as a plain [MutableState] the sticker reads
 * and writes through `by`.
 *
 * ### Why a MutableState and not a destructured Pair
 *
 * These helpers used to return `Pair<T, (T) -> Unit>`, destructured at the call site. That reads
 * fine, but it made the *usage* rewrite hard out of all proportion: the preview server has to turn
 * a sticker into the plain Compose a developer would paste, and a destructuring is the one shape
 * that forces it to replace a **declaration** and rebind a second name at every use site. That
 * needed a whole rule kind of its own, a Kotlin parse to find the entries, and guards for
 * shadowing, `_` entries and setter-as-callee — four separate ways to emit code that looks clean
 * and does not compile.
 *
 * Returning a [MutableState] removes the problem rather than handling it. The call site is already
 * the shape the answer wants:
 * ```
 * var checked by toggleable(true)          →   var checked by remember { mutableStateOf(true) }
 * ```
 *
 * so the rewrite is a single expression substitution — the simplest rule there is — and there is no
 * second name to rebind. Nothing is lost on either lane: the baked snapshot still ignores writes,
 * the live session still recomposes.
 */
@Composable
fun toggleable(initial: Boolean): MutableState<Boolean> =
  if (catalogInteractive()) remember { mutableStateOf(initial) } else frozen(initial)

/** Owned index state, for single-select families (segmented buttons, tabs, navigation bars). */
@Composable
fun selectable(initial: Int): MutableState<Int> =
  if (catalogInteractive()) remember { mutableIntStateOf(initial) } else frozen(initial)

/**
 * Owned multi-select state, for families where any number of cells may be on at once.
 *
 * The set update lives at the call site now rather than behind a two-argument setter — which is
 * also what a developer would write, so the usage snippet reads the same as the sticker.
 */
@Composable
fun multiSelectable(initial: Set<Int>): MutableState<Set<Int>> =
  if (catalogInteractive()) remember { mutableStateOf(initial) } else frozen(initial)

/** Owned float state, for continuous controls (sliders). */
@Composable
fun draggable(initial: Float): MutableState<Float> =
  if (catalogInteractive()) remember { mutableFloatStateOf(initial) } else frozen(initial)

/** Owned text state, for text fields. */
@Composable
fun editable(initial: String): MutableState<String> =
  if (catalogInteractive()) remember { mutableStateOf(initial) } else frozen(initial)

/**
 * A [MutableState] that reports [value] and silently drops writes — the baked lane's contract.
 *
 * A published PNG cannot depend on whether something tapped it, so a click must change nothing.
 * Dropping the write does that while keeping the *type* identical to the live lane's, which is what
 * lets one sticker body serve both.
 */
private fun <T> frozen(initial: T): MutableState<T> =
  object : MutableState<T> {
    override var value: T = initial
      set(@Suppress("UNUSED_PARAMETER") ignored) {
        // Baked lane: deliberately inert, so the frame stays deterministic.
      }

    override fun component1(): T = value

    override fun component2(): (T) -> Unit = {}
  }
