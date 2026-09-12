# Glimmer in this repository

Two systems — `glimmer-catalog` (this repo's own stickers) and `glimmer-samples` (AndroidX's
vendored samples) — for `androidx.xr.glimmer`, the Compose UI toolkit for augmented Android XR
glasses.

They are the first **Android** modules in a repository whose defining build decision was to have
none. That is worth stating plainly, because the rule was not relaxed: it stopped being applicable.

## Why AGP, when `:catalog` exists precisely to avoid it

`catalog/build.gradle.kts` opens by explaining that `:catalog` is deliberately not an Android
module — no AGP means the compose-preview plugin routes it to the Desktop (Skiko) renderer, which
is what lets preview.coo.ee hold a live Compose session against the published bundle.

Glimmer cannot take that lane, and the reason is packaging rather than taste:

| | |
| --- | --- |
| `androidx.xr.glimmer:glimmer:1.0.0-alpha19` | `<packaging>aar</packaging>` |
| AAR metadata | `minCompileSdk=37` |
| Compose Multiplatform port | none |

A desktop JVM module cannot put an AAR on its classpath at all, so the choice was a Robolectric lane
or no Glimmer sheet. What it is *not* is a problem with the code: the POM's only dependencies are
`glimmer-google-fonts`, AndroidX `compose-ui` / `runtime` / `foundation`, `annotation` and
`kotlin-stdlib` — **no XR runtime, no session, no device**. Nothing here needs hardware.

**The cost, stated rather than hidden:** these sheets are rasterised by a different renderer from
`m3-catalog`, so the two are never paired. `glimmer-catalog/catalog.spec.json` declares no
`compareWith`, and nothing in this repository puts a Robolectric capture beside a Skiko one — that
would put a *renderer* difference into a comparison meant to be about design, which is the exact
confound `docs/design/ANDROIDX_SAMPLES.md` refused for the material3 samples.

`glimmer-samples` DOES declare `compareWith: glimmer-catalog`, and that pairing is honest: same
library, same renderer, the component beside how you call it.

## Additive display decides how a sticker is captured

Glimmer draws for glasses with an **additive** display: the panel emits light, so a pure black pixel
emits nothing and reads as fully transparent through the lens. That is not a dark theme — it is what
the library's colour tokens are calibrated against, and it decides the capture.

Every sticker here is therefore captured with `showBackground = true` and
`backgroundColor = 0xFF000000` on a 960x720 dpi-160 device spec (so 1dp is 1px and a token's
declared size is its size in the PNG). What the published PNG shows as black is exactly what the
glasses leave unlit; everything else is light the UI adds. A sheet that composited Glimmer onto
white would be a picture of something the hardware cannot produce — which is also why
`catalog.spec.json` declares a single `dark` mode and a dark display surface rather than the
light/dark pair every other system here carries.

The constants and the `Sticker` wrapper are deliberately the same shape as the `GlimmerSurface`
helper in compose-ai-tools' `:samples:xr-glimmer`, so when that is published from a runtime artifact
this becomes a one-line swap rather than a re-authoring.

`@GlimmerEnvironmentPreview` (Light / Dark / Busy / VeniceCanalCats) composites the same additive
capture over a passthrough scene, which is what a wearer actually sees. It is not applied yet — see
**Still to do**.

## The kit: found late, and the components now name it

**This document originally said Glimmer publishes no Figma kit. That was wrong**, and the error is
worth keeping visible rather than editing away: the claim was made from the absence of a kit in
`androidx.xr.glimmer`'s own publishing, and a community kit was never looked for. There is one:

> [Jetpack Compose Glimmer UI (Community)](https://www.figma.com/design/HKfLClZDLRyMhf4IQQLna8/Jetpack-Compose-Glimmer-UI--Community-)

Every component shipped with `noReference` and a reason that read "no kit node exists to name". That
is worse than a missing reference — it is an *audited* absence that was never audited, which is
exactly the distinction the schema's own wording draws. All eight now carry a real `reference`, each
with the evidence for it in a comment beside the annotation:

| Component | Kit node | What identifies it |
| --- | --- | --- |
| `Button` | `40:655` | `State=` x `Size=Default \| Large` — the size axis this component folds in |
| `ToggleButton` | `40000113:3966` | adds `Toggle=False \| True`, the checked state |
| `IconButton` | `5315:4650` | under the kit's `icon buttons` frame; `State=` only |
| `IconToggleButton` | `40000113:4149` | also under `icon buttons`, but `Toggle=` x `State=` |
| `Card` | `416:2700` | the `Card` component — title chip, image slot, title, subtitle, content |
| `TitleChip` | `5315:4722` | the `Title chip` component, leading-icon slot included |
| `ListItem` | `384:4197` | `Type=1-line \| 2-line \| Card` x `State=` |
| `VoiceInputIndicator` | `40000116:9337` | `Mic Indicators`: `Volume=` x `Contained=Yes \| No` |

The ids are the kit's own component sets, read from the file's `🧩 Components` page. `Card` and
`TitleChip` were confirmed by rendering the node rather than by name alone — the others are
identified by their variant axes, which is stronger evidence than a name.

`scripts/glimmer-design-map.sh` projects those eight into `glimmer-design-map.json`, and the publish
job copies it into place. It replaced a `design-map-command` that projected an empty map.

### What is still missing

- **The kit index exists now, and it did not do what was expected.**
  `glimmer-figma-kit-index.json` holds the kit's 6 component sets, their 55 variants and 2
  standalone components, so `scripts/glimmer-design-map.sh` runs the
  `@design-parity/kit-index resolve` step `design-map.sh` always had.

  It needed no Figma token. `@design-parity/kit-index` builds an index by calling api.figma.com,
  which this repository holds no secret for and which is outside this environment's egress
  allowlist besides; the **Figma MCP server's read-only `get_metadata`** returns the same node ids
  and variant names, one call per component set, and read-only is what `AGENTS.md` requires of
  every Figma interaction here.

  **It resolves nothing, and that is the finding rather than a failure.** The claim above — that
  the eleven are resolvable because the kit publishes every axis they declare — was wrong twice:

  - **The references named component SETS rather than cells**, and a set has no axes to diff a
    declaration against. That was the larger half and it is fixed: all six set references now name
    the base cell the sticker actually draws (`Button` -> `State=Enabled, Size=Default`, and so on),
    which is what `m3-catalog` has always done and what the emitter reports on — it now says "0
    naming their component set" where it used to count six. `size=Large` resolves against
    `State=Enabled, Size=Large` as soon as there is a base cell to diff from, so the resolver
    handles multi-axis names perfectly well; the earlier reading here, that multi-axis never
    matches, was wrong.
  - **The prop vocabulary is Compose's, not the kit's.** The four that still miss are
    `state=checked` against the kit's `Toggle=True`, `container=contained` against `Contained=Yes`,
    and `content=supporting-label` against `Type=2-line`. Renaming the declarations would resolve
    them and would be the wrong trade: `checked` is what a reader of this catalog wants to see, and
    `True` is what the kit calls it. `m3-catalog` keeps both by generating a SECOND view —
    `scripts/generate-exhaustive-kit-cells.mjs` emits one declaration per kit cell with `kitProps`
    pinning the exact vector, leaving the authored variants readable. That port is the next step.
  - Most of the eleven have no counterpart to find. `content=leading-icon` is a Compose slot, and
    the kit's Button set publishes `State=` x `Size=` with no content axis at all. `Card` and
    `Title chip` are single symbols whose content differences are hidden LAYERS — which is why both
    sit in the index's `standalone` rather than `sets`, with no variants to resolve against.

  One resolves today (Button `size=Large`). Four more have a counterpart and are waiting on that
  generated view rather than on anything unknown: Button `size=Large` -> `40000113:3576`, ToggleButton `state=checked` ->
  `40000113:4138`, IconToggleButton `state=checked` -> `40000113:4181`, VoiceInputIndicator
  `container=contained` -> `40000116:9338`, and ListItem `content=supporting-label`, which is the
  kit's `Type=2-line` (`384:4191`) under a Compose name. That is a taxonomy change to the
  annotations, and it belongs with the re-check below rather than ahead of it.
- **No parity lane.** The job still carries no `figma_token` and no `reference-cache-branch`, so
  nothing fetches reference artwork or scores the comparison yet.
- **The direction is settled, by precedent.** An earlier draft of this section treated the kit's
  "(Community)" suffix as weaker authority than a first-party file, and asked whether
  `.design-parity.json`'s repo-wide `design-led` should apply. That was a misreading: **the Figma
  Community is how Google publishes its design kits publicly**, and the file this whole repository
  is built to reproduce — `Material-3-Design-Kit--Community-` — carries exactly the same suffix. The
  Glimmer kit is the public edition of Android's own, not a third-party redraw, so it inherits the
  same authority the Material kit has: the kit is authoritative and a divergence is a bug in this
  code.
- **The taxonomy has not been re-checked against the kit.** These seven components and their variant
  folds were derived from the API surface alone. `AGENTS.md` says membership is the kit's call, and
  that rule now has something to say here — the kit also publishes Button groups, a Progress
  indicator, Entity (avatars, monograms, app icons) and Stacks, none of which this catalog draws.

## The samples inventory is generated, and the spec that skipped it did not publish

`glimmer-samples/catalog.spec.json` originally shipped with no `groups`, on the reading — written
into its own `$comment` — that the inventory could be discovery's own, since every `@Preview` in the
vendored tree renders. That is not a reading the schema allows, and the publish said so:

```
error: `groups` is omitted but the module declares no @CatalogComponent — the catalog has no
inventory. Declare `groups`, or add @CatalogComponent / @CatalogVariant to the module's @Preview
functions.
```

The `glimmer-samples` job failed at *Validate catalog spec*, before rendering anything, on every run
since the module landed — which is why `design-artifacts/glimmer-samples` was never published while
`glimmer-catalog` and `m3-samples` were. The asymmetry was visible in the branch list for hours and
read as slowness rather than as failure.

A catalog declares its inventory one of two ways, and the annotation route is closed here for
exactly the reason `:samples-catalog`'s is: these are upstream's bytes, re-fetched byte-identically
on every import, so an `@CatalogComponent` written into one would not survive the next
`scripts/import-samples.mjs` run. So `groups` it is — generated by
`scripts/glimmer-samples-spec.mjs` and committed, with the regenerate-and-diff `--check` contract
`sample-map.json` and `design-map.json` already carry.

It is a *separate* generator from `scripts/samples-spec.mjs`, whose input is `sample-map.json`: there
is no such map here, for the reason this document already gives — glimmer's AAR publishes no sources
jar, so there is no `@sample` KDoc to read. The sources are the only description of themselves that
exists, and they turn out to be enough, because **upstream's Glimmer samples carry their own
`@Preview` functions**. material3's carry none and need a wrapper generated; these are all the same
four lines, so the preview, the sample it renders, and the API it belongs to are readable without
compiling anything. 18 groups, 50 components — the same 50 discovery finds, by construction.

Seven of the eight `glimmer-catalog` components pick up a `related` back-link by exact name
equality, which works here and does not in `samples-spec.mjs` (60-odd hand-written entries) because
this kit catalog names its components after the Glimmer APIs directly. `VoiceInputIndicator` is the
eighth: upstream writes the sample but no `@Preview` for it, so there is no group to link from.

Two things in the generator are guards rather than parsing, and both earned their place on the first
run:

- It matches the *whole* preview shape and then asserts the count against a plain `@Preview` scan.
  A looser regex silently dropped `GlimmerHorizontalPagerSamplePreview` — the one public preview
  among 50, wrapping its sample in a `Box` — and a dropped component is the bad failure here,
  because the sheet still publishes and still looks complete.
- It identifies the rendered function as *the one the file itself declares*, not by name. A name
  test was the first attempt and it was wrong: half of `CardSamples.kt` renders functions called
  `CardWithLongText` and `CardWithTitleAndLeadingIconAndHeader`, with no `Sample` in the name.

**Where the check now runs matters more than the fix.** `design-artifacts.yml` runs on `main` after
the merge, so a spec it rejects is a sheet that quietly stops publishing. `ci.yml` validates both
Glimmer specs on the pull request instead — no build, seconds — which is where the m3 pair was
already checked and where this one should have been from the start.

## Pinning the samples: the publish date, then the compiler

`glimmer-samples/import.json` is its own manifest rather than a third library in
`samples/import.json`. The two corpora compile against different classpaths — one CMP desktop, one
Android — so a file that must be quarantined in one is routinely fine in the other, and a shared
quarantine list would make each corpus' gaps unreadable. `scripts/import-samples.mjs` takes
`--manifest`, `--patches`, `--quarantine` and `--out` for exactly this.

Unlike material3 there is **no CMP artifact to fingerprint the pin against**, so `samples-drift.mjs`
has nothing to check here and the compiler is the only gate — the same position the Wear side's
samples are in. Deriving the ref was still not a guess, and the two wrong answers are instructive:

| Ref | What the compiler said |
| --- | --- |
| 2026-05-19 (reused from the adaptive pin) | `Card(action = …)` — a slot alpha19 does not have |
| 2026-09-09, the alpha19 publish date | `ImageCard` unresolved |
| **2026-09-09, one commit earlier** | compiles |

The middle row is the interesting one. glimmer 1.0.0-alpha19 was published at 17:01 UTC on
2026-09-09; the commit `9bca189d` — titled *"Introduce ImageCard API in glimmer"* — landed hours
after the release was cut, and rewrote the card samples against an API the published AAR does not
contain. So the pin is its **parent**, `4a14204d`. The Last-Modified rule places the ref within a
day; where a same-day API commit straddles the cut, the compiler says which side to be on.

One file stays quarantined for the same class of reason: `IconMarkerSamples.kt` calls an
`IconMarker` introduced on 2026-09-07 and likewise absent from the alpha19 AAR. That is version
skew expected to disappear on its own — when the pin moves to a release that ships it, re-running
the import without the entry is the check.

## What is built

| | |
| --- | --- |
| `:glimmer-catalog` | 7 components, 19 previews, all rendering |
| `:glimmer-samples` | 19 files vendored, 1 quarantined, 0 patches; 50 previews, all rendering, none blank |
| `androidx.annotation.Sampled` | a second local shim, beside `:samples-catalog`'s, because no published artifact provides it |
| `design-artifacts.yml` | two more `uses:` blocks and a `glimmer` output on the Scope job |
| `glimmer-design-map.json` | the eight kit references, projected from the annotations |
| `glimmer-samples/catalog.spec.json` | 18 groups, 50 components, generated by `scripts/glimmer-samples-spec.mjs` |

## Still to do

- **`@GlimmerEnvironmentPreview`.** The passthrough backdrops are the axis that makes an additive
  sheet legible, and they need `composeai-data-glimmer-environment-connector` on the classpath.
  Worth doing next; it is what turns these from "dark stickers" into pictures of the thing.
- **`catalogs.json` on preview.coo.ee**, with `attributionRepos: ["androidx/androidx"]` for the
  samples sheet.
- **A blank-render guard.** `:catalog` has `CatalogRenderTest` and the Wear repo renders before unit
  tests for exactly this; an additive sheet is the case where a blank frame is hardest to spot by
  eye, because black IS the ground.
- **`VoiceInputIndicator` never settles.** Its render warns `still_changing` — the bars animate
  indefinitely, so the sticker is a frame of an animation rather than a resting state.
