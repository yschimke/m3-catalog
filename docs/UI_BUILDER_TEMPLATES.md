# Adaptive starting templates

`ui-builder.policy.json` offers four ordinary design documents under `ui-builder/designs/`.
The catalog owns these seeds; the shared publisher carries the policy's branch-relative paths and
files onto the delivery branch. A host must consume the published templates to offer them in New
design; declaring them here does not change the deployed host's packaged chooser.

| Starting point | Structure | Intended use |
| --- | --- | --- |
| Blank screen | Scaffold → empty Box | A screen with no prescribed content arrangement |
| Adaptive navigation | NavigationSuiteScaffold → destination body | Top-level destinations; bar or rail chosen by Material |
| Supporting pane | Scaffold → SupportingPaneScaffold | Primary content with related context or tools |
| Adaptive feed | Scaffold → LazyVerticalGrid with adaptive cells | A collection of equivalent cards |

These are small editable layout seeds, not finished apps. The navigation destinations are
placeholders: wire selection and destination content to the app's navigation state. The supporting
pane uses the released builder's adaptive directive and a **preferred** 360dp support width. Its
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

## Builder capabilities needed next

The released builder vocabulary was checked with preview-server 3.100.0. Do not advertise the
following as supported by these documents:

1. **List-detail starter.** Add a dedicated ListDetailPaneScaffold adapter, `listPane`, `detailPane`
   and optional `extraPane` slots, a navigator with selection/content keys, compact back behavior,
   and matching source export. It is not a SupportingPaneScaffold renamed to list-detail.
2. **Fixed/flexible sizing.** Expose preferred, split, fixed-start and fixed-end policies, fixed width
   and split fraction in the schema, inspector, constrained canvas and export. Preferred width alone
   is a hint and must never be labeled fixed. Check the fixed width at two expanded window sizes.
3. **Resize handles.** Add keyboard/touch-accessible drag handles and 360dp, 412dp and centered split
   anchors. Persist user sizing for list-detail; supporting-pane resizing can be temporary.
4. **Extra-large third pane.** Add optional extra content and explicit visibility/navigation policy;
   do not force three panes merely because the window is large.
5. **Alternative adaptation.** Show/hide, reflow below the main pane and levitate as a floating or
   docked pane are separate strategies. The current starter uses show/hide only.

These belong in compose-ui-builder's adapters and export, with catalog-owned vocabulary here (and
shared structural vocabulary in compose-foundation). Until those lanes agree, list-detail stays out
of the chooser rather than publishing a broken template. The Jetcaster reference design remains
owned by the builder and is not a catalog starting template.

## Verification

The local builder's create route validates document shape, slots and property capabilities; its
Compose export diagnostics check the source projection. PNG exports are rendered document views,
not editor viewport screenshots. A zero-diagnostic export still needs compilation against the
catalog's library versions. Repository tests also check that chooser paths exist, trees are reachable
and template-only changes dirty the m3-catalog publishing lane.
