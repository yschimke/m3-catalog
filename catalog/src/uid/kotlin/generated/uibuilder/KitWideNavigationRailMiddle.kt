package generated.uibuilder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuOpen
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.WideNavigationRail
import androidx.compose.material3.WideNavigationRailDefaults
import androidx.compose.material3.WideNavigationRailItem
import androidx.compose.material3.WideNavigationRailValue
import androidx.compose.material3.rememberWideNavigationRailState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ee.schimke.m3catalog.generated.resources.Res
import ee.schimke.m3catalog.generated.resources.action_menu
import ee.schimke.m3catalog.generated.resources.action_new
import ee.schimke.m3catalog.uid.FlatExtendedFloatingActionButton
import ee.schimke.m3catalog.uid.RailStarsIcon
import org.jetbrains.compose.resources.stringResource

@kotlin.OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun KitWideNavigationRailMiddle(modifier: androidx.compose.ui.Modifier, fabLabel: kotlin.String, homeSelected: kotlin.Boolean, homeLabel: kotlin.String, searchSelected: kotlin.Boolean, searchLabel: kotlin.String, youSelected: kotlin.Boolean, youLabel: kotlin.String, onMenuClick: () -> kotlin.Unit, onFabClick: () -> kotlin.Unit, onHomeClick: () -> kotlin.Unit, onSearchClick: () -> kotlin.Unit, onYouClick: () -> kotlin.Unit) {
    Box(modifier = modifier) {
        WideNavigationRail(modifier = Modifier.height(800.dp), state = rememberWideNavigationRailState(initialValue = WideNavigationRailValue.Expanded), colors = WideNavigationRailDefaults.colors(containerColor = Color(0x00000000)), header = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = onMenuClick) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.MenuOpen, contentDescription = stringResource(Res.string.action_menu))
                }
                Box(modifier = Modifier.height(8.dp), content = {})
                FlatExtendedFloatingActionButton(onClick = onFabClick, icon = {
                    Icon(imageVector = Icons.Filled.Edit, contentDescription = stringResource(Res.string.action_new))
                }, text = {
                    Text(text = fabLabel)
                })
            }
        }, arrangement = Arrangement.Center) {
            WideNavigationRailItem(selected = homeSelected, onClick = onHomeClick, icon = {
                RailStarsIcon(selected = homeSelected)
            }, label = {
                Text(text = homeLabel)
            }, railExpanded = true)
            WideNavigationRailItem(selected = searchSelected, onClick = onSearchClick, icon = {
                RailStarsIcon(selected = searchSelected)
            }, label = {
                Text(text = searchLabel)
            }, railExpanded = true)
            WideNavigationRailItem(selected = youSelected, onClick = onYouClick, icon = {
                RailStarsIcon(selected = youSelected)
            }, label = {
                Text(text = youLabel)
            }, railExpanded = true)
        }
    }
}
