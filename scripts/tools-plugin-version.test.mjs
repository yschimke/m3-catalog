import assert from "node:assert/strict";
import { readFileSync } from "node:fs";
import test from "node:test";

test("a BOM-only tools release cannot rewrite the Gradle plugin's version ref", () => {
  const catalog = readFileSync(new URL("../gradle/libs.versions.toml", import.meta.url), "utf8");
  const bom = catalog.match(/^composeai-tools-bom\s*=.*version\.ref\s*=\s*"([^"]+)"/m);
  const plugin = catalog.match(/^composePreview\s*=.*version\.ref\s*=\s*"([^"]+)"/m);
  assert.ok(bom && plugin, "both dependencies must be present in the version catalog");
  assert.notEqual(bom[1], plugin[1], "independently published artifacts need independent version refs");
});
