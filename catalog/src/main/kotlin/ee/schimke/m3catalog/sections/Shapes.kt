@file:CatalogGroup(name = "Shapes", section = "Styles")

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.overrides.previewOverrideFloat
import ee.schimke.composeai.overrides.previewOverrideInt
import ee.schimke.composeai.overrides.previewOverrideString
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.MaterialShapeRecipe
import ee.schimke.m3catalog.MaterialShapeRecipes
import ee.schimke.m3catalog.ShapeTweaks
import ee.schimke.m3catalog.Sticker

// The kit's `Shape` page is a specimen sheet, not a component sheet: 35 named shapes drawn as
// filled silhouettes, plus the corner-radius scale above them. Compose ships the same set as
// `MaterialShapes`, so each sticker here is one entry of that object and nothing else — the point
// of the sticker is the outline, and any content inside it would only be something else to compare.
//
// EVERY STICKER NAMES ITS FIGMA NODE, AND THAT IS THE FEATURE
//
// The `reference` on each component is the node id of that shape's symbol on the kit's `Shape`
// page. `generate-design-map.mjs` projects it into `design-map.json`, and
// `scripts/import-figma-pages.mjs` joins the imported page's `data-node-id` elements against that
// map — so the preview server can hide the kit's own drawing of `Shape=Circle` and put this
// catalog's `Shape/Circle` render in the hole it leaves. A shape with no `reference` still
// publishes; it just isn't swappable on the page view. See `docs/FIGMA_PAGES.md`.
//
// One component per shape rather than one component with 35 variants, which is the exception to
// the fold-variants-behind-defaults rule in `AGENTS.md` and worth stating. These are not a state
// axis of one thing: the kit models each as its own symbol with its own node id, and only a
// per-component `reference` can carry that id — `@CatalogVariant` has no such argument. Folding
// them would cost the join the whole surface is built on.
//
// `Shape=Hexagon` is the kit's layer name for the shape its own caption calls **Clamshell**, and
// Compose calls `MaterialShapes.ClamShell`. The reference follows the node id, not the name.
//
// STOCK BY DEFAULT, INLINED WHEN A KNOB MOVES
//
// A sticker draws `MaterialShapes.<Name>` — the library's own polygon, so the published render is
// exactly what a consumer of Material gets, and the specimen sheet stays a statement about Material
// rather than about this repo's arithmetic. But a finished `RoundedPolygon` has no seam: there is
// nothing to adjust and nothing to learn from it beyond its silhouette.
//
// So each sticker takes a `MaterialShapeRecipe` instead of a polygon, and reads the four shape
// knobs below. While they sit at their defaults the stock entry renders, untouched. Move one and
// the sticker switches to the construction inlined in `MaterialShapeRecipes` — Material's own
// builder for that shape, transcribed with the corner radii, smoothing, repeat count and star inner
// radius left adjustable. `MaterialShapeRecipeTest` asserts the two agree cubic-for-cubic at the
// defaults, so the switch changes what you can change, never what you see by default.

@Composable
private fun cornerScaleRole(): Pair<String, Shape> =
  when (previewOverrideString("corner", "none")) {
    "extra-small" -> "Extra-small · 4dp" to RoundedCornerShape(4.dp)
    "small" -> "Small · 8dp" to RoundedCornerShape(8.dp)
    "medium" -> "Medium · 12dp" to RoundedCornerShape(12.dp)
    "large" -> "Large · 16dp" to RoundedCornerShape(16.dp)
    "large-increased" -> "Large-increased · 20dp" to RoundedCornerShape(20.dp)
    "extra-large" -> "Extra-large · 28dp" to RoundedCornerShape(28.dp)
    "extra-large-increased" -> "Extra-large-increased · 32dp" to RoundedCornerShape(32.dp)
    "extra-extra-large" -> "Extra-extra-large · 48dp" to RoundedCornerShape(48.dp)
    "full" -> "Full · 50%" to RoundedCornerShape(percent = 50)
    else -> "None · 0dp" to RoundedCornerShape(0.dp)
  }

@CatalogComponent(
  id = "Shape/Corner scale",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58548:7181",
  caption = "The Material corner scale, with each of its ten radius tokens folded in as a variant.",
)
@CatalogModes
@OverrideVariant(name = "extra-small", strings = ["corner=extra-small"])
@OverrideVariant(name = "small", strings = ["corner=small"])
@OverrideVariant(name = "medium", strings = ["corner=medium"])
@OverrideVariant(name = "large", strings = ["corner=large"])
@OverrideVariant(name = "large-increased", strings = ["corner=large-increased"])
@OverrideVariant(name = "extra-large", strings = ["corner=extra-large"])
@OverrideVariant(name = "extra-large-increased", strings = ["corner=extra-large-increased"])
@OverrideVariant(name = "extra-extra-large", strings = ["corner=extra-extra-large"])
@OverrideVariant(name = "full", strings = ["corner=full"])
@Composable
fun CornerScaleSticker() = Sticker {
  val (name, shape) = cornerScaleRole()
  Column(
    modifier = Modifier.size(width = 220.dp, height = 176.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Box(Modifier.size(136.dp).clip(shape).background(MaterialTheme.colorScheme.primary))
    Text(name, style = MaterialTheme.typography.labelLarge)
  }
}

@Composable
private fun ShapeSticker(recipe: MaterialShapeRecipe) = Sticker {
  val shape = recipe.resolve(shapeTweaks())
  Box(Modifier.size(96.dp).clip(shape.toShape()).background(MaterialTheme.colorScheme.primary))
}

/**
 * The shape knobs, read once per sticker so every shape offers the same four. Each is a multiplier
 * over what Material authored (`count` a replacement), so the default row is the identity and the
 * stock polygon stays in play — see [ShapeTweaks].
 */
@Composable
private fun shapeTweaks(): ShapeTweaks =
  ShapeTweaks(
    rounding = previewOverrideFloat("rounding", 1f),
    smoothing = previewOverrideFloat("smoothing", 1f),
    innerRadius = previewOverrideFloat("innerRadius", 1f),
    count = previewOverrideInt("count", 0),
  )

@CatalogComponent(
  id = "Shape/Circle",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58548:7249",
  caption = "The kit's Circle shape, drawn from `MaterialShapes.Circle`.",
)
@CatalogModes
@Composable
fun CircleShape() = ShapeSticker(MaterialShapeRecipes.Circle)

@CatalogComponent(
  id = "Shape/Square",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58548:7251",
  caption = "The kit's Square shape, drawn from `MaterialShapes.Square`.",
)
@CatalogModes
@Composable
fun SquareShape() = ShapeSticker(MaterialShapeRecipes.Square)

@CatalogComponent(
  id = "Shape/Slanted",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58548:7253",
  caption = "The kit's Slanted shape, drawn from `MaterialShapes.Slanted`.",
)
@CatalogModes
@Composable
fun SlantedShape() = ShapeSticker(MaterialShapeRecipes.Slanted)

@CatalogComponent(
  id = "Shape/Arch",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58548:7255",
  caption = "The kit's Arch shape, drawn from `MaterialShapes.Arch`.",
)
@CatalogModes
@Composable
fun ArchShape() = ShapeSticker(MaterialShapeRecipes.Arch)

@CatalogComponent(
  id = "Shape/Fan",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58548:7257",
  caption = "The kit's Fan shape, drawn from `MaterialShapes.Fan`.",
)
@CatalogModes
@Composable
fun FanShape() = ShapeSticker(MaterialShapeRecipes.Fan)

@CatalogComponent(
  id = "Shape/Arrow",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58548:7259",
  caption = "The kit's Arrow shape, drawn from `MaterialShapes.Arrow`.",
)
@CatalogModes
@Composable
fun ArrowShape() = ShapeSticker(MaterialShapeRecipes.Arrow)

@CatalogComponent(
  id = "Shape/SemiCircle",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58548:7261",
  caption = "The kit's Semicircle shape, drawn from `MaterialShapes.SemiCircle`.",
)
@CatalogModes
@Composable
fun SemiCircleShape() = ShapeSticker(MaterialShapeRecipes.SemiCircle)

@CatalogComponent(
  id = "Shape/Oval",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58548:7263",
  caption = "The kit's Oval shape, drawn from `MaterialShapes.Oval`.",
)
@CatalogModes
@Composable
fun OvalShape() = ShapeSticker(MaterialShapeRecipes.Oval)

@CatalogComponent(
  id = "Shape/Pill",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58548:7265",
  caption = "The kit's Pill shape, drawn from `MaterialShapes.Pill`.",
)
@CatalogModes
@Composable
fun PillShape() = ShapeSticker(MaterialShapeRecipes.Pill)

@CatalogComponent(
  id = "Shape/Triangle",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58548:7267",
  caption = "The kit's Triangle shape, drawn from `MaterialShapes.Triangle`.",
)
@CatalogModes
@Composable
fun TriangleShape() = ShapeSticker(MaterialShapeRecipes.Triangle)

@CatalogComponent(
  id = "Shape/Diamond",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58548:7269",
  caption = "The kit's Diamond shape, drawn from `MaterialShapes.Diamond`.",
)
@CatalogModes
@Composable
fun DiamondShape() = ShapeSticker(MaterialShapeRecipes.Diamond)

@CatalogComponent(
  id = "Shape/ClamShell",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58548:7271",
  caption = "The kit's Clamshell shape, drawn from `MaterialShapes.ClamShell`.",
)
@CatalogModes
@Composable
fun ClamShellShape() = ShapeSticker(MaterialShapeRecipes.ClamShell)

@CatalogComponent(
  id = "Shape/Pentagon",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58548:7273",
  caption = "The kit's Pentagon shape, drawn from `MaterialShapes.Pentagon`.",
)
@CatalogModes
@Composable
fun PentagonShape() = ShapeSticker(MaterialShapeRecipes.Pentagon)

@CatalogComponent(
  id = "Shape/Gem",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58548:7275",
  caption = "The kit's Gem shape, drawn from `MaterialShapes.Gem`.",
)
@CatalogModes
@Composable
fun GemShape() = ShapeSticker(MaterialShapeRecipes.Gem)

@CatalogComponent(
  id = "Shape/VerySunny",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58548:7277",
  caption = "The kit's Very sunny shape, drawn from `MaterialShapes.VerySunny`.",
)
@CatalogModes
@Composable
fun VerySunnyShape() = ShapeSticker(MaterialShapeRecipes.VerySunny)

// Sunny carries the two demonstration cells for the knob seam — every shape reads the same four
// knobs, and one shape baking two of them is enough to hold the inlined path in the published sheet
// rather than only in a unit test. It is also the clearest shape to show them on: it is a star, so
// it answers both knobs visibly (`rounding=0` gives the raw 8-pointed skeleton, `count=12` the same
// star with twelve points). Baking these on all 35 would double the shape renders to say the same
// thing 35 times.
@CatalogComponent(
  id = "Shape/Sunny",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58548:7279",
  caption =
    "The kit's Sunny shape, drawn from `MaterialShapes.Sunny`. Its `rounding` / `count` knobs " +
      "rebuild it from the inlined star.",
)
@CatalogModes
@OverrideVariant(name = "unrounded", floats = ["rounding=0.0"])
@OverrideVariant(name = "count-12", ints = ["count=12"])
@Composable
fun SunnyShape() = ShapeSticker(MaterialShapeRecipes.Sunny)

@CatalogComponent(
  id = "Shape/Cookie4Sided",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58548:7281",
  caption = "The kit's 4-sided cookie shape, drawn from `MaterialShapes.Cookie4Sided`.",
)
@CatalogModes
@Composable
fun Cookie4SidedShape() = ShapeSticker(MaterialShapeRecipes.Cookie4Sided)

@CatalogComponent(
  id = "Shape/Cookie6Sided",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58548:7283",
  caption = "The kit's 6-sided cookie shape, drawn from `MaterialShapes.Cookie6Sided`.",
)
@CatalogModes
@Composable
fun Cookie6SidedShape() = ShapeSticker(MaterialShapeRecipes.Cookie6Sided)

@CatalogComponent(
  id = "Shape/Cookie7Sided",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58548:7285",
  caption = "The kit's 7-sided cookie shape, drawn from `MaterialShapes.Cookie7Sided`.",
)
@CatalogModes
@Composable
fun Cookie7SidedShape() = ShapeSticker(MaterialShapeRecipes.Cookie7Sided)

@CatalogComponent(
  id = "Shape/Cookie9Sided",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58548:7287",
  caption = "The kit's 9-sided cookie shape, drawn from `MaterialShapes.Cookie9Sided`.",
)
@CatalogModes
@Composable
fun Cookie9SidedShape() = ShapeSticker(MaterialShapeRecipes.Cookie9Sided)

@CatalogComponent(
  id = "Shape/Cookie12Sided",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58548:7289",
  caption = "The kit's 12-sided cookie shape, drawn from `MaterialShapes.Cookie12Sided`.",
)
@CatalogModes
@Composable
fun Cookie12SidedShape() = ShapeSticker(MaterialShapeRecipes.Cookie12Sided)

@CatalogComponent(
  id = "Shape/Ghostish",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58548:7291",
  caption = "The kit's Ghost-ish shape, drawn from `MaterialShapes.Ghostish`.",
)
@CatalogModes
@Composable
fun GhostishShape() = ShapeSticker(MaterialShapeRecipes.Ghostish)

@CatalogComponent(
  id = "Shape/Clover4Leaf",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58548:7293",
  caption = "The kit's 4-leaf clover shape, drawn from `MaterialShapes.Clover4Leaf`.",
)
@CatalogModes
@Composable
fun Clover4LeafShape() = ShapeSticker(MaterialShapeRecipes.Clover4Leaf)

@CatalogComponent(
  id = "Shape/Clover8Leaf",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58548:7295",
  caption = "The kit's 8-leaf clover shape, drawn from `MaterialShapes.Clover8Leaf`.",
)
@CatalogModes
@Composable
fun Clover8LeafShape() = ShapeSticker(MaterialShapeRecipes.Clover8Leaf)

@CatalogComponent(
  id = "Shape/Burst",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58548:7297",
  caption = "The kit's Burst shape, drawn from `MaterialShapes.Burst`.",
)
@CatalogModes
@Composable
fun BurstShape() = ShapeSticker(MaterialShapeRecipes.Burst)

@CatalogComponent(
  id = "Shape/SoftBurst",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58548:7299",
  caption = "The kit's Soft burst shape, drawn from `MaterialShapes.SoftBurst`.",
)
@CatalogModes
@Composable
fun SoftBurstShape() = ShapeSticker(MaterialShapeRecipes.SoftBurst)

@CatalogComponent(
  id = "Shape/Boom",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58548:7301",
  caption = "The kit's Boom shape, drawn from `MaterialShapes.Boom`.",
)
@CatalogModes
@Composable
fun BoomShape() = ShapeSticker(MaterialShapeRecipes.Boom)

@CatalogComponent(
  id = "Shape/SoftBoom",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58548:7303",
  caption = "The kit's Soft boom shape, drawn from `MaterialShapes.SoftBoom`.",
)
@CatalogModes
@Composable
fun SoftBoomShape() = ShapeSticker(MaterialShapeRecipes.SoftBoom)

@CatalogComponent(
  id = "Shape/Flower",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58548:7305",
  caption = "The kit's Flower shape, drawn from `MaterialShapes.Flower`.",
)
@CatalogModes
@Composable
fun FlowerShape() = ShapeSticker(MaterialShapeRecipes.Flower)

@CatalogComponent(
  id = "Shape/Puffy",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58548:7307",
  caption = "The kit's Puffy shape, drawn from `MaterialShapes.Puffy`.",
)
@CatalogModes
@Composable
fun PuffyShape() = ShapeSticker(MaterialShapeRecipes.Puffy)

@CatalogComponent(
  id = "Shape/PuffyDiamond",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58548:7309",
  caption = "The kit's Puffy diamond shape, drawn from `MaterialShapes.PuffyDiamond`.",
)
@CatalogModes
@Composable
fun PuffyDiamondShape() = ShapeSticker(MaterialShapeRecipes.PuffyDiamond)

@CatalogComponent(
  id = "Shape/PixelCircle",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58548:7311",
  caption = "The kit's Pixel circle shape, drawn from `MaterialShapes.PixelCircle`.",
)
@CatalogModes
@Composable
fun PixelCircleShape() = ShapeSticker(MaterialShapeRecipes.PixelCircle)

@CatalogComponent(
  id = "Shape/PixelTriangle",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58548:7313",
  caption = "The kit's Pixel triangle shape, drawn from `MaterialShapes.PixelTriangle`.",
)
@CatalogModes
@Composable
fun PixelTriangleShape() = ShapeSticker(MaterialShapeRecipes.PixelTriangle)

@CatalogComponent(
  id = "Shape/Bun",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58548:7315",
  caption = "The kit's Bun shape, drawn from `MaterialShapes.Bun`.",
)
@CatalogModes
@Composable
fun BunShape() = ShapeSticker(MaterialShapeRecipes.Bun)

@CatalogComponent(
  id = "Shape/Heart",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58548:7317",
  caption = "The kit's Heart shape, drawn from `MaterialShapes.Heart`.",
)
@CatalogModes
@Composable
fun HeartShape() = ShapeSticker(MaterialShapeRecipes.Heart)
