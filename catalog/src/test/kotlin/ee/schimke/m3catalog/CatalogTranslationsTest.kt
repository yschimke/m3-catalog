package ee.schimke.m3catalog

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Guards the **translations** the same way [CatalogInventoryTest] guards the inventory: a locale
 * that silently loses a key does not fail the build, it just renders that one string in English. On
 * a sheet of ~200 stickers nobody spots it, and the published catalog quietly claims a translation
 * it doesn't have.
 *
 * Three invariants, all cheap:
 * * every locale carries **exactly** the keys `values/strings.xml` declares — no missing key
 *   (silent English fallback) and no orphan key (a rename that only landed in one file);
 * * no locale leaves a string **untranslated by copy-paste** beyond the handful that legitimately
 *   are the same word in English (`OK`, `Tonal`, product names);
 * * every declared key is actually **referenced** by a sticker, so the resource file doesn't
 *   accumulate copy no render ever paints.
 *
 * The XML is parsed with a regex rather than a DOM: these files are generated-shaped and flat, and
 * the test wants to run without dragging a parser dependency into a module whose only test deps are
 * kotlin-test and compose-ui-test.
 */
class CatalogTranslationsTest {

  private val resourceRoot = File("src/main/composeResources")

  private val stringPattern =
    Regex("""<string name="([^"]+)">(.*?)</string>""", RegexOption.DOT_MATCHES_ALL)

  private fun stringsOf(dir: File): Map<String, String> =
    stringPattern.findAll(File(dir, "strings.xml").readText()).associate {
      it.groupValues[1] to it.groupValues[2]
    }

  private val defaults: Map<String, String> = stringsOf(File(resourceRoot, "values"))

  private val locales: List<File> =
    resourceRoot
      .listFiles { f: File -> f.isDirectory && f.name.startsWith("values-") }!!
      .sortedBy { it.name }

  /**
   * The locale set the catalog ships. Pinned rather than derived so that *deleting* a locale is a
   * deliberate act that fails this test, the same way adding one is.
   */
  private val expectedLocales =
    listOf(
      "values-ar",
      "values-de",
      "values-es",
      "values-fr",
      "values-hi",
      "values-id",
      "values-it",
      "values-ja",
      "values-ko",
      "values-nl",
      "values-pl",
      "values-pt-rBR",
      "values-ru",
      "values-th",
      "values-tr",
      "values-zh-rCN",
      "values-zh-rTW",
    )

  @Test
  fun shipsTheDeclaredLocales() {
    assertEquals(expectedLocales, locales.map { it.name })
  }

  @Test
  fun everyLocaleCarriesEveryKey() {
    assertTrue(defaults.isNotEmpty(), "values/strings.xml declares no strings")
    for (locale in locales) {
      val translated = stringsOf(locale)
      assertEquals(
        defaults.keys.sorted(),
        translated.keys.sorted(),
        "${locale.name} does not carry the same keys as values/strings.xml",
      )
    }
  }

  /**
   * Strings that are legitimately identical to English in some locale: a product name, an
   * international abbreviation, or a loanword a real translator would leave alone. Anything else
   * matching English is an untranslated copy-paste.
   */
  private val sameAsEnglishAllowed =
    setOf(
      "action_menu",
      "action_ok",
      "appbar_title",
      "field_email",
      "field_name",
      "label_account",
      "label_on",
      "label_text",
      "label_tonal",
      "list_overline",
      "nav_home",
      "nav_search",
      "search_suggestion_symbols",
      "search_suggestion_motion",
      "segment_week",
      "sheet_details",
      "tab_reviews",
      "tab_specs",
    )

  /**
   * Per-locale additions to [sameAsEnglishAllowed] — words that happen to be spelled the same in
   * one language and would be a genuine miss in any other. Kept narrow on purpose: widening the
   * global set to cover a single locale is how this test stops catching anything.
   */
  private val sameAsEnglishAllowedPerLocale =
    mapOf(
      "values-fr" to setOf("action_action", "divider_section"),
      "values-id" to setOf("action_edit"),
    )

  @Test
  fun nothingIsLeftUntranslated() {
    for (locale in locales) {
      val allowed = sameAsEnglishAllowed + sameAsEnglishAllowedPerLocale[locale.name].orEmpty()
      val untranslated =
        stringsOf(locale)
          .filter { (key, value) -> value == defaults[key] && key !in allowed }
          .keys
          .sorted()
      assertEquals(
        emptyList(),
        untranslated,
        "${locale.name} repeats the English copy for these keys",
      )
    }
  }

  @Test
  fun everyKeyIsUsedBySomeSticker() {
    val sources =
      File("src/main/kotlin")
        .walkTopDown()
        .filter { it.name.endsWith(".kt") }
        .joinToString("\n") { it.readText() }
    val unused = defaults.keys.filterNot { sources.contains("Res.string.$it") }.sorted()
    assertEquals(emptyList(), unused, "values/strings.xml declares copy no sticker renders")
  }

  /**
   * Language-bearing copy must enter a visible text or accessibility slot through a resource. These
   * are the deliberate exceptions: person data and a platform keyboard shortcut. Numeric samples
   * are not allowlisted because they must pass through `localizedDigits(...)` instead.
   */
  private val visibleLiteralAllowed =
    mapOf(
      "Dialogs.kt" to setOf("A"),
      "Lists.kt" to setOf("⌘C"),
      "Menus.kt" to setOf("⌘C"),
    )

  private val visibleLiteralPattern =
    Regex(
      """(?:\b(?:Text|BasicText)\s*\(\s*(?:text\s*=\s*)?|\bcontentDescription\s*=\s*)"((?:\\.|[^"\\])*)""""
    )

  @Test
  fun visibleLiteralsAreDeliberate() {
    val sections = File("src/main/kotlin/ee/schimke/m3catalog/sections")
    val found =
      sections
        .walkTopDown()
        .filter { it.extension == "kt" }
        .flatMap { file ->
          visibleLiteralPattern.findAll(file.readText()).map { file.name to it.groupValues[1] }
        }
        .toSet()
    val allowed =
      visibleLiteralAllowed.flatMap { (file, values) -> values.map { file to it } }.toSet()

    assertEquals(
      emptySet(),
      found - allowed,
      "Visible string literals must use a resource or an explicit locale-aware formatter",
    )
    assertEquals(
      allowed,
      found.intersect(allowed),
      "Remove stale visible-literal exceptions when their call sites disappear",
    )
  }
}
