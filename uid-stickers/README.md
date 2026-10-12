# UI builder document stickers (prototype)

The UI builder documents behind 50 of the catalog's 59 stickers. Those stickers are cut over: their
bodies in `:catalog` call the components these documents export, checked in as
`catalog/src/uid/kotlin`, and every capture is pixel-identical to the hand-written stickers they
replaced. Status: [`CUTOVER.md`](CUTOVER.md); method: [`MIGRATING.md`](MIGRATING.md). Opt-in and
unpublished. The findings and the recommendation are in
[`docs/design/UID_STICKERS.md`](../docs/design/UID_STICKERS.md).

| File | What it is |
| --- | --- |
| `stickers/*.uid` | sticker bodies (`card.uid`, `suggestion-chip.uid`): `compose-ui-builder-document/v1-candidate` designs |
| `stickers.json` | its cells, each a list of `ui_builder_apply` operations (`setProperty`, `deleteNode`, …) |
| `builder-foundation-record.json`, `project-components.json` | the builder's `layout/` builtins and this module's image placeholder; every other component resolves against `:catalog`'s published `components.json` + `ui-builder.json` |
| `src/generator` | `generateUidStickers`: exports each document and cell with the builder's `ScreenExportGate` |
| `src/main` | the annotated `Sticker { … }` shell, and the builder-facing image placeholder |
| `compare-local.py` | the cutover check: every capture of each hand-written sticker against its generated shell |
| `cutover-report.py`, `CUTOVER.md` | the readiness table over all 59 stickers |
| `annotations-of.py`, `add-project-component.py`, `tools/` | migration helpers |

```sh
# Until the exporter's `exportComponents` is released, against Maven-local snapshots:
scripts/agent-gradle.sh -PuidStickers=true -PuidExportVersion=3.107.1-SNAPSHOT \
  -PscreenModelVersion=2.51.1-SNAPSHOT :uid-stickers:composePreviewRender
CATALOG_RENDERS=catalog/build/compose-previews/renders python3 uid-stickers/compare-local.py  # after :catalog:composePreviewRender
```

The generated Kotlin lands in `build/generated/uid/kotlin`, and that file is the sticker's source as
a reader should see it. `docs/evidence/uid-stickers/KitCard.generated.kt.txt` is a copy.

The copy in `stickers/*.uid` is the catalog's string resources (`resource` values), passed as the
placement's arguments, so the builder shows translated copy and the shell's knobs default to it.
