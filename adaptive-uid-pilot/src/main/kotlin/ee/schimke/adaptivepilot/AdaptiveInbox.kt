@file:OptIn(androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi::class)

package ee.schimke.adaptivepilot

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.Posture
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.rememberPaneExpansionState
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import kotlinx.coroutines.launch

// This app-style specimen has no Figma node. Its independent design is inbox.uid; it does not
// enter the Material kit inventory and never changes that catalog's Figma-led parity policy.
@Composable
fun AdaptiveInbox(dark: Boolean = false, initialDetail: Boolean = false) {
  MaterialTheme(colorScheme = if (dark) darkColorScheme() else lightColorScheme()) {
    Scaffold { padding ->
      BoxWithConstraints(Modifier.fillMaxSize().padding(padding)) {
        // This bar-free capture's content box equals its window. Apps with bars or rails should
        // derive the size class from the window rather than copy this content-box calculation.
        val info =
          WindowAdaptiveInfo(WindowSizeClass.compute(maxWidth.value, maxHeight.value), Posture())
        val navigator =
          rememberListDetailPaneScaffoldNavigator<Int>(
            scaffoldDirective = calculatePaneScaffoldDirective(info),
            initialDestinationHistory =
              listOf(
                androidx.compose.material3.adaptive.layout.ThreePaneScaffoldDestinationItem(
                  if (initialDetail) ListDetailPaneScaffoldRole.Detail
                  else ListDetailPaneScaffoldRole.List,
                  0,
                )
              ),
          )
        val selected = navigator.currentDestination?.contentKey ?: 0
        val scope = rememberCoroutineScope()
        val expansion = rememberPaneExpansionState()
        val density = LocalDensity.current
        LaunchedEffect(density) {
          expansion.setFirstPaneWidth(with(density) { 360.dp.roundToPx() })
        }
        ListDetailPaneScaffold(
          directive = navigator.scaffoldDirective,
          value = navigator.scaffoldValue,
          paneExpansionState = expansion,
          listPane = {
            AnimatedPane {
              Column(Modifier.fillMaxSize().padding(24.dp).testTag("inbox-list")) {
                Text("Inbox", style = MaterialTheme.typography.headlineMedium)
                listOf("Release planning", "Build status").forEachIndexed { index, title ->
                  Button(
                    onClick = {
                      scope.launch {
                        navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, index)
                      }
                    },
                    modifier = Modifier.testTag("message-$index"),
                  ) {
                    Text(title)
                  }
                }
              }
            }
          },
          detailPane = {
            AnimatedPane {
              Column(Modifier.fillMaxSize().padding(24.dp).testTag("inbox-detail")) {
                Text(
                  if (selected == 0) "Release planning" else "Build status",
                  style = MaterialTheme.typography.titleLarge,
                )
                Text(
                  "Review the adaptive screen on phone and tablet before the release.",
                  style = MaterialTheme.typography.bodyLarge,
                )
                Button(
                  onClick = {
                    scope.launch { navigator.navigateTo(ListDetailPaneScaffoldRole.List, selected) }
                  },
                  modifier = Modifier.testTag("back-to-inbox"),
                ) {
                  Text("Back to inbox")
                }
              }
            }
          },
        )
      }
    }
  }
}

@Preview(name = "Phone list", widthDp = 412, heightDp = 720)
@Preview(name = "Medium list", widthDp = 700, heightDp = 720)
@Preview(name = "Tablet list", widthDp = 960, heightDp = 720)
@Composable
fun InboxListPreview() = AdaptiveInbox()

@Preview(name = "Phone detail", widthDp = 412, heightDp = 720)
@Preview(name = "Medium detail", widthDp = 700, heightDp = 720)
@Preview(name = "Tablet detail", widthDp = 960, heightDp = 720)
@Composable
fun InboxDetailPreview() = AdaptiveInbox(initialDetail = true)
