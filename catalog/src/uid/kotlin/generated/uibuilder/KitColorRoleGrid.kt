package generated.uibuilder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
internal fun KitColorRoleGrid(modifier: androidx.compose.ui.Modifier) {
    Box(modifier = modifier) {
        Column(modifier = Modifier.width(808.dp).background(color = MaterialTheme.colorScheme.surface)) {
            Row(modifier = Modifier.fillMaxWidth().height(186.dp)) {
                Row(modifier = Modifier.width(596.dp).fillMaxHeight()) {
                    Column(modifier = Modifier.width(196.dp).fillMaxHeight()) {
                        Text(text = "primary", modifier = Modifier.fillMaxWidth().height(51.dp).background(color = MaterialTheme.colorScheme.primary).padding(start = 12.dp, top = 10.dp, end = 12.dp), color = MaterialTheme.colorScheme.onPrimary, overflow = TextOverflow.Ellipsis, maxLines = 2, style = MaterialTheme.typography.labelSmall)
                        Text(text = "onPrimary", modifier = Modifier.fillMaxWidth().height(40.dp).background(color = MaterialTheme.colorScheme.onPrimary).padding(start = 12.dp, top = 10.dp, end = 12.dp), color = MaterialTheme.colorScheme.primary, overflow = TextOverflow.Ellipsis, maxLines = 2, style = MaterialTheme.typography.labelSmall)
                        Box(modifier = Modifier.height(4.dp), content = {})
                        Text(text = "primaryContainer", modifier = Modifier.fillMaxWidth().height(51.dp).background(color = MaterialTheme.colorScheme.primaryContainer).padding(start = 12.dp, top = 10.dp, end = 12.dp), color = MaterialTheme.colorScheme.onPrimaryContainer, overflow = TextOverflow.Ellipsis, maxLines = 2, style = MaterialTheme.typography.labelSmall)
                        Text(text = "onPrimaryContainer", modifier = Modifier.fillMaxWidth().height(40.dp).background(color = MaterialTheme.colorScheme.onPrimaryContainer).padding(start = 12.dp, top = 10.dp, end = 12.dp), color = MaterialTheme.colorScheme.primaryContainer, overflow = TextOverflow.Ellipsis, maxLines = 2, style = MaterialTheme.typography.labelSmall)
                    }
                    Box(modifier = Modifier.width(4.dp), content = {})
                    Column(modifier = Modifier.width(196.dp).fillMaxHeight()) {
                        Text(text = "secondary", modifier = Modifier.fillMaxWidth().height(51.dp).background(color = MaterialTheme.colorScheme.secondary).padding(start = 12.dp, top = 10.dp, end = 12.dp), color = MaterialTheme.colorScheme.onSecondary, overflow = TextOverflow.Ellipsis, maxLines = 2, style = MaterialTheme.typography.labelSmall)
                        Text(text = "onSecondary", modifier = Modifier.fillMaxWidth().height(40.dp).background(color = MaterialTheme.colorScheme.onSecondary).padding(start = 12.dp, top = 10.dp, end = 12.dp), color = MaterialTheme.colorScheme.secondary, overflow = TextOverflow.Ellipsis, maxLines = 2, style = MaterialTheme.typography.labelSmall)
                        Box(modifier = Modifier.height(4.dp), content = {})
                        Text(text = "secondaryContainer", modifier = Modifier.fillMaxWidth().height(51.dp).background(color = MaterialTheme.colorScheme.secondaryContainer).padding(start = 12.dp, top = 10.dp, end = 12.dp), color = MaterialTheme.colorScheme.onSecondaryContainer, overflow = TextOverflow.Ellipsis, maxLines = 2, style = MaterialTheme.typography.labelSmall)
                        Text(text = "onSecondaryContainer", modifier = Modifier.fillMaxWidth().height(40.dp).background(color = MaterialTheme.colorScheme.onSecondaryContainer).padding(start = 12.dp, top = 10.dp, end = 12.dp), color = MaterialTheme.colorScheme.secondaryContainer, overflow = TextOverflow.Ellipsis, maxLines = 2, style = MaterialTheme.typography.labelSmall)
                    }
                    Box(modifier = Modifier.width(4.dp), content = {})
                    Column(modifier = Modifier.width(196.dp).fillMaxHeight()) {
                        Text(text = "tertiary", modifier = Modifier.fillMaxWidth().height(51.dp).background(color = MaterialTheme.colorScheme.tertiary).padding(start = 12.dp, top = 10.dp, end = 12.dp), color = MaterialTheme.colorScheme.onTertiary, overflow = TextOverflow.Ellipsis, maxLines = 2, style = MaterialTheme.typography.labelSmall)
                        Text(text = "onTertiary", modifier = Modifier.fillMaxWidth().height(40.dp).background(color = MaterialTheme.colorScheme.onTertiary).padding(start = 12.dp, top = 10.dp, end = 12.dp), color = MaterialTheme.colorScheme.tertiary, overflow = TextOverflow.Ellipsis, maxLines = 2, style = MaterialTheme.typography.labelSmall)
                        Box(modifier = Modifier.height(4.dp), content = {})
                        Text(text = "tertiaryContainer", modifier = Modifier.fillMaxWidth().height(51.dp).background(color = MaterialTheme.colorScheme.tertiaryContainer).padding(start = 12.dp, top = 10.dp, end = 12.dp), color = MaterialTheme.colorScheme.onTertiaryContainer, overflow = TextOverflow.Ellipsis, maxLines = 2, style = MaterialTheme.typography.labelSmall)
                        Text(text = "onTertiaryContainer", modifier = Modifier.fillMaxWidth().height(40.dp).background(color = MaterialTheme.colorScheme.onTertiaryContainer).padding(start = 12.dp, top = 10.dp, end = 12.dp), color = MaterialTheme.colorScheme.tertiaryContainer, overflow = TextOverflow.Ellipsis, maxLines = 2, style = MaterialTheme.typography.labelSmall)
                    }
                }
                Box(modifier = Modifier.width(16.dp), content = {})
                Column(modifier = Modifier.width(196.dp).fillMaxHeight()) {
                    Text(text = "error", modifier = Modifier.fillMaxWidth().height(51.dp).background(color = MaterialTheme.colorScheme.error).padding(start = 12.dp, top = 10.dp, end = 12.dp), color = MaterialTheme.colorScheme.onError, overflow = TextOverflow.Ellipsis, maxLines = 2, style = MaterialTheme.typography.labelSmall)
                    Text(text = "onError", modifier = Modifier.fillMaxWidth().height(40.dp).background(color = MaterialTheme.colorScheme.onError).padding(start = 12.dp, top = 10.dp, end = 12.dp), color = MaterialTheme.colorScheme.error, overflow = TextOverflow.Ellipsis, maxLines = 2, style = MaterialTheme.typography.labelSmall)
                    Box(modifier = Modifier.height(4.dp), content = {})
                    Text(text = "errorContainer", modifier = Modifier.fillMaxWidth().height(51.dp).background(color = MaterialTheme.colorScheme.errorContainer).padding(start = 12.dp, top = 10.dp, end = 12.dp), color = MaterialTheme.colorScheme.onErrorContainer, overflow = TextOverflow.Ellipsis, maxLines = 2, style = MaterialTheme.typography.labelSmall)
                    Text(text = "onErrorContainer", modifier = Modifier.fillMaxWidth().height(40.dp).background(color = MaterialTheme.colorScheme.onErrorContainer).padding(start = 12.dp, top = 10.dp, end = 12.dp), color = MaterialTheme.colorScheme.errorContainer, overflow = TextOverflow.Ellipsis, maxLines = 2, style = MaterialTheme.typography.labelSmall)
                }
            }
            Box(modifier = Modifier.height(16.dp), content = {})
            Row(modifier = Modifier.width(596.dp).height(128.dp)) {
                Column(modifier = Modifier.width(196.dp).fillMaxHeight()) {
                    Row(modifier = Modifier.fillMaxWidth().height(56.dp)) {
                        Text(text = "primaryFixed", modifier = Modifier.weight(1f).fillMaxHeight().background(color = MaterialTheme.colorScheme.primaryFixed).padding(start = 12.dp, top = 10.dp, end = 12.dp), color = MaterialTheme.colorScheme.onPrimaryFixed, overflow = TextOverflow.Ellipsis, maxLines = 2, style = MaterialTheme.typography.labelSmall)
                        Text(text = "primaryFixedDim", modifier = Modifier.weight(1f).fillMaxHeight().background(color = MaterialTheme.colorScheme.primaryFixedDim).padding(start = 12.dp, top = 10.dp, end = 12.dp), color = MaterialTheme.colorScheme.onPrimaryFixedVariant, overflow = TextOverflow.Ellipsis, maxLines = 2, style = MaterialTheme.typography.labelSmall)
                    }
                    Text(text = "onPrimaryFixed", modifier = Modifier.fillMaxWidth().height(36.dp).background(color = MaterialTheme.colorScheme.onPrimaryFixed).padding(start = 12.dp, top = 10.dp, end = 12.dp), color = MaterialTheme.colorScheme.primaryFixed, overflow = TextOverflow.Ellipsis, maxLines = 2, style = MaterialTheme.typography.labelSmall)
                    Text(text = "onPrimaryFixedVariant", modifier = Modifier.fillMaxWidth().height(36.dp).background(color = MaterialTheme.colorScheme.onPrimaryFixedVariant).padding(start = 12.dp, top = 10.dp, end = 12.dp), color = MaterialTheme.colorScheme.primaryFixed, overflow = TextOverflow.Ellipsis, maxLines = 2, style = MaterialTheme.typography.labelSmall)
                }
                Box(modifier = Modifier.width(4.dp), content = {})
                Column(modifier = Modifier.width(196.dp).fillMaxHeight()) {
                    Row(modifier = Modifier.fillMaxWidth().height(56.dp)) {
                        Text(text = "secondaryFixed", modifier = Modifier.weight(1f).fillMaxHeight().background(color = MaterialTheme.colorScheme.secondaryFixed).padding(start = 12.dp, top = 10.dp, end = 12.dp), color = MaterialTheme.colorScheme.onSecondaryFixed, overflow = TextOverflow.Ellipsis, maxLines = 2, style = MaterialTheme.typography.labelSmall)
                        Text(text = "secondaryFixedDim", modifier = Modifier.weight(1f).fillMaxHeight().background(color = MaterialTheme.colorScheme.secondaryFixedDim).padding(start = 12.dp, top = 10.dp, end = 12.dp), color = MaterialTheme.colorScheme.onSecondaryFixedVariant, overflow = TextOverflow.Ellipsis, maxLines = 2, style = MaterialTheme.typography.labelSmall)
                    }
                    Text(text = "onSecondaryFixed", modifier = Modifier.fillMaxWidth().height(36.dp).background(color = MaterialTheme.colorScheme.onSecondaryFixed).padding(start = 12.dp, top = 10.dp, end = 12.dp), color = MaterialTheme.colorScheme.secondaryFixed, overflow = TextOverflow.Ellipsis, maxLines = 2, style = MaterialTheme.typography.labelSmall)
                    Text(text = "onSecondaryFixedVariant", modifier = Modifier.fillMaxWidth().height(36.dp).background(color = MaterialTheme.colorScheme.onSecondaryFixedVariant).padding(start = 12.dp, top = 10.dp, end = 12.dp), color = MaterialTheme.colorScheme.secondaryFixed, overflow = TextOverflow.Ellipsis, maxLines = 2, style = MaterialTheme.typography.labelSmall)
                }
                Box(modifier = Modifier.width(4.dp), content = {})
                Column(modifier = Modifier.width(196.dp).fillMaxHeight()) {
                    Row(modifier = Modifier.fillMaxWidth().height(56.dp)) {
                        Text(text = "tertiaryFixed", modifier = Modifier.weight(1f).fillMaxHeight().background(color = MaterialTheme.colorScheme.tertiaryFixed).padding(start = 12.dp, top = 10.dp, end = 12.dp), color = MaterialTheme.colorScheme.onTertiaryFixed, overflow = TextOverflow.Ellipsis, maxLines = 2, style = MaterialTheme.typography.labelSmall)
                        Text(text = "tertiaryFixedDim", modifier = Modifier.weight(1f).fillMaxHeight().background(color = MaterialTheme.colorScheme.tertiaryFixedDim).padding(start = 12.dp, top = 10.dp, end = 12.dp), color = MaterialTheme.colorScheme.onTertiaryFixedVariant, overflow = TextOverflow.Ellipsis, maxLines = 2, style = MaterialTheme.typography.labelSmall)
                    }
                    Text(text = "onTertiaryFixed", modifier = Modifier.fillMaxWidth().height(36.dp).background(color = MaterialTheme.colorScheme.onTertiaryFixed).padding(start = 12.dp, top = 10.dp, end = 12.dp), color = MaterialTheme.colorScheme.tertiaryFixed, overflow = TextOverflow.Ellipsis, maxLines = 2, style = MaterialTheme.typography.labelSmall)
                    Text(text = "onTertiaryFixedVariant", modifier = Modifier.fillMaxWidth().height(36.dp).background(color = MaterialTheme.colorScheme.onTertiaryFixedVariant).padding(start = 12.dp, top = 10.dp, end = 12.dp), color = MaterialTheme.colorScheme.tertiaryFixed, overflow = TextOverflow.Ellipsis, maxLines = 2, style = MaterialTheme.typography.labelSmall)
                }
            }
            Box(modifier = Modifier.height(16.dp), content = {})
            Row(modifier = Modifier.fillMaxWidth().height(186.dp)) {
                Column(modifier = Modifier.width(596.dp).fillMaxHeight()) {
                    Row(modifier = Modifier.fillMaxWidth().height(69.dp)) {
                        Text(text = "surfaceDim", modifier = Modifier.weight(1f).fillMaxHeight().background(color = MaterialTheme.colorScheme.surfaceDim).padding(start = 12.dp, top = 10.dp, end = 12.dp), color = MaterialTheme.colorScheme.onSurface, overflow = TextOverflow.Ellipsis, maxLines = 2, style = MaterialTheme.typography.labelSmall)
                        Text(text = "surface", modifier = Modifier.weight(1f).fillMaxHeight().background(color = MaterialTheme.colorScheme.surface).padding(start = 12.dp, top = 10.dp, end = 12.dp), color = MaterialTheme.colorScheme.onSurface, overflow = TextOverflow.Ellipsis, maxLines = 2, style = MaterialTheme.typography.labelSmall)
                        Text(text = "surfaceBright", modifier = Modifier.weight(1f).fillMaxHeight().background(color = MaterialTheme.colorScheme.surfaceBright).padding(start = 12.dp, top = 10.dp, end = 12.dp), color = MaterialTheme.colorScheme.onSurface, overflow = TextOverflow.Ellipsis, maxLines = 2, style = MaterialTheme.typography.labelSmall)
                    }
                    Box(modifier = Modifier.height(4.dp), content = {})
                    Row(modifier = Modifier.fillMaxWidth().height(69.dp)) {
                        Text(text = "surfaceContainerLowest", modifier = Modifier.weight(1f).fillMaxHeight().background(color = MaterialTheme.colorScheme.surfaceContainerLowest).padding(start = 12.dp, top = 10.dp, end = 12.dp), color = MaterialTheme.colorScheme.onSurface, overflow = TextOverflow.Ellipsis, maxLines = 2, style = MaterialTheme.typography.labelSmall)
                        Text(text = "surfaceContainerLow", modifier = Modifier.weight(1f).fillMaxHeight().background(color = MaterialTheme.colorScheme.surfaceContainerLow).padding(start = 12.dp, top = 10.dp, end = 12.dp), color = MaterialTheme.colorScheme.onSurface, overflow = TextOverflow.Ellipsis, maxLines = 2, style = MaterialTheme.typography.labelSmall)
                        Text(text = "surfaceContainer", modifier = Modifier.weight(1f).fillMaxHeight().background(color = MaterialTheme.colorScheme.surfaceContainer).padding(start = 12.dp, top = 10.dp, end = 12.dp), color = MaterialTheme.colorScheme.onSurface, overflow = TextOverflow.Ellipsis, maxLines = 2, style = MaterialTheme.typography.labelSmall)
                        Text(text = "surfaceContainerHigh", modifier = Modifier.weight(1f).fillMaxHeight().background(color = MaterialTheme.colorScheme.surfaceContainerHigh).padding(start = 12.dp, top = 10.dp, end = 12.dp), color = MaterialTheme.colorScheme.onSurface, overflow = TextOverflow.Ellipsis, maxLines = 2, style = MaterialTheme.typography.labelSmall)
                        Text(text = "surfaceContainerHighest", modifier = Modifier.weight(1f).fillMaxHeight().background(color = MaterialTheme.colorScheme.surfaceContainerHighest).padding(start = 12.dp, top = 10.dp, end = 12.dp), color = MaterialTheme.colorScheme.onSurface, overflow = TextOverflow.Ellipsis, maxLines = 2, style = MaterialTheme.typography.labelSmall)
                    }
                    Box(modifier = Modifier.height(4.dp), content = {})
                    Row(modifier = Modifier.fillMaxWidth().height(40.dp)) {
                        Text(text = "onSurface", modifier = Modifier.weight(1f).fillMaxHeight().background(color = MaterialTheme.colorScheme.onSurface).padding(start = 12.dp, top = 10.dp, end = 12.dp), color = MaterialTheme.colorScheme.surface, overflow = TextOverflow.Ellipsis, maxLines = 2, style = MaterialTheme.typography.labelSmall)
                        Text(text = "onSurfaceVariant", modifier = Modifier.weight(1f).fillMaxHeight().background(color = MaterialTheme.colorScheme.onSurfaceVariant).padding(start = 12.dp, top = 10.dp, end = 12.dp), color = MaterialTheme.colorScheme.surface, overflow = TextOverflow.Ellipsis, maxLines = 2, style = MaterialTheme.typography.labelSmall)
                        Text(text = "outline", modifier = Modifier.weight(1f).fillMaxHeight().background(color = MaterialTheme.colorScheme.outline).padding(start = 12.dp, top = 10.dp, end = 12.dp), color = MaterialTheme.colorScheme.surface, overflow = TextOverflow.Ellipsis, maxLines = 2, style = MaterialTheme.typography.labelSmall)
                        Text(text = "outlineVariant", modifier = Modifier.weight(1f).fillMaxHeight().background(color = MaterialTheme.colorScheme.outlineVariant).padding(start = 12.dp, top = 10.dp, end = 12.dp), color = MaterialTheme.colorScheme.onSurface, overflow = TextOverflow.Ellipsis, maxLines = 2, style = MaterialTheme.typography.labelSmall)
                    }
                }
                Box(modifier = Modifier.width(16.dp), content = {})
                Column(modifier = Modifier.width(196.dp).fillMaxHeight()) {
                    Text(text = "inverseSurface", modifier = Modifier.fillMaxWidth().height(48.dp).background(color = MaterialTheme.colorScheme.inverseSurface).padding(start = 12.dp, top = 10.dp, end = 12.dp), color = MaterialTheme.colorScheme.inverseOnSurface, overflow = TextOverflow.Ellipsis, maxLines = 2, style = MaterialTheme.typography.labelSmall)
                    Text(text = "inverseOnSurface", modifier = Modifier.fillMaxWidth().height(38.dp).background(color = MaterialTheme.colorScheme.inverseOnSurface).padding(start = 12.dp, top = 10.dp, end = 12.dp), color = MaterialTheme.colorScheme.inverseSurface, overflow = TextOverflow.Ellipsis, maxLines = 2, style = MaterialTheme.typography.labelSmall)
                    Box(modifier = Modifier.height(4.dp), content = {})
                    Text(text = "inversePrimary", modifier = Modifier.fillMaxWidth().height(40.dp).background(color = MaterialTheme.colorScheme.inversePrimary).padding(start = 12.dp, top = 10.dp, end = 12.dp), color = MaterialTheme.colorScheme.inverseSurface, overflow = TextOverflow.Ellipsis, maxLines = 2, style = MaterialTheme.typography.labelSmall)
                    Box(modifier = Modifier.height(16.dp), content = {})
                    Row(modifier = Modifier.fillMaxWidth().height(40.dp)) {
                        Text(text = "scrim", modifier = Modifier.width(90.dp).fillMaxHeight().background(color = MaterialTheme.colorScheme.scrim).padding(start = 12.dp, top = 10.dp, end = 12.dp), color = Color(0xFFFFFFFF), overflow = TextOverflow.Ellipsis, maxLines = 2, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}
