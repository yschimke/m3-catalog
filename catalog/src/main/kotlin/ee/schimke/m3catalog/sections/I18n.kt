@file:CatalogGroup(name = "Scaffold templates", section = "Templates")

package ee.schimke.m3catalog.sections

// Group and section match `Templates.kt` on purpose: everything here is a `@CatalogVariant`
// folding under `Template/AppScaffold`, so these captures belong to that sticker's card rather
// than to a group of their own — a group whose only members are another component's variants
// would publish an empty card.

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant

// The catalog's copy lives in `src/main/composeResources/values*/strings.xml` — 17 translations
// beside the English source — so ANY sticker re-renders translated when the render spec carries a
// `localeTag`, and the preview server's locale control drives exactly that. Nothing here is needed
// for that to work.
//
// What these variants add is EVIDENCE, baked into the published sheet: a reader who never touches
// the locale control still sees that the catalog is translated, and a translation that regresses
// shows up as a render diff on the next PR instead of staying invisible until someone switches
// locale by hand. They fold under their parent sticker like every other axis, so the card count
// does not move.
//
// Two locales, chosen for what they prove rather than for coverage:
//   * **ja** — a non-Latin script, which is the case that exercises the font fallback as well as
//     the string lookup. If the copy resolved but the face could not draw it, this render would be
//     tofu and the diff would say so.
//   * **ar** — right-to-left. The desktop renderer resolves `localeTag` twice over (the
//     composition's `LocaleList`, which flips layout direction, and the JVM default `Locale`, which
//     is what CMP `stringResource(...)` reads), so this one capture carries BOTH halves: mirrored
//     layout and Arabic copy. A pseudolocale (`ar-XB`) would only carry the first — desktop CMP
//     pseudolocalises direction, not text.
//
// The template carries them: a full screen is where translated copy is legible at a glance — an app
// bar title, five list rows and a FAB, rather than one button label.

@CatalogVariant(
  of = "Template/AppScaffold",
  props = ["locale=ja"],
  caption =
    "i18n axis: rendered in Japanese — the same screen with its copy resolved from " +
      "values-ja/strings.xml.",
)
@Preview(name = "Light", device = "id:pixel_8", showSystemUi = true, locale = "ja", group = "i18n")
@Composable
fun AppScaffoldTemplateJa() = AppScaffoldTemplate()

@CatalogVariant(
  of = "Template/AppScaffold",
  props = ["locale=ar"],
  caption =
    "i18n axis: rendered in Arabic — translated copy AND mirrored right-to-left layout, the two " +
      "halves a locale override applies together.",
)
@Preview(name = "Light", device = "id:pixel_8", showSystemUi = true, locale = "ar", group = "i18n")
@Composable
fun AppScaffoldTemplateAr() = AppScaffoldTemplate()
