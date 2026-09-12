#!/usr/bin/env node
/**
 * Generate `glimmer-samples/catalog.spec.json` — the `glimmer-samples` inventory — from the
 * vendored AndroidX Glimmer sources.
 *
 * ## Why this exists at all
 *
 * The spec shipped without a `groups` block, on the reading that discovery's own inventory would
 * do. It will not, and `validate-catalog-spec.mjs` says so in one line:
 *
 *     error: `groups` is omitted but the module declares no @CatalogComponent — the catalog has
 *     no inventory.
 *
 * A catalog must declare its inventory one of two ways: `@CatalogComponent` beside the `@Preview`,
 * or `groups` here. The annotation route is closed for the same reason it is closed for
 * `:samples-catalog` — these are upstream's bytes, re-fetched byte-identically on every import, so
 * an annotation written into one would not survive the next `scripts/import-samples.mjs` run. That
 * leaves `groups`, generated and committed, with the regenerate-and-diff `--check` contract
 * `sample-map.json` and `design-map.json` already carry here.
 *
 * ## Why it is not `scripts/samples-spec.mjs`
 *
 * That script's input is `sample-map.json`, read from the `@sample` KDoc of the artifact the module
 * compiles against. Glimmer publishes an AAR with no sources jar, so there is no KDoc to read and
 * no map to build — the reason `docs/design/GLIMMER.md` already gives for this corpus having no
 * drift check. The sources are the only description of themselves that exists.
 *
 * That turns out to cost nothing, because upstream's Glimmer samples carry their OWN `@Preview`
 * functions — unlike material3's, which carry none and need a wrapper generated. Each is the same
 * four lines:
 *
 *     @Preview
 *     @Composable
 *     private fun ButtonWithLeadingIconPreview() {
 *         GlimmerTheme { ButtonWithLeadingIconSample() }
 *     }
 *
 * so the preview function, the sample it renders, and the API it belongs to are all readable
 * without compiling anything.
 *
 * ## The shape it produces
 *
 * One **group per source file**, named for the API the file demonstrates (`ButtonSamples.kt` ->
 * `Button`). One **component per `@Preview`**, because each is its own call site; a sample is not a
 * state of another sample, so nothing folds as a variant — the same call this repo's own
 * `samples-spec.mjs` makes.
 *
 * `related` links a group to the `glimmer-catalog` component of the same name. Here — and NOT in
 * `samples-spec.mjs`, which needs a hand-written table of 60-odd entries — string equality is the
 * correct join: this kit catalog names its components after the Glimmer APIs directly, because
 * Glimmer's own component names are what the Figma kit uses. `ButtonGroup` does not join to
 * `Button`, and that is the point of requiring exact equality rather than a prefix.
 *
 *     node scripts/glimmer-samples-spec.mjs            # regenerate
 *     node scripts/glimmer-samples-spec.mjs --check    # fail if the committed spec is stale
 */

import { readdirSync, readFileSync, writeFileSync } from "node:fs";
import { join } from "node:path";

const VENDORED = "glimmer-samples/src/main/kotlin/upstream/androidx/xr/glimmer/samples";
const SPEC = "glimmer-samples/catalog.spec.json";
/** The kit catalog these samples are call sites for. */
const KIT_SYSTEM = "glimmer-catalog";
const KIT_SOURCES = "glimmer-catalog/src/main/kotlin/ee/schimke/m3catalog/glimmer";

/**
 * A preview function declaration: bare `@Preview`, `@Composable`, an OPTIONAL `private`, and a
 * zero-argument `fun`.
 *
 * Every one of the 50 previews in the vendored tree is written this way, and the constraints that
 * look incidental are the ones worth keeping. Zero-argument excludes a `@PreviewParameter` sample,
 * whose renders are a set rather than the one sticker a component entry promises; a bare `@Preview`
 * excludes one that sets its own `widthDp` or `uiMode`, which would need the spec to say so. If
 * upstream writes either, this stops with the function named (see [scan]) instead of publishing a
 * component entry that describes something else.
 *
 * `private` is optional because exactly one sample — `GlimmerHorizontalPagerSamplePreview` — is
 * public while the other 49 are not. Nothing about the render depends on it.
 */
const PREVIEW = /@Preview\s*\n\s*@Composable\s*\n\s*(?:private\s+)?fun (\w+)\(\)\s*\{/g;

/** Every `@Preview` in the file, however written — the denominator [scan] checks its matches against. */
const ANY_PREVIEW = /^\s*@Preview\b/gm;

/** Any zero-argument call — narrowed to the file's own functions by [scan]. */
const CALL = /\b([A-Z]\w*)\(\)/g;

/** Every function the file itself declares, at any visibility. */
const DECLARED = /^\s*(?:private\s+|internal\s+)?fun (\w+)\(/gm;

/** `ButtonSamples.kt` -> `Button`, `DepthEffectLevelsSample.kt` -> `DepthEffectLevels`. */
function apiName(file) {
  return file.replace(/\.kt$/, "").replace(/Samples?$/, "");
}

/** The function body starting at the `{` [PREVIEW] ended on, found by counting braces. */
function body(text, openBrace) {
  let depth = 0;
  for (let i = openBrace; i < text.length; i++) {
    if (text[i] === "{") depth++;
    else if (text[i] === "}" && --depth === 0) return text.slice(openBrace + 1, i);
  }
  throw new Error("unbalanced braces");
}

/**
 * Read one file's previews, or throw if it holds a `@Preview` this script cannot describe.
 *
 * Most bodies are the one line `GlimmerTheme { ButtonSample() }`, but not all: the pager's wraps
 * its sample in a `Box` to place it, so the rendered function is found by SEARCHING the body rather
 * than by matching a fixed shape. Requiring exactly one hit is what keeps that search honest — a
 * body rendering two samples has no single `componentId`, and one rendering none is not a sample
 * preview at all.
 *
 * What separates the sample from `GlimmerTheme` and `Box` around it is that the file DECLARES it.
 * A name test was the first attempt and it was wrong: half of `CardSamples.kt` renders functions
 * called `CardWithLongText` and `CardWithTitleAndLeadingIconAndHeader`, with no `Sample` in the
 * name at all. Upstream keeps each sample beside the preview that renders it, so "declared here"
 * is both true and the thing actually being relied on.
 *
 * The count check against [ANY_PREVIEW] is the other half. It turns "my pattern missed one" —
 * which would silently drop a component from the published sheet — into a failure that names the
 * file. Dropping one is the bad failure here, because the sheet would still publish and still look
 * complete.
 */
function scan(file, dir) {
  const text = readFileSync(join(dir, file), "utf8");
  const declaredHere = new Set([...text.matchAll(DECLARED)].map((m) => m[1]));
  const found = [];
  for (const match of text.matchAll(PREVIEW)) {
    const preview = match[1];
    const inner = body(text, match.index + match[0].length - 1);
    const names = [...new Set([...inner.matchAll(CALL)].map((m) => m[1]))].filter(
      (name) => name !== preview && declaredHere.has(name),
    );
    if (names.length !== 1) {
      throw new Error(
        `${file}: ${preview}() renders ${names.length} of the file's own functions ` +
          `(${names.join(", ") || "none"}); a component entry needs exactly one to name.`,
      );
    }
    found.push({ preview, sample: names[0] });
  }
  const declared = [...text.matchAll(ANY_PREVIEW)].length;
  if (found.length !== declared) {
    throw new Error(
      `${file}: ${declared} @Preview(s) declared but ${found.length} matched the expected shape ` +
        `(bare @Preview / @Composable / zero-arg fun). Upstream has written a preview this ` +
        `generator does not describe — widen it deliberately rather than letting the component ` +
        `fall out of the sheet.`,
    );
  }
  return found;
}

/** The `@CatalogComponent` ids `glimmer-catalog` declares, for the `related` join. */
export function kitComponentIds(dir = KIT_SOURCES) {
  const ids = new Set();
  for (const file of readdirSync(dir).filter((f) => f.endsWith(".kt"))) {
    for (const [, id] of readFileSync(join(dir, file), "utf8").matchAll(/id\s*=\s*"([^"]+)"/g)) {
      ids.add(id);
    }
  }
  return ids;
}

export function buildGroups(dir = VENDORED, kitIds = new Set()) {
  const groups = [];
  for (const file of readdirSync(dir).filter((f) => f.endsWith(".kt")).sort()) {
    const api = apiName(file);
    const previews = scan(file, dir);
    // A file whose samples upstream never previews contributes nothing to render, so it
    // contributes no group: an empty `components` is not valid against the schema, and a group
    // holding one would be a heading over nothing. `VoiceInputIndicatorSamples.kt` is the case —
    // its sample exists, its preview does not.
    if (previews.length === 0) continue;
    groups.push({
      name: api,
      components: previews.map(({ preview, sample }) => ({
        componentId: `${api}/${sample}`,
        preview,
        caption: `\`${sample}\` — the sample upstream's own \`@Preview\` renders.`,
        ...(kitIds.has(api) ? { related: [{ system: KIT_SYSTEM, componentId: api }] } : {}),
      })),
    });
  }
  // Sorted by GROUP name rather than by file name, because this list is the sheet's display order
  // and a reader scans it for an API. The two differ: `ButtonGroupSamples.kt` sorts before
  // `ButtonSamples.kt`, which would put `ButtonGroup` above `Button`. Same call, same reason, as
  // `samples-spec.mjs`.
  return groups.sort((a, b) => a.name.localeCompare(b.name));
}

export function buildSpec(groups) {
  return {
    $schema:
      "https://raw.githubusercontent.com/yschimke/compose-ai-tools/main/scripts/design-artifacts/catalog.spec.schema.json",
    $comment:
      "GENERATED by scripts/glimmer-samples-spec.mjs from the vendored sources — do not edit. The " +
      "inventory is generated rather than annotated because the sources are upstream's bytes, " +
      "re-fetched byte-identically on every import: an @CatalogComponent written into one would " +
      "not survive it. Unlike :samples-catalog's spec this one is read from the SOURCES rather " +
      "than from a sample-map, because glimmer's AAR publishes no sources jar and so no @sample " +
      "KDoc to build a map from. See docs/design/GLIMMER.md.",
    system: "glimmer-samples",
    title: "Compose Glimmer Samples",
    library: ["androidx.xr.glimmer:glimmer"],
    module: ":glimmer-samples",
    // One mode, and a dark surface, for the reason GLIMMER.md gives at length: the display is
    // ADDITIVE, so black is unlit rather than a theme choice, and a light pair would be a picture
    // of something the glasses cannot produce.
    modes: ["dark"],
    display: { surface: "dark", role: "samples" },
    // Same library, same Robolectric renderer — so unlike the m3 pair this comparison is about
    // design rather than about two rasterisers.
    compareWith: { system: "glimmer-catalog", repo: "yschimke/m3-catalog" },
    groups,
  };
}

function main(argv) {
  const groups = buildGroups(VENDORED, kitComponentIds());
  const json = `${JSON.stringify(buildSpec(groups), null, 2)}\n`;
  const components = groups.reduce((n, g) => n + g.components.length, 0);

  if (argv.includes("--check")) {
    if (readFileSync(SPEC, "utf8") !== json) {
      console.error(`${SPEC} is stale: regenerate it with \`node scripts/glimmer-samples-spec.mjs\`.`);
      process.exit(1);
    }
    console.log(`${SPEC} is current (${components} component(s) in ${groups.length} group(s)).`);
    return;
  }

  writeFileSync(SPEC, json);
  const linked = groups.filter((g) => g.components.some((c) => c.related)).length;
  console.log(`${SPEC}: ${components} component(s) in ${groups.length} group(s).`);
  console.log(`  ${linked} group(s) linked to a ${KIT_SYSTEM} component of the same name.`);
}

if (import.meta.url === `file://${process.argv[1]}`) main(process.argv.slice(2));
