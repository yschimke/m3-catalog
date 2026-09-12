import { test } from "node:test";
import assert from "node:assert/strict";
import { mkdirSync, mkdtempSync, writeFileSync } from "node:fs";
import { tmpdir } from "node:os";
import { join } from "node:path";

import { buildGroups, kitComponentIds } from "./glimmer-samples-spec.mjs";

/** A throwaway sample tree: `<root>/<name>` for each entry. */
function sources(files) {
  const root = mkdtempSync(join(tmpdir(), "glimmer-samples-spec-test-"));
  for (const [name, text] of Object.entries(files)) {
    const path = join(root, name);
    mkdirSync(join(path, ".."), { recursive: true });
    writeFileSync(path, text);
  }
  return root;
}

/** The four lines upstream writes around every sample. */
const PREVIEW = (name, body) =>
  `@Preview\n@Composable\nprivate fun ${name}Preview() {\n    GlimmerTheme { ${body}() }\n}\n`;
const SAMPLE = (name) => `@Sampled\n@Composable\nfun ${name}() {\n    Text("x")\n}\n`;

test("a group per file, named for the API, with a component per @Preview", () => {
  const dir = sources({
    "ButtonSamples.kt": SAMPLE("ButtonSample") + PREVIEW("Button", "ButtonSample"),
    "CardSamples.kt": SAMPLE("CardSample") + PREVIEW("Card", "CardSample"),
  });

  assert.deepEqual(buildGroups(dir), [
    {
      name: "Button",
      components: [
        {
          componentId: "Button/ButtonSample",
          preview: "ButtonPreview",
          caption: "`ButtonSample` — the sample upstream's own `@Preview` renders.",
        },
      ],
    },
    {
      name: "Card",
      components: [
        {
          componentId: "Card/CardSample",
          preview: "CardPreview",
          caption: "`CardSample` — the sample upstream's own `@Preview` renders.",
        },
      ],
    },
  ]);
});

test("groups sort by API name, not by file name", () => {
  // `ButtonGroupSamples.kt` sorts BEFORE `ButtonSamples.kt`, so a file-order sort would put the
  // narrower API above the one it is named after. This list is the sheet's display order.
  const dir = sources({
    "ButtonGroupSamples.kt": SAMPLE("ButtonGroupSample") + PREVIEW("ButtonGroup", "ButtonGroupSample"),
    "ButtonSamples.kt": SAMPLE("ButtonSample") + PREVIEW("Button", "ButtonSample"),
  });

  assert.deepEqual(
    buildGroups(dir).map((g) => g.name),
    ["Button", "ButtonGroup"],
  );
});

test("`DepthEffectLevelsSample.kt` loses the singular suffix too", () => {
  const dir = sources({
    "DepthEffectLevelsSample.kt": SAMPLE("DepthEffectSample") + PREVIEW("DepthEffect", "DepthEffectSample"),
  });
  assert.equal(buildGroups(dir)[0].name, "DepthEffectLevels");
});

test("the rendered function is the one the FILE declares, not the one whose name says Sample", () => {
  // Half of upstream's `CardSamples.kt` renders functions with no `Sample` in the name at all. A
  // name test passed the rest of the corpus and dropped exactly these.
  const dir = sources({
    "CardSamples.kt":
      SAMPLE("CardWithLongText") + PREVIEW("CardWithLongText", "CardWithLongText"),
  });
  assert.equal(buildGroups(dir)[0].components[0].componentId, "Card/CardWithLongText");
});

test("a public preview wrapping its sample in a layout is still read", () => {
  // `GlimmerHorizontalPagerSamplePreview` is the one public preview among the 50, and the one whose
  // body is not the single line `GlimmerTheme { XSample() }`.
  const dir = sources({
    "GlimmerPagerSamples.kt":
      SAMPLE("GlimmerHorizontalPagerSample") +
      `@Preview\n@Composable\nfun GlimmerHorizontalPagerSamplePreview() {\n` +
      `    GlimmerTheme {\n        Box(modifier = Modifier.fillMaxSize()) {\n` +
      `            GlimmerHorizontalPagerSample()\n        }\n    }\n}\n`,
  });

  assert.deepEqual(buildGroups(dir)[0].components, [
    {
      componentId: "GlimmerPager/GlimmerHorizontalPagerSample",
      preview: "GlimmerHorizontalPagerSamplePreview",
      caption: "`GlimmerHorizontalPagerSample` — the sample upstream's own `@Preview` renders.",
    },
  ]);
});

test("a @Preview the generator cannot describe FAILS rather than falling out of the sheet", () => {
  // The whole point of the count assertion: a silently dropped component still publishes a sheet
  // that looks complete, which is the failure nobody catches by eye.
  const dir = sources({
    "ButtonSamples.kt":
      SAMPLE("ButtonSample") +
      PREVIEW("Button", "ButtonSample") +
      `@Preview(widthDp = 200)\n@Composable\nprivate fun ButtonWidePreview() {\n` +
      `    GlimmerTheme { ButtonSample() }\n}\n`,
  });

  assert.throws(() => buildGroups(dir), /2 @Preview\(s\) declared but 1 matched/);
});

test("a preview rendering two of the file's samples has no single id, and says so", () => {
  const dir = sources({
    "CardSamples.kt":
      SAMPLE("CardSample") +
      SAMPLE("ActionCardSample") +
      `@Preview\n@Composable\nprivate fun BothPreview() {\n` +
      `    GlimmerTheme { CardSample(); ActionCardSample() }\n}\n`,
  });

  assert.throws(() => buildGroups(dir), /renders 2 of the file's own functions/);
});

test("a file whose samples upstream never previews contributes no group", () => {
  // `VoiceInputIndicatorSamples.kt` is the live case. An empty `components` is invalid against the
  // schema, and a group holding one would be a heading over nothing.
  const dir = sources({ "VoiceInputIndicatorSamples.kt": SAMPLE("VoiceInputIndicatorSample") });
  assert.deepEqual(buildGroups(dir), []);
});

test("`related` joins on exact name equality, so ButtonGroup does not link to Button", () => {
  const dir = sources({
    "ButtonSamples.kt": SAMPLE("ButtonSample") + PREVIEW("Button", "ButtonSample"),
    "ButtonGroupSamples.kt": SAMPLE("ButtonGroupSample") + PREVIEW("ButtonGroup", "ButtonGroupSample"),
  });
  const groups = buildGroups(dir, new Set(["Button"]));

  assert.deepEqual(groups[0].components[0].related, [
    { system: "glimmer-catalog", componentId: "Button" },
  ]);
  assert.equal(groups[1].components[0].related, undefined);
});

test("kit ids are read from the catalog's @CatalogComponent annotations", () => {
  const dir = sources({
    "Buttons.kt":
      `@CatalogComponent(\n  id = "Button",\n  reference = "figma:abc/1:2",\n)\n@Preview\n@Composable\nfun ButtonSticker() {}\n` +
      `@CatalogComponent(\n  id = "ToggleButton",\n)\n@Preview\n@Composable\nfun ToggleButtonSticker() {}\n`,
  });
  assert.deepEqual([...kitComponentIds(dir)].sort(), ["Button", "ToggleButton"]);
});
