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
- **And therefore the right thing to import.** A definition sheet is the design
  file *stating* what a component should look like, which is exactly the claim
  this catalog reproduces — so an imported page is a claim we can put our own
  renders on top of, node by node. See [Importing a page](#importing-a-page).

## Style, documentation and utility pages

| Page | Node id | What's on it |
| --- | --- | --- |
| Getting started | `11:1833` | Introduction cards and the kit cover. Prose and screenshots. |
| Table of contents | `55879:3580` | Navigation index — 8 style groups and 38 components. Useful as a coverage checklist against `catalog/src/main/kotlin/ee/schimke/m3catalog/sections/`. |
| Styles | `49823:12141` | Type scale, `material-theme`, light/dark colour sheets. 424 text nodes and 3 instances — a specimen sheet, not components. |
| Shape | `58548:7093` | The corner-radius scale (0–48dp, full) plus the 35-shape Expressive library. The shape set pairs with `sections/Shapes.kt`, one component per shape; the radius scale has no code behind it yet. **This is the page the `/pages` surface imports.** |
| Icons | `55594:2483` | ~150 icon components. |
| Avatars | `55595:3788` | Generic avatar styles (avatar / monogram / check) and 30 3D avatars. |
| Utilities | `55594:2484` | Status bar, gesture/navigation bar, device frame, keyboard configurations, scrim, focus indicator. These are kit utilities rather than catalog components. |

## `Examples` — the composed screens (not the source any more)

`55594:2480`. **The only page in the file with instances on it**, and therefore
the only page the retired screen-backdrop import could use at all. Kept here as
the record of why that approach was dropped. Fourteen screens, each the same
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
  component behind them.
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

## Importing a page

A page is imported as **one SVG with `data-node-id` on every element**, plus a `pages.json` naming
the component nodes on it and the code each one maps to. Two REST calls per page
([`scripts/import-figma-pages.mjs`](../scripts/import-figma-pages.mjs)):

```sh
FIGMA_TOKEN=figd_... node scripts/import-figma-pages.mjs
FIGMA_TOKEN=figd_... node scripts/import-figma-pages.mjs --page shape
```

`design-pages.json` says which pages, and where the cache lands:

```jsonc
{
  "enabled": true,
  "fileKey": "ocdacdEsnHipMJD3egzxKb",
  "outDir": "design/pages",
  // Ask the file for its own page list — one `GET /v1/files/:key?depth=1`, the same call
  // `list-figma-pages.mjs` makes. Every page becomes an import; its id is a slug of its name.
  "discover": true,
  // Drop a page by node id or by name. Empty: every page in the kit is imported.
  "exclude": [],
  // PINS. An entry here fixes the id for that node however discovery names it.
  "pages": [{ "id": "shape", "nodeId": "58548:7093", "name": "Shape" }]
}
```

### Every page, not a hand-kept list

The table above could name only half the file's pages, and the section below still lists fourteen
as bare numbers, because naming one costs a full subtree dump through the MCP server. That is also
what kept this import at a single page: adding one meant a human finding an id, a name and a slug.

So the importer asks the file. With `"discover": true` it enumerates the document's pages in one
request and imports each of them, deriving the page id from the page's **own name** — `Date & time
pickers` → `date-time-pickers` — so a published URL reads like the design file rather than like a
node id. Slugging is pinned by
[`scripts/import-figma-pages.test.mjs`](../scripts/import-figma-pages.test.mjs), because those
slugs *are* URLs.

`pages` survives as the **pin** list: an entry fixes the id for its node wherever discovery finds
it, and `shape` is pinned for exactly that reason — its URL is already published, and a slug is
only stable while the designer leaves the page name alone. The name still follows the file, so a
renamed tab reads correctly in the index without moving.

### A page the file cannot export is skipped; a pinned one still fails the run

At least one page of the kit is a sheet `/v1/images` answers for with no url at all. Under a
hand-kept list that would be a config mistake worth failing on; under discovery it is just
something the file contains, and letting it abort the run would mean one unrenderable sheet costs
the other thirty their import. So a **discovered** page that fails is skipped with a line in the
log, and a **pinned** one still fails the run — a human put that id there. A run that imports
*nothing* fails regardless: that is an expired token or a moved file, not a partial result, and it
must not commit an emptied cache over a good one.

### An oversized sheet is skipped, not cached

The cache is committed here *and* appended to the `design-artifacts/m3-catalog` delivery branch on
every regeneration, so a page's export costs its bytes twice, forever. `Shape` is ~0.8 MB; the
`Buttons` sheet carries a few thousand component nodes and `Examples` fourteen whole screens. A
page whose SVG exceeds `maxSvgBytes` (default 12 MB) is therefore **skipped with a line in the run
log** rather than committed, and any stale export of it is deleted — the server caps a page at 500
nodes regardless, so the densest sheets are mostly undrawable even when they fit. A skip is not a
failure: with discovery on, an enormous sheet is a fact about the design file, not a config
mistake. A page that truncates at that 500-node cap says so in the log too.

`svg_include_node_id=true` is the whole trick. Without it the export is a picture; with it, it is a
**document a consumer can address** — the preview server inlines the SVG, finds `Shape=Circle` by
its node id, hides the design's own drawing of it, and puts this catalog's `Shape/Circle` render in
the hole it leaves. Same sheet, same layout, our pixels.

The join is `design-map.json`, so it is annotation-first like everything else here: a page node
links to code exactly when some `@CatalogComponent(reference = "figma:<key>/<nodeId>")` names that
node. `Shapes.kt` names all 35 symbols of the shape set; nothing on the corner-radius section above
them is named yet, so those import as **unlinked** — which is the finding the surface exists to
report, not a gap in the import.

### What this replaced, and why

The first cut of this surface imported one composed **screen** from `Examples` as a flat PNG and
drew a rectangle per component instance on it. The section above is the post-mortem: `Examples` is
the only page in the file with instances on it, most of each screen there is hand-drawn rather than
assembled from the kit, the densest screen yields **11** placements of which 2 are OS chrome, and a
per-variant `ref` under-links the rest. The kit's value is on the other thirty pages — the
definition sheets — and a definition sheet is exactly the claim this catalog is trying to reproduce.

A raster could never have done the swap, either. Nothing can reach inside an `<img>`, so the old
surface could only lay a translucent overlay on top and hope the eye separated the two drawings.

### No geometry is recorded, deliberately

A node in `pages.json` carries no bounding box. The old PNG manifest had to — a flat raster has no
structure to ask. An SVG does: the element is right there, and the browser measures it. Recording
Figma's `absoluteBoundingBox` alongside would give one question two answers that disagree by a few
pixels on anything with a shadow (the export box is the *render* box, effect bleed included), and a
consumer choosing between them would silently pick the wrong one.

### Refreshing the cache

The [`Refresh Figma pages`](../.github/workflows/figma-pages.yml) workflow runs the import on
demand and commits the refreshed `design/pages/` to the branch it ran on. It is manual and
read-only against Figma for the same reasons `figma-refs.yml` is: the Figma file moves on its own
schedule, and a push-triggered re-import would attribute a designer's edit to whoever opened the PR.

It **commits**, where `figma-refs.yml` only uploads an artifact, because the import is a *cache*:
a committed `design/pages/` means the catalog publish, a fork, and a local `serve` all render the
page with no Figma token at all.

Note that an agent sandbox may have no egress to `figma.com` at all — in the environment this was
built in, both `www.figma.com` and `api.figma.com` were refused at the proxy with `403 CONNECT`, so
no token would have helped. That is what the workflow is for.

### Where it is shown

**On the preview server, at
[`preview.coo.ee/m3-catalog/pages`](https://preview.coo.ee/m3-catalog/pages).** The catalog's
`design-artifacts/m3-catalog` branch carries the cache (`pages/index.json` plus each page's SVG),
the server stages it like any other catalog asset, and the catalog landing links `1 page` beside
"design parity".

Publishing happens inside the ordinary
[`Design Artifacts`](../.github/workflows/design-artifacts.yml) run, and needs **no credential**:
the shared reusable workflow only re-keys the committed import onto the published catalog's serve
preview ids and copies the SVGs into the bundle. So a fork, a token-less run and an offline
republish all produce the same pages.

[kit]: https://www.figma.com/design/ocdacdEsnHipMJD3egzxKb/Material-3-Design-Kit--Community-
