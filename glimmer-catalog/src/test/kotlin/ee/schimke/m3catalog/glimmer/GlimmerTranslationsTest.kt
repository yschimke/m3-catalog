package ee.schimke.m3catalog.glimmer

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * `:catalog`'s `CatalogTranslationsTest`, for this module's Android resource lane.
 *
 * The failure it exists to catch is silent: a locale that loses a key does not break the build, it
 * renders that one string in English, and on a sheet of stickers nobody sees it. The published
 * catalog then claims a translation it does not have.
 *
 * Four invariants, all cheap:
 * * the module ships **exactly** the 17 locales the repository does — deleting one is as deliberate
 *   an act as adding one;
 * * every locale carries **exactly** the keys `values/strings.xml` declares — no silent English
 *   fallback, and no orphan key from a rename that only landed in one file;
 * * no locale leaves a string **untranslated by copy-paste** beyond the handful that legitimately
 *   are the same word;
 * * every declared key is **rendered** by a sticker, so the resource file cannot accumulate copy no
 *   render paints, and every language literal in the sources goes through a resource.
 *
 * The XML is read with a regex rather than a DOM, for the same reason the Material side does: these
 * files are generated-shaped and flat, and the test should not drag a parser into a module whose
 * only test dependency is kotlin-test.
 */
class GlimmerTranslationsTest {

  private val resourceRoot = File("src/main/res")
  private val sourceRoot = File("src/main/kotlin")

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

  /** Pinned rather than derived, so losing a locale fails as loudly as adding one. */
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

  /**
   * The floor under the scans. Three tests below assert that a *found* set is empty, so a pattern
   * that stops matching would pass them while checking nothing.
   */
  @Test
  fun theScansFindTheSource() {
    assertTrue(defaults.size >= 20, "values/strings.xml declares only ${defaults.size} strings")
    for (locale in locales) {
      assertTrue(
        stringsOf(locale).size >= 20,
        "${locale.name}/strings.xml yielded only ${stringsOf(locale).size} strings — the <string> " +
          "pattern has stopped matching, and the untranslated-copy check has nothing to compare",
      )
    }
    val sources = sourceRoot.walkTopDown().filter { it.extension == "kt" }.toList()
    assertTrue(
      sources.size >= 10,
      "only ${sources.size} source files — the visible-literal scan has lost the source tree",
    )
  }

  @Test
  fun shipsTheDeclaredLocales() {
    assertEquals(expectedLocales, locales.map { it.name })
  }

  @Test
  fun everyLocaleCarriesEveryKey() {
    assertTrue(defaults.isNotEmpty(), "values/strings.xml declares no strings")
    for (locale in locales) {
      assertEquals(
        defaults.keys.sorted(),
        stringsOf(locale).keys.sorted(),
        "${locale.name} does not carry the same keys as values/strings.xml",
      )
    }
  }

  /**
   * Words a real translator would leave alone. Kept per-locale and narrow on purpose: widening a
   * global set to cover one language is how this test stops catching anything.
   */
  private val sameAsEnglishAllowedPerLocale =
    mapOf(
      // "Microphone" is the French word too. Every other locale translates it.
      "values-fr" to setOf("cd_microphone")
    )

  @Test
  fun nothingIsLeftUntranslated() {
    for (locale in locales) {
      val allowed = sameAsEnglishAllowedPerLocale[locale.name].orEmpty()
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

  private val sources: String by lazy {
    sourceRoot.walkTopDown().filter { it.extension == "kt" }.joinToString("\n") { it.readText() }
  }

  @Test
  fun everyKeyIsUsedBySomeSticker() {
    val unused = defaults.keys.filterNot { sources.contains("R.string.$it") }.sorted()
    assertEquals(emptyList(), unused, "values/strings.xml declares copy no sticker renders")
  }

  /**
   * The sources with their comments removed.
   *
   * The scan below looks for `Text("…")`, and this module's comments quote exactly that to explain
   * what a sticker used to draw. A prose example is not a render, so it must not read as one — and
   * neither may a commented-out call, which is the case this really guards.
   */
  private fun withoutComments(source: String): String =
    source.replace(Regex("""/\*[\s\S]*?\*/"""), "").replace(Regex("""//[^\n]*"""), "")

  /**
   * Language-bearing copy must enter a visible text or accessibility slot through a resource.
   *
   * There are no exceptions today, and that is worth keeping: the carve-out `AGENTS.md` makes is
   * for token names and sample data that is not language, and nothing this module draws is in that
   * set — even the grocery and calendar rows are words a reader reads.
   *
   * Three shapes, because this module writes all three. `Text("…")` and a named `contentDescription
   * = "…"` are what `:catalog` scans for; a Glimmer `Icon` and a Compose `Image` take their
   * description POSITIONALLY as the second argument, and a scan that knew only the named form
   * passed a sheet with `Image(HeaderImage, "Header artwork", …)` still rendering English in every
   * locale.
   */
  @Test
  fun visibleLiteralsAreDeliberate() {
    val pattern =
      Regex(
        """(?:\b(?:Text|BasicText)\s*\(\s*(?:text\s*=\s*)?|""" +
          """\bcontentDescription\s*=\s*|""" +
          """\b(?:Icon|Image)\s*\(\s*[^,()"]{1,80}(?:\([^()]*\))?\s*,\s*)"((?:\\.|[^"\\])*)""""
      )
    val found =
      sourceRoot
        .walkTopDown()
        .filter { it.extension == "kt" }
        .flatMap { file ->
          pattern.findAll(withoutComments(file.readText())).map { file.name to it.groupValues[1] }
        }
        .toSet()
    assertEquals(
      emptySet(),
      found,
      "Visible string literals must resolve from a string resource",
    )
  }
}
