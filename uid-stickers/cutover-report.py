#!/usr/bin/env python3
"""The cutover table: every `:catalog` sticker, and whether its generated replacement is ready.

  CATALOG_RENDERS=… python3 uid-stickers/compare-local.py --report report.json
  python3 uid-stickers/cutover-report.py report.json > CUTOVER.md

A sticker is ready when compare-local.py found every one of its captures pixel-identical. The
others carry the reason recorded in `blocked/<name>.json` (`$blocked`) or in `BLOCKED` below.
"""
import glob, json, os, re, sys

HERE = os.path.dirname(os.path.abspath(__file__))
SECTIONS = os.path.join(HERE, "../catalog/src/main/kotlin/ee/schimke/m3catalog/sections")

# Stickers with no `blocked/` document, and why.
BLOCKED = {
    "SearchBarSticker": "needs `rememberSearchBarState()` state and a `SearchBarDefaults.InputField` call site",
    "RangeSliderSticker": "needs a `rememberRangeSliderState(start, end, steps)` state bundle and the slider's thumb/track sizes",
    "ContinuousSlider": "8/64 captures; the size cells need `Slider(state)` with `thumb`/`track` slots and `SliderDefaults.Thumb/Track/CenteredTrack`",
    "DropdownMenuGroupSticker": "needs `MenuDefaults.groupShape(index, count)` and the leading/middle/trailing item shapes",
    "SplitButton": "needs `SplitButtonDefaults.LeadingButton`/`TrailingButton` call sites and the checked trailing overload's record",
    "MaterialShapesSticker": "no builder component draws `MaterialShapes`",
}

report = json.load(open(sys.argv[1]))
ready = {shell: r for shell, r in report.items() if not r["missing"] and not r["different"] and not r["extra"]}
blocked_notes = {}
for path in glob.glob(os.path.join(HERE, "blocked/*.json")):
    note = json.load(open(path)).get("$blocked")
    if note:
        blocked_notes[os.path.basename(path)[:-5]] = note if isinstance(note, str) else "; ".join(note)

rows = []
for path in sorted(glob.glob(os.path.join(SECTIONS, "*.kt"))):
    lines = open(path).read().split("\n")
    for i, line in enumerate(lines):
        m = re.search(r'id = "([^"]+)"', line)
        if not m or not any("@CatalogComponent(" in lines[k] for k in range(max(0, i - 2), i + 1)):
            continue
        fn = next((f.group(1) for j in range(i, min(i + 80, len(lines)))
                   if (f := re.match(r"(?:internal |private )?fun (\w+)\(", lines[j]))), None)
        rows.append((m.group(1), fn))

done = [r for r in rows if r[1] in ready]
print(f"**{len(done)} of {len(rows)} stickers ({100 * len(done) // len(rows)}%) are ready to cut over**, "
      f"{sum(ready[f]['captures'] for _, f in done)} captures pixel-identical.\n")
print("| Sticker | Function | Status |\n| --- | --- | --- |")
for cid, fn in rows:
    if fn in ready:
        status = f"ready, {ready[fn]['captures']}/{ready[fn]['captures']} captures"
    else:
        slug = re.sub(r"(?<!^)(?=[A-Z])", "-", fn or "").lower().removesuffix("-sticker")
        note = BLOCKED.get(fn) or next((v for k, v in blocked_notes.items() if k in slug or slug in k), None)
        status = "blocked: " + (note or "see docs/design/UID_STICKERS.md")
    print(f"| {cid} | `{fn}` | {status} |")
