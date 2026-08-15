// The kit's interaction states, held to the map that is supposed to compare them.
//
// `State` is the one axis whose values the catalog could not reach at all until
// compose-ai-tools 1.6.0 made an interaction variant addressable, and the first
// pass covered 69 of its 75 reachable values. The other six draw nothing:
// Compose has no hover appearance for the slider, the text field or the search
// bar, and no press appearance for the search bar (issue #91). Those cells are
// deliberately not authored — a variant whose render is byte-identical to its
// base publishes a sticker that claims a state it does not show, and parity
// then reports a divergence three levels from its cause.
//
// So the exception list IS the finding, and this test is what keeps it true:
//
//   * author a component whose set publishes an interaction state and forget
//     the cell, and the first assertion names it;
//   * fix one of the six upstream, drop it from `NOT_DRAWN_BY_COMPOSE`, and the
//     second assertion tells you the cell is now yours to author;
//   * leave a stale exception in the list after covering it, and the second
//     assertion catches that too.
//
// Both directions are checked against the COMMITTED `design-map.json` and
// `figma-kit-index.json`, so this runs without a render or a Figma token.

import assert from "node:assert/strict";
import { readFileSync } from "node:fs";
import test from "node:test";

const designMap = JSON.parse(readFileSync("design-map.json", "utf8"));
const kitIndex = JSON.parse(readFileSync("figma-kit-index.json", "utf8"));

/**
 * The kit's spellings of an interaction state. `Presssed` is the kit's own
 * misspelling, carried on all ten `Button - outline` press variants and nowhere
 * else; `kit-variants.mjs` translates it, and it counts as covered here for the
 * same reason it resolves there.
 */
const INTERACTION_STATES = ["Hovered", "Focused", "Pressed", "Presssed"];

/**
 * Set name -> the states Compose does not draw, and so the catalog does not
 * author. Every entry is measured — the variant was rendered by driving a real
 * interaction and came out byte-identical to the resting render — and reported
 * upstream in issue #91. Shrink this list as they are fixed; never grow it to
 * silence a failure without a measurement and an issue behind it.
 */
const NOT_DRAWN_BY_COMPOSE = {
  "Search bar": ["Hovered", "Pressed"],
  "Standard slider": ["Hovered"],
  "Range slider": ["Hovered"],
  "Text field": ["Hovered"],
};

const axesOf = (variantName) =>
  Object.fromEntries(
    variantName
      .split(",")
      .map((part) => part.trim())
      .filter(Boolean)
      .map((part) => {
        const at = part.indexOf("=");
        return at < 0 ? [part, ""] : [part.slice(0, at).trim(), part.slice(at + 1).trim()];
      }),
  );

const nodeIndex = new Map();
for (const [setId, set] of Object.entries(kitIndex.sets)) {
  for (const variant of set.variants) {
    nodeIndex.set(variant.id, { setId, setName: set.name, variantName: variant.name });
  }
}

/** Every referenced set, with the interaction states it publishes and the ones the map covers. */
function referencedSets() {
  const covered = new Map();
  for (const component of designMap.components) {
    const refs = Array.isArray(component.ref) ? component.ref : [component.ref];
    for (const entry of refs) {
      const ref = typeof entry === "string" ? entry : entry?.ref;
      if (!ref) continue;
      const node = nodeIndex.get(ref.split("/").pop());
      if (!node) continue;
      if (!covered.has(node.setId)) covered.set(node.setId, new Set());
      const state = axesOf(node.variantName).State;
      if (state) covered.get(node.setId).add(state);
    }
  }
  return [...covered].map(([setId, states]) => {
    const set = kitIndex.sets[setId];
    const published = new Set();
    for (const variant of set.variants) {
      const state = axesOf(variant.name).State;
      if (state) published.add(state);
    }
    return { name: set.name, published, covered: states };
  });
}

test("every interaction state the kit publishes is compared, or listed as undrawable", () => {
  const missing = [];
  for (const set of referencedSets()) {
    const excused = NOT_DRAWN_BY_COMPOSE[set.name] ?? [];
    for (const state of INTERACTION_STATES) {
      if (!set.published.has(state)) continue;
      if (set.covered.has(state)) continue;
      if (excused.includes(state)) continue;
      missing.push(`${set.name} / ${state}`);
    }
  }
  assert.deepEqual(
    missing,
    [],
    "the kit draws these interaction states and nothing in the catalog compares them. Author the " +
      "cell with @InteractionStates (or a single @OverrideVariant(interaction = …)) on the " +
      "component that references the set. If Compose genuinely draws nothing for it, measure that " +
      "— render the variant and diff it against the resting render — then add it to " +
      "NOT_DRAWN_BY_COMPOSE with an upstream issue, the way issue #91 records the current six.",
  );
});

test("nothing sits in the undrawable list that is already compared", () => {
  const byName = new Map(referencedSets().map((set) => [set.name, set]));
  const stale = [];
  for (const [name, states] of Object.entries(NOT_DRAWN_BY_COMPOSE)) {
    const set = byName.get(name);
    if (!set) {
      stale.push(`${name} — no mapped component references this set any more`);
      continue;
    }
    for (const state of states) {
      if (!set.published.has(state)) stale.push(`${name} / ${state} — the kit no longer draws it`);
      if (set.covered.has(state)) stale.push(`${name} / ${state} — already compared`);
    }
  }
  assert.deepEqual(
    stale,
    [],
    "an exception outlived what it was excusing. If Compose now draws the state, delete the entry " +
      "here and author the cell; issue #91 is the record of why each one was added.",
  );
});
