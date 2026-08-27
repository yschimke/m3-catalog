package ee.schimke.m3catalog

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Pins the visible copy authored on the mapped Figma components' default instances. */
class FigmaDefaultContentTest {

  private val stringPattern =
    Regex("""<string name="([^"]+)">(.*?)</string>""", RegexOption.DOT_MATCHES_ALL)

  private val defaults =
    stringPattern
      .findAll(File("src/main/composeResources/values/strings.xml").readText())
      .associate { it.groupValues[1] to it.groupValues[2] }

  /**
   * `defaults` is read out of the XML with a regex, and the assertion below resolves each expected
   * key against it. A pattern that stopped matching would fail that lookup rather than pass
   * vacuously, so this class is not silently weakenable the way [CatalogInventoryTest]'s scans are
   * — but the failure would read as thirty missing keys instead of one broken reader. Issue #108.
   */
  @Test
  fun theStringScanFindsTheResources() {
    assertTrue(
      defaults.size >= 60,
      "values/strings.xml yielded only ${defaults.size} strings — the <string> pattern, not the " +
        "resource file, is what changed",
    )
  }

  @Test
  fun mappedDefaultsUseTheKitCopy() {
    val expected =
      mapOf(
        "label_filled" to "Label",
        "label_tonal" to "Label",
        "label_outlined" to "Label",
        "label_elevated" to "Label",
        "label_text" to "Label",
        "label_on" to "Label",
        "chip_add_to_calendar" to "Label",
        "chip_unread" to "Label",
        "chip_sounds_good" to "Label",
        "nav_home" to "Label",
        "nav_search" to "Label",
        "nav_you" to "Label",
        "segment_day" to "Label",
        "segment_week" to "Label",
        "segment_month" to "Label",
        "card_title" to "Title",
        "card_supporting" to
          "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor",
        "search_hint" to "Hinted search text",
        "search_app_hint" to "Search product",
        "search_input_text" to "Input text",
        "search_suggestion_sticker_sheet" to "Label text",
        "list_item" to "Label text",
        "list_supporting" to "Supporting line text, lorem ipsum dolor",
        "snackbar_short" to "Single-line snackbar",
        "field_name" to "Label",
        "field_placeholder" to "Input",
        "field_supporting" to "Supporting text",
        "tooltip_title" to "Title",
        "tooltip_body" to "Supporting line text lorem ipsum dolor sit amet, consectetur",
        "appbar_title" to "Label",
      )

    assertEquals(expected, expected.keys.associateWith(defaults::getValue))
  }
}
