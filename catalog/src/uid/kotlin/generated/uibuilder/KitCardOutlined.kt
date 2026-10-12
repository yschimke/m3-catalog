package generated.uibuilder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.action_more_options
import ee.schimke.m3catalog.uid.ImagePlaceholder
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun KitCardOutlined(modifier: androidx.compose.ui.Modifier, header: kotlin.String, subhead: kotlin.String, title: kotlin.String, subtitle: kotlin.String, supportingText: kotlin.String, dismissAction: kotlin.String, action: kotlin.String, onMoreClick: () -> kotlin.Unit, onCancelClick: () -> kotlin.Unit, onActionClick: () -> kotlin.Unit) {
    Box(modifier = modifier) {
        OutlinedCard(modifier = Modifier.size(width = 360.dp, height = 480.dp)) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column {
                    Row(modifier = Modifier.fillMaxWidth().height(72.dp).padding(start = 16.dp, end = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(modifier = Modifier.size(40.dp), shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(text = "A", style = MaterialTheme.typography.titleMedium)
                            }
                        }
                        Column(modifier = Modifier.padding(start = 16.dp).weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = header, style = MaterialTheme.typography.titleMedium)
                            Text(text = subhead, style = MaterialTheme.typography.bodyMedium)
                        }
                        IconButton(onClick = onMoreClick) {
                            Icon(imageVector = Icons.Filled.MoreVert, contentDescription = stringResource(Res.string.action_more_options))
                        }
                    }
                    ImagePlaceholder(modifier = Modifier.fillMaxWidth().height(188.dp), cornerRadiusDp = 0.0f, scaleBasis = 360.0f)
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(32.dp)) {
                        Column {
                            Text(text = title, style = MaterialTheme.typography.bodyLarge)
                            Text(text = subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                        }
                        Text(text = supportingText, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End), verticalAlignment = Alignment.CenterVertically) {
                            OutlinedButton(onClick = onCancelClick) {
                                Text(text = dismissAction, style = MaterialTheme.typography.labelLarge)
                            }
                            Button(onClick = onActionClick) {
                                Text(text = action, style = MaterialTheme.typography.labelLarge)
                            }
                        }
                    }
                }
            }
        }
    }
}
