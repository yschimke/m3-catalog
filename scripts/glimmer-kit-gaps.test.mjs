// `glimmer-kit-gaps.json` is only worth committing if it cannot quietly stop being true. These
// drive the checker with the three things that would make a declaration stale — the kit publishing
// the node, the catalog mapping it, and Renovate bumping the library the API evidence was read
// from — and assert that each one fails the build rather than ageing in place.

import assert from "node:assert/strict";
import { existsSync, readFileSync, readdirSync } from "node:fs";
import test from "node:test";

import { checkGaps, indexPages, pinnedVersion } from "./glimmer-kit-gaps.mjs";

const root = new URL("../", import.meta.url);
const read = (name) => readFileSync(new URL(name, root), "utf8");
const readJson = (name) => JSON.parse(read(name));

const stickerDir = new URL("glimmer-catalog/src/main/kotlin/ee/schimke/m3catalog/glimmer/", root);
const stickerSources = readdirSync(stickerDir)
  .filter((name) => name.endsWith(".kt"))
  .map((name) => readFileSync(new URL(name, stickerDir), "utf8"))
  .join("\n")
  .replace(/\/\*[\s\S]*?\*\//g, "")
  .replace(/\/\/[^\n]*/g, "");

const committed = () => ({
  gaps: readJson("glimmer-kit-gaps.json"),
  pages: readJson("glimmer-design/pages/pages.json"),
  designMap: readJson("glimmer-design-map.json"),
  versions: read("gradle/libs.versions.toml"),
  exists: (relative) => existsSync(new URL(relative, root)),
  stickerSources,
});

test("the committed declarations hold", () => {
  assert.deepEqual(checkGaps(committed()), []);
});

test("every declaration names the issue that records it", () => {
  const { gaps } = committed();
  assert.deepEqual(
    gaps.declarations.map((entry) => [entry.id, entry.issue]),
    [
      ["Pager", 429],
      ["Entity", 431],
      ["ProgressIndicator", 433],
      ["GlimmerLazyList", 414],
      ["ListItemDisabled", 374],
      ["ListItemCard", 374],
      ["Styles", 432],
    ],
  );
});

test("the checker reads the pin Renovate owns", () => {
  assert.equal(pinnedVersion(read("gradle/libs.versions.toml"), "glimmer"), "1.0.0-alpha19");
  assert.equal(pinnedVersion('foo = "1.2"\n', "bar"), null);
});

test("a library bump fails until the new API surface is re-read", () => {
  const input = committed();
  const findings = checkGaps({
    ...input,
    versions: input.versions.replace('glimmer = "1.0.0-alpha19"', 'glimmer = "1.0.0-alpha20"'),
  });
  assert.equal(findings.length, 1);
  assert.match(findings[0], /pinned at 1\.0\.0-alpha20/);
});

test("a kit node that gains a sticker closes its gap, loudly", () => {
  const input = committed();
  const designMap = {
    components: [
      ...input.designMap.components,
      {
        code: "glimmer-catalog/src/main/kotlin/ee/schimke/m3catalog/glimmer/Entities.kt#EntitySticker",
        ref: "figma:HKfLClZDLRyMhf4IQQLna8/40000036:1823",
      },
    ],
  };
  const findings = checkGaps({ ...input, designMap });
  assert.equal(findings.length, 1);
  assert.match(findings[0], /40000036:1823 \("Entity"\), which is now mapped to code/);
});

test("a kit node that leaves the file fails rather than being re-stated", () => {
  const input = committed();
  const pages = {
    pages: input.pages.pages.map((page) => ({
      ...page,
      nodes: (page.nodes ?? []).filter((node) => node.nodeId !== "40000116:8540"),
    })),
  };
  const findings = checkGaps({ ...input, pages });
  assert.equal(findings.length, 1);
  assert.match(findings[0], /names kit node 40000116:8540, which the imported page cache no longer/);
});

test("the kit publishing a Pager node fails the declaration that says it does not", () => {
  const input = committed();
  const [first, ...rest] = input.pages.pages;
  const pages = {
    pages: [
      {
        ...first,
        nodes: [
          ...(first.nodes ?? []),
          { nodeId: "9:99", name: "Pager", type: "COMPONENT_SET", link: "unlinked" },
        ],
      },
      ...rest,
    ],
  };
  const findings = checkGaps({ ...input, pages });
  assert.equal(findings.length, 1);
  assert.match(findings[0], /1 node\(s\) now do/);
});

test("a sample that moves out from under a declaration fails", () => {
  const input = committed();
  const findings = checkGaps({ ...input, exists: () => false });
  assert.ok(findings.length >= 2);
  assert.ok(findings.every((finding) => /which does not exist/.test(finding)));
});

test("a declaration with no reason, evidence or kind is refused", () => {
  const input = committed();
  const findings = checkGaps({
    ...input,
    gaps: {
      ...input.gaps,
      library: { ...input.gaps.library, components: [] },
      declarations: [{ id: "Mystery", kind: "vibes" }],
    },
  });
  assert.deepEqual(findings.sort(), [
    'declaration "Mystery" carries no evidence',
    'declaration "Mystery" carries no issue',
    'declaration "Mystery" carries no reason',
    'declaration "Mystery" has kind "vibes", not one of api-gap,sample-only,tokens',
  ]);
});

test("the page index keeps the first page a shared node appears on", () => {
  const index = indexPages({
    pages: [
      { id: "a", nodes: [{ nodeId: "1:1", name: "bg" }] },
      { id: "b", nodes: [{ nodeId: "1:1", name: "bg" }] },
    ],
  });
  assert.equal(index.get("1:1").page, "a");
});

test("every published component composable is drawn or declared", () => {
  // The completeness statement itself, spelled out: the committed inputs already assert it above
  // (the clean run), so what is worth pinning here is that the check can FAIL.
  const input = committed();
  const findings = checkGaps({
    ...input,
    gaps: { ...input.gaps, library: { ...input.gaps.library, components: ["Tooltip"] } },
  });
  assert.equal(findings.length, 1);
  assert.match(findings[0], /publishes Tooltip, and no sticker calls it/);
});

test("an API that is both drawn and declared is a stale declaration", () => {
  const input = committed();
  const findings = checkGaps({
    ...input,
    gaps: {
      ...input.gaps,
      library: { ...input.gaps.library, components: ["Button"] },
      declarations: [
        {
          id: "Button",
          kind: "sample-only",
          issue: 1,
          reason: "r",
          evidence: "e",
          apis: ["Button"],
          sample: "glimmer-kit-gaps.json",
        },
      ],
    },
  });
  assert.deepEqual(findings, ["Button is declared as a gap and called by a sticker. One of the two is stale."]);
});
