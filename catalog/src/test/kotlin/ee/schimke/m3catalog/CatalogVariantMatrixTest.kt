package ee.schimke.m3catalog

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Holds the hand-authored `@OverrideVariant` blocks to the matrices declared in `CatalogAxes.kt`.
 *
 * Three components' worth of variants — 237 annotations across `Buttons.kt`, `ToggleButtons.kt` and
 * `IconButtons.kt` — are cross products of two or three axes, retyped once per component. Nothing
 * in the compile relates them to [CatalogSize] / [CatalogShape] / [CatalogIconWidth], so adding a
 * size to the enum leaves thirteen blocks silently short a row each, and a mistyped seed produces a
 * render that is quietly a duplicate of another cell. Both failures show up only as a wrong sticker
 * sheet at the end of a long CI render, if at all.
 *
 * This asserts the set equality that the annotations were *meant* to satisfy: every cell of the
 * declared matrix has an annotation, and every annotation is a cell of the declared matrix.
 *
 * Two deliberate loosenings, so the test checks meaning rather than spelling:
 * * **Seeds are normalised against the axis defaults.** `IconButtons.kt` spells the default size
 *   explicitly in `s-narrow` (`strings = ["size=s", "width=narrow"]`) and implicitly in `s-square`
 *   (`strings = ["shape=square"]`). Both seed the same render — a knob set to its own default is
 *   what an unseeded knob already resolves to — so the comparison drops default-valued seeds from
 *   both sides rather than pinning one of the two spellings.
 * * **Order is not compared.** A variant's identity is its name; the authored blocks group all the
 *   selected cells before all the unselected ones, which no downstream consumer cares about.
 *
 * The source tree is read as text rather than through reflection, for the same reason
 * `CatalogInventoryTest` does: `@OverrideVariant` is `BINARY` retention, so it is not visible to
 * `java.lang.reflect` at all — discovery reads it with ClassGraph, and a source scan is both
 * sufficient here and independent of the render pipeline being available.
 */
class CatalogVariantMatrixTest {

  /** A component's authored variant block: its id, and the cells its annotations declare. */
  private data class Authored(val id: String, val file: String, val cells: Set<CatalogVariantCell>)

  private val sectionsDir = File("src/main/kotlin/ee/schimke/m3catalog/sections")

  /**
   * Splits a section file into `@CatalogComponent` blocks and reads each one's `@OverrideVariant`
   * annotations. The block ends at `@Composable`, which is what separates a component's annotation
   * stack from the function it annotates.
   */
  private fun authoredIn(fileName: String): List<Authored> {
    val text = File(sectionsDir, fileName).readText()
    return text
      .split(Regex("""(?=^@CatalogComponent\()""", RegexOption.MULTILINE))
      .drop(1)
      .mapNotNull { block ->
        val id =
          Regex("""id = "([^"]+)"""").find(block)?.groupValues?.get(1) ?: return@mapNotNull null
        val stack = block.substringBefore("@Composable")
        Authored(id, fileName, VARIANT.findAll(stack).map(::cellOf).toSet())
      }
  }

  /**
   * One `@OverrideVariant(...)` annotation, including the ktfmt-wrapped multi-line form. Matches up
   * to the closing parenthesis that sits at the start of a line, which is where ktfmt puts it.
   */
  private val VARIANT =
    Regex(
      """^@OverrideVariant\((.*?)\)$""",
      setOf(RegexOption.MULTILINE, RegexOption.DOT_MATCHES_ALL),
    )

  private fun cellOf(match: MatchResult): CatalogVariantCell {
    val args = match.groupValues[1]
    val name = Regex("""name = "([^"]+)"""").find(args)!!.groupValues[1]
    return CatalogVariantCell(name, seedsIn(args, "strings"), seedsIn(args, "booleans"))
  }

  /**
   * Reads one `key = ["a=1", "b=2"]` array into a map, or an empty map when the array is absent.
   */
  private fun seedsIn(args: String, arrayName: String): Map<String, String> {
    val array =
      Regex("""$arrayName = \[(.*?)]""", RegexOption.DOT_MATCHES_ALL)
        .find(args)
        ?.groupValues
        ?.get(1) ?: return emptyMap()
    return Regex(""""([^"=]+)=([^"]*)"""").findAll(array).associate {
      it.groupValues[1] to it.groupValues[2]
    }
  }

  /**
   * Drops seeds whose value is the axis default. Seeding a knob with the value it already resolves
   * to is a no-op, so the two spellings in the tree describe the same cell and must compare equal.
   */
  private fun normalise(
    cell: CatalogVariantCell,
    matrix: CatalogVariantMatrix,
  ): CatalogVariantCell {
    val defaults = matrix.axes.associate { it.key to it.default }
    fun strip(seeds: Map<String, String>) = seeds.filterNot { (k, v) -> defaults[k] == v }
    return cell.copy(strings = strip(cell.strings), booleans = strip(cell.booleans))
  }

  private fun assertMatches(authored: List<Authored>, matrix: (Authored) -> CatalogVariantMatrix) {
    for (component in authored) {
      val expected = matrix(component)
      assertEquals(
        expected.cells.map { normalise(it, expected) }.toSet(),
        component.cells.map { normalise(it, expected) }.toSet(),
        "${component.id} (${component.file}) does not carry exactly the cells of its declared " +
          "matrix (${expected.axes.joinToString(" x ") { it.key }}). The matrix is declared in " +
          "CatalogAxes.kt; a cell missing here renders as a gap in the published sheet and " +
          "design-parity reports the kit's variant as having no candidate render.",
      )
    }
  }

  @Test
  fun `buttons carry the size x shape matrix`() {
    val authored = authoredIn("Buttons.kt")
    assertEquals(5, authored.size, "Buttons.kt should declare the five common M3 buttons")
    assertMatches(authored) { CatalogVariantMatrices.SizeShape }
  }

  @Test
  fun `icon buttons carry the size x width x shape matrix`() {
    val authored = authoredIn("IconButtons.kt")
    assertEquals(4, authored.size, "IconButtons.kt should declare the four icon button emphases")
    assertMatches(authored) { CatalogVariantMatrices.IconButton }
  }

  /**
   * The toggle buttons' `selected` axis defaults **per component** — filled and tonal were authored
   * selected, outlined and elevated unselected — so each block is checked against a matrix built
   * with its own default. That is also what decides whether its cells are suffixed `-off` or `-on`.
   */
  @Test
  fun `toggle buttons carry the size x shape x selected matrix`() {
    val selectedByDefault =
      mapOf(
        "ToggleButton/Filled" to true,
        "ToggleButton/Tonal" to true,
        "ToggleButton/Outlined" to false,
        "ToggleButton/Elevated" to false,
      )
    val authored = authoredIn("ToggleButtons.kt")
    assertEquals(
      selectedByDefault.keys,
      authored.map { it.id }.toSet(),
      "the per-component `selected` defaults above must name exactly the components in the file — " +
        "a component missing from the map would go unchecked",
    )
    assertMatches(authored) {
      CatalogVariantMatrices.toggleButton(selectedByDefault.getValue(it.id))
    }
  }

  /**
   * The `selected` default in the map above has to agree with the `catalogToggleSelected(default =
   * …)` the sticker actually calls, or the matrix is checked against the wrong naming convention
   * and the whole test passes while describing something the renders don't do.
   */
  @Test
  fun `the declared selected defaults match the sticker bodies`() {
    val text = File(sectionsDir, "ToggleButtons.kt").readText()
    val declared =
      Regex("""catalogToggleSelected\(default = (true|false)\)""")
        .findAll(text)
        .map { it.groupValues[1].toBooleanStrict() }
        .toList()
    assertEquals(
      listOf(true, true, false, false),
      declared,
      "ToggleButtons.kt declares its components in the order Filled, Tonal, Outlined, Elevated, " +
        "with the first pair authored selected — the order the per-component defaults in " +
        "`toggle buttons carry the size x shape x selected matrix` assume",
    )
  }
}
