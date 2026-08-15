#!/usr/bin/env bash
# Regenerate design-map.json and its variant sidecar from the @CatalogComponent annotations.
#
#   scripts/design-map.sh [--check]
#
# Two steps, in two repositories, because the question splits there:
#
#   1. compose-ai-tools' `emit-design-map.mjs` knows what the ANNOTATIONS mean — it defines
#      @CatalogComponent / @CatalogVariant / @OverrideVariant and writes them into previews.json.
#      It emits base references, plus a sidecar declaring which other previews are the same
#      component with knobs turned.
#   2. @design-parity/kit-index knows what the KIT means — `size=l` is a fact about a Compose API
#      and `Size=Large` is a fact about the Material 3 kit. It resolves those declarations against
#      the committed figma-kit-index.json into tagged ref/previewId pairs.
#
# Neither step is this repo's business to implement; both used to live in scripts/ and drifted from
# the annotations they project. What stays here is this wrapper and the two committed outputs.
#
# WHY THE FETCH. Step 2 is on npm, so npx reaches it. Step 1 ships in the compose-ai-tools repo
# rather than as a package, and the design-parity reusable workflow runs `design-map-command`
# inside a checkout of THIS repo alone — so there is nothing to `node` unless we go and get it.
# Two files, no dependencies (the module is deliberately dependency-free), floating on main to
# match how ci.yml already takes that repo at `ref: main`.
#
# WHY STEP 2 IS PINNED AND STEP 1 IS NOT. Both outputs are COMMITTED and CI fails on any
# difference, so the resolver's version is an input to a checked-in artifact: float it, and the
# next release upstream turns this repo red for a change nobody here made. That is not
# hypothetical — a floating `npx` resolved 0.1.49 against a map built with 0.1.50's slug matching
# and produced 439 variant references across 58 components where the committed map has 442 across
# 59, which is what CI reported as "stale". Pinning makes the bump a commit that regenerates the
# map in the same diff. Step 1 needs no pin because it only reads
# annotations this repo owns; a change there shows up as an annotation change, not as drift.
#
# WHY IT STAGES. Both steps run into a scratch directory and only the finished pair is copied into
# place. Step 1's map is an INTERMEDIATE — base references with the variants still unresolved — so
# a run that wrote it directly and then failed in step 2 would leave the repo holding a map that
# looks complete and silently compares 442 fewer nodes. Staging also gives `--check` something
# honest to diff: the committed files against the finished output, rather than against a halfway
# artifact that never matches.
set -euo pipefail

CHECK=""
[ "${1:-}" = "--check" ] && CHECK=1

TOOL_DIR="${RUNNER_TEMP:-/tmp}/design-map-tool"
RAW="https://raw.githubusercontent.com/yschimke/compose-ai-tools/main/scripts/design-artifacts"

mkdir -p "$TOOL_DIR"
for f in design-map.mjs emit-design-map.mjs; do
  curl -fsSL --retry 3 --retry-delay 2 -o "$TOOL_DIR/$f" "$RAW/$f"
done

WORK="$(mktemp -d)"
trap 'rm -rf "$WORK"' EXIT

# --strict: this catalog reproduces the kit, so a component with no exact kit node does not belong
# in the published inventory at all (#10). Gated before anything is written, so a failed run leaves
# the committed map intact rather than replacing it with one CI would report as merely stale.
node "$TOOL_DIR/emit-design-map.mjs" \
  --previews catalog/build/compose-previews/previews.json \
  --out "$WORK/design-map.json" \
  --variants "$WORK/design-map-variants.json" \
  --strict

npx --yes @design-parity/kit-index@0.1.50 resolve \
  --map "$WORK/design-map.json" \
  --variants "$WORK/design-map-variants.json" \
  --index figma-kit-index.json \
  --out "$WORK/design-map.json"

# A component that declares no variant axis writes no sidecar; an empty file would assert only that
# it has nothing to say. Reconcile the absence too, so a catalog that loses its last axis does not
# keep a stale one committed.
for f in design-map.json design-map-variants.json; do
  if [ -n "$CHECK" ]; then
    if [ -f "$WORK/$f" ]; then
      diff -q "$WORK/$f" "$f" >/dev/null 2>&1 || {
        echo "::error::$f is out of date — regenerate with scripts/design-map.sh"
        exit 1
      }
    elif [ -f "$f" ]; then
      echo "::error::$f is stale and should be removed — regenerate with scripts/design-map.sh"
      exit 1
    fi
  elif [ -f "$WORK/$f" ]; then
    cp "$WORK/$f" "$f"
  else
    rm -f "$f"
  fi
done

[ -n "$CHECK" ] && echo "✓ design-map.json and its sidecar match the annotations."
exit 0
