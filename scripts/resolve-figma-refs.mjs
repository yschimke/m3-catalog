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

const sleep = (ms) => new Promise((r) => setTimeout(r, ms));

/**
 * One REST call, with backoff.
 *
 * Figma rate-limits per token, and the tree walk below is exactly the shape that trips it: one
 * request per page of a large kit, back to back. A 429 is transient and expected here, not an
 * error — retry it (honouring `Retry-After` when the server sends one) rather than throwing away
 * a walk that is most of the way done. 5xx gets the same treatment; a 4xx that isn't 429 is a real
 * problem (bad token, bad file key) and fails immediately, because retrying it just wastes time.
 */
async function figma(path, { attempts = 5 } = {}) {
  for (let attempt = 1; ; attempt++) {
    const res = await fetch(`https://api.figma.com/v1${path}`, {
      headers: { "X-Figma-Token": token },
    });
    if (res.ok) return res.json();

    const retryable = res.status === 429 || res.status >= 500;
    if (!retryable || attempt >= attempts) {
      throw new Error(`GET ${path} -> ${res.status} ${res.statusText}: ${await res.text()}`);
    }
    const retryAfter = Number(res.headers.get("retry-after"));
    const waitMs = Number.isFinite(retryAfter) && retryAfter > 0
      ? retryAfter * 1000
      : Math.min(60_000, 2 ** attempt * 1000);
    console.log(
      `  ${res.status} on ${path.slice(0, 60)}… — retrying in ${Math.round(waitMs / 1000)}s ` +
        `(attempt ${attempt}/${attempts - 1})`,
    );
    await sleep(waitMs);
  }
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
  const failed = [];
  console.log(`Walking ${pages.length} page(s).`);
  for (const [i, page] of pages.entries()) {
    // Pace the walk. The kit's pages are large and the limiter is per token, so hammering it just
    // converts into backoff waits above; a small gap between pages avoids most of them outright.
    if (i > 0) await sleep(1_500);

    // depth=3 reaches the component sets sitting inside a page's sections/frames, which is where
    // the kit puts them, without descending into every variant's inner layers.
    let nodes;
    try {
      nodes = await figma(`/files/${fileKey}/nodes?ids=${encodeURIComponent(page.id)}&depth=3`);
    } catch (e) {
      // One unreachable page shouldn't discard the pages that did resolve — a partial proposal
      // list is useful, and the summary at the end names what is missing from it.
      console.log(`  page "${page.name}" failed: ${e.message.slice(0, 120)}`);
      failed.push(page.name);
      continue;
    }
    console.log(`  [${i + 1}/${pages.length}] ${page.name}`);
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
  if (failed.length) {
    console.log(`\nWARNING: ${failed.length} page(s) did not resolve: ${failed.join(", ")}`);
    console.log("Components living only on those pages will show as LOW / no candidate.");
  }
  return found;
}

// --- Candidate filtering ------------------------------------------------------------------------
//
// The kit's 350-odd components are not 350 candidates. Most are noise for this purpose, and leaving
// them in doesn't merely add clutter — it wins matches it shouldn't, because an icon named
// `radio_button_checked` shares more tokens with "Checkbox Checked" than the real `Checkbox`
// component set does. The first run proposed exactly that, plus `format_color_fill` for the colour
// role sheet and `text_fields` for the text field.
//
// Three classes get dropped or demoted:
//
//   * **Icons.** The kit vendors Material Symbols as components named in snake_case
//     (`do_not_disturb_on`, `stars_filled`). A design catalog compares components, never glyphs, so
//     they are dropped outright — nothing here should ever resolve to one.
//   * **Building blocks.** `.Building Blocks/…` are the internal parts a component set is assembled
//     from (one segment of a segmented button, a calendar cell). Real nodes, wrong altitude —
//     demoted rather than dropped, so they can still win when nothing else fits.
//   * **XR.** `XR/XR Navigation bar` is a different platform's component. Demoted, because this
//     catalog documents the phone/desktop set.

/** True for a Material Symbols glyph: snake_case with no spaces. */
const isIcon = (name) => /^[a-z0-9]+(_[a-z0-9]+)+$/.test(name.trim());

const isBuildingBlock = (name, containing) =>
  /(^|\/)\.?Building [Bb]locks\//.test(name) || /Building [Bb]locks/.test(containing);

const isXr = (name, containing) => /(^|\/)XR(\/|$)/.test(name) || /\bXR\b/.test(containing);

/** Leading-dot names are the kit's own private components (`.Tonal palettes`, `.Shape`). */
const isPrivate = (name) => name.trim().startsWith(".");

/** Multiplier applied to a candidate's raw name score. 0 drops it entirely. */
function candidateWeight(name, containing) {
  if (isIcon(name)) return 0;
  let w = 1;
  if (isBuildingBlock(name, containing)) w *= 0.35;
  if (isXr(name, containing)) w *= 0.4;
  if (isPrivate(name)) w *= 0.5;
  return w;
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
    .map((k) => {
      // Score against the bare component name first and the name-plus-trail second, taking the
      // better. The trail rescues a component whose own name is generic ("Button segment") and
      // hurts one whose name is already exact, since every extra token dilutes the overlap.
      const raw = Math.max(score(subject, k.name), score(subject, `${k.name} ${k.containing}`));
      return { ...k, s: raw * candidateWeight(k.name, k.containing) };
    })
    .filter((k) => k.s > 0)
    .sort((a, b) => b.s - a.s || a.name.length - b.name.length)
    .slice(0, 3);
  const best = ranked[0];
  const confidence = !best || best.s < 0.34 ? "LOW " : best.s < 0.67 ? "MAYBE" : "GOOD ";
  console.log(`${confidence} ${componentId}`);
  for (const r of ranked) {
    console.log(`        ${r.s.toFixed(2)}  ${r.name}  ->  figma:${fileKey}/${r.nodeId}`);
  }
  if (best?.s) {
    console.log(`        reference = "figma:${fileKey}/${best.nodeId}"`);
  } else {
    console.log(
      `        (no candidate — this component has no place in the inventory; see AGENTS.md)`,
    );
  }
  console.log("");
}
