@file:CatalogGroup(name = "Navigation bar", section = "Navigation")
@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ShortNavigationBar
import androidx.compose.material3.ShortNavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.overrides.previewOverrideString
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.InteractionPreview
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogFilledStars
import ee.schimke.m3catalog.CatalogModes412
import ee.schimke.m3catalog.CatalogOutlinedStars
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogChoice
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.nav_home
import ee.schimke.m3catalog.generated.resources.nav_saved
import ee.schimke.m3catalog.generated.resources.nav_search
import ee.schimke.m3catalog.generated.resources.nav_settings
import ee.schimke.m3catalog.generated.resources.nav_you
import ee.schimke.m3catalog.selectable
import org.jetbrains.compose.resources.stringResource

// The bar owns its selected destination, so a click on the live lane really moves the indicator.
//
// The kit's axes: DESTINATION COUNT (three to five), LABEL VISIBILITY (always / selected only /
// never), and an optional badge.

private val DESTINATIONS =
  listOf(
    Res.string.nav_home,
    Res.string.nav_search,
    Res.string.nav_you,
    Res.string.nav_saved,
    Res.string.nav_settings,
  )

@CatalogComponent(
  id = "NavigationBar/Short",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/58016:37260",
  caption = "The expressive compact bar. Count and labels fold in.",
)
// Three destinations by default, so 2 -> 0 -> 2 is the longest travel this bar offers. The
// distance is the point: the indicator's spring is a function of how far it has to go, so a
// neighbouring hop under-sells exactly what Expressive changed.
@InteractionPreview(
  targets = [2, 0, 2],
  caption =
    "Tap between distant destinations. The selection indicator moves on Expressive's spatial " +
      "spring — watch it overshoot and settle rather than ease linearly into place.",
)
@CatalogModes412
@OverrideVariant(name = "four", strings = ["count=4"])
@OverrideVariant(name = "five", strings = ["count=5"])
@OverrideVariant(name = "labels-none", strings = ["labels=none"])
@Composable
fun ShortNavigationBarSticker() = Sticker {
  val count = previewOverrideString("count", "3").toIntOrNull() ?: 3
  val labels = catalogChoice("labels", "always", "always", "none")
  var selected by selectable(0)
  ShortNavigationBar(Modifier.width(412.dp).height(64.dp)) {
    // Give the broken equal-weight policy one full-width child, then divide that width correctly.
    // Remove after https://github.com/yschimke/m3-catalog/issues/41 is fixed upstream.
    Row(Modifier.fillMaxWidth()) {
      DESTINATIONS.take(count).forEachIndexed { index, label ->
        ShortNavigationBarItem(
          selected = index == selected,
          onClick = { selected = index },
          icon = {
            Icon(
              if (index == selected) CatalogFilledStars else CatalogOutlinedStars,
              contentDescription = null,
            )
          },
          label = if (labels == "none") null else ({ Text(stringResource(label)) }),
          modifier = Modifier.weight(1f),
        )
      }
    }
  }
}
