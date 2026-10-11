# UI builder document stickers (prototype)

The Card and Suggestion chip stickers, each defined as one UI builder document (a component the shell
calls with the catalog's live knobs and click counters) instead of hand-written Compose. Opt-in and
unpublished. The findings and the recommendation are in
[`docs/design/UID_STICKERS.md`](../docs/design/UID_STICKERS.md).

| File | What it is |
| --- | --- |
| `stickers/*.uid` | sticker bodies (`card.uid`, `suggestion-chip.uid`): `compose-ui-builder-document/v1-candidate` designs |
| `stickers.json` | its cells, each a list of `ui_builder_apply` operations (`setProperty`, `deleteNode`, …) |
| `builder-foundation-record.json`, `project-components.json` | the builder's `layout/` builtins and this module's image placeholder; every other component resolves against `:catalog`'s published `components.json` + `ui-builder.json` |
| `src/generator` | `generateUidStickers`: exports each document and cell with the builder's `ScreenExportGate` |
| `src/main` | the annotated `Sticker { … }` shell, and the builder-facing image placeholder |
| `compare-published.py` | pixel-for-pixel comparison against the delivery branch |

```sh
# Until the exporter's `exportComponents` is released, against Maven-local snapshots:
scripts/agent-gradle.sh -PuidStickers=true -PuidExportVersion=3.107.1-SNAPSHOT \
  -PscreenModelVersion=2.50.1-SNAPSHOT :uid-stickers:composePreviewRender
python3 uid-stickers/compare-published.py   # needs Pillow; all 31 cells report SAME
```

The generated Kotlin lands in `build/generated/uid/kotlin`, and that file is the sticker's source as
a reader should see it. `docs/evidence/uid-stickers/KitCard.generated.kt.txt` is a copy.

The copy in `stickers/*.uid` is the catalog's string resources (`resource` values), passed as the
placement's arguments, so the builder shows translated copy and the shell's knobs default to it.
