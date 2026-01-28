package com.faithflow.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faithflow.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        preferencesRepository.sheetUrl,
        preferencesRepository.isDarkMode,
        preferencesRepository.selectedCategories
    ) { sheetUrl, isDarkMode, categories ->
        SettingsUiState(
            sheetUrl = sheetUrl,
            isDarkMode = isDarkMode,
            selectedCategories = categories
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun toggleDarkMode() {
        viewModelScope.launch {
            preferencesRepository.toggleDarkMode()
        }
    }

    fun updateSheetUrl(url: String) {
        viewModelScope.launch {
            preferencesRepository.setSheetUrl(url)
        }
    }

    fun resetFilters() {
        viewModelScope.launch {
            preferencesRepository.resetFilters()
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            preferencesRepository.clearAll()
        }
    }
}

data class SettingsUiState(
    val sheetUrl: String = "",
    val isDarkMode: Boolean = false,
    val selectedCategories: Set<String> = emptySet()
)
