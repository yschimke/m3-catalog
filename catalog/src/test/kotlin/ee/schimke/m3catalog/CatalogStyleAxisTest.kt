package ee.schimke.m3catalog

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Pins the three Styles axes — the type scale, the corner scale and the colour modes — to what they
 * published **before** they became enums (#103).
 *
 * Each was a raw `previewOverrideString` resolved by a `when` over literals, with the drawn label
 * authored a second time beside it. The enums derive both the label and the variant cells from the
 * entry, which is the point; this is what keeps that derivation off the rendered pixels. Two of the
 * three draw their label into the sticker, so a label that came out `Display medium` rather than
 * `Display Large`'s sibling `Display Medium` would move every capture on the sheet.
 *
 * The knob slugs are pinned for a different reason: they are the seeds the published renders carry
 * and the ids a shared link resolves, so they are a compatibility surface, not an implementation
 * detail.
 */
class CatalogStyleAxisTest {

  @Test
  fun `the type scale draws the labels it published as literals`() {
    assertEquals(
      listOf(
        "Display Large",
        "Display Medium",
        "Display Small",
        "Headline Large",
        "Headline Medium",
        "Headline Small",
        "Title Large",
        "Title Medium",
        "Title Small",
        "Body Large",
        "Body Medium",
        "Body Small",
        "Label Large",
        "Label Medium",
        "Label Small",
      ),
      TypeScaleRole.entries.map { it.label },
      "the specimen draws this string, so a change here is a change to every type-scale render",
    )
    assertEquals(
      listOf(
        "display-large",
        "display-medium",
        "display-small",
        "headline-large",
        "headline-medium",
        "headline-small",
        "title-large",
        "title-medium",
        "title-small",
        "body-large",
        "body-medium",
        "body-small",
        "label-large",
        "label-medium",
        "label-small",
      ),
      TypeScaleRole.entries.map { it.knob },
      "the knob slugs are what the published cells seed and what a shared link resolves",
    )
    assertEquals(TypeScaleRole.DisplayLarge, TypeScaleRole.Axis.default)
  }

  @Test
  fun `the corner scale draws the captions it published as literals`() {
    assertEquals(
      listOf(
        "None · 0dp",
        "Extra-small · 4dp",
        "Small · 8dp",
        "Medium · 12dp",
        "Large · 16dp",
        "Large-increased · 20dp",
        "Extra-large · 28dp",
        "Extra-large-increased · 32dp",
        "Extra-extra-large · 48dp",
        "Full · 50%",
      ),
      CornerScaleToken.entries.map { it.label },
      "the specimen draws this caption under the silhouette",
    )
    assertEquals(
      listOf(
        "none",
        "extra-small",
        "small",
        "medium",
        "large",
        "large-increased",
        "extra-large",
        "extra-large-increased",
        "extra-extra-large",
        "full",
      ),
      CornerScaleToken.entries.map { it.knob },
    )
    assertEquals(RoundedCornerShape(0.dp), CornerScaleToken.None.shape)
    assertEquals(RoundedCornerShape(28.dp), CornerScaleToken.ExtraLarge.shape)
    assertEquals(
      RoundedCornerShape(percent = 50),
      CornerScaleToken.Full.shape,
      "`full` is the pill corner, not a radius in dp",
    )
    assertEquals(CornerScaleToken.None, CornerScaleToken.Axis.default)
  }

  @Test
  fun `each colour mode selects the scheme it names`() {
    assertEquals(
      listOf(
        "baseline-light",
        "baseline-dark",
        "light-medium-contrast",
        "light-high-contrast",
        "dark-medium-contrast",
        "dark-high-contrast",
      ),
      CatalogSchemeChoice.entries.map { it.knob },
    )
    assertEquals(BaselineLight, CatalogSchemeChoice.Light.scheme)
    assertEquals(BaselineDark, CatalogSchemeChoice.Dark.scheme)
    assertEquals(BaselineLightMediumContrast, CatalogSchemeChoice.LightMediumContrast.scheme)
    assertEquals(BaselineLightHighContrast, CatalogSchemeChoice.LightHighContrast.scheme)
    assertEquals(BaselineDarkMediumContrast, CatalogSchemeChoice.DarkMediumContrast.scheme)
    assertEquals(BaselineDarkHighContrast, CatalogSchemeChoice.DarkHighContrast.scheme)
    assertEquals(CatalogSchemeChoice.Light, CatalogSchemeChoice.Axis.default)
  }
}
