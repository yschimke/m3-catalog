import { test } from "node:test";
import assert from "node:assert/strict";
import { mkdirSync, mkdtempSync, rmSync, writeFileSync } from "node:fs";
import { tmpdir } from "node:os";
import { join } from "node:path";

import { classifySamples, quarantinedPreviews, renderPreviews } from "./samples-previews.mjs";

/** A throwaway vendored tree: `<root>/<name>` for each entry, nested paths allowed. */
function vendored(files) {
  const root = mkdtempSync(join(tmpdir(), "samples-previews-test-"));
  for (const [name, text] of Object.entries(files)) {
    const path = join(root, name);
    mkdirSync(join(path, ".."), { recursive: true });
    writeFileSync(path, text);
  }
  return root;
}

const PKG = "androidx.compose.foundation.samples";

const SAMPLE = (fn, annotations = "@Sampled\n@Composable", parameters = "", pkg = PKG) =>
  `package ${pkg}\n\n${annotations}\nfun ${fn}(${parameters}) {}\n`;

const names = (samples) => samples.map((s) => s.fn);

test("wraps a zero-argument @Sampled @Composable", () => {
  const root = vendored({ "ColumnSample.kt": SAMPLE("ColumnSample") });
  try {
    const { wrap, hasPreview, takesArguments, quarantined } = classifySamples(root, new Map());
    assert.deepEqual(names(wrap), ["ColumnSample"]);
    assert.deepEqual([hasPreview, takesArguments, quarantined], [[], [], []]);
  } finally {
    rmSync(root, { recursive: true, force: true });
  }
});

test("leaves a sample that already carries @Preview alone", () => {
  // Wrapping it would publish the same composable twice: discovery finds both the upstream
  // annotation and the wrapper.
  const root = vendored({ "A.kt": SAMPLE("AnnotatedSample", "@Sampled\n@Preview\n@Composable") });
  try {
    const { wrap, hasPreview } = classifySamples(root, new Map());
    assert.deepEqual(wrap, []);
    assert.deepEqual(names(hasPreview), ["AnnotatedSample"]);
  } finally {
    rmSync(root, { recursive: true, force: true });
  }
});

test("refuses a @Sampled function that takes arguments", () => {
  // It is a helper a sample calls, not a call site anything can render on its own — and a wrapper
  // for it would not compile.
  const root = vendored({ "A.kt": SAMPLE("HelperSample", "@Sampled\n@Composable", "text: String") });
  try {
    const { wrap, takesArguments } = classifySamples(root, new Map());
    assert.deepEqual(wrap, []);
    assert.deepEqual(takesArguments, ["HelperSample"]);
  } finally {
    rmSync(root, { recursive: true, force: true });
  }
});

test("ignores a function that is not @Sampled", () => {
  // Upstream's foundation trees carry demo previews beside the samples — five of them at the
  // pinned ref — and `@Sampled` is what makes a function a sample rather than something beside one.
  const root = vendored({ "A.kt": SAMPLE("FancyDemo", "@Preview\n@Composable") });
  try {
    const { wrap, hasPreview } = classifySamples(root, new Map());
    assert.deepEqual([wrap, hasPreview], [[], []]);
  } finally {
    rmSync(root, { recursive: true, force: true });
  }
});

test("skips a sample the quarantine says cannot run", () => {
  const root = vendored({ "A.kt": SAMPLE("ThrowsAtCompositionSample") });
  try {
    const skip = new Map([["ThrowsAtCompositionSample", "needs a real Context"]]);
    const { wrap, quarantined } = classifySamples(root, skip);
    assert.deepEqual(wrap, []);
    assert.deepEqual(quarantined, ["ThrowsAtCompositionSample"]);
  } finally {
    rmSync(root, { recursive: true, force: true });
  }
});

test("reads each sample's package from the file that declares it", () => {
  // The load-bearing difference from the Wear generator this is ported from: one corpus spans
  // several upstream packages (`foundation.samples` and `foundation.layout.samples` today, five
  // when ui, ui-text and animation land), so a single hard-coded package would emit calls that do
  // not resolve.
  const root = vendored({
    "a/ColumnSample.kt": SAMPLE("ColumnSample", undefined, "", "androidx.compose.foundation.layout.samples"),
    "b/CanvasSample.kt": SAMPLE("CanvasSample"),
  });
  try {
    const { wrap } = classifySamples(root, new Map());
    assert.deepEqual(
      wrap.map((s) => `${s.package}.${s.fn}`),
      ["androidx.compose.foundation.samples.CanvasSample", "androidx.compose.foundation.layout.samples.ColumnSample"],
    );
  } finally {
    rmSync(root, { recursive: true, force: true });
  }
});

test("descends into subdirectories", () => {
  // The vendored tree keeps upstream's package directories, so a flat scan would find nothing at
  // all here.
  const root = vendored({ "androidx/compose/foundation/samples/A.kt": SAMPLE("NestedSample") });
  try {
    assert.deepEqual(names(classifySamples(root, new Map()).wrap), ["NestedSample"]);
  } finally {
    rmSync(root, { recursive: true, force: true });
  }
});

test("the generated source calls each sample by its own package", () => {
  const source = renderPreviews([
    { fn: "ColumnSample", package: "androidx.compose.foundation.layout.samples", file: "x.kt" },
  ]);
  assert.match(
    source,
    /@Preview\n@Composable\nfun ColumnSamplePreview\(\) = androidx\.compose\.foundation\.layout\.samples\.ColumnSample\(\)/,
  );
  assert.match(source, /^\/\/ GENERATED by scripts\/samples-previews\.mjs/);
  // In this repository's own package, never upstream's: the vendored tree stays byte-identical.
  assert.match(source, /\npackage ee\.schimke\.uisamplescatalog\n/);
});

test("a missing quarantine file is an empty list rather than a crash", () => {
  assert.deepEqual([...quarantinedPreviews("does/not/exist.json")], []);
});
