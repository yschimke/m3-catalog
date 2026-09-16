/*
 * The shared frame every sticker in this module is drawn in, and the placeholder children it draws.
 *
 * A picture of a container is a picture of what it does to its children, so a container sticker is
 * mostly its children — and the children must not be components of any design system, or the
 * picture would say that a Row is a thing you put Material buttons in. They are unlabelled blocks,
 * which is also what the builder's own canvas draws inside a container nothing has been put in yet.
 *
 * ## No Material 3 anywhere in this module, and that is load-bearing
 *
 * The theme below is four colours of this module's own rather than `MaterialTheme`, and there is no
 * material3 dependency at all. Not purism: every library callable a sticker renders enters the
 * component record and is published on this catalog's shelf, so one `Text` inside one sticker would
 * put `compose-foundation/text` in a palette whose whole claim is that it owns containers and not
 * components. `docs/design/FOUNDATION_CATALOG.md` records the same reason for why the three
 * Material-shaped containers in this vocabulary — the two scaffolds and the carousel — are declared
 * in `ui-builder.policy.json` without a picture.
 */
package ee.schimke.foundationcatalog

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** The two blocks a sticker draws its children as, light and dark. */
data class StickerPalette(val child: Color, val altChild: Color)

private val LightPalette = StickerPalette(child = Color(0xFFD7E0FF), altChild = Color(0xFFE7D8F7))
private val DarkPalette = StickerPalette(child = Color(0xFF3A4A80), altChild = Color(0xFF4C3B63))

val LocalStickerPalette = compositionLocalOf { LightPalette }

/**
 * Wraps a sticker, selecting the light or dark blocks from the preview's `uiMode`.
 *
 * There is deliberately no surface and no padding behind a sticker, the same call `:catalog`'s
 * `Sticker` makes and for the same reason: the render's bounds must be the component's bounds.
 * Presentation spacing belongs to the viewer, outside the picture.
 */
@Composable
fun Sticker(content: @Composable () -> Unit) {
  val palette = if (isSystemInDarkTheme()) DarkPalette else LightPalette
  CompositionLocalProvider(LocalStickerPalette provides palette, content = content)
}

/**
 * Light and dark, as one annotation.
 *
 * `uiMode = 32` is `Configuration.UI_MODE_NIGHT_YES`, written as a raw int because the desktop
 * target has no `android.content.res` to name it from — the same literal and the same reason
 * `:catalog`'s own breakpoint previews carry it. It flips `isSystemInDarkTheme()`, which is the one
 * thing [Sticker] reads.
 */
@Preview(name = "Light") @Preview(name = "Dark", uiMode = 32) annotation class FoundationModes

/** One child of a container sticker: an unlabelled block. */
@Composable
fun Child(width: Dp = 56.dp, height: Dp = 32.dp, modifier: Modifier = Modifier) {
  Box(modifier.size(width, height).background(LocalStickerPalette.current.child))
}

/**
 * A second block, in a second colour, for the stickers where telling children apart is the point.
 */
@Composable
fun AltChild(width: Dp = 56.dp, height: Dp = 32.dp, modifier: Modifier = Modifier) {
  Box(modifier.size(width, height).background(LocalStickerPalette.current.altChild))
}
