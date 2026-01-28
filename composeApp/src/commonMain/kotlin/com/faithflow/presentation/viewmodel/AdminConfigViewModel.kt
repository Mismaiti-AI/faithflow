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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class AdminConfigViewModel(
    private val preferencesRepository: PreferencesRepository,
    private val eventRepository: EventRepository,
    private val newsRepository: NewsItemRepository,
    private val profileRepository: ChurchProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AdminConfigUiState>(AdminConfigUiState.Idle)
    val uiState: StateFlow<AdminConfigUiState> = _uiState.asStateFlow()

    private val _sheetUrl = MutableStateFlow("")
    val sheetUrl: StateFlow<String> = _sheetUrl.asStateFlow()

    init {
        viewModelScope.launch {
            preferencesRepository.sheetUrl.collect { url ->
                _sheetUrl.value = url
            }
        }
    }

    fun updateSheetUrl(url: String) {
        _sheetUrl.value = url
    }

    fun testConnection() {
        if (_sheetUrl.value.isBlank()) {
            _uiState.value = AdminConfigUiState.Error("Please enter a Google Sheets URL")
            return
        }

        viewModelScope.launch {
            _uiState.value = AdminConfigUiState.Testing

            try {
                // Save the URL first
                preferencesRepository.setSheetUrl(_sheetUrl.value)

                // Try loading data from all sheets
                val eventResult = eventRepository.refreshEvents()
                val newsResult = newsRepository.refreshNewsItems()
                val profileResult = profileRepository.refreshProfile()

                // Check if at least one succeeded
                val hasData = eventRepository.events.value.isNotEmpty() ||
                        newsRepository.newsItems.value.isNotEmpty() ||
                        profileRepository.profile.value != null

                if (hasData) {
                    _uiState.value = AdminConfigUiState.Success("Connection successful! Data loaded.")
                } else {
                    _uiState.value = AdminConfigUiState.Error(
                        "Connected but no data found. Please check your sheet structure."
                    )
                }
            } catch (e: Exception) {
                _uiState.value = AdminConfigUiState.Error(
                    e.message ?: "Failed to connect to Google Sheets"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = AdminConfigUiState.Idle
    }
}

sealed interface AdminConfigUiState {
    data object Idle : AdminConfigUiState
    data object Testing : AdminConfigUiState
    data class Success(val message: String) : AdminConfigUiState
    data class Error(val message: String) : AdminConfigUiState
}
