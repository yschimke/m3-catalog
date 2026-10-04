# Adaptive starting templates

`ui-builder.policy.json` offers five ordinary design documents under `ui-builder/designs/`.
The catalog owns these seeds; the shared publisher carries the policy's branch-relative paths and
files onto the delivery branch. A host must consume the published templates to offer them in New
design; declaring them here does not change the deployed host's packaged chooser.

| Starting point | Structure | Intended use |
| --- | --- | --- |
| Blank screen | Scaffold → empty Box | A screen with no prescribed content arrangement |
| Adaptive navigation | NavigationSuiteScaffold → destination body | Top-level destinations; bar or rail chosen by Material |
| List-detail | Scaffold → ListDetailPaneScaffold | A collection and independently meaningful selected details |
| Supporting pane | Scaffold → SupportingPaneScaffold | Primary content with related context or tools |
| Adaptive feed | Scaffold → LazyVerticalGrid with adaptive cells | A collection of equivalent cards |

These are small editable layout seeds, not finished apps. The navigation destinations are
placeholders: wire selection and destination content to the app's navigation state. The supporting
pane uses an adaptive directive and a **fixed** 360dp support width in the companion builder. Its
compact preview shows main content; access to hidden supporting content needs app navigation. The
feed's 280dp minimum cell width is a starter content choice, not a Material token. Replace its cards
and copy. Every document declares compact, medium and expanded export devices; test the actual
product at its additional window sizes and font scales.

## Guidance reviewed

- [Adaptive design](https://m3.material.io/foundations/layout/layout-overview/adaptive-design): adapt
  structure and behavior to the current window, input methods and usage, rather than merely scale.
- [Panes](https://m3.material.io/foundations/layout/scaffold/panes): one to three visible panes; every
  layout needs at least one flexible pane. Compact recommends one pane, medium one with two as an
  option, expanded and large two, and extra-large can optionally have three.
- [Canonical layouts](https://developer.android.com/develop/ui/compose/layouts/adaptive/canonical-layouts):
  list-detail, supporting pane and feed. List-detail selection opens independently meaningful detail;
  supporting content depends on the main content. These relationships require different scaffolds.
- [Adaptive navigation](https://developer.android.com/develop/ui/compose/layouts/adaptive/build-adaptive-navigation):
  a NavigationSuiteScaffold is the shell around a destination, not a fourth content relationship.

## Pane sizing in the catalog previews

`adaptive/AdaptiveLayouts.kt` keeps its real ListDetailPaneScaffold and SupportingPaneScaffold
previews outside the kit inventory. Both expose `paneSizing`:

| Value | Behavior with two visible panes |
| --- | --- |
| preferred | Material's own default measure policy |
| split | Two flexible panes; `splitFraction` sets the first pane's proportion, default 0.5 |
| fixedStart | First pane fixed at `fixedPaneWidth`, default 360dp; second takes remaining space |
| fixedEnd | Second pane fixed at `fixedPaneWidth`; first takes remaining space |

The explicit policies use the library's PaneExpansionState, not Modifier.width or preferredWidth.
They apply within the scaffold's bounds and defer single-pane and hinge layout to Material. On a
single-pane window the visible pane fills its partition. `twoPanesOnMedium` remains independent of
sizing. Changing sizing is a live knob, not a baked variant that repeats the compact default.

Material describes a split with its spacer centered in the **whole window** including navigation.
The preview has no surrounding navigation rail, so its default 50% split is also window-centered.
A builder integrating navigation must account for rail width before claiming that same alignment.

## Builder support

The companion compose-ui-builder change adds the dedicated list-detail component, optional extra
slot, preferred/split/fixedStart/fixedEnd policies, inspector controls, constrained rendering and
Kotlin export. The same four adaptive starters appear in the packaged New design chooser.
`fixedPaneWidthDp` and `splitFraction` describe the expansion anchor; preferred widths remain hints.
Sizing applies to two visible panes. Compact windows give the active pane its whole partition.

`activePaneIndex` accepts an integer or integer selection state: 0 is list, 1 is detail,
2 is extra. The catalog starter has placeholders for selection/back wiring. In builds enabling
stateful authoring (`uiBuilderRemoteCompose`), the packaged starter binds Open item and Back to list
to selection state; the stable build keeps a literal initial list destination. An app should
connect its own content key and system back handling; this controlled destination is not a saved
navigation back stack. Gmail and Calendar's first-party builder fixtures use the proper list-detail
scaffold rather than the supporting-pane workaround.

Drag handles and persisted user resizing, automatic three-pane extra-large policy, reflow and
levitate strategies remain future additions. Published templates also need a host version that
supports the new vocabulary; preview-server 3.100.0 predates that support. The Jetcaster reference
design remains builder-owned rather than a catalog starting template.

## Verification

The local builder's create route validates document shape, slots and property capabilities; its
Compose export diagnostics check the source projection. PNG exports are rendered document views,
not editor viewport screenshots. A zero-diagnostic export still needs compilation against the
catalog's library versions. Repository tests also check that chooser paths exist, trees are reachable
and template-only changes dirty the m3-catalog publishing lane.
