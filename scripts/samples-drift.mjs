#!/usr/bin/env node
/**
 * Prove the AndroidX samples pin still matches what this catalog compiles against.
 *
 * This repo's `:catalog` renders through Compose Multiplatform
 * (`org.jetbrains.compose.material3:material3`), while the samples are written against AndroidX
 * (`androidx.compose.material3:material3`). `docs/design/ANDROIDX_SAMPLES.md` records the decision
 * that follows: import the AndroidX samples at the version whose API matches what CMP ships, rather
 * than adding an `androidMain` source set on the latest Android-only ones.
 *
 * That decision is only safe if the match is *checked* rather than asserted once and forgotten —
 * which is what this does.
 *
 * ## The fingerprint
 *
 * Both artifacts publish sources jars, and both carry the `@sample` KDoc. The set of sample FQNs a
 * version references is therefore a cheap, exact version fingerprint: no API diffing, no bytecode,
 * one HTTP fetch each. Measured when the pin was chosen:
 *
 *     AndroidX material3   shared with CMP 1.12.0-alpha03   only in CMP   only in AndroidX
 *     1.4.0                178                              130           0
 *     1.5.0-alpha15        297                              11            3
 *     1.5.0-alpha20        307                              1             1
 *     1.5.0-alpha22        308                              0             0      <- the pin
 *     1.5.0-alpha27        297                              11            22
 *
 * The historic version IS the current one, so the pin costs nothing.
 *
 * ## What a failure means
 *
 * A difference means **CMP has moved**, not that AndroidX has. AndroidX moves most weeks and that
 * is none of this repo's business; CMP moving is exactly when the samples need to move with it. So
 * this fires on the signal that matters and stays quiet otherwise — and when it fires it searches
 * the AndroidX line for the version that does match, and names it, rather than leaving a human to
 * bisect sources jars by hand.
 *
 * Set equality of `@sample` FQNs is a fingerprint, not proof that two trees are identical. It is the
 * right tool for CHOOSING the ref cheaply. The real gate is downstream and much stronger: a
 * generated wrapper calling a sample that does not resolve against the CMP artifact does not
 * compile.
 *
 *     node scripts/samples-drift.mjs            # check the pin
 *     node scripts/samples-drift.mjs --search   # also scan the AndroidX line for a better match
 */

import { execFileSync } from "node:child_process";
import { mkdtempSync, readFileSync, rmSync } from "node:fs";
import { tmpdir } from "node:os";
import { join } from "node:path";

const GOOGLE_MAVEN = "https://dl.google.com/dl/android/maven2";
const MAVEN_CENTRAL = "https://repo1.maven.org/maven2";

/** `group:artifact` -> the repository path segment. */
function groupPath(coordinates) {
  const [group, artifact] = coordinates.split(":");
  return `${group.replaceAll(".", "/")}/${artifact}`;
}

/** Download a sources jar and return the set of `@sample` FQNs it references. */
export function sampleSet(baseUrl, coordinates, version) {
  const [, artifact] = coordinates.split(":");
  const url = `${baseUrl}/${groupPath(coordinates)}/${version}/${artifact}-${version}-sources.jar`;
  const dir = mkdtempSync(join(tmpdir(), "samples-drift-"));
  try {
    const jar = join(dir, "sources.jar");
    execFileSync("curl", ["-sSf", "--max-time", "180", "-o", jar, url], { stdio: "pipe" });
    execFileSync("unzip", ["-q", jar, "-d", join(dir, "src")]);
    const grep = execFileSync(
      "bash",
      ["-c", `grep -rho '@sample [A-Za-z0-9_.]*' ${JSON.stringify(join(dir, "src"))} || true`],
      { encoding: "utf8" },
    );
    return new Set(
      grep
        .split("\n")
        .filter(Boolean)
        .map((line) => line.replace(/^@sample\s+/, "").trim()),
    );
  } finally {
    rmSync(dir, { recursive: true, force: true });
  }
}

/** Every published version of an artifact, oldest first. */
export function publishedVersions(baseUrl, coordinates) {
  const url = `${baseUrl}/${groupPath(coordinates)}/maven-metadata.xml`;
  const xml = execFileSync("curl", ["-sSf", "--max-time", "60", url], { encoding: "utf8" });
  return [...xml.matchAll(/<version>([^<]+)<\/version>/g)].map((m) => m[1]);
}

/** `{shared, onlyLeft, onlyRight}` for two sets. */
export function compare(left, right) {
  const shared = [...left].filter((x) => right.has(x));
  return {
    shared: shared.length,
    onlyLeft: [...left].filter((x) => !right.has(x)).sort(),
    onlyRight: [...right].filter((x) => !left.has(x)).sort(),
  };
}

function main(argv) {
  const pin = JSON.parse(readFileSync("samples/import.json", "utf8"));
  const stale = [];

  // Each library is its own comparison: `material3` tracks the CMP material3 artifact and
  // `material3-adaptive` the CMP adaptive line, which move on independent cadences. Every library
  // is checked before anything fails, so one stale pin does not hide another.
  for (const library of pin.libraries) {
    const cmpVersion = cmpVersionFromCatalog(
      "gradle/libs.versions.toml",
      library.composeMultiplatformVersionRef,
    );
    console.log(
      `Comparing ${library.composeMultiplatformCoordinates}:${cmpVersion} against ` +
        `${library.artifactCoordinates}:${library.artifactVersion} …`,
    );
    const cmp = sampleSet(MAVEN_CENTRAL, library.composeMultiplatformCoordinates, cmpVersion);
    const androidx = sampleSet(GOOGLE_MAVEN, library.artifactCoordinates, library.artifactVersion);
    const { shared, onlyLeft, onlyRight } = compare(cmp, androidx);

    console.log(
      `  shared ${shared}, only in CMP ${onlyLeft.length}, only in AndroidX ${onlyRight.length}`,
    );
    if (onlyLeft.length === 0 && onlyRight.length === 0) {
      console.log(`  ${library.name}: ${library.artifactVersion} is the AndroidX version CMP ships.`);
      continue;
    }
    stale.push({ library, cmp, onlyLeft, onlyRight });
  }

  if (stale.length === 0) {
    console.log(`The pin matches: ${pin.libraries.length} library(ies), no drift.`);
    return;
  }

  console.error("");
  console.error("The samples pin no longer matches the Compose Multiplatform artifacts.");
  console.error("This means CMP has moved — AndroidX moving on its own never fails this check.");
  for (const { library, onlyLeft, onlyRight } of stale) {
    console.error(`  ${library.name} (${library.artifactCoordinates}:${library.artifactVersion})`);
    if (onlyLeft.length > 0) console.error(`    only in CMP:      ${onlyLeft.join(", ")}`);
    if (onlyRight.length > 0) console.error(`    only in AndroidX: ${onlyRight.join(", ")}`);
  }

  if (argv.includes("--search")) {
    for (const { library, cmp } of stale) {
      console.error("");
      console.error(`Searching the ${library.artifactCoordinates} line for a version that matches …`);
      const candidates = publishedVersions(GOOGLE_MAVEN, library.artifactCoordinates).reverse();
      let found = false;
      for (const version of candidates.slice(0, 30)) {
        let set;
        try {
          set = sampleSet(GOOGLE_MAVEN, library.artifactCoordinates, version);
        } catch {
          continue; // no sources jar published for that version
        }
        const result = compare(cmp, set);
        if (result.onlyLeft.length === 0 && result.onlyRight.length === 0) {
          console.error(
            `  MATCH: ${version} — set \`artifactVersion\` on \`${library.name}\` to it, move its ` +
              `\`ref\` to the commit of that version's publish date, and re-run the import.`,
          );
          found = true;
          break;
        }
      }
      if (!found) {
        console.error("  no exact match on the published line; pick the nearest and record why.");
      }
    }
  } else {
    console.error("");
    console.error("Re-run with --search to find the AndroidX versions that do match.");
  }
  process.exit(1);
}

/**
 * A CMP version this repo compiles against, read from the version catalog.
 *
 * The key is the library's own `composeMultiplatformVersionRef`, because "the CMP version" stopped
 * being one number when the manifest grew a second library: material3 tracks
 * `compose-multiplatform-material3` and the adaptive samples track `compose-adaptive`.
 */
export function cmpVersionFromCatalog(path = "gradle/libs.versions.toml", key) {
  const toml = readFileSync(path, "utf8");
  const match = toml.match(new RegExp(`^\\s*${key}\\s*=\\s*"([^"]+)"`, "m"));
  if (!match) throw new Error(`no \`${key}\` in ${path}`);
  return match[1];
}

if (process.argv[1] && process.argv[1].endsWith("samples-drift.mjs")) main(process.argv.slice(2));
