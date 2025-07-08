package com.app.footballfixtures

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.footballfixtures.ui.theme.FootballFixturesTheme
import com.app.matches.ui.MatchesScreen
import com.app.settings.ui.SettingsScreen
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FootballFixturesTheme {
                AppNavGraph()
            }
        }
    }
    @Composable
    fun AppNavGraph() {
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = Route.Matches
        ) {
            composable<Route.Matches> {
                MatchesScreen(
                    onSettingsClick = { navController.navigate(Route.Settings) }
                )
            }

            composable<Route.Settings> {
                SettingsScreen()
            }
        }
    }

}

sealed interface Route {
    @Serializable
    data object Matches : Route

    @Serializable
    data object Settings : Route
}
