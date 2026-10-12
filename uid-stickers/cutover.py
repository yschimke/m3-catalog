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
    """(header, imports, body): the header is everything before the first import, multi-line
    file annotations included."""
    lines = text.split("\n")
    first = next((k for k, l in enumerate(lines) if l.startswith("import ")), None)
    if first is None:
        first = next(k for k, l in enumerate(lines) if l.startswith("package ")) + 1
    last = max([k for k, l in enumerate(lines) if l.startswith("import ")] or [first - 1])
    return lines[:first], [l for l in lines[first:last + 1] if l.startswith("import ")], "\n".join(lines[last + 1:]).strip("\n")


def opt_ins(header):
    """The markers of a header's `@file:OptIn(…)`, in order."""
    found = re.search(r"@file:OptIn\((.*?)\)\s*$", "\n".join(header), re.S | re.M)
    return [m.strip() for m in found.group(1).split(",") if m.strip()] if found else []


def with_opt_ins(header, markers):
    text = "\n".join(header)
    text = re.sub(r"@file:OptIn\((.*?)\)\s*\n", "", text, flags=re.S)
    if not markers:
        return text.split("\n")
    lines = text.split("\n")
    at = next(k for k, l in enumerate(lines) if l.startswith("package "))
    # After the other file annotations, before `package`.
    while at > 0 and not lines[at - 1].strip():
        at -= 1
    return lines[:at] + [f"@file:OptIn({', '.join(markers)})"] + lines[at:]


def hand_file(function):
    for path in glob.glob(os.path.join(SECTIONS, "*.kt")):
        if re.search(rf"^fun {re.escape(function)}\(", open(path).read(), re.M):
            return path
    sys.exit(f"no hand-written {function}")


# Public top-level names of the project-component files, which shells used without an import.
COMPONENT_NAMES = sorted(
    {
        m.group(1)
        for path in glob.glob(os.path.join(SHELLS, "*Components.kt"))
        for m in re.finditer(r"^(?:internal )?(?:fun|val|class|object)\s+(?:<[^>]+>\s*)?(\w+)", open(path).read(), re.M)
    }
)

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
    markers = opt_ins(header)
    _, remaining = declarations(body)
    blines = body.split("\n")
    existing = {name: "\n".join(blines[a:b + 1]) for name, a, b in remaining}
    extra_imports, extra_bodies = set(), []
    for shell_file in shell_files:
        shell_header, shell_imports, shell_body = split_header(open(shell_file).read())
        markers += [m for m in opt_ins(shell_header) if m not in markers]
        slines, sdecls = declarations(shell_body)
        # A same-named declaration that differs from the hand-written one is renamed in this shell.
        for name, a, b in sdecls:
            block = "\n".join(slines[a:b + 1])
            if name in existing and re.sub(r"\s+", " ", existing[name]) != re.sub(r"\s+", " ", block):
                renamed = name + ("_GENERATED" if name.isupper() else "Generated")
                shell_body = re.sub(rf"\b{re.escape(name)}\b", renamed, shell_body)
        slines, sdecls = declarations(shell_body)
        keep = []
        for name, start, end in sdecls:
            block = "\n".join(slines[start:end + 1])
            # A comparison preview (`@Preview` on a function no pair names) stays behind.
            if "@Preview" in block and name not in pairs:
                continue
            # The shell copied a private knob reader the hand-written file still declares: keep one.
            if name in existing:
                continue
            keep.append(block)
        extra_bodies.append(f"// --- Generated sticker bodies, from uid-stickers/{os.path.relpath(shell_file, SHELLS)} ---\n\n" + "\n\n".join(keep))
        extra_imports.update(i for i in shell_imports if not i.startswith("import ee.schimke.m3catalog.sections."))
    shell_text = "\n\n".join(extra_bodies)
    # What the shells used from the project-component files, which leave their package.
    for name in COMPONENT_NAMES:
        if re.search(rf"\b{re.escape(name)}\b", shell_text):
            extra_imports.add(f"import ee.schimke.m3catalog.uid.{name}")
    all_imports = sorted(set(imports) | extra_imports)
    header = with_opt_ins(header, markers)
    # Hand-written private helpers only the replaced stickers used are dead now: drop them.
    while True:
        blines, decls_left = declarations(body)
        dead = [
            (name, a, b)
            for name, a, b in decls_left
            if blines[next(k for k in range(a, b + 1) if not blines[k].startswith(("@", "/", " *", "*"))) ].startswith("private ")
            and len(re.findall(rf"\b{re.escape(name)}\b", body + "\n" + shell_text)) == 1
        ]
        if not dead:
            break
        for name, a, b in sorted(dead, key=lambda d: -d[1]):
            del blines[a:b + 1]
        body = re.sub(r"\n{3,}", "\n\n", "\n".join(blines))
    out = "\n".join(header).rstrip("\n") + "\n\n" + "\n".join(all_imports) + "\n\n" + body.strip("\n") + "\n\n" + shell_text + "\n"
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
