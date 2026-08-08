# Figma page index — Material 3 Design Kit

Node ids for the pages of the kit this catalog reproduces:
[`ocdacdEsnHipMJD3egzxKb`][kit].

`design-map.json` pins each catalogued component to a **node** id. This is the
level above: which **pages** exist, what is on them, and which page a given ref
lives on. It exists because page ids are as undiscoverable as node ids —
[`scripts/resolve-figma-refs.mjs`](../scripts/resolve-figma-refs.mjs) already
notes that *"the Figma MCP server exposes only the page a user is looking at"* —
so without this file the answer is a manual click-through every time.

**Regenerate the complete list with one REST call:**

```sh
FIGMA_TOKEN=figd_... node scripts/list-figma-pages.mjs
```

That returns every page's id and name from `GET /v1/files/:key?depth=1`. The
table below was assembled by inspecting pages one at a time through the MCP
server, which is why part of it is still blank — see
[Unidentified pages](#unidentified-pages).

## The one structural fact that matters

**Every page except `Examples` is a component-*definition* sheet.** Their
contents are `COMPONENT` / `COMPONENT_SET` nodes — the variant grids a designer
pulls instances *from* — not instances of anything.

That decides what each page is good for:

- **Reference targets.** A definition page is where `design-map.json` refs
  point, and where `resolve-figma-refs.mjs` looks.
- **Not page backdrops.** A design-parity page backdrop places *instances* on a
  screen; a page with no instances yields no placements. Only `Examples` holds
  composed screens, so it is the only backdrop source in the file.

## Style, documentation and utility pages

| Page | Node id | What's on it |
| --- | --- | --- |
| Getting started | `11:1833` | Introduction cards and the kit cover. Prose and screenshots. |
| Table of contents | `55879:3580` | Navigation index — 8 style groups and 38 components. Useful as a coverage checklist against `catalog/src/main/kotlin/ee/schimke/m3catalog/sections/`. |
| Styles | `49823:12141` | Type scale, `material-theme`, light/dark colour sheets. 424 text nodes and 3 instances — a specimen sheet, not components. |
| Shape | `58548:7093` | The corner-radius scale (0–48dp, full) plus the 36-shape Expressive library. Pairs with `ShapeScale.kt`. |
| Icons | `55594:2483` | ~150 icon components. |
| Avatars | `55595:3788` | Generic avatar styles (avatar / monogram / check) and 30 3D avatars. |
| Utilities | `55594:2484` | Status bar, gesture/navigation bar, device frame, keyboard configurations, scrim, focus indicator. These are what `FullScreenM3` and `SYSTEM_BAR_INSET` reproduce. |

## `Examples` — the composed screens

`55594:2480`. **The only page in the file with instances on it**, and therefore
the only source of design-parity page backdrops. Fourteen screens, each the same
seven flows at two window size classes, plus a layout-grid section
(`56384:120`) showing all five window size classes with and without a navigation
region.

| Screen | Mobile (412 wide) | Web (905×680) |
| --- | --- | --- |
| Home | `56615:82356` (h 1788) | `56615:82332` |
| Library | `56615:48333` (h 1020) | `56615:48317` |
| Upcoming | `56615:48121` (h 954) | `56615:48097` |
| Detailed view | `56615:47726` (h 1072) | `56615:47706` |
| Gallery | `56615:47041` (h 1184) | `56615:47017` |
| Messaging | `56615:46684` (h 910) | `56615:46667` |
| Reviews | `56615:45927` (h 983) | `56615:45943` |

Not all are equally useful. Instance density varies enormously, because much of
each screen is hand-drawn rather than assembled from the kit's components:

- **`Upcoming-Mobile` is the densest** — a trial import produced **11
  placements**, and 9 of them are components this catalog already binds: App
  bar, Carousel, Button-text, Icon button, and List item ×5. The other two are
  the Status bar and Gesture bar, which are OS chrome and correctly have no code
  component behind them. It is also the closest analogue to
  `Template/AppScaffold`.
- **`Home-Mobile`** is the flagship and twice as tall, but mostly bespoke frames
  (avatars, card grids, labels) with a low instance count.
- **`Messaging-Mobile`** is almost entirely hand-built chat bubbles; only the
  app bar and search bar are kit components.

## Component pages

Each holds one component family's variant sets. The `design-map.json` refs in
the right column are the **container frames** — Figma component *sets*, named
with `Property=Value` variants — which is exactly what an instance's
`componentSetId` points at.

| Page | Node id | `design-map.json` refs verified on it |
| --- | --- | --- |
| App bars | `55141:14169` | `58114:20565` → `TopAppBars.kt#SmallTopAppBar`<br>`51159:5105` → `BottomAppBars.kt#BottomAppBarSticker` |
| Badges | `55141:14167` | `51592:4768` → `Badges.kt#NumberBadge` |
| Buttons | `55141:14168` | — (3018 component nodes; the largest page in the file) |
| Cards | `55141:14171` | — (`Cards.kt` is not yet in `design-map.json`) |
| Carousel | `55141:14172` | `53912:27480` → `Carousel.kt#MultiBrowseCarousel` |
| Checkboxes | `55141:14173` | — (`Checkboxes.kt` is not yet in `design-map.json`) |
| Chips | `55141:14174` | `53923:28089` → `AssistChipSticker`<br>`53923:28270` → `FilterChipSticker`<br>`53923:27888` → `InputChipSticker`<br>`53923:28679` → `SuggestionChipSticker` |
| Date & time pickers | `55141:14175` | `52949:27916` → `TimePickers.kt#TimePickerSticker` |
| Sheets | `55141:14170` | `53198:27851` → `SideSheets.kt#StandardSideSheet`<br>`51827:5859` → `BottomSheets.kt#ModalBottomSheetSticker` |

### Unidentified pages

These fourteen page ids are known to exist but have not been named here.
Enumerating them through the MCP server costs one full subtree dump each — the
`Buttons` page is ~448 KB and `Chips` comparable — which is not a sane way to
learn fourteen names. **Run `scripts/list-figma-pages.mjs` to fill them in**; it
answers the whole question in one request.

```
55141:14176   55141:14177   55141:14249   55141:14250   55141:14251
55141:14252   55141:14253   55141:14254   55141:14256   55141:14257
55141:14258   55141:14259   55141:14261   58295:22726
```

Their names are almost certainly drawn from the Table of contents' component
list, but the ids are **not** in alphabetical order (`App bars` is `14169`,
after `Badges` `14167` and `Buttons` `14168`), so nothing here is inferred from
position — an entry is listed above only if the page was actually opened.

## Using this for a page backdrop

`@design-parity/page-backdrop` is opt-in and off by default; it does nothing
until a `design-pages.json` exists with `"enabled": true`.

```jsonc
{
  "enabled": true,
  "fileKey": "ocdacdEsnHipMJD3egzxKb",
  "pages": [{ "nodeId": "56615:48121", "id": "upcoming-mobile" }],
  "outDir": "design/pages"
}
```

```sh
design-parity-pages import --design-map design-map.json
```

### The import needs the REST API — MCP is not a substitute

A trial run drove the importer from `get_metadata` instead of the REST API. The
geometry came through perfectly: 11 placements, every nested offset resolved
(the text button lands at y=381 = 164 + 217, the five list items at 469…825).
Two things did not, and both are properties of the MCP source rather than bugs:

- **No `componentId`.** `get_metadata` reports id, name, type and box, but never
  an instance's main component — so the only ref the linker can try is the
  instance's own node id, which nothing in `design-map.json` points at. Every
  placement came back `unlinked`. Linking needs `/v1/files/:key/nodes`, which
  returns `componentId` per instance plus the file-level `components` map that
  carries `componentSetId`. Code Connect would also answer this, but the kit is
  a Community file and `get_code_connect_map` requires a Dev/Full seat.
- **Parent-local coordinates.** MCP reports x/y relative to the parent;
  `absoluteBoundingBox` (what the importer consumes) is canvas-absolute. Any
  MCP-backed fetcher has to accumulate offsets down the tree.

The backdrop image needs `/v1/images`. Note that an agent sandbox may not have
egress to `figma.com` at all — in the environment this was tried in, both
`www.figma.com` and `api.figma.com` were refused at the proxy with `403
CONNECT`, so no token would have helped either.

Because this repo is `design-led`, anything the overlay shows drifting is by
definition a bug in the code — which is the whole reason a whole-screen view is
worth having here and not everywhere.

[kit]: https://www.figma.com/design/ocdacdEsnHipMJD3egzxKb/Material-3-Design-Kit--Community-
