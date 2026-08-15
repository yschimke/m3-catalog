// The kit-coverage report, and the declarations that take rows out of it.
//
// `kit-unauthorable.json` is an exemption list, and an exemption list nobody
// rechecks is how a temporary limitation becomes permanent. So every entry is
// held to both halves of its claim:
//
//   * the kit still publishes that set, axis and value — otherwise the entry
//     is about nothing, and its reason is unreadable to the next person;
//   * the catalog still does not cover it — otherwise the work is done and the
//     exemption is a lie the report is repeating.
//
// The second direction is the one that matters: it means authoring a cell that
// was declared unauthorable FAILS until the declaration is deleted, so the file
// cannot drift into a list of stale excuses.
//
// Runs off committed JSON, so it needs neither a render nor a Figma token.

import assert from "node:assert/strict";
import test from "node:test";

import { auditDeclarations, computeCoverage, declaredValues, loadInputs, parseAxes } from "./kit-coverage.mjs";

const { designMap, kitIndex, declarations } = loadInputs();
const coverage = computeCoverage(designMap, kitIndex);
const audit = auditDeclarations(coverage, declarations, kitIndex);

test("every declared value is still published by the kit", () => {
  assert.deepEqual(
    audit.unpublished.map((row) => `${row.set} / ${row.axis}=${row.value}`),
    [],
    "kit-unauthorable.json names something the kit no longer has — recheck the entry",
  );
});

test("no declared value has since been covered", () => {
  assert.deepEqual(
    audit.nowCovered.map((row) => `${row.set} / ${row.axis}=${row.value}`),
    [],
    "this cell is authored now — delete its kit-unauthorable.json entry",
  );
});

test("every declaration carries a reason and its evidence", () => {
  for (const row of declaredValues(declarations)) {
    const where = `${row.set} / ${row.axis}=${row.value}`;
    assert.ok(row.reason?.length > 20, `${where} needs a reason worth reading`);
    assert.ok(row.evidence?.length > 20, `${where} needs the evidence for that reason`);
  }
});

test("the State axis is left to interaction-coverage.test.mjs", () => {
  const claimed = declaredValues(declarations).filter((row) => row.axis === "State");
  assert.deepEqual(claimed, [], "State-axis exemptions belong in interaction-coverage.test.mjs");
  assert.equal(
    coverage.uncovered.some((row) => row.axis === "State"),
    false,
    "kit-coverage should not report State-axis rows at all",
  );
});

test("axis parsing keeps values that contain their own separators", () => {
  assert.deepEqual(parseAxes("Type=Swipable - standard, Nav items=3"), {
    Type: "Swipable - standard",
    "Nav items": "3",
  });
});
