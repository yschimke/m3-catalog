# Material 3 Design Kit — as code

The [Material 3 Design Kit][kit] rebuilt as **Jetpack Compose `@Preview`s**, published as an
importable design catalog.

The Figma kit is the seed and the reference; **this render is the source of truth**. A divergence
between the two is read as a bug in the kit-to-code translation, not as a change the code has to
chase — that is what `direction: "code-led"` in [`.design-parity.json`](.design-parity.json) says.

- **Browse it:** the published catalog is served at `preview.coo.ee/m3-catalog/`.
- **Import it:** the generated bundle lives on the `design-artifacts/m3-catalog` branch —
  `catalog.json` (the inventory), raster `images/`, editable layered `figma/*.svg` vectors,
  schematic `wireframes/`, `code-connect.json`, and a browsable `index.html`. Regenerated from the
  code on every change, and appended as a commit rather than force-pushed, so the branch is
  diffable over time.

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

| Annotation | Declares |
| --- | --- |
| `@file:CatalogGroup(name, section)` | the file's group, and the tab it sits under |
| `@CatalogComponent(id, caption)` | a primary sticker |
| `@CatalogVariant(of, state, props)` | a render folded *under* its parent sticker |
| `@ThemeCatalog` | a named theme, offered in the viewer's theme select |
| `@ColorCatalog` / `@TypographyCatalog` / `@ShapeCatalog` | a whole token object, given a specimen sheet |

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
| Navigation | Top app bar, Bottom app bar, Navigation bar, Navigation rail, Navigation drawer, Search, Tabs |
| Selection | Checkbox, Chips, Date pickers, Menus, Radio button, Sliders, Switch, Time pickers |
| Text inputs | Text fields |
| Styles | Color, Typography, Shape, Elevation |
| Templates | Scaffold templates |

## Two lanes: what a click does

Every sticker is rendered on two surfaces that want opposite things from a pointer.

| Lane | Signal | A click must |
| --- | --- | --- |
| Baked snapshot / the published sheet | `LocalInspectionMode = true` | do **nothing** — a published PNG can't depend on whether something tapped it |
| Held Live Compose session on the preview server | `LocalInspectionMode = false` | visibly change the component |

The split is one derived flag (`catalogInteractive()`), never a hard-coded constant, so a single
sticker body serves both. **No sticker ships a dead handler**: stateful components own their state
(`toggleable` / `selectable` / `draggable` / `editable`), and everything else takes the click tally
(`counted`), which appends `(n)` to a label. At `n == 0` the tally returns the bare label and a
no-op handler, so the baked capture is byte-identical either way. The deliberate exceptions are
disabled stickers, which stay inert because unresponsiveness is the state they document.

## Themes

The kit defines six colour modes — light and dark, each at standard, medium and high contrast — and
all six are declared as `@ThemeCatalog` wrapper providers, so the renderer builds a specimen sheet
per theme and the viewer offers them in its theme select. Any sticker can be re-rendered under any
of them.

| Mode | Source |
| --- | --- |
| Baseline Light / Baseline Dark | Compose's stock `lightColorScheme()` / `darkColorScheme()` |
| Light + Dark × Medium / High Contrast | generated from the baseline seed via MaterialKolor |

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
[`scripts/resolve-figma-refs.mjs`](scripts/resolve-figma-refs.mjs) resolves them over the REST API
and **proposes** a ref per catalogued component, ranked by name similarity. It writes no mapping
file: you paste the ref onto the annotation, and
[`scripts/generate-design-map.mjs`](scripts/generate-design-map.mjs) projects `design-map.json` out
of the discovered manifest from there.

```sh
FIGMA_TOKEN=figd_... node scripts/resolve-figma-refs.mjs
```

Dependencies update themselves via Renovate ([`renovate.json`](.github/renovate.json)). The
compose-ai-tools CLI, Gradle plugin and annotation coordinates are grouped into one automerged PR,
because a skew between them breaks preview discovery outright.

## Licence

Apache 2.0 — see [LICENSE](LICENSE). The vendored Roboto / Roboto Flex / Noto Serif / Droid Sans
Mono faces under `catalog/src/main/resources/fonts/` carry their own OFL/Apache licences, included
alongside them.
