#!/usr/bin/env node
// Dump the Material 3 Design Kit's component inventory, page by page, for issue triage.
//
// This is an investigation aid, not part of the pipeline. `resolve-figma-refs.mjs` walks the file
// at depth=3 and prints only the top three candidates per catalogued component, which cannot answer
// two questions #3 raises: what the kit actually contains under a page, and whether a component set
// the matcher never offered is missing or merely deeper than the walk reached.
//
// It also resolves every ref already on an annotation, reporting the node's type, visibility and
// corner radius — the inputs to #2.
//
//   FIGMA_TOKEN=figd_… node scripts/dump-figma-inventory.mjs --file ocdacdEsnHipMJD3egzxKb \
//     --depth 8 --out figma-inventory.json

import { readFileSync, writeFileSync } from "node:fs";

function arg(name, fallback) {
  const i = process.argv.indexOf(`--${name}`);
  return i >= 0 && process.argv[i + 1] ? process.argv[i + 1] : fallback;
}

const fileKey = arg("file", "ocdacdEsnHipMJD3egzxKb");
const depth = Number(arg("depth", "8"));
const outPath = arg("out", "figma-inventory.json");
const mapPath = arg("map", "design-map.json");
const token = process.env.FIGMA_TOKEN;

if (!token) {
  console.error("FIGMA_TOKEN is not set.");
  process.exit(1);
}

const sleep = (ms) => new Promise((r) => setTimeout(r, ms));

async function figma(path, { attempts = 6 } = {}) {
  for (let attempt = 1; ; attempt++) {
    const res = await fetch(`https://api.figma.com/v1${path}`, {
      headers: { "X-Figma-Token": token },
    });
    if (res.ok) return res.json();
    const retryable = res.status === 429 || res.status >= 500;
    if (!retryable || attempt >= attempts) {
      throw new Error(`GET ${path.slice(0, 80)} -> ${res.status} ${res.statusText}`);
    }
    const retryAfter = Number(res.headers.get("retry-after"));
    const waitMs =
      Number.isFinite(retryAfter) && retryAfter > 0 ? retryAfter * 1000 : Math.min(60_000, 2 ** attempt * 1000);
    console.log(`  ${res.status} — retrying in ${Math.round(waitMs / 1000)}s`);
    await sleep(waitMs);
  }
}

const radiusOf = (n) =>
  n.cornerRadius ?? (Array.isArray(n.rectangleCornerRadii) ? n.rectangleCornerRadii.join("/") : null);

const file = await figma(`/files/${fileKey}?depth=1`);
const pages = file.document.children ?? [];
console.log(`${pages.length} page(s) in ${fileKey}`);

const inventory = [];
for (const [i, page] of pages.entries()) {
  if (i > 0) await sleep(1_500);
  let nodes;
  try {
    nodes = await figma(`/files/${fileKey}/nodes?ids=${encodeURIComponent(page.id)}&depth=${depth}`);
  } catch (e) {
    console.log(`  page "${page.name}" (${page.id}) failed: ${e.message}`);
    inventory.push({ page: page.name, pageId: page.id, error: e.message, components: [] });
    continue;
  }
  const root = nodes.nodes?.[page.id]?.document;
  const components = [];
  let deepest = 0;
  const walk = (node, trail, level, hiddenAbove) => {
    deepest = Math.max(deepest, level);
    const hidden = hiddenAbove || node.visible === false;
    if (node.type === "COMPONENT_SET" || node.type === "COMPONENT") {
      components.push({
        name: node.name,
        id: node.id,
        type: node.type,
        level,
        hidden,
        w: Math.round(node.absoluteBoundingBox?.width ?? 0),
        h: Math.round(node.absoluteBoundingBox?.height ?? 0),
        radius: radiusOf(node),
        variants: (node.children ?? []).length,
        trail: trail.join(" / "),
        // Every variant, because a ref belongs on one of these rather than on the set: a set frame
        // is a variant grid, and its own radius is an editor artifact (see #2).
        children: (node.children ?? []).map((v) => ({
          name: v.name,
          id: v.id,
          w: Math.round(v.absoluteBoundingBox?.width ?? 0),
          h: Math.round(v.absoluteBoundingBox?.height ?? 0),
          radius: radiusOf(v),
        })),
      });
      return; // a set's variants are not separate components for this purpose
    }
    for (const child of node.children ?? []) walk(child, [...trail, node.name], level + 1, hidden);
  };
  if (root) walk(root, [], 0, false);
  console.log(`  [${i + 1}/${pages.length}] ${page.name} (${page.id}): ${components.length} component(s), deepest ${deepest}`);
  inventory.push({ page: page.name, pageId: page.id, deepest, components });
}

// --- Resolve the refs already in use ------------------------------------------------------------

let refs = [];
try {
  const map = JSON.parse(readFileSync(mapPath, "utf8"));
  refs = (map.components ?? [])
    .filter((c) => typeof c.ref === "string" && c.ref.startsWith(`figma:${fileKey}/`))
    .map((c) => ({ code: c.code, nodeId: c.ref.slice(`figma:${fileKey}/`.length) }));
} catch (e) {
  console.log(`No design map at ${mapPath}: ${e.message}`);
}

const mapped = [];
for (let i = 0; i < refs.length; i += 20) {
  const batch = refs.slice(i, i + 20);
  if (i > 0) await sleep(1_000);
  const res = await figma(
    `/files/${fileKey}/nodes?ids=${encodeURIComponent(batch.map((r) => r.nodeId).join(","))}&depth=1`,
  );
  for (const r of batch) {
    const doc = res.nodes?.[r.nodeId]?.document;
    mapped.push({
      ...r,
      found: Boolean(doc),
      name: doc?.name ?? null,
      type: doc?.type ?? null,
      hidden: doc?.visible === false,
      w: Math.round(doc?.absoluteBoundingBox?.width ?? 0),
      h: Math.round(doc?.absoluteBoundingBox?.height ?? 0),
      radius: doc ? radiusOf(doc) : null,
      children: (doc?.children ?? []).length,
    });
  }
}

writeFileSync(outPath, JSON.stringify({ fileKey, depth, pages: inventory, mapped }, null, 2));
console.log(`\nWrote ${outPath}`);

console.log("\n--- Refs in use ---");
for (const m of mapped) {
  const flags = [m.found ? null : "MISSING", m.hidden ? "HIDDEN" : null].filter(Boolean).join(" ");
  console.log(
    `${(m.type ?? "?").padEnd(13)} ${String(m.radius ?? "-").padEnd(8)} ${String(m.w).padStart(5)}x${String(m.h).padEnd(6)} ` +
      `${m.children.toString().padStart(3)} kids  ${m.name ?? "?"}  ${flags}  ${m.code.split("#")[1] ?? m.code}`,
  );
}
