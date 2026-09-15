import { test } from "node:test";
import assert from "node:assert/strict";
import { readFileSync } from "node:fs";

import {
  CANVAS,
  CURATIONS,
  GOLDEN,
  ORDER,
  OWNED_NAMESPACES,
  POLICY,
  ROLES,
  SEAM_NAMESPACE,
  buildBuiltins,
  render,
} from "./foundation-catalog.mjs";

const golden = JSON.parse(readFileSync(GOLDEN, "utf8"));
const policy = JSON.parse(readFileSync(POLICY, "utf8"));
const curations = JSON.parse(readFileSync(CURATIONS, "utf8"));

const declared = new Map(golden.components.map((c) => [c.componentId, c]));

/**
 * The frozen sets, written out here rather than read from the files under test.
 *
 * This is the one place in this suite allowed to restate them: a test that read the sets from the
 * same file it checks would pass on any edit, which is the failure #819 warns about — "a
 * hand-reconstruction of the Wear set missed `remote-compose/custom` and `remote-compose/inline`".
 * These two lists are the issue's own table, and changing one is meant to be a deliberate act with
 * a reviewer.
 */
const MOBILE = [
  "asset/image",
  "layout/box",
  "layout/column",
  "layout/for-each",
  "layout/horizontal-carousel",
  "layout/lazy-column",
  "layout/lazy-grid",
  "layout/lazy-row",
  "layout/row",
  "layout/scaffold",
  "layout/supporting-pane-scaffold",
  "remote-compose/custom",
  "remote-compose/document",
  "remote-compose/inline",
  "shape/colour-dot",
  "shape/linear-gradient",
  "shape/radial-gradient",
];

const WEAR = [
  "layout/box",
  "layout/column",
  "layout/row",
  "asset/image",
  "remote-compose/document",
  "remote-compose/inline",
  "remote-compose/custom",
];

const ids = (list) => list.map((c) => c.componentId);

test("the catalog declares every component it owns, and nothing it does not", () => {
  const owned = golden.components
    .map((c) => c.componentId)
    .filter((id) => OWNED_NAMESPACES.some((ns) => id.startsWith(ns)));
  assert.deepEqual(Object.keys(policy.builtins).slice().sort(), owned.slice().sort());
  // The seams are Remote Compose's. Declaring them here would be this catalog claiming a vocabulary
  // it does not own — the move `m3-catalog`'s own policy refuses one level up.
  for (const id of Object.keys(policy.builtins)) {
    assert.ok(!id.startsWith(SEAM_NAMESPACE), `${id} is a Remote Compose seam, not the foundation's`);
  }
});

test("every declaration is the packaged one, field for field", () => {
  // The whole claim of this catalog: publishing the vocabulary from here is a change of WHERE it
  // lives, not of what it says. Traits, slot acceptance, properties and modifiers are the golden's.
  for (const [id, builtin] of Object.entries(policy.builtins)) {
    const base = declared.get(id);
    assert.deepEqual(builtin.traits, base.traits, `${id}: traits`);
    assert.deepEqual(builtin.modifierCapabilities, base.modifierCapabilities, `${id}: modifiers`);
    assert.equal(builtin.displayName, base.displayName, `${id}: display name`);
    assert.deepEqual(
      (builtin.properties ?? []).map((p) => p.name),
      base.properties.map((p) => p.name),
      `${id}: properties`,
    );
    assert.deepEqual(
      Object.keys(builtin.slots ?? {}),
      base.slots.map((s) => s.name),
      `${id}: slots`,
    );
    for (const slot of base.slots) {
      const published = builtin.slots[slot.name];
      assert.deepEqual(published.acceptedTraits, slot.acceptedTraits, `${id}.${slot.name}: traits`);
      assert.equal(published.required ?? false, slot.cardinality.min >= 1, `${id}.${slot.name}: min`);
      assert.equal(published.max ?? null, slot.cardinality.max, `${id}.${slot.name}: max`);
    }
  }
});

test("every builtin names a role the template engine knows", () => {
  // A closed set of six. A role the engine does not know selects no template, and a typo here is
  // caught before a render rather than during an export.
  const engineRoles = ["screen-root", "list", "list-item", "overlay", "controlled", "decoration"];
  for (const [id, builtin] of Object.entries(policy.builtins)) {
    assert.ok(engineRoles.includes(builtin.role), `${id}: ${builtin.role} is not an engine role`);
    assert.equal(builtin.role, ROLES[declared.get(id).role], `${id}: role mapping`);
  }
});

test("a canvas adapter is claimed only where one is known to exist", () => {
  // `foundation/Column` is named in the contract's own registry; everything else is `placeholder`.
  // A guessed adapter id is the one outcome that draws the wrong thing.
  for (const [id, builtin] of Object.entries(policy.builtins)) {
    assert.equal(builtin.canvas, CANVAS[id] ?? "placeholder", `${id}: canvas`);
  }
});

test("every builtin sits on a shelf the menu orders", () => {
  // An unnamed group sorts after every named one — the failure this repository's own policy records
  // at length, where twenty-nine shelves ended up alphabetised.
  for (const [id, builtin] of Object.entries(policy.builtins)) {
    assert.ok(policy.menu.groupOrder.includes(builtin.group), `${id} is filed under ${builtin.group}`);
  }
});

test("the shelf order is the packaged one, so a palette does not move on cutover", () => {
  const packaged = new Set(Object.values(golden.componentMenu.components).map((c) => c.group));
  for (const group of policy.menu.groupOrder) {
    assert.ok(packaged.has(group), `${group} is a shelf the packaged vocabulary never used`);
  }
});

test("the written order is the shelf order a person reaches for", () => {
  assert.deepEqual(Object.keys(policy.builtins), ORDER);
});

test("the mobile borrow set is the frozen seventeen", () => {
  assert.deepEqual(ids(curations.platforms.mobile.components), MOBILE);
});

test("the wear borrow set is the frozen seven, in wear-m3's own order", () => {
  // The order is not cosmetic: it decides where each one lands in the insert panel.
  assert.deepEqual(ids(curations.platforms.wear.components), WEAR);
});

test("a watch is never offered what its exporter refuses by name", () => {
  // `WearScreenCodeExporter` refuses these with "no Wear Compose Material 3 counterpart this
  // generator can write", and a palette entry that cannot be exported is worse than a missing one.
  const offered = new Set(ids(curations.platforms.wear.components));
  assert.ok(offered.has("layout/box"), "a Wear palette with no box on it");
  for (const refused of ["layout/lazy-grid", "layout/scaffold", "shape/radial-gradient"]) {
    assert.ok(!offered.has(refused), `the wear palette offers ${refused}, which cannot be exported`);
  }
});

test("the two platforms really do borrow different vocabularies", () => {
  // The premise. If they borrowed the same set there would be nothing to curate and a single list
  // would do, and every assertion above would be green while checking nothing.
  assert.notDeepEqual(ids(curations.platforms.mobile.components), ids(curations.platforms.wear.components));
});

test("a curated id is one this catalog declares, or a seam it names", () => {
  const seams = new Set(curations.seams.ids);
  for (const [platform, curation] of Object.entries(curations.platforms)) {
    for (const { componentId, group } of curation.components) {
      assert.ok(
        policy.builtins[componentId] || seams.has(componentId),
        `${platform}: ${componentId} is borrowed but nothing declares it`,
      );
      assert.ok(curation.groupOrder.includes(group), `${platform}: ${componentId} on unordered shelf ${group}`);
    }
  }
});

test("the wear note is carried verbatim, and the seams keep their own", () => {
  // One of the three things #819 says the extraction must carry: a Wear palette rewrites every
  // borrowed FOUNDATION component's note, and injecting the base component would carry the mobile
  // one. Nothing tested notes, so it would have changed silently.
  const wear = curations.platforms.wear;
  assert.match(wear.componentNote, /^Foundation, shared by Compose on both platforms/);
  assert.deepEqual(wear.componentNoteExcept.slice().sort(), curations.seams.ids.slice().sort());
});

test("remote-compose is declined out loud rather than by omission", () => {
  // A consumer must not read the missing curation as "serve that platform an empty palette".
  assert.ok(curations.declined["remote-compose"]);
  assert.equal(curations.platforms["remote-compose"], undefined);
  assert.match(curations.declined["remote-compose"].owner, /wear-m3-catalog/);
});

test("an acceptance role with no structural counterpart fails the build", () => {
  const broken = structuredClone(golden);
  broken.components.find((c) => c.componentId === "layout/box").role = "Somethingelse";
  assert.throws(() => buildBuiltins(broken), /no structural role/);
});

test("the committed policy is what the generator produces", () => {
  // The regenerate-and-diff contract, asserted here as well as by `--check` in CI, so a hand-edited
  // builtin fails the unit tests too rather than only the workflow step.
  assert.equal(readFileSync(POLICY, "utf8"), render(policy, golden));
});

test("the golden records where it was copied from", () => {
  // A frozen golden with no provenance cannot be refreshed by anyone but its author.
  assert.equal(golden.provenance.repository, "yschimke/compose-preview-server");
  assert.match(golden.provenance.commit, /^[0-9a-f]{40}$/);
});
