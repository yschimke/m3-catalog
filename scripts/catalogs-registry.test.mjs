import { test } from "node:test";
import assert from "node:assert/strict";
import { readFileSync } from "node:fs";
import { execFileSync } from "node:child_process";

/**
 * `.compose-preview/catalogs.json` against what this repository actually publishes.
 *
 * The file is a REGISTRY DOCUMENT: a box nominating this project with `--catalog-registry` serves
 * exactly the entries in it, so an id that does not match a delivery branch is a 404 with nobody's
 * name on it, and a published sheet missing from the file is a sheet that never appears. Both
 * directions are checked here, because the two halves are edited at different times — a new catalog
 * lands as a `catalog.spec.json` plus a publish job, and remembering the registry is a separate act.
 */

const REGISTRY = ".compose-preview/catalogs.json";

const registry = JSON.parse(readFileSync(REGISTRY, "utf8"));

/** The `system` of every catalog spec committed here — the published set, from its own source. */
const published = execFileSync("git", ["ls-files", "*catalog.spec.json"], { encoding: "utf8" })
  .split("\n")
  .filter(Boolean)
  .map((path) => JSON.parse(readFileSync(path, "utf8")).system)
  .sort();

test("the registry names every published system, and only those", () => {
  const declared = registry.catalogs.map((c) => c.system).sort();
  assert.deepEqual(declared, published);
});

test("every group a catalog claims is one this document declares", () => {
  // Group claims resolve against the registry's OWN group table, never the box's — a registry
  // cannot file its catalogs under a heading the operator reserved.
  const groups = new Set(registry.groups.map((g) => g.id));
  for (const catalog of registry.catalogs) {
    if (catalog.group === undefined) continue;
    assert.ok(groups.has(catalog.group), `${catalog.system} claims undeclared group ${catalog.group}`);
  }
});

test("a sheet of vendored upstream work attributes it", () => {
  // The content is AndroidX's; the serving repo is not. Without `attributionRepos` the card claims
  // an AndroidX heading from a repository that is not AndroidX.
  for (const catalog of registry.catalogs) {
    if (catalog.group !== "androidx-samples") continue;
    assert.deepEqual(catalog.attributionRepos, ["androidx/androidx"], `${catalog.system}`);
  }
});

test("compose-ui-samples is served but unlisted", () => {
  // The editorial decision, pinned: 202 cards projected mechanically from upstream's @Sampled
  // corpus are a catalog of call sites, not a front page. `listed: false` keeps the sheet reachable
  // at its URL and off the front door; flipping it is a deliberate act with a reviewer.
  const entry = registry.catalogs.find((c) => c.system === "compose-ui-samples");
  assert.equal(entry.listed, false);
  // And nothing else is hidden by accident: every other sheet takes the default.
  for (const catalog of registry.catalogs) {
    if (catalog.system === "compose-ui-samples") continue;
    assert.equal(catalog.listed, undefined, `${catalog.system} declares a listing it does not need`);
  }
});

test("the document hands out no hostname", () => {
  // `sites` in a registry document are ignored by the box — a hostname is the operator's to give —
  // so declaring one here would be a claim that silently does nothing.
  assert.equal(registry.sites, undefined);
});
