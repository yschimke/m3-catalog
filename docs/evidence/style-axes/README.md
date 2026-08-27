# Style-axis evidence

Evidence for the refactor that turned the type scale, the corner scale and the six colour modes
into declared enum axes (issue #103), and named the kit's shadow gutters (issue #105).

**Nothing here is a before/after: that is the finding.** These three specimens draw a label the
refactor now derives from the enum entry (`TypeScaleRole.DisplayMedium` → `Display Medium`) rather
than from a string authored beside a `when` arm, so the risk the change carries is precisely that a
derived label reads differently from the literal it replaced. It does not — and the same holds for
the gutter constants, which inline to the values the annotations spelled.

## How it was checked

`composePreviewRender` was run on the affected previews at `origin/main` and again with the change,
and the two render directories compared:

```
./gradlew :catalog:composePreviewRender --rerun \
  --preview TypeScaleRoleSticker --preview CornerScaleSticker --preview ColorRoleGridSticker
./gradlew :catalog:composePreviewRender --rerun \
  --preview '*Snackbar*' --preview '*Card*' --preview '*Fab*'
```

| Set | Captures | Result |
| --- | --- | --- |
| the three style specimens and all their cells | 56 | every PNG byte-identical |
| the shadow-gutter stickers (FAB, cards, snackbar) | 88 | every PNG byte-identical |

The renderer names each file with a content hash, so the two listings matching **including the hash
suffix** is the same statement said twice.

## The images

The three captures below are what the specimens publish, unchanged by the refactor:

- `type-scale.png` — `Typography/Type scale` at `style=title-medium`, light.
- `corner-scale.png` — `Shape/Corner scale` at `corner=extra-large-increased`,
  light.
- `color-grid.png` — `Color/Role grid` at `theme=dark-high-contrast`.

## What did change, and where to see it

The knobs publish a **closed value set** now, so the viewer offers a picker instead of a text box.
That lands in the overrides sidecar beside each render rather than in the pixels —
`renders/TypeScaleRoleSticker_Light_VARIANT_title_medium-*.overrides.json` carries all fifteen roles
with `"optionsExhaustive": true`, where before it carried a bare string with no alphabet at all.
