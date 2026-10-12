package generated.uibuilder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.WideNavigationRail
import androidx.compose.material3.WideNavigationRailDefaults
import androidx.compose.material3.WideNavigationRailItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.action_menu
import ee.schimke.m3catalog.generated.resources.action_new
import ee.schimke.m3catalog.uid.FlatFloatingActionButton
import ee.schimke.m3catalog.uid.RailStarsIcon
import org.jetbrains.compose.resources.stringResource

@kotlin.OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun KitNavigationRailNoLabelsMiddle(modifier: androidx.compose.ui.Modifier, homeSelected: kotlin.Boolean, searchSelected: kotlin.Boolean, youSelected: kotlin.Boolean, onMenuClick: () -> kotlin.Unit, onFabClick: () -> kotlin.Unit, onHomeClick: () -> kotlin.Unit, onSearchClick: () -> kotlin.Unit, onYouClick: () -> kotlin.Unit) {
    Box(modifier = modifier) {
        WideNavigationRail(modifier = Modifier.height(800.dp), colors = WideNavigationRailDefaults.colors(containerColor = Color(0x00000000)), header = {
            Column(modifier = Modifier.width(96.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = onMenuClick) {
                    Icon(imageVector = Icons.Filled.Menu, contentDescription = stringResource(Res.string.action_menu))
                }
                Box(modifier = Modifier.height(8.dp), content = {})
                FlatFloatingActionButton(onClick = onFabClick) {
                    Icon(imageVector = Icons.Filled.Edit, contentDescription = stringResource(Res.string.action_new))
                }
            }
        }, arrangement = Arrangement.Center) {
            WideNavigationRailItem(selected = homeSelected, onClick = onHomeClick, icon = {
                RailStarsIcon(selected = homeSelected)
            }, label = null, railExpanded = false)
            WideNavigationRailItem(selected = searchSelected, onClick = onSearchClick, icon = {
                RailStarsIcon(selected = searchSelected)
            }, label = null, railExpanded = false)
            WideNavigationRailItem(selected = youSelected, onClick = onYouClick, icon = {
                RailStarsIcon(selected = youSelected)
            }, label = null, railExpanded = false)
        }
    }
}
