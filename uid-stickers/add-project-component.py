#!/usr/bin/env python3
"""Add (or replace) a PROJECT component in `project-components.json` from a compact spec.

  python3 uid-stickers/add-project-component.py m3catalog/stars-icon \\
      ee.schimke.m3catalog.uid.BuilderComponentsKt StarsIcon modifier:androidx.compose.ui.Modifier=

Each parameter is `name:typeFqn`, with a trailing `=` when it has a default, and `@slot` for a
`@Composable () -> Unit` slot (`content@slot=`). The function must live in this module's `src/main`
under the given JVM owner, so the generated code imports and calls it like any library component.
"""
import json, os, sys

HERE = os.path.dirname(os.path.abspath(__file__))
path = os.path.join(HERE, "project-components.json")
component_id, owner, name, *params = sys.argv[1:]
package = owner.rsplit(".", 1)[0]
callable_ = f"{package}.{name}"


def parameter(spec):
    has_default = spec.endswith("=")
    spec = spec.rstrip("=")
    slot = spec.endswith("@slot")
    spec = spec.replace("@slot", "")
    pname, _, type_fqn = spec.partition(":")
    type_fqn = type_fqn or "kotlin.Function2"
    return {"name": pname, "type": ("@Composable () -> Unit" if slot else type_fqn.rsplit(".", 1)[-1]),
            "typeFqn": None if slot else type_fqn, "hasDefault": has_default, "composableSlot": slot,
            "composableSlotReceiver": None, "nullable": False, "noArgConstructible": False,
            "noArgFactory": None, "scopeDslReceiver": None, "lambdaReturnTypeFqn": None}


entry = {
    "canonicalId": f"m3-catalog/{owner}.{name}",
    "componentIds": [component_id, f"{owner}.{name}"],
    "symbol": {"jvmOwner": owner, "callable": callable_, "name": name, "origin": "PROJECT",
               "jvmName": None, "descriptor": None, "sourceFile": None, "docs": "unavailable", "receiver": None},
    "parameters": [parameter(p) for p in params],
    "slots": [], "bindings": [],
    "code": {"call": f"{name}()", "imports": [callable_], "refusedReason": None, "requiredOptIns": [], "androidxOptIns": []},
    "signatureKnown": True, "callableFromAnotherFile": True, "hasTypeParameters": False,
    "hasContextReceivers": False, "overloadsCollided": False, "builder": None,
    "requiredOptIns": [], "androidxOptIns": [],
}
record = json.load(open(path))
record["components"] = [c for c in record["components"] if component_id not in c["componentIds"]] + [entry]
open(path, "w").write(json.dumps(record, indent=2) + "\n")
print(f"{component_id} -> {callable_}")
