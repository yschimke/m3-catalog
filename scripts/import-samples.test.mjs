import { test } from "node:test";
import assert from "node:assert/strict";
import { mkdtempSync, mkdirSync, readFileSync, rmSync, writeFileSync } from "node:fs";
import { tmpdir } from "node:os";
import { join } from "node:path";

import { execFileSync } from "node:child_process";

import {
  applyPatches,
  fetchUpstream,
  quarantined,
  vendor,
  writeProvenance,
} from "./import-samples.mjs";

/** A throwaway upstream checkout: `<root>/<path>/*.kt`. */
function fakeUpstream(files) {
  const root = mkdtempSync(join(tmpdir(), "import-samples-test-"));
  const path = "compose/material3/material3/samples/src/main/java/androidx/compose/material3/samples";
  mkdirSync(join(root, path), { recursive: true });
  for (const [name, text] of Object.entries(files)) {
    writeFileSync(join(root, path, name), text);
  }
  const library = {
    name: "material3",
    paths: [path],
    ref: "abc123",
    artifactCoordinates: "g:a",
    artifactVersion: "1",
  };
  return { root, library, manifest: { repo: "r", libraries: [library] } };
}

test("vendors every .kt file under the manifest's paths", () => {
  const { root, library } = fakeUpstream({
    "ButtonSamples.kt": "fun a() {}",
    "CardSamples.kt": "fun b() {}",
    "README.md": "not kotlin",
  });
  const out = mkdtempSync(join(tmpdir(), "out-"));
  try {
    const result = vendor(root, library, out);
    assert.deepEqual(result.copied, ["ButtonSamples.kt", "CardSamples.kt"]);
    assert.deepEqual(result.skipped, []);
    // Byte-identical: the vendored tree is upstream's bytes, never reformatted.
    assert.equal(readFileSync(join(out, "ButtonSamples.kt"), "utf8"), "fun a() {}");
  } finally {
    rmSync(root, { recursive: true, force: true });
    rmSync(out, { recursive: true, force: true });
  }
});

test("skips a quarantined file and reports it", () => {
  const { root, library } = fakeUpstream({
    "ButtonSamples.kt": "fun a() {}",
    "NeedsActivitySamples.kt": "fun b() {}",
  });
  const out = mkdtempSync(join(tmpdir(), "out-"));
  try {
    const skip = new Map([["NeedsActivitySamples.kt", "needs a real Activity"]]);
    const result = vendor(root, library, out, skip);
    assert.deepEqual(result.copied, ["ButtonSamples.kt"]);
    assert.deepEqual(result.skipped, ["NeedsActivitySamples.kt"]);
  } finally {
    rmSync(root, { recursive: true, force: true });
    rmSync(out, { recursive: true, force: true });
  }
});

test("vendoring is idempotent — a second run reproduces the tree exactly", () => {
  const { root, library } = fakeUpstream({ "A.kt": "fun a() {}" });
  const out = mkdtempSync(join(tmpdir(), "out-"));
  try {
    vendor(root, library, out);
    const first = readFileSync(join(out, "A.kt"), "utf8");
    vendor(root, library, out);
    assert.equal(readFileSync(join(out, "A.kt"), "utf8"), first);
  } finally {
    rmSync(root, { recursive: true, force: true });
    rmSync(out, { recursive: true, force: true });
  }
});

test("vendoring clears a file that upstream no longer has", () => {
  // The destination is rebuilt rather than merged into, so a sample deleted upstream disappears
  // here too instead of lingering as a render nothing can trace back to a commit.
  const { root, library } = fakeUpstream({ "A.kt": "fun a() {}" });
  const out = mkdtempSync(join(tmpdir(), "out-"));
  try {
    mkdirSync(out, { recursive: true });
    writeFileSync(join(out, "Stale.kt"), "fun stale() {}");
    const result = vendor(root, library, out);
    assert.deepEqual(result.copied, ["A.kt"]);
    assert.throws(() => readFileSync(join(out, "Stale.kt"), "utf8"));
  } finally {
    rmSync(root, { recursive: true, force: true });
    rmSync(out, { recursive: true, force: true });
  }
});

test("quarantined() maps file names to their stated reasons", () => {
  const dir = mkdtempSync(join(tmpdir(), "q-"));
  const path = join(dir, "quarantine.json");
  writeFileSync(
    path,
    JSON.stringify({ samples: [{ file: "A.kt", reason: "needs a Context" }, { file: "B.kt" }] }),
  );
  try {
    const map = quarantined(path);
    assert.equal(map.get("A.kt"), "needs a Context");
    // A missing reason is recorded rather than dropped: the entry still excludes the file, and the
    // absence of a reason is itself worth seeing in the import's output.
    assert.equal(map.get("B.kt"), "(no reason given)");
  } finally {
    rmSync(dir, { recursive: true, force: true });
  }
});

test("quarantined() treats an absent list as an empty one", () => {
  assert.equal(quarantined(join(tmpdir(), "definitely-not-here.json")).size, 0);
});

test("applyPatches is a no-op when there are no patches", () => {
  const dir = mkdtempSync(join(tmpdir(), "p-"));
  try {
    assert.deepEqual(applyPatches(dir, join(dir, "missing")), []);
  } finally {
    rmSync(dir, { recursive: true, force: true });
  }
});

test("a patch that does not apply throws, naming the patch and what to do", () => {
  // The load-bearing behaviour: upstream moving under a fix must FAIL, never silently drop the fix.
  const { root, library } = fakeUpstream({ "A.kt": "fun a() {}" });
  const out = mkdtempSync(join(tmpdir(), "out-"));
  const patches = mkdtempSync(join(tmpdir(), "patches-"));
  writeFileSync(
    join(patches, "0001-bogus.patch"),
    "diff --git a/Nope.kt b/Nope.kt\n--- a/Nope.kt\n+++ b/Nope.kt\n@@ -1,1 +1,1 @@\n-nope\n+yes\n",
  );
  try {
    vendor(root, library, out);
    assert.throws(
      () => applyPatches(out, patches),
      (error) => error.message.includes("0001-bogus.patch") && error.message.includes("silently absent"),
    );
  } finally {
    for (const d of [root, out, patches]) rmSync(d, { recursive: true, force: true });
  }
});

test("provenance records each library's own ref and artifact", () => {
  // One entry PER LIBRARY, because the libraries are pinned independently: tracing a render back to
  // a commit needs the commit that particular sample came from, and the import stopped having a
  // single ref when the manifest grew a second library.
  const { root, library, manifest } = fakeUpstream({ "A.kt": "fun a() {}" });
  const out = mkdtempSync(join(tmpdir(), "out-"));
  try {
    const result = vendor(root, library, out, new Map([["B.kt", "why"]]));
    writeProvenance(
      out,
      manifest,
      { ...result, skipped: ["B.kt"], byLibrary: { material3: result.copied } },
      ["0001-fix.patch"],
    );
    const provenance = JSON.parse(readFileSync(join(out, "PROVENANCE.json"), "utf8"));
    assert.deepEqual(provenance.libraries, [
      { name: "material3", ref: "abc123", paths: library.paths, artifact: "g:a:1", files: 1 },
    ]);
    assert.equal(provenance.files, 1);
    assert.deepEqual(provenance.quarantined, ["B.kt"]);
    assert.deepEqual(provenance.patches, ["0001-fix.patch"]);
  } finally {
    rmSync(root, { recursive: true, force: true });
    rmSync(out, { recursive: true, force: true });
  }
});

test("a file name vendored by two libraries fails the import rather than picking a winner", () => {
  // The vendored tree is FLAT and `samples/quarantine.json` keys on the bare file name, so a
  // collision would silently drop one library's sample and quietly widen a quarantine entry.
  const { root, library } = fakeUpstream({ "A.kt": "fun a() {}" });
  const out = mkdtempSync(join(tmpdir(), "out-"));
  try {
    vendor(root, library, out);
    assert.throws(
      () => vendor(root, library, out, new Map(), false),
      (error) => error.message.includes("more than one library"),
    );
  } finally {
    rmSync(root, { recursive: true, force: true });
    rmSync(out, { recursive: true, force: true });
  }
});

test("two libraries sharing a ref each get their own subtree checked out", () => {
  // The cache is keyed by REF, so libraries pinned to the same commit share one checkout. A sparse
  // set applied only after a fresh clone leaves the second library's subtree absent from the
  // working tree, and `vendor` then walks a directory that is not there (ENOENT) — a silent
  // dependency on the two pins differing, which they are free to stop doing after any bump.
  const upstream = mkdtempSync(join(tmpdir(), "upstream-"));
  const cache = join(mkdtempSync(join(tmpdir(), "cache-")), "checkout");
  const git = (args, cwd) => execFileSync("git", args, { cwd, stdio: "pipe" });
  try {
    const a = "compose/material3/material3/samples/src/main/java/androidx/compose/material3/samples";
    const b =
      "compose/material3/adaptive/samples/src/main/java/androidx/compose/material3/adaptive/samples";
    git(["init", "--quiet", "-b", "main", upstream]);
    git(["config", "user.email", "t@example.com"], upstream);
    git(["config", "user.name", "t"], upstream);
    for (const [dir, file] of [
      [a, "ButtonSamples.kt"],
      [b, "ThreePaneScaffoldSample.kt"],
    ]) {
      mkdirSync(join(upstream, dir), { recursive: true });
      writeFileSync(join(upstream, dir, file), "fun x() {}");
    }
    git(["add", "-A"], upstream);
    git(["commit", "--quiet", "-m", "seed"], upstream);
    const ref = execFileSync("git", ["rev-parse", "HEAD"], { cwd: upstream, encoding: "utf8" }).trim();

    fetchUpstream(upstream, { name: "material3", ref, paths: [a] }, cache);
    fetchUpstream(upstream, { name: "material3-adaptive", ref, paths: [b] }, cache);

    // The second library's subtree is what the reused cache used to be missing.
    assert.equal(readFileSync(join(cache, b, "ThreePaneScaffoldSample.kt"), "utf8"), "fun x() {}");
  } finally {
    rmSync(upstream, { recursive: true, force: true });
    rmSync(cache, { recursive: true, force: true });
  }
});
