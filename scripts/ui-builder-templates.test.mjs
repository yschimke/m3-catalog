import { test } from "node:test";
import assert from "node:assert/strict";
import { readFileSync } from "node:fs";
import { execFileSync } from "node:child_process";

const root = new URL("../", import.meta.url);
const read = (path) => readFileSync(new URL(path, root), "utf8");
const policy = JSON.parse(read("ui-builder.policy.json"));

test("chooser templates are complete trees of m3-catalog nodes", () => {
  assert.ok(policy.templates.length > 0);
  const ids = new Set();
  for (const path of policy.templates) {
    assert.match(path, /^ui-builder\/designs\/[\w-]+\.json$/);
    const doc = JSON.parse(read(path));
    assert.equal(doc.schema, "compose-ui-builder-document/v1-candidate");
    assert.equal(doc.catalogPin.systemId, "m3-catalog");
    assert.equal(doc.revision, 0);
    assert.equal(doc.roots.length, 1);
    assert.ok(!ids.has(doc.id), `duplicate template ${doc.id}`);
    ids.add(doc.id);
    const visited = new Set();
    function visit(id) {
      assert.ok(!visited.has(id), `${path}: cycle or repeated parent for ${id}`);
      visited.add(id);
      const node = doc.nodes[id];
      assert.ok(node, `${path}: missing node ${id}`);
      assert.equal(node.id, id);
      assert.match(node.componentId, /^(m3|layout)\//);
      for (const children of Object.values(node.slots)) children.forEach(visit);
    }
    doc.roots.forEach(visit);
    assert.equal(visited.size, Object.keys(doc.nodes).length, `${path}: unreachable nodes`);
    assert.ok(doc.environment.exportDevices.length >= 3, `${path}: missing size coverage`);
  }
});

test("template edits republish m3-catalog, including workflow trigger", () => {
  const workflow = read(".github/workflows/design-artifacts.yml");
  assert.match(workflow, /- 'ui-builder\/\*\*'/);
  const output = execFileSync(new URL("scripts/scope-systems.sh", root).pathname, [], {
    input: "ui-builder/designs/adaptive-feed.json\n",
    encoding: "utf8",
  });
  assert.match(output, /^m3-catalog=true$/m);
  for (const system of ["m3-samples", "compose-ui-samples", "compose-foundation"]) {
    assert.match(output, new RegExp(`^${system}=false$`, "m"));
  }
});
