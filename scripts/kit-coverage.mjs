// Which of the kit's axis values the catalog actually pictures.
//
// `design-map.json` says which kit NODE each sticker renders. A node's name is a
// list of axis values ("Configuration=Small, Elevation=Flat"), so the set of
// nodes a catalog references also says which VALUES it has a picture of — and,
// by subtraction from what the kit publishes, which it does not. That
// subtraction is the work list this repo runs on, and it was being done by hand.
//
// Run it: `node scripts/kit-coverage.mjs` prints the report, and exits non-zero
// only if `kit-unauthorable.json` has gone stale.
//
// Two kinds of axis are deliberately NOT this file's business:
//
//   * `State` — scripts/interaction-coverage.test.mjs owns it, with its own
//     exception list. Two detectors over one axis would eventually disagree.
//   * quantity axes — a sticker showing three of a thing is not missing a
//     picture of four. These are counted and set aside, not listed as work.
//
// Reads only committed JSON, so it needs no render and no Figma token.

import { readFileSync } from "node:fs";

/**
 * Axes whose values are amounts rather than appearances. A catalog covers such
 * an axis by picturing the component, not by picturing every count — `Nav
 * items=5` is the same rail as `Nav items=3` with two more items.
 */
export const QUANTITY_AXES = new Set([
  "Progress",
  "Steps",
  "Density",
  "Segments",
  "Count",
  "Items",
  "Number",
  "Lines",
  "Nav items",
  "Value",
  "Groups",
  "Thickness",
  "Icons",
]);

/** Owned by interaction-coverage.test.mjs; see the header. */
export const FOREIGN_AXES = new Set(["State"]);

/**
 * Interaction states as the kit spells them, INCLUDING its own `Presssed`
 * misspelling (carried on the outlined button's press variants and nowhere
 * else).
 *
 * These appear on axes the sibling detector does not own: a split button names
 * its halves `Leading state` and `Trailing state`, so its hover and press cells
 * are interaction work that the `State`-axis test never sees. Counting them as
 * ordinary appearance cells would overstate the appearance backlog; dropping
 * them would orphan real work. They get their own line instead.
 */
export const INTERACTION_VALUES = new Set([
  "Hovered",
  "Focused",
  "Pressed",
  "Presssed",
  "Dragged",
]);

/** `"Type=Modal, Show back=True"` becomes `{ Type: "Modal", "Show back": "True" }`. */
export function parseAxes(variantName) {
  return Object.fromEntries(
    String(variantName)
      .split(",")
      .map((part) => part.trim())
      .filter(Boolean)
      .map((part) => {
        const at = part.indexOf("=");
        return at < 0 ? [part, ""] : [part.slice(0, at).trim(), part.slice(at + 1).trim()];
      }),
  );
}

/** Every node id the design map points at, whatever shape the entry takes. */
function referencedNodeIds(designMap) {
  const ids = [];
  for (const component of designMap.components ?? []) {
    const refs = Array.isArray(component.ref) ? component.ref : [component.ref];
    for (const ref of refs) {
      const handle = typeof ref === "string" ? ref : ref?.ref;
      if (handle) ids.push(handle.split("/").pop());
    }
  }
  return ids;
}

/**
 * Coverage of the sets the catalog references, value by value.
 *
 * A set the catalog does not reference AT ALL is not reported: that is a missing
 * component, which is a different (and louder) gap than a missing cell, and
 * counting its every value here would bury the cells among them.
 */
export function computeCoverage(designMap, kitIndex) {
  const byNode = new Map();
  for (const [setId, set] of Object.entries(kitIndex.sets ?? {})) {
    for (const variant of set.variants ?? []) {
      byNode.set(variant.id, { setId, setName: set.name, variantName: variant.name });
    }
  }

  const covered = new Map(); // setId -> axis -> Set(value)
  let unknownRefs = 0;
  const nodeIds = referencedNodeIds(designMap);
  for (const id of nodeIds) {
    const info = byNode.get(id);
    if (!info) {
      unknownRefs++;
      continue;
    }
    const axes = covered.get(info.setId) ?? new Map();
    covered.set(info.setId, axes);
    for (const [axis, value] of Object.entries(parseAxes(info.variantName))) {
      const values = axes.get(axis) ?? new Set();
      axes.set(axis, values);
      values.add(value);
    }
  }

  const uncovered = [];
  const interaction = [];
  let quantity = 0;
  for (const [setId, axes] of covered) {
    const set = kitIndex.sets[setId];
    const published = new Map();
    for (const variant of set.variants ?? []) {
      for (const [axis, value] of Object.entries(parseAxes(variant.name))) {
        const values = published.get(axis) ?? new Set();
        published.set(axis, values);
        values.add(value);
      }
    }
    for (const [axis, values] of published) {
      if (FOREIGN_AXES.has(axis)) continue;
      const seen = axes.get(axis) ?? new Set();
      for (const value of values) {
        if (seen.has(value)) continue;
        if (QUANTITY_AXES.has(axis)) {
          quantity++;
          continue;
        }
        if (INTERACTION_VALUES.has(value)) {
          interaction.push({ set: set.name, axis, value });
          continue;
        }
        uncovered.push({ set: set.name, axis, value });
      }
    }
  }
  uncovered.sort((a, b) =>
    `${a.set}${a.axis}${a.value}`.localeCompare(`${b.set}${b.axis}${b.value}`),
  );
  return {
    refs: nodeIds.length,
    unknownRefs,
    setsReferenced: covered.size,
    uncovered,
    interaction,
    quantity,
  };
}

/** Declarations flattened to one row per value, in the order they are written. */
export function declaredValues(declarations) {
  return (declarations.declarations ?? []).flatMap((entry) =>
    entry.values.map((value) => ({
      set: entry.set,
      axis: entry.axis,
      value,
      reason: entry.reason,
      evidence: entry.evidence,
    })),
  );
}

const key = (row) => `${row.set} ${row.axis} ${row.value}`;

/**
 * Both directions, which is the whole point of declaring anything:
 *
 *   * `unpublished` — the entry names a set, axis or value the kit no longer
 *     has. The kit moved; the entry is now about nothing.
 *   * `nowCovered` — the catalog covers it after all. The excuse outlived the
 *     problem and should be deleted, not left as a standing exemption.
 */
export function auditDeclarations(coverage, declarations, kitIndex) {
  const publishedValues = new Set();
  for (const set of Object.values(kitIndex.sets ?? {})) {
    for (const variant of set.variants ?? []) {
      for (const [axis, value] of Object.entries(parseAxes(variant.name))) {
        publishedValues.add(`${set.name} ${axis} ${value}`);
      }
    }
  }
  // Both buckets count as not-covered. A declaration for one of the split
  // button's per-slot interaction cells is still a declaration about something
  // the catalog does not picture, and reporting it as stale would delete the
  // reason and leave the gap.
  const uncoveredKeys = new Set([...coverage.uncovered, ...coverage.interaction].map(key));
  const rows = declaredValues(declarations);
  return {
    rows,
    unpublished: rows.filter((row) => !publishedValues.has(key(row))),
    nowCovered: rows.filter((row) => publishedValues.has(key(row)) && !uncoveredKeys.has(key(row))),
  };
}

export function loadInputs(root = ".") {
  return {
    designMap: JSON.parse(readFileSync(`${root}/design-map.json`, "utf8")),
    kitIndex: JSON.parse(readFileSync(`${root}/figma-kit-index.json`, "utf8")),
    declarations: JSON.parse(readFileSync(`${root}/kit-unauthorable.json`, "utf8")),
  };
}

function main() {
  const { designMap, kitIndex, declarations } = loadInputs();
  const coverage = computeCoverage(designMap, kitIndex);
  const audit = auditDeclarations(coverage, declarations, kitIndex);
  const declared = new Set(audit.rows.map(key));
  const open = coverage.uncovered.filter((row) => !declared.has(key(row)));

  console.log(
    `${coverage.refs} references over ${coverage.setsReferenced} sets` +
      (coverage.unknownRefs ? `, ${coverage.unknownRefs} pointing outside the kit index` : ""),
  );
  console.log(
    `${open.length} uncovered value(s) to author, ` +
      `${audit.rows.length} declared unauthorable, ` +
      `${coverage.quantity} quantity value(s) set aside`,
  );
  if (coverage.interaction.length) {
    const sets = [...new Set(coverage.interaction.map((row) => row.set))].join(", ");
    console.log(
      `  plus ${coverage.interaction.length} interaction value(s) on axes the State ` +
        `detector does not own (${sets})`,
    );
  }

  const byAxis = new Map();
  for (const row of open) {
    const list = byAxis.get(row.axis) ?? [];
    byAxis.set(row.axis, list);
    list.push(`${row.set}=${row.value}`);
  }
  for (const [axis, list] of [...byAxis].sort((a, b) => b[1].length - a[1].length)) {
    console.log(`  ${axis} (${list.length}): ${list.join(" | ")}`);
  }

  for (const row of audit.unpublished) {
    console.error(`STALE: ${row.set} / ${row.axis}=${row.value} is not published by the kit`);
  }
  for (const row of audit.nowCovered) {
    console.error(`STALE: ${row.set} / ${row.axis}=${row.value} is covered — delete the declaration`);
  }
  if (audit.unpublished.length || audit.nowCovered.length) process.exitCode = 1;
}

if (import.meta.url === `file://${process.argv[1]}`) main();
