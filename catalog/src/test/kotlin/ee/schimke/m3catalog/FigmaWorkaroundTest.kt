package ee.schimke.m3catalog

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Keeps [FigmaWorkaround] from becoming the place differences go to be forgotten.
 *
 * A registered cover-up is a debt: the sticker publishes something the component does not produce,
 * and the only thing keeping that visible is the entry and the issue it names. So each entry has to
 * name a real tracking bug, and — the part that rots — has to still be used. An entry whose call
 * site was deleted is a difference that has quietly been fixed or quietly been re-hidden somewhere
 * else, and either way nobody is going to reopen the issue to find out.
 *
 * The complement is not testable here and is deliberate: nothing stops a section file padding a
 * component without registering it. What this test buys is that the registry itself cannot drift.
 */
class FigmaWorkaroundTest {

  private val sources =
    File("src/main/kotlin/ee/schimke/m3catalog")
      .walkTopDown()
      .filter { it.isFile && it.extension == "kt" }
      .filter { it.name != "FigmaWorkaround.kt" }
      .map { it to it.readText() }
      .toList()

  @Test
  fun theSourceScanFindsTheStickers() {
    assertTrue(
      sources.size >= 40,
      "the sticker scan yielded only ${sources.size} files — the walk, not the catalog, is what " +
        "changed",
    )
  }

  @Test
  fun everyRegisteredWorkaroundNamesATrackingBug() {
    for (entry in FigmaWorkaround.entries) {
      assertTrue(
        entry.issue > 0,
        "${entry.name} has no tracking bug — a cover-up without one is just a difference nobody " +
          "is going to find again",
      )
      assertTrue(entry.summary.isNotBlank(), "${entry.name} has no summary")
    }
  }

  @Test
  fun everyRegisteredWorkaroundIsStillApplied() {
    val unused =
      FigmaWorkaround.entries.filter { entry ->
        sources.none { (_, text) -> text.contains("FigmaWorkaround.${entry.name}") }
      }
    assertEquals(
      emptyList(),
      unused.map { it.name },
      "registered but applied nowhere — if the component was fixed, delete the entry and close " +
        "its issue; if the cover-up moved, register it where it now lives",
    )
  }

  /**
   * Every call site names an entry, which is what makes the registry a complete list of the
   * cover-ups rather than a sample of them. The helper's signature already requires a
   * [FigmaWorkaround]; this catches the other spelling — a bare `figmaWorkarounds()` read used to
   * gate something without registering it.
   */
  @Test
  fun nothingGatesOnTheFlagDirectly() {
    val direct =
      sources
        .filter { (_, text) -> text.contains("figmaWorkarounds()") }
        .map { (file, _) -> file.name }
    assertEquals(
      emptyList(),
      direct,
      "reads the flag without naming a FigmaWorkaround entry — use figmaWorkaround(entry, " +
        "upstream = …, workaround = …) so the difference lands in the registry",
    )
  }
}
