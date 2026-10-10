#!/usr/bin/env bash
set -euo pipefail

root=$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)
tools=$(cd "${1:-$root/../compose-ai-tools}" && pwd)
if [[ $# -gt 0 ]]; then shift; fi
builder=$(cd "${1:-$root/../compose-ui-builder}" && pwd)
if [[ $# -gt 0 ]]; then shift; fi

# Parallel trial only: no discovery input, saved design, renderer descriptor or delivery branch
# is rewritten. Use immutable source checkouts in CI until the adapter API ships.
runner=("$root/scripts/agent-gradle.sh")
if [[ ${CI:-false} == true ]]; then runner=("$root/gradlew"); fi
"${runner[@]}" -p "$root" \
  -PtypedM3Catalog=true -PtypedAdapterCatalog=true -PlocalBuilds=tools \
  "-PcomposeAiToolsDir=$tools" "-PcomposeUiBuilderDir=$builder" "-PlocalBuild.tools=$tools" \
  :catalog-typed-adapters:verifyTypedCatalog :catalog-typed-adapters:rendererArchive \
  :catalog-typed-adapters:ktfmtCheck :catalog-typed-policy:ktfmtCheck "$@"

# A fresh host test process has the standard reader but no application implementation dependency.
"${runner[@]}" -p "$root" \
  -PtypedM3Catalog=true -PtypedAdapterCatalog=true -PlocalBuilds=tools \
  "-PcomposeAiToolsDir=$tools" "-PcomposeUiBuilderDir=$builder" "-PlocalBuild.tools=$tools" \
  :catalog-typed-host:test :catalog-typed-host:ktfmtCheck "$@"

node "$root/scripts/verify-typed-m3-runtime.mjs" "$builder"

printf 'Cutover readiness: %s\n' "$root/catalog-typed-adapters/build/browser-evidence/readiness.json"
