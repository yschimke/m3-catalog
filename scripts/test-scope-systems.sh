#!/usr/bin/env bash
#
# Self-test for scripts/scope-systems.sh — the path→system mapping that decides which
# design-artifacts delivery branches a merge regenerates. CI runs this on every PR.
#
# Both directions matter and both are covered below:
#   • a false NEGATIVE silently strands a delivery branch on stale renders — the exact failure the
#     push trigger exists to prevent, and the one that hid a whole catalog (see the
#     `compose-foundation` cases);
#   • a false POSITIVE burns a 15-30 minute render per system on every unrelated merge.

set -uo pipefail

SCRIPT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/scope-systems.sh"
ALL='m3-catalog,m3-samples,compose-ui-samples,compose-foundation,glimmer-catalog,glimmer-samples'
failures=0

# expect <name> <expected-systems-csv-or-"none"> <changed files…>
expect() {
  local name="$1" want="$2"; shift 2
  local got
  got="$(printf '%s\n' "$@" | "$SCRIPT" | grep '=true$' | cut -d= -f1 | paste -sd, -)"
  : "${got:=none}"
  if [ "$got" = "$want" ]; then
    printf 'PASS  %s -> %s\n' "$name" "$got"
  else
    printf 'FAIL  %s -> got "%s", want "%s"\n' "$name" "$got" "$want"
    failures=$((failures + 1))
  fi
}

# expect_cmd <name> <expected-stdout> <args…>  — the no-stdin flag forms.
expect_cmd() {
  local name="$1" want="$2"; shift 2
  local got
  got="$("$SCRIPT" "$@" </dev/null 2>/dev/null | grep '=true$' | cut -d= -f1 | paste -sd, -)"
  : "${got:=none}"
  if [ "$got" = "$want" ]; then
    printf 'PASS  %s -> %s\n' "$name" "$got"
  else
    printf 'FAIL  %s -> got "%s", want "%s"\n' "$name" "$got" "$want"
    failures=$((failures + 1))
  fi
}

# --- one catalog changes → exactly that system ------------------------------
expect 'kit sticker'        'm3-catalog'         'catalog/src/main/kotlin/Buttons.kt'
expect 'kit spec'           'm3-catalog'         'catalog.spec.json'
expect 'kit builder policy' 'm3-catalog'         'ui-builder.policy.json'
expect 'm3 samples tree'    'm3-samples'         'samples-catalog/src/main/kotlin/A.kt'
expect 'm3 sample map'      'm3-samples'         'sample-map.json'
expect 'ui samples tree'    'compose-ui-samples' 'ui-samples-catalog/src/main/kotlin/A.kt'
expect 'foundation sticker' 'compose-foundation' 'foundation-catalog/src/main/kotlin/A.kt'
expect 'foundation gen'     'compose-foundation' 'scripts/foundation-catalog.mjs'

# Both Glimmer sheets move together on a source change — same pattern, separate baselines.
expect 'glimmer catalog' 'glimmer-catalog,glimmer-samples' 'glimmer-catalog/src/main/kotlin/Cards.kt'
expect 'glimmer samples' 'glimmer-catalog,glimmer-samples' 'glimmer-samples/src/main/kotlin/A.kt'
expect 'glimmer map'     'glimmer-catalog,glimmer-samples' 'glimmer-design-map.json'

# --- the shared fan-out -----------------------------------------------------
#
# THE REGRESSION THIS FILE EXISTS FOR. The hand-written Scope job listed the lanes to fan out to by
# hand and omitted `compose-foundation`, so every `gradle/` change — which is every
# compose-ai-tools release, the thing that changes the renderer itself — skipped that lane. It was
# declared as an output and consumed by the job's `if:`, so it read as empty and simply did not run.
expect 'version catalog'  "$ALL" 'gradle/libs.versions.toml'
expect 'settings.gradle'  "$ALL" 'settings.gradle.kts'
expect 'root build file'  "$ALL" 'build.gradle.kts'
expect 'the workflow'     "$ALL" '.github/workflows/design-artifacts.yml'
expect 'this mapper'      "$ALL" 'scripts/scope-systems.sh'
expect 'shared + one'     "$ALL" 'gradle/libs.versions.toml' 'catalog/src/main/kotlin/A.kt'

# --- nothing relevant -------------------------------------------------------
expect 'docs only'        'none' 'docs/evidence/glimmer-google-sans-flex/README.md'
expect 'readme'           'none' 'README.md'
expect 'unrelated ci'     'none' '.github/workflows/ci.yml'
expect 'evidence png'     'none' 'docs/evidence/glimmer-google-sans-flex/card-sticker-after.png'

# --- several at once --------------------------------------------------------
expect 'kit + foundation' 'm3-catalog,compose-foundation' \
  'catalog/src/main/kotlin/A.kt' 'foundation-catalog/src/main/kotlin/B.kt'

# --- fail-safe: no resolvable change set → everything -----------------------
expect 'empty change set' "$ALL" ''

# --- the flag forms a generic driver consumes -------------------------------
expect_cmd '--all'  "$ALL" --all
expect_cmd '--only one lane'  'glimmer-catalog' --only glimmer-catalog
expect_cmd '--only two lanes' 'm3-catalog,glimmer-catalog' --only 'm3-catalog,glimmer-catalog'
# The form a human types, with a space after the comma.
expect_cmd '--only spaced'    'm3-catalog,glimmer-catalog' --only 'm3-catalog, glimmer-catalog'

# --- loud failures rather than a silent "regenerate nothing unusual" --------
check_exit() {
  local name="$1" want="$2"; shift 2
  "$SCRIPT" "$@" </dev/null >/dev/null 2>&1
  local got=$?
  if [ "$got" = "$want" ]; then
    printf 'PASS  %s -> exit %s\n' "$name" "$got"
  else
    printf 'FAIL  %s -> exit %s, want %s\n' "$name" "$got" "$want"
    failures=$((failures + 1))
  fi
}
check_exit 'unknown --only name' 2 --only 'glimmer-katalog'
check_exit 'empty --only'        2 --only ''
check_exit 'unknown --system'    2 --system 'nope'
check_exit 'unknown option'      2 --nope

# --- --list / --table / --shared-re, the generic driver's view --------------
if [ "$("$SCRIPT" --list </dev/null | paste -sd, -)" = "$ALL" ]; then
  printf 'PASS  --list\n'
else
  printf 'FAIL  --list -> %s\n' "$("$SCRIPT" --list </dev/null | paste -sd, -)"
  failures=$((failures + 1))
fi
if [ "$("$SCRIPT" --table </dev/null | wc -l)" -eq 6 ]; then
  printf 'PASS  --table rows\n'
else
  printf 'FAIL  --table rows -> %s\n' "$("$SCRIPT" --table </dev/null | wc -l)"
  failures=$((failures + 1))
fi
if "$SCRIPT" --shared-re </dev/null | grep -q 'gradle/'; then
  printf 'PASS  --shared-re\n'
else
  printf 'FAIL  --shared-re\n'
  failures=$((failures + 1))
fi

# --- --system, the per-lane range scope-step.sh calls back with -------------
one="$(printf '%s\n' 'glimmer-catalog/src/main/kotlin/A.kt' | "$SCRIPT" --system glimmer-catalog)"
if [ "$one" = 'glimmer-catalog=true' ]; then
  printf 'PASS  --system single lane\n'
else
  printf 'FAIL  --system single lane -> %s\n' "$one"
  failures=$((failures + 1))
fi
none="$(printf '%s\n' 'README.md' | "$SCRIPT" --system glimmer-catalog)"
if [ "$none" = 'glimmer-catalog=false' ]; then
  printf 'PASS  --system unrelated change\n'
else
  printf 'FAIL  --system unrelated change -> %s\n' "$none"
  failures=$((failures + 1))
fi

echo
if [ "$failures" -eq 0 ]; then
  echo "All scope-systems checks passed."
else
  echo "$failures scope-systems check(s) failed."
  exit 1
fi
