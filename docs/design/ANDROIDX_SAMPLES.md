# Importing the AndroidX samples as a linked catalog

A strategy, not an implementation. It answers four questions:

1. where the sample **source** comes from, given that AndroidX does not publish it;
2. which **version** to import, and why the phone side and the Wear side answer that differently;
3. how the `@sample` **KDoc** produces a sample → component mapping without anyone hand-writing one;
4. how the result **links** to the catalogs that already exist — here, in
   [`yschimke/wear-m3-catalog`](https://github.com/yschimke/wear-m3-catalog), and on preview.coo.ee.

The companion copy in `yschimke/wear-m3-catalog` covers the Wear and Remote Compose halves. The two
documents are deliberately near-identical; where they differ it is called out.

## What it produces

One new system per repository, published by its own job on the existing `design-artifacts.yml`:

| System | Repository | Module | Renderer |
| --- | --- | --- | --- |
| `m3-samples` | this repo | `:samples-catalog` | CMP desktop (Skiko), matching `:catalog` |
| `wear-m3-samples` | `yschimke/wear-m3-catalog` | `:samples-catalog` | Robolectric, matching that repo's `:catalog` |

`remote-m3` gets no samples catalog of its own — Remote Compose has no upstream sample corpus worth
importing. It links *into* `wear-m3-samples`, which is the case that shapes the linking design below.

Two catalogs from one repository is not new ground: `wear-m3-catalog` already publishes
`wear-m3-catalog` and `remote-m3` from one workflow, with a `changes` job so a push that moves one
does not spend a runner re-rendering the other. This repository has only the single `publish` job
and needs that scoping job added.

## What is built

This document was written as a strategy and is kept as the record of one, with the decisions marked
where measurement has since changed them. What exists in the tree today:

| | |
| --- | --- |
| `samples/import.json` | the pins — repo, and per library a **commit SHA**, subtree paths and the AndroidX version it tracks |
| `scripts/import-samples.mjs` | the vendoring importer (blobless sparse clone), `--check` to re-import and diff |
| `samples/patches/`, `samples/quarantine.json` | the fix and gap mechanisms, both checked |
| `scripts/sample-map.mjs`, `sample-map.json` | the `@sample` reader and its output — 166 APIs, 320 samples |
| `scripts/samples-drift.mjs` | the pins' fingerprint check, passing `308 / 0 / 0` and `12 / 0 / 0` |
| `scripts/samples-spec.mjs`, `samples-catalog/catalog.spec.json` | the generated inventory — **245 components in 104 groups** |
| `:samples-catalog` | the module that compiles and renders them: 40 files vendored, 8 quarantined, 2 patches |
| `design-artifacts.yml` → `m3-samples` | the publish job, plus the `changes`/Scope job it made necessary |

**No `@Preview` wrappers here, and that is a difference from the Wear repo rather than an omission.**
298 of the 317 `@Sampled` functions in `androidx.compose.material3.samples` already carry `@Preview`
upstream, so discovery finds them directly. `androidx.wear.compose.material3.samples` carries it on
34 of 170, which is why that repo generates 115 wrappers and this one generates none. The difference
is the two teams' annotation habits, not anything about the platforms.

Not built yet: the `catalogs.json` registration on preview.coo.ee, the server's "Samples"
affordance, and the weekly `samples-refresh.yml`.

## A second library: material3-adaptive

The manifest started as one pin and is now a **list, one entry per library**, because a sample tree
belongs to the artifact whose KDoc points at it and those artifacts move on their own cadences.

`compose/material3/adaptive/samples` is a single file, `ThreePaneScaffoldSample.kt`, carrying 13
`@Sampled` functions of which 11 are `@Preview` upstream. CMP `adaptive-layout:1.3.0-beta02` — the
artifact `:catalog` and `:samples-catalog` already compile against — references 12 of them, and
AndroidX `adaptive-layout:1.3.0-beta02` references exactly the same 12: `12 / 0 / 0`, the same clean
match material3 got.

**The ref had to be its own, and that is the finding that shaped the schema.** adaptive-layout
1.3.0-beta02 was published on 2026-05-19; material3 1.5.0-alpha22 on 2026-06-17. Vendoring the
adaptive tree at material3's commit compiles it against three weeks of newer adaptive API: the
measured failure is `AnimatedPane(shape = …)`, an overload CMP 1.3.0-beta02 does not have. So each
library carries its own `ref`, resolved by the same Last-Modified rule, and its own
`composeMultiplatformVersionRef` naming the `libs.versions.toml` key holding the CMP version to
compare against.

### What the file cost

Two patches, and they are the first this repo has needed:

- **`NavItemData` implements `android.os.Parcelable`.** It is the content-key type nearly every
  sample in the file uses, so it cannot be quarantined away — and quarantine is per FILE, which for
  this library means all 13 samples or none. Upstream makes it `Parcelable` to survive process
  death; a preview renders one frame and never saves.
- **The two navigation samples do not compile.** `ListDetailWithNavigation2Sample` calls
  `NavigableListDetailPaneScaffold`, which is **absent from CMP's `adaptive-navigation`
  1.3.0-beta02** — that artifact ships `ThreePaneScaffoldNavigator.kt` and `BackNavigationBehavior.kt`
  and nothing else, so this is a missing API rather than a missing dependency, and adding
  `navigation-compose` would not fix it. `ListDetailWithNavigation3Sample` fails on a nav3 signature
  difference (`rememberNavBackStack`'s first parameter). The first IS named by the CMP artifact's
  KDoc, so it stays in `sample-map.json` as a mapped-but-not-rendered sample, which the spec
  generator already reports and skips.

`adaptive-navigation` joins the module's dependencies for the navigator every sample drives.
`adaptive-navigation3` was tried and removed: after the second patch nothing referenced it.

### Two things the import exposed in this repo's own scripts

Both were latent bugs that material3 alone never triggered, and both are now covered by tests:

1. **`enum class` attribution.** `sample-map.mjs` read the identifier after a declaration keyword,
   so `enum class DockedEdge` produced an API named literally `class` — and a catalog group called
   "class". material3 has no `@sample` on an enum; adaptive-layout's `DockedEdge` does.
2. **What counts as renderable.** `samples-spec.mjs` matched `fun name(`, which missed the generic
   and extension samples. Widening it surfaced the real rule, which is narrower than `@Sampled` +
   `@Preview`: **discovery can only invoke a zero-arg, non-extension, Unit-returning composable.**
   - `PaneExpansionDragHandleSample` is an extension with a parameter: discovery does not find it
     and rendering produces no PNG at all.
   - `levitateAsDialogSample`, `levitateAsBottomSheetSample` and `reflowAdaptStrategySample` return
     a `ThreePaneScaffoldNavigator<T>`. A composable that returns a value is a state factory and
     draws nothing — each renders as a **1x1 blank**. Upstream annotating them `@Preview` is
     reasonable there and indefensible here, so they are discovered, rendered and left as bundle
     orphans rather than published as cards claiming a picture they never had.

   Applying that rule changed the material3 count by **zero**, which is the evidence that it is a
   rule and not a workaround.

So of 13 adaptive samples: 2 patched out, 3 blank state factories, 1 unreachable extension, and
**5 published**.

### Known, and not fixed here

Those 5 render as only **2 distinct images**: the three `ListDetailPaneScaffold*` samples are
byte-identical to each other, as are the two `SupportingPaneScaffold*` ones. Upstream's `@Preview`
carries no `widthDp`, so every one of them renders at the default width, where the scaffold collapses
to a single pane and the extra/levitated pane that distinguishes the sample never appears. An
adaptive layout rendered at one width is a picture of the thing the sample is not about.

This is NOT the case `duplicate-renders.json` covers — that is about variant cells of one kit
sticker publishing one picture under two names, and its audit runs against `:catalog`'s render, not
this module's. Fixing it needs a width axis these previews do not carry, which means a generated
wrapper preview — the machinery this repo deliberately does not have, and the deliverable of the
`:ui-samples-catalog` work. Recorded here rather than worked around.

## Acquisition: vendor a pinned subtree

**The samples are not published as artifacts.** Checked against Google Maven directly — every
candidate coordinate 404s:

```
androidx/compose/material3/material3-samples/maven-metadata.xml        404
androidx/wear/compose/compose-material3-samples/maven-metadata.xml     404
androidx/compose/foundation/foundation-samples/maven-metadata.xml      404
```

So a dependency is not available, and the source has to come out of the AndroidX tree. Three ways to
get it, and only one of them is right:

- **A git submodule** — no. `platform/frameworks/support` is enormous; every clone in CI pays for it.
- **A fetch at render time** — no. The published sheet stops being reproducible and a review of a
  sample change has no diff to look at.
- **A vendored subtree, pinned to a commit SHA, committed here** — yes. It makes the render
  reproducible, the upstream bump a reviewable diff, and the licence obligation discharged in the
  obvious place.

`samples/import.json` pins the upstream repository, the **commit SHA** (never a branch), the subtree
paths, and the library version the module compiles against. `scripts/import-samples.mjs` fetches that
subtree into `samples-catalog/src/main/kotlin/upstream/`, preserving the Apache-2.0 headers
verbatim, and writes a provenance file recording repo / SHA / paths / date beside a `NOTICE`.

**Under the directories the samples' own `package` names** — `upstream/androidx/compose/material3/
samples/…`, the ordinary Kotlin layout, derived from the manifest path by splitting it at the
module's source root. Not cosmetic: discovery resolves a preview back to its file by asking which of
the module's sources *ends with* the package-qualified path it reads off the compiled class.
Vendored flat, none did, so every sample's `sourceFile` fell back to that package path — a string
naming no file in this repository. Nothing failed; two surfaces just went quiet. The usage panel
answered `no-usage`, and the page's "source" link 404'd on GitHub. For a catalog whose entire
subject is *the code*, that is the defect that matters most and the one least likely to be caught by
a build.

**Settled, and not by the transport this section first guessed at.** Neither
`android.googlesource.com`'s `+archive` endpoint nor a codeload tarball is used: the first is
unreachable behind some egress policies and the second 403s behind a proxy, and both need a
directory listing to know what to fetch. What `scripts/import-samples.mjs` actually does is a
**blobless sparse clone**:

```
git clone --filter=blob:none --no-checkout --depth 1 <repo> <cache>
git sparse-checkout set <paths…>
git checkout <ref>
```

Only the trees, and only the blobs under the sparse paths, are ever transferred — **13 MB** for the
two sample subtrees, measured, against a multi-gigabyte whole-tree clone. It needs no API token and
no directory listing, which is what makes it behave identically on a CI runner and a laptop, and it
takes the pinned SHA directly.

## Which version — the question, settled

The phone catalog compiles against Compose Multiplatform
(`org.jetbrains.compose.material3:material3`, currently `1.12.0-alpha03`), while the samples are
written against AndroidX (`androidx.compose.material3:material3`, currently on the `1.5.0-alpha2x`
line). So there is a real choice: **import the historic AndroidX samples that match what CMP
actually ships, or add an `androidMain` source set and import the latest Android-only ones.**

It looked like a trade-off until it was measured. Both artifacts publish sources jars, and both carry
the `@sample` KDoc, so the set of sample FQNs each version references is a cheap fingerprint. CMP
material3 `1.12.0-alpha03` references 308 unique samples. Against the AndroidX line:

| AndroidX material3 | shared with CMP | only in CMP | only in AndroidX |
| --- | --- | --- | --- |
| 1.4.0 | 178 | 130 | 0 |
| 1.5.0-alpha10 | 283 | 25 | 3 |
| 1.5.0-alpha15 | 297 | 11 | 3 |
| 1.5.0-alpha19 | 305 | 3 | 3 |
| 1.5.0-alpha20 | 307 | 1 | 1 |
| 1.5.0-alpha21 | 308 | 0 | 1 |
| **1.5.0-alpha22** | **308** | **0** | **0** |
| 1.5.0-alpha27 | 297 | 11 | 22 |

**CMP 1.12.0-alpha03 matches AndroidX material3 1.5.0-alpha22 exactly.** Not approximately — the two
sets are equal, in both directions. The "historic" version is not a guess and not a compromise: it is
*derivable from the artifacts themselves*, and derivable again, automatically, on every bump.

That reframes the question. The historic pin costs nothing, because the historic version is current.
What the `androidMain` alternative would buy is the 22 samples AndroidX has added since alpha22 —
about 7%, and reading the list (`StandardListItems` / `SegmentedListItems` for
`OneLineListItem` / `TwoLineListItem`, `ExitUntilCollapsedLargeFlexibleTopAppBar` for its
`CenterAligned` spelling) most of them are renames of samples the historic pin already has, not new
material. Against that it would cost:

- **The live-render lane.** `catalog/build.gradle.kts` opens by explaining that `:catalog` is
  deliberately not an Android module, so the compose-preview plugin routes it to the Desktop renderer
  and preview.coo.ee can hold a live Compose session against the published bundle. An AGP
  `:samples-catalog` renders through Robolectric and can never do that.
- **Comparable pixels.** The whole point of the link is a side-by-side: the kit cell beside the call
  site. A Robolectric-rendered sample next to a Skiko-rendered sticker puts a *renderer* difference
  into a comparison that is supposed to be about API usage — the one confound the compare page must
  not have.
- **A second Compose line in the repository.** `wear-m3-catalog`'s `:remote-catalog` exists as a
  separate module precisely so an alpha line cannot leak into the catalog that reproduces the kit.
  Reintroducing that split here for 22 renamed samples inverts the trade it made.

**Decision: historic pin on the phone side. Import the AndroidX samples tree at the version whose
`@sample` fingerprint matches the CMP artifact `:samples-catalog` compiles against — 1.5.0-alpha22
today — and make that match a checked invariant rather than a comment.**

`scripts/samples-drift.mjs` recomputes both fingerprints and fails when the pinned AndroidX version's
sample set stops matching the CMP artifact's. That failure *is* the bump signal, and it is the honest
one: it fires when CMP moves, which is when the samples need to move, rather than when AndroidX moves,
which is most weeks and means nothing here.

Set equality of `@sample` FQNs is a fingerprint, not a proof that two source trees are identical. It
is the right tool for *choosing* the ref cheaply. The real gate is downstream and much stronger: a
generated wrapper calling a sample that does not resolve against the CMP artifact does not compile.

**The Wear side has no version question at all**, which is worth stating because it is easy to assume
the problem is symmetric. `wear-m3-catalog:catalog` is already an Android module compiling against
the real `androidx.wear.compose:compose-material3` (`1.7.0-beta02`), whose sources jar is a flat
`androidMain` tree of 136 files carrying 170 unique samples over 235 tag sites. The import there pins
to the same version the catalog compiles against, full stop. No fingerprint, no drift check, no
source-set decision. The dilemma is a CMP artifact, not a samples artifact.

## Transform, and where "small fixes" live

Three mechanical stages, all idempotent and re-runnable, all producing reviewable diffs:

1. **Rewrite.** Strip `@Sampled` / `androidx.annotation.Sampled`; rewrite the Android-only imports the
   desktop side cannot take (resource lookups, `LocalContext`, the Android tooling preview package).
2. **Wrap.** Generate a `@Preview` + `Sticker { }` wrapper per sample into a *separate generated file*,
   so the vendored source stays byte-identical to upstream except for the recorded patches. This is
   mechanical rather than clever because AndroidX enforces that a `@Sampled` function is a zero-arg
   composable.
3. **Patch.** `samples/patches/*.patch`, applied by the importer after download, each carrying a
   one-line reason.

A sample that genuinely cannot be imported — it needs an Activity, a permission, a real
`Context` — goes in `samples/quarantine.json` with a reason. A test fails when a patch stops applying
cleanly **or when a quarantined sample becomes importable**, so the gap cannot silently rot into a
permanent exclusion nobody revisits. This is the same declared-and-checked-gap contract
`kit-unauthorable.json` already carries in this repo, and it is the answer to "make small fixes when
needed": a fix is a patch with a reason, never an untracked edit to a vendored file.

The AndroidX source-set split is a useful predictor of how much of that will be needed. material3
1.5's own sources are 247 `commonMain`, 20 `commonStubsMain`, 6 `jvmAndAndroidMain`; CMP's are 251
`commonMain`, 97 `skikoMain`, 6 `jvmAndAndroidMain`, 2 `desktopMain`. The API surface the samples call
is overwhelmingly common, so the patch set should be small — but it is a prediction, and the
quarantine list is what keeps it honest if it turns out to be wrong.

## Mapping: the KDoc is the source of truth

`scripts/sample-map.mjs` reads the sources jar **of the exact artifact the module compiles against**,
parses the KDoc, and emits a committed `sample-map.json` keyed by Compose API symbol:

```json
{ "api": "Button", "samples": ["…samples.ButtonSample", "…samples.ButtonWithIconSample"] }
```

Reading it from the compiled-against artifact rather than from the imported tree is the point: it
makes the mapping describe the API this catalog actually renders. Both artifacts publish what is
needed (`material3-desktop-1.12.0-alpha03-sources.jar`, 1.0 MB, 418 tag sites;
`compose-material3-1.7.0-beta02-sources.jar`, 440 KB, 235 tag sites).

Committed and regenerate-and-diff tested, the same contract `design-map.json` and
`CatalogMatrixAnnotations.kt` already have here.

**Why it is a scanner and not a regex.** A naive "KDoc block, then the next line" scan
mis-attributes about 12% of blocks — 31 of the 260 that carry a `@sample` in material3
1.5.0-alpha27. The confirmed cause is an **annotated declaration**: `@Deprecated(message = "Use
overload with \`shape\`", level = DeprecationLevel.WARNING)` sits between the KDoc and the `fun`,
spans lines, and contains both parentheses and a string, so "the next line" is `message = …`. So the
parser tracks strings (raw `"""` included), comments and paren depth, and skips modifiers and
annotations with balanced parens.

**Correction to this document's first draft.** It also named "a `@sample` on a *parameter* KDoc" as
a second cause, citing `DatePicker`'s `locale: CalendarLocale,` line. That was wrong. The block in
question documents the `DatePickerState` factory and its `@sample` is at block level — the
line-based prototype simply mis-landed on a `@param` line further down, and `DatePickerState` is the
correct owner. Across material3 1.5.0-alpha27, the CMP artifact and wear-compose 1.7.0-beta02,
**zero** blocks resolve through the parameter path. `sample-map.mjs` still handles it, and is tested
for it, as defence rather than as a fix for anything observed.

**Measured coverage**, which is what the claim rests on rather than a prototype: 260 of 260 blocks
attributed and 319 of 319 unique samples reached on material3 1.5.0-alpha27; 308 of 308 on the CMP
artifact; 170 of 170 on wear-compose. Nothing dropped anywhere.

### Joining a sample to a catalog component

`sample-map.json` is keyed by Compose API symbol; the catalog is keyed by `componentId`. The catalog
side already resolves the bridge: discovery records `previews[].targets[]` — the production composable
each preview renders — which is what `figma-code-connect-target.mjs` consumes, with the spec's
`component` field as an override. So the join is `componentId → target functionName → @sample list`.

Expect the inference to be weak here, because it walks for a *project-local* `@Composable` call and
`Button` is a library call. The durable fix is to declare it: an `api = "Button"` field on
`@CatalogComponent`, added **upstream in compose-ai-tools**, per this repo's standing rule that a pile
of mapping config is a signal of a missing annotation. The spec's existing `component` override works
as the interim bridge and needs no upstream change to get started.

## Linking, in three layers

**1. Id parity — do this first, it makes the other two cheap.** A sample sticker that demonstrates a
catalog component takes the *same* `componentId` (`Button/Filled`), with each individual sample folded
in as a `@CatalogVariant`. Sample-only material takes its own ids grouped by source file. Every
cross-link is then an identity mapping, and the samples catalog is browsable in the taxonomy readers
already know.

**2. `compareWith` + `parallel` — already built, use it as-is.** `m3-samples` declares
`compareWith: { system: "m3-catalog", repo: "yschimke/m3-catalog" }` and each component carries
`parallel` naming its counterpart. `ServeParallelPairing` ranks counterparts by kit node → variant
coordinates → canonical fallback; samples publish no kit node, so pairing lands on `CANONICAL`, which
is exactly the right reading: the kit cell beside how you call it. The server already states the basis
rather than pretending the sibling drew the cell, so nothing needs to change for this to read
honestly.

**3. The back-link needed upstream work, and it has landed.** The component record carried
`sourceFile` / `sourceModule` / `bodyLine` for the source link and `parallel` for the one sibling,
and nothing for a second relationship — so a link *from* `m3-catalog` *into* `m3-samples` was not
expressible at all.

It is now. `related: [{ system, componentId, label }]` is a generic list on the component record, a
**list** and not a second `compareWith` precisely because `remote-m3` has already spent its single
`compareWith` on `wear-m3-catalog` and no pairwise handle could also reach the samples. It carries no
parity semantics: `parallel` says two renders are pictures of one cell and should be diffed, `related`
says only that another catalog is worth looking at from here, so a samples catalog is never dragged
into a comparison that scores it.

Three pieces, all merged, tracked by
[compose-ai-tools#5398](https://github.com/yschimke/compose-ai-tools/issues/5398):

| Piece | Where |
| --- | --- |
| The spec field, validation, and the stamp onto `catalog.json` | compose-ai-tools#5399 |
| `@CatalogComponent(related = […])` | compose-preview-daemon#62 |
| Discovery reads it; the export's inventory parses it | compose-ai-tools#5401 |

Two consequences for this repo. A component can declare its links in `catalog.spec.json` **today**;
the annotation spelling waits on the `composeai-preview-daemon` pin moving to a release that carries
it. And the entry form is `"<system>=<componentId>=<label>"`, where an empty `<componentId>` means
"the same id as mine" — so `related = ["m3-samples==Samples"]` is the ordinary spelling for the
id-parity case above, doubled `=` and all.

What is still missing is the **server affordance**: the "Samples" link in the component view. Until a
catalog actually publishes a `related` link there is nothing real for it to render, which is an
argument for building it after `:samples-catalog` rather than against a fixture.

## The CI job

**Built.** A second `uses:` block against `design-artifacts-reusable.yml`. No forked pipeline;
everything it needs was already a generic input:

- `system: m3-samples`, `spec: samples-catalog/catalog.spec.json`, `module: ':samples-catalog'`
- `cli-version: catalog` + `catalog-key: composePreviewPlugin`, as the existing job does
- `desktop-render: true` here; `false` in the Wear repo
- `split-per-preview: false`, for the reasons the existing job's comment already sets out
- `render-shards: 1`, where the kit sheet takes four. **245** base previews, not the ~308 this
  section first projected — the gap is the mapped samples carrying no `@Preview` upstream, which
  nothing can render. Sharding pays on the kit sheet because its previews multiply across mode and
  device axes; these are one capture each.

Plus the `changes` / `Scope` job, ported from `wear-m3-catalog`. That job is *new* with the samples
sheet: with a single catalog there was nothing to scope and the `paths:` filter was the whole
answer. It fails safe the same way — no resolvable change set means render both.

**One input is load-bearing and not obvious: `design-map-command`.** The kit job passes none, which
is correct for a single-catalog repo — the committed `design-map.json` *is* `:catalog`'s. That
default stops being right the moment a second system publishes from the same checkout: an empty
input would publish `:catalog`'s Figma mappings under the samples sheet's name, every handle naming
a `catalog/src/…` file that module does not contain. So the samples job passes a command that
projects an **empty** map. That is the truthful answer, not a placeholder — these are AndroidX's
call sites, not a reproduction of a published kit, so no sample has a node to be scored against.
Same reason the job carries no `figma_token`, no `reference-cache-branch` and no
`reference-backdrop`: **the samples sheet has no design-parity lane at all.** The reusable workflow
warns that the system "will publish 0% coverage"; 0% is correct, and a warning saying so beats a
number borrowed from the sheet next door.

`ci.yml` runs the build-free spec pre-flight over this spec too, beside the existing one. It reports
**266 `@Preview` functions discovered against 245 in the catalog**, and that gap of 21 is expected
and of two kinds. Eighteen are upstream previews that are not `@Sampled` — `AllShapes`,
`LeadingIconTabs`, `LegacySliderSample`, the scrolling-tab demos — demo previews living in the sample
files that no `@sample` KDoc points at. `@Sampled` is what makes a function a sample rather than
something a sample sits beside, which is the same rule that keeps `FancyIndicator` out while
`FancyIndicatorTabs` is in. The other three are the adaptive state factories described above, which
are `@Sampled` and `@Preview` and still draw nothing. They all render; they land as bundle orphans
rather than as catalog cards. Worth revisiting only with a reason to widen what "a sample" means,
not as a fix.

A weekly `samples-refresh.yml` re-runs the importer and the drift check and opens a PR when the
vendored tree or `sample-map.json` moves — the cadence `figma-pages.yml` and `design-parity-import.yml`
already use, and the right shape for an input that changes on someone else's schedule rather than on a
source edit here.

## Serving on preview.coo.ee

In `compose-preview-server`, `deploy/preview.coo.ee/catalogs.json`:

- two entries, `m3-samples` and `wear-m3-samples`, each with
  `attributionRepos: ["androidx/androidx"]` — the `android/compose-samples` entries are the precedent;
- a new group (`androidx-samples`, heading `androidx/androidx samples`) rather than `design-systems`,
  so the front page keeps the two reference design systems at the top, which `design-systems`'
  `priority: 100` exists to guarantee;
- no `sites` entry — these want no hostname of their own.

`producers.json` needs **no change**: `yschimke/m3-catalog` and `yschimke/wear-m3-catalog`
`design-artifacts/*` are already trusted. That is convenient and worth a second look rather than a
shrug, because on this box trust is eligibility for server-side execution under
`SERVE_ALLOW_RENDER_TRUSTED=1`, and a live bundle here would mean executing vendored third-party
Compose. **Start with `publish-live-bundle: false`** — baked stickers only — and turn live rendering
on as its own deliberate change, with the CMP/desktop decision above being exactly what keeps that
option open.

## Risks

- **Licence and attribution.** Apache-2.0 headers preserved verbatim, `NOTICE` and a provenance file
  in the vendored tree, `attributionRepos` on the served catalog. Non-negotiable, and cheap.
- **Fingerprint ≠ identity.** See above: the `@sample` set match chooses the ref; the compiler is the
  gate.
- **Patch-set creep.** If the patch set grows past a handful, the desktop assumption is wrong and the
  decision above should be revisited with real numbers rather than defended. The quarantine list makes
  that visible instead of gradual.
- **Render budget.** ~308 and ~170 base previews before mode axes, against a main sheet at 4133. Not a
  concern now; it becomes one if samples grow variant matrices, which they should not — a sample has
  one right way to be drawn.

## Reproducing the evidence

Everything above is measured, not recalled. The fingerprint table:

```sh
# CMP's sample set
curl -sO https://repo1.maven.org/maven2/org/jetbrains/compose/material3/material3-desktop/1.12.0-alpha03/material3-desktop-1.12.0-alpha03-sources.jar
unzip -q material3-desktop-1.12.0-alpha03-sources.jar -d cmp
grep -rho '@sample [A-Za-z0-9_.]*' cmp | sed 's/@sample //' | sort -u > cmp.txt

# any AndroidX version's
curl -sO https://dl.google.com/dl/android/maven2/androidx/compose/material3/material3/1.5.0-alpha22/material3-1.5.0-alpha22-sources.jar
unzip -q material3-1.5.0-alpha22-sources.jar -d ax
grep -rho '@sample [A-Za-z0-9_.]*' ax | sed 's/@sample //' | sort -u > ax.txt

comm -12 cmp.txt ax.txt | wc -l   # shared
comm -23 cmp.txt ax.txt | wc -l   # only in CMP
comm -13 cmp.txt ax.txt | wc -l   # only in AndroidX
```

That last triple is what `scripts/samples-drift.mjs` asserts is `308 / 0 / 0`, and does today.
