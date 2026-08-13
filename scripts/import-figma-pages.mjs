#!/usr/bin/env node
// Import whole PAGES of the Material 3 Design Kit as cached SVG, with the node id of every
// component on them joined back to the code that implements it.
//
// WHAT THIS REPLACES
//
// The first cut of this surface imported *one composed screen* (`Examples` → Upcoming-Mobile) as a
// flat PNG and drew a rectangle per component instance on it. `docs/FIGMA_PAGES.md` records why
// that never paid off: `Examples` is the only page in the file with instances on it, most of each
// screen is hand-drawn rather than assembled from the kit, and the densest screen in the whole file
// yields eleven placements of which two are OS chrome.
//
// The kit's *value* is on the other thirty pages — the component definition sheets. A definition
// page is a specimen: `Shape` is the corner-radius scale plus the 35-shape Expressive library,
// `Buttons` is every button variant, laid out as the designer intends them to be read. That is the
// thing worth putting our renders on top of, because a definition sheet is exactly the claim this
// catalog is trying to reproduce.
//
// SO: A PAGE, AS SVG, WITH IDS
//
// Two REST calls per page:
//
//   1. `/v1/files/:key/nodes?ids=<page>` — the node tree. Every COMPONENT / COMPONENT_SET /
//      INSTANCE under the page becomes a placement, carrying its node id and layer name.
//   2. `/v1/images/:key?ids=<page>&format=svg&svg_include_node_id=true` — the page as one SVG,
//      with `data-node-id` on every element.
//
// `svg_include_node_id` is the whole trick. It means the cached SVG is not a picture but a
// *document we can address*: given a node id, a consumer can find that shape in the markup, hide
// it, and put our own render in the hole it leaves — which is what the preview server's
// `/{system}/pages/` surface now does.
//
// NO GEOMETRY IS RECORDED, DELIBERATELY
//
// A placement carries no bounding box. The old PNG manifest had to carry one — a flat raster has no
// structure to ask. An SVG does: the element is right there, and its box is whatever the browser
// measures. Recording Figma's `absoluteBoundingBox` alongside would introduce a second, weaker
// answer to the same question — weaker because the export box is the *render* bounds (it includes
// effect bleed), so the two disagree by a few pixels on anything with a shadow, and a consumer
// choosing between them would silently pick the wrong one. One source of truth: the SVG.
//
// USAGE
//
//   FIGMA_TOKEN=figd_... node scripts/import-figma-pages.mjs
//   FIGMA_TOKEN=figd_... node scripts/import-figma-pages.mjs --page shape
//
// Reads `design-pages.json` (which pages, and where to write them) and `design-map.json` (the
// node → code join, itself derived from the `@CatalogComponent(reference = …)` annotations). Writes
// `<outDir>/pages.json` and one `<outDir>/<id>.svg` per page.
//
// This script is READ-ONLY against Figma, like every other Figma interaction in this repo. The
// token needs `file_content:read` — the same scope `resolve-figma-refs.mjs` and
// `list-figma-pages.mjs` already document.

import { mkdirSync, readFileSync, writeFileSync } from "node:fs";
import path from "node:path";

/** The manifest version this importer writes. Mirrored by `DesignPagesManifest` in the server. */
const PAGES_VERSION = 2;

/** Node types that become placements: the things a definition sheet is *made of*. */
const PLACEABLE_TYPES = new Set(["COMPONENT", "COMPONENT_SET", "INSTANCE"]);

/**
 * How many placements one page may carry. The server caps at 500 and drops the rest; refusing here
 * as well means the cache never carries a node the consumer will silently discard.
 */
const MAX_PLACEMENTS = 500;

function arg(name, fallback) {
  const i = process.argv.indexOf(`--${name}`);
  return i >= 0 && process.argv[i + 1] ? process.argv[i + 1] : fallback;
}

const configPath = arg("config", "design-pages.json");
const designMapPath = arg("design-map", "design-map.json");
const onlyPage = arg("page", null);
const token = process.env.FIGMA_TOKEN;

const sleep = (ms) => new Promise((r) => setTimeout(r, ms));

/** `123-456` and `123:456` are the same node. Figma accepts both on input and answers with `:`. */
function canonicalNodeId(id) {
  return String(id ?? "").replace(/-/g, ":");
}

/**
 * One REST call, with backoff on 429/5xx.
 *
 * Retrying matters more here than in `list-figma-pages.mjs`: a page import is two calls plus an
 * asset download, and the asset host is a different origin with its own limits.
 */
async function get(url, { headers = {}, attempt = 0 } = {}) {
  const res = await fetch(url, { headers });
  if (res.ok) return res;
  const retryable = res.status === 429 || res.status >= 500;
  if (!retryable || attempt >= 4) {
    throw new Error(`${url.replace(/\?.*$/, "")} → HTTP ${res.status} ${await res.text()}`);
  }
  const after = Number(res.headers.get("retry-after"));
  await sleep(Number.isFinite(after) && after > 0 ? after * 1000 : 1000 * 2 ** attempt);
  return get(url, { headers, attempt: attempt + 1 });
}

async function figma(pathAndQuery) {
  const res = await get(`https://api.figma.com${pathAndQuery}`, {
    headers: { "X-Figma-Token": token },
  });
  return res.json();
}

/**
 * `design-map.json` keyed by design ref — the join this whole surface hangs on.
 *
 * The map is a projection of the `@CatalogComponent(reference = …)` annotations
 * (`generate-design-map.mjs`), so a page node links to code exactly when some component named that
 * node id. Both the scalar and the per-state array forms are read; a state array contributes one
 * entry per ref, each paired with the preview id of the *same* state, because that is the render a
 * consumer will draw and pairing by position alone would silently mismatch them.
 */
function readDesignMap(file) {
  const byRef = new Map();
  let map;
  try {
    map = JSON.parse(readFileSync(file, "utf8"));
  } catch (error) {
    console.warn(`import-figma-pages: no usable ${file} (${error.message}); nothing will link`);
    return byRef;
  }
  for (const entry of map.components ?? []) {
    const code = entry.code;
    if (typeof code !== "string" || code === "") continue;

    const previewsByState = new Map();
    let basePreview = null;
    for (const p of asArray(entry.previewId)) {
      if (typeof p === "string") basePreview ??= p;
      else if (p?.previewId) {
        if (p.state) previewsByState.set(p.state, p.previewId);
        else basePreview ??= p.previewId;
      }
    }
    for (const r of asArray(entry.ref)) {
      const ref = typeof r === "string" ? r : r?.ref;
      if (typeof ref !== "string" || ref === "") continue;
      const state = typeof r === "string" ? null : (r?.state ?? null);
      const previewId = (state && previewsByState.get(state)) || basePreview || null;
      // First writer wins: two components naming one node is a mapping bug, and picking the later
      // one silently would make which of them shows depend on file order.
      if (!byRef.has(ref)) byRef.set(ref, { code, previewId });
    }
  }
  return byRef;
}

/** The pages already in the cache, so a scoped refresh adds to it rather than replacing it. */
function readCachedPages(outDir) {
  try {
    const cached = JSON.parse(readFileSync(path.join(outDir, "pages.json"), "utf8"));
    return Array.isArray(cached.pages) ? cached.pages : [];
  } catch {
    return [];
  }
}

function asArray(value) {
  if (value == null) return [];
  return Array.isArray(value) ? value : [value];
}

/** Every COMPONENT / COMPONENT_SET / INSTANCE under `node`, depth-first, with its nesting depth. */
function collectPlacements(node, depth = 0, out = []) {
  if (out.length >= MAX_PLACEMENTS) return out;
  if (depth > 0 && PLACEABLE_TYPES.has(node.type)) {
    out.push({ nodeId: canonicalNodeId(node.id), name: String(node.name ?? ""), depth });
    // A component set's variants are its children and are placements in their own right, so the
    // walk continues through it. An INSTANCE's subtree is not: its children are copies of another
    // component's internals, they carry ids nothing in `design-map.json` can name, and on a
    // definition sheet they are the *inside* of a specimen rather than a specimen.
    if (node.type === "INSTANCE") return out;
  }
  for (const child of node.children ?? []) collectPlacements(child, depth + 1, out);
  return out;
}

/**
 * The SVG's own coordinate space, read off its root element.
 *
 * This is the page's frame: the aspect ratio a consumer lays the stage out with, and the space
 * every `data-node-id` element is positioned in. Taken from the export rather than computed from
 * the node tree precisely so that the number a consumer draws with is the number the picture was
 * drawn at.
 */
function frameOf(svg) {
  const root = /<svg\b[^>]*>/i.exec(svg)?.[0] ?? "";
  const viewBox = /viewBox\s*=\s*"([^"]*)"/i.exec(root)?.[1];
  if (viewBox) {
    const parts = viewBox.trim().split(/[\s,]+/).map(Number);
    if (parts.length === 4 && parts.every(Number.isFinite) && parts[2] > 0 && parts[3] > 0) {
      return { width: parts[2], height: parts[3] };
    }
  }
  const width = Number(/\bwidth\s*=\s*"(\d+(?:\.\d+)?)"/i.exec(root)?.[1]);
  const height = Number(/\bheight\s*=\s*"(\d+(?:\.\d+)?)"/i.exec(root)?.[1]);
  if (width > 0 && height > 0) return { width, height };
  throw new Error("the exported SVG declares no usable viewBox or size");
}

/** How many `data-node-id` attributes the export actually carries — the check that matters most. */
function countNodeIds(svg) {
  return (svg.match(/\bdata-node-id\s*=/g) ?? []).length;
}

async function importPage(page, { fileKey, byRef, outDir }) {
  const nodeId = canonicalNodeId(page.nodeId);
  const encoded = encodeURIComponent(nodeId);

  const nodes = await figma(`/v1/files/${fileKey}/nodes?ids=${encoded}`);
  const document = nodes?.nodes?.[nodeId]?.document;
  if (!document) throw new Error(`node ${nodeId} is not in file ${fileKey}`);

  // `svg_include_node_id` is the reason this surface exists at all — without it the export is a
  // picture. `svg_outline_text` is left at its default (true): outlined text renders identically
  // everywhere, and a specimen sheet is mostly labels, so a font substitution on the consumer's box
  // would make the design half of a comparison wrong in exactly the way it is meant to be right.
  const images = await figma(
    `/v1/images/${fileKey}?ids=${encoded}&format=svg&svg_include_node_id=true`,
  );
  const url = images?.images?.[nodeId];
  if (typeof url !== "string" || url === "") {
    throw new Error(`Figma rendered no SVG for ${nodeId}: ${images?.err ?? "no url"}`);
  }
  const svg = await (await get(url)).text();
  if (!/^\s*<svg\b/i.test(svg)) throw new Error("the export did not start with an <svg> element");

  const placements = collectPlacements(document).map((placement) => {
    const ref = `figma:${fileKey}/${placement.nodeId}`;
    const mapped = byRef.get(ref);
    return {
      ...placement,
      ref,
      link: mapped ? "manifest" : "unlinked",
      ...(mapped?.code ? { code: mapped.code } : {}),
      ...(mapped?.previewId ? { previewId: mapped.previewId } : {}),
      ...(mapped ? { confidence: "high" } : {}),
    };
  });

  const id = page.id;
  writeFileSync(path.join(outDir, `${id}.svg`), svg);
  const linked = placements.filter((p) => p.link !== "unlinked").length;
  console.log(
    `${id}: ${(svg.length / 1024).toFixed(0)} KB SVG, ${countNodeIds(svg)} addressable nodes, ` +
      `${linked}/${placements.length} placements linked`,
  );

  return {
    id,
    name: String(page.name ?? document.name ?? id),
    nodeId,
    frame: frameOf(svg),
    image: { uri: `${id}.svg`, format: "svg" },
    placements,
  };
}

async function main() {
  if (!token) {
    console.error("FIGMA_TOKEN is not set. A read-only PAT with `file_content:read` is enough.");
    process.exit(1);
  }

  const config = JSON.parse(readFileSync(configPath, "utf8"));
  if (config.enabled !== true) {
    console.log(`import-figma-pages: ${configPath} is not enabled; nothing to do`);
    return;
  }
  const fileKey = config.fileKey;
  if (typeof fileKey !== "string" || fileKey === "") {
    console.error(`import-figma-pages: ${configPath} names no fileKey`);
    process.exit(2);
  }
  const outDir = path.resolve(config.outDir || "design/pages");
  const wanted = (config.pages ?? []).filter((p) => !onlyPage || p.id === onlyPage);
  if (wanted.length === 0) {
    console.error(`import-figma-pages: ${configPath} declares no pages to import`);
    process.exit(2);
  }

  const byRef = readDesignMap(designMapPath);
  mkdirSync(outDir, { recursive: true });

  const pages = [];
  for (const page of wanted) {
    // One page's failure does not cost the others their refresh — but it does fail the run, so a
    // broken node id in the config can't quietly shrink the cache to nothing.
    pages.push(await importPage(page, { fileKey, byRef, outDir }));
  }

  // `--page` refreshes ONE page without discarding the rest of the cache. Rewriting the manifest
  // from just what this run fetched would silently delete the others' entries while leaving their
  // SVGs on disk — a cache that disagrees with itself. Order follows the config, so the manifest
  // diffs cleanly however the run was scoped.
  const merged = new Map(readCachedPages(outDir).map((page) => [page.id, page]));
  for (const page of pages) merged.set(page.id, page);
  const ordered = (config.pages ?? []).map((p) => merged.get(p.id)).filter(Boolean);

  writeFileSync(
    path.join(outDir, "pages.json"),
    `${JSON.stringify({ version: PAGES_VERSION, source: "figma", fileKey, pages: ordered }, null, 2)}\n`,
  );
  console.log(
    `import-figma-pages: refreshed ${pages.length} of ${ordered.length} page(s) in ` +
      `${path.relative(".", outDir)}`,
  );
}

await main();
