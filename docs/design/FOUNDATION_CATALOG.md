# `compose-foundation` — the builder's own vocabulary, published

Status: **authored, not yet read** (2026-09). The donor catalog the UI builder hands a published
catalog its containers, shapes and image asset from, as data this repository publishes instead of a
`mapOf` the preview server synthesises at startup.

The consumer's side of this is
[yschimke/compose-preview-server#819](https://github.com/yschimke/compose-preview-server/issues/819),
and the contract both sides answer to is
[`UI_BUILDER_CATALOG_CONTRACT.md`](https://github.com/yschimke/compose-preview-server/blob/main/docs/design/UI_BUILDER_CATALOG_CONTRACT.md).
Read those for the *why*; this document is what this repository owns.

## The problem, stated from this side

`ui-builder.policy.json` in this repository declares **zero** builtins, and says why:

> The builtins the packaged catalog carries — `layout/*`, `shape/*`, `asset/image`,
> `remote-compose/*` — are components OF THE BUILDER: a column is not a Material 3 component and
> this repository does not render one. Declaring them here would be this catalog claiming to own the
> builder's own vocabulary.

That is the right call and it leaves a hole. A design has to be put *inside* something, so the
seventeen components a mobile palette needs are served to production today by a **synthesised**
catalog inside the preview server, on every request — `withBuilderVocabulary` reading a donor built
from the catalog that binary packages. Delete the donor without a replacement and the published m3
shelf has no container to put anything in; keep it and a catalog repository cannot change its own
palette without a server release, which is the whole thing the contract exists to end.

`compose-foundation` is the replacement: the same declarations, owned by a catalog repository,
published as a file.

## What is here

| File | Kind | What it carries |
| --- | --- | --- |
| `foundation-catalog/goldens/packaged-m3-builder-vocabulary.json` | frozen | the seventeen builder-namespace components of the packaged catalog, copied byte-for-byte, plus the packaged shelves and the asset registry |
| `foundation-catalog/curations.json` | authored | which platform borrows which of them, in what order, on which shelf, and what a borrowed component is told about itself |
| `foundation-catalog/compose-foundation.json` | generated | the donor catalog: fourteen owned declarations, three referenced seams, one resolved palette per platform |
| `scripts/foundation-catalog.mjs` | code | the join, and `--check` |
| `scripts/foundation-catalog.test.mjs` | code | the frozen sets, restated once, and the invariants below |

Nothing is typed twice: a component is declared **once**, in the golden, and a curation names it by
id. The generated file is committed and regenerated-and-diffed in CI, the same contract
`design-map.json` and the samples spec already carry here.

**The golden is copied, not retyped.** #819 is explicit about this — "the source builds these lists
partly from constants, and a hand-reconstruction of the Wear set missed `remote-compose/custom` and
`remote-compose/inline`". `provenance` records the repository, path and commit it came from, so a
refresh is a re-copy plus a reviewable diff in the generated catalog.

## The three things the extraction had to carry

#819 names them; here is what each one became.

**1. The per-platform borrow sets.** Not one set. Each platform borrows a curated subset, and the
curation is load-bearing: `WearScreenCodeExporter` refuses `layout/lazy-grid`, `layout/scaffold` and
the shapes with "no Wear Compose Material 3 counterpart this generator can write", so a watch
palette holding the mobile seventeen would guarantee a failed export for any design that used one —
discovered at the end, which is the worst moment.

| platform | count | components |
| --- | --- | --- |
| `mobile` | 17 | `asset/image`, `layout/box`, `layout/column`, `layout/for-each`, `layout/horizontal-carousel`, `layout/lazy-column`, `layout/lazy-grid`, `layout/lazy-row`, `layout/row`, `layout/scaffold`, `layout/supporting-pane-scaffold`, `remote-compose/custom`, `remote-compose/document`, `remote-compose/inline`, `shape/colour-dot`, `shape/linear-gradient`, `shape/radial-gradient` |
| `wear` | 7 | `layout/box`, `layout/column`, `layout/row`, `asset/image`, `remote-compose/document`, `remote-compose/inline`, `remote-compose/custom` |

The orders differ from each other and both are deliberate: the order is the insert panel's.
`scripts/foundation-catalog.test.mjs` restates both lists — the one place in the suite allowed to,
since a test reading the sets from the file it checks would pass on any edit.

**2. The per-platform notes.** A Wear palette rewrites every borrowed *foundation* component's
`wasm.notes` to the Wear explanation ("Foundation, shared by Compose on both platforms…"), which
says the opposite of what a borrowed *Material* component's note said: a borrowed Material component
was a stand-in drawn by the wrong library's lookalike, while Box, Column, Row and Image are the same
declarations on both platforms, so there is nothing to stand in for. Injecting the base component
instead would carry the mobile note, and nothing tested notes, so it would have changed silently.
The text lives once, in `curations.json`, and a test asserts it reaches every borrowed component
except the seams — which keep their own note, because what `remote-compose/document` says about the
CMP player it draws through is true on a watch too.

**3. Slot narrowing on borrowed components: none.** A checked answer, not an omission. The Wear
catalog narrows slots for its *own* components (`wear-m3/edge-button` to `acceptedTraits:
["Action"]`); the foundation curation rewrites notes and touches no slot, property, trait or
modifier on a borrowed one. `the note is the only thing a curation changes about a borrowed
component` is that claim as a test, field for field, on both platforms.

## What this file does not cover

**`remote-compose`, deliberately.** `remote-m3` is published by `yschimke/wear-m3-catalog`'s
`:remote-catalog`, and Remote Compose is a different library with a catalog of its own to describe
it — so the platform that *is* Remote Compose does not borrow its vocabulary from a Compose
foundation catalog published beside Material 3. The preview server keeps its own `remote-compose`
curation (eight components, plus the modifier narrowing `RemoteContentEmitter` can write) and this
file does not replace it.

That absence is declared rather than left to be inferred: `declined["remote-compose"]` names the
reason and the owner, and a test asserts it is there. **A consumer must not read a missing platform
entry as an instruction to serve that platform an empty palette** — an empty palette is a screen
nothing can be put on, which is the failure mode this whole exercise is trying not to ship.

**The seams are referenced, not owned.** `remote-compose/document`, `/inline` and `/custom` are
seams into Remote Compose, so the component belongs to whichever catalog describes it and
`compose-foundation` says only which seam a platform takes and where it sits on the shelf. The
golden carries the packaged declarations so the file resolves today; a reader holding the Remote
Compose catalog's own declaration must prefer it, and a seam no source declares is left off the
palette rather than invented — which is what the preview server already does.

## Why this is not `ui-builder.policy.json`

The obvious home for a published vocabulary is the policy file, under `builtins`. It does not fit
today, and the gaps are worth naming because closing them is the next upstream step rather than
something this repository can do:

| What the donor carries | `compose-ui-builder-policy/v1` |
| --- | --- |
| a component's acceptance role (`Container`, `Leaf`, `Scaffold`) | only the *structural template* role (`screen-root`, `list`, …) — a different, closed vocabulary. Slots may name acceptance roles; a builtin cannot state its own |
| `wasm.notes` per component | no field — and the Wear note is one of the three things the extraction must carry |
| `code.symbol` / `code.imports` | no field; `implementation` names a record entry instead, which a donor with no module of its own has nothing to point at |
| the `svg` block (`status`, `fallback`, `blocksExport`) | no field |
| a slot's `ordered` flag and `min` | `required` and `max` only |
| a per-platform borrow set | nothing: a policy declares one `platform` word, and a donor serves several |

`compose-foundation.json` therefore declares its own small schema (`compose-foundation-donor/v1`)
and carries the declarations in the shape the server already consumes them in
(`compose-ui-builder-capabilities/v1-candidate`). Authoring ahead of a reader is the established
posture here — `ui-builder.policy.json` opens with "Nothing reads this file yet: it is authored so
the catalog it generates can be diffed against the one the preview server packages today, BEFORE the
server changes a reader" — and this file is in the same state.

## What has to happen next, and where

1. **Here, when the vocabulary needs pictures.** The donor has no renderable module: its properties
   are *builder* vocabulary (`verticalArrangement: "spaceBetween"`), not Compose signatures
   (`verticalArrangement: Arrangement.Vertical`), so a record derived from stickers would not
   reproduce them. A `:foundation-catalog` module would buy palette thumbnails and a delivery
   branch, not the declarations; it is worth doing on its own merits and is not a prerequisite.
2. **In the preview server**: `composeFoundationCatalog` loads this file instead of building a list,
   which is #819's "external eventually", and the `remoteM3Catalog` / `wearM3Catalog` generators
   become deletable. `ComposeFoundationFaithfulnessTest` is what proves the swap, and it can only be
   written while both exist.
3. **In compose-ai-tools**, if the donor is ever to be generated rather than authored: the policy
   schema grows the fields in the table above.
