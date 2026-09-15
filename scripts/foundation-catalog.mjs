#!/usr/bin/env node
/**
 * Generate `foundation-catalog/compose-foundation.json` — the donor catalog the UI builder hands a
 * published catalog its own vocabulary from — out of a frozen golden and an authored curation.
 *
 * ## What a donor is, and why it is a file rather than Kotlin
 *
 * A box, a gradient and an image are not Material 3's and not Wear Material 3's: they are
 * `androidx.compose.foundation` and `androidx.compose.ui`, which publish one of each rather than
 * one per design system. This repository's own `ui-builder.policy.json` says so from the other
 * side — it declares **zero** builtins, on the stated grounds that claiming `layout/*` "would be
 * this catalog claiming to own the builder's own vocabulary". Somebody still has to own them, and
 * today that somebody is a `mapOf` inside the preview server, synthesised at startup from the
 * catalog it packages. yschimke/compose-preview-server#819 is the plan to end that, and its shape
 * is "local for now (derived there), external eventually (published like the others)". This file is
 * the external half: the vocabulary as **data a catalog repository publishes**, so the donor loads
 * a file instead of building a list.
 *
 * ## The three inputs, and why there are three
 *
 *   * `foundation-catalog/goldens/packaged-m3-builder-vocabulary.json` — the seventeen
 *     builder-namespace components of the packaged catalog, copied byte-for-byte. #819 is explicit
 *     that these are read off the goldens rather than retyped, because "the source builds these
 *     lists partly from constants, and a hand-reconstruction of the Wear set missed
 *     `remote-compose/custom` and `remote-compose/inline`".
 *   * `foundation-catalog/curations.json` — which platform borrows which of them, in which order,
 *     on which shelf, and what a borrowed component is told about itself.
 *   * this script — the join, and nothing else. It adds no declaration of its own: a component is
 *     declared once, in the golden, and a curation names it by id.
 *
 * ## What it produces
 *
 * `compose-foundation.json`: the fourteen components `compose-foundation` OWNS, the three
 * `remote-compose/` seams it only REFERENCES (a seam is Remote Compose's, and this catalog says
 * only which one a platform takes and where it sits), and one resolved palette per platform —
 * every borrowed component in its panel order, with the per-platform note applied, ready to be
 * handed to a published catalog without further interpretation.
 *
 *     node scripts/foundation-catalog.mjs            # regenerate
 *     node scripts/foundation-catalog.mjs --check    # fail if the committed file is stale
 */

import { readFileSync, writeFileSync } from "node:fs";

export const GOLDEN = "foundation-catalog/goldens/packaged-m3-builder-vocabulary.json";
export const CURATIONS = "foundation-catalog/curations.json";
export const CATALOG = "foundation-catalog/compose-foundation.json";

/** The namespaces this catalog owns outright. `remote-compose/` is deliberately not among them. */
export const OWNED_NAMESPACES = ["layout/", "shape/", "asset/"];

/** The seam namespace: referenced by a curation, owned by whichever catalog describes it. */
export const SEAM_NAMESPACE = "remote-compose/";

/**
 * The resolved palette one platform borrows: the declarations, in panel order, with the shelf and
 * the per-platform note already applied.
 *
 * A borrowed component is otherwise **unchanged**. That is a checked claim rather than a
 * simplification — see `curations.json`'s `$comment_slotNarrowing`: the Wear curation rewrites
 * notes and touches no slot, modifier or property, so anything this function did beyond the note
 * would be inventing a narrowing nobody asked for.
 */
export function resolvePlatform(golden, curation, platform) {
  const declared = new Map(golden.components.map((c) => [c.componentId, c]));
  const exempt = new Set(curation.componentNoteExcept ?? []);
  return curation.components.map(({ componentId, group }) => {
    const component = declared.get(componentId);
    if (!component) {
      throw new Error(
        `${platform}: ${componentId} is curated but the golden declares no such component. ` +
          `Either the id is a typo or the golden needs refreshing from the packaged catalog.`,
      );
    }
    const note = curation.componentNote;
    const renoted =
      note && !exempt.has(componentId)
        ? { ...component, wasm: { ...component.wasm, notes: note } }
        : component;
    return { ...renoted, group };
  });
}

/** The generated catalog document. */
export function buildCatalog(golden, curations) {
  const owns = golden.components.filter((c) =>
    OWNED_NAMESPACES.some((namespace) => c.componentId.startsWith(namespace)),
  );
  const seams = golden.components.filter((c) => c.componentId.startsWith(SEAM_NAMESPACE));

  const platforms = {};
  for (const [platform, curation] of Object.entries(curations.platforms)) {
    const components = resolvePlatform(golden, curation, platform);
    const shelves = new Set(components.map((c) => c.group));
    const unordered = [...shelves].filter((shelf) => !curation.groupOrder.includes(shelf));
    if (unordered.length > 0) {
      // An unnamed group sorts after every named one, so a shelf missing from the order is a
      // component quietly relegated to the bottom of the insert panel rather than an error anyone
      // sees. Refuse it here instead.
      throw new Error(
        `${platform}: ${unordered.join(", ")} carries donated components but is missing from groupOrder.`,
      );
    }
    platforms[platform] = {
      $comment: curation.$comment,
      groupOrder: curation.groupOrder,
      componentNote: curation.componentNote,
      components,
    };
    if (!curation.componentNote) delete platforms[platform].componentNote;
  }

  return {
    $comment:
      "GENERATED by scripts/foundation-catalog.mjs from foundation-catalog/goldens/ and " +
      "foundation-catalog/curations.json — never edited by hand. `--check` regenerates it and fails " +
      "if the committed copy differs, the same regenerate-and-diff contract design-map.json and the " +
      "samples spec carry here. The donor catalog for the UI builder's own vocabulary: what a " +
      "published catalog is handed so a design can be put INSIDE something. See " +
      "docs/design/FOUNDATION_CATALOG.md and yschimke/compose-preview-server#819.",
    schema: "compose-foundation-donor/v1",
    catalogId: curations.catalogId,
    title: "Compose Foundation",
    provenance: golden.provenance,
    $comment_owns:
      "The components this catalog owns and declares. Fourteen: `layout/*`, `shape/*` and " +
      "`asset/image`. The declarations are the packaged catalog's, unchanged — publishing them from " +
      "here is a change of WHERE they live, not of what they say, which is what makes the " +
      "synthesised donors deletable rather than merely redundant.",
    owns,
    $comment_seams: curations.seams.$comment,
    seams: { preferredOwner: curations.seams.preferredOwner, components: seams },
    $comment_assetRegistry: golden.$comment_assetRegistry,
    assetRegistry: golden.assetRegistry,
    $comment_platforms:
      "One resolved palette per platform, in insert-panel order. A consumer takes the list for its " +
      "own platform word and injects it; it does not have to know what a curation is. A platform " +
      "with no entry here is NOT an instruction to serve an empty palette — see `declined`.",
    platforms,
    declined: curations.declined,
  };
}

export function render(golden, curations) {
  return `${JSON.stringify(buildCatalog(golden, curations), null, 2)}\n`;
}

function main(argv) {
  const golden = JSON.parse(readFileSync(GOLDEN, "utf8"));
  const curations = JSON.parse(readFileSync(CURATIONS, "utf8"));
  const json = render(golden, curations);
  const catalog = JSON.parse(json);
  const summary = Object.entries(catalog.platforms)
    .map(([platform, p]) => `${platform}=${p.components.length}`)
    .join(", ");

  if (argv.includes("--check")) {
    if (readFileSync(CATALOG, "utf8") !== json) {
      console.error(
        `${CATALOG} is stale: regenerate it with \`node scripts/foundation-catalog.mjs\`.`,
      );
      process.exit(1);
    }
    console.log(
      `${CATALOG} is current (${catalog.owns.length} owned, ${catalog.seams.components.length} seams; ${summary}).`,
    );
    return;
  }

  writeFileSync(CATALOG, json);
  console.log(`${CATALOG}: ${catalog.owns.length} owned component(s), ${catalog.seams.components.length} seam(s).`);
  console.log(`  platforms: ${summary}`);
  for (const platform of Object.keys(catalog.declined ?? {})) {
    // Reported every run rather than left implicit: a platform this file declines to curate is a
    // decision a reader has to know about, and the one failure mode that matters is a consumer
    // reading the absence as "serve nothing".
    console.log(`  declined: ${platform} (${catalog.declined[platform].reason})`);
  }
}

if (process.argv[1] && process.argv[1].endsWith("foundation-catalog.mjs")) main(process.argv.slice(2));
