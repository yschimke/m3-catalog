# Parallelising preview generation

`design-artifacts.yml` renders every preview in a single `bundle pack` invocation, and that step
grows linearly with the preview count until it hits `render-timeout` — and then the job timeout.
Sharding the render across parallel jobs is the fix, and the machinery for it is built and tested.

Parallelising the *authoring* does not help. It makes this worse: every component another session
adds lengthens the same serial render. That is a different document
([`PARALLEL_SWEEP.md`](PARALLEL_SWEEP.md)) solving a different problem.

> **Status: built and in use.** [`design-artifacts.yml`](../.github/workflows/design-artifacts.yml)
> currently passes **`render-shards: 4`**. §1 has the measurement that governs that number and the
> thresholds for changing it — at today's sheet size the model puts the optimum below 2, so this is
> worth re-checking against a real sharded run.

## 1. What the render costs

Measured on CI, decomposed from the task timings the `Render catalog bundle` step prints
(1147 previews, `ubuntu-latest`):

| | fixed (configure + compile + discover + pack) | `composePreviewRender` | semantics capture | full sheet |
| --- | --- | --- | --- | --- |
| 1147 previews | 100 s | 232 s → **0.202 s/preview** | 211 s → 0.185 s/preview | **543 s** |

```
render_seconds ≈ 100 + 0.39s × previews
```

Two sibling runs agree: 568 s and 548 s for the same step. The marginal cost is small because
[compose-ai-tools#3548](https://github.com/yschimke/compose-ai-tools/pull/3548) moved captures onto
a long-lived warm renderer; before it, each capture forked a JVM and the marginal cost was 2.55 s.

**Per-shard fixed cost, measured**: job prefix ~25 s + a separate `Discover previews` step 87 s +
the render step's own Gradle prologue ~20 s + upload/font-cache ~15 s ≈ **150 s**, plus a ~85 s
merge/generate/publish job.

```
T ≈ 150s + 0.39s × previews/N + 85s        (N > 1)
```

| Previews | N=1 | N=2 | N=4 | N=6 | N=8 |
| --- | --- | --- | --- | --- | --- |
| 1147 (today) | **9.1 min** | 7.6 | 5.8 | 5.2 | 4.9 |
| 2500 | 17.9 | 12.1 | 8.0 | 6.6 | 6.0 |
| 5000 | 34.2 | 20.2 | 12.0 | 9.3 | 8.0 |

**At this size the optimum is below 2**, which follows from cheap marginal work against an unchanged
fixed cost. Sharding today's sheet four ways buys ~3 min of wall clock for three extra runners and
~8 extra runner-minutes, while a serial render would sit at under a quarter of its `render-timeout`.

**When to raise it.** Around **3000 previews** the split starts to be worth its runners; near
**6000** it stops being optional, because that is where a serial render meets `render-timeout`.

Two honest caveats. The ~85 s merge tail was measured with near-empty shard artifacts, so a real
merge of N full bundles costs more — which makes sharding look slightly *better* here than it is.
And the N>1 columns are modelled, not measured: **shard balance on a real sheet has never been
measured**, so the first honest sharded run should check it before anyone trusts them.

## 2. The two mechanisms it rests on

**Restricting what a shard renders — already existed.** `bundle pack` forwards
`composePreview.filter` (a preview-function name pattern, via
`ORG_GRADLE_PROJECT_composePreview.filter`) and `--exclude-preview-id` down to the render.
`--exclude-preview-id` is the load-bearing one: *"Excluded previews stay listed in the bundle
(addressable, just without a baked PNG)."* So every shard emits a structurally identical bundle —
same `previews.json`, same manifest, same re-render classpath — differing only in which
`previews/<id>.*` slots are filled.

**Merging shard renders — a new primitive, because `repack` cannot do it.** `bundle repack` merges
PNG + `figma.svg` only, and only into slots the target already has: `repackRethemedPreviews` swaps
matching filenames in, a render with no matching slot is reported *unmatched and dropped*
(`BundleRepackTest` pins that), and the JSON sidecars are preserved verbatim rather than merged.
Against a shard base every other shard's previews are unmatched, and any that did land would arrive
without their `.semantics.json`, which the completeness gate fails. So:

```
compose-preview bundle merge <base.png> <shard.png>… -o <out.png>
```

unions the per-preview artifacts — the raster, its `.semantics.json` / `.layout.json` /
`.fonts.json` / `.figma.svg` / `.catalog.json` / `.overrides.json` sidecars, the nested
`figma-raster/` crops, `ir/<id>.rc`, `extensions/<id>.json` — adding the slot where the base has
none, base-wins on collision, earlier shards over later ones.

## 3. The shape, as built

```
          ┌── shard 1: discover → plan → bundle pack → upload ──┐
 matrix ──┼── shard 2: …                                        ┼── merge ── generate ── publish
 [1..N]   └── shard N: …                                        ┘
```

1. **Matrix.** `render-shards: N` becomes `[1 … N]`. No checkout, no toolchain — Actions has no
   range expression.
2. **Each shard discovers and plans for itself.** `compose-preview list --json` runs
   `composePreviewDiscover`; that compile is shared with the shard's own render. A central plan
   would have to compile before it could discover, adding its whole prologue to the critical path
   instead of the discovery alone inside each shard's.
3. **Partition by preview id, never by function name.** One function expands to 30 previews (the
   icon-button matrix); a name split is wildly unbalanced. Sort the ids, then round-robin — balanced
   and deterministic, so independent shards agree without talking to each other.
4. **Render in parallel**, each shard excluding everything that is not its share.
5. **Merge**, after cross-checking the shards' uploaded plans (`shard-preview-ids.mjs --verify`):
   same discovered set, pairwise disjoint, complete cover. Without that check a disagreement reaches
   the operator as a completeness-gate failure naming a component, with nothing pointing at the
   shards.
6. **Generate and publish** unchanged, from the merged bundle.

**A plan cross-check is not a completeness check**, and the distinction cost a green run with 828
missing renders: the partition was correct and the cross-check printed `6 shard(s) cover 1095
preview(s) exactly once` truthfully, while unanchored `--exclude-preview-id` matching deleted each
shard's own variants. compose-ai-tools#3885 compares the merged bundle back against what the shards
said they would render, so a shard that loses work now fails the run naming the shard and the ids.

### Balance beats shard count

Render cost per preview is not uniform: a full-screen scaffold at `showSystemUi = true` costs far
more than a 32dp extra-small button, and the slowest shard sets wall clock. Round-robin over the
sorted id list spreads template-heavy groups rather than clustering them. Bin-packing from recorded
per-preview times is worth it only if a straggler shows up.

The one axis it cannot balance is a `@PreviewParameter` provider's **rows**: discovery emits one id
for the parameterized function and the renderer expands the rows later, so such a preview travels
whole into one shard. That is correct — the rows must not be split across bundles — and it is the
most likely source of a straggler.

## 4. Where the work lives

Per [`AGENTS.md`](../AGENTS.md), a capability any catalog could want belongs upstream as a generic
input on `design-artifacts-reusable.yml` — **never a forked pipeline here.**

**Upstream (`yschimke/compose-ai-tools`):**

- `render-shards`, default `1`, preserving unsharded behaviour byte for byte — at the default the
  two new jobs do not run and `generate` renders inline as before.
- `compose-preview bundle merge`, with `BundleMergeTest` pinning the two things repack cannot do:
  adding a slot, and carrying the semantics sidecar with it.
- `scripts/design-artifacts/shard-preview-ids.mjs`, with a `node --test` suite CI already picks up.
- Interaction with the two existing filters: **anything the render will not produce is removed
  before partitioning and re-applied in every shard** — the ids `modePriority` defers, and the ids
  the pre-flight's positive function-name filter drops. Both are exclusions, so a naive union would
  have them compete with the partition for slots; worse, a shard whose whole share happened to be
  filtered-out ids would report work to do and then exclude every id the filter kept, which
  `composePreviewRender` refuses.
- A **CLI-version pre-flight** in the matrix job. The merge runs last, so a `cli-version: catalog`
  pin older than `bundle merge` would fail the run having already burned six twenty-minute shards.
  The probe fails it in the first minute instead, naming the pin to raise.

**Here:** one line, `render-shards:`, in
[`design-artifacts.yml`](../.github/workflows/design-artifacts.yml). It must not reach `main` before
the upstream input does — the caller pins `@main`, and GitHub rejects an
input the called workflow does not declare, failing the run before any job starts.

### Two questions that are settled

- **Live-bundle interaction.** No designated shard is needed. Every shard packs the same module at
  the same commit, so `classes/app.jar` + `libs/` + `android/` are identical in all of them; the
  merge **inherits** the base shard's, along with the manifests and the cover. `publish-live-bundle:
  true` therefore sees exactly the bundle it would have seen unsharded.
- **Cache contention.** Not a factor: this pipeline has no Actions-level Gradle cache to contend on.
  The only shared caches are the downloadable-font cache (small; its save key carries the shard
  index, because Actions caches are immutable and N shards writing one key would have the first win
  and the rest log a conflict) and the **read-only** BuildFetch remote cache, which concurrent
  readers are what it is for. The real new cost is shard bundles moving through the artifact store,
  which is why they upload with `compression-level: 0` (a bundle is already a zip) and
  `retention-days: 1`.

## 5. `modePriority` is not a free win here

The eight named themes are
[`@ThemeCatalog`](../catalog/src/main/kotlin/ee/schimke/m3catalog/CatalogThemes.kt) wrapper
providers — entries in the preview server's **Theme** select plus a handful of synthesised specimen
sheets. They are *not* a per-preview fan-out; nothing renders each component eight times.

The per-preview mode axis is
[`@CatalogModes`](../catalog/src/main/kotlin/ee/schimke/m3catalog/CatalogTheme.kt) — **light and
dark**, which is what `catalog.spec.json` declares in `modes`. `modePriority` resolves a mode by
reading the trailing segment of a discovered preview id against `modes`, so the only thing this
catalog *can* defer is:

```jsonc
"modePriority": { "light": "required", "dark": "deferred" }
```

That is a **~50% cut, and the coverage it gives up is every baked dark sticker.** `images/`, the
`figma/*.svg` vectors and the Figma import would carry light only; dark would exist solely through
the serve host's live lane. That is a product decision about what the published sheet *is*, so it is
deliberately **not applied here**. Sharding, which gives up nothing, is the lever.

If the trade is ever acceptable, the one-line change composes with sharding exactly as designed —
deferral shrinks the work, sharding divides what remains.

## 6. Coordination

Two sessions independently fanned out Toggle buttons because nothing recorded who held what. Before
starting any group:

1. `git fetch origin main && git log --oneline -15 origin/main` — recent commits name their group.
2. `git ls-remote --heads origin` — an in-flight branch is a claim.
3. Tick your row in [`PARALLEL_SWEEP.md`](PARALLEL_SWEEP.md) **in its own commit, pushed before you
   start.** A claim that lands with the finished work is not a claim.

And the rule that would have prevented it outright: **whoever writes the handover does not also work
the queue.**
