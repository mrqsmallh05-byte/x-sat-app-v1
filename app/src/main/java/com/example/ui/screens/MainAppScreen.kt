package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.SatBackground
import com.example.ui.theme.SatCard
import com.example.ui.theme.SatCardBorder
import com.example.ui.theme.SatGold
import com.example.ui.theme.SatPrimary
import com.example.ui.theme.SatTextMuted
import com.example.ui.theme.SatWhite
import com.example.viewmodel.MainTab
import com.example.viewmodel.MainViewModel

@Composable
fun MainAppScreen(
    viewModel: MainViewModel,
    onRequestSatelliteNet: () -> Unit
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    // BackHandler to navigate back to Home tab if on sub-screens
    BackHandler(enabled = currentTab != MainTab.HOME) {
        viewModel.selectTab(MainTab.HOME)
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = SatCard,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = currentTab == MainTab.HOME,
                    onClick = { viewModel.selectTab(MainTab.HOME) },
                    icon = { Icon(Icons.Default.Home, contentDescription = "الرئيسية") },
                    label = { Text("الرئيسية") },
                    colors = navigationItemColors(),
                    modifier = Modifier.testTag("nav_tab_home")
                )

                NavigationBarItem(
                    selected = currentTab == MainTab.PACKAGES,
                    onClick = { viewModel.selectTab(MainTab.PACKAGES) },
                    icon = { Icon(Icons.Default.Inventory2, contentDescription = "الباقات") },
                    label = { Text("الباقات") },
                    colors = navigationItemColors(),
                    modifier = Modifier.testTag("nav_tab_packages")
                )

                NavigationBarItem(
                    selected = currentTab == MainTab.COUNTER,
                    onClick = { viewModel.selectTab(MainTab.COUNTER) },
                    icon = { Icon(Icons.Default.Speed, contentDescription = "العداد") },
                    label = { Text("العداد") },
                    colors = navigationItemColors(),
                    modifier = Modifier.testTag("nav_tab_counter")
                )

                NavigationBarItem(
                    selected = currentTab == MainTab.TERMS,
                    onClick = { viewModel.selectTab(MainTab.TERMS) },
                    icon = { Icon(Icons.Default.Description, contentDescription = "الشروط") },
                    label = { Text("الشروط") },
                    colors = navigationItemColors(),
                    modifier = Modifier.testTag("nav_tab_terms")
                )

                NavigationBarItem(
                    selected = currentTab == MainTab.PROFILE,
                    onClick = { viewModel.selectTab(MainTab.PROFILE) },
                    icon = { Icon(Icons.Default.Person, contentDescription = "حسابي") },
                    label = { Text("حسابي") },
                    colors = navigationItemColors(),
                    modifier = Modifier.testTag("nav_tab_profile")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(SatBackground)
        ) {
            when (currentTab) {
                MainTab.HOME -> HomeScreen(
                    viewModel = viewModel,
                    currentUser = currentUser,
                    onNavigateToPackages = { viewModel.selectTab(MainTab.PACKAGES) },
                    onRequestSatelliteNet = onRequestSatelliteNet
                )
                MainTab.PACKAGES -> PackagesScreen(
                    viewModel = viewModel,
                    currentUser = currentUser,
                    onRequestActivateFree = onRequestSatelliteNet
                )
                MainTab.COUNTER -> CounterScreen(
                    viewModel = viewModel,
                    currentUser = currentUser,
                    onNavigateToPackages = { viewModel.selectTab(MainTab.PACKAGES) },
                    onStartVpn = onRequestSatelliteNet
                )
                MainTab.TERMS -> TermsScreen()
                MainTab.PROFILE -> ProfileScreen(
                    viewModel = viewModel,
                    currentUser = currentUser,
                    onLogout = { viewModel.logout() }
                )
            }
        }
    }
}

@Composable
private fun navigationItemColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = SatPrimary,
    selectedTextColor = SatPrimary,
    indicatorColor = SatPrimary.copy(alpha = 0.15f),
    unselectedIconColor = SatTextMuted,
    unselectedTextColor = SatTextMuted
)
