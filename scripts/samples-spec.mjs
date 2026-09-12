#!/usr/bin/env node
/**
 * Generate `samples-catalog/catalog.spec.json` — the `m3-samples` inventory — from `sample-map.json`
 * and the vendored sources.
 *
 * ## Why the inventory is in the spec here, and annotations elsewhere
 *
 * `AGENTS.md` makes annotations the rule: the catalog inventory lives next to the `@Preview`s, and
 * growing mapping JSON is a signal of a missing annotation. That rule is about **this repository's
 * own code**. The samples are upstream's bytes under upstream's package, vendored from a pinned
 * commit and re-fetched byte-identically on every import — an `@CatalogComponent` written into one
 * would be destroyed by the next import, or would have to become a patch per sample, which is 300
 * patches carrying no fix.
 *
 * So the inventory is generated instead, and `catalog.spec.json` is where an IMPORTED project's
 * inventory belongs: the schema says `groups` is optional precisely because a first-party catalog
 * supplies it from annotations, and this is the other case. Generated and committed, with the
 * regenerate-and-diff contract `design-map.json` already carries here.
 *
 * ## What it produces
 *
 * One **group per Compose API** the samples demonstrate (`Button`, `NavigationBar`, …), read from
 * `sample-map.json`, which was itself read from the KDoc of the artifact this module compiles
 * against. One **component per sample function**, because each sample is its own call site and the
 * thing a reader came to see; a sample is not a state of another sample, so nothing folds as a
 * variant.
 *
 * A sample the map names but the vendored sources do not render is skipped and reported. That is
 * the ordinary case for the ~19 `@Sampled` functions carrying no `@Preview` upstream: discovery has
 * nothing to find, so a spec entry naming one would fail the publish gate rather than draw anything.
 *
 *     node scripts/samples-spec.mjs            # regenerate
 *     node scripts/samples-spec.mjs --check    # fail if the committed spec is stale
 */

import { existsSync, readdirSync, readFileSync, writeFileSync } from "node:fs";
import { join } from "node:path";

const SAMPLE_MAP = "sample-map.json";
const VENDORED = "samples-catalog/src/main/kotlin/upstream";
const SPEC = "samples-catalog/catalog.spec.json";
/** The kit catalog these samples are call sites for: the system name, and where its ids live. */
const KIT_SYSTEM = "m3-catalog";
const KIT_SOURCES = ["catalog/src/main/kotlin"];

/**
 * Compose APIs whose samples belong to a kit component this catalog spells differently.
 *
 * A value is a **kit lookup key** in the sense [kitCellIndex] defines: a full cell id
 * (`Chip/Assist`) links that cell, a bare family name (`TopAppBar`) links the family's
 * first-declared cell. Both shapes are in use below and neither is a fallback for the other.
 *
 * This map is LONG here and short in the Wear repo, for a reason worth knowing before editing it:
 * this kit names its sections after the Material spec (`Fab/Standard`, `Chip/Assist`,
 * `Dialog/Basic`) while AndroidX names its APIs after the functions
 * (`FloatingActionButton`, `AssistChip`, `AlertDialog`). Almost nothing joins by string equality,
 * so almost every join is a deliberate entry rather than a lucky one.
 *
 * Prefer the CELL shape wherever the API names a variant the kit models as a cell:
 * `OutlinedButton -> Button/Outlined`, not `-> Button`, which would resolve to `Button/Filled` and
 * point a reader at the wrong swatch. The family shape is for an API whose subject is the section
 * rather than one cell of it — the four `TopAppBar` sizes against a kit that publishes one
 * `TopAppBar/Small`, or a scroll-behaviour factory that belongs to the whole app bar.
 *
 * A mapped value that resolves to no cell THROWS rather than falling through to null. An unmapped
 * API reaching nothing is the ordinary case; a hand-written override reaching nothing is a typo or
 * a cell someone renamed, and the two must not fail the same way.
 *
 * What is deliberately NOT here, and stays reported by `run` on every regeneration: APIs the kit
 * publishes no cell for in any spelling (every navigation drawer, `BottomSheet`, `PullToRefreshBox`,
 * `TooltipBox`, the dropdown menus, `ButtonGroup`, `SwipeToDismissBox`), primitives that are not
 * components (`Surface`, `Text`, `Label`, `minimumInteractiveComponentSize`, `animateWidth`), and
 * the ones where the honest answer is a cell that does not exist — `IconToggleButton` and its
 * filled/tonal/outlined variants, which this kit has no icon-toggle cell for and which must NOT be
 * pointed at a plain `IconButton`.
 */
export const API_TO_KIT_COMPONENT = new Map([
  // Chips: the kit groups by role, AndroidX by function name. Elevated is a presentation of the
  // same role rather than a role of its own, so it shares its cell.
  ["AssistChip", "Chip/Assist"],
  ["ElevatedAssistChip", "Chip/Assist"],
  ["FilterChip", "Chip/Filter"],
  ["ElevatedFilterChip", "Chip/Filter"],
  ["InputChip", "Chip/Input"],
  ["SuggestionChip", "Chip/Suggestion"],
  ["ElevatedSuggestionChip", "Chip/Suggestion"],
  // Buttons and cards: one kit cell per emphasis level.
  ["ElevatedButton", "Button/Elevated"],
  ["OutlinedButton", "Button/Outlined"],
  ["FilledTonalButton", "Button/Tonal"],
  ["TextButton", "Button/Text"],
  ["ElevatedCard", "Card/Elevated"],
  ["OutlinedCard", "Card/Outlined"],
  ["FilledIconButton", "IconButton/Filled"],
  ["FilledTonalIconButton", "IconButton/Tonal"],
  ["OutlinedIconButton", "IconButton/Outlined"],
  ["ElevatedToggleButton", "ToggleButton/Elevated"],
  ["OutlinedToggleButton", "ToggleButton/Outlined"],
  ["TonalToggleButton", "ToggleButton/Tonal"],
  ["SplitButtonLayout", "SplitButton/Filled"],
  // FABs: the kit has two cells — standard and extended — and AndroidX has eight names, which are
  // sizes of those two rather than distinct components.
  ["FloatingActionButton", "Fab/Standard"],
  ["SmallFloatingActionButton", "Fab/Standard"],
  ["MediumFloatingActionButton", "Fab/Standard"],
  ["LargeFloatingActionButton", "Fab/Standard"],
  ["animateFloatingActionButton", "Fab/Standard"],
  ["ExtendedFloatingActionButton", "Fab/Extended"],
  ["SmallExtendedFloatingActionButton", "Fab/Extended"],
  ["MediumExtendedFloatingActionButton", "Fab/Extended"],
  ["LargeExtendedFloatingActionButton", "Fab/Extended"],
  // Progress: wavy is a shape of the same indicator.
  ["CircularProgressIndicator", "Progress/Circular"],
  ["CircularWavyProgressIndicator", "Progress/Circular"],
  ["LinearProgressIndicator", "Progress/Linear"],
  ["LinearWavyProgressIndicator", "Progress/Linear"],
  ["ContainedLoadingIndicator", "LoadingIndicator"],
  // Navigation and structure.
  ["ListItem", "List/Item"],
  ["SearchBar", "Search/Bar"],
  ["ShortNavigationBar", "NavigationBar/Short"],
  ["WideNavigationRail", "NavigationRail/Wide"],
  ["ModalWideNavigationRail", "NavigationRail/Wide"],
  ["PrimaryTabRow", "Tabs/Primary"],
  ["SecondaryTabRow", "Tabs/Secondary"],
  ["TabRow", "Tabs"],
  ["FlexibleBottomAppBar", "BottomAppBar/Standard"],
  ["HorizontalFloatingToolbar", "Toolbar/HorizontalFloating"],
  ["VerticalFloatingToolbar", "Toolbar"],
  // Selection and input.
  ["AlertDialog", "Dialog/Basic"],
  ["TriStateCheckbox", "Checkbox"],
  ["RangeSlider", "Slider/Range"],
  ["VerticalSlider", "Slider"],
  ["MultiChoiceSegmentedButtonRow", "SegmentedButton"],
  ["BadgedBox", "Badge"],
  ["showSnackbar", "Snackbar/Message"],
  // The top app bar: four AndroidX sizes and four scroll-behaviour factories against one kit cell,
  // so the family shape says "open the app bar section" rather than claiming the small one.
  ["CenterAlignedTopAppBar", "TopAppBar"],
  ["LargeTopAppBar", "TopAppBar"],
  ["LargeFlexibleTopAppBar", "TopAppBar"],
  ["MediumFlexibleTopAppBar", "TopAppBar"],
  ["TwoRowsTopAppBar", "TopAppBar"],
  ["enterAlwaysScrollBehavior", "TopAppBar"],
  ["exitAlwaysScrollBehavior", "TopAppBar"],
  ["exitUntilCollapsedScrollBehavior", "TopAppBar"],
  ["pinnedScrollBehavior", "TopAppBar"],
]);

/**
 * Every function in the vendored sources that is BOTH `@Sampled` and `@Preview`, mapped to the file
 * it lives in.
 *
 * Both halves matter. `@Sampled` is what makes it a sample rather than a helper the sample calls —
 * `FancyIndicator` sits beside `FancyIndicatorTabs` and is not itself a sample. `@Preview` is what
 * makes it renderable: discovery scans for that annotation, so a `@Sampled` function without one
 * draws nothing and must not reach the spec.
 */
export function renderableSamples(dir = VENDORED) {
  const found = new Map();
  // RECURSIVE, and it has to be: the vendored tree mirrors the samples' own package, so every file
  // sits under `androidx/compose/material3/samples/` (and the adaptive library's own package)
  // rather than at the root. A flat scan finds nothing there and reports it as "no sample carries
  // @Preview upstream" — an empty spec that reads as a fact about upstream rather than as a walk
  // that never descended.
  const walk = (d) => {
    for (const entry of readdirSync(d, { withFileTypes: true }).sort((a, b) =>
      a.name.localeCompare(b.name),
    )) {
      const path = join(d, entry.name);
      if (entry.isDirectory()) {
        walk(path);
        continue;
      }
      if (!entry.name.endsWith(".kt")) continue;
      const text = readFileSync(path, "utf8");
      // Annotations immediately preceding a `fun` — the compiler's own rule, so no KDoc scanning is
      // needed here and a commented-out sample cannot slip in. The signature is matched as narrowly
      // as what discovery can actually INVOKE, which is less than `@Sampled` + `@Preview`:
      //
      //   * no extension receiver — there is nothing to call it on. `PaneExpansionDragHandleSample`
      //     is `fun ThreePaneScaffoldScope.…(state)`, carries `@Preview` upstream, and draws nothing
      //     here: rendering it produced no PNG at all.
      //   * no parameters — same reason.
      //   * no return type. A composable that returns a value is a state factory, not a call site
      //     that draws: `fun <T> levitateAsDialogSample(): ThreePaneScaffoldNavigator<T>` renders as
      //     a 1x1 blank. Upstream annotates these `@Preview` anyway, which is reasonable there and
      //     indefensible here — a blank card claims a picture the sample never had.
      //
      // A type-parameter list is fine and is NOT a reason to skip: `<T>` says nothing about whether
      // the function draws.
      for (const match of text.matchAll(
        /((?:@\w+(?:\([^)]*\))?\s*)+)fun\s+(?:<[^>]*>\s*)?(\w+)\s*\(\s*\)\s*(:)?/g,
      )) {
        const [, annotations, fn, returnType] = match;
        if (!annotations.includes("@Sampled")) continue;
        if (!annotations.includes("@Preview")) continue;
        if (returnType) continue;
        if (!found.has(fn)) found.set(fn, entry.name);
      }
    }
  };
  walk(dir);
  return found;
}

/**
 * `kit lookup key -> the cell id it names`, read off `@CatalogComponent(id = …)`.
 *
 * TWO kinds of key, because a link wants to name either granularity:
 *
 *  - a **family** (`Button`) maps to the cell declared FIRST in it (`Button/Filled`) — the cell the
 *    section opens on, and the right destination for a sample about the API in general;
 *  - a **full cell id** (`Button/Outlined`) maps to itself — the right destination for a sample
 *    about that one variant, which the family shape cannot express.
 *
 * A single-cell component is both keys at once and resolves identically either way, so the two
 * shapes never disagree.
 *
 * "First" is DETERMINISTIC rather than incidental: files in sorted path order, declarations in file
 * order. That makes it a convention rather than an accident, and the regenerate-and-diff `--check`
 * gate is what keeps it honest — reordering a section's components changes the committed spec and
 * shows up as a diff to review, instead of silently re-pointing every sample of that family.
 */
export function kitCellIndex(dirs = KIT_SOURCES) {
  const index = new Map();
  // Which families have already taken their first cell. A separate set rather than `index.has`,
  // because a single-cell component writes its id and its family name as the SAME key — so asking
  // the index "is this family assigned?" cannot tell that apart from the cell key it just wrote.
  const familyAssigned = new Set();
  const walk = (d) => {
    if (!existsSync(d)) return;
    for (const entry of readdirSync(d, { withFileTypes: true }).sort((a, b) =>
      a.name.localeCompare(b.name),
    )) {
      const path = join(d, entry.name);
      if (entry.isDirectory()) {
        walk(path);
        continue;
      }
      if (!entry.name.endsWith(".kt")) continue;
      const text = readFileSync(path, "utf8");
      for (const match of text.matchAll(/@CatalogComponent\s*\(([\s\S]*?)\)/g)) {
        const id = /\bid\s*=\s*"([^"]+)"/.exec(match[1])?.[1];
        // A family is the id's first segment. An id with no `/` is its own family and its own
        // first cell, which is the single-cell component's ordinary shape.
        if (!id) continue;
        const family = id.split("/")[0];
        if (!familyAssigned.has(family)) {
          familyAssigned.add(family);
          index.set(family, id);
        }
        // The cell under its own id too, so a `Button/Outlined` override resolves without a second
        // structure to keep in step with this one. After the family, so a bare id that IS its
        // family ends up mapped to itself either way.
        index.set(id, id);
      }
    }
  };
  for (const dir of dirs) walk(dir);
  return index;
}

/**
 * The kit component a sample of [api] is a call site for, or null when the kit has none.
 *
 * Null is the common answer and not a failure: most of the map's APIs are ones this catalog
 * publishes no component for, and a sample of one is still worth publishing — it just has nothing
 * to link back to.
 *
 * An override from [API_TO_KIT_COMPONENT] that resolves to nothing THROWS instead. Someone wrote
 * that key by hand against a cell they had read; if it no longer resolves, the cell was renamed or
 * mistyped, and degrading to "this sample has no kit component" would hide a broken link behind
 * the same silence as the ordinary case.
 */
export function kitComponentFor(api, cellIndex) {
  const override = API_TO_KIT_COMPONENT.get(api);
  if (override !== undefined) {
    const resolved = cellIndex.get(override);
    if (!resolved) {
      throw new Error(
        `API_TO_KIT_COMPONENT maps ${api} to "${override}", which this kit declares no cell for. ` +
          `A value is either a full @CatalogComponent id or a family name; check the id in ` +
          `${KIT_SOURCES.join(", ")} and fix the entry rather than dropping it.`,
      );
    }
    return resolved;
  }
  return cellIndex.get(api) ?? null;
}

/** `sampleFunctionName -> api`, inverted from the map's `api -> samples[]`. */
export function apiBySample(map) {
  const byFunction = new Map();
  for (const { api, samples } of map) {
    for (const fqn of samples) {
      const fn = fqn.split(".").pop();
      // First API wins: a sample demonstrating several APIs is filed under the first that claims
      // it, deterministically, because the map is sorted. Grouping is presentation, not a claim of
      // exclusivity, and the map itself keeps the full relation.
      if (!byFunction.has(fn)) byFunction.set(fn, api);
    }
  }
  return byFunction;
}

/** Build the `groups` array: one group per API, one component per renderable sample. */
export function buildGroups(map, renderable, cellIndex = new Map()) {
  const byApi = apiBySample(map);
  const groups = new Map();
  const unmapped = [];
  const unjoined = new Set();

  for (const [fn] of [...renderable].sort((a, b) => a[0].localeCompare(b[0]))) {
    const api = byApi.get(fn);
    if (!api) {
      // Renderable, `@Sampled`, but no `@sample` tag points at it — upstream declared a sample that
      // no KDoc references. It still renders, and it is still a legitimate call site, so it is
      // published under the group its file implies rather than dropped.
      unmapped.push(fn);
    }
    const group = api ?? "Other";
    if (!groups.has(group)) groups.set(group, []);
    // The kit component this sample is a call site for. Declared HERE, on the generated side,
    // rather than on the kit component pointing back: this file is rewritten from `sample-map.json`
    // on every import, so the link cannot go stale, while the same statement written into the kit's
    // `@CatalogComponent` would be a hand-kept second copy of a map that moves whenever upstream
    // renames a sample. The server derives the other direction at read time.
    //
    // No `label`: the destination catalog's own name for the component is better than one invented
    // here, and an absent label is what tells the server to use it.
    const kitComponentId = api ? kitComponentFor(api, cellIndex) : null;
    if (api && !kitComponentId) unjoined.add(api);
    groups.get(group).push({
      componentId: `${group}/${fn}`,
      preview: fn,
      caption: `${fn} — the sample \`${api ?? "upstream"}\`'s KDoc points at.`,
      ...(kitComponentId
        ? { related: [{ system: KIT_SYSTEM, componentId: kitComponentId }] }
        : {}),
    });
  }

  return {
    groups: [...groups.entries()]
      .sort((a, b) => a[0].localeCompare(b[0]))
      .map(([name, components]) => ({ name, components })),
    unmapped,
    unjoined: [...unjoined].sort(),
  };
}

export function buildSpec(map, renderable, cellIndex = new Map()) {
  const { groups, unmapped, unjoined } = buildGroups(map, renderable, cellIndex);
  return {
    spec: {
      $schema:
        "https://raw.githubusercontent.com/yschimke/compose-ai-tools/main/scripts/design-artifacts/catalog.spec.schema.json",
      $comment:
        "GENERATED by scripts/samples-spec.mjs from sample-map.json and the vendored sources — do " +
        "not edit. The inventory is generated rather than annotated because the sources are " +
        "upstream's bytes, re-fetched byte-identically on every import: an @CatalogComponent " +
        "written into one would not survive it. See docs/design/ANDROIDX_SAMPLES.md.",
      system: "m3-samples",
      title: "Material 3 Samples",
      library: ["org.jetbrains.compose.material3:material3"],
      module: ":samples-catalog",
      modes: ["light", "dark"],
      // WHAT KIND of catalog this is, which the preview server reads to shape the pages: a catalog
      // of CALL SITES drops every comparison lane — a sample is not a rendition of a reference, so
      // a difference between the two is not a defect and offering the lane would invite a reader to
      // read it as one — and stands the source beside the render instead of behind a chip, because
      // here the code is what the page is for. It also names this catalog in the kit's back-links,
      // so a component points at "Samples" rather than at this sheet's title.
      //
      // Declared, because only the catalog knows. The server may not infer it from a system name:
      // which catalogs exist is a deployment's business, and `ui-builder-catalog-literals.sh` there
      // exists to keep that knowledge out of its Kotlin.
      display: { role: "samples" },
      // The one thing an imported project cannot declare for itself. Without it every sample
      // renders on Compose's fallback palette rather than the Material system the catalog beside it
      // is drawn in, and the two would not be comparable.
      themes: [
        {
          kind: "wrapper",
          name: "Material",
          wrapper: "MaterialTheme { content() }",
          imports: ["androidx.compose.material3.MaterialTheme"],
        },
      ],
      // The kit catalog these samples are the call sites for. Pairing lands on CANONICAL — samples
      // publish no kit node — which is the right reading: the kit cell beside how you call it.
      compareWith: { system: "m3-catalog", repo: "yschimke/m3-catalog" },
      groups,
    },
    unmapped,
    unjoined,
  };
}

function main(argv) {
  const map = JSON.parse(readFileSync(SAMPLE_MAP, "utf8"));
  const renderable = renderableSamples();
  const { spec, unmapped, unjoined } = buildSpec(map, renderable, kitCellIndex());
  const json = `${JSON.stringify(spec, null, 2)}\n`;

  const components = spec.groups.reduce((n, g) => n + g.components.length, 0);
  const mapped = new Set(map.flatMap((e) => e.samples.map((s) => s.split(".").pop())));
  const notRenderable = [...mapped].filter((fn) => !renderable.has(fn)).sort();

  if (argv.includes("--check")) {
    if (readFileSync(SPEC, "utf8") !== json) {
      console.error(`${SPEC} is stale: regenerate it with \`node scripts/samples-spec.mjs\`.`);
      process.exit(1);
    }
    console.log(`${SPEC} is current (${components} component(s) in ${spec.groups.length} group(s)).`);
    return;
  }

  writeFileSync(SPEC, json);
  console.log(`${SPEC}: ${components} component(s) in ${spec.groups.length} group(s).`);
  const linked = spec.groups.reduce(
    (n, g) => n + g.components.filter((c) => c.related).length,
    0,
  );
  console.log(`  ${linked} component(s) link back to a ${KIT_SYSTEM} component.`);
  if (unjoined.length > 0) {
    // Reported every run, never inferred. Most of these are APIs this catalog publishes no
    // component for, which is the ordinary case; the few that are a kit family under another name
    // belong in `API_TO_KIT_COMPONENT`, added by someone who knows the taxonomy.
    console.log(
      `  ${unjoined.length} API(s) reach no kit family: ` +
        `${unjoined.slice(0, 8).join(", ")}${unjoined.length > 8 ? " …" : ""}`,
    );
  }
  if (unmapped.length > 0) {
    console.log(`  ${unmapped.length} renderable sample(s) no @sample tag points at, filed under Other.`);
  }
  if (notRenderable.length > 0) {
    console.log(
      `  ${notRenderable.length} mapped sample(s) carry no @Preview upstream and are not published: ` +
        `${notRenderable.slice(0, 8).join(", ")}${notRenderable.length > 8 ? " …" : ""}`,
    );
  }
}

if (process.argv[1] && process.argv[1].endsWith("samples-spec.mjs")) main(process.argv.slice(2));
