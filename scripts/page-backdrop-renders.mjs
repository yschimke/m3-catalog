#!/usr/bin/env node
// Emit `--render <code>=<path>` pairs for `design-parity-pages view`, so the page-backdrop
// viewer can lay each component's own render over its placement on the design.
//
// WHY THIS READS BASELINES RATHER THAN RENDERING
//
// The viewer needs one PNG per code component. Rendering the catalog to get them costs ~43 minutes
// (1095 previews, see docs/PARALLEL_RENDER.md) — absurd when a page backdrop needs a couple of
// dozen. The `compose-preview/main` branch already holds every render, refreshed on every merge to
// main by `compose-preview.yml`. So this joins against that instead: no Gradle, no render, no
// Android SDK.
//
// WHY IT NEVER COMPUTES A FILENAME
//
// A render's filename is not derivable from its preview id. `renderOutput` strips the *common*
// dotted package prefix across every preview in the module (compose-ai-tools
// docs/RENDER_FILENAMES.md), so the answer depends on the whole set, and the baseline branch
// flattens it again to a basename. Both are recorded facts, not rules worth re-deriving: the
// branch's `baselines.json` is keyed `<module>/<previewId>` and carries `module` +
// `renderBasename`. This reads that. If the naming ever changes, the join keeps working.
//
// USAGE
//
//   node scripts/page-backdrop-renders.mjs --baseline-root <checkout of compose-preview/main>
//
// Prints one `code=path` per line on stdout; unmatched components go to stderr as a warning and
// are skipped rather than failing — a component with no render simply has no overlay.

import { existsSync, readFileSync } from "node:fs";
import { join } from "node:path";

function arg(name, fallback) {
  const i = process.argv.indexOf(`--${name}`);
  return i >= 0 && process.argv[i + 1] ? process.argv[i + 1] : fallback;
}

const designMapPath = arg("design-map", "design-map.json");
const baselineRoot = arg("baseline-root", ".compose-preview-baseline");

const baselinesPath = join(baselineRoot, "baselines.json");
if (!existsSync(baselinesPath)) {
  console.error(`page-backdrop-renders: no baselines.json under '${baselineRoot}'.`);
  console.error("Check out the compose-preview/main branch there first.");
  process.exit(1);
}

const designMap = JSON.parse(readFileSync(designMapPath, "utf8"));
const baselines = JSON.parse(readFileSync(baselinesPath, "utf8"));

/** design-map `previewId` is normally a string; the schema also allows variant objects. */
function previewIdsOf(entry) {
  const p = entry.previewId;
  if (typeof p === "string") return [p];
  if (Array.isArray(p)) return p.map((v) => v?.previewId).filter((v) => typeof v === "string");
  return [];
}

const pairs = [];
const missing = [];
const seen = new Set();

for (const entry of designMap.components ?? []) {
  // One render per code handle: the first preview that resolves wins. A design-map entry with
  // several variant previews still overlays one image, since a placement is one rectangle.
  if (seen.has(entry.code)) continue;

  let matched;
  for (const previewId of previewIdsOf(entry)) {
    for (const key of [`catalog/${previewId}`, previewId]) {
      const b = baselines[key];
      if (!b?.renderBasename) continue;
      const rel = join("renders", b.module ?? "catalog", b.renderBasename);
      if (!existsSync(join(baselineRoot, rel))) continue;
      matched = join(baselineRoot, rel);
      break;
    }
    if (matched) break;
  }

  if (matched) {
    seen.add(entry.code);
    pairs.push(`${entry.code}=${matched}`);
  } else {
    missing.push(entry.code);
  }
}

for (const pair of pairs) console.log(pair);

console.error(`page-backdrop-renders: ${pairs.length} render(s) matched, ${missing.length} without one.`);
for (const code of missing) console.error(`  no render: ${code}`);
