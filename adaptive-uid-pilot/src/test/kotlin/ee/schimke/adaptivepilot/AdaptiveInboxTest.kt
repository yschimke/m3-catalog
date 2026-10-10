package ee.schimke.adaptivepilot

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runDesktopComposeUiTest
import androidx.compose.ui.unit.dp
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class AdaptiveInboxTest {
  @Test
  fun selectionSurvivesResizeAndBackReturnsToList() =
    runDesktopComposeUiTest(width = 1000, height = 800) {
      val width = mutableStateOf(412.dp)
      setContent { Box(Modifier.requiredSize(width.value, 720.dp)) { AdaptiveInbox() } }
      onNodeWithTag("inbox-list").assertIsDisplayed()
      onNodeWithTag("inbox-detail").assertDoesNotExist()
      onNodeWithTag("message-1").performClick()
      onNodeWithTag("inbox-detail").assertIsDisplayed()
      onNodeWithText("Build status").assertIsDisplayed()
      onNodeWithTag("inbox-list").assertDoesNotExist()
      runOnIdle { width.value = 960.dp }
      onNodeWithTag("inbox-list").assertIsDisplayed()
      onNodeWithTag("inbox-detail").assertIsDisplayed()
      runOnIdle { width.value = 412.dp }
      onNodeWithText("Build status").assertIsDisplayed()
      onNodeWithTag("back-to-inbox").performClick()
      onNodeWithTag("inbox-list").assertIsDisplayed()
      onNodeWithTag("inbox-detail").assertDoesNotExist()
    }

  @Test
  fun breakpointOpensBothPanesAt840dp() =
    runDesktopComposeUiTest(width = 1000, height = 800) {
      val width = mutableStateOf(839.dp)
      setContent { Box(Modifier.requiredSize(width.value, 720.dp)) { AdaptiveInbox() } }
      onNodeWithTag("inbox-detail").assertDoesNotExist()
      runOnIdle { width.value = 840.dp }
      onNodeWithTag("inbox-list").assertIsDisplayed()
      onNodeWithTag("inbox-detail").assertIsDisplayed()
    }
}
