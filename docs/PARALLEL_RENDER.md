# Parallelising the render to beat the timeout

**The bottleneck is one serial render, not authoring throughput.** `design-artifacts.yml` renders
every preview in a single `bundle pack` invocation, and that step grows linearly with the preview
count until it hits `render-timeout`. Adding people or agents to the *authoring* side does not move
it — it makes it worse, because every added component lengthens the same serial render.

This document is about sharding that render across parallel jobs. For dividing up the *authoring*
work, see [`PARALLEL_SWEEP.md`](PARALLEL_SWEEP.md) — a different problem with a different answer,
and confusing the two is how this document came to be written second.

---

## 1. What the numbers say

Measured on run [31162585271](https://github.com/yschimke/m3-catalog/actions/runs/31162585271),
287 previews:

| Phase | Time | Scales with previews? |
| --- | --- | --- |
| Checkout, JDK, Gradle restore, CLI install | ~3.6 min | no — fixed per job |
| **`bundle pack` (render + semantics)** | **13.6 min** | **yes — linear** |
| Publish design references | 2.7 min | weakly |
| **Job total** | **17.2 min** | |

So ~47 ms per preview of pure render, on a fixed ~3.6 min base.

Projected, unsharded:

| Previews | Render | Against `render-timeout: 2400` |
| --- | --- | --- |
| 287 | 13.6 min | ✅ (the 600s default failed here) |
| 607 | ~29 min | ✅ current `main` |
| 1000 | ~47 min | ✅ but past half the 90-min job limit |
| 1500 | ~71 min | ⚠️ near the job limit |
| 2500 | ~118 min | ❌ exceeds the job limit outright |

The full exhaustive sweep plausibly lands in the 1500–2500 range. **Raising `render-timeout` stops
working before then**, because the job timeout binds next and that one is a hard GitHub limit at 6
hours but a practical limit far sooner.

### Sharding maths

With `N` shards, wall clock is `3.6 + 13.6 × (previews / 287) / N` plus a merge job:

| Previews | N=1 | N=4 | N=8 |
| --- | --- | --- | --- |
| 607 | 29 min | 10.8 min | 7.2 min |
| 1500 | 71 min | 21.4 min | 12.5 min |
| 2500 | 118 min | 33.2 min | 18.4 min |

Diminishing past ~8 because the fixed 3.6 min per shard starts to dominate. **N=4 to N=8 is the
useful range.**

---

## 2. The two mechanisms this rests on

Both already exist; nothing here needs inventing.

**Restricting what a shard renders.** `bundle pack` forwards `composePreview.filter` (a
preview-function name pattern, set via `ORG_GRADLE_PROJECT_composePreview.filter`) and
`--exclude-preview-id` to the render. The reusable workflow already wires the first for
render-priority deferral.

**Merging shard renders.** `compose-preview bundle repack <bundle.png> --renders <dir> -o <out.png>`
swaps re-rendered PNGs — and their `figma.svg` siblings — into an existing bundle. Its documented
behaviour is the important part: *"a filename that matches no baked slot is skipped (reported), so a
partial re-render repacks what it matched."* Partial merges are the supported case, not a hack.

**Why a shard still produces a complete bundle.** A preview excluded from the render stays listed in
the bundle's `previews.json` and simply carries no PNG — the same mechanism render-priority
deferral relies on. So every shard emits a structurally complete bundle differing only in which
slots are filled, which is exactly what `repack` wants.

---

## 3. The shape

```
                     ┌── shard 0: bundle pack --filter <partition 0> ──┐
  discover ids ──────┼── shard 1: bundle pack --filter <partition 1> ──┼── merge ── generate ── publish
   (cheap, once)     ├── shard 2: …                                    │   (repack)
                     └── shard N: …                                    ┘
```

1. **Discover once.** `compose-preview list --json` (or `composePreviewDiscover`) enumerates preview
   ids without rendering. Cheap — seconds. The design-artifacts workflow already runs this when a
   spec defers a mode, so the step exists.
2. **Partition.** Split the id list into `N` roughly equal groups. Partition by **id**, not by
   function name: one function can expand to 30 previews (the icon-button matrix), so a
   function-name split is wildly unbalanced. Deterministic partitioning — sort ids, then round-robin
   or contiguous-chunk — so a re-run reproduces the same shards and the cache warms.
3. **Render in parallel.** A GitHub Actions `matrix` over the partitions, each shard packing only
   its own ids. Each pays the ~3.6 min fixed cost; the Gradle compile is shared through the
   Actions cache after the first shard warms it.
4. **Merge.** One job downloads every shard artifact and `repack`s them into one bundle.
5. **Generate and publish** exactly as today, from the merged bundle.

### Balance matters more than shard count

Render cost per preview is not uniform — a full-screen scaffold template at `showSystemUi = true`
costs far more than a 32dp extra-small button. A naive equal-count split leaves the slowest shard
defining wall clock. Two options, in order of effort:

- **Good enough:** round-robin the sorted id list, so template-heavy groups spread across shards
  rather than clustering.
- **Better, later:** record per-preview render times from a previous run and bin-pack. Only worth it
  if the naive split shows a bad straggler.

---

## 4. Where the work goes

Per [`AGENTS.md`](../AGENTS.md): a capability any catalog could want belongs upstream as a generic
input on `design-artifacts-reusable.yml` — **never as a forked pipeline here.** Sharding is
squarely that; every large catalog hits this wall.

**Upstream (`yschimke/compose-ai-tools`):**

- A `render-shards` input (default `1`, preserving today's behaviour exactly).
- When `> 1`: the discover step, the partition, the `matrix` render, and the `repack` merge.
- The partition helper alongside the existing `scripts/design-artifacts/*.mjs` (`deferred-preview-ids.mjs`
  is the closest precedent — it already derives id lists from `compose-preview list --json`), with
  its own self-test like `test-scope-systems.sh`.
- Interaction with `--exclude-preview-id`: mode-deferral exclusions must apply **within** each
  shard, not compete with the partition.

**Here (`yschimke/m3-catalog`):** one line — `render-shards: 6` — once the input exists.

### Open questions to settle upstream before building

- **Does `repack` merge semantics sidecars**, or only PNG + `figma.svg`? The catalog's completeness
  gate fails a preview with pixels but no semantics, so if `repack` drops the semantics sidecar the
  merge loses them and every shard past the first fails the gate. This is the single biggest risk in
  the design and should be checked first.
- **Live-bundle interaction.** This catalog publishes `publish-live-bundle: true` +
  `split-per-preview: true`. Does the live classpath survive a repack, or must the merge carry it
  from a designated shard?
- **Cache contention.** Six shards restoring the same Gradle cache concurrently may serialise on
  download. Worth measuring before assuming linear speed-up.

---

## 5. The cheaper lever, if sharding is too much work

`modePriority` in `catalog.spec.json` defers non-primary modes to the live server: they are not
rendered, not baked, and not counted by the completeness gate, but stay addressable through the
daemon.

This catalog renders six themes. Deferring the four contrast tiers cuts the baked set to roughly a
third with a one-file change and no upstream work. Upstream measured ~59% fewer renders on a
nine-theme catalog doing the same thing.

It requires a live path, which this catalog already publishes. **Do this first if sharding stalls**
— it buys back more than 4× sharding does, immediately, and the two compose.

---

## 6. Coordination — the thing that already went wrong

Two sessions independently fanned out **Toggle buttons**, because nothing recorded who held what.
Before starting any group:

1. `git fetch origin main && git log --oneline -15 origin/main` — recent commits name their group.
2. `git ls-remote --heads origin` — an in-flight branch is a claim.
3. Tick your row in [`PARALLEL_SWEEP.md`](PARALLEL_SWEEP.md) **in its own commit, pushed before you
   start**, not with the finished work. A claim landing after the work is not a claim.

And the rule that would have prevented it outright: **whoever writes the handover does not also work
the queue.** Hand over or execute — doing both is what produced the collision.
