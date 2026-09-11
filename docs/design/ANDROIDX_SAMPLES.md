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
subtree into `samples-catalog/src/main/kotlin/…/upstream/`, preserving the Apache-2.0 headers
verbatim, and writes a provenance file recording repo / SHA / paths / date beside a `NOTICE`.

One thing to verify at implementation time: `android.googlesource.com` was unreachable from the
sandbox this strategy was researched in (egress policy), so the `+archive` subtree-tarball endpoint
could not be exercised. It is the cleanest option if a CI runner can reach it; the GitHub mirror at a
tag is the fallback. Either way the pin is a SHA, so the choice of transport is not load-bearing.

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

**One parsing caveat, found while prototyping.** A naive "KDoc block, then the next line" scan
mis-attributes about 12% of blocks — 31 of the 260 that carry a `@sample` in material3 1.5.0-alpha27.
Two causes: a `@sample` on a *parameter* KDoc (the next line is the parameter, not the declaration),
and `@Deprecated(…)`-annotated overloads (the next line is `message = …`). The parser needs a
balanced-paren skip over annotation entries, and must attribute a parameter-level `@sample` to its
enclosing declaration. Worth naming here because the naive version looks like it works — it resolves
178 declarations and only lies about a tenth of them.

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

**3. The back-link is the one piece that needs upstream work.** The component record carries
`sourceFile` / `sourceModule` / `bodyLine` for the source link and `parallel` for the one sibling;
there is no per-component "related catalog" surface. A link *from* `m3-catalog` *into* `m3-samples`
therefore needs a small addition in compose-ai-tools, not configuration here.

Recommendation: a generic `related: [{ system, componentId, label }]` on the component record, plus a
"Samples" affordance in the server's component view. A **list**, not a second `compareWith`, and the
reason is `remote-m3`: it has already spent its single `compareWith` on `wear-m3-catalog`, so a
pairwise mechanism cannot also point it at `wear-m3-samples`. Per this repo's AGENTS.md, a capability
any catalog could want belongs upstream as a generic input — never as a forked pipeline here.

## The CI job

A second `uses:` block against `design-artifacts-reusable.yml`. No forked pipeline; everything needed
is a generic input:

- `system: m3-samples`, `spec: samples-catalog/catalog.spec.json`, `module: ':samples-catalog'`
- `cli-version: catalog` + `catalog-key: composePreviewPlugin`, as the existing job does
- `desktop-render: true` here; `false` in the Wear repo
- `split-per-preview: false`, for the reasons the existing job's comment already sets out
- no `render-shards` to begin with — ~308 base previews is nowhere near the 4133 that forced four
  shards on the main sheet, and a shard is only worth its ~150s of setup once the marginal work
  dominates

Plus the `changes` / `Scope` job, ported from `wear-m3-catalog`, so a `catalog/**` push does not drag
the samples sheet through a render it cannot see, and vice versa. It must fail safe the same way: no
resolvable change set means render both.

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

That last triple is what `scripts/samples-drift.mjs` should assert is `308 / 0 / 0`.
