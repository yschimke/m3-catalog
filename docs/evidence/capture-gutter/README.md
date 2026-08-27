# Capture-gutter evidence

Rendered with `./gradlew :catalog:composePreviewRender` at the catalog's 2.625 density, before and
after moving the elevated stickers' shadow gutters from a padded `Box` inside the component tree to
`@CaptureGutter` on the preview (issue #179).

## `elevated-shadow-before-after.png`

`ToggleButton/Elevated` at `m-square`, light, native pixels, canvas outlined in red.

Before (left) the Level 1 shadow stops dead at the container's edge: `Sticker`'s `Surface` clipped
its content to its own bounds, so the 4dp padding bought empty margin rather than shadow. After
(right) the shadow is drawn in full, in the gutter the capture now reserves for it. Both canvases
are 338x171 — the gutter did not grow the render, it moved out of the component's measurement.

## `sheet-row-before-after.png`

`Button/Filled`, `Button/Tonal`, `Button/Outlined`, `Button/Elevated` — each PNG fitted to the
sheet's 213px column, as the front page lays them out. Rows, top to bottom:

1. **before** — the elevated arm draws 7.0% smaller than its three siblings (#179's measurement).
2. **after** — same, because the canvas is still `component + gutter`; what changed is that the
   gutter is now *outside* the component's bounds and declared in `previews.json` as
   `params.captureGutter`.
3. **after, with the declared gutter subtracted** — what a gutter-aware consumer draws: all four at
   the same size. The subtraction is the consumer's half, upstream in compose-ai-tools.
