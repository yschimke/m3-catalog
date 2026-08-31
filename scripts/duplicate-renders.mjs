// Renders of one sticker that are byte-identical to each other.
//
// Every primary `@OverrideVariant` cell exists to show something its default does not.
// When two cells of the same sticker hash the same, the sheet publishes one
// picture under two names: `design-led` compares each of those names against
// its own kit node, so the second cell's parity number is scoring the first
// cell's picture, and the coverage the sheet claims is not there. It is a
// defect at the same level as a missing render, and it is invisible in a text
// review — the render succeeds, the sticker appears, and only a hash says the
// two are the same file.
//
// Three stickers shipped that way (issue #176), and #157 and #175 are the same
// failure with a different cause. So: hash every PRIMARY render, group the renders
// that differ only by their seeds, and fail on a collision that `duplicate-renders.json`
// does not declare. Exact exhaustive kit crossings are `secondary`: they are comparison
// addresses rather than variant-tree claims, and a kit can legitimately repeat pixels across
// cells whose property vectors differ. The declarations are checked in BOTH directions — a
// declared collision that stops colliding fails too, so the list cannot outlive the bugs it names.
//
// Nothing here hashes a PNG. `compose-preview show --json` already carries a
// sha256 per capture, so the scan is a pass over the envelope the render leaves
// at `_previews.json`.
//
// Run it:
//
//   node scripts/duplicate-renders.mjs [--previews _previews.json]
//                                      [--manifest catalog/build/compose-previews/previews.json]
//                                      [--declarations duplicate-renders.json]

import { readFileSync } from "node:fs";

/** The tag discovery puts in the id of an `@OverrideVariant` reseed: `…_VARIANT_<name>`. */
const VARIANT_TAG = "_VARIANT_";

/** What an unseeded render is called in a declaration, since its id carries no tag. */
export const BASE_RENDER = "default";

/** Preview ids discovery marked as secondary, independent of what the render envelope preserves. */
export function secondaryPreviewIds(manifest) {
  const entries = Array.isArray(manifest) ? manifest : (manifest?.previews ?? []);
  return new Set(
    entries
      .filter((entry) => entry?.overrides?.secondary === true)
      .map((entry) => String(entry?.id ?? ""))
      .filter(Boolean),
  );
}

/**
 * One rendered PNG, flattened out of the CLI envelope.
 *
 * `family` is what makes two rows comparable: the same composable, drawn in the
 * same `@Preview` mode, at the same capture index — everything the renderer
 * varies EXCEPT the `@OverrideVariant` seeds. Two rows of one family therefore
 * differ only in what a cell asked for, which is exactly the claim a collision
 * refutes. The mode stays in the family rather than being stripped, so a light
 * render is never compared against a dark one.
 */
export function previewRows(envelope, secondaryIds = new Set()) {
  const entries = Array.isArray(envelope) ? envelope : (envelope?.previews ?? []);
  const rows = [];
  for (const entry of entries) {
    // `secondary` cells exhaustively address the kit's cross-product. They are deliberately left
    // out of the navigable variant tree, so the primary-cell invariant this guard enforces does
    // not apply to them. In particular, a generated exact cell can be the fully-qualified address
    // of the same pixels an independently-authored one-axis primary cell already proves.
    const id = String(entry?.id ?? "");
    if (!id) continue;
    // `compose-preview show --json` is a render result, not the discovery manifest. Some CLI
    // versions preserve the override metadata here and some do not, so discovery is authoritative;
    // the envelope check remains a harmless compatibility fast path.
    if (entry?.overrides?.secondary === true || secondaryIds.has(id)) continue;
    const captures = entry?.captures?.length
      ? entry.captures
      : [{ sha256: entry?.sha256, pngPath: entry?.pngPath }];
    captures.forEach((capture, captureIndex) => {
      const sha = capture?.sha256 ?? "";
      // A row with no sha rendered nothing — an optional capture, or a failure
      // the render pipeline reports on its own. Not this scan's business.
      if (!sha) return;
      const at = id.indexOf(VARIANT_TAG);
      rows.push({
        id,
        sha,
        captureIndex,
        functionName: String(entry?.functionName ?? ""),
        render: at < 0 ? BASE_RENDER : id.slice(at + VARIANT_TAG.length),
        family: `${entry?.module ?? ""}/${at < 0 ? id : id.slice(0, at)}#${captureIndex}`,
      });
    });
  }
  return rows;
}

/**
 * Every set of renders within one family that share a sha256.
 *
 * Reported as the sorted render names rather than the preview ids: a
 * declaration is about the cells, which are the same two names in light and in
 * dark, and naming ids would make one collision two entries that have to be
 * kept in step.
 */
export function findCollisions(rows) {
  const families = new Map();
  for (const row of rows) {
    const byHash = families.get(row.family) ?? new Map();
    families.set(row.family, byHash);
    byHash.set(row.sha, [...(byHash.get(row.sha) ?? []), row]);
  }
  const collisions = [];
  for (const [family, byHash] of families) {
    for (const group of byHash.values()) {
      if (group.length < 2) continue;
      collisions.push({
        family,
        functionName: group[0].functionName,
        renders: group.map((row) => row.render).sort(),
        ids: group.map((row) => row.id).sort(),
      });
    }
  }
  return collisions.sort((a, b) => a.family.localeCompare(b.family));
}

/** `["a","b"]` as the order-independent key a declaration is matched on. */
export const setKey = (renders) => [...new Set(renders)].sort().join(" = ");

/** Every declared collision set, flattened to one row each. */
export function declaredSets(declarations) {
  return (declarations?.declarations ?? []).flatMap((declaration) =>
    (declaration.collisions ?? []).map((renders) => ({
      function: declaration.function,
      issue: declaration.issue,
      reason: declaration.reason,
      renders,
      key: `${declaration.function} ${setKey(renders)}`,
    })),
  );
}

/**
 * Both directions, which is what stops the declarations rotting:
 *
 *   * `unexplained` — renders that coincide with nothing here to say why. This
 *     is the failure the scan exists for.
 *   * `stale` — a declared set whose renders no longer coincide. The bug it
 *     names is fixed; the entry now excuses nothing and should be deleted.
 *
 * A collision matches a declaration when its renders are a SUBSET of a declared
 * set. Subset rather than equality because a partial fix is still progress: if
 * three cells collided and one is fixed, the remaining pair is still covered by
 * the entry that named all three, and the stale check will not fire while any
 * of it still collides.
 */
export function audit(collisions, declarations) {
  const declared = new Map(declaredSets(declarations).map((row) => [row.key, row]));
  const matched = new Set();
  const unexplained = [];
  for (const collision of collisions) {
    const hit = [...declared.values()].find(
      (row) =>
        row.function === collision.functionName &&
        collision.renders.every((render) => row.renders.includes(render)),
    );
    if (hit) matched.add(hit.key);
    else unexplained.push(collision);
  }
  return {
    unexplained,
    stale: [...declared.values()].filter((row) => !matched.has(row.key)),
  };
}

export function loadInputs({
  previews = "_previews.json",
  manifest = "catalog/build/compose-previews/previews.json",
  declarations = "duplicate-renders.json",
}) {
  return {
    envelope: JSON.parse(readFileSync(previews, "utf8")),
    manifest: JSON.parse(readFileSync(manifest, "utf8")),
    declarations: JSON.parse(readFileSync(declarations, "utf8")),
  };
}

function main(argv) {
  const args = {};
  for (let i = 0; i < argv.length; i += 2) args[argv[i].replace(/^--/, "")] = argv[i + 1];
  const { envelope, manifest, declarations } = loadInputs(args);
  const rows = previewRows(envelope, secondaryPreviewIds(manifest));
  if (!rows.length) {
    // A guard that passes because it saw nothing is worse than no guard: it
    // reads as "no duplicates" on a run that never rendered.
    console.error("no rendered previews in the envelope — nothing was scanned");
    process.exitCode = 1;
    return;
  }
  const collisions = findCollisions(rows);
  const { unexplained, stale } = audit(collisions, declarations);

  console.log(
    `${rows.length} render(s), ${collisions.length} collision(s), ` +
      `${declaredSets(declarations).length} declared`,
  );
  for (const collision of unexplained) {
    console.error(
      `DUPLICATE: ${collision.functionName} renders ${collision.renders.join(" and ")} ` +
        `byte-identically (${collision.ids.join(", ")})`,
    );
  }
  for (const row of stale) {
    console.error(
      `STALE: ${row.function} ${row.renders.join(" and ")} no longer coincide — ` +
        `delete the entry from duplicate-renders.json (issue #${row.issue})`,
    );
  }
  if (unexplained.length) {
    console.error(
      "A variant that renders its sibling's picture publishes coverage the sheet does not have. " +
        "Fix the sticker, or declare it in duplicate-renders.json with the issue that tracks it.",
    );
  }
  if (unexplained.length || stale.length) process.exitCode = 1;
}

if (import.meta.url === `file://${process.argv[1]}`) main(process.argv.slice(2));
