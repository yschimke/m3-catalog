// The duplicate-render scan, and the declarations that take collisions out of it.
//
// The scan itself needs a render, which CI runs in compose-preview.yml. These
// tests do not: they drive the pure functions with hand-built envelopes, and
// they hold `duplicate-renders.json` to the shape the scan reads and to the
// standard `kit-unauthorable.json` is held to — every entry names an issue and
// says something worth reading, because an exemption whose reason is "known
// issue" is one nobody can ever decide to delete.
//
// The one thing these tests deliberately cannot check is whether the declared
// collisions are still real; only the render knows that, and the scan fails on
// a stale entry when it runs.

import assert from "node:assert/strict";
import { readFileSync, readdirSync } from "node:fs";
import test from "node:test";

import {
  BASE_RENDER,
  audit,
  declaredSets,
  findCollisions,
  previewRows,
  secondaryPreviewIds,
  setKey,
} from "./duplicate-renders.mjs";

const declarations = JSON.parse(readFileSync("duplicate-renders.json", "utf8"));

/** A `compose-preview show --json` envelope with one entry per `[id, sha]` pair. */
const envelope = (...rows) => ({
  schema: "compose-preview-show/v1",
  previews: rows.map(([id, sha256]) => ({
    id,
    sha256,
    module: ":catalog",
    functionName: id.split(".").pop().split("_")[0],
  })),
});

const FAMILY = "ee.schimke.m3catalog.sections.ListsKt.ListItemSticker_Light";

test("a variant that renders its default's picture is a collision", () => {
  const collisions = findCollisions(
    previewRows(
      envelope([FAMILY, "aaa"], [`${FAMILY}_VARIANT_no-leading`, "aaa"], [`${FAMILY}_VARIANT_one-line`, "bbb"]),
    ),
  );
  assert.equal(collisions.length, 1);
  assert.deepEqual(collisions[0].renders, [BASE_RENDER, "no-leading"]);
  assert.equal(collisions[0].functionName, "ListItemSticker");
});

test("renders that differ are not a collision", () => {
  const rows = previewRows(envelope([FAMILY, "aaa"], [`${FAMILY}_VARIANT_one-line`, "bbb"]));
  assert.deepEqual(findCollisions(rows), []);
});

test("secondary exhaustive cells are outside the primary variant-tree audit", () => {
  const secondary = `${FAMILY}_VARIANT_cell-lines-two-state-enabled-1-2`;
  const input = envelope([FAMILY, "aaa"], [secondary, "aaa"]);
  const manifest = { previews: [{ id: secondary, overrides: { secondary: true } }] };
  assert.deepEqual(findCollisions(previewRows(input, secondaryPreviewIds(manifest))), []);
});

test("render-envelope secondary metadata remains a supported fallback", () => {
  const secondary = `${FAMILY}_VARIANT_cell-lines-two-state-enabled-1-2`;
  const input = envelope([FAMILY, "aaa"], [secondary, "aaa"]);
  input.previews[1].overrides = { secondary: true };
  assert.deepEqual(findCollisions(previewRows(input)), []);
});

test("a light render is never compared against its dark twin", () => {
  // The identical sha here is impossible in practice; the point is that the
  // family key keeps the two modes apart, so a dark-mode catalog cannot be
  // reported as one giant collision.
  const dark = FAMILY.replace("_Light", "_Dark");
  assert.deepEqual(findCollisions(previewRows(envelope([FAMILY, "aaa"], [dark, "aaa"]))), []);
});

test("two stickers that happen to render the same are left alone", () => {
  // Cross-component duplication is a taxonomy question (CatalogInventoryTest
  // owns it), not a variant that failed to vary. Different family, no report.
  const other = "ee.schimke.m3catalog.sections.ListsKt.OtherSticker_Light";
  assert.deepEqual(findCollisions(previewRows(envelope([FAMILY, "aaa"], [other, "aaa"]))), []);
});

test("a capture that rendered nothing is skipped rather than matched", () => {
  // Two empty shas are not "the same picture" — they are two absent ones.
  assert.deepEqual(
    findCollisions(previewRows(envelope([FAMILY, ""], [`${FAMILY}_VARIANT_no-leading`, ""]))),
    [],
  );
});

test("a multi-capture preview compares each capture against its own index", () => {
  const rows = previewRows({
    previews: [
      {
        id: FAMILY,
        module: ":catalog",
        functionName: "ListItemSticker",
        captures: [{ sha256: "aaa" }, { sha256: "bbb" }],
      },
      {
        id: `${FAMILY}_VARIANT_no-leading`,
        module: ":catalog",
        functionName: "ListItemSticker",
        captures: [{ sha256: "aaa" }, { sha256: "ccc" }],
      },
    ],
  });
  const collisions = findCollisions(rows);
  assert.equal(collisions.length, 1, "only the first capture coincides");
  assert.equal(collisions[0].family.endsWith("#0"), true);
});

test("a declared collision is not reported, and counts as live", () => {
  const collisions = findCollisions(
    previewRows(envelope([FAMILY, "aaa"], [`${FAMILY}_VARIANT_no-leading`, "aaa"])),
  );
  const declared = {
    declarations: [
      { function: "ListItemSticker", issue: 1, reason: "x", collisions: [["default", "no-leading"]] },
    ],
  };
  assert.deepEqual(audit(collisions, declared), { unexplained: [], stale: [] });
});

test("a declaration for a collision that no longer happens is stale", () => {
  const declared = {
    declarations: [
      { function: "ListItemSticker", issue: 1, reason: "x", collisions: [["default", "no-leading"]] },
    ],
  };
  const { stale } = audit([], declared);
  assert.deepEqual(
    stale.map((row) => row.key),
    ["ListItemSticker default = no-leading"],
  );
});

test("a declaration for another sticker does not cover this one", () => {
  const collisions = findCollisions(
    previewRows(envelope([FAMILY, "aaa"], [`${FAMILY}_VARIANT_no-leading`, "aaa"])),
  );
  const declared = {
    declarations: [
      { function: "SomethingElse", issue: 1, reason: "x", collisions: [["default", "no-leading"]] },
    ],
  };
  const { unexplained } = audit(collisions, declared);
  assert.equal(unexplained.length, 1);
});

test("the declarations file is the shape the scan reads", () => {
  assert.equal(declarations.schema, "m3-catalog-duplicate-renders/v1");
  const rows = declaredSets(declarations);
  assert.ok(rows.length > 0, "an empty file should be deleted, not kept as a stub");
  for (const row of rows) {
    const where = `${row.function} ${setKey(row.renders)}`;
    assert.ok(row.renders.length >= 2, `${where} declares fewer than two coinciding renders`);
    assert.equal(new Set(row.renders).size, row.renders.length, `${where} repeats a render name`);
    assert.ok(Number.isInteger(row.issue) && row.issue > 0, `${where} must name the issue tracking it`);
    assert.ok(row.reason?.length > 40, `${where} needs a reason worth reading`);
  }
});

test("no two declared sets could claim the same collision", () => {
  // `audit` credits a collision to the first set that covers it, so an entry
  // nested inside another would report as stale forever.
  const rows = declaredSets(declarations);
  const overlapping = rows.filter((row) =>
    rows.some(
      (other) =>
        other !== row &&
        other.function === row.function &&
        row.renders.every((render) => other.renders.includes(render)),
    ),
  );
  assert.deepEqual(
    overlapping.map((row) => `${row.function} ${setKey(row.renders)}`),
    [],
    "one of these sets contains the other — merge them",
  );
});

/** Every `.kt` under the catalog module, concatenated — the source scan's haystack. */
function catalogSources() {
  const dir = "catalog/src/main/kotlin/ee/schimke/m3catalog";
  const files = readdirSync(dir, { recursive: true }).filter((name) => String(name).endsWith(".kt"));
  return files.map((name) => readFileSync(`${dir}/${name}`, "utf8")).join("\n");
}

test("every declared render name is a cell that exists", () => {
  // A renamed `@OverrideVariant` would otherwise turn its entry stale silently
  // — the collision is still there, the name that described it is not, and the
  // scan reports the pair as undeclared with an entry sitting right above it
  // that looks like it covers them.
  const sources = catalogSources();
  const cells = new Set(
    [...sources.matchAll(/@OverrideVariant\(\s*name = "([^"]+)"/g)].map((match) => match[1]),
  );
  const unknown = declaredSets(declarations).flatMap((row) =>
    row.renders
      .filter((render) => render !== BASE_RENDER && !cells.has(render))
      .map((render) => `${row.function} / ${render}`),
  );
  assert.deepEqual(unknown, [], "these declarations name a variant cell nothing declares");
});

test("every declared function is a sticker in the catalog", () => {
  // The scan matches declarations on `functionName`, so an entry naming a
  // function that has been renamed away would report as stale only after a
  // 9-minute render. The source tree answers it here, for free.
  const sources = catalogSources();
  const missing = [...new Set(declaredSets(declarations).map((row) => row.function))].filter(
    (name) => !new RegExp(`^fun ${name}\\(`, "m").test(sources),
  );
  assert.deepEqual(missing, [], "these declarations name a composable that no longer exists");
});
