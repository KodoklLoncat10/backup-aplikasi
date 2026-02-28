package com.example.melanoscan_new

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.melanoscan_new.ui.ViewModelFactory
import com.example.melanoscan_new.ui.navigation.Screen
import com.example.melanoscan_new.ui.screens.*
import com.example.melanoscan_new.ui.theme.MelanoScan_newTheme
import com.example.melanoscan_new.ui.theme.neumorphic
import com.example.melanoscan_new.util.LocaleManager
import com.example.melanoscan_new.util.Prefs

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        val prefs = Prefs(newBase)
        super.attachBaseContext(LocaleManager.setLocale(newBase, prefs.language))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()

        setContent {
            val prefs = Prefs(this)
            var currentLanguage by remember { mutableStateOf(prefs.language) }

            MelanoScan_newTheme {
                val navController = rememberNavController()

                val context = LocalContext.current
                val lifecycleOwner = LocalLifecycleOwner.current
                val viewModelStoreOwner = LocalViewModelStoreOwner.current!!
                val activityResultRegistryOwner = LocalActivityResultRegistryOwner.current!!
                val savedStateRegistryOwner = LocalSavedStateRegistryOwner.current

                val localizedContext = remember(currentLanguage) {
                    LocaleManager.setLocale(context, currentLanguage)
                }

                CompositionLocalProvider(
                    LocalContext provides localizedContext,
                    LocalLifecycleOwner provides lifecycleOwner,
                    LocalViewModelStoreOwner provides viewModelStoreOwner,
                    LocalActivityResultRegistryOwner provides activityResultRegistryOwner,
                    LocalSavedStateRegistryOwner provides savedStateRegistryOwner
                ) {
                    Scaffold(
                        bottomBar = {
                            AppBottomNavigation(navController = navController)
                        }
                    ) {
                        AppNavHost(
                            navController = navController,
                            modifier = Modifier.padding(it),
                            onLanguageChange = { newLanguage -> currentLanguage = newLanguage } // Pass the callback
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppBottomNavigation(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    if (currentRoute != Screen.Splash.route) {
        BottomAppBar(
            containerColor = Color(0xFFF0F2F5),
            modifier = Modifier.neumorphic()
        ) {
            IconButton(onClick = { navController.navigate(Screen.Home.route) }, modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.Home, contentDescription = stringResource(id = R.string.nav_home), tint = if (currentRoute == Screen.Home.route) MaterialTheme.colorScheme.primary else Color.Gray)
            }
            IconButton(onClick = { navController.navigate(Screen.History.route) }, modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.History, contentDescription = stringResource(id = R.string.nav_history), tint = if (currentRoute == Screen.History.route) MaterialTheme.colorScheme.primary else Color.Gray)
            }
            IconButton(onClick = { navController.navigate(Screen.Settings.route) }, modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.Settings, contentDescription = stringResource(id = R.string.nav_settings), tint = if (currentRoute == Screen.Settings.route) MaterialTheme.colorScheme.primary else Color.Gray)
            }
        }
    }
}

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier, onLanguageChange: (String) -> Unit) {
    val context = LocalContext.current.applicationContext as Application
    val factory = ViewModelFactory(context)
    val scanViewModel: ScanViewModel = viewModel(factory = factory)
    val classificationResult by scanViewModel.classificationResult.collectAsState()
    val scannedBitmap by scanViewModel.scannedBitmap.collectAsState()

    NavHost(navController, startDestination = Screen.Splash.route, modifier = modifier) {
        composable(Screen.Splash.route) { SplashScreen(navController) }
        composable(Screen.Home.route) { HomeScreen(navController) }
        composable(Screen.Scan.route) { ScanScreen(navController, scanViewModel) }
        composable(Screen.Result.route) {
            ResultScreen(
                navController = navController,
                result = classificationResult,
                bitmap = scannedBitmap,
                onResultScreenDisposed = { scanViewModel.onResultScreenDisposed() }
            )
        }
        composable(Screen.History.route) { HistoryScreen() }
        composable(Screen.Info.route) { InfoScreen() }
        composable(Screen.About.route) { AboutUsScreen(navController) }
        composable(Screen.Settings.route) { SettingsScreen(navController, onLanguageChange) } // Pass the callback
    }
}