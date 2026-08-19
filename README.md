# Material 3 Design Kit — as code

The [Material 3 Design Kit][kit] rebuilt as **Jetpack Compose `@Preview`s**, published as an
importable design catalog.

Every published component has an exact, renderable node in that kit. Compose-only APIs, generic
screen templates and hand-authored token specimens do not enter the component inventory; colors,
typography and shapes are published separately through their dedicated token-catalog annotations.
Membership is the kit's call that way; **naming is Compose's** — ids follow the Compose API surface
(`NavigationBar/Short` for `ShortNavigationBar`), and a kit word is never borrowed for something the
kit uses it for differently. [`AGENTS.md`](AGENTS.md) states both halves.

**The Figma kit is the source of truth.** A divergence between the two is a bug in this code, and
the code is what changes — that is what `direction: "design-led"` in
[`.design-parity.json`](.design-parity.json) says.

That is the opposite of the component-system catalogs in compose-ai-tools, and deliberately so:
those publish a system whose own render is authoritative, whereas this one exists to *reproduce* a
published kit. The direction also has teeth beyond reporting — design-parity's Code-to-Canvas
push-back is gated on `code-led`, so `design-led` makes writing back to the Figma file structurally
impossible rather than merely forbidden by convention.

**Nothing in this repo writes to Figma.** Every Figma interaction is read-only: the REST API for
node ids and reference images, and the MCP server for variables and metadata. No `use_figma`, no
`create_new_file`, no `upload_assets`, no Code Connect publishing.

- **Browse it:** the published catalog is served at `preview.coo.ee/m3-catalog/`.
- **Import it:** the generated bundle lives on the `design-artifacts/m3-catalog` branch —
  `catalog.json` (the inventory), raster `images/`, editable layered `figma/*.svg` vectors,
  schematic `wireframes/`, `code-connect.json`, and a browsable `index.html`. Regenerated from the
  code on every change, and appended as a commit rather than force-pushed, so the branch is
  diffable over time.

The delivery branch's history is intentional: do not rewrite it into a fresh root commit as a
repository-size workaround. A normal `git clone` fetches that generated branch as well as `main`,
including its large live-render bundles. Contributors who only need the source should use
`git clone --single-branch https://github.com/yschimke/m3-catalog.git`; consumers that need the
published catalog should fetch `design-artifacts/m3-catalog` deliberately.

[kit]: https://www.figma.com/design/ocdacdEsnHipMJD3egzxKb/Material-3-Design-Kit--Community-

## Annotation-first, by design

The catalog's whole inventory — sections, groups, component ids, captions, variants and themes —
lives in **annotations next to the composables**. There is no hand-maintained JSON mapping
components to previews, and that is deliberate: a name-keyed mapping file drifts the moment a
preview is renamed, and it fails silently (the render succeeds; the sticker just never appears).

```kotlin
@file:CatalogGroup(name = "Buttons", section = "Actions")

@CatalogComponent(id = "Button/Filled", caption = "Highest emphasis; the primary action.")
@CatalogModes
@Composable
fun FilledButton() = Sticker {
  val c = counted("Filled")
  Button(onClick = c.onClick) { Text(c.label) }
}

@CatalogVariant(of = "Button/Filled", state = "disabled")
@CatalogModes
@Composable
fun FilledButtonDisabled() = Sticker { Button(onClick = {}, enabled = false) { Text("Filled") } }
```

[`catalog.spec.json`](catalog.spec.json) carries only cover-sheet fields the code has no opinion
about: the system slug, title, primary modes, documented breakpoints and the front-door hero.

Figma has two kinds of variation. Variant axes produce sibling component nodes and map directly.
Boolean, text, instance-swap and slot properties do not: a definition node always renders at their
defaults. The checked-in kit index therefore also records visible instances already configured on
the kit's example pages. Property-shaped catalog variants use an exact matching instance when one
exists and stay explicitly unpaired when it does not; the resolver never substitutes a definition
whose defaults depict different content.

| Annotation | Declares |
| --- | --- |
| `@file:CatalogGroup(name, section)` | the file's group, and the tab it sits under |
| `@CatalogComponent(id, caption)` | a primary sticker |
| `@CatalogVariant(of, state, props)` | a render folded *under* its parent sticker |
| `@ThemeCatalog` | a named theme, offered in the viewer's theme select |
| `@ColorCatalog` / `@TypographyCatalog` / `@ShapeCatalog` | a whole token object, given a specimen sheet |
| `@SizeShapeMatrix` / `@IconButtonMatrix` / `@…ToggleButtonMatrix` | a whole variant matrix, in one line |

The matrices are the one annotation the catalog declares itself, in
[`CatalogMatrixAnnotations.kt`](catalog/src/main/kotlin/ee/schimke/m3catalog/CatalogMatrixAnnotations.kt).
A family's components all render the same cross product — five sizes by two shapes, by three widths
for the icon buttons — so the cells are declared once and applied per component rather than retyped:
250 `@OverrideVariant`s across thirteen blocks became 80 authored once. `CatalogVariantMatrixTest`
holds each matrix to the axes in `CatalogAxes.kt`, so adding a size to `CatalogSize` fails the build
until every matrix carries its new cells.

Variants fold behind defaults rather than fanning out as top-level cards: `Button/Filled` is one
card carrying its disabled and icon+label renders, not three components. That is what keeps a
system this size browsable.

## Sections

One file per component group, under `catalog/src/main/kotlin/ee/schimke/m3catalog/sections/`,
mirroring the kit's own information architecture.

| Section | Groups |
| --- | --- |
| Actions | Buttons, FAB, Icon buttons, Segmented buttons, Split button, Toggle buttons, Toolbars |
| Communication | Badges, Loading indicator, Progress indicators, Snackbar, Tooltips |
| Containment | Bottom sheets, Cards, Carousel, Dialogs, Divider, Lists, Side sheets |
| Navigation | Top app bar, Bottom app bar, Navigation bar, Navigation rail, Search, Tabs |
| Selection | Checkbox, Chips, Date pickers, Menus, Radio button, Sliders, Switch, Time pickers |
| Text inputs | Text fields |
| Styles | Color, Typography, Shape, Elevation |

## Shapes: Material's own by default, adjustable when you ask

The 35 shape specimens draw `MaterialShapes.Circle`, `MaterialShapes.Heart` and the rest —
the library's finished `RoundedPolygon`s, so the published render is what any consumer of Material
gets and not this repo's arithmetic. A finished polygon has no seam to push on, though: the sheet
tells you what a `Heart` looks like and nothing about what makes it one.

So each shape is also written out. `MaterialShapeRecipes` transcribes Material's own private builder
for every entry — the vertex tables, the corner radii, the star's inner radius, the `customPolygon`
repeat helpers — and exposes four knobs over them: `rounding`, `smoothing`, `innerRadius` and
`count`, each a **multiplier over what Material authored** rather than a raw value. A sticker draws
the stock entry while the knobs sit at their defaults and switches to the inlined construction the
moment one moves, so nothing is inlined into the published sheet by accident. `Shape/Sunny` bakes
two cells (`rounding=0`, `count=12`) to hold that path in the sheet; every other shape offers the
same knobs live on the preview server.

Transcribed code drifts, which is the real cost of this. `MaterialShapeRecipeTest` closes it by
rebuilding all 35 shapes at the default knobs and comparing them to `MaterialShapes` **cubic for
cubic** — so a Material release that re-authors a shape fails the build instead of leaving a knob
that reshapes something the library no longer draws.

## Two lanes: what a click does

Every sticker is rendered on two surfaces that want opposite things from a pointer.

| Lane | Signal | A click must |
| --- | --- | --- |
| Baked snapshot / the published sheet | `LocalInspectionMode = true` | do **nothing** — a published PNG can't depend on whether something tapped it |
| Held Live Compose session on the preview server | `LocalInspectionMode = false` | visibly change the component |

The split is one derived flag (`catalogInteractive()`), never a hard-coded constant, so a single
sticker body serves both. **No sticker ships a dead handler**: stateful components own their state
(`toggleable` / `selectable` / `draggable` / `editable`), and everything else takes `counted` — the
label it was given, and a handler that is real on the live lane and a no-op on the baked one. What
a live click *shows* is the component's own press feedback: the ripple, the state layer, the
pressed shape. `counted` used to append `(n)` to the label so a sticker could be seen to respond;
that is `clickCount` now, a knob every sticker exposes and nothing turns on by default, because a
growing label is not what the component does when you press it — and reading it as proof hid the
fact that the ripple was missing on the live lane at all
([wear-m3-catalog#32](https://github.com/yschimke/wear-m3-catalog/issues/32)). The deliberate
exceptions are disabled stickers, which stay inert because unresponsiveness is the state they
document.

## Themes

The catalog declares eight `@ThemeCatalog` wrapper providers: the kit's six colour modes — light
and dark, each at standard, medium and high contrast — plus Material 3 Expressive light and dark.
The renderer builds a specimen sheet per theme and the viewer offers them in its theme select. Any
sticker can be re-rendered under any of them.

| Mode | Source |
| --- | --- |
| Baseline Light / Baseline Dark | Compose's stock `lightColorScheme()` / `darkColorScheme()` |
| Expressive Light / Expressive Dark | `expressiveLightColorScheme()` / `darkColorScheme()` |
| Light + Dark × Medium / High Contrast | generated from the baseline seed via MaterialKolor |

Expressive themes install `MaterialExpressiveTheme` and `MotionScheme.expressive()` rather than
the standard `MaterialTheme` / `MotionScheme.standard()` pair. Component families with expressive
shape overloads use their whole stateful shape sets only under those themes: buttons and icon
buttons morph from their resting shape to the size-specific pressed shape in a live session, while
standard themes keep the single static shape. Expressive-only components remain in the inventory
under every theme because they are components in the kit, not substitutes for a standard sticker.

The standard pair is expressed as the stock schemes rather than re-typed hex, because those defaults
*are* the Material 3 baseline — verified against the kit's own published `M3.sys.light.*` variables
(`primary` `#6750A4`, `on-surface` `#1D1B20`, `outline-variant` `#CAC4D0`, …) and pinned by a test.
Nothing to drift.

Compose ships no contrast schemes, and a contrast level isn't a lookup table — it shifts every role
along its tonal palette by a continuous function of the level — so the four tiers are **computed**
by MaterialKolor, a multiplatform port of the `material-color-utilities` algorithm behind the
Material Theme Builder plugin that produced the kit's variables.

One caveat, documented rather than hidden: running that generator at *zero* contrast does not
exactly reproduce the published baseline (`primary` comes out `#65558F` vs `#6750A4`, and `error`
differs outright because M3's error family is hand-authored, not seed-derived). The published
baseline is a tuned artefact, not the raw output of its own seed. So the catalog keeps both — exact
stock schemes for the standard modes, generated schemes for the tiers Compose has no primitive for.
The tests pin the generator's output so a dependency bump can't silently re-tint four published
themes, and assert the property a tier actually promises: each one holds its content further off its
surface than the one below.

## Translations

Every language-bearing string a sticker renders comes from Compose Multiplatform string resources under
[`catalog/src/main/composeResources/`](catalog/src/main/composeResources) — English in `values/`
and **17 translations** beside it: `ar de es fr hi id it ja ko nl pl pt-rBR ru th tr zh-rCN
zh-rTW`. Nothing in a sticker body is a hard-coded label, so any render that carries a locale — a
`@Preview(locale = …)`, a `localeTag` on a render spec, or the preview server's locale control —
comes back translated rather than as English text in a differently-shaped layout.

The desktop renderer applies the tag twice, and both halves matter: the composition's `LocaleList`
(which flips layout direction, so `ar` mirrors) and the JVM default `Locale` (which is what CMP
`stringResource(...)` actually reads on Skiko). One override therefore moves the copy *and* the
layout together.

What stays a Kotlin literal, deliberately: design-system token names (`primary`, `Display Large`,
`XS`) — they are API identifiers, not copy — and sample data that isn't language
(`alice@example.com`, `⌘E`, person names). Numeric samples such as `10:30` and badge counts pass
through `localizedDigits(...)`, so Arabic and explicit Unicode numbering-system locales receive
their own digit shapes without turning sample data into translated prose. `CatalogTranslationsTest`
pins both directions: every locale carries exactly the keys `values/strings.xml` declares, no locale
silently repeats the English copy, no key is declared that no sticker renders, and direct visible
literals are limited to an explicit allowlist.

## Building

```sh
./gradlew :catalog:assemble                 # compile
./gradlew :catalog:composePreviewDiscover   # the annotation-derived inventory
./gradlew test                              # inventory invariants
./gradlew ktfmtFormat                       # format
```

Rendering previews to PNG needs the [`compose-preview`][cat] CLI:

```sh
compose-preview show --module :catalog \
  --with-extension a11y,theme,semantics,semantics-wireframe --json
```

[cat]: https://github.com/yschimke/compose-ai-tools

## CI

### Where the grouped view lives

Two long-lived branches carry rendered output, and they are **not** the same thing:

| Branch | Shape | For |
| --- | --- | --- |
| `compose-preview/main` | flat — one PNG per `@Preview` expansion, `renders/catalog/<Fn>_<Mode>.png` | the pixel **baseline** the visual-diff bot compares a PR against. Deliberately ungrouped: it is keyed by render id so a diff can be taken, not browsed |
| `design-artifacts/m3-catalog` | grouped — `catalog.json` folds every render under its component | the **catalog** you browse and import. `Button/Filled` is one entry carrying six images tagged `theme: light\|dark`, `state: default\|disabled`, `props: {content: icon+label}` |

So light/dark and states being flat on `compose-preview/main` is expected; the grouping is on
`design-artifacts/m3-catalog` and in the served viewer.

| Workflow | Does |
| --- | --- |
| [`ci.yml`](.github/workflows/ci.yml) | compile, run preview discovery, unit tests, `ktfmtCheck`, and the build-free catalog-spec pre-flight |
| [`compose-preview.yml`](.github/workflows/compose-preview.yml) | renders the previews and posts a before/after visual diff on every PR |
| [`design-artifacts.yml`](.github/workflows/design-artifacts.yml) | renders and publishes the importable bundle to `design-artifacts/m3-catalog` |
| [`design-parity.yml`](.github/workflows/design-parity.yml) | compares the render against the Figma kit and publishes the report to `design-parity/main` |

design-parity is wired but **inert** until two things exist, and it skips with a notice rather than
failing while either is missing:

1. a `FIGMA_TOKEN` repository secret — a read-only PAT with `file_content:read`;
2. at least one component carrying a kit handle on its annotation,
   `@CatalogComponent(reference = "figma:<fileKey>/<nodeId>")`.

Node ids aren't discoverable without API access (the Figma MCP server exposes only the page you're
looking at, and Code Connect needs a Dev/Full seat), so
[`@design-parity/baseline`](https://www.npmjs.com/package/@design-parity/baseline) resolves them
over the REST API and **proposes** a ref per catalogued component, ranked by name similarity. It
writes no mapping file: you paste the ref onto the annotation, and
[`scripts/design-map.sh`](scripts/design-map.sh) projects `design-map.json` out of the discovered
manifest from there — compose-ai-tools' `emit-design-map.mjs` reads the annotations, then
[`@design-parity/kit-index`](https://www.npmjs.com/package/@design-parity/kit-index) resolves each
variant knob against the committed `figma-kit-index.json`.

```sh
./gradlew :catalog:composePreviewDiscover
FIGMA_TOKEN=figd_... npx --yes -p @design-parity/baseline@0.1.51 design-parity-propose-refs \
  --file ocdacdEsnHipMJD3egzxKb \
  --previews catalog/build/compose-previews/previews.json
```

Page ids are undiscoverable for the same reason, one level up.
[`docs/FIGMA_PAGES.md`](docs/FIGMA_PAGES.md) indexes the kit's pages — which
`design-map.json` refs live on which page, and which page holds the composed
example screens (the only one that can back a design-parity page backdrop).
`design-parity-pages list` regenerates that table in one REST call.

The Upcoming-Mobile screen is published with the catalog and browsable at
[`preview.coo.ee/m3-catalog/pages`](https://preview.coo.ee/m3-catalog/pages):
every component instance on it is linked back to the sticker that implements it,
and the catalog's own renders can be laid over the design live.

```sh
FIGMA_TOKEN=figd_... npx --yes -p @design-parity/page-backdrop@0.1.51 design-parity-pages list \
  --file ocdacdEsnHipMJD3egzxKb --slug Material-3-Design-Kit--Community-
```

Both used to be scripts in this repo. They are upstream now, for the same reason
`design-map.json` is projected rather than hand-maintained: the logic is about **design kits**, not
about this catalog, and a copy here drifts from the one everyone else runs. What stays local is the
kit handle on each annotation — the only part that is genuinely this repo's.

Dependencies update themselves via Renovate ([`renovate.json`](.github/renovate.json)). The
compose-ai-tools CLI, Gradle plugin and annotation coordinates are grouped into one automerged PR,
because a skew between them breaks preview discovery outright.

## Contributing

The remaining work is the exhaustive variant sweep, and it partitions one-group-per-file so several
people (or agents) can run it at once. [`docs/PARALLEL_SWEEP.md`](docs/PARALLEL_SWEEP.md) is the
handover: claim a row, confirm the axes against the kit, fan the sticker out, verify, push.

## Licence

Apache 2.0 — see [LICENSE](LICENSE). The vendored Roboto / Roboto Flex / Noto Serif / Droid Sans
Mono faces under `catalog/src/main/resources/fonts/` carry their own OFL/Apache licences, included
alongside them.
