package com.faithflow.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faithflow.domain.model.ChurchProfile
import com.faithflow.domain.repository.ChurchProfileRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChurchProfileViewModel(
    private val repository: ChurchProfileRepository
) : ViewModel() {

    val uiState: StateFlow<ChurchProfileUiState> = combine(
        repository.profile,
        repository.isLoading,
        repository.error
    ) { profile, isLoading, error ->
        ChurchProfileUiState(
            profile = profile,
            isLoading = isLoading,
            error = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ChurchProfileUiState()
    )

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            repository.loadProfile()
        }
    }

    fun refreshProfile() {
        viewModelScope.launch {
            repository.refreshProfile()
        }
    }

    fun clearError() {
        repository.clearError()
    }
}

data class ChurchProfileUiState(
    val profile: ChurchProfile? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
