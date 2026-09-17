# The UI builder's template designs, and why this catalog is due exactly one

The UI builder offers a starting point when somebody makes a new design. For `m3-catalog` there is
one on the New design chooser — **`blank`**, a `Scaffold` over an empty `Box` — and it is a Kotlin
document builder in the preview server today (`ui-builder-export/…/UiBuilderTemplates.kt`).

The plan to move it here is
[`UI_BUILDER_SEED_TEMPLATES.md`](https://github.com/yschimke/compose-preview-server/blob/main/docs/design/UI_BUILDER_SEED_TEMPLATES.md)
in that repository, under the
[catalog contract](https://github.com/yschimke/compose-preview-server/blob/main/docs/design/UI_BUILDER_CATALOG_CONTRACT.md)'s
phase 3a. **Nothing here carries a template yet**; this note records what arrives and, since the
answer is "one small document", what deliberately does not.

## What arrives

`blank` as `ui-builder/designs/blank.json`, named from [`ui-builder.policy.json`](../ui-builder.policy.json)
in a `templates` entry carrying its id, path, chooser label ("Blank screen") and supporting text ("A
Material scaffold with an empty content container"). The pipeline copies `ui-builder/designs/` to the
delivery branch beside `ui-builder.json`, the same way it copies the record. Its generated Kotlin is
four calls of `androidx.compose.material3` and compiles against `:catalog` as it stands, so the round
trip phase 2 asks of wear-m3-catalog — document → `ui-builder.json` → generated Kotlin → compiles
against the module's own classpath — is cheap here and worth having for the same reason: a template
is the one document nobody authored, so nothing catches a property this catalog does not declare
except a check that runs.

## What does not arrive, and why it matters that it is named

**`jetcaster` is not moving.** It is the other document the preview server will seed — the one a URL
naming no template gets — and it is easy to mistake for this catalog's second template. It is not:

- It is that repository's own Jetcaster reference design, the benchmark its builder is measured
  against, drawn to exercise a canvas rather than to start a design.
- It is **not exportable**, and not for a reason this catalog can fix. It validates against
  `m3-catalog` cleanly and then refuses to generate Kotlin: an adaptive `SupportingPaneScaffold`
  layout mode with no parameter to be written to, a carousel whose `items` is a `CarouselScope` DSL,
  grid spans belonging to the wrapper around a node, `selected` properties comparing a state
  variable. No component record fixes any of those — they are values the export vocabulary has no
  Kotlin for.
- It is not on the chooser. `blank` is the only card `m3-catalog` offers.

A template this repository carried and could not compile would be a template it could not test,
which is the whole argument for carrying them. So it stays where it is.

## The builtins question is the real blocker, and it is not this note's

A design needs a screen root to be put into, and this catalog declares **zero** builtins on purpose —
`layout/*`, `shape/*` and `asset/image` are the builder's vocabulary, published by
`:foundation-catalog` as `compose-foundation` (see [`docs/design/FOUNDATION_CATALOG.md`](design/FOUNDATION_CATALOG.md)).
`blank` is a `layout/scaffold` holding a `layout/box`, so both of its nodes are borrowed ones. Until
the published `m3-catalog` catalog composes with that shelf, a `blank` served from this repository has
no scaffold to be — which is why the preview server's lever is off for `m3-catalog` and why the
template move is sequenced behind it rather than in front of it.
