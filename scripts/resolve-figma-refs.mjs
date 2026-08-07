#!/usr/bin/env node
// Resolve the Material 3 Design Kit's Figma components to `figma:<fileKey>/<nodeId>` refs and
// propose one per catalogued component.
//
// WHY THIS EXISTS
//
// design-parity compares a rendered sticker against a Figma node, and it addresses that node by
// **node id**. Node ids are not discoverable without API access: the Figma MCP server exposes only
// the page a user is looking at, and Code Connect — which would hand back the mapping directly — is
// gated behind a Dev/Full seat. So the ids come from the REST API instead.
//
// This script does NOT write a mapping file. It prints proposals. The ref belongs on the
// annotation:
//
//   @CatalogComponent(id = "Button/Filled", reference = "figma:<fileKey>/<nodeId>", caption = "…")
//
// which `scripts/generate-design-map.mjs` then projects into `design-map.json`. Keeping the ref in
// code is the point: a JSON map keyed on preview names drifts the moment a preview is renamed, and
// it fails silently when it does.
//
// USAGE
//
//   FIGMA_TOKEN=figd_... node scripts/resolve-figma-refs.mjs \
//     --file ocdacdEsnHipMJD3egzxKb \
//     --previews catalog/build/compose-previews/previews.json
//
// The token needs `file_content:read` — that scope covers both /v1/files/:key/nodes and /v1/images.
// Run `./gradlew :catalog:composePreviewDiscover` first so the manifest exists.
//
// Output is a table of `componentId -> proposed node`, ranked by name similarity, plus the exact
// `reference = "…"` string to paste. Review it: the kit names components by their own taxonomy
// ("Button - tonal", "Connected button group"), which does not always agree with M3's documented
// component names, so a high-scoring match is a proposal and not a fact.

import { readFileSync } from "node:fs";

function arg(name, fallback) {
  const i = process.argv.indexOf(`--${name}`);
  return i >= 0 && process.argv[i + 1] ? process.argv[i + 1] : fallback;
}

const fileKey = arg("file", "ocdacdEsnHipMJD3egzxKb");
const previewsPath = arg("previews", "catalog/build/compose-previews/previews.json");
const token = process.env.FIGMA_TOKEN;

if (!token) {
  console.error("FIGMA_TOKEN is not set. A read-only PAT with `file_content:read` is enough.");
  process.exit(1);
}

async function figma(path) {
  const res = await fetch(`https://api.figma.com/v1${path}`, {
    headers: { "X-Figma-Token": token },
  });
  if (!res.ok) {
    throw new Error(`GET ${path} -> ${res.status} ${res.statusText}: ${await res.text()}`);
  }
  return res.json();
}

// --- The kit side -----------------------------------------------------------------------------
//
// Two routes, tried in order. `/components` is exact and cheap but only returns components the file
// itself PUBLISHES — a community duplicate subscribes to the original library rather than
// republishing it, and then returns nothing. The tree walk always works.

async function kitComponentsViaPublished() {
  const { meta } = await figma(`/files/${fileKey}/components`);
  return (meta?.components ?? []).map((c) => ({
    name: c.name,
    nodeId: c.node_id,
    containing: c.containing_frame?.name ?? "",
  }));
}

async function kitComponentsViaTree() {
  // depth=1 lists the pages without dragging every node down the wire.
  const file = await figma(`/files/${fileKey}?depth=1`);
  const pages = file.document.children ?? [];
  const found = [];
  for (const page of pages) {
    // depth=3 reaches the component sets sitting inside a page's sections/frames, which is where
    // the kit puts them, without descending into every variant's inner layers.
    const nodes = await figma(
      `/files/${fileKey}/nodes?ids=${encodeURIComponent(page.id)}&depth=3`,
    );
    const root = nodes.nodes?.[page.id]?.document;
    if (!root) continue;
    const walk = (node, trail) => {
      if (node.type === "COMPONENT_SET" || node.type === "COMPONENT") {
        found.push({ name: node.name, nodeId: node.id, containing: trail.join(" / ") });
        return; // don't descend into a component set's variants
      }
      for (const child of node.children ?? []) walk(child, [...trail, node.name]);
    };
    walk(root, []);
  }
  return found;
}

// --- Matching ---------------------------------------------------------------------------------

const normalise = (s) =>
  s
    .toLowerCase()
    .replace(/[^a-z0-9]+/g, " ")
    .trim();

/** Token-overlap score in [0, 1]: how much of the shorter name the two share. */
function score(a, b) {
  const at = new Set(normalise(a).split(" ").filter(Boolean));
  const bt = new Set(normalise(b).split(" ").filter(Boolean));
  if (!at.size || !bt.size) return 0;
  let shared = 0;
  for (const t of at) if (bt.has(t)) shared += 1;
  return shared / Math.min(at.size, bt.size);
}

const manifest = JSON.parse(readFileSync(previewsPath, "utf8"));
const catalogued = new Map();
for (const preview of manifest.previews ?? []) {
  const c = preview.catalog;
  if (c?.role === "COMPONENT" && !catalogued.has(c.componentId)) {
    catalogued.set(c.componentId, { ...c, functionName: preview.functionName });
  }
}

let kit;
try {
  kit = await kitComponentsViaPublished();
  if (!kit.length) throw new Error("no published components");
  console.log(`Resolved ${kit.length} published component(s) from ${fileKey}.`);
} catch (e) {
  console.log(`Published-component lookup unavailable (${e.message}); walking the file tree.`);
  kit = await kitComponentsViaTree();
  console.log(`Found ${kit.length} component(s) by tree walk.`);
}

console.log("");
for (const [componentId, meta] of [...catalogued].sort()) {
  // The kit names by its own taxonomy, so match against the group as well as the id's leaf — a
  // "Button/Tonal" is "Button - tonal" there, and the group carries the word the leaf drops.
  const subject = `${meta.group} ${componentId.replace("/", " ")}`;
  const ranked = kit
    .map((k) => ({ ...k, s: score(subject, `${k.name} ${k.containing}`) }))
    .sort((a, b) => b.s - a.s)
    .slice(0, 3);
  const best = ranked[0];
  const confidence = !best || best.s < 0.34 ? "LOW " : best.s < 0.67 ? "MAYBE" : "GOOD ";
  console.log(`${confidence} ${componentId}`);
  for (const r of ranked) {
    if (!r.s) continue;
    console.log(`        ${r.s.toFixed(2)}  ${r.name}  ->  figma:${fileKey}/${r.nodeId}`);
  }
  if (best?.s) {
    console.log(`        reference = "figma:${fileKey}/${best.nodeId}"`);
  } else {
    console.log(`        (no candidate — leave the reference off; the component simply isn't compared)`);
  }
  console.log("");
}
