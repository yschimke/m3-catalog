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

import { resolveVariantRef, slotFor } from "./kit-variants.mjs";

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
/** Variants whose axis the kit does not model — reported, never guessed at. */
const unresolvedVariants = [];

// Every variant render, grouped by the component it folds under. A variant that
// seeds exactly ONE knob is an axis of that component and gets compared against
// the kit's corresponding variant; one that seeds several is a COMBINATION, and
// comparing the cross product says little the axes do not (#16).
const singleAxisByComponent = new Map();
for (const preview of previews) {
  const catalog = preview.catalog;
  if (!catalog || catalog.role !== "COMPONENT") continue;
  if (!/_Light_VARIANT_/.test(preview.id)) continue;
  const seeds = preview.overrides?.seeds ?? [];
  if (seeds.length !== 1) continue;
  const list = singleAxisByComponent.get(catalog.componentId) ?? [];
  list.push({ preview, seed: seeds[0], name: preview.overrides.name });
  singleAxisByComponent.set(catalog.componentId, list);
}

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

  // The base render is the untagged variant on both sides; each resolvable
  // single-axis render is a tagged pair beside it. design-parity matches the
  // two lists slot for slot (`refVariant` / `previewIdVariant`).
  const refs = [{ ref: catalog.reference }];
  const previewIds = [{ previewId: preview.id }];
  const fileKey = catalog.reference.split(":")[1]?.split("/")[0];
  for (const v of singleAxisByComponent.get(catalog.componentId) ?? []) {
    const hit = resolveVariantRef(catalog.reference, v.seed);
    if (!hit) {
      unresolvedVariants.push(
        `${catalog.componentId} / ${v.name} (${v.seed.key}=${v.seed.raw})`,
      );
      continue;
    }
    const slot = slotFor(v.seed, v.name);
    refs.push({ ref: `figma:${fileKey}/${hit.nodeId}`, ...slot });
    previewIds.push({ previewId: v.preview.id, ...slot });
  }

  components.push({
    // design-parity addresses a code subject as `<path>#<function>`.
    code: `catalog/${preview.sourceFile}#${preview.functionName}`,
    source: catalog.reference.startsWith("figma:") ? "figma" : "claude-design",
    ...(refs.length === 1
      ? { ref: catalog.reference, previewId: preview.id }
      : { ref: refs, previewId: previewIds }),
  });
}

components.sort((a, b) => a.code.localeCompare(b.code));
writeFileSync(outPath, `${JSON.stringify({ components }, null, 2)}\n`);

const variantRefs = components.reduce(
  (n, c) => n + (Array.isArray(c.ref) ? c.ref.length - 1 : 0),
  0,
);
console.log(
  `Wrote ${outPath}: ${components.length} mapped component(s), ` +
    `${variantRefs} variant reference(s) beside them.`,
);
if (unresolvedVariants.length) {
  console.log(
    `\n${unresolvedVariants.length} single-axis variant(s) have no counterpart in the kit ` +
      `— the kit models no such axis, so they are left uncompared:`,
  );
  for (const v of unresolvedVariants.sort()) console.log(`  - ${v}`);
}
if (unmapped.length) {
  console.log(
    `${unmapped.length} component(s) carry no @CatalogComponent(reference = …) and were skipped:`,
  );
  for (const id of unmapped.sort()) console.log(`  - ${id}`);
}
