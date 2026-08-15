# Slider size axis — rendered evidence

Rendered PNGs backing the visual evidence in the pull request for the slider
size axis (`agent/slider-size-axis`). Held on their own branch so the images
have a stable URL a reviewer can see inline, without carrying binaries into
`main` — the delivery branches publish the catalog's renders, and these
previews do not exist there until the change has merged.

* `slider-sizes-continuous.png` / `slider-sizes-range.png` — the five sizes
  stacked, extra small (the unchanged base) through extra large.
* `renders/` — the individual `@Preview` PNGs, exactly as
  `:catalog:composePreviewRender` wrote them.
