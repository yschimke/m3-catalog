#!/usr/bin/env node
// Lay the typed pilot out the way a production delivery branch is laid out, so a preview server
// serves `m3-catalog-typed-pilot` beside `m3-catalog` from `m3-catalog-out`'s
// `design-artifacts/m3-catalog-typed-pilot`:
//
//   catalog.json              the manifest: componentsFile, uiBuilderFile, uiBuilderRuntime
//   components.json           the pilot record: the typed wrappers and the real export targets
//   ui-builder.json           the pilot policy: projections, compose-component-records v1
//   ui-builder/runtime.zip    the pilot's protocol-v2 runtime
//   ui-builder/designs/*.json the starter templates, re-pinned to the pilot
//
// It publishes no stickers: the pilot exists to A/B the Compose source export, and production's
// sheet is the one with pictures. Everything here is read from `:catalog-typed-adapters`' own
// outputs and checked before it is written, so a publish never carries a pair the export test has
// not seen or a runtime whose bytes do not match its manifest.
//
// Usage: node scripts/assemble-typed-pilot-artifacts.mjs --out <dir> [--source-ref <ref>]

import { createHash } from "node:crypto";
import { mkdir, readFile, readdir, rm, writeFile, copyFile } from "node:fs/promises";
import { dirname, join, relative, resolve, sep } from "node:path";
import { fileURLToPath } from "node:url";
import { parseArgs } from "node:util";

export const PILOT_ID = "m3-catalog-typed-pilot";
const COMPONENT_RECORDS = "compose-component-records";
const RUNTIME_MANIFEST = "runtime-manifest.json";
const RUNTIME_PATH = "ui-builder/runtime.zip";

/** The runtime assembler's and the server's framing: path, NUL, length, NUL, bytes; sorted. */
export function treeIntegrity(files) {
  const digest = createHash("sha256");
  for (const [path, bytes] of [...files].sort(([a], [b]) =>
    Buffer.compare(Buffer.from(a, "utf8"), Buffer.from(b, "utf8")),
  )) {
    digest.update(path, "utf8");
    digest.update(Uint8Array.of(0));
    digest.update(String(bytes.length), "utf8");
    digest.update(Uint8Array.of(0));
    digest.update(bytes);
  }
  return digest.digest("hex");
}

async function walk(directory) {
  const files = new Map();
  async function visit(current) {
    for (const entry of await readdir(current, { withFileTypes: true })) {
      const path = join(current, entry.name);
      if (entry.isDirectory()) await visit(path);
      else if (entry.isFile())
        files.set(relative(directory, path).split(sep).join("/"), await readFile(path));
    }
  }
  await visit(directory);
  return files;
}

/** The pilot pair, refused unless it is the pilot and selects the projected export. */
export function checkPair(policy, record) {
  const semantics = policy.statusSemantics ?? {};
  if (policy.catalog?.id !== PILOT_ID)
    throw new Error(`ui-builder.json is catalog '${policy.catalog?.id}', not ${PILOT_ID}`);
  const exportAdapter = semantics.composeSourceExport;
  if (exportAdapter?.adapter !== COMPONENT_RECORDS || exportAdapter?.version !== 1)
    throw new Error(
      `the pilot must select ${COMPONENT_RECORDS} v1, found ${JSON.stringify(exportAdapter)}`,
    );
  const projected = Object.entries(semantics.components ?? {})
    .filter(([, component]) => component.exportProjection)
    .map(([id]) => id);
  if (projected.length === 0) throw new Error("the pilot publishes no export projection");
  const records = new Set((record.components ?? []).map((component) => component.canonicalId));
  for (const [id, component] of Object.entries(semantics.components ?? {})) {
    if (component.record && !records.has(component.record))
      throw new Error(`${id} names record ${component.record}, which components.json lacks`);
  }
  return { projected, templates: semantics.templates ?? [] };
}

/** A starter template pinned to the pilot, so a design made from it selects the pilot. */
export function pilotTemplate(text) {
  const template = JSON.parse(text);
  template.catalogPin = { ...(template.catalogPin ?? {}), systemId: PILOT_ID };
  return `${JSON.stringify(template, null, 2)}\n`;
}

/** The runtime's manifest, once its directory's bytes are proven to be what it declares. */
export async function checkRuntime(distDirectory) {
  const files = await walk(distDirectory);
  const manifestBytes = files.get(RUNTIME_MANIFEST);
  if (!manifestBytes) throw new Error(`${distDirectory} has no ${RUNTIME_MANIFEST}`);
  const manifest = JSON.parse(manifestBytes.toString("utf8"));
  files.delete(RUNTIME_MANIFEST);
  if (!files.has(manifest.entrypoint))
    throw new Error(`the runtime's entrypoint ${manifest.entrypoint} is missing`);
  if (!/^[A-Za-z0-9._-]+$/.test(manifest.runtimeId ?? ""))
    throw new Error(`unsafe runtime id ${manifest.runtimeId}`);
  const actual = treeIntegrity(files);
  if (actual !== manifest.integritySha256)
    throw new Error(`runtime integrity ${actual} does not match ${manifest.integritySha256}`);
  return manifest;
}

export async function assemble({ root, out, sourceRepo, sourceRef }) {
  const build = join(root, "catalog-typed-adapters/build");
  const pair = join(build, "catalog");
  const policyText = await readFile(join(pair, "ui-builder.json"), "utf8");
  const recordText = await readFile(join(pair, "components.json"), "utf8");
  const { projected, templates } = checkPair(JSON.parse(policyText), JSON.parse(recordText));
  const runtime = await checkRuntime(join(build, "wasmRendererDist"));

  await rm(out, { recursive: true, force: true });
  await mkdir(join(out, "ui-builder"), { recursive: true });
  await writeFile(join(out, "components.json"), recordText);
  await writeFile(join(out, "ui-builder.json"), policyText);
  await copyFile(
    join(build, "distributions/m3-catalog-typed-pilot-renderer.zip"),
    join(out, RUNTIME_PATH),
  );
  for (const path of templates) {
    if (path.startsWith("/") || path.split("/").includes(".."))
      throw new Error(`template ${path} escapes the catalog`);
    const target = join(out, path);
    await mkdir(dirname(target), { recursive: true });
    // The committed production template is never rewritten; only the pilot's copy is re-pinned.
    await writeFile(target, pilotTemplate(await readFile(join(root, path), "utf8")));
  }
  const manifest = {
    schema: "design-parity-catalog/v1",
    system: PILOT_ID,
    title: "Material 3 — typed pilot",
    library: [
      "org.jetbrains.compose.material3:material3",
      "org.jetbrains.compose.material3:material3-adaptive-navigation-suite",
    ],
    // No `generatedAt`: the content alone decides whether a publish appends a commit.
    renderer: `typed-pilot ${runtime.runtimeId}`,
    components: [],
    componentsFile: "components.json",
    uiBuilderFile: "ui-builder.json",
    uiBuilderRuntime: {
      path: RUNTIME_PATH,
      runtimeId: runtime.runtimeId,
      protocolVersion: runtime.protocolVersion,
      integritySha256: runtime.integritySha256,
    },
    ...(sourceRepo
      ? { source: { repo: sourceRepo, ref: sourceRef, module: ":catalog-typed-adapters" } }
      : {}),
  };
  await writeFile(join(out, "catalog.json"), `${JSON.stringify(manifest, null, 2)}\n`);
  return { projected, templates: templates.length, runtimeId: runtime.runtimeId };
}

if (process.argv[1] && resolve(process.argv[1]) === fileURLToPath(import.meta.url)) {
  const { values } = parseArgs({
    options: {
      out: { type: "string" },
      "source-repo": { type: "string" },
      "source-ref": { type: "string" },
    },
  });
  if (!values.out) throw new Error("--out is required");
  const root = resolve(dirname(fileURLToPath(import.meta.url)), "..");
  const result = await assemble({
    root,
    out: resolve(values.out),
    sourceRepo: values["source-repo"],
    sourceRef: values["source-ref"],
  });
  console.log(
    `${PILOT_ID}: ${result.projected.length} projected component(s) ` +
      `(${result.projected.join(", ")}), ${result.templates} template(s), ` +
      `runtime ${result.runtimeId} → ${values.out}`,
  );
}
