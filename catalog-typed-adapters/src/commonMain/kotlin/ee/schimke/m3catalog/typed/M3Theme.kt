package ee.schimke.m3catalog.typed

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Composable
internal fun materialColor(value: String, fallback: Color = Color.Unspecified): Color =
  when {
    value.startsWith("#") -> Color(parseArgb(value))
    value == "background" -> MaterialTheme.colorScheme.background
    value == "surface" -> MaterialTheme.colorScheme.surface
    value == "surfaceContainer" -> MaterialTheme.colorScheme.surfaceContainer
    value == "surfaceContainerLow" -> MaterialTheme.colorScheme.surfaceContainerLow
    value == "surfaceContainerHigh" -> MaterialTheme.colorScheme.surfaceContainerHigh
    value == "surfaceContainerHighest" -> MaterialTheme.colorScheme.surfaceContainerHighest
    value == "primary" -> MaterialTheme.colorScheme.primary
    value == "onPrimary" -> MaterialTheme.colorScheme.onPrimary
    value == "tertiary" -> MaterialTheme.colorScheme.tertiary
    value == "onTertiary" -> MaterialTheme.colorScheme.onTertiary
    value == "onSurface" -> MaterialTheme.colorScheme.onSurface
    value == "onSurfaceVariant" -> MaterialTheme.colorScheme.onSurfaceVariant
    value == "outlineVariant" -> MaterialTheme.colorScheme.outlineVariant
    value == "transparent" -> Color.Transparent
    else -> fallback
  }

@Composable
internal fun materialShape(value: String?): Shape =
  when (value) {
    "large" -> MaterialTheme.shapes.large
    "medium" -> MaterialTheme.shapes.medium
    "small" -> MaterialTheme.shapes.small
    else -> RoundedCornerShape(value?.toFloatOrNull()?.dp ?: 0.dp)
  }

private fun parseArgb(value: String): ULong {
  val hex = value.removePrefix("#")
  return when (hex.length) {
    6 -> ("FF$hex").toULong(16)
    8 -> hex.toULong(16)
    else -> 0u
  }
}
