package com.saurav.focusguard.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.saurav.focusguard.ui.MainViewModel
import com.saurav.focusguard.ui.analytics.AnalyticsScreen
import com.saurav.focusguard.ui.apps.AllAppsScreen
import com.saurav.focusguard.ui.blocker.BlockerShieldScreen
import com.saurav.focusguard.ui.home.HomeScreen
import com.saurav.focusguard.ui.pin.PinConfigScreen
import com.saurav.focusguard.ui.settings.SettingsScreen
import com.saurav.focusguard.ui.theme.ActiveTabMaroon
import com.saurav.focusguard.ui.theme.BottomBarNavy
import com.saurav.focusguard.ui.theme.DeepSpaceNavy
import com.saurav.focusguard.ui.websites.BlockWebsitesScreen

enum class BottomNavTab(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME("home", "Home", Icons.Filled.Home, Icons.Outlined.Home),
    BLOCKER("blocker", "Blocker", Icons.Filled.Lock, Icons.Outlined.Lock),
    SETTINGS("settings", "Settings", Icons.Filled.Settings, Icons.Outlined.Settings)
}

@Composable
fun FocusGuardNavHost(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: BottomNavTab.HOME.route

    val showBottomBar = currentRoute in setOf(
        BottomNavTab.HOME.route,
        BottomNavTab.BLOCKER.route,
        BottomNavTab.SETTINGS.route
    )

    Scaffold(
        containerColor = DeepSpaceNavy,
        bottomBar = {
            if (showBottomBar) {
                NoScrollBottomBar(
                    currentRoute = currentRoute,
                    onSelectTab = { tab ->
                        navController.navigate(tab.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = BottomNavTab.HOME.route
            ) {
                composable(BottomNavTab.HOME.route) {
                    HomeScreen(
                        viewModel = viewModel,
                        onNavigateToAnalytics = { navController.navigate("analytics") },
                        onNavigateToAllApps = { navController.navigate("all_apps") },
                        onNavigateToBlockWebsites = { navController.navigate("block_websites") },
                        onNavigateToPinConfig = { navController.navigate("pin_config") }
                    )
                }

                composable(BottomNavTab.BLOCKER.route) {
                    BlockerShieldScreen(
                        viewModel = viewModel,
                        onBack = { navController.navigate(BottomNavTab.HOME.route) },
                        onNavigateToBlockedApps = { navController.navigate("all_apps") },
                        onNavigateToBlockedWebsites = { navController.navigate("block_websites") }
                    )
                }

                composable(BottomNavTab.SETTINGS.route) {
                    SettingsScreen(
                        viewModel = viewModel,
                        onBack = { navController.navigate(BottomNavTab.HOME.route) },
                        onNavigateToPinConfig = { navController.navigate("pin_config") }
                    )
                }

                composable("analytics") {
                    AnalyticsScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable("all_apps") {
                    AllAppsScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable("block_websites") {
                    BlockWebsitesScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable("pin_config") {
                    PinConfigScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

/**
 * Exact recreation of the bottom navigation bar seen in ./reference-ui/screen_01..07:
 * - Dark navy bar (#081226)
 * - Active tab expands into a wide rounded maroon pill (#8A124C) with icon + label
 * - Inactive tabs show a clean white/slate icon
 */
@Composable
private fun NoScrollBottomBar(
    currentRoute: String,
    onSelectTab: (BottomNavTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BottomBarNavy)
            .navigationBarsPadding()
            .height(74.dp)
            .padding(horizontal = 28.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavTab.entries.forEach { tab ->
            val selected = currentRoute == tab.route
            if (selected) {
                Box(
                    modifier = Modifier
                        .height(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(ActiveTabMaroon)
                        .clickable { onSelectTab(tab) }
                        .padding(horizontal = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = tab.selectedIcon,
                            contentDescription = tab.label,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = tab.label,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .clickable { onSelectTab(tab) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = tab.unselectedIcon,
                        contentDescription = tab.label,
                        tint = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
