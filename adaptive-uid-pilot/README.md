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

All five widths have list/detail captures in light/dark, at 720dp height and 2px/dp: 20 comparisons. The implementation uses the real `ListDetailPaneScaffold`, with selection surviving resizing. The interactive test checks selection, back navigation, and the 839/840dp transition. These baked capture IDs are stable explicit matrix IDs; the six annotated IDE previews also remain discoverable for IDE use.

## Run

```sh
scripts/agent-gradle.sh :adaptive-uid-pilot:test :adaptive-uid-pilot:renderPilot :adaptive-uid-pilot:composePreviewDiscover
```

The candidate PNGs are in `build/pilot/previews/`. They come from `AdaptiveInbox`, never from the UID export. To publish independent references, commit the UID first, then use the companion `compose-preview-server` publisher:

```sh
node ../compose-preview-server/scripts/ui-builder/publish-references.mjs \
  --root . --plan adaptive-uid-pilot/references.json \
  --out adaptive-uid-pilot/build/pilot --revision "$(git rev-parse HEAD)" \
  --renderer /path/to/compose-preview-server \
  --catalog /path/to/ui-builder-renderer.bundle.png \
  --components m3-catalog=/path/to/m3-catalog-components-v1.json
```

Use the renderer bundle and component record from the same released server/UI Builder set, with the server's Kotlin compiler sidecar installed. The publisher verifies the committed UID bytes, renders every reference through the native export lane, checks pixel dimensions and records SHA-256 fingerprints. It publishes `references/index.json` only after all captures succeed. Existing references from other providers are retained.

The existing preview comparison reads those references and provides Reference / Diff / Actual, overlay, and region selection for reports. The UI Builder link opens the captured UID; edits stay in the tab until downloaded and committed for review. Source, candidate, reference and report URLs should all point at the same publication. Updating a candidate never approves a new baseline.

## Scope

This is an opt-in local pilot, not an addition to the deployed Material catalog. The companion server change supports the generic reference manifest and snapshot editor. Production publication still needs the app's catalog CI to carry the generated `references/` directory and its source metadata; deployment on preview.coo.ee follows the server release. Historical UID editor links currently refuse stale publications rather than substitute a newer document. The existing historical raster comparisons remain available.
