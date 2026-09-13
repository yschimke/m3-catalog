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
 * ## Which published glyph, and the residual on the unselected one
 *
 * The kit's placeholder is the Material `stars` symbol in both states, so the published set is the
 * honest source. Measured by rendering the copied paths beside the published ones at the same size:
 *
 *  * **Selected** — the kit draws a filled circle with a star knocked out of it, which is
 *    [Icons.Filled.Stars] exactly: 4.6% of pixels differ, which is antialiasing on the same shape.
 *    `Icons.Rounded.Stars` scores identically, so the corner family is invisible at sticker size.
 *  * **Unselected** — the kit draws a ring around a **solid** star (read off the kit's own render on
 *    `design-parity/main`, `ShortNavigationBarSticker/triptych-four.png`). That is Material Symbols
 *    at fill 0. The Compose set is the OLD Material Icons, frozen at 1.7.x, and none of its five
 *    styles draws it: `Outlined` and `TwoTone` hollow the star as well as the circle, while
 *    `Filled`, `Rounded` and `Sharp` are the inverse — a solid disc with a star-shaped hole, which
 *    is the SELECTED glyph and would collide with it.
 *
 * So [Icons.Outlined.Stars] is the closest thing that ships: same symbol, same ring, same star
 * geometry, hollow where the kit is solid. The fill axis is the residual, and it is recorded here
 * rather than closed by redrawing the glyph — a hand-authored path would buy fidelity on this one
 * sticker set by reintroducing exactly the unverifiable, untrackable source this file just removed.
 */
package ee.schimke.m3catalog

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.outlined.Stars
import androidx.compose.ui.graphics.vector.ImageVector

/** The selected `stars` placeholder: a filled circle with a star knocked out. */
val CatalogFilledStars: ImageVector
  get() = Icons.Filled.Stars

/**
 * The unselected `stars` placeholder: a ring around a star.
 *
 * The kit's star is solid and this one is hollow — see the file comment. Closest published glyph,
 * not an exact one.
 */
val CatalogOutlinedStars: ImageVector
  get() = Icons.Outlined.Stars
