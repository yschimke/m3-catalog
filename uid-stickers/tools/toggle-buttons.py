#!/usr/bin/env python3
"""Writes the four toggle-button documents (`stickers/<emphasis>-toggle-button.{uid,json}`).

Each is the hand-written sticker's frame box around the Material toggle button, holding the
selection stars glyph and the label, with the kit's five sizes x two shapes as cells. Run from
anywhere; it rewrites the files in place.
"""
import json, os

HERE = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))

EMPHASES = [
    # file stem, component name, component id, default label resource, default checked
    ("toggle-button", "KitToggleButton", "m3/toggle-button", "label_on", True),
    ("tonal-toggle-button", "KitTonalToggleButton", "m3/tonal-toggle-button", "label_on", True),
    ("outlined-toggle-button", "KitOutlinedToggleButton", "m3/outlined-toggle-button", "label_outlined", False),
    ("elevated-toggle-button", "KitElevatedToggleButton", "m3/elevated-toggle-button", "label_elevated", False),
]

# size key -> (cell name, frame height, container height, content padding (h, v), icon, gap, style)
SIZES = {
    "xs": ("Xs", 32, 32, (12, 6), 20, 4, "labelLarge"),
    "s": ("", 48, 40, (16, 10), 20, 8, "labelLarge"),
    "m": ("M", 56, 56, (24, 16), 24, 8, "titleMedium"),
    "l": ("L", 96, 96, (48, 32), 32, 12, "headlineSmall"),
    "xl": ("Xl", 136, 136, (64, 48), 40, 16, "headlineLarge"),
}


def height(dp):
    return [{"type": "height", "heightDp": dp}]


def icon_modifiers(icon, gap):
    return [
        {"type": "padding", "startDp": 0, "topDp": 0, "endDp": gap, "bottomDp": 0},
        {"type": "size", "widthDp": icon, "heightDp": icon},
    ]


def padding(h, v):
    return {"type": "padding", "startDp": h, "topDp": v, "endDp": h, "bottomDp": v}


def document(stem, name, component_id, label, checked):
    _, frame, container, (h, v), icon, gap, style = SIZES["s"]
    return {
        "schema": "compose-ui-builder-document/v1-candidate",
        "id": stem,
        "title": name,
        "revision": 0,
        "catalogPin": {
            "systemId": "m3-catalog",
            "catalogRevision": "candidate",
            "capabilityDigest": "candidate",
            "nativeRuntimeId": "candidate",
        },
        "environment": {
            "widthDp": 200, "heightDp": 48, "density": 2.625, "theme": "light",
            "locale": "en-US", "fontScale": 1, "layoutDirection": "ltr",
        },
        "roots": ["place"],
        "components": {stem: {"name": name, "root": "frame"}},
        "nodes": {
            "place": {
                "id": "place",
                "componentId": "design/component-instance",
                "component": {
                    "componentKey": stem,
                    "arguments": {
                        "checked": {"type": "bool", "value": checked},
                        "label": {"type": "resource", "resourceKey": label},
                        "enabled": {"type": "bool", "value": True},
                    },
                },
            },
            "icon": {
                "id": "icon",
                "componentId": "m3catalog/selection-stars-icon",
                "properties": {"selected": {"type": "binding", "value": "checked"}},
                "slots": {},
                "modifiers": icon_modifiers(icon, gap),
            },
            "label": {
                "id": "label",
                "componentId": "m3/text",
                "properties": {
                    "text": {"type": "binding", "value": "label"},
                    "style": {"type": "enum", "value": style},
                },
                "slots": {},
            },
            "toggle": {
                "id": "toggle",
                "componentId": component_id,
                "properties": {
                    "checked": {"type": "binding", "value": "checked"},
                    "enabled": {"type": "binding", "value": "enabled"},
                    "shapes": {"type": "enum", "value": "s"},
                    "contentPadding": padding(h, v),
                },
                "slots": {"content": ["icon", "label"]},
                "modifiers": height(container),
                "eventBindings": {"checkedChange": []},
            },
            "frame": {
                "id": "frame",
                "componentId": "layout/box",
                "properties": {"contentAlignment": {"type": "enum", "value": "center"}},
                "slots": {"children": ["toggle"]},
                "modifiers": height(frame),
            },
        },
    }


def cell(size, square):
    cell_name, frame, container, (h, v), icon, gap, style = SIZES[size]
    ops = []
    if size != "s":
        ops += [
            {"type": "setModifiers", "nodeId": "frame", "modifiers": height(frame)},
            {"type": "setModifiers", "nodeId": "toggle", "modifiers": height(container)},
            {"type": "setProperty", "nodeId": "toggle", "property": "contentPadding", "value": padding(h, v)},
            {"type": "setModifiers", "nodeId": "icon", "modifiers": icon_modifiers(icon, gap)},
            {"type": "setProperty", "nodeId": "label", "property": "style", "value": {"type": "enum", "value": style}},
        ]
    if square or size != "s":
        ops.append({
            "type": "setProperty", "nodeId": "toggle", "property": "shapes",
            "value": {"type": "enum", "value": size + ("Square" if square else "")},
        })
    name = (cell_name + " square").strip() if square else cell_name
    return name[0].upper() + name[1:], ops


PAIRS = {
    "toggle-button": "ToggleButtonSticker",
    "tonal-toggle-button": "TonalToggleButtonSticker",
    "outlined-toggle-button": "OutlinedToggleButtonSticker",
    "elevated-toggle-button": "ElevatedToggleButtonSticker",
}

for stem, name, component_id, label, checked in EMPHASES:
    with open(os.path.join(HERE, "stickers", stem + ".uid"), "w") as f:
        f.write(json.dumps(document(stem, name, component_id, label, checked), indent=2) + "\n")
    variants = {}
    for size in SIZES:
        for square in (False, True):
            if size == "s" and not square:
                continue
            n, ops = cell(size, square)
            variants[n] = ops
    pair = PAIRS[stem]
    with open(os.path.join(HERE, "stickers", stem + ".json"), "w") as f:
        f.write(json.dumps({"variants": variants, "pairs": {pair: pair}}, indent=2) + "\n")
