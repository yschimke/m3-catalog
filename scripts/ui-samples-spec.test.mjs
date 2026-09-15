import { test } from "node:test";
import assert from "node:assert/strict";
import { mkdirSync, mkdtempSync, readFileSync, rmSync, writeFileSync } from "node:fs";
import { tmpdir } from "node:os";
import { join } from "node:path";

import { SPEC, apiName, buildGroups, buildSpec } from "./ui-samples-spec.mjs";

/** A throwaway vendored tree: `<root>/<name>` for each entry, nested paths allowed. */
function vendored(files) {
  const root = mkdtempSync(join(tmpdir(), "ui-samples-spec-test-"));
  for (const [name, text] of Object.entries(files)) {
    const path = join(root, name);
    mkdirSync(join(path, ".."), { recursive: true });
    writeFileSync(path, text);
  }
  return root;
}

const SAMPLE = (fn, annotations = "@Sampled\n@Composable") =>
  `package androidx.compose.foundation.samples\n\n${annotations}\nfun ${fn}() {}\n`;

test("the group is the API the file demonstrates, either spelling of the suffix", () => {
  // Upstream uses both `ColumnSample.kt` and `LazyGridSamples.kt`, often for files of the same
  // kind, so the suffix is stripped rather than matched on.
  assert.equal(apiName("a/b/ColumnSample.kt"), "Column");
  assert.equal(apiName("a/b/LazyGridSamples.kt"), "LazyGrid");
  assert.equal(apiName("a/b/BorderSample.kt"), "Border");
});

test("one group per file, one component per rendered sample", () => {
  const root = vendored({
    "ColumnSample.kt": SAMPLE("ColumnSample") + SAMPLE("ColumnEqualWeightSample"),
    "BorderSample.kt": SAMPLE("BorderSample"),
  });
  try {
    const groups = buildGroups(root, new Map());
    assert.deepEqual(
      groups.map((g) => g.name),
      ["Border", "Column"],
    );
    assert.deepEqual(
      groups[1].components.map((c) => c.componentId),
      ["Column/ColumnEqualWeightSample", "Column/ColumnSample"],
    );
  } finally {
    rmSync(root, { recursive: true, force: true });
  }
});

test("a wrapped sample names its generated wrapper, an annotated one names itself", () => {
  // The two routes to a picture, and the card is the same card either way. Getting this backwards
  // names a preview function that does not exist, which the spec pre-flight catches only if the
  // module has been built.
  const root = vendored({
    "ColumnSample.kt": SAMPLE("ColumnSample"),
    "CanvasSample.kt": SAMPLE("CanvasSample", "@Sampled\n@Preview\n@Composable"),
  });
  try {
    const groups = buildGroups(root, new Map());
    const canvas = groups.find((g) => g.name === "Canvas").components[0];
    const column = groups.find((g) => g.name === "Column").components[0];
    assert.equal(canvas.preview, "CanvasSample");
    assert.equal(column.preview, "ColumnSamplePreview");
  } finally {
    rmSync(root, { recursive: true, force: true });
  }
});

test("a sample with no wrapper and no upstream preview contributes no card", () => {
  // A group holding no component is a heading over nothing, and is not valid against the schema.
  const root = vendored({
    "HelperSample.kt": "package p\n\n@Sampled\n@Composable\nfun HelperSample(text: String) {}\n",
  });
  try {
    assert.deepEqual(buildGroups(root, new Map()), []);
  } finally {
    rmSync(root, { recursive: true, force: true });
  }
});

test("the spec declares no compareWith", () => {
  // The first of issue #346's three reasons for a separate module: a `LazyColumn` sample has no kit
  // cell, so pairing it against `m3-catalog` would put it in a comparison that cannot score it.
  const spec = buildSpec([]);
  assert.equal(spec.compareWith, undefined);
  assert.equal(spec.system, "compose-ui-samples");
  assert.equal(spec.module, ":ui-samples-catalog");
});

test("the committed spec matches what the generator produces", () => {
  // The regenerate-and-diff contract, asserted here as well as by `--check` in CI, so a stale spec
  // fails the unit tests too rather than only the workflow step.
  const committed = readFileSync(SPEC, "utf8");
  assert.equal(committed, `${JSON.stringify(buildSpec(buildGroups()), null, 2)}\n`);
});
