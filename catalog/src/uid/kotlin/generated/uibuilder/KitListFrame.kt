package generated.uibuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ee.schimke.m3catalog.uid.RailStarsIcon

@Composable
internal fun KitListFrame(modifier: androidx.compose.ui.Modifier, headline0: kotlin.String, supportingText0: kotlin.String, headline1: kotlin.String, supportingText1: kotlin.String, headline2: kotlin.String, supportingText2: kotlin.String, headline3: kotlin.String, supportingText3: kotlin.String, headline4: kotlin.String, supportingText4: kotlin.String, headline5: kotlin.String, supportingText5: kotlin.String) {
    Box(modifier = modifier) {
        Column(modifier = Modifier.width(280.dp)) {
            Box(modifier = Modifier.fillMaxWidth().height(64.dp)) {
                ListItem(headlineContent = {
                    Text(text = headline0, modifier = Modifier.offset(x = (-6).dp, y = 0.dp))
                }, modifier = Modifier.height(64.dp), supportingContent = {
                    Text(text = supportingText0, modifier = Modifier.offset(x = (-6).dp, y = 0.dp), overflow = TextOverflow.Ellipsis, maxLines = 1)
                }, leadingContent = {
                    RailStarsIcon(selected = false, modifier = Modifier.size(20.dp))
                }, trailingContent = {
                    Row(modifier = Modifier.offset(x = (-4).dp, y = 0.dp)) {
                        Text(text = "⌘C")
                        Box(modifier = Modifier.width(12.dp), content = {})
                        Icon(imageVector = Icons.Filled.ArrowRight, contentDescription = null, modifier = Modifier.size(10.dp))
                    }
                }, colors = ListItemDefaults.colors(containerColor = Color(0x00000000)))
            }
            Box(modifier = Modifier.fillMaxWidth().height(64.dp)) {
                ListItem(headlineContent = {
                    Text(text = headline1, modifier = Modifier.offset(x = (-6).dp, y = 0.dp))
                }, modifier = Modifier.height(64.dp), supportingContent = {
                    Text(text = supportingText1, modifier = Modifier.offset(x = (-6).dp, y = 0.dp), overflow = TextOverflow.Ellipsis, maxLines = 1)
                }, leadingContent = {
                    RailStarsIcon(selected = false, modifier = Modifier.size(20.dp))
                }, trailingContent = {
                    Row(modifier = Modifier.offset(x = (-4).dp, y = 0.dp)) {
                        Text(text = "⌘C")
                        Box(modifier = Modifier.width(12.dp), content = {})
                        Icon(imageVector = Icons.Filled.ArrowRight, contentDescription = null, modifier = Modifier.size(10.dp))
                    }
                }, colors = ListItemDefaults.colors(containerColor = Color(0x00000000)))
            }
            Box(modifier = Modifier.fillMaxWidth().height(64.dp)) {
                ListItem(headlineContent = {
                    Text(text = headline2, modifier = Modifier.offset(x = (-6).dp, y = 0.dp))
                }, modifier = Modifier.height(64.dp), supportingContent = {
                    Text(text = supportingText2, modifier = Modifier.offset(x = (-6).dp, y = 0.dp), overflow = TextOverflow.Ellipsis, maxLines = 1)
                }, leadingContent = {
                    RailStarsIcon(selected = false, modifier = Modifier.size(20.dp))
                }, trailingContent = {
                    Row(modifier = Modifier.offset(x = (-4).dp, y = 0.dp)) {
                        Text(text = "⌘C")
                        Box(modifier = Modifier.width(12.dp), content = {})
                        Icon(imageVector = Icons.Filled.ArrowRight, contentDescription = null, modifier = Modifier.size(10.dp))
                    }
                }, colors = ListItemDefaults.colors(containerColor = Color(0x00000000)))
            }
            Box(modifier = Modifier.fillMaxWidth().height(64.dp)) {
                ListItem(headlineContent = {
                    Text(text = headline3, modifier = Modifier.offset(x = (-6).dp, y = 0.dp))
                }, modifier = Modifier.height(64.dp), supportingContent = {
                    Text(text = supportingText3, modifier = Modifier.offset(x = (-6).dp, y = 0.dp), overflow = TextOverflow.Ellipsis, maxLines = 1)
                }, leadingContent = {
                    RailStarsIcon(selected = false, modifier = Modifier.size(20.dp))
                }, trailingContent = {
                    Row(modifier = Modifier.offset(x = (-4).dp, y = 0.dp)) {
                        Text(text = "⌘C")
                        Box(modifier = Modifier.width(12.dp), content = {})
                        Icon(imageVector = Icons.Filled.ArrowRight, contentDescription = null, modifier = Modifier.size(10.dp))
                    }
                }, colors = ListItemDefaults.colors(containerColor = Color(0x00000000)))
            }
            Box(modifier = Modifier.fillMaxWidth().height(64.dp)) {
                ListItem(headlineContent = {
                    Text(text = headline4, modifier = Modifier.offset(x = (-6).dp, y = 0.dp))
                }, modifier = Modifier.height(64.dp), supportingContent = {
                    Text(text = supportingText4, modifier = Modifier.offset(x = (-6).dp, y = 0.dp), overflow = TextOverflow.Ellipsis, maxLines = 1)
                }, leadingContent = {
                    RailStarsIcon(selected = false, modifier = Modifier.size(20.dp))
                }, trailingContent = {
                    Row(modifier = Modifier.offset(x = (-4).dp, y = 0.dp)) {
                        Text(text = "⌘C")
                        Box(modifier = Modifier.width(12.dp), content = {})
                        Icon(imageVector = Icons.Filled.ArrowRight, contentDescription = null, modifier = Modifier.size(10.dp))
                    }
                }, colors = ListItemDefaults.colors(containerColor = Color(0x00000000)))
            }
            Box(modifier = Modifier.fillMaxWidth().height(64.dp)) {
                ListItem(headlineContent = {
                    Text(text = headline5, modifier = Modifier.offset(x = (-6).dp, y = 0.dp))
                }, modifier = Modifier.height(64.dp), supportingContent = {
                    Text(text = supportingText5, modifier = Modifier.offset(x = (-6).dp, y = 0.dp), overflow = TextOverflow.Ellipsis, maxLines = 1)
                }, leadingContent = {
                    RailStarsIcon(selected = false, modifier = Modifier.size(20.dp))
                }, trailingContent = {
                    Row(modifier = Modifier.offset(x = (-4).dp, y = 0.dp)) {
                        Text(text = "⌘C")
                        Box(modifier = Modifier.width(12.dp), content = {})
                        Icon(imageVector = Icons.Filled.ArrowRight, contentDescription = null, modifier = Modifier.size(10.dp))
                    }
                }, colors = ListItemDefaults.colors(containerColor = Color(0x00000000)))
            }
        }
    }
}
