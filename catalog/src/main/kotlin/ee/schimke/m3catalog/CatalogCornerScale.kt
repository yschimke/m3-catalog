package ee.schimke.m3catalog

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * The Material **corner scale**: the ten radius tokens the kit's shape page draws, as one closed
 * axis.
 *
 * Same story as [TypeScaleRole] (#103): the `corner` knob was a raw string resolved by a `when`,
 * with each token spelled once there, once as a drawn label and once as an `@OverrideVariant` cell.
 * One entry per token now carries all three — the radius the silhouette is clipped to, the caption
 * under it, and the seed the cell publishes.
 *
 * [Full] is the one token that is not a `Dp`: it is the 50% radius a pill draws, so the radius is
 * null and both the shape and the caption branch on that rather than on a sentinel value.
 */
enum class CornerScaleToken(override val knob: String, val radius: Dp?) : CatalogKnob {
  None("none", 0.dp),
  ExtraSmall("extra-small", 4.dp),
  Small("small", 8.dp),
  Medium("medium", 12.dp),
  Large("large", 16.dp),
  LargeIncreased("large-increased", 20.dp),
  ExtraLarge("extra-large", 28.dp),
  ExtraLargeIncreased("extra-large-increased", 32.dp),
  ExtraExtraLarge("extra-extra-large", 48.dp),
  Full("full", null);

  /** The token as a [Shape]: its radius, or the 50% corner [Full] means. */
  val shape: Shape
    get() = radius?.let { RoundedCornerShape(it) } ?: RoundedCornerShape(percent = 50)

  /**
   * What the specimen draws under the silhouette — `Extra-large-increased · 32dp`, `Full · 50%`.
   *
   * The token name hyphenated, plus the measurement, both derived from the entry: the drawn caption
   * is the token and its radius, and neither is worth spelling a second time.
   */
  val label: String
    get() {
      val words = name.split(Regex("(?<!^)(?=\\p{Lu})"))
      val token = words.first() + words.drop(1).joinToString("") { "-" + it.lowercase() }
      return "$token · " + (radius?.let { "${it.value.toInt()}dp" } ?: "50%")
    }

  companion object {
    /** The `corner` axis, defaulting to [None] — the unrounded box an unseeded specimen draws. */
    val Axis = CatalogKnobAxis("corner", entries, None)
  }
}
