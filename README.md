# Interaction states — rendered evidence

Backing the visual evidence in the interaction-state pull request
(`agent/interaction-state-variants`). Held on its own branch so the images have
a stable URL a reviewer can see inline, without carrying binaries into `main`.

* `interaction-states.png` — nine components x enabled / hovered / focused /
  pressed, from the rendered previews.
* `renders/` — the individual `@Preview` PNGs, exactly as
  `:catalog:composePreviewRender` wrote them.

Every capture is a real interaction driven by the renderer before the frame is
taken, not a forged `MutableInteractionSource`.
