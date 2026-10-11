import { test } from "node:test";
import assert from "node:assert/strict";
import { readFileSync, readdirSync } from "node:fs";
import { join } from "node:path";

/**
 * Every canvas id `ui-builder.policy.json` publishes against the adapters
 * `:catalog-ui-builder-renderer` registers.
 *
 * The renderer maps a component to the policy's `canvas` id and draws it from
 * `foundationCanvasAdapters + materialCanvasAdapters`; an id neither registry holds falls through to
 * `UnsupportedComponent`, an error box on the native canvas. Publishing a builtin and registering its
 * adapter are separate edits in separate files, and nothing in the build connects them, so
 * `m3/variable-font-text` and `m3/snackbar-host` both shipped without one. This holds the two
 * together from the sources: each adapter is registered under its id as a string literal (directly,
 * or through a helper such as `card(...)`), and the one registered through a shared constant
 * (`VariableFontText.M3_ID`) is listed below.
 */

const POLICY = "ui-builder.policy.json";
const SOURCES = "catalog-ui-builder-renderer/src/wasmJsMain/kotlin";

/** Ids registered through a constant rather than a literal, with the constant that names them. */
const REGISTERED_BY_CONSTANT = new Map([["m3/variable-font-text", "VariableFontText.M3_ID"]]);

function kotlinSources(dir) {
  return readdirSync(dir, { withFileTypes: true }).flatMap((entry) => {
    const path = join(dir, entry.name);
    if (entry.isDirectory()) return kotlinSources(path);
    return entry.name.endsWith(".kt") ? [readFileSync(path, "utf8")] : [];
  });
}

const source = kotlinSources(SOURCES).join("\n");
const policy = JSON.parse(readFileSync(POLICY, "utf8"));

const canvasIds = [
  ...new Set(
    ["builtins", "components"].flatMap((section) =>
      Object.values(policy[section] ?? {})
        .map((entry) => entry?.canvas)
        .filter((canvas) => typeof canvas === "string"),
    ),
  ),
].sort();

test("the policy publishes canvas ids at all", () => {
  assert.ok(canvasIds.length > 0, "no canvas ids found; did the policy's shape change?");
});

test("every canvas id the policy publishes has a renderer adapter", () => {
  const missing = canvasIds.filter((id) => {
    const constant = REGISTERED_BY_CONSTANT.get(id);
    return constant ? !source.includes(constant) : !source.includes(`"${id}"`);
  });
  assert.deepEqual(missing, [], "canvas ids with no adapter in catalog-ui-builder-renderer");
});

test("every id registered by constant is still a published canvas id", () => {
  for (const id of REGISTERED_BY_CONSTANT.keys()) {
    assert.ok(canvasIds.includes(id), `${id} is no longer published; drop it from this table`);
  }
});
