@file:CatalogGroup(name = "Voice input", section = "Communication")

package ee.schimke.m3catalog.glimmer

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.xr.glimmer.ContainedVoiceInputIndicator
import androidx.xr.glimmer.VoiceInputIndicator
import ee.schimke.composeai.data.overrides.PreviewOverrideOption
import ee.schimke.composeai.overrides.previewOverrideChoice
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant

// The indicator is driven by a level in 0..1 that on a device comes from the microphone. A sticker
// has to pin it: 0.6 is a mid-scale reading, which shows the bars at differing heights rather than
// the flat line either extreme produces.
//
// The kit's `Mic Indicators` set crosses that level with `Volume=Loud | Quiet`, and the level is a
// real parameter a caller passes, so the axis folds in as a variant rather than staying a footnote
// about which of two cells the pinned value is closer to. `volume` is spelled the kit's way, so it
// resolves with no `kitAxis` translation.

/** The two levels the kit's `Volume=` axis names, as a knob rather than a pinned constant. */
@Composable
fun glimmerVoiceLevel(): Float =
  if (
    previewOverrideChoice(
      "volume",
      "Loud",
      listOf(PreviewOverrideOption("Loud", "Loud"), PreviewOverrideOption("Quiet", "Quiet")),
    ) == "Quiet"
  )
    QUIET_LEVEL
  else LOUD_LEVEL

/**
 * The `container` knob: the plain indicator by default, the contained one on demand.
 *
 * `ContainedVoiceInputIndicator` is a separate composable rather than a parameter, and this sticker
 * branches between the two rather than leaving the axis to a second function alone. The kit crosses
 * `Contained=` with `Volume=`, and the generated exact cell for `Volume=Quiet, Contained=Yes` seeds
 * BOTH knobs on this one sticker — a seed naming an axis the sticker cannot reach would publish the
 * plain indicator at the contained address. #374.
 */
@Composable
fun glimmerContained(): Boolean =
  previewOverrideChoice(
    "container",
    "plain",
    listOf(
      PreviewOverrideOption("plain", "Plain"),
      PreviewOverrideOption("contained", "Contained"),
    ),
  ) == "contained"

/**
 * Above the midpoint: the bars stand at differing heights, which is what the kit's `Loud` draws.
 */
private const val LOUD_LEVEL = 0.6f

/** Low but not silent — a flat line is what 0 draws, and that is not the kit's `Quiet` cell. */
private const val QUIET_LEVEL = 0.15f

@CatalogComponent(
  id = "VoiceInputIndicator",
  // `Volume=Loud, Contained=No` in the kit's `Mic Indicators` set (`40000116:9337`), whose
  // axes are `Volume=Loud | Quiet` x `Contained=Yes | No`. Both halves are settled now: this
  // sticker draws the uncontained form at the loud level, and `container=contained` and
  // `volume=Quiet` below are the other two cells the kit publishes against it.
  reference = "figma:HKfLClZDLRyMhf4IQQLna8/40000116:9346",
  caption = "Shows that the glasses are listening, and how loudly. The kit's two levels fold in.",
)
@ee.schimke.m3catalog.glimmer.VoiceInputIndicatorStickerExhaustiveKitCells
@Preview
@Composable
fun VoiceInputIndicatorSticker() = Sticker {
  // Read outside the lambda: `level` is a plain `() -> Float` the component samples on every frame,
  // and a knob lookup is a composable call that may not run there.
  val level = glimmerVoiceLevel()
  if (glimmerContained()) ContainedVoiceInputIndicator(level = { level })
  else VoiceInputIndicator(level = { level })
}

@CatalogVariant(
  of = "VoiceInputIndicator",
  props = ["volume=Quiet"],
  caption = "A quiet reading, where the bars barely rise.",
)
@Preview
@Composable
fun QuietVoiceInputIndicatorSticker() = Sticker { VoiceInputIndicator(level = { QUIET_LEVEL }) }

@CatalogVariant(
  of = "VoiceInputIndicator",
  props = ["container=contained"],
  // `Contained=Yes` on the kit's `Mic Indicators` set — the same axis under a different word.
  kitAxis = "Contained",
  kitValue = "Yes",
  caption = "The contained form, which carries its own surface.",
)
@Preview
@Composable
fun ContainedVoiceInputIndicatorSticker() = Sticker {
  ContainedVoiceInputIndicator(level = { 0.6f })
}
