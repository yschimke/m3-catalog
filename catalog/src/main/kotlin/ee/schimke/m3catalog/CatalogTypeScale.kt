package ee.schimke.m3catalog

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle

/**
 * The Material 3 **type scale**: the fifteen roles `Typography` carries, as one closed axis.
 *
 * The `style` knob used to be a raw `previewOverrideString` resolved by a `when` over string
 * literals, which spelled every role three times over — once as a `when` arm, once as the label the
 * specimen draws, once as an `@OverrideVariant` cell — with nothing relating the three. A role
 * added to one and not the others rendered, silently, as `Display Large` (#103).
 *
 * Here each role is one entry. The `when` is exhaustive over the enum so the compiler catches a
 * missing arm, [label] is derived from the entry name rather than authored beside it, and the cells
 * come out of [CatalogVariantMatrices.TypeScale] rather than being hand-typed.
 *
 * The knob slugs are the ones the published renders already carry, so declaring the set moves no
 * render and breaks no link — it only turns the viewer's free-text box into the picker the fifteen
 * values were always a picker's worth of.
 */
enum class TypeScaleRole(override val knob: String) : CatalogKnob {
  DisplayLarge("display-large"),
  DisplayMedium("display-medium"),
  DisplaySmall("display-small"),
  HeadlineLarge("headline-large"),
  HeadlineMedium("headline-medium"),
  HeadlineSmall("headline-small"),
  TitleLarge("title-large"),
  TitleMedium("title-medium"),
  TitleSmall("title-small"),
  BodyLarge("body-large"),
  BodyMedium("body-medium"),
  BodySmall("body-small"),
  LabelLarge("label-large"),
  LabelMedium("label-medium"),
  LabelSmall("label-small");

  /** The style this role names, straight off [MaterialTheme.typography]. */
  val style: TextStyle
    @Composable
    get() =
      when (this) {
        DisplayLarge -> MaterialTheme.typography.displayLarge
        DisplayMedium -> MaterialTheme.typography.displayMedium
        DisplaySmall -> MaterialTheme.typography.displaySmall
        HeadlineLarge -> MaterialTheme.typography.headlineLarge
        HeadlineMedium -> MaterialTheme.typography.headlineMedium
        HeadlineSmall -> MaterialTheme.typography.headlineSmall
        TitleLarge -> MaterialTheme.typography.titleLarge
        TitleMedium -> MaterialTheme.typography.titleMedium
        TitleSmall -> MaterialTheme.typography.titleSmall
        BodyLarge -> MaterialTheme.typography.bodyLarge
        BodyMedium -> MaterialTheme.typography.bodyMedium
        BodySmall -> MaterialTheme.typography.bodySmall
        LabelLarge -> MaterialTheme.typography.labelLarge
        LabelMedium -> MaterialTheme.typography.labelMedium
        LabelSmall -> MaterialTheme.typography.labelSmall
      }

  /**
   * What the specimen draws: the role's name, spaced — `Title Medium`.
   *
   * Derived from the entry rather than authored beside it, because the drawn label IS the role's
   * name and a second spelling of it is one more pair of strings that can disagree. The published
   * text is unchanged, which is what keeps this refactor off the rendered pixels.
   */
  val label: String
    get() = name.replace(Regex("(?<!^)(?=\\p{Lu})"), " ")

  companion object {
    /** The `style` axis, defaulting to [DisplayLarge] — what an unseeded specimen renders. */
    val Axis = CatalogKnobAxis("style", entries, DisplayLarge)
  }
}
