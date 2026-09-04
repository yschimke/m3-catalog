# Keyboard navigation and the focus colours

Rendered with `./gradlew :catalog:composePreviewRender -PcomposePreview.filter=TimeInputSticker`,
light scheme, native pixels.

## `focus-walk.png`

`TimePicker/Input`, the four steps of `@FocusedPreview(traverse = [Next, Next, Next, Next])` left to
right, captured while investigating what a baked walk would look like. **The annotation is not in
this PR** — see "Why the walk is not baked" below. Each frame is a real
`FocusManager.moveFocus(Next)` against the composition — the same walk a Tab key performs —
captured after it lands:

1. the **hour** entry field, focused;
2. **OK**;
3. the **dial/keyboard** mode toggle;
4. **Cancel**.

Two things are worth reading off it. The focus order is the component's own, not one the catalog
arranges: the dialog's actions come before its mode toggle. And the treatment on steps 2–4 is the
10% state layer — the faint pill behind the label — which is exactly how legible it is at the
kit's own colours, and the reason the next image exists.

## `overlay-colour.png`

The same step 2, twice: as it publishes (left) and with `focusOverlayColour` set to `#E91E63`
(right). Nothing else differs — same walk, same frame, same scheme.

The knob is not on this sticker by default. It appears only once `keyboardNav` is on, which is the
knob that makes the preview take focus at all (`InputMode.Keyboard`, without which Compose's
`Focusability.SystemDefined` refuses focus in a preview host). So the reader who has asked to walk
the form with a keyboard is the one offered a colour to see where it landed, and every published
PNG here is byte-identical to what it published before, because both knobs default to off and
unspecified.

## Why the walk is not baked

Two findings, both measured, and together they are why this PR ships the live knob and not the
annotation.

`@FocusedPreview` **replaces** a preview's captures rather than adding to them. After annotating
`TimeInputSticker`, `previews.json` listed exactly four captures for it — the focus steps — and no
undriven still. So the catalog would publish a focused hour field as `TimePicker/Input`'s picture,
and the parity lane would diff that against the kit's resting `State=Input` node. A design catalog
cannot spend its component sticker on a state.

And both date pickers cannot carry it at all: under the annotation they compose a second root —
`Expected exactly '1' node but found '2' nodes that satisfy: (isRoot)`, the second always the full
945x2100 canvas — and the capture resolves the root to exactly one node, so *every* capture of those
previews fails, the undriven ones included. `DatePicker/Modal` published no PNG at all on the first
push of this PR. Moving the walk to `DateRangePicker`, which composes inline rather than in a
dialog, reproduced it exactly, so the dialog is not the cause.

Both wants a change where the annotation is defined rather than a workaround here: a focus walk
that keeps the resting capture beside its steps.

The `focus-ring` cells carry the same idea for the other treatment — `focusRingOuterColour` and
`focusRingInnerColour`, scoped by the `focus=ring` seed those cells already carry.
