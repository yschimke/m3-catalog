/*
 * The placeholder glyph every sticker draws, from the published Material icon set.
 *
 * ## Why not the kit's own path data
 *
 * These two used to be `figmaStars(...)`: `pathData` strings copied out of the Figma kit and
 * re-scaled by hand until they looked right. That is the wrong source even though it came from the
 * authoritative side of the comparison — a copied path is a snapshot that cannot track the kit, it
 * arrives with no licence trail, and a hand-tuned `scale` / `translation` is a fudge factor nobody
 * can check. It also makes the catalog draw something the library it documents does not ship.
 *
 * The kit's placeholder IS the Material `stars` symbol, so the published set is both the honest
 * source and the same picture. Measured rather than assumed, by rendering the copied paths beside
 * these at the same size: **4.6%** of pixels differ on the filled pair — antialiasing on a glyph of
 * the same shape — and 11.9% on the outlined pair, which is the hand-applied 1.2x scale rather than
 * a difference in the drawing. `Icons.Rounded.Stars` scores identically to `Icons.Filled.Stars`
 * against the copied filled glyph (4.6%), so the corner family is invisible at sticker size and the
 * Filled / Outlined pair is what these names have always meant.
 */
package ee.schimke.m3catalog

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.outlined.Stars
import androidx.compose.ui.graphics.vector.ImageVector

/** The selected `stars` placeholder: a filled circle with a star knocked out. */
val CatalogFilledStars: ImageVector
  get() = Icons.Filled.Stars

/** The unselected `stars` placeholder: an outlined circle with a filled star. */
val CatalogOutlinedStars: ImageVector
  get() = Icons.Outlined.Stars
