@file:CatalogGroup(name = "Shape", section = "Styles")

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.overrides.previewOverrideString
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker

// Shape and corner names are design-system token names, so they deliberately remain literals.

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
private fun expressiveShapeRole(): Pair<String, Shape> =
  when (previewOverrideString("shape", "circle")) {
    "square" -> "Square" to MaterialShapes.Square.toShape()
    "slanted" -> "Slanted" to MaterialShapes.Slanted.toShape()
    "arch" -> "Arch" to MaterialShapes.Arch.toShape()
    "fan" -> "Fan" to MaterialShapes.Fan.toShape()
    "arrow" -> "Arrow" to MaterialShapes.Arrow.toShape()
    "semicircle" -> "Semicircle" to MaterialShapes.SemiCircle.toShape()
    "oval" -> "Oval" to MaterialShapes.Oval.toShape()
    "pill" -> "Pill" to MaterialShapes.Pill.toShape()
    "triangle" -> "Triangle" to MaterialShapes.Triangle.toShape()
    "diamond" -> "Diamond" to MaterialShapes.Diamond.toShape()
    // Compose names the kit's rounded Hexagon geometry `ClamShell`.
    "hexagon" -> "Hexagon" to MaterialShapes.ClamShell.toShape()
    "pentagon" -> "Pentagon" to MaterialShapes.Pentagon.toShape()
    "gem" -> "Gem" to MaterialShapes.Gem.toShape()
    "very sunny" -> "Very sunny" to MaterialShapes.VerySunny.toShape()
    "sunny" -> "Sunny" to MaterialShapes.Sunny.toShape()
    "4-sided cookie" -> "4-sided cookie" to MaterialShapes.Cookie4Sided.toShape()
    "6-sided cookie" -> "6-sided cookie" to MaterialShapes.Cookie6Sided.toShape()
    "7-sided cookie" -> "7-sided cookie" to MaterialShapes.Cookie7Sided.toShape()
    "9-sided cookie" -> "9-sided cookie" to MaterialShapes.Cookie9Sided.toShape()
    "12-sided cookie" -> "12-sided cookie" to MaterialShapes.Cookie12Sided.toShape()
    "ghost-ish" -> "Ghost-ish" to MaterialShapes.Ghostish.toShape()
    "4-leaf clover" -> "4-leaf clover" to MaterialShapes.Clover4Leaf.toShape()
    "8-leaf clover" -> "8-leaf clover" to MaterialShapes.Clover8Leaf.toShape()
    "burst" -> "Burst" to MaterialShapes.Burst.toShape()
    "soft burst" -> "Soft burst" to MaterialShapes.SoftBurst.toShape()
    "boom" -> "Boom" to MaterialShapes.Boom.toShape()
    "soft boom" -> "Soft boom" to MaterialShapes.SoftBoom.toShape()
    "flower" -> "Flower" to MaterialShapes.Flower.toShape()
    "puffy" -> "Puffy" to MaterialShapes.Puffy.toShape()
    "puffy diamond" -> "Puffy diamond" to MaterialShapes.PuffyDiamond.toShape()
    "pixel circle" -> "Pixel Circle" to MaterialShapes.PixelCircle.toShape()
    "pixel triangle" -> "Pixel triangle" to MaterialShapes.PixelTriangle.toShape()
    "bun" -> "Bun" to MaterialShapes.Bun.toShape()
    "heart" -> "Heart" to MaterialShapes.Heart.toShape()
    else -> "Circle" to MaterialShapes.Circle.toShape()
  }

@CatalogComponent(
  id = "Shape/Expressive shapes",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58548:7249",
  referenceSet = "figma:ocdacdEsnHipMJD3egzxKb/58548:7248",
  caption = "The Material expressive shape library, with all thirty-five forms as variants.",
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
@OverrideVariant(name = "cookie-4", strings = ["shape=4-sided cookie"])
@OverrideVariant(name = "cookie-6", strings = ["shape=6-sided cookie"])
@OverrideVariant(name = "cookie-7", strings = ["shape=7-sided cookie"])
@OverrideVariant(name = "cookie-9", strings = ["shape=9-sided cookie"])
@OverrideVariant(name = "cookie-12", strings = ["shape=12-sided cookie"])
@OverrideVariant(name = "ghost-ish", strings = ["shape=ghost-ish"])
@OverrideVariant(name = "clover-4", strings = ["shape=4-leaf clover"])
@OverrideVariant(name = "clover-8", strings = ["shape=8-leaf clover"])
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
@Composable
fun ExpressiveShapeSticker() = Sticker {
  val (name, shape) = expressiveShapeRole()
  Column(
    modifier = Modifier.size(width = 220.dp, height = 176.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Box(Modifier.size(136.dp).clip(shape).background(MaterialTheme.colorScheme.primary))
    Text(name, style = MaterialTheme.typography.labelLarge)
  }
}
