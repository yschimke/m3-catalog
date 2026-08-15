/**
 * The committed `design-map.json`, checked against the committed
 * `figma-kit-index.json` it was resolved from.
 *
 * These are integrity checks on THIS catalog's two artifacts, not tests of the resolver — that
 * moved to `@design-parity/kit-index`, which pins its own behaviour against a slice of the same
 * kit. What is left here is the pair of invariants nobody upstream can check, because they are
 * about this repo's files agreeing with each other: every node the map points at is one the index
 * knows, and every component's references and previews line up slot for slot.
 *
 * Both run off committed files, so they need neither a render nor a Figma token.
 */
import assert from "node:assert/strict";
import { readFileSync } from "node:fs";
import test from "node:test";

const designMap = JSON.parse(readFileSync("design-map.json", "utf8"));
const kitIndex = JSON.parse(readFileSync("figma-kit-index.json", "utf8"));

test("every mapped node exists in the checked-in kit index", () => {
  const indexed = new Set([
    ...Object.values(kitIndex.sets).flatMap((set) => [
      ...set.variants.flatMap((variant) => [variant.id, variant.renderId].filter(Boolean)),
      ...(set.instances ?? []).map((instance) => instance.id),
    ]),
    ...Object.keys(kitIndex.standalone),
    ...Object.keys(kitIndex.specimens ?? {}),
  ]);
  for (const component of designMap.components) {
    const refs = typeof component.ref === "string" ? [{ ref: component.ref }] : component.ref;
    for (const entry of refs) {
      const nodeId = entry.ref.slice(entry.ref.lastIndexOf("/") + 1);
      assert.ok(indexed.has(nodeId), `${component.code} references unindexed Figma node ${nodeId}`);
    }
  }
});

test("every component pairs unique references and previews slot for slot", () => {
  for (const component of designMap.components) {
    const refs = typeof component.ref === "string" ? [{ ref: component.ref }] : component.ref;
    const previews =
      typeof component.previewId === "string"
        ? [{ previewId: component.previewId }]
        : component.previewId;
    assert.equal(refs.length, previews.length, `${component.code} has an unpaired mapping`);
    assert.equal(
      new Set(refs.map((entry) => entry.ref)).size,
      refs.length,
      `${component.code} maps distinct previews to the same Figma node`,
    );
    assert.equal(
      new Set(previews.map((entry) => entry.previewId)).size,
      previews.length,
      `${component.code} maps the same preview more than once`,
    );
    refs.forEach((entry, index) => {
      const { ref: ignoredRef, ...refSlot } = entry;
      const { previewId: ignoredPreview, ...previewSlot } = previews[index];
      assert.deepEqual(refSlot, previewSlot, `${component.code} has mismatched mapping slots`);
    });
  }
});

/**
 * The sidecar the projection writes and the resolver reads. Committed, so a reader can see which
 * renders declared an axis without running anything, and so a drift between it and the map is a
 * reviewable diff rather than a silent re-resolution.
 */
test("every variant declaration names a component the map carries", () => {
  const variants = JSON.parse(readFileSync("design-map-variants.json", "utf8"));
  assert.equal(variants.schema, "compose-preview-design-map-variants/v1");
  const codes = new Set(designMap.components.map((component) => component.code));
  for (const declaration of variants.components) {
    assert.ok(
      codes.has(declaration.code),
      `${declaration.code} declares variants but has no design-map entry`,
    );
    assert.ok(declaration.renders.length > 0, `${declaration.code} declares an empty render list`);
    for (const render of declaration.renders) {
      assert.ok(
        render.seeds.length > 0,
        `${declaration.code} / ${render.name} names no axis, so it cannot be looked up`,
      );
    }
  }
});
