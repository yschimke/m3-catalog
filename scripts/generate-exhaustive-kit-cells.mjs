#!/usr/bin/env node

// Expand the catalog's existing, independently-authored kit axes into exact Figma cells.
//
// The ordinary annotations deliberately keep variants small: one render proves one axis value.
// The imported Figma pages count cells instead — every combination in a component set. This
// generator bridges those two views without adding another component implementation. It learns
// how each axis is rendered from the resolved design map, combines those real seeds, and pins the
// result to the kit's complete vector with `kitProps`.
//
// A target with neither a real seed nor a real interaction is left uncovered. Mapping it would be
// a second name for the default pixels, which would improve a percentage while claiming behavior
// the catalog does not provide. Generated crossings are secondary: they are addressable on the
// live sheet and in the design map, but do not multiply the required baked artifact set.

import { readFileSync, writeFileSync } from "node:fs";

const ROOT = new URL("../", import.meta.url);
const readJson = (name) => JSON.parse(readFileSync(new URL(name, ROOT), "utf8"));

const designMap = readJson("design-map.json");
const variants = readJson("design-map-variants.json");
const kit = readJson("figma-kit-index.json");
const manifestUrl = new URL("exhaustive-kit-cells.json", ROOT);
const outputUrl = new URL(
  "catalog/src/main/kotlin/ee/schimke/m3catalog/ExhaustiveKitCellAnnotations.kt",
  ROOT,
);

const canonicalId = (ref) => String(ref ?? "").split("/").pop();

function parseAxes(name) {
  return Object.fromEntries(
    String(name)
      .split(",")
      .map((part) => part.trim())
      .filter(Boolean)
      .map((part) => {
        const at = part.indexOf("=");
        return [part.slice(0, at).trim(), part.slice(at + 1).trim()];
      }),
  );
}

const byVariantId = new Map();
for (const [setId, set] of Object.entries(kit.sets ?? {})) {
  for (const variant of set.variants ?? []) byVariantId.set(variant.id, { setId, set, variant });
}

const declarationsByCode = new Map((variants.components ?? []).map((entry) => [entry.code, entry]));

// A few kit sets deliberately back more than one Compose component: Stacked card's Style axis is
// Card / ElevatedCard / OutlinedCard, and Text field's is TextField / OutlinedTextField. Those are
// call-site choices, so each sticker owns only the slice matching its base node. Find every axis
// whose base value differs inside a shared set and lock it per component.
const baseByCode = new Map();
const basesBySet = new Map();
for (const component of designMap.components ?? []) {
  const declaration = declarationsByCode.get(component.code);
  const info = byVariantId.get(canonicalId(declaration?.reference));
  if (!info) continue;
  const base = { setId: info.setId, axes: parseAxes(info.variant.name) };
  baseByCode.set(component.code, base);
  const list = basesBySet.get(info.setId) ?? [];
  list.push(base);
  basesBySet.set(info.setId, list);
}

function lockedAxes(code) {
  const base = baseByCode.get(code);
  const siblings = basesBySet.get(base?.setId) ?? [];
  if (siblings.length < 2) return [];
  return Object.keys(base.axes).filter(
    (axis) => new Set(siblings.map((sibling) => sibling.axes[axis])).size > 1,
  );
}

function slots(value, member) {
  const entries = Array.isArray(value) ? value : [value];
  return entries.filter(Boolean).map((entry) => {
    if (typeof entry === "string") return { value: entry, tag: "base" };
    const tag = entry.state ?? entry.size ?? entry.theme ?? "base";
    return { value: entry[member], tag };
  });
}

function seedKind(key, raw) {
  if (["true", "false"].includes(raw)) return "booleans";
  if (["actions", "count", "hours", "inset"].includes(key)) return "ints";
  if (["progress", "rounding"].includes(key)) return "floats";
  return "strings";
}

function slug(value) {
  return String(value)
    .normalize("NFKD")
    .replace(/[^A-Za-z0-9]+/g, "-")
    .replace(/^-|-$/g, "")
    .toLowerCase();
}

function className(code) {
  const functionName = code.slice(code.lastIndexOf("#") + 1);
  return `${functionName}ExhaustiveKitCells`;
}

function changedAxes(base, target) {
  return Object.fromEntries(
    Object.entries(target).filter(([axis, value]) => base[axis] !== value),
  );
}

function isSubset(effect, target) {
  return Object.entries(effect).every(([axis, value]) => target[axis] === value);
}

function combineEffects(baseAxes, targetAxes, effects) {
  const wanted = changedAxes(baseAxes, targetAxes);
  const candidates = effects.filter((effect) => isSubset(effect.axes, wanted));
  const uncovered = new Set(Object.keys(wanted));
  const selected = [];
  while (uncovered.size) {
    const best = candidates
      .map((effect) => ({
        effect,
        gain: Object.keys(effect.axes).filter((axis) => uncovered.has(axis)).length,
      }))
      .filter(({ gain }) => gain > 0)
      .sort((a, b) => b.gain - a.gain || Object.keys(a.effect.axes).length - Object.keys(b.effect.axes).length)[0];
    if (!best) break;
    selected.push(best.effect);
    Object.keys(best.effect.axes).forEach((axis) => uncovered.delete(axis));
  }
  // A crossing is useful only when every changed kit axis is backed by an existing real seed or
  // renderer-driven interaction. Letting another changed axis carry an unsupported value would
  // link pixels to a state the API never produced merely because some different axis did move.
  if (uncovered.size) return null;

  const seeds = new Map();
  for (const { render } of selected) {
    for (const seed of render.seeds ?? []) {
      // Hover/focus/press are driven by the renderer, not by a sticker knob. `state` values such
      // as selected/unselected remain ordinary seeds and pass through below.
      if (seed.key === "state" && ["hovered", "focused", "pressed"].includes(seed.raw)) continue;
      const previous = seeds.get(seed.key);
      if (previous != null && previous !== seed.raw) return null;
      seeds.set(seed.key, seed.raw);
    }
  }

  const state = targetAxes.State;
  const interaction =
    state === "Hovered" ? "Hovered" : state === "Focused" ? "Focused" : state === "Pressed" ? "Pressed" : null;
  return { seeds: [...seeds].map(([key, raw]) => ({ key, raw, kind: seedKind(key, raw) })), interaction };
}

function deriveCells(component) {
  const declaration = declarationsByCode.get(component.code);
  if (!declaration) return null;
  const baseId = canonicalId(declaration.reference);
  const baseInfo = byVariantId.get(baseId);
  if (!baseInfo) return null;

  const refs = slots(component.ref, "ref");
  const previews = new Map(slots(component.previewId, "previewId").map((entry) => [entry.tag, entry.value]));
  const rendersByPreview = new Map((declaration.renders ?? []).map((render) => [render.previewId, render]));
  const mappedIds = new Set([baseId]);
  const baseAxes = parseAxes(baseInfo.variant.name);
  const effects = [];
  for (const ref of refs) {
    if (ref.tag === "base") continue;
    const info = byVariantId.get(canonicalId(ref.value));
    const render = rendersByPreview.get(previews.get(ref.tag));
    if (!info || !render || info.setId !== baseInfo.setId) continue;
    // Generated cells appear in the next resolved map. They must not teach the generator its own
    // guesses on a later run; only the hand-authored variants are evidence for an axis.
    if (render.name.startsWith("cell-")) continue;
    mappedIds.add(canonicalId(ref.value));
    effects.push({ axes: changedAxes(baseAxes, parseAxes(info.variant.name)), render });
  }

  const cells = [];
  for (const target of baseInfo.set.variants) {
    if (mappedIds.has(target.id)) continue;
    const targetAxes = parseAxes(target.name);
    const combined = combineEffects(baseAxes, targetAxes, effects);
    if (!combined || (combined.seeds.length === 0 && !combined.interaction)) continue;
    cells.push({
      id: target.id,
      name: `cell-${Object.entries(targetAxes)
        .map(([axis, value]) => `${slug(axis)}-${slug(value)}`)
        .join("-")}-${target.id.replace(":", "-")}`,
      kitProps: Object.entries(targetAxes).map(([axis, value]) => `${axis}=${value}`),
      ...combined,
    });
  }
  return { code: component.code, annotation: className(component.code), cells };
}

let previous = { schema: "m3-catalog-exhaustive-kit-cells/v1", components: [] };
try {
  previous = JSON.parse(readFileSync(manifestUrl, "utf8"));
} catch {}

const byCode = new Map((previous.components ?? []).map((entry) => [entry.code, entry]));
for (const component of designMap.components ?? []) {
  const derived = deriveCells(component);
  if (!derived) continue;
  byCode.set(derived.code, {
    ...derived,
    cells: [...derived.cells].sort((a, b) => a.id.localeCompare(b.id)),
  });
}

const manifest = {
  schema: "m3-catalog-exhaustive-kit-cells/v1",
  components: [...byCode.values()]
    .map((component) => {
      const base = baseByCode.get(component.code);
      const locked = lockedAxes(component.code);
      return {
        ...component,
        cells: component.cells.filter((cell) => {
          const axes = parseAxes(cell.kitProps.join(", "));
          return locked.every((axis) => axes[axis] === base.axes[axis]);
        }),
      };
    })
    .filter((component) => component.cells.length > 0)
    .sort((a, b) => a.code.localeCompare(b.code)),
};
writeFileSync(manifestUrl, `${JSON.stringify(manifest, null, 2)}\n`);

const quotedArray = (values) => `[${values.map((value) => JSON.stringify(value)).join(", ")}]`;
const seedArray = (cell, kind) =>
  cell.seeds.filter((seed) => seed.kind === kind).map((seed) => `${seed.key}=${seed.raw}`);

let kotlin = `package ee.schimke.m3catalog

import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.composeai.preview.VariantInteraction

// GENERATED by scripts/generate-exhaustive-kit-cells.mjs. Edit the real sticker knobs and the
// generator, never this file. These are secondary exact-cell crossings for the imported Figma
// pages; every render still invokes the component named by its catalog sticker.
`;
for (const component of manifest.components) {
  kotlin += `\n/** Exact kit cells reachable by crossing ${component.code}. */\n`;
  for (const cell of component.cells) {
    const args = [`name = ${JSON.stringify(cell.name)}`];
    for (const kind of ["booleans", "ints", "floats", "strings"]) {
      const values = seedArray(cell, kind);
      if (values.length) args.push(`${kind} = ${quotedArray(values)}`);
    }
    if (cell.interaction) args.push(`interaction = VariantInteraction.${cell.interaction}`);
    args.push(`kitProps = ${quotedArray(cell.kitProps)}`);
    args.push("secondary = true");
    kotlin += `@OverrideVariant(\n${args.map((arg) => `  ${arg},`).join("\n")}\n)\n`;
  }
  kotlin += `annotation class ${component.annotation}\n`;
}
writeFileSync(outputUrl, kotlin);

const cellCount = manifest.components.reduce((total, component) => total + component.cells.length, 0);
console.log(`wrote ${manifest.components.length} annotations with ${cellCount} exact kit cells`);
