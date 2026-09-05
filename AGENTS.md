# Repository instructions for AI agents

Two handover docs, for two different problems:

- **Render too slow / timing out?** [docs/PARALLEL_RENDER.md](docs/PARALLEL_RENDER.md) — shards the
  render across parallel jobs. This is the one that addresses the timeout.
- **Adding component coverage?** [docs/PARALLEL_SWEEP.md](docs/PARALLEL_SWEEP.md) — divides the
  authoring work, with a claim protocol. It does *not* help with the timeout.

Read [README.md](README.md) first — it describes what this repo is and how it is laid out. This
file records the conventions that are easy to violate by accident.

## Annotation-first is the rule, not a preference

The catalog inventory lives in annotations next to the composables. **Do not** reintroduce a
`groups` array in `catalog.spec.json` to add, rename or recaption a component — put it on the
`@CatalogComponent` / `@CatalogVariant`. The spec is layered over the annotations as a field-level
override and exists for cover-sheet fields only.

If you find yourself writing a lot of mapping config to express something, that is a signal the
upstream libraries are missing an annotation — **raise it in
[compose-ai-tools](https://github.com/yschimke/compose-ai-tools) and add the annotation there**
rather than growing a JSON file here. The same rule applies to the CI pipeline: a capability any
catalog could want belongs as a generic input on the reusable `design-artifacts-reusable.yml`
workflow, never as a forked copy of the pipeline in this repo.

## Direction: design-led, and Figma is read-only

Two rules that together decide what "correct" means here.

**The kit is the source of truth.** `.design-parity.json` says `design-led`, so a parity finding is
a defect in this code, not a note about the kit. When the render and the kit disagree — the corner
radii are the live example, where `ButtonDefaults.shape` gives 20 against the kit's 16 — the code
moves. Recording the divergence and moving on is the *code-led* posture, and it is not this repo's.
Where Compose genuinely cannot express what the kit specifies, say so in the caption or the
component's KDoc rather than silently rendering something else.

**Never write to Figma.** Every interaction with the kit is read-only: the REST API for node ids
and reference images, the MCP server for variables and metadata. Do not call `use_figma`,
`create_new_file`, `upload_assets`, `add_code_connect_map`, `send_code_connect_mappings`, or any
other mutating Figma tool, and do not enable design-parity's Code-to-Canvas push-back. The
`design-led` direction already gates that push-back off, so the config and the convention agree —
keep them that way.

## What enters the inventory, and what it is called

The same question has arrived four times (#3, #5, #9, #10): what does this catalog do when Compose
has a component and the kit does not, and whose word wins when the two taxonomies disagree? One
answer to both halves.

**Membership is the kit's call.** Every published component names one exact, renderable kit node in
its `reference`. A Compose API the kit never published or has retired — the 80dp `NavigationBar`
against the kit's single 64dp bar, the navigation drawers the kit files under `Deprecated`, the
generic screen templates — does not enter the component inventory at all. There is no
"published but unmapped" state, and no reason field buys an exemption: `CatalogInventoryTest`'s
`every component maps to Figma` fails the build for a `@CatalogComponent` with no `reference`, and
`scripts/design-map.sh` fails the same way (it passes `--strict`) before a render is ever
attempted. Leaving the
reference off to keep an unmatched component is the posture that rule replaced — a sticker with
nothing to compare against publishes a picture the catalog cannot defend.

**Membership does not make every kit cell authorable.** A kit component set can expose an axis the
exact named Material API cannot express. Keep that value out of the sticker and declare it in
`kit-unauthorable.json` with evidence; never substitute a differently named Material API or map the
value to pixels that do not depict it. The declaration is a checked gap, not component-level
"published but unmapped" state: `scripts/kit-coverage.mjs` fails when the kit removes the value or
the catalog later covers it. `Suggestion chip / Selected=True` is the precedent — suggestion chips
are action chips in Compose, while selection belongs to `FilterChip`, so the kit-only selected
cells remain explicitly uncovered.

**A component the kit does not publish can still be a preview.** Membership decides what enters the
*inventory*, not what the module may render. The adaptive layouts — `NavigationSuiteScaffold`,
`ListDetailPaneScaffold`, `SupportingPaneScaffold` — live under
`catalog/src/main/kotlin/ee/schimke/m3catalog/adaptive/` as plain `@Preview`s for exactly that
reason: the kit has no node for them, so they carry no `@CatalogComponent`, add nothing to the
design map and are compared against nothing. Do not "fix" that by moving them into `sections/` and
inventing a reference. Anything published there needs the same treatment: outside `sections/`,
no `@CatalogComponent`, and a comment saying which kit node was looked for and not found.

**Naming is Compose's call.** Ids follow the Compose API surface, because that is what a reader of a
Compose catalog greps for: `NavigationBar/Short` is named for `ShortNavigationBar` even though the
kit calls that node simply "Navigation bar". The one hard rule is not to borrow a kit word for
something the kit uses differently — `DatePicker/Docked` named the bare `DatePicker()` grid while
"docked" in the kit means the text-field-led picker, so the catalog and the kit disagreed about the
same word. Where Compose has no name of its own, take the kit's.

## Sticker conventions

- One file per component **group**, opening with `@file:CatalogGroup(name = …, section = …)`.
- Every `@CatalogComponent` carries a `caption`. A component with no caption publishes as a bare
  picture, and a test fails the build for it.
- **Fold variants behind defaults.** A state / content axis is a `@CatalogVariant(of = …)` under its
  parent, not a new top-level component. A system this size is only browsable because the card count
  tracks components, not renders.
- **One kit component set is one catalog component — except for emphasis.** The kit's set boundary
  decides the taxonomy, so a kit variant property folds in as a variant: `Type = Flat | Wave` on the
  progress indicators, `Orientation` on the toolbars and sliders, `Size = Small | Large` on badges,
  `Configuration` on the app bars, `Shape=` on all 35 shapes. The one carve-out is the
  **emphasis/style** axis — filled / outlined / elevated / tonal / text, and the tabs' primary vs
  secondary — which stays a component per style. That is not a fudge: the kit itself splits that
  axis into five `Button` sets, four `Icon button` sets, four `Toggle button` sets and four chip
  sets, and `Stacked card` and `Text field` are the only two sets where it does not. It is also the
  choice a reader makes at the call site (`Card` vs `OutlinedCard` vs `ElevatedCard`), where the
  folding axes are states of one component.
  **"It is a separate composable" is not a reason to split.** That is a fact about the Compose API,
  not about the taxonomy — `HorizontalCenteredHeroCarousel`, `VerticalSlider`, `DockedSearchBar` and
  `CircularWavyProgressIndicator` are all separate functions folded as variants. Naming still
  follows Compose (see above); membership follows the kit.
  `CatalogInventoryTest` fails the build when two components resolve to the same kit set without an
  entry in its exemption table, so a new split has to state its reason.
- **Check a fold by regenerating the map, not by reading it.** A variant's props are matched against
  the kit's own variant *values*, so the prop has to spell what the kit says — `type=range` resolves
  to nothing against `Type=Full-screen (range)`, and drops that node from the comparison with no
  diagnostic anywhere. Run `scripts/design-map.sh` and confirm the node count did not fall.
  (compose-ai-tools#4086 would let a variant name the Compose value and the kit value separately.)
- Component ids are the published sticker's URL and the join key for `@CatalogVariant(of = …)`.
  Renaming one moves a published URL — do it deliberately.
- **No dead handlers.** Stateful components own their state (`toggleable` / `selectable` /
  `draggable` / `editable`); everything else takes `counted`. Disabled stickers stay inert on
  purpose. See `CatalogInteractive.kt`.
- **A live click answers with the component, not with the label.** `counted` returns the label it
  was given; the ripple, state layer and pressed shape are what a click is supposed to show. The
  `(n)` tally is the `clickCount` knob, off by default — reach for it when the question really is
  "did the handler run?", never as a sticker's standing answer to a press
  ([wear-m3-catalog#32](https://github.com/yschimke/wear-m3-catalog/issues/32)).
- **Expose the scalar parameters a reader can use.** The live preview server builds its Overrides
  panel from the `previewOverride*` calls a sticker makes, so a pinned value is invisible there.
  Name a knob after the Compose parameter or content slot it feeds (`label`, `value`,
  `supportingText`, `preferredItemWidth`), use `catalogChoice` for a closed set, and use the typed
  boolean / int / float / dp override instead of parsing a string. User-visible copy goes through
  `catalogText(key, stringResource(...))`: the baked default stays translated and design-led, while
  the held live render gets an editable text field. Keep state keyed on its override-derived
  initial value; otherwise the panel changes while the remembered component does not.
- **A kit BOUND goes on a frame; a kit SIZE goes on the component.** The two look the same in a
  diff and are not the same thing. A size the kit specifies and a caller would really pass —
  `Card`'s 360x480, a nav bar's 412dp, a text field's 210dp — belongs on the component: that is the
  component being used as documented. A bound that is the component's OWN measured extent in the kit
  — the snackbar's 344x48 bar, the floating toolbar's 168dp — belongs on a `Box` around it, the way
  `ButtonFrame` / `ToggleButtonFrame` / `IconButtonFrame` already do it. `Modifier.width(…)` is a
  TIGHT constraint, so pinning one of those hands the component a minimum it has to honour: the
  snackbar measured its action and close slots at the full bar width and drew the message across
  them, and the toolbar published a container 8dp wider than Compose builds (#177). A frame gives the
  same extent with a loose minimum, so the sticker still renders at the kit node's size and the
  component still measures itself.
- **A primary variant cell has to change a pixel.** A `@CatalogVariant` / primary
  `@OverrideVariant` seeds a knob;
  if the composable never reads that knob — an early return taken before the slot resolvers run, a
  hardcoded slot, a pinned size the component cannot lay out inside — the render is byte-identical
  to its default and the sheet publishes one picture under two names. `design-led` then scores that
  cell against a kit node it is not a picture of. `scripts/duplicate-renders.mjs` hashes every
  primary render of a sticker after the PR render in `compose-preview.yml` and fails on any
  collision that `duplicate-renders.json` does not declare against an open issue; the declarations
  are checked in both directions, so a set that stops colliding fails until its entry is deleted.
  Fix the sticker first — a declaration is a record of a known bug, not a way to keep one.
  Secondary exhaustive cells are exact kit comparison addresses, not navigable variant-tree
  claims. Different kit property vectors may legitimately repeat pixels, so they are outside this
  audit.
- Renders must be **deterministic**: a date picker is pinned to a fixed instant, a time picker to
  10:10. An unpinned picker would open on "today" and every nightly render would differ from the
  last, which turns the delivery branch's history into noise.
- **User-visible language copy is a string resource, never a literal.** A sticker's labels, titles,
  supporting text and `contentDescription`s resolve with `stringResource(Res.string.…)` from
  `catalog/src/main/composeResources/values*/strings.xml`. Adding a string means adding it to
  `values/` **and to all 17 locale files** — `CatalogTranslationsTest` fails the build for a key
  that is missing from a locale, left as the English copy, or declared and never rendered. What
  stays a literal: token names (`primary`, `Display Large`, `XS`) and sample data that isn't
  language (`alice@example.com`, `⌘E`, person names). Numeric samples such as `10:30` and badge
  counts pass through `localizedDigits(...)` so the locale's numbering system still applies.
- Every published comparison must invoke the actual named Material 3 composable. Rebuilding a
  component from `Surface`, `Column`, or its `*Defaults` can make a replica line up, but it cannot
  test the library and therefore does not belong in the comparison inventory. Dialog APIs may use
  `InlineDialogHost`, which replaces only their platform window while leaving the real Material
  layout intact. Popup-hosted components stay out of the inventory until the renderer can capture
  their popup surface (compose-ai-tools#3916). A kit component with no Compose Material 3 API also
  stays out of the inventory.

## The catalog over MCP

`.mcp.json` at the repository root registers the hosted catalog server
(`compose-preview-catalog`, `POST https://preview.coo.ee/mcp`) for every agent that reads
project-scoped MCP config, so an agent working here can list and render published previews without
being handed the endpoint first.

- **Default to `m3-catalog`, this repository's own catalog.** Catalog-bearing tools take a `catalog`
  argument, and the endpoint is the aggregate one deliberately: the reference design systems
  (`m3-catalog`, `wear-m3-catalog`) and the app catalogs stay reachable for a cross-catalog
  comparison. Reach for a neighbour on purpose, not by leaving the argument off.
- **No credential is committed, and none may be.** The file passes
  `X-Compose-Preview-Token: ${COMPOSE_PREVIEW_TOKEN:-}`, so a session that exports a grant token
  uses it and a session that does not sends an empty header — which the server reads exactly like
  no header at all. Reading a catalog needs a short-lived grant; `initialize`, `ping`, `tools/list`,
  `request_access` and `poll_access` do not
  ([compose-preview-server#277](https://github.com/yschimke/compose-preview-server/pull/277)).
- **Getting a grant in-band:** call `request_access`, show the human its `approveUrl` and `userCode`
  and let them approve, poll `poll_access` until it answers `approved`, then export the bearer as
  `COMPOSE_PREVIEW_TOKEN` and reconnect the server. The last step is not optional — an MCP host
  cannot inject a header its config never declared, so the token has to reach the transport through
  the environment.
- A server older than that change answers `401` to **every** MCP message, handshake included
  (preview.coo.ee served 2.22.0 when this landed). Against one of those the file only helps a
  session that already holds a token.

### The UI builder is on that same endpoint

There is no second server to register. The `ui_builder_*` tools arrive on the endpoint `.mcp.json`
already declares. A sidecar on a path of its own was designed and then rejected — an agent holds
exactly one bearer for the box, and two endpoints would have meant two origin checks, two body caps
and two places to drift about what a grant means
([`CATALOG_MCP.md` → Relationship to UI-builder MCP](https://github.com/yschimke/compose-preview-server/blob/main/docs/design/CATALOG_MCP.md#relationship-to-ui-builder-mcp)).
Pointing a second entry at `/ui-builder/mcp` gets a `404`.

| Tool | Needs |
| --- | --- |
| `ui_builder_list_catalogs`, `ui_builder_list_designs`, `ui_builder_get_design` | `ui-builder-read` |
| `ui_builder_create_design`, `ui_builder_apply` | `ui-builder-write` |
| `ui_builder_export`, `ui_builder_render_native` | `ui-builder-export` |

- **Capabilities are not scopes, and you have to ask for them.** `preview` and `live` do not carry
  the builder; pass `capabilities: ["ui-builder-read", …]` to `request_access` alongside `scope`.
  The human approving the request sees exactly what was asked for, and every tool is then checked
  per call against the same `UiBuilderRouteCapability` mapping the browser's Design API uses — the
  gate an agent reaches is the gate a person reaches.
- **A design pins a component catalog, and the pin is checked.** Start at `ui_builder_list_catalogs`
  so the `catalogPin` names a real revision. There is deliberately no blank-template argument:
  create from a whole `document`, or `fromDesignId` to copy an existing design and inherit a pin
  that is real by construction.
- **`baseRevision` is required, but it is not a revision lock.** It is the base the service computes
  conflicts against, not a value it insists still be current: an apply quoting a stale base whose
  edits touch nothing that moved since is **accepted**, at a new revision, with `conflicts: []`.
  Verified against 3.1.0 — an insert quoting `baseRevision: 0` on a design already at revision 1
  committed as revision 2. So quote the revision `ui_builder_get_design` returned, because that is
  what makes the conflict report meaningful; do not expect a stale one to be refused for you.
- **What the service does refuse and deduplicate.** Re-sending an `operationId` already applied
  answers `idempotentReplay: true` at the revision it first committed, rather than applying twice —
  so `operationId` is yours to choose and makes a retry safe. A *new* operation reusing an existing
  node id is rejected outright: `code: invalidCommand`, `node id is blank or already used`, naming
  the `operationIndex` and `nodeId`.
- **The tools are absent unless the box serves a builder** (`--ui-builder-dir`) — absent rather than
  listed-and-failing, because listed-and-failing tells an agent the server can do something it
  cannot. `tools/list` needs no grant, so checking costs one unauthenticated call rather than a
  grant request. `preview.coo.ee` served `3.0.0` when this landed and advertised none of them; the
  builder's Design API was already up there (`/api/ui-builder/v1/identity` answers `401`, not
  `404`), so the gap is the deploy, not the box —
  [compose-preview-server#300](https://github.com/yschimke/compose-preview-server/pull/300) is
  newer than the running image.

## Kotlin

- ktfmt Google style, 100 columns. `./gradlew ktfmtFormat`.
- Kotlin block comments **nest**, so `/*` inside a KDoc opens a nested comment and swallows the rest
  of the file. Write `M3.sys.light.primary`, not `M3/sys/*`. This has bitten this repo already.

## Git

- **`main` is protected — every change goes through a PR.** The `Protect Main` ruleset requires a
  pull request (0 approvals) with all CI checks green, and squash is the only merge method, so
  `main` stays one commit per change. Branch names are `agent/…`.
- Conventional commit subjects (`feat:`, `fix:`, `docs:`, `chore:`). The squash commit is built from
  the **PR title**, so the PR title is what lands on `main` — write it as the commit subject.
- **Never attribute a commit to an AI agent** — no `Co-authored-by:` trailer naming an agent, and
  no agent author/committer identity. Links to an agent session and the
  `_Generated by [Claude Code]_` footer are fine; they don't claim authorship. Enforced by the
  hooks in `.githooks/` (install with `scripts/install-git-hooks.sh`).

## Dependencies

- **Renovate owns the version bumps; don't hand-bump.** `.github/renovate.json` automerges anything
  that is not a major once CI is green, so a bump you make by hand is a PR Renovate would have
  opened, reviewed and landed on its own. Change the config instead when the policy is wrong.
- Two groups are deliberately **not** automerged: **majors**, and **Compose Multiplatform** (with its
  independently-versioned material3 artifact). Both change what the catalog renders, and the render
  is the product — a human reads the visual diff before it lands.
- `compose-ai-tools` is the exception in the other direction: the CLI, the Gradle plugin marker, the
  `preview-annotations` coord and the pinned CI action ref are one release and move together in a
  single PR, unscheduled and automerged. A skew between them breaks preview discovery outright.
- Repository settings — squash-only merges, auto-merge, and the `Protect Main` ruleset that
  automerge depends on — are applied by `scripts/setup-repo-protection.sh`. They need an admin
  token, so no workflow (and no agent session) can set them; the script is the record of what they
  are meant to be, and re-running it repairs drift. `DRY_RUN=1` prints without writing.

## Verifying a change

```sh
./gradlew :catalog:assemble :catalog:composePreviewDiscover test ktfmtCheck
scripts/design-map.sh
node --test scripts/*.test.mjs
```

`composePreviewDiscover` is the real contract: it is what turns the annotations into the published
inventory. A component that compiles but is not discovered vanishes from the sheet silently.
