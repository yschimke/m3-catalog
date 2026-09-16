/*
 * Where `:glimmer-catalog`'s user-visible copy comes from.
 *
 * `AGENTS.md`: "User-visible language copy is a string resource, never a literal." This module
 * shipped its labels, titles and `contentDescription`s as literals until #358, which is not a
 * cosmetic difference — a sticker sheet that hardcodes English cannot be published in a locale, and
 * the rule protects the SOURCE whether or not a locale axis is rendered today.
 *
 * ## Android resources, not compose-resources
 *
 * `:catalog` resolves `Res.string.…` from `composeResources`, which is the Compose Multiplatform
 * lane. This module is AGP with no multiplatform plugin, so its native lane is `res/values*` and
 * `androidx.compose.ui.res.stringResource` — the same 17 locales, the same rule, through the
 * mechanism the module actually has. `GlimmerTranslationsTest` is the guard `CatalogTranslationsTest`
 * is on the other side: a locale that loses a key renders that string in English and nothing else
 * goes wrong, which is precisely why it needs a test rather than a reviewer.
 *
 * ## What stays a literal
 *
 * Token names and sample data that is not language — the same carve-out `AGENTS.md` makes. Nothing
 * in this module is in that set today: even the grocery and calendar rows are words a reader reads.
 *
 * `:glimmer-samples` is out of scope and always will be: that tree is upstream's bytes, vendored
 * byte-identically from a pinned commit, and rewriting its copy would break the property the whole
 * import rests on.
 */
package ee.schimke.m3catalog.glimmer

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import ee.schimke.composeai.overrides.previewOverrideString
import java.text.DecimalFormatSymbols

/**
 * User-visible copy whose baked default stays translated and whose value is editable on the live
 * lane — `:catalog`'s `catalogText`, for this module.
 *
 * [default] must already have been resolved from a string resource by the caller, so the published
 * PNG keeps the locale's copy while the held session publishes [key] as a plain text control. Keys
 * name the component parameter or slot (`label`, `title`, `supportingLabel`) rather than the
 * resource, so the Overrides panel reads like the API being explored.
 */
@Composable
fun glimmerText(key: String, default: String): String = previewOverrideString(key, default)

/**
 * `:catalog`'s `localizedDigits`, for this module.
 *
 * A translated time string carries the locale's words — `08:00 – 08:30` against `8 – 8:30 AM` — but
 * its DIGITS are whatever was typed into the resource file, and that is not the same question. A
 * locale can name its numbering system explicitly (`hi-IN-u-nu-deva`), and Arabic uses Arabic-Indic
 * digits by default; a resource file cannot answer either, because the answer is a property of the
 * configuration the render runs in rather than of the language.
 *
 * So the resources hold ASCII digits and this shapes them at render time, leaving the punctuation
 * and the translated words alone. `AGENTS.md` requires exactly this of numeric samples, and a
 * rendered clock value is one whether it arrives as a literal or from a resource.
 */
@Composable
fun localizedDigits(value: String): String {
  val locale = LocalConfiguration.current.locales[0]
  val zero = DecimalFormatSymbols.getInstance(locale).zeroDigit
  if (zero == '0') return value
  return buildString(value.length) {
    value.forEach { char ->
      append(if (char in '0'..'9') (zero.code + (char - '0')).toChar() else char)
    }
  }
}
