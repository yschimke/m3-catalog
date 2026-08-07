# Parallelising preview generation

**The bottleneck is one serial render.** `design-artifacts.yml` renders every preview in a single
`bundle pack` invocation, and that step grows linearly with the preview count until it hits
`render-timeout` — and then, not far behind it, the job timeout.

Parallelising the *authoring* does not help. It makes this worse: every component another session
adds lengthens the same serial render. That is a different document
([`PARALLEL_SWEEP.md`](PARALLEL_SWEEP.md)) solving a different problem.

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

Per-shard wall clock is `1 min setup + 3.7 min compile + 2.15s × (previews / N)`, plus a merge job
of ~4 min:

| Previews | N=1 | N=4 | N=6 | N=8 |
| --- | --- | --- | --- | --- |
| 689 | 29.4 | 14.9 | 12.8 | 11.8 |
| 1000 | 40.5 | 17.6 | 14.7 | 13.2 |
| 1500 | 58.4 | 22.1 | 17.6 | 15.4 |
| 2500 | 94.2 | 31.1 | 23.6 | 19.9 |

**N=4 to N=6 is the sweet spot.** Going 6→8 at 1500 previews saves 2.2 min for two more runners,
because the 4.7 min every shard pays before rendering anything now dominates. Anyone reaching for
N=16 is buying compile time, not throughput.

Sharding turns the worst case from *impossible* into ~24 min. It does not need to be perfect.

---

## 4. The two mechanisms it rests on

Both already exist. Nothing here needs inventing.

**Restricting what a shard renders.** `bundle pack` forwards `composePreview.filter` (a
preview-function name pattern, via `ORG_GRADLE_PROJECT_composePreview.filter`) and
`--exclude-preview-id` down to the render. The reusable workflow already wires the first, for
render-priority deferral.

**Merging shard renders.** `compose-preview bundle repack <bundle.png> --renders <dir> -o <out.png>`
swaps re-rendered PNGs — and their `figma.svg` siblings — into an existing bundle. Its documented
behaviour is the load-bearing part: *"a filename that matches no baked slot is skipped (reported),
so a partial re-render repacks what it matched."* Partial merge is the supported case, not a hack.

**Why a shard still yields a complete bundle.** A preview excluded from the render stays listed in
`previews.json` and simply carries no PNG — the same mechanism render-priority deferral relies on.
So every shard emits a structurally identical bundle, differing only in which slots are filled,
which is exactly what `repack` consumes.

---

## 5. The shape

```
                    ┌── shard 1: bundle pack, partition 1 ──┐
 discover ids ──────┼── shard 2: bundle pack, partition 2 ──┼── repack ── generate ── publish
  (seconds)         ├── shard 3: …                          │  (merge)
                    └── shard N: …                          ┘
```

1. **Discover once.** `compose-preview list --json` enumerates preview ids without rendering —
   seconds, not minutes. The workflow already runs this when a spec defers a mode.
2. **Partition by preview id, never by function name.** One function expands to 30 previews (the
   icon-button matrix); a name split is wildly unbalanced. Sort ids, then round-robin, so the split
   is deterministic and reproducible across re-runs.
3. **Render in parallel** — an Actions `matrix`, each shard packing only its partition.
4. **Merge** — one job downloads every shard artifact and `repack`s them together.
5. **Generate and publish** unchanged, from the merged bundle.

### Balance beats shard count

Render cost per preview is not uniform: a full-screen scaffold at `showSystemUi = true` costs far
more than a 32dp extra-small button, and the slowest shard sets wall clock. Round-robin over the
sorted id list spreads template-heavy groups rather than clustering them, which is enough to start.
Bin-packing from recorded per-preview times is worth it only if a straggler shows up.

---

## 6. Where the work goes

Per [`AGENTS.md`](../AGENTS.md), a capability any catalog could want belongs upstream as a generic
input on `design-artifacts-reusable.yml` — **never a forked pipeline here.** Every large catalog
hits this wall.

**Upstream (`yschimke/compose-ai-tools`):**

- A `render-shards` input, default `1`, preserving today's behaviour byte for byte.
- When `> 1`: the discover step, the partition, the `matrix` render, and the `repack` merge.
- The partition helper beside the existing `scripts/design-artifacts/*.mjs`.
  `deferred-preview-ids.mjs` is the precedent — it already derives id lists from
  `compose-preview list --json` — with a self-test in the style of `test-scope-systems.sh`.
- Interaction with `--exclude-preview-id`: mode-deferral exclusions must apply *within* each shard,
  not compete with the partition.

**Here:** one line, `render-shards: 6`, once the input exists.

### Settle these before building

- **Does `repack` merge semantics sidecars, or only PNG + `figma.svg`?** The completeness gate fails
  a preview that has pixels but no semantics, so if the sidecar is dropped, every shard past the
  first fails the gate and the whole design collapses. **Check this first** — it is the one answer
  that decides whether the rest is worth writing.
- **Live-bundle interaction.** This catalog publishes `publish-live-bundle: true` +
  `split-per-preview: true`. Does the live classpath survive a repack, or must the merge take it
  from a designated shard?
- **Cache contention.** Six shards restoring the same Gradle cache concurrently may serialise on
  download, eroding the compile-time assumption above. Measure before trusting the projection.

---

## 7. Do this first: `modePriority`

`modePriority` in `catalog.spec.json` defers non-primary modes to the live server — not rendered,
not baked, not counted by the completeness gate, but still addressable through the daemon.

This catalog renders **six themes**. Deferring the four contrast tiers cuts the baked set to roughly
a third: at 1500 previews that is ~58 min → ~22 min, from **a one-file change with no upstream work
at all**. Upstream measured ~59% fewer renders doing the same thing on a nine-theme catalog.

It requires a live path, which this catalog already publishes. It beats 4× sharding, lands today,
and the two compose — deferral shrinks the work, sharding divides what remains.

**If you only do one thing on this page, do this one.**

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
