package com.faithflow.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faithflow.domain.repository.ChurchProfileRepository
import com.faithflow.domain.repository.EventRepository
import com.faithflow.domain.repository.NewsItemRepository
import com.faithflow.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val preferencesRepository: PreferencesRepository,
    private val eventRepository: EventRepository,
    private val newsRepository: NewsItemRepository,
    private val profileRepository: ChurchProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<OnboardingUiState>(OnboardingUiState.Input)
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    private val _sheetUrl = MutableStateFlow("")
    val sheetUrl: StateFlow<String> = _sheetUrl.asStateFlow()

    init {
        checkExistingData()
    }

    private fun checkExistingData() {
        viewModelScope.launch {
            val existingUrl = preferencesRepository.getSheetUrl()
            val isComplete = preferencesRepository.isOnboardingComplete.value

            if (existingUrl.isNotBlank() && isComplete) {
                _uiState.value = OnboardingUiState.AlreadyConfigured
            }
        }
    }

    fun updateSheetUrl(url: String) {
        _sheetUrl.value = url
    }

    fun completeSetup() {
        if (_sheetUrl.value.isBlank()) {
            _uiState.value = OnboardingUiState.Error("Please enter a Google Sheets URL")
            return
        }

        viewModelScope.launch {
            _uiState.value = OnboardingUiState.Loading

            try {
                // Save the URL
                preferencesRepository.setSheetUrl(_sheetUrl.value)

                // Try loading data
                eventRepository.refreshEvents()
                newsRepository.refreshNewsItems()
                profileRepository.refreshProfile()

                // Mark onboarding as complete
                preferencesRepository.completeOnboarding()

                _uiState.value = OnboardingUiState.Success
            } catch (e: Exception) {
                _uiState.value = OnboardingUiState.Error(
                    e.message ?: "Failed to connect to Google Sheets"
                )
            }
        }
    }

    fun skipOnboarding() {
        viewModelScope.launch {
            preferencesRepository.completeOnboarding()
            _uiState.value = OnboardingUiState.Success
        }
    }

    fun clearError() {
        _uiState.value = OnboardingUiState.Input
    }
}

sealed interface OnboardingUiState {
    data object Input : OnboardingUiState
    data object Loading : OnboardingUiState
    data object Success : OnboardingUiState
    data object AlreadyConfigured : OnboardingUiState
    data class Error(val message: String) : OnboardingUiState
}
