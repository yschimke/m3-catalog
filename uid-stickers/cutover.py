#!/usr/bin/env python3
"""Cut the paired stickers over: their bodies become the generated UI builder components.

  python3 uid-stickers/cutover.py [--dry-run]

Run on a checkout after a full `compare-local.py` passes. For every pair in `stickers/*.json`:

- the hand-written function in `catalog/.../sections/<File>.kt` is deleted (its KDoc, annotations
  and body), and the shell that replaces it, from `uid-stickers/src/main/kotlin/.../uid/`, is
  appended to the same file in package `ee.schimke.m3catalog.sections`. Same file, same facade
  class, same function name and annotations: every preview id, and every repository file keyed
  by one (`design-map.json` …), is unchanged.
- the generated components move to `catalog/src/uid/kotlin` (checked in, so `:catalog` needs no
  exporter to build) and the project components the documents use move to `:catalog`.

Shell files' other previews (the German locale pair) are dropped: they compare, they don't publish.
"""
import glob, json, os, re, shutil, sys

ROOT = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..")
SECTIONS = os.path.join(ROOT, "catalog/src/main/kotlin/ee/schimke/m3catalog/sections")
SHELLS = os.path.join(ROOT, "uid-stickers/src/main/kotlin/ee/schimke/m3catalog/uid")
GENERATED = os.path.join(ROOT, "uid-stickers/build/generated/uid/kotlin")
CATALOG_GENERATED = os.path.join(ROOT, "catalog/src/uid/kotlin")
CATALOG_UID = os.path.join(ROOT, "catalog/src/main/kotlin/ee/schimke/m3catalog/uid")
DRY = "--dry-run" in sys.argv

pairs = {}
for side in glob.glob(os.path.join(ROOT, "uid-stickers/stickers/*.json")):
    for shell, hand in json.load(open(side)).get("pairs", {}).items():
        if not hand.startswith("self:"):
            pairs[shell] = hand


def declarations(text):
    """Top-level declarations as (name, start, end) line spans, with their KDoc and annotations."""
    lines = text.split("\n")
    out, i = [], 0
    while i < len(lines):
        m = re.match(r"(?:(?:private|internal|public) )?(?:fun|val|class|object|enum class|annotation class|data class)\s+(?:<[^>]+>\s*)?([A-Za-z_][A-Za-z0-9_.]*)", lines[i])
        if not m:
            i += 1
            continue
        start = i
        while start > 0 and (lines[start - 1].startswith(("@", " ", ")", "//", "/**", " *", "*/")) or lines[start - 1].strip().startswith("*")):
            if lines[start - 1].strip() == "" or lines[start - 1].startswith("import "):
                break
            start -= 1
        depth, end, opened = 0, i, False
        j = i
        while j < len(lines):
            code = re.sub(r'"(?:\\.|[^"\\])*"', '""', lines[j])
            code = code.split("//")[0]
            depth += code.count("{") - code.count("}")
            depth += code.count("(") - code.count(")")
            opened = opened or "{" in code or "=" in code
            if opened and depth <= 0 and (j + 1 >= len(lines) or not lines[j + 1].startswith((" ", ")", "}", "."))):
                end = j
                break
            j += 1
        out.append((m.group(1).split(".")[-1], start, end))
        i = end + 1
    return lines, out


def split_header(text):
    lines = text.split("\n")
    body_start = 0
    imports, header = [], []
    for k, line in enumerate(lines):
        if line.startswith("import "):
            imports.append(line)
            body_start = k + 1
        elif line.startswith(("package ", "@file:")) or (not line.strip() and k < body_start + 1):
            header.append(line)
            body_start = max(body_start, k + 1)
    return header, imports, "\n".join(lines[body_start:]).strip("\n")


def hand_file(function):
    for path in glob.glob(os.path.join(SECTIONS, "*.kt")):
        if re.search(rf"^fun {re.escape(function)}\(", open(path).read(), re.M):
            return path
    sys.exit(f"no hand-written {function}")


# Shell files grouped by the hand-written file their shells replace.
moves = {}
for shell_file in sorted(glob.glob(os.path.join(SHELLS, "*.kt"))):
    text = open(shell_file).read()
    shells = [s for s in pairs if re.search(rf"^fun {re.escape(s)}\(", text, re.M)]
    if not shells:
        continue
    targets = {hand_file(pairs[s]) for s in shells}
    if len(targets) != 1:
        sys.exit(f"{shell_file} replaces stickers in {targets}")
    moves.setdefault(targets.pop(), []).append(shell_file)

for target, shell_files in sorted(moves.items()):
    lines, decls = declarations(open(target).read())
    replaced = {pairs[s] for f in shell_files for s in pairs if re.search(rf"^fun {re.escape(s)}\(", open(f).read(), re.M)}
    drop = sorted((d for d in decls if d[0] in replaced), key=lambda d: -d[1])
    for name, start, end in drop:
        del lines[start:end + 1]
        while start < len(lines) and start > 0 and lines[start - 1] == "" and lines[start] == "":
            del lines[start]
    header, imports, body = split_header("\n".join(lines))
    extra_imports, extra_bodies = set(), []
    for shell_file in shell_files:
        _, shell_imports, shell_body = split_header(open(shell_file).read())
        slines, sdecls = declarations(shell_body)
        keep = []
        for name, start, end in sdecls:
            block = "\n".join(slines[start:end + 1])
            # A comparison preview (`@Preview` on a function no pair names) stays behind.
            if "@Preview" in block and name not in pairs:
                continue
            keep.append(block)
        extra_bodies.append(f"// --- Generated sticker bodies, from uid-stickers/{os.path.relpath(shell_file, SHELLS)} ---\n\n" + "\n\n".join(keep))
        extra_imports.update(i for i in shell_imports if not i.startswith("import ee.schimke.m3catalog.sections."))
    all_imports = sorted(set(imports) | extra_imports)
    out = "\n".join(header).rstrip("\n") + "\n\n" + "\n".join(all_imports) + "\n\n" + body.rstrip("\n") + "\n\n" + "\n\n".join(extra_bodies) + "\n"
    print(f"{os.path.relpath(target, ROOT)}: {len(drop)} hand-written replaced by {', '.join(os.path.basename(f) for f in shell_files)}")
    if not DRY:
        open(target, "w").write(out)

# Project components and generated code move into :catalog.
for component in glob.glob(os.path.join(SHELLS, "*Components.kt")):
    print(f"move {os.path.basename(component)} -> catalog uid package")
    if not DRY:
        os.makedirs(CATALOG_UID, exist_ok=True)
        shutil.copy(component, CATALOG_UID)
CATALOG_BUILD = os.path.join(ROOT, "catalog/build.gradle.kts")
SRC_DIR = 'kotlin.sourceSets.named("main") { kotlin.srcDir("src/uid/kotlin") }'
if not DRY and SRC_DIR not in open(CATALOG_BUILD).read():
    with open(CATALOG_BUILD, "a") as build:
        build.write(
            "\n// The stickers' generated UI builder components, checked in by `uid-stickers/cutover.py` and\n"
            "// regenerated by `:uid-stickers:generateUidStickers` (`-PuidStickers=true`).\n" + SRC_DIR + "\n"
        )
if not DRY:
    if os.path.isdir(CATALOG_GENERATED):
        shutil.rmtree(CATALOG_GENERATED)
    shutil.copytree(GENERATED, CATALOG_GENERATED)
    print(f"generated components -> {os.path.relpath(CATALOG_GENERATED, ROOT)}")
