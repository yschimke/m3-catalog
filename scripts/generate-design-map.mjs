#!/usr/bin/env node
// Generate `design-map.json` from the discovered preview manifest.
//
// design-parity joins a code subject to a design reference through a
// `design-map.json` entry: `{ code, source, ref, previewId }`. Hand-maintaining
// that for ~90 previews would be exactly the mapping-config sprawl this catalog
// exists to avoid — and it would drift the moment a preview is renamed, since
// the join keys on the fully-qualified preview id.
//
// So the map is DERIVED instead. Each catalogued component already carries its
// seed-kit handle on the annotation:
//
//   @CatalogComponent(id = "Button/Filled", reference = "figma:<fileKey>/<nodeId>")
//
// `composePreviewDiscover` writes that through to `previews.json` as
// `catalog.reference`, so this script is a pure projection of the annotations:
// every component with a `reference` becomes one design-map entry pinned to its
// LIGHT preview id (the mode the kit's frames are drawn in). Components with no
// reference are skipped and reported, so an unmapped component reads as itself
// rather than as a silent gap.
//
//   node scripts/generate-design-map.mjs \
//     --previews catalog/build/compose-previews/previews.json \
//     --out design-map.json
//
// Regenerate rather than edit: the file is an output.

import { readFileSync, writeFileSync } from "node:fs";

function arg(name, fallback) {
  const i = process.argv.indexOf(`--${name}`);
  return i >= 0 && process.argv[i + 1] ? process.argv[i + 1] : fallback;
}

const previewsPath = arg("previews", "catalog/build/compose-previews/previews.json");
const outPath = arg("out", "design-map.json");

const manifest = JSON.parse(readFileSync(previewsPath, "utf8"));
const previews = manifest.previews ?? [];

const components = [];
const unmapped = [];

for (const preview of previews) {
  const catalog = preview.catalog;
  if (!catalog || catalog.role !== "COMPONENT") continue;

  // One entry per component, not per rendered mode: pick the light capture,
  // which is what the kit's frames are drawn in.
  if (!/_Light$/.test(preview.id)) continue;

  if (!catalog.reference) {
    unmapped.push(catalog.componentId);
    continue;
  }

  components.push({
    // design-parity addresses a code subject as `<path>#<function>`.
    code: `catalog/${preview.sourceFile}#${preview.functionName}`,
    source: catalog.reference.startsWith("figma:") ? "figma" : "claude-design",
    ref: catalog.reference,
    previewId: preview.id,
  });
}

components.sort((a, b) => a.code.localeCompare(b.code));
writeFileSync(outPath, `${JSON.stringify({ components }, null, 2)}\n`);

console.log(`Wrote ${outPath}: ${components.length} mapped component(s).`);
if (unmapped.length) {
  console.log(
    `${unmapped.length} component(s) carry no @CatalogComponent(reference = …) and were skipped:`,
  );
  for (const id of unmapped.sort()) console.log(`  - ${id}`);
}
