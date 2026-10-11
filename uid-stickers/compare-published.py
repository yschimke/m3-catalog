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
    # The live `layout` knob on the media sticker, against the same published slot cells.
    "FilledCardUid_Light_VARIANT_slot": "card-filled/ideal__slot__compact",
    "FilledCardUid_Dark_VARIANT_slot": "card-filled/ideal__slot__dark__compact",
    "ElevatedCardUid_Light_VARIANT_slot": "card-elevated/ideal__slot__compact",
    "ElevatedCardUid_Dark_VARIANT_slot": "card-elevated/ideal__slot__dark__compact",
    "OutlinedCardUid_Light_VARIANT_slot": "card-outlined/ideal__slot__compact",
    "OutlinedCardUid_Dark_VARIANT_slot": "card-outlined/ideal__slot__dark__compact",
    "SuggestionChipUid_Light": "chip-suggestion/ideal__default__light",
    "SuggestionChipUid_Dark": "chip-suggestion/ideal__default__dark",
    # The live `status` knob, as the published override cell sets it.
    "SuggestionChipUid_Light_VARIANT_disabled": "chip-suggestion/ideal__disabled",
    "SuggestionChipUid_Dark_VARIANT_disabled": "chip-suggestion/ideal__disabled__dark",
    # Interaction cells: the harness applies the state; the document knows nothing about it.
    "SuggestionChipUid_Light_VARIANT_hovered": "chip-suggestion/ideal__hovered",
    "SuggestionChipUid_Dark_VARIANT_hovered": "chip-suggestion/ideal__hovered__dark",
    "SuggestionChipUid_Light_VARIANT_focused": "chip-suggestion/ideal__focused",
    "SuggestionChipUid_Dark_VARIANT_focused": "chip-suggestion/ideal__focused__dark",
    "SuggestionChipUid_Light_VARIANT_focus_ring": "chip-suggestion/ideal__focus-ring",
    "SuggestionChipUid_Dark_VARIANT_focus_ring": "chip-suggestion/ideal__focus-ring__dark",
    "SuggestionChipUid_Light_VARIANT_pressed": "chip-suggestion/ideal__pressed",
    "SuggestionChipUid_Dark_VARIANT_pressed": "chip-suggestion/ideal__pressed__dark",
}

# Pairs rendered by this module: a generated sticker against the hand-written one it replaces,
# where the delivery branch publishes no capture to compare with (another locale).
LOCAL_PAIRS = {
    "FilledCardUidGerman": "FilledCardHandWrittenGerman",
}

failed = 0
for preview, published in CELLS.items():
    ours = Image.open(glob.glob(f"{RENDERS}/{preview}-*.png")[0]).convert("RGBA")
    theirs = Image.open(io.BytesIO(urllib.request.urlopen(f"{BASE}/{published}.png").read())).convert("RGBA")
    if ours.size != theirs.size:
        print(f"DIFF  {preview}: size {ours.size} vs {theirs.size}"); failed += 1; continue
    diff = ImageChops.difference(ours, theirs)
    box = diff.getbbox(alpha_only=False)  # the default only looks at alpha
    if box is None:
        print(f"SAME  {preview} {ours.size[0]}x{ours.size[1]} == {published}")
    else:
        n = sum(1 for p in diff.convert("L").getdata() if p)
        print(f"DIFF  {preview}: {n} px differ within {box}"); failed += 1
for preview, reference in LOCAL_PAIRS.items():
    ours = Image.open(glob.glob(f"{RENDERS}/{preview}_German-*.png")[0]).convert("RGBA")
    theirs = Image.open(glob.glob(f"{RENDERS}/{reference}_German-*.png")[0]).convert("RGBA")
    box = None if ours.size != theirs.size else ImageChops.difference(ours, theirs).getbbox(alpha_only=False)
    if ours.size == theirs.size and box is None:
        print(f"SAME  {preview} {ours.size[0]}x{ours.size[1]} == {reference} (local)")
    else:
        print(f"DIFF  {preview} vs {reference}: {ours.size} {theirs.size} {box}"); failed += 1
sys.exit(1 if failed else 0)
