#!/usr/bin/env node
/**
 * Generate a `@Preview` wrapper for every vendored sample that does not carry one upstream.
 *
 * ## Why this exists, when `docs/design/ANDROIDX_SAMPLES.md` says it does not
 *
 * That document records that this repository generates **no** wrappers, because 298 of the 317
 * `@Sampled` functions in `androidx.compose.material3.samples` already carry `@Preview` — and calls
 * that "a difference from the Wear repo rather than an omission". It stops being true one tier
 * down: `androidx.compose.foundation` ships a preview annotation on **7 of its 73 sample files**,
 * and `ui`, `ui-text` and `animation` ship none at all (issue #346). Without wrappers
 * `:ui-samples-catalog` would publish almost nothing.
 *
 * So the finding was right for one corpus and wrong for the other, and the difference is upstream's
 * own habit rather than anything about the libraries. This is `yschimke/wear-m3-catalog`'s
 * generator ported rather than a second one written — same classification, same refusals — with the
 * two things that repository's copy hard-codes made arguments: the corpus paths, and the upstream
 * package, which is read per FILE here because this corpus spans two of them
 * (`androidx.compose.foundation.samples` and `androidx.compose.foundation.layout.samples`) and will
 * span five when `ui`, `ui-text` and `animation` land.
 *
 * ## What it generates, and what it refuses to
 *
 * One wrapper per sample, in this repository's own package and its own generated file — never
 * inside the vendored tree, which stays byte-identical to upstream:
 *
 * ```kotlin
 * @Preview @Composable fun ColumnSamplePreview() = androidx.compose.foundation.layout.samples.ColumnSample()
 * ```
 *
 * A sample is wrapped only when it is a **zero-argument `@Composable`**. Anything else is skipped
 * and reported:
 *
 *  - a `@Sampled` function taking parameters is a helper a sample calls, not a call site anyone can
 *    render on its own;
 *  - a sample that already carries a preview annotation is left alone, or discovery would find the
 *    same composable twice and publish it twice;
 *  - a sample named in the corpus' `quarantine.json` `previews` list compiles but cannot RUN here,
 *    and a wrapper for it would fail the whole render job rather than just itself.
 *
 * Refusing rather than guessing matters: a wrapper that does not compile fails the module, and a
 * wrapper that renders something meaningless is worse than a missing card.
 *
 *     node scripts/samples-previews.mjs            # regenerate
 *     node scripts/samples-previews.mjs --check    # fail if the generated file is stale
 */

import { existsSync, readdirSync, readFileSync, writeFileSync } from "node:fs";
import { join } from "node:path";

/** `:ui-samples-catalog`'s paths. Every one is overridable, the way the importer's are. */
export const VENDORED = "ui-samples-catalog/src/main/kotlin/upstream";
export const QUARANTINE = "ui-samples-catalog/quarantine.json";
export const PACKAGE = "ee.schimke.uisamplescatalog";
export const OUT = `ui-samples-catalog/src/main/kotlin/${PACKAGE.replace(/\./g, "/")}/SamplePreviews.kt`;

/** Every `.kt` file under [dir], recursively — the vendored tree keeps upstream's package dirs. */
export function kotlinSources(dir) {
  const out = [];
  for (const entry of readdirSync(dir, { withFileTypes: true })) {
    const path = join(dir, entry.name);
    if (entry.isDirectory()) out.push(...kotlinSources(path));
    else if (entry.name.endsWith(".kt")) out.push(path);
  }
  return out.sort();
}

/**
 * The sample function names the corpus' quarantine declares unrenderable, mapped to their reasons.
 *
 * The per-FILE `samples` list is the importer's unit and is not read here: a file that does not
 * compile never reaches the vendored tree at all. This is the other failure mode — a sample that
 * compiles and then throws at composition, where taking out its whole file would drop the siblings
 * that render perfectly well.
 */
export function quarantinedPreviews(path = QUARANTINE) {
  if (!existsSync(path)) return new Map();
  const parsed = JSON.parse(readFileSync(path, "utf8"));
  return new Map(
    (parsed.previews ?? []).map((entry) => [entry.sample, entry.reason ?? "(no reason given)"]),
  );
}

/**
 * Classify every `@Sampled` function in the vendored tree.
 *
 * A wrapped sample is `{ fn, package }`: the package is read from the file that declares it rather
 * than assumed, because one corpus spans several. Getting that wrong does not fail quietly — an
 * unresolved call fails the module — but reading it is also simply cheaper than maintaining a table.
 *
 * @returns {{wrap: Sample[], hasPreview: Sample[], takesArguments: string[], quarantined: string[]}}
 *   where a `Sample` is `{ fn, package, file }` — `file` is the vendored path, which
 *   `scripts/ui-samples-spec.mjs` groups the published inventory by.
 */
export function classifySamples(dir = VENDORED, skip = quarantinedPreviews()) {
  const wrap = new Map();
  const hasPreview = new Map();
  const takesArguments = [];
  const quarantined = [];
  for (const file of kotlinSources(dir)) {
    const text = readFileSync(file, "utf8");
    const declaredPackage = text.match(/^package\s+([\w.]+)/m)?.[1];
    if (!declaredPackage) continue;
    // Annotations immediately preceding a `fun`, then its parameter list. The compiler's own rule,
    // so a commented-out sample cannot slip in and a KDoc `@sample` cannot be mistaken for one.
    for (const match of text.matchAll(/((?:@\w+(?:\([^)]*\))?\s*)+)fun\s+(\w+)\s*\(([^)]*)\)/g)) {
      const [, annotations, fn, parameters] = match;
      if (!annotations.includes("@Sampled")) continue;
      if (!annotations.includes("@Composable")) continue;
      if (annotations.includes("@Preview")) {
        hasPreview.set(fn, { fn, package: declaredPackage, file });
        continue;
      }
      if (skip.has(fn)) {
        quarantined.push(fn);
        continue;
      }
      if (parameters.trim() !== "") {
        takesArguments.push(fn);
        continue;
      }
      wrap.set(fn, { fn, package: declaredPackage, file });
    }
  }
  return {
    wrap: [...wrap.values()].sort((a, b) => a.fn.localeCompare(b.fn)),
    hasPreview: [...hasPreview.values()].sort((a, b) => a.fn.localeCompare(b.fn)),
    takesArguments: [...new Set(takesArguments)].sort(),
    quarantined: [...new Set(quarantined)].sort(),
  };
}

/**
 * The generated Kotlin source for [samples].
 *
 * Fully qualified call sites rather than imports: two of upstream's sample packages declare
 * functions of the same name (`SpacerSample` exists in both foundation trees at other refs), and a
 * generated file that has to resolve an import collision is a generator that has to understand
 * Kotlin. A qualified call never can.
 *
 * No `device =` and no background: a sample is a picture of a call site and measures itself, and
 * pinning one to a screen would frame it in a canvas it does not fill — the mistake
 * `glimmer-catalog`'s own `AI_GLASSES_DEVICE_SPEC` note records. The Wear generator this is ported
 * from needs a device for its full-screen samples; nothing here fills a phone screen by design.
 */
export function renderPreviews(samples) {
  const header = `// GENERATED by scripts/samples-previews.mjs — do not edit.
//
// A \`@Preview\` wrapper for every vendored foundation sample that does not carry one upstream. Only
// 7 of the 73 sample files in \`androidx.compose.foundation.samples\` and
// \`androidx.compose.foundation.layout.samples\` are annotated, so without these the catalog would
// publish a tenth of the corpus. \`:samples-catalog\` needs no equivalent: 298 of its 317 material3
// samples are already annotated.
//
// The wrappers live HERE, in this module's own package, and never inside \`upstream/\` — that tree is
// upstream's bytes and is re-fetched byte-identically on every import.

package ${PACKAGE}

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
`;
  const body = samples
    .map(({ fn, package: pkg }) => `\n@Preview\n@Composable\nfun ${fn}Preview() = ${pkg}.${fn}()\n`)
    .join("");
  return header + body;
}

function main(argv) {
  const skip = quarantinedPreviews();
  const { wrap, hasPreview, takesArguments, quarantined } = classifySamples(VENDORED, skip);
  const source = renderPreviews(wrap);

  if (argv.includes("--check")) {
    if (readFileSync(OUT, "utf8") !== source) {
      console.error(`${OUT} is stale: regenerate it with \`node scripts/samples-previews.mjs\`.`);
      process.exit(1);
    }
    console.log(`${OUT} is current (${wrap.length} wrapper(s)).`);
    return;
  }

  writeFileSync(OUT, source);
  console.log(`${OUT}: ${wrap.length} wrapper(s) generated.`);
  console.log(`  ${hasPreview.length} sample(s) already carry @Preview upstream and are left alone.`);
  for (const fn of quarantined) console.log(`  quarantined: ${fn} — ${skip.get(fn)}`);
  if (takesArguments.length > 0) {
    console.log(
      `  ${takesArguments.length} @Sampled function(s) take arguments and are not renderable on ` +
        `their own: ${takesArguments.slice(0, 6).join(", ")}${takesArguments.length > 6 ? " …" : ""}`,
    );
  }
}

if (process.argv[1] && process.argv[1].endsWith("samples-previews.mjs")) main(process.argv.slice(2));
