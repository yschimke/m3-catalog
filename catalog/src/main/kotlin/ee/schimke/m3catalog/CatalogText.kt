package ee.schimke.m3catalog

import androidx.compose.runtime.Composable
import ee.schimke.composeai.overrides.previewOverrideString

/**
 * User-visible copy whose published default remains locale-aware and whose value can be edited in
 * the preview server.
 *
 * The default must already have been resolved from a string resource by the caller. That keeps the
 * catalog's translated baked renders unchanged while publishing [key] as a plain text control for
 * the held live preview. Keys describe the component parameter or slot (`label`, `supportingText`,
 * `placeholder`) rather than the resource name, so the controls read like the API being explored.
 */
@Composable
fun catalogText(key: String, default: String, index: Int? = null): String =
  previewOverrideString(key, default, index)
