#!/usr/bin/env node
// Check `glimmer-kit-gaps.json` — the places `glimmer-catalog`'s inventory stops, and why.
//
// The sibling of `scripts/kit-coverage.mjs`, one level up. That script checks the kit's axis VALUES
// a mapped component does not picture; this one checks whole components that never entered the
// inventory at all, on either side of the join:
//
//   * a kit set with no Glimmer API to invoke (`api-gap`) — the kit's Entity and its four progress
//     indicator sets;
//   * a Glimmer API with no kit node to be a rendition of (`sample-only`) — the pager;
//   * token types whose design counterparts are Styles-page specimens rather than component sets
//     (`tokens`).
//
// Left undeclared, all three look exactly like an unlinked node in the coverage report, which is
// how a decision nobody made and a decision somebody made carefully end up indistinguishable.
//
// ## Checked in both directions, like kit-unauthorable.json
//
// A declaration that cannot rot is the whole point, so every field is re-derived from committed
// data on each run:
//
//   * a named kit node must still be in the imported page cache, and must still be UNLINKED. A node
//     that has gained a sticker means the gap closed, and the declaration has to go.
//   * `absentFromKit` is re-searched across every cached page. The day the kit publishes a node
//     whose name matches, the declaration fails — which is the notification this repo would
//     otherwise never get.
//   * a named sample file must still exist.
//   * `library.version` must equal the version `gradle/libs.versions.toml` pins. Renovate owns that
//     pin, so a bump fails this check until a human re-reads the published API surface. That is the
//     "after the dependency update lands" trigger #433 asks for, wired to the thing that moves.
//
// Reads only committed files — no render, no Figma token, no network.
//
//   node scripts/glimmer-kit-gaps.mjs

import { existsSync, readdirSync, readFileSync } from "node:fs";
import path from "node:path";
import { fileURLToPath } from "node:url";

const ROOT = new URL("../", import.meta.url);
const read = (name) => readFileSync(new URL(name, ROOT), "utf8");
const readJson = (name) => JSON.parse(read(name));

const KINDS = new Set(["api-gap", "sample-only", "tokens"]);

/** The version `gradle/libs.versions.toml` pins for a `[versions]` key. */
export function pinnedVersion(toml, key) {
  const match = new RegExp(`^${key}\\s*=\\s*"([^"]+)"`, "m").exec(toml);
  return match?.[1] ?? null;
}

/** Every node the imported page cache carries, by id, with the page it sits on. */
export function indexPages(pages) {
  const byId = new Map();
  for (const page of pages.pages ?? []) {
    for (const node of page.nodes ?? []) {
      if (!byId.has(node.nodeId)) byId.set(node.nodeId, { ...node, page: page.id });
    }
  }
  return byId;
}

/**
 * Every finding the declarations produce. An empty array is a clean run.
 *
 * Pure, so `glimmer-kit-gaps.test.mjs` can drive it with a moved kit and a bumped version rather
 * than waiting for either to happen.
 */
export function checkGaps({ gaps, pages, designMap, versions, exists, stickerSources = "" }) {
  const findings = [];
  const byId = indexPages(pages);
  const pageIds = new Set((pages.pages ?? []).map((page) => page.id));
  const mappedNodes = new Set(
    (designMap.components ?? []).map((component) => String(component.ref ?? "").split("/").pop()),
  );

  const pinned = pinnedVersion(versions, gaps.library?.versionRef ?? "glimmer");
  if (pinned == null) {
    findings.push(`library.versionRef "${gaps.library?.versionRef}" names no [versions] entry`);
  } else if (pinned !== gaps.library?.version) {
    findings.push(
      `${gaps.library?.coordinate} is pinned at ${pinned}, and every api-gap below was read from ` +
        `${gaps.library?.version}. Re-read the published API surface for that version and update ` +
        `glimmer-kit-gaps.json — a gap that closed upstream is a component to implement, not a ` +
        `line to re-date.`,
    );
  }

  const seen = new Set();
  for (const declaration of gaps.declarations ?? []) {
    const id = declaration.id;
    const at = `declaration ${JSON.stringify(id ?? "")}`;
    if (!id) findings.push("a declaration names no id");
    else if (seen.has(id)) findings.push(`${at} is declared twice`);
    seen.add(id);
    if (!KINDS.has(declaration.kind)) {
      findings.push(`${at} has kind ${JSON.stringify(declaration.kind)}, not one of ${[...KINDS]}`);
    }
    for (const field of ["reason", "evidence", "issue"]) {
      if (!declaration[field]) findings.push(`${at} carries no ${field}`);
    }

    for (const nodeId of declaration.kitNodes ?? []) {
      const node = byId.get(nodeId);
      if (!node) {
        findings.push(
          `${at} names kit node ${nodeId}, which the imported page cache no longer carries — the ` +
            `kit moved, so the gap has to be re-read rather than re-stated`,
        );
        continue;
      }
      if (node.link !== "unlinked" || mappedNodes.has(nodeId)) {
        findings.push(
          `${at} names kit node ${nodeId} ("${node.name}"), which is now mapped to code. The gap ` +
            `closed: delete the declaration.`,
        );
      }
    }

    if (declaration.absentFromKit) {
      const pattern = new RegExp(declaration.absentFromKit, "i");
      const found = [...byId.values()].filter((node) => pattern.test(node.name));
      if (found.length > 0) {
        findings.push(
          `${at} says the kit publishes nothing matching /${declaration.absentFromKit}/i, but ` +
            `${found.length} node(s) now do — ${found[0].page}/${found[0].nodeId} ` +
            `("${found[0].name}"). Implement it, in its own PR, and delete this declaration.`,
        );
      }
    }

    for (const page of declaration.pages ?? []) {
      if (!pageIds.has(page)) {
        findings.push(`${at} names page "${page}", which the imported page cache does not carry`);
      }
    }

    const samples = [declaration.sample, ...(declaration.samples ?? [])].filter(Boolean);
    for (const sample of samples) {
      if (!exists(sample)) findings.push(`${at} names ${sample}, which does not exist`);
    }
    if (declaration.kind === "sample-only" && samples.length === 0) {
      findings.push(`${at} is sample-only and names no sample`);
    }
    if (declaration.kind === "api-gap" && (declaration.kitNodes ?? []).length === 0) {
      findings.push(`${at} is an api-gap and names no kit node`);
    }
  }
  // The completeness statement: every public component composable the pinned library exports is
  // either called by a sticker or declared here. #414 asked for the foundation components, and this
  // is what "all of them" means in a form the build can check — an API a new release adds fails the
  // version pin above, and an API this repository stops calling fails right here.
  const declaredApis = new Set(
    (gaps.declarations ?? []).flatMap((declaration) => declaration.apis ?? []),
  );
  for (const component of gaps.library?.components ?? []) {
    const invoked = new RegExp(`\\b${component}\\s*\\(`).test(stickerSources);
    if (!invoked && !declaredApis.has(component)) {
      findings.push(
        `${gaps.library.coordinate} publishes ${component}, and no sticker calls it and no ` +
          `declaration accounts for it. Implement it against its kit node, or declare the gap.`,
      );
    }
    if (invoked && declaredApis.has(component)) {
      findings.push(
        `${component} is declared as a gap and called by a sticker. One of the two is stale.`,
      );
    }
  }

  return findings;
}

/** Every sticker source in `:glimmer-catalog`, concatenated, with comments removed. */
function readStickerSources() {
  const dir = new URL("glimmer-catalog/src/main/kotlin/ee/schimke/m3catalog/glimmer/", ROOT);
  return readdirSync(dir)
    .filter((name) => name.endsWith(".kt"))
    .map((name) => readFileSync(new URL(name, dir), "utf8"))
    .join("\n")
    // A comment naming a component is not a call to it — this module's comments are long and quote
    // the APIs they discuss, including ones it deliberately does not draw.
    .replace(/\/\*[\s\S]*?\*\//g, "")
    .replace(/\/\/[^\n]*/g, "");
}

function main() {
  const gaps = readJson("glimmer-kit-gaps.json");
  const findings = checkGaps({
    gaps,
    pages: readJson("glimmer-design/pages/pages.json"),
    designMap: readJson("glimmer-design-map.json"),
    versions: read("gradle/libs.versions.toml"),
    exists: (relative) => existsSync(new URL(relative, ROOT)),
    stickerSources: readStickerSources(),
  });

  for (const declaration of gaps.declarations ?? []) {
    const where =
      declaration.kind === "api-gap"
        ? `${(declaration.kitNodes ?? []).length} kit node(s), no Glimmer API`
        : declaration.kind === "sample-only"
          ? "a Glimmer API, no kit node"
          : `${(declaration.pages ?? []).length} Styles page(s), no component set`;
    console.log(`  ${declaration.id.padEnd(18)} ${where}  (#${declaration.issue})`);
  }

  if (findings.length > 0) {
    for (const finding of findings) console.error(`::error::glimmer-kit-gaps: ${finding}`);
    process.exit(1);
  }
  console.log(
    `✓ ${gaps.declarations.length} declared gap(s) still hold against the kit, the page cache ` +
      `and ${gaps.library.coordinate}:${gaps.library.version}.`,
  );
}

// Only when run as a script: the test drives `checkGaps` with a moved kit rather than the real one.
if (process.argv[1] && path.resolve(process.argv[1]) === fileURLToPath(import.meta.url)) main();
