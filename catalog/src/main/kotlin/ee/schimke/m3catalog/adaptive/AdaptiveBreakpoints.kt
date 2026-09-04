package ee.schimke.m3catalog.adaptive

import androidx.compose.ui.tooling.preview.Preview

/**
 * The catalog's breakpoint multipreview, for the adaptive layouts only.
 *
 * `@CatalogModes` renders light and dark at the harness's wrap-content width, which is the right
 * frame for a component whose picture does not depend on how much room it has. An adaptive layout
 * is the opposite case: the *only* thing these composables express is what they do as the window
 * grows, so a single width would publish one third of each of them.
 *
 * The three widths are the ones `catalog.spec.json` already documents as this catalog's breakpoints
 * — compact 412, medium 700, expanded 960 — so the renders and the cover sheet name the same
 * numbers rather than drifting apart. They sit on the boundaries Material's own window size classes
 * use (600 and 840), so each width lands squarely inside one class rather than on its edge.
 *
 * `heightDp` is pinned as well, and it is load-bearing rather than cosmetic: the pane scaffolds
 * fill the height they are given, and the wrap-content harness offers an unbounded one.
 *
 * `uiMode = 32` is `Configuration.UI_MODE_NIGHT_YES`, written as a raw int for the same reason
 * `@CatalogModes` does — the desktop source set has no Android `Configuration`.
 */
@Preview(name = "Compact Light", group = "breakpoints", widthDp = 412, heightDp = 720)
@Preview(name = "Compact Dark", uiMode = 32, group = "breakpoints", widthDp = 412, heightDp = 720)
@Preview(name = "Medium Light", group = "breakpoints", widthDp = 700, heightDp = 720)
@Preview(name = "Medium Dark", uiMode = 32, group = "breakpoints", widthDp = 700, heightDp = 720)
@Preview(name = "Expanded Light", group = "breakpoints", widthDp = 960, heightDp = 720)
@Preview(name = "Expanded Dark", uiMode = 32, group = "breakpoints", widthDp = 960, heightDp = 720)
annotation class CatalogBreakpoints
