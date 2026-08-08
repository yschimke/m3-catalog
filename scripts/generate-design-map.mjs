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
// A component may also name the component SET its reference is one variant of
// (`@CatalogComponent(referenceSet = …)`), which projects to `refSet`. `ref`
// stays the single node parity diffs the render against; `refSet` is what a
// whole-screen import matches an instance through, because a screen almost
// never uses the exact variant this catalog chose to picture.
//
//   node scripts/generate-design-map.mjs \
//     --previews catalog/build/compose-previews/previews.json \
//     --out design-map.json
//
// Regenerate rather than edit: the file is an output.

import { readFileSync, writeFileSync } from "node:fs";

import { defaultedContent, propertyForSeed, resolveVariantRef, slotFor } from "./kit-variants.mjs";

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
/**
 * Variants the kit models as a component PROPERTY. Reported apart from the
 * above because they are a different problem with a different owner: the kit
 * has the thing, a node reference just cannot ask for it.
 */
const propertyVariants = [];
/** References that draw optional content by default, whatever the code drew. */
const defaultedRefs = [];

// Every variant render, grouped by the component it folds under. A variant that
// moves exactly ONE knob is an axis of that component and gets compared against
// the kit's corresponding variant; one that moves several is a COMBINATION, and
// comparing the cross product says little the axes do not (#16).
//
// A variant reaches us two ways, and both name their axis:
//
//   @OverrideVariant(name = "l", strings = ["size=l"])   — a reseeded render of
//     the same composable. Arrives as role COMPONENT with `_VARIANT_` in the id
//     and the knob in `overrides.seeds`.
//
//   @CatalogVariant(of = "Fab/Standard", props = ["size=large"])   — its own
//     composable, because the difference is more than a knob. Arrives as role
//     VARIANT with the knob in `catalog.props`.
//
// The second form was invisible here until now, which is why the FAB size axis
// read as unauthored when `FabSmall`/`FabMedium`/`FabLarge` were sitting in the
// catalog all along (#16). The annotation states the axis either way; nothing
// has to be inferred from a function name.
const singleAxisByComponent = new Map();
const add = (componentId, entry) => {
  const list = singleAxisByComponent.get(componentId) ?? [];
  list.push(entry);
  singleAxisByComponent.set(componentId, list);
};
for (const preview of previews) {
  const catalog = preview.catalog;
  if (!catalog || !/_Light(_VARIANT_.*)?$/.test(preview.id)) continue;

  if (catalog.role === "COMPONENT" && /_Light_VARIANT_/.test(preview.id)) {
    const seeds = preview.overrides?.seeds ?? [];
    if (seeds.length === 1) {
      add(catalog.componentId, { preview, seed: seeds[0], name: preview.overrides.name });
    }
  } else if (catalog.role === "VARIANT") {
    // `props` names the axis; `state` is the annotation's shorthand for the one
    // axis common enough to have its own parameter. Either is a declaration, so
    // neither is inferred — `@CatalogVariant(state = "disabled")` says the state
    // axis as plainly as `props = ["state=disabled"]` would.
    const props = catalog.props?.length
      ? catalog.props
      : catalog.state
        ? [{ key: "state", value: catalog.state }]
        : [];
    if (props.length === 1) {
      add(catalog.componentId, {
        preview,
        seed: { key: props[0].key, raw: props[0].value },
        name: props[0].value,
      });
    }
  }
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
      const where = `${catalog.componentId} / ${v.name} (${v.seed.key}=${v.seed.raw})`;
      const prop = propertyForSeed(catalog.reference, v.seed);
      if (prop) {
        const named = prop.properties
          .map((p) => `\`${p.name}\` (${p.type}, default ${JSON.stringify(p.default)})`)
          .join(", ");
        propertyVariants.push(
          `${where} — ${prop.setName}: ${named}` +
            (prop.coversVariant ? " — the reference already draws THIS variant" : ""),
        );
      } else {
        unresolvedVariants.push(where);
      }
      continue;
    }
    const slot = slotFor(v.seed, v.name);
    refs.push({ ref: `figma:${fileKey}/${hit.nodeId}`, ...slot });
    previewIds.push({ previewId: v.preview.id, ...slot });
  }

  const defaulted = defaultedContent(catalog.reference);
  if (defaulted.length) {
    defaultedRefs.push(
      `${catalog.componentId} — ${defaulted[0].setName}: ` +
        defaulted.map((d) => `\`${d.name}\``).join(", "),
    );
  }

  const binding =
    refs.length === 1
      ? { ref: catalog.reference, previewId: preview.id }
      : { ref: refs, previewId: previewIds };

  components.push({
    // design-parity addresses a code subject as `<path>#<function>`.
    code: `catalog/${preview.sourceFile}#${preview.functionName}`,
    source: catalog.reference.startsWith("figma:") ? "figma" : "claude-design",
    ref: binding.ref,
    // The component SET, when the annotation names one. `ref` stays the one
    // variant parity diffs against; `refSet` is what a whole-screen import
    // matches an instance through, since a screen rarely uses the exact variant
    // this sticker pictures. Absent unless the annotation says so.
    ...(catalog.referenceSet ? { refSet: catalog.referenceSet } : {}),
    previewId: binding.previewId,
  });
}

components.sort((a, b) => a.code.localeCompare(b.code));
writeFileSync(outPath, `${JSON.stringify({ components }, null, 2)}\n`);

const variantRefs = components.reduce(
  (n, c) => n + (Array.isArray(c.ref) ? c.ref.length - 1 : 0),
  0,
);
const withSet = components.filter((c) => c.refSet).length;
console.log(
  `Wrote ${outPath}: ${components.length} mapped component(s), ` +
    `${variantRefs} variant reference(s) beside them, ` +
    `${withSet} naming their component set.`,
);
if (propertyVariants.length) {
  console.log(
    `\n${propertyVariants.length} single-axis variant(s) are a component PROPERTY in the kit, ` +
      `not a variant beside it. The kit models them; a node reference cannot ask for ` +
      `them, because a reference renders at the property defaults:`,
  );
  for (const v of propertyVariants.sort()) console.log(`  - ${v}`);
}
if (unresolvedVariants.length) {
  console.log(
    `\n${unresolvedVariants.length} single-axis variant(s) have no counterpart in the kit ` +
      `— neither an axis nor a property, so they are left uncompared:`,
  );
  for (const v of unresolvedVariants.sort()) console.log(`  - ${v}`);
}
if (defaultedRefs.length) {
  console.log(
    `\n${defaultedRefs.length} reference(s) draw optional content by default. Every render ` +
      `made from them includes it, so a sticker that leaves it out is compared against ` +
      `something it never claimed (#21):`,
  );
  for (const r of defaultedRefs.sort()) console.log(`  - ${r}`);
}
if (unmapped.length) {
  console.log(
    `${unmapped.length} component(s) carry no @CatalogComponent(reference = …) and were skipped:`,
  );
  for (const id of unmapped.sort()) console.log(`  - ${id}`);
}
