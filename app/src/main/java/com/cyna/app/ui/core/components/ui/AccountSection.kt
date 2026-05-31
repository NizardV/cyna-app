package com.cyna.app.ui.core.components.ui

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cyna.app.ui.screens.ordershistory.OrderHistoryScreen
import com.cyna.app.ui.screens.profile.ProfileScreen

@Composable
fun AccountSection(
    initialTab: NavTab = NavTab.ORDERS,
) {
    val innerNav = rememberNavController()
    var currentTab by remember { mutableStateOf(initialTab) }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                currentTab = currentTab,
                onTabSelected = { tab ->
                    val route = when (tab) {
                        NavTab.ORDERS -> "orders"
                        NavTab.PROFILE -> "profile"
                    }
                    innerNav.navigate(route) {
                        popUpTo(innerNav.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = innerNav,
            startDestination = when (initialTab) {
                NavTab.ORDERS  -> "orders"
                NavTab.PROFILE -> "profile"
            }
        ) {
            composable("orders") {
                OrderHistoryScreen(innerNav)
            }
            composable("profile") {
                ProfileScreen(innerNav)
            }
        }
    }
}