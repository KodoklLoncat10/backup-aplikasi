package com.example.melanoscan_new.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class Screen(val route: String, val title: String? = null, val icon: ImageVector? = null) {
    object Splash : Screen("splash")
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Scan : Screen("scan", "Scan", Icons.Default.Scanner)
    object Result : Screen("result/{imageUri}") {
        fun createRoute(imageUri: String): String {
            val encodedUri = URLEncoder.encode(imageUri, StandardCharsets.UTF_8.toString())
            return "result/$encodedUri"
        }
    }
    object History : Screen("history", "History", Icons.Default.History)
    object Info : Screen("info", "Info", Icons.Default.Info)
    object About : Screen("about", "About Us")
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}