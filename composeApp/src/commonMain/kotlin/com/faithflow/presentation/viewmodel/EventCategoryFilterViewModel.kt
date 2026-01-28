package com.faithflow.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faithflow.domain.repository.EventRepository
import com.faithflow.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EventCategoryFilterViewModel(
    private val eventRepository: EventRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    val uiState: StateFlow<EventCategoryFilterUiState> = combine(
        eventRepository.events,
        preferencesRepository.selectedCategories
    ) { events, selectedCategories ->
        val allCategories = events.map { it.category }.distinct().sorted()
        EventCategoryFilterUiState(
            allCategories = allCategories,
            selectedCategories = selectedCategories
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = EventCategoryFilterUiState()
    )

    fun toggleCategory(category: String) {
        viewModelScope.launch {
            val current = preferencesRepository.selectedCategories.value
            val updated = if (current.contains(category)) {
                current - category
            } else {
                current + category
            }
            preferencesRepository.setSelectedCategories(updated)
        }
    }

    fun savePreferences(onSuccess: () -> Unit) {
        // Preferences are saved automatically when toggled
        onSuccess()
    }

    fun resetFilters() {
        viewModelScope.launch {
            preferencesRepository.resetFilters()
        }
    }
}

data class EventCategoryFilterUiState(
    val allCategories: List<String> = emptyList(),
    val selectedCategories: Set<String> = emptySet()
)
