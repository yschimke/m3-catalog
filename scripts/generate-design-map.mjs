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
// LIGHT preview id (the mode the kit's frames are drawn in).
//
// A component with NO reference is an error here, not a skip. This catalog
// reproduces the kit, so a component with no exact kit node does not belong in
// the published inventory at all (#10) — skipping it would publish a sticker
// with nothing to compare against while the map quietly stayed one entry short.
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

import {
  defaultedContent,
  propertyForSeed,
  renderableRef,
  resolveVariantRef,
  slotFor,
} from "./kit-variants.mjs";

function arg(name, fallback) {
  const i = process.argv.indexOf(`--${name}`);
  return i >= 0 && process.argv[i + 1] ? process.argv[i + 1] : fallback;
}

const previewsPath = arg("previews", "catalog/build/compose-previews/previews.json");
const outPath = arg("out", "design-map.json");

const manifest = JSON.parse(readFileSync(previewsPath, "utf8"));
const previews = manifest.previews ?? [];

const components = [];
/** Components carrying no `reference` at all — collected, then thrown as one. */
const unreferenced = [];
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

// Every variant render, grouped by the component it folds under. Every exact
// combination the catalog renders is compared when the kit publishes the same
// axis vector. The catalog exists to reproduce the kit, so dropping a rendered
// cross-product cell merely because it moves several knobs hides real parity
// defects and leaves known Figma nodes unused.
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
const variantsByComponent = new Map();
const add = (componentId, entry) => {
  const list = variantsByComponent.get(componentId) ?? [];
  list.push(entry);
  variantsByComponent.set(componentId, list);
};
for (const preview of previews) {
  const catalog = preview.catalog;
  if (!catalog || !/_Light(_VARIANT_.*)?$/.test(preview.id)) continue;

  if (catalog.role === "COMPONENT" && /_Light_VARIANT_/.test(preview.id)) {
    const seeds = [...(preview.overrides?.seeds ?? [])];
    // An interaction variant seeds no knob — the renderer drives real hover,
    // focus or press against the composed node instead, so the difference is
    // in the harness rather than in the data. The kit models it as a value of
    // the same `State` axis that carries Enabled and Disabled, so it enters
    // resolution as a seed of the `state` knob and reaches the kit through the
    // alias that knob already has. Without this the variant resolves nothing:
    // `seeds` is empty, and an empty vector matches every sibling.
    const interaction = preview.overrides?.interaction;
    if (interaction && interaction !== "None") {
      seeds.push({ key: "state", index: null, kind: "STRING", raw: interaction.toLowerCase() });
    }
    if (seeds.length) {
      add(catalog.componentId, { preview, seeds, name: preview.overrides.name });
    }
  } else if (catalog.role === "VARIANT") {
    // `props` names the axis; `state` is the annotation's shorthand for the one
    // axis common enough to have its own parameter. Either is a declaration, so
    // neither is inferred — `@CatalogVariant(state = "disabled")` says the state
    // axis as plainly as `props = ["state=disabled"]` would.
    const props = [...(catalog.props ?? [])];
    if (catalog.state && !props.some((p) => p.key === "state")) {
      props.push({ key: "state", value: catalog.state });
    }
    if (props.length) {
      add(catalog.componentId, {
        preview,
        seeds: props.map((p) => ({ key: p.key, raw: p.value })),
        name: catalog.state ?? props.map((p) => p.value).join("-"),
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
    unreferenced.push(catalog.componentId);
    continue;
  }

  // The base render is the untagged variant on both sides; each resolvable
  // variant render is a tagged pair beside it. design-parity matches the
  // two lists slot for slot (`refVariant` / `previewIdVariant`).
  const baseRef = renderableRef(catalog.reference);
  const refs = [{ ref: baseRef }];
  const previewIds = [{ previewId: preview.id }];
  const refOwners = new Map([[baseRef, "default"]]);
  const fileKey = catalog.reference.split(":")[1]?.split("/")[0];
  for (const v of variantsByComponent.get(catalog.componentId) ?? []) {
    const hit = resolveVariantRef(catalog.reference, v.seeds);
    if (!hit) {
      const vector = v.seeds.map((seed) => `${seed.key}=${seed.raw}`).join(", ");
      const where = `${catalog.componentId} / ${v.name} (${vector})`;
      const props = v.seeds.map((seed) => propertyForSeed(catalog.reference, seed)).filter(Boolean);
      if (props.length) {
        const prop = props[0];
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
    const slot = slotFor(v.seeds, v.name);
    const resolvedRef = `figma:${fileKey}/${hit.nodeId}`;
    const previous = refOwners.get(resolvedRef);
    if (previous) {
      throw new Error(
        `${catalog.componentId} maps both ${previous} and ${v.name} to ${resolvedRef}; ` +
          `two distinct previews cannot be the same exact Figma variant`,
      );
    }
    refOwners.set(resolvedRef, v.name);
    refs.push({ ref: resolvedRef, ...slot });
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
    // Figma normally exports only the referenced node. Preserve an explicit per-component opt-out
    // when the annotation says this reference intentionally relies on overlapping sheet content.
    ...(catalog.referenceContentsOnly === false ? { referenceContentsOnly: false } : {}),
    previewId: binding.previewId,
  });
}

// Collected rather than thrown on the first hit: an author who dropped a whole
// group's references wants the list, not one name at a time. Thrown before the
// write, so a run that fails leaves the committed map intact instead of
// replacing it with a partial one CI would then report as merely stale.
if (unreferenced.length) {
  throw new Error(
    `${unreferenced.length} component(s) carry no @CatalogComponent(reference = …): ` +
      `${unreferenced.sort().join(", ")}. This catalog reproduces the Figma kit, so a component ` +
      `with no exact, renderable kit node does not belong in the published inventory — remove it ` +
      `rather than publishing a sticker with nothing to compare against. See AGENTS.md, ` +
      `"What enters the inventory, and what it is called".`,
  );
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
    `\n${propertyVariants.length} variant(s) are a component PROPERTY in the kit, ` +
      `not a variant beside it. A definition node renders at the defaults, and no exact ` +
      `configured instance was indexed for these values, so they remain unpaired:`,
  );
  for (const v of propertyVariants.sort()) console.log(`  - ${v}`);
}
if (unresolvedVariants.length) {
  console.log(
    `\n${unresolvedVariants.length} variant(s) have no counterpart in the kit ` +
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
