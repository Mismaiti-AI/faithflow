package com.faithflow.data.repository

import com.faithflow.domain.repository.PreferencesRepository
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferencesRepositoryImpl(
    private val settings: Settings
) : PreferencesRepository {

    private val _sheetUrl = MutableStateFlow(settings.getString(KEY_SHEET_URL, ""))
    override val sheetUrl: StateFlow<String> = _sheetUrl.asStateFlow()

    private val _selectedCategories = MutableStateFlow(
        settings.getString(KEY_CATEGORIES, "").split(",").filter { it.isNotBlank() }.toSet()
    )
    override val selectedCategories: StateFlow<Set<String>> = _selectedCategories.asStateFlow()

    private val _isDarkMode = MutableStateFlow(settings.getBoolean(KEY_DARK_MODE, false))
    override val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _isOnboardingComplete = MutableStateFlow(settings.getBoolean(KEY_ONBOARDING_COMPLETE, false))
    override val isOnboardingComplete: StateFlow<Boolean> = _isOnboardingComplete.asStateFlow()

    override suspend fun setSheetUrl(url: String) {
        settings.putString(KEY_SHEET_URL, url)
        _sheetUrl.value = url
    }

    override suspend fun getSheetUrl(): String {
        return _sheetUrl.value
    }

    override suspend fun setSelectedCategories(categories: Set<String>) {
        val categoriesString = categories.joinToString(",")
        settings.putString(KEY_CATEGORIES, categoriesString)
        _selectedCategories.value = categories
    }

    override suspend fun toggleDarkMode() {
        val newValue = !_isDarkMode.value
        settings.putBoolean(KEY_DARK_MODE, newValue)
        _isDarkMode.value = newValue
    }

    override suspend fun setDarkMode(enabled: Boolean) {
        settings.putBoolean(KEY_DARK_MODE, enabled)
        _isDarkMode.value = enabled
    }

    override suspend fun completeOnboarding() {
        settings.putBoolean(KEY_ONBOARDING_COMPLETE, true)
        _isOnboardingComplete.value = true
    }

    override suspend fun resetFilters() {
        settings.remove(KEY_CATEGORIES)
        _selectedCategories.value = emptySet()
    }

    override suspend fun clearAll() {
        settings.clear()
        _sheetUrl.value = ""
        _selectedCategories.value = emptySet()
        _isDarkMode.value = false
        _isOnboardingComplete.value = false
    }

    companion object {
        private const val KEY_SHEET_URL = "sheet_url"
        private const val KEY_CATEGORIES = "selected_categories"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_ONBOARDING_COMPLETE = "onboarding_complete"
    }
}
