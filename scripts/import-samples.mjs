#!/usr/bin/env node
/**
 * Vendor the AndroidX samples named by `samples/import.json` into this repository.
 *
 * See `docs/design/ANDROIDX_SAMPLES.md` for why vendoring is the only option: the sample modules are
 * **not published as artifacts** — `material3-samples`, `compose-material3-samples` and
 * `foundation-samples` all 404 on Google Maven — so there is nothing to depend on, and the source
 * has to come out of the AndroidX tree.
 *
 * ## How the fetch works, and why it is not a clone
 *
 * `platform/frameworks/support` is enormous; a plain clone would cost every CI run minutes and
 * gigabytes for two directories. This uses a **blobless sparse clone** instead:
 *
 *     git clone --filter=blob:none --no-checkout --depth 1 <repo> <cache>
 *     git sparse-checkout set <paths…>
 *     git checkout <ref>
 *
 * Only the trees, and only the blobs under the sparse paths, are ever transferred — **13 MB** for
 * the two sample subtrees, measured. It needs no API token and no directory listing, which is what
 * makes it work identically on a CI runner and a laptop.
 *
 * `ref` is a **commit SHA**, never a branch. A branch would make the published catalog
 * irreproducible and turn an upstream bump into an invisible event rather than a reviewable diff.
 *
 * ## One manifest, several libraries
 *
 * `samples/import.json` holds a LIST of libraries, each with its own `ref`, `paths` and artifact
 * version. That is not tidiness: a sample tree belongs to the artifact whose KDoc points at it, and
 * those artifacts move independently — adaptive-layout 1.3.0-beta02 was published three weeks
 * before material3 1.5.0-alpha22. Vendoring the adaptive samples at material3's commit compiled
 * them against an `AnimatedPane(shape = …)` overload Compose Multiplatform does not ship.
 *
 * The libraries' files land in ONE flat directory, so a file name may not repeat across them — the
 * vendor step fails rather than letting one overwrite the other.
 *
 * ## Small fixes are patches, never edits
 *
 * The vendored tree stays byte-identical to upstream except for the patches in `samples/patches/`,
 * each applied here after the copy and each carrying its reason in its own header. That is the whole
 * of "make small fixes when needed": a fix is a patch with a stated reason, so the next import
 * re-applies it and a patch that stops applying FAILS rather than silently reverting the fix.
 *
 * A sample that cannot be imported at all — it needs an Activity, a permission, a real `Context` —
 * is listed in `samples/quarantine.json` with a reason and skipped. `--check` fails when a
 * quarantined sample becomes importable again, so the gap cannot rot into a permanent exclusion
 * nobody revisits. Same declared-and-checked-gap contract `kit-unauthorable.json` carries here.
 *
 * Vendored sources must NOT be reformatted: they are upstream's bytes, and ktfmt would rewrite them
 * into a diff against every future import. The module that compiles them excludes this directory
 * from formatting.
 *
 *     node scripts/import-samples.mjs --out <dir>     # vendor into <dir>
 *     node scripts/import-samples.mjs --check         # re-import and diff against the committed tree
 */

import { execFileSync } from "node:child_process";
import {
  cpSync,
  existsSync,
  mkdirSync,
  mkdtempSync,
  readdirSync,
  readFileSync,
  rmSync,
  writeFileSync,
} from "node:fs";
import { tmpdir } from "node:os";
import { basename, dirname, join, resolve } from "node:path";

const MANIFEST = "samples/import.json";
const PATCH_DIR = "samples/patches";
const QUARANTINE = "samples/quarantine.json";

const run = (cmd, args, opts = {}) =>
  execFileSync(cmd, args, { encoding: "utf8", stdio: ["ignore", "pipe", "pipe"], ...opts });

/**
 * Fetch one library's subtrees at ITS pinned ref into [cache], reusing an existing checkout when it
 * already sits on that exact commit — an import is then a no-op rather than a re-download.
 *
 * Per library, not per manifest, because each library pins the commit matching the artifact version
 * it is compared against, and those cadences are independent: adaptive-layout 1.3.0-beta02 and
 * material3 1.5.0-alpha22 were published three weeks apart. Each ref therefore gets its own cache
 * directory; two libraries sharing a ref share the checkout.
 */
export function fetchUpstream(repo, library, cache) {
  const atRef =
    existsSync(join(cache, ".git")) &&
    (() => {
      try {
        return run("git", ["-C", cache, "rev-parse", "HEAD"]).trim() === library.ref;
      } catch {
        return false;
      }
    })();

  if (!atRef) {
    rmSync(cache, { recursive: true, force: true });
    mkdirSync(dirname(cache), { recursive: true });
    run("git", ["clone", "--filter=blob:none", "--no-checkout", "--depth", "1", repo, cache]);
    run("git", ["-C", cache, "sparse-checkout", "init", "--cone"]);
    run("git", ["-C", cache, "sparse-checkout", "set", ...library.paths]);
    // The pinned commit may not be the shallow tip, so fetch it by id before checking it out.
    try {
      run("git", ["-C", cache, "fetch", "--depth", "1", "origin", library.ref]);
    } catch {
      // A ref already present in the shallow pack needs no fetch; checkout below is the real test.
    }
    run("git", ["-C", cache, "checkout", library.ref]);
  }
  return cache;
}

/** The quarantined sample file names, mapped to their stated reasons. */
export function quarantined(path = QUARANTINE) {
  if (!existsSync(path)) return new Map();
  const parsed = JSON.parse(readFileSync(path, "utf8"));
  return new Map(
    (parsed.samples ?? []).map((entry) => [entry.file, entry.reason ?? "(no reason given)"]),
  );
}

/**
 * Copy every `.kt` file under the manifest's paths into [out], skipping quarantined ones.
 *
 * RECURSIVE, and that is not incidental. This copy was flat until the Wear import proved it a bug:
 * `androidx.wear.compose.material3.samples` keeps its shared glyphs in a `samples/icons/`
 * subpackage, and a flat copy left it behind in silence — 24 compile errors pointing at a
 * directory nobody had noticed was missing. A sample tree is a PACKAGE, not a directory of files,
 * so the whole package travels.
 *
 * `androidx.compose.material3.samples` is flat today, so this changes nothing here right now —
 * verified against the pinned tree rather than assumed. It is ported so that the day upstream adds
 * a subpackage, this import does not quietly thin the catalog instead of failing.
 *
 * Quarantine matches on the file's name, not its path, because that is the unit a reader names in
 * `samples/quarantine.json` and sample file names are unique within a corpus.
 */
export function vendor(cache, library, out, skip = new Map(), clear = true) {
  if (clear) rmSync(out, { recursive: true, force: true });
  mkdirSync(out, { recursive: true });
  const copied = [];
  const skipped = [];
  const walk = (from, relative) => {
    for (const entry of readdirSync(from, { withFileTypes: true })) {
      const source = join(from, entry.name);
      const target = relative ? `${relative}/${entry.name}` : entry.name;
      if (entry.isDirectory()) {
        walk(source, target);
        continue;
      }
      if (!entry.isFile() || !entry.name.endsWith(".kt")) continue;
      if (skip.has(entry.name)) {
        skipped.push(entry.name);
        continue;
      }
      // Two libraries' trees land in ONE flat vendored directory, so a shared file name would have
      // one silently overwrite the other — and `samples/quarantine.json` keys on the bare name,
      // which assumes the same uniqueness. Fail instead of picking a winner.
      if (!clear && existsSync(join(out, target))) {
        throw new Error(
          `${target} is vendored by more than one library in samples/import.json. File names must ` +
            `be unique across the corpus: quarantine keys on the name, and the vendored tree is flat.`,
        );
      }
      mkdirSync(dirname(join(out, target)), { recursive: true });
      cpSync(source, join(out, target));
      copied.push(target);
    }
  };
  for (const path of library.paths) walk(join(cache, path), "");
  return { copied: copied.sort(), skipped: skipped.sort() };
}

/**
 * Copy the manifest's `resourcePaths` verbatim into [out] — the Android resource directory the
 * samples resolve `R.drawable.*` against.
 *
 * Only meaningful for an ANDROID samples module, which THIS repo's is not: `:samples-catalog`
 * renders through Compose Multiplatform desktop, where no `R` class is generated for any resources
 * at all. `material3/samples` does carry a `res/` (five carousel JPEGs and a `strings.xml`), and
 * vendoring it would still not compile `CarouselSamples.kt` — which is why that file is
 * quarantined rather than fixed by copying them, and why `resourcePaths` is absent from this
 * repo's manifest.
 *
 * The code is carried anyway so the two repos' importers stay one script with one behaviour; an
 * absent `resourcePaths` is a no-op.
 *
 * Returns the number of files copied; absent `resourcePaths` is a no-op, so the phone repo's
 * manifest shape stays valid against this same script.
 */
export function vendorResources(cache, library, out, clear = true) {
  const paths = library.resourcePaths ?? [];
  if (paths.length === 0) return 0;
  if (clear) rmSync(out, { recursive: true, force: true });
  let copied = 0;
  for (const path of paths) {
    const from = join(cache, path);
    if (!existsSync(from)) continue;
    cpSync(from, out, { recursive: true });
    const count = (dir) =>
      readdirSync(dir, { withFileTypes: true }).reduce(
        (n, e) => n + (e.isDirectory() ? count(join(dir, e.name)) : 1),
        0,
      );
    copied += count(from);
  }
  return copied;
}

/**
 * Apply every patch in `samples/patches/`, in name order.
 *
 * A patch that does not apply is an ERROR and not a warning: it means upstream changed under a fix
 * whose reason may or may not still hold, and the one thing that must not happen is the fix quietly
 * disappearing from the vendored tree while the patch file sits there looking authoritative.
 */
export function applyPatches(out, dir = PATCH_DIR) {
  if (!existsSync(dir)) return [];
  const patches = readdirSync(dir)
    .filter((name) => name.endsWith(".patch"))
    .sort();
  for (const patch of patches) {
    try {
      // Applied with `out` as the working directory rather than via `--directory`, because `out` is
      // a temp directory under `--check` and git refuses a `--directory` path outside the work tree
      // ("invalid path"). The patch paths are therefore repo-relative to the vendored tree itself.
      run("git", ["apply", "-p1", resolve(dir, patch)], { cwd: out });
    } catch (error) {
      const detail = error.stderr?.toString().trim() || error.message;
      throw new Error(
        `patch ${patch} does not apply to the freshly imported tree:\n${detail}\n\n` +
          `Upstream has moved under it. Re-cut the patch against ${out}, or drop it and say why in ` +
          `the commit — never leave a patch that cannot apply, because the fix it carries is then ` +
          `silently absent from the vendored sources.`,
      );
    }
  }
  return patches;
}

/**
 * Record where the bytes came from, beside them.
 *
 * One entry per library rather than one ref for the tree, because the libraries are pinned
 * independently: a reader tracing a render back to a commit needs to know WHICH commit that
 * particular sample came from, and "the import's ref" stopped being a single answer when the
 * manifest grew a second library.
 */
export function writeProvenance(out, manifest, result, patches) {
  const provenance = {
    $comment:
      "GENERATED by scripts/import-samples.mjs — do not edit. Records exactly which upstream " +
      "commit these vendored sources came from, so a published render can be traced back to it.",
    repo: manifest.repo,
    libraries: manifest.libraries.map((library) => ({
      name: library.name,
      ref: library.ref,
      paths: library.paths,
      artifact: `${library.artifactCoordinates}:${library.artifactVersion}`,
      files: (result.byLibrary?.[library.name] ?? []).length,
    })),
    files: result.copied.length,
    quarantined: result.skipped,
    patches,
  };
  writeFileSync(join(out, "PROVENANCE.json"), `${JSON.stringify(provenance, null, 2)}\n`);
}

function main(argv) {
  const args = new Map();
  for (let i = 0; i < argv.length; i += 1) {
    if (argv[i].startsWith("--")) args.set(argv[i].slice(2), argv[i + 1]);
  }
  const manifest = JSON.parse(readFileSync(MANIFEST, "utf8"));
  const check = argv.includes("--check");
  const committed = args.get("out") ?? "samples-catalog/src/main/kotlin/upstream";
  const cacheBase = args.get("cache") ?? join(tmpdir(), "androidx-samples");

  const skip = quarantined();
  const out = check ? mkdtempSync(join(tmpdir(), "samples-import-")) : committed;
  const resourcesOut = args.get("res") ?? "samples-catalog/src/main/res";
  const result = { copied: [], skipped: [], byLibrary: {} };
  let resources = 0;
  // The destination is cleared by the FIRST library and appended to by the rest, so a sample
  // deleted upstream still disappears here rather than lingering.
  manifest.libraries.forEach((library, index) => {
    const cache = join(cacheBase, basename(library.ref));
    console.log(`Fetching ${manifest.repo} at ${library.ref.slice(0, 12)} for ${library.name} …`);
    fetchUpstream(manifest.repo, library, cache);
    const vendored = vendor(cache, library, out, skip, index === 0);
    result.copied.push(...vendored.copied);
    result.skipped.push(...vendored.skipped);
    result.byLibrary[library.name] = vendored.copied;
    if (!check) resources += vendorResources(cache, library, resourcesOut, index === 0);
  });
  result.copied.sort();
  result.skipped.sort();
  let patches;
  try {
    patches = applyPatches(out);
  } catch (error) {
    // The message already says which patch and what to do about it; a stack trace on top only
    // buries it, and this is a failure a human has to read and act on.
    console.error(`\n${error.message}`);
    if (check) rmSync(out, { recursive: true, force: true });
    process.exit(1);
  }
  writeProvenance(out, manifest, result, patches);

  console.log(
    `  ${result.copied.length} file(s) vendored, ${result.skipped.length} quarantined, ` +
      `${patches.length} patch(es) applied` +
      (resources > 0 ? `, ${resources} resource(s) copied.` : "."),
  );
  for (const name of result.skipped) console.log(`    quarantined: ${name} — ${skip.get(name)}`);

  if (check) {
    const diff = (() => {
      try {
        run("diff", ["-ru", committed, out]);
        return "";
      } catch (error) {
        return error.stdout?.toString() ?? "differs";
      }
    })();
    rmSync(out, { recursive: true, force: true });
    if (diff) {
      console.error("\nThe committed sources differ from a fresh import at the pinned ref:\n");
      console.error(diff.slice(0, 4000));
      console.error("Re-run `node scripts/import-samples.mjs` and commit the result.");
      process.exit(1);
    }
    console.log("The vendored sources match a fresh import at the pinned ref.");
  }
}

if (process.argv[1] && process.argv[1].endsWith("import-samples.mjs")) main(process.argv.slice(2));
