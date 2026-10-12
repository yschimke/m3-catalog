@file:CatalogGroup(name = "Divider", section = "Containment")

package ee.schimke.m3catalog.sections

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.composeai.overrides.previewOverrideDp
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.catalogText
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.divider_above
import ee.schimke.m3catalog.generated.resources.divider_below
import ee.schimke.m3catalog.generated.resources.divider_left
import ee.schimke.m3catalog.generated.resources.divider_right
import ee.schimke.m3catalog.generated.resources.divider_section
import generated.uibuilder.KitHorizontalDivider
import generated.uibuilder.KitHorizontalDividerContext
import generated.uibuilder.KitHorizontalDividerInsetContext
import generated.uibuilder.KitHorizontalDividerInsetContextSubhead
import generated.uibuilder.KitHorizontalDividerInsetPlain
import generated.uibuilder.KitHorizontalDividerInsetSubhead
import generated.uibuilder.KitHorizontalDividerPlain
import generated.uibuilder.KitHorizontalDividerSubhead
import generated.uibuilder.KitVerticalDivider
import generated.uibuilder.KitVerticalDividerPlain
import org.jetbrains.compose.resources.stringResource

// A divider is a static separator: no state, nothing to click, so it ships no handler.
//
// The kit's default instances are the divider itself with no surrounding sample copy. Inset folds
// onto the horizontal form.

// --- Generated sticker bodies, from uid-stickers/Dividers.kt ---

@CatalogComponent(
  id = "Divider/Horizontal",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/51816:5860",
  caption = "Separates content in a vertical list. Inset and subhead fold in.",
)
@CatalogModes
@OverrideVariant(name = "inset", floats = ["inset=16"])
@OverrideVariant(name = "context", booleans = ["context=true"])
@OverrideVariant(name = "subhead", booleans = ["subhead=true"])
@Composable
fun HorizontalDividerSticker(context: Boolean = false, subhead: Boolean = false) = Sticker {
  val inset = previewOverrideDp("inset", 0.dp) > 0.dp
  val before =
    if (context) catalogText("beforeText", stringResource(Res.string.divider_above)) else ""
  val sub = if (subhead) catalogText("subhead", stringResource(Res.string.divider_section)) else ""
  val after =
    if (context) catalogText("afterText", stringResource(Res.string.divider_below)) else ""
  val m = Modifier
  when {
    inset && context && subhead ->
      KitHorizontalDividerInsetContextSubhead(
        m,
        beforeText = before,
        subhead = sub,
        afterText = after,
      )
    inset && context -> KitHorizontalDividerInsetContext(m, beforeText = before, afterText = after)
    inset && subhead -> KitHorizontalDividerInsetSubhead(m, subhead = sub)
    inset -> KitHorizontalDividerInsetPlain(m)
    context && subhead ->
      KitHorizontalDivider(m, beforeText = before, subhead = sub, afterText = after)
    context -> KitHorizontalDividerContext(m, beforeText = before, afterText = after)
    subhead -> KitHorizontalDividerSubhead(m, subhead = sub)
    else -> KitHorizontalDividerPlain(m)
  }
}

@CatalogComponent(
  id = "Divider/Vertical",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/51816:5862",
  caption = "Separates content in a horizontal row.",
)
@CatalogModes
@OverrideVariant(name = "context", booleans = ["context=true"])
@Composable
fun VerticalDividerSticker(context: Boolean = false) = Sticker {
  if (context) {
    KitVerticalDivider(
      Modifier,
      beforeText = catalogText("beforeText", stringResource(Res.string.divider_left)),
      afterText = catalogText("afterText", stringResource(Res.string.divider_right)),
    )
  } else {
    KitVerticalDividerPlain(Modifier)
  }
}
