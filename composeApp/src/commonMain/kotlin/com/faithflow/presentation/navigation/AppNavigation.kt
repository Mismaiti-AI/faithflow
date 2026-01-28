package com.faithflow.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.faithflow.domain.repository.PreferencesRepository
import com.faithflow.presentation.screens.events.EventCalendarScreen
import com.faithflow.presentation.screens.home.HomeScreen
import com.faithflow.presentation.screens.news.NewsFeedScreen
import com.faithflow.presentation.screens.profile.ChurchProfileScreen
import com.faithflow.presentation.screens.setup.SetupScreen
import org.koin.compose.koinInject

/**
 * Main navigation setup for FaithFlow
 * Handles navigation between all screens with type-safe routes
 */
@Composable
fun AppNavigation(
    preferencesRepository: PreferencesRepository = koinInject()
) {
    val navController = rememberNavController()
    val sheetUrl by preferencesRepository.observeSheetUrl().collectAsState(initial = null)

    // Determine start destination based on setup status
    val startDestination = if (sheetUrl.isNullOrBlank()) Setup else Home

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Setup screen (first-time configuration)
        composable<Setup> {
            SetupScreen(
                onSetupComplete = {
                    navController.navigate(Home) {
                        popUpTo<Setup> { inclusive = true }
                    }
                }
            )
        }

        // Home screen (main hub)
        composable<Home> {
            HomeScreen(
                onEventCalendarClick = {
                    navController.navigate(EventCalendar)
                },
                onNewsFeedClick = {
                    navController.navigate(NewsFeed)
                },
                onChurchProfileClick = {
                    navController.navigate(ChurchProfile)
                },
                onSettingsClick = {
                    navController.navigate(Settings)
                }
            )
        }

        // Event Calendar screen
        composable<EventCalendar> {
            EventCalendarScreen(
                onBack = { navController.navigateUp() },
                onEventClick = { eventId ->
                    navController.navigate(EventDetail(eventId))
                }
            )
        }

        // Event Detail screen
        composable<EventDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<EventDetail>()
            // TODO: Create EventDetailScreen
            EventCalendarScreen(
                onBack = { navController.navigateUp() },
                onEventClick = {}
            )
        }

        // News Feed screen
        composable<NewsFeed> {
            NewsFeedScreen(
                onBack = { navController.navigateUp() },
                onNewsClick = { newsId ->
                    navController.navigate(NewsDetail(newsId))
                }
            )
        }

        // News Detail screen
        composable<NewsDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<NewsDetail>()
            // TODO: Create NewsDetailScreen
            NewsFeedScreen(
                onBack = { navController.navigateUp() },
                onNewsClick = {}
            )
        }

        // Church Profile screen
        composable<ChurchProfile> {
            ChurchProfileScreen(
                onBack = { navController.navigateUp() }
            )
        }

        // Settings screen (reuse Setup for now)
        composable<Settings> {
            SetupScreen(
                onSetupComplete = {
                    navController.navigateUp()
                }
            )
        }
    }
}
