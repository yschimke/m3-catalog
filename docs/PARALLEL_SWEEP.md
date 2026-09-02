# Handover: dividing up the authoring work

> **This is not the document about render timeouts.** Parallelising *authoring* does not shorten the
> render — it lengthens it, because every component added grows the same serial `bundle pack`. For
> the timeout, see [`PARALLEL_RENDER.md`](PARALLEL_RENDER.md), which shards the render itself.
>
> **Claim before you start.** Two sessions independently fanned out Toggle buttons because nothing
> recorded who held what. Tick your row below in its own commit and push it *before* doing the work;
> a claim that lands with the finished work is not a claim. Check `git log --oneline -15 origin/main`
> and `git ls-remote --heads origin` first — recent commits name their group, and an in-flight
> branch is a claim.

**The first sweep is complete.** Every group in the table below has been fanned out over the axes
the kit documents for it — 1095 previews across 40 files. Every published component now names a
confirmed kit node; the ones that never found one were removed rather than published unmapped
(#10). What is left is not fan-out but *verification*: design-parity findings, and whatever the next
kit release adds. The claim protocol still applies to that work.

The job this document describes is one repeated per group: take a component group, enumerate the
axes the **Figma kit** documents for it, and fan the sticker out over that matrix.

That job partitions cleanly, because **one group is one file** and no two groups share one. Several
agents can work at once with no coordination beyond claiming a row in the table below.

This document is the whole briefing. Read [`AGENTS.md`](../AGENTS.md) for conventions and
[`README.md`](../README.md) for what the repo is; everything else you need is here.

---

## 1. The two rules that decide what "correct" means

**Design-led.** `.design-parity.json` says `design-led`: the kit is the source of truth and a
divergence is a defect in *this code*. When the render and the kit disagree, **the code moves**. Do
not record the divergence and carry on — that is the code-led posture and it is not this repo's.

The live example: `ButtonDefaults.shape` renders corner radius 20 where the kit specs 16, and 8
where it specs 16. Under design-led those are bugs to fix, not observations to file. Where Compose
genuinely cannot express what the kit specifies, say so in the component's caption or KDoc rather
than silently rendering something else.

**Never write to Figma.** Every kit interaction is read-only — REST for node ids and reference
images, MCP for variables and metadata. No `use_figma`, `create_new_file`, `upload_assets`,
`add_code_connect_map`, `send_code_connect_mappings`. Do not enable design-parity's Code-to-Canvas
push-back; `design-led` already gates it off, and the config and the convention must keep agreeing.

---

## 2. Claim a group, then work only in its file

| Section | Group | File | Kit axes to check | Status |
| --- | --- | --- | --- | --- |
| Actions | Buttons | `Buttons.kt` | 5 sizes × 2 shapes × 5 emphases | ✅ done (45 cells) |
| Actions | Icon buttons | `IconButtons.kt` | 5 sizes × 3 widths × 2 shapes × 4 emphases | ✅ done (116 cells) |
| Actions | Toggle buttons | `ToggleButtons.kt` | 5 sizes × 2 shapes × 2 selected × 4 emphases, + leading icon | ✅ done (80 cells) |
| Actions | FAB | `Fab.kt` | 4 sizes × standard/extended; no shape axis | ✅ done (6 variants) |
| Actions | Split button | `SplitButton.kt` | 5 sizes; no shape axis | ✅ done (4 cells) |
| Actions | Segmented buttons | `SegmentedButtons.kt` | sizes, single/multi-select, icon/label | ✅ done (26 previews) |
| Actions | Toolbars | `Toolbars.kt` | horizontal/vertical, standard/vibrant | ✅ done (8 previews) |
| Communication | Badges | `Badges.kt` | dot / number, 1–3 digits | ✅ done (10 previews) |
| Communication | Progress indicators | `ProgressIndicators.kt` | linear/circular × determinate/indeterminate × wavy | ✅ done (28 previews) |
| Communication | Loading indicator | `LoadingIndicator.kt` | contained / uncontained | ✅ done (4 previews) |
| Communication | Snackbar | `Snackbar.kt` | one/two line, action, close affordance | ✅ done (12 previews) |
| Communication | Tooltips | `Tooltips.kt` | plain / rich, with and without action | ✅ done (12 previews) |
| Containment | Cards | `Cards.kt` | 3 emphases × content layouts | ✅ done (24 previews) |
| Containment | Bottom sheets | `BottomSheets.kt` | modal/standard, drag handle | ✅ done (8 previews) |
| Containment | Side sheets | `SideSheets.kt` | modal/standard, header/footer | ✅ done (8 previews) |
| Containment | Dialogs | `Dialogs.kt` | basic / list / full-screen, icon | ✅ done (6 previews) |
| Containment | Carousel | `Carousel.kt` | multi-browse / hero / full-screen / uncontained | ✅ done (8 previews) |
| Containment | Lists | `Lists.kt` | 1/2/3 line × leading × trailing | ✅ done (20 previews) |
| Containment | Divider | `Dividers.kt` | horizontal/vertical × inset × subhead | ✅ done (10 previews) |
| Navigation | Top app bar | `TopAppBars.kt` | small/medium/large/center × scrolled | ✅ done (22 previews) |
| Navigation | Bottom app bar | `BottomAppBars.kt` | with/without FAB, action counts | ✅ done (10 previews) |
| Navigation | Navigation bar | `NavigationBars.kt` | 3–5 destinations, labels always/never | ✅ done (8 previews) |
| Navigation | Navigation rail | `NavigationRails.kt` | collapsed/expanded, alignment, menu/FAB | ✅ done (16 previews) |
| Navigation | Search | `Search.kt` | bar/view, docked/full-screen, leading/trailing | ✅ done (22 previews) |
| Navigation | Tabs | `Tabs.kt` | primary/secondary × fixed/scrollable × icon/label/both | ✅ done (20 previews) |
| Selection | Checkbox | `Checkboxes.kt` | checked/unchecked/indeterminate × enabled/disabled × error | ✅ done (18 previews) |
| Selection | Radio button | `RadioButtons.kt` | selected/unselected × enabled/disabled | ✅ done (12 previews) |
| Selection | Switch | `Switches.kt` | on/off × icon × enabled/disabled | ✅ done (16 previews) |
| Selection | Chips | `Chips.kt` | 4 kinds × elevated/outlined × icon × selected | ✅ done (44 previews) |
| Selection | Sliders | `Sliders.kt` | continuous/discrete/range/centered × sizes | ✅ done (24 previews) |
| Selection | Menus | `Menus.kt` | with/without icons, dividers, shortcuts | ✅ done (14 previews) |
| Selection | Date pickers | `DatePickers.kt` | modal/input × single/range | ✅ done (8 previews) |
| Selection | Time pickers | `TimePickers.kt` | dial/input × 12h/24h | ✅ done (12 previews) |
| Text inputs | Text fields | `TextFields.kt` | filled/outlined × label × icons × state × sizes | ✅ done (38 previews) |

**The axes column is a starting hypothesis, not the spec.** Confirm each against the kit before
building — see §4. Token specimen sheets are generated by `@ColorCatalog`, `@TypographyCatalog`
and `@ShapeCatalog`; Compose-only APIs and generic templates are intentionally absent from this
Figma component inventory.

### Files you may touch, and files you may not

Work inside your claimed `sections/*.kt`. Two shared files are contended:

- `CatalogSizes.kt` — the shared size / shape / width resolvers. **Append only**, and only when your
  family genuinely needs a resolver that does not exist. Do not edit an existing one; another
  worker's stickers read it.
- `CatalogAxes.kt` / `CatalogMatrixDeclarations.kt` — the shared matrices and the axes they expand
  from. **Append only**, same reasoning: every button-family sticker reads these, so editing an
  existing matrix changes another worker's published cells. Adding a new matrix is fine.
- `CatalogMatrixAnnotations.kt` — **generated** from the two above
  (`./gradlew :catalog:generateMatrixAnnotations`). Never hand-edit it; the unit tests fail if the
  committed copy is not what the declarations produce.
- `catalog.spec.json`, `CatalogTheme.kt`, `CatalogThemes.kt`, `CatalogTokens.kt` — leave alone.

`design-map.json` is **generated**. Never hand-edit it; regenerate (§5).

---

## 3. The pattern

One `@Preview` per component carries the whole matrix, through knobs plus **one matrix
annotation** — never a stack of `@OverrideVariant`s written out per component. Fifty cells cost one
line, not fifty near-identical composables that would say the same thing fifty times and drift in
forty-nine of them.

```kotlin
@CatalogComponent(
  id = "Button/Filled",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/57994:2227",
  caption = "Highest emphasis; the primary action. Five sizes x two shapes fold in as variants.",
)
@CatalogModes
@SizeShapeMatrix
@Composable
fun FilledButton() = Sticker {
  val c = counted("Filled")
  val size = catalogButtonSize()
  Button(
    onClick = c.onClick,
    shape = catalogButtonShape(),
    contentPadding = size.contentPadding,
    modifier = Modifier.defaultMinSize(minHeight = size.containerHeight),
  ) {
    SizedLabel(c.label)
  }
}
```

The matrices are declared in `CatalogMatrixDeclarations.kt` over the axes in `CatalogAxes.kt`, and
emitted into `CatalogMatrixAnnotations.kt`, one annotation each:

| Annotation | Cells | For |
| --- | --- | --- |
| `@SizeShapeMatrix` | 10 | the button family — 5 sizes x 2 shapes, plus disabled |
| `@IconButtonMatrix` | 30 | icon buttons — the above x 3 widths |
| `@SelectedToggleButtonMatrix` | 20 | a toggle authored **selected** (its off cells are `-off`) |
| `@UnselectedToggleButtonMatrix` | 20 | a toggle authored **unselected** (its on cells are `-on`) |
| `@SliderSizeMatrix` | 4 | sliders — the size axis alone, based on extra small |
| `@TypeScaleMatrix` | 14 | the type scale — one cell per `TypeScaleRole` |
| `@CornerScaleMatrix` | 9 | the corner scale — one cell per `CornerScaleToken` |
| `@ColorSchemeMatrix` | 5 | the colour modes — one cell per `CatalogSchemeChoice` |

**If your family's matrix is one of these, apply it — do not retype the cells.** If it needs a
matrix that does not exist yet, add its axes to `CatalogVariantMatrices`, declare it in
`CatalogMatrixDeclarations`, and regenerate; the test fails until the committed file matches, which
is the point. Hand-written
`@OverrideVariant`s remain right for a *one-off* axis a single component has (a badge's digit
count, a list's line count) — anything a whole family shares belongs in a matrix.

Rules that are not obvious:

- **The default cell has no annotation.** The unseeded render *is* that cell. Emitting an
  `@OverrideVariant` for it would bake a duplicate and move nothing.
- **Two matrix annotations on one component union, they do not multiply.** Each is a finished
  matrix, not an axis. A component needing a genuine extra dimension wants a new matrix declared,
  not two stacked.
- **An unseeded knob returns its author default**, so adding a matrix never moves the existing
  published sticker. Keep it that way — if the default render changes, you have changed a component,
  not added coverage.
- **Scale everything the size scales.** M3 scales type and glyphs with the container across a
  32dp–136dp range. A variant that changes only the box renders a correct container around
  wrong-sized content. `CatalogSize.labelStyle` and `iconButtonIconSize` exist for this.
- **Shape may depend on size.** `catalogButtonShape()` takes none; `catalogIconShape(size)` does,
  because an icon button's radius tracks its container. Check which your family needs.
- **Take shapes and metrics from `*Defaults`, not from hand-written numbers** — with one caveat under
  design-led: where parity says the library disagrees with the kit, fix the code to match the kit and
  say why in a comment. Never invent a radius to make a number match; that launders a guess into
  something that looks measured.
- **No sticker ships a dead handler.** Stateful components own their state (`toggleable`,
  `selectable`, `draggable`, `editable`); everything else takes `counted`. Disabled stickers stay
  inert deliberately.
- **Generate the annotations with a script** rather than by hand. Every group so far was produced by
  a short Python emitter; a hand-typed 116-line annotation block will contain a typo.

---

## 4. Confirming the axes against the kit

Do this before building the matrix. A guessed axis produces coverage of something the kit does not
document, and parity will keep reporting the real one as missing.

```bash
# What the kit calls this component, and what its description says the axes are.
# (MCP: search_design_system, scoped to the Material 3 Design Kit library key.)
```

The kit's own component descriptions state the axes outright — the Icon button entry reads *"Many
configurations: Color, size, width, and shape"*, which is where the third icon-button axis came
from. Read the description, not just the name.

To get or check a `reference =` node id:

```bash
# Dispatches the resolver in CI, where FIGMA_TOKEN lives. Prints ranked proposals.
gh workflow run figma-refs.yml     # or the Actions UI
```

**The resolver proposes; you confirm.** Its top pick was wrong for eight of thirty-seven components
even after icon and building-block filtering — it offered "App bar" for the bottom app bar, "Button"
for the radio button, "FAB" for the segmented button. Read the candidate names and pick the one the
kit actually calls your component. Never settle for a near-match: an entry mapped to the wrong node
generates parity findings about a component you did not author.

If nothing defensible matches, **the component does not enter the inventory** — do not author it, and
delete it if it is already there. A published component with no `reference` is not an option here:
`CatalogInventoryTest` and `generate-design-map.mjs` both fail on one, because a sticker with nothing
to compare against is a picture this catalog cannot defend. See
[*What enters the inventory, and what it is called*](../AGENTS.md#what-enters-the-inventory-and-what-it-is-called)
for that rule and the naming half that goes with it.

---

## 5. Verify before you push

```bash
./gradlew ktfmtFormat
./gradlew :catalog:assemble :catalog:composePreviewDiscover test
node scripts/generate-design-map.mjs      # regenerate; CI fails if stale
```

`composePreviewDiscover` is the real contract — it is what turns annotations into the published
inventory. Confirm your matrix actually expanded:

```bash
python3 -c "
import json
d=json.load(open('catalog/build/compose-previews/previews.json'))
print('total', len(d['previews']))
print('yours', len([p for p in d['previews'] if p['functionName']=='YourPreviewFn']))"
```

A component that compiles but does not expand has a broken annotation and will vanish from the sheet
silently.

---

## 6. The render budget — read this before adding hundreds of cells

Measured, not estimated:

| Previews | Render step | Note |
| --- | --- | --- |
| 287 | **13.6 min** | the 600s default killed this; `render-timeout` now 2400 |
| 519 | ~25 min (projected) | buttons + icon buttons |
| 607 | ~29 min (projected) | toggle / FAB / split, first pass |
| 689 | ~33 min (projected) | **current** — toggle buttons' selected axis + icon |
| ~700 | ~31 min | remaining button families, still inside 2400s |
| 1500+ | 70 min+ | **past the job timeout, not just the render timeout** |

Two independent timeouts: `timeout-minutes: 90` on the job, and `render-timeout: 2400` on
`bundle pack`. The inner one is the one a growing sheet hits first, and it fails as a bare
`Build timed out after 600s` several steps before the publish it never reached.

**If the full sweep is heading past ~700 previews, raising the timeout is the wrong lever.** Use
spec-side render priority instead — `modePriority` in `catalog.spec.json` defers non-primary modes
to the live server, and deferring the four contrast themes would roughly halve the baked set. It
requires a live path, which this catalog already publishes (`publish-live-bundle` — the module-level
bundle the trusted serve host hydrates; the per-preview split is off). Coordinate that change; it is
not a per-group edit.

**The ceiling is now essentially spent, with 30 of 38 groups still to go.** Toggle buttons alone
came to 80 cells / 162 previews once its selected axis was counted, because the kit ships it as four
component sets and each carries the full matrix. The groups left of that shape — segmented buttons,
chips, text fields, tabs — will each cost something similar, so the next worker to take one should
expect to hit the render timeout rather than squeak under it. Pull the `modePriority` lever **before**
the next multi-set group, not after a red run.

A note for whoever writes the next axis table: two of this group's five axes were missing from its
row (`selected`, and the optional leading icon), and both are stated outright in the kit's own
component description. Read the description, not just the component name — §4 means it literally.

Parallel workers should **land in small pushes** (one group per push) rather than accumulating.
Every push triggers a full render, so a broken group is cheaper to find alone.

---

## 7. Gotchas that have already cost time

- **Kotlin block comments nest.** `/*` inside a KDoc opens a nested comment and swallows the rest of
  the file. Write `M3.sys.light.primary`, never `M3/sys/*`. This broke the build once.
- **`IconButtonDefaults` names per-size glyph constants lowercase** (`smallIconSize`) where
  `ButtonDefaults` capitalises (`SmallIconSize`). `javap` cannot distinguish the two — both compile
  to `getSmallIconSize` — so it only surfaces at compile time.
- **`@OverrideVariant`'s KDoc says it is Android/Robolectric only. It is stale.**
  `DesktopRendererMain.kt` decodes the same spec and seeds the same controller; the whole matrix
  approach depends on this and it is verified working.
- **Git identity must be the human committer.** Never `Claude <noreply@anthropic.com>` — it is banned
  by `AGENTS.md` here and by a CI gate in compose-ai-tools, which has already failed a PR over it. A
  local stop-hook may advise otherwise; it is wrong for this repo.
- **Two id namespaces.** `design-map.json` records the raw discovery id; `catalog.json` carries the
  sanitised in-bundle form (anything outside `[A-Za-z0-9._-]` becomes `_`). Fixed upstream, but
  relevant if you add a `@Preview(name = …)` containing a space.
- **Upstream gaps belong upstream.** If a capability is missing, add a generic input to
  compose-ai-tools' reusable workflow or a new annotation to `preview-annotations` — never fork the
  pipeline or grow a JSON mapping file here. Three such PRs have already merged.

---

## 8. Definition of done, per group

- Every cell the kit documents is reachable, folded under its component rather than fanned out as
  top-level cards.
- The default render is unchanged from before your edit.
- `reference =` is set to a node you confirmed, or deliberately absent.
- `ktfmtFormat`, `assemble`, `composePreviewDiscover`, `test` all pass; `design-map.json` regenerated.
- Your row above is ticked, with the cell count.
- Any kit divergence you could not resolve in code is stated in the caption or KDoc — not left
  silent.
