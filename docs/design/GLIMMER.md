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
`backgroundColor = 0xFF000000`. What the published PNG shows as black is exactly what the glasses
leave unlit; everything else is light the UI adds. A sheet that composited Glimmer onto white would
be a picture of something the hardware cannot produce — which is also why `catalog.spec.json`
declares a single `dark` mode and a dark display surface rather than the light/dark pair every other
system here carries.

### The glasses display is a measuring bound, not a frame

Nothing in this module names a device. The stickers are device-less `@Preview`s, and
`PreviewDiscovery.retargetGlimmerStickers` — added upstream in compose-ai-tools#5435 — gives a
module that compiles against `androidx.xr.glimmer` the AI-glasses display as its **wrap sandbox**:
960x720 at **density 1.0**. So 1dp is still 1px and a token's declared size is still its size in the
PNG, but each sticker crops to its own measured bounds instead of sitting in a screen it does not
fill.

The distinction is the whole of #367. This module used to write
`device = "spec:width=960,height=720,dpi=160"` on all 19 previews, and a device PINS the frame — the
intrinsic crop never runs. A 118x48 toggle button shipped adrift in a 691,200-pixel black frame, and
the sheet averaged **4.1%** content coverage. It is 96% now, with 18 of the 19 at exactly 100%; the
odd one out is `VoiceInputIndicator`, whose 24x10 bars sit in the component's own 32dp box.

Density is the half worth not losing. Glimmer sizes UI in **visual angle**, not dp: at ~30 pixels
per degree the library's type and targets land on the angular sizes it is calibrated for (18dp text
-> 18px -> 0.6 degrees), and that identity holds only at density 1.0. Dropping the device spec
without the retarget would have handed these stickers the renderer's 2.625 phone default — further
from calibration than the dpi=240 that `samples/xr-glimmer` had already rejected for reading
contrast and legibility optimistically.

It also fixes what `fillMaxWidth` resolves against. `Card` and `ListItem` measure to 960dp — the
display — rather than to a 400dp phone bound they have no relationship to.

The `Sticker` wrapper is now one line, `GlimmerTheme(content = content)`. The
`fillMaxSize().background(Color.Black).padding(24.dp)` it used to carry was the capture's job in the
tree: the ground belongs on `showBackground`, and in-tree padding is the mistake
`CatalogCaptureGutters.kt` already names (#179) — it measures the component in a smaller box and
grows the canvas. It stays a function rather than being inlined at 19 call sites because it is still
the seam that swaps to compose-ai-tools' `GlimmerSurface` when that is published from a runtime
artifact.

### The capture is radiance, and the compositing is additive

`@GlimmerEnvironmentPreview` (Light / Dark / Busy / VeniceCanalCats) composites the same additive
capture over a passthrough scene, which is what a wearer actually sees. It is applied now —
`GlimmerEnvironments.kt`, four scenes each for `Button` and `Card`.

Doing that settled what the capture convention above actually means, and it is worth stating because
[#380](https://github.com/yschimke/m3-catalog/issues/380) reads as though the opaque ground were a
bug. `data-glimmer-environment-connector`'s compositor is, per channel,

    min(255, backdrop + sticker)

— clamped **additive**, with the alpha byte never read. The capture is a RADIANCE image: black is
zero light, and the panel can only ever add to the scene. So opaque additive-zero black is the
correct input to that path, not a placeholder for transparency.

Which is why `showBackground = false` is **not** the fix #380 wants, measured rather than argued: it
does produce real alpha (colour type 6, min alpha 0 against a published sticker's 255), and it
removes only the unpainted frame around a component — 1.3% of pixels on `ListItemSticker` — because
Glimmer's own containers are opaque `#303030` surfaces. The black rectangle in that report is mostly
the component. And alpha says the wrong thing besides: alpha OCCLUDES (a dim pixel darkens what is
behind it) where this surface EMITS. What is missing is not an alpha channel but a way for the sheet
to declare "these pixels are light to be added", which no field in `catalog.spec.schema.json`
expresses — [#394](https://github.com/yschimke/m3-catalog/issues/394). The environment axis is stuck
one layer down for a related reason: the scene is a capture property rather than something a viewer
can pick — [#395](https://github.com/yschimke/m3-catalog/issues/395).

## The kit: found late, and the components now name it

**This document originally said Glimmer publishes no Figma kit. That was wrong**, and the error is
worth keeping visible rather than editing away: the claim was made from the absence of a kit in
`androidx.xr.glimmer`'s own publishing, and a community kit was never looked for. There is one:

> [Jetpack Compose Glimmer UI (Community)](https://www.figma.com/design/HKfLClZDLRyMhf4IQQLna8/Jetpack-Compose-Glimmer-UI--Community-)

Every component shipped with `noReference` and a reason that read "no kit node exists to name". That
is worse than a missing reference — it is an *audited* absence that was never audited, which is
exactly the distinction the schema's own wording draws. All twelve now carry a real `reference`, each
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
| `VerticalStack` | `40000042:4077` | `Type=Cards \| Home` |
| `ButtonGroup` | `40000116:6496` | `Type=` x `Length=` x `Focus=` |
| `GlimmerLazyColumn` | `4116:5211` | the one-line list set, including its title and scroll-position axes |
| `GlimmerLazyColumn/TwoLine` | `4116:5250` | the two-line list set populated through `ListItem.supportingLabel` |
| `VoiceInputIndicator` | `40000116:9337` | `Mic Indicators`: `Volume=` x `Contained=Yes \| No` |

The ids are the kit's own component sets, read from the file's `🧩 Components` page. `Card` and
`TitleChip` were confirmed by rendering the node rather than by name alone — the others are
identified by their variant axes, which is stronger evidence than a name.

`scripts/glimmer-design-map.sh` projects those twelve into `glimmer-design-map.json`, and the publish
job copies it into place. It replaced a `design-map-command` that projected an empty map.

### What is still missing

- **The kit index exists now, and it did not do what was expected.**
  `glimmer-figma-kit-index.json` holds the kit's 10 component sets, their 78 variants and 2
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

  **Twenty-one resolve today**, where one did when this was written (Button `size=Large`). The
  difference is the `State` axis below: an interaction cell's name is the kit's own value, so
  `focused` meets `State=Focused` and the size crossings come with it. What still misses is the
  handful whose props are Compose's vocabulary rather than the kit's — ToggleButton and
  IconToggleButton `state=checked` against `Toggle=True`, VoiceInputIndicator `container=contained`
  against `Contained=Yes`, ListItem `content=supporting-label` against `Type=2-line` — which is
  #374's step 2 and still wants the generated view rather than a rename.

- **The kit's `State` axis is drawn now, and the coverage is 41 of the original six sets' 55
  cells.**
  [#374](https://github.com/yschimke/m3-catalog/issues/374) measured the old number and named the
  cause: a generated kit cell is emitted only when every axis it changes is backed by an authored
  variant that resolves, and `State` had nothing behind it — this module drew no interaction states
  at all. `GlimmerStates.kt` is that backing, and the resolver picks it up with no change to
  `scripts/glimmer-design-map.sh`:

  | set | at #374 | states drawn | + kit vocabulary and the generated cells |
  | --- | --- | --- | --- |
  | `Button` | 2/10 | 8/10 | 8/10 |
  | `Toggle Button` | 1/16 | 8/16 | **14/16** |
  | `Toggle` (icon) | 1/8 | 4/8 | **7/8** |
  | `Icon button` | 1/5 | 4/5 | 4/5 |
  | `List Item` | 1/12 | 2/12 | **4/12** |
  | `Mic Indicators` | 1/4 | 1/4 | **4/4** |
  | **total** | **7/55** | **27/55** | **41/55** |

  `focused` and `pressed` are driven by the RENDERER against the composed node — the distinction
  `:catalog`'s `CatalogInteractionAnnotations.kt` makes, and it works on this module's Robolectric
  lane, which was the open question. `disabled` seeds a `status` knob, because it is a parameter
  rather than an interaction.

  What is still uncovered is uncovered for a stated reason rather than for want of drawing:

  | cells | why |
  | --- | --- |
  | `Disabled+Focused` (6) | a disabled Glimmer component takes no focus: pinned `enabled = false`, the focus-driven capture is BYTE-IDENTICAL to the resting one. The render is its `Disabled` render, and a duplicate cell is a build failure here. [#392](https://github.com/yschimke/m3-catalog/issues/392) |
  | `State=Hovered` (2, `List Item` only) | Glimmer draws no hover treatment — measured byte-identical to the resting capture — because a glasses surface has no pointer. The kit's `List Item` set is the one that names `Hovered` where its siblings name `Focused`; this catalog draws the focus Glimmer actually has. [#392](https://github.com/yschimke/m3-catalog/issues/392) |
  | `List Item` `State=Disabled` (2) | alpha19's `ListItem` has **no `enabled` parameter**. There is no call that draws a disabled row — an upstream gap rather than a missing sticker, declared as `ListItemDisabled` in `glimmer-kit-gaps.json` |
  | `List Item` `Type=Card` (4) | Glimmer publishes `Card` and `ListItem` as separate components and no card-shaped row type on `ListItem`. Declared as `ListItemCard` in `glimmer-kit-gaps.json` |

  The first two rows are a different kind of gap from the rest, and
  [#392](https://github.com/yschimke/m3-catalog/issues/392) is where they live now: **eight cells
  whose state no Glimmer component can be in.** `design-led` normally says the code moves, and
  those eight are the case it cannot reach — there is no call, parameter or interaction that
  produces the picture, so authoring them would publish the `Disabled` and resting renders again
  under other names, which `scripts/duplicate-renders.mjs` fails the build for. The last two rows
  are upstream API gaps, checked rather than described: `scripts/glimmer-kit-gaps.mjs` fails the
  build if the kit drops the node, if a sticker starts mapping it, or if Renovate bumps the library
  the evidence was read from.

  **What closed the other fourteen cells** was #374's steps 2 and 3, and the escape hatch the issue
  asked for turned out to exist already:

  - **`kitAxis` / `kitValue` on `@CatalogVariant`** carries the kit's spelling beside Compose's, so
    `state=checked` resolves against `Toggle=True`, `container=contained` against `Contained=Yes`
    and `content=supporting-label` against `Type=2-line` without the sheet having to say `True`,
    `Yes` or `2-line` to a reader. #374's comment tried `reference` and correctly found it ignored;
    these two are the fields the resolver reads (compose-ai-tools#4086), and `:catalog` has used
    them on the date pickers, text fields and chips since.
  - **`scripts/generate-exhaustive-kit-cells.mjs` now takes its inputs as arguments**, so the same
    generator runs against the Glimmer kit — the port #374 planned, as a generic input rather than
    a forked copy. It writes `glimmer-exhaustive-kit-cells.json` and a generated annotation file
    beside the stickers, and the eight cells it emits are the `Toggle=True` x `State=` x `Size=`
    crossings and `Volume=Quiet, Contained=Yes`.
  - **The base stickers grew the knobs those cells seed.** This is the part that is easy to get
    wrong: a generated cell seeds a knob on the sticker it is attached to, and `glimmer-catalog`
    writes a composable per variant, so a `size=Large` seed on a sticker that hardcodes the default
    size would have published the default pixels at the kit's `Size=Large` address. `size`, `state`
    (checked), `volume` and `container` are knobs on the base stickers now, which is also what
    `AGENTS.md` asks for — a pinned value is invisible in the live lane's Overrides panel.

- **The parity lane exists and has published.** `design-parity.yml`'s `glimmer` job carries the
  token and the `design-parity/glimmer-reference` cache, and the board lands on
  `design-parity/glimmer`. What its first run said, and what this catalog did about it, is
  **The first board, and what it moved** below.
- **The direction is settled, by precedent.** An earlier draft of this section treated the kit's
  "(Community)" suffix as weaker authority than a first-party file, and asked whether
  `.design-parity.json`'s repo-wide `design-led` should apply. That was a misreading: **the Figma
  Community is how Google publishes its design kits publicly**, and the file this whole repository
  is built to reproduce — `Material-3-Design-Kit--Community-` — carries exactly the same suffix. The
  Glimmer kit is the public edition of Android's own, not a third-party redraw, so it inherits the
  same authority the Material kit has: the kit is authoritative and a divergence is a bug in this
  code.
- **The interaction states are drawn** — `GlimmerStates.kt`, and the coverage table above is what
  they bought. [#374](https://github.com/yschimke/m3-catalog/issues/374)'s steps 2 and 3 landed with
  them: `kitAxis` / `kitValue` keeps a reader-facing variant name beside the kit's own property
  vector, and the exhaustive generator runs against this kit too.
- **The taxonomy has not been re-checked against the kit.** These seven components and their variant
  folds were derived from the API surface alone. `AGENTS.md` says membership is the kit's call, and
  that rule now has something to say here — the kit also publishes Button groups, a Progress
  indicator, Entity (avatars, monograms, app icons) and Stacks, none of which this catalog draws.

## The first board, and what it moved

`design-parity/glimmer` published its first report from `91bef40e` (board `659f047`): status `warn`,
nothing blocked — `block-on-verdict: false` on this lane. It reported 30 visual findings, 5 pairing
findings, 1 layout finding, 5 semantic findings and 5 i18n findings across the eight components, and
[#381](https://github.com/yschimke/m3-catalog/issues/381) established that nearly all of the visual
ones had a single cause, stated in the board's own pairing lines:

> reference renders with `Label=true, Label Text=Button, Show Leading icon=true, Show Trailing
> icon=false` — component property defaults the variant name does not state

**The kit cells draw their slots filled; the stickers drew bare components.** The variant a sticker
names — `State=Enabled, Size=Default` — says nothing about a leading icon, and the kit turns one on
anyway. So the comparison was scoring two different pictures, and design-led says the code moves.

The numbers that made it look worse than it was: design-parity normalizes the reference to the
CANDIDATE's width before comparing, so Button's 146x48 cell became 77x25 against our 77x48 and the
board reported "67.4% of pixels differ". That was an aspect-ratio artifact of the missing icon, not
a button drawn wrong — and the heights matched exactly everywhere the slots did (48/48, 44/44,
80/80, 32/32).

Two changes, both of them #381's own options:

**The base stickers now draw what their cell draws.** Leading icon and the label "Button" on
`Button` and `ToggleButton`, an entity and "Title Chip" on `TitleChip`, icon and "Title" on
`ListItem`, and the kit's full content set on `Card` — header image, entity, title, subtitle, body.
The BARE forms did not disappear; they inverted into variants (`content=label-only`,
`content=text-only`), which is the honest taxonomy once the populated form is what the kit publishes
as the base. `Card`'s `Show Action` layer is the one the base cannot carry — alpha19 puts the action
slot on `ActionCard`, and a sticker published as `Card` has to invoke `Card` — so it folds in as
`content=action`, the treatment `AGENTS.md` gives every "it is a separate composable" axis.

**`Card` and `ListItem` are bound to the kit's 420dp column** (`ContentFrame`), where they were
filling the 960dp glasses display #376 made the wrap sandbox — 2.3x the width the kit draws them at.
It goes on a FRAME rather than on the component because 420 is the component's own measured extent
in the kit rather than a number a caller passes, and because `Modifier.width` would hand a card that
fills its width a tight minimum.

What that does to the measurements, rendered locally against the cells #381 read from the reference
cache:

| component | kit cell | before | after |
| --- | --- | --- | --- |
| `Button` | 146 x 48 | 77 x 48 | 130 x 48 |
| `ToggleButton` | 146 x 48 | 121 x 48 | 130 x 48 |
| `TitleChip` | 146 x 44 | 107 x 44 | 134 x 44 |
| `ListItem` | 420 x 80 | 960 x 80 | **420 x 80** |
| `Card` | 420 x 412 | 960 x 80 | 420 x 371 |
| `IconButton` | 48 x 48 | 48 x 48 | 48 x 48 |
| `IconToggleButton` | 48 x 48 | 48 x 48 | 48 x 48 |
| `VoiceInputIndicator` | 32 x 32 | 32 x 32 | 32 x 32 |

The residue is small and named rather than swept up. The 16dp the buttons and the chip still give
away is the kit's own icon-to-label spacing against Compose's; `Card`'s 41dp is the header image's
aspect against the kit's photograph. `ButtonSize.Large` is the one that did not improve — 72dp
against the cell's 64 — and it is a token difference rather than a content one.

**What the board reported that this did NOT fix**, each already tracked:

- [#382](https://github.com/yschimke/m3-catalog/issues/382) — `Card`, `ListItem`, `TitleChip` and
  `VoiceInputIndicator` expose no accessibility roles, and the indicator no label either. That is a
  property of what the LIBRARY puts in the semantics tree; papering over it with
  `Modifier.semantics` at the call site would make these stickers more accessible than the library
  they document and hide the gap. It stays honest reporting until the upstream question is settled.
- [#383](https://github.com/yschimke/m3-catalog/issues/383) — every label needs roughly 2x its width
  when localized and none has it. Taking the kit's copy shortens four of the five strings, which
  moves the numbers without settling the question; #358 (string resources) comes first either way.
- [#388](https://github.com/yschimke/m3-catalog/issues/388) — `VoiceInputIndicator` never settles,
  so its 90.2% pixel difference is measuring an arbitrary animation frame rather than a divergence.
- The icon GLYPH on `IconButton` / `IconToggleButton`, which is the whole of those two cells'
  content. That one is **fixed**: see *The kit names its glyphs* below.
- [#389](https://github.com/yschimke/m3-catalog/issues/389) — the `token` findings, all `info`:
  Glimmer's colour and typography tokens have no Material-role mapping for design-parity to resolve
  them through, so the whole token lane reports `unverified` rather than compliant or divergent.
  That one lives in the comparison tool; nothing in this catalog can move it.

## The kit names its glyphs, so the catalog does not get to choose

Every icon slot in the Glimmer kit is a **Material Symbol**, and each cell names its glyph by
ligature in the node itself — read from the reference cache rather than guessed at:

| kit node | slot | glyph |
| --- | --- | --- |
| `Button` `40:660`, `40000113:3576` | `Leading icon`, `Trailing icon` | `send` |
| `Toggle Button` `40000113:3991` | `Leading icon`, `Trailing icon` | `send` |
| `List Item` `384:4195` | `System Icon` | `send` |
| `Icon button` `5315:4651` | `System Icon` | `mic_off` |
| `Toggle` `40000113:4150` | `System Icon` | `mic_off` |
| `Title chip` `5315:4722`, `Card` `416:2700` | `Entity` | an avatar / product icon, not a symbol |

So the stickers draw `Icons.AutoMirrored.Rounded.Send`, `Icons.Rounded.MicOff` (and
`Icons.Rounded.Mic` when the toggle is checked, which is the pair the kit's vocabulary implies), and
`Icons.Rounded.AccountCircle` where the kit places an entity. Rounded because the kit's own family is
Material Symbols **Rounded**.

That retires the reason this module used to give for drawing its own star — *"Glimmer is not
Material, and putting a Material icon set on this module's classpath would invite exactly the mix-up
this catalog exists to avoid"*. The premise was wrong: Glimmer's kit draws Material Symbols
throughout, so a Material icon is the kit's own choice rather than a category error. `StarIcon` is
gone and `androidx.compose.material:material-icons-extended` is on the module.

**The published set is the source, not the kit's vector data.** Lifting the exact path out of the
reference SVG was tried first and is the wrong answer even though it is the authoritative side of
the comparison: a copied path is a snapshot that cannot track the kit, it carries no licence trail,
and it makes the catalog draw something the library it documents does not ship. The same reasoning
retired `figmaStars` in `:catalog` — see `CatalogIcons.kt`, where the kit's `stars` placeholder had
been copied as path data and hand-scaled, and is now `Icons.Filled.Stars` / `Icons.Outlined.Stars`.

What is left is one residual worth stating: the kit draws `send` at Material Symbols' **fill 0** — a
hollow paper plane — and the published Compose icon set has no unfilled `send`, in any of its five
styles. `Icons.AutoMirrored.Rounded.Send` is the closest thing that ships; the fill axis is not
something to redraw by hand.

## The kit types in Google Sans Flex, and so does the sheet now

The kit's glyphs were settled first (above); its **typeface** was not, and the sheet was typing in
the wrong one the whole time.

Read off the 35 cached reference nodes on `design-parity/glimmer-reference`, every text run in the
kit is one family:

| family | role | runs |
| --- | --- | --- |
| Google Sans Flex | all text | 74 |
| Material Symbols Rounded | icon font | 111 |
| Material Symbols Outlined | icon font | 4 |

`GlimmerTheme()` with no `typography` argument types in the platform default, which on this
Robolectric lane is Roboto. So `Button`, `Card`, `TitleChip` and `ListItem` were each being scored
against the kit in the wrong face — a difference no amount of layout work could close, and one that
would have kept re-appearing on the board as an unexplained text-layer diff.

`androidx.xr.glimmer:glimmer-google-fonts` exists for exactly this and now backs
[`Sticker`](../../glimmer-catalog/src/main/kotlin/ee/schimke/m3catalog/glimmer/GlimmerCatalog.kt):

```kotlin
GlimmerTheme(typography = createGoogleSansFlexTypography(), content = content)
```

Taking the typography from the library rather than hand-building a `FontFamily` matters because
Google Sans Flex is a **variable** font and the kit uses it as one: its labels sit at weight 725 and
750, which are axis positions rather than named styles. `createGoogleSansFlexTypography()` carries a
`FontVariation.Settings` per role, so the axis values come from Glimmer instead of from a reading of
the kit.

### Why a downloaded face is allowed here

`gradle/libs.versions.toml` used to exclude this artifact, with the note that it *"resolves typefaces
over the network at render time, which a reproducible sticker sheet cannot depend on"*. The premise
was right; the conclusion did not follow, because the render already has machinery for precisely
this case:

* the faces land in `$XDG_CACHE_HOME/composeai/fonts` (else `~/.cache/composeai/fonts`), the shared
  cache every render lane restores before and saves after — including the design-parity glimmer lane,
  keyed `composeai-fonts-<os>-<system>-`;
* `composeai.fonts.failOnFallback` is **ON by default**, so an unresolved `Font(GoogleFont(...))`
  FAILS the preview rather than quietly substituting Roboto.

The sheet is reproducible because an irreproducible one cannot pass — a stronger guarantee than
never asking for the font.

### Proving the cache is actually hit

Not asserted, measured. Three runs of `:glimmer-catalog:composePreviewRenderAll`:

| run | cache | `fontsOffline` | result |
| --- | --- | --- | --- |
| 1 | cold | off | 59 renders, `google-sans-flex-400.ttf` appears in the cache |
| 2 | warm | **on** | BUILD SUCCESSFUL — served entirely from cache, no egress |
| 3 | **cleared** | **on** | BUILD FAILED (the control) |

Run 3 is the one that carries the proof. Without it, run 2 only shows that the build passed:

```
FontFallbackException: Downloadable font(s) fell back to the platform default (Roboto), so this
preview would render in the wrong typeface. … Unresolved: Google Sans Flex (weight=400) — offline
(composeai.fonts.offline=true) and the face was not already cached
→ 3 previews, e.g. ButtonsKt.ButtonSticker, CardsKt.CardSticker, ListsKt.ListItemSupportingSticker
```

So the font is genuinely required by the tree, the cache is what serves it, and the guard fires when
it cannot. Across all 59 renders every `*.warnings.json` sidecar reports `"fontFallbacks": []`; the
22 sidecars that do carry a warning are the pre-existing `unsettledCaptures` on animated
focused/pressed variants.

### Pre-populating, and what is missing

No repo change is needed to warm the cache in CI: the reusable workflow already restores it before
the render and saves it after, and the restore-keys fall back to this catalog's prior runs and then
to any catalog's, so only a genuinely first-ever run fetches live.

What has no first-class mechanism is a **deterministic seed** for that first run. It matters more
here than elsewhere, and compose-ai-tools' own cache documentation says why: the Google Fonts CSS API
can serve a different face for the same `(family, weight, italic)` — a static sub-font at the exact
weight, or the family's variable TTF — and those have different text metrics. The cache removes that
coin flip only *after* the first fetch. For a sheet whose whole purpose is pixel comparison against
the kit, the first fetch deciding which metrics the baseline carries is a real exposure. There is no
`fonts-prefetch` input and no `composeai fonts warm` command to close it with, so it is written down
here rather than papered over with a local hack.

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

Ten of the twelve `glimmer-catalog` components pick up a `related` back-link. Eight join by exact
name equality; the two explicit mappings connect `StackSamples.kt` to `VerticalStack` and
`GlimmerLazyListSamples.kt` to `GlimmerLazyColumn`. That stays much smaller than
`samples-spec.mjs`'s 60-odd hand-written entries and avoids unsafe prefix matching. The two
components without their own link are `GlimmerLazyColumn/TwoLine`, which is another kit set of the
same sample-backed API, and `VoiceInputIndicator`, whose upstream sample has no `@Preview`.

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
| `:glimmer-catalog` | 12 components, 56 previews (26 stickers, 22 state cells, 8 environment composites), all rendering |
| `:glimmer-samples` | 19 files vendored, 1 quarantined, 0 patches; 50 previews, all rendering, none blank; published WITH a live bundle |
| `androidx.annotation.Sampled` | a second local shim, beside `:samples-catalog`'s, because no published artifact provides it |
| `design-artifacts.yml` | two more `uses:` blocks and a `glimmer` output on the Scope job |
| `glimmer-design-map.json` | the twelve kit references, projected from the annotations |
| `glimmer-samples/catalog.spec.json` | 18 groups, 50 components, generated by `scripts/glimmer-samples-spec.mjs` |

## Still to do

- **The environment axis, past the eight pictures it has now.** `GlimmerEnvironments.kt` composites
  `Button` and `Card` over all four scenes, which is what turns these from "dark stickers" into
  pictures of the thing. It stops there because `@GlimmerEnvironmentPreview` is not repeatable and
  the scene is baked into the capture rather than chosen by the viewer: the cross-product is
  hand-written, and a reader cannot ask "how does this look against MY scene".
  [#395](https://github.com/yschimke/m3-catalog/issues/395).
- **`catalogs.json` on preview.coo.ee**, with `attributionRepos: ["androidx/androidx"]` for the
  samples sheet.
- **A blank-render guard.** `:catalog` has `CatalogRenderTest` and the Wear repo renders before unit
  tests for exactly this; an additive sheet is the case where a blank frame is hardest to spot by
  eye, because black IS the ground.
- **`VoiceInputIndicator` never settles.** Its render warns `still_changing` — the bars animate
  indefinitely, so the sticker is a frame of an animation rather than a resting state, and the
  parity board scores that frame against the kit's resting one.
  [#388](https://github.com/yschimke/m3-catalog/issues/388).
