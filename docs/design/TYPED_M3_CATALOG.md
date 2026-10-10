# Parallel Kotlin-authored M3 catalog

The production catalog still reads `ui-builder.policy.json` and builds the existing renderer.
The typed implementation generates separate artifacts under `m3-catalog-typed-pilot`; delivery
and saved runtime pins stay on their existing path until compatibility is proven.

`:catalog-typed-policy` declares the entire policy in Kotlin: 38 component policies, two builtins,
menu order, templates, modifiers, insertion defaults, source-export routing and upgrade rules.
Generation does not read the old JSON. An independent equivalence test compares the generated
policy with that baseline, normalizing protocol defaults and documentation comments.
The common Kotlin policy declarations also supply `:catalog-typed-adapters` directly.

The typed adapter catalog now covers **all 38 explicit component policy definitions and both
builtins**, including variable-font text and snackbar host. Sticker and MaterialExpressiveTheme
retain their existing shelf exclusions. This covers the explicit builder policy; it does not
claim to replace every automatically discovered sticker in the wider Material inventory.

Public wrappers call the Material APIs, outside the kit-mapped sticker inventory. They add no
inventory annotations or Figma traffic. JVM discovery reads real compiled signatures; property,
callback and slot references generate the portable catalog pair and the same Wasm adapter table.
Wrapper and adapter defaults share declarations. Eleven foundation layout adapters support all
six existing starter templates, including adaptive panes.

Existing policy vocabulary and richer metadata are preserved alongside the typed capabilities.
The compatibility bridge accepts existing primitive wire encodings before strict typed decoding.
It does not implement additional behavior for every richer legacy policy field. Invalid typed
values remain diagnostics rather than silently receiving defaults.

## Validation

With sibling source checkouts of compose-ai-tools and compose-ui-builder:

```sh
scripts/validate-typed-m3-catalog.sh
# Or specify both source checkouts:
scripts/validate-typed-m3-catalog.sh /path/to/compose-ai-tools /path/to/compose-ui-builder
```

The source-composite workflow uses pinned upstream commits while the renderer bridge is
experimental and unreleased. It proves the API by consuming it here rather than waiting for a
release. Production dependency versions remain unchanged.

Validation includes:

- Full policy equivalence, compiled signature validation and atomic paired-artifact round trips.
- Pixel comparisons for all 40 wrappers with populated slots, rendered directly and through the
  typed registry at the same pixel origin. Selection controls also cover twelve state combinations.
- Bound checkbox/switch interactions, text-input state updates and correct callback event names,
  numeric codec checks, and malformed-value isolation.
- **86 generated source fixtures** compiled by a separate consumer: default and explicit calls
  for all 40 wrappers, plus six state-read fixtures. These do not prove the production exporter's
  complete callback/state/event projection.
- A separate standard published-catalog reader test process without application implementation
  classes on its classpath. This proves metadata transport independently of executable rendering.
- The actual packaged Wasm ZIP served in a sandbox iframe and controlled through protocol v2.
  Chromium renders all 38 active component/builtin entries and six templates at phone and tablet
  widths, checks measured inspection, rejects an original runtime pin, and clicks a state-bound
  checkbox. Archive integrity is recomputed independently. Semantic protocol activation of a
  bound callback without a click binding remains an upstream SDK limitation; pointer interaction
  exercises the real component callback.

![Selection controls](../evidence/typed-m3-catalog/selection-controls.png)

Outputs live beneath `catalog-typed-adapters/build/`:

- `catalog/{components.json,ui-builder.json}`: generated catalog pair.
- `catalog/readiness.json`: component/builtin coverage, contract differences and cutover gates.
- `distributions/m3-catalog-typed-pilot-renderer.zip`: isolated protocol-v2 runtime and manifest.
- `render-evidence/full-catalog/`: direct and typed native captures for all 40 registrations.
- `browser-evidence/`: browser screenshots, runtime verification results and assessed readiness.

## Before switching

Component coverage is complete for the explicit policy, but `readyForCutover` remains false.
Canvas identities and wrapper source routing differ from production, and typed capabilities
introduce contract differences. Preserve the report until those differences are resolved.

Existing saved-design fixtures still need compatibility validation through the standard host,
including stateEquals and richer value forms. Production source export needs complete state/event
projection and rendering/interaction comparisons against the renderer. Browser verification
proves the packaged trial, without establishing compatibility with saved production runtime pins.
Only after those gates pass should ordinary discovery and delivery consume the generated Kotlin
artifacts and matching runtime together. Retain the old catalog revisions and runtime bytes that
saved pins require.

## M3 samples

The parallel policy consumer also authors `m3-samples` in Kotlin. `generateSamplesPolicy` writes
`catalog-typed-policy/build/catalog/m3-samples/ui-builder.policy.json` and an independent baseline
test preserves the catalog identity and deliberately empty component, builtin and menu declarations.
The vendored AndroidX samples stay untouched and discovery continues to own their inventory.
The production samples policy and delivery inputs remain authoritative during this trial.

## Complete portable catalog generation

`scripts/validate-typed-m3-catalogs.sh` first compiles and
re-discovers every covered module, then runs the independent `catalog-typed-generator` consumer.
It uses the published tools BOM on the existing pinned line and calls the owner `UiBuilderCatalogs.generate` API with the authored Kotlin policy, the live
`ComponentRecordFile` and the existing cover sheet. It does not read the old policy as a generation
input. Annotation-owned membership, call signatures, callback contracts and opt-ins remain in the
original compiled record, copied byte for byte alongside the generated `ui-builder.json`.

Generation compares the entire typed output with fresh legacy discovery, including identities,
code contracts, exclusions, builtins and diagnostics, before replacing its task-owned output.
Only documentation comments are ignored. A changed contract or missing discovery/template fails the
build. Template files travel with the pair. Artifacts and compatibility evidence are uploaded by the
typed workflow from `catalog-typed-generator/build/catalog/<catalog-id>/`.

This completes portable JSON generation alongside production. It does not claim the experimental
property-reference renderer bridge is released or ready for cutover. Production delivery continues
using its existing inputs until the compiled-adapter and packaged-runtime gates pass.
