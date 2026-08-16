package ee.schimke.m3catalog

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import ee.schimke.m3catalog.sections.DatePickerModalSticker
import ee.schimke.m3catalog.sections.TimePickerSticker
import java.util.Locale
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

/**
 * The live lane's contract, which no baked capture can hold: a sticker composed with
 * `LocalInspectionMode = false` must **visibly change** when it is clicked.
 *
 * `CatalogInventoryTest` guards what the catalog publishes and the render pipeline guards what it
 * looks like; between them sits the failure this test exists for — a sticker that renders perfectly
 * and is inert. Both cases here shipped that way: the date picker's headline was once fixed sample
 * copy, so picking a date moved the grid selection under a stale caption, and the time picker's
 * mode switch carried the click tally, whose only effect was on a `contentDescription` nothing
 * paints. Neither is visible to a render diff, because the baked frame — the one the published
 * catalog shows — is correct in both the broken and the fixed build.
 *
 * Locale is pinned to US for the duration: the assertions read formatted dates, which the picker
 * derives from the default locale, and a test that only passes on an `en-US` machine is worse than
 * no test.
 */
@OptIn(ExperimentalTestApi::class)
class CatalogInteractionTest {

  private var previousLocale: Locale? = null

  @BeforeTest
  fun pinLocale() {
    previousLocale = Locale.getDefault()
    Locale.setDefault(Locale.US)
  }

  @AfterTest
  fun restoreLocale() {
    previousLocale?.let { Locale.setDefault(it) }
  }

  @Test
  fun pickingADateMovesTheSelection() = runComposeUiTest {
    setContent { DatePickerModalSticker() }

    // A day cell carries its verbose date as its semantics text, not the bare number.
    val seeded = onNodeWithText("Sunday, August 17, 2025")
    val target = onNodeWithText("Thursday, August 21, 2025")
    seeded.assertIsSelected()
    target.performClick()

    target.assertIsSelected()
    seeded.assertIsNotSelected()
  }

  @Test
  fun clearingADateClearsTheSelection() = runComposeUiTest {
    setContent { DatePickerModalSticker() }

    val seeded = onNodeWithText("Sunday, August 17, 2025")
    seeded.assertIsSelected()
    onNodeWithText("Clear").performClick()

    seeded.assertIsNotSelected()
  }

  @Test
  fun switchingTimeEntryModeSwapsTheDialForTheKeyboard() = runComposeUiTest {
    setContent { TimePickerSticker() }

    onNodeWithText("Select time").assertIsDisplayed()

    // The icon button's description names the mode it switches *to*, so it is the dial's
    // "Enter time" before the click and the keyboard form's "Select time" after.
    onNodeWithContentDescription("Enter time").performClick()

    onNodeWithText("Enter time").assertIsDisplayed()
    onNodeWithContentDescription("Select time").assertIsDisplayed()
  }
}
