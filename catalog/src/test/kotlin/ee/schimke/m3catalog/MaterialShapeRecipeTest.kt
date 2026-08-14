package ee.schimke.m3catalog

import androidx.graphics.shapes.RoundedPolygon
import java.io.File
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/**
 * Holds the inlined shape constructions to the `MaterialShapes` entries they claim to rebuild.
 *
 * `MaterialShapeRecipes` transcribes builders that are `internal` in `androidx.compose.material3`,
 * which is the only way to expose their parameters — and transcribed code drifts. Every shape here
 * is rebuilt at [ShapeTweaks.Default] and compared to the library's own polygon **cubic for
 * cubic**, so a Material release that re-authors a shape, or a mistyped literal, fails the build
 * instead of shipping a knob that reshapes something the library no longer draws.
 *
 * The default render path is unaffected either way — a sticker draws `MaterialShapes` until a knob
 * moves — so what this test protects is the *promise* that turning a knob starts from where the
 * stock shape left off.
 */
class MaterialShapeRecipeTest {

  /** Enough to catch a wrong literal, loose enough for float noise across two build paths. */
  private val tolerance = 1e-4f

  private fun RoundedPolygon.points(): List<Float> = cubics.flatMap {
    listOf(
      it.anchor0X,
      it.anchor0Y,
      it.control0X,
      it.control0Y,
      it.control1X,
      it.control1Y,
      it.anchor1X,
      it.anchor1Y,
    )
  }

  private fun assertSameOutline(expected: RoundedPolygon, actual: RoundedPolygon, label: String) {
    val a = expected.points()
    val b = actual.points()
    assertEquals(a.size, b.size, "$label: cubic count")
    for (i in a.indices) {
      assertTrue(
        abs(a[i] - b[i]) <= tolerance,
        "$label: coordinate $i is ${b[i]}, expected ${a[i]}",
      )
    }
  }

  @Test
  fun `every recipe rebuilds its MaterialShapes entry`() {
    for (recipe in MaterialShapeRecipes.All) {
      assertSameOutline(recipe.stock(), recipe.inlined(), recipe.toString())
    }
  }

  @Test
  fun `the catalog covers all 35 kit shapes, each a distinct outline`() {
    assertEquals(35, MaterialShapeRecipes.All.size)
    val outlines =
      MaterialShapeRecipes.All.map { recipe ->
        recipe.stock().points().joinToString(",") { "%.3f".format(it) }
      }
    assertEquals(outlines.size, outlines.toSet().size, "two recipes wrap the same shape")
  }

  @Test
  fun `untouched knobs keep the stock polygon, a moved one rebuilds`() {
    val sunny = MaterialShapeRecipes.Sunny
    assertTrue(ShapeTweaks.Default.isDefault)
    assertTrue(
      sunny.resolve(ShapeTweaks.Default) === sunny.stock(),
      "the default path must hand back MaterialShapes' own polygon, not a copy",
    )

    // Un-rounding the star's corners and re-pointing it both have to change the outline; the
    // knobs are worthless if they resolve back to the stock shape.
    val unrounded = sunny.resolve(ShapeTweaks(rounding = 0f))
    assertNotEquals(sunny.stock().points(), unrounded.points())
    assertEquals(
      MaterialShapeRecipes.Sunny.inlined(ShapeTweaks(count = 12)).cubics.size,
      sunny.resolve(ShapeTweaks(count = 12)).cubics.size,
    )
    assertTrue(
      sunny.inlined(ShapeTweaks(count = 12)).cubics.size >
        sunny.inlined(ShapeTweaks(count = 8)).cubics.size,
      "a 12-point star has more cubics than an 8-point one",
    )
  }

  @Test
  fun `a shape sticker draws the recipe of the same name`() {
    val source = File("src/main/kotlin/ee/schimke/m3catalog/sections/Shapes.kt").readText()
    val stickers =
      Regex("""id = "Shape/([^"]+)"[\s\S]*?ShapeSticker\(MaterialShapeRecipes\.(\w+)\)""")
        .findAll(source)
        .map { it.groupValues[1] to it.groupValues[2] }
        .toList()
    assertEquals(35, stickers.size)
    for ((id, recipe) in stickers) {
      assertEquals(id, recipe, "the `Shape/$id` sticker draws MaterialShapeRecipes.$recipe")
    }
  }
}
