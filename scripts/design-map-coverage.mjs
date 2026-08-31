#!/usr/bin/env node

import { readFileSync } from "node:fs";
import { pathToFileURL } from "node:url";

const SCHEMA = "compose-preview-design-map-coverage/v1";

function previewIds(component) {
  if (typeof component.previewId === "string") return [component.previewId];
  return component.previewId.map((entry) => entry.previewId);
}

export function measureCoverage(designMap, variants) {
  const mapped = new Set(designMap.components.flatMap(previewIds));
  const renders = variants.components.flatMap((component) => component.renders);
  const resolved = renders.filter((render) => mapped.has(render.previewId)).length;
  return {
    declared: renders.length,
    resolved,
    unresolved: renders.length - resolved,
  };
}

function validateCounts(label, counts) {
  for (const field of ["declared", "resolved", "unresolved"]) {
    if (!Number.isSafeInteger(counts[field]) || counts[field] < 0) {
      throw new Error(`${label}.${field} must be a non-negative integer`);
    }
  }
  if (counts.resolved + counts.unresolved !== counts.declared) {
    throw new Error(`${label} must satisfy resolved + unresolved = declared`);
  }
}

export function compareCoverage(current, baseline) {
  validateCounts("current", current);
  validateCounts("baseline", baseline);

  const regressions = [];
  const improvements = [];
  const ratioDelta =
    current.unresolved * baseline.declared - baseline.unresolved * current.declared;

  if (current.unresolved > baseline.unresolved) {
    regressions.push(
      `unresolved variants rose from ${baseline.unresolved} to ${current.unresolved}`,
    );
  } else if (current.unresolved < baseline.unresolved) {
    improvements.push(
      `unresolved variants fell from ${baseline.unresolved} to ${current.unresolved}`,
    );
  }

  if (ratioDelta > 0) {
    regressions.push(
      `unresolved share rose from ${formatPercent(baseline)} to ${formatPercent(current)}`,
    );
  } else if (ratioDelta < 0) {
    improvements.push(
      `unresolved share fell from ${formatPercent(baseline)} to ${formatPercent(current)}`,
    );
  }

  const changed = ["declared", "resolved", "unresolved"].some(
    (field) => current[field] !== baseline[field],
  );
  return { changed, regressions, improvements };
}

export function formatPercent(counts) {
  return counts.declared === 0 ? "0.0%" : `${((100 * counts.unresolved) / counts.declared).toFixed(1)}%`;
}

function readJson(path) {
  return JSON.parse(readFileSync(path, "utf8"));
}

function parseArgs(args) {
  const values = {};
  for (let index = 0; index < args.length; index += 2) {
    const flag = args[index];
    const value = args[index + 1];
    if (!["--map", "--variants", "--baseline"].includes(flag) || !value) {
      throw new Error(
        "usage: design-map-coverage.mjs --map MAP --variants VARIANTS --baseline BASELINE",
      );
    }
    values[flag.slice(2)] = value;
  }
  if (!values.map || !values.variants || !values.baseline) {
    throw new Error(
      "usage: design-map-coverage.mjs --map MAP --variants VARIANTS --baseline BASELINE",
    );
  }
  return values;
}

function run() {
  const paths = parseArgs(process.argv.slice(2));
  const baseline = readJson(paths.baseline);
  if (baseline.schema !== SCHEMA) {
    throw new Error(`${paths.baseline} has unsupported schema ${baseline.schema}`);
  }

  const current = measureCoverage(readJson(paths.map), readJson(paths.variants));
  const comparison = compareCoverage(current, baseline);
  const summary =
    `${current.resolved}/${current.declared} authored variants resolve; ` +
    `${current.unresolved} unresolved (${formatPercent(current)})`;

  if (!comparison.changed) {
    console.log(`✓ design-map coverage: ${summary}.`);
    return;
  }

  for (const regression of comparison.regressions) {
    console.error(`::error::Design-map coverage regressed: ${regression}.`);
  }
  for (const improvement of comparison.improvements) {
    console.error(`::error::Design-map coverage improved: ${improvement}.`);
  }
  if (comparison.regressions.length === 0 && comparison.improvements.length === 0) {
    console.error("::error::Design-map coverage counts changed without changing the unresolved share.");
  }
  console.error(`Current measurement: ${summary}.`);
  console.error(
    comparison.regressions.length > 0
      ? "Do not raise design-map-coverage.json without explaining the regression tracked by #101."
      : "Lower design-map-coverage.json to ratchet the improvement.",
  );
  process.exitCode = 1;
}

if (process.argv[1] && import.meta.url === pathToFileURL(process.argv[1]).href) {
  try {
    run();
  } catch (error) {
    console.error(`::error::${error.message}`);
    process.exitCode = 1;
  }
}
