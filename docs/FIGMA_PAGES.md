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
importer now makes the same call itself (`"discover": true`), so the tables
below are no longer hand-assembled: every page name and id in them came out of
[`design/pages/pages.json`](../design/pages/pages.json), which the import
writes. The fourteen pages this file used to list as bare numbers are named in
[Component pages](#component-pages).

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
| Shape | `58548:7093` | The corner-radius scale (0–48dp, full) plus the 35-shape Expressive library. The shape set pairs with `sections/Shapes.kt`, one component per shape; the radius scale has no code behind it yet. **The page this surface started with, and still the only one whose specimens are almost all linked** (35 of 38). |
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

Each holds one component family's variant sets — Figma component *sets*, named
with `Property=Value` variants, which is exactly what an instance's
`componentSetId` points at and what a `design-map.json` ref names. Every one of
these pages is imported; per-node detail (which node, which layer name, which
Kotlin member) lives in [`design/pages/pages.json`](../design/pages/pages.json).

**Nodes** is how many component nodes the import recorded for the page (`500` is
the cap — the sheet has more); **linked** is how many of them `design-map.json`
joins to code, which is this surface's coverage number. Both come straight out
of the last import, so they are a snapshot, not a target.

| Page | Node id | Page id | Nodes | Linked |
| --- | --- | --- | --- | --- |
| App bars | `55141:14169` | `app-bars` | 141 | 8 |
| Badges | `55141:14167` | `badges` | 4 | 2 |
| Buttons | `55141:14168` | `buttons` | 500 | 29 |
| Cards | `55141:14171` | `cards` | 69 | 6 |
| Carousel | `55141:14172` | `carousel` | 58 | 2 |
| Checkboxes | `55141:14173` | `checkboxes` | 58 | 6 |
| Chips | `55141:14174` | `chips` | 500 | 14 |
| Date & time pickers | `55141:14175` | `date-time-pickers` | 500 | 2 |
| Dialogs | `55141:14176` | `dialogs` | 51 | 3 |
| Dividers | `55141:14177` | `dividers` | 8 | 4 |
| Lists | `55141:14249` | `lists` | 500 | 0 |
| Loading & progress | `55141:14252` | `loading-progress` | 439 | 12 |
| Menu | `55141:14250` | `menu` | 381 | 1 |
| Navigation | `55141:14251` | `navigation` | 500 | 0 |
| Radio button | `55141:14253` | `radio-button` | 24 | 4 |
| Search | `55141:14254` | `search` | 98 | 4 |
| Sheets | `55141:14170` | `sheets` | 50 | 2 |
| Sliders | `55141:14255` | `sliders` | 500 | 4 |
| Snackbar | `55141:14256` | `snackbar` | 49 | 6 |
| Switch | `55141:14257` | `switch` | 36 | 8 |
| Tabs | `55141:14258` | `tabs` | 226 | 9 |
| Text fields | `55141:14259` | `text-fields` | 254 | 6 |
| Toolbars | `58295:22726` | `toolbars` | 500 | 4 |
| Tooltips | `55141:14261` | `tooltips` | 9 | 2 |

The fourteen ids this file used to list as unidentified are in that table:
`14176` is Dialogs, `14177` Dividers, `14249` Lists, `14250` Menu, `14251`
Navigation, `14252` Loading & progress, `14253` Radio button, `14254` Search,
`14255` Sliders, `14256` Snackbar, `14257` Switch, `14258` Tabs, `14259` Text
fields, `14261` Tooltips, and `58295:22726` Toolbars. They were never in
alphabetical order, which is why guessing them from the Table of contents would
have been wrong — the import read them off the file.

### The three pages not imported

| Page | Node id | Why |
| --- | --- | --- |
| Avatars | `55595:3788` | Exports at **52 MB** — generic avatar styles plus 30 3D renders. Over any sane cap, and no component in this catalog stands behind it. |
| Getting started | `11:1833` | Excluded by name. 12.8 MB of introduction cards and screenshots, with no `COMPONENT` nodes for a render to sit on top of. |
| *(unnamed)* | `55597:372` | Figma answers `/v1/images` with **no url** for it. Its layer name slugs to nothing printable, so the log calls it `55597-372`. |

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
the server stages it like any other catalog asset, and the catalog landing links the page count beside
"design parity" — 30 of them, listed by name in the catalog's navigation tree.

Publishing happens inside the ordinary
[`Design Artifacts`](../.github/workflows/design-artifacts.yml) run, and needs **no credential**:
the shared reusable workflow only re-keys the committed import onto the published catalog's serve
preview ids and copies the SVGs into the bundle. So a fork, a token-less run and an offline
republish all produce the same pages.

[kit]: https://www.figma.com/design/ocdacdEsnHipMJD3egzxKb/Material-3-Design-Kit--Community-
