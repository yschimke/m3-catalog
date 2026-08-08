// Resolve every single-axis catalog variant to the kit variant node it documents.
//
// The base ref is a variant node whose name is an axis vector — `Type=Round,
// Size=Small, State=Enabled`. A variant seeding ONE knob should land on the
// sibling with ONE axis changed. That is checkable: the resolved name must
// exist in the same set, so a wrong guess cannot survive.
// Resolve a catalog variant onto the kit variant node it documents.
//
// A component's reference is a variant node whose NAME is an axis vector —
// `Type=Round, Size=Small, State=Enabled`. A variant that seeds exactly one
// knob should land on the sibling with exactly one axis changed. That is
// checkable rather than plausible: the resolved combination has to exist in the
// kit's own variant list, so a wrong translation finds nothing instead of
// producing a confident bad reference — which under `design-led` would drive
// this code away from the kit it is copying.
//
// Combinations are deliberately out. Comparing every axis is the goal; every
// cross product of them is a different, much larger thing that says little the
// single axes do not (see #16).
import { readFileSync } from "node:fs";

const index = JSON.parse(readFileSync("figma-kit-index.json", "utf8"));

const variantIndex = new Map();
const setIndex = new Map();
for (const [setId, set] of Object.entries(index.sets)) {
  setIndex.set(setId, { id: setId, name: set.name, children: set.variants });
  for (const v of set.variants) {
    const axes = {};
    for (const part of v.name.split(", ")) {
      const i = part.indexOf("=");
      if (i > 0) axes[part.slice(0, i)] = part.slice(i + 1);
    }
    variantIndex.set(v.id, { setId, setName: set.name, name: v.name, axes });
  }
}

// Knob key -> the kit axis it names. The kit's taxonomy is its own; ours
// describes the Compose API. Where the two disagree it is a translation, not a
// guess — each entry is checked against the set's real axis list before use.
const AXIS_ALIASES = {
  status: ["State"],
  state: ["State", "Type", "Selected", "Configuration"],
  selected: ["Selected", "Type"],
  size: ["Size"],
  shape: ["Type", "Shape"],
  count: ["Nav items", "Segments", "Icons", "Groups", "# of lines"],
  actions: ["Icons"],
  lines: ["Multi-line", "# of lines"],
  labels: ["Configuration", "Label"],
  content: ["Configuration", "Layout", "Show icon", "Icon"],
  leading: ["Configuration", "Leading icon"],
  trailing: ["Show trailing icon", "Trailing icon", "Configuration"],
  icon: ["Icon", "Show icon"],
  style: ["Style"],
  layout: ["Layout"],
  mode: ["Type"],
  hours: ["Format"],
  header: ["Show back", "Configuration"],
  progress: ["Progress"],
  handle: ["Configuration"],
  dividers: ["Groups", "Configuration"],
  avatar: ["Show avatar"],
  fab: ["Configuration"],
  expanded: ["Type"],
  menu: ["Configuration"],
  badge: ["Badge"],
  caret: ["Configuration"],
  footer: ["Configuration"],
  headline: ["Configuration"],
  nav: ["Configuration"],
  inset: ["Configuration"],
  label: ["Size"],
  action: ["Configuration"],
};

// Knob value -> the kit's spelling. Multiple candidates are tried in order.
const VALUE_ALIASES = {
  xs: ["XSmall"], s: ["Small"], m: ["Medium"], l: ["Large"], xl: ["XLarge"],
  on: ["True", "Enabled"], off: ["False", "Unselected"],
  true: ["True"], false: ["False"],
  none: ["False", "Label only", "None"],
  icon: ["True", "Label & icon", "Icon only", "Label & leading icon"],
  disabled: ["Disabled"], enabled: ["Enabled"],
  selected: ["True", "Selected"], unselected: ["False", "Unselected"],
  checked: ["Selected", "True"], unchecked: ["Unselected", "False"],
  indeterminate: ["Indeterminate"],
  error: ["Error selected", "Error"],
  empty: ["False", "0"],
  square: ["Square"], round: ["Round"],
  input: ["Keyboard", "Input"],
  vertical: ["Vertical"], horizontal: ["Horizontal"],
  media: ["Media & text"], slot: ["Slot"],
  avatar: ["Label & avatar", "True"],
  "icon+label": ["Label & icon"],
  both: ["Label & icon"],
  query: ["True"],
  text: ["Label only"],
  "12": ["12 hour"], "24": ["24 hour"],
};

const norm = (s) => String(s).toLowerCase().replace(/[^a-z0-9]/g, "");

function axisCandidates(knob, axes) {
  const named = AXIS_ALIASES[knob] ?? [];
  const byName = Object.keys(axes).filter((a) => {
    const n = norm(a), k = norm(knob);
    return n === k || n.startsWith(k) || k.startsWith(n);
  });
  const aliased = named.filter((a) => a in axes);
  // Last resort: any axis at all. Still verified against the real variant list,
  // so a nonsense pairing simply finds nothing rather than producing a bad ref.
  return [...new Set([...byName, ...aliased, ...Object.keys(axes)])];
}

function valueCandidates(raw) {
  const key = String(raw).toLowerCase();
  const out = [...(VALUE_ALIASES[key] ?? [])];
  out.push(String(raw));
  if (/^\d+$/.test(String(raw))) out.push(String(raw));
  // A float knob like progress=0.25 is a percentage in the kit.
  const f = Number(raw);
  if (Number.isFinite(f) && f <= 1 && String(raw).includes(".")) out.push(String(Math.round(f * 100)));
  if (Number.isFinite(f) && f === 0) out.push("0");
  if (Number.isFinite(f) && f === 1) out.push("100");
  // The kit capitalises its values ("Elevated"); our knobs do not.
  out.push(String(raw).charAt(0).toUpperCase() + String(raw).slice(1));
  return [...new Set(out)];
}

/**
 * Some components are not variants of a set: the kit models dividers as five
 * standalone components in a `Horizontal/…` / `Vertical/…` folder. Their
 * "variants" are their folder siblings, matched on the leaf name.
 */
const components = Object.entries(index.standalone).map(([id, c]) => ({ id, name: c.name }));
const byId = new Map(components.map((c) => [c.id, c]));

function componentSiblings(nodeId) {
  const self = byId.get(nodeId);
  if (!self) return null;
  const slash = self.name.lastIndexOf("/");
  if (slash < 0) return null;
  const folder = self.name.slice(0, slash);
  return { self, folder, peers: components.filter((c) => c.name.startsWith(folder + "/") && c.id !== nodeId) };
}

function matchSibling({ peers }, seed) {
  const want = [String(seed.raw), String(seed.key), `${seed.key}-${seed.raw}`].map(norm);
  // Exact leaf first, then the shortest containing one. Without the ordering,
  // `inset` matches `Middle-inset` before `Inset` — a real divider, but the
  // wrong one, and a wrong reference is worse here than none.
  const scored = [];
  for (const peer of peers) {
    const leaf = norm(peer.name.slice(peer.name.lastIndexOf("/") + 1));
    if (want.some((w) => w === leaf)) scored.push([0, leaf.length, peer]);
    else if (want.some((w) => leaf.includes(w) || w.includes(leaf))) scored.push([1, leaf.length, peer]);
  }
  scored.sort((a, b) => a[0] - b[0] || a[1] - b[1]);
  const best = scored[0]?.[2];
  return best ? { axis: "component", want: best.name, id: best.id, name: best.name } : null;
}

/** Which design-map slot a knob fills: the schema tags refs by state/size/theme. */
const SIZE_KNOBS = new Set(["size", "shape"]);

/**
 * The kit node for `seed` applied to the component referenced by `ref`, or
 * undefined when the kit models no such axis — a badge's digit count, a top app
 * bar's action count. Those are real gaps, reported rather than guessed at.
 */
export function resolveVariantRef(ref, seed) {
  const nodeId = ref.split("/")[1];
  const baseVar = variantIndex.get(nodeId);
  if (!baseVar) {
    const sib = componentSiblings(nodeId);
    const hit = sib && matchSibling(sib, seed);
    return hit ? { nodeId: hit.id, name: hit.name } : undefined;
  }
  const set = setIndex.get(baseVar.setId);
  const eq = (a, b) => String(a).toLowerCase() === String(b).toLowerCase();
  for (const axis of axisCandidates(seed.key, baseVar.axes)) {
    for (const want of valueCandidates(seed.raw)) {
      if (eq(baseVar.axes[axis], want)) continue;
      const target = { ...baseVar.axes, [axis]: want };
      const match = set.children.find((v) => {
        const vv = variantIndex.get(v.id);
        return Object.keys(target).every((a) => eq(vv.axes[a], target[a]));
      });
      if (match) return { nodeId: match.id, name: variantIndex.get(match.id).name };
    }
  }
  return undefined;
}

/** `size` for a size/shape knob, `state` for everything else. */
export function slotFor(seed, variantName) {
  return SIZE_KNOBS.has(seed.key)
    ? { size: String(seed.raw) }
    : { state: variantName };
}
