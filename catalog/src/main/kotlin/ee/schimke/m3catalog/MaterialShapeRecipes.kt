package ee.schimke.m3catalog

import androidx.compose.material3.MaterialShapes
import androidx.compose.ui.geometry.Offset
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.TransformResult
import androidx.graphics.shapes.circle
import androidx.graphics.shapes.rectangle
import androidx.graphics.shapes.star
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * The 35 [MaterialShapes] entries, each paired with the **construction that produces it** so a
 * viewer can take one apart.
 *
 * `MaterialShapes.Heart` is a `RoundedPolygon` and nothing else — a finished outline with no seam
 * to push on. That is the right default for the sticker sheet (the published render comes from
 * Material's own object, byte-identical to what any consumer of the library gets), but it makes the
 * shape page a wall of silhouettes you cannot interrogate: there is no way to ask *what makes a
 * heart a heart* short of reading the library source.
 *
 * So every shape here carries two paths:
 * * [MaterialShapeRecipe.stock] — the `MaterialShapes` entry itself. What renders by default.
 * * [MaterialShapeRecipe.inlined] — the same construction written out here, with [ShapeTweaks]
 *   applied. What renders as soon as any knob moves off its default.
 *
 * The inlined half is a transcription of `androidx.compose.material3.MaterialShapes`' own private
 * builders (`circle()`, `square()`, `slanted()`, … and the `customPolygon` / `doRepeat` helpers
 * they share), which are `internal` upstream and so cannot be called from here. Transcribed code
 * drifts silently, which is the whole risk of doing this — `MaterialShapeRecipeTest` closes it by
 * asserting every recipe rebuilds its `MaterialShapes` entry cubic-for-cubic at default tweaks. If
 * a Material release re-authors a shape, that test fails rather than the catalog quietly offering
 * knobs that reshape something the library no longer draws.
 */
object MaterialShapeRecipes {

  val Circle: MaterialShapeRecipe = recipe({ MaterialShapes.Circle }) { circle(it) }
  val Square: MaterialShapeRecipe = recipe({ MaterialShapes.Square }) { square(it) }
  val Slanted: MaterialShapeRecipe = recipe({ MaterialShapes.Slanted }) { slanted(it) }
  val Arch: MaterialShapeRecipe = recipe({ MaterialShapes.Arch }) { arch(it) }
  val Fan: MaterialShapeRecipe = recipe({ MaterialShapes.Fan }) { fan(it) }
  val Arrow: MaterialShapeRecipe = recipe({ MaterialShapes.Arrow }) { arrow(it) }
  val SemiCircle: MaterialShapeRecipe = recipe({ MaterialShapes.SemiCircle }) { semiCircle(it) }
  val Oval: MaterialShapeRecipe = recipe({ MaterialShapes.Oval }) { oval(it) }
  val Pill: MaterialShapeRecipe = recipe({ MaterialShapes.Pill }) { pill(it) }
  val Triangle: MaterialShapeRecipe = recipe({ MaterialShapes.Triangle }) { triangle(it) }
  val Diamond: MaterialShapeRecipe = recipe({ MaterialShapes.Diamond }) { diamond(it) }
  val ClamShell: MaterialShapeRecipe = recipe({ MaterialShapes.ClamShell }) { clamShell(it) }
  val Pentagon: MaterialShapeRecipe = recipe({ MaterialShapes.Pentagon }) { pentagon(it) }
  val Gem: MaterialShapeRecipe = recipe({ MaterialShapes.Gem }) { gem(it) }
  val Sunny: MaterialShapeRecipe = recipe({ MaterialShapes.Sunny }) { sunny(it) }
  val VerySunny: MaterialShapeRecipe = recipe({ MaterialShapes.VerySunny }) { verySunny(it) }
  val Cookie4Sided: MaterialShapeRecipe = recipe({ MaterialShapes.Cookie4Sided }) { cookie4(it) }
  val Cookie6Sided: MaterialShapeRecipe = recipe({ MaterialShapes.Cookie6Sided }) { cookie6(it) }
  val Cookie7Sided: MaterialShapeRecipe = recipe({ MaterialShapes.Cookie7Sided }) { cookie7(it) }
  val Cookie9Sided: MaterialShapeRecipe = recipe({ MaterialShapes.Cookie9Sided }) { cookie9(it) }
  val Cookie12Sided: MaterialShapeRecipe = recipe({ MaterialShapes.Cookie12Sided }) { cookie12(it) }
  val Ghostish: MaterialShapeRecipe = recipe({ MaterialShapes.Ghostish }) { ghostish(it) }
  val Clover4Leaf: MaterialShapeRecipe = recipe({ MaterialShapes.Clover4Leaf }) { clover4(it) }
  val Clover8Leaf: MaterialShapeRecipe = recipe({ MaterialShapes.Clover8Leaf }) { clover8(it) }
  val Burst: MaterialShapeRecipe = recipe({ MaterialShapes.Burst }) { burst(it) }
  val SoftBurst: MaterialShapeRecipe = recipe({ MaterialShapes.SoftBurst }) { softBurst(it) }
  val Boom: MaterialShapeRecipe = recipe({ MaterialShapes.Boom }) { boom(it) }
  val SoftBoom: MaterialShapeRecipe = recipe({ MaterialShapes.SoftBoom }) { softBoom(it) }
  val Flower: MaterialShapeRecipe = recipe({ MaterialShapes.Flower }) { flower(it) }
  val Puffy: MaterialShapeRecipe = recipe({ MaterialShapes.Puffy }) { puffy(it) }
  val PuffyDiamond: MaterialShapeRecipe =
    recipe({ MaterialShapes.PuffyDiamond }) { puffyDiamond(it) }
  val PixelCircle: MaterialShapeRecipe = recipe({ MaterialShapes.PixelCircle }) { pixelCircle(it) }
  val PixelTriangle: MaterialShapeRecipe =
    recipe({ MaterialShapes.PixelTriangle }) { pixelTriangle(it) }
  val Bun: MaterialShapeRecipe = recipe({ MaterialShapes.Bun }) { bun(it) }
  val Heart: MaterialShapeRecipe = recipe({ MaterialShapes.Heart }) { heart(it) }

  /** Every recipe, in the order the kit's `Shape` page lays the specimens out. */
  val All: List<MaterialShapeRecipe> =
    listOf(
      Circle,
      Square,
      Slanted,
      Arch,
      Fan,
      Arrow,
      SemiCircle,
      Oval,
      Pill,
      Triangle,
      Diamond,
      ClamShell,
      Pentagon,
      Gem,
      VerySunny,
      Sunny,
      Cookie4Sided,
      Cookie6Sided,
      Cookie7Sided,
      Cookie9Sided,
      Cookie12Sided,
      Ghostish,
      Clover4Leaf,
      Clover8Leaf,
      Burst,
      SoftBurst,
      Boom,
      SoftBoom,
      Flower,
      Puffy,
      PuffyDiamond,
      PixelCircle,
      PixelTriangle,
      Bun,
      Heart,
    )

  // --- The transcribed builders -------------------------------------------------------------
  //
  // One function per shape, mirroring `MaterialShapes`' own private builder of the same name. The
  // literals are upstream's, unedited; `tweaks` is the only thing this file adds, and at
  // `ShapeTweaks.Default` every `tweaks.…` call below is the identity.

  private fun circle(t: ShapeTweaks) = RoundedPolygon.circle(numVertices = t.count(10))

  private fun square(t: ShapeTweaks) =
    RoundedPolygon.rectangle(width = 1f, height = 1f, rounding = t.corner(CornerRounding(.3f)))

  private fun slanted(t: ShapeTweaks) =
    customPolygon(
      listOf(
        PointNRound(Offset(0.926f, 0.970f), CornerRounding(0.189f, 0.811f)),
        PointNRound(Offset(-0.021f, 0.967f), CornerRounding(0.187f, 0.057f)),
      ),
      2,
      t = t,
    )

  private fun arch(t: ShapeTweaks) =
    RoundedPolygon(
        numVertices = t.count(4),
        perVertexRounding =
          t.perVertex(
            t.count(4),
            listOf(CornerRounding(1f), CornerRounding(1f), CornerRounding(.2f), CornerRounding(.2f)),
          ),
      )
      .rotated(-135f)

  private fun fan(t: ShapeTweaks) =
    customPolygon(
      listOf(
        PointNRound(Offset(1.004f, 1.000f), CornerRounding(0.148f, 0.417f)),
        PointNRound(Offset(0.000f, 1.000f), CornerRounding(0.151f)),
        PointNRound(Offset(0.000f, -0.003f), CornerRounding(0.148f)),
        PointNRound(Offset(0.978f, 0.020f), CornerRounding(0.803f)),
      ),
      1,
      t = t,
    )

  private fun arrow(t: ShapeTweaks) =
    customPolygon(
      listOf(
        PointNRound(Offset(0.500f, 0.892f), CornerRounding(0.313f)),
        PointNRound(Offset(-0.216f, 1.050f), CornerRounding(0.207f)),
        PointNRound(Offset(0.499f, -0.160f), CornerRounding(0.215f, 1.000f)),
        PointNRound(Offset(1.225f, 1.060f), CornerRounding(0.211f)),
      ),
      1,
      t = t,
    )

  private fun semiCircle(t: ShapeTweaks) =
    RoundedPolygon.rectangle(
      width = 1.6f,
      height = 1f,
      perVertexRounding =
        listOf(
          t.corner(CornerRounding(.2f)),
          t.corner(CornerRounding(.2f)),
          t.corner(CornerRounding(1f)),
          t.corner(CornerRounding(1f)),
        ),
    )

  private fun oval(t: ShapeTweaks) =
    RoundedPolygon.circle(numVertices = t.count(8)).scaled(1f, 0.64f).rotated(-45f)

  private fun pill(t: ShapeTweaks) =
    customPolygon(
      listOf(
        PointNRound(Offset(0.961f, 0.039f), CornerRounding(0.426f)),
        PointNRound(Offset(1.001f, 0.428f)),
        PointNRound(Offset(1.000f, 0.609f), CornerRounding(1.000f)),
      ),
      reps = 2,
      mirroring = true,
      t = t,
    )

  private fun triangle(t: ShapeTweaks) =
    RoundedPolygon(numVertices = t.count(3), rounding = t.corner(CornerRounding(.2f))).rotated(-90f)

  private fun diamond(t: ShapeTweaks) =
    customPolygon(
      listOf(
        PointNRound(Offset(0.500f, 1.096f), CornerRounding(0.151f, 0.524f)),
        PointNRound(Offset(0.040f, 0.500f), CornerRounding(0.159f)),
      ),
      2,
      t = t,
    )

  private fun clamShell(t: ShapeTweaks) =
    customPolygon(
      listOf(
        PointNRound(Offset(0.171f, 0.841f), CornerRounding(0.159f)),
        PointNRound(Offset(-0.020f, 0.500f), CornerRounding(0.140f)),
        PointNRound(Offset(0.170f, 0.159f), CornerRounding(0.159f)),
      ),
      2,
      t = t,
    )

  private fun pentagon(t: ShapeTweaks) =
    customPolygon(
      listOf(
        PointNRound(Offset(0.500f, -0.009f), CornerRounding(0.172f)),
        PointNRound(Offset(1.030f, 0.365f), CornerRounding(0.164f)),
        PointNRound(Offset(0.828f, 0.970f), CornerRounding(0.169f)),
      ),
      reps = 1,
      mirroring = true,
      t = t,
    )

  private fun gem(t: ShapeTweaks) =
    customPolygon(
      listOf(
        PointNRound(Offset(0.499f, 1.023f), CornerRounding(0.241f, 0.778f)),
        PointNRound(Offset(-0.005f, 0.792f), CornerRounding(0.208f)),
        PointNRound(Offset(0.073f, 0.258f), CornerRounding(0.228f)),
        PointNRound(Offset(0.433f, -0.000f), CornerRounding(0.491f)),
      ),
      1,
      mirroring = true,
      t = t,
    )

  private fun sunny(t: ShapeTweaks) =
    RoundedPolygon.star(
      numVerticesPerRadius = t.count(8),
      innerRadius = t.innerRadius(.8f),
      rounding = t.corner(CornerRounding(.15f)),
    )

  private fun verySunny(t: ShapeTweaks) =
    customPolygon(
      listOf(
        PointNRound(Offset(0.500f, 1.080f), CornerRounding(0.085f)),
        PointNRound(Offset(0.358f, 0.843f), CornerRounding(0.085f)),
      ),
      8,
      t = t,
    )

  private fun cookie4(t: ShapeTweaks) =
    customPolygon(
      listOf(
        PointNRound(Offset(1.237f, 1.236f), CornerRounding(0.258f)),
        PointNRound(Offset(0.500f, 0.918f), CornerRounding(0.233f)),
      ),
      4,
      t = t,
    )

  private fun cookie6(t: ShapeTweaks) =
    customPolygon(
      listOf(
        PointNRound(Offset(0.723f, 0.884f), CornerRounding(0.394f)),
        PointNRound(Offset(0.500f, 1.099f), CornerRounding(0.398f)),
      ),
      6,
      t = t,
    )

  private fun cookie7(t: ShapeTweaks) =
    RoundedPolygon.star(
        numVerticesPerRadius = t.count(7),
        innerRadius = t.innerRadius(.75f),
        rounding = t.corner(CornerRounding(.5f)),
      )
      .rotated(-90f)

  private fun cookie9(t: ShapeTweaks) =
    RoundedPolygon.star(
        numVerticesPerRadius = t.count(9),
        innerRadius = t.innerRadius(.8f),
        rounding = t.corner(CornerRounding(.5f)),
      )
      .rotated(-90f)

  private fun cookie12(t: ShapeTweaks) =
    RoundedPolygon.star(
        numVerticesPerRadius = t.count(12),
        innerRadius = t.innerRadius(.8f),
        rounding = t.corner(CornerRounding(.5f)),
      )
      .rotated(-90f)

  private fun ghostish(t: ShapeTweaks) =
    customPolygon(
      listOf(
        PointNRound(Offset(0.500f, 0f), CornerRounding(1.000f)),
        PointNRound(Offset(1f, 0f), CornerRounding(1.000f)),
        PointNRound(Offset(1f, 1.140f), CornerRounding(0.254f, 0.106f)),
        PointNRound(Offset(0.575f, 0.906f), CornerRounding(0.253f)),
      ),
      reps = 1,
      mirroring = true,
      t = t,
    )

  private fun clover4(t: ShapeTweaks) =
    customPolygon(
      listOf(
        PointNRound(Offset(0.500f, 0.074f)),
        PointNRound(Offset(0.725f, -0.099f), CornerRounding(0.476f)),
      ),
      reps = 4,
      mirroring = true,
      t = t,
    )

  private fun clover8(t: ShapeTweaks) =
    customPolygon(
      listOf(
        PointNRound(Offset(0.500f, 0.036f)),
        PointNRound(Offset(0.758f, -0.101f), CornerRounding(0.209f)),
      ),
      reps = 8,
      t = t,
    )

  private fun burst(t: ShapeTweaks) =
    customPolygon(
      listOf(
        PointNRound(Offset(0.500f, -0.006f), CornerRounding(0.006f)),
        PointNRound(Offset(0.592f, 0.158f), CornerRounding(0.006f)),
      ),
      reps = 12,
      t = t,
    )

  private fun softBurst(t: ShapeTweaks) =
    customPolygon(
      listOf(
        PointNRound(Offset(0.193f, 0.277f), CornerRounding(0.053f)),
        PointNRound(Offset(0.176f, 0.055f), CornerRounding(0.053f)),
      ),
      reps = 10,
      t = t,
    )

  private fun boom(t: ShapeTweaks) =
    customPolygon(
      listOf(
        PointNRound(Offset(0.457f, 0.296f), CornerRounding(0.007f)),
        PointNRound(Offset(0.500f, -0.051f), CornerRounding(0.007f)),
      ),
      reps = 15,
      t = t,
    )

  private fun softBoom(t: ShapeTweaks) =
    customPolygon(
      listOf(
        PointNRound(Offset(0.733f, 0.454f)),
        PointNRound(Offset(0.839f, 0.437f), CornerRounding(0.532f)),
        PointNRound(Offset(0.949f, 0.449f), CornerRounding(0.439f, 1.000f)),
        PointNRound(Offset(0.998f, 0.478f), CornerRounding(0.174f)),
      ),
      reps = 16,
      mirroring = true,
      t = t,
    )

  private fun flower(t: ShapeTweaks) =
    customPolygon(
      listOf(
        PointNRound(Offset(0.370f, 0.187f)),
        PointNRound(Offset(0.416f, 0.049f), CornerRounding(0.381f)),
        PointNRound(Offset(0.479f, 0.001f), CornerRounding(0.095f)),
      ),
      reps = 8,
      mirroring = true,
      t = t,
    )

  private fun puffy(t: ShapeTweaks) =
    customPolygon(
        listOf(
          PointNRound(Offset(0.500f, 0.053f)),
          PointNRound(Offset(0.545f, -0.040f), CornerRounding(0.405f)),
          PointNRound(Offset(0.670f, -0.035f), CornerRounding(0.426f)),
          PointNRound(Offset(0.717f, 0.066f), CornerRounding(0.574f)),
          PointNRound(Offset(0.722f, 0.128f)),
          PointNRound(Offset(0.777f, 0.002f), CornerRounding(0.360f)),
          PointNRound(Offset(0.914f, 0.149f), CornerRounding(0.660f)),
          PointNRound(Offset(0.926f, 0.289f), CornerRounding(0.660f)),
          PointNRound(Offset(0.881f, 0.346f)),
          PointNRound(Offset(0.940f, 0.344f), CornerRounding(0.126f)),
          PointNRound(Offset(1.003f, 0.437f), CornerRounding(0.255f)),
        ),
        reps = 2,
        mirroring = true,
        t = t,
      )
      .scaled(1f, 0.742f)

  private fun puffyDiamond(t: ShapeTweaks) =
    customPolygon(
      listOf(
        PointNRound(Offset(0.870f, 0.130f), CornerRounding(0.146f)),
        PointNRound(Offset(0.818f, 0.357f)),
        PointNRound(Offset(1.000f, 0.332f), CornerRounding(0.853f)),
      ),
      reps = 4,
      mirroring = true,
      t = t,
    )

  private fun pixelCircle(t: ShapeTweaks) =
    customPolygon(
      listOf(
        PointNRound(Offset(0.500f, 0.000f)),
        PointNRound(Offset(0.704f, 0.000f)),
        PointNRound(Offset(0.704f, 0.065f)),
        PointNRound(Offset(0.843f, 0.065f)),
        PointNRound(Offset(0.843f, 0.148f)),
        PointNRound(Offset(0.926f, 0.148f)),
        PointNRound(Offset(0.926f, 0.296f)),
        PointNRound(Offset(1.000f, 0.296f)),
      ),
      reps = 2,
      mirroring = true,
      t = t,
    )

  private fun pixelTriangle(t: ShapeTweaks) =
    customPolygon(
      listOf(
        PointNRound(Offset(0.110f, 0.500f)),
        PointNRound(Offset(0.113f, 0.000f)),
        PointNRound(Offset(0.287f, 0.000f)),
        PointNRound(Offset(0.287f, 0.087f)),
        PointNRound(Offset(0.421f, 0.087f)),
        PointNRound(Offset(0.421f, 0.170f)),
        PointNRound(Offset(0.560f, 0.170f)),
        PointNRound(Offset(0.560f, 0.265f)),
        PointNRound(Offset(0.674f, 0.265f)),
        PointNRound(Offset(0.675f, 0.344f)),
        PointNRound(Offset(0.789f, 0.344f)),
        PointNRound(Offset(0.789f, 0.439f)),
        PointNRound(Offset(0.888f, 0.439f)),
      ),
      reps = 1,
      mirroring = true,
      t = t,
    )

  private fun bun(t: ShapeTweaks) =
    customPolygon(
      listOf(
        PointNRound(Offset(0.796f, 0.500f)),
        PointNRound(Offset(0.853f, 0.518f), CornerRounding(1f)),
        PointNRound(Offset(0.992f, 0.631f), CornerRounding(1f)),
        PointNRound(Offset(0.968f, 1.000f), CornerRounding(1f)),
      ),
      reps = 2,
      mirroring = true,
      t = t,
    )

  private fun heart(t: ShapeTweaks) =
    customPolygon(
      listOf(
        PointNRound(Offset(0.500f, 0.268f), CornerRounding(0.016f)),
        PointNRound(Offset(0.792f, -0.066f), CornerRounding(0.958f)),
        PointNRound(Offset(1.064f, 0.276f), CornerRounding(1.000f)),
        PointNRound(Offset(0.501f, 0.946f), CornerRounding(0.129f)),
      ),
      reps = 1,
      mirroring = true,
      t = t,
    )

  // --- Upstream's shared helpers, transcribed -------------------------------------------------

  private data class PointNRound(val o: Offset, val r: CornerRounding = CornerRounding.Unrounded)

  private fun doRepeat(
    points: List<PointNRound>,
    reps: Int,
    center: Offset,
    mirroring: Boolean,
  ): List<PointNRound> =
    if (mirroring) {
      buildList {
        val angles = points.map { (it.o - center).angleDegrees() }
        val distances = points.map { (it.o - center).getDistance() }
        val actualReps = reps * 2
        val sectionAngle = 360f / actualReps
        repeat(actualReps) {
          points.indices.forEach { index ->
            val i = if (it % 2 == 0) index else points.lastIndex - index
            if (i > 0 || it % 2 == 0) {
              val a =
                (sectionAngle * it +
                    if (it % 2 == 0) angles[i] else sectionAngle - angles[i] + 2 * angles[0])
                  .toRadians()
              val finalPoint = Offset(cos(a), sin(a)) * distances[i] + center
              add(PointNRound(finalPoint, points[i].r))
            }
          }
        }
      }
    } else {
      points.size.let { np ->
        (0 until np * reps).map {
          val point = points[it % np].o.rotateDegrees((it / np) * 360f / reps, center)
          PointNRound(point, points[it % np].r)
        }
      }
    }

  private fun Offset.rotateDegrees(angle: Float, center: Offset = Offset.Zero) =
    (angle.toRadians()).let { a ->
      val off = this - center
      Offset(off.x * cos(a) - off.y * sin(a), off.x * sin(a) + off.y * cos(a)) + center
    }

  private fun Float.toRadians(): Float = this / 360f * 2 * PI.toFloat()

  private fun Offset.angleDegrees() = atan2(y, x) * 180f / PI.toFloat()

  private fun customPolygon(
    pnr: List<PointNRound>,
    reps: Int,
    center: Offset = Offset(0.5f, 0.5f),
    mirroring: Boolean = false,
    t: ShapeTweaks,
  ): RoundedPolygon {
    val actualPoints =
      doRepeat(pnr.map { it.copy(r = t.corner(it.r)) }, t.count(reps), center, mirroring)
    return RoundedPolygon(
      vertices =
        FloatArray(actualPoints.size * 2) { ix ->
          actualPoints[ix / 2].o.let { if (ix % 2 == 0) it.x else it.y }
        },
      perVertexRounding = actualPoints.map { it.r },
      centerX = center.x,
      centerY = center.y,
    )
  }

  /**
   * `Matrix`-free stand-ins for the rotate / scale transforms upstream applies. Material's own
   * `RoundedPolygon.transformed(Matrix)` is `internal`, so the same transform is expressed through
   * the public `PointTransformer` overload — identical arithmetic, applied about the origin exactly
   * as `Matrix.rotateZ` / `Matrix.scale` would.
   */
  private fun RoundedPolygon.rotated(degrees: Float): RoundedPolygon {
    val a = degrees.toRadians()
    val cosA = cos(a)
    val sinA = sin(a)
    return transformed { x, y -> TransformResult(x * cosA - y * sinA, x * sinA + y * cosA) }
  }

  private fun RoundedPolygon.scaled(scaleX: Float, scaleY: Float): RoundedPolygon =
    transformed { x, y ->
      TransformResult(x * scaleX, y * scaleY)
    }

  private fun recipe(
    stock: () -> RoundedPolygon,
    inlined: (ShapeTweaks) -> RoundedPolygon,
  ): MaterialShapeRecipe = MaterialShapeRecipe(stock, inlined)
}

/**
 * One [MaterialShapes] entry, in both of its forms.
 *
 * [resolve] is what a sticker calls: the library's own polygon while the knobs sit at their
 * defaults, the inlined rebuild the moment one moves. Nothing here is composable — the knob reading
 * happens in the sticker, so this whole file stays testable as plain Kotlin.
 */
class MaterialShapeRecipe
internal constructor(
  private val stock: () -> RoundedPolygon,
  private val inlined: (ShapeTweaks) -> RoundedPolygon,
) {
  /** The `MaterialShapes` entry itself — normalized by the library. */
  fun stock(): RoundedPolygon = stock.invoke()

  /**
   * The shape rebuilt from the construction inlined in [MaterialShapeRecipes], with [tweaks]
   * applied. At [ShapeTweaks.Default] this is the same outline as [stock], which
   * `MaterialShapeRecipeTest` pins.
   */
  fun inlined(tweaks: ShapeTweaks = ShapeTweaks.Default): RoundedPolygon =
    inlined.invoke(tweaks).normalized()

  /** [stock] while [tweaks] are untouched, [inlined] once any of them moves. */
  fun resolve(tweaks: ShapeTweaks): RoundedPolygon =
    if (tweaks.isDefault) stock() else inlined(tweaks)
}

/**
 * The knobs a shape's inlined construction exposes.
 *
 * Deliberately a small, shape-agnostic set rather than one parameter per literal: a `Heart` has
 * sixteen authored numbers and a UI offering all of them teaches nothing. These four are the axes
 * the constructions actually share — corner radius, corner smoothing, how many times the motif goes
 * round, and how deep a star's inner radius cuts — expressed as **multipliers over what Material
 * authored** so `1f` reproduces the shape and `0f` / `2f` say something about it. A shape whose
 * construction has no such axis (a rectangle has no repeat count) simply ignores that knob.
 */
data class ShapeTweaks(
  /**
   * Multiplies every corner radius the construction authors. `0f` gives the un-rounded skeleton.
   */
  val rounding: Float = 1f,
  /** Multiplies every corner smoothing. Clamped to the `0f..1f` [CornerRounding] accepts. */
  val smoothing: Float = 1f,
  /** Multiplies a star's inner radius (`Sunny`, the round-numbered cookies). Star shapes only. */
  val innerRadius: Float = 1f,
  /**
   * Replaces the construction's repeat count: a star's `numVerticesPerRadius`, a polygon's
   * `numVertices`, or a custom outline's `reps`. `0` keeps whatever the shape authored.
   */
  val count: Int = 0,
) {
  /** True while every knob is at its author default, which is what keeps `stock` in play. */
  val isDefault: Boolean
    get() = this == Default

  internal fun corner(r: CornerRounding): CornerRounding =
    if (isDefault) r
    else
      CornerRounding(
        radius = (r.radius * rounding).coerceAtLeast(0f),
        smoothing = (r.smoothing * smoothing).coerceIn(0f, 1f),
      )

  internal fun perVertex(vertices: Int, authored: List<CornerRounding>): List<CornerRounding> =
    List(vertices) { corner(authored[it % authored.size]) }

  internal fun count(authored: Int): Int = if (count > 0) count else authored

  internal fun innerRadius(authored: Float): Float = (authored * innerRadius).coerceIn(0.05f, 0.95f)

  companion object {
    /** Every knob at its author default — the state in which the stock `MaterialShapes` renders. */
    val Default: ShapeTweaks = ShapeTweaks()
  }
}
