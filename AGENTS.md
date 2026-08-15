# Repository instructions for AI agents

Two handover docs, for two different problems:

- **Render too slow / timing out?** [docs/PARALLEL_RENDER.md](docs/PARALLEL_RENDER.md) — shards the
  render across parallel jobs. This is the one that addresses the timeout.
- **Adding component coverage?** [docs/PARALLEL_SWEEP.md](docs/PARALLEL_SWEEP.md) — divides the
  authoring work, with a claim protocol. It does *not* help with the timeout.

Read [README.md](README.md) first — it describes what this repo is and how it is laid out. This
file records the conventions that are easy to violate by accident.

## Annotation-first is the rule, not a preference

The catalog inventory lives in annotations next to the composables. **Do not** reintroduce a
`groups` array in `catalog.spec.json` to add, rename or recaption a component — put it on the
`@CatalogComponent` / `@CatalogVariant`. The spec is layered over the annotations as a field-level
override and exists for cover-sheet fields only.

If you find yourself writing a lot of mapping config to express something, that is a signal the
upstream libraries are missing an annotation — **raise it in
[compose-ai-tools](https://github.com/yschimke/compose-ai-tools) and add the annotation there**
rather than growing a JSON file here. The same rule applies to the CI pipeline: a capability any
catalog could want belongs as a generic input on the reusable `design-artifacts-reusable.yml`
workflow, never as a forked copy of the pipeline in this repo.

## Direction: design-led, and Figma is read-only

Two rules that together decide what "correct" means here.

**The kit is the source of truth.** `.design-parity.json` says `design-led`, so a parity finding is
a defect in this code, not a note about the kit. When the render and the kit disagree — the corner
radii are the live example, where `ButtonDefaults.shape` gives 20 against the kit's 16 — the code
moves. Recording the divergence and moving on is the *code-led* posture, and it is not this repo's.
Where Compose genuinely cannot express what the kit specifies, say so in the caption or the
component's KDoc rather than silently rendering something else.

**Never write to Figma.** Every interaction with the kit is read-only: the REST API for node ids
and reference images, the MCP server for variables and metadata. Do not call `use_figma`,
`create_new_file`, `upload_assets`, `add_code_connect_map`, `send_code_connect_mappings`, or any
other mutating Figma tool, and do not enable design-parity's Code-to-Canvas push-back. The
`design-led` direction already gates that push-back off, so the config and the convention agree —
keep them that way.

## Sticker conventions

- One file per component **group**, opening with `@file:CatalogGroup(name = …, section = …)`.
- Every `@CatalogComponent` carries a `caption`. A component with no caption publishes as a bare
  picture, and a test fails the build for it.
- **Fold variants behind defaults.** A state / content axis is a `@CatalogVariant(of = …)` under its
  parent, not a new top-level component. A system this size is only browsable because the card count
  tracks components, not renders.
- Component ids are the published sticker's URL and the join key for `@CatalogVariant(of = …)`.
  Renaming one moves a published URL — do it deliberately.
- **No dead handlers.** Stateful components own their state (`toggleable` / `selectable` /
  `draggable` / `editable`); everything else takes `counted`. Disabled stickers stay inert on
  purpose. See `CatalogInteractive.kt`.
- Renders must be **deterministic**: a date picker is pinned to a fixed instant, a time picker to
  10:10. An unpinned picker would open on "today" and every nightly render would differ from the
  last, which turns the delivery branch's history into noise.
- **User-visible language copy is a string resource, never a literal.** A sticker's labels, titles,
  supporting text and `contentDescription`s resolve with `stringResource(Res.string.…)` from
  `catalog/src/main/composeResources/values*/strings.xml`. Adding a string means adding it to
  `values/` **and to all 17 locale files** — `CatalogTranslationsTest` fails the build for a key
  that is missing from a locale, left as the English copy, or declared and never rendered. What
  stays a literal: token names (`primary`, `Display Large`, `XS`) and sample data that isn't
  language (`alice@example.com`, `⌘E`, person names). Numeric samples such as `10:30` and badge
  counts pass through `localizedDigits(...)` so the locale's numbering system still applies.
- Components hosted in their own platform window (dialogs, modal sheets, dropdown menus) cannot be
  reached by a single-surface capture. Compose the component's **container** from its own
  `*Defaults` shape / colour / elevation and say so in a comment — never a hand-drawn lookalike, and
  never an entry that renders nothing.

## Kotlin

- ktfmt Google style, 100 columns. `./gradlew ktfmtFormat`.
- Kotlin block comments **nest**, so `/*` inside a KDoc opens a nested comment and swallows the rest
  of the file. Write `M3.sys.light.primary`, not `M3/sys/*`. This has bitten this repo already.

## Git

- **`main` is protected — every change goes through a PR.** The `Protect Main` ruleset requires a
  pull request (0 approvals) with all CI checks green, and squash is the only merge method, so
  `main` stays one commit per change. Branch names are `agent/…`.
- Conventional commit subjects (`feat:`, `fix:`, `docs:`, `chore:`). The squash commit is built from
  the **PR title**, so the PR title is what lands on `main` — write it as the commit subject.
- **Never attribute a commit to an AI agent** — no `Co-authored-by:` trailer naming an agent, and
  no agent author/committer identity. Links to an agent session and the
  `_Generated by [Claude Code]_` footer are fine; they don't claim authorship. Enforced by the
  hooks in `.githooks/` (install with `scripts/install-git-hooks.sh`).

## Dependencies

- **Renovate owns the version bumps; don't hand-bump.** `.github/renovate.json` automerges anything
  that is not a major once CI is green, so a bump you make by hand is a PR Renovate would have
  opened, reviewed and landed on its own. Change the config instead when the policy is wrong.
- Two groups are deliberately **not** automerged: **majors**, and **Compose Multiplatform** (with its
  independently-versioned material3 artifact). Both change what the catalog renders, and the render
  is the product — a human reads the visual diff before it lands.
- `compose-ai-tools` is the exception in the other direction: the CLI, the Gradle plugin marker, the
  `preview-annotations` coord and the pinned CI action ref are one release and move together in a
  single PR, unscheduled and automerged. A skew between them breaks preview discovery outright.
- Repository settings — squash-only merges, auto-merge, and the `Protect Main` ruleset that
  automerge depends on — are applied by `scripts/setup-repo-protection.sh`. They need an admin
  token, so no workflow (and no agent session) can set them; the script is the record of what they
  are meant to be, and re-running it repairs drift. `DRY_RUN=1` prints without writing.

## Verifying a change

```sh
./gradlew :catalog:assemble :catalog:composePreviewDiscover test ktfmtCheck
node scripts/generate-design-map.mjs
```

`composePreviewDiscover` is the real contract: it is what turns the annotations into the published
inventory. A component that compiles but is not discovered vanishes from the sheet silently.
