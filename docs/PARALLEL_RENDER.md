# Parallelising preview generation

**The bottleneck is one serial render.** `design-artifacts.yml` renders every preview in a single
`bundle pack` invocation, and that step grows linearly with the preview count until it hits
`render-timeout` — and then, not far behind it, the job timeout.

Parallelising the *authoring* does not help. It makes this worse: every component another session
adds lengthens the same serial render. That is a different document
([`PARALLEL_SWEEP.md`](PARALLEL_SWEEP.md)) solving a different problem.

> **Status: built, and currently switched off — read §9 first.** `render-shards` is an input on the
> upstream reusable workflow and the machinery here works, but this repo passes **`render-shards: 1`**
> because the cost model §1–§3 were derived from has since been invalidated. §9 has the
> re-measurement and is the section that governs; §1–§3 are kept as the *original* fit and should be
> read as history, not as guidance. §4–§6 are what was built and how the three open questions
> resolved — the first of them, *does `repack` merge semantics sidecars*, resolved to **no**, which
> is why there is now a `bundle merge`. §7 records a claim the earlier draft made about
> `modePriority` that does not hold for this catalog.
>
> **Upstream landed** as compose-ai-tools#3439, and the two gates it named are both cleared:
> `compose-preview` 0.19.44 shipped `bundle merge`, and the sharded render's silent data loss —
> unanchored `--exclude-preview-id` deleting each shard's own variants — was fixed by
> compose-ai-tools#3561 (anchored `=<id>` matching) and #3570 (the pipeline emits it), with the
> reusable workflow now hard-erroring up front on a CLI that predates it. What remains unresolved is
> only the third question the draft raised — **shard balance on a real sheet has still never been
> measured**, because the one sharded run this catalog took was the buggy one.

---

## 1. What the render actually costs

Four Design Artifacts runs on `main`, measured:

| Commit | Previews | Render step | Job total |
| --- | --- | --- | --- |
| `d354560` | 287 | 13.6 min | 17.2 min |
| `323e4a2` | 519 | 22.5 min | 24.0 min |
| `5edc70a` | 607 | 26.7 min | 29.6 min |
| `f35c2e5` | 689 | 27.2 min | 28.9 min |

Least-squares fit:

```
render_minutes ≈ 3.7 + 2.15s × previews
```

Two numbers matter, and the first is the one that decides the design:

- **~3.7 min is fixed inside the render step** — Gradle configure and compile, before a single
  preview is drawn. **Every shard pays this in full.** It is what caps useful shard count, and it is
  far larger than the setup cost around it (~1 min of checkout / JDK / cache restore).
- **~2.15 s per preview** is the marginal cost, and the only part sharding divides.

## 2. Where this stops working

| Previews | Render (unsharded) | Verdict |
| --- | --- | --- |
| 689 (today) | ~28 min | fine |
| 1000 | ~40 min | fine, but half the job budget |
| 1500 | ~58 min | `render-timeout: 2400` (40 min) **fails** |
| 2500 | ~94 min | **exceeds the 90 min job timeout** |

The exhaustive sweep plausibly lands in the 1500–2500 range: 5 of 40 groups are done and they are
689 previews. **Raising `render-timeout` buys one more step and then stops** — past ~2400 previews
the job timeout binds, and that is not a number worth chasing upward either.

## 3. Sharded projection

Per-shard wall clock is `1 min setup + 3.7 min compile + ~0.9 min discovery + 2.15s × (previews / N)`,
plus a merge + generate job of ~4 min:

| Previews | N=1 | N=4 | N=6 | N=8 |
| --- | --- | --- | --- | --- |
| 689 | 29.4 | 15.8 | 13.7 | 12.7 |
| 1000 | 40.5 | 18.6 | 15.6 | 14.1 |
| 1500 | 58.4 | 23.0 | 18.6 | 16.3 |
| 2500 | 94.3 | 32.0 | 24.5 | 20.8 |

**N=4 to N=6 is the sweet spot.** Going 6→8 at 1500 previews saves 2.3 min for two more runners,
because the ~5.6 min every shard pays before rendering anything now dominates. Anyone reaching for
N=16 is buying compile time, not throughput.

Sharding turns the worst case from *impossible* into ~25 min. It does not need to be perfect.

> The 0.9 min of discovery is per shard, not once: each shard derives its own partition from its own
> `compose-preview list --json` rather than waiting on a central plan job. A central plan would have
> to compile before it could discover, so it would add its *whole* 4.6 min to the critical path
> instead of 0.9 min inside each shard's own compile.

---

## 4. The two mechanisms it rests on

**Restricting what a shard renders — already existed.** `bundle pack` forwards `composePreview.filter`
(a preview-function name pattern, via `ORG_GRADLE_PROJECT_composePreview.filter`) and
`--exclude-preview-id` down to the render. `--exclude-preview-id` is the load-bearing one, and its
documented behaviour is exactly right: *"Excluded previews stay listed in the bundle (addressable,
just without a baked PNG)."* So every shard emits a structurally identical bundle — same
`previews.json`, same manifest, same re-render classpath — differing only in which
`previews/<id>.*` slots are filled.

**Merging shard renders — did not exist, and `repack` could not be made to do it.** This was the
open question at the top of §6, and the answer decided the shape of everything else:

> **`bundle repack` merges PNG + `figma.svg` only, and only into slots the target already has.**
> `repackRethemedPreviews` collects the baked `previews/<id>.png` / `previews/<id>.figma.svg` entries
> and swaps matching filenames in; a render with no matching slot is reported *unmatched and dropped*
> (`BundleRepackTest` pins that), and the JSON sidecars are explicitly preserved verbatim rather than
> merged. Against a shard base, every other shard's previews are unmatched — the base's render
> excluded them, so there is no slot — and any that did land would arrive without their
> `.semantics.json`, which the completeness gate fails.

So the merge is a new primitive:

```
compose-preview bundle merge <base.png> <shard.png>… -o <out.png>
```

It unions the per-preview artifacts — the raster, its `.semantics.json` / `.layout.json` /
`.fonts.json` / `.figma.svg` / `.catalog.json` / `.overrides.json` sidecars, the nested
`figma-raster/` crops, `ir/<id>.rc`, `extensions/<id>.json` — adding the slot where the base has
none, base-wins on collision, earlier shards over later ones.

---

## 5. The shape, as built

```
          ┌── shard 1: discover → plan → bundle pack → upload ──┐
 matrix ──┼── shard 2: …                                        ┼── merge ── generate ── publish
 [1..N]   └── shard N: …                                        ┘
```

1. **Matrix.** `render-shards: N` becomes `[1 … N]`. No checkout, no toolchain — Actions simply has
   no range expression.
2. **Each shard discovers and plans for itself.** `compose-preview list --json` runs
   `composePreviewDiscover`; that compile is shared with the shard's own render, so it costs
   discovery, not a second compile.
3. **Partition by preview id, never by function name.** One function expands to 30 previews (the
   icon-button matrix); a name split is wildly unbalanced. Sort the ids, then round-robin — which is
   both balanced and deterministic, so independent shards agree without talking to each other.
4. **Render in parallel**, each shard excluding everything that is not its share.
5. **Merge**, after cross-checking the shards' uploaded plans (`shard-preview-ids.mjs --verify`):
   same discovered set, pairwise disjoint, complete cover. Without that check a disagreement would
   reach the operator as a completeness-gate failure naming a component, with nothing pointing at
   the shards.
6. **Generate and publish** unchanged, from the merged bundle.

### Balance beats shard count

Render cost per preview is not uniform: a full-screen scaffold at `showSystemUi = true` costs far
more than a 32dp extra-small button, and the slowest shard sets wall clock. Round-robin over the
sorted id list spreads template-heavy groups rather than clustering them, which is enough to start.
Bin-packing from recorded per-preview times is worth it only if a straggler shows up.

The one axis it cannot balance is a `@PreviewParameter` provider's **rows**: discovery emits one id
for the parameterized function and the renderer expands the rows later, so such a preview travels
whole into one shard. That is correct — the rows must not be split across bundles — and it is the
most likely source of a straggler.

---

## 6. Where the work went

Per [`AGENTS.md`](../AGENTS.md), a capability any catalog could want belongs upstream as a generic
input on `design-artifacts-reusable.yml` — **never a forked pipeline here.** Every large catalog
hits this wall.

**Upstream (`yschimke/compose-ai-tools`):**

- `render-shards`, default `1`, preserving today's behaviour byte for byte — at the default the two
  new jobs do not run at all and `generate` renders inline as before.
- `compose-preview bundle merge`, with `BundleMergeTest` pinning the two things repack cannot do:
  adding a slot, and carrying the semantics sidecar with it.
- `scripts/design-artifacts/shard-preview-ids.mjs` beside `deferred-preview-ids.mjs` (the precedent
  it reuses `previewsFromJson` from), with a `node --test` suite CI already picks up.
- Interaction with the two existing filters: **anything the render will not produce is removed
  before partitioning and re-applied in every shard** — the ids `modePriority` defers, and the ids
  the pre-flight's positive function-name filter drops. Both are exclusions, so a naive union would
  have them compete with the partition for slots; worse, a shard whose whole share happened to be
  filtered-out ids would report work to do and then exclude every id the filter kept, which
  `composePreviewRender` refuses.
- A **CLI-version pre-flight** in the matrix job. The merge runs last, so a `cli-version: catalog`
  pin older than `bundle merge` — which is this catalog's exact configuration until its plugin pin
  moves — would fail the run having already burned six twenty-minute shards. The probe fails it in
  the first minute instead, naming the pin to raise.

**Here:** one line, `render-shards: 6`, which is now in
[`design-artifacts.yml`](../.github/workflows/design-artifacts.yml). It must not reach `main` before
the upstream input does — the caller pins `@main`, and GitHub rejects an input the called workflow
does not declare, failing the run before any job starts.

### The three questions, settled

- **Does `repack` merge semantics sidecars?** **No** — see §4. That is why `bundle merge` exists,
  and it was worth checking first: it is the one answer that decided whether the rest was worth
  writing.
- **Live-bundle interaction.** No designated shard is needed. Every shard packs the same module at
  the same commit, so `classes/app.jar` + `libs/` + `android/` are identical in all of them; the
  merge **inherits** the base shard's rather than merging them, along with the manifests and the
  cover. `publish-live-bundle: true` + `split-per-preview: true` therefore see exactly the bundle
  they would have seen unsharded.
- **Cache contention.** Not a factor: this pipeline has **no Actions-level Gradle cache** to contend
  on — no `setup-gradle` cache step, no `actions/cache` over `~/.gradle`. The only shared caches are
  the downloadable-font cache (small; the save key now carries the shard index, because Actions
  caches are immutable and N shards writing one key would have the first win and the rest log a
  conflict) and the **read-only** BuildFetch remote cache, which six concurrent readers is what it
  is for. The real new cost is the shard bundles moving through the artifact store, which is why
  they upload with `compression-level: 0` (a bundle is already a zip) and `retention-days: 1`.

---

## 7. `modePriority` here: the arithmetic does not hold

The earlier draft of this page said: *"This catalog renders six themes. Deferring the four contrast
tiers cuts the baked set to roughly a third."* **That is wrong for this catalog**, and worth
recording so nobody re-derives it.

The six themes are [`@ThemeCatalog`](../catalog/src/main/kotlin/ee/schimke/m3catalog/CatalogThemes.kt)
wrapper providers — Baseline Light/Dark plus four contrast tiers. They are entries in the preview
server's **Theme** select and a handful of synthesised specimen sheets; they are *not* a per-preview
fan-out. Nothing renders each component six times.

The per-preview mode axis is
[`@CatalogModes`](../catalog/src/main/kotlin/ee/schimke/m3catalog/CatalogTheme.kt) — **light and
dark**, and that is what `catalog.spec.json` declares in `modes`. Every component in the sweep
carries it (78 of them today). `modePriority` resolves a
mode by reading the trailing segment of a discovered preview id against `modes`, so the only thing
this catalog *can* defer is:

```jsonc
"modePriority": { "light": "required", "dark": "deferred" }
```

which is a **~50% cut, not a two-thirds one — and the coverage it gives up is every baked dark
sticker.** `images/`, the `figma/*.svg` vectors and the Figma import would carry light only; dark
would exist solely through the serve host's live lane. That is a real product decision about what
the published sheet *is*, not the free win the earlier draft described, so it is deliberately **not
applied here**. Sharding, which gives up nothing, is.

If the trade is acceptable, the one-line change above composes with sharding exactly as designed —
deferral shrinks the work, sharding divides what remains.

---

## 8. Coordination

Two sessions independently fanned out Toggle buttons because nothing recorded who held what. Before
starting any group:

1. `git fetch origin main && git log --oneline -15 origin/main` — recent commits name their group.
2. `git ls-remote --heads origin` — an in-flight branch is a claim.
3. Tick your row in [`PARALLEL_SWEEP.md`](PARALLEL_SWEEP.md) **in its own commit, pushed before you
   start.** A claim that lands with the finished work is not a claim.

And the rule that would have prevented it outright: **whoever writes the handover does not also work
the queue.**

---

## 9. Re-measured after the warm renderer — and why the answer is now `render-shards: 1`

§1's fit, `render_minutes ≈ 3.7 + 2.15s × previews`, was measured when every capture forked a JVM.
[compose-ai-tools#3548](https://github.com/yschimke/compose-ai-tools/pull/3548) moved captures onto a
long-lived warm renderer, which invalidated it. Re-measured on CI (compose-ai-tools#3559), both rows
below being the same `Render catalog bundle` step on `ubuntu-latest`, decomposed from the task
timings the step itself prints:

| | fixed (configure + compile + discover + pack) | `composePreviewRender` | semantics capture | full sheet |
| --- | --- | --- | --- | --- |
| before #3548 — 1095 previews, [run 31192651935](https://github.com/yschimke/m3-catalog/actions/runs/31192651935) | 104 s | 2603 s → **2.38 s/preview** | 186 s → 0.17 s/preview | **2893 s** |
| after #3548 — 1147 previews, [run 31282283505](https://github.com/yschimke/m3-catalog/actions/runs/31282283505) | 100 s | 232 s → **0.202 s/preview** | 211 s → 0.185 s/preview | **543 s** |

```
render_seconds ≈ 100 + 0.39s × previews        (was ≈ 104 + 2.55s × previews)
```

Two sibling runs agree with the second row: 568 s and 548 s for the same step.

**Both terms of §1 were wrong.** The marginal cost fell 6.5x. The fixed cost never moved — and was
never 3.7 min: §1 read the whole render step as if it were prologue. The ~100 s is real and it is
what every shard would pay, but it is a third of what the sharding case was argued on.

**Per-shard fixed cost, measured** on the (buggy) six-shard run
[31245536104](https://github.com/yschimke/m3-catalog/actions/runs/31245536104) — its *render*
durations are meaningless because the shards dropped most of their work, but its fixed steps are
real: job prefix ~25 s + a separate `Discover previews` step **87 s** + the render step's own Gradle
prologue ~20 s + upload/font-cache ~15 s ≈ **150 s**, plus a ~85 s merge/generate/publish job.

```
T ≈ 150s + 0.39s × previews/N + 85s          (N > 1)
```

| Previews | N=1 | N=2 | N=4 | N=6 | N=8 |
| --- | --- | --- | --- | --- | --- |
| 1147 (today) | **9.1 min** | 7.6 | 5.8 | 5.2 | 4.9 |
| 2500 | 17.9 | 12.1 | 8.0 | 6.6 | 6.0 |
| 5000 | 34.2 | 20.2 | 12.0 | 9.3 | 8.0 |

**So the optimum moved down, and at this size it moved below 2.** That is not a surprise once
stated: cheaper marginal work against an unchanged fixed cost always moves it down. Sharding today's
sheet four ways buys ~3 min of wall clock for three extra runners and ~8 extra runner-minutes, while
the serial render sits at under a quarter of its 2400 s `render-timeout` — and the 48-minute render
that motivated this whole document no longer exists. `render-shards: 1`.

**When to raise it.** Around **3000 previews** the split starts to be worth its runners; near
**6000** it stops being optional, because that is where a serial render meets `render-timeout`. The
machinery stays wired and tested for exactly that, and turning it up is now a one-line change to a
correct fan-out rather than a data-loss risk.

Two honest caveats. The ~85 s merge tail was measured with near-empty shard artifacts, so a real
merge of N full bundles costs more — which makes sharding look slightly *better* here than it is.
And the projections above remain modelled, not measured sharded: the straggler question from §6
(`@PreviewParameter` rows travelling whole with their parent id) is still open, and the first honest
sharded run should check shard balance before anyone trusts the N>1 columns.
