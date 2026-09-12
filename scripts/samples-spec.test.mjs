import { test } from "node:test";
import assert from "node:assert/strict";
import { mkdirSync, mkdtempSync, rmSync, writeFileSync } from "node:fs";
import { tmpdir } from "node:os";
import { join } from "node:path";

import {
  API_TO_KIT_COMPONENT,
  buildGroups,
  buildSpec,
  kitComponentFor,
  kitCellIndex,
  renderableSamples,
} from "./samples-spec.mjs";

/** A throwaway kit source tree: `<root>/<name>` for each entry. */
function kitSources(files) {
  const root = mkdtempSync(join(tmpdir(), "samples-spec-test-"));
  for (const [name, text] of Object.entries(files)) {
    const path = join(root, name);
    mkdirSync(join(path, ".."), { recursive: true });
    writeFileSync(path, text);
  }
  return root;
}

const COMPONENT = (id, extra = "") =>
  `@CatalogComponent(\n  id = "${id}",\n  caption = "A cell."${extra}\n)\n@Preview\n@Composable\nfun P${id.replace(/\\W/g, "")}() {}\n`;

test("a family's FIRST declared cell is the one a sample links to", () => {
  const root = kitSources({
    "Buttons.kt": COMPONENT("Button/Filled") + COMPONENT("Button/Outlined"),
  });
  try {
    assert.equal(kitCellIndex([root]).get("Button"), "Button/Filled");
  } finally {
    rmSync(root, { recursive: true, force: true });
  }
});

test("declaration order is file order, and files are walked in sorted path order", () => {
  // Determinism is the whole claim: "first" has to be a convention someone can rely on, not
  // whatever order the filesystem happened to hand back.
  const root = kitSources({
    "Zzz.kt": COMPONENT("Button/Last"),
    "Aaa.kt": COMPONENT("Button/First"),
  });
  try {
    assert.equal(kitCellIndex([root]).get("Button"), "Button/First");
  } finally {
    rmSync(root, { recursive: true, force: true });
  }
});

test("an id with no slash is its own family and its own first cell", () => {
  const root = kitSources({ "Dialogs.kt": COMPONENT("AlertDialog") });
  try {
    assert.equal(kitCellIndex([root]).get("AlertDialog"), "AlertDialog");
  } finally {
    rmSync(root, { recursive: true, force: true });
  }
});

test("a multi-line annotation with other fields still yields its id", () => {
  // The real ones carry `reference`, `referenceSet`, `caption` and more across several lines.
  const root = kitSources({
    "Swipe.kt": COMPONENT("SwipeToReveal/Card", `,\n  reference = "figma:abc/1:2"`),
  });
  try {
    assert.equal(kitCellIndex([root]).get("SwipeToReveal"), "SwipeToReveal/Card");
  } finally {
    rmSync(root, { recursive: true, force: true });
  }
});

test("an API the kit publishes no component for reaches nothing", () => {
  // The ordinary case, and not a failure: most of the map's APIs are ones this catalog has no
  // component for, and their samples are still worth publishing.
  assert.equal(kitComponentFor("NoSuchApi", new Map([["Button", "Button/Filled"]])), null);
});

test("a sample component carries the link, and one with no kit family carries none", () => {
  const map = [
    { api: "Button", samples: ["androidx.compose.material3.samples.ButtonSample"] },
    { api: "Orphan", samples: ["androidx.compose.material3.samples.OrphanSample"] },
  ];
  // This catalog publishes the upstream `@Preview` itself, so a sample's preview IS its own name.
  const renderable = new Map([
    ["ButtonSample", "ButtonSample"],
    ["OrphanSample", "OrphanSample"],
  ]);
  const { groups, unjoined } = buildGroups(
    map,
    renderable,
    new Map([["Button", "Button/Filled"]]),
  );
  const components = groups.flatMap((g) => g.components);
  const button = components.find((c) => c.componentId === "Button/ButtonSample");
  const orphan = components.find((c) => c.componentId === "Orphan/OrphanSample");

  assert.deepEqual(button.related, [
    { system: "m3-catalog", componentId: "Button/Filled" },
  ]);
  // Absent, not empty: "declared and empty" and "not declared" mean the same thing downstream, and
  // the shorter one does not make an absent link look declared.
  assert.equal(orphan.related, undefined);
  assert.deepEqual(unjoined, ["Orphan"]);
});

test("the spec declares what kind of catalog this is", () => {
  // The server reads this to shape the pages (comparison lanes dropped, source beside the render)
  // and to name this catalog in the kit's back-links. It cannot infer it: which catalogs exist is
  // the deployment's business, not the server's.
  const { spec } = buildSpec([], new Map(), new Map());
  assert.equal(spec.display.role, "samples");
});

test("no label is emitted, so the destination names the component", () => {
  const { groups } = buildGroups(
    [{ api: "Button", samples: ["a.b.ButtonSample"] }],
    new Map([["ButtonSample", "ButtonSample"]]),
    new Map([["Button", "Button/Filled"]]),
  );
  const link = groups.flatMap((g) => g.components)[0].related[0];
  assert.equal("label" in link, false);
});

test("samples are found under the package directories they are vendored into", () => {
  // The vendored tree mirrors the samples' own package, so nothing sits at its root. A flat scan
  // finds no sample at all and reports it as "none carries @Preview upstream" — an empty spec that
  // reads as an upstream fact rather than as a walk that never descended. Caught exactly that way.
  const root = kitSources({
    "androidx/compose/material3/samples/ButtonSamples.kt":
      "@Sampled\n@Preview\n@Composable\nfun ButtonSample() {}\n" +
      "@Composable\nfun FancyHelper() {}\n",
  });
  try {
    const found = renderableSamples(root);
    assert.deepEqual([...found.keys()], ["ButtonSample"]);
  } finally {
    rmSync(root, { recursive: true, force: true });
  }
});

test("renderable means what discovery can invoke, not merely @Sampled + @Preview", () => {
  // Discovery calls a preview with no arguments and no receiver, and a composable that RETURNS a
  // value draws nothing. The adaptive samples supply all three shapes: publishing them produced one
  // card with no PNG at all and three cards whose render is a 1x1 blank.
  const dir = mkdtempSync(join(tmpdir(), "renderable-"));
  try {
    writeFileSync(
      join(dir, "ThreePaneScaffoldSample.kt"),
      `
@Preview
@Sampled
@Composable
fun ListDetailPaneScaffoldSample() {}

@Preview
@Sampled
@Composable
fun <T> genericButDrawsSample() {}

@Preview
@Sampled
@Composable
fun <T> levitateAsDialogSample(): ThreePaneScaffoldNavigator<T> {}

@Preview
@Sampled
@Composable
fun ThreePaneScaffoldScope.PaneExpansionDragHandleSample(state: PaneExpansionState) {}

@Sampled
@Composable
fun NotPreviewed() {}
`,
    );
    assert.deepEqual(
      [...renderableSamples(dir).keys()].sort(),
      // The generic one stays: `<T>` says nothing about whether the function draws.
      ["ListDetailPaneScaffoldSample", "genericButDrawsSample"],
    );
  } finally {
    rmSync(dir, { recursive: true, force: true });
  }
});

test("a cell id in the index maps to itself, so an override can name one directly", () => {
  const root = kitSources({
    "Buttons.kt": COMPONENT("Button/Filled") + COMPONENT("Button/Outlined"),
  });
  try {
    const index = kitCellIndex([root]);
    // Both granularities, from one walk: the family opens on its first cell, and each cell is
    // addressable on its own.
    assert.equal(index.get("Button"), "Button/Filled");
    assert.equal(index.get("Button/Outlined"), "Button/Outlined");
  } finally {
    rmSync(root, { recursive: true, force: true });
  }
});

test("an override naming a cell links THAT cell, not its family's first", () => {
  // The whole reason values may be cell ids, and the common case in this repo: the kit names its
  // sections after the spec while AndroidX names its APIs after the functions, so `OutlinedButton`
  // mapped to the family `Button` would resolve to `Button/Filled` — the wrong swatch.
  const index = new Map([
    ["Button", "Button/Filled"],
    ["Button/Outlined", "Button/Outlined"],
  ]);
  assert.equal(kitComponentFor("OutlinedButton", index), "Button/Outlined");
});

test("an override naming a family links the family's first cell", () => {
  // The other shape, for an API whose subject is the section rather than one cell of it — the four
  // TopAppBar sizes against a kit that publishes one.
  const index = new Map([["TopAppBar", "TopAppBar/Small"]]);
  assert.equal(kitComponentFor("LargeTopAppBar", index), "TopAppBar/Small");
});

test("an override that resolves to nothing throws, naming the value", () => {
  // A hand-written override reaching nothing is a typo or a renamed cell. An UNMAPPED api reaching
  // nothing is the ordinary case, so the two must not fail the same way — this is the whole
  // difference between the two branches of `kitComponentFor`.
  assert.throws(
    () => kitComponentFor("OutlinedButton", new Map([["Button", "Button/Filled"]])),
    (error) =>
      error.message.includes("OutlinedButton") && error.message.includes("Button/Outlined"),
  );
});

test("every override in the shipped map resolves against the real kit", () => {
  // With 61 entries this is the test that matters: it turns a renamed cell into a red build rather
  // than a `--check` diff nobody reads closely.
  const index = kitCellIndex();
  for (const [api, value] of API_TO_KIT_COMPONENT) {
    assert.equal(typeof index.get(value), "string", `${api} -> ${value} resolves to no kit cell`);
  }
});
