#!/usr/bin/env bash
# Regenerate glimmer-design-map.json from `:glimmer-catalog`'s @CatalogComponent annotations.
#
#   scripts/glimmer-design-map.sh [--check]
#
# The sibling of `scripts/design-map.sh`, and deliberately a SEPARATE script rather than a flag on
# that one: they resolve against different kits, and the kit is the thing that decides what a
# reference means.
#
# ## One step here, two there
#
# `design-map.sh` runs `@yschimke/compose-design-map` and THEN `@design-parity/kit-index resolve`,
# which turns variant prop declarations (`size=l`) into the kit's own variant values (`Size=Large`)
# by looking them up in the committed `figma-kit-index.json`. There is no such index for the Glimmer
# kit yet, so that second step is absent and this map carries base component references only — the
# eight `@CatalogComponent(reference = …)` handles, with the eleven `@CatalogVariant`s unresolved.
#
# That is a real gap rather than a simplification, and it is the next piece of work: the kit DOES
# publish the axes the variants declare (`Size=Default | Large` on Button, `Toggle=False | True` on
# the toggles, `Type=1-line | 2-line | Card` on List Item, `Contained=Yes | No` on the mic
# indicator), so they are resolvable — indexing the kit needs a Figma token, which is a CI secret.
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
