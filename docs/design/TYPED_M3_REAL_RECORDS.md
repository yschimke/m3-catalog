# Typed Material 3 export on the real records

The next step for the typed pilot ([`TYPED_M3_CATALOG.md`](TYPED_M3_CATALOG.md)): its Compose
source export moves off the builder's hand-written `compose-material3` tables onto typed
definitions on the **real** Compose Material 3 composables, each carrying an `ExportProjection`
the builder interprets. It is the approach wear-m3-catalog's pilot takes
(`docs/design/TYPED_WEAR_REAL_RECORDS.md` there), following compose-ai-tools'
`docs/design/TYPED_EXPORT_ADOPTION.md` checklist. Production `m3-catalog` is unchanged: its policy
keeps `compose-material3` v1 and its committed JSON; only the `m3-catalog-typed-pilot` pair selects
`compose-component-records` v1.

## Records

`RealM3Records` reads the real composables from the library metadata on `catalog-typed-adapters`'
JVM classpath (`TypedAdapterDiscovery.discoverLibrary`, compose-ai-tools 2.52.0). The Compose
Multiplatform desktop artifact publishes the same `androidx.compose.material3` API the Android
export calls. Every non-deprecated overload is published on the record; the record's own
parameters are the overload a `LibraryCallable` picks:

| Component | Record | Picked by |
| --- | --- | --- |
| `m3/text` | `material3.Text` | `text: String` and `color: Color`, not `AnnotatedString` or `ColorProducer` |
| `m3/icon` | `material3.Icon` | `imageVector` and `tint: Color` |
| `m3/button` | `Button`, `FilledTonalButton`, `TextButton`, `FloatingActionButton` | the `shape` overloads, not the expressive `shapes: ButtonShapes` ones |
| `m3/card` | `Card`, `ElevatedCard`, `OutlinedCard` | not `onClick` (the clickable overload is chosen when a click is bound) |
| `m3/navigation-suite-item` | `navigationsuite.NavigationSuiteItem` | its only overload |
| `m3/navigation-suite-scaffold` | `navigationsuite.NavigationSuiteScaffold` | `navigationItems` and `primaryActionContent`, not the `NavigationSuiteScope` DSL |

The card projections call `CardDefaults.{card,elevatedCard,outlinedCard}{Colors,Elevation}` and the
button projections `ButtonDefaults.{button,filledTonalButton,textButton}Colors`, read with
`discoverFactories`. The shared layouts (`layout/column`, `layout/box`, `layout/scaffold`,
`layout/lazy-grid` and the two pane scaffolds) are not published here: a host joins
compose-ui-builder's foundation record pack, and `RealM3ExportTest` joins it the same way, from the
builder checkout's `compose-foundation-components-v1.json`.

These are the components the committed designs under `ui-builder/designs/` use, plus `m3/button`,
which every adaptive design homeassistant-remotecompose and meshcore-mobile pin to `m3-catalog`
uses: with it, all 38 of those export through the pilot too.

## The definitions

`real/RealM3Adapters.kt` defines them, each validated against its record (`validateAgainst` is
empty in `RealM3AdaptersTest`):

| Component | Writes |
| --- | --- |
| `m3/text` | `text`, `style` (`MaterialTheme.typography.<role>`), `color`, weight, style, sizes in `sp`, alignment, decoration, line limits, `softWrap`, `overflow` |
| `m3/icon` | `Icons.<style>.<Name>` from the host's icon table, `contentDescription`, `tint`, `Modifier.size(sizeDp.dp)` when set |
| `m3/button` | the `style`'s button as the hand exporter picks it (`filled` or unset `Button`, `filledTonal`, `text`, `fab`), `enabled` when false, `ButtonDefaults.…Colors(containerColor)` (a FAB's own `containerColor`), `onClick`; children are its `RowScope` content |
| `m3/card` | the `style`'s card, `CardDefaults.…Colors(containerColor)`, `CardDefaults.…Elevation(defaultElevation)`; children are its `ColumnScope` content |
| `m3/navigation-suite-item` | `selected` (a literal or a `stateEquals` read), `enabled`, `onClick`, the `icon` and `label` slots |
| `m3/navigation-suite-scaffold` | `containerColor`, `contentColor`, `navigationItems`, `primaryAction` as `primaryActionContent`, `content` |

## Publication

`TypedM3Catalog(…, RealM3Records.publication())` publishes, as Wear's pilot does: each covered
shelf component carries its definition's `exportProjection` (the wrapper still draws it), the real
records join `components.json` as export targets kept off the shelf (`m3/export-…`, excluded), and
the policy selects `composeSourceExport: compose-component-records` v1. There is **no `code`
block**: an M3 screen's root is its own scaffold, so there is no frame around it, no locals and no
item scope.

## Every committed design

`RealM3ExportTest` exports every design under `ui-builder/designs/` through compose-ui-builder's
`ScreenExportGate` with the generated pair, the way the editor exports it. **All six emit**:

| Design | Exports through the projections |
| --- | --- |
| `adaptive-navigation` | `NavigationSuiteScaffold(navigationItems = { NavigationSuiteItem(…, icon = { Icon(…) }, label = { Text(…) }) })` |
| `adaptive-feed` | three `Card { Column { Text, Text } }` in the builder's `LazyVerticalGrid` |
| `hello`, `list-detail`, `supporting-pane` | their `Text`s, inside the builder's scaffold and pane scaffolds |
| `blank` | nothing projected: a `Scaffold` and a `Box` |

None is refused. The test fails when a design changes outcome, so a refusal has to be added to its
`REFUSED` table, with the reason documented here, rather than appear silently.

## Still refused or written differently

Each is refused by name rather than approximated, or written differently on purpose:

- A card's `shape`: the hand exporter resolves a shape token, a constant or a corner radius through
  three tables, which one codec cannot state yet. Refused (`exportRefuses`), and pinned by a test.
- A card's `stableKey` (the lazy item key the canvas uses) and `onClickAction`: no card parameter
  carries them. A bound click (`eventBindings.click`) still picks the clickable overload.
- A text's `alignment`: the hand exporter writes `Modifier.align(…)` through the parent box's
  `BoxScope`, which a projection's top-level modifier link cannot name. `weight` is the builder's
  own and still exports.
- A button's `selected`: a toolbar item's selection the canvas draws, which no button parameter
  carries. Refused (`exportRefuses`).
- An icon's Material Symbols properties (`iconName`, `iconStyle`, the four axes, `iconAutoMirror`):
  they draw an outline the host records in the design's assets, which no `Icon` parameter carries.
- A card's children are its `ColumnScope` content directly. The hand exporter wraps them in a `Box`
  (as the canvas draws them) so each child gets `BoxScope`; for the single column every committed
  card holds, the two lay out the same.

## Verification

`RealM3AdaptersTest` (records, validation, lowering), `RealM3ExportTest` (every design through
`ScreenExportGate`, the emitted Kotlin kept under `catalog-typed-adapters/build/export-evidence/`)
and `PublishedM3CatalogTest` (the published reader composes the pair with its export targets) run in
`scripts/validate-typed-m3-catalog.sh`, against compose-ai-tools v2.52.0 and compose-ui-builder
v3.109.0. The emitted Kotlin is not compiled here.

## Order

1. Records and definitions for the components the committed designs use (this change).
2. The pilot pair publishing them, and every committed design exported (this change).
3. The pilot published to `m3-catalog-out`'s `design-artifacts/m3-catalog-typed-pilot`, beside
   production, for an A/B comparison of the two exports (this change). `typed-pilot-artifacts.yml`
   builds the pair, the runtime and `FullM3RendererTest`'s `<case>-typed.png` renders, and
   compose-ai-tools' `typed-catalog-artifacts-reusable.yml` publishes them; the renders are the
   catalog's images, without which a preview server refuses to load it.
4. The rest of the shelf, then cutover: the production policy, runtime and `composeSourceExport`
   switch together.
