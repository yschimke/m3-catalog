import { test } from "node:test";
import assert from "node:assert/strict";
import { mkdtemp, mkdir, writeFile } from "node:fs/promises";
import { tmpdir } from "node:os";
import { join } from "node:path";

import {
  PILOT_ID,
  checkPair,
  checkRuntime,
  pilotTemplate,
  treeIntegrity,
} from "./assemble-typed-pilot-artifacts.mjs";

/** A pair shaped like the generated one: the pilot id, the projected export and its records. */
function pair({ id = PILOT_ID, adapter = "compose-component-records", record = "r/Text" } = {}) {
  return {
    policy: {
      catalog: { id },
      statusSemantics: {
        composeSourceExport: { adapter, version: 1 },
        templates: ["ui-builder/designs/hello.json"],
        components: {
          "m3/text": { record, exportProjection: { targets: [] } },
          "m3/export-text": { record, excluded: "an export target of m3/text" },
        },
      },
    },
    record: { components: [{ canonicalId: "r/Text" }] },
  };
}

test("the pilot pair passes and names its projections and templates", () => {
  const { policy, record } = pair();
  assert.deepEqual(checkPair(policy, record), {
    projected: ["m3/text"],
    templates: ["ui-builder/designs/hello.json"],
  });
});

test("production's pair, or one not selecting the projected export, is refused", () => {
  const production = pair({ id: "m3-catalog" });
  assert.throws(() => checkPair(production.policy, production.record), /not m3-catalog-typed-pilot/);
  const material3 = pair({ adapter: "compose-material3" });
  assert.throws(() => checkPair(material3.policy, material3.record), /compose-component-records/);
  const dangling = pair({ record: "r/Missing" });
  assert.throws(() => checkPair(dangling.policy, dangling.record), /components.json lacks/);
});

test("a template is re-pinned to the pilot and keeps everything else", () => {
  const template = JSON.parse(
    pilotTemplate(
      JSON.stringify({ id: "hello", catalogPin: { systemId: "m3-catalog", catalogRevision: "x" } }),
    ),
  );
  assert.deepEqual(template, {
    id: "hello",
    catalogPin: { systemId: PILOT_ID, catalogRevision: "x" },
  });
});

test("a runtime is accepted only when its bytes match its manifest", async () => {
  const directory = await mkdtemp(join(tmpdir(), "typed-pilot-runtime-"));
  await mkdir(join(directory, "fonts"));
  const files = new Map([
    ["index.html", Buffer.from("<html></html>")],
    ["fonts/a.ttf", Buffer.from([0, 1, 2])],
  ]);
  for (const [path, bytes] of files) await writeFile(join(directory, path), bytes);
  const manifest = {
    schema: "compose-ui-builder-runtime/v1",
    runtimeId: "m3-typed-pilot-0123456789ab",
    protocolVersion: 2,
    entrypoint: "index.html",
    integritySha256: treeIntegrity(files),
  };
  await writeFile(join(directory, "runtime-manifest.json"), JSON.stringify(manifest));
  assert.deepEqual(await checkRuntime(directory), manifest);

  await writeFile(join(directory, "index.html"), "<html>changed</html>");
  await assert.rejects(checkRuntime(directory), /does not match/);
});
