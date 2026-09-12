# Material 3 Samples — design artifacts

Importable sticker-sheet for **`org.jetbrains.compose.material3:material3`**, rendered from the committed
`@Preview` catalog in [`yschimke/m3-catalog`](https://github.com/yschimke/m3-catalog). This branch is a
**generated delivery artifact** — browse it in the page below, or pull it into
Figma / Stitch / Claude Design.

## 🔎 Browse the catalog

**[▶ Open the rendered catalog (htmlpreview)](https://htmlpreview.github.io/?https://github.com/yschimke/m3-catalog/blob/design-artifacts/m3-samples/index.html)**

A self-contained gallery — one card per component with its rendered PNG,
dimensions, accessibility greenlines, and a link to an editable SVG wireframe.

## 🔬 Compare SVG vs PNG

**[▶ Open the SVG↔PNG comparison (htmlpreview)](https://htmlpreview.github.io/?https://github.com/yschimke/m3-catalog/blob/design-artifacts/m3-samples/compare.html)**

Every component on one row: its editable **figma-svg** re-rasterized by the
browser beside the rendered **PNG** it is measured against — the design vector on
the left, the render on the right, as everywhere else the two are shown together
— plus a live **structural-similarity (SSIM)** match
score — so you can eyeball vector fidelity across the whole system at once and
spot which stickers drift. The score is pre-blurred and downscaled, so a
half-pixel rasterizer offset doesn't read as a mismatch.

## ↔ Compare across systems

**[▶ Open the Material 3 Samples ↔ Material 3 Design Kit matches (htmlpreview)](https://htmlpreview.github.io/?https://github.com/yschimke/m3-catalog/blob/design-artifacts/m3-samples/matches.html)**

Every component paired with its counterpart in **Material 3 Design Kit**, side by side — the
authored `parallel` mapping in the catalog spec, rendered as a cross-system contact sheet. Both
sides are static thumbnails — this branch's baked render on the left, the m3-catalog
render baked from its own `design-artifacts/m3-catalog` branch on the right — and each
links to the live preview server on click.

## 🎛 Customise live

**[▶ Open this catalog in the live preview server](https://preview.coo.ee/m3-samples/)**

The same rendered components, served live by `compose-preview serve --catalogs m3-samples` —
open one, then change the theme, locale, font scale, or device and watch it
re-render. Every entry in `catalog.json` carries a per-variant `livePreview`
deep link to its exact preview on the same server, so browsing this branch and
customising the live render are two ends of one workflow.

## At a glance

| | |
| --- | --- |
| Components | **240** |
| Rendered images (PNG) | **240** |
| Editable wireframes (SVG) | **240** |
| Editable design vectors (figma-svg) | **240** |
| Components with a11y greenlines | **185** |
| Library | `org.jetbrains.compose.material3:material3` |
| Renderer | compose-preview 2.11.0 |
| Schema | `design-parity-catalog/v1` |
| Generated | 2026-09-12 |

## Components by group

| Group | Count |
| --- | ---: |
| SplitButtonLayout | 12 |
| Button | 9 |
| ListItem | 9 |
| ToggleButton | 7 |
| FlexibleBottomAppBar | 6 |
| PullToRefreshBox | 6 |
| FilterChip | 5 |
| HorizontalFloatingToolbar | 5 |
| IconButton | 5 |
| InputChip | 5 |
| Slider | 5 |
| TopAppBar | 5 |
| VerticalFloatingToolbar | 5 |
| PrimaryTabRow | 4 |
| Surface | 4 |
| AlertDialog | 3 |
| BottomAppBar | 3 |
| Checkbox | 3 |
| CircularWavyProgressIndicator | 3 |
| ContainedLoadingIndicator | 3 |
| ExtendedFloatingActionButton | 3 |
| LargeExtendedFloatingActionButton | 3 |
| LinearWavyProgressIndicator | 3 |
| MediumExtendedFloatingActionButton | 3 |
| OutlinedIconButton | 3 |
| SegmentedListItem | 3 |
| SmallExtendedFloatingActionButton | 3 |
| WideNavigationRail | 3 |
| AppBarWithSearch | 2 |
| ButtonGroup | 2 |
| Card | 2 |
| CircularProgressIndicator | 2 |
| ElevatedButton | 2 |
| ElevatedCard | 2 |
| ElevatedFilterChip | 2 |
| ExposedDropdownMenuBox | 2 |
| FilledIconButton | 2 |
| FilledIconToggleButton | 2 |
| FilledTonalButton | 2 |
| FilledTonalIconButton | 2 |
| FilledTonalIconToggleButton | 2 |
| IconToggleButton | 2 |
| Label | 2 |
| LinearProgressIndicator | 2 |
| LoadingIndicator | 2 |
| minimumInteractiveComponentSize | 2 |
| ModalWideNavigationRail | 2 |
| OutlinedButton | 2 |
| OutlinedCard | 2 |
| OutlinedIconToggleButton | 2 |
| pinnedScrollBehavior | 2 |
| RadioButton | 2 |
| RangeSlider | 2 |
| Scaffold | 2 |
| ScrollField | 2 |
| ShortNavigationBar | 2 |
| showSnackbar | 2 |
| Snackbar | 2 |
| Switch | 2 |
| TextButton | 2 |
| TooltipBox | 2 |
| TriStateCheckbox | 2 |
| VerticalSlider | 2 |
| animateFloatingActionButton | 1 |
| animateWidth | 1 |
| AssistChip | 1 |
| BadgedBox | 1 |
| BottomSheet | 1 |
| BottomSheetScaffold | 1 |
| CenterAlignedTopAppBar | 1 |
| DismissibleNavigationDrawer | 1 |
| DropdownMenuItem | 1 |
| ElevatedAssistChip | 1 |
| ElevatedSuggestionChip | 1 |
| ElevatedToggleButton | 1 |
| enterAlwaysScrollBehavior | 1 |
| exitAlwaysScrollBehavior | 1 |
| exitUntilCollapsedScrollBehavior | 1 |
| FloatingActionButton | 1 |
| LargeFlexibleTopAppBar | 1 |
| LargeFloatingActionButton | 1 |
| LargeTopAppBar | 1 |
| MediumFlexibleTopAppBar | 1 |
| MediumFloatingActionButton | 1 |
| ModalBottomSheet | 1 |
| ModalNavigationDrawer | 1 |
| MultiChoiceSegmentedButtonRow | 1 |
| NavigationBar | 1 |
| NavigationRail | 1 |
| OutlinedToggleButton | 1 |
| PermanentNavigationDrawer | 1 |
| PullToRefreshState | 1 |
| SearchBar | 1 |
| SecondaryTabRow | 1 |
| SegmentedButton | 1 |
| SmallFloatingActionButton | 1 |
| SuggestionChip | 1 |
| SwipeToDismissBox | 1 |
| TabRow | 1 |
| Text | 1 |
| TonalToggleButton | 1 |
| TwoRowsTopAppBar | 1 |

## What's in this branch

| Path | What it is |
| --- | --- |
| `index.html` | Self-contained gallery — [open via htmlpreview](https://htmlpreview.github.io/?https://github.com/yschimke/m3-catalog/blob/design-artifacts/m3-samples/index.html) |
| `compare.html` | SVG↔PNG comparison with a live structural-similarity score — [open via htmlpreview](https://htmlpreview.github.io/?https://github.com/yschimke/m3-catalog/blob/design-artifacts/m3-samples/compare.html) |
| `matches.html` | Cross-system component pairing vs `m3-catalog` — [open via htmlpreview](https://htmlpreview.github.io/?https://github.com/yschimke/m3-catalog/blob/design-artifacts/m3-samples/matches.html) |
| `catalog.json` | Machine-readable catalog (`design-parity-catalog/v1`): components, variants, design tokens, greenlines, and per-variant `livePreview` deep links |
| `images/` | Rendered PNGs — the source of truth for each variant |
| `wireframes/` | One editable SVG per component (layout-inspector tree → token-styled shapes) |

## Using it

- **Figma / Stitch / Claude Design** — import `catalog.json` + `images/` as a sticker sheet.
- **Browse** — open `index.html` through htmlpreview (link above), or clone the branch and open it locally.
- **Customise** — open the [live preview server](https://preview.coo.ee/m3-samples/) (or any image's `livePreview` link in `catalog.json`) to re-render a component under different themes / locales / devices.
- **Adopt structure** — the `wireframes/*.svg` are plain vector files; drop one into any editor to start from the real layout instead of tracing a screenshot.

## Provenance

Generated by the [`Design Artifacts`](https://github.com/yschimke/m3-catalog/actions/workflows/design-artifacts.yml)
workflow: `compose-preview bundle pack` → catalog-export driver → force-push to
this branch. The render is the source of truth.

> ⚠️ **This branch is regenerated (force-pushed) from `main`** — weekly and after
> catalog/renderer changes. Don't commit work here by hand; it will be
> overwritten on the next run.
