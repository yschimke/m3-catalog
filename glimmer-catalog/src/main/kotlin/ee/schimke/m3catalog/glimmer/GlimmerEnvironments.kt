/*
 * What a wearer actually sees: the same additive capture, composited over a passthrough scene.
 *
 * ## Why these exist beside the stickers rather than replacing them
 *
 * `@GlimmerEnvironmentPreview` is a CAPTURE property, not a knob — discovery records it as
 * `captures[].glimmerEnvironment`, and the composite REPLACES the preview's published PNG (the
 * uncomposited render is kept beside it as `<name>.raw.png`). Putting it on `ButtonSticker` would
 * therefore change what that sticker publishes, and `design-parity` would score a photograph
 * against a kit node drawn on nothing. So the environments get preview functions of their own.
 *
 * They carry no `@CatalogComponent` / `@CatalogVariant` on purpose. These are not kit cells — the
 * Glimmer kit draws its components on a flat ground and publishes no environment axis — so they add
 * nothing to `glimmer-design-map.json` and are compared against nothing, exactly like the adaptive
 * layouts in `catalog/src/main/kotlin/ee/schimke/m3catalog/adaptive/`. `AGENTS.md` is explicit that
 * this is the right treatment for a preview the kit has no node for.
 *
 * ## The compositing is ADDITIVE, and that is the whole point
 *
 * `data-glimmer-environment-connector`'s compositor is, per channel,
 * `min(255, backdrop + sticker)` — read off the bytecode rather than assumed. That is the physics
 * of the hardware: the panel EMITS light into the scene, so a black pixel adds nothing and a bright
 * one adds its own value to whatever is behind it. It also explains why the stickers are captured
 * on opaque black rather than on transparency: the compositor ignores alpha entirely and reads RGB
 * as radiance, so additive-zero black IS the transparent pixel. See #380 and #394.
 *
 * The `Busy` scene is the one worth looking at: a chip that is perfectly legible on `Dark` washes
 * out against a bright cluttered street, because additive light cannot darken anything. That is a
 * true statement about the hardware and not a rendering artifact.
 *
 * One environment per function, because `@GlimmerEnvironmentPreview` is not repeatable — Kotlin
 * rejects a second one on the same function outright.
 */
package ee.schimke.m3catalog.glimmer

import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.xr.glimmer.Button
import androidx.xr.glimmer.Card
import androidx.xr.glimmer.Icon
import androidx.xr.glimmer.Text
import ee.schimke.composeai.preview.GlimmerEnvironment
import ee.schimke.composeai.preview.GlimmerEnvironmentPreview

@Composable
private fun EnvironmentButton() {
  Button(
    onClick = {},
    leadingIcon = { Icon(Icons.AutoMirrored.Rounded.Send, stringResource(R.string.cd_send)) },
  ) {
    Text(stringResource(R.string.label_button))
  }
}

@Composable
private fun EnvironmentCard() {
  ContentFrame {
    Card(
      header = { Image(HeaderImage, "Header artwork", contentScale = ContentScale.FillWidth) },
      title = { Text(stringResource(R.string.label_title)) },
      subtitle = { Text(stringResource(R.string.label_subtitle)) },
      leadingIcon = { Icon(Icons.AutoMirrored.Rounded.Send, stringResource(R.string.cd_send)) },
    ) {
      Text(stringResource(R.string.label_body))
    }
  }
}

@GlimmerEnvironmentPreview(environment = GlimmerEnvironment.Light)
@Preview
@Composable
fun ButtonOnLightEnvironment() = Sticker { EnvironmentButton() }

@GlimmerEnvironmentPreview(environment = GlimmerEnvironment.Dark)
@Preview
@Composable
fun ButtonOnDarkEnvironment() = Sticker { EnvironmentButton() }

@GlimmerEnvironmentPreview(environment = GlimmerEnvironment.Busy)
@Preview
@Composable
fun ButtonOnBusyEnvironment() = Sticker { EnvironmentButton() }

@GlimmerEnvironmentPreview(environment = GlimmerEnvironment.VeniceCanalCats)
@Preview
@Composable
fun ButtonOnVeniceCanalCatsEnvironment() = Sticker { EnvironmentButton() }

@GlimmerEnvironmentPreview(environment = GlimmerEnvironment.Light)
@Preview
@Composable
fun CardOnLightEnvironment() = Sticker { EnvironmentCard() }

@GlimmerEnvironmentPreview(environment = GlimmerEnvironment.Dark)
@Preview
@Composable
fun CardOnDarkEnvironment() = Sticker { EnvironmentCard() }

@GlimmerEnvironmentPreview(environment = GlimmerEnvironment.Busy)
@Preview
@Composable
fun CardOnBusyEnvironment() = Sticker { EnvironmentCard() }

@GlimmerEnvironmentPreview(environment = GlimmerEnvironment.VeniceCanalCats)
@Preview
@Composable
fun CardOnVeniceCanalCatsEnvironment() = Sticker { EnvironmentCard() }
