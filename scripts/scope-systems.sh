#!/usr/bin/env bash
#
# Which design-artifacts delivery branches does a change set dirty?
#
# This repository's path→lane mapping, in the shape `scope-step.sh` in
# yschimke/compose-ai-tools consumes (`SCOPE_MAPPER`). That script is the whole Scope job,
# generic over the lane table; this file is the only part that is ours. Adopting it rather than
# keeping another copy of the job is what compose-ai-tools#5465 asks callers to do, and the reason
# is that the defect it fixed lived in the job's PREMISE — so a repository that keeps its own copy
# keeps the defect.
#
# Reads a newline-separated list of changed file paths on stdin and prints one
# `<system>=true|false` line per system, in the order of SYSTEMS.
#
# Usage:
#   printf '%s\n' "${changed[@]}" | scripts/scope-systems.sh
#   scripts/scope-systems.sh --all                      # every system, no stdin
#   scripts/scope-systems.sh --only m3-catalog         # exactly these, no stdin
#   printf '%s\n' "${changed[@]}" | scripts/scope-systems.sh --system m3-catalog
#   scripts/scope-systems.sh --list                     # system names, one per line
#   scripts/scope-systems.sh --table                    # `<system>\t<regex>` rows
#   scripts/scope-systems.sh --shared-re                # the all-systems regex
#
# Lane names are the DELIVERY-BRANCH SLUGS, not the old workflow output names, because the
# self-healing baseline is the ref `refs/design-artifacts/source/<system>` the reusable workflow
# writes on each successful publish. A mapper keyed on anything else would look up markers that do
# not exist and silently fall back to the old behaviour.
#
set -euo pipefail

SYSTEMS=(m3-catalog m3-samples compose-ui-samples compose-foundation)

# Inputs that change the shape of EVERY bundle: the version catalog (it carries the
# composePreviewPlugin pin the CLI is resolved from, so a compose-ai-tools release changes the
# renderer itself), the build files, this workflow and this mapper.
#
# The hand-written job this replaces listed the lanes to fan out to BY HAND and omitted
# `compose-foundation`, which is declared as an output and consumed by that lane's `if:`. Every
# `gradle/` change therefore left it empty and skipped the lane — a catalog that silently stopped
# being republished from the day it was added. Fanning out over SYSTEMS rather than a written-out
# list is why that cannot happen again.
SHARED_RE='^(gradle/|settings\.gradle\.kts$|build\.gradle\.kts$|\.github/workflows/design-artifacts\.yml$|scripts/scope-systems\.sh$)'

# Per-system inputs.
system_pattern() {
  case "$1" in
    # `ui-builder.policy.json` is as much a render input as the spec — `composePreviewDiscover`
    # reads it and its overrides reach the published `ui-builder.json`.
    m3-catalog)
      echo '^(catalog/|catalog-ui-builder-renderer/|catalog\.spec\.json$|ui-builder\.policy\.json$|design-pages\.json$|design/pages/)' ;;
    # `scripts/samples-` also matches `samples-previews.mjs`, the FOUNDATION corpus' wrapper
    # generator. Dirtying this sheet for it is a spare render and never a stale sheet, which is the
    # direction this mapping fails in on purpose.
    m3-samples)
      echo '^(samples-catalog/|samples/|sample-map\.json$|scripts/samples-)' ;;
    compose-ui-samples)
      echo '^(ui-samples-catalog/|scripts/samples-previews\.mjs$|scripts/ui-samples-spec\.mjs$)' ;;
    # The generator is listed because a change to it moves the published `ui-builder.json` — which
    # IS this catalog's payload — with no change to the module at all.
    compose-foundation)
      echo '^(foundation-catalog/|scripts/foundation-catalog\.mjs$)' ;;
    *) echo "unknown system: $1" >&2; exit 2 ;;
  esac
}

# The systems this invocation reports on: every system, or the single one `--system` named.
selected=("${SYSTEMS[@]}")

emit_all() {
  for system in "${selected[@]}"; do echo "$system=true"; done
  exit 0
}

# `if`, not `&&` — under `set -e` a failing `[ … ] && emit_all` AND-list would exit the script with
# the test's non-zero status instead of falling through.
case "${1:-}" in
  --all)
    emit_all
    ;;
  --list)
    printf '%s\n' "${SYSTEMS[@]}"
    exit 0
    ;;
  --shared-re)
    echo "$SHARED_RE"
    exit 0
    ;;
  --table)
    for system in "${SYSTEMS[@]}"; do
      printf '%s\t%s\n' "$system" "$(system_pattern "$system")"
    done
    exit 0
    ;;
  --only)
    # The dispatch lane selector: the manual remedy for one stale lane should re-render that lane,
    # not every catalog here. Tokens are normalised before they are validated OR matched — the
    # unquoted command substitution word-splits, so surrounding whitespace is gone before
    # `system_pattern` sees a name. An unknown name is a typo that would otherwise read as
    # "regenerate nothing unusual", so it is rejected loudly.
    only=','
    for name in $(tr ',' '\n' <<<"${2:-}"); do
      system_pattern "$name" > /dev/null
      only="$only$name,"
    done
    # Empty, blank, or nothing but separators. Falling through would report every system false and
    # regenerate nothing at all — a dispatch that silently does less than the default it replaced.
    if [ "$only" = ',' ]; then
      echo "--only needs a comma-separated system list" >&2
      exit 2
    fi
    for system in "${SYSTEMS[@]}"; do
      case "$only" in
        *",$system,"*) echo "$system=true" ;;
        *)             echo "$system=false" ;;
      esac
    done
    exit 0
    ;;
  --system)
    # Validates the name as a side effect: an unknown one exits 2 here rather than reporting a lane
    # nobody publishes.
    system_pattern "${2:-}" > /dev/null
    selected=("$2")
    ;;
  '') ;;
  *)
    echo "unknown option: $1" >&2
    exit 2
    ;;
esac

files="$(cat)"

# No resolvable change set → fail SAFE and regenerate everything. Publishing a fresh bundle is
# never wrong; skipping a stale one is.
if [ -z "$files" ]; then
  emit_all
fi

if grep -qE "$SHARED_RE" <<<"$files"; then
  emit_all
fi

for system in "${selected[@]}"; do
  if grep -qE "$(system_pattern "$system")" <<<"$files"; then
    echo "$system=true"
  else
    echo "$system=false"
  fi
done
