@file:OptIn(ExperimentalMaterial3ComponentOverrideApi::class)

package ee.schimke.m3catalog

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.BasicAlertDialogOverride
import androidx.compose.material3.BasicAlertDialogOverrideScope
import androidx.compose.material3.ExperimentalMaterial3ComponentOverrideApi
import androidx.compose.material3.LocalBasicAlertDialogOverride
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

/** Renders Material dialog content in the sticker surface instead of a platform window. */
@Composable
internal fun InlineDialogHost(content: @Composable () -> Unit) {
  CompositionLocalProvider(LocalBasicAlertDialogOverride provides InlineBasicAlertDialogOverride) {
    content()
  }
}

private object InlineBasicAlertDialogOverride : BasicAlertDialogOverride {
  @Composable
  override fun BasicAlertDialogOverrideScope.BasicAlertDialog() {
    Box(modifier = modifier, propagateMinConstraints = true) { content() }
  }
}
