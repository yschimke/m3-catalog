#!/usr/bin/env bash
# Regenerate glimmer-design-map.json from `:glimmer-catalog`'s @CatalogComponent annotations.
#
#   scripts/glimmer-design-map.sh [--check]
#
# The sibling of `scripts/design-map.sh`, and deliberately a SEPARATE script rather than a flag on
# that one: they resolve against different kits, and the kit is the thing that decides what a
# reference means.
#
# ## Two steps, as `design-map.sh` has always had
#
# The second step — `@design-parity/kit-index resolve` — used to be absent here because no index of
# the Glimmer kit existed. `glimmer-figma-kit-index.json` is that index now: 6 component sets, 55
# variants and 2 standalone components, READ FROM FIGMA rather than hand-written. Not by
# `@design-parity/kit-index` itself, which talks to api.figma.com and needs a token this repository
# holds no secret for, but through the Figma MCP server's read-only `get_metadata` — one call per
# component set, which `AGENTS.md`'s read-only rule allows and which needs no token at all.
#
# ## What resolves today: nothing, and that is information rather than a bug
#
# The step runs and reports 11 variants uncompared. The reason is worth stating, because "add the
# index and the variants resolve" was the expectation and it was wrong:
#
#  * The kit's variant names are MULTI-AXIS (`State=Enabled, Size=Large`), never the single axis a
#    `props = ["size=Large"]` declaration matches. `m3-catalog` resolves 1885 of 2005 the same way
#    every multi-axis kit must — `scripts/generate-exhaustive-kit-cells.mjs` emits a declaration per
#    kit cell naming every axis and the node id — not by matching one prop against one name.
#  * Most of these eleven have NO kit counterpart to find. `content=leading-icon` is a Compose slot;
#    the kit's Button set publishes `State=` x `Size=` and no content axis at all. `Card` and
#    `Title chip` are single symbols whose content differences are hidden LAYERS, so neither has a
#    variant to resolve against — which is why both sit in `standalone` rather than `sets`.
#
# Five of the eleven do have a counterpart and would resolve if declared as kit cells rather than as
# Compose props: Button `size=Large` -> `40000113:3576`, ToggleButton `state=checked` ->
# `40000113:4138`, IconToggleButton `state=checked` -> `40000113:4181`, VoiceInputIndicator
# `container=contained` -> `40000116:9338`, and ListItem `content=supporting-label`, which is the
# kit's `Type=2-line` (`384:4191`) under a Compose name. Doing that is a taxonomy change to the
# annotations rather than a change here.
#
# ## --prefix
#
# Discovery records `sourceFile` relative to its MODULE (`src/main/kotlin/…`), and the emitter
# prefixes it to make a repo-relative handle. Its default is `catalog`, which is right for the kit
# sheet and silently wrong here — it would publish handles naming `catalog/src/…/glimmer/Buttons.kt`,
# a path this repository does not contain.
set -euo pipefail

CHECK=""
[ "${1:-}" = "--check" ] && CHECK=1

WORK="$(mktemp -d)"
trap 'rm -rf "$WORK"' EXIT

npx --yes @yschimke/compose-design-map@1.55.0 \
  --previews glimmer-catalog/build/compose-previews/previews.json \
  --prefix glimmer-catalog \
  --out "$WORK/glimmer-design-map.json" \
  --variants "$WORK/glimmer-design-map-variants.json" \
  --strict

# Resolve variant declarations against the kit. Left in even while it resolves nothing: the run
# PRINTS what it could not compare and why, which is the only place that gap is visible, and the
# day a declaration starts naming a kit cell it resolves with no change to this script.
npx --yes @design-parity/kit-index@0.1.53 resolve \
  --map "$WORK/glimmer-design-map.json" \
  --variants "$WORK/glimmer-design-map-variants.json" \
  --index glimmer-figma-kit-index.json \
  --out "$WORK/glimmer-design-map.json"

for f in glimmer-design-map.json glimmer-design-map-variants.json; do
  if [ -n "$CHECK" ]; then
    if [ -f "$WORK/$f" ]; then
      diff -q "$WORK/$f" "$f" >/dev/null 2>&1 || {
        echo "::error::$f is out of date — regenerate with scripts/glimmer-design-map.sh"
        exit 1
      }
    elif [ -f "$f" ]; then
      echo "::error::$f is stale and should be removed — regenerate with scripts/glimmer-design-map.sh"
      exit 1
    fi
  elif [ -f "$WORK/$f" ]; then
    cp "$WORK/$f" "$f"
  else
    rm -f "$f"
  fi
done

[ -n "$CHECK" ] && echo "✓ glimmer-design-map.json matches the annotations."
exit 0
