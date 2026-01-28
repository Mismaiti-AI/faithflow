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
import com.faithflow.presentation.screen.AdminConfigScreen
import com.faithflow.presentation.screen.ChurchProfileScreen
import com.faithflow.presentation.screen.EventCalendarScreen
import com.faithflow.presentation.screen.EventCategoryFilterScreen
import com.faithflow.presentation.screen.EventDetailScreen
import com.faithflow.presentation.screen.HomeScreen
import com.faithflow.presentation.screen.NewsDetailScreen
import com.faithflow.presentation.screen.NewsFeedScreen
import com.faithflow.presentation.screen.OnboardingScreen
import com.faithflow.presentation.screen.SettingsScreen
import org.koin.compose.koinInject

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val preferencesRepository: PreferencesRepository = koinInject()
    val isOnboardingComplete by preferencesRepository.isOnboardingComplete.collectAsState()

    LaunchedEffect(isOnboardingComplete) {
        if (!isOnboardingComplete) {
            navController.navigate(Onboarding) {
                popUpTo(Home) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (isOnboardingComplete) Home else Onboarding
    ) {
        composable<Onboarding> {
            OnboardingScreen(
                onComplete = {
                    navController.navigate(Home) {
                        popUpTo(Onboarding) { inclusive = true }
                    }
                }
            )
        }

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

        composable<EventCalendar> {
            EventCalendarScreen(
                onBack = { navController.navigateUp() },
                onEventClick = { eventId ->
                    navController.navigate(EventDetail(eventId))
                },
                onFilterClick = {
                    navController.navigate(EventCategoryFilter)
                }
            )
        }

        composable<EventDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<EventDetail>()
            EventDetailScreen(
                eventId = route.eventId,
                onBack = { navController.navigateUp() }
            )
        }

        composable<EventCategoryFilter> {
            EventCategoryFilterScreen(
                onBack = { navController.navigateUp() }
            )
        }

        composable<NewsFeed> {
            NewsFeedScreen(
                onBack = { navController.navigateUp() },
                onNewsClick = { newsItemId ->
                    navController.navigate(NewsDetail(newsItemId))
                }
            )
        }

        composable<NewsDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<NewsDetail>()
            NewsDetailScreen(
                newsItemId = route.newsItemId,
                onBack = { navController.navigateUp() }
            )
        }

        composable<ChurchProfile> {
            ChurchProfileScreen(
                onBack = { navController.navigateUp() }
            )
        }

        composable<AdminConfig> {
            AdminConfigScreen(
                onBack = { navController.navigateUp() },
                onSuccess = { navController.navigateUp() }
            )
        }

        composable<Settings> {
            SettingsScreen(
                onBack = { navController.navigateUp() },
                onConfigureSheet = {
                    navController.navigate(AdminConfig)
                }
            )
        }
    }
}
