# Migrating a sticker to a UI builder document

> 50 stickers are already cut over (`CUTOVER.md`): their shells live in `:catalog`'s `sections/`
> files and their generated components in `catalog/src/uid/kotlin`. To migrate one of the rest,
> develop its shell in `uid-stickers/src/main/kotlin/ee/schimke/m3catalog/uid/` as below, check it
> with `compare-local.py` against a `:catalog` render, then run `cutover.py` to move it in.

A migrated sticker is ready to cut over when `compare-local.py` reports it `SAME`: every capture
the hand-written `:catalog` sticker publishes (modes, override variants, interaction states,
exhaustive kit cells) has a pixel-identical capture from the generated one. Cutting over is then a
body swap, because the shell already carries the hand-written annotations and name.

## The pieces, per sticker

| File | Holds |
| --- | --- |
| `stickers/<name>.uid` | the body: one component definition (`components`) placed once (`roots`) |
| `stickers/<name>.json` | `variants` (cells: builder operations applied before export) and `pairs` (shell function → hand-written function) |
| `src/main/kotlin/ee/schimke/m3catalog/uid/<Group>.kt` | the shells: verbatim annotations, the hand-written function name, and the knobs |
| `project-components-<group>.json` + `<Group>Components.kt` | only when the document needs a kit add-on the catalog has no record for |

## Steps

1. **Read the hand-written sticker** in `catalog/src/main/kotlin/ee/schimke/m3catalog/sections/`.
   Note every knob it reads (`catalogText`, `catalogChoice`, `catalogBoolean`, `counted`, function
   parameters), in order, and every branch.
2. **Write the document.** The sticker's visual body becomes a component definition:
   - copy is a `binding` named for the knob key; the placement's `arguments` give the default, as a
     `resource` (`{"type": "resource", "resourceKey": "chip_sounds_good"}`) where the sticker uses a
     string resource;
   - a boolean or enum knob that only feeds a parameter is a `binding` too (`enabled`);
   - a click is an action-free event (`"eventBindings": {"click": []}`), which becomes an
     `on<Node><Event>` parameter the shell wires to `counted(…).onClick`;
   - component ids come from `:catalog`'s policy (`catalog/build/compose-previews/ui-builder.json`,
     `statusSemantics.components`), e.g. `m3/suggestion-chip`; layout is `layout/row|column|box`.
3. **Cells for structural axes.** An axis that changes the tree, or the Compose function, is a cell
   in `stickers/<name>.json`: `setProperty`, `removeNodeProperty`, `deleteNode`, `setModifiers`,
   `addNode` (a node appended to a parent's slot), `setComponentId` (`ElevatedSuggestionChip` is
   `m3/elevated-suggestion-chip`). Each cell exports as its own component, `<Name><Cell>`.
4. **Write the shell.** `python3 uid-stickers/annotations-of.py <HandWrittenFunction>` prints the
   annotation block; paste it verbatim and give the shell the same function name and parameters.
   The body reads the same knobs in the same order and calls the generated component (named
   arguments), choosing the cell from the structural knobs.
5. **Pair it** in `stickers/<name>.json`: `"pairs": {"<Shell>": "<HandWritten>"}`.
6. **Check:**

   > Since the cut-over `:uid-stickers` is generator-only and renders nothing, so
   > `compare-local.py` (which reads this module's own renders) has no lane here. The check is the
   > catalog's own render: swap the sticker's body for the generated call and diff `:catalog`
   > before and after. wear-m3-catalog and remote-m3-catalog compile their shells into the catalog
   > behind `-PuidStickers=true` and pair them by package; that is the shape to adopt for a
   > side-by-side lane.

   ```sh
   scripts/agent-gradle.sh -PuidStickers=true :uid-stickers:generateUidStickers
   scripts/agent-gradle.sh :catalog:composePreviewRender   # before and after the swap
   ```

   Any pixel difference between the two renders is a real difference; read the generated Kotlin
   in `catalog/src/uid/kotlin/` against the hand-written body. The PR's visual diff shows the same
   comparison.

## Rules

- **The Material component stays in the document.** A project component is for a kit add-on or a
  graphic (`m3catalog/image-placeholder`, `m3catalog/stars-icon`), or a value the builder cannot
  spell yet; never a wrapper around the component under comparison.
- **When the exporter refuses**, record the refusal verbatim: it is the vocabulary to add upstream.
  Do not work around it with catalog code in the document.
- **No catalog helpers in the document.** `counted`, `catalogText`, `catalogChoice` and
  `catalogInteractive()` belong in the shell.
