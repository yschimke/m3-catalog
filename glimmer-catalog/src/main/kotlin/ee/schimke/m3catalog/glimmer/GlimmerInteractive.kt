/*
 * The live-lane handler contract, ported for the Glimmer sheets.
 *
 * `AGENTS.md` states it for this repository's stickers generally — "No dead handlers. Stateful
 * components own their state; everything else takes `counted`" — and the reasoning is not specific
 * to Material: a Glimmer sticker is served on the same two surfaces, a baked PNG that must not
 * depend on whether something tapped it, and a held live session where a click has to visibly
 * change the component.
 *
 * A separate copy rather than a dependency on `:catalog`'s `CatalogInteractive.kt`: that module is
 * Compose Multiplatform desktop and this one is Android, so there is no compilation they share.
 * The `clickCount` knob is carried too, off by default, so a Glimmer sticker exposes the same
 * affordance a Material one does.
 */
package ee.schimke.m3catalog.glimmer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalInspectionMode
import ee.schimke.composeai.overrides.previewOverrideBoolean

/** A label and a handler that is real on the live lane and inert on the baked one. */
class Counted internal constructor(val label: String, val onClick: () -> Unit)

/** True only on the held live session; false while a sticker is being baked to PNG. */
@Composable fun glimmerInteractive(): Boolean = !LocalInspectionMode.current

/**
 * [label] and a real `onClick` — no-op on the baked lane, live on the other.
 *
 * The `(n)` tally is the `clickCount` knob and is off by default, for the reason
 * `CatalogInteractive.kt` sets out at length: a growing label is not what the component does when
 * you press it, and it competes with the press feedback that is the real answer.
 */
@Composable
fun counted(label: String): Counted {
  if (!glimmerInteractive()) return Counted(label) {}
  var n by remember { mutableIntStateOf(0) }
  val tally = previewOverrideBoolean("clickCount", false)
  return Counted(if (tally && n > 0) "$label ($n)" else label) { n++ }
}
