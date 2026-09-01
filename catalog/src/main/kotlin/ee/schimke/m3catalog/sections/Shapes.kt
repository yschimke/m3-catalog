@file:CatalogGroup(name = "Shapes", section = "Styles")
@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.overrides.previewOverrideFloat
import ee.schimke.composeai.overrides.previewOverrideInt
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.CornerScaleMatrix
import ee.schimke.m3catalog.CornerScaleToken
import ee.schimke.m3catalog.MaterialShapeRecipe
import ee.schimke.m3catalog.MaterialShapeRecipes
import ee.schimke.m3catalog.ShapeTweaks
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogChoice

// The kit's `Shape` page is a specimen sheet, not a component sheet: 35 named shapes drawn as
// filled silhouettes, plus the corner-radius scale above them. Compose ships the same set as
// `MaterialShapes`, so each sticker here is one entry of that object and nothing else — the point
// of the sticker is the outline, and any content inside it would only be something else to compare.
//
// ONE COMPONENT, THIRTY-FIVE CELLS
//
// The kit models these as ONE component set — `Shape Set`, varying a single `Shape=` property — so
// by this catalog's own rule (membership is the kit's call, see `AGENTS.md`) they are one component
// with a shape axis, not 35 components. They used to be 35, and the comment here justified that on
// tooling grounds: `@CatalogVariant` has no `reference` argument, so folding was said to cost each
// shape its node id. That was never the whole picture and is now simply wrong — `@design-parity/
// kit-index` resolves each cell's seed against the parent's component set and supplies the node,
// the same way `type=wave` resolves for the progress indicators.
//
// EVERY CELL STILL NAMES ITS FIGMA NODE, AND THAT IS THE FEATURE
//
// This is what the fold had to preserve, and does. `design-map.json` carries the component's `ref`
// as a TAGGED ARRAY — the base node plus one per cell, each tagged with the cell's state — and
// `scripts/import-figma-pages.mjs` walks that array, so the preview server can still hide the kit's
// own drawing of `Shape=Circle` and put this catalog's render in the hole it leaves. All 35 node
// ids stay addressable; what changed is that they hang off one component instead of 35. A shape
// whose seed fails to resolve drops out of that array silently, which is why `CatalogShapeSetTest`
// exists and why the seed keys below are the kit's spelling. See `docs/FIGMA_PAGES.md`.
//
// The knob is a `MaterialShapeRecipe` selector rather than 35 functions, which is exactly how the
// `Shape/Corner scale` sticker above already handles its ten radius tokens. Renders are unchanged
// in number and in content: 35 silhouettes, plus Sunny's two knob-seam cells.
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

@CatalogComponent(
  id = "Shape/Corner scale",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58548:7181",
  caption = "The Material corner scale, with each of its ten radius tokens folded in as a variant.",
)
@CatalogModes
@CornerScaleMatrix
@Composable
fun CornerScaleSticker() = Sticker {
  val token = CornerScaleToken.Axis.current()
  Column(
    modifier = Modifier.size(width = 220.dp, height = 176.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Box(Modifier.size(136.dp).clip(token.shape).background(MaterialTheme.colorScheme.primary))
    Text(token.label, style = MaterialTheme.typography.labelLarge)
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

/**
 * Every shape in the set, keyed by THE KIT'S OWN variant value, lower-cased.
 *
 * The key is the seed the `shape` knob takes, and the kit-index resolver matches that seed against
 * the `Shape Set`'s `Shape=` property to find each cell's node — so the key has to be the kit's
 * spelling, not Compose's. Mostly they agree. Where they do not, the kit wins and the Compose name
 * survives only on the right-hand side: `hexagon` is `MaterialShapes.ClamShell` (the kit's layer
 * name for the shape its own caption calls Clamshell), and the cookies and clovers put their count
 * first. A key spelled the Compose way resolves to nothing and drops that shape's node silently —
 * see yschimke/compose-ai-tools#4086, which is the gap that would let this name both sides.
 *
 * `CatalogShapeSetTest` holds this table to [MaterialShapeRecipes.All] and to the
 * `@OverrideVariant` cells below, so a shape cannot be added to one and forgotten in the others.
 */
internal val SHAPE_SET: List<Pair<String, MaterialShapeRecipe>> =
  listOf(
    "circle" to MaterialShapeRecipes.Circle,
    "square" to MaterialShapeRecipes.Square,
    "slanted" to MaterialShapeRecipes.Slanted,
    "arch" to MaterialShapeRecipes.Arch,
    "fan" to MaterialShapeRecipes.Fan,
    "arrow" to MaterialShapeRecipes.Arrow,
    "semicircle" to MaterialShapeRecipes.SemiCircle,
    "oval" to MaterialShapeRecipes.Oval,
    "pill" to MaterialShapeRecipes.Pill,
    "triangle" to MaterialShapeRecipes.Triangle,
    "diamond" to MaterialShapeRecipes.Diamond,
    "hexagon" to MaterialShapeRecipes.ClamShell,
    "pentagon" to MaterialShapeRecipes.Pentagon,
    "gem" to MaterialShapeRecipes.Gem,
    "very sunny" to MaterialShapeRecipes.VerySunny,
    "sunny" to MaterialShapeRecipes.Sunny,
    "4-sided cookie" to MaterialShapeRecipes.Cookie4Sided,
    "6-sided cookie" to MaterialShapeRecipes.Cookie6Sided,
    "7-sided cookie" to MaterialShapeRecipes.Cookie7Sided,
    "9-sided cookie" to MaterialShapeRecipes.Cookie9Sided,
    "12-sided cookie" to MaterialShapeRecipes.Cookie12Sided,
    "ghost-ish" to MaterialShapeRecipes.Ghostish,
    "4-leaf clover" to MaterialShapeRecipes.Clover4Leaf,
    "8-leaf clover" to MaterialShapeRecipes.Clover8Leaf,
    "burst" to MaterialShapeRecipes.Burst,
    "soft burst" to MaterialShapeRecipes.SoftBurst,
    "boom" to MaterialShapeRecipes.Boom,
    "soft boom" to MaterialShapeRecipes.SoftBoom,
    "flower" to MaterialShapeRecipes.Flower,
    "puffy" to MaterialShapeRecipes.Puffy,
    "puffy diamond" to MaterialShapeRecipes.PuffyDiamond,
    "pixel circle" to MaterialShapeRecipes.PixelCircle,
    "pixel triangle" to MaterialShapeRecipes.PixelTriangle,
    "bun" to MaterialShapeRecipes.Bun,
    "heart" to MaterialShapeRecipes.Heart,
  )

@Composable
private fun catalogShape(): MaterialShapeRecipe {
  val key = catalogChoice("shape", "circle", *SHAPE_SET.map { it.first }.toTypedArray())
  return SHAPE_SET.firstOrNull { it.first == key }?.second ?: MaterialShapeRecipes.Circle
}

@CatalogComponent(
  id = "Shape/MaterialShapes",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58548:7249",
  caption = "The expressive shape library. All 35 of `MaterialShapes` fold in as variants.",
)
@CatalogModes
@OverrideVariant(name = "square", strings = ["shape=square"])
@OverrideVariant(name = "slanted", strings = ["shape=slanted"])
@OverrideVariant(name = "arch", strings = ["shape=arch"])
@OverrideVariant(name = "fan", strings = ["shape=fan"])
@OverrideVariant(name = "arrow", strings = ["shape=arrow"])
@OverrideVariant(name = "semicircle", strings = ["shape=semicircle"])
@OverrideVariant(name = "oval", strings = ["shape=oval"])
@OverrideVariant(name = "pill", strings = ["shape=pill"])
@OverrideVariant(name = "triangle", strings = ["shape=triangle"])
@OverrideVariant(name = "diamond", strings = ["shape=diamond"])
@OverrideVariant(name = "hexagon", strings = ["shape=hexagon"])
@OverrideVariant(name = "pentagon", strings = ["shape=pentagon"])
@OverrideVariant(name = "gem", strings = ["shape=gem"])
@OverrideVariant(name = "very-sunny", strings = ["shape=very sunny"])
@OverrideVariant(name = "sunny", strings = ["shape=sunny"])
@OverrideVariant(name = "4-sided-cookie", strings = ["shape=4-sided cookie"])
@OverrideVariant(name = "6-sided-cookie", strings = ["shape=6-sided cookie"])
@OverrideVariant(name = "7-sided-cookie", strings = ["shape=7-sided cookie"])
@OverrideVariant(name = "9-sided-cookie", strings = ["shape=9-sided cookie"])
@OverrideVariant(name = "12-sided-cookie", strings = ["shape=12-sided cookie"])
@OverrideVariant(name = "ghost-ish", strings = ["shape=ghost-ish"])
@OverrideVariant(name = "4-leaf-clover", strings = ["shape=4-leaf clover"])
@OverrideVariant(name = "8-leaf-clover", strings = ["shape=8-leaf clover"])
@OverrideVariant(name = "burst", strings = ["shape=burst"])
@OverrideVariant(name = "soft-burst", strings = ["shape=soft burst"])
@OverrideVariant(name = "boom", strings = ["shape=boom"])
@OverrideVariant(name = "soft-boom", strings = ["shape=soft boom"])
@OverrideVariant(name = "flower", strings = ["shape=flower"])
@OverrideVariant(name = "puffy", strings = ["shape=puffy"])
@OverrideVariant(name = "puffy-diamond", strings = ["shape=puffy diamond"])
@OverrideVariant(name = "pixel-circle", strings = ["shape=pixel circle"])
@OverrideVariant(name = "pixel-triangle", strings = ["shape=pixel triangle"])
@OverrideVariant(name = "bun", strings = ["shape=bun"])
@OverrideVariant(name = "heart", strings = ["shape=heart"])
// Sunny carries the two demonstration cells for the knob seam — every shape reads the same four
// knobs, and one shape baking two of them is enough to hold the inlined path in the published sheet
// rather than only in a unit test. It is also the clearest shape to show them on: it is a star, so
// it answers both knobs visibly (`rounding=0` gives the raw 8-pointed skeleton, `count=12` the same
// star with twelve points). Baking these on all 35 would double the shape renders to say the same
// thing 35 times.
@OverrideVariant(name = "sunny-unrounded", strings = ["shape=sunny"], floats = ["rounding=0.0"])
@OverrideVariant(name = "sunny-count-12", strings = ["shape=sunny"], ints = ["count=12"])
@Composable
fun MaterialShapesSticker() = ShapeSticker(catalogShape())
