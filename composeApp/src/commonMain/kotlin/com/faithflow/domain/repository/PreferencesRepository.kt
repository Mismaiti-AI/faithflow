package com.faithflow.domain.repository

import kotlinx.coroutines.flow.StateFlow

interface PreferencesRepository {
    val sheetUrl: StateFlow<String>
    val selectedCategories: StateFlow<Set<String>>
    val isDarkMode: StateFlow<Boolean>
    val isOnboardingComplete: StateFlow<Boolean>

    suspend fun setSheetUrl(url: String)
    suspend fun getSheetUrl(): String
    suspend fun setSelectedCategories(categories: Set<String>)
    suspend fun toggleDarkMode()
    suspend fun setDarkMode(enabled: Boolean)
    suspend fun completeOnboarding()
    suspend fun resetFilters()
    suspend fun clearAll()
}
