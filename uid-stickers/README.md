# UI builder document stickers (prototype)

The Card stickers, defined as one UI builder document instead of hand-written Compose. Opt-in and
unpublished. The findings and the recommendation are in
[`docs/design/UID_STICKERS.md`](../docs/design/UID_STICKERS.md).

| File | What it is |
| --- | --- |
| `stickers/card.uid` | the sticker body: a `compose-ui-builder-document/v1-candidate` design |
| `stickers.json` | its cells, each a list of `ui_builder_apply` operations (`setProperty`, `deleteNode`, …) |
| `component-record.json` | the vocabulary the export resolves against: the served `m3-catalog` record plus `OutlinedButton` and `m3catalog/image-placeholder` |
| `src/generator` | `generateUidStickers`: exports each document and cell with the builder's `ScreenExportGate` |
| `src/main` | the annotated `Sticker { … }` shell, and the builder-facing image placeholder |
| `compare-published.py` | pixel-for-pixel comparison against the delivery branch |

```sh
scripts/agent-gradle.sh -PuidStickers=true :uid-stickers:composePreviewRender
python3 uid-stickers/compare-published.py   # needs Pillow; all 12 cells report SAME
```

The generated Kotlin lands in `build/generated/uid/kotlin`, and that file is the sticker's source as
a reader should see it. `docs/evidence/uid-stickers/KitCard.generated.kt.txt` is a copy.

The copy in `stickers/*.uid` is English literals. The catalog's string-resource rule cannot be met
until documents can name a string resource; the design doc covers this.
