# Parameter knobs in this catalog

Compose-ai-tools carries two ways to declare a preview's editable knobs, and this catalog now uses
both. This file records which of its knobs have moved to the newer format, which cannot and why,
and how the move was verified not to change a pixel or a control. It is written so the question does
not have to be re-investigated from the source the next time it comes up — the upstream half of the
story is
[`compose-ai-tools/docs/design/PARAMETER_KNOB_MIGRATION.md`](https://github.com/yschimke/compose-ai-tools/blob/main/docs/design/PARAMETER_KNOB_MIGRATION.md),
and this file does not restate it.

The two formats, in one line each:

| | `previewOverride*` (named overrides) | Parameter knobs |
| --- | --- | --- |
| Where the knob is declared | by **executing a lookup in the composable body** | by the **function signature** |
| How a value is seeded | the harness writes it into a process-static controller | ordinary argument passing |
| What the sticker's body contains | a harness call per knob | nothing — no harness dependency at all |

## What has moved

Every knob the format supports: a literal default, a seedable kind (`String` / `Boolean` / `Int` /
`Long` / `Float` / `Double`), and a reading site the preview can own. Sixty-one knobs across
twenty-seven stickers, declaring on 618 of the module's 4149 captures.

| Sticker(s) | Knobs |
| --- | --- |
| `ProgressIndicators.kt` — linear, circular and both wavy | `progress` |
| `Chips.kt` — filter, input, suggestion | `trailing`, `closing`, `icon` |
| `TextFields.kt` — filled, outlined | `leading`, `trailing` |
| `TopAppBars.kt` — small, centre, medium, large | `nav`, `actions` |
| `BottomAppBars.kt` | `actions`, `fab` |
| `NavigationBars.kt`, `NavigationRails.kt` — rail and wide rail | `count`, `menu`, `fab` |
| `SegmentedButtons.kt` — single and multi choice | `count` |
| `Dialogs.kt` — basic, list | `icon` |
| `Dividers.kt` — horizontal, vertical | `context`, `subhead` (`inset` stays: `Dp`) |
| `Snackbar.kt` | `close` |
| `Badges.kt` | `label` |
| `SideSheets.kt`, `BottomSheets.kt`, `AdaptiveLayouts.kt` | the pilot's eight |

Where a knob was read in a private helper with more than one caller — `fieldSpec()`,
`RailHeaderContent()`, `NavIcon()` / `Actions()`, `SheetBody()`, `paneDirective()` — the helper takes
it as an ordinary parameter and each preview declares its own. That is the format's shape: a knob
belongs to the preview whose signature names it.

### How this was verified

The migration must be invisible: the same pixels, and the same controls on the preview server. Both
were checked whole rather than sampled, base against head:

* **Renders.** All 618 affected captures rendered before and after. **Every one byte-identical (md5).**
* **Preview-server control surface.** Every capture's `renders/<stem>.overrides.json` compared on
  key, type, default, current, index and options. **Identical across all 618** — including seeded
  cells, where `LinearProgress_VARIANT_full` still reports `progress` with default `0.5` and current
  `1.0`, which is what draws the control at the variant's value.
* **Inventory.** `previews.json`: 4149 previews both sides, ids identical *including order*, and no
  `targets` changed. What changed is `knobs` (0 → 618, the point) and `componentTargets` (richer,
  because the walk can now see into a capturing lambda).
* **Figma mappings.** `design-map.json` and `design-map-variants.json` regenerate byte-identically
  from the annotations, and all six mapping suites pass — `design-map`, `interaction-coverage`,
  `kit-coverage`, `duplicate-renders`, `import-figma-pages`, `exhaustive-kit-cells`.

### What that verification caught

Rendering the whole affected set is not ceremony. The first run produced **396 PNGs and 222
`.error.json` sidecars** — while the manifest still reported success. The parameter-knob format had
only reached the ordinary bake path; the **focus, motion and scroll** lanes could not resolve a
defaulted-parameter preview, did not bind its seed, could not invoke it with a partial seed, and did
not record its declarations into the sidecar. Every interaction-state cell of a migrated sticker was
affected, and those cells are the kit comparison addresses.

Fixed upstream in compose-ai-tools#5115; the four stages and their capture counts are in that PR.
**This migration needs the release that carries it.** Until then the module renders correctly only
against a patched renderer, which is why the version bump here moves in the same change.

## What cannot move at all

These are structural, not release-shaped, and are upstream gaps rather than defects here:

| Knob family | Why |
| --- | --- |
| `catalogText(...)` everywhere | its defaults are `stringResource(...)` expressions, which cannot be recovered and so cannot be declared; many are indexed per row, which a fixed-arity parameter list cannot express; and it is one helper serving dozens of stickers |
| `catalogChoice(...)` / the axes in `CatalogAxes.kt` | a `String` parameter carries no closed value set, so the viewer would draw a text field where the axis wants a picker |
| `CatalogFocus.kt`'s `focusOverlayColour`, `focusRingOuterColour`, `focusRingInnerColour` | `Color` is not a seedable parameter kind, and they are read in a wrapper on behalf of every sticker |
| `Dividers.kt`'s `inset`, `Carousel.kt`'s `preferredItemWidth` | `Dp` is not a seedable parameter kind |
| `CatalogSizes.kt`'s `catalogToggleSelected`, `CatalogInteractive.kt`'s `clickCount` | declared in a shared helper or wrapper, with the default supplied by the caller |
| `TimePickers.kt`'s `hour` / `minute` | read in the dialog frame three pickers share, so moving them would copy both onto all three |
| `DatePickers.kt`'s date knobs | the default is an expression (`default.toString()`), which cannot be recovered and so cannot be declared |

`Shapes.kt` **could** move — four literal-defaulted `Float` / `Int` knobs in one preview — and
deliberately does not. It is the in-repo reference for what the lookup format reads like, the same
way compose-ai-tools keeps `OverridablePreviews.kt` beside `ParameterKnobPreviews.kt`; keeping it
also keeps a live `previewOverrideFloat` and `previewOverrideInt` in the sources, which is what
`CatalogOverrideSurfaceTest` asserts.

`CatalogOverrideSurfaceTest` keeps one live call of each of the six `previewOverride*` kinds in the
sources, so the control contract the preview server reads stays exercised whatever else migrates.
That test is a floor, not a ceiling — but a migration that empties a kind out of the catalog has to
answer it deliberately rather than by deleting the assertion.

## When to reach for which

* **Parameter knobs** when the sticker is self-contained and the editable values are its own
  arguments — the shape it would have if real code called it — and no baked variant seeds them.
* **`previewOverride*`** when the knob is a colour or a dimension, when each row of a repeated
  component needs its own value, when the value set is closed and a picker is the right control, or
  when one wrapper declares the knob for many stickers.

Neither replaces the other, which is why the catalog uses both.
