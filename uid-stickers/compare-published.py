#!/usr/bin/env python3
"""Compare this module's renders with the published hand-written stickers, pixel for pixel.

Prototype tooling: `python3 uid-stickers/compare-published.py` after `composePreviewRender`.
Fetches each cell from the m3-catalog delivery branch and reports any differing pixel.
"""
import glob, io, os, sys, urllib.request
from PIL import Image, ImageChops

BASE = "https://raw.githubusercontent.com/yschimke/m3-catalog-out/design-artifacts/m3-catalog/images"
RENDERS = os.path.join(os.path.dirname(__file__), "build/compose-previews/renders")
CELLS = {  # this module's preview -> the published capture of the hand-written sticker
    "FilledCardUid_Light": "card-filled/ideal__default__light__compact",
    "FilledCardUid_Dark": "card-filled/ideal__default__dark__compact",
    "FilledCardUidSlot_Light": "card-filled/ideal__slot__compact",
    "FilledCardUidSlot_Dark": "card-filled/ideal__slot__dark__compact",
    "ElevatedCardUid_Light": "card-elevated/ideal__default__light__compact",
    "ElevatedCardUid_Dark": "card-elevated/ideal__default__dark__compact",
    "ElevatedCardUidSlot_Light": "card-elevated/ideal__slot__compact",
    "ElevatedCardUidSlot_Dark": "card-elevated/ideal__slot__dark__compact",
    "OutlinedCardUid_Light": "card-outlined/ideal__default__light__compact",
    "OutlinedCardUid_Dark": "card-outlined/ideal__default__dark__compact",
    "OutlinedCardUidSlot_Light": "card-outlined/ideal__slot__compact",
    "OutlinedCardUidSlot_Dark": "card-outlined/ideal__slot__dark__compact",
}

failed = 0
for preview, published in CELLS.items():
    ours = Image.open(glob.glob(f"{RENDERS}/{preview}-*.png")[0]).convert("RGBA")
    theirs = Image.open(io.BytesIO(urllib.request.urlopen(f"{BASE}/{published}.png").read())).convert("RGBA")
    if ours.size != theirs.size:
        print(f"DIFF  {preview}: size {ours.size} vs {theirs.size}"); failed += 1; continue
    diff = ImageChops.difference(ours, theirs)
    box = diff.getbbox()
    if box is None:
        print(f"SAME  {preview} {ours.size[0]}x{ours.size[1]} == {published}")
    else:
        n = sum(1 for p in diff.convert("L").getdata() if p)
        print(f"DIFF  {preview}: {n} px differ within {box}"); failed += 1
sys.exit(1 if failed else 0)
