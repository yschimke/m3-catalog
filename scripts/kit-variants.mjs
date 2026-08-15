// Resolve every catalog variant to the kit variant node it documents.
//
// A component's reference is a variant node whose NAME is an axis vector —
// `Type=Round, Size=Small, State=Enabled`. Every seed is projected onto that
// vector, and the exact final combination must name a real sibling in the same
// component set. This handles both an axis (`size=l`) and a rendered
// cross-product cell (`size=l, shape=square`) without ever inventing a Figma
// combination. A wrong translation finds nothing instead of producing a
// confident bad reference — which under `design-led` would drive this code away
// from the kit it is copying.
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
    variantIndex.set(v.id, {
      setId,
      setName: set.name,
      name: v.name,
      axes,
      renderId: v.renderId,
    });
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
  close: ["Show close affordance"],
  configuration: ["Configuration"],
};

// Knob value -> the kit's spelling. Multiple candidates are tried in order.
const VALUE_ALIASES = {
  xs: ["XSmall"], s: ["Small"], m: ["Medium"], l: ["Large"], xl: ["XLarge"],
  on: ["True", "Enabled"], off: ["False", "Unselected"],
  true: ["True"], false: ["False"],
  none: ["False", "Label only", "None"],
  icon: ["Icon only", "True"],
  disabled: ["Disabled"], enabled: ["Enabled"],
  hovered: ["Hovered"], focused: ["Focused"],
  // `Presssed` is the kit's own misspelling, on all ten `Button - outline` press
  // variants and nowhere else. Figma is read-only here, so the choice is to
  // carry the typo or leave that component's press state uncompared; the
  // correct spelling is tried first, so this only ever catches that one set.
  pressed: ["Pressed", "Presssed"],
  selected: ["True", "Selected"], unselected: ["False", "Unselected"],
  checked: ["Selected", "True"], unchecked: ["Unselected", "False"],
  indeterminate: ["Indeterminate"],
  error: ["Error selected", "Error"],
  empty: ["False", "0"],
  square: ["Square"], round: ["Round"],
  input: ["Keyboard", "Input"],
  vertical: ["Vertical"], horizontal: ["Horizontal"],
  media: ["Media & text"], slot: ["Slot"],
  "text+action": ["Text & action"], two: ["Two lines"], one: ["One line"],
  // Container roles: our knobs hyphenate what the kit spaces.
  "primary-container": ["Primary container"],
  "secondary-container": ["Secondary container"],
  "tertiary-container": ["Tertiary container"],
  avatar: ["Label & avatar", "True"],
  "icon+label": ["Label & icon"],
  both: ["Label & icon"],
  query: ["True"],
  text: ["Label only"],
  "12": ["12 hour"], "24": ["24 hour"],
};

const norm = (s) => String(s).toLowerCase().replace(/[^a-z0-9]/g, "");

function axisCandidates(knob, axes, raw) {
  const named = AXIS_ALIASES[knob] ?? [];
  const byName = Object.keys(axes).filter((a) => {
    const n = norm(a), k = norm(knob);
    return n === k || n.startsWith(k) || k.startsWith(n);
  });
  const aliased = named.filter((a) => a in axes);
  // Last resort: an axis the vocabulary does not name, but only one this knob is
  // recognisably ABOUT — its name has to share a word with the knob's key or its
  // value. Verifying against the real variant list is not enough on its own: a
  // boolean axis accepts `True` from any knob, so `footer=true` cheerfully
  // matched `Show back=True` and `supporting=on` matched `Leading icon=True`.
  // Both are confident references to the wrong node, which is worse than none —
  // design-parity then measures a difference nobody asked about.
  // The affinity can be with the knob or with its value: `content=avatar` means
  // the `Show avatar` axis, and it is the VALUE that says so.
  // Matched WORD for word, not by substring: `Leading icon` contains the letters
  // of `on`, so `supporting=on` looked related to it and resolved to the wrong
  // axis with the right value.
  const words = [norm(knob), norm(raw ?? "")].filter(Boolean);
  const related = Object.keys(axes).filter((a) => {
    const parts = a.toLowerCase().split(/[^a-z0-9]+/).filter(Boolean);
    return words.some((w) => parts.includes(w) || norm(a) === w);
  });
  return [...new Set([...byName, ...aliased, ...related])];
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

// --- Component properties ----------------------------------------------------
//
// Not every knob the kit models is an axis. A button's icon, a rail's menu, a
// sheet's drag handle are COMPONENT PROPERTIES: a switch on the node rather
// than a variant beside it. That distinction matters twice over.
//
// Reading a miss: "no counterpart in the kit" is true of a badge's digit count
// in a way it is not true of a bottom bar's FAB. The FAB is right there — it
// just is not addressable, because `/v1/images` renders a node at its property
// DEFAULTS and the reference is a node id with nowhere to hang an override.
// Calling both "absent" hides which ones are our authoring gap and which are a
// limit of what a reference can express.
//
// Reading a match: those defaults are applied whether or not anyone chose them.
// A set whose `Show icon` defaults to true draws an icon in every render made
// from it, so a label-only sticker is compared against an icon'd reference and
// the width divergence that follows is an artefact, not a finding (#21).

/** Words that describe the property rather than name the thing it controls. */
const PROP_FILLER = new Set(["show", "text", "the"]);

const TRUTHY = new Set(["true", "on", "yes", "1"]);
const FALSY = new Set(["false", "off", "no", "0", "none"]);

/** The set a reference points into, when it points at one of its variants. */
function setForRef(ref) {
  const v = variantIndex.get(ref.split("/")[1]);
  return v ? { id: v.setId, name: v.setName, properties: index.sets[v.setId].properties } : undefined;
}

/** `actions` and `action` name the same thing; our knobs pluralise, the kit does not. */
const singular = (s) => (s.length > 3 && s.endsWith("s") ? s.slice(0, -1) : s);

function matchProperty(properties, knob) {
  const k = singular(norm(knob));
  // The axis vocabulary serves here too: a knob the kit spells as an axis on one
  // component it spells as a property on another — `content` is `Configuration`
  // on a list item and `Show icon` on a button — and one translation table
  // beats two that can disagree.
  const aliased = new Set((AXIS_ALIASES[knob] ?? []).map((a) => singular(norm(a))));
  let best = [];
  for (const [name, def] of Object.entries(properties ?? {})) {
    const meaty = name
      .toLowerCase()
      .split(/[^a-z0-9]+/)
      .filter((t) => t && !PROP_FILLER.has(t))
      .map(singular);
    // Whole name, then a spelling the vocabulary already knows, then the name
    // minus its filler words, then any single word of it — so `icon` reaches
    // `Show icon` while `Icon (selected)` loses to it.
    const score =
      singular(norm(name)) === k ? 0
      : aliased.has(singular(norm(name))) ? 1
      : meaty.join("") === k ? 2
      : meaty.includes(k) ? 3
      : -1;
    if (score < 0) continue;
    const rank = [score, meaty.length];
    const cmp = best.length ? rank[0] - best[0].rank[0] || rank[1] - best[0].rank[1] : -1;
    if (cmp < 0) best = [{ rank, name, type: def.type, default: def.default }];
    // A tie is not ambiguity to break: one knob genuinely spanning several
    // properties — a count over `Show 1st/2nd/3rd trailing action` — is the
    // finding, and naming one of the three would misreport it as a single switch.
    else if (cmp === 0) best.push({ rank, name, type: def.type, default: def.default });
  }
  return best.length ? best : undefined;
}

const eqValue = (a, b) => JSON.stringify(a) === JSON.stringify(b);

/**
 * Translate one catalog seed into the value of a Figma component property.
 *
 * Boolean and text properties have a lossless representation. Instance swaps
 * and slots do not: `leading=icon` says what the content means, not which Figma
 * component id supplies it, so those stay unpairable rather than guessing.
 */
function seededPropertyValue(property, seed, peers) {
  const raw = String(seed.raw);
  const lower = raw.toLowerCase();
  if (property.type === "BOOLEAN") {
    if (TRUTHY.has(lower)) return true;
    if (FALSY.has(lower)) return false;

    // A numeric count controls the kit's ordinal switches as a family:
    // actions=2 means the first two are on and the third is off.
    const ordinal = property.name.match(/\b(\d+)(?:st|nd|rd|th)\b/i)?.[1];
    const count = Number(raw);
    if (ordinal && Number.isInteger(count)) return Number(ordinal) <= count;

    const name = norm(property.name);
    if (name.includes("icon")) {
      if (["label", "text", "none"].includes(lower)) return false;
      if (["icon", "both", "icon+label", "label+icon"].includes(lower)) return true;
    }
    return undefined;
  }
  if (property.type === "TEXT") {
    // When a sibling visibility property is off, its hidden text stays at the
    // default. A lone text property uses the empty string to express absence.
    if (FALSY.has(lower)) {
      return peers.some((peer) => peer.type === "BOOLEAN") ? property.default : "";
    }
    return raw;
  }
  if (property.type === "INSTANCE_SWAP") {
    // `content=label` commonly matches both `Show icon` and `Icon`. The
    // instance-swap value is immaterial when the paired visibility switch is
    // off, so retain its default instead of demanding an icon id the catalog
    // seed intentionally does not name.
    const hidden = peers
      .filter((peer) => peer.type === "BOOLEAN")
      .some((peer) => seededPropertyValue(peer, seed, peers) === false);
    return hidden ? property.default : undefined;
  }
  return undefined;
}

/**
 * Resolve a property-shaped variant to a visible instance already configured
 * that way in the kit. Exported as a pure helper so false booleans, text and
 * multi-switch counts can be pinned without depending on the live kit file.
 */
export function resolvePropertyInstance(set, componentId, seeds) {
  if (!set?.properties || !set?.instances?.length) return undefined;
  const seedList = Array.isArray(seeds) ? seeds : [seeds];
  const target = Object.fromEntries(
    Object.entries(set.properties).map(([name, def]) => [name, def.default]),
  );

  let claimed = false;
  for (const seed of seedList) {
    const matches = matchProperty(set.properties, seed.key);
    if (!matches) continue;
    const values = matches.map((property) => seededPropertyValue(property, seed, matches));
    if (values.some((value) => value === undefined)) return undefined;
    matches.forEach((property, index) => {
      target[property.name] = values[index];
    });
    claimed = true;
  }
  if (!claimed) return undefined;

  const hit = set.instances.find(
    (instance) =>
      instance.componentId === componentId &&
      Object.entries(target).every(([name, value]) => eqValue(instance.properties?.[name], value)),
  );
  return hit ? { nodeId: hit.id, properties: target } : undefined;
}

/**
 * The kit property a knob names, when the kit models it as a property rather
 * than an axis — so there is no sibling node to compare against, and the miss
 * is a limit of references rather than a gap in the kit.
 *
 * `coversVariant` says the property's default already equals what this variant
 * seeds: the reference draws the VARIANT, which means it is the base pair
 * beside it that depicts something its render never claimed.
 */
export function propertyForSeed(ref, seed) {
  const set = setForRef(ref);
  if (!set) return undefined;
  const props = matchProperty(set.properties, seed.key);
  if (!props) return undefined;
  const raw = String(seed.raw).toLowerCase();
  const seeded = TRUTHY.has(raw) ? true : FALSY.has(raw) ? false : undefined;
  return {
    setName: set.name,
    properties: props.map(({ name, type, default: dflt }) => ({ name, type, default: dflt })),
    coversVariant:
      seeded !== undefined &&
      props.every((p) => p.type === "BOOLEAN" && p.default === seeded),
  };
}

/**
 * Optional content the kit switches ON by default, which every render made from
 * this reference therefore includes whether or not the code does.
 */
export function defaultedContent(ref) {
  const set = setForRef(ref);
  if (!set) return [];
  return Object.entries(set.properties ?? {})
    .filter(([, def]) => def.type === "BOOLEAN" && def.default === true)
    .map(([name]) => ({ name, setName: set.name }));
}

/** Which design-map slot a knob fills: the schema tags refs by state/size/theme. */
const SIZE_KNOBS = new Set(["size", "shape"]);

/**
 * The kit node for `seed` applied to the component referenced by `ref`, or
 * undefined when the kit models no such axis — a badge's digit count, a top app
 * bar's action count. Those are real gaps, reported rather than guessed at.
 */
function resolveSetVariantRef(baseVar, seeds) {
  const set = setIndex.get(baseVar.setId);
  const eq = (a, b) => String(a).toLowerCase() === String(b).toLowerCase();
  const search = (index, target, usedAxes) => {
    if (index === seeds.length) {
      return set.children.find((v) => {
        const vv = variantIndex.get(v.id);
        return Object.keys(target).every((a) => eq(vv.axes[a], target[a]));
      });
    }
    const seed = seeds[index];
    for (const axis of axisCandidates(seed.key, baseVar.axes, seed.raw)) {
      if (usedAxes.has(axis)) continue;
      for (const want of valueCandidates(seed.raw)) {
        const noOp = eq(baseVar.axes[axis], want);
        // Some shared matrices spell their default size explicitly in a
        // combination (`size=s, width=narrow, shape=square`). That seed is a
        // valid no-op there, but a one-axis no-op is only a duplicate of base.
        if (noOp && !(seeds.length > 1 && seed.key === "size")) continue;
        const match = search(
          index + 1,
          noOp ? target : { ...target, [axis]: want },
          new Set([...usedAxes, axis]),
        );
        if (match) return match;
      }
    }
    return undefined;
  };
  const match = search(0, baseVar.axes, new Set());
  return match
    ? {
        nodeId: variantIndex.get(match.id).renderId ?? match.id,
        name: variantIndex.get(match.id).name,
      }
    : undefined;
}

/**
 * The node that Figma can actually export for a definition reference.
 *
 * Most definitions render directly. Hidden component sets are the exception:
 * the kit keeps their definitions as vocabulary and places visible instances
 * on the component page as the renderable examples.
 */
export function renderableRef(ref) {
  const slash = ref.lastIndexOf("/");
  if (slash < 0) return ref;
  const variant = variantIndex.get(ref.slice(slash + 1));
  return variant?.renderId ? `${ref.slice(0, slash + 1)}${variant.renderId}` : ref;
}

/**
 * The kit node reached by applying every seed in order to `ref`.
 *
 * Discovery preserves the matrix annotation's axis order. Every seed must map
 * to a distinct Figma axis and the exact resulting vector must exist in the
 * kit's component set.
 */
export function resolveVariantRef(ref, seedOrSeeds) {
  const seeds = Array.isArray(seedOrSeeds) ? seedOrSeeds : [seedOrSeeds];
  // Standalone-folder siblings are complete configurations, not independently
  // composable axes. Applying `subhead` and then `inset` would merely walk from
  // one sibling to another and falsely call the last one their combination.
  // Without an exact compound component to target, leave a multi-seed
  // standalone render unmapped.
  const nodeId = ref.split("/")[1];
  const baseVar = variantIndex.get(nodeId);
  if (baseVar) {
    // Axes are the stronger signal: they name an exact sibling definition.
    // Only project a seed onto component properties when the set cannot
    // express it as an axis. This prevents `Icon (selected)` from stealing the
    // real `Selected` axis, or a `Segments` slot from stealing a count axis.
    const exactAxisHit = resolveSetVariantRef(baseVar, seeds);
    if (exactAxisHit) return exactAxisHit;

    const set = setIndex.get(baseVar.setId);
    const propertySeeds = seeds.filter(
      (seed) =>
        !resolveSetVariantRef(baseVar, [seed]) &&
        matchProperty(index.sets[baseVar.setId].properties, seed.key),
    );
    const axisSeeds = seeds.filter((seed) => !propertySeeds.includes(seed));
    const axisHit = axisSeeds.length ? resolveSetVariantRef(baseVar, axisSeeds) : undefined;
    if (axisSeeds.length && !axisHit) return undefined;
    const definitionId = axisHit
      ? set.children.find(
          (child) => (variantIndex.get(child.id).renderId ?? child.id) === axisHit.nodeId,
        )?.id
      : nodeId;
    if (propertySeeds.length && definitionId) {
      const propertyHit = resolvePropertyInstance(
        index.sets[baseVar.setId],
        definitionId,
        propertySeeds,
      );
      if (propertyHit) {
        return {
          nodeId: propertyHit.nodeId,
          name: `${variantIndex.get(definitionId).name} (configured instance)`,
        };
      }
      return undefined;
    }
    return axisHit;
  }
  if (seeds.length > 1) return undefined;
  const siblings = componentSiblings(nodeId);
  const hit = siblings && matchSibling(siblings, seeds[0]);
  if (hit) {
    return { nodeId: hit.id, name: hit.name };
  }
  return undefined;
}

/** `size` for a size/shape knob, `state` for everything else. */
export function slotFor(seedOrSeeds, variantName) {
  const seeds = Array.isArray(seedOrSeeds) ? seedOrSeeds : [seedOrSeeds];
  return seeds.length === 1 && SIZE_KNOBS.has(seeds[0].key)
    ? { size: String(seeds[0].raw) }
    : { state: variantName };
}
