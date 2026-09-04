# Parameter knobs in this catalog

Compose-ai-tools carries two ways to declare a preview's editable knobs, and this catalog now uses
both. This file records which of its knobs have moved to the newer format, which cannot, and the one
release-shaped reason the largest group is still waiting. It is written so the question does not
have to be re-investigated from the source the next time it comes up — the upstream half of the
story is
[`compose-ai-tools/docs/design/PARAMETER_KNOB_MIGRATION.md`](https://github.com/yschimke/compose-ai-tools/blob/main/docs/design/PARAMETER_KNOB_MIGRATION.md),
and this file does not restate it.

The two formats, in one line each:

| | `previewOverride*` (named overrides) | Parameter knobs |
| --- | --- | --- |
| Where the knob is declared | by **executing a lookup in the composable body** | by the **function signature** |
| How a value is seeded | the harness writes it into a process-static controller | ordinary argument passing |
| What the sticker's body contains | a harness call per knob | nothing — no harness dependency at all |

## What has moved

Eight knobs, across four stickers. All of them share one property that made them safe to move
today: **no `@OverrideVariant`, no generated matrix cell and no exhaustive kit cell seeds any of
them**, so no baked render depends on a seed reaching them.

| Sticker | Knobs | Shape |
| --- | --- | --- |
| `sections/SideSheets.kt` → `StandardSideSheet` | `header`, `back`, `content`, `footer` | all four read directly in the preview's own body |
| `sections/BottomSheets.kt` → `ModalBottomSheetSticker` | `handle`, `header`, `content` | read in `SheetBody`, which has exactly one caller, so they thread through as arguments |
| `adaptive/AdaptiveLayouts.kt` → `ListDetailPaneScaffoldSticker`, `SupportingPaneScaffoldSticker` | `twoPanesOnMedium` | read in `paneDirective`, called by both scaffolds; each declares it on its own signature |

The adaptive pair is the half of the pilot that renders: 12 captures across the
`@CatalogBreakpoints` multipreview, **byte-identical before and after** (`composePreviewRender
--preview=PaneScaffoldSticker`, md5 of all twelve PNGs unchanged). The two sheets are replicas that
the inventory deliberately does not publish — `StandardSideSheet` because Compose Material 3 has no
side-sheet API, `ModalBottomSheetSticker` because popup surfaces cannot be captured yet
(compose-ai-tools#3916) — so neither is a discovered preview and neither renders at all.

## What is blocked, and on what

Most of the catalog's remaining scalar knobs would move cleanly on the format's own terms — a
literal default, a seedable kind, read inside the sticker. They are held by the render lane instead.

**This catalog renders on the desktop backend** (`desktop-render: true` in
[`design-artifacts.yml`](../.github/workflows/design-artifacts.yml); the module is CMP desktop, not
Android). The desktop bake's binding of an `@OverrideVariant` seed onto a parameter knob's argument
list is compose-ai-tools#5103, which landed upstream **after** v1.73.0 and is not in a release yet.
Until it is, a seed naming a parameter knob is silently dropped and every variant renders the
default.

That is not a subtle failure, and it reproduces in one command. Migrating `LinearProgress`'s
`progress` knob to a `progress: Float = 0.5f` parameter and running
`./gradlew :catalog:composePreviewRender --preview=LinearProgress` on compose-ai-tools 1.73.0:

| Capture | Unmigrated | Migrated |
| --- | --- | --- |
| `LinearProgress_Light` | `77b940e5…` | `77b940e5…` |
| `…_VARIANT_empty` | `e511728e…` | `77b940e5…` |
| `…_VARIANT_quarter` | `19b80f19…` | `77b940e5…` |
| `…_VARIANT_full` | `b8cd98dd…` | `77b940e5…` |

Four distinct progress values collapse into one picture. `scripts/duplicate-renders.mjs` would fail
the build on exactly that collision, which is the gate doing its job rather than a reason to declare
the collision — the fix is upstream, not here.

**Nothing needs writing upstream either: #5103 already does it, it is only unreleased.** Verified
against compose-ai-tools at HEAD by putting an `@OverrideVariant(name = "seven-rows", ints =
["itemCount=7"])` on its own parameter-knob sample (`samples/cmp/.../ParameterKnobPreviews.kt`) and
baking it on the same desktop lane. The variant PNG differs from the default, and its
`.overrides.json` sidecar reads `"key":"itemCount"` with `default` 3 and `current` 7 — the seed
reaching the argument list, and the declaration reaching the sidecar `compose-preview serve` reads.
The chain is `@OverrideVariant` → the plugin's `composeai.preview.knobs` system property (and worker
protocol v2 frame) → `PreviewKnobBake.seedArgs` in the desktop renderer, and all of it is on
compose-ai-tools `main`.

So the next step for the rest of this migration is a compose-ai-tools release carrying #5103, which
Renovate then lands here on its own. No code in this repository is waiting on anything else.

So the rule for the next pass: **a knob that any `@OverrideVariant`, matrix cell or exhaustive kit
cell seeds stays on `previewOverride*` until the desktop lane ships #5103.** Grep for `"<key>=` under
`catalog/src/main` before moving one.

## What cannot move at all

These are structural, not release-shaped, and are upstream gaps rather than defects here:

| Knob family | Why |
| --- | --- |
| `catalogText(...)` everywhere | its defaults are `stringResource(...)` expressions, which cannot be recovered and so cannot be declared; many are indexed per row, which a fixed-arity parameter list cannot express; and it is one helper serving dozens of stickers |
| `catalogChoice(...)` / the axes in `CatalogAxes.kt` | a `String` parameter carries no closed value set, so the viewer would draw a text field where the axis wants a picker |
| `CatalogFocus.kt`'s `focusOverlayColour`, `focusRingOuterColour`, `focusRingInnerColour` | `Color` is not a seedable parameter kind, and they are read in a wrapper on behalf of every sticker |
| `Dividers.kt`'s `inset`, `Carousel.kt`'s `preferredItemWidth` | `Dp` is not a seedable parameter kind |
| `CatalogSizes.kt`'s `catalogToggleSelected`, `CatalogInteractive.kt`'s `clickCount` | declared in a shared helper or wrapper, with the default supplied by the caller |

`CatalogOverrideSurfaceTest` keeps one live call of each of the six `previewOverride*` kinds in the
sources, so the control contract the preview server reads stays exercised whatever else migrates.
That test is a floor, not a ceiling — but a migration that empties a kind out of the catalog has to
answer it deliberately rather than by deleting the assertion.

## When to reach for which

* **Parameter knobs** when the sticker is self-contained and the editable values are its own
  arguments — the shape it would have if real code called it — and no baked variant seeds them.
* **`previewOverride*`** when the knob is a colour or a dimension, when each row of a repeated
  component needs its own value, when the value set is closed and a picker is the right control, or
  when one wrapper declares the knob for many stickers.

Neither replaces the other, which is why the catalog uses both.
