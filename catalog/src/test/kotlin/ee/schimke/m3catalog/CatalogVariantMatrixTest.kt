package ee.schimke.m3catalog

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Holds the generated matrix annotations in `CatalogMatrixAnnotations.kt` to the declarations they
 * come from, and each component to the matrix it should carry.
 *
 * The cells used to be written out per component — 250 `@OverrideVariant` annotations across
 * thirteen blocks — then once each on `@SizeShapeMatrix` / `@IconButtonMatrix` /
 * `@SelectedToggleButtonMatrix` / `@UnselectedToggleButtonMatrix`, hand-typed and compared against
 * the axes by a regex scan of the source. They are now GENERATED from those axes (#107), so the
 * comparison is no longer between two authored spellings of one fact: it is between the committed
 * file and the generator's output, byte for byte.
 *
 * That is a stronger assertion than the set equality it replaces — it catches a hand-edit to the
 * generated file, a missing regenerate after an axis changes, and a formatting drift that would
 * make `ktfmtCheck` and the generator disagree — and it needs no Kotlin parsing, which is what the
 * old scan's ktfmt-tuned regexes were (#108).
 *
 * The second test pins the other half — that every component actually carries its annotation —
 * because a matrix nothing references declares cells that render nowhere, and a component carrying
 * the wrong one publishes another family's variants under its id. That one still reads the section
 * sources as text, for the reason `CatalogInventoryTest` does: `@OverrideVariant` is `BINARY`
 * retention, so it is not visible to `java.lang.reflect` at all.
 */
class CatalogVariantMatrixTest {

  private val sectionsDir = File("src/main/kotlin/ee/schimke/m3catalog/sections")
  private val matrixFile = File("src/main/kotlin/ee/schimke/m3catalog/CatalogMatrixAnnotations.kt")

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
  fun `the committed matrix annotations are what the declarations generate`() {
    assertEquals(
      MatrixAnnotationsGenerator.render(),
      matrixFile.readText(),
      "CatalogMatrixAnnotations.kt is stale or hand-edited. It is generated from " +
        "CatalogMatrixDeclarations (and the axes in CatalogAxes.kt those expand); regenerate it " +
        "with `./gradlew :catalog:generateMatrixAnnotations`. A cell missing there renders as a " +
        "gap in the published sheet and design-parity reports the kit's variant as having no " +
        "candidate render.",
    )
  }

  /**
   * The slider matrix is the one whose base is not [CatalogSize.Small], so its cells name four
   * sizes where the button matrices name four and leave small unnamed. `Slider/Vertical` carries no
   * matrix on purpose: it is the same `Standard slider` set as `Slider/Continuous` seen down the
   * other axis, so its sizes would resolve to variants the horizontal cells already compare.
   */
  @Test
  fun `the slider components carry the slider matrix`() {
    assertCarries(
      "Sliders.kt",
      // The vertical slider is no longer here to check: it is a `@CatalogVariant` cell of
      // `Slider/Continuous` (the kit carries orientation as a property of the one `Standard slider`
      // set), and this helper reads top-level components. Its one size is asserted by the map it
      // resolves into rather than by a matrix annotation it deliberately does not carry.
      mapOf("Slider/Continuous" to "SliderSizeMatrix", "Slider/Range" to "SliderSizeMatrix"),
    )
  }

  /**
   * The three Styles specimens, whose axes were raw `previewOverrideString` knobs until #103 made
   * each an enum with a declared value set. `Shape/MaterialShapes` deliberately carries none: its
   * 35 cells are the kit's own `Shape Set` spelling, held to `SHAPE_SET` by
   * `MaterialShapeRecipeTest` rather than expanded from an axis.
   */
  @Test
  fun `every styles specimen carries its matrix`() {
    assertCarries("Typography.kt", mapOf("Typography/Type scale" to "TypeScaleMatrix"))
    assertCarries(
      "Shapes.kt",
      mapOf("Shape/Corner scale" to "CornerScaleMatrix", "Shape/MaterialShapes" to null),
    )
    assertCarries("Colors.kt", mapOf("Color/Role grid" to "ColorSchemeMatrix"))
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
