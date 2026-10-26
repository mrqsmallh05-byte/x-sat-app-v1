package com.example

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.VpnService
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MainAppScreen
import com.example.ui.screens.RegisterScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.TermsScreen
import com.example.ui.theme.SatBackground
import com.example.ui.theme.XSatTheme
import com.example.viewmodel.AppDestination
import com.example.viewmodel.MainTab
import com.example.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    private val vpnPrepareLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            viewModel.startSatelliteConnectionProcess(this) {}
        }
    }

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* granted or denied */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        handleIntent(intent)
        checkNotificationPermission()

        setContent {
            val currentThemeMode by viewModel.currentThemeMode.collectAsState()
            XSatTheme(themeMode = currentThemeMode) {
                val currentDest by viewModel.currentDestination.collectAsState()
                val authError by viewModel.authError.collectAsState()
                val isLoading by viewModel.isLoading.collectAsState()

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(SatBackground)
                        .safeDrawingPadding()
                ) {
                    when (currentDest) {
                        AppDestination.SPLASH -> {
                            SplashScreen()
                        }
                        AppDestination.LOGIN -> {
                            LoginScreen(
                                onLoginClick = { phone, pass ->
                                    viewModel.login(phone, pass)
                                },
                                onNavigateToRegister = {
                                    viewModel.navigateTo(AppDestination.REGISTER)
                                },
                                onNavigateToTerms = {
                                    viewModel.navigateTo(AppDestination.MAIN_APP)
                                    viewModel.selectTab(MainTab.TERMS)
                                },
                                errorMessage = authError,
                                isLoading = isLoading
                            )
                        }
                        AppDestination.REGISTER -> {
                            BackHandler {
                                viewModel.navigateTo(AppDestination.LOGIN)
                            }
                            RegisterScreen(
                                onRegisterClick = { fullName, phone, region, phoneModel, pass, confirmPass, agreed ->
                                    viewModel.register(
                                        fullName = fullName,
                                        phone = phone,
                                        region = region,
                                        phoneModel = phoneModel,
                                        pass = pass,
                                        confirmPass = confirmPass,
                                        agreedToTerms = agreed
                                    )
                                },
                                onNavigateToLogin = {
                                    viewModel.navigateTo(AppDestination.LOGIN)
                                },
                                onNavigateToTerms = {
                                    viewModel.navigateTo(AppDestination.MAIN_APP)
                                    viewModel.selectTab(MainTab.TERMS)
                                },
                                errorMessage = authError,
                                isLoading = isLoading
                            )
                        }
                        AppDestination.MAIN_APP -> {
                            MainAppScreen(
                                viewModel = viewModel,
                                onRequestSatelliteNet = {
                                    requestSatelliteVpnConnection()
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val targetTab = intent?.getStringExtra("NAVIGATE_TAB")
        if (targetTab == "PACKAGES") {
            viewModel.selectTab(MainTab.PACKAGES)
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(permission)
            }
        }
    }

    private fun requestSatelliteVpnConnection() {
        val prepareIntent = VpnService.prepare(this)
        if (prepareIntent != null) {
            vpnPrepareLauncher.launch(prepareIntent)
        } else {
            viewModel.startSatelliteConnectionProcess(this) {}
        }
    }
}
