package ee.schimke.m3catalog

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import com.materialkolor.Contrast
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamicColorScheme

/**
 * The kit's four **contrast** modes, generated from the baseline seed.
 *
 * The Material 3 Design Kit's variable collection carries six colour modes: light and dark, each at
 * standard, medium and high contrast. Compose ships only the standard pair — there is no
 * `lightMediumContrastColorScheme()`. The other four can't responsibly be hand-written either,
 * because a contrast level is not a lookup table: it shifts every role along its tonal palette by a
 * continuous function of the requested contrast, and a re-typed approximation would be a sheet
 * claiming to document M3 accessibility while showing something else.
 *
 * So they come from the generator. [dynamicColorScheme] is MaterialKolor's port of Google's
 * `material-color-utilities` — the same algorithm behind the Material Theme Builder plugin that
 * produced the kit's variables — driven by [BASELINE_SEED] in the [PaletteStyle.TonalSpot] style
 * the M3 baseline uses.
 *
 * ### Known, deliberate caveat: generated ≠ the published baseline
 *
 * Running the generator at **zero** contrast does *not* exactly reproduce Compose's
 * `lightColorScheme()`. Measured on this pin (MaterialKolor 5.0.0):
 *
 * | Role               | Generated | Published baseline |
 * |--------------------|-----------|--------------------|
 * | `primary`          | `#65558F` | `#6750A4`          |
 * | `onSurfaceVariant` | `#49454E` | `#49454F`          |
 * | `outlineVariant`   | `#CAC4CF` | `#CAC4D0`          |
 * | `error`            | `#BA1A1A` | `#B3261E`          |
 *
 * Most roles land within one or two units per channel; `error` differs outright because M3's error
 * family is hand-authored rather than derived from the primary seed. The published baseline is a
 * *tuned* artefact, not the raw TonalSpot output of its own seed.
 *
 * The catalog therefore keeps both, rather than pretending one is the other:
 * * **Standard light/dark** stay [BaselineLight] / [BaselineDark], Compose's stock schemes — which
 *   match the kit's `M3.sys.light.*` variables exactly, and are pinned to those hexes by a test.
 * * **The four contrast tiers** are generated here, and are the closest faithful rendering of a
 *   mode Compose has no primitive for. A tier's job is the contrast *relationship*, and that is
 *   what the tests assert: each tier separates `onSurface` from `surface` strictly more than the
 *   one below it. `CatalogInventoryTest` additionally pins the generator's zero-contrast output to
 *   the values above, so a MaterialKolor upgrade that moves the algorithm fails the build instead
 *   of silently re-tinting four published themes.
 */

/**
 * The Material 3 baseline seed — `M3.sys.light.primary`, `#6750A4`, read from the kit's own
 * published variables.
 */
val BASELINE_SEED = Color(0xFF6750A4)

/** Builds one of the kit's modes from the baseline seed at the requested [contrast]. */
fun baselineScheme(dark: Boolean, contrast: Contrast): ColorScheme =
  dynamicColorScheme(
    seedColor = BASELINE_SEED,
    isDark = dark,
    isAmoled = false,
    style = PaletteStyle.TonalSpot,
    contrastLevel = contrast.value,
  )

/** Light, medium contrast — the kit's `Light Medium Contrast` mode. */
val BaselineLightMediumContrast: ColorScheme = baselineScheme(dark = false, Contrast.Medium)

/** Light, high contrast — the kit's `Light High Contrast` mode. */
val BaselineLightHighContrast: ColorScheme = baselineScheme(dark = false, Contrast.High)

/** Dark, medium contrast — the kit's `Dark Medium Contrast` mode. */
val BaselineDarkMediumContrast: ColorScheme = baselineScheme(dark = true, Contrast.Medium)

/** Dark, high contrast — the kit's `Dark High Contrast` mode. */
val BaselineDarkHighContrast: ColorScheme = baselineScheme(dark = true, Contrast.High)
