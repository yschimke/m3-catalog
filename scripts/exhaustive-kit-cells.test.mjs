// The generated exact-cell view for the Material 3 kit.
//
// `scripts/generate-exhaustive-kit-cells.mjs` writes a manifest and a Kotlin annotation file per
// catalog. The generator's inputs remain arguments so its mechanics are reusable, while this test
// holds the invocation owned by this repository still.
//
// What it holds still is that a generated cell is an EXACT kit address: its `kitProps` is the kit
// variant's own name, it has a real input behind it rather than being a second name for the default
// pixels, and the annotation it lives in is actually attached to the sticker it claims to cross.

import assert from "node:assert/strict";
import { readFileSync } from "node:fs";
import test from "node:test";

const CATALOGS = [
  {
    name: "m3-catalog",
    manifest: "exhaustive-kit-cells.json",
    index: "figma-kit-index.json",
    generated: "catalog/src/main/kotlin/ee/schimke/m3catalog/ExhaustiveKitCellAnnotations.kt",
    annotationPackage: "ee\\.schimke\\.m3catalog",
  },
];

const interactionByKitState = new Map([
  ["Hovered", "Hovered"],
  ["Focused", "Focused"],
  ["Pressed", "Pressed"],
  // Typo in the published Outlined Button set; the exact kit value remains authoritative.
  ["Presssed", "Pressed"],
]);

for (const catalog of CATALOGS) {
  const manifest = JSON.parse(readFileSync(catalog.manifest, "utf8"));
  const index = JSON.parse(readFileSync(catalog.index, "utf8"));
  const generated = readFileSync(catalog.generated, "utf8");

  const indexed = new Map(
    Object.values(index.sets).flatMap((set) =>
      set.variants.map((variant) => [variant.id, variant.name.split(", ")]),
    ),
  );

  test(`${catalog.name}: every exhaustive cell is an exact indexed vector with a real input`, () => {
    assert.equal(manifest.schema, "m3-catalog-exhaustive-kit-cells/v1");
    for (const component of manifest.components) {
      assert.equal(new Set(component.cells.map((cell) => cell.id)).size, component.cells.length);
      assert.equal(new Set(component.cells.map((cell) => cell.name)).size, component.cells.length);
      for (const cell of component.cells) {
        assert.deepEqual(cell.kitProps, indexed.get(cell.id), `${component.code} / ${cell.name}`);
        assert.ok(cell.seeds.length > 0 || cell.interaction, `${component.code} / ${cell.name}`);
        const kitState = cell.kitProps.find((prop) => prop.startsWith("State="))?.slice(6);
        if (interactionByKitState.has(kitState)) {
          assert.equal(
            cell.interaction,
            interactionByKitState.get(kitState),
            `${component.code} / ${cell.name}`,
          );
        }
      }
    }
  });

  test(`${catalog.name}: every non-empty generated annotation is attached to its real sticker`, () => {
    for (const component of manifest.components.filter((entry) => entry.cells.length > 0)) {
      const file = component.code.slice(0, component.code.lastIndexOf("#"));
      const source = readFileSync(file, "utf8");
      assert.match(source, new RegExp(`@${catalog.annotationPackage}\\.${component.annotation}\\b`));
    }
  });

  test(`${catalog.name}: the generated Kotlin carries every manifest cell as secondary`, () => {
    const cells = manifest.components.reduce((count, entry) => count + entry.cells.length, 0);
    assert.equal((generated.match(/@OverrideVariant\(/g) ?? []).length, cells);
    assert.equal((generated.match(/secondary = true/g) ?? []).length, cells);
    for (const component of manifest.components) {
      assert.match(generated, new RegExp(`annotation class ${component.annotation}\\b`));
    }
  });

}
