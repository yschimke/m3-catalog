# Adaptive UID reference pilot

An app screen without a Figma design: a hand-written Compose inbox compared with an independently authored UI Builder document. This module stays outside the Material kit inventory and its Figma parity rules. The English strings are fixed capture data for this specimen.

`ui-builder/designs/inbox.uid` is the reviewed design. `references.json` selects an explicit design, viewport, theme and pane state for each reference. A selection in an editor tab never changes the reviewed baseline.

| Width | Expected layout |
| --- | --- |
| 412dp | Phone: list or detail |
| 700dp | Medium: list or detail |
| 839dp | Last width below the two-pane breakpoint |
| 840dp | First width with both panes |
| 960dp | Tablet: list beside detail |

All five widths have list/detail captures in light/dark, at 720dp height and 2px/dp: 20 comparisons. The implementation uses the real `ListDetailPaneScaffold`, with selection surviving resizing. The interactive test checks selection, back navigation, and the 839/840dp transition.

**This matrix uses a custom baked bundle.** Its IDs are the PNG file stems written by `RenderPilot`, not discovery IDs. The six annotated IDE previews have different IDs and do not carry these references. Sending `references.json` through the normal discovery/catalog pipeline would not attach the 20 references. A production integration must either publish this custom matrix explicitly or make all matrix cells discoverable and map their discovered IDs.

## Run

```sh
scripts/agent-gradle.sh -PadaptiveUidPilot=true :adaptive-uid-pilot:test :adaptive-uid-pilot:renderPilot :adaptive-uid-pilot:composePreviewDiscover
```

The candidate PNGs are in `adaptive-uid-pilot/build/pilot/previews/`. They come from `AdaptiveInbox`, never from the UID export. To publish independent references, commit both the UID and capture plan first, then use the companion `compose-preview-server` publisher:

```sh
node ../compose-preview-server/scripts/ui-builder/publish-references.mjs \
  --root . --plan adaptive-uid-pilot/references.json \
  --out adaptive-uid-pilot/build/pilot --revision "$(git rev-parse HEAD)" \
  --renderer /path/to/compose-preview-server \
  --catalog /path/to/ui-builder-renderer.bundle.png \
  --components m3-catalog=/path/to/m3-catalog-components-v1.json
```

Use the renderer bundle and component record from the same released server/UI Builder set, with the server's Kotlin compiler sidecar installed. The publisher verifies the committed plan and UID bytes, renders every reference through the native export lane, checks pixel dimensions and records SHA-256 fingerprints. It publishes `references/index.json` only after all captures succeed. Existing references from other providers are retained. Use a fresh output directory for each publication so previous content-addressed artifacts are not shipped again.

## Serve the custom bundle

After the render and reference steps above, package the two directories unchanged. Their matching file-stem IDs are the binding between the candidate PNGs and the references:

```sh
cd adaptive-uid-pilot/build/pilot
python3 -m zipfile -c ../adaptive-uid-pilot.zip previews references
cd ../../..
/path/to/compose-preview-server serve \
  --host 127.0.0.1 --port 18081 \
  --bundle adaptive-inbox=adaptive-uid-pilot/build/adaptive-uid-pilot.zip \
  --ui-builder-dir /path/to/server-distribution/ui-builder
```

Use the server's authenticated browse link, then open `/adaptive-inbox/compare/inbox-412-detail-light`. The comparison, digest-bound editor link and UID download work from this uploaded bundle. The zip does not carry catalog source/provenance metadata, so it cannot reproduce the app-specific code link or issue destination by itself.

The recorded browser evidence was produced by directly registering the same `build/pilot` directory as a `ServeBundleHost`, with these additional catalog fields:

```kotlin
catalogSource = ServeWeb.CatalogSource(
  repo = "yschimke/m3-catalog", ref = "7790f6e22749e98541279cb7c4f9e0db860ae9f0",
  module = "adaptive-uid-pilot",
)
provenance = ServeWeb.CatalogProvenance(
  repo = "yschimke/m3-catalog", branch = "agent/adaptive-uid-pilot",
)
```

It also wrote `previews/variants.json`, keyed by each capture ID, with `componentId: "Adaptive inbox"`, the plan's `state`, `theme`, `size: "<widthDp>dp"`, and `sourceFile: "src/main/kotlin/ee/schimke/adaptivepilot/AdaptiveInbox.kt"`. The host was registered as session `adaptive-inbox`; the server served the released UI Builder assets. This staging supplied the code/report context visible in the evidence. A normal catalog producer supplies the equivalent fields through `catalog.json`; that production adapter is still future work.

The existing preview comparison reads those references and provides Reference / Diff / Actual, overlay, and region selection for reports. The UI Builder link opens the captured UID; edits stay in the tab until downloaded and committed for review. Source, candidate, reference and report URLs should all point at the same publication. Updating a candidate never approves a new baseline.

## Scope

This is an opt-in local pilot, not an addition to the deployed Material catalog. The module is included only with `-PadaptiveUidPilot=true`, so the default whole-build render workflow does not discover or publish its previews. The companion server change supports the generic reference manifest and snapshot editor. Production publication still needs the custom-matrix/discovery decision above and an adapter carrying the generated references and source metadata; deployment on preview.coo.ee follows the server release. Historical UID editor links currently refuse stale publications rather than substitute a newer document. The existing historical raster comparisons remain available.

The reviewed UID currently keeps Back visible in two-pane mode, and the app mirrors it. Changing that affordance is a design change to make on both sides, not a baseline correction. Detail content follows the navigator's destination key, so selection and navigation history have one source of truth.
