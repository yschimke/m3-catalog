@file:CatalogGroup(name = "Badges", section = "Communication")

package ee.schimke.m3catalog.sections

import androidx.compose.material3.Badge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.m3catalog.CatalogModes
import ee.schimke.m3catalog.Sticker
import ee.schimke.m3catalog.localizedDigits
import generated.uibuilder.KitNumberBadge
import java.text.DecimalFormatSymbols
import java.util.Locale

// A badge is a status carrier, not a control: nothing to respond to, so it ships no handler.
//
// The kit's axis is the LABEL, and digit count is not cosmetic — the container grows from a circle
// at one digit to a pill at three, and overflows to "999+" past its cap. Each width is its own
// cell.
//
// The dot is the same `Badge()` composable with no content, and the kit's `Badges` set carries
// exactly two variants — `Size=Small` (the dot) and `Size=Large` (the count). So it is one
// component with a size axis, not two components.

@CatalogVariant(
  of = "Badge/Number",
  props = ["size=small"],
  caption = "Something changed, without a count — the smallest badge form.",
)
@CatalogModes
@Composable
fun DotBadge() = Sticker { Badge() }

// --- Generated sticker bodies, from uid-stickers/Badges.kt ---

/**
 * The catalog's `localizedDigits`, which `:catalog` keeps internal: ASCII digits in [locale]'s
 * numbering system.
 */
internal fun localizedDigits(value: String, locale: Locale = Locale.getDefault()): String {
  val zero = DecimalFormatSymbols.getInstance(locale).zeroDigit
  if (zero == '0') return value
  return buildString(value.length) {
    value.forEach { char ->
      append(if (char in '0'..'9') (zero.code + (char - '0')).toChar() else char)
    }
  }
}

@CatalogComponent(
  id = "Badge/Number",
  reference = "figma:ocdacdEsnHipMJD3egzxKb/51592:4770",
  caption = "Unread count badge. One to three digits and overflow fold in.",
)
@CatalogModes
@OverrideVariant(name = "digits-2", strings = ["label=42"])
@OverrideVariant(name = "digits-3", strings = ["label=147"])
@OverrideVariant(name = "overflow", strings = ["label=999+"])
@Composable
fun NumberBadge(label: String = "3") = Sticker {
  KitNumberBadge(Modifier, label = localizedDigits(label))
}
