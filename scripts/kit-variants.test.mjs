import assert from "node:assert/strict";
import { readFileSync } from "node:fs";
import test from "node:test";

import {
  renderableRef,
  resolvePropertyInstance,
  resolveVariantRef,
} from "./kit-variants.mjs";

const FILE = "ocdacdEsnHipMJD3egzxKb";
const ref = (nodeId) => `figma:${FILE}/${nodeId}`;
const designMap = JSON.parse(readFileSync("design-map.json", "utf8"));
const kitIndex = JSON.parse(readFileSync("figma-kit-index.json", "utf8"));

test("resolves an exact multi-axis button cell", () => {
  assert.deepEqual(
    resolveVariantRef(ref("57994:2324"), [
      { key: "size", raw: "l" },
      { key: "shape", raw: "square" },
    ]),
    { nodeId: "57994:2310", name: "Type=Square, Size=Large, State=Enabled" },
  );
});

test("accepts an explicit default size inside a multi-axis matrix cell", () => {
  assert.deepEqual(
    resolveVariantRef(ref("57994:10132"), [
      { key: "size", raw: "s" },
      { key: "width", raw: "narrow" },
      { key: "shape", raw: "square" },
    ]),
    {
      nodeId: "57994:10104",
      name: "Type=Square, Size=Small, Width=Narrow, State=Enabled",
    },
  );
});

test("prefers a real variant axis over a similarly named component property", () => {
  assert.deepEqual(resolveVariantRef(ref("57994:2485"), { key: "selected", raw: "true" }), {
    nodeId: "57994:2475",
    name: "Type=Round, Size=Small, State=Enabled, Selected=True",
  });
});

test("does not map two different content configurations to label and icon", () => {
  assert.deepEqual(resolveVariantRef(ref("54563:40116"), { key: "content", raw: "icon" }), {
    nodeId: "54563:40070",
    name: "Configuration=Fixed, Style=Primary, Layout=Icon only",
  });
  assert.equal(
    resolveVariantRef(ref("54563:40047"), { key: "content", raw: "icon" }),
    undefined,
  );
});

test("does not compose mutually exclusive standalone siblings", () => {
  assert.equal(
    resolveVariantRef(ref("51816:5860"), [
      { key: "subhead", raw: "true" },
      { key: "inset", raw: "16" },
    ]),
    undefined,
  );
});

test("does not duplicate a base reference for a redundant single-axis seed", () => {
  assert.equal(
    resolveVariantRef(ref("57998:43398"), { key: "size", raw: "small" }),
    undefined,
  );
});

test("uses visible examples for hidden component-set definitions", () => {
  assert.equal(renderableRef(ref("53977:33611")), ref("53977:34289"));
  assert.deepEqual(
    resolveVariantRef(ref("53977:33611"), { key: "configuration", raw: "text+action" }),
    {
      nodeId: "53977:34287",
      name: "Configuration=Text & action, # of lines=One line, Show close affordance=False",
    },
  );
});

test("resolves false boolean and text properties to exact configured instances", () => {
  const set = {
    properties: {
      "Show icon": { type: "BOOLEAN", default: true },
      Label: { type: "TEXT", default: "Label" },
    },
    instances: [
      {
        id: "instance:label-only",
        componentId: "component:enabled",
        properties: { "Show icon": false, Label: "Save" },
      },
    ],
  };
  assert.deepEqual(
    resolvePropertyInstance(set, "component:enabled", [
      { key: "icon", raw: "false" },
      { key: "label", raw: "Save" },
    ]),
    {
      nodeId: "instance:label-only",
      properties: { "Show icon": false, Label: "Save" },
    },
  );
});

test("resolves a count across ordinal boolean properties as one exact vector", () => {
  const set = {
    properties: {
      "Show 1st trailing action": { type: "BOOLEAN", default: true },
      "Show 2nd trailing action": { type: "BOOLEAN", default: false },
      "Show 3rd trailing action": { type: "BOOLEAN", default: false },
    },
    instances: [
      {
        id: "instance:two-actions",
        componentId: "component:small",
        properties: {
          "Show 1st trailing action": true,
          "Show 2nd trailing action": true,
          "Show 3rd trailing action": false,
        },
      },
    ],
  };
  assert.equal(
    resolvePropertyInstance(set, "component:small", { key: "actions", raw: "2" })?.nodeId,
    "instance:two-actions",
  );
});

test("does not guess an instance-swap property from a semantic seed", () => {
  const set = {
    properties: { "Leading icon": { type: "INSTANCE_SWAP", default: "icon:star" } },
    instances: [
      {
        id: "instance:icon",
        componentId: "component:enabled",
        properties: { "Leading icon": "icon:add" },
      },
    ],
  };
  assert.equal(
    resolvePropertyInstance(set, "component:enabled", { key: "leading", raw: "icon" }),
    undefined,
  );
});

test("every mapped node exists in the checked-in kit index", () => {
  const indexed = new Set([
    ...Object.values(kitIndex.sets).flatMap((set) =>
      [
        ...set.variants.flatMap((variant) => [variant.id, variant.renderId].filter(Boolean)),
        ...(set.instances ?? []).map((instance) => instance.id),
      ],
    ),
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
