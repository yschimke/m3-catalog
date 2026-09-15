import { test } from "node:test";
import assert from "node:assert/strict";
import { readFileSync } from "node:fs";

import {
  CATALOG,
  CURATIONS,
  GOLDEN,
  OWNED_NAMESPACES,
  SEAM_NAMESPACE,
  buildCatalog,
  resolvePlatform,
} from "./foundation-catalog.mjs";

const golden = JSON.parse(readFileSync(GOLDEN, "utf8"));
const curations = JSON.parse(readFileSync(CURATIONS, "utf8"));
const catalog = JSON.parse(readFileSync(CATALOG, "utf8"));

const ids = (components) => components.map((c) => c.componentId);

/**
 * The frozen sets, written out here rather than read from the files under test.
 *
 * This is the one place in this suite that is allowed to restate them: a test that read the sets
 * from the same file it checks would pass on any edit, which is exactly the failure #819 warns
 * about ("a hand-reconstruction of the Wear set missed `remote-compose/custom` and
 * `remote-compose/inline`"). These two lists are the issue's own table, and changing one is meant
 * to be a deliberate act with a reviewer.
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

test("the mobile palette is the frozen seventeen, in the packaged order", () => {
  assert.deepEqual(ids(catalog.platforms.mobile.components), MOBILE);
});

test("the wear palette is the frozen seven, in wear-m3's own order", () => {
  // The order is not cosmetic: it decides where each one lands in the insert panel.
  assert.deepEqual(ids(catalog.platforms.wear.components), WEAR);
});

test("a watch is never offered what its exporter refuses by name", () => {
  // `WearScreenCodeExporter` refuses these with "no Wear Compose Material 3 counterpart this
  // generator can write", and a palette entry that cannot be exported is worse than a missing one.
  const offered = new Set(ids(catalog.platforms.wear.components));
  assert.ok(offered.has("layout/box"), "a Wear palette with no box on it");
  for (const refused of ["layout/lazy-grid", "layout/scaffold", "shape/radial-gradient"]) {
    assert.ok(!offered.has(refused), `the wear palette offers ${refused}, which cannot be exported`);
  }
});

test("the two platforms really do borrow different vocabularies", () => {
  // The premise. If they borrowed the same set there would be nothing to curate, a single list
  // would do, and every assertion above would be green while checking nothing.
  assert.notDeepEqual(ids(catalog.platforms.mobile.components), ids(catalog.platforms.wear.components));
});

test("every borrowed component on a wear palette carries the wear note", () => {
  const note = curations.platforms.wear.componentNote;
  const seams = new Set(curations.platforms.wear.componentNoteExcept);
  for (const component of catalog.platforms.wear.components) {
    if (seams.has(component.componentId)) continue;
    assert.equal(
      component.wasm.notes,
      note,
      `${component.componentId} would carry the mobile note onto a watch`,
    );
  }
});

test("a seam keeps its own note rather than being told it is foundation", () => {
  const declared = new Map(golden.components.map((c) => [c.componentId, c]));
  for (const id of curations.platforms.wear.componentNoteExcept) {
    const borrowed = catalog.platforms.wear.components.find((c) => c.componentId === id);
    assert.equal(borrowed.wasm.notes, declared.get(id).wasm.notes);
  }
});

test("the note is the only thing a curation changes about a borrowed component", () => {
  // #819's third question — whether any borrowed component is also slot-narrowed — answered as a
  // test rather than as a comment. Slots, properties, traits and modifier capabilities are the
  // golden's, field for field, on both platforms.
  const declared = new Map(golden.components.map((c) => [c.componentId, c]));
  for (const [platform, palette] of Object.entries(catalog.platforms)) {
    for (const borrowed of palette.components) {
      const base = declared.get(borrowed.componentId);
      for (const field of ["slots", "properties", "traits", "modifierCapabilities", "role", "code"]) {
        assert.deepEqual(
          borrowed[field],
          base[field],
          `${platform}: ${borrowed.componentId} was narrowed on ${field}`,
        );
      }
    }
  }
});

test("every borrowed component names a shelf the platform's order lists", () => {
  for (const [platform, palette] of Object.entries(catalog.platforms)) {
    for (const component of palette.components) {
      assert.ok(
        palette.groupOrder.includes(component.group),
        `${platform}: ${component.componentId} is filed under ${component.group}, which sorts after every named shelf`,
      );
    }
  }
});

test("the catalog owns the foundation namespaces and only references the seams", () => {
  for (const component of catalog.owns) {
    assert.ok(
      OWNED_NAMESPACES.some((ns) => component.componentId.startsWith(ns)),
      `${component.componentId} is owned but is not a foundation namespace`,
    );
  }
  assert.deepEqual(
    ids(catalog.seams.components).sort(),
    curations.seams.ids.slice().sort(),
  );
  for (const component of catalog.seams.components) {
    assert.ok(component.componentId.startsWith(SEAM_NAMESPACE));
  }
  assert.equal(catalog.owns.length + catalog.seams.components.length, golden.components.length);
});

test("remote-compose is declined out loud rather than by omission", () => {
  // A consumer must not read the missing curation as "serve that platform an empty palette".
  assert.ok(catalog.declined["remote-compose"]);
  assert.equal(catalog.platforms["remote-compose"], undefined);
  assert.match(catalog.declined["remote-compose"].owner, /wear-m3-catalog/);
});

test("a curated id the golden does not declare fails the build rather than vanishing", () => {
  assert.throws(
    () =>
      resolvePlatform(golden, { components: [{ componentId: "layout/nope", group: "Layout" }] }, "mobile"),
    /layout\/nope/,
  );
});

test("a shelf missing from groupOrder fails the build", () => {
  const broken = structuredClone(curations);
  broken.platforms.wear.groupOrder = ["Layout"];
  assert.throws(() => buildCatalog(golden, broken), /groupOrder/);
});

test("the donor carries the asset registry a donated asset/image validates against", () => {
  // Without it `asset/image` arrives with a null registry, which accepts any key — the refusal then
  // lands at commit instead of at insert.
  assert.deepEqual(catalog.assetRegistry, golden.assetRegistry);
  assert.ok(catalog.assetRegistry.keys.length > 0);
});

test("the golden records where it was copied from", () => {
  // A frozen golden with no provenance cannot be refreshed by anyone but its author.
  assert.equal(golden.provenance.repository, "yschimke/compose-preview-server");
  assert.match(golden.provenance.commit, /^[0-9a-f]{40}$/);
  assert.equal(catalog.provenance.commit, golden.provenance.commit);
});
