@file:CatalogGroup(name = "Voice input", section = "Communication")

package ee.schimke.m3catalog.glimmer

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.xr.glimmer.ContainedVoiceInputIndicator
import androidx.xr.glimmer.VoiceInputIndicator
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant

// The indicator is driven by a level in 0..1 that on a device comes from the microphone. A sticker
// has to pin it: 0.6 is a mid-scale reading, which shows the bars at differing heights rather than
// the flat line either extreme produces.

@CatalogComponent(
  id = "VoiceInputIndicator",
  // `Volume=Loud, Contained=No` in the kit's `Mic Indicators` set (`40000116:9337`), whose
  // axes are `Volume=Loud | Quiet` x `Contained=Yes | No`. `Contained=No` is settled — this
  // sticker draws the uncontained form and `container=contained` below is the other. The
  // VOLUME half is a judgement rather than a fact: the sticker pins `level = 0.6f`, above the
  // midpoint, so `Loud` is the closer of two cells the kit gives no third option between.
  // Nothing resolves against it either way, because the catalog declares no volume axis.
  reference = "figma:HKfLClZDLRyMhf4IQQLna8/40000116:9346",
  caption = "Shows that the glasses are listening, and how loudly. Level pinned at 0.6.",
)
@Preview(
  device = AI_GLASSES_DEVICE_SPEC,
  showBackground = true,
  backgroundColor = ADDITIVE_ZERO_BACKGROUND,
)
@Composable
fun VoiceInputIndicatorSticker() = Sticker { VoiceInputIndicator(level = { 0.6f }) }

@CatalogVariant(
  of = "VoiceInputIndicator",
  props = ["container=contained"],
  caption = "The contained form, which carries its own surface.",
)
@Preview(
  device = AI_GLASSES_DEVICE_SPEC,
  showBackground = true,
  backgroundColor = ADDITIVE_ZERO_BACKGROUND,
)
@Composable
fun ContainedVoiceInputIndicatorSticker() = Sticker {
  ContainedVoiceInputIndicator(level = { 0.6f })
}
