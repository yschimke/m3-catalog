package ee.schimke.m3catalog

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Holds the matrix **annotation classes** in `CatalogMatrixAnnotations.kt` to the matrices declared
 * in `CatalogAxes.kt`, and each component to the matrix it should carry.
 *
 * The cells used to be written out per component — 250 `@OverrideVariant` annotations across
 * thirteen blocks — and now live once each on `@SizeShapeMatrix` / `@IconButtonMatrix` /
 * `@SelectedToggleButtonMatrix` / `@UnselectedToggleButtonMatrix`. That removes the copy-drift this
 * test was originally written to catch, but not the reason for it: nothing in the compile relates a
 * cell to [CatalogSize] / [CatalogShape] / [CatalogIconWidth], so adding a size to the enum still
 * leaves every matrix silently short a row, and a mistyped seed still produces a render that is
 * quietly a duplicate of another cell. Both show up only as a wrong sticker sheet at the end of a
 * long CI render, if at all.
 *
 * So the same set equality is asserted, one level up: every cell of a declared matrix is on its
 * annotation class, and every cell on the annotation class belongs to the matrix. A second test
 * pins the other half — that every component actually carries its annotation — because a matrix
 * nothing references declares cells that render nowhere, and a component carrying the wrong one
 * publishes another family's variants under its id. Neither failure is visible in the first test.
 *
 * Two deliberate loosenings, so this checks meaning rather than spelling:
 * * **Seeds are normalised against the axis defaults.** A cell may spell a default-valued axis
 *   explicitly or leave it out; both seed the same render, because a knob set to its own default is
 *   what an unseeded knob already resolves to.
 * * **Order is not compared.** A variant's identity is its name.
 *
 * The source tree is read as text rather than through reflection, for the same reason
 * `CatalogInventoryTest` does: `@OverrideVariant` is `BINARY` retention, so it is not visible to
 * `java.lang.reflect` at all — discovery reads it with ClassGraph, and a source scan is both
 * sufficient here and independent of the render pipeline being available.
 */
class CatalogVariantMatrixTest {

  private val sectionsDir = File("src/main/kotlin/ee/schimke/m3catalog/sections")
  private val matrixFile = File("src/main/kotlin/ee/schimke/m3catalog/CatalogMatrixAnnotations.kt")

  /**
   * One `@OverrideVariant(...)` annotation, including the ktfmt-wrapped multi-line form. Matches up
   * to the closing parenthesis that sits at the start of a line, which is where ktfmt puts it.
   */
  private val VARIANT =
    Regex(
      """^@OverrideVariant\((.*?)\)$""",
      setOf(RegexOption.MULTILINE, RegexOption.DOT_MATCHES_ALL),
    )

  /**
   * The cells declared on one matrix annotation class — every `@OverrideVariant` between the end of
   * the previous declaration and `annotation class <name>`.
   */
  private fun cellsOn(annotation: String): Set<CatalogVariantCell> {
    val text = matrixFile.readText()
    val end = text.indexOf("annotation class $annotation")
    require(end >= 0) { "$annotation is not declared in ${matrixFile.name}" }
    val previous = text.lastIndexOf("annotation class ", (end - 1).coerceAtLeast(0))
    val start = if (previous < 0) 0 else previous
    return VARIANT.findAll(text.substring(start, end)).map(::cellOf).toSet()
  }

  /** The matrix annotation each `@CatalogComponent` in a section file carries, by component id. */
  private fun matrixByComponent(fileName: String): Map<String, String?> =
    File(sectionsDir, fileName)
      .readText()
      .split(Regex("""(?=^@CatalogComponent\()""", RegexOption.MULTILINE))
      .drop(1)
      .mapNotNull { block ->
        val id =
          Regex("""id = "([^"]+)"""").find(block)?.groupValues?.get(1) ?: return@mapNotNull null
        val stack = block.substringBefore("@Composable")
        id to Regex("""^@(\w*Matrix)$""", RegexOption.MULTILINE).find(stack)?.groupValues?.get(1)
      }
      .toMap()

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
   * to is a no-op, so the two spellings describe the same cell and must compare equal.
   */
  private fun normalise(
    cell: CatalogVariantCell,
    matrix: CatalogVariantMatrix,
  ): CatalogVariantCell {
    val defaults = (matrix.axes + matrix.alongside).associate { it.key to it.default }
    fun strip(seeds: Map<String, String>) = seeds.filterNot { (k, v) -> defaults[k] == v }
    return cell.copy(strings = strip(cell.strings), booleans = strip(cell.booleans))
  }

  private fun assertMatches(annotation: String, expected: CatalogVariantMatrix) {
    assertEquals(
      expected.cells.map { normalise(it, expected) }.toSet(),
      cellsOn(annotation).map { normalise(it, expected) }.toSet(),
      "@$annotation does not carry exactly the cells of its declared matrix " +
        "(${expected.axes.joinToString(" x ") { it.key }}). The matrix is declared in " +
        "CatalogAxes.kt; a cell missing here renders as a gap in the published sheet and " +
        "design-parity reports the kit's variant as having no candidate render.",
    )
  }

  private fun assertCarries(fileName: String, expected: Map<String, String?>) {
    assertEquals(
      expected,
      matrixByComponent(fileName),
      "every component in $fileName must carry its matrix annotation — a matrix nothing " +
        "references declares cells that render nowhere, and a component carrying the wrong one " +
        "publishes another family's variants under its id",
    )
  }

  @Test
  fun `the button matrix carries the size x shape cells`() {
    assertMatches("SizeShapeMatrix", CatalogVariantMatrices.SizeShape)
  }

  @Test
  fun `the icon button matrix carries the size x width x shape cells`() {
    assertMatches("IconButtonMatrix", CatalogVariantMatrices.IconButton)
  }

  /**
   * The toggle-button `selected` axis defaults **per component** — filled and tonal were authored
   * selected, outlined and elevated unselected — and that default is what the cell names are
   * relative to (`-off` against a selected default, `-on` against an unselected one). Hence two
   * annotations rather than one, each checked against a matrix built with its own default.
   */
  @Test
  fun `the toggle button matrices carry the size x shape x selected cells`() {
    assertMatches("SelectedToggleButtonMatrix", CatalogVariantMatrices.toggleButton(true))
    assertMatches("UnselectedToggleButtonMatrix", CatalogVariantMatrices.toggleButton(false))
  }

  /**
   * The slider matrix is the one whose base is not [CatalogSize.Small], so its cells name four
   * sizes where the button matrices name four and leave small unnamed. `Slider/Vertical` carries no
   * matrix on purpose: it is the same `Standard slider` set as `Slider/Continuous` seen down the
   * other axis, so its sizes would resolve to variants the horizontal cells already compare.
   */
  @Test
  fun `the slider matrix carries the size cells`() {
    assertMatches("SliderSizeMatrix", CatalogVariantMatrices.SliderSize)
    assertCarries(
      "Sliders.kt",
      mapOf(
        "Slider/Continuous" to "SliderSizeMatrix",
        "Slider/Range" to "SliderSizeMatrix",
        "Slider/Vertical" to null,
      ),
    )
  }

  @Test
  fun `every button family component carries its matrix`() {
    assertCarries(
      "Buttons.kt",
      mapOf(
        "Button/Filled" to "SizeShapeMatrix",
        "Button/Tonal" to "SizeShapeMatrix",
        "Button/Outlined" to "SizeShapeMatrix",
        "Button/Elevated" to "SizeShapeMatrix",
        "Button/Text" to "SizeShapeMatrix",
      ),
    )
    assertCarries(
      "IconButtons.kt",
      mapOf(
        "IconButton/Standard" to "IconButtonMatrix",
        "IconButton/Filled" to "IconButtonMatrix",
        "IconButton/Tonal" to "IconButtonMatrix",
        "IconButton/Outlined" to "IconButtonMatrix",
      ),
    )
    assertCarries(
      "ToggleButtons.kt",
      mapOf(
        "ToggleButton/Filled" to "SelectedToggleButtonMatrix",
        "ToggleButton/Tonal" to "SelectedToggleButtonMatrix",
        "ToggleButton/Outlined" to "UnselectedToggleButtonMatrix",
        "ToggleButton/Elevated" to "UnselectedToggleButtonMatrix",
      ),
    )
  }

  /**
   * Which of the two toggle matrices a component carries has to agree with the
   * `catalogToggleSelected(default = …)` its sticker actually calls, or it publishes cells named
   * against a default the render does not use — `-off` variants that are really the on state.
   */
  @Test
  fun `the toggle matrices match the sticker bodies`() {
    val text = File(sectionsDir, "ToggleButtons.kt").readText()
    val declared =
      Regex("""catalogToggleSelected\(default = (true|false)\)""")
        .findAll(text)
        .map { it.groupValues[1].toBooleanStrict() }
        .toList()
    val carried =
      matrixByComponent("ToggleButtons.kt").values.map { it == "SelectedToggleButtonMatrix" }
    assertEquals(
      declared,
      carried,
      "each toggle component's matrix annotation must match the `selected` default its body " +
        "passes — the annotation decides how its cells are named, the body decides what they " +
        "render, and a mismatch publishes one labelled as the other",
    )
    assertEquals(
      listOf(true, true, false, false),
      declared,
      "ToggleButtons.kt declares Filled, Tonal, Outlined, Elevated in that order, with the first " +
        "pair authored selected",
    )
  }
}
