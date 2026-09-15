#!/usr/bin/env node
/**
 * Write `foundation-catalog/ui-builder.policy.json`'s `builtins` block from the frozen golden.
 *
 * ## Why this block is generated and the rest of the file is not
 *
 * `compose-foundation` publishes the UI builder's own vocabulary — the containers, the screen
 * frames, the image asset and the shape layers a design is assembled out of. Those declarations
 * already exist: the preview server synthesises them at startup from the catalog it packages, and
 * they are what every published palette is handed today. Publishing them from here is a change of
 * WHERE they live, not of what they say, so retyping them would be the one way to get it wrong —
 * and #819 says so in as many words: "a hand-reconstruction of the Wear set missed
 * `remote-compose/custom` and `remote-compose/inline`".
 *
 * So the block is projected from `foundation-catalog/goldens/packaged-m3-builder-vocabulary.json`,
 * a byte-for-byte copy of that catalog's builder-namespace slice, and `--check` regenerates and
 * diffs it — the contract `design-map.json` and the samples specs already carry here. Everything
 * else in the policy file is authored and untouched: the platform word, the frame, the shelf order,
 * the reasons.
 *
 * ## What this file adds to the golden, and why it has to
 *
 * Two things the packaged declarations do not carry, because the packaged catalog is read by a
 * builder that already knows them:
 *
 *  - **a structural role**, which `compose-ui-builder-policy/v1` requires of every builtin. The
 *    golden's own `role` is the ACCEPTANCE vocabulary (`Container`, `Leaf`, `Scaffold`) that slots
 *    name, which is a different question from which template writes the component. [ROLES] maps
 *    them, and the mapping is lossy — see its comment.
 *  - **a canvas adapter**, claimed only where the contract's own registry names one. "The catalog
 *    did not say" and "the catalog said placeholder" are different facts, and a guessed adapter id
 *    would draw a lazy column as whatever that id happens to mean in some future build.
 *
 *     node scripts/foundation-catalog.mjs            # regenerate
 *     node scripts/foundation-catalog.mjs --check    # fail if the committed policy is stale
 */

import { readFileSync, writeFileSync } from "node:fs";

export const GOLDEN = "foundation-catalog/goldens/packaged-m3-builder-vocabulary.json";
export const POLICY = "foundation-catalog/ui-builder.policy.json";
export const CURATIONS = "foundation-catalog/curations.json";

/** The namespaces this catalog owns. `remote-compose/` is deliberately not among them. */
export const OWNED_NAMESPACES = ["layout/", "shape/", "asset/"];

/** The seam namespace: borrowed by a platform, owned by whichever catalog describes Remote Compose. */
export const SEAM_NAMESPACE = "remote-compose/";

/**
 * Shelf order — the order a builtin is written in, and the order a palette reads it in.
 *
 * Not the golden's own (alphabetical by id), because this list is what a person scans: the screen
 * frame first, then the primitives, then the repeaters, then content, then the decorations.
 */
export const ORDER = [
  "layout/scaffold",
  "layout/supporting-pane-scaffold",
  "layout/box",
  "layout/column",
  "layout/row",
  "layout/for-each",
  "layout/lazy-column",
  "layout/lazy-row",
  "layout/lazy-grid",
  "layout/horizontal-carousel",
  "asset/image",
  "shape/colour-dot",
  "shape/linear-gradient",
  "shape/radial-gradient",
];

/**
 * Acceptance role → structural-template role, and the one lossy step in this projection.
 *
 * The schema's roles are a closed set of six — `screen-root`, `list`, `list-item`, `overlay`,
 * `controlled`, `decoration` — described as the roles the Wear screen emitter's 1,406 lines
 * decompose into. The builder's own vocabulary does not decompose into them: a `Box` is not a
 * scrolling list, and `list` is simply the closest of the six for "writes its children in order".
 * `asset/image` and the shapes take `decoration`, which fits the shapes exactly and an image only
 * roughly.
 *
 * Stated here rather than smoothed over, because the contract sets a test for growing the set —
 * "the test that a new role is general is that two catalogs use it" — and a `container` role is the
 * candidate this catalog is the first evidence for. Nothing reads this file yet, so the mismatch
 * costs nothing today and would cost an export later.
 */
export const ROLES = {
  Scaffold: "screen-root",
  Container: "list",
  Leaf: "decoration",
};

/**
 * The canvas adapters this catalog claims, and the only ones it may.
 *
 * `foundation/Column` is named in the contract's own adapter registry, so these three are claims a
 * builder can honour rather than guesses. Everything else gets `placeholder`: a builder that ships
 * no adapter by a name draws the placeholder and logs it once, which is the right outcome for a
 * component nobody has written an adapter for — and a wrong id is the one outcome that draws the
 * wrong thing.
 */
export const CANVAS = {
  "layout/box": "foundation/Box",
  "layout/column": "foundation/Column",
  "layout/row": "foundation/Row",
};

/** The `builtins` block, projected from the golden. */
export function buildBuiltins(golden) {
  const declared = new Map(golden.components.map((c) => [c.componentId, c]));
  const shelves = golden.componentMenu.components;
  const builtins = {};
  for (const id of ORDER) {
    const component = declared.get(id);
    if (!component) throw new Error(`${id} is ordered but the golden declares no such component.`);
    const role = ROLES[component.role];
    if (!role) throw new Error(`${id}: no structural role for acceptance role ${component.role}.`);
    const entry = {
      role,
      displayName: component.displayName,
      group: shelves[id].group,
      canvas: CANVAS[id] ?? "placeholder",
      traits: component.traits,
    };
    if (component.slots.length > 0) {
      entry.slots = Object.fromEntries(
        component.slots.map((slot) => {
          const declaredSlot = {};
          if (slot.acceptedRoles?.length) declaredSlot.acceptedRoles = slot.acceptedRoles;
          if (slot.acceptedTraits?.length) declaredSlot.acceptedTraits = slot.acceptedTraits;
          // `required` and `max` are the two ends of the golden's cardinality. `ordered` has no
          // field in this schema and is dropped, which is recorded in the design doc's table of
          // what a policy cannot yet say.
          if (slot.cardinality.min >= 1) declaredSlot.required = true;
          if (slot.cardinality.max != null) declaredSlot.max = slot.cardinality.max;
          return [slot.name, declaredSlot];
        }),
      );
    }
    if (component.properties.length > 0) {
      entry.properties = component.properties.map((property) => {
        const declaredProperty = {
          name: property.name,
          jsonType: property.jsonType,
          required: Boolean(property.required),
        };
        if (property.allowedValues) declaredProperty.allowedValues = property.allowedValues;
        if (property.notes) declaredProperty.notes = property.notes;
        return declaredProperty;
      });
    }
    entry.modifierCapabilities = component.modifierCapabilities;
    builtins[id] = entry;
  }
  return builtins;
}

/** The policy file with its `builtins` block replaced, every other key untouched and in place. */
export function render(policy, golden) {
  return `${JSON.stringify({ ...policy, builtins: buildBuiltins(golden) }, null, 2)}\n`;
}

function main(argv) {
  const golden = JSON.parse(readFileSync(GOLDEN, "utf8"));
  const policy = JSON.parse(readFileSync(POLICY, "utf8"));
  const json = render(policy, golden);
  const count = Object.keys(JSON.parse(json).builtins).length;

  if (argv.includes("--check")) {
    if (readFileSync(POLICY, "utf8") !== json) {
      console.error(
        `${POLICY}'s builtins are stale: regenerate with \`node scripts/foundation-catalog.mjs\`.`,
      );
      process.exit(1);
    }
    console.log(`${POLICY} is current (${count} builtin(s) from the frozen golden).`);
    return;
  }

  writeFileSync(POLICY, json);
  console.log(`${POLICY}: ${count} builtin(s) projected from ${GOLDEN}.`);
  const curations = JSON.parse(readFileSync(CURATIONS, "utf8"));
  for (const [platform, curation] of Object.entries(curations.platforms)) {
    console.log(`  ${platform} borrows ${curation.components.length}`);
  }
  for (const [platform, declined] of Object.entries(curations.declined ?? {})) {
    console.log(`  ${platform}: not curated here (${declined.reason})`);
  }
}

if (process.argv[1] && process.argv[1].endsWith("foundation-catalog.mjs")) main(process.argv.slice(2));
