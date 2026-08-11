#!/usr/bin/env node
// Project `figma-kit-index.json` — the kit vocabulary the design map resolves
// variants against — from a design map and an inventory dump.
//
//   FIGMA_TOKEN=figd_… node scripts/build-kit-index.mjs \
//     --map design-map.json --inventory figma-inventory.json \
//     --file ocdacdEsnHipMJD3egzxKb --out figma-kit-index.json
//
// WHY A SCRIPT AND NOT A ONE-OFF
//
// The index is an input to `generate-design-map.mjs`, which CI checks for
// staleness. An index built by hand goes quietly out of date the first time the
// kit gains a variant — and being quiet is the whole problem, because a missing
// variant reads as "no counterpart in the kit" rather than "nobody looked".
//
// Nothing here is specific to this catalog: the sets it keeps are the ones the
// map references, so another catalog pointed at another file gets its own
// vocabulary from the same code.
//
// WHY IT CALLS FIGMA AT ALL
//
// The inventory walk sees variant names, which carry the axes. It does NOT see
// `componentPropertyDefinitions` — Figma returns those only for nodes requested
// directly, never for one reached by descending a page. Properties are where
// the kit keeps everything the axes do not (whether a button draws its icon,
// whether a card has an action row), and a reference rendered without them is
// rendered at their defaults, so an index that omits them describes something
// other than what a reader will see (#21).
//
// The requests are batched: the referenced sets are asked for in chunks, which
// is a handful of calls rather than one per set.

import { readFileSync, writeFileSync } from "node:fs";

function arg(name, fallback) {
  const i = process.argv.indexOf(`--${name}`);
  return i >= 0 && process.argv[i + 1] ? process.argv[i + 1] : fallback;
}

const mapPath = arg("map", "design-map.json");
const inventoryPath = arg("inventory", "figma-inventory.json");
const fileKey = arg("file", "ocdacdEsnHipMJD3egzxKb");
const outPath = arg("out", "figma-kit-index.json");
const token = process.env.FIGMA_TOKEN;

/** Node ids per request. Small enough to retry cheaply, large enough to matter. */
const BATCH = 40;

const sleep = (ms) => new Promise((r) => setTimeout(r, ms));

async function figma(path, { attempts = 5 } = {}) {
  for (let attempt = 1; ; attempt++) {
    const res = await fetch(`https://api.figma.com/v1${path}`, {
      headers: { "X-Figma-Token": token },
    });
    if (res.ok) return res.json();
    const retryable = res.status === 429 || res.status >= 500;
    if (!retryable || attempt >= attempts) {
      throw new Error(`GET ${path.slice(0, 70)} -> ${res.status}`);
    }
    const after = Number(res.headers.get("retry-after"));
    await sleep(Number.isFinite(after) && after > 0 ? after * 1000 : 2 ** attempt * 500);
  }
}

const map = JSON.parse(readFileSync(mapPath, "utf8"));
const inventory = JSON.parse(readFileSync(inventoryPath, "utf8"));

// Every node the map points at, across both the string and the tagged-list form.
const referenced = new Set();
for (const c of map.components ?? []) {
  const refs = typeof c.ref === "string" ? [{ ref: c.ref }] : (c.ref ?? []);
  for (const r of refs) {
    // `figma:<fileKey>/<nodeId>`, and the node id itself contains a colon —
    // so strip the scheme, then split on the FIRST slash only.
    if (!r.ref.startsWith("figma:")) continue;
    const rest = r.ref.slice("figma:".length);
    const slash = rest.indexOf("/");
    if (slash < 0) continue;
    if (rest.slice(0, slash) === fileKey) referenced.add(rest.slice(slash + 1));
  }
}

const components = [];
for (const page of inventory.pages ?? []) {
  for (const c of page.components ?? []) components.push({ ...c, inventoryPage: page });
}

// A referenced node is either one variant of a set — in which case the whole set
// is the vocabulary for that component — or a standalone component. Keep the
// standalone itself unconditionally; when its name has a `Horizontal/…`-style
// folder, its siblings form the variant vocabulary and are kept beside it.
const keepSets = new Set();
const keepFolders = new Set();
const keepStandalone = new Set();
for (const c of components) {
  if (referenced.has(c.id)) {
    keepStandalone.add(c.id);
    if (c.name.includes("/")) {
      keepFolders.add(c.name.slice(0, c.name.lastIndexOf("/")));
    }
  }
  for (const v of c.children ?? []) if (referenced.has(v.id)) keepSets.add(c.id);
}

const sets = {};
const standalone = {};
for (const c of components) {
  if (keepSets.has(c.id)) {
    sets[c.id] = {
      name: c.name,
      variants: (c.children ?? []).map((v) => {
        // A few kit component sets are deliberately hidden. Figma returns their
        // definition ids from `/nodes`, but `/images` exports them as a 1px
        // placeholder or "node not found". Component pages place one visible
        // instance of every variant under an Examples frame; use that instance
        // as the render handle while retaining the definition id as vocabulary.
        const examples = (c.inventoryPage.renderInstances ?? []).filter(
          (instance) => instance.componentId === v.id && instance.example,
        );
        return {
          id: v.id,
          name: v.name,
          ...(c.hidden && examples.length === 1 ? { renderId: examples[0].id } : {}),
        };
      }),
    };
  }
  if (
    keepStandalone.has(c.id) ||
    (c.name.includes("/") && keepFolders.has(c.name.slice(0, c.name.lastIndexOf("/"))))
  ) {
    standalone[c.id] = { name: c.name };
  }
}

// --- Properties, which the walk could not see --------------------------------

const ids = Object.keys(sets);
let propertied = 0;
if (token && ids.length) {
  for (let i = 0; i < ids.length; i += BATCH) {
    const chunk = ids.slice(i, i + BATCH);
    if (i > 0) await sleep(500);
    const res = await figma(
      `/files/${fileKey}/nodes?ids=${encodeURIComponent(chunk.join(","))}&depth=1`,
    );
    for (const id of chunk) {
      const defs = res.nodes?.[id]?.document?.componentPropertyDefinitions ?? {};
      // VARIANT entries restate the axes already in the variant names; the rest
      // is what a reference render silently applies at its default.
      const props = Object.entries(defs)
        .filter(([, v]) => v.type !== "VARIANT")
        .map(([k, v]) => [k.split("#")[0], { type: v.type, default: v.defaultValue }]);
      if (props.length) {
        sets[id].properties = Object.fromEntries(props);
        propertied += 1;
      }
    }
  }
} else if (!token) {
  console.log("FIGMA_TOKEN is not set — writing the index without component properties.");
}

const variants = Object.values(sets).reduce((n, s) => n + s.variants.length, 0);
const renderAliases = Object.values(sets).reduce(
  (n, s) => n + s.variants.filter((variant) => variant.renderId).length,
  0,
);
writeFileSync(
  outPath,
  `${JSON.stringify({ fileKey, generatedBy: "scripts/build-kit-index.mjs", sets, standalone }, null, 2)}\n`,
);
console.log(
  `Wrote ${outPath}: ${Object.keys(sets).length} set(s), ${variants} variant(s), ` +
    `${renderAliases} hidden variant render alias(es), ` +
    `${Object.keys(standalone).length} standalone component(s), ` +
    `${propertied} set(s) carrying component properties.`,
);
