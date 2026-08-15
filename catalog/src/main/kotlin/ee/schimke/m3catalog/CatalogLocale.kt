package ee.schimke.m3catalog

import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * Shapes ASCII digits in fixed, language-neutral sample data for [locale]'s numbering system.
 *
 * Catalog samples such as a clock value or badge count are not translated copy, but rendering their
 * ASCII digits unchanged makes the locale axis incomplete. Keeping the punctuation intact also
 * preserves the design kit's authored sample while honoring locales such as Arabic and explicit
 * Unicode numbering-system extensions.
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
