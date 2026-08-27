package ee.schimke.m3catalog

/**
 * The kit's **shadow gutters**: how much canvas a sticker's capture needs beyond the component's
 * own bounds so an elevation shadow is not clipped out of the render.
 *
 * A Material shadow is drawn *outside* the container it belongs to and is offset **downward** — the
 * key light sits above the surface — so the room it needs is asymmetric: a little above, twice as
 * much below, and the same on both sides. That asymmetry was six copies of two unnamed four-value
 * literals across `Fab.kt`, `Menus.kt`, `Snackbar.kt`, `Buttons.kt`, `ToggleButtons.kt` and
 * `Cards.kt`, with nothing saying what `top = 7` was — a nudge someone eyeballed, or a measurement
 * (#105). They are measurements, and this is where they are stated.
 *
 * `const val` rather than `Dp`, because these are `@CaptureGutter` arguments and an annotation
 * takes compile-time constants. The gutter belongs to the **capture**, never to the tree: padding
 * inside the sticker measures the component in a smaller box and grows the canvas, which is what
 * drew a guttered sticker smaller than its siblings (#179).
 *
 * Nothing derives a `@Preview` width from these any more. The frames used to — `@CatalogModes366`
 * was "a 344dp snackbar plus its 11dp gutter", the same fact spelled a second time as arithmetic
 * baked into an annotation name — and a sticker then paid for the gutter twice. Now the frame is
 * the kit's own measurement of the component and the gutter is here, so the two cannot disagree.
 */
object KitShadowGutter {

  /** Level 3, offset downward: the FAB, the snackbar bar and the menu container. */
  const val Level3Side = 11

  /** @see Level3Side */
  const val Level3Top = 7

  /** @see Level3Side */
  const val Level3Bottom = 15

  /** Level 1, uniform but for the extra pixel the downward offset needs: the elevated buttons. */
  const val Level1All = 4

  /** @see Level1All */
  const val Level1Bottom = 5
}
