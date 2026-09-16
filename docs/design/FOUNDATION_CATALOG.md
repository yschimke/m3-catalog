# `compose-foundation` — the builder's own vocabulary, as a catalog

Status: **published, not yet read** (2026-09). The containers, screen frames, image asset and shape
layers a UI-builder design is assembled out of, rendered and published from this repository as an
ordinary catalog — `:foundation-catalog` → `design-artifacts/compose-foundation` — so `m3-catalog`
and `wear-m3-catalog` borrow them from a delivery branch instead of from Kotlin synthesised inside
the preview server.

The consumer's side is
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
catalog inside the preview server, on every request. Delete the synthesiser without a replacement
and the published m3 shelf has no container to put anything in; keep it and a catalog repository
cannot change its own palette without a server release, which is the whole thing the contract exists
to end.

`compose-foundation` is the replacement, and it is a catalog like any other here: a Gradle module, a
cover sheet, a builder policy, a delivery branch.

## What is here

| Path | Kind | What it carries |
| --- | --- | --- |
| `foundation-catalog/` (`:foundation-catalog`) | module | seven stickers — the pictures for the shelf |
| `foundation-catalog/catalog.spec.json` | authored | the cover sheet: system, title, modes, hero |
| `foundation-catalog/ui-builder.policy.json` | authored, **`builtins` generated** | the fourteen declarations, the shelf order, the platform word |
| `foundation-catalog/goldens/packaged-m3-builder-vocabulary.json` | frozen | the packaged catalog's builder-namespace slice, copied byte-for-byte with provenance |
| `foundation-catalog/curations.json` | authored | which platform borrows which of them, in what order, and the Wear note |
| `scripts/foundation-catalog.mjs` | code | the projection golden → `builtins`, and `--check` |
| `scripts/foundation-catalog.test.mjs` | code | the frozen sets, restated once, and the invariants |

**The golden is copied, not retyped.** #819 is explicit: "the source builds these lists partly from
constants, and a hand-reconstruction of the Wear set missed `remote-compose/custom` and
`remote-compose/inline`". `provenance` records the repository, path and commit, so a refresh is a
re-copy plus a reviewable diff in the generated block.

## Why all fourteen are `builtins`

The schema's rule is that a component with a call site belongs in the record, and a builtin is for
what has none. Every component of this vocabulary meets that condition **in this catalog's record**,
for two different reasons:

- **Discovery does not reach foundation symbols.** The record scopes library components to
  `material3` / `material` / `wear`
  ([`COMPONENT_RECORD.md`](https://github.com/yschimke/compose-ai-tools/blob/main/docs/design/COMPONENT_RECORD.md):
  "Layout primitives (`Column`, `Row`, `Box`) are also absent, because inference scopes library
  components to `material3`/`material`/`wear`"). A `@BuilderComponent` on the `Box` sticker attached
  to nothing and was reported as `component.policy.orphaned` — eleven times, on the first run. That
  is the measurement this design is built on rather than a guess about it.
- **The three Material-shaped containers are deliberately not rendered here.** `layout/scaffold`,
  `layout/supporting-pane-scaffold` and `layout/horizontal-carousel` are Material 3 composables, and
  every library callable a sticker renders enters the record and is published on this catalog's
  shelf. Drawing them would put `compose-foundation/text`, `/surface` and `/top-app-bar` in a palette
  whose whole claim is that it owns containers and not components. They are declared without a
  picture; `:catalog` is where a Material scaffold is pictured.

The module therefore depends on `foundation` and `ui` and nothing else, draws its children as plain
coloured blocks, and carries exactly **one** `@BuilderComponent` — an `exclude` that keeps `Sticker`,
its own theme wrapper, off the shelf. Without it the one project-origin composable in the module
would be offered as a component of the catalog.

## The one thing the schema cannot say honestly

`builtins.role` is the **structural-template** role, a closed set of six — `screen-root`, `list`,
`list-item`, `overlay`, `controlled`, `decoration` — described as the roles the Wear screen emitter
decomposes into. The builder's own vocabulary does not decompose into them:

| declaration | acceptance role | structural role published | honest? |
| --- | --- | --- | --- |
| `layout/scaffold`, `layout/supporting-pane-scaffold` | `Scaffold` | `screen-root` | yes |
| `layout/lazy-*`, `layout/for-each`, `layout/horizontal-carousel` | `Container` | `list` | close enough — all write repeated children |
| `layout/box`, `layout/column`, `layout/row` | `Container` | `list` | **no** — a box is not a scrolling list |
| `shape/*` | `Leaf` | `decoration` | yes |
| `asset/image` | `Leaf` | `decoration` | roughly |

The mapping lives in `ROLES` in `scripts/foundation-catalog.mjs`, with the same note. The contract
sets the test for growing the set — "the test that a new role is general is that two catalogs use
it" — and a `container` role is the candidate this catalog is the first evidence for. Nothing reads
the file yet, so the mismatch costs nothing today and would cost an export later.

Four further fields the packaged declarations carry and `compose-ui-builder-policy/v1` has no home
for, recorded so the next upstream step is a list rather than a rediscovery: a component's own
acceptance role, `wasm.notes` per component, `code.symbol` / `code.imports`, the `svg` block, and a
slot's `ordered` flag.

## The per-platform borrow sets

Not one set. Each platform borrows a curated subset, and the curation is load-bearing:
`WearScreenCodeExporter` refuses `layout/lazy-grid`, `layout/scaffold` and the shapes with "no Wear
Compose Material 3 counterpart this generator can write", so a watch palette holding the mobile
seventeen would guarantee a failed export for any design that used one — discovered at the end,
which is the worst moment.

| platform | count | components |
| --- | --- | --- |
| `mobile` | 17 | `asset/image`, `layout/box`, `layout/column`, `layout/for-each`, `layout/horizontal-carousel`, `layout/lazy-column`, `layout/lazy-grid`, `layout/lazy-row`, `layout/row`, `layout/scaffold`, `layout/supporting-pane-scaffold`, `remote-compose/custom`, `remote-compose/document`, `remote-compose/inline`, `shape/colour-dot`, `shape/linear-gradient`, `shape/radial-gradient` |
| `wear` | 7 | `layout/box`, `layout/column`, `layout/row`, `asset/image`, `remote-compose/document`, `remote-compose/inline`, `remote-compose/custom` |

The orders differ and both are deliberate: the order is the insert panel's.
`scripts/foundation-catalog.test.mjs` restates both lists — the one place in the suite allowed to,
since a test reading the sets from the file it checks would pass on any edit.

`curations.json` also carries the **Wear note**: a Wear palette rewrites every borrowed *foundation*
component's `wasm.notes` to the Wear explanation ("Foundation, shared by Compose on both platforms…"),
which says the opposite of what a borrowed *Material* component's note said. Injecting the base
component instead would carry the mobile note, and nothing tested notes, so it would have changed
silently. The seams keep their own note, because what `remote-compose/document` says about the CMP
player it draws through is true on a watch too.

And it answers #819's third question, **slot narrowing: none**. The Wear catalog narrows slots for
its *own* components (`wear-m3/edge-button` to `acceptedTraits: ["Action"]`); no borrowed component
is narrowed, so this catalog publishes one declaration per component and a borrowing palette takes
it unchanged apart from the note.

**`curations.json` has no reader yet, and cannot have one in this schema.** A policy declares one
`platform` word; a donor serves several. Until the schema grows a per-platform block, the file is
authored data with tests around it — the same posture `ui-builder.policy.json` was written in.

## What this catalog does not own

**The `remote-compose/` seams.** `remote-compose/document`, `/inline` and `/custom` are seams into
Remote Compose — a different library, with `remote-m3` as the catalog that describes it, published
from `yschimke/wear-m3-catalog`'s `:remote-catalog`. Declaring them here would be this catalog
claiming a vocabulary it does not own, which is the move `m3-catalog`'s policy refuses one level up.
A borrowing palette still gets them: from Remote Compose's own catalog, or from the packaged
fallback until that publishes them.

**The `remote-compose` platform's curation.** `remote-m3` is out of scope here by the same
reasoning, and the absence is declared in `curations.json`'s `declined` block with its reason and its
owner rather than left to be inferred. **A consumer must not read a missing platform entry as an
instruction to serve that platform an empty palette** — an empty palette is a screen nothing can be
put on, which is the failure this whole exercise exists to avoid.

## What has to happen next, and where

1. **In the preview server**: `composeFoundationCatalog` loads the published file instead of building
   a list, which is #819's "external eventually", and the `remoteM3Catalog` / `wearM3Catalog`
   generators become deletable. `ComposeFoundationFaithfulnessTest` is what proves the swap, and it
   can only be written while both exist.
2. **In compose-ai-tools**: a `container` structural role, and the four fields in the list above — or
   a decision that a donor catalog publishes its declarations in the capabilities shape rather than
   through a policy.
3. **Here**, when either lands: the three Material-shaped containers get pictures of their own, from
   whichever module can render them without dragging Material components onto this shelf.
