package ee.schimke.m3catalog

import java.util.Locale
import kotlin.test.Test
import kotlin.test.assertEquals

class CatalogLocaleTest {

  @Test
  fun shapesDigitsForTheLocaleNumberingSystem() {
    assertEquals("10:30", localizedDigits("10:30", Locale.US))
    assertEquals("١٠:٣٠", localizedDigits("10:30", Locale.forLanguageTag("ar-EG")))
    assertEquals(
      "१२३४+",
      localizedDigits("1234+", Locale.forLanguageTag("hi-IN-u-nu-deva")),
    )
  }
}
