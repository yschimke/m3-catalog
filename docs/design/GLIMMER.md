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

## The inventory has no kit, and says so

`AGENTS.md` makes membership the kit's call and fails the build for a `@CatalogComponent` with no
`reference`. That rule is about the **Material 3 Design Kit**, which is `m3-catalog`'s subject.
Glimmer publishes no Figma kit at all, so every component here carries `noReference` with the reason
instead — the schema's own field for "the absence is a finding, not a gap nobody has looked at".

Consequently both Glimmer jobs in `design-artifacts.yml` publish an **empty design map** and carry
no `figma_token` and no `reference-cache-branch`. 0% parity coverage is the truthful number, not a
missing integration.

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
